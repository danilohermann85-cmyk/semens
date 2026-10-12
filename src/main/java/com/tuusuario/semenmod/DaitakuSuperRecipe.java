package com.tuusuario.semenmod;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

/**
 * "Super crafteo": mesa de crafteo 3x3 con 64 Bloques de Daitaku en CADA casilla
 * (9 x 64 = 576 bloques) -> 1 item Daitaku.
 */
public class DaitakuSuperRecipe extends SpecialCraftingRecipe {
    public static final int PER_SLOT = 64;

    public DaitakuSuperRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inv, World world) {
        if (inv.getWidth() != 3 || inv.getHeight() != 3) return false;
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isOf(ModBlocks.DAITAKU_BLOCK_ITEM) || s.getCount() < PER_SLOT) return false;
        }
        return true;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryManager) {
        return new ItemStack(ModItems.DAITAKU);
    }

    /**
     * Minecraft solo quita 1 item por casilla al craftear. Aqui dejamos cada casilla en 1
     * justo antes de que se haga esa resta, asi se gastan los 64 de cada una.
     */
    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inv) {
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty()) s.setCount(1);
        }
        return DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return ModRecipes.DAITAKU_SUPER_SERIALIZER;
    }
}
