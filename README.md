# JMeter Modbus Sampler

A JMeter plugin for sampling Modbus servers. This plugin includes samplers for connecting to Modbus servers, writing values, reading values, and closing connections.

## Features

- Supports different write methods and data types.
- Write Single Register
- Write Multiple Registers
- Write Single Coil
- Write Multiple Coils
- Supports different data types: Integer, Hexadecimal, Float, String, Boolean
- Configurable timeouts and keep-alive settings.
- Option to reset old values.
- Detailed logging for easy debugging.

## Plugin Overview

The Modbus Sampler plugin for JMeter allows for the following operations:
- **Modbus Connection Sampler**: Establish a connection to the Modbus server.
- **Modbus Write Sampler**: Write data to the Modbus server.
- **Modbus Read Sampler**: Read data from the Modbus server.
- **Modbus Close Connection Sampler**: Close the connection to the Modbus server.

## Requirements

- **Apache JMeter 5.6.3 or later**
- **Java 8 or later** (Ensure Java is properly installed and configured)

## Steps to Install

### Download the JAR File

#### Option 1: Clone the Repository and Build with Maven
1. Clone the repository:
    ```sh
    git clone https://github.com/Sahermatter2024/jmeter-modbus-sampler.git
    cd jmeter-modbus-sampler
    ```
2. Build the project using Maven:
    ```sh
    mvn clean package
    ```

