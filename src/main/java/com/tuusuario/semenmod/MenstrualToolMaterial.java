package com.tuusuario.semenmod;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

// Mejor que la netherite: durabilidad 2031 -> 3000, velocidad 9 -> 10, daño 4 -> 5
public class MenstrualToolMaterial implements ToolMaterial {
    public static final MenstrualToolMaterial INSTANCE = new MenstrualToolMaterial();

    @Override public int getDurability() { return 3000; }
    @Override public float getMiningSpeedMultiplier() { return 10.0f; }
    @Override public float getAttackDamage() { return 5.0f; } // espada: 3 + 5 = 8 de daño (netherite = 7)
    @Override public int getMiningLevel() { return 4; }
    @Override public int getEnchantability() { return 25; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.ofItems(ModItems.SANITARY_PAD); }
}
