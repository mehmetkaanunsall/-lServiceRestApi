/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 03:41:04
 */
package com.mepsan.marwiz.automation.fuelshift.business;

import com.mepsan.marwiz.automation.fuelshift.dao.FuelShiftControlFile;
import com.mepsan.marwiz.automation.fuelshift.dao.FuelShiftPreview;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.automation.ShiftPayment;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IFuelShiftTransferService extends ICrud<FuelShift> {

    public List<FuelShiftSales> findAllAttendant(FuelShift fuelShift, BranchSetting branchSetting);

    public List<ShiftPayment> findAllShiftPayment(FuelShift fuelShift, Account account, int type);

    public List<FuelShiftSales> findAllAttendantSale(FuelShiftSales fuelShiftSales);

    public int delete(FuelShift fuelShift);

    public int createFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList);

    public int updateFinDocAndShiftPayment(int processType, ShiftPayment shiftPayment, String accountList);

    public int delete(ShiftPayment shiftPayment);

    public String jsonArrayAccounts(List<AccountMovement> accountMovements);

    public List<FuelShiftSales> findAllSaleForShift(FuelShift fuelShift);

    public List<FuelShiftSales> findAllSale(FuelShift fuelShift);

    public List<FuelShiftSales> findAllCreditSales(FuelShift fuelShift, FuelShiftSales fuelShiftSales, BranchSetting branchSetting, boolean isAllSales);

    public String jsonArrayShiftSale(List<FuelShiftSales> listOfFuelShiftSale);

    public List<FuelShift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, boolean isCheckDeleted);

    public List<FuelShift> count(String where, boolean isCheckDeleted);

    public void createExcelFile(FuelShift fuelShift, BranchSetting branchSetting);

    public void createPdfFile(FuelShift fuelShift, BranchSetting branchSetting);

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

    public FuelShift findShift(FuelShift obj);

    public int reSendErrorShift();

    public List<FuelShift> nonTransferableShift();

    public String jsonArrayShiftControl(List<FuelShiftControlFile> shiftControlList);

    public List<FuelShiftControlFile> controlShiftNo(String shiftList);

    public int controlVehicleAccountCon(FuelShiftSales fuelShiftSales);

    public String createWhere(Date beginDate, Date endDate);
}
