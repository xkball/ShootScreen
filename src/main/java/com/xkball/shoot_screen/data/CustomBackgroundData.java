package com.xkball.shoot_screen.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.shoot_screen.ShootScreen;
import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import com.xkball.shoot_screen.utils.SSCodecs;
import com.xkball.shoot_screen.utils.ThrowableFunction;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public record CustomBackgroundData(String name,
                                   ScreenRect screenRect,
                                   Vector3f viewPos,
                                   Vector3f lightPos,
                                   Vector3f lightColor,
                                   float moireStrength,
                                   float ambientStrength,
                                   float specularStrength,
                                   int shininess
                                   ) {
    
    public static final Codec<CustomBackgroundData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.STRING.fieldOf("name").forGetter(CustomBackgroundData::name),
            ScreenRect.CODEC.fieldOf("screenRect").forGetter(CustomBackgroundData::screenRect),
            ExtraCodecs.VECTOR3F.fieldOf("viewPos").forGetter(CustomBackgroundData::viewPos),
            ExtraCodecs.VECTOR3F.fieldOf("lightPos").forGetter(CustomBackgroundData::lightPos),
            ExtraCodecs.VECTOR3F.fieldOf("lightColor").forGetter(CustomBackgroundData::lightColor),
            Codec.FLOAT.fieldOf("moireStrength").forGetter(CustomBackgroundData::moireStrength),
            Codec.FLOAT.fieldOf("ambientStrength").forGetter(CustomBackgroundData::ambientStrength),
            Codec.FLOAT.fieldOf("specularStrength").forGetter(CustomBackgroundData::specularStrength),
            Codec.INT.fieldOf("shininess").forGetter(CustomBackgroundData::shininess)
    ).apply(ins, CustomBackgroundData::new));
    
    @Nullable
    public static volatile CustomBackgroundData currentUse;
    
    public Matrix4f packLightData(){
        return new Matrix4f(
                //ordered by column!
                new Vector4f(viewPos,0),
                new Vector4f(lightPos,0),
                new Vector4f(lightColor,0),
                new Vector4f(moireStrength,ambientStrength,specularStrength,shininess)
        );
    }
    
    public record ScreenRect(Vector2f leftDown, Vector2f rightDown, Vector2f rightUp, Vector2f leftUp) {
        
        public static final Codec<ScreenRect> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                SSCodecs.VECTOR2F.fieldOf("leftDown").forGetter(ScreenRect::leftDown),
                SSCodecs.VECTOR2F.fieldOf("rightDown").forGetter(ScreenRect::rightDown),
                SSCodecs.VECTOR2F.fieldOf("rightUp").forGetter(ScreenRect::rightUp),
                SSCodecs.VECTOR2F.fieldOf("leftUp").forGetter(ScreenRect::leftUp)
        ).apply(ins, ScreenRect::new));
    }
    
    @EventBusSubscriber(modid = ShootScreen.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CustomBackgroundDataLoader extends SimplePreparableReloadListener<List<CustomBackgroundData>> {
        
        public static final CustomBackgroundDataLoader INSTANCE = new CustomBackgroundDataLoader();
        
        public List<CustomBackgroundData> list = List.of();
        
        private CustomBackgroundDataLoader(){}
        
        
        @Override
        protected List<CustomBackgroundData> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            var resources = resourceManager.listResources("custom_background",rl -> true);
            return resources.values().stream()
                    .map(ThrowableFunction.unwrapOrThrow(VanillaUtils::readJsonFromResource))
                    .map(json -> CustomBackgroundData.CODEC.decode(JsonOps.INSTANCE, json))
                    .map(dr -> dr.getOrThrow().getFirst())
                    .toList();
        }
        
        @Override
        protected void apply(List<CustomBackgroundData> object, ResourceManager resourceManager, ProfilerFiller profiler) {
            this.list = object;
            var data = VanillaUtils.pickRandom(list);
            if(data != null) {
                CustomBackgroundData.currentUse = data;
                if (SSPostProcesses.SHOOT_SCREEN_PROCESS != null) {
                    SSPostProcesses.SHOOT_SCREEN_PROCESS.update();
                }
            }
            else {
                CustomBackgroundData.currentUse = null;
            }
        }
        
        @SubscribeEvent
        public static void onDataPackReload(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(INSTANCE);
        }
    }
}
