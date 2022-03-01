/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.presentation;

import com.mepsan.marwiz.finance.order.business.IOrderItemService;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class OrderItemTabBean extends GeneralDefinitionBean<OrderItem> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{orderItemService}")
    private IOrderItemService orderItemService;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    public StockBookFilterBean stockBookFilterBean;

    private Order selectedOrder;

    private List<OrderItem> orderItemListUpdate;

    private int orderQuantityType;
    private String orderQuantityMessage;

    private OrderItem manuelOrderItem; // hesaplamadan gelmeyen ürünler için 

    private OrderItem selectedOrderItemForDescription;

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setOrderItemService(IOrderItemService orderItemService) {
        this.orderItemService = orderItemService;
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

    public OrderItem getSelectedOrderItemForDescription() {
        return selectedOrderItemForDescription;
    }

    public void setSelectedOrderItemForDescription(OrderItem selectedOrderItemForDescription) {
        this.selectedOrderItemForDescription = selectedOrderItemForDescription;
    }

    public OrderItem getManuelOrderItem() {
        return manuelOrderItem;
    }

    public void setManuelOrderItem(OrderItem manuelOrderItem) {
        this.manuelOrderItem = manuelOrderItem;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--OrderItemTabBean------");
        selectedObject = new OrderItem();
        selectedOrder = new Order();
        selectedOrderItemForDescription = new OrderItem();
        manuelOrderItem = new OrderItem();
        orderItemListUpdate = new ArrayList();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Order) {
                    selectedOrder = (Order) ((ArrayList) sessionBean.parameter).get(i);

                    break;
                }
            }
        }

        listOfObjects = findall(1, selectedOrder);
        setListBtn(sessionBean.checkAuthority(new int[]{334,338}, 0));

    }

    public List<OrderItem> findall(int processType, Order obj) {
        return orderItemService.findAllAccordingToOrder(processType, obj);
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
        }else if (obj.getBoxQuantity().compareTo(BigDecimal.ZERO) == 0 || obj.getBoxQuantity().compareTo(BigDecimal.ZERO) == 0 || obj.getBoxQuantity().compareTo(BigDecimal.ZERO) == 0 || obj.getBoxQuantity().compareTo(BigDecimal.ZERO) == 0 || obj.isIsNewStockControl()){
            // manuel eklenmiştir kotnrole gerek yok
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
        }else if (obj.getQuantity() != null && obj.getQuantity().compareTo(BigDecimal.ZERO) == 1) {

            if (selectedOrder.getType().getId() == 102 && (obj.getQuantity().compareTo(obj.getMinQuantity()) < 0 || obj.getQuantity().compareTo(obj.getMaxQuantity()) > 0)) {
                obj.setQuantity(BigDecimal.ZERO);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thevalueenteredmustbeintherangeofminimumtomaximumvalue")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                RequestContext.getCurrentInstance().update("tbvOrder:frmOrderItem:dtbOrderItem");
            } else if ((selectedOrder.getType().getId() == 100 || selectedOrder.getType().getId() == 101) && (obj.getQuantity().compareTo(obj.getMaxQuantity()) > 0)) {
                obj.setQuantity(BigDecimal.ZERO);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("theenteredvaluecannotbegreaterthanthemaximumvalue")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                RequestContext.getCurrentInstance().update("tbvOrder:frmOrderItem:dtbOrderItem");
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
        for (OrderItem listOfObject : listOfObjects) {
            if (orderQuantityType == 1) {
                listOfObject.setQuantity(listOfObject.getMinQuantity());
            } else if (orderQuantityType == 2) {
                listOfObject.setQuantity(listOfObject.getMaxQuantity());
            } else if (orderQuantityType == 3) {
                listOfObject.setQuantity((listOfObject.getMinQuantity().add(listOfObject.getMaxQuantity())).divide(BigDecimal.valueOf(2)));
            }
            orderItemListUpdate.add(listOfObject);
        }

        RequestContext.getCurrentInstance().update("tbvOrder:frmOrderItem:dtbOrderItem");

    }

    @Override
    public void create() {
        manuelOrderItem=new OrderItem();
        RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').show();");
    }

    /**
     * kitaptan stok secıldıgınde calısır
     *
     */
    public void updateAllInformation() {

        if (stockBookFilterBean.getSelectedData() != null) {
            manuelOrderItem.setStock(stockBookFilterBean.getSelectedData());
            manuelOrderItem.setOrder(selectedOrder);
            manuelOrderItem.setUnit(stockBookFilterBean.getSelectedData().getUnit());
            manuelOrderItem.setCurrency(stockBookFilterBean.getSelectedData().getStockInfo().getPurchaseCurrency());
            manuelOrderItem.setRecommendedPrice(stockBookFilterBean.getSelectedData().getStockInfo().getPurchaseRecommendedPrice());

            List<OrderItem> findAllAccordingToStock = orderItemService.findAllAccordingToStock(1, manuelOrderItem);
            if(!findAllAccordingToStock.isEmpty()){
                manuelOrderItem=findAllAccordingToStock.get(0);
            }


            RequestContext.getCurrentInstance().update("frmManuelOrderItem:grdStokProcess");
            stockBookFilterBean.setSelectedData(null);
        }

    }

    @Override
    public void save() {
        if (!orderItemListUpdate.isEmpty()) {
            boolean isQuantityControl = false;

            for (OrderItem obj : orderItemListUpdate) {

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
                int result = orderItemService.createAll(orderItemListUpdate, selectedOrder);
                if (result > 0) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    OrderProcessBean orderProcessBean = (OrderProcessBean) viewMap.get("orderProcessBean");
                    orderProcessBean.setIsSendCenter(true);
                    orderItemListUpdate.clear();
                    listOfObjects = findall(1, selectedOrder);
                    RequestContext.getCurrentInstance().update("tbvOrder:frmOrderItem:dtbOrderItem");
                    boolean isStartOrder = false;
                    for (OrderItem ord : listOfObjects) {
                        if (ord.getQuantity().compareTo(BigDecimal.valueOf(0)) == 1) {
                            orderProcessBean.getSelectedObject().getStatus().setId(66);
                            isStartOrder = true;
                            RequestContext.getCurrentInstance().update("frmOrderProcess:slcStatu");
                            break;
                        }
                    }
                    if (isStartOrder == false) {
                        orderProcessBean.getSelectedObject().getStatus().setId(59);
                        RequestContext.getCurrentInstance().update("frmOrderProcess:slcStatu");
                    }
                }
                sessionBean.createUpdateMessage(result);
            }
        } else {
            sessionBean.createUpdateMessage(1);
        }

    }

    public void saveDescription(){
        if(selectedOrderItemForDescription.getStock().getId()>0){
            selectedOrderItemForDescription.setOrder(selectedOrder);
            int result = orderItemService.saveDescription(selectedOrderItemForDescription);
            if(result>0){
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                OrderProcessBean orderProcessBean = (OrderProcessBean) viewMap.get("orderProcessBean");
                orderProcessBean.setIsSendCenter(true);
                RequestContext.getCurrentInstance().execute("PF('dlg_description').hide();");
                RequestContext.getCurrentInstance().update("tbvOrder:frmOrderItem:dtbOrderItem");
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    public void saveManuelOrderItem(){
        manuelOrderItem.setOrder(selectedOrder);
        if(manuelOrderItem.getQuantity().compareTo(BigDecimal.ZERO)<=0){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("enteredvaluemustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }else if(manuelOrderItem.getMaxQuantity()!=null && manuelOrderItem.getMaxQuantity().compareTo(BigDecimal.ZERO)>0 && manuelOrderItem.getQuantity().compareTo(manuelOrderItem.getMaxQuantity())>0 && !manuelOrderItem.isIsNewStockControl()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("theenteredvaluecannotbegreaterthanthemaximumvalue")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }else{
            List<OrderItem> orderItems=new ArrayList<>();
            orderItems.add(manuelOrderItem);
            int result = orderItemService.createAll(orderItems, selectedOrder);
            if (result > 0) {
                RequestContext.getCurrentInstance().execute("PF('dlg_StockProcess').hide();");
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                OrderProcessBean orderProcessBean = (OrderProcessBean) viewMap.get("orderProcessBean");
                orderProcessBean.setIsSendCenter(true);
                orderItemListUpdate.clear();
                listOfObjects.add(manuelOrderItem);
                RequestContext.getCurrentInstance().update("tbvOrder:frmOrderItem:dtbOrderItem");

                boolean isStartOrder = false;
                for (OrderItem ord : listOfObjects) {
                    if (ord.getQuantity().compareTo(BigDecimal.valueOf(0)) == 1) {
                        orderProcessBean.getSelectedObject().getStatus().setId(66);
                        isStartOrder = true;
                        RequestContext.getCurrentInstance().update("frmOrderProcess:slcStatu");
                        break;
                    }
                }
                if (isStartOrder == false) {
                    orderProcessBean.getSelectedObject().getStatus().setId(59);
                    RequestContext.getCurrentInstance().update("frmOrderProcess:slcStatu");
                }
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    @Override
    public List<OrderItem> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
