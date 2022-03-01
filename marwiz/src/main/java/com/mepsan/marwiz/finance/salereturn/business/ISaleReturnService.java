/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 18.07.2018 17:39:21
 */
package com.mepsan.marwiz.finance.salereturn.business;

import com.mepsan.marwiz.finance.salereturn.dao.ResponseSalesReturn;
import com.mepsan.marwiz.finance.salereturn.dao.SaleReturnReport;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.util.Date;
import java.util.List;

public interface ISaleReturnService {

    public List<SaleReturnReport> findReceipt(String receiptNo, Date processDate, int branchId);

    public List<SaleReturnReport> findSalePayment(int saleId);

    public List<SaleReturnReport> findCreditPayment(int saleId);

    public List<SaleReturnReport> findCreditPaymentDetail(int saleId);

    public int acceptReturn(SaleReturnReport obj, BranchSetting branchSetting, UserData userdata);

    public List<SaleReturnReport> findSaleWithoutReceipt(List<Stock> stockList, Date beginDate, Date endDate, int branchId);

    public int checkSafeStatus(int saleId);

    
     public int createParoSales(int saleId);
     
      public List<BranchSetting> findBranchSetting();
     

}
