package f_commanding;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.UnitType;

import java.util.List;

import static battlecode.common.UnitType.*;

public class CommanderBehavior {
  private static boolean isSquadDone = false;
  private static int squadProgress = 0;

    /*
    TODO LIST
      - [X] Setup build orders
      - [] Assign a squad leader
      - [] Assign squad invitation
      _ Tactics
         - Squad Types
            - [] scout - two soldiers to find ruins and enemy towers
            - [] build - splashers to build towers fast
            - [] Rush - kill enemy turrets
            - [] splash - spread paint as much as you can
            - [] Patrol - moppers that are just generally a nuisance to any passing enemies ... needs a splasher to make a road. Needs a supplying mopper for refills
            - [] resupply - a couple moppers that just move paint to one of the front lines
      - [] comms by dancing (mv costs only 1, pnt costs 5)
    */

  static UnitType[][] buildOrders;
  private static UnitType[] currentBuildOrder;
  private static Squad currentTactic;
  private static SquadBuildPhase phase = SquadBuildPhase.BUILD;

  private static RobotController rc;
  private static MapLocation lastBuilt;
  private static int currentSquad = 1;

  static void init(RobotController controller){
    rc = controller;
  }

  enum Squad {
    SCOUT,BUILD,RUSH,SPLASH,PATROL,RESUPPLY
  }

  enum SquadBuildPhase{
    BUILD,
    ASSIGN
  }

  static {
    buildOrders = new UnitType[Squad.values().length][];

    buildOrders[Squad.SCOUT.ordinal()]    = new UnitType[]{SOLDIER,SOLDIER};
    buildOrders[Squad.BUILD.ordinal()]    = new UnitType[]{SOLDIER,SPLASHER,SOLDIER,SPLASHER,};
    buildOrders[Squad.RUSH.ordinal()]     = new UnitType[]{SOLDIER,SOLDIER,SOLDIER,SOLDIER};
    buildOrders[Squad.SPLASH.ordinal()]   = new UnitType[]{SPLASHER,SPLASHER,SOLDIER};
    buildOrders[Squad.PATROL.ordinal()]   = new UnitType[]{MOPPER,MOPPER,SPLASHER};
    buildOrders[Squad.RESUPPLY.ordinal()] = new UnitType[]{MOPPER, MOPPER};
  }

  static void run() throws GameActionException{
    getUpdateTactics();
    buildSquad();
  }

  private static void assignSquad() {

  }

  private static void getUpdateTactics() {
    currentTactic = Squad.SCOUT; // TODO this is where the AI REALLY is...after all the plumbing work gets sorted.
  }


  private static void buildSquad() throws GameActionException {
    if(currentBuildOrder==null)
      return;
    List<MapLocation> availBldSquares = RangePatterns.getPattern(4);
    //error handling
    if(availBldSquares==null){System.err.println("ERROR TRYING TO GET BUILD PATTERN FOR SIZE 4 IN CommandBehavior");return;}
    //Build in first place you can.
    for (MapLocation bldLoc : availBldSquares){
      if (!isSquadDone
          && rc.canBuildRobot(currentBuildOrder[squadProgress], bldLoc)
      ) {
        rc.buildRobot(currentBuildOrder[squadProgress], bldLoc);
        lastBuilt = bldLoc;
        squadProgress++;
        assignBotToSquad();
        if (isSquadComplete()) {
          System.out.println("Completed "+currentSquad+"-squad-" +currentSquad+"..." );
          removeCurrentSquadFromBuild();
          currentSquad ++; //get another squad
        }
        //
        break; // don't try to build anymore, it'll just waste bytecodes
      }
    }
  }

  private static void assignBotToSquad() throws GameActionException {
    System.out.println("Attempting to send message to bot at " + lastBuilt);
    rc.sendMessage(lastBuilt,new CmdAssignSquad().setSquadNumber(currentSquad).encode());
  }

  private static void removeCurrentSquadFromBuild() {
    currentBuildOrder = null;
    squadProgress=0;
  }

  private static boolean isSquadComplete() {
    return squadProgress == currentBuildOrder.length;
  }




}
