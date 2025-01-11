package f_c2;

import battlecode.common.*;

class Globals {
    static RobotController rc;
    static MapLocation here;
    static Team us;
    static Team them;
    static int myID;
    static UnitType myType;
    static int myAttackRadiusSquared;
    static int mySensorRadiusSquared;

    static void init(RobotController theRC) {
        rc = theRC;
        us = rc.getTeam();
        them = us.opponent();
        myID = rc.getID();
        myType = rc.getType();
        myAttackRadiusSquared = myType.actionRadiusSquared;
        mySensorRadiusSquared = GameConstants.VISION_RADIUS_SQUARED;
        here = rc.getLocation();
    }

    static void update() {
        here = rc.getLocation();
    }
}