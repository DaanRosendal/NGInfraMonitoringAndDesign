package com.nerdygadgets.monitoring;

import java.util.Random;

public class Server {

    private String name, ip;
    private int diskMbUsed, diskMbTotal, cpuUsage;

    public Server(String name, String ip) {
        this.name = name;
        this.ip = ip;
        retrieveData();
    }

    public void retrieveData() {
        // TODO: Actually retrieve data from server

        Random random = new Random();
        this.diskMbTotal = random.nextInt(1024*16);
        this.diskMbUsed = random.nextInt(diskMbTotal);
        this.cpuUsage = random.nextInt(100);
    }

    /**
     * Get n
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the IP of this server
     * @return server IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * Get total storage used in megabytes
     * @return storage usage in mb
     */
    public int getDiskMbUsed() {
        return diskMbUsed;
    }

    /**
     * Get total storage size in megabytes
     * @return total storage size in mb
     */
    public int getDiskMbTotal() {
        return diskMbTotal;
    }

    /**
     * Get the current CPU utilisation for this server as a percentage between 0-100
     * @return current CPU utilisation
     */
    public int getCpuUsage() {
        return cpuUsage;
    }


}
