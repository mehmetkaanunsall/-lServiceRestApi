/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bank.dao;

import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;
import java.util.Map;

public interface IBankDao extends ICrud<Bank> {

    public List<Bank> findAll();

    public List<Bank> bankBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int bankBookCount(String where, String type, List<Object> param);

    public List<Bank> selectBank();

    public int testBeforeDelete(String branchList);

    public int delete(Bank bank);
}
