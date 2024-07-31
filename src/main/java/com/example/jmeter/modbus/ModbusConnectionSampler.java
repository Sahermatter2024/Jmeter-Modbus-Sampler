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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

public class ModbusConnectionSampler extends AbstractSampler {
    private static final Logger log = LogManager.getLogger(ModbusConnectionSampler.class);

    public static final String IP_ADDRESS = "ModbusConnectionSampler.ipAddress";
    public static final String PORT = "ModbusConnectionSampler.port";
    public static final String TIMEOUT = "ModbusConnectionSampler.timeout";
    public static final String KEEP_ALIVE = "ModbusConnectionSampler.keepAlive";
    public static final String RETRY_COUNT = "ModbusConnectionSampler.retryCount";

    private ScheduledExecutorService scheduler;

    public void setIpAddress(String ipAddress) {
        setProperty(IP_ADDRESS, ipAddress);
    }

    public String getIpAddress() {
        return getPropertyAsString(IP_ADDRESS);
    }

    public void setPort(String port) {
        setProperty(PORT, port);
    }

    public String getPort() {
        return getPropertyAsString(PORT);
    }

    public void setTimeout(String timeout) {
        setProperty(TIMEOUT, timeout);
    }

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT);
    }

    public void setKeepAlive(String keepAlive) {
        setProperty(KEEP_ALIVE, keepAlive);
    }

    public String getKeepAlive() {
        return getPropertyAsString(KEEP_ALIVE);
    }

    public void setRetryCount(String retryCount) {
        setProperty(RETRY_COUNT, retryCount);
    }

    public String getRetryCount() {
        return getPropertyAsString(RETRY_COUNT);
    }

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();

        String ipAddress = getIpAddress();
        int port = Integer.parseInt(getPort());
        int timeout = Integer.parseInt(getTimeout());
        int keepAlive = Integer.parseInt(getKeepAlive());
        int retryCount = Integer.parseInt(getRetryCount());

        TCPMasterConnection connection = null;
        boolean success = false;

        try {
            scheduler = Executors.newScheduledThreadPool(1);

            for (int attempt = 0; attempt <= retryCount; attempt++) {
                try {
                    InetAddress address = InetAddress.getByName(ipAddress);
                    connection = new TCPMasterConnection(address);
                    connection.setPort(port);
                    connection.setTimeout(timeout);
                    connection.connect();

                    if (connection.isConnected()) {
                        log.info("Connected to Modbus server at {}:{}", ipAddress, port);
                        result.setResponseMessage("Connected to Modbus server.");
                        result.setSuccessful(true);
                        success = true;
                        break; // Exit the retry loop if connection is successful
                    }
                } catch (UnknownHostException ex) {
                    log.error("Unknown host: {}", ipAddress, ex);
                    result.setResponseMessage("Unknown host: " + ipAddress);
                    result.setSuccessful(false);
                    break;
                } catch (Exception ex) {
                    log.error("Attempt {} failed: ", attempt + 1, ex);
                    if (attempt >= retryCount) {
                        result.setResponseMessage("Error connecting to Modbus server after retries: " + ex.getMessage());
                        result.setSuccessful(false);
                    } else {
                        // Schedule the next retry
                        scheduler.schedule(() -> log.info("Retrying connection to Modbus server..."), 1, TimeUnit.SECONDS);
                    }
                }
            }

            // Store the connection in a JMeter variable if successful
            if (success) {
                JMeterContextService.getContext().getVariables().putObject("modbusConnection", connection);
                if (keepAlive > 0) {
                    log.info("Connection will be kept alive for {} milliseconds", keepAlive);
                    TCPMasterConnection finalConnection = connection;
                    scheduler.schedule(() -> {
                        finalConnection.close();
                        log.info("Connection closed after {} milliseconds", keepAlive);
                    }, keepAlive, TimeUnit.MILLISECONDS);
                }
            } else {
                if (connection != null && connection.isConnected()) {
                    connection.close();
                }
            }
        } catch (Exception ex) {
            log.error("Error during connection sampling", ex);
            result.setResponseMessage("Error during connection sampling: " + ex.getMessage());
            result.setSuccessful(false);
        } finally {
            result.sampleEnd();
            if (connection != null && connection.isConnected() && keepAlive < 0) {
                connection.close();
                log.info("Connection closed.");
            }
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        }

        return result;
    }
}
