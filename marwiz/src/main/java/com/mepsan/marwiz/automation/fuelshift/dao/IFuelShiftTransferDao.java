/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 03:40:56
 */
package com.mepsan.marwiz.automation.fuelshift.dao;

import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.automation.ShiftPayment;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface IFuelShiftTransferDao extends ICrud<FuelShift> {

    public List<FuelShiftSales> findAllAttendant(FuelShift fuelShift, BranchSetting branchSetting);

    public List<ShiftPayment> findAllShiftPayment(FuelShift fuelShift, Account account, int type);

    public List<FuelShiftSales> findAllAttendantSale(FuelShiftSales fuelShiftSales);

    public int delete(FuelShift fuelShift);
    
    public int createFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList);

    public int updateFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList);

    public int delete(ShiftPayment shiftPayment);

    public List<FuelShiftSales> findAllSaleForShift(FuelShift fuelShift);

    public List<FuelShiftSales> findAllSale(FuelShift fuelShift);

    public List<FuelShiftSales> findAllCreditSales(FuelShift fuelShift, FuelShiftSales fuelShiftSales, BranchSetting branchSetting, boolean isAllSales);

    public List<FuelShift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, boolean isCheckDeleted);

    public List<FuelShift> count(String where, boolean isCheckDeleted);

    public String findSalesAccordingToStockForExcel(FuelShift fuelShift);

    public String shiftPaymentCashDetail(FuelShift fuelShift);

    public String shiftPaymentCredit(FuelShift fuelShift);

    public String shiftGeneralTotal(FuelShift fuelShift, BranchSetting branchSetting);

    public String shiftPaymentCreditCardDetail(FuelShift fuelShift);

    public String shiftPaymentDeficitExcess(FuelShift fuelShift);

    public List<FuelShiftPreview> findSalesAccordingToStockForPreview(FuelShift fuelShift);

    public List<FuelShiftPreview> shiftPaymentCashDetailForPreview(FuelShift fuelShift);

    public List<FuelShiftPreview> shiftPaymentCreditCardDetailForPreview(FuelShift fuelShift);

    public List<FuelShiftPreview> shiftPaymentCreditForPreview(FuelShift fuelShift);

    public List<FuelShiftPreview> shiftPaymentDeficitExcessForPreview(FuelShift fuelShift);

    public List<FuelShiftPreview> shiftGeneralTotalForPreview(FuelShift fuelShift, BranchSetting branchSetting);

    public  List<FuelShift> nonTransferableShift();
    
    public int reSendErrorShift();
    
    public List<FuelShiftControlFile> controlShiftNo(String shiftList);
    
    public int controlVehicleAccountCon(FuelShiftSales fuelShiftSales);

    public DataSource getDatasource();

}
