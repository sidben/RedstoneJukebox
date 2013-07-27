package sidben.redstonejukebox.helper;


/**
 * Helper class to encapsulate the coordinates where a record is getting played.
 * 
 */
public class MusicCoords {

    public int x;
    public int y;
    public int z;
    public int dim;


    public MusicCoords(int xCoord, int yCoord, int zCoord, int dimension) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.dim = dimension;
    }


    public void set(int xCoord, int yCoord, int zCoord, int dimension) {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.dim = dimension;
    }


    public void set(MusicCoords musicCoords) {
        this.set(musicCoords.x, musicCoords.y, musicCoords.z, musicCoords.dim);
    }


    public void reset() {
        this.set(0, -1, 0, 0);
    }


    public boolean isEqual(int xCoord, int yCoord, int zCoord, int dimension) {
        return this.x == xCoord && this.y == yCoord && this.z == zCoord && this.dim == dimension;
    }


    public boolean isEqual(int xCoord, int yCoord, int zCoord) {
        // OBS: dimension is ignored
        return this.x == xCoord && this.y == yCoord && this.z == zCoord;
    }



}
