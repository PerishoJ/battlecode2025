package f_commanding;

import battlecode.common.MapLocation;

public class CommandMove {
    int x,y;

    int encode(){
        return new Codec()
                .encodeInt(x,6)
                .encodeInt(y,6)
                .encodeInt(Commands.MOVE.ordinal(),3)
                .encode();
    }

    static MapLocation decode(int message){
        Codec msg = new Codec(message);
        msg.decodeInt(3); // this is the Command ... we can throw that away. We know it's a move command ( i hope)
        int y = msg.decodeInt(6);
        int x = msg.decodeInt(6);
        return new MapLocation(x,y);
    }

}
