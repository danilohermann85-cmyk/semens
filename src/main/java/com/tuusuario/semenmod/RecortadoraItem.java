package com.tuusuario.semenmod;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * La Recortadora: ataca lento (0.5 golpes por segundo) pero hace 999 de dano.
 * No se craftea: solo la suelta El Barbero al morir.
 */
public class RecortadoraItem extends Item {
    public static final double BONUS_DAMAGE = 998.0;   // 1 (mano) + 998 = 999 de dano
    public static final double ATTACK_SPEED = -3.5;    // 4.0 - 3.5 = 0.5 golpes por segundo

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    public RecortadoraItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> b = ImmutableMultimap.builder();
        b.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(
                ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", BONUS_DAMAGE, EntityAttributeModifier.Operation.ADDITION));
        b.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(
                ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", ATTACK_SPEED, EntityAttributeModifier.Operation.ADDITION));
        this.attributes = b.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? attributes : super.getAttributeModifiers(slot);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Lenta, pero mata de un golpe").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Premio por derrotar a El Barbero").formatted(Formatting.GOLD));
    }
}
