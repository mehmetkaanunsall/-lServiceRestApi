/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 03:34:15
 */
package com.mepsan.marwiz.general.model.automat;

import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Date;

public class AutomatShift extends WotLogging {

    private int id;
    private String shiftNo;
    private Date beginDate;
    private Date endDate;
    private Status status;

    public AutomatShift() {
        this.status = new Status();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.getShiftNo();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
