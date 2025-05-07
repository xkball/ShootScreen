package com.xkball.shoot_screen.mixin;

import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    
    
    @Inject(method = "resize",at = @At("HEAD"))
    public void onResize(int width, int height, CallbackInfo ci){
        SSPostProcesses.resize(width, height);
    }
}
