package server.webservice;

import gramatics.ModifiedRegexParser;
import gramatics.NormalRegexParser;
import graph.Graph;
import graph.Node;
import helpClasses.NodeWithInt;
import helpClasses.RegexNodeWithString;
import nodes.RegexNode;
import nodes.RegexNodeGenerator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transitionFunction.TransitionTable;

import java.io.File;
import fileControllers.FileHandler;
import java.io.StringReader;


@RestController
@RequestMapping("/api/input")
public class RegexInput {

    private RegexNodeWithString createTree (String value){
        RegexNode regexNode;
        String annotationType;
        if(value==null){
            return null;
        }
        NormalRegexParser normalRegexParser = new NormalRegexParser(new StringReader(value));
        ModifiedRegexParser modifiedRegexParser = new ModifiedRegexParser(new StringReader(value));
        try {
            regexNode = normalRegexParser.input();
            annotationType = "normal";
        }catch (Exception e1){
            try {
                regexNode = modifiedRegexParser.input();
                annotationType = "modified";
            }catch (Exception | Error e2){
                return null;
            }
        }catch (Error e1){
            try {
                regexNode = modifiedRegexParser.input();
                annotationType = "modified";
            }catch (Exception | Error e2){
                return null;
            }
        }
        return new RegexNodeWithString(regexNode,annotationType);
    }

