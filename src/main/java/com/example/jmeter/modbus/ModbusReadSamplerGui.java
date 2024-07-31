/*
 * Copyright (c) 2024 Mohammed Hlayel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * This project includes the use of third-party libraries. For more information, please refer to the NOTICE file.
 */

package com.example.jmeter.modbus;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ModbusReadSamplerGui extends AbstractSamplerGui {

    private static final Logger logger = LogManager.getLogger(ModbusReadSamplerGui.class);

    private JCheckBox useExistingConnectionCheckbox;
    private JTextField ipAddressField;
    private JTextField portField;
    private JTextField addressField;
    private JTextField lengthField;
    private JComboBox<String> readMethodDropdown;
    private JComboBox<String> dataTypeDropdown;
    private JTextField keepAliveField;
    private JTextField retryCountField;
    private JTextField timeoutField;

    private Map<String, String[]> dataTypeToReadMethods;

    public ModbusReadSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel connectionPanel = new JPanel(new GridBagLayout());
        JPanel settingsPanel = new JPanel(new GridBagLayout());

        useExistingConnectionCheckbox = new JCheckBox("Use Existing Connection");
        useExistingConnectionCheckbox.addActionListener(e -> toggleConnectionFields());

        ipAddressField = new JTextField(15);
        portField = new JTextField(5);
        addressField = new JTextField(5);
        lengthField = new JTextField(5);

        readMethodDropdown = new JComboBox<>(new String[]{"Read Coils", "Read Input Discretes", "Read Holding Registers", "Read Input Registers"});
        dataTypeDropdown = new JComboBox<>(new String[]{"Integer", "Hexadecimal", "Float", "String", "Boolean"});
        dataTypeDropdown.addActionListener(e -> updateReadMethodDropdown());

        keepAliveField = new JTextField(5);
        retryCountField = new JTextField(5);
        timeoutField = new JTextField(5);

        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection Settings"));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Modbus Read Settings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.ipadx = 2; // Add internal padding
        gbc.weightx = 1.0;

        Dimension fieldDimension = new Dimension(200, 25); // Set preferred size for all text fields
        Dimension labelDimension = new Dimension(100, 25); // Set preferred size for all labels

        // Connection Settings
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel useExistingConnectionLabel = new JLabel("Use Existing Connection:");
        useExistingConnectionLabel.setPreferredSize(labelDimension);
        connectionPanel.add(useExistingConnectionLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        useExistingConnectionCheckbox.setPreferredSize(fieldDimension);
        connectionPanel.add(useExistingConnectionCheckbox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel ipAddressLabel = new JLabel("IP Address:");
        ipAddressLabel.setPreferredSize(labelDimension);
        connectionPanel.add(ipAddressLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        ipAddressField.setPreferredSize(fieldDimension);
        connectionPanel.add(ipAddressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel portLabel = new JLabel("Port:");
        portLabel.setPreferredSize(labelDimension);
        connectionPanel.add(portLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        portField.setPreferredSize(fieldDimension);
        connectionPanel.add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel keepAliveLabel = new JLabel("Keep Alive (ms, 0=forever):");
        keepAliveLabel.setPreferredSize(labelDimension);
        connectionPanel.add(keepAliveLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        keepAliveField.setPreferredSize(fieldDimension);
        connectionPanel.add(keepAliveField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel retryCountLabel = new JLabel("Retry Count:");
        retryCountLabel.setPreferredSize(labelDimension);
        connectionPanel.add(retryCountLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        retryCountField.setPreferredSize(fieldDimension);
        connectionPanel.add(retryCountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel timeoutLabel = new JLabel("Timeout (ms):");
        timeoutLabel.setPreferredSize(labelDimension);
        connectionPanel.add(timeoutLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        timeoutField.setPreferredSize(fieldDimension);
        connectionPanel.add(timeoutField, gbc);

        // Modbus Read Settings
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setPreferredSize(labelDimension);
        settingsPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        addressField.setPreferredSize(fieldDimension);
        settingsPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel lengthLabel = new JLabel("Length:");
        lengthLabel.setPreferredSize(labelDimension);
        settingsPanel.add(lengthLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        lengthField.setPreferredSize(fieldDimension);
        settingsPanel.add(lengthField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel dataTypeLabel = new JLabel("Data Type:");
        dataTypeLabel.setPreferredSize(labelDimension);
        settingsPanel.add(dataTypeLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        dataTypeDropdown.setPreferredSize(fieldDimension);
        settingsPanel.add(dataTypeDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel readMethodLabel = new JLabel("Read Method:");
        readMethodLabel.setPreferredSize(labelDimension);
        settingsPanel.add(readMethodLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        readMethodDropdown.setPreferredSize(fieldDimension);
        settingsPanel.add(readMethodDropdown, gbc);

        mainPanel.add(connectionPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(settingsPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Add developer information and hyperlink
        JPanel footerPanel = getFooterPanel();

        add(footerPanel, BorderLayout.SOUTH);

        toggleConnectionFields(); // Initial toggle based on checkbox state

        // Initialize data type to read methods map
        initDataTypeToReadMethodsMap();
    }

    private static @NotNull JPanel getFooterPanel() {
        JLabel developerLabel = new JLabel("<html><i>Developed by Mohammed Hlayel;</i></html>");
        JLabel githubLink = new JLabel("<html><a href='https://github.com/Sahermatter2024'>Help and Update</a></html>");
        githubLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Sahermatter2024"));
                } catch (Exception ex) {
                    logger.error("Error opening link", ex);
                }
            }
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(developerLabel);
        footerPanel.add(githubLink);
        return footerPanel;
    }

    private void initDataTypeToReadMethodsMap() {
        dataTypeToReadMethods = new HashMap<>();
        dataTypeToReadMethods.put("Integer", new String[]{"Read Holding Registers", "Read Input Registers"});
        dataTypeToReadMethods.put("Hexadecimal", new String[]{"Read Holding Registers", "Read Input Registers"});
        dataTypeToReadMethods.put("Float", new String[]{"Read Holding Registers", "Read Input Registers"});
        dataTypeToReadMethods.put("String", new String[]{"Read Holding Registers"});
        dataTypeToReadMethods.put("Boolean", new String[]{"Read Coils", "Read Input Discretes"});
    }

    private void updateReadMethodDropdown() {
        String selectedDataType = (String) dataTypeDropdown.getSelectedItem();
        String[] readMethods = dataTypeToReadMethods.getOrDefault(selectedDataType, new String[]{});
        String previousSelection = (String) readMethodDropdown.getSelectedItem();

        readMethodDropdown.removeAllItems();
        for (String method : readMethods) {
            readMethodDropdown.addItem(method);
        }

        if (previousSelection != null && java.util.Arrays.asList(readMethods).contains(previousSelection)) {
            readMethodDropdown.setSelectedItem(previousSelection);
        }
    }

    private void toggleConnectionFields() {
        boolean useExisting = useExistingConnectionCheckbox.isSelected();
        ipAddressField.setEnabled(!useExisting);
        portField.setEnabled(!useExisting);
        keepAliveField.setEnabled(!useExisting);
        retryCountField.setEnabled(!useExisting);
        timeoutField.setEnabled(!useExisting);
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        ModbusReadSampler sampler = (ModbusReadSampler) element;
        useExistingConnectionCheckbox.setSelected(sampler.getUseExistingConnection());
        ipAddressField.setText(sampler.getIpAddress());
        portField.setText(sampler.getPort());
        addressField.setText(sampler.getAddress());
        lengthField.setText(sampler.getLength());
        dataTypeDropdown.setSelectedItem(sampler.getDataType());
        // Update readMethodDropdown after setting dataTypeDropdown
        updateReadMethodDropdown();
        readMethodDropdown.setSelectedItem(sampler.getReadMethod());
        keepAliveField.setText(sampler.getKeepAlive());
        retryCountField.setText(sampler.getRetryCount());
        timeoutField.setText(sampler.getTimeout());
        toggleConnectionFields();
    }

    @Override
    public TestElement createTestElement() {
        ModbusReadSampler sampler = new ModbusReadSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        ModbusReadSampler sampler = (ModbusReadSampler) element;
        sampler.setUseExistingConnection(useExistingConnectionCheckbox.isSelected());
        sampler.setIpAddress(ipAddressField.getText());
        sampler.setPort(portField.getText());
        sampler.setAddress(addressField.getText());
        sampler.setLength(lengthField.getText());
        sampler.setReadMethod(readMethodDropdown.getSelectedItem() != null ? readMethodDropdown.getSelectedItem().toString() : "");
        sampler.setDataType(dataTypeDropdown.getSelectedItem() != null ? dataTypeDropdown.getSelectedItem().toString() : "");
        sampler.setKeepAlive(keepAliveField.getText());
        sampler.setRetryCount(retryCountField.getText());
        sampler.setTimeout(timeoutField.getText());
    }

    @Override
    public void clearGui() {
        super.clearGui();
        useExistingConnectionCheckbox.setSelected(false);
        ipAddressField.setText("");
        portField.setText("");
        addressField.setText("");
        lengthField.setText("");
        readMethodDropdown.setSelectedIndex(0);
        dataTypeDropdown.setSelectedIndex(0);
        keepAliveField.setText("");
        retryCountField.setText("");
        timeoutField.setText("");
        toggleConnectionFields();
    }

    @Override
    public String getLabelResource() {
        return "modbus_read_sampler_title";
    }

    @Override
    public String getStaticLabel() {
        return "Modbus Read Sampler";
    }
}
