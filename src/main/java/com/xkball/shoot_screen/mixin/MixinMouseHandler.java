package com.xkball.shoot_screen.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.xkball.shoot_screen.client.postprocess.SSPostProcesses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Debug
@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    
    @Shadow @Final private Minecraft minecraft;
    
    @Shadow private boolean mouseGrabbed;
    
    @Shadow private double xpos;
    
    @Shadow private double ypos;
    
    @WrapMethod(method = "onMove")
    public void wrapMouseMove(long windowPointer, double xPos, double yPos, Operation<Void> original){
        if(SSPostProcesses.SHOOT_SCREEN_PROCESS != null && SSPostProcesses.SHOOT_SCREEN_PROCESS.ready()){
            var remappedPos = shootScreen$remapMousePos(xPos, yPos);
            xPos = remappedPos.x;
            yPos = remappedPos.y;
        }
        original.call(windowPointer, xPos, yPos);
    }
    
    @WrapMethod(method = "grabMouse")
    public void wrapGrabMouse(Operation<Void> original){
        boolean flag = this.minecraft.isWindowActive() && !this.mouseGrabbed && SSPostProcesses.SHOOT_SCREEN_PROCESS != null && SSPostProcesses.SHOOT_SCREEN_PROCESS.ready();
        original.call();
        if(flag){
            shootScreen$setMouseInRemappedCenter();
        }
    }
    
    @WrapMethod(method = "releaseMouse")
    public void wrapReleaseMouse(Operation<Void> original){
        boolean flag = this.mouseGrabbed && SSPostProcesses.SHOOT_SCREEN_PROCESS != null && SSPostProcesses.SHOOT_SCREEN_PROCESS.ready();
        original.call();
        if(flag){
            shootScreen$setMouseInRemappedCenter();
        }
    }
    
    @Unique
    private void shootScreen$setMouseInRemappedCenter(){
        assert SSPostProcesses.SHOOT_SCREEN_PROCESS != null;
        var screenH = Minecraft.getInstance().getWindow().getHeight();
        var screenW = Minecraft.getInstance().getWindow().getWidth();
        GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(),
                SSPostProcesses.SHOOT_SCREEN_PROCESS.center.x*screenW,
                (1-SSPostProcesses.SHOOT_SCREEN_PROCESS.center.y)*screenH);
    }
    
    @Unique
    private Vector2f shootScreen$remapMousePos(double x, double y){
        assert SSPostProcesses.SHOOT_SCREEN_PROCESS != null;
        var screenH = Minecraft.getInstance().getWindow().getHeight();
        var screenW = Minecraft.getInstance().getWindow().getWidth();
        
        float pX = (float) (x/screenW);
        float pY = (float) (y/screenH);
        var homographyMatrix = SSPostProcesses.SHOOT_SCREEN_PROCESS.homographyMouse;
        var remappedPos = homographyMatrix.transform(new Vector3f(pX,pY,1));
        var xPos_ = remappedPos.x/remappedPos.z * screenW;
        var yPos_ = remappedPos.y/remappedPos.z * screenH;
        return new Vector2f(xPos_, yPos_);
    }
}
