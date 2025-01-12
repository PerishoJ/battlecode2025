package f_commanding;

public enum BotCmd {
    MOVE,ASSIGN_SQUAD;

    // used for encoding commands in messages
    public static final int COMMAND_SIZE_IN_BYTES ;
    // just so I don't have to re-code this...ever
    static {
        COMMAND_SIZE_IN_BYTES = Util.byteSize(BotCmd.values().length);
    }

}
