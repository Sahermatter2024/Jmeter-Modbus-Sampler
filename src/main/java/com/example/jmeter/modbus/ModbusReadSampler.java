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
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadCoilsRequest;
import com.ghgande.j2mod.modbus.msg.ReadCoilsResponse;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputDiscretesResponse;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class ModbusReadSampler extends AbstractSampler {
    private static final Logger log = LogManager.getLogger(ModbusReadSampler.class);

    public static final String USE_EXISTING_CONNECTION = "ModbusReadSampler.useExistingConnection";
    public static final String IP_ADDRESS = "ModbusReadSampler.ipAddress";
    public static final String PORT = "ModbusReadSampler.port";
    public static final String ADDRESS = "ModbusReadSampler.address";
    public static final String LENGTH = "ModbusReadSampler.length";
    public static final String READ_METHOD = "ModbusReadSampler.readMethod";
    public static final String DATA_TYPE = "ModbusReadSampler.dataType";
    public static final String KEEP_ALIVE = "ModbusReadSampler.keepAlive";
    public static final String RETRY_COUNT = "ModbusReadSampler.retryCount";
    public static final String TIMEOUT = "ModbusReadSampler.timeout";

    private Timer connectionCloseTimer;

    public void setUseExistingConnection(boolean useExistingConnection) {
        setProperty(USE_EXISTING_CONNECTION, useExistingConnection);
    }

    public boolean getUseExistingConnection() {
        return getPropertyAsBoolean(USE_EXISTING_CONNECTION);
    }

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

    public void setAddress(String address) {
        setProperty(ADDRESS, address);
    }

    public String getAddress() {
        return getPropertyAsString(ADDRESS);
    }

    public void setLength(String length) {
        setProperty(LENGTH, length);
    }

    public String getLength() {
        return getPropertyAsString(LENGTH);
    }

    public void setReadMethod(String readMethod) {
        setProperty(READ_METHOD, readMethod);
    }

    public String getReadMethod() {
        return getPropertyAsString(READ_METHOD);
    }

    public void setDataType(String dataType) {
        setProperty(DATA_TYPE, dataType);
    }

    public String getDataType() {
        return getPropertyAsString(DATA_TYPE);
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

    public void setTimeout(String timeout) {
        setProperty(TIMEOUT, timeout);
    }

    public String getTimeout() {
        return getPropertyAsString(TIMEOUT, "2000");
    }

    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();

        TCPMasterConnection connection = null;
        String readMethod = getReadMethod();
        int retryCount = Integer.parseInt(getRetryCount());
        int keepAlive = Integer.parseInt(getKeepAlive());
        boolean success = false;

        try {
            for (int attempt = 0; attempt <= retryCount && !success; attempt++) {
                try {
                    if (getUseExistingConnection()) {
                        connection = (TCPMasterConnection) JMeterContextService.getContext().getVariables().getObject("modbusConnection");
                        if (connection == null || !connection.isConnected()) {
                            throw new IllegalStateException("No existing Modbus connection available.");
                        }
                    } else {
                        InetAddress address = InetAddress.getByName(getIpAddress());
                        connection = new TCPMasterConnection(address);
                        connection.setPort(Integer.parseInt(getPort()));
                        connection.setTimeout(Integer.parseInt(getTimeout()));
                        connection.connect();
                        JMeterContextService.getContext().getVariables().putObject("modbusConnection", connection);
                    }

                    if (getAddress().isEmpty() || getLength().isEmpty()) {
                        throw new IllegalArgumentException("Address and Length fields cannot be empty.");
                    }

                    int address = Integer.parseInt(getAddress());
                    int length = Integer.parseInt(getLength());
                    ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

                    switch (readMethod) {
                        case "Read Coils":
                            ReadCoilsRequest readCoilsRequest = new ReadCoilsRequest(address, length);
                            transaction.setRequest(readCoilsRequest);
                            transaction.execute();
                            ReadCoilsResponse readCoilsResponse = (ReadCoilsResponse) transaction.getResponse();
                            result.setResponseData(readCoilsResponse.getCoils().toString().getBytes());
                            break;
                        case "Read Input Discretes":
                            ReadInputDiscretesRequest readInputDiscretesRequest = new ReadInputDiscretesRequest(address, length);
                            transaction.setRequest(readInputDiscretesRequest);
                            transaction.execute();
                            ReadInputDiscretesResponse readInputDiscretesResponse = (ReadInputDiscretesResponse) transaction.getResponse();
                            result.setResponseData(readInputDiscretesResponse.getDiscretes().toString().getBytes());
                            break;
                        case "Read Holding Registers":
                            ReadMultipleRegistersRequest readMultipleRegistersRequest = new ReadMultipleRegistersRequest(address, length);
                            transaction.setRequest(readMultipleRegistersRequest);
                            transaction.execute();
                            ReadMultipleRegistersResponse readMultipleRegistersResponse = (ReadMultipleRegistersResponse) transaction.getResponse();
                            log.info("Reading Holding Registers: {}", (Object) readMultipleRegistersResponse.getRegisters());
                            result.setResponseData(convertValue(readMultipleRegistersResponse.getRegisters(), getDataType()).getBytes());
                            break;
                        case "Read Input Registers":
                            ReadInputRegistersRequest readInputRegistersRequest = new ReadInputRegistersRequest(address, length);
                            transaction.setRequest(readInputRegistersRequest);
                            transaction.execute();
                            ReadInputRegistersResponse readInputRegistersResponse = (ReadInputRegistersResponse) transaction.getResponse();
                            log.info("Reading Input Registers: {}", (Object) readInputRegistersResponse.getRegisters());
                            result.setResponseData(convertValue(readInputRegistersResponse.getRegisters(), getDataType()).getBytes());
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported read method: " + readMethod);
                    }

                    success = true; // If the operation is successful, exit the loop
                } catch (Exception ex) {
                    log.error("Attempt {} failed: ", attempt + 1, ex);
                    if (attempt >= retryCount) {
                        throw ex; // Rethrow the exception if all retries are exhausted
                    }
                }
            }

            result.setResponseMessage("Read operation successful.");
            result.setSuccessful(true);
        } catch (Exception ex) {
            log.error("Error during Modbus read operation", ex);
            result.setResponseMessage("Error: " + ex.getMessage());
            result.setSuccessful(false);
        } finally {
            result.sampleEnd();
            if (connection != null && !getUseExistingConnection()) {
                if (keepAlive > 0) {
                    scheduleConnectionClose(connection, keepAlive);
                } else if (keepAlive == 0) {
                    log.info("Keeping the connection open indefinitely.");
                } else {
                    connection.close();
                    log.info("Connection closed immediately.");
                }
            }
        }

        return result;
    }

    private void scheduleConnectionClose(TCPMasterConnection connection, int keepAlive) {
        if (connectionCloseTimer != null) {
            connectionCloseTimer.cancel();
        }
        connectionCloseTimer = new Timer();
        connectionCloseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                closeConnection(connection);
            }
        }, keepAlive);
    }

    private void closeConnection(TCPMasterConnection connection) {
        try {
            if (connection != null && connection.isConnected()) {
                connection.close();
                JMeterContext context = JMeterContextService.getContext();
                if (context != null && context.getVariables() != null) {
                    context.getVariables().remove("modbusConnection");
                }
                log.info("Connection closed after keep-alive duration.");
            }
        } catch (Exception ex) {
            log.error("Error closing connection", ex);
        }
    }

    private String convertValue(InputRegister[] registers, String dataType) {
        switch (dataType) {
            case "Hexadecimal":
                StringBuilder hexBuilder = new StringBuilder();
                for (InputRegister register : registers) {
                    hexBuilder.append(Integer.toHexString(register.getValue())).append(",");
                }
                return hexBuilder.toString().replaceAll(",$", "");
            case "Integer":
                StringBuilder intBuilder = new StringBuilder();
                for (InputRegister register : registers) {
                    intBuilder.append(register.getValue()).append(",");
                }
                return intBuilder.toString().replaceAll(",$", "");
            case "Float":
                StringBuilder floatBuilder = new StringBuilder();
                for (int i = 0; i < registers.length; i += 2) {
                    if (i + 1 < registers.length) {
                        int high = registers[i].getValue();
                        int low = registers[i + 1].getValue();
                        int bits = (high << 16) | (low & 0xFFFF);
                        float value = Float.intBitsToFloat(bits);
                        log.info("High: {}, Low: {}, Bits: {}, Float: {}", high, low, bits, value);
                        floatBuilder.append(value).append(",");
                    }
                }
                return floatBuilder.toString().replaceAll(",$", "");
            case "String":
                StringBuilder stringBuilder = new StringBuilder();
                for (InputRegister register : registers) {
                    stringBuilder.append((char) (register.getValue() & 0xFF)).append((char) ((register.getValue() >> 8) & 0xFF));
                }
                return stringBuilder.toString();
            case "Boolean":
                StringBuilder boolBuilder = new StringBuilder();
                for (InputRegister register : registers) {
                    boolBuilder.append(register.getValue() != 0).append(",");
                }
                return boolBuilder.toString().replaceAll(",$", "");
            default:
                throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
    }
}
