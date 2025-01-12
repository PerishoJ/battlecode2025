package f_commanding;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.UnitType;

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

  static RobotController rc;
  static UnitType[][] buildOrders;
  private static UnitType[] currentBuildOrder;
  private static Squad currentTactic;

  static void init(RobotController rc){
    CommanderBehavior.rc = rc;
  }

  static enum Squad {
    SCOUT,BUILD,RUSH,SPLASH,PATROL,RESUPPLY
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

  private static void buildSquad() throws GameActionException {
    if(currentBuildOrder==null)
      return;
    for (Direction dir : Direction.allDirections()){
      if (!isSquadDone && rc.canBuildRobot(currentBuildOrder[squadProgress], rc.getLocation().add(dir) )) {
        rc.buildRobot(currentBuildOrder[squadProgress], rc.getLocation().add(dir));
        squadProgress++;
        if (isSquadComplete()) {
          removeCurrentSquadFromBuild();
        }
      }
    }
  }

  private static void removeCurrentSquadFromBuild() {
    currentBuildOrder = null;
    squadProgress=0;
  }

  private static boolean isSquadComplete() {
    return squadProgress == currentBuildOrder.length;
  }

  private static void getUpdateTactics() {
    currentTactic = Squad.SCOUT; // TODO this is where the AI REALLY is...after all the plumbing work gets sorted.
  }


}
