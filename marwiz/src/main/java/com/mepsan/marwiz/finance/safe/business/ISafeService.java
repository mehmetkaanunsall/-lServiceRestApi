/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 10:20:54
 */
package com.mepsan.marwiz.finance.safe.business;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.Date;
import java.util.List;

public interface ISafeService extends ICrudService<Safe> {

    public List<Safe> selectSafe();

    public List<Safe> findAll();

    public List<Safe> findSafeByCurrency(String where);

    public String createWhere(int type, List<Currency> currencyList);

    public int delete(Safe safe);

    public List<Safe> findSafeBalanceForDate(Safe safe);

    public String exportSafeReport(Safe safe);

    public List<Safe> selectSafe(Branch branch);
    
    public List<Safe> selectSafe(List<Branch> branchList);

}
