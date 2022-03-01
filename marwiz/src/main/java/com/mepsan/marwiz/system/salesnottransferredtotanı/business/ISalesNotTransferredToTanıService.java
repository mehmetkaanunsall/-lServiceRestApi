/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.salesnottransferredtotanı.business;

import com.mepsan.marwiz.system.salesnottransferredtotanı.dao.SalesNotTransferredToTanı;
import java.util.List;

/**
 *
 * @author sinem.arslan
 */
public interface ISalesNotTransferredToTanıService {

    public List<SalesNotTransferredToTanı> listOfSalesCount();
    public int transferSales();

}
