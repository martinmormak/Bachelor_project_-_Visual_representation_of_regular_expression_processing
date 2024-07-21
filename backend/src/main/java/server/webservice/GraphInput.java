package server.webservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graph.Edge;
import graph.Graph;
import graph.Node;
import nodes.RegexNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/graph")
public class GraphInput {
    @PostMapping("/validate/")
    public ResponseEntity<String> validateInput(@RequestBody String jsonString) {
        RegexNode regex = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // Create a Graph instance
            Graph graph = new Graph();

            // Process nodes and edges
            processNodes(jsonNode, graph);
            processEdges(jsonNode, graph);

            // Access nodes and edges from the graph
            List<Node> nodes = graph.getNodes();
            List<Edge> edges = graph.getEdges();

            regex = graph.reverseConverting();

            boolean wasChanged = true;
            while (wasChanged) {
                wasChanged = regex.minimize();
                if (wasChanged) {
                    regex.convert();
                } else {
                    wasChanged = regex.convert();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(regex.normalToString(), HttpStatus.OK);
    }

    private static void processNodes(JsonNode jsonNode, Graph graph) {
        JsonNode nodesArray = jsonNode.get("nodes");
        if (nodesArray != null && nodesArray.isArray()) {
            for (JsonNode nodeElement : nodesArray) {
                JsonNode dataNode = nodeElement.get("data");
                JsonNode positionNode = nodeElement.get("position");

                String id = dataNode.get("id").asText();
                String value = dataNode.get("value").asText();
                int coordinateX = positionNode.get("x").asInt();
                int coordinateY = positionNode.get("y").asInt();
                String flag = dataNode.get("flag").asText();

                graph.addNode(id, value.charAt(0), coordinateX, coordinateY, flag);
            }
        }
    }

    private static void processEdges(JsonNode jsonNode, Graph graph) {
        JsonNode edgesArray = jsonNode.get("edges");
        if (edgesArray != null && edgesArray.isArray()) {
            for (JsonNode edgeElement : edgesArray) {
                JsonNode dataNode = edgeElement.get("data");

                String sourceNodeId = dataNode.get("source").asText() + '1';
                String targetNodeId = dataNode.get("target").asText() + '1';

                Node sourceNode = graph.getNodeById(sourceNodeId);
                Node targetNode = graph.getNodeById(targetNodeId);

                if (sourceNode != null && targetNode != null) {
                    graph.addEdge(sourceNode, targetNode);
                } else {
                    System.err.println("Source or target node not found for edge with IDs: " +
                            sourceNodeId + " -> " + targetNodeId);
                }
            }
        }
    }
}
