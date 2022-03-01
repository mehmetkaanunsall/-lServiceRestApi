/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 02:58:49
 */
package com.mepsan.marwiz.general.categorization.presentation;

import com.mepsan.marwiz.general.categorization.business.ICategorizationService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

public abstract class CategorizationBean<T> extends AuthenticationLists{

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{categorizationService}")
    public ICategorizationService categorizationService;

    public TreeNode root;
    public TreeNode[] selectedCategories;
    public Categorization category, selectedData;
    public Item item;
    public TreeNode findTreeNode;
    public List<T> selectedCategoryList;
    public T removedCategory;
    public int processType;
    public boolean isSelectAllCategory;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCategorizationService(ICategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public Categorization getSelectedData() {
        return selectedData;
    }

    public void setSelectedData(Categorization selectedData) {
        this.selectedData = selectedData;
    }

    public Categorization getCategory() {
        return category;
    }

    public void setCategory(Categorization category) {
        this.category = category;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public TreeNode[] getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(TreeNode[] selectedCategories) {
        this.selectedCategories = selectedCategories;
    }

    public List<T> getSelectedCategoryList() {
        return selectedCategoryList;
    }

    public void setSelectedCategoryList(List<T> selectedCategoryList) {
        this.selectedCategoryList = selectedCategoryList;
    }

    public boolean isIsSelectAllCategory() {
        return isSelectAllCategory;
    }

    public void setIsSelectAllCategory(boolean isSelectAllCategory) {
        this.isSelectAllCategory = isSelectAllCategory;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public abstract void init();

    public abstract TreeNode createCategoryTree();

    public abstract void findChildren(CheckboxTreeNode treeNode, List<Categorization> list);

    public abstract void onNodeSelect(NodeSelectEvent event);

    public abstract void selectChild(TreeNode node);

    public abstract void selectParent(TreeNode node);

    public abstract void onNodeUnSelect(NodeUnselectEvent event);

    public abstract void unSelectChild(TreeNode node);

    public abstract void unSelectParent(TreeNode node);

    public abstract void removeSelectedCategories();

    public abstract void selectAll();

    public abstract void allChildrens(TreeNode node, boolean b);

    public abstract T newCategorizationConnection(Categorization categorization);

    /**
     * Bu methot seçilen kategorinin tree de hangi node a karşılık geldiğini
     * bulur.
     *
     * @param node
     * @param categorization
     */
    public void convertCategoryToTreeeNode(TreeNode node, Categorization categorization) {
        List<TreeNode> children = node.getChildren();
        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {

                if (((Categorization) treeNode.getData()).getId() == categorization.getId()) {
                    findTreeNode = treeNode;

                    break;

                } else {
                    convertCategoryToTreeeNode(treeNode, categorization);
                }

            }
        }
    }

    /**
     * Bu methot işlem tipine göre ana kategori ekleme,alt kategori ekleme ya da
     * kategori düzenleme için dialog açar.
     *
     * @param type işlem tipi
     * @param item categorinin item i
     */
    public void createDialog(int type, Item item) {
        processType = type;
        switch (type) {
            case 1:
                /*   ana categori ekleme      */
                category = new Categorization();
                category.setItem(item);
                break;
            case 2:
                /*  çocuk ekleme  */
                category = new Categorization();
                category.setItem(item);
                category.setParentId(selectedData);
                break;
            default:/* düzenleme   */
                category = selectedData;
                break;
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_category').show();");
    }

    /**
     * Bu methot dialogdaki kaydet butonunda çağırılır. İşlem tipine göre ana
     * kategori ekleme,alt kategori ekleme ya da kategori düzenleme yapar.
     */
    public void save() {

        int result = 0;
        switch (processType) {

            case 1:
                /*   ana categori ekleme      */
                result = categorizationService.create(category);
                if (result > 0) {
                    category.setId(result);
                    TreeNode nodeParent = new CheckboxTreeNode(category, root);
                    nodeParent.setExpanded(true);
                }
                break;
            case 2:
                /*  çocuk ekleme  */
                result = categorizationService.create(category);
                if (result > 0) {
                    category.setId(result);
                    convertCategoryToTreeeNode(root, selectedData);
                    TreeNode nodeChild = new CheckboxTreeNode(category, findTreeNode);
                    nodeChild.setExpanded(true);
                    unSelectParent(nodeChild);
                }
                break;
            default:/* düzenleme   */
                result = categorizationService.update(category);
                break;
        }
        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_category').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void testBeforeDelete(Item item) {
        int result = 0;
        selectedData.setItem(item);
        result = categorizationService.testBeforeDelete(selectedData);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmCategory:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteCategory').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausecategoryhassubcategory")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        if (selectedData != null) {
            int result = 0;
            result = categorizationService.delete(selectedData);
            if (result > 0) {
                convertCategoryToTreeeNode(root, selectedData);
                if (((Categorization) findTreeNode.getParent().getData()).getId() == 0) {
                    root.getChildren().remove(findTreeNode);
                } else {
                    findTreeNode.getParent().getChildren().remove(findTreeNode);

                }
                selectedData = null;
                selectedCategories = null;
                selectedCategoryList = new ArrayList();
                root = createCategoryTree();

            }
            sessionBean.createUpdateMessage(result);
        }
    }

}
