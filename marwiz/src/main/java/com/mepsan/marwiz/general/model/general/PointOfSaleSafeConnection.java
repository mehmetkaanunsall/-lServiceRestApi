/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   14.02.2018 03:04:06
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.wot.WotLogging;

public class PointOfSaleSafeConnection extends WotLogging {

    private int id;
    private PointOfSale pointOfSale;
    private Safe safe;

    public PointOfSaleSafeConnection() {
        this.pointOfSale = new PointOfSale();
        this.safe = new Safe();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PointOfSale getPointOfSale() {
        return pointOfSale;
    }

    public void setPointOfSale(PointOfSale pointOfSale) {
        this.pointOfSale = pointOfSale;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    @Override
    public String toString() {
        return this.getPointOfSale().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
