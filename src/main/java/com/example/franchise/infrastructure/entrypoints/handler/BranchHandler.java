package com.example.franchise.infrastructure.entrypoints.handler;

import com.example.franchise.domain.api.BranchServicePort;
import com.example.franchise.domain.enums.TechnicalMessage;
import com.example.franchise.domain.exceptions.BusinessException;
import com.example.franchise.domain.model.Branch;
import com.example.franchise.infrastructure.entrypoints.dto.BranchCreateDTO;
import com.example.franchise.infrastructure.entrypoints.mapper.BranchMapper;
import com.example.franchise.infrastructure.entrypoints.util.APIResponse;
import com.example.franchise.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.franchise.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BranchHandler {

    private final BranchServicePort branchService;
    private final BranchMapper mapper;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        String messageId = getMessageIdOrGenerate(request);

        return Mono.defer(() -> Mono.just(request.pathVariable("franchiseId")))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                .zipWith(request.bodyToMono(BranchCreateDTO.class))
                .map(tuple -> {
                    Long franchiseId = tuple.getT1();
                    BranchCreateDTO dto = tuple.getT2();
                    return new Branch(null, franchiseId, dto.getName());
                })
                .flatMap(br -> branchService.createBranch(br.franchiseId(), br))
                .doOnSuccess(b -> log.info("Branch created successfully. messageId={}, franchiseId={}, branchId={}", messageId, b.franchiseId(), b.id()))
                .doOnError(ex -> log.error("Error creating branch. messageId={}", messageId, ex))
                .flatMap(branch ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(APIResponse.builder()
                                        .code(TechnicalMessage.BRANCH_CREATED.getCode())
                                        .message(TechnicalMessage.BRANCH_CREATED.getMessage())
                                        .identifier(messageId)
                                        .date(Instant.now().toString())
                                        .data(mapper.toDto(branch))
                                        .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .onErrorResume(BusinessException.class, ex ->
                        buildErrorResponse(
                                HttpStatus.valueOf(safeParseStatus(ex.getTechnicalMessage().getCode(), HttpStatus.BAD_REQUEST.value())),
                                messageId,
                                TechnicalMessage.INVALID_INPUT,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())
                        )
                )
                .onErrorResume(ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                .build())
                ));
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status, String identifier,
                                                    com.example.franchise.domain.enums.TechnicalMessage envelope,
                                                    List<ErrorDTO> errors) {
        return Mono.defer(() -> ServerResponse.status(status)
                .bodyValue(APIResponse.builder()
                        .code(envelope.getCode())
                        .message(envelope.getMessage())
                        .identifier(identifier)
                        .date(Instant.now().toString())
                        .errors(errors)
                        .build()));
    }

    private String getMessageIdOrGenerate(ServerRequest serverRequest) {
        String mid = serverRequest.headers().firstHeader(X_MESSAGE_ID);
        return (mid == null || mid.isBlank()) ? UUID.randomUUID().toString() : mid;
    }

    private int safeParseStatus(String code, int defaultStatus) {
        try {
            return Integer.parseInt(code.split("-")[0]);
        } catch (Exception e) {
            return defaultStatus;
        }
    }
}
