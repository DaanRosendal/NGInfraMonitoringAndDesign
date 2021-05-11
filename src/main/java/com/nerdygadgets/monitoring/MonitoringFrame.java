package com.nerdygadgets.monitoring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MonitoringFrame extends JFrame implements ActionListener {

    private MonitoringPanel panel;

    public MonitoringFrame(Server... servers) {
        setTitle("NerdyGadgets Infra Monitoring");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        this.panel = new MonitoringPanel(servers);
        this.add(this.panel);

        setVisible(true);

        setLocationRelativeTo(null);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Arrays.stream(servers).forEach(Server::retrieveData);
                panel.update();
            }
        }, 3 * 1000L, 3 * 1000L);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
