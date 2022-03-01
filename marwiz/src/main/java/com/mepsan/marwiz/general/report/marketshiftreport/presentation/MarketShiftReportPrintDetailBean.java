/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   09.02.2018 11:48:21
 */
package com.mepsan.marwiz.general.report.marketshiftreport.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UserData;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftReportPrintService;

@ManagedBean
@ViewScoped
public class MarketShiftReportPrintDetailBean {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marketShiftReportPrintService}")
    private IMarketShiftReportPrintService marketShiftReportPrintService;


    private Shift selectedObject;
    private List<SalePayment> listOfUser;
    private HashMap<Integer, BigDecimal> groupCurrencyTotal;
    private UserData selectedUser;
    private int processType;


    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public Shift getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Shift selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<SalePayment> getListOfUser() {
        return listOfUser;
    }

    public void setListOfUser(List<SalePayment> listOfUser) {
        this.listOfUser = listOfUser;
    }

    public void setMarketShiftReportPrintService(IMarketShiftReportPrintService marketShiftReportPrintService) {
        this.marketShiftReportPrintService = marketShiftReportPrintService;
    }

    public UserData getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(UserData selectedUser) {
        this.selectedUser = selectedUser;
    }


    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

   
    @PostConstruct
    public void init() {
        System.out.println("-------MarketShiftReportPrintDetailBean-------");
        selectedObject = new Shift();
        listOfUser = new ArrayList<>();
        groupCurrencyTotal = new HashMap<>();
       
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Shift) {
                    selectedObject = (Shift) ((ArrayList) sessionBean.parameter).get(i);
                    listOfUser = marketShiftReportPrintService.listOfUser(selectedObject);
                }
            }
        }

    }

    public String calculateTotalMoney() {
        String totalMoneyForSale = "";
        groupCurrencyTotal.clear();

        for (SalePayment u : listOfUser) {

            if (groupCurrencyTotal.containsKey(u.getCurrency().getId())) {
                BigDecimal old = groupCurrencyTotal.get(u.getCurrency().getId());
                groupCurrencyTotal.put(u.getCurrency().getId(), old.add(u.getPrice()));
            } else {
                groupCurrencyTotal.put(u.getCurrency().getId(), u.getPrice());
            }
        }
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotal.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp != 0) {
                if (temp == 0) {
                    temp = 1;
                    totalMoneyForSale += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        totalMoneyForSale += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    totalMoneyForSale += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        totalMoneyForSale += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }
        return totalMoneyForSale;
    }

    /**
     * Kullanıcı ve ödeme tipi bazında toplam ödemeleri gösterebilmek için
     * yazıldı.
     *
     * @param id
     * @param type
     * @return
     */
    public String calculateTotalMoneyForUser(int id, int type) {
        String totalMoney = "";

        groupCurrencyTotal.clear();

        for (SalePayment u : listOfUser) {
            if (type == 1) {
                if (u.getType().getId() == id) {
                    if (groupCurrencyTotal.containsKey(u.getCurrency().getId())) {
                        BigDecimal old = groupCurrencyTotal.get(u.getCurrency().getId());
                        groupCurrencyTotal.put(u.getCurrency().getId(), old.add(u.getPrice()));
                    } else {
                        groupCurrencyTotal.put(u.getCurrency().getId(), u.getPrice());
                    }
                }
            } else if (type == 2) {
                if (u.getUser().getId() == id) {
                    if (groupCurrencyTotal.containsKey(u.getCurrency().getId())) {
                        BigDecimal old = groupCurrencyTotal.get(u.getCurrency().getId());
                        groupCurrencyTotal.put(u.getCurrency().getId(), old.add(u.getPrice()));
                    } else {
                        groupCurrencyTotal.put(u.getCurrency().getId(), u.getPrice());
                    }
                }
            } else if (type == 3) {
                if (u.getUser().getId() == id) {
                    if (u.getType().getId() == 17 || u.getType().getId() == 18 || u.getType().getId() == 75) {
                        if (groupCurrencyTotal.containsKey(u.getCurrency().getId())) {
                            BigDecimal old = groupCurrencyTotal.get(u.getCurrency().getId());
                            groupCurrencyTotal.put(u.getCurrency().getId(), old.add(u.getPrice()));
                        } else {
                            groupCurrencyTotal.put(u.getCurrency().getId(), u.getPrice());
                        }
                    }

                }
            }

        }
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyTotal.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp != 0) {
                if (temp == 0) {
                    temp = 1;
                    totalMoney += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                   
                    if (entry.getKey() != 0) {
                        totalMoney += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    totalMoney += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        totalMoney += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }
        if (totalMoney.isEmpty() || totalMoney.equals("")) {
            totalMoney = "0";
        }
        return totalMoney;
    }


}
