package helpClasses;

import graph.Node;

public class NodeWithInt {
    private Node node;
    private int coordinateX;

    public NodeWithInt(Node node, int coordinateX) {
        this.node = node;
        this.coordinateX = coordinateX;
    }

    public Node getNode() {
        return node;
    }

    public int getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }
}