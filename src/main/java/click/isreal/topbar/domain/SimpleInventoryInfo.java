package click.isreal.topbar.domain;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;

public class SimpleInventoryInfo {
    private final String title;
    private GenericContainerScreenHandler screenHandler;
    private final SimpleInventory simpleInventory;

    public SimpleInventoryInfo(String title, SimpleInventory simpleInventory) {
        this.title = title;
        this.simpleInventory = simpleInventory;
    }

    public String title() {
        return title;
    }

    public SimpleInventory inventory() {
        return simpleInventory;
    }

    public GenericContainerScreenHandler screenHandler() {
        return screenHandler;
    }

    public void setScreenHandler(GenericContainerScreenHandler screenHandler) {
        this.screenHandler = screenHandler;
    }
}
