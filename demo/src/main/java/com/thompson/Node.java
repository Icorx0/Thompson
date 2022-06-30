package com.thompson;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {

    public int ID;
    public HashMap<Node, Character> nextNodes = new HashMap<>();
    public HashMap<Node, Integer> nextNodeArrow = new HashMap<>();

    private int[] xy = new int[2];

    public Node(int ID, int x, int y) {
        this.xy[0] = x;
        this.xy[1] = y;
        this.ID = ID;
    }
    public int getX() {
        return this.xy[0];
    }
    public int getY() {
        return this.xy[0];
    }
    public void setX(int x) {
        this.xy[0] = x;
    }
    public void setY(int y) {
        this.xy[1] = y;
    }

    /*
     * Arrow types:
     * 0: o->
     * 1: o<:
     * 2: :>o
     * 3: ---I
     * 4: v---
     */
    public void setNext(Node node, Character letter, Integer arrowtype) {
        nextNodes.put(node, letter);
    }

    public ArrayList<Node> nodesWith(ArrayList<Node> passed, Character letter) {
        ArrayList<Node> match = new ArrayList<>();
        for(Node nextNode: nextNodes.keySet()) {
            if(nextNodes.get(nextNode).equals(letter) && !passed.contains(nextNode)) {
                match.add(nextNode);
                passed.add(nextNode);
            }
        }
        if(letter == ' ') {
            if(!passed.contains(this)) {
                match.add(this);
                passed.add(this);
            }
            int i = 0;
            while(i < match.size()) {
                Node node = match.get(i);
                match.addAll(node.nodesWith(passed, letter));
                i++;
            }
        }
        return match;
    }
}
