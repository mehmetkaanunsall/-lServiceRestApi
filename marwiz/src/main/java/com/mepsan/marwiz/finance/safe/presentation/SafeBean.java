/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 08:52:31
 */
package com.mepsan.marwiz.finance.safe.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SafeBean extends GeneralDefinitionBean<Safe> {

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{safeService}") // session
    public ISafeService safeService;
    
    private HashMap<Integer, Safe> subTotals;
    private String subTotalBalance;

    private Object object;  // yeniye bastığında gotopage fonksiyonuna parametre olarak bunu gönderiyoruz. ekleme modunda açılsın diye

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public HashMap<Integer, Safe> getSubTotals() {
        return subTotals;
    }

    public void setSubTotals(HashMap<Integer, Safe> subTotals) {
        this.subTotals = subTotals;
    }

    public String getSubTotalBalance() {
        return subTotalBalance;
    }

    public void setSubTotalBalance(String subTotalBalance) {
        this.subTotalBalance = subTotalBalance;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--------------SafeBean");
        object = new Object();
        listOfObjects = new ArrayList<>();
        listOfFilteredObjects=new ArrayList<>();
        listOfObjects = findall();
        listOfFilteredObjects.addAll(listOfObjects);
        subTotals=new HashMap<>();
        if(!listOfObjects.isEmpty()){
            
            calcSubTotals();
        }
        
        toogleList = new ArrayList<>();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true);
        }
        
         setListBtn(sessionBean.checkAuthority(new int[]{116}, 0));
    }

    @Override
    public void create() {
        marwiz.goToPage("/pages/finance/safe/safeprocess.xhtml", object, 0, 19);
    }

    public void update() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/finance/safe/safeprocess.xhtml", list, 0, 19);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Safe> findall() {
        return safeService.findAll();
    }

    /**
     * balance değerinin sıfırdan büyük mü küçük mü olduğuna bakılır.
     *
     * @param balance
     * @param type 1 ise : sıfırdan küçükse true döner. 2 ise : sıfırdan büyükse
     * true döner.
     * @return
     */
    public boolean renderedColumnValue(BigDecimal balance, int type) {
        if (type == 1) {
            if (balance.compareTo(BigDecimal.valueOf(0)) == -1) {
                return true;
            } else {
                return false;
            }

        } else if (type == 2) {
            if (balance.compareTo(BigDecimal.valueOf(0)) == 1) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }
    
    public void calcSubTotals() {
        
        
        subTotalBalance = "";

        subTotals.clear();

        for (Safe u : listOfFilteredObjects) {

            if (subTotals.containsKey(u.getCurrency().getId())) {
                Safe old = new Safe();
                old.setBalance(BigDecimal.ZERO);
                old.setBalance(subTotals.get(u.getCurrency().getId()).getBalance().add(u.getBalance()));
                subTotals.put(u.getCurrency().getId(), old);
            } else {
                subTotals.put(u.getCurrency().getId(), u);
            }

        }
        int temp = 0;
        for (Map.Entry<Integer, Safe> entry : subTotals.entrySet()) {
            if (temp == 0) {
                temp = 1;

                if (entry.getKey() != 0) {

                    subTotalBalance += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

                }
            } else if (entry.getKey() != 0) {

                subTotalBalance += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

            }

        }

        if (subTotalBalance.isEmpty() || subTotalBalance.equals("")) {
            subTotalBalance = "0";
        }
    }

}
