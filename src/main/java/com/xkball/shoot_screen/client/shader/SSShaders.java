package com.xkball.shoot_screen.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import com.xkball.shoot_screen.utils.ThrowableSupplier;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = ShootScreen.MODID,bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class SSShaders {
    
    public static ShaderInstance SHOOT_SCREEN_SHADER;
    public static ShaderInstance BLOOM_COMPOSITE_SHADER;
    public static ShaderInstance DOWN_SAMPLER_BLUR_SHADER;
    
    @SubscribeEvent
    public static void onRegShader(RegisterShadersEvent event) {
        var res = event.getResourceProvider();
        var testShader = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res, VanillaUtils.modRL("shoot_screen_shader"), DefaultVertexFormat.POSITION),
                "Failed to create test shader");
        event.registerShader(testShader,s -> SHOOT_SCREEN_SHADER = s);
        var bloomCompositeShader = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res,VanillaUtils.modRL("bloom_composite"),DefaultVertexFormat.POSITION));
        event.registerShader(bloomCompositeShader,s -> BLOOM_COMPOSITE_SHADER = bloomCompositeShader);
        var downSamplerBlurShader = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res,VanillaUtils.modRL("down_sampler_blur"),DefaultVertexFormat.POSITION));
        event.registerShader(downSamplerBlurShader,
            s -> {
            DOWN_SAMPLER_BLUR_SHADER = s;
            SSPostProcesses.createPostProcess();
        });
    }
    
}
