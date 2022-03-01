/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.categorization.presentation.CategorizationBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockCategorizationConnection;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.inventory.stock.business.IStockCategorizationService;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author sinem.arslan
 */
@ManagedBean
@ViewScoped
public class StockCentralCategorizationTabBean extends CategorizationBean<StockCategorizationConnection> {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{stockCategorizationService}")
    public IStockCategorizationService stockCategorizationService;

    private Stock selectedObject;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setStockCategorizationService(IStockCategorizationService stockCategorizationService) {
        this.stockCategorizationService = stockCategorizationService;
    }

    public Stock getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Stock selectedObject) {
        this.selectedObject = selectedObject;
    }

    @PostConstruct
    @Override
    public void init() {

        System.out.println("--------StockCentralCategorizationTabBean---------");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    selectedObject = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    selectedCategoryList = new ArrayList<>();
                    item = new Item(45);
                    root = createCategoryTree();
                }
            }
        }
    }

    /**
     * Bu methot tree yi oluşur.
     *
     * @return root
     */
    @Override
    public TreeNode createCategoryTree() {

        root = new DefaultTreeNode(new Categorization(), null);
        root.setExpanded(true);
        List<Categorization> listCategorization = stockCategorizationService.listCategorization(selectedObject, item);
        listCategorization = new LinkedList<>(listCategorization);

        for (Categorization categorization : listCategorization) {
            if (categorization.getParentId().getId() == 0) {
                TreeNode parentTreeNode = new DefaultTreeNode(categorization, root);
                parentTreeNode.setSelectable(false);
                parentTreeNode.setExpanded(true);
                if (categorization.isChecked()) {
                    parentTreeNode.setSelected(true);
                    StockCategorizationConnection categorizationConnection = newCategorizationConnection(categorization);
                    selectedCategoryList.add(categorizationConnection);
                }

                findChildren(parentTreeNode, listCategorization);
            }
        }

        return root;
    }

    public void findChildren(TreeNode treeNode, List<Categorization> list) {

        for (Categorization categorization : list) {
            if (categorization.getParentId().getId() != 0) {
                if (categorization.getParentId().getId() == ((Categorization) treeNode.getData()).getId()) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(categorization, treeNode);
                    childTreeNode.setExpanded(true);
                    childTreeNode.setSelectable(false);
                    if (categorization.isChecked()) {
                        childTreeNode.setSelected(true);
                        StockCategorizationConnection categorizationConnection = newCategorizationConnection(categorization);
                        selectedCategoryList.add(categorizationConnection);
                    }
                    findChildren(childTreeNode, list);
                }
            }
        }
    }

    @Override
    public void onNodeSelect(NodeSelectEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selectChild(TreeNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selectParent(TreeNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onNodeUnSelect(NodeUnselectEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unSelectChild(TreeNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unSelectParent(TreeNode node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSelectedCategories() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selectAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void allChildrens(TreeNode node, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override	
    public void findChildren(CheckboxTreeNode treeNode, List<Categorization> list) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Bu methot yeni categoryconnection oluşturur.
     *
     * @param categorization
     * @return
     */
    @Override
    public StockCategorizationConnection newCategorizationConnection(Categorization categorization) {
        StockCategorizationConnection categorizationConnection = new StockCategorizationConnection();
        categorizationConnection.setStock(selectedObject);
        categorizationConnection.setCategorization(categorization);
        return categorizationConnection;
    }

}
