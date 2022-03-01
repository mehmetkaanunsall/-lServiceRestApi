/**
 * This class ...
 *
 *
 * @author Emrullah YAKIŞAN
 *
 * @date   11.04.2019 09:46:15
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.categorization.business.ICategorizationService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class CategoryBookFilterBean extends BookFilterBean<Categorization> {

    @ManagedProperty(value = "#{categorizationService}")
    private ICategorizationService categorizationService;

    private TreeNode root;
    private TreeNode selectedNode;
    public Categorization category;

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void setCategorizationService(ICategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------CategoryBookFilterBean-----------");
        category = new Categorization();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Long longid = (Long) request.getAttribute("categoryBookItemId");
        System.out.println("---request.getAttribute(\"categoryBookItemId\");----" + request.getAttribute("categoryBookItemId"));
        System.out.println("---longid----" + longid);
        int id = (int) (long) longid;
        category.setItem(new Item(id));

        root = new DefaultTreeNode();

    }

    @Override
    public List<Categorization> callService(List<Object> param, String type) {
        root = new DefaultTreeNode(new Categorization(), null);
        root.setExpanded(true);

        List<Categorization> listCegorization = categorizationService.listCategorization(category);
        for (Categorization categorization : listCegorization) {
            if (categorization.getParentId().getId() == 0) {
                DefaultTreeNode parentTreeNode = new DefaultTreeNode(categorization, root);
                parentTreeNode.setExpanded(true);
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

                    findChildren(childTreeNode, list);
                }
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
