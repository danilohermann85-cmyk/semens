package com.tuusuario.semenmod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

/**
 * Estructura que invoca al Barbero (se arma de abajo hacia arriba y el bloque de Duff va AL FINAL):
 *
 *            [ Bloque de Duff   ]
 *            [ Concreto amarillo]
 *            [ Concreto negro   ]
 *     [Hierro][Hierro][Hierro]     <- base de 3x3 bloques de hierro (9 en total)
 *     [Hierro][Hierro][Hierro]
 *     [Hierro][Hierro][Hierro]
 *
 * La columna va sobre el hierro del centro.
 */
public class BarberoSummon {
    private static BlockPattern pattern;

    private static BlockPattern pattern() {
        if (pattern == null) {
            pattern = BlockPatternBuilder.start()
                    .aisle("   ", "   ", "   ", "III")
                    .aisle(" D ", " Y ", " B ", "III")
                    .aisle("   ", "   ", "   ", "III")
                    .where('I', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
                    .where('B', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.BLACK_CONCRETE)))
                    .where('Y', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.YELLOW_CONCRETE)))
                    .where('D', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(ModBlocks.DUFF_BLOCK)))
                    .build();
        }
        return pattern;
    }

    /** Se llama al colocar un Bloque de Duff. */
    public static void tryCreate(World world, BlockPos pos) {
        if (world.isClient) return;
        BlockPattern.Result result = pattern().searchAround(world, pos);
        if (result == null || result.getUp() != Direction.UP) return;   // solo si esta de pie

        if (world.getDifficulty() == Difficulty.PEACEFUL) {
            PlayerEntity p = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 12.0, false);
            if (p != null) p.sendMessage(Text.literal("El Barbero no aparece en dificultad pacifica."), true);
            return;
        }

        // Quita la estructura: base 3x3 (fila de abajo) + columna del centro
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) clear(world, result.translate(x, 3, z).getBlockPos());
        }
        for (int y = 0; y < 3; y++) clear(world, result.translate(1, y, 1).getBlockPos());

        BlockPos base = result.translate(1, 3, 1).getBlockPos();
        BarberoEntity boss = ModEntities.BARBERO.create(world);
        if (boss == null) return;
        boss.refreshPositionAndAngles(base.getX() + 0.5, base.getY(), base.getZ() + 0.5, 0.0f, 0.0f);
        world.spawnEntity(boss);
        world.syncGlobalEvent(1023, base, 0);   // el sonido de aparicion del Wither
    }

    private static void clear(World world, BlockPos pos) {
        world.syncWorldEvent(2001, pos, Block.getRawIdFromState(world.getBlockState(pos)));
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
    }
}
