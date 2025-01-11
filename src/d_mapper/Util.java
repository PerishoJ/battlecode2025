package d_mapper;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.UnitType;

public class Util {

    private static int codesInit;
    
    static boolean isOnMap(MapLocation loc, RobotController rc){
        return (loc.x >=0 && loc.x<rc.getMapWidth() && loc.y>=0 && loc.y<rc.getMapHeight());
    }

    static boolean isPaintTower(UnitType rc) {
        return rc == UnitType.LEVEL_THREE_PAINT_TOWER || rc == UnitType.LEVEL_TWO_PAINT_TOWER || rc == UnitType.LEVEL_ONE_PAINT_TOWER;
    }


    static void timerStart(){
        codesInit = Clock.getBytecodeNum();
    }

    static void timerEnd(){
        timerEnd("UNKNOWN");
    }

    static void timerEnd(String tag){
        System.out.println(tag+" took :" + (Clock.getBytecodeNum() - codesInit));
    }
}
