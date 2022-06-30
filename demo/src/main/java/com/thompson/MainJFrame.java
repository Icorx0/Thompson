package com.thompson;

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.BorderFactory;

public class MainJFrame {
    private static Color TEXT_COLOR = new Color(239, 235, 252);
    private static Color FIELD_COLOR = new Color(66, 70, 77);
    private static Color FIELD_HOVER_COLOR = new Color(14, 9, 0, 44);
    private static Color BG_PRIMARY_COLOR = new Color(47, 49, 54);
    private static Color BG_SECONDARY_COLOR = new Color(32, 34, 37);
    private static Font MAIN_FONT = new Font("Calibri",Font.PLAIN, 16);
    private static Font BUTTON_FONT = new Font(MAIN_FONT.getName(), MAIN_FONT.getStyle(), MAIN_FONT.getSize() - 6);

    public static void main(String[] args) {
        // Jframe
        JFrame window = new JFrame("Thomspon");
        window.setSize(815, 490);
        window.setLayout(null);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UIManager.getLookAndFeelDefaults().put("defaultFont", MAIN_FONT);

        // Graph panel with the resolution
        JPanel graphPanel = new JPanel();
        graphPanel.setBackground(BG_PRIMARY_COLOR);
        graphPanel.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(graphPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        window.add(scrollPane);
        
        // Field to enter the regular expression
        JPanel entertextpanel = EnterText(scrollPane);
        window.add(entertextpanel);

        // setting the scroll panel height after setting the entertexpanel
        scrollPane.setBounds(0,entertextpanel.getHeight(),815,410);

        // set resize listener
        window.getContentPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Component c = (Component)e.getSource();
                for(Component a: getAllJPanels(window)) {
                    a.setSize(c.getWidth(), (int)a.getSize().getHeight());
                }
                scrollPane.setSize(c.getWidth(), c.getHeight() - entertextpanel.getHeight());
                scrollPane.revalidate();
            }
        });
    }
    
    public static List<Component> getAllJPanels(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            if(comp instanceof JPanel) compList.add(comp);
            if (comp instanceof Container)
                compList.addAll(getAllJPanels((Container) comp));
        }
        return compList;
    }

    public static JPanel EnterText(JScrollPane scrollPane) {
        // Panel basics
        int width = 815;
        int fieldhight = MAIN_FONT.getSize() + 8;
        int hight = 70 + fieldhight;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(BG_SECONDARY_COLOR);
        panel.setBounds(0, 0, width, hight);

        // Panel texfield
        JTextField textfield = new JTextField();
        textfield.setBounds(20, 20, 160, fieldhight);
        textfield.setBackground(FIELD_COLOR);
        textfield.setForeground(TEXT_COLOR);
        textfield.setLayout(null);
        textfield.setBorder(BorderFactory.createEmptyBorder());
        panel.add(textfield);

        // Panel JLabel
        JLabel jLabel = new JLabel();
        jLabel.setBounds(20, 30 + fieldhight, 795, fieldhight);
        jLabel.setBackground(FIELD_COLOR);
        jLabel.setForeground(TEXT_COLOR);
        jLabel.setLayout(null);
        jLabel.setText("Ingresa una expresión regular (puedes usar \"a,b,1,0...\" y \"()|*\")");
        panel.add(jLabel);

        // Panel button
        JButton button = new JButton();
        button.setBounds(200, 20, 100, fieldhight);
        button.setBackground(FIELD_COLOR);
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setLayout(null);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setText("Mostrar Thompson");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == button) {
                    PrintResolution(textfield, scrollPane, jLabel);
                }
            }
        });
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(FIELD_HOVER_COLOR);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(FIELD_COLOR);
            }
        });
        panel.add(button);

        return panel;
    }

    // Imprimir solución y handler de errores
    public static void PrintResolution(JTextField textfield, JScrollPane scrollPane, JLabel jLabel) {
        try {
            String regularExpresion = textfield.getText();
            Thompson thompson = new Thompson(regularExpresion);
            if(thompson.hasErrors()) {
                setToScrollPanel(thompson.getErrorMessage(), scrollPane, jLabel);
            } else {
                setToScrollPanel("Mostrando el procedimiento de: " + regularExpresion, scrollPane, jLabel, thompson);
            }
        } catch (Exception exception) {
            setToScrollPanel("Error fatal desconocido, ingresa la expresión nuevamente...", scrollPane, jLabel);
            System.out.println(exception.getMessage());
        }
        // free memory
        System.gc();
    }
    public static void setToScrollPanel( String string, JScrollPane scrollPane, JLabel jLabel) {
        // Panel de error
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(null);
        errorPanel.setBackground(BG_PRIMARY_COLOR);
        scrollPane.setViewportView(errorPanel);
        jLabel.setForeground(new Color(255,0,0));
        jLabel.setText(string);
    }
    public static void setToScrollPanel(String string, JScrollPane scrollPane, JLabel jLabel, Thompson thompson) {
        GraphPanel graphPanel = new GraphPanel(thompson);
        scrollPane.setViewportView(graphPanel);
        jLabel.setForeground(TEXT_COLOR);
        jLabel.setText(string);
    }

}