    @PostMapping("/validate/{value}")
    public ResponseEntity<String> validateInput(@PathVariable String value) {
        if(value==null){
            return new ResponseEntity<>("Input is empty", HttpStatus.BAD_REQUEST);
        }
        NormalRegexParser normalRegexParser = new NormalRegexParser(new StringReader(value));
        ModifiedRegexParser modifiedRegexParser = new ModifiedRegexParser(new StringReader(value));
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return new ResponseEntity<>("Input is not valid", HttpStatus.BAD_REQUEST);
        }
        /*try {
            regexNode = normalRegexParser.input();
            annotationType = "normal";
        }catch (Exception e1){
            try {
                regexNode = modifiedRegexParser.input();
                annotationType = "modified";
            }catch (Exception e2){
                regexNode=null;
                annotationType = null;
                return new ResponseEntity<>("Input is not valid", HttpStatus.BAD_REQUEST);
            }catch (Error e2){
                regexNode=null;
                annotationType = null;
                return new ResponseEntity<>("Input is not valid", HttpStatus.BAD_REQUEST);
            }
        }catch (Error e1){
            try {
                regexNode = modifiedRegexParser.input();
                annotationType = "modified";
            }catch (Exception e2){
                regexNode=null;
                annotationType = null;
                return new ResponseEntity<>("Input is not valid", HttpStatus.BAD_REQUEST);
            }catch (Error e2){
                regexNode=null;
                annotationType = null;
                return new ResponseEntity<>("Input is not valid", HttpStatus.BAD_REQUEST);
            }
        }*/
        if(regexNodeWithInt.getRegexNode()!=null) {
            int coordinateX = 50;
            int coordinateY = 50;
            Graph graph = new Graph();
            Node start = graph.addNode("start", coordinateX, coordinateY,"empty");
            NodeWithInt previousNode = regexNodeWithInt.getRegexNode().createGraph(graph, new NodeWithInt(start, coordinateX + 50), coordinateY, false);
            Node end = graph.addNode("end", previousNode.getCoordinateX(), coordinateY,"empty");
            graph.addEdge(previousNode.getNode(), end);
        }
        return new ResponseEntity<>("Input is valid", HttpStatus.OK);
    }

    @GetMapping("/generate/{numCharacters}/{length}/{modified}/{acceptCluster}/{acceptAlternative}/{acceptVolume}/{acceptTransitive}/{acceptRepeat}")
    public String generateRegex(@PathVariable int numCharacters, @PathVariable int length, @PathVariable boolean modified, @PathVariable boolean acceptCluster, @PathVariable boolean acceptAlternative, @PathVariable boolean acceptVolume, @PathVariable boolean acceptTransitive, @PathVariable boolean acceptRepeat) {
        if(numCharacters == 0 || length == 0){
            return null;
        }
        RegexNodeGenerator regexNodeGenerator = new RegexNodeGenerator();
        RegexNode regexNode=regexNodeGenerator.generateRandomRegex(numCharacters, length, acceptCluster, acceptAlternative, acceptVolume, acceptTransitive, acceptRepeat);
        if(regexNode!=null){
            boolean wasChanged = true;
            while (wasChanged) {
                wasChanged = regexNode.minimize();
                if (wasChanged) {
                    regexNode.convert();
                } else {
                    wasChanged = regexNode.convert();
                }
            }
            if(modified){
                return regexNode.modifiedToString();
            }else {
                return regexNode.normalToString();
            }
        }
        return null;
    }

    @GetMapping("/graph/{value}")
    public String getGraph(@PathVariable String value) {
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        if(regexNodeWithInt.getRegexNode()!=null){
            boolean wasChanged = true;
            while (wasChanged) {
                wasChanged = regexNodeWithInt.getRegexNode().minimize();
                if (wasChanged) {
                    regexNodeWithInt.getRegexNode().convert();
                } else {
                    wasChanged = regexNodeWithInt.getRegexNode().convert();
                }
            }
            int coordinateX = 50;
            int coordinateY = 50;
            Graph graph = new Graph();
            Node start = graph.addNode("start", coordinateX, coordinateY,"empty");
            NodeWithInt previousNode = regexNodeWithInt.getRegexNode().createGraph(graph, new NodeWithInt(start, coordinateX + 50), coordinateY, false);
            Node end = graph.addNode("end", previousNode.getCoordinateX(), coordinateY,"empty");
            graph.addEdge(previousNode.getNode(), end);
            return graph.toJSON();
        }
        return null;
    }

    @GetMapping("/alternative/{value}")
    public String getAlternativeForm(@PathVariable String value) {
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        if(regexNodeWithInt.getRegexNode()!=null){
            boolean wasChanged = true;
            while (wasChanged) {
                wasChanged = regexNodeWithInt.getRegexNode().convert();
            }
            if(regexNodeWithInt.getAnnotation().equals("normal")){
                return regexNodeWithInt.getRegexNode().modifiedToString();
            } else if (regexNodeWithInt.getAnnotation().equals("modified")) {
                return regexNodeWithInt.getRegexNode().normalToString();
            }
            return null;
        }
        return null;
    }

    @GetMapping("/minimal/{value}")
    public String getMinimalForm(@PathVariable String value) {
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        RegexNode clonedRegexNode = regexNodeWithInt.getRegexNode().clone();
        if(clonedRegexNode!=null){
            boolean wasChanged = true;
            while (wasChanged) {
                wasChanged = clonedRegexNode.minimize();
                if (wasChanged) {
                    clonedRegexNode.convert();
                } else {
                    wasChanged = clonedRegexNode.convert();
                }
            }
            if(regexNodeWithInt.getAnnotation().equals("normal") && !regexNodeWithInt.getRegexNode().normalToString().equals(clonedRegexNode.normalToString())){
                return clonedRegexNode.normalToString();
            } else if (regexNodeWithInt.getAnnotation().equals("modified") && !regexNodeWithInt.getRegexNode().modifiedToString().equals(clonedRegexNode.modifiedToString())) {
                return clonedRegexNode.modifiedToString();
            }
            return null;
        }
        return null;
    }

    @GetMapping("/functions/{value}")
    public String getFunctions(@PathVariable String value){
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        if(regexNodeWithInt.getRegexNode()!=null){
            TransitionTable transitionTable=new TransitionTable(regexNodeWithInt.getRegexNode());
            return transitionTable.functionsToJSON();
        }
        return null;
    }

    @GetMapping("/table/{value}")
    public String getTable(@PathVariable String value){
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        if(regexNodeWithInt.getRegexNode()!=null){
            TransitionTable transitionTable=new TransitionTable(regexNodeWithInt.getRegexNode());
            return transitionTable.tableToJSON();
        }
        return null;
    }

    @GetMapping("/c-code/{value}")
    public ResponseEntity<FileSystemResource> getCCode(@PathVariable String value){
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        if(regexNodeWithInt.getRegexNode()!=null){
            TransitionTable transitionTable=new TransitionTable(regexNodeWithInt.getRegexNode());
            String fileName = System.getProperty("user.dir")+"/code/c/generated_code.c";
            transitionTable.tableToCFile(fileName);

            File file = new File(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new FileSystemResource(file));
        }
        return null;
    }

    @GetMapping("/py-code/{value}")
    public ResponseEntity<FileSystemResource> getPythonCode(@PathVariable String value){
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        if(regexNodeWithInt.getRegexNode()!=null){
            TransitionTable transitionTable=new TransitionTable(regexNodeWithInt.getRegexNode());
            String fileName = System.getProperty("user.dir")+"/code/python/generated_code.py";
            transitionTable.tableToPythonFile(fileName);

            File file = new File(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new FileSystemResource(file));
        }
        return null;
    }

    @GetMapping("/java-package/{value}")
    public ResponseEntity<FileSystemResource> getJavaPackage(@PathVariable String value) {
        RegexNodeWithString regexNodeWithInt = createTree(value);
        if(regexNodeWithInt==null){
            return null;
        }
        if (regexNodeWithInt.getRegexNode() != null) {

            TransitionTable transitionTable=new TransitionTable(regexNodeWithInt.getRegexNode());
            String directory = System.getProperty("user.dir")+"/code/java/";
            transitionTable.tableToJavaFile(directory);

            // Zip the generated files
            String zipFileName = System.getProperty("user.dir")+"/code/generated_package.zip";
            FileHandler.zipDirectory(directory, zipFileName);

            File zipFile = new File(zipFileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFile.getName());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(zipFile.length())
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(new FileSystemResource(zipFile));
        }
        return null;
    }
}
