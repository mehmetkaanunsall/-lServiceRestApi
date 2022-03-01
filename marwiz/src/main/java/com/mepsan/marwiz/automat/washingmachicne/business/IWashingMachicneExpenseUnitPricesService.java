/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 9:13:53 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.general.model.automat.ExpenseUnitPrice;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IWashingMachicneExpenseUnitPricesService extends ICrudService<ExpenseUnitPrice> {

    public List<ExpenseUnitPrice> findAll(WashingMachicne obj);

    public int delete(ExpenseUnitPrice obj);

}
