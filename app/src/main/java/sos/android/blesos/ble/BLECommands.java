package sos.android.blesos.ble;

/**
 * Created by soorianarayanan on 3/31/2016.
 */
public interface BLECommands {

    //send data to BLE peripheral device
    String COMMAND_SEND_LIGHT_OFF = "NISL0\r\n";

    String COMMAND_SEND_LIGHT_ON = "NISL1\r\n";

    String COMMAND_SEND_DOOR_UNLOCK = "NISD0\r\n";

    String COMMAND_SEND_DOOR_LOCK = "NISD1\r\n";

    String COMMAND_SEND_HORN = "NISH1\r\n";


    //Receive data from BLE peripheral device
    String COMMAND_RX_LIGHT_OFF = "TCL0";

    String COMMAND_RX_LIGHT_ON = "TCL1";

    String COMMAND_RX_DOOR_UNLOCK = "TCD0";

    String COMMAND_RX_DOOR_LOCK = "TCD1";

    String COMMAND_RX_HORN = "TCH1";


    //send data to BLE peripheral device
    //Relay1
    String COMMAND_RELAY1_LIGHT_ON = "R11\r\n";
    String COMMAND_RELAY1_LIGHT_OFF = "R10\r\n";

    //Relay2
    String COMMAND_RELAY2_LIGHT_ON = "R21\r\n";
    String COMMAND_RELAY2_LIGHT_OFF = "R20\r\n";

    //Relay3
    String COMMAND_RELAY3_LIGHT_ON = "R31\r\n";
    String COMMAND_RELAY3_LIGHT_OFF = "R30\r\n";

    //Relay4
    String COMMAND_RELAY4_LIGHT_ON = "R41\r\n";
    String COMMAND_RELAY4_LIGHT_OFF = "R40\r\n";

    //Relay5
    String COMMAND_RELAY5_LIGHT_ON = "R51\r\n";
    String COMMAND_RELAY5_LIGHT_OFF = "R50\r\n";

    //Relay6
    String COMMAND_RELAY6_LIGHT_ON = "R61\r\n";
    String COMMAND_RELAY6_LIGHT_OFF = "R60\r\n";

    //Relay7
    String COMMAND_RELAY7_LIGHT_ON = "R71\r\n";
    String COMMAND_RELAY7_LIGHT_OFF = "R70\r\n";

    //Relay8
    String COMMAND_RELAY8_LIGHT_ON = "R81\r\n";
    String COMMAND_RELAY8_LIGHT_OFF = "R80\r\n";


}
