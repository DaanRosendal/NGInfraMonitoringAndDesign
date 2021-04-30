package com.nerdygadgets.design;

import javax.swing.*;
import java.awt.*;

public class DesignPanel extends JPanel {
    private Firewall firewall;

    public DesignPanel(){
        setPreferredSize(new Dimension(700, 500));
        setBackground(Color.white);
        firewall = new Firewall(this);
        add(firewall);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(Component c : this.getComponents()){
            if(c instanceof WebServer || c instanceof DatabaseServer){
                g.drawLine(firewall.getX()+32, firewall.getY()+32, c.getX()+32, c.getY()+32);
            }
        }
    }
}
