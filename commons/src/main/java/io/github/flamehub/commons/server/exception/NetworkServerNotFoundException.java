package io.github.flamehub.commons.server.exception;

public final class NetworkServerNotFoundException extends RuntimeException {

    public NetworkServerNotFoundException() {
        super();
    }

    public NetworkServerNotFoundException(String message) {
        super(message);
    }

}
