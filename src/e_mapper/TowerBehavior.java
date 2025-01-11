package e_mapper;

import battlecode.common.*;


public class TowerBehavior {

    private static boolean hasBuilt = false;

    static void run(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        // Pick a random robot type to build.
        int robotType = RobotPlayer.rng.nextInt(2);
//        if (robotType == 0 && rc.canBuildRobot(UnitType.SOLDIER, nextLoc)){
//            rc.buildRobot(UnitType.SOLDIER, nextLoc);
//            System.out.println("BUILT A SOLDIER");
//        }
//        if (robotType == 1 && rc.canBuildRobot(UnitType.MOPPER, nextLoc)){
//            rc.buildRobot(UnitType.MOPPER, nextLoc);
//            System.out.println("BUILT A MOPPER");
//        }
//        else

        if (!hasBuilt && rc.canBuildRobot(UnitType.SPLASHER, nextLoc)){
            rc.buildRobot(UnitType.SPLASHER, nextLoc);
            System.out.println("BUILT A SPLASHER");
//            rc.setIndicatorString("SPLASHER NOT IMPLEMENTED YET");
            hasBuilt =true;
        }

        // Read incoming messages
        Message[] messages = rc.readMessages(-1);
//        for (Message m : messages) {
//            System.out.println("Tower received message: '#" + m.getSenderID() + " " + m.getBytes());
//        }

        RobotInfo[] robots = rc.senseNearbyRobots();

        if( Util.isPaintTower(rc.getType()) && rc.canUpgradeTower(rc.getLocation()))
            rc.upgradeTower(rc.getLocation());

        for(RobotInfo robot : robots){
            if(     rc.getTeam().equals(robot.getTeam())
                    && robot.getType().isRobotType()
                    && Util.isPaintTower(rc.getType())
                    && rc.canSendMessage(robot.getLocation())
            ){
                rc.sendMessage(robot.getLocation() , RobotInfoCodec.encode(robot));
            }
        }
        murderNearbyRobotsWithTower(rc,robots);
    }


    private static void murderNearbyRobotsWithTower(RobotController rc, RobotInfo[] robots ) throws GameActionException {
        // Murder any robots stupid enough to come close
        RobotInfo leastHealthBot = null;
        for(RobotInfo bot : robots){
            // can we attack this?
            if(bot.getTeam() != rc.getTeam() && rc.canAttack(bot.getLocation())){
                if(leastHealthBot != null) {
                    // target the weak!
                    if (bot.getHealth() <= leastHealthBot.getHealth()) {
                        leastHealthBot = bot;
                        // target the closest
                    } else if (bot.getHealth() == leastHealthBot.getHealth()
                            && bot.location.distanceSquaredTo(rc.getLocation()) < leastHealthBot.location.distanceSquaredTo(rc.getLocation())){
                        leastHealthBot = bot;
                    }
                } else {
                    leastHealthBot = bot;
                }
            }
        }
        //...now murder them
        if(leastHealthBot!=null && rc.canAttack(leastHealthBot.getLocation())){
            rc.attack(leastHealthBot.getLocation());
            rc.setIndicatorLine(rc.getLocation(),leastHealthBot.getLocation(),255,0,0);
        }
    }

}
