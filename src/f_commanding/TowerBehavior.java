package f_commanding;

import battlecode.common.*;


public class TowerBehavior {

    private static boolean hasBuilt = false;

    //Build orders
    private static UnitType[] buildOrderSoldiers = new UnitType[]{UnitType.SOLDIER,UnitType.SOLDIER,UnitType.SOLDIER,UnitType.SOLDIER};
    private static int buildOrderIt = 0;
    private static UnitType[] buildOrder = buildOrderSoldiers; // use indirection so we can change build orders quickly, and in the game.
    
    
    static void run(RobotController rc) throws GameActionException {
        tryToUpgrade(rc);
        buildBots(rc);
        readIncomingMessages(rc);
        //scan
        RobotInfo[] robots = rc.senseNearbyRobots();
        commandYourArmies(rc, robots);
        slayYourEnemies(rc,robots);//See them flee before you. Hear the lamentation of their women.
    }

    private static void commandYourArmies(RobotController rc, RobotInfo[] robots) throws GameActionException {
        for(RobotInfo robot : robots){
            if(     rc.getTeam().equals(robot.getTeam())
                    && robot.getType().isRobotType()
                    && Util.isPaintTower(rc.getType())
                    && rc.canSendMessage(robot.getLocation())
            ){
                rc.sendMessage(robot.getLocation() , RobotInfoCodec.encode(robot));
            }
        }
    }

    /**
     * right now we just upgrade paint towers because you always need more paint
     * @param rc
     * @throws GameActionException
     */
    private static void tryToUpgrade(RobotController rc) throws GameActionException {
        if( Util.isPaintTower(rc.getType()) && rc.canUpgradeTower(rc.getLocation()))
            rc.upgradeTower(rc.getLocation());
    }

    /**
     * Usually this is to report a tower (either ally or enemy) or a ruin.
     * @param rc
     */
    private static void readIncomingMessages(RobotController rc) {
        Message[] messages = rc.readMessages(-1);
        for (Message m : messages) {
            System.out.println("Tower received message: '#" + m.getSenderID() + " " + m.getBytes());
        }
    }

    private static void buildBots(RobotController rc) throws GameActionException {
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);

        if (!hasBuilt && rc.canBuildRobot(buildOrder[buildOrderIt], nextLoc)){
            rc.buildRobot(UnitType.SPLASHER, nextLoc);
            System.out.println("BUILT A " + buildOrder[buildOrderIt]);
            rc.setIndicatorString("BUILT: " + buildOrder[buildOrderIt]);
            hasBuilt =true;
        }
    }


    private static void slayYourEnemies(RobotController rc, RobotInfo[] robots ) throws GameActionException {
        // Murder any robots stupid enough to come close
        RobotInfo leastHealthBot = null;
        for(RobotInfo bot : robots){
            // can we attack this?
            if(bot.getTeam() != rc.getTeam() // Is bad?
                && rc.canAttack(bot.getLocation())){ // Can shoot it?
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
