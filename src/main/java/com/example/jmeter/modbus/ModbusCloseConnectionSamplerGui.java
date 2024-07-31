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

public class ModbusCloseConnectionSamplerGui extends AbstractSamplerGui {
    private static final Logger log = LogManager.getLogger(ModbusCloseConnectionSamplerGui.class);

    public ModbusCloseConnectionSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        JPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());

        mainPanel.add(new JLabel("This sampler will close the Modbus connection."));

        add(mainPanel, BorderLayout.CENTER);

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
        return "modbusCloseConnectionSampler_title";
    }

    @Override
    public String getStaticLabel() {
        return "Modbus Close Connection Sampler";
    }

    @Override
    public TestElement createTestElement() {
        ModbusCloseConnectionSampler sampler = new ModbusCloseConnectionSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        if (element instanceof ModbusCloseConnectionSampler) {
            // No fields to set in this example
        }
        super.configureTestElement(element);
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        // No fields to configure in this example
    }

    @Override
    public void clearGui() {
        super.clearGui();
    }
}
