/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.presentation;

import com.mepsan.marwiz.finance.bank.business.IBankBranchService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.finance.chequebill.business.IChequeBillService;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class ChequeBillBean extends GeneralDefinitionBean<ChequeBill> {

    private Object object;
    private int processType;
    private List<DocumentNumber> listOfDocumentNumber;
    private List<BankBranch> listOfBankBranch;
    private String exchange;
    private int chequeBillType;
    private boolean isDisabled;

    private List<Branch> listOfBranch;
    String createWhere;

    @ManagedProperty(value = "#{chequeBillService}")
    public IChequeBillService chequeBillService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    @ManagedProperty(value = "#{documentNumberService}")
    public IDocumentNumberService documentNumberService;

    @ManagedProperty(value = "#{bankBranchService}")
    public IBankBranchService bankBranchService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{applicationBean}")
    public ApplicationBean applicationBean;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    public class ChequeBillParam {

        private List<Branch> selectedBranchList;

        public ChequeBillParam() {
            this.selectedBranchList = new ArrayList<>();
        }

        public List<Branch> getSelectedBranchList() {
            return selectedBranchList;
        }

        public void setSelectedBranchList(List<Branch> selectedBranchList) {
            this.selectedBranchList = selectedBranchList;
        }

    }

    private ChequeBillParam searchObject;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<DocumentNumber> getListOfDocumentNumber() {
        return listOfDocumentNumber;
    }

    public void setListOfDocumentNumber(List<DocumentNumber> listOfDocumentNumber) {
        this.listOfDocumentNumber = listOfDocumentNumber;
    }

    public List<BankBranch> getListOfBankBranch() {
        return listOfBankBranch;
    }

    public void setListOfBankBranch(List<BankBranch> listOfBankBranch) {
        this.listOfBankBranch = listOfBankBranch;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setChequeBillService(IChequeBillService chequeBillService) {
        this.chequeBillService = chequeBillService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setDocumentNumberService(IDocumentNumberService documentNumberService) {
        this.documentNumberService = documentNumberService;
    }

    public void setBankBranchService(IBankBranchService bankBranchService) {
        this.bankBranchService = bankBranchService;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public int getChequeBillType() {
        return chequeBillType;
    }

    public void setChequeBillType(int chequeBillType) {
        this.chequeBillType = chequeBillType;
    }

    public boolean isIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public ChequeBillParam getSearchObject() {
        return searchObject;
    }

    public void setSearchObject(ChequeBillParam searchObject) {
        this.searchObject = searchObject;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-----------ChequeBillBean");
        searchObject = new ChequeBillParam();
        listOfBranch = new ArrayList<>();

        object = new Object();
        toogleList = new ArrayList<>();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true);
        }

        listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof ChequeBillParam) {
                    searchObject = (ChequeBillParam) (((ArrayList) sessionBean.parameter).get(i));
                    List<Branch> temp = new ArrayList<>();
                    temp.addAll(searchObject.getSelectedBranchList());
                    searchObject.getSelectedBranchList().clear();
                    for (Branch br : listOfBranch) {
                        for (Branch sbr : temp) {
                            if (br.getId() == sbr.getId()) {
                                searchObject.getSelectedBranchList().add(br);
                            }
                        }
                    }
                    break;
                }
            }
        } else {
            for (Branch br : listOfBranch) {
                if (br.getId() == sessionBean.getUser().getLastBranch().getId()) {
                    searchObject.getSelectedBranchList().add(br);
                    break;
                }
            }
        }
        find();

        setListBtn(sessionBean.checkAuthority(new int[]{25, 26}, 0));

    }

    @Override
    public void create() {
        selectedObject = new ChequeBill();

        for (Branch b : listOfBranch) {
            if (b.getId() == sessionBean.getUser().getLastBranch().getId()) {
                selectedObject.getBranch().setId(b.getId());
                break;
            }
        }

        System.out.println("----chequeBillType----" + chequeBillType);
        switch (chequeBillType) {
            case 1:
                // müşteri çeki
                selectedObject.setIsCheque(true);
                selectedObject.setIsCustomer(true);
                break;
            case 2:
                // borç çeki
                selectedObject.setIsCheque(true);
                selectedObject.setIsCustomer(false);
                break;
            case 3:
                // müşteri senedi
                selectedObject.setIsCheque(false);
                selectedObject.setIsCustomer(true);
                break;
            case 4:
                // borç senedi
                selectedObject.setIsCheque(false);
                selectedObject.setIsCustomer(false);
                break;
            default:
                selectedObject.setIsCheque(true);
                selectedObject.setIsCustomer(true);
                break;
        }

        selectedObject.getStatus().setId(31);
        selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
        changeExchange();
        Parameter parameter = applicationBean.getParameterMap().get("default_country");
        selectedObject.getCountry().setId(Integer.parseInt(parameter.getValue()));
        citiesAndCountiesBean.updateCity(selectedObject.getCountry(), selectedObject.getPaymentCity(), null);
        changeBranch();
        listOfBankBranch = bankBranchService.selectBankBranch();
        processType = 1;
        RequestContext.getCurrentInstance().execute("PF('dlg_chequebillproc').show();");
    }

    public void find() {
        createWhere = chequeBillService.createWhere(searchObject, listOfBranch);
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmChequeBill:dtbChequeBill");
    }

    //cari seçildiğinde calısır
    public void updateAllInformation() throws IOException {
        if (accountBookFilterBean.getSelectedData() != null) {
            selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmNewChequeBill:txtAccountName");
        }
    }

    /**
     * bu metot para birimi değiştiğinde tetiklenir.
     */
    public void changeExchange() {
        selectedObject.setExchangeRate(exchangeService.bringExchangeRate(selectedObject.getCurrency(), sessionBean.getUser().getLastBranch().getCurrency(), sessionBean.getUser()));
        exchange = sessionBean.currencySignOrCode(selectedObject.getCurrency().getId(), 0) + " -> " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);// örn: $->€

    }

    public void bringDocument() {
        for (DocumentNumber dn : listOfDocumentNumber) {
            if (dn.getId() == selectedObject.getDocumentNumber().getId()) {
                selectedObject.getDocumentNumber().setActualNumber(dn.getActualNumber());
                selectedObject.getDocumentNumber().setSerial(dn.getSerial());
                selectedObject.setDocumentSerial(dn.getSerial());
                selectedObject.setDocumentNo("" + dn.getActualNumber());
            }
        }
    }

    public void update() {
        List<Object> list = new ArrayList<>();
        list.add(searchObject);
        list.add(selectedObject);
        marwiz.goToPage("/pages/finance/chequebill/chequebillprocess.xhtml", list, 0, 81);
    }

    @Override
    public void save() {
        int result = 0;
        if (processType == 1) {
            selectedObject.setRemainingMoney(selectedObject.getTotalMoney());
            result = chequeBillService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                List<Object> list = new ArrayList<>();
                list.add(searchObject);
                list.add(selectedObject);
                marwiz.goToPage("/pages/finance/chequebill/chequebillprocess.xhtml", list, 1, 81);
                sessionBean.createUpdateMessage(result);
            }

        }
    }

    public List<ChequeBill> findall(String where) {
        return chequeBillService.findAll(chequeBillType, where);
    }

    public void changeChequeBillType() {
        listOfObjects = findall(createWhere);
    }

    @Override
    public List<ChequeBill> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeBranch() {
        selectedObject.getAccount().setId(0);
        selectedObject.getAccount().setName("");
        selectedObject.getAccount().setTitle("");
        listOfDocumentNumber = documentNumberService.listOfDocumentNumber(new Item(18), selectedObject.getBranch());//çek senet için seri numarları çektik.
    }

}
