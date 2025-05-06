package com.xkball.shoot_screen.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.utils.ThrowableSupplier;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = ShootScreen.MODID,bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
public class SSShaders {
    
    public static ShaderInstance TEST_SHADER;
    
    @SubscribeEvent
    public void onRegShader(RegisterShadersEvent event) {
        var res = event.getResourceProvider();
        TEST_SHADER = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res, VanillaUtils.modRL("test_shader"), DefaultVertexFormat.POSITION),
                "Failed to create test shader");
        event.registerShader(TEST_SHADER,s -> {});
    }
}
