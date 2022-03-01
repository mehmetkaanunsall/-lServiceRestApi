/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2018 02:42:45
 */
package com.mepsan.marwiz.finance.customeragreements.business;

import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import java.util.List;

public interface ICustomerAgreementsService {

    public List<CustomerAgreements> findAll(String where,boolean checkCreditControl,int creditType);

    public String createWhere(CustomerAgreements obj);
    
}
