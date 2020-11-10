package animus.vault;

import animus.AnimusPlugin;
import animus.world.geometry.task.GeometryFloodFillTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

/**
 * @author Ethan Borawsik
 */
public class VaultManager {

    private List<Vault> vaults;
    private VaultConfig config;

    public VaultManager(VaultConfig config) {
        this.config = config;
    }

    public List<Vault> getVaults() {
        return vaults;
    }

    public VaultConfig getConfig() {
        return config;
    }

    public void reload() {
        this.vaults = config.load();
    }

    public Vault getVault(Location door) {
        for (Vault vault : vaults) {
            if(vault.getDoorGeometry().isInside(door)) return vault;
        }

        return null;
    }

    public void invokeAnimation(Vault v, boolean open) {
        BukkitScheduler scheduler = AnimusPlugin.instance().getServer().getScheduler();

        Material from = open ? Material.IRON_BLOCK : Material.AIR;
        Material to = open ? Material.AIR : Material.IRON_BLOCK;

        GeometryFloodFillTask fillTask = new GeometryFloodFillTask(
                scheduler,
                v.getDoorGeometry(),
                from, to,
                3);
        scheduler.runTask(AnimusPlugin.instance(), fillTask);
    }

}
