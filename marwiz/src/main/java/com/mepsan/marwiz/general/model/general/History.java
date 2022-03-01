/**
 *
 *
 *
 * @author SALİM VELA ABDULHADİ
 *
 * @date 10.10.2016 11:38:24
 */
package com.mepsan.marwiz.general.model.general;

import java.util.Date;

public class History {

    private int id;
    private String tableName;
    private String processType;
    private int rowId;
    private String columnName;
    private String columnType;
    private String oldValue;
    private String newValue;
    private Date processDate;
    private UserData userData;
    private String fOldValue;
    private String fNewValue;
    private String referencetable;
    private String pageOfDeleteOrInsert;
    private String itemValue;
    private Branch branch;

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public History() {
        this.userData = new UserData();
        this.branch = new Branch();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public String getfOldValue() {
        return fOldValue;
    }

    public void setfOldValue(String fOldValue) {
        this.fOldValue = fOldValue;
    }

    public String getfNewValue() {
        return fNewValue;
    }

    public void setfNewValue(String fNewValue) {
        this.fNewValue = fNewValue;
    }

    public String getReferencetable() {
        return referencetable;
    }

    public void setReferencetable(String referencetable) {
        this.referencetable = referencetable;
    }

    public String getPageOfDeleteOrInsert() {
        return pageOfDeleteOrInsert;
    }

    public void setPageOfDeleteOrInsert(String pageOfDeleteOrInsert) {
        this.pageOfDeleteOrInsert = pageOfDeleteOrInsert;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

}
