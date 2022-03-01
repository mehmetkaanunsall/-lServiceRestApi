/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   04.12.2019 09:53:54
 */
package com.mepsan.marwiz.finance.invoice.presentation;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceItemService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

@ManagedBean
@ViewScoped
public class InvoicePriceDifferenceStockTabBean extends GeneralDefinitionBean<InvoiceItem> {

    @ManagedProperty(value = "#{invoiceProcessBean}")
    public InvoiceProcessBean invoiceProcessBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{invoiceItemService}")
    public IInvoiceItemService invoiceItemService;

    @ManagedProperty(value = "#{exchangeService}")
    public IExchangeService exchangeService;

    private int processType;
    private Invoice selectedInvoice;
    private List<InvoiceItem> listOfPriceDifference;
    private InvoiceItem selectedPriceDifference;
    private List<InvoiceItem> listOfCreateList;
    private BigDecimal oldPriceDifferentTotalMoney;
    boolean isSave;
    private BigDecimal oldUnitPrice;
    private List<InvoiceItem> listOfTaxs;
    private List<InvoiceItem> listOfItemOld;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setInvoiceProcessBean(InvoiceProcessBean invoiceProcessBean) {
        this.invoiceProcessBean = invoiceProcessBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<InvoiceItem> getListOfPriceDifference() {
        return listOfPriceDifference;
    }

    public void setListOfPriceDifference(List<InvoiceItem> listOfPriceDifference) {
        this.listOfPriceDifference = listOfPriceDifference;
    }

    public void setInvoiceItemService(IInvoiceItemService invoiceItemService) {
        this.invoiceItemService = invoiceItemService;
    }

    public InvoiceItem getSelectedPriceDifference() {
        return selectedPriceDifference;
    }

    public void setSelectedPriceDifference(InvoiceItem selectedPriceDifference) {
        this.selectedPriceDifference = selectedPriceDifference;
    }

    public Invoice getSelectedInvoice() {
        return selectedInvoice;
    }

    public void setSelectedInvoice(Invoice selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }

    public boolean isIsSave() {
        return isSave;
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public BigDecimal getOldUnitPrice() {
        return oldUnitPrice;
    }

    public void setOldUnitPrice(BigDecimal oldUnitPrice) {
        this.oldUnitPrice = oldUnitPrice;
    }

    public List<InvoiceItem> getListOfTaxs() {
        return listOfTaxs;
    }

    public void setListOfTaxs(List<InvoiceItem> listOfTaxs) {
        this.listOfTaxs = listOfTaxs;
    }

    public List<InvoiceItem> getListOfItemOld() {
        return listOfItemOld;
    }

    public void setListOfItemOld(List<InvoiceItem> listOfItemOld) {
        this.listOfItemOld = listOfItemOld;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-----------InvoicePriceDifferenceStockTabBean");
        selectedInvoice = new Invoice();
        listOfObjects = new ArrayList<>();
        listOfPriceDifference = new ArrayList<>();
        listOfItemOld = new ArrayList<>();
        listOfTaxs = new ArrayList<>();
        isSave = false;

        selectedInvoice = invoiceProcessBean.getSelectedObject();
        if (selectedInvoice.getType().getId() == 26) {
            selectedObject = new InvoiceItem();
            selectedPriceDifference = new InvoiceItem();
            listOfCreateList = new ArrayList<>();
            listOfItemOld = findAll(3);
            listOfObjects = findAll(2);
            calcInvoicePrice();
        }
        setListBtn(sessionBean.checkAuthority(new int[]{5, 6, 7}, 0));
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    public void create() {
        isSave = false;
        processType = 1;//create 

        listOfPriceDifference = new ArrayList<>();

        listOfPriceDifference = findAll(1);

        RequestContext.getCurrentInstance().update("dlgPriceDifferenceStock");
        RequestContext.getCurrentInstance().execute("PF('dlg_PriceDifferenceStock').show()");

    }

    public void update() {
        isSave = false;
        processType = 2;
        oldUnitPrice = selectedObject.getUnitPrice();
        oldPriceDifferentTotalMoney = selectedObject.getPriceDifferentTotalMoney();
        RequestContext.getCurrentInstance().update("dlgPriceDifferenceStockUpdate");
        RequestContext.getCurrentInstance().execute("PF('dlg_PriceDifferenceStockUpdate').show()");
    }

    public void onCellEditPriceDifference(CellEditEvent event) {
        isSave = false;
        FacesContext context = FacesContext.getCurrentInstance();
        selectedPriceDifference = context.getApplication().evaluateExpressionGet(context, "#{item}", InvoiceItem.class);

        if (selectedInvoice.isIsDifferenceDirection()) {//Arttır
            isSave = true;
        } else {//Azalt
            if (selectedPriceDifference.getNewUnitPrice().compareTo(selectedPriceDifference.getUnitPrice()) < 0) {
                isSave = true;
            } else {
                isSave = false;
            }
        }

        if (selectedPriceDifference.getQuantity() != null) {
            BigDecimal totalTax = BigDecimal.ZERO;
            BigDecimal totalPrice = BigDecimal.ZERO;

            totalPrice = selectedPriceDifference.getQuantity().multiply(selectedPriceDifference.getNewUnitPrice());
            selectedPriceDifference.setTotalPrice(totalPrice);

            BigDecimal x = BigDecimal.ONE.add(selectedPriceDifference.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));

            totalTax = selectedPriceDifference.getTotalPrice().multiply(selectedPriceDifference.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
            selectedPriceDifference.setTotalTax(totalTax);
            selectedPriceDifference.setTotalMoney(selectedPriceDifference.getTotalPrice().add(selectedPriceDifference.getTotalTax()));
            selectedPriceDifference.setOldUnitPrice(selectedPriceDifference.getUnitPrice());
        }

        for (Iterator<InvoiceItem> iterator = listOfPriceDifference.iterator(); iterator.hasNext();) {
            InvoiceItem next = iterator.next();
            if (next.getId() == selectedPriceDifference.getId()) {
                iterator.remove();
                break;
            }
        }

        listOfPriceDifference.add(selectedPriceDifference);
        RequestContext.getCurrentInstance().execute("updateDatatablePriceDifference();");
    }

    public void calcUnitPrice() {
        isSave = false;

        for (InvoiceItem item : listOfItemOld) {

            if (item.getStock().getId() == selectedObject.getStock().getId()) {
                if (selectedInvoice.isIsDifferenceDirection()) {//Arttır
                    isSave = true;
                } else {//Azalt
                    if (selectedObject.getUnitPrice().compareTo(item.getUnitPrice()) < 0) {
                        isSave = true;
                    } else {
                        isSave = false;
                    }
                }
                break;
            }
        }

        if (selectedObject.getQuantity() != null) {
            BigDecimal totalTax = BigDecimal.ZERO;
            BigDecimal totalPrice = BigDecimal.ZERO;

            totalPrice = selectedObject.getQuantity().multiply(selectedObject.getUnitPrice());
            selectedObject.setTotalPrice(totalPrice);

            BigDecimal x = BigDecimal.ONE.add(selectedObject.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));

            totalTax = selectedObject.getTotalPrice().multiply(selectedObject.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
            selectedObject.setTotalTax(totalTax);
            selectedObject.setTotalMoney(selectedObject.getTotalPrice().add(selectedObject.getTotalTax()));

        }
    }

    @Override
    public void save() {
        if (processType == 1) {
            RequestContext.getCurrentInstance().execute("saveItem1();");
        } else {
            RequestContext.getCurrentInstance().execute("saveItem();");
        }

    }

    public void delete() {
        if (sessionBean.isPeriodClosed(selectedInvoice.getInvoiceDate())) {
            RequestContext context = RequestContext.getCurrentInstance();
            int result = invoiceItemService.delete(selectedObject);
            context.execute("PF('dlg_PriceDifferenceStockUpdate').hide()");
            if (result > 0) {
                listOfObjects = findAll(2);
                calcInvoicePrice();
                context.update("tbvInvoice:frmPriceDifferenceTab:dtbPriceDifferenceTab");
                if (listOfFilteredObjects != null) {
                    listOfFilteredObjects.clear();
                }
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    public List<InvoiceItem> findAll(int type) {

        if (type == 1) {
            return invoiceItemService.listInvoiceStocks(selectedInvoice.getPriceDifferenceInvoice(), "pricedifferentinvoice");
        } else if (type == 2) {
            return invoiceItemService.listInvoiceStocks(selectedInvoice, "");
        } else {
            return invoiceItemService.listInvoiceStocks(selectedInvoice.getPriceDifferenceInvoice(), "");
        }

    }

    @Override
    public List<InvoiceItem> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void calcInvoicePrice() {

        selectedInvoice.setPriceDifferenceTotalMoney(BigDecimal.valueOf(0));
        BigDecimal unitPrice = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (InvoiceItem item : listOfObjects) {
            selectedInvoice.setPriceDifferenceTotalMoney(item.getPriceDifferentTotalMoney() != null ? selectedInvoice.getPriceDifferenceTotalMoney().add(item.getPriceDifferentTotalMoney()) : BigDecimal.valueOf(0));
            unitPrice = item.getPriceDifferentTotalMoney().divide(BigDecimal.ONE.add(item.getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN)), 4, RoundingMode.HALF_EVEN);

            unitPrice = unitPrice.divide(item.getQuantity(), 4, RoundingMode.HALF_EVEN);
            item.setUnitPrice(unitPrice);

            totalPrice = item.getQuantity().multiply(item.getUnitPrice());
            item.setTotalPrice(totalPrice);

            BigDecimal x = BigDecimal.ONE.add(item.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));

            totalTax = item.getTotalPrice().multiply(item.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
            item.setTotalTax(totalTax);
            item.setTotalMoney(item.getTotalPrice().add(item.getTotalTax()));
            for (InvoiceItem invi : listOfItemOld) {
                if (invi.getStock().getId() == item.getStock().getId()) {
                    item.setOldUnitPrice(invi.getUnitPrice());
                }
            }
        }

    }

    public void changeTotalPrice(InvoiceItem invoiceItem) {

        BigDecimal unitPrice = BigDecimal.ZERO;
        if (processType == 2) {

            invoiceItem.setPriceDifferentTotalMoney((invoiceItem.getUnitPrice().multiply(BigDecimal.ONE.add(invoiceItem.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN)))).multiply(invoiceItem.getQuantity()));
            for (InvoiceItem item : listOfItemOld) {
                if (item.getStock().getId() == invoiceItem.getStock().getId()) {
                    if (selectedInvoice.isIsDifferenceDirection()) {
                        unitPrice = item.getUnitPrice().add(invoiceItem.getUnitPrice());
                    } else {
                        unitPrice = item.getUnitPrice().subtract(invoiceItem.getUnitPrice());
                    }
                    break;
                }
            }

        } else {
            invoiceItem.setPriceDifferentTotalMoney((invoiceItem.getNewUnitPrice().multiply(BigDecimal.ONE.add(invoiceItem.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN)))).multiply(invoiceItem.getQuantity()));
            if (selectedInvoice.isIsDifferenceDirection()) {
                unitPrice = invoiceItem.getUnitPrice().add(invoiceItem.getNewUnitPrice());
            } else {
                unitPrice = invoiceItem.getUnitPrice().subtract(invoiceItem.getNewUnitPrice());

            }
        }

        invoiceItem.setUnitPrice(unitPrice);

        invoiceItemService.calculater(invoiceItem,1);

    }

    public void saveItem() {

        int result = 0;

        RequestContext context = RequestContext.getCurrentInstance();
        if (processType == 1) {
            listOfCreateList.clear();
            if (isSave) {

                for (InvoiceItem item : listOfPriceDifference) {
                    if (item.getNewUnitPrice() != null) {
                        if (item.getNewUnitPrice().compareTo(BigDecimal.valueOf(0)) == 1 && (!selectedInvoice.isIsDifferenceDirection() ? item.getUnitPrice().compareTo(item.getNewUnitPrice()) == 1 ? true : false : true)) {
                            item.setInvoice(selectedInvoice);
                            item.setExchangeRate(exchangeService.bringExchangeRate(item.getCurrency(), selectedInvoice.getCurrency(), sessionBean.getUser()));

                            InvoiceItem newPriceDifferentInvoiceItem = new InvoiceItem();
                            newPriceDifferentInvoiceItem.setId(item.getId());
                            item.setPriceDifferentInvoiceItem(newPriceDifferentInvoiceItem);
                            item.setId(0);

                            changeTotalPrice(item);
                            listOfCreateList.add(item);
                        }
                    }
                }
                if (!listOfCreateList.isEmpty()) {
                    result = invoiceItemService.createAll(listOfCreateList, selectedInvoice);
                    if (result > 0) {
                        context.execute("PF('dlg_PriceDifferenceStock').hide()");
                    }
                } else {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                            sessionBean.loc.getString("warning"),
                            sessionBean.loc.getString("pleaseenteraunitpriceforatleastoneproduct"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }

            }

        } else if (processType == 2) {

            if (isSave) {
                changeTotalPrice(selectedObject);
                selectedObject.setInvoice(selectedInvoice);
                result = invoiceItemService.update(selectedObject);
                if (result > 0) {
                    context.execute("PF('dlg_PriceDifferenceStockUpdate').hide()");
                }
            }

        }

        if (isSave) {
            if (result > 0) {
                listOfObjects = findAll(2);
                calcInvoicePrice();
                context.update("tbvInvoice:frmPriceDifferenceTab:dtbPriceDifferenceTab");
            }
            sessionBean.createUpdateMessage(result);
        } else {

            boolean isEmptyPrice = false;

            for (InvoiceItem item : listOfPriceDifference) {
                if (item.getNewUnitPrice() != null) {
                    isEmptyPrice = true;
                    break;
                }
            }

            if (processType == 1) {
                if (listOfCreateList.isEmpty() && !isEmptyPrice) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                            sessionBean.loc.getString("warning"),
                            sessionBean.loc.getString("pleaseenteraunitpriceforatleastoneproduct"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                } else {
                    if (selectedInvoice.isIsDifferenceDirection()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("newunitpricemustbehigherthanoldunitprice")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("newunitpriceshouldbelowerthanoldunitprice")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                }
            } else {
                if (selectedInvoice.isIsDifferenceDirection()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("newunitpricemustbehigherthanoldunitprice")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("newunitpriceshouldbelowerthanoldunitprice")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
            }

        }
    }

    /**
     * Bu metot toplam stok miktarını birime göre gruplar.
     *
     * @return
     */
    public String totalAmountText() {

        HashMap<String, BigDecimal> unitList = new HashMap<>();
        for (InvoiceItem is : listOfObjects) {
            if (unitList.containsKey(is.getUnit().getSortName())) {//bu oran onceden vardır
                unitList.put(is.getUnit().getSortName(), is.getQuantity() != null ? unitList.get(is.getUnit().getSortName()).add(is.getQuantity()) : BigDecimal.ZERO);
            } else {//vergi grubu yok ise
                unitList.put(is.getUnit().getSortName(), is.getQuantity() != null ? is.getQuantity() : BigDecimal.ZERO);
            }
        }

        StringBuilder sb = new StringBuilder();
        NumberFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        for (Map.Entry<String, BigDecimal> me : unitList.entrySet()) {
            sb.append(formatter.format(me.getValue()));
            sb.append(me.getKey());
            sb.append(" - ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }

    /**
     * Bu metot tablonun alt toplamlarını göstermek için tüm listeyi döner
     *
     * @return
     */
    public BigDecimal sumTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem invoiceStock : listOfObjects) {
            total = total.add((invoiceStock.getUnitPrice() == null ? BigDecimal.ZERO : invoiceStock.getQuantity() == null ? BigDecimal.ZERO : invoiceStock.getTotalPrice()).multiply(invoiceStock.getExchangeRate() == null ? BigDecimal.ONE : invoiceStock.getExchangeRate()));
        }
        return total;
    }

    public BigDecimal sumTotalTax() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem invoiceStock : listOfObjects) {
            total = total.add((invoiceStock.getTotalTax() == null ? BigDecimal.ZERO : invoiceStock.getTotalTax()));
        }
        return total;
    }

    public BigDecimal sumTotalMoney() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem invoiceStock : listOfObjects) {
            total = total.add((invoiceStock.getTotalMoney() == null ? BigDecimal.ZERO : invoiceStock.getTotalMoney()));
        }
        return total;
    }

    public List<InvoiceItem> calcTaxList() {

        listOfTaxs = new ArrayList<>();
        List<InvoiceItem> tempList = new ArrayList<>();
        for (InvoiceItem a : listOfObjects) {
            InvoiceItem i = new InvoiceItem();
            i.setTotalTax((a.getTotalTax() != null ? a.getTotalTax() : BigDecimal.valueOf(0)).multiply(a.getExchangeRate() != null ? a.getExchangeRate() : BigDecimal.valueOf(1)));
            i.setTaxRate(a.getTaxRate());
            if (i.getTotalTax().compareTo(BigDecimal.valueOf(0)) != 0) {
                tempList.add(i);
            }
        }

        listOfTaxs = tempList.stream()
                .collect(Collectors.groupingBy(
                        InvoiceItem::getTaxRate,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                InvoiceItem::getTotalTax,
                                BigDecimal::add)))
                .entrySet()
                .stream()
                .map(e2 -> new InvoiceItem(e2.getValue(), e2.getKey()))
                .collect(Collectors.toList());

        return listOfTaxs;

    }

    public String bringTaxrates() {
        BigDecimal totalTemp = BigDecimal.ZERO;

        HashMap<BigDecimal, BigDecimal> taxList = new HashMap<>();
        for (InvoiceItem is : listOfObjects) {
            if (taxList.containsKey(is.getTaxRate())) {//bu oran onceden vardır
                taxList.put(is.getTaxRate(), is.getTotalTax() != null ? taxList.get(is.getTaxRate()).add(is.getTotalTax().multiply(is.getExchangeRate())) : BigDecimal.ZERO);
            } else {//vergi grubu yok ise
                taxList.put(is.getTaxRate(), is.getTotalTax() != null ? is.getTotalTax().multiply(is.getExchangeRate()) : BigDecimal.ZERO);
            }
        }

        StringBuilder sb = new StringBuilder();
        NumberFormat formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranch().getCurrencyrounding());
        for (Map.Entry<BigDecimal, BigDecimal> me : taxList.entrySet()) {
            sb.append("(%");
            sb.append(formatter.format(me.getKey()));
            sb.append(" : ");
            sb.append(formatter.format(me.getValue()));
            sb.append(") ");
            sb.append(sessionBean.currencySignOrCode(selectedInvoice.getCurrency().getId(), 0));
            sb.append(" - ");
        }
        if (sb.length() > 3) {
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();

    }

}
