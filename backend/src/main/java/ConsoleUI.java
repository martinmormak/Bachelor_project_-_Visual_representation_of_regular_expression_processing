import gramatics.ModifiedRegexParser;
import gramatics.NormalRegexParser;
import graph.Graph;
import graph.Node;
import helpClasses.NodeWithInt;
import nodes.RegexNode;
import nodes.RegexNodeGenerator;
import transitionFunction.TransitionTable;

import java.io.StringReader;

public class ConsoleUI {
    public void show(String input) {
        NormalRegexParser normalRegexParser = new NormalRegexParser(new StringReader(input));
        ModifiedRegexParser modifiedRegexParser = new ModifiedRegexParser(new StringReader(input));
        RegexNode node = null;
        try {
            node = normalRegexParser.input();
        } catch (Exception e1) {
            try {
                node = modifiedRegexParser.input();
            } catch (Exception e2) {
                System.err.println(e1);
                System.err.println(e2);
            } catch (Error e2) {
                System.err.println(e1);
                System.err.println(e2);
            }
        } catch (Error e1) {
            try {
                node = modifiedRegexParser.input();
            } catch (Exception e2) {
                System.err.println(e1);
                System.err.println(e2);
            } catch (Error e2) {
                System.err.println(e1);
                System.err.println(e2);
            }
        }
        if (node != null) {
            System.out.println(node);
            System.out.println(node.normalToString());
            System.out.println(node.modifiedToString());

            boolean wasChanged = true;
            while (wasChanged) {
                wasChanged = node.minimize();
                if (wasChanged) {
                    node.convert();
                } else {
                    wasChanged = node.convert();
                }
            }

            System.out.println(node);
            System.out.println(node.normalToString());
            System.out.println(node.modifiedToString());

            int coordinateX = 50;
            int coordinateY = 50;
            Graph graph = new Graph();
            Node start = graph.addNode("start", coordinateX, coordinateY, "empty");
            NodeWithInt previousNode = node.createGraph(graph, new NodeWithInt(start, coordinateX + 50), coordinateY, false);
            Node end = graph.addNode("end", previousNode.getCoordinateX(), coordinateY, "empty");
            graph.addEdge(previousNode.getNode(), end);
            String graphString = graph.toString();

            TransitionTable transitionTable = new TransitionTable(node);
            System.out.println(transitionTable);
            System.out.println(transitionTable.functionsToJSON());

            String fileName = System.getProperty("user.dir") + "/code/c/generated_code.c";
            String generatedCCode = transitionTable.tableToCFile(fileName);
            fileName = System.getProperty("user.dir") + "/code/python/generated_code.py";
            String generatedPythonCode = transitionTable.tableToPythonFile(fileName);
            String directory = System.getProperty("user.dir") + "/code/java/";
            String generatedJavaCode = transitionTable.tableToJavaFile(directory);
            RegexNodeGenerator regexNodeGenerator = new RegexNodeGenerator();
            node=regexNodeGenerator.generateRandomRegex(62,10,true,true,true,true,true);
            wasChanged = true;
            while (wasChanged) {
                wasChanged = node.minimize();
                if (wasChanged) {
                    node.convert();
                } else {
                    wasChanged = node.convert();
                }
            }
            System.out.println(node);
            System.out.println(node.normalToString());
            System.out.println(node.modifiedToString());

        }
    }
}
