package com.nerdygadgets.monitoring;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Server {

    private final String name;
    private final String ip;
    private double cpuUsage;
    private int diskMbUsed, diskMbTotal, uptime;
    private boolean online;

    public Server(String name, String ip) {
        this.name = name;
        this.ip = ip;
        retrieveData();
    }

    public void retrieveData() {
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(100)).build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("http://%s:8080/status", ip)))
                    .GET()
                    .build();

            String response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

            JsonObject obj = JsonParser.parseString(response).getAsJsonObject().get("data").getAsJsonObject();
            JsonObject hdd = obj.get("hdd").getAsJsonObject();

            this.cpuUsage = obj.get("cpu").getAsDouble();
            this.diskMbUsed = hdd.get("usedMb").getAsInt();
            this.diskMbTotal = hdd.get("totalMb").getAsInt();

        } catch (java.net.http.HttpConnectTimeoutException ex) {
            this.online = false;
        } catch (IOException | InterruptedException | JsonSyntaxException ex) {
            this.online = false;
            ex.printStackTrace();
        }

    }

    /**
     * Get name of server
     *
     * @return servername
     */
    public String getName() {
        return name;
    }

    /**
     * Get the IP of this server
     *
     * @return server IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * Check if the server is online and the service is running according to HAProxy
     *
     * @return true if online
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Change the online status of this server
     *
     * @param online true if offline
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Get total storage used in megabytes
     *
     * @return storage usage in mb
     */
    public int getDiskMbUsed() {
        return diskMbUsed;
    }

    /**
     * Get total storage size in megabytes
     *
     * @return total storage size in mb
     */
    public int getDiskMbTotal() {
        return diskMbTotal;
    }

    /**
     * Get the current CPU utilisation for this server as a percentage between 0-100
     *
     * @return current CPU utilisation
     */
    public double getCpuUsage() {
        return cpuUsage;
    }

    /**
     * Return the up- or downtime of this server
     *
     * @return up- or downtime in seconds
     */
    public int getUptime() {
        return uptime;
    }

    /**
     * Return the formatted up- or downtime of this server
     *
     * @return formatted up- or downtime in seconds
     */
    public String getFormattedUptime() {
        return formatSeconds(uptime);
    }

    /**
     * Set the up- or downtime of this server
     *
     * @param uptime uptime in seconds
     */
    public void setUptime(int uptime) {
        this.uptime = uptime;
    }

    /**
     * Format seconds as time
     *
     * @param seconds seconds
     * @return hh:mm:ss date format
     */
    public static String formatSeconds(int seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
    }

}
