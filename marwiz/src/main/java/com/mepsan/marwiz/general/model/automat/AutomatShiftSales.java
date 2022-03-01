/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 10:38:16 AM
 */
package com.mepsan.marwiz.general.model.automat;

import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;

public class AutomatShiftSales extends AutomatSales {

    private AutomatShiftReport shiftReport;

    public AutomatShiftSales() {
        this.shiftReport = new AutomatShiftReport();
    }

    public AutomatShiftReport getShiftReport() {
        return shiftReport;
    }

    public void setShiftReport(AutomatShiftReport shiftReport) {
        this.shiftReport = shiftReport;
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
