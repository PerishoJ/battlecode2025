package f_commanding;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Wanderer {
    private static MapLocation destination = null;
    private static int MAX_ATTENTION = -1;
    static int attentionToWhereIAmGoing = MAX_ATTENTION;

    static void moveRnd(RobotController rc) throws GameActionException {
        if(MAX_ATTENTION == -1){
            MAX_ATTENTION = ( rc.getMapHeight() + rc.getMapWidth() ) / 2; // The average of height and width is a good guestimate...maybe?
        }
        boolean isCloseEnough = destination != null && rc.getLocation().distanceSquaredTo(destination) < 5;
        boolean shouldIChangeDirection = destination == null || attentionToWhereIAmGoing < 1 || isCloseEnough;
        if(shouldIChangeDirection){
            //get a random location
            destination = new MapLocation( RobotPlayer.rng.nextInt(rc.getMapHeight()) , RobotPlayer.rng.nextInt(rc.getMapWidth()) );
            attentionToWhereIAmGoing = MAX_ATTENTION;
        }
        Bug.goTo(destination);
        attentionToWhereIAmGoing -= 1;
        rc.setIndicatorLine(rc.getLocation(),destination,0,0,255);
    }
}
