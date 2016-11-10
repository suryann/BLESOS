/**
 *
 */

package sos.android.blesos.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.io.IOException;

import sos.android.blesos.db.DBManager;
import sos.android.blesos.db.Serializer;

/**
 * Created by Radso Technologies on 17/10/16.
 */
public class NoSqlDao extends BaseDao {

    private static final String NO_SQL_TABLE = "no_sql_table";

    private static final String COLUMN_NAME_ID = "id";
    private static final int COLUMN_NAME_ID_INDEX = 0;

    private static final String COLUMN_NAME_VALUE = "value";
    private static final int COLUMN_NAME_VALUE_INDEX = 1;

    private static NoSqlDao mInstance;

    /**
     * Default private constructor.
     */
    private NoSqlDao() {
    }

    /**
     * Get the NoSqlDao instance.
     *
     * @return Return the NoSqlDao instance.
     */
    public static NoSqlDao getInstance() {
        if (mInstance == null) {
            mInstance = new NoSqlDao();
        }

        return mInstance;
    }

    /**
     * Query to create the no_sql_table which have column id (id is not null and unique) and value.
     *
     * @return Return the create query.
     */
    public String getCreateTableQuery() {
        return "CREATE TABLE IF NOT EXISTS " + NO_SQL_TABLE + " (" + COLUMN_NAME_ID + " VARCHAR(200) NOT NULL unique, " + COLUMN_NAME_VALUE + " BLOB NOT NULL, timestamp BIGINT" + ");";
    }

    /**
     * Insert the key value to the database <BR/>
     * NOTE: check before insert the value.
     *
     * @param key   Key should be not null and unique.
     * @param value String value to insert into table.
     */
    public void insertValues(String key, String value) {
        insertValues(key, getPersistByteValue(value));
    }

    /**
     * Insert the object to the database.
     *
     * @param key Key should be not null and unique.
     * @param obj Object to insert into table.
     */
    public void insertValues(String key, Object obj) {
        insertValues(key, getPersistSerializeData(obj));
    }

    /**
     * Insert the byte array to the database.
     *
     * @param key       Key should be not null and unique.
     * @param byteValue Byte array to insert into table.
     */
    public void insertValues(String key, byte[] byteValue) {
        // No need to do action if the value is null.
        if (byteValue == null) {
            return;
        }

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME_VALUE, byteValue);

            // Check before insert the value
            if (!isAlreadyExists(key)) {
                contentValues.put(COLUMN_NAME_ID, key);

                DBManager.getInstance().insert(NO_SQL_TABLE, null, contentValues);
            } else {
                // If already exists update it.
                final String whereClause = COLUMN_NAME_ID + " = ?";

                final String[] whereArgs = {key};

                DBManager.getInstance().update(NO_SQL_TABLE, contentValues, whereClause, whereArgs);
            }
        } catch (final SQLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * To check whether the key is already exists in DB or not.
     *
     * @param key to search
     * @return true if already exits else false.
     */
    private boolean isAlreadyExists(String key) {
        final String whereClause = COLUMN_NAME_ID + " = ?";

        final String[] whereArgs = {key};

        Cursor cursor = DBManager.getInstance()
                .query(NO_SQL_TABLE, null, whereClause, whereArgs, null, null, null);

        int count = 0;

        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * To get the value by key.
     *
     * @param key to search.
     * @return value for the key.
     */
    public String getValue(String key) {
        String value = "";

        byte[] data = findByte(key);
        if (data != null) {
            value = new String(data);
        }

        return value;
    }

    /**
     * Find serialize data for the given key.
     *
     * @param key to search.
     * @return value for the key.
     */
    public Object findSerializeData(String key) {
        byte[] data = findByte(key);

        if (data != null) {
            try {
                // Convert byte array to Object.
                return Serializer.deserialize(data);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Find byte array for the given key.
     *
     * @param key to search.
     * @return value for the key.
     */
    private byte[] findByte(String key) {
        byte[] data = null;
        final String whereClause = COLUMN_NAME_ID + " = ?";

        final String[] whereArgs = {key};

        try {
            Cursor cursor = DBManager.getInstance()
                    .query(NO_SQL_TABLE, null, whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    data = cursor.getBlob(cursor.getColumnIndex(COLUMN_NAME_VALUE));
                }
            }

            if (cursor != null) {
                cursor.close();

                cursor = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Delete the row in table for the given key.
     *
     * @param key To delete specific row.
     */
    public void delete(String key) {
        DBManager.getInstance().delete(NO_SQL_TABLE, "id=?", new String[]{key});
    }

    /**
     * To delete all data from the table.
     */
    public void deleteAll() {
        //To remove all rows and get a count pass "1" as the whereClause.
        DBManager.getInstance().delete(NO_SQL_TABLE, "1", null);
    }

}
