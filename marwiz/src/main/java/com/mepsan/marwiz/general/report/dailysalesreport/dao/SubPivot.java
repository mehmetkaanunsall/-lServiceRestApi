/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.11.2019 10:35:03
 */
package com.mepsan.marwiz.general.report.dailysalesreport.dao;

import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;

public class SubPivot {

    private Type type;
    private BigDecimal totalPrice;

    public SubPivot() {
        this.type = new Type();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

}
