package animus.world.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public interface WorldGeometry {

    Location getCenter();
    List<Vector> getPoints();
    boolean isInside(Location location);
    double minimumX();
    double minimumZ();
    double maximumX();
    double maximumZ();
    boolean rotateZ();

}
