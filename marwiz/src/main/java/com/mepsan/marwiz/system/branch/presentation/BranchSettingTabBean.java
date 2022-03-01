/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 01:51:32
 */
package com.mepsan.marwiz.system.branch.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.categorization.business.ICategorizationService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.pointofsale.business.IPointOfSaleService;
import com.mepsan.marwiz.service.backup.business.IGetBackUpParameterService;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.DualListModel;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@ManagedBean
@ViewScoped
public class BranchSettingTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{categorizationService}")
    private ICategorizationService categorizationService;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{getBackUpParameterService}")
    public IGetBackUpParameterService getBackUpParameterService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{pointOfSaleService}")
    private IPointOfSaleService pointOfSaleService;

    private Branch selectedObject;
    private BranchSetting branchSetting;
    private List<String> listOfSelectedPages;
    private List<Branch> branchList;//Sistem Şube İşlemlerinden Geldi ise Bu Liste Kullanılır.
    private List<Categorization> listCategorization;

    private DualListModel<Type> receiptSalesTypeList;
    private DualListModel<Type> authSalesTypeList;
    private List<Type> receiptSType, droppedReceiptSType;
    private List<Type> authSType, droppedSType;
    private String password;
    private int automationId;
    private List<String> selectedSaleType;
    private List<Type> printSalesTypeList;
    private boolean isNormal;
    private List<BankAccount> listOfBankAccount;
    private int controlStarbucksUrl;
    private List<PointOfSale> listOfPointOfSale, listOfSelectablePOS;
    private PointOfSale selectedPointOfSale, selectedDialogPointOfSale;
    private int specialItem;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public DualListModel<Type> getReceiptSalesTypeList() {
        return receiptSalesTypeList;
    }

    public void setReceiptSalesTypeList(DualListModel<Type> receiptSalesTypeList) {
        this.receiptSalesTypeList = receiptSalesTypeList;
    }

    public DualListModel<Type> getAuthSalesTypeList() {
        return authSalesTypeList;
    }

    public void setAuthSalesTypeList(DualListModel<Type> authSalesTypeList) {
        this.authSalesTypeList = authSalesTypeList;
    }

    public List<String> getListOfSelectedPages() {
        return listOfSelectedPages;
    }

    public void setListOfSelectedPages(List<String> listOfSelectedPages) {
        this.listOfSelectedPages = listOfSelectedPages;
    }

    public Marwiz getMarwiz() {
        return marwiz;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public void setCategorizationService(ICategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    public List<Categorization> getListCategorization() {
        return listCategorization;
    }

    public void setListCategorization(List<Categorization> listCategorization) {
        this.listCategorization = listCategorization;
    }

    public List<String> getSelectedSaleType() {
        return selectedSaleType;
    }

    public void setSelectedSaleType(List<String> selectedSaleType) {
        this.selectedSaleType = selectedSaleType;
    }

    public List<Type> getPrintSalesTypeList() {
        return printSalesTypeList;
    }

    public void setPrintSalesTypeList(List<Type> printSalesTypeList) {
        this.printSalesTypeList = printSalesTypeList;
    }

    public boolean isIsNormal() {
        return isNormal;
    }

    public void setIsNormal(boolean isNormal) {
        this.isNormal = isNormal;
    }

    public List<BankAccount> getListOfBankAccount() {
        return listOfBankAccount;
    }

    public void setListOfBankAccount(List<BankAccount> listOfBankAccount) {
        this.listOfBankAccount = listOfBankAccount;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setGetBackUpParameterService(IGetBackUpParameterService getBackUpParameterService) {
        this.getBackUpParameterService = getBackUpParameterService;
    }

    public Branch getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Branch selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<PointOfSale> getListOfPointOfSale() {
        return listOfPointOfSale;
    }

    public void setListOfPointOfSale(List<PointOfSale> listOfPointOfSale) {
        this.listOfPointOfSale = listOfPointOfSale;
    }

    public PointOfSale getSelectedPointOfSale() {
        return selectedPointOfSale;
    }

    public void setSelectedPointOfSale(PointOfSale selectedPointOfSale) {
        this.selectedPointOfSale = selectedPointOfSale;
    }

    public void setPointOfSaleService(IPointOfSaleService pointOfSaleService) {
        this.pointOfSaleService = pointOfSaleService;
    }

    public PointOfSale getSelectedDialogPointOfSale() {
        return selectedDialogPointOfSale;
    }

    public void setSelectedDialogPointOfSale(PointOfSale selectedDialogPointOfSale) {
        this.selectedDialogPointOfSale = selectedDialogPointOfSale;
    }

    public List<PointOfSale> getListOfSelectablePOS() {
        return listOfSelectablePOS;
    }

    public void setListOfSelectablePOS(List<PointOfSale> listOfSelectablePOS) {
        this.listOfSelectablePOS = listOfSelectablePOS;
    }

    public int getSpecialItem() {
        return specialItem;
    }

    public void setSpecialItem(int specialItem) {
        this.specialItem = specialItem;
    }

    @PostConstruct
    public void init() {
        listOfSelectedPages = new ArrayList<>();
        selectedObject = new Branch();
        listCategorization = new ArrayList<>();
        selectedSaleType = new ArrayList<>();
        printSalesTypeList = new ArrayList<>();
        listOfBankAccount = new ArrayList<>();
        listOfPointOfSale = new ArrayList<>();
        selectedPointOfSale = new PointOfSale();
        selectedDialogPointOfSale = new PointOfSale();
        listOfSelectablePOS = new ArrayList<>();
        isNormal = false;
        if (marwiz.getPageIdOfGoToPage() == 105) {//Sistem Şube İşlemlerinden Geldi İSe
            branchList = new ArrayList<>();
            branchList = branchService.findAll(" AND br.id = " + sessionBean.getUser().getLastBranch().getId());
            if (branchList.size() > 0) {
                selectedObject = branchList.get(0);
            }
            sessionBean.setParameter(selectedObject);
        }
        if (sessionBean.parameter instanceof Branch) {
            selectedObject = (Branch) sessionBean.parameter;
            bringAllSettings();
            findSalesTypeList();
            findReportPageAuth();
            findPrintSalesType();
        }

        Categorization obj = new Categorization();
        obj.setItem(new Item(Integer.valueOf(2)));
        listCategorization = categorizationService.listCategorization(obj);
        if (branchSetting.getPrintSaleType() != null) {

            String[] words = branchSetting.getPrintSaleType().split(",");
            for (int i = 0; i < words.length; i++) {
                selectedSaleType.add(words[i]);
                if (words[i].equalsIgnoreCase("80")) {
                    isNormal = true;
                }
            }
        }
        if (branchSetting.isIsCentralIntegration()) {
            if (branchSetting.getParoUrl() == null || branchSetting.getParoUrl().isEmpty()) {
                branchSetting.setParoUrl("https://webservis.paro.com.tr");
            }
            if (branchSetting.getParoConnectionTimeOut() == 0) {
                branchSetting.setParoConnectionTimeOut(5);
            }
            if (branchSetting.getParoRequestTimeOut() == 0) {
                branchSetting.setParoRequestTimeOut(5);
            }
        }
        listOfPointOfSale = pointOfSaleService.listIntegrationPointOfSale(selectedObject, " AND pos.integrationcode IS NOT NULL ");

        setListBtn(sessionBean.checkAuthority(new int[]{173, 198, 199}, 0));

        specialItem = branchSetting.getSpecialItem();
    }

    public void bringAllSettings() {
        branchSetting = new BranchSetting();
        branchSetting.setBranch(selectedObject);

        branchSetting = branchSettingService.find(branchSetting);
        //  branchSetting.setAutomationId(0);
//        //Stawiz+ ise aç
//        branchSetting.setIsManagerPumpScreen(branchSetting.getAutomationId()==1);
//        
////        if (branchSetting.getAutomationId() == 1) {
////            branchSetting.setIsManagerPumpScreen(true);
////        }

        password = branchSetting.getWebServicePassword();
        automationId = applicationBean.getAppService().controlAutomationSettingBranch(1);
        controlStarbucksUrl = applicationBean.getAppService().controlStarbucksSettingBranch();
        bringAutomationSetting();

    }

    public void save() {
        int result = 0;
        String saleTypeList = "";
        branchSetting.getlAuthPaymentType().clear();
        branchSetting.getlAuthReport().clear();
        branchSetting.getlPrintPaymentType().clear();

        if (branchSetting.getWebServicePassword() == null || branchSetting.getWebServicePassword().equals("")) {
            branchSetting.setWebServicePassword(password);
        }
        for (int i = 0; i < selectedSaleType.size(); i++) {
            saleTypeList = saleTypeList + "," + selectedSaleType.get(i);
        }

        if (!saleTypeList.equals("")) {
            saleTypeList = saleTypeList.substring(1, saleTypeList.length());
        }
        branchSetting.setPrintSaleType(saleTypeList);

        for (Type t : receiptSalesTypeList.getTarget()) {
            branchSetting.getlPrintPaymentType().add(t.getId());
        }
        for (Type t : authSalesTypeList.getTarget()) {
            branchSetting.getlAuthPaymentType().add(t.getId());
        }
        for (String a : listOfSelectedPages) {
            branchSetting.getlAuthReport().add(Integer.parseInt(a));
        }
        result = branchSettingService.update(branchSetting);

        password = branchSetting.getWebServicePassword();

        if (result > 0) {
            applicationBean.refreshBranchSetting();
            if (sessionBean.getUser().getLastBranch().getId() == selectedObject.getId()) {
                sessionBean.getUser().getLastBranchSetting().setIsCentralIntegration(branchSetting.isIsCentralIntegration());
                sessionBean.getUser().getLastBranchSetting().setIsEInvoice(branchSetting.isIsEInvoice());
                sessionBean.getUser().getLastBranchSetting().setIsMinusMainSafe(branchSetting.isIsMinusMainSafe());
                sessionBean.getUser().getLastBranchSetting().setIsShowPassiveAccount(branchSetting.isIsShowPassiveAccount());
                sessionBean.getUser().getLastBranchSetting().setIsProcessPassiveAccount(branchSetting.isIsProcessPassiveAccount());
                sessionBean.getUser().getLastBranchSetting().setIsInvoiceStockSalePriceList(branchSetting.isIsInvoiceStockSalePriceList());
                sessionBean.getUser().getLastBranchSetting().setIsTaxMandatory(branchSetting.isIsTaxMandatory());
                sessionBean.getUser().getLastBranchSetting().seteInvoicePrefix(branchSetting.geteInvoicePrefix());
                sessionBean.getUser().getLastBranchSetting().seteArchivePrefix(branchSetting.geteArchivePrefix());
                sessionBean.getUser().getLastBranchSetting().setIsPurchaseInvoiceProductSupplierUpdate(branchSetting.isIsPurchaseInvoiceProductSupplierUpdate());
                sessionBean.getUser().getLastBranchSetting().setParoUrl(branchSetting.getParoUrl());
                sessionBean.getUser().getLastBranchSetting().setIsShiftControl(branchSetting.isIsShiftControl());
                sessionBean.getUser().getLastBranchSetting().setShiftCurrencyRounding(branchSetting.getShiftCurrencyRounding());
            }
            if (automationId == 0 && branchSetting.getAutomationId() == 1) {// Stawiz Entegrasyonu önceden yoksa ve yenisi eklenen Stawizse job 'ı çalıştır!
                applicationBean.createSendAutomationShiftJob();
            } else if (automationId == 1 && applicationBean.getAppService().controlAutomationSettingBranch(1) == 0) {//Eskiden Stawiz entegrasyonu varsa ve şimdiki Stawiz kalmadıysa job'ı durdur!!
                try {
                    applicationBean.getScheduler().deleteJob(new JobKey("job_sendautomationshift", "group_sendautomationshift"));
                    TriggerKey triggerKey = new TriggerKey("trigger_sendautomationshift", "group_sendautomationshift");
                    applicationBean.getScheduler().unscheduleJob(triggerKey);
                } catch (SchedulerException ex) {
                    Logger.getLogger(BranchSettingTabBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            automationId = applicationBean.getAppService().controlAutomationSettingBranch(1);

            if (controlStarbucksUrl == 0 && branchSetting.getStarbucksWebServiceUrl() != null && !branchSetting.getStarbucksWebServiceUrl().equals("")) {// Starbucks Url önceden yoksa ve yenisi eklendiyse job 'ı çalıştır!
                applicationBean.createListStarbucksStockJob();
            } else if (controlStarbucksUrl == 1 && applicationBean.getAppService().controlStarbucksSettingBranch() == 0) {//Eskiden Starbucks url varsa ve şimdi kaldırıldıysa job'ı durdur!!
                try {
                    applicationBean.getScheduler().deleteJob(new JobKey("job_liststarbucksstock", "group_liststarbucksstock"));
                    TriggerKey triggerKey = new TriggerKey("trigger_liststarbucksstock", "group_liststarbucksstock");
                    applicationBean.getScheduler().unscheduleJob(triggerKey);
                } catch (SchedulerException ex) {
                    Logger.getLogger(BranchSettingTabBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            controlStarbucksUrl = applicationBean.getAppService().controlStarbucksSettingBranch();
        }
        sessionBean.createUpdateMessage(result);

    }

    public void findReportPageAuth() {
        listOfSelectedPages.clear();
        for (Integer a : branchSetting.getlAuthReport()) {
            listOfSelectedPages.add(Integer.toString(a));
        }
    }

    public void findSalesTypeList() {
        receiptSType = new ArrayList<>();
        droppedReceiptSType = new ArrayList<Type>();
        authSType = new ArrayList<>();
        droppedSType = new ArrayList<>();

        for (Type m : sessionBean.getTypes(15)) {
            if (branchSetting.getlPrintPaymentType().contains(m.getId()) && m.getId() != 66 && m.getId() != 67 && m.getId() != 68 && m.getId() != 69 && m.getId() != 75 && m.getId() != 20 && m.getId() != 106) {
                droppedReceiptSType.add(m);
            } else if (m.getId() != 66 && m.getId() != 67 && m.getId() != 68 && m.getId() != 69 && m.getId() != 75 && m.getId() != 20 && m.getId() != 106) {
                receiptSType.add(m);
            }
        }

        receiptSalesTypeList = new DualListModel<Type>(receiptSType, droppedReceiptSType);

        for (Type m : sessionBean.getTypes(15)) {
            if (branchSetting.getlAuthPaymentType().contains(m.getId()) && m.getId() != 66 && m.getId() != 67 && m.getId() != 68 && m.getId() != 69 && m.getId() != 75 && m.getId() != 20 && m.getId() != 106) {
                droppedSType.add(m);
            } else if (m.getId() != 66 && m.getId() != 67 && m.getId() != 68 && m.getId() != 69 && m.getId() != 75 && m.getId() != 20 && m.getId() != 106) {
                authSType.add(m);
            }
        }
        authSalesTypeList = new DualListModel<Type>(authSType, droppedSType);

    }

    public void findPrintSalesType() {

        for (Type m : sessionBean.getTypes(32)) {
            printSalesTypeList.add(m);
        }

    }

    public void updateIntegratorType() {

        if (!branchSetting.isIsEInvoice()) {

            branchSetting.seteInvoiceIntegrationTypeId(0);

        }

    }

    public void changePrintSaleType() {
        isNormal = false;
        for (int i = 0; i < selectedSaleType.size(); i++) {
            if (selectedSaleType.get(i).equalsIgnoreCase("80")) {
                isNormal = true;
            }
        }
    }

    public void bringAutomationSetting() {
        if (branchSetting.getAutomationId() == 2) {
            listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN(16, 104) ", selectedObject);
        }
    }

    public void updateAllInformation() {
        if (accountBookFilterBean.getSelectedData() != null) {
            branchSetting.setAutomationScoreAccount(accountBookFilterBean.getSelectedData());
            if (marwiz.getPageIdOfGoToPage() == 105) {
                RequestContext.getCurrentInstance().update("txtAutomationPointAccount");
            } else {
                RequestContext.getCurrentInstance().update("tbvBranchProcess:txtAutomationPointAccount");
            }

            accountBookFilterBean.setSelectedData(null);
        }
    }

    public void callWebServiceForBackup() {
        int result = 0;
        branchSetting.getBranch().setLicenceCode(selectedObject.getLicenceCode());
        result = getBackUpParameterService.listBackUpParameters(branchSetting);
        RequestContext.getCurrentInstance().execute("PF('dlg_RunWebService').hide();");
        sessionBean.createUpdateMessage(result);
    }

    public void callWebServiceForCampaignSetting() {
        branchSetting.getBranch().setId(selectedObject.getId());
        int result = 0;
        if (branchSetting.getParoUrl() != null && !branchSetting.getParoUrl().isEmpty()
                && selectedObject.getLicenceCode() != null && !selectedObject.getLicenceCode().isEmpty()) {
            WebServiceClient serviceClient = new WebServiceClient();
            String url = "";
            url = branchSetting.getParoUrl() + "/prjWebService/WsPodIslemleri?invoke=opetIstasyonVerisiSorgula&isyeriKod="
                      + branchSetting.getParoCenterAccountCode() + "&yetkiliKod=" + branchSetting.getParoCenterResponsibleCode() + "&uhkKod=" + selectedObject.getLicenceCode()
                      + "&islemTip=1&param1=";

            String resultMessage = serviceClient.requestGetMethod(url);
            if (resultMessage != null) {
                if (!resultMessage.isEmpty()) {
                    try {
                        String resultString = "";
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder;

                        builder = factory.newDocumentBuilder();
                        InputSource inputSource = new InputSource(new StringReader(resultMessage));
                        Document document = builder.parse(inputSource);
                        resultString = document.getElementsByTagName("return").item(0).getTextContent();
                        System.out.println("--resultString" + resultString);
                        DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder2;
                        String branchCode = "", pcID;
                        String responsibleCode = "";
                        String accountCode = "";
                        List<PointOfSale> pointOfSalesList = new ArrayList<>();
                        List<String> pcIDList = new ArrayList<>();
                        //Resonse İçerisinden Gelen Sonuç Çözümlenir.
                        builder2 = factory2.newDocumentBuilder();
                        InputSource inputSource2 = new InputSource(new StringReader(resultString));
                        Document document2 = builder2.parse(inputSource2);

                        NodeList nodeList = document2.getElementsByTagName("ISYERIPCID");
                        if (nodeList.getLength() > 0) {
                            for (int i = 0; i < nodeList.getLength(); i++) {
                                Node node = nodeList.item(i);
                                org.w3c.dom.Element element = null;
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    element = (org.w3c.dom.Element) node;

                                    //Tanı 1: UltraMarket  2: FullMarket  3: FullPaket 4:Opet
                                    //1- UltraMarket; 2- FullPaket; 3- FullMarket;
                                    int conceptType;
                                    if (selectedObject.getConceptType() == 1) {
                                        conceptType = 1;
                                    } else if (selectedObject.getConceptType() == 2) {
                                        conceptType = 3;
                                    } else {
                                        conceptType = 2;
                                    }
                                    if (Integer.parseInt(getTagValue("MARKAKODU", element)) == conceptType) {
                                        branchCode = getTagValue("SUBEKOD", element);
                                        responsibleCode = getTagValue("YETKILIKODU", element);
                                        accountCode = getTagValue("ISYERIKOD", element);
                                        branchSetting.setParoBranchCode(branchCode);
                                        branchSetting.setParoAccountCode(accountCode);
                                        branchSetting.setParoResponsibleCode(responsibleCode);
                                        pcID = getTagValue("PCID", element);
                                        pcIDList.add(pcID);
                                    }
                                }
                            }
                            if (!branchCode.equals("") && !branchCode.isEmpty() && !responsibleCode.equals("") && !responsibleCode.isEmpty()
                                    && !accountCode.equals("") && !accountCode.isEmpty()) {
                                result = branchSettingService.updateParoInformation(branchSetting, pcIDList);
                            }
                        }

                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(BranchSettingTabBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SAXException ex) {
                        Logger.getLogger(BranchSettingTabBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(BranchSettingTabBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("unsuccesfuloperation")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

        }
        sessionBean.createUpdateMessage(result);
        RequestContext.getCurrentInstance().execute("PF('dlg_RunWebServiceForCampaign').hide();");
        listOfPointOfSale = pointOfSaleService.listIntegrationPointOfSale(selectedObject, " AND pos.integrationcode IS NOT NULL ");
        RequestContext.getCurrentInstance().execute("updateDatatable()");
        if (marwiz.getPageIdOfGoToPage() == 105) {
            RequestContext.getCurrentInstance().update("tbvIntegrationSettings:txtParoBranchCode");
            RequestContext.getCurrentInstance().update("tbvIntegrationSettings:txtParoResponsibleCode");
            RequestContext.getCurrentInstance().update("tbvIntegrationSettings:txtParoWorkCode");
        } else {
            RequestContext.getCurrentInstance().update("tbvBranchProcess:tbvIntegrationSettings:txtParoBranchCode");
            RequestContext.getCurrentInstance().update("tbvBranchProcess:tbvIntegrationSettings:txtParoResponsibleCode");
            RequestContext.getCurrentInstance().update("tbvBranchProcess:tbvIntegrationSettings:txtParoWorkCode");
        }
    }

    private String getTagValue(String tag, org.w3c.dom.Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        if (node != null) {
            return node.getNodeValue();
        } else {
            return "";
        }
    }

    public void onCellEdit(CellEditEvent event) {
        int result = 0;
        selectedPointOfSale = FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{pointOfSale}", PointOfSale.class);

        result = pointOfSaleService.updateIntegrationCode(selectedPointOfSale);
        sessionBean.createUpdateMessage(result);

        if (result > 0) {
            listOfPointOfSale = pointOfSaleService.listIntegrationPointOfSale(selectedObject, " AND pos.integrationcode IS NOT NULL ");
            RequestContext.getCurrentInstance().execute("updateDatatable()");
        }

    }

    public void updateParoIntgrationCode() {
        selectedDialogPointOfSale = new PointOfSale();
        listOfSelectablePOS = pointOfSaleService.listIntegrationPointOfSale(selectedObject, " AND pos.integrationcode IS NULL ");
        RequestContext.getCurrentInstance().execute("PF('dlg_UpdateParoIntegrationCode').show();");
    }

    public void savePointOfSale() {
        int result = pointOfSaleService.updateIntegrationCode(selectedDialogPointOfSale);
        sessionBean.createUpdateMessage(result);
        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_UpdateParoIntegrationCode').hide();");
            listOfPointOfSale = pointOfSaleService.listIntegrationPointOfSale(selectedObject, " AND pos.integrationcode IS NOT NULL ");
            if (marwiz.getPageIdOfGoToPage() == 105) {
                RequestContext.getCurrentInstance().update("tbvIntegrationSettings:dtbPOS");
            } else {
                RequestContext.getCurrentInstance().update("tbvBranchProcess:tbvIntegrationSettings:dtbPOS");
            }

        }
    }

}
