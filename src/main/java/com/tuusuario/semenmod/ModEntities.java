package com.tuusuario.semenmod;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    // Zombie gigante = 6x (11.7 bloques). El Barbero = 4.5x (~8.8 bloques, unos 3 menos)
    public static final EntityType<BarberoEntity> BARBERO = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(SemenMod.MOD_ID, "barbero"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, BarberoEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f * BarberoEntity.SCALE, 1.95f * BarberoEntity.SCALE))
                    .fireImmune()
                    .trackRangeChunks(16)
                    .build());

    public static void register() {
        FabricDefaultAttributeRegistry.register(BARBERO, BarberoEntity.createBarberoAttributes());
    }
}
