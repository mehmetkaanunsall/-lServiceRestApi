/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 10:00:02
 */
package com.mepsan.marwiz.system.filetransfer.dao;

import com.mepsan.marwiz.general.model.general.Shift;
import java.util.Date;

public class FileTransfer {

    private Date beginDate;
    private Date endDate;
    private Shift shift;

    public FileTransfer() {
        shift = new Shift();
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

}
