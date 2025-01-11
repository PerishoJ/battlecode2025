package f_c2;

import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * a semi-lazily inited map of the world, that is updated as the player progresses
 */
public class Mapper {

    static MapInfoDecorator[][] map;

    static void init (RobotController rc){

        map = new MapInfoDecorator[rc.getMapWidth()][];
//        int bytes = Clock.getBytecodeNum();
        // This costs about 6K bytecodes...not terrible.
        for(int mapX = 0 ; mapX<map.length ; mapX++){
            map[mapX] = new MapInfoDecorator[rc.getMapHeight()];
            for(int mapY = 0 ; mapY<rc.getMapHeight(); mapY++) {
                map[mapX][mapY] = null;
            }
        }
//        System.out.println("Mapping costs " + (Clock.getBytecodeNum() - bytes  ) + " bytecodes");
    }

    static void update(MapInfo[] nearbyInfo){
        for(MapInfo cell : nearbyInfo){
            Mapper.set(cell);
        }
    }

    static void set(MapInfo info){
        map[info.getMapLocation().x][info.getMapLocation().y] = new MapInfoDecorator(info, RobotPlayer.turnCount);
    }

    static void set(MapInfoDecorator info){
        map[info.mapInfo.getMapLocation().x][info.mapInfo.getMapLocation().y] = info;
    }

    static MapInfoDecorator get(MapLocation loc){
        return map[loc.x][loc.y];
    }

    static MapInfoDecorator get(int x, int y){
        return map[x][y];
    }

}
