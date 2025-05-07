package com.xkball.shoot_screen.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import com.xkball.shoot_screen.utils.ThrowableSupplier;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = ShootScreen.MODID,bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class SSShaders {
    
    public static ShaderInstance SHOOT_SCREEN_SHADER;
    
    @SubscribeEvent
    public static void onRegShader(RegisterShadersEvent event) {
        var res = event.getResourceProvider();
        var testShader = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res, VanillaUtils.modRL("shoot_screen_shader"), DefaultVertexFormat.POSITION),
                "Failed to create test shader");
        event.registerShader(testShader,s -> {
            SHOOT_SCREEN_SHADER = s;
            SSPostProcesses.createPostProcess();
        });
    }
}
