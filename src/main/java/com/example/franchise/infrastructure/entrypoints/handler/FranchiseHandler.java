package com.example.franchise.infrastructure.entrypoints.handler;

import com.example.franchise.domain.api.FranchiseServicePort;
import com.example.franchise.domain.enums.TechnicalMessage;
import com.example.franchise.domain.exceptions.BusinessException;
import com.example.franchise.infrastructure.entrypoints.dto.FranchiseDTO;
import com.example.franchise.infrastructure.entrypoints.mapper.FranchiseMapper;
import com.example.franchise.infrastructure.entrypoints.mapper.TopProductPerBranchMapper;
import com.example.franchise.infrastructure.entrypoints.util.APIResponse;
import com.example.franchise.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import com.example.franchise.infrastructure.entrypoints.dto.FranchiseRenameDTO;

import java.time.Instant;
import java.util.List;

import static com.example.franchise.infrastructure.entrypoints.util.Constants.FRANCHISE_ERROR;
import static com.example.franchise.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FranchiseHandler {

    private final FranchiseServicePort franchiseService;
    private final FranchiseMapper mapper;
    private final TopProductPerBranchMapper topMapper;
    private final Validator validator;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        String messageId = getMessageId(request);

        return request.bodyToMono(FranchiseDTO.class)
                .flatMap(dto -> franchiseService.createFranchise(mapper.toModel(dto)))
                .doOnSuccess(franchise -> log.info("Franchise created successfully with messageId: {}", messageId))
                .flatMap(franchise ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(APIResponse.builder()
                                        .code(TechnicalMessage.FRANCHISE_CREATED.getCode())
                                        .message(TechnicalMessage.FRANCHISE_CREATED.getMessage())
                                        .identifier(messageId)
                                        .date(Instant.now().toString())
                                        .data(mapper.toDto(franchise))
                                        .build())
                )
                .contextWrite(ctx -> {
                        String mid = messageId;
                        return (mid == null || mid.isBlank()) ? ctx : ctx.put(X_MESSAGE_ID, mid);
                })
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex))
                .onErrorResume(BusinessException.class, ex ->
                        buildErrorResponse(
                                HttpStatus.valueOf(
                                        safeParseStatus(ex.getTechnicalMessage().getCode(), HttpStatus.BAD_REQUEST.value())
                                ),
                                messageId,
                                TechnicalMessage.INVALID_INPUT,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    public Mono<ServerResponse> getTopProductsByBranch(ServerRequest request) {
        String messageId = getMessageId(request);

        return Mono.defer(() -> Mono.just(request.pathVariable("franchiseId")))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                .flatMapMany(franchiseId -> franchiseService.getTopProductsByBranch(franchiseId))
                .map(topMapper::toDto)
                .collectList()
                .doOnSuccess(list -> log.info("Top products fetched. messageId={}, count={}", messageId, list.size()))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex))
                .flatMap(list -> ServerResponse.ok().bodyValue(
                        APIResponse.builder()
                                .code(TechnicalMessage.TOP_PRODUCTS_BY_BRANCH.getCode()) // crea este enum si no existe
                                .message(TechnicalMessage.TOP_PRODUCTS_BY_BRANCH.getMessage())
                                .identifier(messageId)
                                .date(Instant.now().toString())
                                .data(list)
                                .build()
                ))
                .contextWrite(ctx -> {
                    String mid = messageId;
                    return (mid == null || mid.isBlank()) ? ctx : ctx.put(X_MESSAGE_ID, mid);
                })
                .onErrorResume(BusinessException.class, ex ->
                        buildErrorResponse(
                                HttpStatus.valueOf(
                                        safeParseStatus(ex.getTechnicalMessage().getCode(), HttpStatus.BAD_REQUEST.value())
                                ),
                                messageId,
                                TechnicalMessage.INVALID_INPUT,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error in top-products. messageId={}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    public Mono<ServerResponse> renameFranchise(ServerRequest request) {
        String messageId = getMessageId(request);

        return Mono.defer(() -> Mono.just(request.pathVariable("id")))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND)))
                .zipWith(request.bodyToMono(FranchiseRenameDTO.class))
                .flatMap(tuple -> {
                    Long id = tuple.getT1();
                    String newName = tuple.getT2().getName();
                    return franchiseService.renameFranchise(id, newName);
                })
                .doOnSuccess(franchise -> log.info("Franchise renamed successfully. messageId={}, franchiseId={}", messageId, franchise.id()))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(
                        APIResponse.builder()
                                .code(TechnicalMessage.FRANCHISE_RENAMED.getCode())
                                .message(TechnicalMessage.FRANCHISE_RENAMED.getMessage())
                                .identifier(messageId)
                                .date(Instant.now().toString())
                                .data(mapper.toDto(franchise))
                                .build()
                ))
                .contextWrite(ctx -> {
                    String mid = messageId;
                    return (mid == null || mid.isBlank()) ? ctx : ctx.put(X_MESSAGE_ID, mid);
                })
                .onErrorResume(BusinessException.class, ex ->
                        buildErrorResponse(
                                HttpStatus.valueOf(
                                        safeParseStatus(ex.getTechnicalMessage().getCode(), HttpStatus.BAD_REQUEST.value())
                                ),
                                messageId,
                                TechnicalMessage.INVALID_INPUT,
                                List.of(ErrorDTO.builder()
                                        .code(ex.getTechnicalMessage().getCode())
                                        .message(ex.getTechnicalMessage().getMessage())
                                        .param(ex.getTechnicalMessage().getParam())
                                        .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred in renameFranchise. messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus status,
                                                    String identifier,
                                                    TechnicalMessage envelope,
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

    private String getMessageId(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(X_MESSAGE_ID);
    }

    private int safeParseStatus(String code, int defaultStatus) {
        try {
            return Integer.parseInt(code.split("-")[0]);
        } catch (Exception e) {
            return defaultStatus;
        }
    }
}
