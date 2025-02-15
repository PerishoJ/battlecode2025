package f_commanding;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;

/**
 * Handles HQ commands.
 */
class TroopBehavior {

  public static final int UNASSIGNED = -1;
  RingBuffer<MapLocation> patrolPath;
  int DISTANCE_SQR_THRESHOLD = 4;
  int squadNumber = UNASSIGNED;
  /*
  TODO LIST
   - Accept a squad leader
   - Accept squad invitation
   - Copy squad leader behavior
      - Go where they go
      - fall back when leader falls back
      - attack when leader attacks
   _ Squad Leader
      - Go where HQ tells you
      - report back ally and enemy HQ's
      - calculate whether to attack or retreat !?
      - Squad Types
          - scout squad - two soldiers to find ruins and enemy towers
          - build squad - splashers to build towers fast
        - Rush squad - kill enemy turrets
        - splash squad - spread paint as much as you can
        - Patrol squad - moppers that are just generally a nuisance to any passing enemies ... needs a splasher to make a road. Needs a supplying mopper for refills
        - resupply squad - a couple moppers that just move paint to one of the front lines
   - comms by dancing (mv costs only 1, pnt costs 5)
  */




  void run(RobotController rc, Message[] msgs) throws GameActionException {
    if(patrolPath.poll()==null)
      return; //nowhere to go
    for(Message msg : msgs ){
      if(BotCmd.ASSIGN_SQUAD == Codec.getCommand(msg) ){
        squadNumber = CmdAssignSquad.decodeSquad(msg);
        System.out.println("Newly assigned to squad " + squadNumber);
      }
    }

    //Start moving
    Bug.goTo(patrolPath.poll());
    //get next location on the path
    if(hasReachedWaypoint(rc)){
      getNextWayPoint(rc);
    }
  }

  private void getNextWayPoint(RobotController rc) {
    if(patrolPath.take() == null){
      System.out.println("Destination reached at " + rc.getLocation());//get next
    }
  }

  private boolean hasReachedWaypoint(RobotController rc) {
    return rc.getLocation().distanceSquaredTo(patrolPath.poll()) <= DISTANCE_SQR_THRESHOLD;
  }

  TroopBehavior(int patrolLength){
    patrolPath = new RingBuffer<>(patrolLength);
  }

  void addWayPoint( MapLocation waypoint){
    patrolPath.push(waypoint);
  }

  /**
   * Adds a location to go to.
   * @param MoveCommand the next location to go to
   */
  void handleMoveCommand(CmdMove mv){
    addWayPoint(new MapLocation(mv.x,mv.y));
  }

}
