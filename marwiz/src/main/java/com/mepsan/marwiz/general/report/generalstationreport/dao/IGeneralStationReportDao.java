/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.generalstationreport.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.GeneralStation;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author m.duzoylum
 */
public interface IGeneralStationReportDao extends ILazyGrid<GeneralStation> {

	public List<GeneralStation> findAll( Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable,int centralIntegrationIf, int costType);

	public List<GeneralStation> findAllMarket(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int centralIntegrationIf, int costType);

	public List<GeneralStation> findAllAutomat(Date beginDate, Date endDate, String branchList, int lastUnitPrice, int costType);

	public List<GeneralStation> totals(String where, Date beginDate, Date endDate, String branchList, int lastUnitPrice, int typeOfTable,int centralIntegrationIf,int costType);
	
	

}
