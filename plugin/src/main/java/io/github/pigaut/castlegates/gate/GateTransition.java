package io.github.pigaut.castlegates.gate;

public enum GateTransition {

    OPENING,
    CLOSING;

    public boolean isOpening() {
        return this == OPENING;
    }

}
