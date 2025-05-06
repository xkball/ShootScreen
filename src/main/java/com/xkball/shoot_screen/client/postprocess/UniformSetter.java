package com.xkball.shoot_screen.client.postprocess;

import net.minecraft.client.renderer.ShaderInstance;

@FunctionalInterface
public interface UniformSetter {
    void setUniforms(ShaderInstance shader);
}
