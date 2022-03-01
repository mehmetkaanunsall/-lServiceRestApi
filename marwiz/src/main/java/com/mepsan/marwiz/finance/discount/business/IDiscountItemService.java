/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 09.04.2019 08:27:43
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountItem;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IDiscountItemService extends ICrudService<DiscountItem> {

    public List<DiscountItem> listofDiscountItem(Discount obj);
    
    public int testBeforeDelete(DiscountItem discount);

    public int delete(DiscountItem obj);

    public List<String> listDay();

    public List<String> listMonth();

    public List<String> listMonthDay();

    public void customizeDayMonth(DiscountItem discountItem);

    public List<Stock> convertStock(String stocks);

    public List<Brand> convertBrand(String brands, List<Brand> brandList);
}
