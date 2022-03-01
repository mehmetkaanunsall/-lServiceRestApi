/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2018 02:42:24
 */
package com.mepsan.marwiz.finance.customeragreements.dao;

import java.util.List;

public interface ICustomerAgreementsDao {

    public List<CustomerAgreements> findAll(String where,boolean checkCreditControl,int creditType);
      
}
