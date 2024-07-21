package graph;

public class Node {
    private String id;
    private Character value;
    private int coordinateX = 0;
    private int coordinateY = 0;
    private String flag;

    public Node(String id, int coordinateX, int coordinateY, String flag) {
        this.id = id;
        this.value = this.id.charAt(0);
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.flag = flag;
    }

    public Node(String id, Character value, int coordinateX, int coordinateY, String flag) {
        this.id = id;
        this.value = value;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value.toString();
    }

    public String getFlag() {
        return flag;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinates(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    @Override
    public String toString() {
        return "\n\t{\n\t\t\"data\": { \"id\": \"" + this.id + "\", \"value\": \"" + this.value + "\", \"flag\": \"" + this.flag + "\"},\n\t\t\"position\": { \"x\": " + this.coordinateX + ", \"y\": " + this.coordinateY + "},\n\t},";
    }

    public String toJSON() {
        return "{ \"data\": { \"id\": \"" + this.id + "\", \"value\": \"" + this.value + "\", \"flag\": \"" + this.flag + "\"}, \"position\": { \"x\": " + this.coordinateX + ", \"y\": " + this.coordinateY + "}},\n";
    }
}
