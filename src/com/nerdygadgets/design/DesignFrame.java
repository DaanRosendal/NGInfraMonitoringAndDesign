package com.nerdygadgets.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesignFrame extends JFrame implements ActionListener {
    JButton jbAddFirewall, jbAddWebServer, jbAddDatabaseServer;
    DesignPanel designPanel;

    public DesignFrame() {
        setTitle("NerdyGadgets Infrastructure Design Tool");
        setSize(800, 600);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        jbAddFirewall = new JButton("Add Firewall Server");
        jbAddFirewall.addActionListener(this);
        add(jbAddFirewall);

        jbAddWebServer = new JButton("Add Web Server");
        jbAddWebServer.addActionListener(this);
        add(jbAddWebServer);

        jbAddDatabaseServer = new JButton("Add Database Server");
        jbAddDatabaseServer.addActionListener(this);
        add(jbAddDatabaseServer);

        designPanel = new DesignPanel();
        add(designPanel);

        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == jbAddDatabaseServer){
            //TODO modal
            InfrastructureComponent dbComponent = new InfrastructureComponent("databaseserver");
            designPanel.add(dbComponent);
        } else if (e.getSource() == jbAddFirewall){
            //TODO modal
            InfrastructureComponent fwComponent = new InfrastructureComponent("firewall");
            designPanel.add(fwComponent);
        } else if (e.getSource() == jbAddWebServer){
            //TODO modal
            InfrastructureComponent fwComponent = new InfrastructureComponent("webserver");
            designPanel.add(fwComponent);
        }
        designPanel.repaint();
    }
}
