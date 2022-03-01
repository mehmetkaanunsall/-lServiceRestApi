package com.mepsan.marwiz.system.unsuccessfulsalesprocess.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UnsuccessfulSalesProcess;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elif.mart
 */
public interface IUnsuccessfulSalesProcessDao {

    public List<UnsuccessfulSalesProcess> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where,String branchList);

    public int count(String branchList);
    
    public List<UnsuccessfulSalesProcess> sendIntegration(String branchlist);
}
