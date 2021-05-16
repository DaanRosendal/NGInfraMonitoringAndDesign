package com.nerdygadgets.monitoring;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class MonitoringPanel extends JPanel {

    private final Server[] servers;
    private boolean websiteOnline;
    public int websiteUptime;

    public MonitoringPanel(Server... servers) {
        this.setPreferredSize(new Dimension(600, 300));
        this.setLayout(new GridLayout(servers.length + 1, 3));
        this.servers = servers;

        for (int i = 0; i <= servers.length; i++) {
            this.add(new JLabel(" "));
            this.add(new JLabel(" "));
            this.add(new JLabel(" "));
        }
    }

    public void update() {
        //bodge something together to prevent needing to save the positions of the JLabels in memory

        //Website uptime:
        String uptime = websiteOnline ? "Uptime" : "Downtime";
        this.getComponent(0).setForeground(websiteOnline ? Color.decode("#00c229") : Color.RED);
        ((JLabel) this.getComponent(0)).setText("https://nerdygadgets.shop");
        ((JLabel) this.getComponent(1)).setText(uptime + ":" + Server.formatSeconds(this.websiteUptime));

        //Server uptime:
        for (int i = 1; i <= servers.length; i++) {
            Server server = servers[i - 1];

            uptime = server.isOnline() ? "Uptime" : "Downtime";
            ((JLabel) this.getComponent(i * 3)).setText("<html>" + server.getName() + " (" + server.getIp() + ")<br>" + uptime + ": " + server.getFormattedUptime() + "</html>");
            if (server.isOnline()) {
                this.getComponent(i * 3).setForeground(Color.decode("#00c229"));
                ((JLabel) this.getComponent(i * 3 + 1)).setText("CPU: " + server.getCpuUsage() + "%");
                ((JLabel) this.getComponent(i * 3 + 2)).setText("Storage: " + server.getDiskMbUsed() + "mb/" + server.getDiskMbTotal() + "mb");
            } else {
                this.getComponent(i * 3).setForeground(Color.RED);
                ((JLabel) this.getComponent(i * 3 + 1)).setText("OFFLINE!");
                ((JLabel) this.getComponent(i * 3 + 2)).setText("OFFLINE!");
            }
        }
    }

    public void cacheUptime() {
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(100)).build();

            byte[] encodedAuth = Base64.getEncoder().encode("admin:ictm2p2".getBytes(StandardCharsets.ISO_8859_1));
            String authHeader = "Basic " + new String(encodedAuth);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .timeout(Duration.ofMillis(250))
                    .uri(URI.create("https://www.nerdygadgets.shop/haproxy?stats;csv"))
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            String response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

            List<String> serverNames = Arrays.stream(servers).map(Server::getName).collect(Collectors.toList());

            for (String line : response.split("\\n")) {
                String[] csv = line.split(",");

                if (csv[0].equals("nerdygadgets.shop_ipv4") && csv[1].equals("BACKEND")) {
                    this.websiteOnline = csv[17].equals("UP");
                    this.websiteUptime = Integer.valueOf(csv[23]);
                    continue;
                }

                if (serverNames.contains(csv[1])) {
                    Server server = Arrays.stream(servers).filter(srv -> srv.getName().equals(csv[1])).findFirst().orElse(null);
                    if (server == null)
                        continue;

                    server.setOnline(csv[17].equals("UP"));
                    server.setUptime(Integer.valueOf(csv[23]));
                }
            }
        } catch (InterruptedException | IOException e) {
            if (this.websiteOnline) {
                this.websiteUptime = 0;
            }else{
                // Increase website 'downtime' with 3 seconds, since this check is performed every 3 seconds.
                this.websiteUptime = this.websiteUptime + 3;
            }
            this.websiteOnline = false;
            e.printStackTrace();
        }
    }
}
