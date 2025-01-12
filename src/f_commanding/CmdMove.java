package f_commanding;

import battlecode.common.MapLocation;

import static f_commanding.BotCmd.COMMAND_SIZE_IN_BYTES;

public class CmdMove {
    int x,y;

    int encode(){
        return new Codec()
                .encodeInt(x,6)
                .encodeInt(y,6)
                .encodeInt(BotCmd.MOVE.ordinal(),COMMAND_SIZE_IN_BYTES)
                .encode();
    }

    CmdMove decode(int message){
        Codec msg = new Codec(message);
        msg.decodeInt(COMMAND_SIZE_IN_BYTES); // this is the Command ... we can throw that away. We know it's a move command ( i hope)
        int y = msg.decodeInt(6);
        int x = msg.decodeInt(6);
        return this; // so we can chain functions
    }
}
