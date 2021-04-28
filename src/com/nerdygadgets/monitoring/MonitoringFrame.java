package com.nerdygadgets.monitoring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MonitoringFrame extends JFrame implements ActionListener {

    public static void main(String[] args) {
        new MonitoringFrame();
    }

    public MonitoringFrame() {
        setTitle("NerdyGadgets Infra Monitoring");
        setSize(600, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        this.add(new Speedometer());


        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
