package animus.world.event;

import animus.world.event.task.WorldEventTask;
import animus.world.geometry.WorldGeometry;

public interface WorldEvent {
    String id();
    WorldGeometry getGeometry();
    WorldEventTask worldEventTask();
    int tickInterval();
    int tickLength();
}
