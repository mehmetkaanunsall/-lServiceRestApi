/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.dao;

import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IChequeBillDao extends ICrud<ChequeBill>{
    
    public List<ChequeBill> findAll(int chequeBillType, String where);
    
    public List<CheckDelete> testBeforeDelete(ChequeBill chequeBill);

    public int delete(ChequeBill chequeBill);
    
}
