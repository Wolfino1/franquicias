package com.example.franchise.domain.enums;

public enum TechnicalMessage {
    INTERNAL_ERROR("500", "Something went wrong, please try again", ""),
    INVALID_INPUT("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS("400", "Bad Parameters, please verify data", ""),

    FRANCHISE_CREATED("201", "Franchise created successfully", ""),
    FRANCHISE_ALREADY_EXISTS("409", "Franchise name already exists", "name"),
    INVALID_FRANCHISE_NAME("400", "Invalid franchise name", "name"),
    FRANCHISE_NOT_FOUND("404", "Franchise not found", "franchiseId"),

    BRANCH_CREATED("201", "Branch created successfully", ""),
    BRANCH_ALREADY_EXISTS("409", "Branch name already exists for this franchise", "name"),
    INVALID_BRANCH_NAME("400", "Invalid branch name", "name"),
    BRANCH_NOT_FOUND("404", "Branch not found", "branchId"),

    PRODUCT_CREATED("201", "Product created successfully", ""),
    PRODUCT_ALREADY_EXISTS("409", "Product name already exists for this branch", "name"),
    INVALID_PRODUCT_NAME("400", "Invalid product name", "name"),
    INVALID_STOCK("400", "Invalid stock value", "stock"),
    PRODUCT_NOT_FOUND("404", "Product not found", "productId"),
    PRODUCT_NOT_IN_BRANCH("404", "Product not found", "productId"),
    PRODUCT_DELETED("200", "Product deleted successfully", ""),
    PRODUCT_STOCK_UPDATED("200", "Product stock updated successfully", "");

    private final String code;
    private final String message;
    private final String param;

    TechnicalMessage(String code, String message, String param) {
        this.code = code;
        this.message = message;
        this.param = param;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getParam() {
        return param;
    }
}
