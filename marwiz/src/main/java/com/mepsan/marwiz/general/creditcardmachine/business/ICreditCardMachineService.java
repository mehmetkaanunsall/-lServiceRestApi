/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   19.02.2018 05:06:55
 */
package com.mepsan.marwiz.general.creditcardmachine.business;

import com.mepsan.marwiz.general.model.general.CreditCardMachine;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface ICreditCardMachineService extends ICrudService<CreditCardMachine> {

    public List<CreditCardMachine> listOfCreditCardMachine();
    
    public int delete(CreditCardMachine obj);

}
