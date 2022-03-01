/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 08:42:06
 */
package com.mepsan.marwiz.finance.invoice.dao;

import java.util.Date;

public class RelatedRecord {

    private int id;
    private String documentNumber;
    private int documentType;
    private Date documentDate;
    private int relatedId;

    private boolean isRemoveButton;

    public boolean isIsRemoveButton() {
        return isRemoveButton;
    }

    public void setIsRemoveButton(boolean isRemoveButton) {
        this.isRemoveButton = isRemoveButton;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType(int documentType) {
        this.documentType = documentType;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

}
