/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2018 10:30:25
 */
package com.mepsan.marwiz.general.marketshift.presentation;

import com.mepsan.marwiz.finance.invoice.business.IInvoiceService;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.business.GFMarketShiftService;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.marketshift.dao.ErrorOfflinePos;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.UnsuccessfulSalesProcess;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftPaymentService;
import java.io.IOException;
import java.io.StringReader;
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
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.mepsan.marwiz.system.sapintegration.business.ISapIntegrationService;
import com.mepsan.marwiz.system.unsuccessfulsalesprocess.business.IUnsuccessfulSalesProcessService;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;

@ManagedBean
@ViewScoped
public class MarketShiftBean extends GeneralBean<Shift> {

    @ManagedProperty(value = "#{marketShiftService}")
    public IMarketShiftService marketShiftService;

    @ManagedProperty(value = "#{gfMarketShiftService}")
    public GFMarketShiftService gfMarketShiftService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{marketShiftPaymentService}")
    private IMarketShiftPaymentService marketShiftPaymentService;

    @ManagedProperty(value = "#{unsuccessfulSalesProcessService}")
    private IUnsuccessfulSalesProcessService unsuccessfulSalesProcessService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{invoiceService}")
    private IInvoiceService invoiceService;

    private String message;
    private String newShiftName;
    private boolean haveOpenShift;
    private Shift openShift;
    private Shift selectedConfirmShift;
    private HashMap<Integer, BigDecimal> groupCurrencySaleTotal;
    private HashMap<Integer, BigDecimal> groupCurrencyPaymentTotal;
    private HashMap<Integer, BigDecimal> groupCurrencyDiff;
    private int compare;
    private Invoice selectedOpenInvoice;
    private boolean isCheck;

    private List<PointOfSale> listOfPos;
    private List<ErrorOfflinePos> listErrorPos;

    private Date beginDate;
    private Date endDate;
    private String createWhere;

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isHaveOpenShift() {
        return haveOpenShift;
    }

    public void setHaveOpenShift(boolean haveOpenShift) {
        this.haveOpenShift = haveOpenShift;
    }

    public Shift getOpenShift() {
        return openShift;
    }

    public void setOpenShift(Shift openShift) {
        this.openShift = openShift;
    }

