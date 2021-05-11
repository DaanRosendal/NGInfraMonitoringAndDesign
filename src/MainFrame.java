import com.nerdygadgets.design.DesignFrame;
import com.nerdygadgets.monitoring.MonitoringFrame;
import com.nerdygadgets.monitoring.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements ActionListener {
    private JButton jbMonitoring, jbDesign;

    public MainFrame() {
        setTitle("NerdyGadgets Infra Monitoring & Design");
        setSize(600, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        jbMonitoring = new JButton("Monitoring");
        jbMonitoring.addActionListener(this);
        add(jbMonitoring);

        jbDesign = new JButton("Design");
        jbDesign.addActionListener(this);
        add(jbDesign);

        setVisible(true);
        setLocationRelativeTo(null); // centers the frame
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbMonitoring) {
            // Add dummy servers
            MonitoringFrame mf = new MonitoringFrame(new Server("WS1", "192.168.2.2"), new Server("WS2", "192.168.2.3"), new Server("DB1", "192.168.3.2"), new Server("DB2", "192.168.3.3"), new Server("DB3", "192.168.3.4"));
        } else if (e.getSource() == jbDesign) {
            DesignFrame df = new DesignFrame();
        }
    }
}