package sos.android.blesos.db.model;

import java.io.Serializable;

/**
 * Created by soorianarayanan on 17/10/16.
 */

public class User implements Serializable{
    String name;
    String mobileNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
