package com.tuusuario.semenmod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** Cuerpo de humano (como el zombie) agrandado, con la skin que pasaste. */
@Environment(EnvType.CLIENT)
public class BarberoEntityRenderer extends BipedEntityRenderer<BarberoEntity, BipedEntityModel<BarberoEntity>> {
    private static final Identifier TEXTURE = new Identifier(SemenMod.MOD_ID, "textures/entity/barbero.png");

    public BarberoEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5f * BarberoEntity.SCALE);
    }

    @Override
    protected void scale(BarberoEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(BarberoEntity.SCALE, BarberoEntity.SCALE, BarberoEntity.SCALE);
    }

    @Override
    public Identifier getTexture(BarberoEntity entity) {
        return TEXTURE;
    }
}
