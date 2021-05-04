package com.nerdygadgets.design.components;

import javax.swing.*;

public class Firewall extends InfrastructureComponent {
    public Firewall(JPanel parentPanel, String name, double availability, double annualPrice) {
        super(parentPanel, "pfSense", 99.998, 4000);
    }

    public Firewall(JPanel parentPanel, String name, double availability, double annualPrice, int panelX, int panelY){
        super(parentPanel, name, availability, annualPrice, panelX, panelY);
    }
}
