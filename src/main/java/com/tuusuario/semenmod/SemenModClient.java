package com.tuusuario.semenmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class SemenModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Para que el fondo de la imagen sea transparente al plantarlo
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PICKLE_RICK_CROP, RenderLayer.getCutout());
        EntityRendererRegistry.register(ModEntities.BARBERO, BarberoEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPRINGFIELD_PORTAL, RenderLayer.getTranslucent());
    }
}
