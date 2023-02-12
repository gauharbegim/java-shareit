package ru.practicum.shareit.item.exception;

public class IncorrectOwnerParameterException extends RuntimeException {
    private final String parameter;

    public IncorrectOwnerParameterException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
