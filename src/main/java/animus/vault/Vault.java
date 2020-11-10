package animus.vault;

import animus.item.AnimusItem;
import animus.world.geometry.WorldGeometry;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author Ethan Borawski
 */
public class Vault {

    private String id;
    private ArmorStand[] itemStands;
    private WorldGeometry region;
    private WorldGeometry doorGeometry;

    private List<AnimusItem> currentItems;
    private UUID currentViewer;
    private boolean locked = false;

    public Vault(String id) {
        this.id = id;
        this.currentItems = new LinkedList<>();
    }

    public String getId() {
        return id;
    }

    public ArmorStand[] getItemStands() {
        return itemStands;
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

    public List<AnimusItem> getCurrentItems() {
        return currentItems;
    }

    public UUID getCurrentViewer() {
        return currentViewer;
    }

    public void setCurrentViewer(UUID current) {
        currentViewer = current;
    }

    public void removeViewer() {
        currentViewer = null;
    }

    public boolean isLocked() {
        return currentViewer != null;
    }

}
