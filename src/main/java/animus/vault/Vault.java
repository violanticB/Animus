package animus.vault;

import animus.item.AnimusItem;
import animus.world.geometry.WorldGeometry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import sun.awt.image.ImageWatched;

import java.util.*;

/**
 * @author Ethan Borawski
 */
public class Vault {

    private String id;
    private WorldGeometry region;
    private WorldGeometry doorGeometry;
    private List<Location> itemSlots;
    private List<ArmorStand> stands;

    private AnimusItem[] currentItems;
    private Set<Integer> removedItems;
    private UUID currentViewer;
    private boolean locked = false;

    public Vault(String id) {
        this.id = id;
        this.itemSlots = new ArrayList<>();
        this.removedItems = new HashSet<>();
        this.stands = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setSlot(int index, ItemStack stack) {
        // Set item on armorstand head / hand
    }

    public WorldGeometry getDoorGeometry() {
        return doorGeometry;
    }

    public void setDoorGeometry(WorldGeometry doorGeometry) {
        this.doorGeometry = doorGeometry;
    }

    public WorldGeometry getRegion() {
        return region;
    }

    public void setRegion(WorldGeometry region) {
        this.region = region;
    }

    public List<ArmorStand> getStands() {
        return stands;
    }

    public Set<Integer> getRemovedItems() {
        return removedItems;
    }

    public AnimusItem[] getCurrentItems() {
        return currentItems;
    }

    public void setCurrentItems(AnimusItem[] currentItems) {
        this.currentItems = currentItems;
    }

    public List<Location> getItemSlots() {
        return itemSlots;
    }

    public UUID getCurrentViewer() {
        return currentViewer;
    }

    public void setCurrentViewer(UUID current) {
        currentViewer = current;
    }

    public void removeViewer() {
        this.currentViewer = null;
        this.removedItems = new HashSet<>();
        Arrays.fill(currentItems, null);

        this.stands.forEach(stand -> {
            stand.setHelmet(new ItemStack(Material.AIR));
        });
    }

    public boolean isLocked() {
        return currentViewer != null;
    }

}
