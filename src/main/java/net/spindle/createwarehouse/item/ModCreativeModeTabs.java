package net.spindle.createwarehouse.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spindle.createwarehouse.CreateWarehouse;
import net.spindle.createwarehouse.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateWarehouse.MODID);

    public static final Supplier<CreativeModeTab> CREATE_WAREHOUSE_TAB = CREATIVE_MODE_TAB.register("create_warehouse_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.DRUM_PACKAGER.get()))
                    .title(Component.translatable("creativetab.create_warehouse"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.DRUM_PACKAGER);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
