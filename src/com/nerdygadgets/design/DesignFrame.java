package com.nerdygadgets.design;

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

        // TODO nodig?
        jbAddFirewall = new JButton("Add Firewall");
        jbAddFirewall.addActionListener(this);
        //add(jbAddFirewall);

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
            DatabaseServer dbComponent = new DatabaseServer(designPanel);
            designPanel.add(dbComponent);
        } else if (e.getSource() == jbAddFirewall){
            //TODO modal
            Firewall fwComponent = new Firewall(designPanel);
            designPanel.add(fwComponent);
        } else if (e.getSource() == jbAddWebServer){
            //TODO modal
            WebServer wsComponent = new WebServer(designPanel);
            designPanel.add(wsComponent);
        }
        designPanel.repaint();
    }
}
