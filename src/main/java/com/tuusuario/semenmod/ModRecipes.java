package com.tuusuario.semenmod;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<DaitakuSuperRecipe> DAITAKU_SUPER_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            new Identifier(SemenMod.MOD_ID, "daitaku_super_crafting"),
            new SpecialRecipeSerializer<>(DaitakuSuperRecipe::new));

    public static void register() {}
}
