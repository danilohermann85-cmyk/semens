package com.tuusuario.semenmod;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Inmortalidad temporal: mientras dure, el jugador no recibe NINGUN dano. */
public class DaitakuImmortality {
    public static final int DURATION_TICKS = 20 * 20;   // 20 segundos

    private static final Map<UUID, Integer> UNTIL = new HashMap<>();

    public static void grant(ServerPlayerEntity player) {
        UNTIL.put(player.getUuid(), player.getServer().getTicks() + DURATION_TICKS);
    }

    public static void register() {
        // Devolver false cancela el dano
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player && entity.getServer() != null) {
                Integer until = UNTIL.get(player.getUuid());
                if (until != null) {
                    if (entity.getServer().getTicks() < until) return false;
                    UNTIL.remove(player.getUuid());
                }
            }
            return true;
        });
    }
}
