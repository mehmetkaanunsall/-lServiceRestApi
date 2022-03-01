/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   08.08.2016 16:21:40
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.Date;

public class UserFailer {

    private Date failTime;
    private String ip;

    public Date getFailTime() {
        return failTime;
    }

    public void setFailTime(Date failTime) {
        this.failTime = failTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
