/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sos.android.blesos.ble;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes.
 */
public class GattAttributes {
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";

    public static String SERIAL_PORT_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    //Read client data
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    //IRIS data service
    public static String SERIAL_PORT_DATA_SERVICE = "00005500-d102-11e1-9b23-00025b00a5a5";
    public static String SERIAL_PORT_DATA_MEASUREMENT = "00005501-d102-11e1-9b23-00025b00a5a5";
    private static HashMap<String, String> attributes = new HashMap();

//    UUID_SERIAL_SERVICE            0x00005500D10211E19B2300025B00A5A5

    static {
        // Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put(SERIAL_PORT_DATA_SERVICE, "KEMSYS SERVICE");

        // Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put(SERIAL_PORT_DATA_MEASUREMENT, "KEMSYS Characteristic");

        attributes.put(BLEUUIDS.ALERT_LEVEL.getUuid().toString(), BLEUUIDS.ALERT_LEVEL.name());
        attributes.put(BLEUUIDS.ALERT_NOTIFICATION_CONTROL_POINT.getUuid().toString(), BLEUUIDS.ALERT_NOTIFICATION_CONTROL_POINT.name());
        attributes.put(BLEUUIDS.ALERT_NOTIFICATION_SERVICE.getUuid().toString(), BLEUUIDS.ALERT_NOTIFICATION_SERVICE.name());

        //BP
        attributes.put(BLEUUIDS.BP_MEASUREMENT.getUuid().toString(), BLEUUIDS.BP_MEASUREMENT.name());
        attributes.put(BLEUUIDS.BP_SERVICE.getUuid().toString(), BLEUUIDS.BP_SERVICE.name());
        //Battery
        attributes.put(BLEUUIDS.BATTERY_LEVEL.getUuid().toString(), BLEUUIDS.BATTERY_LEVEL.name());
        attributes.put(BLEUUIDS.BATTERY_SERVICE.getUuid().toString(), BLEUUIDS.BATTERY_SERVICE.name());

        attributes.put(BLEUUIDS.CCC.getUuid().toString(), BLEUUIDS.CCC.name());
        attributes.put(BLEUUIDS.CSC_CONTROL_POINT.getUuid().toString(), BLEUUIDS.CSC_CONTROL_POINT.name());
        attributes.put(BLEUUIDS.CSC_FEATURE.getUuid().toString(), BLEUUIDS.CSC_FEATURE.name());
        attributes.put(BLEUUIDS.CSC_MEASUREMENT.getUuid().toString(), BLEUUIDS.CSC_MEASUREMENT.name());
        attributes.put(BLEUUIDS.CSC_SERVICE.getUuid().toString(), BLEUUIDS.CSC_SERVICE.name());


        //Device info service
        attributes.put(BLEUUIDS.DEVICE_INFORMATION_SERVICE.getUuid().toString(), BLEUUIDS.DEVICE_INFORMATION_SERVICE.name());
        attributes.put(BLEUUIDS.FIRMWARE_REVISION.getUuid().toString(), BLEUUIDS.FIRMWARE_REVISION.name());
        attributes.put(BLEUUIDS.HARDWARE_REVISION.getUuid().toString(), BLEUUIDS.HARDWARE_REVISION.name());

        //Heart rate service
        attributes.put(BLEUUIDS.HEART_RATE_LOCATION.getUuid().toString(), BLEUUIDS.HEART_RATE_LOCATION.name());
        attributes.put(BLEUUIDS.HEART_RATE_MEASUREMENT.getUuid().toString(), BLEUUIDS.HEART_RATE_MEASUREMENT.name());
        attributes.put(BLEUUIDS.HRP_SERVICE.getUuid().toString(), BLEUUIDS.HRP_SERVICE.name());

        attributes.put(BLEUUIDS.IMMEDIATE_ALERT.getUuid().toString(), BLEUUIDS.IMMEDIATE_ALERT.name());

        attributes.put(BLEUUIDS.MANUFACTURER_NAME.getUuid().toString(), BLEUUIDS.MANUFACTURER_NAME.name());
        attributes.put(BLEUUIDS.MODEL_NUMBER.getUuid().toString(), BLEUUIDS.MODEL_NUMBER.name());
        attributes.put(BLEUUIDS.NEW_ALERT.getUuid().toString(), BLEUUIDS.NEW_ALERT.name());
        attributes.put(BLEUUIDS.NEW_ALERT_CATEGORY.getUuid().toString(), BLEUUIDS.NEW_ALERT_CATEGORY.name());
        //RSC service
        attributes.put(BLEUUIDS.RSC_MEASUREMENT.getUuid().toString(), BLEUUIDS.RSC_MEASUREMENT.name());
        attributes.put(BLEUUIDS.RSC_SERVICE.getUuid().toString(), BLEUUIDS.RSC_SERVICE.name());
        attributes.put(BLEUUIDS.SC_CONTROL_POINT.getUuid().toString(), BLEUUIDS.SC_CONTROL_POINT.name());
        attributes.put(BLEUUIDS.SENSOR_LOCATION.getUuid().toString(), BLEUUIDS.SENSOR_LOCATION.name());
        attributes.put(BLEUUIDS.SERIAL_NUMBER.getUuid().toString(), BLEUUIDS.SERIAL_NUMBER.name());
        attributes.put(BLEUUIDS.SOFTWARE_REVISION.getUuid().toString(), BLEUUIDS.SOFTWARE_REVISION.name());

        attributes.put(BLEUUIDS.UNREAD_ALERT_CATEGORY.getUuid().toString(), BLEUUIDS.UNREAD_ALERT_CATEGORY.name());
        attributes.put(BLEUUIDS.UNREAD_ALERT_STATUS.getUuid().toString(), BLEUUIDS.UNREAD_ALERT_STATUS.name());

    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public enum BLEUUIDS {
        HRP_SERVICE("0000180d-0000-1000-8000-00805f9b34fb"),
        HEART_RATE_MEASUREMENT("00002a37-0000-1000-8000-00805f9b34fb"),
        HEART_RATE_LOCATION("00002a38-0000-1000-8000-00805f9b34fb"),
        CCC("00002902-0000-1000-8000-00805f9b34fb"),
        IMMEDIATE_ALERT("00001802-0000-1000-8000-00805f9b34fb"),
        ALERT_LEVEL("00002a06-0000-1000-8000-00805f9b34fb"),
        ALERT_NOTIFICATION_SERVICE("00001811-0000-1000-8000-00805f9b34fb"),
        ALERT_NOTIFICATION_CONTROL_POINT("00002a44-0000-1000-8000-00805f9b34fb"),
        UNREAD_ALERT_STATUS("00002a45-0000-1000-8000-00805f9b34fb"),
        NEW_ALERT("00002a46-0000-1000-8000-00805f9b34fb"),
        NEW_ALERT_CATEGORY("00002a47-0000-1000-8000-00805f9b34fb"),
        UNREAD_ALERT_CATEGORY("00002a48-0000-1000-8000-00805f9b34fb"),
        DEVICE_INFORMATION_SERVICE("0000180A-0000-1000-8000-00805f9b34fb"),
        MANUFACTURER_NAME("00002A29-0000-1000-8000-00805f9b34fb"),
        MODEL_NUMBER("00002a24-0000-1000-8000-00805f9b34fb"),
        SERIAL_NUMBER("00002a25-0000-1000-8000-00805f9b34fb"),
        HARDWARE_REVISION("00002a27-0000-1000-8000-00805f9b34fb"),
        FIRMWARE_REVISION("00002a26-0000-1000-8000-00805f9b34fb"),
        SOFTWARE_REVISION("00002a28-0000-1000-8000-00805f9b34fb"),
        BATTERY_SERVICE("0000180f-0000-1000-8000-00805f9b34fb"),
        BATTERY_LEVEL("00002a19-0000-1000-8000-00805f9b34fb"),
        CSC_SERVICE("00001816-0000-1000-8000-00805f9b34fb"),
        CSC_MEASUREMENT("0002a5b-0000-1000-8000-00805f9b34fb"),
        CSC_FEATURE("00002a5c-0000-1000-8000-00805f9b34fb"),
        SENSOR_LOCATION("00002a5d-0000-1000-8000-00805f9b34fb"),
        CSC_CONTROL_POINT("00002a55-0000-1000-8000-00805f9b34fb"),
        RSC_SERVICE("00001814-0000-1000-8000-00805f9b34fb"),
        RSC_MEASUREMENT("00002a53-0000-1000-8000-00805f9b34fb"),
        SC_CONTROL_POINT("00002a55-0000-1000-8000-00805f9b34fb"),
        BP_SERVICE("00001810-0000-1000-8000-00805f9b34fb"),
        BP_MEASUREMENT("00002a35-0000-1000-8000-00805f9b34fb");

        private UUID value;

        private BLEUUIDS(String value) {
            this.value = UUID.fromString(value);
        }

        public UUID getUuid() {
            return value;
        }
    }
}
