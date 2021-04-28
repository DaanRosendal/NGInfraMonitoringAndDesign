package com.nerdygadgets.design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesignFrame extends JFrame implements ActionListener {
    JButton jbAddRouter, jbAddWebServer, jbAddDatabaseServer;

    public DesignFrame() {
        setTitle("NerdyGadgets Infrastructure Designer");
        setSize(600, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        jbAddWebServer = new JButton("Add Web Server");
        jbAddWebServer.addActionListener(this);
        add(jbAddWebServer);

        jbAddDatabaseServer = new JButton("Add Database Server");
        jbAddDatabaseServer.addActionListener(this);
        add(jbAddDatabaseServer);

        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
