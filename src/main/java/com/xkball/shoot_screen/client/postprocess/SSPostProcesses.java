package com.xkball.shoot_screen.client.postprocess;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class SSPostProcesses {

    @Nullable
    public static ShootScreenProcess SHOOT_SCREEN_PROCESS;
    
    public static void createPostProcess() {
        var window = Minecraft.getInstance().getWindow();
        SHOOT_SCREEN_PROCESS = new ShootScreenProcess(window.getWidth(), window.getHeight());
    }
    
    public static void resize(int width, int height) {
        if(SHOOT_SCREEN_PROCESS != null){
            SHOOT_SCREEN_PROCESS.resize(width, height);
        }
    }
}
