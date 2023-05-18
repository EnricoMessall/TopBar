package click.isreal.topbar.events;

import click.isreal.topbar.domain.SimpleInventoryInfo;
import net.minecraft.inventory.SimpleInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryOpenEvent {

    private List<Consumer<SimpleInventoryInfo>> listener = new ArrayList<>();

    public void register(Consumer<SimpleInventoryInfo> consume){
        this.listener.add(consume);
    }

    public void clear(){
        this.listener = new ArrayList<>();
    }

    public void isOpen(SimpleInventoryInfo simpleInventory){
        this.listener.forEach(c -> c.accept(simpleInventory));
    }
}
