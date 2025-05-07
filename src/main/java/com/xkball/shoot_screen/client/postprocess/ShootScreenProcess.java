package com.xkball.shoot_screen.client.postprocess;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.xkball.shoot_screen.client.shader.SSShaders;
import com.xkball.shoot_screen.data.CustomBackgroundData;
import com.xkball.shoot_screen.utils.MathUtils;
import com.xkball.shoot_screen.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.Objects;

public class ShootScreenProcess extends PostProcess{
    
    private static final float[] WINDOW_UV = {0,0, 1,0, 1,1, 0,1};
    private final RenderTarget swap;
    private int backgroundTexture;
    public Matrix3f homography;
    public Matrix3f homographyMouse;
    public Vector2f center;
    public CustomBackgroundData data;
    public Matrix4f lightData;
    
    public ShootScreenProcess(int xSize, int ySize) {
        super(xSize, ySize);
        this.swap = new MainTarget(xSize, ySize);
        this.swap.setClearColor(0,0,0,0);
        this.update();
//        var ld = new Vector2f(0.106f,0.249f);
//        var rd = new Vector2f(0.952f,0.256f);
//        var ru = new Vector2f(0.951f,0.92f);
//        var lu = new Vector2f(0.0879f,0.876f);

    }
    
    public static Matrix3f createScreenHomography(Vector2f leftDown, Vector2f rightDown, Vector2f rightUp, Vector2f leftUp) {
        return MathUtils.resolveHomography(WINDOW_UV,new float[]{leftDown.x,leftDown.y,rightDown.x,rightDown.y,rightUp.x,rightUp.y,leftUp.x,leftUp.y}).invert();
    }
    
    public static Matrix3f createMouseHomography(Vector2f leftDown, Vector2f rightDown, Vector2f rightUp, Vector2f leftUp) {
        return MathUtils.resolveHomography(WINDOW_UV,new float[]{leftUp.x,1-leftUp.y,rightUp.x,1-rightUp.y,rightDown.x,1-rightDown.y,leftDown.x,1-leftDown.y}).invert();
    }
    
    public void update(){
        if(CustomBackgroundData.currentUse!=null){
            this.data = Objects.requireNonNull(CustomBackgroundData.currentUse);
            var ld = data.screenRect().leftDown();
            var rd = data.screenRect().rightDown();
            var ru = data.screenRect().rightUp();
            var lu = data.screenRect().leftUp();
            this.center = MathUtils.centerPoint(ld,rd,ru,lu);
            this.homographyMouse = createMouseHomography(ld,rd,ru,lu);
            this.homography = createScreenHomography(ld,rd,ru,lu);
            this.backgroundTexture = Minecraft.getInstance().getTextureManager().getTexture(VanillaUtils.modRL("textures/custom_background/"+data.name()+".png")).getId();
            this.lightData = data.packLightData();
        }
        else {
            this.data = null;
        }
    }
    
    @Override
    public void resize(int xSize, int ySize) {
        super.resize(xSize, ySize);
        this.swap.resize(xSize,ySize, Minecraft.ON_OSX);
    }
    
    public boolean ready(){
        return this.data != null;
    }
    
    @Override
    public void apply(int inputTexture) {
        if(!this.ready()) return;
        this.swap.clear(Minecraft.ON_OSX);
        this.processOnce(SSShaders.SHOOT_SCREEN_SHADER,inputTexture,swap,
                s -> {
            s.setSampler("Background", backgroundTexture);
            s.safeGetUniform("ProjMat").set(this.projectionMatrix);
            s.safeGetUniform("OutSize").set((float) xSize, (float) ySize);
            s.safeGetUniform("Homography").set(homography);
            s.safeGetUniform("LightData").set(lightData);
        });
        VanillaUtils.ClientHandler.copyFrameBufferColorTo(swap,Minecraft.getInstance().getMainRenderTarget().frameBufferId);
    }
}
