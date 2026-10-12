package com.tuusuario.semenmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Bloque de Duff: al ponerlo encima de la estructura correcta, aparece El Barbero. */
public class DuffBlock extends Block {
    public DuffBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BarberoSummon.tryCreate(world, pos);
    }
}
