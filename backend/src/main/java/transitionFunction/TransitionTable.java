package transitionFunction;

import fileControllers.FileHandler;
import nodes.RegexNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransitionTable {
    List<Character> listOfCharacters;
    List<RegexNode> listOfStates;
    ArrayList<ArrayList<Integer>> table;

    public TransitionTable(RegexNode regexNode) {
        regexNode.setRoot();
        listOfCharacters = new ArrayList<>();
        listOfStates = new ArrayList<>();
        table = new ArrayList<>();
        RegexNode originalNode = regexNode.clone();
        boolean wasChanged;
        do {
            wasChanged = originalNode.reverseConvert();
        } while (wasChanged);
        for (Character character : originalNode.normalToString().toCharArray()) {
            if (character != '|' && character != '*' && character != '+' & character != '?' && character != '(' & character != ')') {
                if (!listOfCharacters.contains(character)) {
                    listOfCharacters.add(character);
                }
            }
        }
        RegexNode q0 = originalNode.clone();
        boolean overflow = q0.addTokens();
        if (overflow) {
            q0.addTokenToTheEnd();
        }
        listOfStates.add(q0);
        for (int index = 0; index < listOfStates.size(); index++) {
            table.add(new ArrayList<>());
            for (int j = 0; j < listOfCharacters.size(); j++) {
                table.get(index).add(-1);
            }
            RegexNode q = listOfStates.get(index);
            int characterIndex = 0;
            for (Character character : listOfCharacters) {
                RegexNode newNode = originalNode.clone();
                RegexNode stateForExpand = q.clone();
                overflow = stateForExpand.getFunction(character, newNode);
                if (overflow) {
                    newNode.addTokenToTheEnd();
                }
                if (!originalNode.isSame(originalNode, newNode)) {
                    boolean wasFounded = false;
                    int function = 0;
                    for (RegexNode state : listOfStates) {
                        if (state.isSame(state, newNode)) {
                            function = listOfStates.indexOf(state);
                            wasFounded = true;
                            break;
                        }
                    }
                    if (!wasFounded) {
                        listOfStates.add(newNode);
                        function = listOfStates.size() - 1;
                    }
                    table.get(index).set(characterIndex, function);
                }
                characterIndex++;
            }
        }
        for (int index = 0; index < listOfStates.size(); index++) {
            RegexNode q = listOfStates.get(index);
        }
    }

    public String functionsToJSON() {
        String string = "[\n{\"state\": null, \"input\": null, \"nextState\": \"0\", \"value\": \"" + listOfStates.get(0).functionToString() + "\"}";
        for (int index = 0; index < listOfStates.size(); index++) {
            int idx = 0;
            for (int stateIndex : table.get(index)) {
                if (stateIndex != -1) {
                    string = string + ",\n{\"state\": \"" + index + "\", \"input\": \"" + listOfCharacters.get(idx) + "\", \"nextState\": \"" + stateIndex + "\", \"value\": \"" + listOfStates.get(stateIndex).functionToString() + "\"}";
                } else {
                    string = string + ",\n{\"state\": \"" + index + "\", \"input\": \"" + listOfCharacters.get(idx) + "\", \"nextState\": null, \"value\": \"⊥\"}";
                }
                idx++;
            }
        }
        string = string + "\n]";
        return string;
    }

    public String tableToJSON() {
        String string = "[";
        for (int index = 0; index < listOfStates.size(); index++) {
            int idx = 0;
            for (int stateIndex : table.get(index)) {
                string = string + "\n{\"id\": \"q" + index + listOfCharacters.get(idx) + "-cell\",\"value\": ";
                if (stateIndex != -1) {
                    string = string + "\"q" + stateIndex + "\"";
                    if(listOfStates.get(stateIndex).isEndState()){
                        string = string + ",\"flag\": \"end\"";
                    }else{
                        string = string + ",\"flag\": \"normal\"";
                    }
                } else {
                    string = string + "\"⊥\",\"flag\": \"normal\"";
                }
                string = string + "},";
                idx++;
            }
        }
        string = string.substring(0, string.length() - 1);
        string = string + "\n]";
        return string;
    }

    public String tableToCFile(String fileName) {
        String string = "#include <stdio.h>\n#include <string.h>\n\n// Define constants for states\n";
        for (int index = 0; index < listOfStates.size(); index++) {
            string = string +
                    "#define Q" + index + " " + index + "\n";
        }
        string = string + "\n#define ERROR_STATE -1\n\nint state;\nint end;";
        for (int index = 0; index < listOfStates.size(); index++) {
            boolean isFirst = true;
            string = string + "\n\nvoid q" + index + " (char c){\n\t";
            int idx = 0;
            for (int stateIndex : table.get(index)) {
                if (stateIndex != -1) {
                    isFirst = false;
                    string = string +
                            "if(c=='" + listOfCharacters.get(idx) + "'){\n" +
                            "\t\tstate=Q" + stateIndex + ";\n" +
                            "\t}else ";
                }
                idx++;
            }
            if (isFirst) {
                string = string +
                        "state=ERROR_STATE;\n" +
                        "}";
            } else {
                string = string +
                        "{\n" +
                        "\t\tstate=ERROR_STATE;\n" +
                        "\t}\n" +
                        "}";
            }
        }
        //add parser
        string = string +
                "\n\nint parser(char s[]){\n" +
                "\tstate=Q0;\n" +
                "\tend=-1;\n" +
                "\tfor(size_t i=0;i<strlen(s);i++){\n" +
                "\t\tchar c=s[i];\n" +
                "\t\tswitch(state){\n";
        for (int index = 0; index < listOfStates.size(); index++) {
            string = string +
                    "\t\t\tcase Q" + index + ":{\n";
            if (listOfStates.get(index).functionToString().charAt(listOfStates.get(index).functionToString().length() - 1) == '.') {
                string = string +
                        "\t\t\t\tend=i;\n";
            }
            string = string +
                    "\t\t\t\tq" + index + "(c);\n" +
                    "\t\t\t\tbreak;\n" +
                    "\t\t\t}\n";
        }
        string = string +
                "\t\t\tcase ERROR_STATE:{\n" +
                "\t\t\t\treturn end;\n" +
                "\t\t\t}\n" +
                "\t\t\tdefault:{\n" +
                "\t\t\t\tbreak;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\treturn end;\n" +
                "}";
        //add parseString
        string = string +
                "\n\nint parseString(char inputString[]){\n" +
                "\tsize_t length=strlen(inputString);\n" +
                "\tint countParsedString=0;\n" +
                "\tfor(size_t i=0;i<length;){\n" +
                "\t\tchar substring[length - i + 1];\n" +
                "\t\tstrcpy(substring, &inputString[i]);\n" +
                "\t\tint idx=parser(substring);\n" +
                "\t\tif(idx!=-1){\n" +
                "\t\t\tprintf(\"Next parsed string [%ld;%ld]: \",i+1,i+idx);\n" +
                "\t\t\tfor(int x=0;x<idx;x++){\n" +
                "\t\t\t\tprintf(\"%c\",substring[x]);\n" +
                "\t\t\t}\n" +
                "\t\t\tprintf(\"\\n\");\n" +
                "\t\t\tcountParsedString++;\n" +
                "\t\t\ti=i+idx;\n" +
                "\t\t}else{\n" +
                "\t\t\ti=i+1;\n" +
                "\t\t}\n" +
                "\t\tstate=Q0;\n" +
                "\t}\n" +
                "\treturn countParsedString;\n" +
                "}";
        //add main
        string = string +
                "\n\nint main(){\n" +
                "\tprintf(\"Enter your input string: \");\n" +
                "\tchar inputString[1000];\n" +
                "\tfgets(inputString,1000,stdin);\n" +
                "\tint countParsedString=parseString(inputString);\n" +
                "\tif(countParsedString!=0) {\n" +
                "\t\tprintf(\"I found %d parsed strings from your input string.\\n\", countParsedString);\n" +
                "\t} else{\n" +
                "\t\tprintf(\"I don't found parse string from your input string.\\n\");\n" +
                "\t}\n" +
                "\treturn 0;\n" +
                "}";

        FileHandler.writeToFile(fileName, string);
        return string;
    }

    public String tableToPythonFile(String fileName) {
        String string = "";
        for (int index = 0; index < listOfStates.size(); index++) {
            string = string +
                    "Q" + index + " = "+ index + "\n";
        }
        string = string + "ERROR_STATE = -1\n\n";
        for (int index = 0; index < listOfStates.size(); index++) {
            boolean isFirst = true;
            string = string + "def q" + index + " (c):\n\tglobal state;\n\t";
            int idx = 0;
            for (int stateIndex : table.get(index)) {
                if (stateIndex != -1) {
                    isFirst = false;
                    string = string +
                            "if c=='" + listOfCharacters.get(idx) + "':\n" +
                            "\t\tstate=Q" + stateIndex + ";\n" +
                            "\tel";
                }
                idx++;
            }
            if (isFirst) {
                string = string +
                        "state=ERROR_STATE;\n\n";
            } else {
                string = string +
                        "se:\n" +
                        "\t\tstate=ERROR_STATE;\n\n";
            }
        }
        //add parser
        string = string +
                "def parser(s):\n" +
                "\tglobal state;\n" +
                "\tstate=Q0;\n" +
                "\tend=-1;\n" +
                "\tfor i in range(len(s)):\n" +
                "\t\tc=s[i];\n\t\t";
        for (int index = 0; index < listOfStates.size(); index++) {
            string = string +
                    "if state==Q" + index + ":\n";
            if (listOfStates.get(index).functionToString().charAt(listOfStates.get(index).functionToString().length() - 1) == '.') {
                string = string +
                        "\t\t\tend=i;\n";
            }
            string = string +
                    "\t\t\tq" + index + "(c);\n" +
                    "\t\tel";
        }
        string = string +
                "se:\n" +
                "\t\t\treturn end;\n" +
                "\treturn end;\n\n";
        //add parseString
        string = string +
                "def parseString(inputString):\n" +
                "\tcountParsedString=0;\n" +
                "\ti=0;\n" +
                "\twhile i<len(inputString):\n" +
                "\t\tsubstring=inputString[i:];\n" +
                "\t\tidx=parser(substring);\n" +
                "\t\tif idx!=-1:\n" +
                "\t\t\tprint(\"Next parsed string [\",i+1,\";\",i+idx,\"]: \",substring[:idx]);\n" +
                "\t\t\tcountParsedString=countParsedString+1;\n" +
                "\t\t\ti=i+idx;\n" +
                "\t\telse:\n" +
                "\t\t\ti=i+1;\n" +
                "\t\tstate=Q0;\n" +
                "\treturn countParsedString;\n\n";
        //add main
        string = string +
                "print(\"Enter your input string: \");\n" +
                "inputString=input();\n" +
                "inputString=inputString+'\\n';" +
                "countParsedString=parseString(inputString);\n" +
                "if countParsedString!=0:\n" +
                "\tprint(\"I found \",countParsedString,\" parsed strings from your input string.\");\n" +
                "else:\n" +
                "\tprint(\"I don't found parse string from your input string.\");";

        FileHandler.writeToFile(fileName, string);
        return string;
    }

    public String tableToJavaFile(String directory) {
        clearDirectory(directory);
        String string = "";
        for (int index = 0; index < listOfStates.size(); index++) {
            boolean isFirst = true;
            string = "public class Q" + index + " implements State{\n" +
                    "\tpublic void function(char c, Parser parser){\n" +
                    "\t\t";
            int idx = 0;
            for (int stateIndex : table.get(index)) {
                if (stateIndex != -1) {
                    isFirst = false;
                    string = string +
                            "if (c=='" + listOfCharacters.get(idx) + "'){\n" +
                            "\t\t\tparser.setState(" + stateIndex + ");\n" +
                            "\t\t}else ";
                }
                idx++;
            }
            if (isFirst) {
                string = string +
                        "parser.setState(-1);\n\n" +
                        "\t\t}\n" +
                        "\t}";
            } else {
                string = string +
                        "{\n" +
                        "\t\t\tparser.setState(-1);\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "}";
            }
            FileHandler.writeToFile(directory + "Q" + index + ".java", string);

        }
        //add parser
        string = "public class Parser {\n" +
                "\tprivate int state;\n" +
                "\tprivate int end;\n\n";

        for (int index = 0; index < listOfStates.size(); index++) {
            string = string +
                    "private static final int Q" + index + " = " + index + ";\n";
        }
        string = string +
                "\tprivate static final int ERROR_STATE = -1;" +
                "\tpublic void setState(int state) {\n" +
                "\t\tthis.state = state;\n" +
                "\t}\n\n" +
                "\tpublic void setEnd(int end) {\n" +
                "\t\tthis.end = end;\n" +
                "\t}\n\n" +
                "\tpublic int parse(String s){\n" +
                "\t\tsetState(Q0);\n" +
                "\t\tsetEnd(-1);\n" +
                "\t\tState stateClass = null;\n\n" +
                "\t\tfor(int i=0;i<s.length();i++){\n" +
                "\t\t\tchar c=s.charAt(i);\n" +
                "\t\t\tswitch(state){\n" +
                "\t\t\t\t";
        for (int index = 0; index < listOfStates.size(); index++) {
            string = string +
                    "case Q" + index + ":{\n";
            if (listOfStates.get(index).functionToString().charAt(listOfStates.get(index).functionToString().length() - 1) == '.') {
                string = string +
                        "\t\t\t\t\tsetEnd(i);\n";
            }
            string = string +
                    "\t\t\t\t\tstateClass=new Q" + index + "();\n" +
                    "\t\t\t\t\tbreak;\n" +
                    "\t\t\t\t}\n                ";
        }
        string = string +
                "case ERROR_STATE:{\n" +
                "\t\t\t\t\treturn end;\n" +
                "\t\t\t\t}\n\t\t\t\t" +
                "default:{\n" +
                "\t\t\t\t\tbreak;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t\tif(stateClass!=null) {\n" +
                "\t\t\t\tstateClass.function(c, this);\n" +
                "\t\t\t}else {\n" +
                "\t\t\t\treturn end;\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t\treturn end;\n" +
                "\t}\n" +
                "}";

        FileHandler.writeToFile(directory + "Parser.java", string);

        //add parseString
        string = "public class StringParser {\n" +
                "\tpublic static int parseString(String inputString){\n" +
                "\t\tint length = inputString.length();\n" +
                "\t\tint countParsedString = 0;\n" +
                "\t\tParser parser=new Parser();\n\n" +
                "\t\tfor (int i = 0; i < length; ) {\n" +
                "\t\t\tString substring = inputString.substring(i);\n" +
                "\t\t\tint idx = parser.parse(substring);\n\n" +
                "\t\t\tif (idx != -1) {\n" +
                "\t\t\t\tSystem.out.print(\"Next parsed string [\" + (i + 1) + \";\" + (i + idx) + \"]: \");\n" +
                "\t\t\t\tfor (int x = 0; x < idx; x++) {\n" +
                "\t\t\t\t\tSystem.out.print(substring.charAt(x));\n" +
                "\t\t\t\t}\n" +
                "\t\t\t\tSystem.out.println();\n" +
                "\t\t\t\tcountParsedString++;\n" +
                "\t\t\t\ti = i + idx;\n" +
                "\t\t\t} else {\n" +
                "\t\t\t\ti = i + 1;\n" +
                "\t\t\t}\n" +
                "\t\t\tparser.setState(0);\n" +
                "\t\t}\n" +
                "\t\treturn countParsedString;\n" +
                "\t}\n" +
                "}";

        FileHandler.writeToFile(directory + "StringParser.java", string);

        //add main
        string = "import java.util.Scanner;\n\n" +
                "public class Main {\n" +
                "\tpublic static void main(String[] args) {\n" +
                "\t\tScanner scanner = new Scanner(System.in);\n" +
                "\t\tSystem.out.print(\"Enter your input string: \");\n" +
                "\t\tString inputString = scanner.nextLine();\n" +
                "\t\tinputString = inputString+\"\\n\";\n\n" +
                "\t\tint countParsedString = StringParser.parseString(inputString);\n\n" +
                "\t\tif (countParsedString != 0) {\n" +
                "\t\t\tSystem.out.println(\"I found \" + countParsedString + \" parsed strings from your input string.\");\n" +
                "\t\t} else {\n" +
                "\t\t\tSystem.out.println(\"I didn't find any parsed strings from your input string.\");\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}";

        FileHandler.writeToFile(directory + "Main.java", string);

        string = "public interface State {\n" +
                "\tvoid function(char c, Parser parser);\n" +
                "}";

        FileHandler.writeToFile(directory + "State.java", string);
        return string;
    }

    private static void clearDirectory(String path) {
        File file = new File(path);

        // Check if the file or directory exists
        if (file.exists()) {
            if (file.isDirectory()) {
                // If it's a directory, list its contents and delete recursively
                File[] files = file.listFiles();
                if (files != null) {
                    for (File subFile : files) {
                        clearDirectory(subFile.getAbsolutePath());
                    }
                }
            }

            // Delete the file or empty directory
            file.delete();
        }
    }

    @Override
    public String toString() {
        String string = "  ";
        for (Character character : this.listOfCharacters) {
            string = string + "| " + character + " ";
        }
        for (int index = 0; index < listOfStates.size(); index++) {
            string = string + "\nq" + index;
            for (int stateIndex : table.get(index)) {
                if (stateIndex != -1) {
                    string = string + "|q" + stateIndex + ' ';
                } else {
                    string = string + "| ⊥ ";
                }
            }
        }
        return string;
    }
}
