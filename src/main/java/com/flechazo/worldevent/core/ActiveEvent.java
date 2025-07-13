package com.flechazo.worldevent.core;

import com.flechazo.worldevent.Config;
import com.flechazo.worldevent.WorldEvent;
import com.flechazo.worldevent.api.effect.EffectContext;
import com.flechazo.worldevent.data.EventMessages;
import com.flechazo.worldevent.data.WorldEventDefinition;
import com.flechazo.worldevent.network.NetworkHandler;
import com.flechazo.worldevent.network.packets.EventStartPacket;
import com.flechazo.worldevent.network.packets.EventEndPacket;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class ActiveEvent {
    private final WorldEventDefinition definition;
    private final ServerLevel level;
    private final int startTick;
    private final UUID eventId;
    private final BlockPos centerPos;
    private final Set<ChunkPos> forcedChunks;
    private boolean started = false;
    
    public ActiveEvent(WorldEventDefinition definition, ServerLevel level, int startTick) {
        this.definition = definition;
        this.level = level;
        this.startTick = startTick;
        this.eventId = UUID.randomUUID();
        this.centerPos = findEventCenter(level);
        this.forcedChunks = new HashSet<>();
    }
    
    public void start() {
        if (started) return;
        started = true;

        forceLoadChunks();

        sendStartMessage();

        if (Config.syncVisualEffects) {
            syncToClients(true);
        }

        executeEffects(true);
        
        WorldEvent.LOGGER.info("Started event {} at {} in {}", 
            definition.id(), centerPos, level.dimension().location());
    }
    
    public void update(int currentTick) {
        if (!started) return;

        executeEffects(false);
    }
    
    public void end() {
        if (!started) return;

        releaseChunks();

        sendEndMessage();

        if (Config.syncVisualEffects) {
            syncToClients(false);
        }
        
        WorldEvent.LOGGER.info("Ended event {} at {} in {}", 
            definition.id(), centerPos, level.dimension().location());
    }
    
    public boolean isExpired(int currentTick) {
        return (currentTick - startTick) >= definition.impactScope().durationTicks();
    }
    
    private void executeEffects(boolean isStart) {
        EffectContext context = new EffectContext(level, centerPos, eventId, isStart);
        
        definition.effects().forEach(effect -> {
            try {
                effect.execute(context);
            } catch (Exception e) {
                WorldEvent.LOGGER.error("Failed to execute effect for event {}: {}", 
                    definition.id(), e.getMessage(), e);
            }
        });
    }
    
    private void forceLoadChunks() {
        int radius = Math.min(definition.impactScope().radiusChunks(), Config.maxChunkLoadRadius);
        ChunkPos centerChunk = new ChunkPos(centerPos);
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                ChunkPos chunkPos = new ChunkPos(centerChunk.x + x, centerChunk.z + z);
                if (ForgeChunkManager.forceChunk(level, WorldEvent.MOD_ID, eventId, chunkPos.x, chunkPos.z, true, true)) {
                    forcedChunks.add(chunkPos);
                }
            }
        }
        
        if (Config.enableDebugLogging) {
            WorldEvent.LOGGER.debug("Force loaded {} chunks for event {}", forcedChunks.size(), definition.id());
        }
    }
    
    private void releaseChunks() {
        forcedChunks.forEach(chunkPos ->
            ForgeChunkManager.forceChunk(level, WorldEvent.MOD_ID, eventId, chunkPos.x, chunkPos.z, false, true));
        forcedChunks.clear();
    }
    
    private void sendStartMessage() {
        definition.messages().flatMap(EventMessages::getStartComponent).ifPresent(component -> {
            for (ServerPlayer player : getPlayersInRange()) {
                player.sendSystemMessage(component);
            }
        });
    }
    
    private void sendEndMessage() {
        definition.messages().flatMap(EventMessages::getEndComponent).ifPresent(component -> {
            for (ServerPlayer player : getPlayersInRange()) {
                player.sendSystemMessage(component);
            }
        });
    }
    
    private void syncToClients(boolean starting) {
        for (ServerPlayer player : getPlayersInRange()) {
            if (starting) {
                NetworkHandler.sendToPlayer(new EventStartPacket(eventId, definition.id(), centerPos), player);
            } else {
                NetworkHandler.sendToPlayer(new EventEndPacket(eventId), player);
            }
        }
    }
    
    private Set<ServerPlayer> getPlayersInRange() {
        Set<ServerPlayer> players = new HashSet<>();
        int radius = definition.impactScope().radiusChunks() * 16;
        
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(centerPos) <= radius * radius) {
                players.add(player);
            }
        }
        
        return players;
    }
    
    private BlockPos findEventCenter(ServerLevel level) {
        // 简单实现：随机选择一个位置
        // TODO 实现更复杂更精细的逻辑
        var random = level.random;
        int x = random.nextInt(1000) - 500;
        int z = random.nextInt(1000) - 500;
        int y = level.getHeight();
        return new BlockPos(x, y, z);
    }
}