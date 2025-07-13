package com.flechazo.worldevent;

import com.flechazo.worldevent.api.WorldEventAPI;
import com.flechazo.worldevent.core.EventRegistry;
import com.flechazo.worldevent.core.EventTriggerManager;
import com.flechazo.worldevent.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WorldEvent.MOD_ID)
public class WorldEvent {
    public static final String MOD_ID = "world_event";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static WorldEvent INSTANCE;
    
    public WorldEvent() {
        INSTANCE = this;

        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EventTriggerManager.class);
        
        LOGGER.info("WorldEvent mod initialized");
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.init();

            EventRegistry.init();

            WorldEventAPI.init();
            
            LOGGER.info("WorldEvent common setup completed");
        });
    }
    
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}