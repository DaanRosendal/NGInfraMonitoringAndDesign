package com.nerdygadgets.design;

import com.nerdygadgets.design.components.DatabaseServer;
import com.nerdygadgets.design.components.Firewall;
import com.nerdygadgets.design.components.InfrastructureComponent;
import com.nerdygadgets.design.components.WebServer;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class DesignFrame extends JFrame implements ActionListener, WindowStateListener {
    private JButton jbSave;
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

        designPanel = new DesignPanel(this);



        WebServer w1 = new WebServer(designPanel, "HAL9001W", 80, 2200);
        WebServer w2 = new WebServer(designPanel, "HAL9002W", 90, 3200);
        WebServer w3 = new WebServer(designPanel, "HAL9003W", 95, 5100);
        webservers = new InfrastructureComponent[]{w1, w2, w3};
        jcWebservers = new JComboBox(webservers);
        jcWebservers.setRenderer(new MyComboBoxRenderer("Webserver"));
        jcWebservers.setSelectedIndex(-1);
        add(jcWebservers);
        jcWebservers.addActionListener(this);

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

        addWindowStateListener(this);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == jbSave){
            ArrayList<InfrastructureComponent> components = designPanel.getInfrastructureComponents();

            // TODO Create json file

            // TODO Save file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save your Infrastructure Design File");

            int userSelection = fileChooser.showSaveDialog(this);
            
            if(userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            }
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
            jcDatabaseservers.setSelectedIndex(-1);
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
            jcWebservers.setSelectedIndex(-1);
        }
        designPanel.repaint();
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        System.out.println("a");
        designPanel.setResponsiveSize();
    }
}
