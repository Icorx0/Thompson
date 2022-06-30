package com.thompson;

import java.util.ArrayList;
import java.util.HashMap;

public class DState {
    
    public static String nodesToString(ArrayList<Node> Nodes) {
        String str = "";
        for(int i = 0; i < Nodes.size(); i++) {
            if( i != Nodes.size() - 1) {
                str += Nodes.get(i).ID + ",";
            } else str += Nodes.get(i).ID;
        }
        return str;
    }
    public static String DStatesToString(ArrayList<DState> Dstates) {
        String str = "";
        for(int i = 0; i < Dstates.size(); i++) {
            if( i != Dstates.size() - 1) {
                str += "s" + Dstates.get(i).ID + ",";
            } else str += "s" + Dstates.get(i).ID;
        }
        return str;
    }

    public ArrayList<Node> Nodes = new ArrayList<>();
    public HashMap<Character, DState> F = new HashMap<>(); 
    public HashMap<Character, DState> G = new HashMap<>(); 
    public boolean isTerminal = false;
    public boolean isStarting = false;
    public int ID;
    public DState(ArrayList<Node> array, ArrayList<DState> states, Node finalNode) {
        Nodes = array;
        ID = states.size();
        states.add(this);
        if(Nodes.contains(finalNode)) isTerminal = true;
    }

    public DState alreadyExists(ArrayList<DState> states) {
        DState matchstatenode = this;
        // no equal states
        for(DState statenode: states) {
            if(statenode.equals(this)) continue;
            if(statenode.Nodes.size() == Nodes.size()) {
                int matches = 0;
                for(Node node1 : Nodes) {
                    if(statenode.Nodes.contains(node1)) {
                        matches++;
                    } else {
                        break;
                    }
                }
                if(matches == Nodes.size()) {
                    matchstatenode = statenode;
                    states.remove(this);
                    break;
                }
            } 
        }
        return matchstatenode;
    }
}
