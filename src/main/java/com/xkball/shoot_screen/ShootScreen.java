package com.xkball.shoot_screen;

import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import com.xkball.shoot_screen.utils.TheSystemTray;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@Mod(value = ShootScreen.MODID,dist = Dist.CLIENT)
public class ShootScreen {
    
    public static final String MODID = "shoot_screen";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static volatile boolean enabled = false;
    public static String currentWindowTitle = "";
    public static TheSystemTray tray;

    public ShootScreen(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, SSConfig.SPEC);
    }
    
    public static boolean usingPostProcess() {
        return SSPostProcesses.SHOOT_SCREEN_PROCESS != null && SSPostProcesses.SHOOT_SCREEN_PROCESS.ready() && enabled;
    }
    
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
 
        }
    }
}
