package animus.world.geometry.shapes;

import animus.world.geometry.WorldGeometry;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public class SquareWorldGeometry implements WorldGeometry {

    private double size;
    private Location center;
    private final double x0;
    private double z0;
    private final double x1;
    private double z1;
    private double y0;
    private double y1;
    private boolean rotateZ;
    private List<Vector> points;

    public SquareWorldGeometry(Location center, double size, boolean rotateZ) {
        this.size = size;
        this.center = center.add(0.5, 0, 0.5);
//        this.points = GeometryUtil.getSquareVectors(size);

        double r = size / 2;
        this.x0 = center.getX() - r;
        this.z0 = center.getZ() - r;
        this.y0 = center.getY() - r;
        this.x1 = center.getX() + r;
        this.z1 = center.getZ() + r;
        this.y1 = center.getY() + r;

        this.rotateZ = rotateZ;
        if(rotateZ) {
            this.z0 = center.getY() + r;
            this.z1 = center.getY() - r;
        }
    }

    public SquareWorldGeometry(double x0, double z0, double y0,
                               double x1, double z1, double y1,
                               boolean rotateZ) {
        this.x0 = x0;
        this.z0 = z0;
        this.y0 = y0;
        this.x1 = x1;
        this.z1 = z1;
        this.y1 = y1;
        this.rotateZ = rotateZ;
    }

    public double getSize() {
        return size;
    }

    @Override
    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    @Override
    public List<Vector> getPoints() {
        return points;
    }

    @Override
    public boolean isInside(Location location) {
        double x = location.getX();
        double z = location.getZ();
        double y = location.getY();

        //        if(x < 0 && z < 0) {
//
//            System.out.println("In negative coordinate space");
//            return ((x0 >= x && x >= x1) && (y0 <= y && y < y1) && (z0 >= z && z >= z1));
//
//        } else if (x > 0 && z > 0) {
//
//            System.out.println("In positive coordinate space");
//            return ((x0 <= x && x <= x1) && (z0 <= z && z <= z1) && (y0 <= y && y <= y1));
//
//        }

        return ((x0 <= x && x <= x1) && (z0 <= z && z <= z1) && (y0 <= y && y <= y1));
    }

    @Override
    public double minimumX() {
        return x0;
    }

    @Override
    public double minimumZ() {
        return z0;
    }

    @Override
    public double maximumX() {
        return x1;
    }

    @Override
    public double maximumZ() {
        return z1;
    }

    @Override
    public boolean rotateZ() {
        return rotateZ;
    }

    public void setRotateZ(boolean rotateZ) {
        this.rotateZ = rotateZ;
    }
}
