/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.salesnottransferredtotanı.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.system.salesnottransferredtotanı.dao.ISalesNotTransferredToTanıDao;
import com.mepsan.marwiz.system.salesnottransferredtotanı.dao.SalesNotTransferredToTanı;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sinem.arslan
 */
public class SalesNotTransferredToTanıService implements ISalesNotTransferredToTanıService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private ISalesNotTransferredToTanıDao salesNotTransferredToTanıDao;

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public ISalesNotTransferredToTanıDao getSalesNotTransferredToTanıDao() {
        return salesNotTransferredToTanıDao;
    }

    public void setSalesNotTransferredToTanıDao(ISalesNotTransferredToTanıDao salesNotTransferredToTanıDao) {
        this.salesNotTransferredToTanıDao = salesNotTransferredToTanıDao;
    }

    @Override
    public List<SalesNotTransferredToTanı> listOfSalesCount() {
        return salesNotTransferredToTanıDao.listOfSalesCount();

    }

    @Override
    public int transferSales() {
        return salesNotTransferredToTanıDao.transferSales();
    }

}
