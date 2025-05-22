package com.shinobunoinu.shinobu.client.model.entity;

import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import net.minecraft.resources.ResourceLocation;


import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;


public class ShinobuModel extends DefaultedEntityGeoModel<ShinobuEntity> {
	// We use the alternate super-constructor here to tell the model it should handle head-turning for us
	public ShinobuModel() {
		super(new ResourceLocation(GeckoLib.MOD_ID, "bat"), true);
	}
}
