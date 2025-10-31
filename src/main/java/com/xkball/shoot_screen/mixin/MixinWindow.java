package com.xkball.shoot_screen.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.utils.TheSystemTray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class MixinWindow {
    
    @Inject(method = "<init>",at = @At("RETURN"))
    public void onInit(WindowEventHandler eventHandler, ScreenManager screenManager, DisplayData displayData, String preferredFullscreenVideoMode, String title, CallbackInfo ci){
        ShootScreen.tray = new TheSystemTray();
    }
    
    @Inject(method = "setTitle",at = @At("RETURN"))
    public void onSetTitle(String title, CallbackInfo ci){
        ShootScreen.currentWindowTitle = title;
    }
    
    @Inject(method = "close",at = @At("RETURN"))
    public void onClose(CallbackInfo ci){
        ShootScreen.tray.closeTray();
    }
}
