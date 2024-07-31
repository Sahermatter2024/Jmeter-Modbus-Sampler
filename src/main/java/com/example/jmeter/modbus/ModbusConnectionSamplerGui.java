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

import org.apache.jmeter.gui.util.VerticalPanel;
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

public class ModbusConnectionSamplerGui extends AbstractSamplerGui {
    private static final Logger log = LogManager.getLogger(ModbusConnectionSamplerGui.class);

    private JTextField ipAddressField;
    private JTextField portField;
    private JTextField timeoutField;
    private JTextField keepAliveField;
    private JTextField retryCountField;

    public ModbusConnectionSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());

        ipAddressField = new JTextField(20);
        portField = new JTextField(5);
        timeoutField = new JTextField(10);
        keepAliveField = new JTextField(5);
        retryCountField = new JTextField(5);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        fieldsPanel.add(new JLabel("IP Address:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(ipAddressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Timeout (ms):"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(timeoutField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        fieldsPanel.add(new JLabel("Keep Alive (ms, 0=forever):"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(keepAliveField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        fieldsPanel.add(new JLabel("Retry Count:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(retryCountField, gbc);

        mainPanel.add(fieldsPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Add developer information and hyperlink
        JPanel footerPanel = getFooterPanel();

        add(footerPanel, BorderLayout.SOUTH);
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
                    log.error("Error opening link", ex);
                }
            }
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(developerLabel);
        footerPanel.add(githubLink);
        return footerPanel;
    }

    @Override
    public String getLabelResource() {
        return "modbusConnectionSampler_title";
    }

    @Override
    public String getStaticLabel() {
        return "Modbus Connection Sampler";
    }

    @Override
    public TestElement createTestElement() {
        ModbusConnectionSampler sampler = new ModbusConnectionSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        if (element instanceof ModbusConnectionSampler) {
            ModbusConnectionSampler sampler = (ModbusConnectionSampler) element;
            sampler.setIpAddress(ipAddressField.getText());
            sampler.setPort(portField.getText());
            sampler.setTimeout(timeoutField.getText().isEmpty() ? "2000" : timeoutField.getText());
            sampler.setKeepAlive(keepAliveField.getText().isEmpty() ? "0" : keepAliveField.getText());
            sampler.setRetryCount(retryCountField.getText().isEmpty() ? "3" : retryCountField.getText());
        }
        super.configureTestElement(element);
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof ModbusConnectionSampler) {
            ModbusConnectionSampler sampler = (ModbusConnectionSampler) element;
            ipAddressField.setText(sampler.getIpAddress());
            portField.setText(sampler.getPort());
            timeoutField.setText(sampler.getTimeout());
            keepAliveField.setText(sampler.getKeepAlive());
            retryCountField.setText(sampler.getRetryCount());
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        ipAddressField.setText("");
        portField.setText("");
        timeoutField.setText("");
        keepAliveField.setText("");
        retryCountField.setText("");
    }
}
