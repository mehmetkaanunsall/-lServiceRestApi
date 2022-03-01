/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 09:59:24
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import javax.validation.constraints.Size;

public class PriceList extends WotLogging {

    private int id;
    @Size(max = 60)
    private String name;
    @Size(max = 20)
    private String code;
    private Status status;
    private boolean isDefault;
    private boolean isPurchase;

    public PriceList() {
        this.status = new Status();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

   @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
