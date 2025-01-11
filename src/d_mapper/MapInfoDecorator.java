package d_mapper;

import battlecode.common.MapInfo;
import battlecode.common.MapLocation;
import battlecode.common.PaintType;
import battlecode.common.RobotController;

public class MapInfoDecorator extends MapInfo {

    // Used by Splasher to determine which places to Splash. Needed for better dynamic programming
    int paintEffectiveness = -1;
    int lastUpdated = -SPOILAGE;// Turn count that tile was updated
    static int SPOILAGE = 50; // number of turns before data is considered stale

    public MapInfoDecorator(MapInfo info, int lastUpdated){
        super(  info.getMapLocation(),
                info.isPassable(),
                info.isWall(),
                info.getPaint(),
                info.getMark(),
                info.hasRuin());
        this.lastUpdated = lastUpdated;
    }

    public MapInfoDecorator(MapInfo info){
        super(  info.getMapLocation(),
                info.isPassable(),
                info.isWall(),
                info.getPaint(),
                info.getMark(),
                info.hasRuin());
    }



    public MapInfoDecorator(MapLocation loc, boolean isPassable, boolean isWall, PaintType paint, PaintType mark, boolean hasRuin){
        super( loc,  isPassable,  isWall,  paint,  mark,  hasRuin);
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
