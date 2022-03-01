/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:53:13 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.ExpenseUnitPrice;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicneExpenseUnitPricesDao extends ICrud<ExpenseUnitPrice> {

    public List<ExpenseUnitPrice> findAll(WashingMachicne obj);

    public int delete(ExpenseUnitPrice obj);

}
