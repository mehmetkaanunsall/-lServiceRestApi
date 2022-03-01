/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import com.mepsan.marwiz.system.officialaccounting.business.IOfficialAccountingService;
import com.mepsan.marwiz.system.officialaccounting.dao.TotalCount;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author ali.kurt
 */
@ManagedBean
@ViewScoped
public class OfficialAccountingBean extends GeneralDefinitionBean<OfficalAccounting> {

    @ManagedProperty(value = "#{officialAccountingService}")
    private IOfficialAccountingService officialAccountingService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private List<OfficalAccounting> listOfOfficialAccount, listOfSelected;
    private TotalCount totalCount;
    private int firstSelect, secondSelect, processType;
    private boolean isFind = false, isRetail = false, isSend = false;
    private int selectSize;
    private Date begin, end;
    private Date maxDate;
    private int cardType;
    private List<BranchSetting> listOfBranch;
    private BranchSetting selectedBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;

    public boolean isIsSend() {
        return isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public boolean isIsRetail() {
        return isRetail;
    }

    public void setIsRetail(boolean isRetail) {
        this.isRetail = isRetail;
    }

    public Date getBegin() {
        return begin;
    }

    public int getFirstSelect() {
        return firstSelect;
    }

    public void setFirstSelect(int firstSelect) {
        this.firstSelect = firstSelect;
    }

    public int getSecondSelect() {
        return secondSelect;
    }

    public void setSecondSelect(int secondSelect) {
        this.secondSelect = secondSelect;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public TotalCount getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(TotalCount totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public List<OfficalAccounting> getListOfSelected() {
        return listOfSelected;
    }

    public void setListOfSelected(List<OfficalAccounting> listOfSelected) {
        this.listOfSelected = listOfSelected;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setOfficialAccountingService(IOfficialAccountingService officialAccountingService) {
        this.officialAccountingService = officialAccountingService;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public BranchSetting getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch(BranchSetting selectedBranch) {
        this.selectedBranch = selectedBranch;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public List<OfficalAccounting> getListOfOfficialAccount() {
        return listOfOfficialAccount;
    }

    public void setListOfOfficialAccount(List<OfficalAccounting> listOfOfficialAccount) {
        this.listOfOfficialAccount = listOfOfficialAccount;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    @PostConstruct
    @Override
    public void init() {
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true);
        listOfSelected = new ArrayList<>();
        listOfObjects = new ArrayList<>();
        listOfBranch = new ArrayList<>();
        totalCount = new TotalCount();
        selectedBranch = new BranchSetting();
        listOfOfficialAccount = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        branchList = "";
        processType = 1;
        firstSelect = 0;
        isRetail = false;
        cardType = 1;

        listOfBranch = officialAccountingService.findBranch(); // kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) {
                selectedBranchList.add(branchSetting);
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedBranch = branchSetting;
                }
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedBranch = branchSetting;
                    selectedBranchList.add(branchSetting);
                    break;
                }
            }
        }
        changeBranch();

        end = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(end);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        end = c.getTime();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        begin = c.getTime();

        c.add(Calendar.MONTH, +2);
        maxDate = c.getTime();
    }

    public void find() {

        if ((cardType == 1 && firstSelect == 0) || (cardType == 2 && secondSelect == 0)) { // Entegrasyon tipinde Hepsi seçeneği seçili ise uyarı verilir
            if (cardType == 1) {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseclickonsubmitbuttontosendallidentificationcards")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");

            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseclickonsubmitbuttontosendalltransactioncards")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            isFind = true;
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmOfficalAccountingDatatable:dtbOfficalAccounting");
            if (dataTable != null) {
                dataTable.setFirst(0);
            }

//            isSend = false;
            findall();
            changeIsSend();
        }

    }

    @Override
    public List<OfficalAccounting> findall() {
        listOfOfficialAccount = new ArrayList<>();
        List<OfficalAccounting> retailList = new ArrayList<>();
        List<OfficalAccounting> tempList = new ArrayList<>();
        List<BranchSetting> tempBranchList = new ArrayList<>();

        if (selectedBranchList.isEmpty()) {
            tempBranchList.addAll(listOfBranch);
        } else {
            tempBranchList.addAll(selectedBranchList);
        }

        System.out.println("----findall parameters----");
        System.out.println("------processtype---" + processType);
        System.out.println("------isretail---" + isRetail);
        System.out.println("------begin-----" + begin);
        System.out.println("------end------" + end);

        if ((processType == 6 || processType == 9) && isRetail) {

            System.out.println("-- if ((processType == 6 || processType == 9) && isRetail)--");
            if (!tempBranchList.isEmpty()) {
                for (BranchSetting branch : tempBranchList) {
                    System.out.println("-------ŞUBE : " + branch.getBranch().getName());
                    if (branch.isIsErpUseShift()) {
                        System.out.println("-----if vardiya ise----");
                        List<OfficalAccounting> returnList = new ArrayList<>();
                        returnList = officialAccountingService.listOfIntegration(processType, isRetail, begin, end, branch);
                        System.out.println("----------sonuç liste size---" + returnList.size());
                        if (!returnList.isEmpty()) {
                            for (OfficalAccounting obj : returnList) {
                                obj.setBranchSetting(branch);
                            }
                            tempList.addAll(returnList);
                        }

                    } else {

                        System.out.println("---vardiya değil ise----");

                        if (!retailList.isEmpty()) {
                            retailList.clear();
                        }
                        Date oldBegin = begin;
                        Date oldEnd = end;
                        Date oldMinDate = maxDate;
                        int days = 0;
                        days = (int) ((end.getTime() - begin.getTime()) / (1000 * 60 * 60 * 24));
                        for (int i = 0; i <= days; i++) {

                            Calendar c = Calendar.getInstance();
                            c.setTime(begin);
                            c.set(Calendar.HOUR_OF_DAY, 23);
                            c.set(Calendar.MINUTE, 59);
                            c.set(Calendar.SECOND, 59);
                            end = c.getTime();

                            Calendar c2 = Calendar.getInstance();
                            c2.setTime(c.getTime());
                            c2.set(Calendar.HOUR_OF_DAY, 0);
                            c2.set(Calendar.MINUTE, 0);
                            c2.set(Calendar.SECOND, 0);
                            maxDate = c2.getTime();
                            begin = maxDate;
                            retailList.addAll(officialAccountingService.listOfIntegration(processType, isRetail, begin, end, branch));

                            Calendar c3 = Calendar.getInstance();
                            c3.setTime(c.getTime());
                            c3.set(Calendar.HOUR_OF_DAY, 0);
                            c3.set(Calendar.MINUTE, 0);
                            c3.set(Calendar.SECOND, 0);
                            c3.add(Calendar.DAY_OF_MONTH, +1);
                            maxDate = c3.getTime();
                            begin = maxDate;

                        }
                        begin = oldBegin;
                        end = oldEnd;
                        maxDate = oldMinDate;
                        if (retailList.size() > 0) {

                            for (OfficalAccounting obj : retailList) {
                                obj.setBranchSetting(branch);
                            }

                            listOfOfficialAccount.addAll(retailList);
                        }

                        System.out.println("-----sonuç liste size----" + listOfOfficialAccount.size());

                    }
                }

                if (!tempList.isEmpty()) {
                    listOfOfficialAccount.addAll(tempList);
                }
            }

        } else {

            System.out.println("-- if ((processType == 6 || processType == 9) && isRetail)-   ELSEEEEEEEEEEEEEEEEE----------");
            if (!tempBranchList.isEmpty()) {
                for (BranchSetting branch : tempBranchList) {
                    List<OfficalAccounting> returnList = new ArrayList<>();
                    returnList = officialAccountingService.listOfIntegration(processType, isRetail, begin, end, branch);
                    if (!returnList.isEmpty()) {
                        for (OfficalAccounting obj : returnList) {
                            obj.setBranchSetting(branch);
                        }
                        tempList.addAll(returnList);
                    }
                }
                if (!tempList.isEmpty()) {
                    listOfOfficialAccount.addAll(tempList);
                }

                System.out.println("-------sonuç liste size----" + listOfOfficialAccount.size());
            }

//            listOfOfficialAccount = officialAccountingService.listOfIntegration(processType, isRetail, begin, end, selectedBranch);
            if ((cardType == 1 && firstSelect != 0) || (cardType == 2 && secondSelect != 0)) {
                if (processType < 6) {

                    totalCount = officialAccountingService.getTotalCounts(branchList);//gönderilen gödnerlmeyen sayısını aldık
                }
            }
        }
        return listOfOfficialAccount;
    }

    public void changeIsSend() {
        int sendSize = 0;
        int notSendSize = 0;
        listOfObjects.clear();

        for (OfficalAccounting oa : listOfOfficialAccount) {
            if (oa.isIsSend()) {
                sendSize++;
                if (isSend) {
                    listOfObjects.add(oa);
                }
            } else {
                notSendSize++;
                if (!isSend) {
                    listOfObjects.add(oa);
                }
            }
        }

        totalCount.setSendCount(sendSize);
        totalCount.setNotSendCount(notSendSize);
        if (!listOfSelected.isEmpty()) {
            listOfSelected.clear();
        }
        autoCompleteValue = "";
        clearFilter("officialaccountingPF");
        RequestContext.getCurrentInstance().update("frmOfficalAccountingDatatable:dtbOfficalAccounting");
        RequestContext.getCurrentInstance().update("frmOfficalAccounting:pgOfficalAccounting");

    }

    //web servise gönderecek
    public void sendIntegration() {
        officialAccountingService.sendDataIntegration(listOfSelected, selectedBranch);
        if (firstSelect != 0 && secondSelect != 0) {
            changeIsSend();
        }

        RequestContext.getCurrentInstance().update("frmOfficalAccountingDatatable:dtbOfficalAccounting");
        RequestContext.getCurrentInstance().update("frmOfficalAccounting:pgOfficalAccounting");
    }

    public void check(SelectEvent event) {
        selectSize = listOfSelected.size();

    }

    public void unCheck(UnselectEvent event) {
        selectSize = listOfSelected.size();
    }

    public void checkAll() {
        if (selectSize < listOfSelected.size()) {
            listOfSelected.clear();
            listOfSelected.addAll(listOfObjects);
        } else {
            listOfSelected.clear();
        }
        selectSize = listOfSelected.size();
    }

    /**
     * işlem tipi dallandığı için bu şekilde yapıldı.
     */
    public void changeProcessType() {
        isFind = false;
//        boolean isAll = false;

        switch (cardType) {

            case 1:
                switch (firstSelect) {
                    case 1:
                        processType = 1;
                        break;
                    case 2:
                        processType = 2;
                        break;
                    case 3:
                        processType = 3;
                        break;
                    case 4:
                        processType = 4;
                        break;
                    case 5:
                        processType = 5;
                        break;
                    default:
                        processType = 1;
                        break;
                }
                break;
            case 2:
                switch (secondSelect) {
                    case 1:
                        processType = 6;//finansman belgesi
                        break;
                    case 2:
                        processType = 7;//devir fişi
                        break;
                    case 3:
                        processType = 8;//çek-senet
                        break;
                    case 4:
                        processType = 9;//fatura
                        break;
                    case 5:
                        processType = 10;//irsaliye
                        break;
                    case 6:
                        processType = 11;//depo fişi
                        break;
                    default:
                        processType = 6;
                        break;
                }
                break;
            default:
                processType = 1;
                break;
        }

        if ((processType == 6 || processType == 9) && isRetail) {

            Calendar c = Calendar.getInstance();
            c.setTime(begin);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            begin = c.getTime();

            Calendar c2 = Calendar.getInstance();
            c2.setTime(c.getTime());
            c2.add(Calendar.DAY_OF_MONTH, +20);
            c2.set(Calendar.HOUR_OF_DAY, 23);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            maxDate = c2.getTime();
            end = maxDate;

        } else {

            Calendar c = Calendar.getInstance();
            c.setTime(begin);
            begin = c.getTime();
            maxDate = c.getTime();

            Calendar c2 = Calendar.getInstance();
            c2.setTime(c.getTime());
            c2.set(Calendar.HOUR_OF_DAY, 23);
            c2.set(Calendar.MINUTE, 59);
            c2.set(Calendar.SECOND, 59);
            c2.add(Calendar.MONTH, +1);
            end = c2.getTime();
            maxDate = c2.getTime();
        }

        RequestContext.getCurrentInstance().update("frmOfficalAccounting");

    }

    public String getFinancingType(int typeId) {
        return officialAccountingService.getFinancingType(typeId);
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeCardType() {

        isFind = false;
        if (cardType == 1) {
            firstSelect = 0;
        } else if (cardType == 2) {
            secondSelect = 0;
        }
        RequestContext.getCurrentInstance().update("frmOfficalAccounting");
    }

    public List<OfficalAccounting> changeDataSend(List<OfficalAccounting> listData) {
        List<OfficalAccounting> list = new ArrayList<>();
        if (!listData.isEmpty()) {
            list = listData;
            for (Iterator<OfficalAccounting> iterator = list.iterator(); iterator.hasNext();) {
                OfficalAccounting value = iterator.next();
                if (value.isIsSend()) {
                    iterator.remove();

                }
            }
        }
        return list;

    }

    //Hepsi seçeneği seçili ise ilgili işlem tiplerinin tamamını web servise gönderir.
    public void sendIntegrationBatch() {

        Date oldBegin = begin;
        Date oldEnd = end;
        Date oldMinDate = maxDate;

        if (cardType == 1 && firstSelect == 0) { //Tanım kartı için işlem tipi Hepsi seçili ise
            System.out.println("----tanım kartı hepsi ----");
            //Cari Kart
            listOfSelected.clear();
            processType = 1;
            listOfSelected.addAll(findall());
            listOfSelected = changeDataSend(listOfSelected);
            sendIntegration();
            System.out.println("----cari kartı --size--" + listOfSelected.size());

            //Ürün Kart
            listOfSelected.clear();
            processType = 2;
            listOfSelected.addAll(findall());
            listOfSelected = changeDataSend(listOfSelected);
            sendIntegration();
            System.out.println("----stokkart --size--" + listOfSelected.size());

            //Kasa Kart
            listOfSelected.clear();
            processType = 3;
            listOfSelected.addAll(findall());
            listOfSelected = changeDataSend(listOfSelected);
            sendIntegration();
            System.out.println("----kasakart --size--" + listOfSelected.size());

            //Banka Kart
            listOfSelected.clear();
            processType = 4;
            listOfSelected.addAll(findall());
            listOfSelected = changeDataSend(listOfSelected);
            sendIntegration();
            System.out.println("----bankakart --size--" + listOfSelected.size());

            //Depo Kart
            listOfSelected.clear();
            processType = 5;
            listOfSelected.addAll(findall());
            listOfSelected = changeDataSend(listOfSelected);
            sendIntegration();
            System.out.println("----depokart --size--" + listOfSelected.size());

            listOfSelected.clear();

        } else if (cardType == 2 && secondSelect == 0) { //İşlem tipi Hepsi seçili ise
            System.out.println("---işlem tipi hepsi seçili ise-----");

            if (!isRetail) {
                System.out.println("------isretail değil ise---");
                //Finansman Belgesi
                listOfSelected.clear();
                processType = 6;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Finansman Belgesi --size--" + listOfSelected.size());

                //Devir Fişi
                listOfSelected.clear();
                processType = 7;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Devir Fişi --size--" + listOfSelected.size());

                //Çek Senet
                listOfSelected.clear();
                processType = 8;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Çek Senet --size--" + listOfSelected.size());

                //Fatura
                listOfSelected.clear();
                processType = 9;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Fatura --size--" + listOfSelected.size());

                //İrsaliye
                listOfSelected.clear();
                processType = 10;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----İrsaliye --size--" + listOfSelected.size());

                //Depo Fişi
                listOfSelected.clear();
                processType = 11;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Depo Fişi --size--" + listOfSelected.size());

                listOfSelected.clear();

            } else if (isRetail) { // perakende seçili ise tarih aralığında gün gün çekilip gönderildi
                System.out.println("---is retail ise---------");
                
                System.out.println("----Devir fişi başlangıç --size--");
                //Devir Fişi
                listOfSelected.clear();
                processType = 7;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Devir fişi  --size--" + listOfSelected.size());
                
                System.out.println("----Çek Senet başlangıç ----");

                //Çek Senet
                listOfSelected.clear();
                processType = 8;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Çek Senet --size--" + listOfSelected.size());
                System.out.println("----İrsaliye başlangıç ----");

                //İrsaliye
                listOfSelected.clear();
                processType = 10;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----İrsaliye --size--" + listOfSelected.size());
                System.out.println("----Depo Fişi başlangıç ----");

                //Depo Fişi
                listOfSelected.clear();
                processType = 11;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Depo Fişi --size--" + listOfSelected.size());
                System.out.println("----Finansman Belgesi başlangıç ----");

                //Finansman Belgesi
                listOfSelected.clear();
                processType = 6;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Finansman Belgesi --size--" + listOfSelected.size());
                System.out.println("----Fatura başlangıç ----");

                //Fatura 
                listOfSelected.clear();
                processType = 9;
                listOfSelected.addAll(findall());
                listOfSelected = changeDataSend(listOfSelected);
                sendIntegration();
                System.out.println("----Fatura --size--" + listOfSelected.size());

            }
            listOfSelected.clear();
        }
        RequestContext.getCurrentInstance().update("frmOfficalAccounting");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcompleted")));
        RequestContext.getCurrentInstance().update("grwProcessMessage");

    }

    public void changeBranch() {
        branchList = "";
        List<BranchSetting> tempList = new ArrayList<>();

        if (selectedBranchList.isEmpty()) {
            tempList.addAll(listOfBranch);
        } else {
            tempList.addAll(selectedBranchList);
        }

        if (!tempList.isEmpty()) {
            for (BranchSetting branchSetting : tempList) {
                branchList = branchList + "," + branchSetting.getBranch().getId();
            }

            if (!branchList.isEmpty()) {
                branchList = branchList.substring(1, branchList.length());
            }
        }

    }

}
