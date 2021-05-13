package com.nerdygadgets.design.components;

import com.nerdygadgets.design.MyComboBoxRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.JOptionPane.showMessageDialog;


public class CustomComponentDialog extends JDialog implements ActionListener {
    private JLabel jlName, jlPrice, jlAvailability, jlType;
    private JTextField jtfName, jtfPrice, jtfAvailability;
    private JComboBox<String> jcTypes;
    private JButton jbOk, jbCancel;
    private boolean isOk;
    private double price, availability;

    public CustomComponentDialog(JFrame frame){
        super(frame,true);
        setTitle("Custom Component");
        setSize(350, 200);
        setLayout(new GridLayout(5,2, 0, 10));

        jlName = new JLabel("Name: ");
        add(jlName);
        jtfName = new JTextField(20);
        add(jtfName);

        jlPrice = new JLabel("Annual Price (€): ");
        add(jlPrice);
        jtfPrice = new JTextField(20);
        add(jtfPrice);

        jlAvailability = new JLabel("Availability (%): ");
        add(jlAvailability);
        jtfAvailability = new JTextField(20);
        add(jtfAvailability);

        jlType = new JLabel("Component type: ");
        add(jlType);

        String type1 = "Database Server";
        String type2 = "Web Server";
        String[] types = new String[]{type1, type2};
        jcTypes = new JComboBox(types);
        jcTypes.setRenderer(new MyComboBoxRenderer("Choose type"));
        jcTypes.setSelectedIndex(-1);
        add(jcTypes);

        jbOk = new JButton("Create");
        jbOk.addActionListener(this);
        add(jbOk);

        jbCancel = new JButton("Cancel");
        jbCancel.addActionListener(this);
        add(jbCancel);

        setLocationRelativeTo(frame); // Centers the frame
        setVisible(true);
    }

    public String getComponentName() {
        return jtfName.getText();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAvailability() {
        return availability;
    }

    public void setAvailability(double availability) {
        this.availability = availability;
    }

    public String getComponentType(){
        return String.valueOf(jcTypes.getSelectedItem());
    }

    public boolean isOk() {
        return isOk;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbOk) {
            // Check if values are empty
            if(getComponentName().equals("")){
                showMessageDialog(this, "Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if(jtfPrice.getText().equals("")){
                showMessageDialog(this, "Price is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if(jtfAvailability.getText().equals("")){
                showMessageDialog(this, "Availability is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else if(jcTypes.getSelectedIndex() == -1){
                showMessageDialog(this, "Select a component type!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Convert price to a double with error handling
            try{
                setPrice(Double.parseDouble(jtfPrice.getText().replaceAll(",",".")));
            } catch (NumberFormatException nfe){
                showMessageDialog(this, "Invalid price!", "Error", JOptionPane.ERROR_MESSAGE);
                jtfPrice.setText("");
                return;
            }

            // Convert availability to a double with error handling
            try{
                setAvailability(Double.parseDouble(jtfAvailability.getText().replaceAll(",",".")));
            } catch (NumberFormatException nfe){
                showMessageDialog(this, "Invalid availability!", "Error", JOptionPane.ERROR_MESSAGE);
                jtfAvailability.setText("");
                return;
            }

            // Check if price and availability are reasonable values
            if(getPrice() < 0){
                showMessageDialog(this, "Price must be €0 or higher!", "Error", JOptionPane.ERROR_MESSAGE);
                jtfPrice.setText("");
                return;
            } else if(getAvailability() < 0 || getAvailability() > 100){
                showMessageDialog(this, "Availability must be between 0% and 100%!", "Error", JOptionPane.ERROR_MESSAGE);
                jtfAvailability.setText("");
                return;
            }

            isOk = true;
            setVisible(false);
        } else if (e.getSource() == jbCancel) {
            isOk = false;
            setVisible(false);
        }
    }
}
