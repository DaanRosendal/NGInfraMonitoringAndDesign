package com.nerdygadgets.design;

import com.google.gson.*;
import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.Firewall;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class DesignFrame extends JFrame implements ActionListener, WindowStateListener {
    private JButton jbSave, jbOpen;
    private JComboBox jcWebservers, jcDatabaseservers;
    private InfrastructureComponent[] webservers, databaseservers;
    private DesignPanel designPanel;

    public DesignFrame() {
        setTitle("NerdyGadgets Infrastructure Design Tool");
        setSize(800, 600);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        jbSave = new JButton("Save File");
        jbSave.addActionListener(this);
        add(jbSave);

        jbOpen = new JButton("Open File");
        jbOpen.addActionListener(this);
        add(jbOpen);

        designPanel = new DesignPanel(this);

        Firewall fw = new Firewall(designPanel, "pfSense", 99.998, 4000);
        designPanel.add(fw);

        // Create JComboBox for webservers
        WebServer w1 = new WebServer(designPanel, "HAL9001W", 80, 2200);
        WebServer w2 = new WebServer(designPanel, "HAL9002W", 90, 3200);
        WebServer w3 = new WebServer(designPanel, "HAL9003W", 95, 5100);
        webservers = new InfrastructureComponent[]{w1, w2, w3};
        jcWebservers = new JComboBox(webservers);
        jcWebservers.setRenderer(new MyComboBoxRenderer("Webserver"));
        jcWebservers.setSelectedIndex(-1);
        add(jcWebservers);
        jcWebservers.addActionListener(this);

        // Create JComboBox for databaseservers
        DatabaseServer db1 = new DatabaseServer(designPanel, "HAL9001DB", 90, 5100);
        DatabaseServer db2 = new DatabaseServer(designPanel, "HAL9002DB", 95, 7700);
        DatabaseServer db3 = new DatabaseServer(designPanel, "HAL9003DB", 98, 12200);
        databaseservers = new InfrastructureComponent[]{db1, db2, db3};
        jcDatabaseservers = new JComboBox(databaseservers);
        jcDatabaseservers.setRenderer(new MyComboBoxRenderer("Databaseserver"));
        jcDatabaseservers.setSelectedIndex(-1);
        add(jcDatabaseservers);
        jcDatabaseservers.addActionListener(this);

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
                try {
                    // Create file reader
                    Scanner reader = new Scanner(file);
                    JsonParser parser = new JsonParser();

                    // Convert file to a json array
                    JsonArray a = (JsonArray) parser.parse(new FileReader(file.getAbsolutePath()));

                    // Clear the panel
                    designPanel.removeAll();

                    // Loop over the json array to retrieve the infrastructure components
                    for(Object o : a){
                        JsonObject jo = (JsonObject) o;

                        // Convert json component values to usable variables
                        String name = jo.get("name").getAsString();
                        String type = jo.get("type").getAsString();
                        double availability = jo.get("availability").getAsDouble();
                        double annualPrice = jo.get("annualPrice").getAsDouble();
                        int panelX = jo.get("panelX").getAsInt();
                        int panelY = jo.get("panelY").getAsInt();

                        // Create infrastructure components
                        if(type.equals("firewall")){
                            Firewall fw = new Firewall(designPanel, name, availability, annualPrice, panelX, panelY);
                            designPanel.add(fw);
                        } else if(type.equals("databaseserver")){
                            DatabaseServer dbs = new DatabaseServer(designPanel, name, availability, annualPrice, panelX, panelY);
                            designPanel.add(dbs);
                        } else if(type.equals("webserver")){
                            WebServer ws = new WebServer(designPanel, name, availability, annualPrice, panelX, panelY);
                            designPanel.add(ws);
                        } else {
                            System.err.println("The json object has an invalid type: " + type);
                        }
                    }

                    // Refresh designPanel
                    designPanel.repaint();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }

        // If an item from the JComboBox databaseservers was picked
        } else if(e.getSource() == jcDatabaseservers){
            // Search for selected item in webserver dropdown menu
            for(InfrastructureComponent dbs : databaseservers){
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
            for(InfrastructureComponent ws : webservers){
                if(ws.toString().equals(String.valueOf(jcWebservers.getSelectedItem()))){
                    // Add selected item to the design
                    WebServer webServer = new WebServer(ws.getParentPanel(), ws.getComponentName(), ws.getAvailability(), ws.getAnnualPrice());
                    designPanel.add(webServer);
                    break;
                }
            }
            // Make dropdown show title
            jcWebservers.setSelectedIndex(-1);
        }

        designPanel.repaint();
    }

    // Resize designPanel when this frame is minimized or maximized
    @Override
    public void windowStateChanged(WindowEvent e) {
        designPanel.setResponsiveSize();
    }
}
