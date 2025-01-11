package d_mapper;

import battlecode.common.*;

import java.util.HashSet;

import static java.lang.Math.min;

public class PaintRefill {
    private static final HashSet<MapLocation> paintTowers = new HashSet<>();

    static void findNearbyPaintTowers(RobotController rc, RobotInfo[] bots, Message[] msgs) {
        for(Message msg : msgs){
            RobotInfo bot = RobotInfoCodec.decode(msg.getBytes());
        }
        for(RobotInfo bot : bots){
            recordIfPaintTower(rc, bot);
        }
    }

    private static boolean recordIfPaintTower(RobotController rc, RobotInfo bot) {
        if(bot.getType().isTowerType() && bot.getTeam().equals(rc.getTeam())) {
            if(Util.isPaintTower(bot.getType())){
                paintTowers.add(bot.getLocation()); // set will automatically kick it if already there.
                return true;
            }
        }
        return false;
    }

    static void tryToRefillPaint(RobotController rc) throws GameActionException {
//        System.out.println("Moving to a paint tower");
        if(!paintTowers.isEmpty()) {
            MapLocation closestTowerLocation = getNearestPaintTower(rc);

            Direction dirToTower =  rc.getLocation().directionTo(closestTowerLocation);
            if(rc.canMove(dirToTower))
                rc.move(dirToTower);

            // If we have any paint tower location (visible or remembered)
            if (closestTowerLocation != null) {
                // Try to withdraw paint if we're close enough, maximum paint withdrawal value of 20 (arbitrary)
                if (rc.getLocation().isWithinDistanceSquared(closestTowerLocation, 2)) {
                    int paintNeeded = rc.getType().paintCapacity - rc.getPaint();
                    if (paintNeeded > 0 && rc.canTransferPaint(closestTowerLocation, -paintNeeded)) {
                        rc.transferPaint(closestTowerLocation, Math.max(-paintNeeded, -20));
                        System.out.println("Withdrew " + paintNeeded + " paint from tower");
                    }
                }
            }

            if(rc.canSenseLocation(closestTowerLocation)) {
                RobotInfo towerInfo = rc.senseRobotAtLocation(closestTowerLocation);
                // Either take what's left in the tower, or however much you can carry
                int paintNeeded = min( towerInfo.getPaintAmount() , rc.getType().paintCapacity - rc.getPaint());
                System.out.println("Transfering " + paintNeeded + " paint");
                if(rc.canTransferPaint(closestTowerLocation, -paintNeeded)){
                    rc.transferPaint(closestTowerLocation, -paintNeeded);
                } //set paint to negative since we are taking it
            }

        } else {
            Wanderer.moveRnd(rc);
            rc.setIndicatorString("I don't know where paint is!!!");
        }
    }

    static MapLocation getNearestPaintTower(RobotController rc) {
        MapLocation nearestPaintTower = null;
        int closestDist = Integer.MAX_VALUE;
        for( MapLocation loc : paintTowers){
            if(nearestPaintTower == null){
                nearestPaintTower = loc;
                closestDist = loc.distanceSquaredTo(rc.getLocation());
            } else {
                if(loc.distanceSquaredTo(rc.getLocation())<closestDist){
                    nearestPaintTower = loc;
                    closestDist = loc.distanceSquaredTo(rc.getLocation());
                }
            }
        }
        return nearestPaintTower;
    }
}
