package com.prodapt.order.exception;

public class InvalidMonthFormatException extends RuntimeException {

    public InvalidMonthFormatException(String value) {
        super("Invalid month format: '" + value + "'. Expected format: YYYY-MM (e.g. 2024-01)");
    }

}
