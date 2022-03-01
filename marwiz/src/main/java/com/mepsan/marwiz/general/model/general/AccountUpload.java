/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:09:34 AM
 */
package com.mepsan.marwiz.general.model.general;

public class AccountUpload extends Account {

    private AccountMovement accountMovement;
    private int excelDataType; //import edilen dosyadan gelen kaydın hatalı olup olmadığı bilgisi için eklendi.

    public AccountUpload() {
        this.accountMovement = new AccountMovement();
    }

    public AccountMovement getAccountMovement() {
        return accountMovement;
    }

    public void setAccountMovement(AccountMovement accountMovement) {
        this.accountMovement = accountMovement;
    }

    public int getExcelDataType() {
        return excelDataType;
    }

    public void setExcelDataType(int excelDataType) {
        this.excelDataType = excelDataType;
    }

}
