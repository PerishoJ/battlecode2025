package c_splashers;

import battlecode.common.*;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

public class SplasherBehavior {

    public static final int MINIMUM_ACCEPTABLE_DAMAGE = 5;

    static void splashStuff(RobotController rc) throws GameActionException {
        // Find all the nearby places you can paint
        MapInfo[] nearbyInfo = rc.senseNearbyMapInfos(); // TODO kinda expensive, could track this better later
        Set<MapLocation> paintablePlaces = new HashSet<>();
        // TODO add more heuristic data so splashers prioritize HQ's and enemy robots
        for(MapInfo cell : nearbyInfo){
            if(!cell.isWall() && !cell.getPaint().isAlly()){ //can't paint walls, shouldn't repaint
                paintablePlaces.add(cell.getMapLocation());
            }
        }

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
                            if (isInBlashZone){
                                if(paintablePlaces.contains(testPlace.translate(x,y))){
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
