/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.08.2020 08:48:06
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.model.finance.BankAccountCommission;

public interface IBankAccountCommissionDao {

    public int createCommission(BankAccountCommission bankAccountCommission, String jsonFinancingDocument, String jsonCommissionFinancingDocument);

    public int updateCommission(BankAccountCommission bankAccountCommission, String jsonFinancingDocument, String jsonCommissionFinancingDocument);

    public int deleteCommission(BankAccountCommission bankAccountCommission);

    public BankAccountCommission findBankAccountCommission(int bankAccountCommissionId);
}
