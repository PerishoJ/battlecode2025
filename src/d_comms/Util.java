package d_comms;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.UnitType;

public class Util {

    static boolean isOnMap(MapLocation loc, RobotController rc){
        return (loc.x >=0 && loc.x<rc.getMapWidth() && loc.y>=0 && loc.y<rc.getMapHeight());
    }

    static boolean isPaintTower(UnitType rc) {
        return rc == UnitType.LEVEL_THREE_PAINT_TOWER || rc == UnitType.LEVEL_TWO_PAINT_TOWER || rc == UnitType.LEVEL_ONE_PAINT_TOWER;
    }
}
