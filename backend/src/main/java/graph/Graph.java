package graph;

import nodes.RegexNode;
import nodes.RegexNodeEnum;

import java.util.LinkedList;

public class Graph {
    private LinkedList<Node> nodes;
    private LinkedList<Edge> edges;

    public Graph() {
        this.nodes = new LinkedList<>();
        this.edges = new LinkedList<>();
    }

    public LinkedList<Node> getNodes() {
        return nodes;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public Node getNodeById(String id) {
        for (Node node : this.nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    public Node addNode(String identifier, int coordinateX, int coordinateY) {
        return addNode(identifier, coordinateX, coordinateY, "terminal");
    }

    public Node addNode(String identifier, Character value, int coordinateX, int coordinateY, String flag) {
        boolean found = false;
        int max = 0;

        for (Node node : nodes) {
            String nodeId = node.getId();
            if (nodeId.matches("^" + identifier + "\\d*$")) {
                found = true;
                String numPart = nodeId.replaceAll("\\D", ""); // Extract the number part
                if (!numPart.isEmpty()) {
                    int num = Integer.parseInt(numPart);
                    if (num > max) {
                        max = num;
                    }
                }
            }
        }
        if (isInteger(identifier)) {
            int number = Integer.parseInt(identifier);
            max = max - number * 10;
        }
        Node node;
        if (found) {
            node = new Node(identifier + (max + 1), value, coordinateX, coordinateY, flag);
            nodes.add(node);
        } else {
            node = new Node(identifier + 1, value, coordinateX, coordinateY, flag);
            nodes.add(node);
        }
        return node;
    }

    public Node addNode(String identifier, int coordinateX, int coordinateY, String flag) {
        boolean found = false;
        int max = 0;

        for (Node node : nodes) {
            String nodeId = node.getId();
            if (nodeId.matches("^" + identifier + "\\d*$")) {
                found = true;
                String numPart = nodeId.replaceAll("\\D", ""); // Extract the number part
                if (!numPart.isEmpty()) {
                    int num = Integer.parseInt(numPart);
                    if (num > max) {
                        max = num;
                    }
                }
            }
        }
        if (isInteger(identifier)) {
            int number = Integer.parseInt(identifier);
            max = max - number * 10;
        }
        Node node;
        if (found) {
            node = new Node(identifier + (max + 1), coordinateX, coordinateY, flag);
            nodes.add(node);
        } else {
            node = new Node(identifier + 1, coordinateX, coordinateY, flag);
            nodes.add(node);
        }
        return node;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public void addEdge(Node sourceNode, Node targetNode) {
        String id = sourceNode.getId() + targetNode.getId();
        for (Edge edge : edges) {
            if (edge.getId().equals(id)) {
                return;
            }
        }
        String flag = "normal";
        /*if (sourceNode.getFlag() == "empty") {
            String sourceFlag = sourceNode.getId();
            String targetFlag = targetNode.getId();
            if (targetNode.getFlag() == "empty") {
                int sourceStartIndexNormal = sourceFlag.indexOf("Start");
                int targetEndIndexNormal = targetFlag.indexOf("End");
                int sourceStartIndexAlternative = sourceFlag.indexOf("End");
                int targetEndIndexAlternative = targetFlag.indexOf("Start");

                if (sourceStartIndexNormal != -1 && targetEndIndexNormal != -1) {
                    String sourceContent = sourceFlag.substring(0, sourceStartIndexNormal);
                    String targetContent = targetFlag.substring(0, targetEndIndexNormal);

                    String sourceNumber = sourceFlag.substring(sourceStartIndexNormal + 5);
                    String targetNumber = targetFlag.substring(targetEndIndexNormal + 3);

                    if (sourceContent.equals(targetContent) && sourceNumber.equals(targetNumber)) {
                        flag = "bothEmptyWithoutArrow";
                    } else {
                        flag = "bothEmpty";
                    }
                } else if (sourceStartIndexAlternative != -1 && targetEndIndexAlternative != -1) {
                    String sourceContent = sourceFlag.substring(0, sourceStartIndexAlternative);
                    String targetContent = targetFlag.substring(0, targetEndIndexAlternative);

                    String sourceNumber = sourceFlag.substring(sourceStartIndexAlternative + 3);
                    String targetNumber = targetFlag.substring(targetEndIndexAlternative + 5);

                    if (sourceContent.equals(targetContent) && sourceNumber.equals(targetNumber)) {
                        flag = "bothEmptyWithoutArrow";
                    } else {
                        flag = "bothEmpty";
                    }
                } else if (sourceNode.getId().indexOf("Start") != -1) {
                    flag = "bothEmptyWithoutArrow";
                } else {
                    flag = "bothEmpty";
                }
            } else {
                flag = "sourceEmpty";
            }
        } else if (targetNode.getFlag() == "empty") {
            int sourceStartIndex = targetNode.getId().indexOf("end");
            if (sourceNode.getCoordinateY() == targetNode.getCoordinateY() && sourceStartIndex == -1) {
                flag = "targetEmptyWithoutArrow";
            } else {
                flag = "targetEmpty";
            }
        }*/
        if (targetNode.getFlag() == "empty") {
            flag = "targetEmpty";
        }
        Edge edge = new Edge(sourceNode, targetNode, flag);
        this.edges.add(edge);
    }

    public RegexNode reverseConverting() {
        RegexNode node = null;
        for (Edge actualEdge : this.edges) {
            if (actualEdge.getSource().getId().equals("start1")) {
                node = toRegexNode(actualEdge);
            }
        }
        return node;
    }

    private String getOppositeStringSource(Edge startEdge) {
        if (startEdge.getSource().getId().contains("Start")) {
            return startEdge.getSource().getId().replace("Start", "End");
        } else if (startEdge.getSource().getId().contains("End")) {
            return startEdge.getSource().getId().replace("End", "Start");
        }
        return null;
    }

    private String getOppositeStringTarget(Edge startEdge) {
        if (startEdge.getTarget().getId().contains("Start")) {
            return startEdge.getTarget().getId().replace("Start", "End");
        } else if (startEdge.getTarget().getId().contains("End")) {
            return startEdge.getTarget().getId().replace("End", "Start");
        }
        return null;
    }

    private boolean checkIfIsCorrectEdge(Edge oldEdge, String target, LinkedList<Edge> visitedEdges) {
        if (visitedEdges == null) {
            visitedEdges = new LinkedList<>();
        }
        for (Edge edge : edges) {
            if (!visitedEdges.contains(edge) && edge.getSource().getId().equals(oldEdge.getTarget().getId())) {
                if (edge.getTarget().getId().equals(target)) {
                    visitedEdges.add(edge);
                    return true;
                } else {
                    visitedEdges.add(edge);
                    return checkIfIsCorrectEdge(edge, target, visitedEdges);
                }
            }
        }
        return false;
    }

    private boolean checkIfIsCorrectEdge(Edge oldEdge) {
        for (Edge edge : edges) {
            if (edge.getSource().getId().equals(oldEdge.getTarget().getId())) {
                if (edge.getTarget().getId().equals("start")) {
                    return true;
                } else {
                    return checkIfIsCorrectEdge(edge, "start", null);
                }
            }
        }
        return false;
    }

    private RegexNode toRegexNode(Edge startEdge) {
        RegexNode node = null;
        boolean isSpecial = false;
        if (startEdge.getSource().getId().startsWith("alternative") || startEdge.getSource().getId().startsWith("volume") || startEdge.getSource().getId().startsWith("transparency")) {
            isSpecial = true;
        }
        Edge actualEdge = startEdge;
        String source = getOppositeStringSource(startEdge);
        String target = getOppositeStringTarget(startEdge);
        while (!this.edges.isEmpty()) {
            if (isSpecial == true && actualEdge.getTarget().getId().equals(source) && !source.equals(startEdge.getSource().getId())) {
                this.edges.remove(actualEdge);
                break;
            }
            Edge oldEdge = actualEdge;
            Node continueNode = null;
            if (actualEdge.getTarget().getFlag().equals("terminal")) {
                if (node == null) {
                    node = new RegexNode(RegexNodeEnum.EMPTY, new RegexNode(RegexNodeEnum.CHARACTER, actualEdge.getTarget().getValue()));
                    this.edges.remove(actualEdge);
                } else {
                    node.addToList(new RegexNode(RegexNodeEnum.CHARACTER, actualEdge.getTarget().getValue()));
                    this.edges.remove(actualEdge);
                }
                for (Edge edge : this.edges) {
                    if (edge.getSource().getId().equals(oldEdge.getTarget().getId())) {
                        actualEdge = edge;
                        break;
                    }
                }
            } else if (actualEdge.getTarget().getFlag().equals("empty")) {
                if (isSpecial == true && actualEdge.getSource().getId().equals(target) && !target.equals(startEdge.getTarget().getId())) {
                    this.edges.remove(actualEdge);
                    break;
                }
                if (actualEdge.getTarget().getId().startsWith("alternative")) {
                    for (Edge edge : this.edges) {
                        if (edge.getTarget().getId().equals(getOppositeStringTarget(oldEdge))) {
                            continueNode = edge.getTarget();
                            break;
                        }
                    }
                    RegexNode alternativeNode = new RegexNode(RegexNodeEnum.ALTERNATIVE);
                    //find first alternative edge
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(oldEdge.getTarget().getId())) {
                            actualEdge = edge;
                            break;
                        }
                    }
                    alternativeNode.addToList(this.toRegexNode(actualEdge));
                    //find second alternative edge
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(oldEdge.getTarget().getId())) {
                            actualEdge = edge;
                            break;
                        }
                    }
                    alternativeNode.addToList(this.toRegexNode(actualEdge));
                    RegexNode clusterNode = new RegexNode(RegexNodeEnum.CLUSTER, alternativeNode);
                    if (node == null) {
                        node = new RegexNode(RegexNodeEnum.EMPTY, clusterNode);
                    } else {
                        node.addToList(clusterNode);
                    }
                    this.edges.remove(oldEdge);
                    oldEdge = actualEdge;
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(continueNode.getId())) {
                            actualEdge = edge;
                            break;
                        }
                    }
                } else if (actualEdge.getTarget().getId().startsWith("volume")) {
                    //remove edge between start and end
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(oldEdge.getTarget().getId()) && edge.getTarget().getId().equals(getOppositeStringTarget(oldEdge))) {
                            actualEdge = edge;
                            continueNode = edge.getTarget();
                            break;
                        }
                    }
                    this.edges.remove(actualEdge);
                    //find edge from start to the nearest node
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(oldEdge.getTarget().getId())) {
                            actualEdge = edge;
                            break;
                        }
                    }
                    //remove edge that brought me to current node
                    this.edges.remove(oldEdge);
                    oldEdge = actualEdge;
                    if (node == null) {
                        node = new RegexNode(RegexNodeEnum.EMPTY, new RegexNode(RegexNodeEnum.VOLUME, this.toRegexNode(actualEdge).getList()));
                    } else {
                        node.addToList(new RegexNode(RegexNodeEnum.VOLUME, this.toRegexNode(actualEdge).getList()));
                    }
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(continueNode.getId())) {
                            actualEdge = edge;
                            break;
                        }
                    }
                } else if (actualEdge.getTarget().getId().startsWith("transparency")) {
                    //remove edge between start and end
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(oldEdge.getTarget().getId()) && edge.getTarget().getId().equals(getOppositeStringTarget(oldEdge))) {
                            actualEdge = edge;
                            continueNode = edge.getTarget();
                            break;
                        }
                    }
                    this.edges.remove(actualEdge);
                    //find edge from start to the nearest node
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(getOppositeStringTarget(oldEdge))) {
                            if (checkIfIsCorrectEdge(edge, oldEdge.getTarget().getId(), null)) {
                                actualEdge = edge;
                                break;
                            }
                        }
                    }
                    //remove edge that brought me to current node
                    this.edges.remove(oldEdge);
                    oldEdge = actualEdge;
                    if (node == null) {
                        node = new RegexNode(RegexNodeEnum.EMPTY, new RegexNode(RegexNodeEnum.TRANSPARENCY, this.toRegexNode(actualEdge).getList()));
                    } else {
                        node.addToList(new RegexNode(RegexNodeEnum.TRANSPARENCY, this.toRegexNode(actualEdge).getList()));
                    }
                    for (Edge edge : this.edges) {
                        if (edge.getSource().getId().equals(continueNode.getId())) {
                            actualEdge = edge;
                            break;
                        }
                    }
                }
            }
        }
        return node;
    }

    public String toJSON() {
        String string = "[";
        for (Node node : this.nodes) {
            string = string + node.toJSON();
        }
        for (Edge edge : this.edges) {
            string = string + edge.toJSON();
        }
        string = string.substring(0, string.length() - 2) + "]";
        return string;
    }

    @Override
    public String toString() {
        String string = "export const elements = [";
        for (Node node : this.nodes) {
            string = string + node.toString();
        }
        for (Edge edge : this.edges) {
            string = string + edge.toString();
        }
        string = string + "\n];";
        return string;
    }
}
