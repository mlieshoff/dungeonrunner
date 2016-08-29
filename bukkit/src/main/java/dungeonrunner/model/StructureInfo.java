package dungeonrunner.model;

import dungeonrunner.Point3D;
import org.bukkit.Location;

/**
 * @author Michael Lieshoff
 */
public class StructureInfo {

    private final Point3D start;
    private final Point3D end;

    public StructureInfo(Point3D start, Point3D end) {
        this.start = start;
        this.end = end;
    }

    public Point3D getStart() {
        return start;
    }

    public Point3D getEnd() {
        return end;
    }

    public int getHeight() {
        return Math.abs(start.getY() - end.getY());
    }

    public int getWidth() {
        return Math.abs(start.getZ() - end.getZ());
    }

    public Location centerLocation(org.bukkit.World world) {
        Point3D center = start.center(end);
        return new Location(world, start.getX() + center.getX(), start.getY() + center.getY(), start.getZ() + center.getZ());
    }

}
