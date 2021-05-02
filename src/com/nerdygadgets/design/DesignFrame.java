package com.nerdygadgets.design;

import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.Firewall;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesignFrame extends JFrame implements ActionListener {
    private JButton jbAddFirewall, jbAddWebServer, jbAddDatabaseServer;
    private DesignPanel designPanel;

    public DesignFrame() {
        setTitle("NerdyGadgets Infrastructure Design Tool");
        setSize(800, 600);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(false);

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
            ComponentDialog cd = new ComponentDialog(this, designPanel, "databaseserver");
            if(cd.getIsOk()){
                InfrastructureComponent dbComponent = cd.getComponent();
                designPanel.add(dbComponent);
            }
        } else if (e.getSource() == jbAddWebServer){
            ComponentDialog cd = new ComponentDialog(this, designPanel, "webserver");
            if(cd.getIsOk()){
                InfrastructureComponent wComponent = cd.getComponent();
                designPanel.add(wComponent);
            }
        }
        designPanel.repaint();
    }
}
