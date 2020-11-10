package animus.world.geometry.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Map;

public class GeometryUndoTask implements Runnable {

    private final World world;
    private final Map<Location, Material> previousMaterials;

    public GeometryUndoTask(World world, Map<Location, Material> previousMaterials) {
        this.world = world;
        this.previousMaterials = previousMaterials;
    }

    public void run() {

        previousMaterials.forEach((location, material) -> {
            world.getBlockAt(location).setType(material);

//            BlockState state = world.getBlockAt(location).getState();
//
//            state.setType(material);
//            state.update();
        });

    }
}
