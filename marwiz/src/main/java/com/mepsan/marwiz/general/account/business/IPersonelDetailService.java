/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author samet.dag
 */
public interface IPersonelDetailService {

    public int update(String integrationcode, BigDecimal exactsalary, int agi, Date startDate, Date endDate, int accountId);

    public EmployeeInfo find(int accountId);

}
