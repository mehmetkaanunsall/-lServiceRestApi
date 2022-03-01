/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBranchCon;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IAccountBranchDao extends ICrud<AccountBranchCon> {
    
    public List<AccountBranchCon> findAccountBranchCon(Account account);

    public int delete(AccountBranchCon accountBranchCon);

    public int testBeforeDelete(AccountBranchCon accountBranchCon);
}
