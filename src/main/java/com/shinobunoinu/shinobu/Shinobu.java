package com.shinobunoinu.shinobu;
import com.shinobunoinu.shinobu.entity.ShinobuEntity;
import com.shinobunoinu.shinobu.registry.BlockRegistry;
import com.shinobunoinu.shinobu.registry.EntityRegistry;
import com.shinobunoinu.shinobu.registry.ItemRegistry;
import com.shinobunoinu.shinobu.registry.TabRegistry;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;



// The value here should match an entry in the META-INF/mods.toml file
@Mod(Shinobu.MODID)
public class Shinobu {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "shinobu";

    public static final Logger LOGGER = LogManager.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace




    public Shinobu() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TabRegistry.TABS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);

        GeckoLib.initialize();}}








