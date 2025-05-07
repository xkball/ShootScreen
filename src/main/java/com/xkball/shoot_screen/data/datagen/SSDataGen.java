package com.xkball.shoot_screen.data.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class SSDataGen {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var existingFileHelper = event.getExistingFileHelper();
        var dataGenerator = event.getGenerator();
        var runClient = event.includeClient();
        var runServer = event.includeServer();
        var packOutput = dataGenerator.getPackOutput();
        var registries = event.getLookupProvider();
        
        dataGenerator.addProvider(runClient,new CustomBackgroundProvider(packOutput,registries,existingFileHelper));
    }
}
