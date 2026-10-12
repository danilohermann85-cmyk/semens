package com.tuusuario.semenmod;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Hand;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * El Barbero: jefe gigante, lento, que siempre te persigue, destruye todo bloque que toca
 * (menos el suelo bajo sus pies) y te mata de un solo golpe.
 */
public class BarberoEntity extends HostileEntity {
    public static final float SCALE = 4.5f;               // tamano (zombie gigante = 6)
    public static final double MAX_HEALTH = 500.0;        // vida
    public static final double SPEED = 0.045;             // velocidad (zombie normal = 0.23)
    public static final float KILL_DAMAGE = 1000.0f;      // un golpe = muerte
    public static final boolean DROP_BLOCKS = false;      // true = los bloques rotos sueltan items
    private static final int MAX_BREAK_PER_PASS = 150;

    private final ServerBossBar bossBar =
            new ServerBossBar(Text.literal("El Barbero"), BossBar.Color.YELLOW, BossBar.Style.PROGRESS);
    private int attackCooldown = 0;

    public BarberoEntity(EntityType<? extends BarberoEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 500;
    }

    public static DefaultAttributeContainer.Builder createBarberoAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, KILL_DAMAGE)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 2048.0);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        bossBar.setPercent(getHealth() / getMaxHealth());
        if (!(getWorld() instanceof ServerWorld world)) return;

        // Mantiene cargados los chunks por donde camina, asi sigue avanzando aunque estes muy lejos
        if (age % 20 == 0) {
            world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(getBlockPos()), 3, getBlockPos());
        }

        // Siempre va hacia el jugador mas cercano de la dimension, sin importar la distancia
        PlayerEntity target = world.getClosestPlayer(getX(), getY(), getZ(), -1.0, e -> e.isAlive() && !e.isSpectator());
        if (target != null) {
            getLookControl().lookAt(target, 30.0f, 30.0f);
            getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1.0);

            // Golpe mortal si lo tienes al alcance
            if (attackCooldown <= 0) {
                double dx = target.getX() - getX();
                double dz = target.getZ() - getZ();
                double horizontal = Math.sqrt(dx * dx + dz * dz);
                boolean sameHeight = target.getY() < getY() + getHeight() && target.getY() + target.getHeight() > getY();
                if (horizontal <= getWidth() / 2.0 + 2.0 && sameHeight) {
                    swingHand(Hand.MAIN_HAND);
                    target.damage(getDamageSources().mobAttack(this), KILL_DAMAGE);
                    attackCooldown = 20;
                }
            }
        }
        if (attackCooldown > 0) attackCooldown--;

        if (age % 4 == 0) destroyBlocks(world);
    }

    /** Rompe todo lo que toca (adelante, atras, a los lados y arriba), pero NO el suelo bajo sus pies. */
    private void destroyBlocks(ServerWorld world) {
        Box box = getBoundingBox().expand(1.0, 0.0, 1.0).stretch(0.0, 1.0, 0.0);
        int minY = MathHelper.ceil(getY() - 0.001);    // la capa de abajo (y - 1) no se toca
        int maxY = MathHelper.floor(box.maxY);
        int count = 0;
        BlockPos.Mutable p = new BlockPos.Mutable();
        for (int x = MathHelper.floor(box.minX); x <= MathHelper.floor(box.maxX); x++) {
            for (int z = MathHelper.floor(box.minZ); z <= MathHelper.floor(box.maxZ); z++) {
                for (int y = minY; y <= maxY; y++) {
                    p.set(x, y, z);
                    BlockState state = world.getBlockState(p);
                    if (state.isAir() || state.getBlock() instanceof FluidBlock) continue;
                    if (state.getHardness(world, p) < 0) continue;   // bedrock, barreras, portales...
                    world.breakBlock(p.toImmutable(), DROP_BLOCKS, this);
                    if (++count >= MAX_BREAK_PER_PASS) return;
                }
            }
        }
    }

    // ---- premio: al morir suelta la Recortadora (no desaparece ni se destruye) ----
    @Override
    protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
        super.dropLoot(damageSource, causedByPlayer);
        ItemEntity drop = new ItemEntity(getWorld(), getX(), getY() + 1.0, getZ(), new ItemStack(ModItems.RECORTADORA));
        drop.setNeverDespawn();
        drop.setInvulnerable(true);
        getWorld().spawnEntity(drop);
    }

    // ---- nunca desaparece ----
    @Override
    public void checkDespawn() {}

    // ---- inmune a lo que le pasaria por caminar dentro de la tierra ----
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.isOf(DamageTypes.IN_WALL) || source.isOf(DamageTypes.FALL)
                || source.isOf(DamageTypes.DROWN) || source.isOf(DamageTypes.FLY_INTO_WALL)
                || super.isInvulnerableTo(source);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.74f * SCALE;
    }

    // ---- sonido: el mismo al dar un paso, recibir dano y morir ----
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.BARBERO, 3.0f, 1.0f);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BARBERO;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BARBERO;
    }

    @Override
    protected float getSoundVolume() {
        return 3.0f;
    }

    // ---- barra de jefe ----
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        bossBar.removePlayer(player);
    }
}
