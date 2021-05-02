package com.nerdygadgets.design;

import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.Firewall;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;

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
        g.setFont(new Font("SansSerif", Font.BOLD, 12));

        // Draw lines from every component to the firewall
        for(Component c : this.getComponents()){
            if(c instanceof WebServer || c instanceof DatabaseServer){
                g.drawLine(firewall.getX()+32, firewall.getY()+32, c.getX()+32, c.getY()+32);
            }

            // Draw details under component
            if(c instanceof InfrastructureComponent){
                InfrastructureComponent ic = (InfrastructureComponent) c;
                if(ic instanceof Firewall){
                    g.drawString(ic.getComponentName(), c.getX(), c.getY()+64);
                    g.drawString(ic.getAvailability() + "%", c.getX(), c.getY()+79);
                    g.drawString("€" + ic.getAnnualPrice(), c.getX(), c.getY()+94);
                } else {
                    g.drawString(ic.getComponentName(), c.getX(), c.getY()+74);
                    g.drawString(ic.getAvailability() + "%", c.getX(), c.getY()+89);
                    g.drawString("€" + ic.getAnnualPrice(), c.getX(), c.getY()+104);
                }
            }
        }
    }
}
