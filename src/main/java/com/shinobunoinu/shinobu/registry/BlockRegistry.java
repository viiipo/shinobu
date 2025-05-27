package com.shinobunoinu.shinobu.registry;
import com.shinobunoinu.shinobu.block.ShinobuBlock;
import com.shinobunoinu.shinobu.Shinobu;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Shinobu.MODID);


    // ▶▶▶ 新方块注册位置 ▶▶▶
    public static final RegistryObject<Block> SHINOBU_BLOCK = BLOCKS.register("shinobu_block", ShinobuBlock::new);

    // 在此处添加新的注册条目，格式如下：
    // public static final RegistryObject<Block> 大写方块名 =
    //     BLOCKS.register("小写方块", 自定义方块类::new);

    // ▼▼▼ 示例：注册一个名为 "brick_block" 的物品 ▼▼▼
    // public static final RegistryObject<Block> BRICK_BLOCK =
    //     BLOCKS.register("brick_block", BrickBlock::new);
}
