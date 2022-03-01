/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBranchCon;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IAccountBranchService extends ICrudService<AccountBranchCon>{
    
    public List<AccountBranchCon> findAccountBranchCon(Account account);

    public int delete(AccountBranchCon accountBranchCon);

    public int testBeforeDelete(AccountBranchCon accountBranchCon);
    
}
