package com.nerdygadgets.design;

import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.Firewall;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

public class DesignPanel extends JPanel implements ComponentListener {
    private Firewall firewall;
    private JFrame frame;

    public DesignPanel(JFrame frame) {
        this.frame = frame;
        frame.addComponentListener(this);
        setResponsiveSize();
        setBackground(Color.white);
        setLayout(null);
        repaint();
    }

    public ArrayList<InfrastructureComponent> getInfrastructureComponents() {
        ArrayList<InfrastructureComponent> components = new ArrayList<InfrastructureComponent>();
        for (Component c : this.getComponents()) {
            if (c instanceof InfrastructureComponent) {
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
                break;
            }
        }

        g.drawString("Prijs: €" + calculateTotalAnnualPrice(), getWidth() - 200, 10);
        g.drawString("Beschikbaarheid: " + calculateTotalAvailability() + "%", getWidth() - 200, 25);

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
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    // Set panel size so it fits in the parent JFrame
    public void setResponsiveSize() {
        setPreferredSize(new Dimension(frame.getWidth() - 25, frame.getHeight() - 80));
    }

    // Register component x & y panel values
    public void determineComponentPositions() {
        for (Component c : this.getComponents()) {
            if (c instanceof InfrastructureComponent) {
                InfrastructureComponent ic = (InfrastructureComponent) c;
                ic.setPanelX(c.getX());
                ic.setPanelY(c.getY());
            }
        }
    }

    public String calculateTotalAnnualPrice() {
        double totalAnnualPrice = 0;
        for (Component c : this.getComponents()) {
            if (c instanceof InfrastructureComponent) {
                InfrastructureComponent ic = (InfrastructureComponent) c;
                totalAnnualPrice += ic.getAnnualPrice();
            }
        }
        return removeTrailingZeros(totalAnnualPrice);
    }

    public String calculateTotalAvailability() {
        double firewallAvailability = 1;
        double webAvailability = 1;
        double databaseAvailability = 1;

        for (Component c : this.getComponents()) {
            if (c instanceof InfrastructureComponent) {
                InfrastructureComponent ic = (InfrastructureComponent) c;
                if (ic instanceof Firewall) {
                    firewallAvailability *= (1 - (ic.getAvailability() / 100));
                }else if (ic instanceof WebServer) {
                    webAvailability *= (1 - (ic.getAvailability() / 100));
                }else if (ic instanceof DatabaseServer) {
                    databaseAvailability *= (1 - (ic.getAvailability() / 100));
                }
            }
        }
        double totalAvailability = (1 - firewallAvailability) * (1 - webAvailability) * (1 - databaseAvailability);

        // System.out.println("Resultaat: \t" + totalAvailability);

        return removeTrailingZeros((double) Math.round((totalAvailability*100) * 1000d)/1000d);
    }

    // Remove trailing zeros from a double, example: 90.0 becomes 90
    public String removeTrailingZeros(double number) {
        if (number % 1 == 0) {
            return String.format("%.0f", number);
        } else {
            return String.valueOf(number);
        }
    }
}
