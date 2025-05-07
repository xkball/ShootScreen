package com.xkball.shoot_screen.utils;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import org.joml.Vector2f;

import java.util.List;

public class SSCodecs {
    public static final Codec<Vector2f> VECTOR2F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    fList -> Util.fixedSize(fList, 2).map(fList_ -> new Vector2f(fList_.getFirst(), fList_.get(1))),
                    vec2 -> List.of(vec2.x(), vec2.y())
            );
}
