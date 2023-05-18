package click.isreal.topbar.domain;

import net.minecraft.item.ItemStack;

import java.util.List;

public class BackpackScanData {
    private String backpackName;
    private int currentCapacity = 0;
    private int maxCapacity = 0;
    private List<ItemStack> capacityPatching;

    public String backpackName() {
        return backpackName;
    }

    public void setBackpackName(String backpackName) {
        this.backpackName = backpackName;
    }

    public int currentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void addCurrentCapacity(int add) {
        if(this.currentCapacity + add > this.maxCapacity) this.currentCapacity = maxCapacity;
        this.currentCapacity = currentCapacity + add;
    }

    public int maxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<ItemStack> capacityPatching() {
        return capacityPatching;
    }

    public void setCapacityPatching(List<ItemStack> capacityPatching) {
        this.capacityPatching = capacityPatching;
    }

    @Override
    public String toString() {
        return "BackpackScanData{" +
                "backpackName='" + backpackName + '\'' +
                ", minCapacity='" + currentCapacity + '\'' +
                ", maxCapacity='" + maxCapacity + '\'' +
                ", capacityPatchingItemsSize=" + capacityPatching.toString() +
                '}';
    }
}
