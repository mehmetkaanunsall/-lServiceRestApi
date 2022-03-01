/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.AccountInfo;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author samet.dag
 */
public interface IAccountDetailService {

    public int update(String fuelintegrationcode, String accountingintegrationcode, int accountId);

    public AccountInfo find(int accountId);

}
