package com.thompson;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GraphPanel extends JPanel {
    private static Font mainfont = new Font("Calibri",Font.PLAIN, 16);
    private static Color text = new Color(239, 235, 252);
    private static Color bgPrimary = new Color(47, 49, 54);

    private int maxheight = 0;
    private int maxwidth = 0;

    private BufferedImage bufferedImage;

    private int Ylevels = 1;
    private int YlevelSize = 100;
    private int Xlevels = 1;
    private int XlevelSize = 100;

    public GraphPanel(Thompson thompson) {
        // Panel basics
        setLayout(null);
        setBackground(bgPrimary);

        // Node image
        int nodessize = thompson.nodes.size();
        bufferedImage = new BufferedImage(2*XlevelSize*nodessize, 2*YlevelSize*nodessize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        printNextNode(thompson.firstNode, g2d, thompson);
        g2d.dispose();

        // DM Resolution
        int labelhight = mainfont.getSize() + 8;
        AffineTransform affinetransform = new AffineTransform();     
        FontRenderContext frc = new FontRenderContext(affinetransform,true,true); 
        for(String line: thompson.DMProcess.split("\n")) {
            JLabel labelLine = new JLabel();
            int textwidth = (int)(mainfont.getStringBounds(line, frc).getWidth());
            labelLine.setBounds(20, maxheight + labelhight + 10, textwidth, labelhight);
            labelLine.setFont(mainfont);
            labelLine.setForeground(text);
            labelLine.setLayout(null);
            labelLine.setText(line);
            add(labelLine);
            maxheight += labelhight;
            maxwidth = (maxwidth<textwidth) ? textwidth : maxwidth;
        }
        maxheight += 10;

        // set maxheight
        setPreferredSize(new Dimension(maxwidth, maxheight)); 
    }

    public void createNodeImage(Thompson thompson) {
    }
    
    // Remember this function is called multiple times
    //public void paintComponent(Graphics g) {
        //super.paintComponent(g);    
        
        // draw resolution
        /*Graphics2D graphics2d = (Graphics2D)g;
        graphics2d.setFont(mainfont);
        graphics2d.setColor(text);
        printNode(thompson.firstNode, graphics2d);
        maxheight += Ylevels*YlevelSize;
        printNextNode(thompson.firstNode, graphics2d);
        setPreferredSize(new Dimension(maxwidth, maxheight)); */


        //maxheight = 0;
        //maxwidth = 0;
        //printed.clear();
    //}

    private HashMap<Node,Integer[]> nodePosition = new HashMap<>();
    private ArrayList<Integer> printed = new ArrayList<>();
    private void printNextNode(Node node, Graphics2D graphics2d, Thompson thompson) {
        // print this node
        if(!printed.contains(node.ID)) {
            //printNode(node, graphics2d, thompson);
            printed.add(node.ID);
        }
        // print next nodes
        for(Node nextNode: node.nextNodes.keySet()) {
            System.out.println(node.ID + "-> " + nextNode.ID);
            if(printed.contains(nextNode.ID)) continue;
            printNextNode(nextNode, graphics2d, thompson);
            if(nextNode.nodesbefore > 1) {
                // Lo
            } else if(node.nextNodes.size() > 1) {
                // o<
            } else if(nextNode.ID < node.ID) {
                    // <-o
            } else {
                // o->
            }
        }
    }

    private ArrayList<Node> orderSet(Set<Node> set) {
        ArrayList<Node> orderedSet = new ArrayList<>();
        for(Node nextNode: set) {
            int size = orderedSet.size();
            if(size == 0) {
                orderedSet.add(nextNode);
                continue;
            }
            if(nextNode.ID > orderedSet.get(size - 1).ID) {
                orderedSet.add(size, nextNode);
            } else {
                for(int i = 0; i < size; i ++) {
                    if(nextNode.ID < orderedSet.get(i).ID) {
                        orderedSet.add(i, nextNode);
                        break;
                    }
                }
            }
        }
        return orderedSet;
    }

    private void printLater() {
    }

    private void printNode(Node node, Graphics2D graphics2d, Thompson thompson) {
        Integer[] xy = {0, 0};
        nodePosition.put(node, xy);
        if(thompson.finalNode.equals(node)) {
            graphics2d.drawArc(xy[0]+25, xy[1]+25, XlevelSize-50, YlevelSize-50, 0, 360);
        }
        if(thompson.firstNode.equals(node)){
            graphics2d.drawArc(xy[0]+10, xy[1]+10, XlevelSize-20, YlevelSize-20, 0, 360);
            int[] xs = {0,10,0};
            int[] ys = {20,YlevelSize/2,YlevelSize - 20};
            graphics2d.drawPolyline(xs, ys, xs.length);
        }else {
            graphics2d.drawArc(xy[0], xy[1], XlevelSize, YlevelSize, 0, 360);
        }
        Rectangle2D bound = graphics2d.getFontMetrics().getStringBounds("" + node.ID, graphics2d);
        int width = (int)(bound.getMaxX() - bound.getMinX());
        int height = (int)(bound.getMaxY() - bound.getMinY());
        graphics2d.drawString("" + node.ID, (xy[0] + XlevelSize/2) - width/2, (xy[1] + YlevelSize/2) + height/2);
    }

    private void addYLevelUp() {
        Ylevels += 1;
        for(Node node: nodePosition.keySet()) {
            Integer[] xy = nodePosition.get(node);
            Integer[] newxy = new Integer[2];
            newxy[0] = xy[0];
            newxy[1] = xy[1] + YlevelSize;
            nodePosition.put(node, newxy);
        }
    }
    private void addYLevelDown() {
        Ylevels += 1;
    }
}
