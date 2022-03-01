/**
 *
 * Unit tablosunun model sınıfı.
 *
 * @author Ali Kurt
 *
 * Created on 22.01.2018 08:42:23
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.Size;

public class Unit extends WotLogging {

    private int id;
    @Size(max = 60)
    private String name;
    @Size(max = 10)
    private String sortName;
    private int unitRounding;
    private int centerunit_id;
    private String internationalCode;
    private String integrationCode;
    private BigDecimal mainWeight;
    private Unit mainWeightUnit;

    public Unit(int id) {
        this.id = id;

    }

    public Unit() {
    }

    public String getInternationalCode() {
        return internationalCode;
    }

    public void setInternationalCode(String internationalCode) {
        this.internationalCode = internationalCode;
    }

    public int getCenterunit_id() {
        return centerunit_id;
    }

    public void setCenterunit_id(int centerunit_id) {
        this.centerunit_id = centerunit_id;
    }

    public int getUnitRounding() {
        return unitRounding;
    }

    public void setUnitRounding(int unitRounding) {
        this.unitRounding = unitRounding;
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

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
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
        final Unit other = (Unit) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public String getIntegrationCode() {
        return integrationCode;
    }

    public void setIntegrationCode(String integrationCode) {
        this.integrationCode = integrationCode;
    }

    public BigDecimal getMainWeight() {
        return mainWeight;
    }

    public void setMainWeight(BigDecimal mainWeight) {
        this.mainWeight = mainWeight;
    }

    public Unit getMainWeightUnit() {
        return mainWeightUnit;
    }

    public void setMainWeightUnit(Unit mainWeightUnit) {
        this.mainWeightUnit = mainWeightUnit;
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
