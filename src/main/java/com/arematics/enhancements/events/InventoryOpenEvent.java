package com.arematics.enhancements.events;

import com.arematics.enhancements.domain.SimpleInventoryInfo;

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
