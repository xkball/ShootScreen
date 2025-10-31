package com.xkball.shoot_screen.data.datagen;

import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.data.CustomBackgroundData;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.concurrent.CompletableFuture;

public class CustomBackgroundProvider extends JsonCodecProvider<CustomBackgroundData> {
    
    public CustomBackgroundProvider(PackOutput output , CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, PackOutput.Target.RESOURCE_PACK, "custom_background", PackType.CLIENT_RESOURCES, CustomBackgroundData.CODEC, lookupProvider, ShootScreen.MODID, existingFileHelper);
    }
    
    @Override
    protected void gather() {
        this.unconditional(VanillaUtils.modRL("example"),
                new CustomBackgroundData("example_background",
                        new CustomBackgroundData.ScreenRect(
                                new Vector2f(0.205f,0.339f),
                                new Vector2f(0.810f,0.3347f),
                                new Vector2f(0.817f,0.9186f),
                                new Vector2f(0.196f,0.9157f)),
                        new Vector3f(0.6f,0.5f,1f),
                        new Vector3f(1.7f,1.0f,2f),
                        new Vector3f(1f,1f,1f),
                        0.2f,
                        0.2f,
                        1f,
                        32));
//        this.unconditional(VanillaUtils.modRL("image1"),
//                new CustomBackgroundData("image1",
//                        new CustomBackgroundData.ScreenRect(
//                                new Vector2f(0.106f,0.249f),
//                                new Vector2f(0.952f,0.256f),
//                                new Vector2f(0.951f,0.92f),
//                                new Vector2f(0.0879f,0.876f)),
//                        new Vector3f(0.6f,0.5f,1f),
//                        new Vector3f(1.7f,1.0f,2f),
//                        new Vector3f(1f,1f,1f),
//                        0.6f,
//                        0.2f,
//                        10f,
//                        16));
        this.unconditional(VanillaUtils.modRL("image2"),
                new CustomBackgroundData("image2",
                        new CustomBackgroundData.ScreenRect(
                                new Vector2f(0.1468f,0.2833f),
                                new Vector2f(0.8281f,0.3052f),
                                new Vector2f(0.7906f,0.7771f),
                                new Vector2f(0.1593f,0.7718f)),
                        new Vector3f(0.6f,0.5f,1f),
                        new Vector3f(1.7f,1.0f,2f),
                        new Vector3f(1f,1f,1f),
                        0.3f,
                        0.2f,
                        1f,
                        16));
        this.unconditional(VanillaUtils.modRL("image3"),
                new CustomBackgroundData("image3",
                        new CustomBackgroundData.ScreenRect(
                                new Vector2f(0.0593f,0.1764f),
                                new Vector2f(0.5593f,0.2117f),
                                new Vector2f(0.5468f,0.7411f),
                                new Vector2f(0.0625f,0.7458f)),
                        new Vector3f(0.6f,0.5f,1f),
                        new Vector3f(1.7f,1.0f,2f),
                        new Vector3f(1f,1f,1f),
                        0.3f,
                        0.2f,
                        1f,
                        16));
    }
}
