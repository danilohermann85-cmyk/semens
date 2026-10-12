package com.tuusuario.semenmod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SpringfieldDimension {
    public static final RegistryKey<World> KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(SemenMod.MOD_ID, "springfield"));

    // Donde apareces: delante de la casa, mirando hacia ella (este = yaw -90)
    private static final Vec3d ARRIVAL = new Vec3d(27.5, 47.0, 29.5);
    private static final float ARRIVAL_YAW = -90.0f;
    // Una esquina de barrera que sirve para saber si la casa ya fue construida
    private static final BlockPos MARKER = new BlockPos(10, 60, 10);
    private static final BlockPos ORIGIN = new BlockPos(0, 39, 0);

    private record ReturnPoint(RegistryKey<World> world, Vec3d pos, float yaw) {}

    private static final Map<UUID, ReturnPoint> RETURN_POINTS = new HashMap<>();
    private static final Map<UUID, long[]> TIMERS = new HashMap<>();

    /** Se llama cada tick mientras el jugador esta dentro de un bloque de portal. */
    public static void onPortalTick(ServerPlayerEntity player, BlockPos pos) {
        // Igual que en el Nether: tras teletransportarte tienes que salir del portal y volver a entrar
        if (player.hasPortalCooldown()) {
            player.resetPortalCooldown();
            TIMERS.remove(player.getUuid());
            return;
        }
        ServerWorld world = player.getServerWorld();
        long now = world.getTime();
        long[] t = TIMERS.get(player.getUuid());
        if (t == null || now - t[1] > 3) {
            t = new long[]{now, now};
            world.playSound(null, pos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 0.5f, 1.0f);
        } else {
            t[1] = now;
        }
        TIMERS.put(player.getUuid(), t);

        long needed = player.isCreative() ? 1 : 60;   // 3 segundos en supervivencia
        if (now - t[0] >= needed) {
            TIMERS.remove(player.getUuid());
            teleport(player);
        }
    }

    private static void teleport(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        ServerWorld current = player.getServerWorld();

        if (current.getRegistryKey().equals(KEY)) {
            // Volver: al punto desde donde entraste o, si no se sabe, al spawn del overworld
            ReturnPoint rp = RETURN_POINTS.remove(player.getUuid());
            ServerWorld dest = rp != null ? server.getWorld(rp.world()) : null;
            if (dest == null) {
                dest = server.getOverworld();
                BlockPos sp = dest.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, dest.getSpawnPos());
                player.teleport(dest, sp.getX() + 0.5, sp.getY(), sp.getZ() + 0.5, player.getYaw(), player.getPitch());
            } else {
                player.teleport(dest, rp.pos().x, rp.pos().y, rp.pos().z, rp.yaw(), player.getPitch());
            }
        } else {
            ServerWorld target = server.getWorld(KEY);
            if (target == null) {
                player.sendMessage(Text.literal("La dimension de Springfield no esta cargada."), true);
                return;
            }
            RETURN_POINTS.put(player.getUuid(), new ReturnPoint(current.getRegistryKey(), player.getPos(), player.getYaw()));
            ensureBuilt(target);
            player.teleport(target, ARRIVAL.x, ARRIVAL.y, ARRIVAL.z, ARRIVAL_YAW, 0.0f);
        }
        player.resetPortalCooldown();
    }

    /** Coloca la casa de los Simpson (estructura guardada en el mod) la primera vez que alguien entra. */
    private static void ensureBuilt(ServerWorld world) {
        if (world.getBlockState(MARKER).isOf(Blocks.BARRIER)) return;
        Optional<StructureTemplate> template =
                world.getStructureTemplateManager().getTemplate(new Identifier(SemenMod.MOD_ID, "springfield_house"));
        if (template.isEmpty()) {
            System.err.println("[semenmod] No se encontro la estructura springfield_house");
            return;
        }
        StructurePlacementData data = new StructurePlacementData().setIgnoreEntities(false);
        template.get().place(world, ORIGIN, ORIGIN, data, world.getRandom(), Block.NOTIFY_LISTENERS);
    }
}
