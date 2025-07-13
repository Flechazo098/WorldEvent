package com.flechazo.worldevent;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = WorldEvent.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 全局配置
    public static final ForgeConfigSpec.BooleanValue ENABLE_WORLD_EVENTS = BUILDER
            .comment("Enable world events system")
            .define("enableWorldEvents", true);
    
    public static final ForgeConfigSpec.IntValue MAX_CONCURRENT_EVENTS = BUILDER
            .comment("Maximum number of concurrent events per dimension")
            .defineInRange("maxConcurrentEvents", 5, 1, 20);
    
    public static final ForgeConfigSpec.IntValue EVENT_CHECK_INTERVAL = BUILDER
            .comment("Interval in ticks between event trigger checks")
            .defineInRange("eventCheckInterval", 20, 1, 1200);
    
    // 性能配置
    public static final ForgeConfigSpec.IntValue MAX_CHUNK_LOAD_RADIUS = BUILDER
            .comment("Maximum chunk loading radius for events")
            .defineInRange("maxChunkLoadRadius", 5, 1, 16);
    
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING = BUILDER
            .comment("Enable debug logging for world events")
            .define("enableDebugLogging", false);
    
    // 网络配置
    public static final ForgeConfigSpec.BooleanValue SYNC_VISUAL_EFFECTS = BUILDER
            .comment("Synchronize visual effects to clients")
            .define("syncVisualEffects", true);
    
    public static final ForgeConfigSpec.IntValue NETWORK_UPDATE_INTERVAL = BUILDER
            .comment("Interval in ticks between network updates")
            .defineInRange("networkUpdateInterval", 10, 1, 100);
    
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    
    public static boolean enableWorldEvents;
    public static int maxConcurrentEvents;
    public static int eventCheckInterval;
    public static int maxChunkLoadRadius;
    public static boolean enableDebugLogging;
    public static boolean syncVisualEffects;
    public static int networkUpdateInterval;
    
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableWorldEvents = ENABLE_WORLD_EVENTS.get();
        maxConcurrentEvents = MAX_CONCURRENT_EVENTS.get();
        eventCheckInterval = EVENT_CHECK_INTERVAL.get();
        maxChunkLoadRadius = MAX_CHUNK_LOAD_RADIUS.get();
        enableDebugLogging = ENABLE_DEBUG_LOGGING.get();
        syncVisualEffects = SYNC_VISUAL_EFFECTS.get();
        networkUpdateInterval = NETWORK_UPDATE_INTERVAL.get();
    }
}