#### Option 2: Download from Releases Page
1. Visit the [releases page](https://github.com/Sahermatter2024/Jmeter-modbus-sampler/releases) of the project repository.
2. Download the latest JAR file for the plugin.
   - Note: There are two JAR files available:
      - **JAR with dependencies**: This larger JAR file (around 1.6 MB) includes all necessary dependencies, making it easy to use without additional setup.
      - **JAR without dependencies**: A smaller JAR file that requires manually adding the dependencies.
         - **Main Dependency Required**: The primary dependency required is `[j2mod-3.2.1.jar](https://jar-download.com/artifacts/com.ghgande/j2mod)` for Modbus protocol support.


<!--
#### Option 3: Install via JMeter Plugins Manager
1. Open the JMeter Plugins Manager.
2. Search for "Modbus Samplers by Mohammed Hlayel".
3. Download and install the plugin.
-->

### Copy the JAR File
1. Copy the generated/downloaded JAR file.
2. Locate your JMeter installation directory.
3. Navigate to the `lib/ext` directory within your JMeter installation directory.
4. Paste the JAR file into the `lib/ext` directory.

### Restart JMeter
1. If JMeter is already running, close it.
2. Restart JMeter to load the new plugin.

## Verifying Installation
1. Open JMeter.
2. Add a sampler to your test plan.
3. Look for the following samplers under the sampler options:
   - Modbus Connection Sampler
   - Modbus Write Sampler
   - Modbus Read Sampler
   - Modbus Close Connection Sampler

## Parameters Explained

### Timeout
- **Definition**: The maximum time (in milliseconds) to wait for a response from the Modbus server.
- **Usage**: Useful for ensuring that the sampler does not hang indefinitely waiting for a response.

### KeepAlive
- **Definition**: The duration (in milliseconds) for which the connection should be kept alive.
- **Usage**: Keeps the connection open for a specified duration to reuse it for multiple operations.

### Reset Old Values
- **Definition**: A checkbox to determine if old values should be reset on the server before writing new ones.
- **Usage**: Ensures that previous values are cleared before writing new data, preventing data overlap or corruption.


## Summary of Reading and Writing Methods with Data Types

### Reading Methods
- **Read Coils**
   - **Data Types:** Boolean
   - **Description:** Reads the status of coils (discrete outputs) from the PLC.

- **Read Input Discretes**
   - **Data Types:** Boolean
   - **Description:** Reads the status of discrete inputs from the PLC.

- **Read Holding Registers**
   - **Data Types:** Integer, Hexadecimal, Float, String
   - **Description:** Reads the contents of holding registers, which can be used to store any type of data.

- **Read Input Registers**
   - **Data Types:** Integer, Hexadecimal, Float
   - **Description:** Reads the contents of input registers, typically used for analog inputs.

### Writing Methods
- **Single Register**
   - **Data Types:** Integer, Hexadecimal
   - **Description:** Writes a single value to a specified register.

- **Multiple Registers**
   - **Data Types:** Integer, Hexadecimal, Float, String
   - **Description:** Writes multiple values to consecutive registers starting from a specified address.

- **Single Coil**
   - **Data Types:** Boolean
   - **Description:** Writes a single Boolean value to a specified coil.

- **Multiple Coils**
   - **Data Types:** Boolean
   - **Description:** Writes multiple Boolean values to consecutive coils starting from a specified address.



## Usage
### Modbus Write Sampler

1. **Configure Connection Settings**:
    - **Use Existing Connection**: Select if you want to use an existing Modbus connection.
    - **IP Address**: Enter the IP address of the Modbus server.
    - **Port**: Enter the port number of the Modbus server.
    - **Keep Alive**: Set the keep-alive duration in milliseconds. This determines how long the connection should remain open after the last activity. A value of `0` keeps the connection open indefinitely.
    - **Retry Count**: Set the number of retry attempts. This helps in handling transient network issues by retrying the connection or operation a specified number of times.
    - **Timeout**: Set the connection timeout in milliseconds. This is the duration the sampler will wait for a response from the server before considering the attempt as failed.
2. **Configure Write Settings**:
   - **Address**: Enter the address to write the value to.
   - **Value**: Enter the value to write. For multiple values, separate them with commas. For Boolean, use `true` or `false`.
      - **Example for Multiple Registers (Integer)**: `10, 20, 30`
      - **Example for Multiple Coils (Boolean)**: `true, false, true`
   - **Data Type**: Select the data type (Integer, Hexadecimal, Float, String, Boolean).
   - **Write Method**: Select the write method (Single Register, Multiple Registers, Single Coil, Multiple Coils).
      - **Example**: If writing multiple integer values, select "Multiple Registers" and enter the values as `10, 20, 30`.
   - **Reset Old Values**: Select if you want to reset old values on the server. When selected, the length field is enabled, allowing you to specify the number of registers or coils to reset.
   - **Length**: Enter the length of values (used if Reset Old Values is selected).
      - **Example**: If you want to reset 5 registers, enter `5` in the length field.

#### Example

To write a float value of `1.23` to address `0`:

- IP Address: `192.168.1.100`
- Port: `502`
- Address: `0`
- Value: `1.23`
- Data Type: `Float`
- Write Method: `Multiple Registers`
- Reset Old Values: `Checked`
- Length: `2`

To write a float value of `true,false,true,true` to address `1`:

- IP Address: `192.168.1.100`
- Port: `1502`
- Address: `1`
- Value: `true,false,true,true`
- Data Type: `boolean`
- Write Method: `Multiple Coils`
- Reset Old Values: `Unchecked`
- Length: ``

### Modbus Read Sampler

1. **Configure Connection Settings**:
    - **Use Existing Connection**: Select if you want to use an existing Modbus connection.
    - **IP Address**: Enter the IP address of the Modbus server.
    - **Port**: Enter the port number of the Modbus server.
    - **Keep Alive**: Set the keep-alive duration in milliseconds.
    - **Retry Count**: Set the number of retry attempts.
    - **Timeout**: Set the connection timeout in milliseconds.

2. **Configure Read Settings**:
    - **Address**: Enter the address to read the value from.
    - **Length**: Enter the number of registers to read.
    - **Data Type**: Select the data type (Integer, Hexadecimal, Float, String, Boolean).
    - **Read Method**: Select the read method (Read Coils, Read Input Discretes, Read Holding Registers, Read Input Registers).

#### Example

To read two registers as a float starting from address `0`:

- IP Address: `192.168.1.100`
- Port: `502`
- Address: `0`
- Length: `2`
- Data Type: `Float`
- Read Method: `Read Holding Registers`

## Project Background
This project is part of a larger effort to test the Modbus protocol for exchanging and communicating with a Digital Twin developed in Unity and a Siemens PLC installed in a remote location over Node-RED.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing
Any contribution is welcome! If you would like to contribute, please fork the repository and submit a pull request. For major changes, please open an issue first to discuss what you would like to change.

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/YourFeature`).
3. Commit your changes (`git commit -am 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Create a new Pull Request.

## Acknowledgments
Special thanks to the JMeter and Modbus4J communities for their invaluable resources and support.
This project utilizes the following libraries:

- Apache JMeter
- J2Mod
- Log4j
- JUnit
- Hamcrest
- Mockito
- 
## Author

Developed by Mohammed Hlayel

