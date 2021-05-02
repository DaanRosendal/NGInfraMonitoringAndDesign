package com.nerdygadgets.design.components;

import javax.swing.*;

public class Firewall extends InfrastructureComponent {
    public Firewall(JPanel parentPanel) {
        super(parentPanel, "pfSense", 99.998, 4000);
    }
}
