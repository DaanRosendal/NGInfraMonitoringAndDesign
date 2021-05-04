package com.nerdygadgets.monitoring;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class MonitoringPanel extends JPanel {

    private Server[] servers;

    public MonitoringPanel(Server... servers) {
        this.setPreferredSize(new Dimension(600, 300));
        this.setLayout(new GridLayout(servers.length, 3));
        this.servers = servers;

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
    }

    public void update() {
        //bodge something together to prevent needing to save the positions of the JLabels in memory
        for (int i = 0; i < servers.length; i++) {
            Server server = servers[i];

            if (server.isOnline()) {
                this.getComponent(i*3).setForeground(Color.GREEN);
                ((JLabel) this.getComponent(i*3)).setText(server.getName() + " (" + server.getIp() + ")");
                ((JLabel) this.getComponent(i*3+1)).setText("CPU: " + server.getCpuUsage() + "%");
                ((JLabel) this.getComponent(i*3+2)).setText("Storage: " + server.getDiskMbUsed() + "mb/" + server.getDiskMbTotal() + "mb");
            } else {
                this.getComponent(i*3).setForeground(Color.RED);
                ((JLabel) this.getComponent(i*3)).setText(server.getName() + " (" + server.getIp() + ")");
                ((JLabel) this.getComponent(i*3+1)).setText("OFFLINE!");
                ((JLabel) this.getComponent(i*3+2)).setText("OFFLINE!");
            }
        }
    }
}
