/**
 * This class ...
 *
 *
 * @author Emrullah YAKIŞAN
 *
 * @date   04.02.2019 10:58:39
 */
package com.mepsan.marwiz.automation.tank.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.automation.tank.business.ITankService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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

public class TankBean extends GeneralDefinitionBean<Warehouse> {

    private Object object;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{tankService}")
    private ITankService tankService;

    private Warehouse subTotalWarehouse;

    public void setTankService(ITankService tankService) {
        this.tankService = tankService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Warehouse getSubTotalWarehouse() {
        return subTotalWarehouse;
    }

    public void setSubTotalWarehouse(Warehouse subTotalWarehouse) {
        this.subTotalWarehouse = subTotalWarehouse;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-----TankBean----------");
        subTotalWarehouse = new Warehouse();
        listOfObjects = findall();

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        subTotalWarehouse.setSalesTotalLiter(BigDecimal.ZERO);
        subTotalWarehouse.setPurchaseTotalLiter(BigDecimal.ZERO);
        subTotalWarehouse.setLastQuantity(BigDecimal.ZERO);
        subTotalWarehouse.setPurchaseTotalPrice(BigDecimal.ZERO);
        subTotalWarehouse.setPurchaseTotalMoney(BigDecimal.ZERO);
        subTotalWarehouse.setSalesTotalPrice(BigDecimal.ZERO);
        subTotalWarehouse.setSalesTotalMoney(BigDecimal.ZERO);

        subTotalWarehouse.setAvailableStockWithoutSalesPrice(BigDecimal.ZERO);
        subTotalWarehouse.setAvailableStockWithSalesPrice(BigDecimal.ZERO);
        subTotalWarehouse.setAvailableStockWithoutPurchasePrice(BigDecimal.ZERO);
        subTotalWarehouse.setAvailableStockWithPurchasePrice(BigDecimal.ZERO);

        for (Warehouse listOfObject : listOfObjects) {

            listOfObject.setAvailableStockWithoutSalesPrice(calculateAvailableStock(listOfObject, false, true));
            listOfObject.setAvailableStockWithSalesPrice(calculateAvailableStock(listOfObject, true, true));
            listOfObject.setAvailableStockWithoutPurchasePrice(calculateAvailableStock(listOfObject, false, false));
            listOfObject.setAvailableStockWithPurchasePrice(calculateAvailableStock(listOfObject, true, false));

            subTotalWarehouse.getSalesTotalLiter().add(listOfObject.getSalesTotalLiter());
            subTotalWarehouse.getPurchaseTotalLiter().add(listOfObject.getPurchaseTotalLiter());
            subTotalWarehouse.getLastQuantity().add(listOfObject.getLastQuantity());
            subTotalWarehouse.getPurchaseTotalPrice().add(listOfObject.getPurchaseTotalPrice());
            subTotalWarehouse.getPurchaseTotalMoney().add(listOfObject.getPurchaseTotalMoney());
            subTotalWarehouse.getSalesTotalPrice().add(listOfObject.getSalesTotalPrice());
            subTotalWarehouse.getSalesTotalMoney().add(listOfObject.getSalesTotalMoney());
            subTotalWarehouse.getAvailableStockWithoutSalesPrice().add(listOfObject.getAvailableStockWithoutSalesPrice());
            subTotalWarehouse.getAvailableStockWithSalesPrice().add(listOfObject.getAvailableStockWithSalesPrice());
            subTotalWarehouse.getAvailableStockWithoutPurchasePrice().add(listOfObject.getAvailableStockWithoutPurchasePrice());
            subTotalWarehouse.getAvailableStockWithPurchasePrice().add(listOfObject.getAvailableStockWithPurchasePrice());

        }
        //  updateGridProperties("frmTank:dtbTank");

       setListBtn(sessionBean.checkAuthority(new int[]{164}, 0));
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public void create() {
        List<Object> list = new ArrayList<>();
        marwiz.goToPage("/pages/automation/tank/tankprocess.xhtml", list, 0, 111);
    }

    public void gotoProcess() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/automation/tank/tankprocess.xhtml", list, 0, 111);

    }

    public BigDecimal calculateAvailableStock(Warehouse warehouse, boolean isWithTax, boolean isSales) {
        BigDecimal result = BigDecimal.ZERO;

        if (isWithTax) {
            if (isSales) {
                result = warehouse.getLastQuantity().multiply(warehouse.getSalesUnitPriceWithTax());
            } else {
                result = warehouse.getLastQuantity().multiply(warehouse.getPurchaseUnitPriceWithTax());
            }
        } else if (!isWithTax) {
            if (isSales) {
                result = warehouse.getLastQuantity().multiply(warehouse.getSalesUnitPriceWithoutTax());
            } else {
                result = warehouse.getLastQuantity().multiply(warehouse.getPurchaseUnitPriceWithoutTax());
            }
        }

        return result;

    }

    public String calculateSubTotal(int columnId) {
        String total = "";
        HashMap<Integer, Unit> hm = new HashMap();

        for (Warehouse listOfObject : listOfObjects) {
            hm.put(listOfObject.getStock().getUnit().getId(), listOfObject.getStock().getUnit());           
        }
        System.out.println("columnId =" + columnId);
        for (Map.Entry<Integer, Unit> entry : hm.entrySet()) {
            Unit value = entry.getValue();

            BigDecimal totalValue = BigDecimal.ZERO;
            for (Warehouse listOfObject : listOfObjects) {
                if (listOfObject.getStock().getUnit().getId() == entry.getKey()) {
                    switch (columnId) {
                        case 1:
                            totalValue = totalValue.add(listOfObject.getPurchaseTotalLiter());
                            break;
                        case 2:
                            System.out.println("listOfObject.getSalesTotalLiter()=" + listOfObject.getSalesTotalLiter());
                            totalValue = totalValue.add(listOfObject.getSalesTotalLiter());
                            System.out.println("totalValue=" + totalValue);
                            break;
                        case 3:
                            totalValue = totalValue.add(listOfObject.getLastQuantity());
                            break;
                        case 4:
                            totalValue = totalValue.add(listOfObject.getPurchaseTotalPrice());
                            break;
                        case 5:
                            totalValue = totalValue.add(listOfObject.getPurchaseTotalMoney());
                            break;
                        case 6:
                            totalValue = totalValue.add(listOfObject.getSalesTotalPrice());
                            break;
                        case 7:
                            totalValue = totalValue.add(listOfObject.getSalesTotalMoney());
                            break;
                        case 8:
                            totalValue = totalValue.add(listOfObject.getAvailableStockWithoutSalesPrice());
                            break;
                        case 9:
                            totalValue = totalValue.add(listOfObject.getAvailableStockWithSalesPrice());
                            break;
                        case 10:
                            totalValue = totalValue.add(listOfObject.getAvailableStockWithoutPurchasePrice());
                            break;
                        case 11:
                            totalValue = totalValue.add(listOfObject.getAvailableStockWithPurchasePrice());
                        default:
                            break;
                    }
                }
            }

            if (columnId == 1 || columnId == 2 || columnId == 3) {
                total = total + " " + String.valueOf(unitNumberFormat(entry.getValue().getUnitRounding()).format(totalValue)) + entry.getValue().getSortName() + " +";
            } else {
                total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranch().getCurrencyrounding()).format(totalValue)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0) + " +";
            }

        }
        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;

    }

    public NumberFormat unitNumberFormat(int currencyRounding) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(currencyRounding);
        formatter.setMinimumFractionDigits(currencyRounding);
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        return formatter;
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Warehouse> findall() {
        return tankService.findAll();
    }

}
