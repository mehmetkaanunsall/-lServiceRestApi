/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.03.2018 09:11:27
 */
package com.mepsan.marwiz.general.report.totalgiroreport.dao;

import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface ITotalGiroReportDao extends ICrud<TotalGiroReport> {

    public List<TotalGiroReport> findAll(String where,String branchList);

}
