/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 23.03.2018 08:50:51
 */
package com.mepsan.marwiz.service.model;

public class SendResult {

    private boolean successful;
    private Integer errorcode;
    private String error;
    private String resultmessage;

    public SendResult() {
    }

    public SendResult(boolean successful) {
        this.successful = successful;
    }

    public SendResult(boolean successful, Integer errorcode, String error) {
        this.successful = successful;
        this.error = error;
        this.errorcode = errorcode;
    }

    public boolean isResult() {
        return successful;
    }

    public void setResult(boolean successful) {
        this.successful = successful;
    }

    public Integer getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(Integer errorcode) {
        this.errorcode = errorcode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getResultmessage() {
        return resultmessage;
    }

    public void setResultmessage(String resultmessage) {
        this.resultmessage = resultmessage;
    }

}
