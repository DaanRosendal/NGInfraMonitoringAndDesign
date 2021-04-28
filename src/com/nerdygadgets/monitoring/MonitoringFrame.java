package com.nerdygadgets.monitoring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class MonitoringFrame extends JFrame implements ActionListener {

    public MonitoringFrame(Server... servers) {
        setTitle("NerdyGadgets Infra Monitoring");
        setSize(600, 300);
        setLayout(new GridLayout(servers.length, 3));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        for (Server server : servers) {

            JLabel serverInfo = new JLabel(server.getName() + " (" + server.getIp() + ")");
            if (server.isOnline()) {
                serverInfo.setForeground(Color.GREEN);
                this.add(serverInfo);
                this.add(new JLabel("CPU: " + server.getCpuUsage() + "%"));
                this.add(new JLabel("Storage: " + server.getDiskMbUsed() + "mb/" + server.getDiskMbTotal() + "mb"));
            } else {
                serverInfo.setForeground(Color.RED);
                this.add(serverInfo);
                this.add(new JLabel("OFFLINE!"));
                this.add(new JLabel("OFFLINE!"));
            }
        }

        setVisible(true);

        setLocationRelativeTo(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
