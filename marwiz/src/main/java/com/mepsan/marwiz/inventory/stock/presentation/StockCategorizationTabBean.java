/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   18.01.2018 04:40:07
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
import javax.faces.context.FacesContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class StockCategorizationTabBean extends CategorizationBean<StockCategorizationConnection> {

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

        System.out.println("--------StockCategorizationTabBean---------");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    selectedObject = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    selectedCategoryList = new ArrayList<>();
                    item = new Item(2);
                    root = createCategoryTree();
                }
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{135, 136, 137, 138}, 0));

    }

    /**
     * Bu methot tree yi oluşur.
     *
     * @return root
     */
    @Override
    public TreeNode createCategoryTree() {

        root = new CheckboxTreeNode(new Categorization(), null);
        root.setExpanded(true);
        List<Categorization> listCategorization = stockCategorizationService.listCategorization(selectedObject, item);
        listCategorization = new LinkedList<>(listCategorization);

        for (Categorization categorization : listCategorization) {
            if (categorization.getParentId().getId() == 0) {
                CheckboxTreeNode parentTreeNode = new CheckboxTreeNode(categorization, root);
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

    /**
     * tree ye eklenen nodeların çocuklarını ekler.
     *
     * @param treeNode
     * @param list
     */
    @Override
    public void findChildren(CheckboxTreeNode treeNode, List<Categorization> list) {

        for (Categorization categorization : list) {
            if (categorization.getParentId().getId() != 0) {
                if (categorization.getParentId().getId() == ((Categorization) treeNode.getData()).getId()) {
                    CheckboxTreeNode childTreeNode = new CheckboxTreeNode(categorization, treeNode);
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

    /**
     * Treede select event ile dataliste veriyi ekleyen fonksiyondur.
     *
     * @param event
     */
    @Override
    public void onNodeSelect(NodeSelectEvent event) {

        Categorization selectCategory = (Categorization) event.getTreeNode().getData();
        StockCategorizationConnection categorizationConnection = newCategorizationConnection(selectCategory);
        if (!selectedCategoryList.contains(categorizationConnection)) {
            stockCategorizationService.create(categorizationConnection);
            selectedCategoryList.add(categorizationConnection);
        }
        selectChild(event.getTreeNode());
        selectParent(event.getTreeNode());

    }

    /**
     * Select eventi ile seçilen kategorinin childrenlarını bulup dataliste
     * ekleyen fonksiyondur.
     *
     * @param node seçilen kategori
     */
    @Override
    public void selectChild(TreeNode node) {
        List<TreeNode> children = node.getChildren();

        if (!children.isEmpty()) {

            for (TreeNode treeNode : children) {
                StockCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                if (!selectedCategoryList.contains(categorizationConnection)) {
                    selectedCategoryList.add(categorizationConnection);
                    stockCategorizationService.create(categorizationConnection);
                }

                selectChild(treeNode);
            }
        }

    }

    /**
     * Select eventi ile seçilen kategorinin parentlarını bulup dataliste
     * ekleyen fonksiyondur.
     *
     * @param node
     */
    @Override
    public void selectParent(TreeNode node) {
        while (node.getParent().getParent() != null) {
            StockCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) node.getParent().getData());
            if (!selectedCategoryList.contains(categorizationConnection)) {

                if (node.getParent().isSelected() == true) {
                    selectedCategoryList.add(categorizationConnection);
                    stockCategorizationService.create(categorizationConnection);
                }

            }
            node = node.getParent();
        }
    }

    /**
     * Treede unselect event ile datalisten kategoriyi silen fonksiyondur.
     *
     * @param event
     */
    @Override
    public void onNodeUnSelect(NodeUnselectEvent event) {
        Categorization unSelectCategory = (Categorization) event.getTreeNode().getData();
        StockCategorizationConnection categorizationConnection = newCategorizationConnection(unSelectCategory);
        if (selectedCategoryList.contains(categorizationConnection)) {
            selectedCategoryList.remove(categorizationConnection);
            stockCategorizationService.update(categorizationConnection);
            unSelectChild(event.getTreeNode());
            unSelectParent(event.getTreeNode());

        }

    }

    /**
     * Unselect eventi ile seçimi kaldırılan kategorinin childrenlarını bulup
     * datalistten silen fonksiyondur.
     *
     * @param node
     */
    @Override
    public void unSelectChild(TreeNode node) {
        List<TreeNode> children = node.getChildren();

        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {
                StockCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                if (selectedCategoryList.contains(categorizationConnection)) {
                    selectedCategoryList.remove(categorizationConnection);
                    stockCategorizationService.update(categorizationConnection);
                }

                unSelectChild(treeNode);
            }
        }

    }

    /**
     * Seçilen kategorinin parentlarını bularak listede varsa silen
     * fonksiyondur.
     *
     * @param node
     */
    @Override
    public void unSelectParent(TreeNode node) {
        while (node.getParent().getParent() != null) {
            StockCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) node.getParent().getData());
            if (selectedCategoryList.contains(categorizationConnection)) {
                selectedCategoryList.remove(categorizationConnection);
                stockCategorizationService.update(categorizationConnection);
            }
            node = node.getParent();
        }
    }

    /**
     * Bu metot sağdaki kategorinin yanındaki çarpı butonuna basınca çalışır.
     * Seçilen kategori listede bulunarak listeden çıkarılır. Çocukları ve
     * prentları bulunarak onlar da listeden çıkartılır.
     */
    @Override
    public void removeSelectedCategories() {
        String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("category");
        int id = Integer.parseInt(param);

        for (StockCategorizationConnection c : selectedCategoryList) {
            if (c.getCategorization().getId() == id) {

                removedCategory = c;
                break;
            }
        }
        selectedCategoryList.remove(removedCategory);
        stockCategorizationService.update(removedCategory);
        convertCategoryToTreeeNode(root, removedCategory.getCategorization());
        findTreeNode.setSelected(false);
        unSelectChild(findTreeNode);
        unSelectParent(findTreeNode);
        selectedCategories = null;
    }

    /**
     * Tüm kategoriler seçildiğinde checkboxa göre tümünü ekler ya da çıkarır.
     */
    @Override
    public void selectAll() {
        if (isSelectAllCategory) {
            stockCategorizationService.allCreat(selectedObject, selectedCategoryList, item);

        } else {
            stockCategorizationService.allUpdate(selectedObject, selectedCategoryList);
        }
        selectedCategoryList = new ArrayList<>();
        allChildrens(root, isSelectAllCategory);
    }

    /**
     * Tümünü seçtiğinde tüm tree yi dolaşarak ekleme ya da çıkarma yapar.
     *
     * @param node
     * @param b
     */
    @Override
    public void allChildrens(TreeNode node, boolean b) {
        for (TreeNode treeNode : node.getChildren()) {
            if (b) {
                treeNode.setSelected(true);
                StockCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                selectedCategoryList.add(categorizationConnection);
            } else {
                treeNode.setSelected(false);
            }
            allChildrens(treeNode, b);
        }
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
