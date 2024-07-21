package nodes;

import graph.Graph;
import graph.Node;
import helpClasses.NodeWithInt;

import java.util.LinkedList;

public class RegexNode implements Cloneable {
    private boolean isRoot;
    private RegexNodeEnum type;
    private LinkedList<RegexNode> list;
    private final String value;

    public RegexNode(RegexNodeEnum type) {
        this.type = type;
        this.list = new LinkedList<>();
        this.value = null;
    }

    public RegexNode(RegexNodeEnum type, LinkedList<RegexNode> list) {
        this.type = type;
        this.list = list;
        this.value = null;
    }

    public RegexNode(RegexNodeEnum type, RegexNode node) {
        this.type = type;
        this.list = new LinkedList<>();
        this.addToList(node);
        this.value = null;
    }

    public RegexNode(RegexNodeEnum type, String value) {
        this.type = type;
        this.list = new LinkedList<>();
        this.value = value;
    }

    public RegexNodeEnum getType() {
        return type;
    }

    public void addToList(RegexNode node) {
        this.list.add(node);
    }

    public LinkedList<RegexNode> getList() {
        return list;
    }

    public String getValue() {
        return value;
    }

    public boolean isEndState(){
        return this.list.get(this.list.size() - 1).type == RegexNodeEnum.TOKEN && this.list.get(this.list.size() - 1).value.equals(".");
    }

    public boolean isSame(RegexNode firstNode, RegexNode secondNode) {
        if (firstNode == null || secondNode == null) {
            return false;
        }
        boolean isSame = true;
        if (firstNode.type == RegexNodeEnum.CHARACTER) {
            if (!firstNode.value.equals(secondNode.value) || secondNode.type != RegexNodeEnum.CHARACTER) {
                isSame = false;
            }
        } else {
            if (firstNode.type == secondNode.type && firstNode.list.size() == secondNode.list.size()) {
                for (int index = 0; index < firstNode.list.size(); index++) {
                    if (isSame != false && !isSame(firstNode.list.get(index), secondNode.list.get(index))) {
                        isSame = false;
                    }
                }
            } else {
                isSame = false;
            }
        }
        return isSame;
    }

    public boolean convert() {
        boolean wasChanged = false;
        boolean result;
        if (this.list != null) {
            for (RegexNode node : this.list) {
                result = node.convert();
                if (wasChanged == false) {
                    wasChanged = result;
                }
            }
        }
        for (int index = 0; index < this.list.size(); index++) {
            if (this.list.get(index).type == RegexNodeEnum.TRANSPARENCY) {
                boolean isSame = true;
                if (this.list.get(index).list.size() <= index) {
                    for (int position = 1; position <= this.list.get(index).list.size(); position++) {
                        if (!this.isSame(this.list.get(index).list.get(this.list.get(index).list.size() - position), this.list.get(index - position))) {
                            isSame = false;
                        }
                    }
                } else {
                    isSame = false;
                }
                if (isSame) {
                    if (index - this.list.get(index).list.size() >= 0 || index <= this.list.size()) {
                        RegexNode regexNode = new RegexNode(RegexNodeEnum.REPEAT, this.list.get(index).list);
                        this.list.set(index, regexNode);
                        if (this.list.get(index - 1).type == RegexNodeEnum.CLUSTER) {
                            this.list.subList(index - 1, index).clear();
                        } else {
                            this.list.subList(index - this.list.get(index).list.size(), index).clear();
                        }
                        if (wasChanged == false) {
                            wasChanged = true;
                        }
                    }
                }
            }
        }
        return wasChanged;
    }

    public void setRoot() {
        this.isRoot = true;
    }

    public boolean reverseConvert() {
        boolean wasChanged = false;
        boolean result;
        if (this.list != null) {
            for (RegexNode node : this.list) {
                result = node.reverseConvert();
                if (wasChanged == false) {
                    wasChanged = result;
                }
            }
        }
        for (int index = 0; index < this.list.size(); index++) {
            if (this.list.get(index).type == RegexNodeEnum.REPEAT) {
                if (index - this.list.get(index).list.size() >= 0 || index <= this.list.size()) {
                    RegexNode regexNode = new RegexNode(RegexNodeEnum.TRANSPARENCY, this.list.get(index).list);
                    this.list.set(index, regexNode);
                    if (this.type != RegexNodeEnum.ALTERNATIVE) {
                        this.list.addAll(index, regexNode.list);
                    } else {
                        regexNode = new RegexNode(RegexNodeEnum.EMPTY, this.list.get(index).list);
                        this.list.set(index, regexNode);
                    }
                    /*if (this.list.get(index - 1).type == RegexNodeEnum.CLUSTER) {
                        this.list.subList(index - 1, index).clear();
                    } else {
                        this.list.subList(index - this.list.get(index).list.size(), index).clear();
                    }*/
                    if (wasChanged == false) {
                        wasChanged = true;
                    }
                }
            }
        }
        return wasChanged;
    }

