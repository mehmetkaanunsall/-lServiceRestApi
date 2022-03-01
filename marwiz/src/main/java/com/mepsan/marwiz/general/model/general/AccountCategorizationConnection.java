/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 03:02:18
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Objects;

public class AccountCategorizationConnection extends WotLogging {

    private int id;
    private Categorization categorization;
    private Account account;

    public AccountCategorizationConnection(int id) {
        this.id = id;
    }

    public AccountCategorizationConnection() {
        this.categorization = new Categorization();
        this.account = new Account();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categorization getCategorization() {
        return categorization;
    }

    public void setCategorization(Categorization categorization) {
        this.categorization = categorization;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return categorization.getName();
    }

    @Override
    public int hashCode() {
        return this.categorization.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccountCategorizationConnection other = (AccountCategorizationConnection) obj;
        if (!Objects.equals(this.categorization, other.categorization)) {
            return false;
        }
        return true;
    }

}
