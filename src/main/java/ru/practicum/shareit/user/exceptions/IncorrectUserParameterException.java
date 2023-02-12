package ru.practicum.shareit.user.exceptions;

public class IncorrectUserParameterException extends RuntimeException {
    private final String parameter;

    public IncorrectUserParameterException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
