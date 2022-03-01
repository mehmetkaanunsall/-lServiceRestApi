/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   19.02.2018 05:07:12
 */
package com.mepsan.marwiz.general.creditcardmachine.dao;

import com.mepsan.marwiz.general.model.general.CreditCardMachine;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface ICreditCardMachineDao extends ICrud<CreditCardMachine> {

    public List<CreditCardMachine> listOfCreditCardMachine();
    
    public int delete(CreditCardMachine obj);
}
