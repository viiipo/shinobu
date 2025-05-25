package com.shinobunoinu.shinobu.client.renderer.model;

import com.shinobunoinu.shinobu.client.model.KokorowatariModel;
import com.shinobunoinu.shinobu.item.KokorowatariItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class KokorowatariRenderer extends GeoItemRenderer<KokorowatariItem> {
    public KokorowatariRenderer() {
        super(new KokorowatariModel());
    }
}