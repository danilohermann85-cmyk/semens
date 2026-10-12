package com.tuusuario.semenmod;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.UUID;

public class SleepEventHandler {

    // Ticks que lleva durmiendo cada jugador
    private static final HashMap<UUID, Integer> sleepingTicks = new HashMap<>();

    public static void registerEvents() {
        EntitySleepEvents.START_SLEEPING.register((entity, pos) -> {
            if (entity instanceof ServerPlayerEntity player) {
                sleepingTicks.put(player.getUuid(), 0);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID id = player.getUuid();
                if (player.isSleeping() && sleepingTicks.containsKey(id)) {
                    int ticks = sleepingTicks.get(id) + 1;
                    sleepingTicks.put(id, ticks);

                    // Sonido viscoso periodico mientras duerme
                    if (ticks % 10 == 0) {
                        player.getWorld().playSound(null, player.getBlockPos(),
                                SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 0.6f, 1.0f);
                    }
                }
            }
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> {
            if (entity instanceof ServerPlayerEntity player) {
                Integer total = sleepingTicks.remove(player.getUuid());
                // Si durmio al menos 2 segundos, recibe 1 gota
                if (total != null && total >= 40) {
                    player.getInventory().offerOrDrop(new ItemStack(ModItems.SEMEN_DROP, 1));
                    player.getWorld().playSound(null, player.getBlockPos(),
                            SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                }
            }
        });
    }
}
