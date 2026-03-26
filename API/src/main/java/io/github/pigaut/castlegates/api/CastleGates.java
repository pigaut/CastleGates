package io.github.pigaut.castlegates.api;

import org.jetbrains.annotations.*;

public class CastleGates {

    private static CastleGatesAPI API;

    public static CastleGatesAPI getAPI() {
        if (API == null) {
            throw new IllegalStateException("Api has not been initialized yet.");
        }
        return API;
    }

    public static void setApiInstance(@NotNull CastleGatesAPI API) {
        if (CastleGates.API != null) {
            throw new UnsupportedOperationException("You cannot initialize the api instance after it was initialized.");
        }
        CastleGates.API = API;
    }

}
