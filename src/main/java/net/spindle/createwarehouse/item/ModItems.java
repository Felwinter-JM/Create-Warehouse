package net.spindle.createwarehouse.item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spindle.createwarehouse.CreateWarehouse;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateWarehouse.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
