package d_mapper;

import battlecode.common.MapInfo;
import battlecode.common.RobotController;

/**
 * a semi-lazily inited map of the world, that is updated as the player progresses
 */
public class Mapper {

    static MapInfo[][] map;

    static void init (RobotController rc){

        map = new MapInfo[rc.getMapWidth()][];
//        int bytes = Clock.getBytecodeNum();
        // This costs about 6K bytecodes...not terrible.
        for(int mapX = 0 ; mapX<map.length ; mapX++){
            map[mapX] = new MapInfo[rc.getMapHeight()];
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
        map[info.getMapLocation().x][info.getMapLocation().y] = info;
    }

    static MapInfo get(int x, int y){
        return map[x][y];
    }

}
