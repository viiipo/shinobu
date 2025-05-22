package com.shinobunoinu.shinobu.client.renderer.entity;



import com.shinobunoinu.shinobu.client.model.entity.ShinobuModel;
import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import com.shinobunoinu.shinobu.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShinobuRenderer extends GeoEntityRenderer<ShinobuEntity> {
    // 不再需要手动处理头部旋转（由模型类的 true 参数自动处理）
    public ShinobuRenderer(EntityRendererProvider.Context context) {
        super(context, new ShinobuModel());
    }
}