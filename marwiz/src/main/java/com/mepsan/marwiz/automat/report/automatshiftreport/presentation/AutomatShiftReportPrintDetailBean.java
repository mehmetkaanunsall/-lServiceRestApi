/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:41:31 PM
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.presentation;

import com.mepsan.marwiz.automat.report.automatshiftreport.business.IAutomatShiftPrintDetailService;
import com.mepsan.marwiz.automat.report.automatshiftreport.dao.AutomatShiftReport;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;

@ManagedBean
@ViewScoped
public class AutomatShiftReportPrintDetailBean extends GeneralReportBean<AutomatShiftReport> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{automatShiftPrintDetailService}")
    private IAutomatShiftPrintDetailService automatShiftPrintDetailService;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    private List<AutomatShiftReport> listOfPaymentType;
    private List<AutomatShiftReport> listOfProduct;
    private List<AutomatShiftReport> listOfPlatform;
    private List<Unit> listOfUnit;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAutomatShiftPrintDetailService(IAutomatShiftPrintDetailService automatShiftPrintDetailService) {
        this.automatShiftPrintDetailService = automatShiftPrintDetailService;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public List<AutomatShiftReport> getListOfPaymentType() {
        return listOfPaymentType;
    }

    public void setListOfPaymentType(List<AutomatShiftReport> listOfPaymentType) {
        this.listOfPaymentType = listOfPaymentType;
    }

    public List<AutomatShiftReport> getListOfProduct() {
        return listOfProduct;
    }

    public void setListOfProduct(List<AutomatShiftReport> listOfProduct) {
        this.listOfProduct = listOfProduct;
    }

    public List<AutomatShiftReport> getListOfPlatform() {
        return listOfPlatform;
    }

    public void setListOfPlatform(List<AutomatShiftReport> listOfPlatform) {
        this.listOfPlatform = listOfPlatform;
    }

    @PostConstruct
    @Override
    public void init() {
        selectedObject = new AutomatShiftReport();
        if (sessionBean.parameter instanceof AutomatShiftReport) {
            selectedObject = (AutomatShiftReport) sessionBean.parameter;
        }
        listOfPaymentType = new ArrayList<>();
        listOfPlatform = new ArrayList<>();
        listOfProduct = new ArrayList<>();
        listOfPaymentType = automatShiftPrintDetailService.listOfPaymentType(selectedObject);

        listOfProduct = automatShiftPrintDetailService.listOfProduct(selectedObject);
        listOfPlatform = automatShiftPrintDetailService.listOfPlatform(selectedObject);

        listOfUnit = unitService.listOfUnit();

    }

    @Override
    public void find() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<AutomatShiftReport> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BigDecimal calcTotal(int saleTypeId, int type) {
        BigDecimal total = BigDecimal.ZERO;
        switch (type) {
            case 1://litreleri topla

                for (AutomatShiftReport a : listOfPaymentType) {
                    if (a.getAutomatSalesItem().getPaymentType() == saleTypeId || saleTypeId == 0) {
                        total = total.add(a.getAutomatSalesItem().getOperationAmount());
                    }
                }
                break;
            case 2://satışları topla

                for (AutomatShiftReport a : listOfPaymentType) {
                    if (a.getAutomatSalesItem().getPaymentType() == saleTypeId || saleTypeId == 0) {
                        total = total.add(a.getAutomatSalesItem().getTotalMoney());
                    }
                }
                break;

            case 5://
                for (AutomatShiftReport a : listOfPaymentType) {
                    if (a.getAutomatSalesItem().getPaymentType() == saleTypeId) {
                        total = total.add(BigDecimal.valueOf(a.getTotalCount()));
                    }
                }
                break;
        }

        return total;

    }

    public BigDecimal calcProduct(int productId, int type) {
        System.out.println("-productId--" + productId);
        BigDecimal total = BigDecimal.ZERO;
        switch (type) {
            case 1://litreleri topla

                for (AutomatShiftReport a : listOfProduct) {
                    if (a.getAutomatSalesItem().getStock().getId() == productId) {
                        total = total.add(a.getAutomatSalesItem().getOperationAmount());
                    }
                }
                break;
            case 2://satışları topla
                for (AutomatShiftReport a : listOfProduct) {
                    if (a.getAutomatSalesItem().getStock().getId() == productId) {
                        total = total.add(a.getAutomatSalesItem().getTotalMoney());
                    }
                }
                break;

            case 3://Satış sayılarını topla
                for (AutomatShiftReport a : listOfProduct) {
                    if (a.getAutomatSalesItem().getStock().getId() == productId) {
                        total = total.add(BigDecimal.valueOf(a.getTotalCount()));
                    }
                }
                break;
            case 4://litreleri topla

                for (AutomatShiftReport a : listOfProduct) {
                    if (a.getAutomatSalesItem().getStock().getId() == productId || productId == 0) {
                        total = total.add(a.getAutomatSalesItem().getOperationAmount());
                    }
                }
                break;
            case 5://satışları topla
                for (AutomatShiftReport a : listOfProduct) {
                    if (a.getAutomatSalesItem().getStock().getId() == productId || productId == 0) {
                        total = total.add(a.getAutomatSalesItem().getTotalMoney());
                    }
                }
                break;
        }
        return total;
    }

    public BigDecimal calcPlatform(int productId, int type) {
        BigDecimal total = BigDecimal.ZERO;
        switch (type) {
            case 1://litreleri topla

                for (AutomatShiftReport a : listOfPlatform) {
                    if (a.getAutomatSalesItem().getPlatform().getId() == productId || productId == 0) {
                        total = total.add(a.getAutomatSalesItem().getOperationAmount());
                    }
                }
                break;
            case 2://satışları topla
                for (AutomatShiftReport a : listOfPlatform) {
                    if (a.getAutomatSalesItem().getPlatform().getId() == productId || productId == 0) {
                        total = total.add(a.getAutomatSalesItem().getTotalMoney());
                    }
                }
                break;

            case 3://Satış sayılarını topla
                for (AutomatShiftReport a : listOfPlatform) {
                    if (a.getAutomatSalesItem().getPlatform().getId() == productId) {
                        total = total.add(BigDecimal.valueOf(a.getTotalCount()));
                    }
                }
                break;
        }
        return total;
    }

    public String calculateSalesAmount(int type) {
        String totalMoneyForSale = "";
        HashMap<Integer, BigDecimal> hashMap = new HashMap();
        hashMap.clear();

        switch (type) {
            case 1: // ödeme tipi
                for (AutomatShiftReport u : listOfPaymentType) {
                    if (hashMap.containsKey(u.getAutomatSalesItem().getStock().getUnit().getId())) {
                        BigDecimal old = hashMap.get(u.getAutomatSalesItem().getStock().getUnit().getId());
                        BigDecimal val = old.add(u.getAutomatSalesItem().getOperationAmount());
                        hashMap.put(u.getAutomatSalesItem().getStock().getUnit().getId(), val);
                    } else {
                        hashMap.put(u.getAutomatSalesItem().getStock().getUnit().getId(), u.getAutomatSalesItem().getOperationAmount());
                    }
                }
                break;
            case 2: // ürün
                for (AutomatShiftReport u : listOfProduct) {
                    if (hashMap.containsKey(u.getAutomatSalesItem().getStock().getUnit().getId())) {
                        BigDecimal old = hashMap.get(u.getAutomatSalesItem().getStock().getUnit().getId());
                        BigDecimal val = old.add(u.getAutomatSalesItem().getOperationAmount());

                        hashMap.put(u.getAutomatSalesItem().getStock().getUnit().getId(), val);
                    } else {
                        hashMap.put(u.getAutomatSalesItem().getStock().getUnit().getId(), u.getAutomatSalesItem().getOperationAmount());
                    }
                }
                break;
            case 3: // peron
                for (AutomatShiftReport u : listOfPlatform) {
                    if (hashMap.containsKey(u.getAutomatSalesItem().getStock().getUnit().getId())) {
                        BigDecimal old = hashMap.get(u.getAutomatSalesItem().getStock().getUnit().getId());
                        BigDecimal val = old.add(u.getAutomatSalesItem().getOperationAmount());

                        hashMap.put(u.getAutomatSalesItem().getStock().getUnit().getId(), val);
                    } else {
                        hashMap.put(u.getAutomatSalesItem().getStock().getUnit().getId(), u.getAutomatSalesItem().getOperationAmount());
                    }
                }
                break;
        }

        int temp = 0;

        for (Map.Entry<Integer, BigDecimal> entry : hashMap.entrySet()) {
            String sortName = "";
            for (Unit unit : listOfUnit) {
                if (entry.getKey() == unit.getId()) {
                    sortName = unit.getSortName();
                    break;
                }
            }
            if (temp == 0) {
                temp = 1;
                totalMoneyForSale += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                if (entry.getKey() != 0) {
                    totalMoneyForSale += " " + sortName;
                }
            } else {
                totalMoneyForSale += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                if (entry.getKey() != 0) {

                    totalMoneyForSale += " " + sortName;
                }
            }
        }

        return totalMoneyForSale;
    }
}
