package com.tuusuario.semenmod;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class ModLootTables {

    // Probabilidad de que un cofre de aldea tenga toallas (0.5 = 50%)
    private static final float CHANCE = 0.5f;

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Solo tablas originales del juego y solo cofres de aldeas
            if (source.isBuiltin() && id.getPath().startsWith("chests/village/")) {
                LootPool.Builder pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(CHANCE))
                        .with(ItemEntry.builder(ModItems.SANITARY_PAD)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))));
                tableBuilder.pool(pool);
            }
        });
    }
}
