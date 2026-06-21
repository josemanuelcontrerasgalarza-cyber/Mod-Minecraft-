package com.kratos.arsenal.registry;

import com.kratos.arsenal.KratosArsenal;
import com.kratos.arsenal.gun.AmmoType;
import com.kratos.arsenal.gun.AttachmentType;
import com.kratos.arsenal.gun.GunType;
import com.kratos.arsenal.item.AttachmentItem;
import com.kratos.arsenal.item.GunItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro central de objetos: armas, munición, accesorios y materiales.
 *
 * <p>Mantiene listas y mapas auxiliares que usan el grupo creativo
 * ({@link ModItemGroups}) y el sistema de munición ({@code InventoryHelper}).</p>
 */
public final class ModItems {

    // Listas para el orden del grupo creativo.
    public static final List<Item> GUNS = new ArrayList<>();
    public static final List<Item> AMMO = new ArrayList<>();
    public static final List<Item> ATTACHMENTS = new ArrayList<>();
    public static final List<Item> MATERIALS = new ArrayList<>();

    private static final Map<GunType, GunItem> GUN_ITEMS = new EnumMap<>(GunType.class);
    private static final Map<AmmoType, Item> AMMO_ITEMS = new EnumMap<>(AmmoType.class);

    // Materiales de fabricación.
    public static Item STEEL_INGOT;
    public static Item TITANIUM_INGOT;
    public static Item RAW_TITANIUM;
    public static Item ELECTRONIC_COMPONENTS;
    public static Item ADVANCED_GUNPOWDER;
    public static Item GUN_PARTS;

    private ModItems() {}

    public static void register() {
        // ---- Armas ----
        for (GunType type : GunType.values()) {
            GunItem gun = new GunItem(type, new Item.Settings());
            register(type.getId(), gun);
            GUNS.add(gun);
            GUN_ITEMS.put(type, gun);
        }

        // ---- Munición (un objeto por tipo) ----
        for (AmmoType type : AmmoType.values()) {
            Item ammo = new Item(new Item.Settings().maxCount(64));
            register(type.getItemName(), ammo);
            AMMO.add(ammo);
            AMMO_ITEMS.put(type, ammo);
        }

        // ---- Accesorios ----
        // Usamos un mapa para evitar registrar dos veces si dos enums comparten id.
        Map<String, AttachmentType> uniqueAttachments = new LinkedHashMap<>();
        for (AttachmentType type : AttachmentType.values()) {
            uniqueAttachments.putIfAbsent(type.getId(), type);
        }
        for (AttachmentType type : uniqueAttachments.values()) {
            AttachmentItem attachment = new AttachmentItem(type, new Item.Settings().maxCount(16));
            register(type.getId(), attachment);
            ATTACHMENTS.add(attachment);
        }

        // ---- Materiales ----
        STEEL_INGOT = registerMaterial("steel_ingot");
        TITANIUM_INGOT = registerMaterial("titanium_ingot");
        RAW_TITANIUM = registerMaterial("raw_titanium");
        ELECTRONIC_COMPONENTS = registerMaterial("electronic_components");
        ADVANCED_GUNPOWDER = registerMaterial("advanced_gunpowder");
        GUN_PARTS = registerMaterial("gun_parts");
    }

    private static Item registerMaterial(String name) {
        Item item = new Item(new Item.Settings().maxCount(64));
        register(name, item);
        MATERIALS.add(item);
        return item;
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, KratosArsenal.id(name), item);
    }

    /** Devuelve el objeto de munición asociado a un tipo, o {@code null}. */
    public static Item getAmmoItem(AmmoType type) {
        return AMMO_ITEMS.get(type);
    }

    /** Devuelve el {@link GunItem} de un tipo de arma. */
    public static GunItem getGunItem(GunType type) {
        return GUN_ITEMS.get(type);
    }
}
