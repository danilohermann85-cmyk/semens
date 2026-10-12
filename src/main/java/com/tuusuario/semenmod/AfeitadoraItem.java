package com.tuusuario.semenmod;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * La afeitadora: 100 usos.
 *  - Arma: 4 de dano por golpe (1 de la mano + 3 de bonus).
 *  - Clic derecho en el marco de concreto: abre el portal de Springfield.
 */
public class AfeitadoraItem extends Item {
    public static final double BONUS_DAMAGE = 3.0;   // 1 (mano) + 3 = 4 de dano
    public static final double ATTACK_SPEED = -2.0;  // velocidad de ataque 2.0

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    public AfeitadoraItem(Settings settings) {
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

    /** Cada golpe gasta 1 uso. */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (!SpringfieldPortal.isFrameBlock(world.getBlockState(pos))) return ActionResult.PASS;

        SpringfieldPortal.Frame frame = SpringfieldPortal.findFrame(world, pos);
        if (frame == null) return ActionResult.PASS;
        if (world.isClient) return ActionResult.SUCCESS;

        SpringfieldPortal.light(world, frame);
        world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
        }
        return ActionResult.SUCCESS;
    }
}
