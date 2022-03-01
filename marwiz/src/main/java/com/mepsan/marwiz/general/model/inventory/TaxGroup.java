/**
 *
 * taxgroup tablosunun model sınıfı.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 16:08:26
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import javax.validation.constraints.Size;

public class TaxGroup extends WotLogging {

    private int id;
    @Size(max = 60)
    private String name;
    private BigDecimal rate;
    private Type type;
    private int centertaxgroup_id;

    public TaxGroup() {
        this.type = new Type();
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

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getCentertaxgroup_id() {
        return centertaxgroup_id;
    }

    public void setCentertaxgroup_id(int centertaxgroup_id) {
        this.centertaxgroup_id = centertaxgroup_id;
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