    public boolean minimize() {
        boolean wasChanged = false;
        boolean result;
        if (this.list != null) {
            for (RegexNode node : this.list) {
                result = node.minimize();
                if (wasChanged == false) {
                    wasChanged = result;
                }
            }
        }
        for (int index = 0; index < this.list.size(); index++) {
            while (this.list != null && this.list.size() >= 1 && this.list.get(index).list != null && this.list.get(index).list.size() >= 1) {
                if (this.list.get(index).type == RegexNodeEnum.CLUSTER && this.list.get(index).getList().getFirst().type == RegexNodeEnum.ALTERNATIVE && this.list.size() == 1 && this.list.get(index).getList().size() == 1) {
                    RegexNode regexNode = new RegexNode(RegexNodeEnum.ALTERNATIVE, this.list.get(index).list);
                    this.list.set(index, regexNode);
                    if (wasChanged == false) {
                        wasChanged = true;
                    }
                }
                if ((this.list.get(index).type == this.list.get(index).list.getFirst().getType()) && this.list.get(index).type != RegexNodeEnum.CHARACTER) {
                    if (this.list.get(index).list.size() == 1) {
                        /*LinkedList<RegexNode> l = this.list.get(index).list;
                        this.list.get(index).list = this.list.get(index).list.getFirst().getList();
                        if (l.size() > 1) {
                            for (RegexNode node : l) {
                                if (node != l.getFirst()) {
                                    this.list.add(node);
                                }
                            }
                        }*/
                        RegexNode regexNode = new RegexNode(this.list.get(index).type, this.list.get(index).list.getFirst().list);
                        this.list.set(index, regexNode);
                        if (wasChanged == false) {
                            wasChanged = true;
                        }
                    } else {
                        break;
                    }
                } else if (this.list.get(index).type == RegexNodeEnum.TRANSPARENCY && this.list.get(index).list.getFirst().getType() == RegexNodeEnum.VOLUME || this.list.get(index).type == RegexNodeEnum.VOLUME && this.list.get(index).list.getFirst().getType() == RegexNodeEnum.TRANSPARENCY) {
                    if (this.list.get(index).list.size() == 1) {
                        RegexNode regexNode = new RegexNode(RegexNodeEnum.TRANSPARENCY, this.list.get(index).list.getFirst().list);
                        this.list.set(index, regexNode);
                        if (wasChanged == false) {
                            wasChanged = true;
                        }
                    } else {
                        break;
                    }
                } else if (this.list != null && this.list.size() >= 1 && this.list.get(index).list != null && this.list.get(index).list.size() >= 1 && this.list.get(index).list.getFirst().list != null && this.list.get(index).list.getFirst().list.size() >= 1) {
                    if ((this.list.get(index).getType() == RegexNodeEnum.TRANSPARENCY || this.list.get(index).getType() == RegexNodeEnum.REPEAT || this.list.get(index).getType() == RegexNodeEnum.VOLUME) && this.list.get(index).list.getFirst().getType() == RegexNodeEnum.CLUSTER) {
                        if (this.list.get(index).list.size() == 1) {
                            /*LinkedList<RegexNode> l = this.list.get(index).list;
                            this.list.get(index).list = this.list.get(index).list.getFirst().list.getFirst().getList();
                            if (l.size() > 1) {
                                for (RegexNode node : l) {
                                    if (node != l.getFirst()) {
                                        this.list.add(node);
                                    }
                                }
                            }*/
                            RegexNode regexNode = new RegexNode(this.list.get(index).getType(), this.list.get(index).list.getFirst().list);
                            this.list.set(index, regexNode);
                        } else {
                            break;
                        }
                    } else if (this.list.get(index).getType() == RegexNodeEnum.CLUSTER && (this.list.get(index).list.getFirst().getType() == RegexNodeEnum.TRANSPARENCY || this.list.get(index).list.getFirst().getType() == RegexNodeEnum.REPEAT || this.list.get(index).list.getFirst().getType() == RegexNodeEnum.VOLUME)) {
                        if (this.list.get(index).list.size() == 1) {
                            RegexNode regexNode = new RegexNode(this.list.get(index).list.getFirst().getType(), this.list.get(index).list.getFirst().list);
                            this.list.set(index, regexNode);
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                } else if((this.list!=null && this.type!=RegexNodeEnum.ALTERNATIVE && this.list.get(index).type==RegexNodeEnum.CLUSTER) || (this.list!=null &&this.type==RegexNodeEnum.ALTERNATIVE && this.list.get(index).type==RegexNodeEnum.CLUSTER && this.list.get(index).list.size()==1)){
                    RegexNode node = this.list.get(index);
                    this.list.remove(index);
                    this.list.addAll(index, node.list);
                } else {
                    break;
                }
            }
        }
        return wasChanged;
    }

    public String functionToString() {
        if (this.list == null && this.type != RegexNodeEnum.CHARACTER) {
            return null;
        }
        String string = null;
        if (this.type == RegexNodeEnum.CHARACTER) {
            if (string != null) {
                string = string + this.value;
            } else {
                string = this.value;
            }
        } else if (this.type == RegexNodeEnum.TOKEN) {
            if (!this.value.equals(".")) {
                if (string != null) {
                    string = string + "." + this.value;
                } else {
                    string = "." + this.value;
                }
            } else {
                if (string != null) {
                    string = string + ".";
                } else {
                    string = ".";
                }
            }
        }
        if (this.list != null && this.list.size() >= 1) {
            if (this.type == RegexNodeEnum.CLUSTER) {
                if (string != null) {
                    string = string + '(';
                } else {
                    string = "(";
                }
                /*if(string!=null) {
                    string = string + this.list.getFirst().functionToString();
                }else {
                    string = this.list.getFirst().functionToString();
                }*/
                for (RegexNode node : this.list) {
                    if (string != null) {
                        string = string + node.functionToString();
                    } else {
                        string = node.functionToString();
                    }
                }
                if (string != null) {
                    string = string + ')';
                } else {
                    string = ")";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.ALTERNATIVE) {
                if (string != null) {
                    string = string + this.list.getFirst().functionToString();
                } else {
                    string = this.list.getFirst().functionToString();
                }
                if (string != null) {
                    string = string + '|';
                } else {
                    string = "|";
                }
                if (string != null) {
                    string = string + this.list.getLast().functionToString();
                } else {
                    string = this.list.getLast().functionToString();
                }
            } else if (this.type == RegexNodeEnum.TRANSPARENCY) {
                if (string != null) {
                    string = string + '{';
                } else {
                    string = "{";
                }
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().functionToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().functionToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.functionToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + '}';
                } else {
                    string = "}";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.REPEAT) {
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().functionToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().functionToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.functionToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + '{';
                } else {
                    string = "{";
                }
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().functionToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().functionToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.functionToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + '}';
                } else {
                    string = "}";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.VOLUME) {
                if (string != null) {
                    string = string + '[';
                } else {
                    string = "[";
                }
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().functionToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().functionToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().functionToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.functionToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + ']';
                } else {
                    string = "]";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.functionToString();
                        } else {
                            string = node.functionToString();
                        }
                    }
                }*/
            } else {
                for (RegexNode node : this.list) {
                    if (string != null) {
                        string = string + node.functionToString();
                    } else {
                        string = node.functionToString();
                    }
                }
            }
        }
        return string;
    }

    public String normalToString() {
        if (this.list == null && this.type != RegexNodeEnum.CHARACTER) {
            return null;
        }
        String string = null;
        if (this.type == RegexNodeEnum.CHARACTER) {
            if (string != null) {
                string = string + this.value;
            } else {
                string = this.value;
            }
        } else if (this.type == RegexNodeEnum.TOKEN) {
            if (!this.value.equals(".")) {
                if (string != null) {
                    string = string + this.value;
                } else {
                    string = this.value;
                }
            } else {
                string = "";
            }
        }
        if (this.list != null && this.list.size() >= 1) {
            if (this.type == RegexNodeEnum.CLUSTER) {
                if (string != null) {
                    string = string + '(';
                } else {
                    string = "(";
                }
                /*if(string!=null) {
                    string = string + this.list.getFirst().normalToString();
                }else {
                    string = this.list.getFirst().normalToString();
                }*/
                for (RegexNode node : this.list) {
                    if (string != null) {
                        string = string + node.normalToString();
                    } else {
                        string = node.normalToString();
                    }
                }
                if (string != null) {
                    string = string + ')';
                } else {
                    string = ")";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.normalToString();
                        } else {
                            string = node.normalToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.ALTERNATIVE) {
                if (string != null) {
                    string = string + this.list.getFirst().normalToString();
                } else {
                    string = this.list.getFirst().normalToString();
                }
                if (string != null) {
                    string = string + '|';
                } else {
                    string = "|";
                }
                if (string != null) {
                    string = string + this.list.getLast().normalToString();
                } else {
                    string = this.list.getLast().normalToString();
                }
            } else if (this.type == RegexNodeEnum.TRANSPARENCY) {
                if (this.list != null) {
                    if (this.list.size() > 1 || (this.list.size() == 1 && (this.list.get(0).type == RegexNodeEnum.TRANSPARENCY || this.list.get(0).type == RegexNodeEnum.VOLUME || this.list.get(0).type == RegexNodeEnum.REPEAT || this.list.get(0).type == RegexNodeEnum.ALTERNATIVE))) {
                        if (string != null) {
                            string = string + '(';
                        } else {
                            string = "(";
                        }
                    }
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.normalToString();
                        } else {
                            string = node.normalToString();
                        }
                    }
                    if (this.list.size() > 1 || (this.list.size() == 1 && (this.list.get(0).type == RegexNodeEnum.TRANSPARENCY || this.list.get(0).type == RegexNodeEnum.VOLUME || this.list.get(0).type == RegexNodeEnum.REPEAT || this.list.get(0).type == RegexNodeEnum.ALTERNATIVE))) {
                        if (string != null) {
                            string = string + ')';
                        } else {
                            string = ")";
                        }
                    }
                }
                if (string != null) {
                    string = string + '*';
                } else {
                    string = "*";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.normalToString();
                        } else {
                            string = node.normalToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.REPEAT) {
                if (this.list != null) {
                    if (this.list.size() > 1 || (this.list.size() == 1 && (this.list.get(0).type == RegexNodeEnum.TRANSPARENCY || this.list.get(0).type == RegexNodeEnum.VOLUME || this.list.get(0).type == RegexNodeEnum.REPEAT || this.list.get(0).type == RegexNodeEnum.ALTERNATIVE))) {
                        if (string != null) {
                            string = string + '(';
                        } else {
                            string = "(";
                        }
                    }
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.normalToString();
                        } else {
                            string = node.normalToString();
                        }
                    }
                    if (this.list.size() > 1 || (this.list.size() == 1 && (this.list.get(0).type == RegexNodeEnum.TRANSPARENCY || this.list.get(0).type == RegexNodeEnum.VOLUME || this.list.get(0).type == RegexNodeEnum.REPEAT || this.list.get(0).type == RegexNodeEnum.ALTERNATIVE))) {
                        if (string != null) {
                            string = string + ')';
                        } else {
                            string = ")";
                        }
                    }
                }
                if (string != null) {
                    string = string + '+';
                } else {
                    string = "+";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.normalToString();
                        } else {
                            string = node.normalToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.VOLUME) {
                if (this.list != null) {
                    if (this.list.size() > 1 || (this.list.size() == 1 && (this.list.get(0).type == RegexNodeEnum.TRANSPARENCY || this.list.get(0).type == RegexNodeEnum.VOLUME || this.list.get(0).type == RegexNodeEnum.REPEAT))) {
                        if (string != null) {
                            string = string + '(';
                        } else {
                            string = "(";
                        }
                    }
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.normalToString();
                        } else {
                            string = node.normalToString();
                        }
                    }
                    if (this.list.size() > 1 || (this.list.size() == 1 && (this.list.get(0).type == RegexNodeEnum.TRANSPARENCY || this.list.get(0).type == RegexNodeEnum.VOLUME || this.list.get(0).type == RegexNodeEnum.REPEAT))) {
                        if (string != null) {
                            string = string + ')';
                        } else {
                            string = ")";
                        }
                    }
                }
                if (string != null) {
                    string = string + '?';
                } else {
                    string = "?";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.normalToString();
                        } else {
                            string = node.normalToString();
                        }
                    }
                }*/
            } else {
                for (RegexNode node : this.list) {
                    if (string != null) {
                        string = string + node.normalToString();
                    } else {
                        string = node.normalToString();
                    }
                }
            }
        }
        return string;
    }

    public String modifiedToString() {
        if (this.list == null && this.type != RegexNodeEnum.CHARACTER) {
            return null;
        }
        String string = null;
        if (this.type == RegexNodeEnum.CHARACTER) {
            if (string != null) {
                string = string + this.value;
            } else {
                string = this.value;
            }
        } else if (this.type == RegexNodeEnum.TOKEN) {
            if (!this.value.equals(".")) {
                if (string != null) {
                    string = string + "." + this.value;
                } else {
                    string = "." + this.value;
                }
            } else {
                if (string != null) {
                    string = string + ".";
                } else {
                    string = ".";
                }
            }
        }
        if (this.list != null && this.list.size() >= 1) {
            if (this.type == RegexNodeEnum.CLUSTER) {
                if (string != null) {
                    string = string + '(';
                } else {
                    string = "(";
                }
                /*if(string!=null) {
                    string = string + this.list.getFirst().modifiedToString();
                }else {
                    string = this.list.getFirst().modifiedToString();
                }*/
                for (RegexNode node : this.list) {
                    if (string != null) {
                        string = string + node.modifiedToString();
                    } else {
                        string = node.modifiedToString();
                    }
                }
                if (string != null) {
                    string = string + ')';
                } else {
                    string = ")";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.ALTERNATIVE) {
                if (string != null) {
                    string = string + this.list.getFirst().modifiedToString();
                } else {
                    string = this.list.getFirst().modifiedToString();
                }
                if (string != null) {
                    string = string + '|';
                } else {
                    string = "|";
                }
                if (string != null) {
                    string = string + this.list.getLast().modifiedToString();
                } else {
                    string = this.list.getLast().modifiedToString();
                }
            } else if (this.type == RegexNodeEnum.TRANSPARENCY) {
                if (string != null) {
                    string = string + '{';
                } else {
                    string = "{";
                }
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().modifiedToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().modifiedToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.modifiedToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + '}';
                } else {
                    string = "}";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.REPEAT) {
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().modifiedToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().modifiedToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.modifiedToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + '{';
                } else {
                    string = "{";
                }
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().modifiedToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().modifiedToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.modifiedToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + '}';
                } else {
                    string = "}";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }*/
            } else if (this.type == RegexNodeEnum.VOLUME) {
                if (string != null) {
                    string = string + '[';
                } else {
                    string = "[";
                }
                /*if(this.list.getFirst().type!=RegexNodeEnum.CLUSTER) {
                    if (string != null) {
                        string = string + this.list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().modifiedToString();
                    }
                }else {
                    if (string != null) {
                        string = string + this.list.getFirst().list.getFirst().modifiedToString();
                    } else {
                        string = this.list.getFirst().list.getFirst().modifiedToString();
                    }
                }*/
                if (this.list.getFirst().type == RegexNodeEnum.CLUSTER) {
                    for (RegexNode node : this.list) {
                        String modifiedResult = node.modifiedToString();
                        if (string != null) {
                            string = string + modifiedResult.substring(1, modifiedResult.length() - 1);
                        } else {
                            string = modifiedResult.substring(1, modifiedResult.length() - 1);
                        }
                    }
                } else {
                    for (RegexNode node : this.list) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }
                if (string != null) {
                    string = string + ']';
                } else {
                    string = "]";
                }
                /*for (RegexNode node : this.list) {
                    if(node!=this.list.getFirst()) {
                        if (string != null) {
                            string = string + node.modifiedToString();
                        } else {
                            string = node.modifiedToString();
                        }
                    }
                }*/
            } else {
                for (RegexNode node : this.list) {
                    if (string != null) {
                        string = string + node.modifiedToString();
                    } else {
                        string = node.modifiedToString();
                    }
                }
            }
        }
        return string;
    }

    private int countOfLevels() {
        int levels = 1;
        for (RegexNode regexNode : this.list) {
            if (regexNode.type != RegexNodeEnum.CHARACTER) {
                int actualLevels = regexNode.countOfLevels();
                if (actualLevels >= levels) {
                    if (actualLevels == 1) {
                        levels = 2;
                    } else {
                        levels = actualLevels + 1;
                    }
                }
            }
        }
        return levels;
    }

    public NodeWithInt createGraph(Graph graph, NodeWithInt previousNode, int coordinateY, boolean revert) {
        int coordinateX = previousNode.getCoordinateX();
        if (graph == null || previousNode == null) {
            return null;
        }
        if (this.getType() == RegexNodeEnum.CHARACTER) {
            Node actualNode = graph.addNode(this.getValue(), coordinateX, coordinateY);
            coordinateX = coordinateX + 50;
            if (revert) {
                graph.addEdge(actualNode, previousNode.getNode());
            } else {
                graph.addEdge(previousNode.getNode(), actualNode);
            }
            previousNode = new NodeWithInt(actualNode, coordinateX);
        } else if (this.getType() == RegexNodeEnum.CLUSTER) {
            if (revert) {
                for (int i = this.list.size() - 1; i >= 0; i--) {
                    RegexNode listNode = this.list.get(i);
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY, revert);
                }
            } else {
                for (RegexNode listNode : this.list) {
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY, revert);
                }
            }
        } else if (this.getType() == RegexNodeEnum.ALTERNATIVE) {
            Node alternativeStartNode = graph.addNode("alternativeStart", coordinateX, coordinateY, "empty");
            Node alternativeNewLineStartNode = graph.addNode("alternativeNewLineStart", coordinateX, coordinateY, "empty");


            Node alternativeEndNode = graph.addNode("alternativeEnd", coordinateX, coordinateY, "empty");
            Node alternativeNewLineEndNode = graph.addNode("alternativeNewLineEnd", coordinateX, coordinateY, "empty");

            if (!revert) {
                graph.addEdge(alternativeStartNode, alternativeNewLineStartNode);
                graph.addEdge(alternativeNewLineEndNode, alternativeEndNode);
            } else {
                graph.addEdge(alternativeNewLineStartNode, alternativeStartNode);
                graph.addEdge(alternativeEndNode, alternativeNewLineEndNode);
            }

            if (this.list.size() > 1) {
                coordinateX = coordinateX + 50;
            }
            if (revert) {
                graph.addEdge(alternativeStartNode, previousNode.getNode());
            } else {
                graph.addEdge(previousNode.getNode(), alternativeStartNode);
            }

            previousNode = new NodeWithInt(alternativeStartNode, coordinateX);

            NodeWithInt alternativeNode1 = this.list.get(0).createGraph(graph, previousNode, coordinateY, revert);
            if (this.list.get(1).getType() != RegexNodeEnum.CHARACTER && this.list.get(1).list.get(0).getType() == RegexNodeEnum.ALTERNATIVE) {
                previousNode.setCoordinateX(coordinateX - 50);
            }
            int levels = this.list.get(0).countOfLevels();
            if (this.list.get(0).type != RegexNodeEnum.EMPTY) {
                levels++;
            }

            alternativeNewLineStartNode.setCoordinates(alternativeNewLineStartNode.getCoordinateX(),coordinateY + levels * 50);

            previousNode = new NodeWithInt(alternativeNewLineStartNode, coordinateX);

            /*if(this.list.get(1).getType()!=RegexNodeEnum.CHARACTER && this.list.get(1).getType()!=RegexNodeEnum.EMPTY){
                previousNode.setCoordinateX(previousNode.getCoordinateX()-50);
            }*/

            NodeWithInt alternativeNode2 = this.list.get(1).createGraph(graph, previousNode, coordinateY + levels * 50, revert);

            /*if(this.list.get(1).getType()!=RegexNodeEnum.CHARACTER && this.list.get(1).getType()!=RegexNodeEnum.EMPTY){
                previousNode.setCoordinateX(previousNode.getCoordinateX()+50);
            }*/

            if (alternativeNode1.getCoordinateX() > alternativeNode2.getCoordinateX()) {
                coordinateX = alternativeNode1.getCoordinateX();
            } else {
                coordinateX = alternativeNode2.getCoordinateX();
            }

            if (this.list.get(1).getType() != RegexNodeEnum.CHARACTER && this.list.get(1).list.get(0).getType() == RegexNodeEnum.ALTERNATIVE) {
                coordinateX = coordinateX - 50;
            }


            /*if(this.list.get(1).getType()!=RegexNodeEnum.CHARACTER && this.list.get(1).getType()!=RegexNodeEnum.EMPTY){
                coordinateX = coordinateX - 50;
            }*/

            alternativeEndNode.setCoordinates(coordinateX, coordinateY);
            alternativeNewLineEndNode.setCoordinates(coordinateX, coordinateY + levels * 50);
            coordinateX = coordinateX + 50;

            previousNode = new NodeWithInt(alternativeEndNode, coordinateX);

            if (revert) {
                graph.addEdge(previousNode.getNode(), alternativeNode1.getNode());
                graph.addEdge(alternativeNewLineEndNode, alternativeNode2.getNode());
            } else {
                graph.addEdge(alternativeNode1.getNode(), previousNode.getNode());
                graph.addEdge(alternativeNode2.getNode(), alternativeNewLineEndNode);
            }
        } else if (this.getType() == RegexNodeEnum.TRANSPARENCY) {


            Node transparencyStartNode = graph.addNode("transparencyStart", coordinateX, coordinateY, "empty");
            Node transparencyNewLineStartNode = graph.addNode("transparencyNewLineStart", coordinateX, coordinateY+50, "empty");


            Node transparencyEndNode = graph.addNode("transparencyEnd", coordinateX, coordinateY, "empty");
            Node transparencyNewLineEndNode = graph.addNode("transparencyNewLineEnd", coordinateX, coordinateY+50, "empty");

            if (revert) {
                graph.addEdge(transparencyStartNode, transparencyNewLineStartNode);
                graph.addEdge(transparencyNewLineEndNode, transparencyEndNode);
            } else {
                graph.addEdge(transparencyNewLineStartNode, transparencyStartNode);
                graph.addEdge(transparencyEndNode, transparencyNewLineEndNode);
            }

            coordinateX = coordinateX + 25;
            if (revert) {
                graph.addEdge(transparencyStartNode, previousNode.getNode());
            } else {
                graph.addEdge(previousNode.getNode(), transparencyStartNode);
            }

            previousNode = new NodeWithInt(transparencyNewLineStartNode, coordinateX);
            if (revert) {
                for (RegexNode listNode : this.list) {
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, !revert);
                    coordinateX = previousNode.getCoordinateX();
                }
            } else {
                for (int i = this.list.size() - 1; i >= 0; i--) {
                    RegexNode listNode = this.list.get(i);
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, !revert);
                    coordinateX = previousNode.getCoordinateX();
                }
            }

            coordinateX = coordinateX - 25;
            transparencyEndNode.setCoordinates(coordinateX, coordinateY);
            transparencyNewLineEndNode.setCoordinates(coordinateX, coordinateY+50);
            coordinateX = coordinateX + 50;
            if (revert) {
                graph.addEdge(previousNode.getNode(), transparencyNewLineEndNode);
            } else {
                graph.addEdge(transparencyNewLineEndNode, previousNode.getNode());
            }
            previousNode = new NodeWithInt(transparencyEndNode, coordinateX);

            if (revert) {
                graph.addEdge(transparencyEndNode, transparencyStartNode);
            } else {
                graph.addEdge(transparencyStartNode, transparencyEndNode);
            }
        } else if (this.getType() == RegexNodeEnum.REPEAT) {
            if (!revert) {
                for (RegexNode listNode : this.list) {
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY, revert);
                }
                coordinateX = previousNode.getCoordinateX();
            }

            Node repeatStartNode = graph.addNode("repeatStart", coordinateX, coordinateY, "empty");
            Node repeatNewLineStartNode = graph.addNode("repeatNewLineStart", coordinateX, coordinateY+50, "empty");


            Node repeatEndNode = graph.addNode("repeatEnd", coordinateX, coordinateY, "empty");
            Node repeatNewLineEndNode = graph.addNode("repeatNewLineEnd", coordinateX, coordinateY+50, "empty");

            if (revert) {
                graph.addEdge(repeatStartNode, repeatNewLineStartNode);
                graph.addEdge(repeatNewLineEndNode, repeatEndNode);
            } else {
                graph.addEdge(repeatNewLineStartNode, repeatStartNode);
                graph.addEdge(repeatEndNode, repeatNewLineEndNode);
            }

            coordinateX = coordinateX + 25;
            if (revert) {
                graph.addEdge(repeatStartNode, previousNode.getNode());
            } else {
                graph.addEdge(previousNode.getNode(), repeatStartNode);
            }

            previousNode = new NodeWithInt(repeatNewLineStartNode, coordinateX);
            if (revert) {
                for (RegexNode listNode : this.list) {
                    /*if (listNode.getType() == RegexNodeEnum.CHARACTER) {
                        Node actualNode = graph.addNode(listNode.getValue(), coordinateX, coordinateY + 50);
                        coordinateX = coordinateX + 50;
                        if (revert) {
                            graph.addEdge(previousNode.getNode(), actualNode);
                        } else {
                            graph.addEdge(actualNode, previousNode.getNode());
                        }
                        previousNode = new NodeWithInt(actualNode, coordinateX);
                    } else {
                        previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, !revert);
                        coordinateX = previousNode.getCoordinateX();
                    }*/
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, !revert);
                    coordinateX = previousNode.getCoordinateX();
                }
            } else {
                for (int i = this.list.size() - 1; i >= 0; i--) {
                    RegexNode listNode = this.list.get(i);
                    /*if (listNode.getType() == RegexNodeEnum.CHARACTER) {
                        Node actualNode = graph.addNode(listNode.getValue(), coordinateX, coordinateY + 50);
                        coordinateX = coordinateX + 50;
                        if (revert) {
                            graph.addEdge(previousNode.getNode(), actualNode);
                        } else {
                            graph.addEdge(actualNode, previousNode.getNode());
                        }
                        previousNode = new NodeWithInt(actualNode, coordinateX);
                    } else {
                        previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, !revert);
                        coordinateX = previousNode.getCoordinateX();
                    }*/
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, !revert);
                    coordinateX = previousNode.getCoordinateX();
                }
            }

            coordinateX = coordinateX - 25;
            repeatEndNode.setCoordinates(coordinateX, coordinateY);
            repeatNewLineEndNode.setCoordinates(coordinateX, coordinateY+50);
            coordinateX = coordinateX + 50;
            if (revert) {
                graph.addEdge(previousNode.getNode(), repeatNewLineEndNode);
            } else {
                graph.addEdge(repeatNewLineEndNode, previousNode.getNode());
            }
            previousNode = new NodeWithInt(repeatEndNode, coordinateX);

            if (revert) {
                graph.addEdge(repeatEndNode, repeatStartNode);
            } else {
                graph.addEdge(repeatStartNode, repeatEndNode);
            }

            if (revert) {
                for (int i = this.list.size() - 1; i >= 0; i--) {
                    RegexNode listNode = this.list.get(i);
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY, revert);
                }
            }
        } else if (this.getType() == RegexNodeEnum.VOLUME) {

            Node volumeStartNode = graph.addNode("volumeStart", coordinateX, coordinateY, "empty");
            Node volumeNewLineStartNode = graph.addNode("volumeNewLineStart", coordinateX, coordinateY+50, "empty");


            Node volumeEndNode = graph.addNode("volumeEnd", coordinateX, coordinateY, "empty");
            Node volumeNewLineEndNode = graph.addNode("volumeNewLineEnd", coordinateX, coordinateY+50, "empty");

            if (revert) {
                graph.addEdge(volumeNewLineStartNode, volumeStartNode);
                graph.addEdge(volumeEndNode, volumeNewLineEndNode);
            } else {
                graph.addEdge(volumeStartNode, volumeNewLineStartNode);
                graph.addEdge(volumeNewLineEndNode, volumeEndNode);
            }

            coordinateX = coordinateX + 25;
            if (revert) {
                graph.addEdge(volumeStartNode, previousNode.getNode());
            } else {
                graph.addEdge(previousNode.getNode(), volumeStartNode);
            }

            previousNode = new NodeWithInt(volumeNewLineStartNode, coordinateX);

            if (revert) {
                for (int i = this.list.size() - 1; i >= 0; i--) {
                    RegexNode listNode = this.list.get(i);
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, revert);
                }
            } else {
                for (RegexNode listNode : this.list) {
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY + 50, revert);
                }
            }
            coordinateX = previousNode.getCoordinateX();

            coordinateX = coordinateX - 25;
            volumeEndNode.setCoordinates(coordinateX, coordinateY);
            volumeNewLineEndNode.setCoordinates(coordinateX, coordinateY+50);
            coordinateX = coordinateX + 50;

            if (revert) {
                graph.addEdge(volumeEndNode, volumeStartNode);
                graph.addEdge(volumeNewLineEndNode, previousNode.getNode());
            } else {
                graph.addEdge(volumeStartNode, volumeEndNode);
                graph.addEdge(previousNode.getNode(), volumeNewLineEndNode);
            }

            previousNode = new NodeWithInt(volumeEndNode, coordinateX);
        } else if (this.type == RegexNodeEnum.EMPTY) {
            if (revert) {
                for (int i = this.list.size() - 1; i >= 0; i--) {
                    RegexNode listNode = this.list.get(i);
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY, revert);
                }
            } else {
                for (RegexNode listNode : this.list) {
                    previousNode = listNode.createGraph(graph, previousNode, coordinateY, revert);
                }
            }
        }
        return previousNode;
    }

    public boolean addTokens() {
        int position = 0;
        boolean thisOverflow = false;
        boolean childrenOverflow = false;
        do {
            if (this.type == RegexNodeEnum.EMPTY) {
                childrenOverflow = this.list.get(position).addTokens();
            } else if (this.type == RegexNodeEnum.CHARACTER) {
                childrenOverflow = false;
                this.type = RegexNodeEnum.TOKEN;
            } else if (this.type == RegexNodeEnum.CLUSTER) {
                childrenOverflow = this.list.get(position).addTokens();
            } else if (this.type == RegexNodeEnum.ALTERNATIVE) {
                childrenOverflow = this.list.get(position).addTokens();
                if (childrenOverflow) {
                    this.list.get(position + 1).addTokens();
                } else {
                    childrenOverflow = this.list.get(position + 1).addTokens();
                }
                position++;
            } else if (this.type == RegexNodeEnum.TRANSPARENCY) {
                childrenOverflow = this.list.get(position).addTokens();
                thisOverflow = true;
            } else if (this.type == RegexNodeEnum.REPEAT) {
                childrenOverflow = this.list.get(position).addTokens();
            } else if (this.type == RegexNodeEnum.VOLUME) {
                childrenOverflow = this.list.get(position).addTokens();
                thisOverflow = true;
            }
            position++;
            if (position >= this.list.size()) {
                if (!thisOverflow) {
                    thisOverflow = childrenOverflow;
                }
                childrenOverflow = false;
            }
        } while (childrenOverflow);
        return thisOverflow;
    }

    public void addTokenToTheEnd() {
        this.list.add(new RegexNode(RegexNodeEnum.TOKEN, "."));
    }

    public boolean getFunction(Character character, RegexNode newState) {
        boolean booleanBuffer = isRoot;
        isRoot = false;
        boolean thisOverflow = false;
        boolean childrenOverflow = false;
        if (this.type == RegexNodeEnum.EMPTY) {
            boolean bufferChildrenOverflow = false;
            for (int index = 0; index < this.list.size(); index++) {
                if (index < newState.list.size()) {
                    childrenOverflow = this.list.get(index).getFunction(character, newState.list.get(index));
                    int offset = 1;
                    while (childrenOverflow) {
                        bufferChildrenOverflow = false;
                        if (index + offset < newState.list.size()) {
                            childrenOverflow = newState.list.get(index + offset).addTokens();
                        } else {
                            bufferChildrenOverflow = true;
                            childrenOverflow = false;
                            if (booleanBuffer) {
                                bufferChildrenOverflow = false;
                                newState.addTokenToTheEnd();
                                isRoot = booleanBuffer;
                            }
                        }
                        offset++;
                    }
                    if (bufferChildrenOverflow) {
                        childrenOverflow = bufferChildrenOverflow;
                    }
                }
            }
            thisOverflow = childrenOverflow;
        } else if (this.type == RegexNodeEnum.CHARACTER) {
            thisOverflow = false;
        } else if (this.type == RegexNodeEnum.CLUSTER) {
            for (int index = 0; index < this.list.size(); index++) {
                if (index < newState.list.size()) {
                    childrenOverflow = this.list.get(index).getFunction(character, newState.list.get(index));
                    int offset = 1;
                    while (childrenOverflow) {
                        if (index + offset < newState.list.size()) {
                            childrenOverflow = newState.list.get(index + offset).addTokens();
                        } else {
                            /*childrenOverflow =false;
                            newState.addTokenToTheEnd();*/
                            break;
                        }
                        offset++;
                    }
                }
            }
            thisOverflow = childrenOverflow;
        } else if (this.type == RegexNodeEnum.ALTERNATIVE) {
            childrenOverflow = this.list.get(0).getFunction(character, newState.list.get(0));
            thisOverflow = this.list.get(1).getFunction(character, newState.list.get(1));
            if (childrenOverflow) {
                thisOverflow = childrenOverflow;
            }
        } else if (this.type == RegexNodeEnum.TRANSPARENCY) {
            for (int index = 0; index < this.list.size(); index++) {
                if (index < newState.list.size()) {
                    childrenOverflow = this.list.get(index).getFunction(character, newState.list.get(index));
                    int offset = 1;
                    while (childrenOverflow) {
                        if (index + offset < newState.list.size()) {
                            childrenOverflow = newState.list.get(index + offset).addTokens();
                        } else {
                            /*childrenOverflow =false;
                            newState.addTokenToTheEnd();*/
                            break;
                        }
                        offset++;
                    }
                }
            }
            if (childrenOverflow) {
                newState.addTokens();
            }
            thisOverflow = childrenOverflow;
        } else if (this.type == RegexNodeEnum.REPEAT) {
        } else if (this.type == RegexNodeEnum.VOLUME) {
            childrenOverflow = false;
            boolean bufferChildrenOverflow = false;
            for (int index = 0; index < this.list.size(); index++) {
                if (index < newState.list.size()) {
                    childrenOverflow = this.list.get(index).getFunction(character, newState.list.get(index));
                    int offset = 1;
                    while (childrenOverflow) {
                        if (index + offset < newState.list.size()) {
                            childrenOverflow = newState.list.get(index + offset).addTokens();
                            bufferChildrenOverflow = childrenOverflow;
                        } else {
                            /*childrenOverflow =false;
                            newState.addTokenToTheEnd();*/
                            break;
                        }
                        offset++;
                    }
                    if (bufferChildrenOverflow) {
                        childrenOverflow = bufferChildrenOverflow;
                    }
                }
            }

            thisOverflow = childrenOverflow;
        } else if (this.type == RegexNodeEnum.TOKEN) {
            this.type = RegexNodeEnum.CHARACTER;
            if (character == this.value.charAt(0)) {
                thisOverflow = true;
            } else {
                thisOverflow = false;
            }
        }
        return thisOverflow;
    }

    @Override
    public String toString() {
        if (this.list == null && this.type != RegexNodeEnum.CHARACTER) {
            return null;
        }
        String string = null;
        if (this.type == RegexNodeEnum.CHARACTER || this.type == RegexNodeEnum.TOKEN) {
            if (string != null) {
                string = string + ' ' + this.type.toString() + ' ' + this.value;
            } else {
                string = this.type.toString() + ' ' + this.value;
            }
        } else {
            if (string != null) {
                string = string + ' ' + this.type.toString();
            } else {
                string = this.type.toString();
            }
        }
        if (this.list != null && this.list.size() >= 1) {
            if (string != null) {
                string = string + '[';
            } else {
                string = "[";
            }
            for (RegexNode node : this.list) {
                if (string != null) {
                    string = string + ' ' + node.toString();
                } else {
                    string = node.toString();
                }
            }
            if (string != null) {
                string = string + ']';
            } else {
                string = "]";
            }
        }
        return string;
    }

    @Override
    public RegexNode clone() {
        try {
            // Create a shallow copy of the current object
            RegexNode clonedNode = (RegexNode) super.clone();

            // Deep copy the list
            if (this.list != null) {
                LinkedList<RegexNode> clonedList = new LinkedList<>();
                for (RegexNode node : this.list) {
                    clonedList.add(node.clone());
                }
                clonedNode.list = clonedList;
            }

            return clonedNode;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

