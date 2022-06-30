package com.thompson;

import java.util.ArrayList;
import java.util.Collections;

public final class Thompson {

    public ArrayList<DState> dstates = new ArrayList<>();
    public ArrayList<DState> terminalDStates = new ArrayList<>();
    public ArrayList<Node> nodes = new ArrayList<>();
    public Node firstNode;
    public Node finalNode;

    private boolean errors = false;
    private int errorcode = 0;

    private ArrayList<Character> lenguage = new ArrayList<>();

    public boolean hasErrors() {
        if(errors) {
            return true;
        } else return false;
    }

    public String getErrorMessage() {
        String erromessage;
        switch(errorcode) {
            case 1:
                erromessage = "Te falta cerrar uno o más paréntesis.";
                break;
            case 2:
                erromessage = "Hay una pleca | que no está dentro de ningún paréntesis. Ejm: (0|1) Pero no: 0|1";
                break;
            case 3:
                erromessage = "Hay un asterisco adicional. Ejm: (1)*, 1*. Pero no: 2**, (1)**";
                break;
            case 4:
                erromessage = "Hay un paréntesis adicional.";
                break;
            case 5:
                erromessage = "Hay una pleca | que no tiene opciones. Ejm: (0|1). Pero no: (0|)";
                break;
            case 6:
                erromessage = "Hay una paréntesis que no tiene opciones. Ejm: (0|1). Pero no: ()";
                break;
            case 7:
                erromessage = "No hay expresión regular";
                break;
            default:
                erromessage = "Error desconocido, ingresa la expresión nuevamente...";
                break;
        }
        return erromessage;
    }

    public String DMProcess = "";
    public Thompson(String regularExpresion) {
        // get the nodes
        if(regularExpresion.isBlank() || regularExpresion.isEmpty()) {
            errorcode = 7;
            errors = true;
            return;
        }
        nodes = analysis(regularExpresion);
        if(errors) return;

        // get final and first node
        finalNode = getLast(nodes);
        firstNode = getFirst(nodes);
        
        // get states
        Collections.sort(lenguage);
        DState s0 = new DState(firstNode.nodesWith(new ArrayList<Node>(), ' '), dstates, finalNode);
        DMProcess += "1)\n";
        DMProcess += "s0 = cl({0}) = {" + DState.nodesToString(s0.Nodes) + "}\n";
        DMProcess += "2)\n";
        DM(s0);
        DMProcess += "3)\n";
        DMProcess += "DT = {" + DState.DStatesToString(terminalDStates) + "}\n";
        DMProcess += "4)\n";
        DMProcess += "A = (Sigma, DS, DT, s0, DM)\n";
        DMProcess += "Sigma = {" + lenguageString() + "}\n";
        DMProcess += "DS = {" + DState.DStatesToString(dstates) + "}\n";
        DMProcess += "DT = {" + DState.DStatesToString(terminalDStates) + "}\n";
        DMProcess += "El estado inicial es s0\n";
        DMProcess += "DM Está definido por\n";
        // Tabla
        /*DMProcess += "A\tDM\n";
        DMProcess += lenguageString2() + "\n";
        for(DState dstate: dstates) {
            String str = "s" + dstate.ID + "\t";
            for(char letter: lenguage) {
                str += "s" + dstate.F.get(letter).ID + "\t";
            }
            DMProcess += str + "\n";
        }*/
    }

    private ArrayList<Node> analysis(String chain) {
        ArrayList<Node> structure = new ArrayList<>();
        int i = 0;
        while(i < chain.length()) {
            // stop when there are errors
            if(errors) {
                return new ArrayList<>();
            }

            char c = chain.charAt(i);
            Node n0;
            if(structure.size() == 0) {
                n0 = new Node(0, 0, 0);
                structure.add(n0);
            } else n0 = getLast(structure);
            int floor = n0.getY();
            int division = n0.getX();

            switch(c) {
                case '(':
                    int end = getParentesisEnd(chain.substring(i + 1, chain.length())) + i + 1;
                    // if parentesis has no end
                    if(end == -1 + i + 1) {
                        errors = true;
                        errorcode = 1;
                        return new ArrayList<>();
                    }
                    String substring = chain.substring(i + 1, end);
                    ArrayList<Node> substructure = new ArrayList<>();

                    // | structures
                    if(substring.contains("|")) {
                        // gets subsubstructures
                        ArrayList<Integer> divisions = getLineDivisions(substring);
                        ArrayList<ArrayList<Node>> subsubstructures = new ArrayList<>();
                        divisions.add(substring.length());
                        int start = 0;
                        for(Integer div: divisions) {
                            String subsubstring = substring.substring(start, div);
                            // if there are no options for pleca
                            if(subsubstring.isEmpty() || subsubstring.isBlank()){
                                errors = true;
                                errorcode = 5;
                                return new ArrayList<>();
                            }
                            subsubstructures.add(analysis(subsubstring));
                            start = div+1; 
                        }

                        Node nf = new Node(0, division, floor);
                        // creates substructure based on subsubstructures
                        if(chain.length() > end+1 && chain.charAt(end+1) == '*') {
                            // (|)*
                            Node ni = new Node(0, division + 1, floor);
                            substructure.add(ni);
                            n0.setNext(ni, ' ', 0);
                            connectSubsubstructure(ni, nf, subsubstructures, substructure);
                        } else {
                            // (|)
                            connectSubsubstructure(n0, nf, subsubstructures, substructure);
                        }
                    } else {
                        // if there are no options for parentesis
                        if(substring.isEmpty() || substring.isBlank()) {
                            errors = true;
                            errorcode = 6;
                            return new ArrayList<>();
                        }
                        substructure = analysis(substring);
                        n0.setNext(getFirst(substructure), ' ', 0);
                    }
                    if(chain.length() > end+1 && chain.charAt(end+1) == '*') {
                        // ()*
                        Node substructureLastNode = getLast(substructure);
                        substructureLastNode.setNext(getFirst(substructure), ' ', 4);
                        Node nf = new Node(substructure.size(), substructureLastNode.getX() + 1 ,floor);
                        substructureLastNode.setNext(nf, ' ', 0);
                        substructure.add(nf);

                        // connection to structure
                        n0.setNext(nf, ' ', 3);
                        end += 1;
                    }
                    // conection to structure
                    moveIDStructure(substructure, structure.size());
                    structure.addAll(substructure);
                    i = end + 1;
                    break;
                case ')':
                    errors = true;
                    errorcode = 4;
                    break;
                case '*':
                    errors = true;
                    errorcode = 3;
                    break;
                case '|':
                    errors = true;
                    errorcode = 2;
                    break;
                case ' ':
                    i++;
                    break;
                default:
                    if(chain.length()>i+1 && chain.charAt(i+1) == '*') {
                        // 1*    
                        Node ni = new Node(n0.ID + 1, division + 1, floor);
                        Node n1 = new Node(ni.ID + 1, division + 2, floor);
                        Node n2 = new Node(n1.ID + 1, division + 3, floor);
                        n0.setNext(ni, ' ',0);
                        n0.setNext(n2, ' ',3);
                        ni.setNext(n1, c,0);
                        n1.setNext(ni, ' ',4);
                        n1.setNext(n2, ' ',0);
                        structure.add(ni);
                        structure.add(n1);
                        structure.add(n2);
                        
                        i += 2;
                    } else {
                        // 1
                        Node n1 = new Node(n0.ID + 1, division+1, floor);
                        n0.setNext(n1, c, 0);
                        structure.add(n1);
                        i++;
                    }
                    if(!lenguage.contains(c)) lenguage.add(c);
                    break;
            }
        }
        return structure;
    }

