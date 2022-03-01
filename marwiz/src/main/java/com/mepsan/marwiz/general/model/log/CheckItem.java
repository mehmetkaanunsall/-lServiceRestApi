/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 09.05.2018 12:29:03
 */
package com.mepsan.marwiz.general.model.log;

import java.util.Date;

public class CheckItem {

    private int type;
    private Date processDate;
    private boolean isSuccess;
    private String response;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    
}
