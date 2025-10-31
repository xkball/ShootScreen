package com.xkball.shoot_screen.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public record WindowState(int x, int y, int width, int height, boolean asWallpaper, boolean fullscreen) {
    
    public void apply(){
        if(!asWallpaper){
            VanillaUtils.ClientHandler.cancelWindowAsBG();
        }
        var mcWindow = Minecraft.getInstance().getWindow();
        var window = mcWindow.getWindow();
        GLFW.glfwSetWindowPos(window, x, y);
        GLFW.glfwSetWindowSize(window, width, height);
        if(mcWindow.isFullscreen() != fullscreen){
            mcWindow.toggleFullScreen();
            mcWindow.updateDisplay();
        }
        if(asWallpaper){
            VanillaUtils.ClientHandler.setWindowAsBG(width,height);
        }
    }
    
    public static WindowState current(){
        var mcWindow = Minecraft.getInstance().getWindow();
        return new WindowState(mcWindow.getX(), mcWindow.getY(), mcWindow.getWidth(), mcWindow.getHeight(), VanillaUtils.ClientHandler.isWindowAsBG(), mcWindow.isFullscreen());
    }
    
    public static WindowState withASWallpaper(){
        var current = current();
        var screenManager = Minecraft.getInstance().virtualScreen.screenManager;
        var monitor = screenManager.getMonitor(GLFW.glfwGetPrimaryMonitor());
        //noinspection DataFlowIssue
        return new WindowState(current().x(), current().y(), monitor.getCurrentMode().getWidth(), monitor.getCurrentMode().getHeight(), true, false);
    }
}
