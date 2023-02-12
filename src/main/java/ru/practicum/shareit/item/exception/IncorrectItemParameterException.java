package ru.practicum.shareit.item.exception;

public class IncorrectItemParameterException extends RuntimeException {
    private final String parameter;

    public IncorrectItemParameterException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
