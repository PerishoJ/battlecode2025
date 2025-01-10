package x_splashers;

import battlecode.common.*;

import java.util.*;


/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public class RobotPlayer {
    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    private static boolean hasBuilt = false;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
        System.out.println("I'm alive");

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("Hello world!");
        Globals.init(rc);
        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!
            Globals.update();
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // The same run() function is called for every robot on your team, even if they are
                // different types. Here, we separate the control depending on the UnitType, so we can
                // use different strategies on different robots. If you wish, you are free to rewrite
                // this into a different control structure!
                switch (rc.getType()){
                    case SOLDIER: runSoldier(rc); break; 
                    case MOPPER: runMopper(rc); break;
                    case SPLASHER: runSplasher(rc); break;
                    default: runTower(rc); break;
                    }
                }
             catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println("GameActionException");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println("Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

    private static void runSplasher(RobotController rc) throws GameActionException {
        // SPLISH SPLASH TIME TO TAKE A BATH!
        RobotInfo[] bots = rc.senseNearbyRobots();
        Message[] msgs = rc.readMessages(-1);

        PaintRefill.findNearbyPaintTowers(rc, bots, msgs);

        boolean isOnHallowedGround = rc.senseMapInfo(rc.getLocation()).getPaint().isAlly();
        //don't go below 1/2 health unless
        MapLocation bestSplashPlace = null;
        // Will I go into handicap paint levels( lvl <1/2 paint capacity) if I attack one more time???
        boolean willCauseNoHandicaps = ( rc.getPaint() >= (UnitType.SPLASHER.paintCapacity / 2 ) + UnitType.SPLASHER.attackCost);

        if(rc.isActionReady() && willCauseNoHandicaps) {
            SplasherBehavior.splashStuff(rc);
        }

        if(willCauseNoHandicaps) {
            moveRnd(rc); // Bumble around painting random junk
        } else { // oh no, the paint is running out, and we are about to take performance losses. Retreat
            PaintRefill.tryToRefillPaint(rc);
        }

    }

    /**
     * Run a single turn for towers.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runTower(RobotController rc) throws GameActionException{
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        // Pick a random robot type to build.
        int robotType = rng.nextInt(2);
//        if (robotType == 0 && rc.canBuildRobot(UnitType.SOLDIER, nextLoc)){
//            rc.buildRobot(UnitType.SOLDIER, nextLoc);
//            System.out.println("BUILT A SOLDIER");
//        }
//        if (robotType == 0 && rc.canBuildRobot(UnitType.MOPPER, nextLoc)){
//            rc.buildRobot(UnitType.MOPPER, nextLoc);
//            System.out.println("BUILT A MOPPER");
//        }
//        else



        if (!hasBuilt && rc.canBuildRobot(UnitType.SPLASHER, nextLoc)){
             rc.buildRobot(UnitType.SPLASHER, nextLoc);
             System.out.println("BUILT A SPLASHER");
            rc.setIndicatorString("SPLASHER NOT IMPLEMENTED YET");
            hasBuilt =true;
        }

        // Read incoming messages
        Message[] messages = rc.readMessages(-1);
        for (Message m : messages) {
            System.out.println("Tower received message: '#" + m.getSenderID() + " " + m.getBytes());
        }

        RobotInfo[] robots = rc.senseNearbyRobots();

        for(RobotInfo robot : robots){
            if(     robot.getTeam()==rc.getTeam()
                    && robot.getType().isRobotType()
                    && ( rc.getType() == UnitType.LEVEL_THREE_PAINT_TOWER || rc.getType() == UnitType.LEVEL_TWO_PAINT_TOWER || rc.getType() == UnitType.LEVEL_ONE_PAINT_TOWER)
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


    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runSoldier(RobotController rc) throws GameActionException{
        // Sense information about all visible nearby tiles.
        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();

        MapInfo curRuin = getRuinSquare(nearbyTiles);
        if (curRuin != null){
            goTryToBuildATowerThere(rc, curRuin);
        }

        // Move and attack randomly if no objective.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        if (rc.canMove(dir)){
            rc.move(dir);
        }

        // Try to paint beneath us as we walk to avoid paint penalties.
        // Avoiding wasting paint by re-painting our own tiles.
        MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
        if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
            rc.attack(rc.getLocation());
        }
    }

    private static void goTryToBuildATowerThere(RobotController rc, MapInfo curRuin) throws GameActionException {
        // keep moving towards that ruin
        MapLocation targetLoc = curRuin.getMapLocation();
        Direction dir = rc.getLocation().directionTo(targetLoc);
        if (rc.canMove(dir))
            rc.move(dir);
        // Mark the pattern we need to draw to build a tower here if we haven't already.
        MapLocation shouldBeMarked = curRuin.getMapLocation().subtract(dir);
        if (rc.senseMapInfo(shouldBeMarked).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)){
            rc.markTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
            System.out.println("Trying to build a tower at " + targetLoc);
        }
        // Fill in any spots in the pattern with the appropriate paint.
        for (MapInfo patternTile : rc.senseNearbyMapInfos(targetLoc, 8)){
            if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY){
                boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
                if (rc.canAttack(patternTile.getMapLocation()))
                    rc.attack(patternTile.getMapLocation(), useSecondaryColor);
            }
        }
        // Complete the ruin if we can.
        if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)){
            rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
            rc.setTimelineMarker("Tower built", 0, 255, 0);
            System.out.println("Built a tower at " + targetLoc + "!");
        }
    }

    private static MapInfo getRuinSquare(MapInfo[] nearbyTiles) {
        MapInfo curRuin = null;
        for (MapInfo tile : nearbyTiles){
            if (tile.hasRuin()){
                curRuin = tile;
            }

        }
        return curRuin;
    }


    /**
     * Run a single turn for a Mopper.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void runMopper(RobotController rc) throws GameActionException{
        // Move and attack randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        moveRnd(rc);

        if (rc.canMopSwing(dir)){
            rc.mopSwing(dir);
//            System.out.println("Mop Swing! Booyah!");
        }
        else if (rc.canAttack(nextLoc)){
            rc.attack(nextLoc);
        }
        // We can also move our code into different methods or classes to better organize it!
        updateEnemyRobots(rc);
    }


    private static MapLocation destination = null;
    private static int MAX_ATTENTION = -1;
    private static int DISTRACTION_LEVEL = 1;
    static int attentionToWhereIAmGoing = MAX_ATTENTION;
    public static void moveRnd(RobotController rc) throws GameActionException {
        if(MAX_ATTENTION == -1){
            MAX_ATTENTION = ( rc.getMapHeight() + rc.getMapWidth() ) / 2; // The average of height and width is a good guestimate...maybe?
        }
        boolean isCloseEnough = destination != null && rc.getLocation().distanceSquaredTo(destination) < 5;
        if(isCloseEnough) rc.setIndicatorString("Yep, close enough...new location!");
        if(attentionToWhereIAmGoing < 1 ) rc.setIndicatorString("I am bored ... new location");
        boolean shouldIChangeDirection = destination == null || attentionToWhereIAmGoing < 1 || isCloseEnough;
        if(shouldIChangeDirection){
            System.out.println("Changing directions now");
            //get a random location
            destination = new MapLocation( rng.nextInt(rc.getMapHeight()) , rng.nextInt(rc.getMapWidth()) );
            attentionToWhereIAmGoing = MAX_ATTENTION;
        }

        Bug.goTo(destination);
        attentionToWhereIAmGoing -= DISTRACTION_LEVEL;
        rc.setIndicatorLine(rc.getLocation(),destination,0,0,255);
    }

    public static void updateEnemyRobots(RobotController rc) throws GameActionException{
        // Sensing methods can be passed in a radius of -1 to automatically 
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0){
            rc.setIndicatorString("There are nearby enemy robots! Scary!");
            // Save an array of locations with enemy robots in them for possible future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++){
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            RobotInfo[] allyRobots = rc.senseNearbyRobots(-1, rc.getTeam());
            // Occasionally try to tell nearby allies how many enemy robots we see.
            if (rc.getRoundNum() % 20 == 0){
                for (RobotInfo ally : allyRobots){
                    if (rc.canSendMessage(ally.location, enemyRobots.length)){
                        rc.sendMessage(ally.location, enemyRobots.length);
                    }
                }
            }
        }
    }
}
