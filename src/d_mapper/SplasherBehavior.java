package d_mapper;

import battlecode.common.*;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

public class SplasherBehavior {

    static final int MINIMUM_ACCEPTABLE_DAMAGE = 5;
    public static final double FULL_ENOUGH_PERCENT = .95;

    static boolean shouldFindPaint = false; // This is here so that robots STAY at the paint tower until they are full again

    static void run(RobotController rc) throws Exception{
        // SPLISH SPLASH TIME TO TAKE A BATH!
        RobotInfo[] bots = rc.senseNearbyRobots();
        Message[] msgs = rc.readMessages(-1);

        MapInfo[] nearbyInfo = rc.senseNearbyMapInfos();
        Mapper.update(nearbyInfo);

        PaintRefill.findNearbyPaintTowers(rc, bots, msgs);

        // Will I go into handicap paint levels( lvl <1/2 paint capacity) if I attack one more time???
        boolean willCauseNoHandicaps = ( rc.getPaint() >= (UnitType.SPLASHER.paintCapacity / 2 ) + UnitType.SPLASHER.attackCost);

        if(rc.isActionReady() && willCauseNoHandicaps) {
            splashStuff(rc);
        }

        if(willCauseNoHandicaps && !shouldFindPaint) {
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

    private static boolean isPaintFullEnough(RobotController rc) {
        return rc.getPaint() > (rc.getType().paintCapacity * FULL_ENOUGH_PERCENT);
    }

    static void splashStuff(RobotController rc) throws GameActionException {

        // Pick some random spots, and see how much paint WOULD be painted if it were splashed there
        // ...pay close attention to bytecode count, cuz I'd rather abort or NOT paint than run out of bytecodes
        int rng = (int) ceil(sqrt(UnitType.SPLASHER.actionRadiusSquared)); // Because this isn't a constant for some reason
        MapLocation bestSplashPlace = null;
        int bestDamage = MINIMUM_ACCEPTABLE_DAMAGE;
        // go through each attackable place...just kidding. Just check the top,bottom,left,right, and middle locations cuz we're low on bytecodes.
        // Mostly because I can't think of a good dynamic coding solution...maybe convolutions and byte masking, but that'll take forever.
        for(int attackX = -rng ; attackX <= rng ; attackX+=rng/2){ //just simplify this junk
            for(int attachY = -rng ; attachY <= rng ; attachY+=rng/2){
                boolean inRange = (attackX * attackX + attachY * attachY) < rng;
                if(inRange) {
                    MapLocation testPlace = rc.getLocation();
                    testPlace = testPlace.translate(attackX,attachY);
                    int splashRadius = UnitType.SPLASHER.actionRadiusSquared;
                    int damage = 0;
                    //Estimate the damge it'll do
                    //got through each place in the blast zone to see if it'll leave paint
                    for(int x = -splashRadius ; x<=splashRadius;x++){
                        for(int y = -splashRadius ; y<=splashRadius;y++){
                            boolean isInBlashZone = x * x + y * y < splashRadius;
                            MapLocation blastSection = testPlace.translate(x,y);
                            MapInfo bInfo = Mapper.get(blastSection.x,blastSection.y);
                            if (isInBlashZone){
                                if( bInfo == null || //Optimistic damage estimation here
                                    (bInfo.isPassable() || !bInfo.getPaint().isAlly()) // realistic, accurate prediction
                                ){
                                    damage ++;
                                }
                            }
                        }
                    }
                    if(damage>bestDamage){
                        bestSplashPlace = testPlace;
                        bestDamage = damage;
                    }
                }
            }

        }

        if(bestSplashPlace!=null && rc.canAttack(bestSplashPlace)){
            rc.attack(bestSplashPlace);
        }
    }
}
