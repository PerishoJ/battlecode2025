package f_c2;

import battlecode.common.*;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

public class SplasherBehavior_II {

    static final int MINIMUM_ACCEPTABLE_DAMAGE = 5;
    public static final double FULL_ENOUGH_PERCENT = 0.95;
    public static final int ATTACK_SCAN_THROTTLE = 4; // kept running out of bytecodes...so
    private static final int range = (int) ceil(sqrt(UnitType.SPLASHER.actionRadiusSquared)); // Because this isn't a constant for some reason
    static boolean shouldFindPaint = false; // This is here so that robots STAY at the paint tower until they are full again

    static void run(RobotController rc) throws Exception{
        scanForPaintTowers(rc);

        MapLocation bestSplashPlace = getBestSplashPlace(rc);
        if(     bestSplashPlace!=null
                && rc.isActionReady()
                && !isImpared(rc)
                && rc.canAttack(bestSplashPlace)){
            rc.attack(bestSplashPlace);
        } else {
            StringBuffer sb = new StringBuffer("Cannot attack because:");
            if ( bestSplashPlace==null) sb.append("(No best place found)");
            if ( ! rc.isActionReady()) sb.append("(Action not ready)");
            if ( isImpared(rc)) sb.append("(I'm disabled!)");
            if ( ! rc.canAttack(bestSplashPlace)) sb.append("(Cannot attack for some reason)");
            System.out.println(sb);
        }

        if(!isImpared(rc) && !shouldFindPaint) {
            rc.setIndicatorString("Moving randomly");
            Wanderer.moveRnd(rc); // Bumble around painting random junk
        } else { // oh no, the paint is running out, and we are about to take performance losses. Retreat
            shouldFindPaint = true;
            PaintRefill.tryToRefillPaint(rc);
            if(isPaintFullEnough(rc)){
                shouldFindPaint = false;
                System.out.println("Paint is at " + rc.getPaint() + " Which is " + FULL_ENOUGH_PERCENT + "% of max capacity: " + rc.getType().paintCapacity);
            }
        }
    }

    private static void scanForPaintTowers(RobotController rc) {
        RobotInfo[] bots = rc.senseNearbyRobots();
        Message[] msgs = rc.readMessages(-1);
        PaintRefill.findNearbyPaintTowers(rc, bots, msgs);
    }

    private static boolean isImpared(RobotController rc) {
        return rc.getPaint() < UnitType.SPLASHER.paintCapacity / 2;
    }

    /**
     * just find the first one in the grid that ISN'T painted
     * @param rc
     * @return
     * @throws GameActionException
     */
    private static MapLocation getBestSplashPlace(RobotController rc) throws GameActionException {
        for (int x=-range ; x <= range ; x++){
            for (int y= -range ; y <= range ; y ++){
                MapInfo info = rc.senseMapInfo( rc.getLocation().translate(x,y) );
                rc.setIndicatorDot(info.getMapLocation(), 0, 100, 255);
                if(     isInRange(x,y,range)
                        && isOnGridLoc(info)
                        && info.isPassable()
                        && !info.getPaint().isAlly()
                        ){
                    rc.setIndicatorDot(info.getMapLocation(), 255, 255, 0);
                    return info.getMapLocation();
                }
            }
        }
        return null;
    }
    private static boolean isInRange(int attackX, int attachY, int rng) {
        int attackDistSquared = (attackX * attackX + attachY * attachY);
        boolean inRange =  attackDistSquared < rng;
        return inRange;
    }
    /**
     * assume that the fill pattern is a square. Use modulo to make the WHOLE grid split into smaller chunks that
     * are approximately that far apart...bam, you've got yourself a place to paint
     * @param info
     * @return
     */
    private static boolean isOnGridLoc(MapInfo info) {
        return info.getMapLocation().x % (range*2) == 0 && info.getMapLocation().y % (range*2) == 0;
    }

    private static boolean isPaintFullEnough(RobotController rc) {
        return rc.getPaint() > (rc.getType().paintCapacity * FULL_ENOUGH_PERCENT);
    }

}
