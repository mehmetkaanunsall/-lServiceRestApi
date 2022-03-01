/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 09:31:42
 */
package com.mepsan.marwiz.finance.safe.dao;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.Date;
import java.util.List;

public interface ISafeDao extends ICrud<Safe> {

    public List<Safe> selectSafe();

    public List<Safe> findAll();

    public List<Safe> findSafeByCurrency(String where);

    public int delete(Safe safe);

    public List<Safe> findSafeBalanceForDate(Safe safe);

    public List<Safe> selectSafe(Branch branch);
    
    public List<Safe> selectSafe(List<Branch> branchList);
}
