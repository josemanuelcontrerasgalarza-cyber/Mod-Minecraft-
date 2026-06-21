package com.kratos.arsenal.mixin;

import com.kratos.arsenal.client.ClientGunState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Aplica el zoom de la mira (ADS) multiplicando el FOV calculado por el motor.
 * Un factor menor que 1.0 acerca la imagen (efecto de mira).
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void kratos$applyZoom(Camera camera, float tickDelta, boolean changingFov,
                                  CallbackInfoReturnable<Double> cir) {
        double zoom = ClientGunState.zoom;
        if (zoom != 1.0) {
            cir.setReturnValue(cir.getReturnValue() * zoom);
        }
    }
}
