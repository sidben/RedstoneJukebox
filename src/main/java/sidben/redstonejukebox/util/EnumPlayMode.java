package sidben.redstonejukebox.util;

public enum EnumPlayMode {
    SEQUENCE((byte) 0),
    RANDOM((byte) 1);


    private final byte _id;

    public byte getId()
    {
        return _id;
    }

    private EnumPlayMode(byte id) {
        _id = id;
    }
    
    
    public static EnumPlayMode parse(byte value)
    {
        if (value == 1) return RANDOM;
        return SEQUENCE;
    }
}
