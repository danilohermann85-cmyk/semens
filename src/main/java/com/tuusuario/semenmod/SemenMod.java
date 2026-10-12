package com.tuusuario.semenmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SemenMod implements ModInitializer {

    public static final String MOD_ID = "semenmod";

    public static final ArmorMaterial SEMEN_MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurability(ArmorItem.Type type) {
            return 1000;
        }

        @Override
        public int getProtection(ArmorItem.Type type) {
            return switch (type) {
                case HELMET -> 5;
                case CHESTPLATE -> 8;
                case LEGGINGS -> 5;
                case BOOTS -> 2; // total 20 con el set completo
            };
        }

        @Override
        public int getEnchantability() {
            return 25;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ENTITY_SLIME_SQUISH;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(ModItems.SEMEN_DROP);
        }

        @Override
        public String getName() {
            return "semenmod:semen"; // busca semen_layer_1/2.png en assets/semenmod
        }

        @Override
        public float getToughness() {
            return 4.0f;
        }

        @Override
        public float getKnockbackResistance() {
            return 0.2f;
        }
    };

    public static final Item SEMEN_HELMET = new ArmorItem(SEMEN_MATERIAL, ArmorItem.Type.HELMET, new Item.Settings());
    public static final Item SEMEN_CHESTPLATE = new ArmorItem(SEMEN_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings());
    public static final Item SEMEN_LEGGINGS = new ArmorItem(SEMEN_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings());
    public static final Item SEMEN_BOOTS = new ArmorItem(SEMEN_MATERIAL, ArmorItem.Type.BOOTS, new Item.Settings());

    @Override
    public void onInitialize() {
        ModSounds.register();
        ModEntities.register();
        ModBlocks.register();
        ModRecipes.register();
        DaitakuImmortality.register();
        ModItems.register();

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "semen_helmet"), SEMEN_HELMET);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "semen_chestplate"), SEMEN_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "semen_leggings"), SEMEN_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "semen_boots"), SEMEN_BOOTS);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(SEMEN_HELMET);
            entries.add(SEMEN_CHESTPLATE);
            entries.add(SEMEN_LEGGINGS);
            entries.add(SEMEN_BOOTS);
            entries.add(ModItems.MENSTRUAL_SWORD);
            entries.add(ModItems.RECORTADORA);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->
                {
            entries.add(ModItems.SEMEN_DROP);
            entries.add(ModItems.SANITARY_PAD);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries ->
                entries.add(ModItems.PICKLE_RICK));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->
                { entries.add(ModItems.AFEITADORA); entries.add(ModItems.DAITAKU); });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries ->
                entries.add(ModItems.DUFF_BEER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries ->
                { entries.add(ModBlocks.DUFF_BLOCK_ITEM); entries.add(ModBlocks.DAITAKU_BLOCK_ITEM); });

        ModLootTables.register();
        SleepEventHandler.registerEvents();

        // Efectos con el set completo puesto
        ServerTickEvents.END_SERVER_TICK.register(server ->
            server.getPlayerManager().getPlayerList().forEach(player -> {
                boolean fullSet =
                        player.getEquippedStack(EquipmentSlot.HEAD).isOf(SEMEN_HELMET)
                        && player.getEquippedStack(EquipmentSlot.CHEST).isOf(SEMEN_CHESTPLATE)
                        && player.getEquippedStack(EquipmentSlot.LEGS).isOf(SEMEN_LEGGINGS)
                        && player.getEquippedStack(EquipmentSlot.FEET).isOf(SEMEN_BOOTS);
                if (fullSet) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 1, true, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 1, true, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 40, 0, true, false, true));
                }
            }));
    }
}
