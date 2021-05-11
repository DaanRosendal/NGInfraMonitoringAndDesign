package com.nerdygadgets.design.components;

import javax.swing.*;

public class DatabaseServer extends InfrastructureComponent {
    public DatabaseServer(JPanel parentPanel, String name, double availability, double annualPrice){
        super(parentPanel, name, availability, annualPrice);
    }

    public DatabaseServer(JPanel parentPanel, String name, double availability, double annualPrice, int panelX, int panelY){
        super(parentPanel, name, availability, annualPrice, panelX, panelY);
    }
}
