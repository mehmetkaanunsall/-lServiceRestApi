/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 01:55:38
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BranchSetting extends WotLogging {

    private int id;
    private Branch branch;
    private String printPaymentType;
    private String authPaymentType;
    private boolean isManagerDiscount;
    private boolean isManagerReturn;
    private int automationId;
    private int washingTypeId;
    private int washingType;
    private boolean isManagerPumpScreen;
    private boolean isCentralIntegration;
    private boolean isOpen;
    private boolean isDocumentCreditNow;
    private boolean isPurchaseControl;
    private boolean isShiftControl;
    private boolean isReturnWithoutReceipt;
    private boolean isContinueCrError;
    private boolean isForeignCurrency;
    private Date pastPeriodClosingDate;
    private int sleepTime;
    private String uscIpAddress;
    private int uscPort;
    private int uscProtocol;
    private String webServiceUserName;
    private String webServicePassword;
    private String wSendPoint;
    private String localServerIpAddress;
    private BigDecimal roundingConstraint;
    private boolean isUnitPriceAffectedByDiscount;
    private int purchaseUnitPriceUpdateOptionId;
    private BigDecimal profitabilityTolerance;
    private Categorization integrationCategorization;
    private int erpIntegrationId;
    private String erpUrl;
    private String erpUsername;
    private String erpPassword;
    private int erpTimeout;
    private String automationUrl;
    private String automationUserName;
    private String automationPassword;
    private int automationTimeOut;
    private String erpEntegrationCode;
    private String starbucksWebServiceUrl;
    private String starbucksMachicneName;
    private String starbucksApiKey;
    private String paroUrl;
    private String paroAccountCode;
    private String paroBranchCode;
    private String paroResponsibleCode;
    private boolean isEInvoice;
    private boolean isAllBranch;
    private int eInvoiceIntegrationTypeId;
    private String eInvoiceAccountCode;
    private String eInvoiceUrl;
    private String eInvoiceUserName;
    private String eInvoicePassword;
    private String eInvoicePrefix;
    private String eArchivePrefix;
    private String eInvoiceTagInfo;
    private boolean isShowPassiveAccount;
    private boolean isProcessPassiveAccount;
    private boolean isMinusMainSafe;
    private boolean isInvoiceStockSalePriceList;
    private boolean isProductRemoval;
    private boolean isTaxMandatory;
    private String applicationServerUrl;
    private Boolean isCashierEnterCashShift;
    private int wsConnectionTimeOut;
    private int wsRequestTimeOut;
    private int paroConnectionTimeOut;
    private int paroRequestTimeOut;
    private boolean isManagerAutomatProduct;
    private String printSaleType;

    List<Integer> lPrintPaymentType;
    List<Integer> lAuthPaymentType;
    List<Integer> lAuthReport;
    private BankAccount automationBankAccount;
    private String automationTestKeyword;
    private String autoFileCreatePassword;
    private String autoFileCreateUrl;
    private String autoFileCreateUserName;
    private String washingMachicneUrl;
    private String washingMachicneOfflineUrl;
    private String washingMachicneUsername;
    private String washingMachicnePassword;
    private int autoFileCreateType;
    private int eInvoiceCount;
    private int eArchiveCount;
    private Account automationScoreAccount;
    private boolean isPurchaseInvoiceProductSupplierUpdate;
    private boolean isErpUseShift;
    private boolean isCashierEnterBarcode;
    private boolean isCashierUseTheSyncButton;

    private boolean isCashierEnterQuantity;
    private String magiclickUrl;
    private String magiclickConsumerKey;
    private String magiclickConsumerSecret;
    private int magiclickTimeOut;

    private BigDecimal minStockQuantity;
    private String wSEEndPoint;
    private String paroCenterAccountCode;
    private String paroCenterResponsibleCode;
    private boolean isCashierStockInventory;
    private BankAccount automationPaymentBankAccount;

    private int shiftCurrencyRounding;
    private boolean isPassiveGet;
    private Date getInOperableDate;
    private int specialItem;
    private BigDecimal orderDeliveryRate;
    private BigDecimal generalOrderDeliveryRate;
    private boolean isWashingSaleZReport;

    public BranchSetting() {
        this.branch = new Branch();
        this.integrationCategorization = new Categorization();
        this.lPrintPaymentType = new ArrayList<>();
        this.lAuthPaymentType = new ArrayList<>();
        this.lAuthReport = new ArrayList<>();
        this.automationBankAccount = new BankAccount();
        this.automationScoreAccount = new Account();
        this.automationPaymentBankAccount = new BankAccount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getPrintPaymentType() {
        return printPaymentType;
    }

    public void setPrintPaymentType(String printPaymentType) {
        this.printPaymentType = printPaymentType;
    }

    public boolean isIsShiftControl() {
        return isShiftControl;
    }

    public void setIsShiftControl(boolean isShiftControl) {
        this.isShiftControl = isShiftControl;
    }

    public String getAuthPaymentType() {
        return authPaymentType;
    }

    public void setAuthPaymentType(String authPaymentType) {
        this.authPaymentType = authPaymentType;
    }

    public boolean isIsManagerDiscount() {
        return isManagerDiscount;
    }

    public void setIsManagerDiscount(boolean isManagerDiscount) {
        this.isManagerDiscount = isManagerDiscount;
    }

    public List<Integer> getlPrintPaymentType() {
        return lPrintPaymentType;
    }

    public void setlPrintPaymentType(List<Integer> lPrintPaymentType) {
        this.lPrintPaymentType = lPrintPaymentType;
    }

    public List<Integer> getlAuthPaymentType() {
        return lAuthPaymentType;
    }

    public String getwSEEndPoint() {
        return wSEEndPoint;
    }

    public void setwSEEndPoint(String wSEEndPoint) {
        this.wSEEndPoint = wSEEndPoint;
    }

    public void setlAuthPaymentType(List<Integer> lAuthPaymentType) {
        this.lAuthPaymentType = lAuthPaymentType;
    }

    public List<Integer> getlAuthReport() {
        return lAuthReport;
    }

    public void setlAuthReport(List<Integer> lAuthReport) {
        this.lAuthReport = lAuthReport;
    }

    public boolean isIsManagerReturn() {
        return isManagerReturn;
    }

    public void setIsManagerReturn(boolean isManagerReturn) {
        this.isManagerReturn = isManagerReturn;
    }

    public boolean isIsManagerPumpScreen() {
        return isManagerPumpScreen;
    }

    public void setIsManagerPumpScreen(boolean isManagerPumpScreen) {
        this.isManagerPumpScreen = isManagerPumpScreen;
    }

    public boolean isIsCentralIntegration() {
        return isCentralIntegration;
    }

    public void setIsCentralIntegration(boolean isCentralIntegration) {
        this.isCentralIntegration = isCentralIntegration;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getUscIpAddress() {
        return uscIpAddress;
    }

    public void setUscIpAddress(String uscIpAddress) {
        this.uscIpAddress = uscIpAddress;
    }

    public int getUscPort() {
        return uscPort;
    }

    public void setUscPort(int uscPort) {
        this.uscPort = uscPort;
    }

    public int getUscProtocol() {
        return uscProtocol;
    }

    public void setUscProtocol(int uscProtocol) {
        this.uscProtocol = uscProtocol;
    }

    public String getWebServiceUserName() {
        return webServiceUserName;
    }

    public void setWebServiceUserName(String webServiceUserName) {
        this.webServiceUserName = webServiceUserName;
    }

    public String getWebServicePassword() {
        return webServicePassword;
    }

    public void setWebServicePassword(String webServicePassword) {
        this.webServicePassword = webServicePassword;
    }

    @Override
    public String toString() {
        return this.getBranch().getName();
    }

    public String getwSendPoint() {
        return wSendPoint;
    }

    public void setwSendPoint(String wSendPoint) {
        this.wSendPoint = wSendPoint;
    }

    public String getLocalServerIpAddress() {
        return localServerIpAddress;
    }

    public void setLocalServerIpAddress(String localServerIpAddress) {
        this.localServerIpAddress = localServerIpAddress;
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public int getAutomationId() {
        return automationId;
    }

    public void setAutomationId(int automationId) {
        this.automationId = automationId;
    }

    public int getWashingTypeId() {
        return washingTypeId;
    }

    public void setWashingTypeId(int washingTypeId) {
        this.washingTypeId = washingTypeId;
    }

    public int getWashingType() {
        return washingType;
    }

    public void setWashingType(int washingType) {
        this.washingType = washingType;
    }

    public boolean isIsDocumentCreditNow() {
        return isDocumentCreditNow;
    }

    public void setIsDocumentCreditNow(boolean isDocumentCreditNow) {
        this.isDocumentCreditNow = isDocumentCreditNow;
    }

    public boolean isIsPurchaseControl() {
        return isPurchaseControl;
    }

    public void setIsPurchaseControl(boolean isPurchaseControl) {
        this.isPurchaseControl = isPurchaseControl;
    }

    public boolean isIsReturnWithoutReceipt() {
        return isReturnWithoutReceipt;
    }

    public void setIsReturnWithoutReceipt(boolean isReturnWithoutReceipt) {
        this.isReturnWithoutReceipt = isReturnWithoutReceipt;
    }

    public Date getPastPeriodClosingDate() {
        return pastPeriodClosingDate;
    }

    public void setPastPeriodClosingDate(Date pastPeriodClosingDate) {
        this.pastPeriodClosingDate = pastPeriodClosingDate;
    }

    public boolean isIsContinueCrError() {
        return isContinueCrError;
    }

    public void setIsContinueCrError(boolean isContinueCrError) {
        this.isContinueCrError = isContinueCrError;
    }

    public BigDecimal getRoundingConstraint() {
        return roundingConstraint;
    }

    public void setRoundingConstraint(BigDecimal roundingConstraint) {
        this.roundingConstraint = roundingConstraint;
    }

    public boolean isIsUnitPriceAffectedByDiscount() {
        return isUnitPriceAffectedByDiscount;
    }

    public void setIsUnitPriceAffectedByDiscount(boolean isUnitPriceAffectedByDiscount) {
        this.isUnitPriceAffectedByDiscount = isUnitPriceAffectedByDiscount;
    }

    public int getPurchaseUnitPriceUpdateOptionId() {
        return purchaseUnitPriceUpdateOptionId;
    }

    public void setPurchaseUnitPriceUpdateOptionId(int purchaseUnitPriceUpdateOptionId) {
        this.purchaseUnitPriceUpdateOptionId = purchaseUnitPriceUpdateOptionId;
    }

    public boolean isIsForeignCurrency() {
        return isForeignCurrency;
    }

    public void setIsForeignCurrency(boolean isForeignCurrency) {
        this.isForeignCurrency = isForeignCurrency;
    }

    public BigDecimal getProfitabilityTolerance() {
        return profitabilityTolerance;
    }

    public void setProfitabilityTolerance(BigDecimal profitabilityTolerance) {
        this.profitabilityTolerance = profitabilityTolerance;
    }

    public Categorization getIntegrationCategorization() {
        return integrationCategorization;
    }

    public void setIntegrationCategorization(Categorization integrationCategorization) {
        this.integrationCategorization = integrationCategorization;
    }

    public int getErpIntegrationId() {
        return erpIntegrationId;
    }

    public void setErpIntegrationId(int erpIntegrationId) {
        this.erpIntegrationId = erpIntegrationId;
    }

    public String getErpUrl() {
        return erpUrl;
    }

    public void setErpUrl(String erpUrl) {
        this.erpUrl = erpUrl;
    }

    public String getErpUsername() {
        return erpUsername;
    }

    public void setErpUsername(String erpUsername) {
        this.erpUsername = erpUsername;
    }

    public String getErpPassword() {
        return erpPassword;
    }

    public void setErpPassword(String erpPassword) {
        this.erpPassword = erpPassword;
    }

    public int getErpTimeout() {
        return erpTimeout;
    }

    public void setErpTimeout(int erpTimeout) {
        this.erpTimeout = erpTimeout;
    }

    public String getAutomationUrl() {
        return automationUrl;
    }

    public void setAutomationUrl(String automationUrl) {
        this.automationUrl = automationUrl;
    }

    public String getAutomationUserName() {
        return automationUserName;
    }

    public void setAutomationUserName(String automationUserName) {
        this.automationUserName = automationUserName;
    }

    public String getAutomationPassword() {
        return automationPassword;
    }

    public void setAutomationPassword(String automationPassword) {
        this.automationPassword = automationPassword;
    }

    public int getAutomationTimeOut() {
        return automationTimeOut;
    }

    public void setAutomationTimeOut(int automationTimeOut) {
        this.automationTimeOut = automationTimeOut;
    }

    public String getErpEntegrationCode() {
        return erpEntegrationCode;
    }

    public void setErpEntegrationCode(String erpEntegrationCode) {
        this.erpEntegrationCode = erpEntegrationCode;
    }

    public String getStarbucksWebServiceUrl() {
        return starbucksWebServiceUrl;
    }

    public void setStarbucksWebServiceUrl(String starbucksWebServiceUrl) {
        this.starbucksWebServiceUrl = starbucksWebServiceUrl;
    }

    public String getStarbucksMachicneName() {
        return starbucksMachicneName;
    }

    public void setStarbucksMachicneName(String starbucksMachicneName) {
        this.starbucksMachicneName = starbucksMachicneName;
    }

    public String getStarbucksApiKey() {
        return starbucksApiKey;
    }

    public void setStarbucksApiKey(String starbucksApiKey) {
        this.starbucksApiKey = starbucksApiKey;
    }

    public String getParoUrl() {
        return paroUrl;
    }

    public void setParoUrl(String paroUrl) {
        this.paroUrl = paroUrl;
    }

    public String getParoAccountCode() {
        return paroAccountCode;
    }

    public void setParoAccountCode(String paroAccountCode) {
        this.paroAccountCode = paroAccountCode;
    }

    public String getParoBranchCode() {
        return paroBranchCode;
    }

    public void setParoBranchCode(String paroBranchCode) {
        this.paroBranchCode = paroBranchCode;
    }

    public String getParoResponsibleCode() {
        return paroResponsibleCode;
    }

    public void setParoResponsibleCode(String paroResponsibleCode) {
        this.paroResponsibleCode = paroResponsibleCode;
    }

    public boolean isIsAllBranch() {
        return isAllBranch;
    }

    public void setIsAllBranch(boolean isAllBranch) {
        this.isAllBranch = isAllBranch;
    }

    public boolean isIsShowPassiveAccount() {
        return isShowPassiveAccount;
    }

    public void setIsShowPassiveAccount(boolean isShowPassiveAccount) {
        this.isShowPassiveAccount = isShowPassiveAccount;
    }

    public boolean isIsProcessPassiveAccount() {
        return isProcessPassiveAccount;
    }

    public void setIsProcessPassiveAccount(boolean isProcessPassiveAccount) {
        this.isProcessPassiveAccount = isProcessPassiveAccount;
    }

    public boolean isIsMinusMainSafe() {
        return isMinusMainSafe;
    }

    public void setIsMinusMainSafe(boolean isMinusMainSafe) {
        this.isMinusMainSafe = isMinusMainSafe;
    }

    public boolean isIsInvoiceStockSalePriceList() {
        return isInvoiceStockSalePriceList;
    }

    public void setIsInvoiceStockSalePriceList(boolean isInvoiceStockSalePriceList) {
        this.isInvoiceStockSalePriceList = isInvoiceStockSalePriceList;
    }

    public String geteInvoiceTagInfo() {
        return eInvoiceTagInfo;
    }

    public void seteInvoiceTagInfo(String eInvoiceTagInfo) {
        this.eInvoiceTagInfo = eInvoiceTagInfo;
    }

    public String geteArchivePrefix() {
        return eArchivePrefix;
    }

    public void seteArchivePrefix(String eArchivePrefix) {
        this.eArchivePrefix = eArchivePrefix;
    }

    public String geteInvoicePrefix() {
        return eInvoicePrefix;
    }

    public void seteInvoicePrefix(String eInvoicePrefix) {
        this.eInvoicePrefix = eInvoicePrefix;
    }

    public int geteInvoiceIntegrationTypeId() {
        return eInvoiceIntegrationTypeId;
    }

    public void seteInvoiceIntegrationTypeId(int eInvoiceIntegrationTypeId) {
        this.eInvoiceIntegrationTypeId = eInvoiceIntegrationTypeId;
    }

    public String geteInvoiceAccountCode() {
        return eInvoiceAccountCode;
    }

    public void seteInvoiceAccountCode(String eInvoiceAccountCode) {
        this.eInvoiceAccountCode = eInvoiceAccountCode;
    }

    public String geteInvoiceUrl() {
        return eInvoiceUrl;
    }

    public void seteInvoiceUrl(String eInvoiceUrl) {
        this.eInvoiceUrl = eInvoiceUrl;
    }

    public String geteInvoiceUserName() {
        return eInvoiceUserName;
    }

    public void seteInvoiceUserName(String eInvoiceUserName) {
        this.eInvoiceUserName = eInvoiceUserName;
    }

    public String geteInvoicePassword() {
        return eInvoicePassword;
    }

    public void seteInvoicePassword(String eInvoicePassword) {
        this.eInvoicePassword = eInvoicePassword;
    }

    public boolean isIsEInvoice() {
        return isEInvoice;
    }

    public void setIsEInvoice(boolean isEInvoice) {
        this.isEInvoice = isEInvoice;
    }

    public boolean isIsProductRemoval() {
        return isProductRemoval;
    }

    public void setIsProductRemoval(boolean isProductRemoval) {
        this.isProductRemoval = isProductRemoval;
    }

    public boolean isIsTaxMandatory() {
        return isTaxMandatory;
    }

    public void setIsTaxMandatory(boolean isTaxMandatory) {
        this.isTaxMandatory = isTaxMandatory;
    }

    public String getApplicationServerUrl() {
        return applicationServerUrl;
    }

    public void setApplicationServerUrl(String applicationServerUrl) {
        this.applicationServerUrl = applicationServerUrl;
    }

    public Boolean getIsCashierEnterCashShift() {
        return isCashierEnterCashShift;
    }

    public void setIsCashierEnterCashShift(Boolean isCashierEnterCashShift) {
        this.isCashierEnterCashShift = isCashierEnterCashShift;
    }

    public int getWsConnectionTimeOut() {
        return wsConnectionTimeOut;
    }

    public void setWsConnectionTimeOut(int wsConnectionTimeOut) {
        this.wsConnectionTimeOut = wsConnectionTimeOut;
    }

    public int getWsRequestTimeOut() {
        return wsRequestTimeOut;
    }

    public void setWsRequestTimeOut(int wsRequestTimeOut) {
        this.wsRequestTimeOut = wsRequestTimeOut;
    }

    public int getParoConnectionTimeOut() {
        return paroConnectionTimeOut;
    }

    public void setParoConnectionTimeOut(int paroConnectionTimeOut) {
        this.paroConnectionTimeOut = paroConnectionTimeOut;
    }

    public int getParoRequestTimeOut() {
        return paroRequestTimeOut;
    }

    public void setParoRequestTimeOut(int paroRequestTimeOut) {
        this.paroRequestTimeOut = paroRequestTimeOut;
    }

    public String getPrintSaleType() {
        return printSaleType;
    }

    public void setPrintSaleType(String printSaleType) {
        this.printSaleType = printSaleType;
    }

    public boolean isIsManagerAutomatProduct() {
        return isManagerAutomatProduct;
    }

    public void setIsManagerAutomatProduct(boolean isManagerAutomatProduct) {
        this.isManagerAutomatProduct = isManagerAutomatProduct;
    }

    public BankAccount getAutomationBankAccount() {
        return automationBankAccount;
    }

    public void setAutomationBankAccount(BankAccount automationBankAccount) {
        this.automationBankAccount = automationBankAccount;
    }

    public String getAutomationTestKeyword() {
        return automationTestKeyword;
    }

    public void setAutomationTestKeyword(String automationTestKeyword) {
        this.automationTestKeyword = automationTestKeyword;
    }

    public String getAutoFileCreatePassword() {
        return autoFileCreatePassword;
    }

    public void setAutoFileCreatePassword(String autoFileCreatePassword) {
        this.autoFileCreatePassword = autoFileCreatePassword;
    }

    public String getAutoFileCreateUrl() {
        return autoFileCreateUrl;
    }

    public void setAutoFileCreateUrl(String autoFileCreateUrl) {
        this.autoFileCreateUrl = autoFileCreateUrl;
    }

    public String getAutoFileCreateUserName() {
        return autoFileCreateUserName;
    }

    public void setAutoFileCreateUserName(String autoFileCreateUserName) {
        this.autoFileCreateUserName = autoFileCreateUserName;
    }

    public int getAutoFileCreateType() {
        return autoFileCreateType;
    }

    public void setAutoFileCreateType(int autoFileCreateType) {
        this.autoFileCreateType = autoFileCreateType;
    }

    public String getWashingMachicneUrl() {
        return washingMachicneUrl;
    }

    public void setWashingMachicneUrl(String washingMachicneUrl) {
        this.washingMachicneUrl = washingMachicneUrl;
    }

    public String getWashingMachicneOfflineUrl() {
        return washingMachicneOfflineUrl;
    }

    public void setWashingMachicneOfflineUrl(String washingMachicneOfflineUrl) {
        this.washingMachicneOfflineUrl = washingMachicneOfflineUrl;
    }

    public String getWashingMachicneUsername() {
        return washingMachicneUsername;
    }

    public void setWashingMachicneUsername(String washingMachicneUsername) {
        this.washingMachicneUsername = washingMachicneUsername;
    }

    public String getWashingMachicnePassword() {
        return washingMachicnePassword;
    }

    public void setWashingMachicnePassword(String washingMachicnePassword) {
        this.washingMachicnePassword = washingMachicnePassword;
    }

    public int geteInvoiceCount() {
        return eInvoiceCount;
    }

    public void seteInvoiceCount(int eInvoiceCount) {
        this.eInvoiceCount = eInvoiceCount;
    }

    public int geteArchiveCount() {
        return eArchiveCount;
    }

    public void seteArchiveCount(int eArchiveCount) {
        this.eArchiveCount = eArchiveCount;
    }

    public Account getAutomationScoreAccount() {
        return automationScoreAccount;
    }

    public void setAutomationScoreAccount(Account automationScoreAccount) {
        this.automationScoreAccount = automationScoreAccount;
    }

    public boolean isIsPurchaseInvoiceProductSupplierUpdate() {
        return isPurchaseInvoiceProductSupplierUpdate;
    }

    public void setIsPurchaseInvoiceProductSupplierUpdate(boolean isPurchaseInvoiceProductSupplierUpdate) {
        this.isPurchaseInvoiceProductSupplierUpdate = isPurchaseInvoiceProductSupplierUpdate;
    }

    public boolean isIsErpUseShift() {
        return isErpUseShift;
    }

    public void setIsErpUseShift(boolean isErpUseShift) {
        this.isErpUseShift = isErpUseShift;
    }

    public boolean isIsCashierEnterBarcode() {
        return isCashierEnterBarcode;
    }

    public void setIsCashierEnterBarcode(boolean isCashierEnterBarcode) {
        this.isCashierEnterBarcode = isCashierEnterBarcode;
    }

    public boolean isIsCashierUseTheSyncButton() {
        return isCashierUseTheSyncButton;
    }

    public void setIsCashierUseTheSyncButton(boolean isCashierUseTheSyncButton) {
        this.isCashierUseTheSyncButton = isCashierUseTheSyncButton;
    }

    public boolean isIsCashierEnterQuantity() {
        return isCashierEnterQuantity;
    }

    public void setIsCashierEnterQuantity(boolean isCashierEnterQuantity) {
        this.isCashierEnterQuantity = isCashierEnterQuantity;
    }

    public String getMagiclickUrl() {
        return magiclickUrl;
    }

    public void setMagiclickUrl(String magiclickUrl) {
        this.magiclickUrl = magiclickUrl;
    }

    public String getMagiclickConsumerKey() {
        return magiclickConsumerKey;
    }

    public void setMagiclickConsumerKey(String magiclickConsumerKey) {
        this.magiclickConsumerKey = magiclickConsumerKey;
    }

    public String getMagiclickConsumerSecret() {
        return magiclickConsumerSecret;
    }

    public void setMagiclickConsumerSecret(String magiclickConsumerSecret) {
        this.magiclickConsumerSecret = magiclickConsumerSecret;
    }

    public int getMagiclickTimeOut() {
        return magiclickTimeOut;
    }

    public void setMagiclickTimeOut(int magiclickTimeOut) {
        this.magiclickTimeOut = magiclickTimeOut;
    }

    public BigDecimal getMinStockQuantity() {
        return minStockQuantity;
    }

    public void setMinStockQuantity(BigDecimal minStockQuantity) {
        this.minStockQuantity = minStockQuantity;
    }

    public String getParoCenterAccountCode() {
        return paroCenterAccountCode;
    }

    public void setParoCenterAccountCode(String paroCenterAccountCode) {
        this.paroCenterAccountCode = paroCenterAccountCode;
    }

    public String getParoCenterResponsibleCode() {
        return paroCenterResponsibleCode;
    }

    public void setParoCenterResponsibleCode(String paroCenterResponsibleCode) {
        this.paroCenterResponsibleCode = paroCenterResponsibleCode;
    }

    public BankAccount getAutomationPaymentBankAccount() {
        return automationPaymentBankAccount;
    }

    public boolean isIsCashierStockInventory() {
        return isCashierStockInventory;
    }

    public void setIsCashierStockInventory(boolean isCashierStockInventory) {
        this.isCashierStockInventory = isCashierStockInventory;
    }

    public void setAutomationPaymentBankAccount(BankAccount automationPaymentBankAccount) {
        this.automationPaymentBankAccount = automationPaymentBankAccount;
    }

    public int getShiftCurrencyRounding() {
        return shiftCurrencyRounding;
    }

    public void setShiftCurrencyRounding(int shiftCurrencyRounding) {
        this.shiftCurrencyRounding = shiftCurrencyRounding;
    }

    public boolean isIsPassiveGet() {
        return isPassiveGet;
    }

    public void setIsPassiveGet(boolean isPassiveGet) {
        this.isPassiveGet = isPassiveGet;
    }

    public Date getGetInOperableDate() {
        return getInOperableDate;
    }

    public void setGetInOperableDate(Date getInOperableDate) {
        this.getInOperableDate = getInOperableDate;
    }

    public int getSpecialItem() {
        return specialItem;
    }

    public void setSpecialItem(int specialItem) {
        this.specialItem = specialItem;
    }

    public BigDecimal getOrderDeliveryRate() {
        return orderDeliveryRate;
    }

    public void setOrderDeliveryRate(BigDecimal orderDeliveryRate) {
        this.orderDeliveryRate = orderDeliveryRate;
    }

    public BigDecimal getGeneralOrderDeliveryRate() {
        return generalOrderDeliveryRate;
    }

    public void setGeneralOrderDeliveryRate(BigDecimal generalOrderDeliveryRate) {
        this.generalOrderDeliveryRate = generalOrderDeliveryRate;
    }

    public boolean isIsWashingSaleZReport() {
        return isWashingSaleZReport;
    }

    public void setIsWashingSaleZReport(boolean isWashingSaleZReport) {
        this.isWashingSaleZReport = isWashingSaleZReport;
    }
    
    @Override
    public int hashCode() {
        return this.getId();
    }

}
