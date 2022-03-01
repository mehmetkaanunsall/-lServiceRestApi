/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:56:31 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.AutomatShift;

public class AutomatShiftReport extends AutomatShift {

    private AutomatSales automatSalesItem;
    private int totalCount;

    public AutomatShiftReport() {
        this.automatSalesItem = new AutomatSales();
    }

    public AutomatSales getAutomatSalesItem() {
        return automatSalesItem;
    }

    public void setAutomatSalesItem(AutomatSales automatSalesItem) {
        this.automatSalesItem = automatSalesItem;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
