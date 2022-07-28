package mc.portalcraft.autosort.utils;

public class Location {
    private int x = 0;
    private int y;
    private int z;

    private String dim;


    public Location(int x, int y, int z, String dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;

    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setDim(String dim) {
        this.dim = dim;
    }


    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getZ() {
        return z;
    }
    public String getDim() {
        return dim;
    }
}
