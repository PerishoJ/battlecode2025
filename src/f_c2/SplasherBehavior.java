package f_c2;

import battlecode.common.*;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

public class SplasherBehavior {

    static final int MINIMUM_ACCEPTABLE_DAMAGE = 5;
    public static final double FULL_ENOUGH_PERCENT = 0.95;
    public static final int ATTACK_SCAN_THROTTLE = 3; // kept running out of bytecodes...so
    static boolean shouldFindPaint = false; // This is here so that robots STAY at the paint tower until they are full again

    static void run(RobotController rc) throws Exception{
        // SPLISH SPLASH TIME TO TAKE A BATH!
//        Util.timerStart();
        RobotInfo[] bots = rc.senseNearbyRobots();
        Message[] msgs = rc.readMessages(-1);

        MapInfo[] nearbyInfo = rc.senseNearbyMapInfos();
        Mapper.update(nearbyInfo);
//        Util.timerEnd("Update Map");

//        Util.timerStart();
        PaintRefill.findNearbyPaintTowers(rc, bots, msgs);
//        Util.timerEnd("Find Towers");
        // Will I go into handicap paint levels( lvl <1/2 paint capacity) if I attack one more time???
        boolean isHandicapped = rc.getPaint() < UnitType.SPLASHER.paintCapacity / 2 ;
        if(rc.isActionReady() && !isHandicapped) {
            splashStuff(rc);
        }

        if(!isHandicapped && !shouldFindPaint) {
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
        AttackResult bestAttackResult = new AttackResult(null,MINIMUM_ACCEPTABLE_DAMAGE);
//        Util.timerStart();
        // go through each attackable place...just kidding. Just check the top,bottom,left,right, and middle locations cuz we're low on bytecodes.
        // Mostly because I can't think of a good dynamic coding solution...maybe convolutions and byte masking, but that'll take forever.
        for(int attackX = -rng ; attackX <= rng ; attackX+=rng/2){ //just simplify this junk
            for(int attachY = -rng ; attachY <= rng ; attachY+=rng/2){
                bestAttackResult = getAttackResult(rc, attackX, attachY, rng, bestAttackResult); // Only returns a new value if higher score
            }
        }
        if( null != bestAttackResult.bestSplashPlace()
                && rc.canAttack(bestAttackResult.bestSplashPlace())){
            rc.attack(bestAttackResult.bestSplashPlace());
        }
//        Util.timerEnd("Damage Calculation");
    }

    private static AttackResult getAttackResult(RobotController rc, int attackX, int attachY, int rng, AttackResult bestAttackResult) throws GameActionException {
        if(isInRange(attackX, attachY, rng)
                && Util.isOnMap(rc.getLocation().x + attackX, rc.getLocation().y + attachY)
                ) { //

            MapLocation testPlace = rc.getLocation().translate(attackX, attachY);
            int damage = 0 ;
            if (Mapper.get(testPlace).isFresh()){
                // Use Cached values
                damage =  Mapper.get(testPlace).paintEffectiveness;
            } else {
                // because this is so expensive, randomly choose to NOT scan through this and update results
                damage = isNotThrottled() ?
                        calcAndCacheDmg(testPlace)//
                        : -1 ; // Not scanned
            }
            if(damage > 0){
                rc.setIndicatorDot(testPlace,0,0,255);
            }
            if(damage > bestAttackResult.bestDamage()){
                bestAttackResult = new AttackResult(testPlace,damage);
            }
        }
        return bestAttackResult;
    }
    // Only Randomly scan around. Don't check everything
    private static boolean isNotThrottled() {
        return RobotPlayer.rng.nextInt(ATTACK_SCAN_THROTTLE) == 0;
    }

    private static boolean isInRange(int attackX, int attachY, int rng) {
        int attackDistSquared = (attackX * attackX + attachY * attachY);
        boolean inRange =  attackDistSquared< rng;
        return inRange;
    }

    private static int calcAndCacheDmg(MapLocation testPlace) {
        int splashRadius = UnitType.SPLASHER.actionRadiusSquared;
        //Estimate the damage it'll do
        int damage = 0;
        //got through each place in the blast zone to see if it'll leave paint
        for(int x = -splashRadius ; x<=splashRadius;x++){
            for(int y = -splashRadius ; y<=splashRadius;y++){
                if(Util.isOnMap(testPlace.x+x, testPlace.y+y)) {
                    boolean isInBlashZone = x * x + y * y < splashRadius;
                    MapLocation blastSection = testPlace.translate(x,y);
                    MapInfo bInfo = Mapper.get(blastSection.x, blastSection.y).mapInfo;
                    if (isInBlashZone) {
                        if (    bInfo == null // Optimistic damage estimation here. If you can't see it, it's GOTTA work
                                || isPaintable(bInfo) // realistic, accurate prediction
                            ) {
                            damage++;
                        }
                    }
                }
            }
        }
        updateCache(testPlace, damage);
        return damage;
    }

    private static void updateCache(MapLocation testPlace, int damage) {
        MapInfoDecorator attackInfo = new MapInfoDecorator(Mapper.get(testPlace).mapInfo); //automatically sets turn count
        attackInfo.paintEffectiveness = damage;
        Mapper.set(attackInfo);
    }

    private record AttackResult(MapLocation bestSplashPlace, int bestDamage) {
    }

    private static boolean isPaintable(MapInfo bInfo) {
        return bInfo.isPassable() || !bInfo.getPaint().isAlly();
    }
}
