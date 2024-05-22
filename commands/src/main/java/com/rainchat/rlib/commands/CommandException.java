package com.rainchat.rlib.commands;

@SuppressWarnings("serial")
public class CommandException extends Exception {

    public CommandException() {
        super();
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(Throwable throwable) {
        super(throwable);
    }

    public CommandException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
