/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 07:41:20
 */

package com.mepsan.marwiz.finance.safe.dao;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;


public interface ISafeMovementDao {

    public List<SafeMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId);

    //public int count(String where, Safe safe, int opType, Date beginDate, Date endDate);

    public int count(String where, Safe safe, Branch branch);
    
    public List<SafeMovement> count(String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId);

    public List<SafeMovement> exportData(String where, String safeString, List<Branch> listOfBranch, int opType, Date beginDate, Date endDate, int financingTypeId);
    
}
