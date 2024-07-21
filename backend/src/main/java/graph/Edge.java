package graph;

public class Edge {
    private String id;
    private String value;
    private Node source;
    private Node target;
    private String flag;

    public Edge(Node source, Node target, String flag) {
        this.id = source.getId() + target.getId();
        this.value = extractNonNumericCharacters(this.id);
        this.source = source;
        this.target = target;
        this.flag = flag;
    }

    private String extractNonNumericCharacters(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char currentChar = str.charAt(i);
            if (!Character.isDigit(currentChar)) {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    public String getId() {
        return id;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "\n\t{\n\t\t\"data\": { \"id\": \"" + this.id + "\", \"value\": \"" + this.value + "\", \"flag\": \"" + this.flag + "\", \"source\": \"" + this.source.getId() + "\", \"target\": \"" + this.target.getId() + "\" },\n\t},";
    }

    public String toJSON() {
        return "{ \"data\": { \"id\": \"" + this.id + "\", \"value\": \"" + this.value + "\", \"flag\": \"" + this.flag + "\", \"source\": \"" + this.source.getId() + "\", \"target\": \"" + this.target.getId() + "\" }},\n";
    }
}
