package com.kratos.arsenal.gun;

/**
 * Tipos de munición. Cada arma consume un tipo concreto y la recarga sólo
 * funciona si el jugador tiene el objeto de munición correspondiente.
 *
 * <p>El campo {@code itemName} debe coincidir con el nombre de registro del
 * objeto de munición definido en {@code ModItems}.</p>
 */
public enum AmmoType {
    NINE_MM("ammo_9mm"),
    FIFTY_AE("ammo_50ae"),
    SEVEN_SIX_TWO("ammo_762"),
    FIVE_FIVE_SIX("ammo_556"),
    FIFTY_BMG("ammo_50bmg"),
    SHELL("ammo_shell"),
    ENERGY_CELL("energy_cell"),
    ROCKET("rocket");

    private final String itemName;

    AmmoType(String itemName) {
        this.itemName = itemName;
    }

    /** Nombre de registro del objeto de munición asociado. */
    public String getItemName() {
        return itemName;
    }
}
