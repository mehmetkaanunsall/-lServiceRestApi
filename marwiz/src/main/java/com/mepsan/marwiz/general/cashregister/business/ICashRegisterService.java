/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 06:03:14
 */
package com.mepsan.marwiz.general.cashregister.business;

import com.mepsan.marwiz.general.model.general.CashRegister;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface ICashRegisterService extends ICrudService<CashRegister> {

    public List<CashRegister> selectListCashRegister();

    public List<CashRegister> listOfCashRegister();

    public int delete(CashRegister obj);
}
