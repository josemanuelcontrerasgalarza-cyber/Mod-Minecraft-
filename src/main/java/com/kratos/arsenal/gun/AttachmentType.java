package com.kratos.arsenal.gun;

/**
 * Accesorios montables sobre las armas. Cada accesorio ocupa una ranura
 * ({@link Slot}) y modifica el comportamiento del arma.
 */
public enum AttachmentType {
    /** Silenciador: reduce el ruido y el fogonazo. */
    SUPPRESSOR("suppressor", Slot.BARREL, 1.0f, 0.0, 1.0f),
    /** Mira holográfica: zoom ligero y menos dispersión. */
    HOLO_SIGHT("holo_sight", Slot.SIGHT, 1.15f, -0.30, 1.0f),
    /** Mira x4. */
    SCOPE_X4("scope_x4", Slot.SIGHT, 4.0f, -0.55, 1.0f),
    /** Mira x8. */
    SCOPE_X8("scope_x8", Slot.SIGHT, 8.0f, -0.70, 1.0f),
    /** Empuñadura: reduce el retroceso. */
    FOREGRIP("foregrip", Slot.GRIP, 1.0f, -0.20, 0.65f),
    /** Láser táctico: reduce mucho la dispersión disparando desde la cadera. */
    TACTICAL_LASER("tactical_laser", Slot.LASER, 1.0f, -0.45, 1.0f);

    /** Ranuras disponibles; sólo se puede montar un accesorio por ranura. */
    public enum Slot { SIGHT, BARREL, GRIP, LASER }

    private final String id;
    private final Slot slot;
    private final float zoomFactor;
    private final double spreadModifier;   // multiplicador relativo (porcentaje) sobre la dispersión base
    private final float recoilModifier;    // multiplicador del retroceso

    AttachmentType(String id, Slot slot, float zoomFactor, double spreadModifier, float recoilModifier) {
        this.id = id;
        this.slot = slot;
        this.zoomFactor = zoomFactor;
        this.spreadModifier = spreadModifier;
        this.recoilModifier = recoilModifier;
    }

    public String getId() { return id; }
    public Slot getSlot() { return slot; }

    /** Factor de zoom aportado por la mira (1.0 = sin zoom). */
    public float getZoomFactor() { return zoomFactor; }

    /** Variación porcentual de la dispersión (p.ej. -0.30 = -30%). */
    public double getSpreadModifier() { return spreadModifier; }

    /** Multiplicador del retroceso (1.0 = sin cambio). */
    public float getRecoilModifier() { return recoilModifier; }

    public boolean isSilencer() {
        return this == SUPPRESSOR;
    }

    public static AttachmentType byId(String id) {
        for (AttachmentType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
