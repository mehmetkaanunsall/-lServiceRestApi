/**
 *
 * brand tablosunun model sınıfı.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 08:18:41
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import javax.validation.constraints.Size;

public class Brand extends WotLogging {

    private int id;
    @Size(max = 60)
    private String name;
    private Item item;
    private int centerbrand_id;

    public Brand() {
        this.item = new Item();
    }

    public Brand(int id) {
        this.id = id;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getCenterbrand_id() {
        return centerbrand_id;
    }

    public void setCenterbrand_id(int centerbrand_id) {
        this.centerbrand_id = centerbrand_id;
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
