package cs108.stanford.edu.bunnyworld;

public class ShapeRes {
    private String shapeName;
    private int id;

    protected ShapeRes(String name, int id) {
        shapeName = name;
        this.id = id;
    }

    protected int getResId(){ return id; }
    protected String getResName() { return shapeName; }
}
