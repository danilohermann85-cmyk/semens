package com.tuusuario.semenmod;

import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SEMEN_DROP = register("semen_drop", new Item(new Item.Settings()));

    // Se encuentra en cofres de aldeas; 2 de estas = espada
    public static final Item SANITARY_PAD = register("sanitary_pad", new Item(new Item.Settings()));

    // Igual que la netherite: ataque base 3, velocidad -2.4, a prueba de fuego
    public static final Item MENSTRUAL_SWORD = register("menstrual_sword",
            new SwordItem(MenstrualToolMaterial.INSTANCE, 3, -2.4f, new Item.Settings().fireproof()));

    // Comida y semilla a la vez: clic derecho en tierra labrada lo planta, 2 a 5 al cosechar
    public static final Item PICKLE_RICK = register("pickle_rick",
            new AliasedBlockItem(ModBlocks.PICKLE_RICK_CROP, new Item.Settings()
                    .food(new FoodComponent.Builder().hunger(4).saturationModifier(0.6f).build())));

    // Abre el portal de Springfield (clic derecho en el marco de concreto)
    public static final Item AFEITADORA = register("afeitadora",
            new AfeitadoraItem(new Item.Settings().maxDamage(100)));

    // Solo se consigue encontrandola escondida en la casa de los Simpson (hay 9)
    public static final Item DUFF_BEER = register("duff_beer", new Item(new Item.Settings()));

    // Resultado del super crafteo (576 Bloques de Daitaku): clic derecho = viaje al End
    public static final Item DAITAKU = register("daitaku",
            new DaitakuItem(new Item.Settings().maxCount(16).rarity(Rarity.EPIC)));

    // Sin receta: solo la suelta El Barbero al morir. 999 de dano, ataque lento.
    public static final Item RECORTADORA = register("recortadora",
            new RecortadoraItem(new Item.Settings().maxDamage(500).rarity(Rarity.EPIC).fireproof()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(SemenMod.MOD_ID, name), item);
    }

    // Llamar a esto fuerza la carga de la clase y el registro de los items
    public static void register() {}
}