    private void connectSubsubstructure(Node ni, Node nf, ArrayList<ArrayList<Node>> subsubstructures, ArrayList<Node> substructure) {
        int iniDivision = ni.getX();
        for(ArrayList<Node> subsubstructure: subsubstructures) {
            ni.setNext(getFirst(subsubstructure), ' ', 1);
            getLast(subsubstructure).setNext(nf, ' ', 2);
            moveIDStructure(subsubstructure, substructure.size());
            substructure.addAll(subsubstructure);
        }
        nf.ID = nf.ID + substructure.size();
        nf.setX(getLast(substructure).getX() + 1);
        substructure.add(nf);
    }

    private void moveIDStructure(ArrayList<Node> structure, int move) {
        for(Node node: structure) {
            node.ID = node.ID + move;
        }
    }

    private Node getLast(ArrayList<Node> structure) {
        return structure.get(structure.size()-1);
    }

    private Node getFirst(ArrayList<Node> structure) {
        return structure.get(0);
    }

    // here is possible to get errors
    private ArrayList<Integer> getLineDivisions(String chain) {
        ArrayList<Integer> divisions = new ArrayList<>();
        int needToClose = 0;
        for(int i = 0; i < chain.length(); i++) {
            char c = chain.charAt(i);
            if(c == '(') {
                needToClose++;            
            } else if (c == ')') {
                needToClose--;
            } else if(c == '|' && needToClose == 0) {
                divisions.add(i);
            }
        }
        return divisions;
    }

    private int getParentesisEnd(String chain) {
        int end = -1;
        int needToClose = 0;
        for(int i = 0; i < chain.length(); i++) {
            char c = chain.charAt(i);
            if(c == '(') {
                needToClose++;            
            } else if (c == ')') {
                if(needToClose == 0) {
                    end = i;
                    break;
                }else needToClose--;
            } 
        }
        return end;
    }

    private String lenguageString2() {
        String str = "DS\t";
        for(int i = 0; i < lenguage.size(); i++) {
            if( i != lenguage.size() - 1) {
                str += lenguage.get(i) + "\t";
            } else str += lenguage.get(i);
        }
        return str;
    }

    private String lenguageString() {
        String str = "";
        for(int i = 0; i < lenguage.size(); i++) {
            if( i != lenguage.size() - 1) {
                str += lenguage.get(i) + ",";
            } else str += lenguage.get(i);
        }
        return str;
    }

    private void DM(DState state) {
        // get DS and DM
        for(char letter: lenguage) {
            ArrayList<Node> match = new ArrayList<>();
            for(Node node: state.Nodes) {
                match.addAll(node.nodesWith(new ArrayList<Node>(), letter));
            }
            ArrayList<Node> clausure = new ArrayList<>();
            for(Node node: match) {
                @SuppressWarnings("unchecked")
                ArrayList<Node> clausure2 = (ArrayList<Node>) clausure.clone();
                clausure.addAll(node.nodesWith(clausure2, ' '));
            }
            DState newDState = new DState(clausure, dstates, finalNode);
            // if state already exists
            newDState = newDState.alreadyExists(dstates);
            state.F.put(letter, newDState);
            // if not already on terminal states, put
            if(newDState.isTerminal && !terminalDStates.contains(newDState)) terminalDStates.add(newDState);
            DMProcess += "DM(s" + state.ID + "," + letter + ") = cl({" + DState.nodesToString(match) + "}) = {" + DState.nodesToString(clausure) + "} = s" + newDState.ID + "\n";
        }
        if(state.ID + 1 < dstates.size()) {
            DM(dstates.get(state.ID+1));
        }
    }
}
