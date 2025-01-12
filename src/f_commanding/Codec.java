package f_commanding;


import battlecode.common.Message;

/**
 * This codec works like a stack, so you must read the values in the reverse order they were added.
 * Also, fairly immutable. Once you've encoded a value, it's stuck.
 *
 * HIGHLY suggest this be used in specific codec classes that KNOW what information needs to be serialized
 * and implements a higher level of serialization, rather than using this directly.
 */
class Codec {

    private int msg = 0;

    Codec(){}
    Codec(int message){msg = message;}

    /**
     * Encode fields in a Builder Style fashion;
     * @param value
     * @param bytes
     * @return
     */
    protected Codec encodeInt(int value, int bytes){
        //apply bit mask to ensure well-behaved
        value = value & ( (1<<bytes) - 1);
        // bit shift any existing values up and add the to front
        msg = msg<<bytes | value;
        // normally, I would verify and check for OUT-OF-BOUNDS...but bytecodes are really limited
        return this;
    }
    int encode(){
        return msg;
    }

    /**
     * Must be decoded in the reverse order that fields were encoded
     * @param bytes
     * @return
     */
    protected int decodeInt(int bytes){
        int value = msg & ( (1<<bytes) - 1); // mask the front values
        msg = msg >> bytes; // move to the next value
        return value;
    }

    protected BotCmd getCommand(){
        int cmd = msg & ( (1<<BotCmd.COMMAND_SIZE_IN_BYTES) - 1);
        if(cmd<BotCmd.values().length){
            return BotCmd.values()[cmd];
        } else {
            System.err.println("Error trying to find command with ordinal of " + cmd);
            return null;
        }
    }

    static BotCmd getCommand(Message msg){
        return getCommand(msg.getBytes());
    }

    static BotCmd getCommand(int msg){
        int cmd = msg & ( (1<<BotCmd.COMMAND_SIZE_IN_BYTES) - 1);
        if(cmd<BotCmd.values().length){
            return BotCmd.values()[cmd];
        } else {
            System.err.println("Error trying to find command with ordinal of " + cmd);
            return null;
        }
    }

}
