package animus.world.geometry.task;

import animus.AnimusPlugin;
import animus.util.DirectionAxis;
import animus.world.geometry.WorldGeometry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.LinkedList;
import java.util.List;


public class GeometryFloodFillTask implements Runnable {

    private List<VisitedNode> visitedNodes;
    private BukkitScheduler scheduler;
    private WorldGeometry geometry;
    private Material from;
    private Material to;
    private long delay;

    public GeometryFloodFillTask(BukkitScheduler scheduler, WorldGeometry geometry,
                                 Material from, Material to, long delay) {
        this.scheduler = scheduler;
        this.geometry = geometry;
        this.from = from;
        this.to = to;
        this.delay = delay;
        this.visitedNodes = new LinkedList<>();

        int dx = (int) (geometry.maximumX() - geometry.minimumX());
        int dz = (int) (geometry.maximumZ() - geometry.minimumZ());
    }

    @Override
    public void run() {


//        Location start = new Location();
        floodFill(geometry.getCenter(), from, to, DirectionAxis.Y);
    }

    public void floodFill(Location current, Material from, Material to, DirectionAxis axis) {
        if(!geometry.isInside(current)) {
            return;
        }

        if(current.getBlock().getType() != from) {
            return;
        }

        if(hasVisited(current)) {
            return;
        }

        VisitedNode node = new VisitedNode();
        node.x = current.getX();
        node.y = current.getY();
        node.z = current.getZ();

        visitedNodes.add(node);

        current.getBlock().setType(to);
        current.getWorld().playSound(current, Sound.BLOCK_ANVIL_HIT, 1, 1);

        new BukkitRunnable() {
            public void run () {
                if(axis == DirectionAxis.Y) {
                    floodFill(current.clone().add(1, 0, 0), from, to, DirectionAxis.Y);
                    floodFill(current.clone().add(-1, 0, 0), from, to, DirectionAxis.Y);
                    floodFill(current.clone().add(0, 1, 0), from, to, DirectionAxis.Y);
                    floodFill(current.clone().add(0, -1, 0), from, to, DirectionAxis.Y);

                } else if(axis == DirectionAxis.Z){
                    floodFill(current.clone().add(1, 0, 0), from, to, DirectionAxis.Z);
                    floodFill(current.clone().add(-1, 0, 0), from, to, DirectionAxis.Z);
                    floodFill(current.clone().add(0, 0, 1), from, to, DirectionAxis.Z);
                    floodFill(current.clone().add(0, 0, -1), from, to, DirectionAxis.Z);

                }
            }

        }.runTaskLater(AnimusPlugin.instance(), delay);
    }

    public boolean hasVisited(Location location) {
        for (VisitedNode visitedNode : visitedNodes) {
            if(visitedNode.x == location.getX() && visitedNode.y == location.getY()
                    && visitedNode.z == location.getZ()) {
                return true;
            }

        }

        return false;
    }

    private class VisitedNode {
        double x, y, z;
    }
}
