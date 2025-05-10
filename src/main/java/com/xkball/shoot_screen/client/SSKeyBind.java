package com.xkball.shoot_screen.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.xkball.shoot_screen.ShootScreen;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class SSKeyBind {
    
    public static final Lazy<KeyMapping> SWITCH_KEY = Lazy.of(() -> new KeyMapping("keys.shoot_screen.switch", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z,"key.categories.misc"));
    
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(SWITCH_KEY.get());
    }
    
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameEventHandler{
        
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event){
            if(event.getAction() != InputConstants.PRESS || !SWITCH_KEY.get().isActiveAndMatches(InputConstants.getKey(event.getKey(), event.getScanCode()))) return;
            synchronized (ShootScreen.class){
                ShootScreen.enabled = !ShootScreen.enabled;
            }
        }
    }
    
}
