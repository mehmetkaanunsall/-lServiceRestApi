/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 09:58:07
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountCategorizationService;
import com.mepsan.marwiz.general.categorization.presentation.CategorizationBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountCategorizationConnection;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class AccountCategorizationTabBean extends CategorizationBean<AccountCategorizationConnection> {

    @ManagedProperty(value = "#{accountCategorizationService}")
    public IAccountCategorizationService accountCategorizationService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Account selectedObject;

    public Account getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Account selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setAccountCategorizationService(IAccountCategorizationService accountCategorizationService) {
        this.accountCategorizationService = accountCategorizationService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {

        System.out.println("--------AccountCategorizationTabBean---------");

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    selectedObject = (Account) ((ArrayList) sessionBean.parameter).get(i);

                }
            }
        }

        selectedCategoryList = new ArrayList<>();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Long itemId = (Long) request.getAttribute("item");

        item = new Item((int) (long) itemId);

        root = createCategoryTree();

        if (marwiz.getPageIdOfGoToPage() == 11) {
            setListBtn(sessionBean.checkAuthority(new int[]{87, 88, 89, 90}, 0));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{91, 92, 93, 94}, 0));
        }
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
        List<Categorization> listCategorization = accountCategorizationService.listCategorization(selectedObject, item);

        for (Categorization categorization : listCategorization) {
            if (categorization.getParentId().getId() == 0) {
                CheckboxTreeNode parentTreeNode = new CheckboxTreeNode(categorization, root);
                parentTreeNode.setExpanded(true);
                if (categorization.isChecked()) {
                    parentTreeNode.setSelected(true);
                    AccountCategorizationConnection categorizationConnection = newCategorizationConnection(categorization);
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
                    childTreeNode.setExpanded(true);
                    if (categorization.isChecked()) {
                        childTreeNode.setSelected(true);
                        AccountCategorizationConnection categorizationConnection = newCategorizationConnection(categorization);
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
        AccountCategorizationConnection categorizationConnection = newCategorizationConnection(selectCategory);
        accountCategorizationService.create(categorizationConnection);
        selectedCategoryList.add(categorizationConnection);
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
                AccountCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                if (!selectedCategoryList.contains(categorizationConnection)) {
                    selectedCategoryList.add(categorizationConnection);
                    accountCategorizationService.create(categorizationConnection);
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
            AccountCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) node.getParent().getData());
            if (!selectedCategoryList.contains(categorizationConnection)) {

                if (node.getParent().isSelected() == true) {
                    selectedCategoryList.add(categorizationConnection);
                    accountCategorizationService.create(categorizationConnection);
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
        AccountCategorizationConnection categorizationConnection = newCategorizationConnection(unSelectCategory);
        if (selectedCategoryList.contains(categorizationConnection)) {
            selectedCategoryList.remove(categorizationConnection);
            accountCategorizationService.update(categorizationConnection);
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
                AccountCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
                if (selectedCategoryList.contains(categorizationConnection)) {
                    selectedCategoryList.remove(categorizationConnection);
                    accountCategorizationService.update(categorizationConnection);
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
            AccountCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) node.getParent().getData());
            if (selectedCategoryList.contains(categorizationConnection)) {
                selectedCategoryList.remove(categorizationConnection);
                accountCategorizationService.update(categorizationConnection);
            }
            node = node.getParent();
        }
    }

    /**
     * Bu metot sağdaki kategorinin yanındaki çarpı butonuna basınca çalışır.
     * Seçilen kategori listede bulunarak listeden çıkarılır. Çocukları ve
     * parentları bulunarak onlar da listeden çıkartılır.
     */
    @Override
    public void removeSelectedCategories() {
        String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("category");
        int id = Integer.parseInt(param);

        for (AccountCategorizationConnection c : selectedCategoryList) {
            if (c.getCategorization().getId() == id) {

                removedCategory = c;
                break;
            }
        }
        selectedCategoryList.remove(removedCategory);
        accountCategorizationService.update(removedCategory);
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
            AccountCategorizationConnection acc = newCategorizationConnection(new Categorization());
            accountCategorizationService.allCreat(acc, selectedCategoryList, item);

        } else {
            accountCategorizationService.allUpdate(selectedObject, selectedCategoryList);
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
                AccountCategorizationConnection categorizationConnection = newCategorizationConnection((Categorization) treeNode.getData());
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
    public AccountCategorizationConnection newCategorizationConnection(Categorization categorization) {
        AccountCategorizationConnection categorizationConnection = new AccountCategorizationConnection();
        categorizationConnection.setAccount(selectedObject);
        categorizationConnection.setCategorization(categorization);
        return categorizationConnection;
    }
}
