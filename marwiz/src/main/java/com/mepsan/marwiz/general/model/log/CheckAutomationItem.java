/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.05.2019 11:51:02
 */
package com.mepsan.marwiz.general.model.log;

import java.util.Date;

public class CheckAutomationItem {

    private int id;
    private int type;
    private Date processDate;
    private boolean isSuccess;
    private String response;
    private String processResponse;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcessResponse() {
        return processResponse;
    }

    public void setProcessResponse(String processResponse) {
        this.processResponse = processResponse;
    }

}
