package com.xkball.shoot_screen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@EventBusSubscriber(modid = ShootScreen.MODID, bus = EventBusSubscriber.Bus.MOD)
public class SSConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue DEFAULT_ENABLE_CONFIG = BUILDER.define("default_enable", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        synchronized (ShootScreen.class){
            ShootScreen.enabled = DEFAULT_ENABLE_CONFIG.get();
        }
    }
    
//    @SubscribeEvent
//    public static void onConfigReload(ModConfigEvent.Reloading event) {
//
//    }
}
