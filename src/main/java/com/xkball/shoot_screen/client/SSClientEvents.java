package com.xkball.shoot_screen.client;

import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

//@EventBusSubscriber(modid = ShootScreen.MODID,bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
public class SSClientEvents {
    
   // @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        //if(Minecraft.getInstance().screen != null) return;
        if(SSPostProcesses.SHOOT_SCREEN_PROCESS != null) {
            var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
            SSPostProcesses.SHOOT_SCREEN_PROCESS.apply(mainBuffer.getColorTextureId());
            mainBuffer.bindWrite(true);
        }
    }
    
    //@SubscribeEvent
    public static void afterRenderGUI(ScreenEvent.Render.Post event) {
        var guiGraphics = event.getGuiGraphics();
        var mouseHandler = Minecraft.getInstance().mouseHandler;
        guiGraphics.drawString(Minecraft.getInstance().font,(int)mouseHandler.xpos() + ":" + (int)mouseHandler.ypos(),0,0, VanillaUtils.getColor(255,255,255,255));
    }
    
    
}
