package com.tuusuario.semenmod;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/** Clic derecho: te manda al End al instante, con 20 segundos de inmortalidad. Se gasta al usarlo. */
public class DaitakuItem extends Item {
    public DaitakuItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient || !(user instanceof ServerPlayerEntity player)) {
            return TypedActionResult.success(stack);
        }
        MinecraftServer server = player.getServer();
        ServerWorld end = server == null ? null : server.getWorld(World.END);
        if (end == null) {
            player.sendMessage(Text.literal("El End no esta disponible."), true);
            return TypedActionResult.fail(stack);
        }
        if (world.getRegistryKey() == World.END) {
            player.sendMessage(Text.literal("Ya estas en el End."), true);
            return TypedActionResult.fail(stack);
        }

        // Inmortal ANTES de teletransportar, por si pasa algo durante el viaje
        DaitakuImmortality.grant(player);

        // Plataforma de obsidiana del spawn del End: lugar seguro, el mismo que usa el portal normal
        ServerWorld.createEndSpawnPlatform(end);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.teleport(end, 100.5, 49.0, 0.5, 90.0f, 0.0f);
        end.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.sendMessage(Text.literal("Daitaku te llevo al End: inmortal por 20 segundos").formatted(Formatting.LIGHT_PURPLE), true);

        if (!player.getAbilities().creativeMode) stack.decrement(1);
        return TypedActionResult.success(stack, true);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Clic derecho: te lleva al End").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("20 segundos de inmortalidad. Se gasta al usarlo.").formatted(Formatting.DARK_PURPLE));
    }
}
