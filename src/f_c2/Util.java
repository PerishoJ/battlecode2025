package f_c2;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.UnitType;

public class Util {

    private static int codesInit;
    static RobotController rc;
    static void init(RobotController rc){
        Util.rc = rc;
    }
    static boolean isOnMap(int x, int y){
        return (x >=0 && x<rc.getMapWidth() && y>=0 && y<rc.getMapHeight());
    }


    static boolean isPaintTower(UnitType rc) {
        return rc == UnitType.LEVEL_ONE_PAINT_TOWER || rc == UnitType.LEVEL_TWO_PAINT_TOWER || rc == UnitType.LEVEL_THREE_PAINT_TOWER;
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
