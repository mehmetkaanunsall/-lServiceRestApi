/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.06.2019 09:59:19
 */
package com.mepsan.marwiz.general.marketshift.dao;

import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.report.marketshiftreport.dao.MarketShiftPayment;

public class MarketShiftPaymentFinancingDocumentCon {

    private int id;
    private MarketShiftPayment shiftPayment;
    private FinancingDocument financingDocument;

    public MarketShiftPaymentFinancingDocumentCon() {
        this.shiftPayment = new MarketShiftPayment();
        this.financingDocument = new FinancingDocument();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MarketShiftPayment getShiftPayment() {
        return shiftPayment;
    }

    public void setShiftPayment(MarketShiftPayment shiftPayment) {
        this.shiftPayment = shiftPayment;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

}
