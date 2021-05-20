package com.nerdygadgets.design;

import com.google.gson.*;
import com.nerdygadgets.design.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static javax.swing.JOptionPane.showMessageDialog;

public class DesignFrame extends JFrame implements ActionListener, WindowStateListener {
    // Design interface variables
    private JButton jbSave, jbOpen, jbCustomComponent, jbOptimize, jbOptimizeCurrentDesign;
    private JComboBox jcWebservers, jcDatabaseservers;
    private ArrayList<WebServer> webServers;
    private ArrayList<DatabaseServer> databaseServers;
    private DesignPanel designPanel;
    private Firewall firewall;

    // Optimization variables
    private int maximumServerCount;
    private int[] webServerCountPerKind = {};
    private int[] databaseServerCountPerKind = {};
    private double[] webServerAvailabilityPerKind = {};
    private double[] webServerCostPerKind = {};
    private double[] databaseServerAvailabilityPerKind = {};
    private double[] databaseServerCostPerKind = {};
    private int totalAmountOfWebServers = -1;
    private int totalAmountOfDatabaseServers = -1;
    private double desiredAvailability = -1;
    private double minimalCost = Double.MAX_VALUE;
    private int totalSetupCounter = 0;
    private String serverSetup;
    private int[] optimalWebServerSetup = {};
    private int[] optimalDatabaseServerSetup = {};

