package com.tuusuario.semenmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Marco del portal (4 de ancho x 5 de alto, interior de 2x3), todo de concreto:
 *
 *   [esquina A] [azul] [azul] [esquina B]
 *   [amarillo ] [portal portal] [amarillo]   (x3 filas)
 *   [esquina A] [azul] [azul] [esquina B]
 *
 * Las esquinas de un lado son de una color y las del otro lado de la otra
 * (da igual de que lado quede cada una). Cambia los bloques aqui si quieres otros colores.
 */
public class SpringfieldPortal {
    public static final Block CORNER_A = Blocks.WHITE_CONCRETE;
    public static final Block CORNER_B = Blocks.ORANGE_CONCRETE;
    public static final Block EDGE = Blocks.BLUE_CONCRETE;     // arriba y abajo
    public static final Block SIDE = Blocks.YELLOW_CONCRETE;   // costados
    public static final int W = 4, H = 5;

    public record Frame(BlockPos origin, Direction right, Direction.Axis axis) {}

    public static boolean isFrameBlock(BlockState s) {
        return s.isOf(CORNER_A) || s.isOf(CORNER_B) || s.isOf(EDGE) || s.isOf(SIDE);
    }

    /** Busca un marco valido que contenga el bloque clicado (sin modificar nada). */
    public static Frame findFrame(World world, BlockPos clicked) {
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
            for (int i = 0; i < W; i++) {
                for (int j = 0; j < H; j++) {
                    BlockPos origin = clicked.offset(right.getOpposite(), i).down(j);
                    if (matches(world, origin, right)) return new Frame(origin, right, axis);
                }
            }
        }
        return null;
    }

    private static boolean matches(World world, BlockPos origin, Direction right) {
        Block left = world.getBlockState(origin).getBlock();
        if (left != CORNER_A && left != CORNER_B) return false;
        Block rightCorner = left == CORNER_A ? CORNER_B : CORNER_A;
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                BlockState s = world.getBlockState(origin.offset(right, i).up(j));
                boolean edgeRow = j == 0 || j == H - 1;
                boolean edgeCol = i == 0 || i == W - 1;
                if (edgeRow && edgeCol) {
                    if (!s.isOf(i == 0 ? left : rightCorner)) return false;
                } else if (edgeRow) {
                    if (!s.isOf(EDGE)) return false;
                } else if (edgeCol) {
                    if (!s.isOf(SIDE)) return false;
                } else if (!s.isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Rellena el interior con bloques de portal. */
    public static void light(World world, Frame f) {
        BlockState portal = ModBlocks.SPRINGFIELD_PORTAL.getDefaultState()
                .with(SpringfieldPortalBlock.AXIS, f.axis());
        for (int i = 1; i < W - 1; i++) {
            for (int j = 1; j < H - 1; j++) {
                world.setBlockState(f.origin().offset(f.right(), i).up(j), portal, Block.NOTIFY_ALL);
            }
        }
    }
}
