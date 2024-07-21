package helpClasses;

import nodes.RegexNode;

public class RegexNodeWithString {
    private RegexNode node;
    private String annotation;

    public RegexNodeWithString(RegexNode node, String annotation) {
        this.node = node;
        this.annotation = annotation;
    }

    public RegexNode getRegexNode() {
        return node;
    }

    public String getAnnotation() {
        return annotation;
    }
}