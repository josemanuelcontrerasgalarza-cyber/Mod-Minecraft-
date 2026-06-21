package com.kratos.arsenal.gun;

/**
 * Modos de disparo soportados por las armas.
 */
public enum FireMode {
    /** Semiautomático: un disparo por pulsación. */
    SEMI("semi", "Semiautomático"),
    /** Ráfaga: dispara un número fijo de balas por pulsación. */
    BURST("burst", "Ráfaga"),
    /** Automático: dispara de forma continua mientras se mantenga pulsado. */
    AUTO("auto", "Automático");

    private final String id;
    private final String displayName;

    FireMode(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Devuelve el siguiente modo de disparo disponible dentro de la lista dada. */
    public static FireMode next(FireMode current, FireMode[] available) {
        if (available.length == 0) {
            return current;
        }
        int index = -1;
        for (int i = 0; i < available.length; i++) {
            if (available[i] == current) {
                index = i;
                break;
            }
        }
        return available[(index + 1) % available.length];
    }

    public static FireMode byId(String id) {
        for (FireMode mode : values()) {
            if (mode.id.equals(id)) {
                return mode;
            }
        }
        return SEMI;
    }
}
