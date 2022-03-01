/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.08.2020 08:46:40
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.mepsan.marwiz.general.model.finance.BankAccountCommission;

public interface IBankAccountCommissionService {

    public int createCommission(BankAccountCommission bankAccountCommission);

    public int updateCommission(BankAccountCommission bankAccountCommission);

    public int deleteCommission(BankAccountCommission bankAccountCommission);

    public BankAccountCommission findBankAccountCommission(int bankAccountCommissionId);
}
