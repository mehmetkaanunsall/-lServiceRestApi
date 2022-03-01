/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 08.03.2018 15:17:57
 */
package com.mepsan.marwiz.general.report.bankextract.dao;

import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IBankExtractDao {

    public List<BankAccountMovement> findAll(Date beginDate, Date endDate, String where);

}
