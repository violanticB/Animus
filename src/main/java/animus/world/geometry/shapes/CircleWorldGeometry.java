package animus.world.geometry.shapes;

import animus.world.geometry.WorldGeometry;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public class CircleWorldGeometry implements WorldGeometry {

    private Location center;
    private double radius;
    private List<Vector> points;

    public CircleWorldGeometry(Location center, double radius) {
        this.center = center;
        this.radius = radius;
//        this.points = GeometryUtil.getCircleVectors(radius);
    }

    @Override
    public Location getCenter() {
        return center;
    }

    @Override
    public List<Vector> getPoints() {
        return points;
    }

    @Override
    public boolean isInside(Location location) {
        return false;
    }

    @Override
    public double minimumX() {
        return 0;
    }

    @Override
    public double minimumZ() {
        return 0;
    }

    @Override
    public double maximumX() {
        return 0;
    }

    @Override
    public double maximumZ() {
        return 0;
    }

    @Override
    public boolean rotateZ() {
        return false;
    }
}
