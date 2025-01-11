package e_mapper;

import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;

public class MapInfoDecorator  {

    // Used by Splasher to determine which places to Splash. Needed for better dynamic programming
    int paintEffectiveness = -1;
    int lastUpdated = -SPOILAGE;// Turn count that tile was updated
    static int SPOILAGE = 50; // number of turns before data is considered stale

    MapInfo mapInfo;

    public MapInfoDecorator(MapInfo info, int lastUpdated){
        this.mapInfo = info;
        this.lastUpdated = lastUpdated;
    }

    public MapInfoDecorator(MapInfo info){
        this.mapInfo = info;
        lastUpdated = RobotPlayer.turnCount;
    }

    /**
     * If your info
     * @return
     */
    boolean isFresh(){
        return (RobotPlayer.turnCount - lastUpdated >= SPOILAGE);
    }

    void taint(){
        lastUpdated = -SPOILAGE;
    }

}
