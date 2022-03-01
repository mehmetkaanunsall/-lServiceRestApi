package com.mepsan.marwiz.system.unsuccessfulsalesprocess.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UnsuccessfulSalesProcess;
import com.mepsan.marwiz.system.unsuccessfulsalesprocess.dao.IUnsuccessfulSalesProcessDao;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class UnsuccessfulSalesProcessService implements IUnsuccessfulSalesProcessService {

    @Autowired
    IUnsuccessfulSalesProcessDao unsuccessfulSalesProcessDao;

    public IUnsuccessfulSalesProcessDao getUnsuccessfulSalesProcessDao() {
        return unsuccessfulSalesProcessDao;
    }

    public void setUnsuccessfulSalesProcessDao(IUnsuccessfulSalesProcessDao unsuccessfulSalesProcessDao) {
        this.unsuccessfulSalesProcessDao = unsuccessfulSalesProcessDao;
    }

    @Override
    public List<UnsuccessfulSalesProcess> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String branchList) {
        return unsuccessfulSalesProcessDao.findAll(first, pageSize, sortField, sortOrder, filters, where, branchList);
    }

    @Override
    public int count(String branchList) {
        return unsuccessfulSalesProcessDao.count(branchList);
    }

    @Override
    public List<UnsuccessfulSalesProcess> sendIntegration(String branchlist) {
        return unsuccessfulSalesProcessDao.sendIntegration(branchlist);
    }

}
