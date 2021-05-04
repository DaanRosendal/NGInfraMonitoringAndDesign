package com.nerdygadgets.design;

import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.Firewall;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DesignPanel extends JPanel implements ComponentListener {
    private Firewall firewall;
    private JFrame frame;

    public DesignPanel(JFrame frame){
        this.frame = frame;
        frame.addComponentListener(this);
        setResponsiveSize();
        setBackground(Color.white);
        setLayout(null);
        repaint();
    }

    public ArrayList<InfrastructureComponent> getInfrastructureComponents(){
        ArrayList<InfrastructureComponent> components = new ArrayList<InfrastructureComponent>();
        for(Component c : this.getComponents()){
            if(c instanceof InfrastructureComponent){
                InfrastructureComponent ic = (InfrastructureComponent) c;
                components.add(ic);
            }
        }
        return components;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));

        // TODO show total cost/availability

        Firewall firewall = null;
        for (Component c : this.getComponents()) {
            if (c instanceof Firewall) {
                firewall = (Firewall) c;
            }
        }

        for (Component c : this.getComponents()) {
            // Draw lines from every component to the firewall
            if (c instanceof WebServer || c instanceof DatabaseServer) {
                g.drawLine(firewall.getX() + 32, firewall.getY() + 32, c.getX() + 32, c.getY() + 32);
            }

            // Draw details under component
            if (c instanceof InfrastructureComponent) {
                InfrastructureComponent ic = (InfrastructureComponent) c;
                if (ic instanceof Firewall) {
                    g.drawString(ic.getComponentName(), c.getX(), c.getY() + 64);
                    g.drawString(ic.removeTrailingZeros(ic.getAvailability()) + "%", c.getX(), c.getY() + 79);
                    g.drawString("€" + ic.removeTrailingZeros(ic.getAnnualPrice()), c.getX(), c.getY() + 94);
                } else {
                    g.drawString(ic.getComponentName(), c.getX(), c.getY() + 74);
                    g.drawString(ic.removeTrailingZeros(ic.getAvailability()) + "%", c.getX(), c.getY() + 89);
                    g.drawString("€" + ic.removeTrailingZeros(ic.getAnnualPrice()), c.getX(), c.getY() + 104);
                }
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        setResponsiveSize();
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    // Set panel size so it fits in the parent JFrame
    public void setResponsiveSize(){
        setPreferredSize(new Dimension(frame.getWidth()-25, frame.getHeight()-80));
    }

    // Register component x & y panel values
    public void determineComponentPositions(){
        for(Component c : this.getComponents()){
            if(c instanceof InfrastructureComponent){
                InfrastructureComponent ic = (InfrastructureComponent) c;
                ic.setPanelX(c.getX());
                ic.setPanelY(c.getY());
            }
        }
    }
}
