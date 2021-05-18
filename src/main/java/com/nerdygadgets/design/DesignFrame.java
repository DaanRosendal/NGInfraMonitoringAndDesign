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
    private JButton jbSave, jbOpen, jbCustomComponent, jbOptimize;
    private JComboBox jcWebservers, jcDatabaseservers;
    private ArrayList<WebServer> webServers;
    private ArrayList<DatabaseServer> databaseServers;
    private DesignPanel designPanel;
    private Firewall firewall;

    // For optimization
    private static int maxLoop = 10;
    private static int[] wbAantal = {};
    private static int[] dbAantal = {};
    private static double[] wbBeschikbaarheid = {};
    private static double[] wbKosten = {};
    private static double[] dbBeschikbaarheid = {};
    private static double[] dbKosten = {};
    private static int maxWBSrt = -1;
    private static int maxDBSrt = -1;
    private static double minBeschikbaarheid = 0.9991;
    private static double minKosten = Double.MAX_VALUE;
    private static int totaalTeller = 0;
    private static String serverset;

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

        firewall = new Firewall(designPanel, "pfSense", 99.998, 4000);
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
        } else if(e.getSource() == jbOptimize){
            optimize(0);
        }

        designPanel.repaint();
    }

    // Resize designPanel when this frame is minimized or maximized
    @Override
    public void windowStateChanged(WindowEvent e) {
        designPanel.setResponsiveSize();
    }

    public void optimize(double desiredAvailability){
        resetOptimizationValues();

        for(WebServer w: webServers){
            wbBeschikbaarheid = addDoubleElement(wbBeschikbaarheid, w.getAvailability()/100);
            wbKosten = addDoubleElement(wbKosten, w.getAnnualPrice());
            wbAantal = addIntElement(wbAantal, 0);
            maxWBSrt++;
        }
        System.out.println(Arrays.toString(wbBeschikbaarheid));

        for(DatabaseServer d: databaseServers){
            dbBeschikbaarheid = addDoubleElement(dbBeschikbaarheid, d.getAvailability()/100);
            dbKosten = addDoubleElement(dbKosten, d.getAnnualPrice());
            dbAantal = addIntElement(dbAantal, 0);
            maxDBSrt++;
        }
        System.out.println(Arrays.toString(dbBeschikbaarheid));


        LoopWB(0,0);
        System.out.println(totaalTeller + " combinaties onderzocht " + minKosten + " - " + serverset);
    }

    private int LoopWB(int totaalSrvrWB,int srvnr){
        int teller = 0;
        while(teller<maxLoop-totaalSrvrWB){
            wbAantal[srvnr]= teller;
            if (srvnr<maxWBSrt) {
                LoopWB(teller+totaalSrvrWB,srvnr+1);
            }
            if(srvnr==maxWBSrt){
                LoopDB(0,0);
            }

            teller++;
        }
        return srvnr;
    }

    private int LoopDB(int totaalSrvrDB, int srvnr){
        int teller = 0;
        while(teller<maxLoop-totaalSrvrDB){
            dbAantal[srvnr]= teller;
            teller++;
            if (srvnr<maxDBSrt) {
                LoopDB(teller+totaalSrvrDB,srvnr+1);
            }

            if(srvnr==maxDBSrt) {
                totaalTeller++;
                System.out.print(totaalTeller + " Wb: ");
                for(int i=0; i<wbAantal.length; i++){
                    if(i==0){
                        System.out.print(wbAantal[i]);
                    } else {
                        System.out.print("-" + wbAantal[i]);
                    }
                }
                System.out.print(" | Db: ");
                for(int i=0; i<dbAantal.length; i++){
                    if(i==0){
                        System.out.print(dbAantal[i]);
                    } else {
                        System.out.print("-" + dbAantal[i]);
                    }
                }
                System.out.println(" -> Besch: " + BerekenBeschikbaarheid() + " | Kost: " + Berekenkosten() + " | Beste: Kost: " + minKosten + " | Set: " + serverset);

                double berekendeBeschikbaarheid = BerekenBeschikbaarheid();
                double berekendeKosten = Berekenkosten();

                if (berekendeBeschikbaarheid > minBeschikbaarheid) {
                    if (berekendeKosten < minKosten) {
                        minKosten = berekendeKosten;
                        serverset = "Fw: 1 | Wb: ";
                        for(int i=0; i<wbAantal.length; i++){
                            if(i==0){
                                serverset += wbAantal[i];
                            } else {
                                serverset += "-" + wbAantal[i];
                            }
                        }
                        serverset += " | Db: ";
                        for(int i=0; i<dbAantal.length; i++){
                            if(i==0){
                                serverset += dbAantal[i];
                            } else {
                                serverset += "-" + dbAantal[i];
                            }
                        }
                    }
                    return srvnr;
                }
            }
        }
        return srvnr;
    }

    private double BerekenBeschikbaarheid(){
        double beschikbaarheidFW = 1 - Math.pow((1 - firewall.getAvailability() / 100), 1);
        double beschikbaarheidWB = 1;
        double beschikbaarheidDB = 1;

        beschikbaarheidFW = 1-beschikbaarheidFW;

        for(int i=0; i<wbAantal.length; i++){
            beschikbaarheidWB *= Math.pow((1 - wbBeschikbaarheid[i]), wbAantal[i]);
        }
        beschikbaarheidWB = 1-beschikbaarheidWB;

        for(int i=0; i<dbAantal.length; i++){
            beschikbaarheidDB *= Math.pow((1 - dbBeschikbaarheid[i]), dbAantal[i]);
        }
        beschikbaarheidDB = 1-beschikbaarheidDB;

        double totaleBeschikbaarheid = beschikbaarheidFW * beschikbaarheidDB * beschikbaarheidWB;
        return totaleBeschikbaarheid;

//        double beschikbaarheidFW = 1 - Math.pow((1 - 0.99998), 1);
//        double beschikbaarheidDB = 1 - Math.pow((1 - dbBeschikbaarheid[0]), dbAantal[0]) * Math.pow((1 - dbBeschikbaarheid[1]), dbAantal[1]) * Math.pow((1 - dbBeschikbaarheid[2]), dbAantal[2]);
//        double beschikbaarheidWB = 1 - Math.pow((1 - wbBeschikbaarheid[0]), wbAantal[0]) * Math.pow((1 - wbBeschikbaarheid[1]), wbAantal[1]) * Math.pow((1 - wbBeschikbaarheid[2]), wbAantal[2]);
//        double totaleBeschikbaarheid = beschikbaarheidFW * beschikbaarheidDB * beschikbaarheidWB;
//
//        return totaleBeschikbaarheid;
    }

    private double Berekenkosten(){
        double kostenFW = firewall.getAnnualPrice();

        double kostenDB = 0;
        for(int i=0; i<dbAantal.length; i++){
            kostenDB += (dbAantal[i] * dbKosten[i]);
        }

        double kostenWB = 0;
        for(int i=0; i<wbAantal.length; i++){
            kostenWB += (wbAantal[i] * wbKosten[i]);
        }

        double totalekosten = kostenFW + kostenDB + kostenWB;
        return totalekosten;
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
        wbAantal = new int[]{};
        dbAantal = new int[]{};
        wbBeschikbaarheid = new double[]{};
        wbKosten = new double[]{};
        dbBeschikbaarheid = new double[]{};
        dbKosten = new double[]{};
        maxWBSrt = -1;
        maxDBSrt = -1;
        minKosten = Double.MAX_VALUE;
        totaalTeller = 0;
    }
}