    public DesignFrame() {
        setTitle("NerdyGadgets Infrastructure Design Tool");
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Set size to 75% of user's screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize(screenWidth/4*3, screenHeight/4*3);

        jbSave = new JButton("Save File");
        jbSave.addActionListener(this);
        add(jbSave);

        jbOpen = new JButton("Open File");
        jbOpen.addActionListener(this);
        add(jbOpen);

        designPanel = new DesignPanel(this);

        firewall = new Firewall(designPanel, "pfSense", 99.998, 4000, designPanel.getWidth()/2, designPanel.getHeight()/2);
        designPanel.add(firewall);

        // Create JComboBox for webservers
        WebServer w1 = new WebServer(designPanel, "HAL9001W", 80, 2200);
        WebServer w2 = new WebServer(designPanel, "HAL9002W", 90, 3200);
        WebServer w3 = new WebServer(designPanel, "HAL9003W", 95, 5100);
        webServers = new ArrayList<WebServer>(Arrays.asList(w1, w2, w3));
        jcWebservers = new JComboBox(webServers.toArray());
        jcWebservers.setRenderer(new MyComboBoxRenderer("Web Server"));
        jcWebservers.setSelectedIndex(-1);
        jcWebservers.addActionListener(this);
        add(jcWebservers);

        // Create JComboBox for databaseservers
        DatabaseServer db1 = new DatabaseServer(designPanel, "HAL9001DB", 90, 5100);
        DatabaseServer db2 = new DatabaseServer(designPanel, "HAL9002DB", 95, 7700);
        DatabaseServer db3 = new DatabaseServer(designPanel, "HAL9003DB", 98, 12200);
        databaseServers = new ArrayList<DatabaseServer>(Arrays.asList(db1, db2, db3));
        jcDatabaseservers = new JComboBox(databaseServers.toArray());
        jcDatabaseservers.setRenderer(new MyComboBoxRenderer("Database Server"));
        jcDatabaseservers.setSelectedIndex(-1);
        jcDatabaseservers.addActionListener(this);
        add(jcDatabaseservers);

        jbCustomComponent = new JButton("Custom Component");
        jbCustomComponent.addActionListener(this);
        add(jbCustomComponent);

        jbOptimize = new JButton("Optimize");
        jbOptimize.addActionListener(this);
        add(jbOptimize);

        jbOptimizeCurrentDesign = new JButton("Optimize Current Design");
        jbOptimizeCurrentDesign.addActionListener(this);
        add(jbOptimizeCurrentDesign);

        add(designPanel);
        designPanel.updateComponentPositions();

        addWindowStateListener(this);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // If the save button was clicked
        if(e.getSource() == jbSave) {
            // Make sure panelX and panelY values are updated for each component
            designPanel.updateComponentPositions();

            // Configure gson
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();

            // Convert components on the designPanel to a json array
            String fileContent = "[";
            boolean firstValue = true;
            for (Component c : designPanel.getComponents()) {
                if (c instanceof InfrastructureComponent) {
                    InfrastructureComponent ic = (InfrastructureComponent) c;
                    if(firstValue){
                        firstValue = false;
                    } else {
                        fileContent += ",\n";
                    }
                    fileContent += gson.toJson(ic);
                }
            }
            fileContent += "]";

            // Let user pick a location/name to save the file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save your Infrastructure Design File");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File savePath = fileChooser.getSelectedFile();

                try {
                    FileWriter file = new FileWriter(savePath.getAbsolutePath());

                    // Save json components array to a file
                    file.write(fileContent);
                    file.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            // If the open file button was clicked
        } else if(e.getSource() == jbOpen) {
            // Configure gson
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            // Let user pick a file to open
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Infrastructure Design File");
            int option = fileChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                JsonArray array = null;
                try {
                    // Create file reader
                    Scanner reader = new Scanner(file);
                    JsonParser parser = new JsonParser();

                    // Convert file to a json array
                    array = (JsonArray) parser.parse(new FileReader(file.getAbsolutePath()));
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }

                // Clear the panel
                designPanel.removeAll();

                try {
                    // Loop over the json array to retrieve the infrastructure components
                    for (Object object : array) {
                        JsonObject jsonObject = (JsonObject) object;

                        // Convert json component values to usable variables
                        String name = jsonObject.get("name").getAsString();
                        String type = jsonObject.get("type").getAsString();
                        double availability = jsonObject.get("availability").getAsDouble();
                        double annualPrice = jsonObject.get("annualPrice").getAsDouble();
                        int panelX = jsonObject.get("panelX").getAsInt();
                        int panelY = jsonObject.get("panelY").getAsInt();

                        // Create infrastructure components
                        if (type.equals("Firewall")) {
                            Firewall fw = new Firewall(designPanel, name, availability, annualPrice, panelX, panelY);
                            this.firewall = fw;
                            designPanel.add(fw);
                        } else if (type.equals("Database Server")) {
                            DatabaseServer dbs = new DatabaseServer(designPanel, name, availability, annualPrice, panelX, panelY);
                            designPanel.add(dbs);
                        } else if (type.equals("Web Server")) {
                            WebServer ws = new WebServer(designPanel, name, availability, annualPrice, panelX, panelY);
                            designPanel.add(ws);
                        } else {
                            System.err.println("The json object has an invalid type: " + type);
                        }
                    }

                } catch (Exception ex){
                    System.err.println(ex.getMessage());
                    showMessageDialog(this, "The file you are trying to open is incompatible " +
                            "with this design tool", "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Refresh designPanel
                designPanel.repaint();
            }
        // If an item from the JComboBox databaseservers was picked
        } else if(e.getSource() == jcDatabaseservers){
            // Search for selected item in webserver dropdown menu
            for(InfrastructureComponent dbs : databaseServers){
                if(dbs.toString().equals(String.valueOf(jcDatabaseservers.getSelectedItem()))){
                    // Add selected item to the design
                    DatabaseServer dbServer = new DatabaseServer(dbs.getParentPanel(), dbs.getComponentName(), dbs.getAvailability(), dbs.getAnnualPrice());
                    designPanel.add(dbServer);
                    break;
                }
            }
            // Make dropdown show title
            jcDatabaseservers.setSelectedIndex(-1);

            // If an item from the JComboBox webservers was picked
        } else if(e.getSource() == jcWebservers){
            // Search for selected item in webserver dropdown menu
            for(InfrastructureComponent ws : webServers){
                if(ws.toString().equals(String.valueOf(jcWebservers.getSelectedItem()))){
                    // Add selected item to the design
                    WebServer webServer = new WebServer(ws.getParentPanel(), ws.getComponentName(), ws.getAvailability(), ws.getAnnualPrice());
                    designPanel.add(webServer);
                    break;
                }
            }
            // Make dropdown show title
            jcWebservers.setSelectedIndex(-1);
        // If the custom component button was clicked
        } else if(e.getSource() == jbCustomComponent){
            CustomComponentDialog dialog = new CustomComponentDialog(this);
            if(dialog.isOk()){
                String type = dialog.getComponentType();
                String name = dialog.getComponentName();
                double price = dialog.getPrice();
                double availability = dialog.getAvailability();

                if(type.equals("Database Server")){
                    DatabaseServer dbs = new DatabaseServer(designPanel, name, availability, price);
                    designPanel.add(dbs);
                    databaseServers.add(dbs);
                    jcDatabaseservers.addItem(dbs);
                } else if(type.equals("Web Server")){
                    WebServer ws = new WebServer(designPanel, name, availability, price);
                    designPanel.add(ws);
                    webServers.add(ws);
                    jcWebservers.addItem(ws);
                }
            }
        // If the optimize button was clicked
        } else if(e.getSource() == jbOptimize){
            OptimizationDialog dialog = new OptimizationDialog(this);
            if(dialog.isOk()){
                double desiredAvailability = dialog.getDesiredAvailability();
                if(dialog.isCustomServerLimitEnabled()){
                    this.maximumServerCount = dialog.getCustomServerLimit();
                } else {
                    this.maximumServerCount = OptimizationDialog.getDefaultServerLimit();
                }

                optimize(desiredAvailability/100);
            }
        } else if(e.getSource() == jbOptimizeCurrentDesign){
            if(!designPanel.hasWebServer()){
                showMessageDialog(this, "Add a web server before optimizing your current design!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if(!designPanel.hasDatabaseServer()){
                showMessageDialog(this, "Add a database server before optimizing your current design!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                this.maximumServerCount = OptimizationDialog.getDefaultServerLimit();
                optimize(Double.parseDouble(designPanel.calculateTotalAvailability())/100);
            }
        }

        designPanel.repaint();
    }

    // Resize designPanel when this frame is minimized or maximized
    @Override
    public void windowStateChanged(WindowEvent e) {
        designPanel.setResponsiveSize();
    }

    public void optimize(double desiredAvailability){
        long startTime = System.nanoTime();
        resetOptimizationValues();
        this.desiredAvailability = desiredAvailability;

        for(WebServer w: webServers){
            webServerAvailabilityPerKind = addDoubleElement(webServerAvailabilityPerKind, w.getAvailability()/100);
            webServerCostPerKind = addDoubleElement(webServerCostPerKind, w.getAnnualPrice());
            webServerCountPerKind = addIntElement(webServerCountPerKind, 0);
            totalAmountOfWebServers++;
        }

        for(DatabaseServer d: databaseServers){
            databaseServerAvailabilityPerKind = addDoubleElement(databaseServerAvailabilityPerKind, d.getAvailability()/100);
            databaseServerCostPerKind = addDoubleElement(databaseServerCostPerKind, d.getAnnualPrice());
            databaseServerCountPerKind = addIntElement(databaseServerCountPerKind, 0);
            totalAmountOfDatabaseServers++;
        }

        LoopWB(0,0);
        System.out.println(totalSetupCounter + " setups considered | Cost: " + minimalCost + " | Setup: " + serverSetup);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime); // in nanoseconds

        //microseconds = duration/1000
        //milliseconds = duration/1000000
        System.out.println("duration: " + duration/1000000 + " milliseconds");

        buildOptimizedDesign();
    }

    private int LoopWB(int totalWebServers, int currentWebServer){
        int count = 0;
        while(count < maximumServerCount - totalWebServers){
            webServerCountPerKind[currentWebServer] = count;
            if(currentWebServer < totalAmountOfWebServers) {
                LoopWB(count+totalWebServers, currentWebServer+1);
            }
            if(currentWebServer == totalAmountOfWebServers){
                LoopDB(0,0);
            }

            count++;
        }
        return currentWebServer;
    }

    private int LoopDB(int totalDatabaseServers, int currentDatabaseServer){
        int count = 0;
        while(count < maximumServerCount - totalDatabaseServers){
            databaseServerCountPerKind[currentDatabaseServer] = count;
            count++;
            if(currentDatabaseServer < totalAmountOfDatabaseServers) {
                LoopDB(count+totalDatabaseServers, currentDatabaseServer+1);
            }

            if(currentDatabaseServer == totalAmountOfDatabaseServers) {
                /* LOGGING
                System.out.print(totalSetupCounter + " Wb: ");
                for(int i = 0; i< webServerCountPerKind.length; i++){
                    if(i==0){
                        System.out.print(webServerCountPerKind[i]);
                    } else {
                        System.out.print("-" + webServerCountPerKind[i]);
                    }
                }
                System.out.print(" | Db: ");
                for(int i = 0; i< databaseServerCountPerKind.length; i++){
                    if(i==0){
                        System.out.print(databaseServerCountPerKind[i]);
                    } else {
                        System.out.print("-" + databaseServerCountPerKind[i]);
                    }
                }
                System.out.println(" -> Besch: " + BerekenBeschikbaarheid() + " | Kost: " + Berekenkosten() + " | Beste: Kost: " + minimalCost + " | Set: " + serverSetup);
                */

                totalSetupCounter++;
                double setupAvailability = calculateAvailabilityOfCurrentSetup();
                double setupCost = calculateCostOfCurrentSetup();

                if (setupAvailability > desiredAvailability) {
                    if (setupCost < minimalCost) {
                        minimalCost = setupCost;
                        optimalWebServerSetup = new int[]{};
                        optimalDatabaseServerSetup = new int[]{};
                        serverSetup = "Fw: 1 | Wb: ";
                        for(int i = 0; i< webServerCountPerKind.length; i++){
                            if(i==0){
                                serverSetup += webServerCountPerKind[i];
                            } else {
                                serverSetup += "-" + webServerCountPerKind[i];
                            }
                            optimalWebServerSetup = addIntElement(optimalWebServerSetup, webServerCountPerKind[i]);
                        }
                        serverSetup += " | Db: ";
                        for(int i = 0; i< databaseServerCountPerKind.length; i++){
                            if(i==0){
                                serverSetup += databaseServerCountPerKind[i];
                            } else {
                                serverSetup += "-" + databaseServerCountPerKind[i];
                            }
                            optimalDatabaseServerSetup = addIntElement(optimalDatabaseServerSetup, databaseServerCountPerKind[i]);
                        }
                    }
                    return currentDatabaseServer;
                }
            }
        }
        return currentDatabaseServer;
    }

    private double calculateAvailabilityOfCurrentSetup(){
        double availabilityFW = 1 - Math.pow((1 - firewall.getAvailability() / 100), 1);
        double availabilityWB = 1;
        double availabilityDB = 1;

        for(int i = 0; i< webServerCountPerKind.length; i++){
            availabilityWB *= Math.pow((1 - webServerAvailabilityPerKind[i]), webServerCountPerKind[i]);
        }
        availabilityWB = 1-availabilityWB;

        for(int i = 0; i< databaseServerCountPerKind.length; i++){
            availabilityDB *= Math.pow((1 - databaseServerAvailabilityPerKind[i]), databaseServerCountPerKind[i]);
        }
        availabilityDB = 1-availabilityDB;

        double totalAvailability = availabilityFW * availabilityDB * availabilityWB;
        return totalAvailability;
    }

    private double calculateCostOfCurrentSetup(){
        double costFW = firewall.getAnnualPrice();

        double costDB = 0;
        for(int i = 0; i< databaseServerCountPerKind.length; i++){
            costDB += (databaseServerCountPerKind[i] * databaseServerCostPerKind[i]);
        }

        double costWB = 0;
        for(int i = 0; i< webServerCountPerKind.length; i++){
            costWB += (webServerCountPerKind[i] * webServerCostPerKind[i]);
        }

        double totalCost = costFW + costDB + costWB;
        return totalCost;
    }

    static double[] addDoubleElement(double[] a, double e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    static int[] addIntElement(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    private void resetOptimizationValues(){
        webServerCountPerKind = new int[]{};
        databaseServerCountPerKind = new int[]{};
        webServerAvailabilityPerKind = new double[]{};
        webServerCostPerKind = new double[]{};
        databaseServerAvailabilityPerKind = new double[]{};
        databaseServerCostPerKind = new double[]{};
        totalAmountOfWebServers = -1;
        totalAmountOfDatabaseServers = -1;
        minimalCost = Double.MAX_VALUE;
        totalSetupCounter = 0;
    }

    private void buildOptimizedDesign(){
        designPanel.removeAll();

        // Add Firewall
        firewall = new Firewall(firewall.getParentPanel(), firewall.getComponentName(), firewall.getAvailability(), firewall.getAnnualPrice(),
                designPanel.getWidth()/2, designPanel.getHeight()/2);
        designPanel.add(firewall);

        // Add web servers
        int count = 0;
        for(int i=0; i<optimalWebServerSetup.length; i++){
            for(int j=0; j<optimalWebServerSetup[i]; j++){
                WebServer ws = webServers.get(i);
                WebServer wsClone = new WebServer(ws.getParentPanel(), ws.getComponentName(), ws.getAvailability(), ws.getAnnualPrice(),
                        designPanel.getWidth()/4, 110*count++);
                designPanel.add(wsClone);
            }
        }

        // Add database servers
        count = 0;
        for(int i=0; i<optimalDatabaseServerSetup.length; i++){
            for(int j=0; j<optimalDatabaseServerSetup[i]; j++){
                DatabaseServer dbs = databaseServers.get(i);
                DatabaseServer dbsClone = new DatabaseServer(dbs.getParentPanel(), dbs.getComponentName(), dbs.getAvailability(), dbs.getAnnualPrice(),
                        designPanel.getWidth()/4*3, 110*count++);
                designPanel.add(dbsClone);
            }
        }
    }
}
