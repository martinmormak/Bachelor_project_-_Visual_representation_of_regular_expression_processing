package nodes;

import java.util.HashSet;
import java.util.Random;
import java.util.LinkedList;
import java.util.Set;

public class RegexNodeGenerator {
    private static final Random random = new Random();
    private static final char[] CHARACTER_SET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    char[] characterSubset;
    private int lengthOfGeneratedRegex;
    private int totalLength;

    private boolean acceptCluster=false;
    private boolean acceptAlternative=false;
    private boolean acceptVolume=false;
    private boolean acceptTransitive=false;
    private boolean acceptRepeat=false;

    private boolean wasBeforeCharacter;

    public char[] generateSelectedCharset(int numCharacters){
        char[] characters=new char[numCharacters];
        Set<Character> selectedCharacters = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < numCharacters; i++) {
            char randomChar;
            do {
                int randomIndex = random.nextInt(CHARACTER_SET.length);
                randomChar = CHARACTER_SET[randomIndex];
            } while (selectedCharacters.contains(randomChar));
            characters[i] = randomChar;
            selectedCharacters.add(randomChar);
        }
        return characters;
    }

    public RegexNode generateRandomRegex(int numCharacters, int length, boolean acceptCluster, boolean acceptAlternative, boolean acceptVolume, boolean acceptTransitive, boolean acceptRepeat) {
        if(numCharacters<=0||length<=0||numCharacters>CHARACTER_SET.length){
            return null;
        }
        this.lengthOfGeneratedRegex=0;
        this.totalLength=length;
        this.characterSubset=generateSelectedCharset(numCharacters);
        this.acceptCluster=acceptCluster;
        this.acceptAlternative=acceptAlternative;
        this.acceptVolume=acceptVolume;
        this.acceptTransitive=acceptTransitive;
        this.acceptRepeat=acceptRepeat;
        this.wasBeforeCharacter=true;

        return generateRandomPartOfRegex(0);
    }

    public RegexNode generateRandomPartOfRegex(int deep){
        LinkedList<RegexNode> nodeList = new LinkedList<>();
        if(lengthOfGeneratedRegex+1>=this.totalLength){
            return generateRandomCharacterNode(1);
        }
        for (int i = lengthOfGeneratedRegex; i < this.totalLength; i++) {
            RegexNode regexNode = generateRandomNode(deep);
            if(regexNode!=null){
                nodeList.add(regexNode);
            }
            i=lengthOfGeneratedRegex;
        }
        return new RegexNode(RegexNodeEnum.EMPTY, nodeList);
    }

    private RegexNode generateRandomCharacterNode(int numCharacters) {
        StringBuilder stringBuilder = new StringBuilder();
        if(this.characterSubset==null){
            return null;
        }
        for (int i = 0; i < numCharacters; i++) {
            stringBuilder.append(characterSubset[random.nextInt(characterSubset.length)]);
        }
        lengthOfGeneratedRegex+=numCharacters;
        return new RegexNode(RegexNodeEnum.CHARACTER, stringBuilder.toString());
    }

    private RegexNode generateRandomNode(int deep) {
        RegexNode regexNode=null;
        RegexNodeEnum[] enums = RegexNodeEnum.values();
        RegexNodeEnum randomEnum;
        do {
            randomEnum = enums[random.nextInt(enums.length)];
        } while ((deep>=2 && randomEnum !=RegexNodeEnum.CHARACTER) ||
                (!wasBeforeCharacter && randomEnum !=RegexNodeEnum.CHARACTER) ||
                (!acceptCluster && randomEnum == RegexNodeEnum.CLUSTER) ||
                (!acceptAlternative && randomEnum == RegexNodeEnum.ALTERNATIVE) ||
                (!acceptVolume && randomEnum == RegexNodeEnum.VOLUME) ||
                (!acceptTransitive && randomEnum == RegexNodeEnum.TRANSPARENCY) ||
                (!acceptRepeat && randomEnum == RegexNodeEnum.REPEAT) ||
                (randomEnum == RegexNodeEnum.ALTERNATIVE && this.totalLength-2<this.lengthOfGeneratedRegex));

        switch (randomEnum) {
            case EMPTY:
            case CHARACTER:
                this.wasBeforeCharacter=true;
                return generateRandomCharacterNode(1);
            case CLUSTER:
                this.wasBeforeCharacter=false;
                regexNode=generateRandomPartOfRegex(deep+1);
                if(regexNode==null){
                    return null;
                }
                return new RegexNode(RegexNodeEnum.CLUSTER,regexNode.getList());
            case ALTERNATIVE:
                this.wasBeforeCharacter=false;
                int average = (this.totalLength+this.lengthOfGeneratedRegex)/2;
                this.lengthOfGeneratedRegex=this.lengthOfGeneratedRegex+average;
                RegexNode part1 = generateRandomPartOfRegex(deep+1);
                this.lengthOfGeneratedRegex=this.lengthOfGeneratedRegex-average;
                RegexNode part2 = generateRandomPartOfRegex(deep+1);
                if (part1 == null || part2 == null) {
                    return null;
                }
                regexNode = new RegexNode(RegexNodeEnum.ALTERNATIVE);
                regexNode.addToList(part1);
                regexNode.addToList(part2);
                return regexNode;
            case TRANSPARENCY:
                this.wasBeforeCharacter=false;
                regexNode=generateRandomPartOfRegex(deep+1);
                if(regexNode==null){
                    return null;
                }
                return new RegexNode(RegexNodeEnum.TRANSPARENCY,regexNode.getList());
            case REPEAT:
                this.wasBeforeCharacter=false;
                regexNode=generateRandomPartOfRegex(deep+1);
                if(regexNode==null){
                    return null;
                }
                return new RegexNode(RegexNodeEnum.REPEAT,regexNode.getList());
            case VOLUME:
                this.wasBeforeCharacter=false;
                regexNode=generateRandomPartOfRegex(deep+1);
                if(regexNode==null){
                    return null;
                }
                return new RegexNode(RegexNodeEnum.VOLUME,regexNode.getList());
            case TOKEN:
            default:
                return null;
        }
    }
}
