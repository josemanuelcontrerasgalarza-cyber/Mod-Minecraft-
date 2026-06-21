package com.kratos.arsenal.gun;

/**
 * Catálogo de armas del mod con todas sus estadísticas balísticas.
 *
 * <p>Cada valor de este enum representa un arma jugable. Toda la lógica
 * ({@code GunItem}) es genérica y se configura a partir de estos parámetros,
 * de modo que añadir un arma nueva es tan sencillo como añadir una entrada.</p>
 *
 * <p>Unidades:
 * <ul>
 *     <li>{@code damage}: corazones * 2 (puntos de daño de Minecraft).</li>
 *     <li>{@code fireCooldownTicks}: ticks entre disparos (20 ticks = 1 s).</li>
 *     <li>{@code reloadTicks}: duración de la recarga en ticks.</li>
 *     <li>{@code baseSpread}: dispersión base en radianes aproximados.</li>
 *     <li>{@code range}: alcance del hitscan en bloques.</li>
 *     <li>{@code recoil}: retroceso vertical en grados.</li>
 * </ul>
 */
public enum GunType {

    //                 id              ammo                 dmg  hs   mag  cd  reload spread range recoil pell burst  modes                                              default     falloff explo power ignite
    GLOCK19("glock19", AmmoType.NINE_MM,        5.0f, 1.6f, 15,  5,  28,   0.018, 48,  1.3f, 1, 1,  new FireMode[]{FireMode.SEMI},                              FireMode.SEMI, 18, false, 0f, 0),
    DESERT_EAGLE("desert_eagle", AmmoType.FIFTY_AE, 9.5f, 1.8f, 7,  9,  34,   0.020, 55,  3.0f, 1, 1,  new FireMode[]{FireMode.SEMI},                              FireMode.SEMI, 22, false, 0f, 0),
    MP5("mp5", AmmoType.NINE_MM,               4.5f, 1.5f, 30,  3,  40,   0.026, 50,  1.0f, 1, 3,  new FireMode[]{FireMode.AUTO, FireMode.BURST, FireMode.SEMI}, FireMode.AUTO, 16, false, 0f, 0),
    AK47("ak47", AmmoType.SEVEN_SIX_TWO,       7.0f, 1.7f, 30,  4,  48,   0.030, 70,  2.4f, 1, 1,  new FireMode[]{FireMode.AUTO, FireMode.SEMI},               FireMode.AUTO, 30, false, 0f, 0),
    M4A1("m4a1", AmmoType.FIVE_FIVE_SIX,       6.0f, 1.7f, 30,  3,  46,   0.022, 75,  1.8f, 1, 3,  new FireMode[]{FireMode.AUTO, FireMode.BURST, FireMode.SEMI}, FireMode.AUTO, 32, false, 0f, 0),
    BARRETT_M82("barrett_m82", AmmoType.FIFTY_BMG, 26.0f, 2.5f, 10, 28, 75, 0.004, 220, 7.0f, 1, 1, new FireMode[]{FireMode.SEMI},                             FireMode.SEMI, 200, false, 0f, 0),
    SPAS12("spas12", AmmoType.SHELL,           3.2f, 1.4f, 8,  14,  55,   0.075, 28,  4.0f, 8, 1,  new FireMode[]{FireMode.SEMI},                              FireMode.SEMI, 8,  false, 0f, 0),
    MINIGUN("minigun", AmmoType.SEVEN_SIX_TWO, 5.0f, 1.3f, 200, 2,  120,  0.050, 80,  0.9f, 1, 1,  new FireMode[]{FireMode.AUTO},                              FireMode.AUTO, 40, false, 0f, 0),
    PLASMA_RIFLE("plasma_rifle", AmmoType.ENERGY_CELL, 12.0f, 1.6f, 20, 6, 50, 0.012, 90, 1.6f, 1, 1, new FireMode[]{FireMode.AUTO, FireMode.SEMI},            FireMode.AUTO, 0,  false, 0f, 60),
    ROCKET_LAUNCHER("rocket_launcher", AmmoType.ROCKET, 0.0f, 1.0f, 1, 30, 60, 0.004, 120, 9.0f, 1, 1, new FireMode[]{FireMode.SEMI},                          FireMode.SEMI, 0,  true,  4.0f, 0);

    private final String id;
    private final AmmoType ammoType;
    private final float damage;
    private final float headshotMultiplier;
    private final int magSize;
    private final int fireCooldownTicks;
    private final int reloadTicks;
    private final double baseSpread;
    private final double range;
    private final float recoil;
    private final int pellets;
    private final int burstCount;
    private final FireMode[] modes;
    private final FireMode defaultMode;
    private final double falloffStart;
    private final boolean explosive;
    private final float explosionPower;
    private final int igniteTicks;

    GunType(String id, AmmoType ammoType, float damage, float headshotMultiplier, int magSize,
            int fireCooldownTicks, int reloadTicks, double baseSpread, double range, float recoil,
            int pellets, int burstCount, FireMode[] modes, FireMode defaultMode, double falloffStart,
            boolean explosive, float explosionPower, int igniteTicks) {
        this.id = id;
        this.ammoType = ammoType;
        this.damage = damage;
        this.headshotMultiplier = headshotMultiplier;
        this.magSize = magSize;
        this.fireCooldownTicks = fireCooldownTicks;
        this.reloadTicks = reloadTicks;
        this.baseSpread = baseSpread;
        this.range = range;
        this.recoil = recoil;
        this.pellets = pellets;
        this.burstCount = burstCount;
        this.modes = modes;
        this.defaultMode = defaultMode;
        this.falloffStart = falloffStart;
        this.explosive = explosive;
        this.explosionPower = explosionPower;
        this.igniteTicks = igniteTicks;
    }

    public String getId() { return id; }
    public AmmoType getAmmoType() { return ammoType; }
    public float getDamage() { return damage; }
    public float getHeadshotMultiplier() { return headshotMultiplier; }
    public int getMagSize() { return magSize; }
    public int getFireCooldownTicks() { return fireCooldownTicks; }
    public int getReloadTicks() { return reloadTicks; }
    public double getBaseSpread() { return baseSpread; }
    public double getRange() { return range; }
    public float getRecoil() { return recoil; }
    public int getPellets() { return pellets; }
    public int getBurstCount() { return burstCount; }
    public FireMode[] getModes() { return modes; }
    public FireMode getDefaultMode() { return defaultMode; }
    public double getFalloffStart() { return falloffStart; }
    public boolean isExplosive() { return explosive; }
    public float getExplosionPower() { return explosionPower; }
    public int getIgniteTicks() { return igniteTicks; }

    /** {@code true} si el daño disminuye con la distancia. */
    public boolean hasFalloff() {
        return falloffStart > 0.0;
    }
}
