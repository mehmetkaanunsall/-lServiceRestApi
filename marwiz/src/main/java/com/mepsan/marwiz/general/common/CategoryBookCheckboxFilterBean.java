/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   03.03.2017 09:46:15
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.finance.discount.presentation.DiscountAccountTabCategoryBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.categorization.business.ICategorizationService;
import com.mepsan.marwiz.general.dashboard.presentation.DashboardBean;
import com.mepsan.marwiz.general.report.accountextract.presentation.AccountExtractBean;
import com.mepsan.marwiz.general.report.entryexitsummaryreport.presentation.EntryExitSummaryReportBean;
import com.mepsan.marwiz.general.report.freestockreport.presentation.FreeStockReportBean;
import com.mepsan.marwiz.general.report.fulltakingreport.presentation.FullTakingReportBean;
import com.mepsan.marwiz.general.report.movementreportbetweenwarehousetakings.presentation.MovementReportBetweenWarehouseTakingsBean;
import com.mepsan.marwiz.general.report.orderlistreport.presentation.OrderListReportBean;
import com.mepsan.marwiz.general.report.productmovementreport.presentation.ProductMovementReportBean;
import com.mepsan.marwiz.general.report.profitmarginreport.presentation.ProfitMarginReportBean;
import com.mepsan.marwiz.general.report.purchasedetailreport.presentation.PurchaseDetailReportBean;
import com.mepsan.marwiz.general.report.purchasesalesreport.presentation.PurchaseSalesReportBean;
import com.mepsan.marwiz.general.report.salesdetailreport.presentation.SalesDetailReportBean;
import com.mepsan.marwiz.general.report.salessummaryreport.presentation.SalesSummaryReportBean;
import com.mepsan.marwiz.general.report.stockinventoryreport.presentation.StockInventoryReportBean;
import com.mepsan.marwiz.general.report.wastereport.presentation.WasteReportBean;
import com.mepsan.marwiz.inventory.stockoperations.presentation.StockOperationsBean;
import com.mepsan.marwiz.inventory.stocktaking.presentation.StockTakingBean;
import com.mepsan.marwiz.inventory.stocktaking.presentation.StockTakingProcessBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class CategoryBookCheckboxFilterBean extends BookFilterBean<Categorization> {

    @ManagedProperty(value = "#{categorizationService}")
    private ICategorizationService categorizationService;

    private TreeNode root;
    public Categorization category;
    String selectedCount;
    private List<Categorization> listOfCategorization;
    private TreeNode[] selectedCategories;
    private String types;

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public void setCategorizationService(ICategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    public String getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(String selectedCount) {
        this.selectedCount = selectedCount;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public TreeNode[] getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(TreeNode[] selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------CategoryBookCheckboxFilterBean-----------");
        category = new Categorization();

        root = new DefaultTreeNode();

        listOfCategorization = new ArrayList<>();

    }

    @Override
    public List<Categorization> callService(List<Object> param, String type) {
        root = new DefaultTreeNode(new Categorization(), null);
        root.setExpanded(true);
        types = type;
        category.setItem(new Item((int) (long) param.get(0)));

        List<Categorization> listCegorization = categorizationService.listCategorization(category);
        for (Categorization categorization : listCegorization) {
            if (categorization.getParentId().getId() == 0) {
                DefaultTreeNode parentTreeNode = new DefaultTreeNode(categorization, root);
                parentTreeNode.setExpanded(true);
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                if (types.equals("productMovementReportcheckbox")) {
                    ProductMovementReportBean productMovementReportBean = (ProductMovementReportBean) viewMap.get("productMovementReportBean");
                    if (productMovementReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("purchasesalereportCheckbox")) {
                    PurchaseSalesReportBean purchaseSalesReportBean = (PurchaseSalesReportBean) viewMap.get("purchaseSalesReportBean");
                    if (purchaseSalesReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("fulltakingreport")) {
                    FullTakingReportBean fullTakingReportBean = (FullTakingReportBean) viewMap.get("fullTakingReportBean");
                    if (fullTakingReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("SaleSummaryReportCheckbox")) {
                    SalesSummaryReportBean salesSummaryReportBean = (SalesSummaryReportBean) viewMap.get("salesSummaryReportBean");
                    if (salesSummaryReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("SaleDetailReportCheckbox")) {
                    SalesDetailReportBean salesDetailReportBean = (SalesDetailReportBean) viewMap.get("salesDetailReportBean");
                    if (salesDetailReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("entryExitSummaryReportcheckbox")) {
                    EntryExitSummaryReportBean entryExitSummaryReportBean = (EntryExitSummaryReportBean) viewMap.get("entryExitSummaryReportBean");
                    if (entryExitSummaryReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("DashboardCheckbox")) {
                    DashboardBean dashboardBean = (DashboardBean) viewMap.get("dashboardBean");
                    if (dashboardBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("profitMarginReportCheckbox")) {
                    ProfitMarginReportBean profitMarginReportBean = (ProfitMarginReportBean) viewMap.get("profitMarginReportBean");
                    if (isAll) {
                        if (!profitMarginReportBean.getListOfCategorization().contains(categorization)) {
                            profitMarginReportBean.getListOfCategorization().add(categorization);
                        }
                        parentTreeNode.setSelected(true);
                    } else if (!isAll) {
                        if (profitMarginReportBean.getListOfCategorization().contains(categorization)) {
                            parentTreeNode.setSelected(true);
                        }
                    }
                } else if (types.equals("stockTakingCheckbox")) {
                    StockTakingProcessBean stockTakingProcessBean = (StockTakingProcessBean) viewMap.get("stockTakingProcessBean");
                    if (stockTakingProcessBean != null) {
                        if (stockTakingProcessBean.getSelectedObject().getListOfCategorization().contains(categorization)) {
                            parentTreeNode.setSelected(true);
                        }
                    } else if (stockTakingProcessBean == null) {
                        StockTakingBean stockTakingBean = (StockTakingBean) viewMap.get("stockTakingBean");
                        if (stockTakingBean != null) {
                            if (stockTakingBean.getSelectedObject().getListOfCategorization().contains(categorization)) {
                                parentTreeNode.setSelected(true);
                            }
                        }
                    }
                } else if (types.equals("stockinventoryreport")) {
                    StockInventoryReportBean stockInventoryReportBean = (StockInventoryReportBean) viewMap.get("stockInventoryReportBean");
                    if (stockInventoryReportBean.getSelectedObject().getListOfStockCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("orderListReportCheckbox")) {
                    OrderListReportBean orderListReportBean = (OrderListReportBean) viewMap.get("orderListReportBean");
                    if (orderListReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("movementReportBetweenWarehouseTakings")) {
                    MovementReportBetweenWarehouseTakingsBean movementReportBetweenWarehouseTakingsBean = (MovementReportBetweenWarehouseTakingsBean) viewMap.get("movementReportBetweenWarehouseTakingsBean");
                    if (movementReportBetweenWarehouseTakingsBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("accountExtract")) {
                    AccountExtractBean accountExtractBean = (AccountExtractBean) viewMap.get("accountExtractBean");
                    if (accountExtractBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("wasteReportCheckbox")) {
                    WasteReportBean wasteReportBean = (WasteReportBean) viewMap.get("wasteReportBean");
                    if (wasteReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("freeStockReportCheckbox")) {
                    FreeStockReportBean freeStockReportBean = (FreeStockReportBean) viewMap.get("freeStockReportBean");
                    if (freeStockReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("purchaseDetailReportCheckbox")) {
                    PurchaseDetailReportBean purchaseDetailReportBean = (PurchaseDetailReportBean) viewMap.get("purchaseDetailReportBean");
                    if (purchaseDetailReportBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                } else if (types.equals("stockOperationCategoryCheckbox")) {
                    StockOperationsBean stockOperationsBean = (StockOperationsBean) viewMap.get("stockOperationsBean");
                    if (stockOperationsBean.getListOfCategorization().contains(categorization)) {
                        parentTreeNode.setSelected(true);
                    }
                }

                findChildren(parentTreeNode, listCegorization);
            }
        }

        return null;
    }

    public void findChildren(DefaultTreeNode treeNode, List<Categorization> list) {
        for (Categorization categorization : list) {
            if (categorization.getParentId().getId() != 0) {
                if (categorization.getParentId().getId() == ((Categorization) treeNode.getData()).getId()) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(categorization, treeNode);
                    childTreeNode.setExpanded(true);
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    if (types.equals("productMovementReportcheckbox")) {
                        ProductMovementReportBean productMovementReportBean = (ProductMovementReportBean) viewMap.get("productMovementReportBean");
                        if (productMovementReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("fulltakingreport")) {
                        FullTakingReportBean fullTakingReportBean = (FullTakingReportBean) viewMap.get("fullTakingReportBean");
                        if (fullTakingReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("purchasesalereportCheckbox")) {
                        PurchaseSalesReportBean purchaseSalesReportBean = (PurchaseSalesReportBean) viewMap.get("purchaseSalesReportBean");
                        if (purchaseSalesReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("SaleSummaryReportCheckbox")) {
                        SalesSummaryReportBean salesSummaryReportBean = (SalesSummaryReportBean) viewMap.get("salesSummaryReportBean");
                        if (salesSummaryReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("SaleDetailReportCheckbox")) {
                        SalesDetailReportBean salesDetailReportBean = (SalesDetailReportBean) viewMap.get("salesDetailReportBean");
                        if (salesDetailReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("entryExitSummaryReportcheckbox")) {
                        EntryExitSummaryReportBean entryExitSummaryReportBean = (EntryExitSummaryReportBean) viewMap.get("entryExitSummaryReportBean");
                        if (entryExitSummaryReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("DashboardCheckbox")) {
                        DashboardBean dashboardBean = (DashboardBean) viewMap.get("dashboardBean");
                        if (dashboardBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("profitMarginReportCheckbox")) {
                        ProfitMarginReportBean profitMarginReportBean = (ProfitMarginReportBean) viewMap.get("profitMarginReportBean");
                        if (isAll) {
                            childTreeNode.setSelected(true);
                            if (!profitMarginReportBean.getListOfCategorization().contains(categorization)) {
                                profitMarginReportBean.getListOfCategorization().add(categorization);
                            }
                        } else if (!isAll) {
                            if (profitMarginReportBean.getListOfCategorization().contains(categorization)) {
                                childTreeNode.setSelected(true);
                            }
                        }

                    } else if (types.equals("stockTakingCheckbox")) {
                        StockTakingProcessBean stockTakingProcessBean = (StockTakingProcessBean) viewMap.get("stockTakingProcessBean");
                        if (stockTakingProcessBean != null) {
                            if (stockTakingProcessBean.getSelectedObject().getListOfCategorization().contains(categorization)) {
                                childTreeNode.setSelected(true);
                            }
                        } else if (stockTakingProcessBean == null) {
                            StockTakingBean stockTakingBean = (StockTakingBean) viewMap.get("stockTakingBean");
                            if (stockTakingBean != null) {
                                if (stockTakingBean.getSelectedObject().getListOfCategorization().contains(categorization)) {
                                    childTreeNode.setSelected(true);
                                }
                            }
                        }
                    } else if (types.equals("stockinventoryreport")) {
                        StockInventoryReportBean stockInventoryReportBean = (StockInventoryReportBean) viewMap.get("stockInventoryReportBean");
                        if (stockInventoryReportBean.getSelectedObject().getListOfStockCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("orderListReportCheckbox")) {
                        OrderListReportBean orderListReportBean = (OrderListReportBean) viewMap.get("orderListReportBean");
                        if (orderListReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("movementReportBetweenWarehouseTakings")) {
                        MovementReportBetweenWarehouseTakingsBean movementReportBetweenWarehouseTakingsBean = (MovementReportBetweenWarehouseTakingsBean) viewMap.get("movementReportBetweenWarehouseTakingsBean");
                        if (movementReportBetweenWarehouseTakingsBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("accountExtract")) {
                        AccountExtractBean accountExtractBean = (AccountExtractBean) viewMap.get("accountExtractBean");
                        if (accountExtractBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("wasteReportCheckbox")) {
                        WasteReportBean wasteReportBean = (WasteReportBean) viewMap.get("wasteReportBean");
                        if (wasteReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("freeStockReportCheckbox")) {
                        FreeStockReportBean freeStockReportBean = (FreeStockReportBean) viewMap.get("freeStockReportBean");
                        if (freeStockReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("purchaseDetailReportCheckbox")) {
                        PurchaseDetailReportBean purchaseDetailReportBean = (PurchaseDetailReportBean) viewMap.get("purchaseDetailReportBean");
                        if (purchaseDetailReportBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    } else if (types.equals("stockOperationCategoryCheckbox")) {
                        StockOperationsBean stockOperationsBean = (StockOperationsBean) viewMap.get("stockOperationsBean");
                        if (stockOperationsBean.getListOfCategorization().contains(categorization)) {
                            childTreeNode.setSelected(true);
                        }
                    }

                    findChildren(childTreeNode, list);
                }
            }
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        if (types.equals("profitMarginReportCheckbox")) {
            isAll = Boolean.FALSE;
            RequestContext.getCurrentInstance().update("frmCategoryBookFilter");
        }
        listOfCategorization.add((Categorization) event.getTreeNode().getData());
        selectChild(event.getTreeNode());
        selectParent(event.getTreeNode());

    }

    public void onNodeSelectDashboard(NodeSelectEvent nodeSelectEvent) {
        listOfCategorization.add((Categorization) nodeSelectEvent.getTreeNode().getData());
    }

    public void onNodeUnSelectDashboard(NodeUnselectEvent nodeUnselectEvent) {
        if (listOfCategorization.contains((Categorization) nodeUnselectEvent.getTreeNode().getData())) {
            listOfCategorization.remove((Categorization) nodeUnselectEvent.getTreeNode().getData());
        }
    }

    /**
     * Hepsi checkBoxu tıklanıldığında tüm parent ve childleri seçer.
     *
     * @param isAll
     */
    public void selectAll(boolean isAll) {
        List<TreeNode> children = root.getChildren();

        for (TreeNode treeNode : children) {
            if (isAll) {
                treeNode.setSelected(true);
                if (!treeNode.getChildren().isEmpty()) {
                    selectAllChildren(treeNode.getChildren());
                }
            } else {
                treeNode.setSelected(false);
                if (!treeNode.getChildren().isEmpty()) {
                    unSelectAllChildren(treeNode.getChildren());
                }
            }
        }

    }

    public void selectAllChildren(List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            node.setSelected(true);
            if (!node.getChildren().isEmpty()) {
                selectAllChildren(node.getChildren());
            }
        }
    }

    public void unSelectAllChildren(List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            node.setSelected(false);
            if (!node.getChildren().isEmpty()) {
                unSelectAllChildren(node.getChildren());
            }
        }
    }

    public void selectChild(TreeNode node) {
        List<TreeNode> children = node.getChildren();
        if (!children.isEmpty()) {

            for (TreeNode treeNode : children) {
                if (!listOfCategorization.contains((Categorization) treeNode.getData())) {
                    listOfCategorization.add((Categorization) treeNode.getData());

                }
                selectChild(treeNode);
            }
        }

    }

    public void selectParent(TreeNode node) {
        while (node.getParent().getParent() != null) {
            if (!listOfCategorization.contains((Categorization) node.getParent().getData())) {

                if (node.getParent().isSelected() == true) {
                    listOfCategorization.add((Categorization) node.getParent().getData());
                }

            }
            node = node.getParent();
        }
    }

    public void onNodeUnSelect(NodeUnselectEvent event) {
        if (listOfCategorization.contains((Categorization) event.getTreeNode().getData())) {
            if (types.equals("profitMarginReportCheckbox")) {
                isAll = Boolean.FALSE;
                RequestContext.getCurrentInstance().update("frmCategoryBookFilter");
            }
            listOfCategorization.remove((Categorization) event.getTreeNode().getData());
            unSelectChild(event.getTreeNode());
            unSelectParent(event.getTreeNode());

        }

    }

    public void unSelectChild(TreeNode node) {
        List<TreeNode> children = node.getChildren();

        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {
                if (listOfCategorization.contains((Categorization) treeNode.getData())) {
                    listOfCategorization.remove((Categorization) treeNode.getData());
                }

                unSelectChild(treeNode);
            }
        }

    }

    public void unSelectParent(TreeNode node) {
        while (node.getParent().getParent() != null) {
            if (listOfCategorization.contains((Categorization) node.getParent().getData())) {
                listOfCategorization.remove((Categorization) node.getParent().getData());
            }
            node = node.getParent();
        }
    }

    public void clearSelected() {
        clearSelectedChildren(root);
        selectedCategories = null;
        listOfCategorization.clear();
        RequestContext.getCurrentInstance().update(getUpdate() + ":ttbCategory");

    }

    public void clearSelectedChildren(TreeNode node) {
        List<TreeNode> children = node.getChildren();

        if (!children.isEmpty()) {

            for (TreeNode treeNode : children) {
                treeNode.setSelected(false);
                clearSelectedChildren(treeNode);
            }
        }

    }

    @Override
    public LazyDataModel<Categorization> callServiceLazyLoading(String where, List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter(String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
