/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 10.04.2019 14:13:01
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountCategorizationConnection;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class DiscountAccountConnection extends WotLogging {

    private int id;
    private Discount discount;
    private Account account;
    private Categorization accountCategorization;

    public DiscountAccountConnection() {
        this.discount = new Discount();
        this.account = new Account();
        this.accountCategorization = new Categorization();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Categorization getAccountCategorization() {
        return accountCategorization;
    }

    public void setAccountCategorization(Categorization accountCategorization) {
        this.accountCategorization = accountCategorization;
    }

}
