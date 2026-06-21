package com.kratos.arsenal.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Teclas configurables del mod: recargar, cambiar modo de disparo y apuntar.
 */
@Environment(EnvType.CLIENT)
public final class ModKeyBindings {

    private static final String CATEGORY = "key.category.kratos_arsenal";

    public static KeyBinding reload;
    public static KeyBinding fireMode;
    public static KeyBinding aim;

    private ModKeyBindings() {}

    public static void register() {
        reload = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kratos_arsenal.reload", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY));
        fireMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kratos_arsenal.fire_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY));
        aim = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.kratos_arsenal.aim", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY));
    }
}
