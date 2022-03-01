/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 12.01.2018 10:53:34
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.Objects;

public class StockCategorizationConnection extends WotLogging {

    private int id;
    private Stock stock;
    private Categorization categorization;

    public StockCategorizationConnection() {
        this.stock = new Stock();
        this.categorization = new Categorization();
    }

    public StockCategorizationConnection(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Categorization getCategorization() {
        return categorization;
    }

    public void setCategorization(Categorization categorization) {
        this.categorization = categorization;
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
        final StockCategorizationConnection other = (StockCategorizationConnection) obj;
        if (!Objects.equals(this.categorization, other.categorization)) {
            return false;
        }
        return true;
    }
}
