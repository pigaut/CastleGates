package io.github.pigaut.castlegates.core;

public class GateCreateException extends Exception {

    public GateCreateException(String message) {
        super(message);
    }

    public GateCreateException(String world, int x, int y, int z, String reason) {
        super(String.format("Failed to create gate at %s, %d, %d, %d. Reason: %s.", world, x, y, z, reason));
    }

}
