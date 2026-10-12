package com.tuusuario.semenmod;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    // Sin item de bloque propio: el item "Pepinillo Rick" lo planta (AliasedBlockItem)
    public static final Block PICKLE_RICK_CROP = Registry.register(
            Registries.BLOCK,
            new Identifier(SemenMod.MOD_ID, "pickle_rick_crop"),
            new PickleRickCropBlock(AbstractBlock.Settings.copy(Blocks.WHEAT)));

    // Portal amarillo de la dimension de Springfield (copia las propiedades del portal del Nether)
    public static final Block SPRINGFIELD_PORTAL = Registry.register(
            Registries.BLOCK,
            new Identifier(SemenMod.MOD_ID, "springfield_portal"),
            new SpringfieldPortalBlock(AbstractBlock.Settings.copy(Blocks.NETHER_PORTAL)));

    // Bloque de Duff: 9 cervezas = 1 bloque (como el hierro), sale tambien de vuelta en 9 cervezas
    public static final Block DUFF_BLOCK = Registry.register(
            Registries.BLOCK,
            new Identifier(SemenMod.MOD_ID, "duff_block"),
            new DuffBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Item DUFF_BLOCK_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(SemenMod.MOD_ID, "duff_block"),
            new BlockItem(DUFF_BLOCK, new Item.Settings()));

    // Bloque blanco con la imagen de Daitaku en todos sus lados (2 bloques por receta)
    public static final Block DAITAKU_BLOCK = Registry.register(
            Registries.BLOCK,
            new Identifier(SemenMod.MOD_ID, "daitaku_block"),
            new Block(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL)));
    public static final Item DAITAKU_BLOCK_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(SemenMod.MOD_ID, "daitaku_block"),
            new BlockItem(DAITAKU_BLOCK, new Item.Settings()));

    public static void register() {}
}
