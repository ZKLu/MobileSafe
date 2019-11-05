package com.samlu.mobilesafe.db.domin;

/**
 * Created by sam lu on 2019/11/5.
 */
public class BlackNumberInfo {
    public String phone;
    public String mode;

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "phone='" + phone + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


}
