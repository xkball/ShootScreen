package com.xkball.shoot_screen.mixin;

import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    
    @Inject(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;flush()V", shift = At.Shift.AFTER))
    public void afterRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci){
        if(SSPostProcesses.SHOOT_SCREEN_PROCESS != null && ShootScreen.usingPostProcess()) {
            var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
            SSPostProcesses.SHOOT_SCREEN_PROCESS.apply(mainBuffer.getColorTextureId());
            mainBuffer.bindWrite(true);
        }
    }
    
}
