package animus.vault;

import animus.AnimusPlugin;
import animus.world.geometry.shapes.SquareWorldGeometry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

/**
 * @author Ethan Borawski
 */
public class VaultConfig {

    private final ConfigurationSection section;
    public VaultConfig(ConfigurationSection section) {
        this.section = section;
    }

    public Location fromString(World w, String l) {
        String[] locToken = l.split(";");
        return new Location(w, Double.parseDouble(locToken[0]), Double.parseDouble(locToken[1]), Double.parseDouble(locToken[2]));
    }

    public List<Vault> load() {
        List<Vault> vaults = new LinkedList<>();
        BukkitScheduler scheduler = AnimusPlugin.instance().getServer().getScheduler();

        section.getKeys(false).forEach((vaultId) -> {
            Vault vault = new Vault(vaultId);
            String worldName = section.getString(vaultId + ".world");
            World world = Bukkit.getWorld(worldName);

            System.out.println(vaultId + " registering in: " + worldName);

            // x0, z0, x1, z1
            Location cornerA = fromString(world, section.getString(vaultId + ".corner_1"));
            Location cornerB = fromString(world, section.getString(vaultId + ".corner_2"));

            double x0 = Math.min(cornerA.getX(), cornerB.getX());
            double z0 = Math.min(cornerA.getZ(), cornerB.getZ());
            double y0 = Math.min(cornerA.getY(), cornerB.getY());
            double x1 = Math.max(cornerA.getX(), cornerB.getX());
            double z1 = Math.max(cornerA.getZ(), cornerB.getZ());
            double y1 = Math.max(cornerA.getY(), cornerB.getY());
            SquareWorldGeometry region = new SquareWorldGeometry(x0, z0, y0, x1, z1, y1,false);
            vault.setRegion(region);

//            String[] locToken = section.getString(vaultId + ".door_center").split(";");
//            Location center = new Location(world, Double.parseDouble(locToken[0]), Double.parseDouble(locToken[1]), Double.parseDouble(locToken[2]));

            Location doorCenter = fromString(world, section.getString(vaultId + ".door_center"));
            Location doorCornerA = fromString(world, section.getString(vaultId + ".door_corner1"));
            Location doorCornerB = fromString(world, section.getString(vaultId + ".door_corner2"));

            x0 = Math.min(doorCornerA.getX(), doorCornerB.getX());
            z0 = Math.min(doorCornerA.getZ(), doorCornerB.getZ());
            y0 = Math.min(doorCornerA.getY(), doorCornerB.getY());
            x1 = Math.max(doorCornerA.getX(), doorCornerB.getX());
            z1 = Math.max(doorCornerA.getZ(), doorCornerB.getZ());
            y1 = Math.max(doorCornerA.getY(), doorCornerB.getY());
            
            SquareWorldGeometry doorGeometry = new SquareWorldGeometry(x0, z0, y0, x1, z1, y1,false);
            doorGeometry.setCenter(doorCenter);
            vault.setDoorGeometry(doorGeometry);
            vault.removeViewer();
            vaults.add(vault);
        });

        return vaults;
    }
}
