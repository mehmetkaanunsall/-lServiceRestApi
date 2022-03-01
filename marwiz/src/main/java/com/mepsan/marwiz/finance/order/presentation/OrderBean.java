/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.presentation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.order.business.GFOrderItemService;
import com.mepsan.marwiz.finance.order.business.GFOrderService;
import com.mepsan.marwiz.finance.order.business.IOrderItemService;
import com.mepsan.marwiz.finance.order.business.IOrderService;
import com.mepsan.marwiz.finance.order.dao.OrderReport;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.dashboard.dao.NotificationRecommendedPrice;
import com.mepsan.marwiz.general.dashboard.dao.UserNotification;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.log.SendOrder;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.service.order.business.ISendOrderService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class OrderBean extends GeneralBean<Order> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{orderService}")
    private IOrderService orderService;

    @ManagedProperty(value = "#{orderItemService}")
    private IOrderItemService orderItemService;

    @ManagedProperty(value = "#{gfOrderService}")
    private GFOrderService gFOrderService;

    @ManagedProperty(value = "#{gfOrderItemService}")
    private GFOrderItemService gFOrderItemService;

    @ManagedProperty(value = "#{sendOrderService}")
    public ISendOrderService sendOrderService;

    private OrderReport searchObject;
    private String createWhere;
    private List<BranchSetting> listOfBranch;

    private LazyDataModel<OrderItem> listOfObjectsItem;
    private OrderItem selectedObjectItem;

    private List<OrderItem> orderItemListUpdate;

    private List<Order> orderListForInvoice;
    private List<OrderItem> orderItemListForInvoice;

    private Order orderForInvoice;

    private List<Order> sendCenterOrders;

    private int orderQuantityType;
    private String orderQuantityMessage;

    private NotificationRecommendedPrice notificationRecommendedPrice;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public OrderReport getSearchObject() {
        return searchObject;
    }

    public void setSearchObject(OrderReport searchObject) {
        this.searchObject = searchObject;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public void setOrderService(IOrderService orderService) {
        this.orderService = orderService;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setOrderItemService(IOrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    public LazyDataModel<OrderItem> getListOfObjectsItem() {
        return listOfObjectsItem;
    }

    public void setListOfObjectsItem(LazyDataModel<OrderItem> listOfObjectsItem) {
        this.listOfObjectsItem = listOfObjectsItem;
    }

    public OrderItem getSelectedObjectItem() {
        return selectedObjectItem;
    }

    public void setSelectedObjectItem(OrderItem selectedObjectItem) {
        this.selectedObjectItem = selectedObjectItem;
    }

    public List<Order> getOrderListForInvoice() {
        return orderListForInvoice;
    }

    public void setOrderListForInvoice(List<Order> orderListForInvoice) {
        this.orderListForInvoice = orderListForInvoice;
    }

    public List<OrderItem> getOrderItemListForInvoice() {
        return orderItemListForInvoice;
    }

    public void setOrderItemListForInvoice(List<OrderItem> orderItemListForInvoice) {
        this.orderItemListForInvoice = orderItemListForInvoice;
    }

    public List<OrderItem> getOrderItemListUpdate() {
        return orderItemListUpdate;
    }

    public void setOrderItemListUpdate(List<OrderItem> orderItemListUpdate) {
        this.orderItemListUpdate = orderItemListUpdate;
    }

    public int getOrderQuantityType() {
        return orderQuantityType;
    }

    public void setOrderQuantityType(int orderQuantityType) {
        this.orderQuantityType = orderQuantityType;
    }

    public String getOrderQuantityMessage() {
        return orderQuantityMessage;
    }

    public void setOrderQuantityMessage(String orderQuantityMessage) {
        this.orderQuantityMessage = orderQuantityMessage;
    }

    public void setgFOrderService(GFOrderService gFOrderService) {
        this.gFOrderService = gFOrderService;
    }

    public void setgFOrderItemService(GFOrderItemService gFOrderItemService) {
        this.gFOrderItemService = gFOrderItemService;
    }

    public void setSendOrderService(ISendOrderService sendOrderService) {
        this.sendOrderService = sendOrderService;
    }

    public Order getOrderForInvoice() {
        return orderForInvoice;
    }

    public void setOrderForInvoice(Order orderForInvoice) {
        this.orderForInvoice = orderForInvoice;
    }

    public NotificationRecommendedPrice getNotificationRecommendedPrice() {
        return notificationRecommendedPrice;
    }

    public void setNotificationRecommendedPrice(NotificationRecommendedPrice notificationRecommendedPrice) {
        this.notificationRecommendedPrice = notificationRecommendedPrice;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-------------------OrderBean");
        toogleList = createToggleList(sessionBean.getUser());
        listOfBranch = new ArrayList<>();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true);
        }
        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker
        selectedObject = new Order();
        selectedObjectItem = new OrderItem();

        orderItemListUpdate = new ArrayList<>();
        orderListForInvoice = new ArrayList<>();
        orderItemListForInvoice = new ArrayList<>();

        orderForInvoice = new Order();

        sendCenterOrders = new ArrayList<>();
        notificationRecommendedPrice = new NotificationRecommendedPrice();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof OrderReport) {
                    searchObject = (OrderReport) (((ArrayList) sessionBean.parameter).get(i));

                    accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
                    accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getAccountList());
                    if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else {
                        accountBookCheckboxFilterBean.selectedDataList.clear();
                        accountBookCheckboxFilterBean.selectedDataList.addAll(searchObject.getAccountList());
                        accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
                    }

                    stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
                    stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getStockList());
                    if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
                    } else {
                        stockBookCheckboxFilterBean.selectedDataList.clear();
                        stockBookCheckboxFilterBean.selectedDataList.addAll(searchObject.getStockList());
                        stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
                    }

                    List<BranchSetting> temp = new ArrayList<>();
                    temp.addAll(searchObject.getSelectedBranchList());
                    searchObject.getSelectedBranchList().clear();
                    for (BranchSetting br : listOfBranch) {
                        for (BranchSetting sbr : temp) {
                            if (br.getBranch().getId() == sbr.getBranch().getId()) {
                                searchObject.getSelectedBranchList().add(br);
                            }
                        }
                    }
                    break;
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof NotificationRecommendedPrice) {
                    notificationRecommendedPrice = (NotificationRecommendedPrice) (((ArrayList) sessionBean.parameter).get(i));
                    searchObject = new OrderReport();
                    searchObject.setIsCheckItem(true);
                    searchObject.getOrderType().setId(102);
                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    Date date = null;
                    try {
                        date = format.parse(notificationRecommendedPrice.getProcessdate());
                    } catch (ParseException ex) {
                        Logger.getLogger(OrderBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("date" + date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, 00);
                    calendar.set(Calendar.MINUTE, 00);
                    calendar.set(Calendar.SECOND, 00);
                    searchObject.setBeginDate(calendar.getTime());
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    searchObject.setEndDate(calendar.getTime());

                    for (BranchSetting branchSetting : listOfBranch) {
                        if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                            searchObject.getSelectedBranchList().add(branchSetting);
                            break;
                        }
                    }
                    changeBranch();
                }
            }
        }

        if (searchObject == null) {
            searchObject = new OrderReport();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            searchObject.setBeginDate(calendar.getTime());
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            searchObject.setEndDate(calendar.getTime());

            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    searchObject.getSelectedBranchList().add(branchSetting);
                    break;
                }
            }
            changeBranch();
        }

        createWhere = orderService.createWhere(searchObject, listOfBranch);
        find();

        setListBtn(sessionBean.checkAuthority(new int[]{331, 334, 339, 340}, 0));

    }

    public void find() {

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(searchObject.getEndDate());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        searchObject.setEndDate(cal.getTime());

        cal.setTime(searchObject.getBeginDate());
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        searchObject.setBeginDate(cal.getTime());

        orderItemListUpdate.clear();
        orderItemListForInvoice.clear();
        orderListForInvoice.clear();

        createWhere = orderService.createWhere(searchObject, listOfBranch);
        if (!searchObject.isIsCheckItem()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true);
            listOfObjects = findall(createWhere);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            listOfObjectsItem = findallItem(createWhere);
        }
        RequestContext.getCurrentInstance().update("pgrOrderDatatable");
    }

    @Override
    public void create() {
        List<Object> list = new ArrayList<>();
        Order order = new Order();
        list.add(order);
        marwiz.goToPage("/pages/finance/order/orderprocess.xhtml", list, 0, 229);
    }

    public void goToProcess() {
        List<Object> list = new ArrayList<>();
        list.add(searchObject);
        if (!searchObject.isIsCheckItem()) {
            list.add(selectedObject);
        } else {
            list.add(selectedObjectItem.getOrder());
        }
        marwiz.goToPage("/pages/finance/order/orderprocess.xhtml", list, 0, 229);
    }

    @Override
    public void save() {

        if (!orderItemListUpdate.isEmpty()) {
            boolean isQuantityControl = false;
            for (OrderItem obj : orderItemListUpdate) {
                System.out.println("------" + obj.getStock().getName() + "--quantity" + obj.getQuantity());

                if (obj.getQuantity().compareTo(BigDecimal.ZERO) != 0) {

                    if (obj.getQuantity().compareTo(BigDecimal.ZERO) == -1) {
                        isQuantityControl = true;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("ordercontrolzeroquantity")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;
                    }

                    if (obj.getOrder().getType().getId() == 102 && (obj.getQuantity().compareTo(obj.getMinQuantity()) < 0 || obj.getQuantity().compareTo(obj.getMaxQuantity()) > 0) && !obj.isIsNewStockControl()) {
                        isQuantityControl = true;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("ordercontrolmaxminquantity")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;
                    }

                    if ((obj.getOrder().getType().getId() == 100 || obj.getOrder().getType().getId() == 101) && (obj.getQuantity().compareTo(obj.getMaxQuantity()) > 0) && !obj.isIsNewStockControl()) {
                        isQuantityControl = true;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("ordercontrolmaxquantity")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;
                    }
                }
            }

            if (!isQuantityControl) {
                int result = orderItemService.updateAll(orderItemListUpdate, new Order());
                if (result > 0) {
                    if (sessionBean.getLastBranchSetting().isIsCentralIntegration()) {
                        //merkeze gönderilecek siparişler belirleniyor.
                        for (OrderItem orderItem : orderItemListUpdate) {
                            boolean isThere = false;
                            for (Order order1 : sendCenterOrders) {
                                if (order1.getId() == orderItem.getOrder().getId()) {
                                    isThere = true;
                                    break;
                                }
                            }
                            if (!isThere) {
                                sendCenterOrders.add(orderItem.getOrder());
                            }
                        }
                    }

                    orderItemListUpdate.clear();

                    RequestContext.getCurrentInstance().update("pgrOrderDatatable");
                }
                sessionBean.createUpdateMessage(result);
            }
        } else {
            sessionBean.createUpdateMessage(1);
        }

    }

    @Override
    public void generalFilter() {

        if (!searchObject.isIsCheckItem()) {
            if (autoCompleteValue == null) {
                listOfObjects = findall(createWhere);
            } else {
                gFOrderService.makeSearch(autoCompleteValue, createWhere);
                listOfObjects = gFOrderService.searchResult;
            }
        } else {
            if (autoCompleteValue == null) {
                listOfObjectsItem = findallItem(createWhere);
            } else {
                gFOrderItemService.makeSearch(autoCompleteValue, createWhere);
                listOfObjectsItem = gFOrderItemService.searchResult;
            }
        }
    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {
            searchObject.getStockList().clear();
            if (stockBookCheckboxFilterBean.isAll) {
                Stock s = new Stock(0);
                if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Stock stock = new Stock(0);
                    stock.setName(sessionBean.loc.getString("all"));
                    stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
                }
            } else if (!stockBookCheckboxFilterBean.isAll) {
                if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }
            searchObject.getStockList().addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());
            RequestContext.getCurrentInstance().update("frmOrderReport:txtStock");
        } else {
            searchObject.getAccountList().clear();
            if (accountBookCheckboxFilterBean.isAll) {
                Account s = new Account(0);
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Account a = new Account(0);
                    a.setName(sessionBean.loc.getString("all"));
                    accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, a);
                }
            } else if (!accountBookCheckboxFilterBean.isAll) {
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        accountBookCheckboxFilterBean.getTempSelectedDataList().remove(accountBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }

            if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
            }
            searchObject.getAccountList().addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());
            RequestContext.getCurrentInstance().update("frmOrderReport:txtCustomer");
        }
    }

    public void openDialog(int type) {

        if (type == 0) {//cari
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!searchObject.getAccountList().isEmpty()) {
                if (searchObject.getAccountList().get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }
            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getAccountList());
        } else {//stok
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!searchObject.getStockList().isEmpty()) {
                if (searchObject.getStockList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(searchObject.getStockList());
        }
    }

    @Override
    public LazyDataModel<Order> findall(String where) {
        return new CentrowizLazyDataModel<Order>() {
            @Override
            public List<Order> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<Order> result = orderService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = orderService.count(where);
                listOfObjects.setRowCount(count);
                findCheckInvoice(result);
                DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmOrder:dtbOrder");
                if (dataTable != null) {
                    dataTable.setFirst(0);
                }
                return result;
            }
        };
    }

    public List findCheckInvoice(List<Order> result) {
        for (Order order : orderListForInvoice) {
            for (Order r : result) {
                if (order.getId() == r.getId()) {
                    r.setIsCheckInvoice(order.isIsCheckInvoice());
                    break;
                }
            }
        }
        return result;
    }

    public LazyDataModel<OrderItem> findallItem(String where) {
        return new CentrowizLazyDataModel<OrderItem>() {
            @Override
            public List<OrderItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<OrderItem> result = orderItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = orderItemService.count(where);
                findItemCheckInvoice(result);
                findItemQuantity(result);
                listOfObjectsItem.setRowCount(count);
                DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmOrder:dtbOrder");
                if (dataTable != null) {
                    dataTable.setFirst(0);
                }
                return result;
            }
        };
    }

    public List findItemCheckInvoice(List<OrderItem> result) {
        for (OrderItem orderItem : orderItemListForInvoice) {
            for (OrderItem r : result) {
                if (orderItem.getId() == r.getId()) {
                    r.setIsCheckInvoice(orderItem.isIsCheckInvoice());
                    break;
                }
            }
        }
        return result;
    }

    public void findItemQuantity(List<OrderItem> result) {

        for (OrderItem orderItem : orderItemListUpdate) {
            for (OrderItem r : result) {
                if (orderItem.getId() == r.getId()) {
                    r.setQuantity(orderItem.getQuantity());
                    break;
                }
            }
        }

    }

    public void changeItemQuantity(OrderItem obj) {

        if (obj.getQuantity() == null || obj.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            //silme için
            obj.setQuantity(BigDecimal.ZERO);
            boolean isThere = false;
            for (OrderItem orderItem1 : orderItemListUpdate) {
                if (orderItem1.getStock().getId() == obj.getStock().getId()) {
                    orderItem1.setQuantity(obj.getQuantity());
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                orderItemListUpdate.add(obj);
            }
        } else if (obj.getQuantity() != null && obj.getQuantity().compareTo(BigDecimal.ZERO) == 1) {

            if ((obj.getOrder().getType().getId() == 102 && (obj.getQuantity().compareTo(obj.getMinQuantity()) < 0 || obj.getQuantity().compareTo(obj.getMaxQuantity()) > 0)) && !obj.isIsNewStockControl()) {
                obj.setQuantity(BigDecimal.ZERO);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thevalueenteredmustbeintherangeofminimumtomaximumvalue")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                RequestContext.getCurrentInstance().update("pgrOrderDatatable");
            } else if (((obj.getOrder().getType().getId() == 100 || obj.getOrder().getType().getId() == 101) && (obj.getQuantity().compareTo(obj.getMaxQuantity()) > 0)) && !obj.isIsNewStockControl()) {
                obj.setQuantity(BigDecimal.ZERO);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("theenteredvaluecannotbegreaterthanthemaximumvalue")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                RequestContext.getCurrentInstance().update("pgrOrderDatatable");
            } else {

                boolean isThere = false;
                for (OrderItem orderItem1 : orderItemListUpdate) {
                    if (orderItem1.getStock().getId() == obj.getStock().getId()) {
                        orderItem1.setQuantity(obj.getQuantity());
                        isThere = true;
                        break;
                    }
                }
                if (!isThere) {
                    orderItemListUpdate.add(obj);
                }
            }
        }

    }

    public void showOrderQuantityMessage() {
        if (orderQuantityType == 1) {
            setOrderQuantityMessage(sessionBean.getLoc().getString("ordercontrolsetminquantity"));
        } else if (orderQuantityType == 2) {
            setOrderQuantityMessage(sessionBean.getLoc().getString("ordercontrolsetmaxquantity"));
        } else if (orderQuantityType == 3) {
            setOrderQuantityMessage(sessionBean.getLoc().getString("ordercontrolsetaveragequantity"));
        }
        RequestContext.getCurrentInstance().update("dlgOrderQuantityMessage");
        RequestContext.getCurrentInstance().execute("PF('dlg_OrderQuantityMessage').show();");

    }

    public void setOrderQuantity() {
        orderItemListUpdate.clear();
        List<OrderItem> findAllNotLazy = orderItemService.findAllNotLazy(createWhere);
        for (OrderItem orderItem : findAllNotLazy) {
            if (orderQuantityType == 1) {
                orderItem.setQuantity(orderItem.getMinQuantity());
            } else if (orderQuantityType == 2) {
                orderItem.setQuantity(orderItem.getMaxQuantity());
            } else if (orderQuantityType == 3) {
                orderItem.setQuantity((orderItem.getMinQuantity().add(orderItem.getMaxQuantity())).divide(BigDecimal.valueOf(2)));
            }
            orderItemListUpdate.add(orderItem);
        }
        RequestContext.getCurrentInstance().update("pgrOrderDatatable");
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeBranch() {
        accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        searchObject.getAccountList().clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        searchObject.getStockList().clear();

    }

    public void checkOrderItemForInvoice(OrderItem orderItem) {

        if (orderItem.isIsCheckInvoice()) {
            boolean isThere = false;
            boolean isControl = false;
            for (OrderItem orderItem1 : orderItemListForInvoice) {

                if (orderItem1.getOrder().getAccount().getId() != orderItem.getOrder().getAccount().getId()) {
                    orderItem.setIsCheckInvoice(false);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("theaccountsoftheselectedorderscannotbedifferent")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    RequestContext.getCurrentInstance().update("pgrOrderDatatable");
                    isControl = true;
                    break;

                }

                if (orderItem1.getOrder().getBranchSetting().getBranch().getId() != orderItem.getOrder().getBranchSetting().getBranch().getId()) {
                    orderItem.setIsCheckInvoice(false);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thebranchsoftheselectedorderscannotbedifferent")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    RequestContext.getCurrentInstance().update("pgrOrderDatatable");
                    isControl = true;
                    break;
                }

                if (orderItem1.getStock().getId() == orderItem.getStock().getId()) {
                    isThere = true;
                    break;
                }
            }
            if (!isThere && !isControl) {
                orderItemListForInvoice.add(orderItem);
            }
        } else {
            for (Iterator<OrderItem> iterator = orderItemListForInvoice.iterator(); iterator.hasNext();) {
                OrderItem value = iterator.next();
                if (value.getStock().getId() == orderItem.getStock().getId()) {
                    iterator.remove();
                }
            }
        }
        orderForInvoice = new Order();
        if (!orderItemListForInvoice.isEmpty()) {
            orderForInvoice = orderItemListForInvoice.get(0).getOrder();
        }

    }

    public void checkOrderForInvoice(Order order) {

        if (order.isIsCheckInvoice()) {
            boolean isThere = false;
            boolean isControl = false;
            for (Order order1 : orderListForInvoice) {

                if (order1.getAccount().getId() != order.getAccount().getId()) {
                    order.setIsCheckInvoice(false);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("theaccountsoftheselectedorderscannotbedifferent")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    RequestContext.getCurrentInstance().update("pgrOrderDatatable");
                    isControl = true;
                    break;
                }

                if (order1.getBranchSetting().getBranch().getId() != order.getBranchSetting().getBranch().getId()) {
                    order.setIsCheckInvoice(false);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thebranchsoftheselectedorderscannotbedifferent")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    RequestContext.getCurrentInstance().update("pgrOrderDatatable");
                    isControl = true;
                    break;
                }

                if (order1.getId() == order.getId()) {
                    isThere = true;
                    break;
                }
            }
            if (!isThere && !isControl) {
                orderListForInvoice.add(order);
            }
        } else {
            for (Iterator<Order> iterator = orderListForInvoice.iterator(); iterator.hasNext();) {
                Order value = iterator.next();
                if (value.getId() == order.getId()) {
                    iterator.remove();
                }
            }
        }
        orderForInvoice = new Order();
        if (!orderListForInvoice.isEmpty()) {
            orderForInvoice = orderListForInvoice.get(0);
        }
    }

    public void createInvoice() {
        if (orderListForInvoice.isEmpty() && orderItemListForInvoice.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("selectatleastoneorder")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {

            List<OrderItem> listItem = orderItemService.listOrderItemForCreateInvoice(orderListForInvoice, orderItemListForInvoice);

            if (!listItem.isEmpty()) {//Fatura oluşturabilir ise
                List<Object> list = new ArrayList<>();
                JsonArray jsonArray = new JsonArray();

                for (OrderItem item : listItem) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", item.getId());
                    jsonObject.addProperty("order_id", item.getOrder().getId());
                    jsonObject.addProperty("stock_id", item.getStock().getId());
                    jsonObject.addProperty("stock_centerproductcode", item.getStock().getCenterProductCode());
                    jsonObject.addProperty("unitrounding", item.getUnit().getUnitRounding());
                    jsonObject.addProperty("stock_name", item.getStock().getName());
                    jsonObject.addProperty("stock_barcode", item.getStock().getBarcode());
                    jsonObject.addProperty("unit_id", item.getUnit().getId());
                    jsonObject.addProperty("unit_sortname", item.getUnit().getSortName());
                    jsonObject.addProperty("unit_name", item.getUnit().getName());
                    jsonObject.addProperty("quantity", item.getQuantity());
                    jsonObject.addProperty("remainingquantity", item.getRemainingQuantity());
                    jsonObject.addProperty("currenctsaleprice", item.getStock().getStockInfo().getCurrentSalePrice());
                    jsonObject.addProperty("currenctsalecurrency", item.getStock().getStockInfo().getCurrentSaleCurrency().getId());
                    jsonObject.addProperty("taxrate", item.getTaxGroup().getRate());
                    jsonObject.addProperty("pricelistprice", item.getRecommendedPrice() == null ? BigDecimal.valueOf(0) : item.getRecommendedPrice());
                    jsonObject.addProperty("unitrounding", item.getUnit().getUnitRounding());
                    if (item.getCurrency().getId() == 0) {
                        jsonObject.addProperty("pricelistcurrency", item.getOrder().getBranchSetting().getBranch().getCurrency().getId());
                        jsonObject.addProperty("pricelistcurrencyname", sessionBean.currencySignOrCode(item.getOrder().getBranchSetting().getBranch().getCurrency().getId(), 0));
                    } else {
                        jsonObject.addProperty("pricelistcurrency", item.getCurrency().getId());
                        jsonObject.addProperty("pricelistcurrencyname", sessionBean.currencySignOrCode(item.getCurrency().getId(), 0));
                    }

                    jsonArray.add(jsonObject);
                }
                list.add(orderForInvoice);
                list.add(jsonArray);
                marwiz.goToPage("/pages/finance/invoice/invoiceprocess.xhtml", list, 0, 26);
            } else {//Fatura için ürün yok uyarı ver
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("selectedordersdoesnothaveanyproducts"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

        }
    }

    public void createWaybill() {
        if (orderListForInvoice.isEmpty() && orderItemListForInvoice.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("selectatleastoneorder")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {

            List<OrderItem> listItem = orderItemService.listOrderItemForCreateInvoice(orderListForInvoice, orderItemListForInvoice);

            if (!listItem.isEmpty()) {//İrsaliye oluşturabilir ise
                List<Object> list = new ArrayList<>();
                JsonArray jsonArray = new JsonArray();

                for (OrderItem item : listItem) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("id", item.getId());
                    jsonObject.addProperty("order_id", item.getOrder().getId());
                    jsonObject.addProperty("stock_id", item.getStock().getId());
                    jsonObject.addProperty("stock_centerproductcode", item.getStock().getCenterProductCode());
                    jsonObject.addProperty("stock_name", item.getStock().getName());
                    jsonObject.addProperty("stock_barcode", item.getStock().getBarcode());
                    jsonObject.addProperty("unit_id", item.getUnit().getId());
                    jsonObject.addProperty("unit_sortname", item.getUnit().getSortName());
                    jsonObject.addProperty("unit_name", item.getUnit().getName());
                    jsonObject.addProperty("unitrounding", item.getUnit().getUnitRounding());
                    jsonObject.addProperty("quantity", item.getQuantity());
                    jsonObject.addProperty("remainingquantity", item.getRemainingQuantity());
                    jsonObject.addProperty("currenctsaleprice", item.getStock().getStockInfo().getCurrentSalePrice());
                    jsonObject.addProperty("currenctsalecurrency", item.getStock().getStockInfo().getCurrentSaleCurrency().getId());
                    jsonObject.addProperty("taxrate", item.getTaxGroup().getRate());
                    jsonObject.addProperty("pricelistprice", item.getRecommendedPrice() == null ? BigDecimal.valueOf(0) : item.getRecommendedPrice());
                    jsonObject.addProperty("unitrounding", item.getUnit().getUnitRounding());
                    if (item.getCurrency().getId() == 0) {
                        jsonObject.addProperty("pricelistcurrency", item.getOrder().getBranchSetting().getBranch().getCurrency().getId());
                    } else {
                        jsonObject.addProperty("pricelistcurrency", item.getCurrency().getId());
                    }
                    jsonObject.addProperty("pricelistcurrencyname", item.getCurrency().getId() == 0 ? "" : item.getCurrency().getTag());

                    jsonArray.add(jsonObject);
                }

                list.add(orderForInvoice);
                list.add(jsonArray);
                System.out.println("jsonArray" + jsonArray);
                marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 0, 41);
            } else {//Fatura için ürün yok uyarı ver
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("selectedordersdoesnothaveanyproducts"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }

        }
    }

    /**
     * Bu metot sayfadan çıkıldığı anda tetiklenir. Merkez entegrasyonu var ise
     * ve değişikli oldu ise gerekli fonksiyonu tetkiler
     */
    @PreDestroy
    public void destroy() {
        int result;
        System.out.println("destroy");
        System.out.println("sendCenterOrders" + sendCenterOrders.size());
        if (sessionBean.getLastBranchSetting().isIsCentralIntegration()) {//merkeze gönderilecek ise ve merkez entegrasyonu var ise ve silinmedi ise

            for (Order sendCenterOrder : sendCenterOrders) {

                result = orderService.sendOrderCenter(sendCenterOrder);

                if (result > 0) {//işlem başarılı loga kayıt eklendi ise gönderme metodunu çağır.

                    SendOrder order = sendOrderService.findByOrderId(sendCenterOrder.getId());
                    sendOrderService.sendOrderToCenter(order);

                }
            }
        }

    }

}
