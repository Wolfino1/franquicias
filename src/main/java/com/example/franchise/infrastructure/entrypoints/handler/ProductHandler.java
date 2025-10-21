package com.example.franchise.infrastructure.entrypoints.handler;

import com.example.franchise.domain.api.ProductServicePort;
import com.example.franchise.domain.enums.TechnicalMessage;
import com.example.franchise.domain.exceptions.BusinessException;
import com.example.franchise.domain.model.Product;
import com.example.franchise.infrastructure.entrypoints.dto.ProductCreateDTO;
import com.example.franchise.infrastructure.entrypoints.dto.ProductStockUpdateDTO;
import com.example.franchise.infrastructure.entrypoints.mapper.ProductMapper;
import com.example.franchise.infrastructure.entrypoints.util.APIResponse;
import com.example.franchise.infrastructure.entrypoints.util.Constants;
import com.example.franchise.infrastructure.entrypoints.util.ErrorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
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
public class ProductHandler {

    private final ProductServicePort productService;
    private final ProductMapper mapper;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        String messageId = getMessageIdOrGenerate(request);

        return Mono.defer(() -> Mono.just(request.pathVariable(Constants.BRANCH_ID)))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)))
                .zipWith(request.bodyToMono(ProductCreateDTO.class))
                .map(tuple -> {
                    Long branchId = tuple.getT1();
                    ProductCreateDTO dto = tuple.getT2();
                    return new Product(null, branchId, dto.getName(), dto.getStock());
                })
                .flatMap(p -> productService.createProduct(p.branchId(), p))
                .doOnSuccess(p -> log.info("Product created successfully. messageId={}, branchId={}, productId={}",
                        messageId, p.branchId(), p.id()))
                .doOnError(ex -> log.error("Error creating product. messageId={}", messageId, ex))
                .flatMap(product ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .bodyValue(APIResponse.builder()
                                        .code(TechnicalMessage.PRODUCT_CREATED.getCode())
                                        .message(TechnicalMessage.PRODUCT_CREATED.getMessage())
                                        .identifier(messageId)
                                        .date(Instant.now().toString())
                                        .data(mapper.toDto(product))
                                        .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .onErrorResume(DuplicateKeyException.class, ex ->
                        buildErrorResponse(
                                HttpStatus.CONFLICT,
                                messageId,
                                TechnicalMessage.INVALID_INPUT,
                                List.of(ErrorDTO.builder()
                                        .code(TechnicalMessage.PRODUCT_ALREADY_EXISTS.getCode())
                                        .message(TechnicalMessage.PRODUCT_ALREADY_EXISTS.getMessage())
                                        .param(TechnicalMessage.PRODUCT_ALREADY_EXISTS.getParam())
                                        .build())
                        )
                )
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

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String messageId = getMessageIdOrGenerate(request);

        Mono<Long> branchIdMono = Mono.defer(() -> Mono.just(request.pathVariable(Constants.BRANCH_ID)))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)));

        Mono<Long> productIdMono = Mono.defer(() -> Mono.just(request.pathVariable(Constants.PRODUCT_ID)))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)));

        return Mono.zip(branchIdMono, productIdMono)
                .flatMap(t -> productService.deleteProduct(t.getT1(), t.getT2()))
                .doOnSuccess(v -> log.info("Product deleted successfully. messageId={}", messageId))
                .doOnError(ex -> log.error("Error deleting product. messageId={}", messageId, ex))
                .then(ServerResponse.ok().bodyValue(APIResponse.builder()
                        .code(TechnicalMessage.PRODUCT_DELETED.getCode())
                        .message(TechnicalMessage.PRODUCT_DELETED.getMessage())
                        .identifier(messageId)
                        .date(Instant.now().toString())
                        .build()))
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

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        String messageId = getMessageIdOrGenerate(request);

        Mono<Long> branchIdMono = Mono.defer(() -> Mono.just(request.pathVariable(Constants.BRANCH_ID)))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND)));

        Mono<Long> productIdMono = Mono.defer(() -> Mono.just(request.pathVariable("productId")))
                .map(Long::valueOf)
                .onErrorResume(e -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_FOUND)));

        Mono<ProductStockUpdateDTO> bodyMono = request.bodyToMono(ProductStockUpdateDTO.class);

        return Mono.zip(branchIdMono, productIdMono, bodyMono)
                .flatMap(t -> productService.updateStock(t.getT1(), t.getT2(), t.getT3().getStock()))
                .doOnSuccess(p -> log.info("Stock updated successfully. messageId={}, productId={}, newStock={}",
                        messageId, p.id(), p.stock()))
                .doOnError(ex -> log.error("Error updating stock. messageId={}", messageId, ex))
                .flatMap(product -> ServerResponse.ok().bodyValue(APIResponse.builder()
                        .code(TechnicalMessage.PRODUCT_STOCK_UPDATED.getCode())
                        .message(TechnicalMessage.PRODUCT_STOCK_UPDATED.getMessage())
                        .identifier(messageId)
                        .date(Instant.now().toString())
                        .data(mapper.toDto(product))
                        .build()))
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
