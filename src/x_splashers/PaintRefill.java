package x_splashers;

import battlecode.common.*;

import java.util.HashSet;

import static java.lang.Math.min;

public class PaintRefill {
    private static final HashSet<MapLocation> paintTowers = new HashSet<>();

    static void findNearbyPaintTowers(RobotController rc, RobotInfo[] bots, Message[] msgs) {
        for(Message msg : msgs){
            RobotInfo bot = RobotInfoCodec.decode(msg.getBytes());
            if (recordIfPaintTower(rc, bot))
                System.out.println("Paint Tower Location Received from Message");

        }
        for(RobotInfo bot : bots){
            recordIfPaintTower(rc, bot);
            System.out.println("Paint Tower Location found from Sensors");
        }
    }

    private static boolean recordIfPaintTower(RobotController rc, RobotInfo bot) {
        if(bot.getType().isTowerType() && bot.getTeam().equals(rc.getTeam())) {
            if(bot.getType() == UnitType.LEVEL_THREE_PAINT_TOWER
                    || bot.getType() == UnitType.LEVEL_TWO_PAINT_TOWER
                    || bot.getType() == UnitType.LEVEL_ONE_PAINT_TOWER){
                paintTowers.add(bot.getLocation()); // set will automatically kick it if already there.
                return true;
            }
        }
        return false;
    }

    static void tryToRefillPaint(RobotController rc) throws GameActionException {
        System.out.println("Moving to a paint tower");
        if(!paintTowers.isEmpty()) {
            MapLocation nearestPaintTower = getNearestPaintTower(rc);

            Direction dirToTower =  rc.getLocation().directionTo(nearestPaintTower);
            if(rc.canMove(dirToTower))
                rc.move(dirToTower);

            RobotInfo towerInfo = rc.senseRobotAtLocation(nearestPaintTower);
            // Either take what's left in the tower, or however much you can carry
            int paintAvailable = min( towerInfo.getPaintAmount() , UnitType.SPLASHER.paintCapacity-rc.getPaint());
            System.out.println("Transfering " + paintAvailable + " paint");
            if(rc.canTransferPaint(nearestPaintTower, -paintAvailable)){
                rc.transferPaint(nearestPaintTower, -paintAvailable);
            }; //set paint to negative since we are taking it
        } else {
            RobotPlayer.moveRnd(rc);
            System.out.println("Oh no! I have no idea where I am going!");
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
