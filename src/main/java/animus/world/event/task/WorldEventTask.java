package animus.world.event.task;

import animus.world.geometry.WorldGeometry;

public interface WorldEventTask extends Runnable {

    WorldGeometry getGeometry();

}
