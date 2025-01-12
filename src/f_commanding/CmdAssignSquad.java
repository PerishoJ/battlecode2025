package f_commanding;

import battlecode.common.Message;

import static f_commanding.BotCmd.COMMAND_SIZE_IN_BYTES;

public class CmdAssignSquad {

    int squadNumber;
    CmdAssignSquad setSquadNumber(int squad){
        squadNumber = squad;
        return this;
    }
    /**
     *
     * @return the encoded int value that contains the data from this class
     */
    int encode(){
        return new Codec()
                .encodeInt(squadNumber,6) //highly doubt there will be more than 100 squads
                .encodeInt(BotCmd.MOVE.ordinal(),COMMAND_SIZE_IN_BYTES)
                .encode();
    }

    CmdAssignSquad decode(int message){
        Codec msg = new Codec(message);
        msg.decodeInt(COMMAND_SIZE_IN_BYTES); // this is the Command ... we can throw that away. We know it's a move command ( i hope)
        squadNumber =  msg.decodeInt(6); // just has squad number
        return this; //just so we can use function chaining
    }

    static int decodeSquad(Message message){
        Codec msg = new Codec(message.getBytes());
        msg.decodeInt(COMMAND_SIZE_IN_BYTES); // this is the Command ... we can throw that away. We know it's a move command ( i hope)
        return  msg.decodeInt(6);
    }
}
