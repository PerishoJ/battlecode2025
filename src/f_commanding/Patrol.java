package f_commanding;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

class Patrol {

  RingBuffer<MapLocation> patrolPath;
  int DISTANCE_SQR_THRESHOLD = 4;
  void run(RobotController rc) throws GameActionException {

    if(patrolPath.poll()==null)
      return; //nowhere to go

    //Start moving
    Bug.goTo(patrolPath.poll());

    //get next location on the path
    if(rc.getLocation().distanceSquaredTo(patrolPath.poll())<=DISTANCE_SQR_THRESHOLD){
      if(patrolPath.take() == null){
        System.out.println("Destination reached at " + rc.getLocation());//get next
      }
    }
  }

  Patrol(int patrolLength){
    patrolPath = new RingBuffer<>(patrolLength);
  }

  void addWayPoint( MapLocation waypoint){
    patrolPath.push(waypoint);
  }

  /**
   * Adds a location to go to.
   * @param MoveCommand the next location to go to
   */
  void handleMoveCommand(CommandMove mv){
    addWayPoint(new MapLocation(mv.x,mv.y));
  }

}
