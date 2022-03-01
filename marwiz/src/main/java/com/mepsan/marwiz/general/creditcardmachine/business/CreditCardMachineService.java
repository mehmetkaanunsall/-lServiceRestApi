/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   19.02.2018 05:07:03
 */
package com.mepsan.marwiz.general.creditcardmachine.business;

import com.mepsan.marwiz.general.creditcardmachine.dao.ICreditCardMachineDao;
import com.mepsan.marwiz.general.model.general.CreditCardMachine;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class CreditCardMachineService implements ICreditCardMachineService {
    
    @Autowired
    private ICreditCardMachineDao creditCardMachineDao;
    
    public void setCreditCardMachineDao(ICreditCardMachineDao creditCardMachineDao) {
        this.creditCardMachineDao = creditCardMachineDao;
    }
    
    @Override
    public List<CreditCardMachine> listOfCreditCardMachine() {
        return creditCardMachineDao.listOfCreditCardMachine();
    }
    
    @Override
    public int create(CreditCardMachine obj) {
        return creditCardMachineDao.create(obj);
    }
    
    @Override
    public int update(CreditCardMachine obj) {
        return creditCardMachineDao.update(obj);
    }

    @Override
    public int delete(CreditCardMachine obj) {
        return creditCardMachineDao.delete(obj);
    }
    
}
