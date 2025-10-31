package com.xkball.shoot_screen.client.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.xkball.shoot_screen.client.shader.SSShaders;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL46;

public class BloomPostProcess extends PostProcess {
    
    private final int samplerDepth;
    private final RenderTarget composite;
    private final RenderTarget[] downSamplersH;
    private final RenderTarget[] downSamplersV;
    
    public BloomPostProcess(int xSize, int ySize) {
        this(xSize,ySize,4);
    }
    
    private BloomPostProcess(int xSize, int ySize, int samplerDepth) {
        super(xSize, ySize);
        assert samplerDepth > 0;
        this.samplerDepth = samplerDepth;
        this.composite = createRenderTarget(xSize, ySize);
        this.downSamplersH = new RenderTarget[samplerDepth];
        this.downSamplersV = new RenderTarget[samplerDepth];
        for(var i = 0; i < samplerDepth; i++) {
            var factor = 2 << i;
            downSamplersH[i] = createRenderTarget(xSize/factor,ySize/factor);
            downSamplersV[i] = createRenderTarget(xSize/factor,ySize/factor);
        }
    }
    
    @Override
    public void resize(int xSize, int ySize) {
        super.resize(xSize, ySize);
        composite.resize(xSize,ySize,Minecraft.ON_OSX);
        for(var i = 0; i < samplerDepth; i++) {
            var factor = 2 << i;
            downSamplersH[i].resize(xSize/factor,ySize/factor,Minecraft.ON_OSX);
            downSamplersV[i].resize(xSize/factor,ySize/factor,Minecraft.ON_OSX);
        }
    }
    
    @Override
    public void apply(int inputTexture) {
        var src = inputTexture;
        var shader = SSShaders.DOWN_SAMPLER_BLUR_SHADER;
        for(var i = 0; i < samplerDepth; i++) {
            var h = downSamplersH[i];
            var v = downSamplersV[i];
            this.processOnce(shader,src,h, downSamplerBlurSetter(i,true));
            this.processOnce(shader,h,v, downSamplerBlurSetter(i,false));
            src = h.getColorTextureId();
        }
        
        var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
        shader = SSShaders.BLOOM_COMPOSITE_SHADER;
        this.processOnce(shader,mainBuffer,composite,bloomCompositeSetter(inputTexture));
        VanillaUtils.ClientHandler.copyFrameBufferColorTo(composite,mainBuffer);
    }
    
    private UniformSetter downSamplerBlurSetter(int i, boolean horizontal) {
        return s -> {
            var factor = 2 << i;
            s.safeGetUniform("OutSize").set((float) xSize/factor, (float) ySize/factor);
            s.safeGetUniform("Factor").set(factor);
            s.safeGetUniform("Radius").set((i + 1) * 6 + 1);
            s.safeGetUniform("BlurDir").set(horizontal ? 1f : 0f, horizontal ? 0f : 1f);
        };
    }
    
    private UniformSetter bloomCompositeSetter(int input){
        return s -> {
            s.safeGetUniform("OutSize").set((float) xSize, (float) ySize);
            s.setSampler("HighLight",input);
            for(var i = 0; i < samplerDepth; i++) {
                s.setSampler("BlurTexture" + (i+1),downSamplersV[i]);
            }
        };
    }
    
    private static RenderTarget createRenderTarget(int width, int height) {
        var renderTarget = new TextureTarget(width, height, false, Minecraft.ON_OSX);
        renderTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderTarget.setFilterMode(GL46.GL_LINEAR);
        return renderTarget;
    }
    
}
