/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.05.2019 09:03:21
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;

public class Vehicle extends WotLogging {

    private int id;
    private String plate;
    private Account account;

    public Vehicle() {
        this.account = new Account();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return this.getPlate();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
