package sos.android.blesos.db.dao;

import java.io.IOException;

import sos.android.blesos.db.Serializer;

/**
 * Created by Radso Technologies on 17/10/16.
 */
public abstract class BaseDao {

    /**
     * Convert the string to byte[] array.
     *
     * @param value Input String to convert into byte array.
     * @return Byte array if value is not null else return null.
     */
    protected byte[] getPersistByteValue(String value) {
        if (value != null) {
            return value.getBytes();
        }

        return null;
    }

    /**
     * Convert object to byte[] array.
     *
     * @param obj Object to convert into byte array.
     * @return Byte array if object is not null else return null.
     */
    protected byte[] getPersistSerializeData(Object obj) {
        byte[] bytes = null;

        try {
            if (obj != null) {
                bytes = Serializer.serialize(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }
}
