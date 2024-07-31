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
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteCoilRequest;
import com.ghgande.j2mod.modbus.msg.WriteMultipleCoilsRequest;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.BitVector;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class ModbusWriteSampler extends AbstractSampler {
    private static final Logger log = LogManager.getLogger(ModbusWriteSampler.class);

    public static final String USE_EXISTING_CONNECTION = "ModbusWriteSampler.useExistingConnection";
    public static final String IP_ADDRESS = "ModbusWriteSampler.ipAddress";
    public static final String PORT = "ModbusWriteSampler.port";
    public static final String ADDRESS = "ModbusWriteSampler.address";
    public static final String VALUE = "ModbusWriteSampler.value";
    public static final String LENGTH = "ModbusWriteSampler.length";
    public static final String WRITE_METHOD = "ModbusWriteSampler.writeMethod";
    public static final String DATA_TYPE = "ModbusWriteSampler.dataType";
    public static final String KEEP_ALIVE = "ModbusWriteSampler.keepAlive";
    public static final String RETRY_COUNT = "ModbusWriteSampler.retryCount";
    public static final String TIMEOUT = "ModbusWriteSampler.timeout";
    public static final String RESET_OLD_VALUES = "ModbusWriteSampler.resetOldValues";

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

    public void setValue(String value) {
        setProperty(VALUE, value);
    }

    public String getValue() {
        return getPropertyAsString(VALUE);
    }

    public void setLength(String length) {
        setProperty(LENGTH, length);
    }

    public String getLength() {
        return getPropertyAsString(LENGTH);
    }

    public void setWriteMethod(String writeMethod) {
        setProperty(WRITE_METHOD, writeMethod);
    }

    public String getWriteMethod() {
        return getPropertyAsString(WRITE_METHOD);
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

    public void setResetOldValues(boolean resetOldValues) {
        setProperty(RESET_OLD_VALUES, resetOldValues);
    }

    public boolean getResetOldValues() {
        return getPropertyAsBoolean(RESET_OLD_VALUES);
    }

    @Override
    public SampleResult sample(Entry e) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.sampleStart();

        TCPMasterConnection connection = null;
        String writeMethod = getWriteMethod();
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

                    if (getAddress().isEmpty() || getValue().isEmpty()) {
                        throw new IllegalArgumentException("Address and Value fields cannot be empty.");
                    }

                    int address = Integer.parseInt(getAddress());

                    if (getResetOldValues()) {
                        resetValues(connection, writeMethod, address, getDataType(), getLength());
                    }

                    switch (writeMethod) {
                        case "Single Register":
                            log.info("Writing Single Register with address {} and value {}", address, getValue());
                            writeSingleRegister(connection, address, parseValueAsInt(getValue(), getDataType()));
                            break;
                        case "Multiple Registers":
                            log.info("Writing Multiple Registers with values {}", getValue());
                            if (getDataType().equals("Float")) {
                                writeMultipleRegisters(connection, address, parseFloatArray(getValue()));
                            } else if (getDataType().equals("String")) {
                                writeMultipleRegisters(connection, address, convertStringToRegisters(getValue()));
                            } else {
                                writeMultipleRegisters(connection, address, parseIntArray(getValue(), getDataType()));
                            }
                            break;
                        case "Single Coil":
                            log.info("Writing Single Coil with address {} and value {}", address, getValue());
                            writeSingleCoil(connection, address, Boolean.parseBoolean(getValue()));
                            break;
                        case "Multiple Coils":
                            log.info("Writing Multiple Coils with values {}", getValue());
                            writeMultipleCoils(connection, address, parseBooleanArray(getValue()));
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported write method: " + writeMethod);
                    }

                    success = true; // If the operation is successful, exit the loop
                } catch (Exception ex) {
                    log.info("Attempt {} failed: {}", attempt + 1, ex.getMessage());
                    if (attempt >= retryCount) {
                        throw ex; // Rethrow the exception if all retries are exhausted
                    }
                }
            }

            result.setResponseMessage("Write operation successful.");
            result.setSuccessful(true);
        } catch (Exception ex) {
            log.info("Error during Modbus write operation: {}", ex.getMessage());
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

    private void resetValues(TCPMasterConnection connection, String writeMethod, int address, String dataType, String length) throws Exception {
        switch (writeMethod) {
            case "Single Register":
                writeSingleRegister(connection, address, 0);
                break;
            case "Multiple Registers":
                int[] resetValues = new int[Integer.parseInt(length)];
                for (int i = 0; i < resetValues.length; i++) {
                    resetValues[i] = 0;
                }
                writeMultipleRegisters(connection, address, resetValues);
                break;
            case "Single Coil":
                writeSingleCoil(connection, address, false);
                break;
            case "Multiple Coils":
                boolean[] resetCoils = new boolean[Integer.parseInt(length)];
                for (int i = 0; i < resetCoils.length; i++) {
                    resetCoils[i] = false;
                }
                writeMultipleCoils(connection, address, resetCoils);
                break;
            default:
                throw new IllegalArgumentException("Unsupported write method for reset: " + writeMethod);
        }
    }

    private void writeSingleRegister(TCPMasterConnection connection, int address, int value) throws Exception {
        WriteSingleRegisterRequest request = new WriteSingleRegisterRequest(address, new SimpleRegister(value));
        request.setUnitID(1);
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
    }

    private void writeMultipleRegisters(TCPMasterConnection connection, int address, int[] values) throws Exception {
        Register[] registers = new Register[values.length];
        for (int i = 0; i < values.length; i++) {
            registers[i] = new SimpleRegister(values[i]);
        }
        WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(address, registers);
        request.setUnitID(1);
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
    }

    private void writeMultipleRegisters(TCPMasterConnection connection, int address, float[] values) throws Exception {
        Register[] registers = convertToRegisters(values);
        WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(address, registers);
        request.setUnitID(1);
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
    }

    private void writeMultipleRegisters(TCPMasterConnection connection, int address, Register[] registers) throws Exception {
        WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(address, registers);
        request.setUnitID(1);
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
    }

    private void writeSingleCoil(TCPMasterConnection connection, int address, boolean value) throws Exception {
        WriteCoilRequest request = new WriteCoilRequest(address, value);
        request.setUnitID(1);
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
    }

    private void writeMultipleCoils(TCPMasterConnection connection, int address, boolean[] values) throws Exception {
        BitVector bitVector = new BitVector(values.length);
        for (int i = 0; i < values.length; i++) {
            bitVector.setBit(i, values[i]);
        }
        WriteMultipleCoilsRequest request = new WriteMultipleCoilsRequest(address, bitVector);
        request.setUnitID(1);
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
    }

    int parseValueAsInt(String value, String dataType) {
        switch (dataType) {
            case "Hexadecimal":
                return Integer.parseInt(value.replace("0x", ""), 16);
            case "Integer":
                return Integer.parseInt(value);
            case "Float":
                return Float.floatToIntBits(Float.parseFloat(value));
            case "String":
                return Integer.parseInt(value); // Assuming string represents an integer
            case "Boolean":
                return Boolean.parseBoolean(value) ? 1 : 0;
            default:
                throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
    }

    private boolean[] parseBooleanArray(String value) {
        String[] parts = value.split(",");
        boolean[] result = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Boolean.parseBoolean(parts[i].trim());
        }
        return result;
    }

    private int[] parseIntArray(String value, String dataType) {
        String[] parts = value.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = parseValueAsInt(parts[i].trim(), dataType);
        }
        return result;
    }

    private float[] parseFloatArray(String value) {
        String[] parts = value.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }

    private Register[] convertToRegisters(float[] values) {
        Register[] registers = new Register[values.length * 2];
        for (int i = 0; i < values.length; i++) {
            int intBits = Float.floatToIntBits(values[i]);
            registers[i * 2] = new SimpleRegister((intBits >> 16) & 0xFFFF);
            registers[i * 2 + 1] = new SimpleRegister(intBits & 0xFFFF);
        }
        return registers;
    }

    private Register[] convertStringToRegisters(String value) {
        char[] chars = value.toCharArray();
        Register[] registers = new Register[(chars.length + 1) / 2];
        for (int i = 0; i < chars.length; i += 2) {
            int high = (i < chars.length) ? chars[i] : 0;
            int low = (i + 1 < chars.length) ? chars[i + 1] : 0;
            registers[i / 2] = new SimpleRegister((high & 0xFF) | (low << 8));
        }
        return registers;
    }
}