    public void setGfMarketShiftService(GFMarketShiftService gfMarketShiftService) {
        this.gfMarketShiftService = gfMarketShiftService;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setMarketShiftPaymentService(IMarketShiftPaymentService marketShiftPaymentService) {
        this.marketShiftPaymentService = marketShiftPaymentService;
    }

    public Shift getSelectedConfirmShift() {
        return selectedConfirmShift;
    }

    public void setSelectedConfirmShift(Shift selectedConfirmShift) {
        this.selectedConfirmShift = selectedConfirmShift;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public String getNewShiftName() {
        return newShiftName;
    }

    public void setNewShiftName(String newShiftName) {
        this.newShiftName = newShiftName;
    }

    public int getCompare() {
        return compare;
    }

    public void setCompare(int compare) {
        this.compare = compare;
    }

    public Invoice getSelectedOpenInvoice() {
        return selectedOpenInvoice;
    }

    public void setSelectedOpenInvoice(Invoice selectedOpenInvoice) {
        this.selectedOpenInvoice = selectedOpenInvoice;
    }

    public void setInvoiceService(IInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public boolean isIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public List<PointOfSale> getListOfPos() {
        return listOfPos;
    }

    public void setListOfPos(List<PointOfSale> listOfPos) {
        this.listOfPos = listOfPos;
    }

    public List<ErrorOfflinePos> getListErrorPos() {
        return listErrorPos;
    }

    public void setListErrorPos(List<ErrorOfflinePos> listErrorPos) {
        this.listErrorPos = listErrorPos;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public void setUnsuccessfulSalesProcessService(IUnsuccessfulSalesProcessService unsuccessfulSalesProcessService) {
        this.unsuccessfulSalesProcessService = unsuccessfulSalesProcessService;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------MarketShiftBean");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        setBeginDate(calendar.getTime());
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        setEndDate(calendar.getTime());

        createWhere = marketShiftService.createWhere(beginDate, endDate);
        listOfObjects = findall(createWhere);
        selectedObject = new Shift();
        selectedConfirmShift = new Shift();
        groupCurrencySaleTotal = new HashMap<>();
        groupCurrencyPaymentTotal = new HashMap<>();
        groupCurrencyDiff = new HashMap<>();
        compare = 2;//default
        selectedOpenInvoice = new Invoice();
        listOfPos = new ArrayList<>();
        listErrorPos = new ArrayList<>();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{49, 50}, 0));

        listOfPos = marketShiftService.listPointOfSale();

    }

    public void find() {
        createWhere = marketShiftService.createWhere(beginDate, endDate);
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmMarketShift:dtbMarketShift");

    }

    @Override
    public LazyDataModel<Shift> findall(String where) {
        return new CentrowizLazyDataModel<Shift>() {
            @Override
            public List<Shift> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<Shift> result = marketShiftService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = marketShiftService.count(where);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().update("frmMarketShift:dtbMarketShift");
                return result;
            }
        };
    }

    public void closeShift() {
        if (selectedObject.getStatus().getId() == 7) {
            if (getRendered(49, 0)) {
                RequestContext.getCurrentInstance().execute("PF('dlgCloseShiftConfirm').show();");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("youarenotallowedforthisprocess")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
    }

    public void pressYesButtonForCloseShift() {
        int result = 0;
        boolean resultUpdateIsCheck = false;
        resultUpdateIsCheck = marketShiftService.controlIsCheck();

        if (resultUpdateIsCheck == false) {
            boolean isOfflineSaleControl = true;
            if (!listOfPos.isEmpty()) {
                isOfflineSaleControl = controlOfflineSales();
            }

            if (isOfflineSaleControl) {
                List<UnsuccessfulSalesProcess> resultUnsuccessfulSales = new ArrayList<>();
                resultUnsuccessfulSales = unsuccessfulSalesProcessService.sendIntegration(String.valueOf(sessionBean.getUser().getLastBranch().getId()));

                if (resultUnsuccessfulSales.get(0).getResponseCode() == 1) {//Başarısız kayıtlar aktarıldı.
                    selectedObject.getStatus().setId(8);
                    result = marketShiftService.update(selectedObject);
                    if (result > 0) {
                        controlOpenShiftPayment();
                        RequestContext.getCurrentInstance().execute("PF('dlgCloseShiftConfirm').hide();");
                        RequestContext.getCurrentInstance().update("frmMarketShift:dtbMarketShift");
                    }
                    sessionBean.createUpdateMessage(result);

                    marketShiftService.transferShiftAndPos(listOfPos);
                } else {//Başarısız satış aktarımı hata
                    marketShiftService.updateIsCheck(selectedObject);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("notification"), sessionBean.loc.getString("unsuccessfullsalesrecordscannotbeenteredsystem")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }

            } else {
                marketShiftService.updateIsCheck(selectedObject);
                RequestContext.getCurrentInstance().update("dlgNonTransferableOfflineSales");
                RequestContext.getCurrentInstance().execute("PF('dlg_nontransferableofflinesales').show();");
            }
        } else {//Vardiya kapatılmaya çalışılmaktadır. İşleme izin verilmemeli!
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("notification"), sessionBean.loc.getString("shiftisclosednowfromotheruserprocesscannotbedone")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

    }

    public boolean controlOfflineSales() {
        boolean isControlOffline;
        listErrorPos = new ArrayList<>();
        List<ErrorOfflinePos> listOfflinePosSales = new ArrayList<>();
        listOfflinePosSales = marketShiftService.controlOfflinePos(listOfPos);

        if (!listOfflinePosSales.isEmpty()) {
            for (ErrorOfflinePos errorPos : listOfflinePosSales) {
                if ((errorPos.isIsSuccessful() == true && errorPos.getNotSendCount() != 0) || (errorPos.isIsSuccessful() == false && errorPos.getNotSendCount() != 0) || errorPos.isIsSuccessful() == false) {
                    listErrorPos.add(errorPos);
                }
            }
        }

        if (listErrorPos.isEmpty()) {
            isControlOffline = true;
        } else {
            isControlOffline = false;
        }

        return isControlOffline;

    }

    @Override
    public void create() {
        openShift = new Shift();
        message = "";
        openShift = marketShiftService.controlHaveOpenShift();
        if (openShift.getId() == 0) {//Açık vardiya yoksa
            message = sessionBean.loc.getString("areyousureopennewshift");
            haveOpenShift = false;
            RequestContext.getCurrentInstance().execute("PF('dlgNewShiftConfirm').show();");
        } else if (openShift.getId() > 0) {//Açık Vardiya Varsa
            message = sessionBean.loc.getString("areyousureclosethisshiftandopennewshift");
            haveOpenShift = true;
            RequestContext.getCurrentInstance().execute("PF('dlgNewShiftConfirm').show();");

        }

    }

    public void pressYesButtonForNewShift() {
        boolean resultUpdateIsCheck = false;
        if (haveOpenShift == false) {//Açık vardiya yoksa
            resultUpdateIsCheck = true;
        } else {//Açık Vardiya Varsa
            resultUpdateIsCheck = marketShiftService.controlIsCheck();
        }

        if (resultUpdateIsCheck == false) {

            boolean isOfflineControl = true;

            if (!listOfPos.isEmpty()) {
                isOfflineControl = controlOfflineSales();
            }

            int result = 0;
            selectedObject = new Shift();
            selectedObject.setName(newShiftName);

            if (openShift.getId() > 0) {
                List<UnsuccessfulSalesProcess> resultUnsuccessfulSales = new ArrayList<>();
                resultUnsuccessfulSales = unsuccessfulSalesProcessService.sendIntegration(String.valueOf(sessionBean.getUser().getLastBranch().getId()));

                if (resultUnsuccessfulSales.get(0).getResponseCode() != 1) {
                    marketShiftService.updateIsCheck(openShift);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("notification"), sessionBean.loc.getString("unsuccessfullsalesrecordscannotbeenteredsystem")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }

            result = marketShiftService.create(selectedObject, isOfflineControl);

            if (result > 0) {
                controlOpenShiftPayment();
                selectedObject.setId(result);
                RequestContext.getCurrentInstance().execute("PF('dlgNewShiftConfirm').hide();");
                RequestContext.getCurrentInstance().update("frmMarketShift:dtbMarketShift");
                marketShiftService.transferShiftAndPos(listOfPos);
            } else if (result == -1) {
                RequestContext.getCurrentInstance().update("dlg_nontransferableofflinesales");
                RequestContext.getCurrentInstance().execute("PF('dlg_nontransferableofflinesales').show();");
            }
        } else {//Vardiya kapatılmaya çalışılmaktadır. İşleme izin verilmemeli!
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("notification"), sessionBean.loc.getString("shiftisclosednowfromotheruserprocesscannotbedone")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        String where = " ";

        if (isCheck) {
            where = " AND shf.is_confirm= FALSE ";

        }
        where = where + createWhere;

        if (autoCompleteValue == null) {
            listOfObjects = findall(where);
        } else {
            gfMarketShiftService.makeSearch(where, autoCompleteValue);
            listOfObjects = gfMarketShiftService.searchResult;
        }
    }

    public void showList() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMarketShift:dtbMarketShift");
        dataTable.setFirst(0);
        generalFilter();
        RequestContext.getCurrentInstance().update("frmMarketShift:dtbMarketShift");
    }

    public String readXml(int type, String totalSaleAmount, int calculateType) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');

        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        String totalAmount = "";
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(totalSaleAmount));
            src.setEncoding("UTF-8");
            org.w3c.dom.Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();
            int count = 0;
            if (type == 1) {
                NodeList list = doc.getElementsByTagName("shiftsaletotal");
                if (list.getLength() == 0) {
                    totalAmount = Integer.toString(0);
                }
                for (int s = 0; s < list.getLength(); s++) {
                    NodeList elements = list.item(s).getChildNodes();
                    if (count == 0) {
                        if (new BigDecimal(elements.item(0).getTextContent()).compareTo(BigDecimal.valueOf(0)) != 0) {
                            totalAmount += (numberFormat.format(new BigDecimal(elements.item(0).getTextContent()))).toString() + " " + sessionBean.currencySignOrCode(Integer.valueOf(elements.item(1).getTextContent()), 0);
                            count = 1;
                        }
                    } else if (new BigDecimal(elements.item(0).getTextContent()).compareTo(BigDecimal.valueOf(0)) != 0) {
                        totalAmount += " + " + (numberFormat.format(new BigDecimal(elements.item(0).getTextContent()))).toString() + " " + sessionBean.currencySignOrCode(Integer.valueOf(elements.item(1).getTextContent()), 0);
                        count = 1;
                    }

                    if (calculateType == 1) { //Sale Total
                        if (groupCurrencySaleTotal.containsKey(Integer.valueOf(elements.item(1).getTextContent()))) {
                            BigDecimal old = groupCurrencySaleTotal.get(Integer.valueOf(elements.item(1).getTextContent()));
                            groupCurrencySaleTotal.put(Integer.valueOf(elements.item(1).getTextContent()), old.add(new BigDecimal(elements.item(0).getTextContent())));
                        } else {
                            groupCurrencySaleTotal.put(Integer.valueOf(elements.item(1).getTextContent()), new BigDecimal(elements.item(0).getTextContent()));
                        }
                    } else if (calculateType == 2) {// Payment Total

                        if (groupCurrencyPaymentTotal.containsKey(Integer.valueOf(elements.item(1).getTextContent()))) {
                            BigDecimal old = groupCurrencyPaymentTotal.get(Integer.valueOf(elements.item(1).getTextContent()));
                            groupCurrencyPaymentTotal.put(Integer.valueOf(elements.item(1).getTextContent()), old.add(new BigDecimal(elements.item(0).getTextContent())));
                        } else {
                            groupCurrencyPaymentTotal.put(Integer.valueOf(elements.item(1).getTextContent()), new BigDecimal(elements.item(0).getTextContent()));
                        }
                    }

                }
                if (totalAmount.equals("")) {
                    totalAmount = "0";
                }
            } else {
                NodeList list = doc.getElementsByTagName("usersaleshift");
                if (list.getLength() == 0) {
                    totalAmount = "";
                }
                for (int s = 0; s < list.getLength(); s++) {
                    NodeList elements = list.item(s).getChildNodes();
                    if (count == 0) {
                        totalAmount += elements.item(0).getTextContent() + " " + elements.item(1).getTextContent();
                    } else {
                        totalAmount += " , " + elements.item(0).getTextContent() + " " + elements.item(1).getTextContent();
                    }
                    count = 1;

                }
            }
        } catch (ParserConfigurationException ex) {
        } catch (SAXException | IOException ex) {
        }
        return totalAmount;

    }

    public void controlOpenShiftPayment() {
        int isopenShiftPayment = 0;
        isopenShiftPayment = marketShiftPaymentService.controlOpenShiftPayment();
        if (isopenShiftPayment == 0) {
            applicationBean.getBranchShiftPaymentMap().put(sessionBean.getUser().getLastBranch().getId(), false);
        } else if (isopenShiftPayment == 1) {
            applicationBean.getBranchShiftPaymentMap().put(sessionBean.getUser().getLastBranch().getId(), true);
        }
    }

    public void goToShiftPaymentProcess() {

        if (getRendered(50, 0)) {
            List<Object> list = new ArrayList<>();
            list.add(selectedConfirmShift);
            marwiz.goToPage("/pages/general/marketshift/marketshifttransferprocess.xhtml", list, 0, 104);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("youarenotallowedforthisprocess")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void goToShiftDetail() {

        List<Object> list = new ArrayList<>();
        list.add(selectedConfirmShift);
        marwiz.goToPage("/pages/general/report/marketshiftreport/marketshiftreportdetail.xhtml", list, 0, 149);

    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String calculateDifference(String no) {

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');

        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        compare = 4;
        String diff = "";
        HashMap<Integer, BigDecimal> tList = new HashMap<>();
        BigDecimal diff1 = BigDecimal.valueOf(0);
        for (Map.Entry<Integer, BigDecimal> entrySale : groupCurrencySaleTotal.entrySet()) {
            boolean isThere = false;
            for (Map.Entry<Integer, BigDecimal> entryPayment : groupCurrencyPaymentTotal.entrySet()) {
                if (entryPayment.getKey() == entrySale.getKey()) {
                    diff1 = entrySale.getValue().subtract(entryPayment.getValue());
                    groupCurrencyDiff.put(entryPayment.getKey(), diff1);
                    isThere = true;
                    tList.put(entryPayment.getKey(), diff1);
                }
            }
            if (!isThere) {
                groupCurrencyDiff.put(entrySale.getKey(), entrySale.getValue());
            }

        }
        if (tList.size() != groupCurrencyPaymentTotal.size()) {
            for (Map.Entry<Integer, BigDecimal> e1 : groupCurrencyPaymentTotal.entrySet()) {
                for (Map.Entry<Integer, BigDecimal> e2 : tList.entrySet()) {
                    if (e1.getKey() != e2.getKey()) {
                        if (e1.getValue().compareTo(BigDecimal.valueOf(0)) != 0) {
                            groupCurrencyDiff.put(e1.getKey(), e1.getValue().multiply(BigDecimal.valueOf(-1)));
                        }
                    }
                }
            }
        }
        int temp = 0;
        int c = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyDiff.entrySet()) {
            if (temp == 0) {
                temp = 1;
                diff += String.valueOf(numberFormat.format(entry.getValue()));
                if (entry.getKey() != 0) {
                    diff += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                }
            } else {
                diff += " + " + String.valueOf(numberFormat.format(entry.getValue()));
                if (entry.getKey() != 0) {
                    diff += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                }
            }
            if (entry.getValue().compareTo(BigDecimal.valueOf(0)) == -1) {
                c++;
            }

        }
        if (c == groupCurrencyDiff.size() && !groupCurrencyDiff.isEmpty()) {
            compare = -1;
        } else {
            compare = 1;
        }

        int x = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyDiff.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.valueOf(0)) == 0) {
                x++;
            }
        }
        if (x == groupCurrencyDiff.size()) {
            compare = 0;
        }

        if (diff.equals("")) {
            diff = "0";
            compare = 0;
        }

        groupCurrencyDiff.clear();
        groupCurrencyPaymentTotal.clear();
        groupCurrencySaleTotal.clear();
        return diff;

    }

}
