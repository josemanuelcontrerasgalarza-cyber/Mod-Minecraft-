package com.kratos.arsenal;

import com.kratos.arsenal.network.ModNetworking;
import com.kratos.arsenal.registry.ModBlocks;
import com.kratos.arsenal.registry.ModItemGroups;
import com.kratos.arsenal.registry.ModItems;
import com.kratos.arsenal.registry.ModSounds;
import com.kratos.arsenal.world.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Punto de entrada común (cliente + servidor) del mod "Kratos Arsenal".
 *
 * <p>Aquí se registran todos los contenidos del mod: bloques, objetos, sonidos,
 * grupos creativos, generación de mundo y los canales de red. La lógica
 * exclusiva de cliente (HUD, teclas, zoom, partículas locales) se inicializa
 * en {@code com.kratos.arsenal.client.KratosArsenalClient}.</p>
 */
public class KratosArsenal implements ModInitializer {

    /** Identificador único del mod. Se usa como namespace en todos los recursos. */
    public static final String MOD_ID = "kratos_arsenal";

    /** Logger compartido por todo el mod. */
    public static final Logger LOGGER = LoggerFactory.getLogger("Kratos Arsenal");

    /** Crea un {@link Identifier} en el namespace del mod. */
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("[Kratos Arsenal] Inicializando arsenal de armas...");

        // El orden importa: primero sonidos y bloques, luego objetos, luego el grupo creativo.
        ModSounds.register();
        ModBlocks.register();
        ModItems.register();
        ModItemGroups.register();

        // Canales de red (disparo, recarga, retroceso, etc.).
        ModNetworking.registerServerReceivers();

        // Generación de mineral de titanio en el mundo.
        ModWorldGen.register();

        LOGGER.info("[Kratos Arsenal] Arsenal cargado correctamente.");
    }
}
