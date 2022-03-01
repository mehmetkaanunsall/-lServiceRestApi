/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 18.07.2018 17:46:52
 */
package com.mepsan.marwiz.finance.salereturn.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.util.Date;
import java.util.List;

public interface ISaleReturnDao {

    public List<SaleReturnReport> findReceipt(String receiptNo, Date processDate, int branchId);

    public List<SaleReturnReport> findSalePayment(int saleId);

    public List<SaleReturnReport> findCreditPayment(int saleId);

    public List<SaleReturnReport> findCreditPaymentDetail(int saleId);

    public int acceptReturn(SaleReturnReport obj, BranchSetting branchSetting, UserData userdata);

    public List<SaleReturnReport> findSaleWithoutReceipt(String listStock, Date beginDate, Date endDate, int branchId);

    public int checkSafeStatus(int saleId);

    
     public int createParoSales(int saleId);
     
      public List<BranchSetting> findBranchSetting();

}
