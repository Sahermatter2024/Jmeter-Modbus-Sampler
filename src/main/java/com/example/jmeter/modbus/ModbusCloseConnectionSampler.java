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

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

public class ModbusCloseConnectionSampler extends AbstractSampler {
    private static final Logger log = LogManager.getLogger(ModbusCloseConnectionSampler.class);

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();

        try {
            TCPMasterConnection connection = (TCPMasterConnection) JMeterContextService.getContext().getVariables().getObject("modbusConnection");
            if (connection != null && connection.isConnected()) {
                connection.close();
                JMeterContextService.getContext().getVariables().remove("modbusConnection");
                log.info("Closed Modbus connection.");
                result.setResponseMessage("Closed Modbus connection.");
                result.setSuccessful(true);
            } else {
                log.warn("No active Modbus connection found.");
                result.setResponseMessage("No active Modbus connection found.");
                result.setSuccessful(false);
            }
        } catch (Exception ex) {
            log.error("Error closing Modbus connection", ex);
            result.setResponseMessage("Error closing Modbus connection: " + ex.getMessage());
            result.setSuccessful(false);
        } finally {
            result.sampleEnd();
        }

        return result;
    }
}
