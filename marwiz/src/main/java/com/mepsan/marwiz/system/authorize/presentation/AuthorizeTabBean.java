/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   18.01.2018 07:34:47
 */
package com.mepsan.marwiz.system.authorize.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Button;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.admin.Tab;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.authorize.business.AuthorizeService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class AuthorizeTabBean extends AuthenticationLists {

    private List<Page> pageList;
    private Authorize authorize;
    List<Integer> lPages;
    List<Integer> lFolders;
    List<Integer> lTabs;
    List<Integer> lButtons;
    List<TreeNode> controlList;

    private List<Module> modules;
    private List<Module> listOfModules;
    private List<Module> droppedModules;
    private Module module;
    private boolean isModuleTab;
    private DualListModel<Module> moduleList;

    private TreeNode rootProcess = new DefaultTreeNode();
    private TreeNode rootCard = new DefaultTreeNode();
    private TreeNode rootReport = new DefaultTreeNode();
    private TreeNode rootDefinition = new DefaultTreeNode();

    private boolean isSelectAllCard, isSelectAllProcess, isSelectAllReport, isSelectAllDefinition;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{authorizeService}")
    private AuthorizeService authorizeService;

    public List<Page> getPageList() {
        return pageList;
    }

    public void setPageList(List<Page> pageList) {
        this.pageList = pageList;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public TreeNode getRootProcess() {
        return rootProcess;
    }

    public void setRootProcess(TreeNode rootProcess) {
        this.rootProcess = rootProcess;
    }

    public TreeNode getRootCard() {
        return rootCard;
    }

    public void setRootCard(TreeNode rootCard) {
        this.rootCard = rootCard;
    }

    public TreeNode getRootReport() {
        return rootReport;
    }

    public void setRootReport(TreeNode rootReport) {
        this.rootReport = rootReport;
    }

    public TreeNode getRootDefinition() {
        return rootDefinition;
    }

    public void setRootDefinition(TreeNode rootDefinition) {
        this.rootDefinition = rootDefinition;
    }

    public boolean isIsSelectAllCard() {
        return isSelectAllCard;
    }

    public void setIsSelectAllCard(boolean isSelectAllCard) {
        this.isSelectAllCard = isSelectAllCard;
    }

    public boolean isIsSelectAllProcess() {
        return isSelectAllProcess;
    }

    public void setIsSelectAllProcess(boolean isSelectAllProcess) {
        this.isSelectAllProcess = isSelectAllProcess;
    }

    public boolean isIsSelectAllReport() {
        return isSelectAllReport;
    }

    public void setIsSelectAllReport(boolean isSelectAllReport) {
        this.isSelectAllReport = isSelectAllReport;
    }

    public boolean isIsSelectAllDefinition() {
        return isSelectAllDefinition;
    }

    public void setIsSelectAllDefinition(boolean isSelectAllDefinition) {
        this.isSelectAllDefinition = isSelectAllDefinition;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean isIsModuleTab() {
        return isModuleTab;
    }

    public void setIsModuleTab(boolean isModuleTab) {
        this.isModuleTab = isModuleTab;
    }

    public DualListModel<Module> getModuleList() {
        return moduleList;
    }

    public void setModuleList(DualListModel<Module> moduleList) {
        this.moduleList = moduleList;
    }

    @PostConstruct
    public void init() {
        pageList = new ArrayList();
        authorize = new Authorize();
        controlList = new ArrayList<>();
        isModuleTab = true;
        modules = new ArrayList<>();
        droppedModules = new ArrayList<Module>();
        listOfModules = new ArrayList<>();

        if (sessionBean.parameter instanceof Authorize) {
            authorize = (Authorize) sessionBean.parameter;
        }

        listOfModules = applicationBean.getListOfModules();

        for (Module m : listOfModules) {
            if (authorize.getListOfModules() != null) {
                if (authorize.getListOfModules().contains(m.getId())) {
                    droppedModules.add(m);
                } else {
                    modules.add(m);
                }
            } else {
                modules.add(m);
            }

        }

        moduleList = new DualListModel<Module>(modules, droppedModules);

        lPages = new ArrayList<>();
        lFolders = new ArrayList<>();
        lTabs = new ArrayList<>();
        lButtons = new ArrayList<>();

        if (authorize.getListOfPages() != null) {
            lPages.addAll(authorize.getListOfPages());
        }
        if (authorize.getListOfFolders() != null) {
            lFolders.addAll(authorize.getListOfFolders());
        }
        if (authorize.getListOfTabs() != null) {
            lTabs.addAll(authorize.getListOfTabs());
        }
        if (authorize.getListOfButtons() != null) {
            lButtons.addAll(authorize.getListOfButtons());
        }
        //createCheckboxFolder();

        setListBtn(sessionBean.checkAuthority(new int[]{267}, 0));
    }

    /**
     * Bu method ile yetkinin folder,page,tab ve buttonlar veri tabanına
     * kaydedilir.
     */
    public void save() {
         Comparator<Integer> compareById
                  = (Integer o1, Integer o2) -> o1.compareTo(o2);

        Collections.sort(lPages, compareById);
        Collections.sort(lFolders, compareById);
        Collections.sort(lTabs, compareById);
        Collections.sort(lButtons, compareById);

        int result = 0;
        authorize.setListOfPages(lPages);
        authorize.setListOfFolders(lFolders);
        authorize.setListOfTabs(lTabs);
        authorize.setListOfButtons(lButtons);

        result = authorizeService.updatePageTab(authorize);
        if (result > 0) {
            if (sessionBean.getUser().getLastAuthorize().getId() == authorize.getId()) {
                sessionBean.getUser().getLastAuthorize().setListOfPages(lPages);
                sessionBean.getUser().getLastAuthorize().setListOfFolders(lFolders);
                sessionBean.getUser().getLastAuthorize().setListOfTabs(lTabs);
                sessionBean.getUser().getLastAuthorize().setListOfButtons(lButtons);
            }

            goToPcrdForSelectedModule(false);
        }
        sessionBean.createUpdateMessage(result);

    }

    /**
     * PickListdeki transfer olayıyla transfer olan modulun veritabanına
     * kaydedilmesini sağlayan fonksiyon
     *
     * @param event ajax tranfer olayıyla gönderilen parametre
     */
    public void onTransfer(TransferEvent event) {
        authorize.setListOfModules(new ArrayList<>());
        for (Module m : moduleList.getTarget()) {
            authorize.getListOfModules().add(m.getId());

        }
        authorizeService.updateModuleTab(authorize);
        for (Object item : event.getItems()) {
            if (moduleList.getSource().contains((Module) item)) {
                deleteAllTreeNode((Module) item);
                authorizeService.updatePageTab(authorize);

            }
        }

    }

    /**
     * Bu method bir modulün yetkisi kaldırıldığında otomatik olarak bütün
     * klasör,page.. vb. yetkilerini kaldırmaktadır.
     *
     * @param item source tarafına transfer edilen modulü temsil eder.
     */
    public void deleteAllTreeNode(Module item) {

        if (!item.getFolders().isEmpty()) {
            for (Folder folder : item.getFolders()) {
                if (authorize.getListOfFolders().contains(folder.getId())) {
                    authorize.getListOfFolders().remove((Integer) folder.getId());
                }
                if (folder.getPages() != null) {
                    for (Page page : folder.getPages()) {
                        if (authorize.getListOfPages().contains(page.getId())) {
                            authorize.getListOfPages().remove((Integer) page.getId());
                            if (page.getTabs() != null) {
                                for (Tab tab : page.getTabs()) {
                                    if (authorize.getListOfTabs().contains(tab.getId())) {
                                        authorize.getListOfTabs().remove((Integer) tab.getId());
                                        if (tab.getListOfButtons() != null) {
                                            for (Button b : tab.getListOfButtons()) {
                                                if (authorize.getListOfButtons().contains(b.getId())) {
                                                    authorize.getListOfButtons().remove((Integer) b.getId());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (page.getButtons() != null) {
                                for (Button button : page.getButtons()) {
                                    if (authorize.getListOfButtons().contains(button.getId())) {
                                        authorize.getListOfButtons().remove((Integer) button.getId());
                                    }
                                }
                            }
                            if (page.getSubPages() != null) {
                                for (Page subpage : page.getSubPages()) {
                                    if (authorize.getListOfPages().contains(subpage.getId())) {
                                        authorize.getListOfPages().remove((Integer) subpage.getId());
                                        if (subpage.getTabs() != null) {
                                            for (Tab tab : subpage.getTabs()) {
                                                if (authorize.getListOfTabs().contains(tab.getId())) {
                                                    authorize.getListOfTabs().remove((Integer) tab.getId());
                                                    if (tab.getListOfButtons() != null) {
                                                        for (Button b : tab.getListOfButtons()) {
                                                            if (authorize.getListOfButtons().contains(b.getId())) {
                                                                authorize.getListOfButtons().remove((Integer) b.getId());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (subpage.getButtons() != null) {
                                            for (Button button : subpage.getButtons()) {
                                                if (authorize.getListOfButtons().contains(button.getId())) {
                                                    authorize.getListOfButtons().remove((Integer) button.getId());
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Bu method seçili olan modülü xhtml sayfasından alarak managed beane
     * gönderir.
     *
     * @param e seçili modülü alır.
     */
    public void passParameterToAuthorize(ActionEvent e) {
        String param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("module");
        int id = Integer.parseInt(param);

        for (Module module1 : moduleList.getTarget()) {
            if (module1.getId() == id) {

                module = module1;
                break;
            }
        }

        goToPcrdForSelectedModule(true);
    }

    /**
     * Bu method seçili module tıklandığında tab değiştirilerek module göre
     * folder, page,tab ve buttonların çekildiği fonksiyonu çağırmaktadır.Modül
     * seçilmeden devam edilmek istendiğinde ise uyarı verilmektedir.
     *
     * @param render tabın renderinin true ve ya false olarak xhtml tarafından
     * gönderilir
     */
    public void goToPcrdForSelectedModule(boolean render) {

        controlList = new ArrayList();
        if (render == true) {

            createCheckboxFolder();

            isModuleTab = false;

        } else if (render == false) {

            isSelectAllCard = false;
            isSelectAllDefinition = false;
            isSelectAllProcess = false;
            isSelectAllReport = false;
            isModuleTab = true;
        }
    }

    /**
     * Treenin oluşturulması için folderların çekilip tree ye basıldığı
     * fonksiyondur.
     */
    public void createCheckboxFolder() {

        List<Folder> folderList = new ArrayList();
        TreeNode folderTree = new CheckboxTreeNode();
        rootProcess = new CheckboxTreeNode(new Folder(), null);
        rootCard = new CheckboxTreeNode(new Folder(), null);
        rootReport = new CheckboxTreeNode(new Folder(), null);
        rootDefinition = new CheckboxTreeNode(new Folder(), null);
        folderList = module.getFolders();

        for (Folder f : folderList) {

            switch (f.getType()) {
                case 0: {
                    folderTree = new CheckboxTreeNode("folder", f, rootProcess);

                    break;
                }

                case 1: {
                    folderTree = new CheckboxTreeNode("folder", f, rootCard);
                    break;
                }
                case 2: {
                    folderTree = new CheckboxTreeNode("folder", f, rootReport);
                    break;
                }
                case 3: {
                    folderTree = new CheckboxTreeNode("folder", f, rootDefinition);
                    break;
                }
                default:
                    break;
            }
            folderTree.setExpanded(true);
            if (authorize.getListOfFolders().contains(f.getId())) {
                folderTree.setSelected(true);
            }
            findPages(folderTree);
            addChild(folderTree);

            if (authorize.isIsAdmin()) {//Admin yetki grubundan yetki kaldırılmasın diye
                if (f.getId() == 23) {
                    folderTree.setSelectable(false);
                }
            }

        }

    }

    /**
     * Oluşan foldera göre pagelerin tree ye eklendiği fonksiyondur.
     *
     * @param folderTree parent olarak childrenlarını bulmak için gönderilir.
     *
     */
    public void findPages(TreeNode folderTree) {

        List<Page> listOfPage = new ArrayList();
        listOfPage = ((Folder) folderTree.getData()).getPages();
        TreeNode pageTree = new CheckboxTreeNode();
        if (listOfPage != null) {
            for (Page p : listOfPage) {
                if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()
                          || (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && (p.getId() != 75 && p.getId() != 115))) {
                    if (sessionBean.getUser().getLastBranchSetting().getParoUrl() == null && p.getId() != 233
                              || sessionBean.getUser().getLastBranchSetting().getParoUrl() != null) {

                        pageTree = new CheckboxTreeNode("page", p, folderTree);
                        pageTree.setExpanded(true);
                        if (authorize.getListOfPages().contains(p.getId())) {
                            pageTree.setSelected(true);
                        }

                        findSubPages(pageTree);
                        findTabs(pageTree);
                        findButtons(pageTree);
                        addChild(pageTree);
                        if (authorize.isIsAdmin()) {//Admin yetki grubundan yetki kaldırılmasın diye
                            if (p.getId() == 15) {
                                pageTree.setSelectable(false);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Oluşan page göre subPagelerin tree ye eklendiği fonksiyondur.
     *
     * @param pageTree parent olarak childrenlarını bulmak için gönderilir.
     *
     */
    public void findSubPages(TreeNode pageTree) {
        TreeNode subPageTree = new CheckboxTreeNode();
        List<Page> listOfSubPage = new ArrayList();
        listOfSubPage = ((Page) pageTree.getData()).getSubPages();
        if (listOfSubPage != null) {
            for (Page subPage : listOfSubPage) {
                subPageTree = new CheckboxTreeNode("page", subPage, pageTree);
                subPageTree.setExpanded(true);
                if (authorize.getListOfPages().contains(subPage.getId())) {
                    subPageTree.setSelected(true);
                }
                findTabs(subPageTree);
                findButtons(subPageTree);
                addChild(subPageTree);
                if (authorize.isIsAdmin()) {//Admin yetki grubundan yetki kaldırılmasın diye
                    if (subPage.getId() == 16 || subPage.getId() == 175) {
                        subPageTree.setSelectable(false);
                    }
                }
            }
        }
    }

    /**
     * Oluşan page ve subPagelere göre tabların tree ye eklendiği fonksiyondur.
     *
     * @param pageTree parent olarak childrenlarını bulmak için gönderilir.
     *
     */
    public void findTabs(TreeNode pageTree) {
        TreeNode tabTree = new CheckboxTreeNode();
        List<Tab> listOfTabs = new ArrayList();
        listOfTabs = ((Page) pageTree.getData()).getTabs();
        if (listOfTabs != null) {
            for (Tab tab : listOfTabs) {
                tabTree = new CheckboxTreeNode("tab", tab, pageTree);
                tabTree.setExpanded(true);
                if (authorize.getListOfTabs().contains(tab.getId())) {
                    tabTree.setSelected(true);
                }
                findButtonsForTabs(tabTree);
                addChild(tabTree);
                if (authorize.isIsAdmin()) {//Admin yetki grubundan yetki kaldırılmasın diye
                    if (tab.getId() == 51 || tab.getId() == 52) {
                        tabTree.setSelectable(false);
                    }
                }
            }
        }
    }

    /**
     * Oluşan page ve subPagelere göre buttonların tree ye eklendiği
     * fonksiyondur.
     *
     * @param page parent olarak childrenlarını bulmak için gönderilir.
     */
    public void findButtons(TreeNode page) {
        TreeNode buttonTree = new CheckboxTreeNode();
        List<Button> listOfButtons = new ArrayList();
        listOfButtons = ((Page) page.getData()).getButtons();
        if (listOfButtons != null) {
            for (Button button : listOfButtons) {
                buttonTree = new CheckboxTreeNode("button", button, page);
                if (authorize.getListOfButtons().contains(button.getId())) {
                    buttonTree.setSelected(true);
                }
                if (authorize.isIsAdmin()) {//Admin yetki grubundan yetki kaldırılmasın diye
                    if (button.getId() == 180 || button.getId() == 181 || button.getId() == 182 || button.getId() == 184 || button.getId() == 185) {
                        buttonTree.setSelectable(false);
                    }
                }

            }
        }

    }

    /**
     * Oluşan tablara göre buttonların tree ye eklendiği fonksiyondur.
     *
     * @param tab parent olarak childrenlarını bulmak için gönderilir.
     *
     */
    public void findButtonsForTabs(TreeNode tab) {
        TreeNode buttonTree = new CheckboxTreeNode();
        List<Button> listOfButtons = new ArrayList();
        listOfButtons = ((Tab) tab.getData()).getListOfButtons();
        if (listOfButtons != null) {
            for (Button button : listOfButtons) {
                buttonTree = new CheckboxTreeNode("button", button, tab);
                if (authorize.getListOfButtons().contains(button.getId())) {
                    buttonTree.setSelected(true);
                }

                if (authorize.isIsAdmin()) {//Admin yetki grubundan yetki kaldırılmasın diye
                    if (button.getId() == 183 || button.getId() == 267) {
                        buttonTree.setSelectable(false);
                    }
                }
            }
        }
    }

    /**
     * Treenin hiçbir childrenı eklenmediğinde parentın otomatik olarak
     * yetkisini görsel olarak kaldırmasını önlemek için, tüm parentlara
     * görünmeyen bir hayalet çocuk eklenmesini sağlayan fonksiyondur.
     *
     * @param node parent node olarak children eklenmek üzere fonksiyona
     * gönderilir.
     */
    public void addChild(TreeNode node) {

        if (node.getChildren().size() != 0) {
            TreeNode childNodeTree = new CheckboxTreeNode();
            boolean isAvailable = false;
            childNodeTree = new CheckboxTreeNode("childNode", new Object(), node);
            List<TreeNode> children = node.getChildren();
            if (node.getType() == "folder") {
                if (authorize.getListOfFolders().contains(((Folder) node.getData()).getId())) {
                    isAvailable = true;
                }
            } else if (node.getType() == "page") {
                if (authorize.getListOfPages().contains(((Page) node.getData()).getId())) {
                    isAvailable = true;
                }
            } else if (node.getType() == "tab") {
                if (authorize.getListOfTabs().contains(((Tab) node.getData()).getId())) {
                    isAvailable = true;
                }
            } else if (node.getType() == "button") {
                if (authorize.getListOfButtons().contains(((Button) node.getData()).getId())) {
                    isAvailable = true;
                }
            }
            if (isAvailable) {
                childNodeTree.setSelected(true);
            } else {
                childNodeTree.setSelected(false);
            }

        }
    }

    /**
     * Herhangi bir node eklendiğinde, parentının yetkisi var mı diye kontrol
     * edebilmek için seçilen node'un tüm parentlarını bulan fonksiyondur.
     *
     * @param node seçilen node'u temsil eder.
     */
    public void findParent(TreeNode node) {
        if (node != null) {

            controlList.add(node);
            findParent(node.getParent());

        }
    }

    /**
     * Seçilen node'un parentları bulunduktan sonra, o parentın yetkisinin olup
     * olmadığını kontrol eden fonksiyondur.
     *
     * @param node seçilen node'u;
     * @param parentNode seçilen node'un parentını temsil eder.
     */
    public void controlAuthorizationForParent(TreeNode node, TreeNode parentNode) {

        if (parentNode.getType() == "page") {
            if (!lPages.contains(((Page) parentNode.getData()).getId())) {
                findChildrenForAuthorizationNodeDelete(parentNode);
            }
        } else if (parentNode.getType() == "folder") {
            if (!lFolders.contains(((Folder) parentNode.getData()).getId())) {
                findChildrenForAuthorizationNodeDelete(parentNode);
            }
        } else if (parentNode.getType() == "tab") {
            if (!lTabs.contains(((Tab) parentNode.getData()).getId())) {
                findChildrenForAuthorizationNodeDelete(parentNode);
            }
        } else if (parentNode.getType() == "button") {
            if (!lButtons.contains(((Button) parentNode.getData()).getId())) {
                findChildrenForAuthorizationNodeDelete(parentNode);
            }
        }
    }

    /**
     * controlAuthorizationForParent fonksiyonuyla kontrol edilen ve yetkisi
     * olmayan parentların childrenlarının tüm yetkilerinin silindiği
     * fonksiyondur.
     *
     * @param parentNode seçilen node'un parentıdır.
     */
    public void findChildrenForAuthorizationNodeDelete(TreeNode parentNode) {
        List<TreeNode> children = new ArrayList<>();
        children = parentNode.getChildren();
        for (TreeNode n : children) {
            if (n.getType() == "page") {
                if (lPages.contains(((Page) n.getData()).getId())) {
                    lPages.remove((Integer) ((Page) n.getData()).getId());
                }
            } else if (n.getType() == "folder") {
                if (lFolders.contains(((Folder) n.getData()).getId())) {
                    lFolders.remove((Integer) ((Folder) n.getData()).getId());
                }
            } else if (n.getType() == "tab") {
                if (lTabs.contains(((Tab) n.getData()).getId())) {
                    lTabs.remove((Integer) ((Tab) n.getData()).getId());
                }
            } else if (n.getType() == "button") {
                if (lButtons.contains(((Button) n.getData()).getId())) {
                    lButtons.remove((Integer) ((Button) n.getData()).getId());
                }
            }
            findChildrenUnSelectNode(n);
        }

    }

    /**
     * Tree'deki select eventi ile seçilen nodeları listeye ekleyen
     * fonksiyondur..
     *
     * @param event select ajax eventindeki seçilen node'dur.
     */
    public void onNodeSelect(NodeSelectEvent event) {
        List<TreeNode> children;

        if ("page".equals(event.getTreeNode().getType())) {
            if (!lPages.contains(((Page) event.getTreeNode().getData()).getId())) {
                lPages.add(((Page) event.getTreeNode().getData()).getId());
                event.getTreeNode().setSelectable(true);
                children = event.getTreeNode().getChildren();
                addSelectedNodeToList(children);

            }
        } else if ("folder".equals(event.getTreeNode().getType())) {
            if (!lFolders.contains(((Folder) event.getTreeNode().getData()).getId())) {
                event.getTreeNode().setSelectable(true);
                lFolders.add(((Folder) event.getTreeNode().getData()).getId());
                children = event.getTreeNode().getChildren();
                addSelectedNodeToList(children);
            }
        } else if ("tab".equals(event.getTreeNode().getType())) {
            if (!lTabs.contains(((Tab) event.getTreeNode().getData()).getId())) {
                event.getTreeNode().setSelectable(true);
                lTabs.add(((Tab) event.getTreeNode().getData()).getId());
                children = event.getTreeNode().getChildren();
                addSelectedNodeToList(children);
            }
        } else if ("button".equals(event.getTreeNode().getType())) {
            if (!lButtons.contains(((Button) event.getTreeNode().getData()).getId())) {
                event.getTreeNode().setSelectable(true);
                lButtons.add(((Button) event.getTreeNode().getData()).getId());
                children = event.getTreeNode().getChildren();
                addSelectedNodeToList(children);
            }
        }

        if (event.getTreeNode().getParent() != null) {
            findParent(event.getTreeNode().getParent());
            controlList.remove(controlList.size() - 1);
            for (int i = controlList.size() - 1; i >= 0; i--) {
                controlAuthorizationForParent(event.getTreeNode(), controlList.get(i));
            }

        }

    }

    /**
     * Seçilen node'un tüm çocuklarını bularak, listeye ekleyen fonksiyondur.
     *
     * @param treeNode seçilen node'dur.
     */
    public void findChildrenSelectNode(TreeNode treeNode) {
        List<TreeNode> children = new ArrayList<>();
        children = treeNode.getChildren();
        if (!children.isEmpty()) {
            for (TreeNode node : children) {

                if (node.getType() == "page") {
                    if (!lPages.contains(((Page) node.getData()).getId())) {
                        lPages.add(((Page) node.getData()).getId());
                    }
                } else if (node.getType() == "folder") {
                    if (!lFolders.contains(((Folder) node.getData()).getId())) {
                        lFolders.add(((Folder) node.getData()).getId());
                    }
                } else if (node.getType() == "tab") {
                    if (!lTabs.contains(((Tab) node.getData()).getId())) {
                        lTabs.add(((Tab) node.getData()).getId());
                    }
                } else if (node.getType() == "button") {
                    if (!lButtons.contains(((Button) node.getData()).getId())) {
                        lButtons.add(((Button) node.getData()).getId());
                    }
                }

                findChildrenSelectNode(node);
            }
        }
    }

    /**
     * onNodeSelect fonksiyonuyla seçilen node'un tipini belirleyerek uygun olan
     * listeye eklenmesini sağlayan fonksiyondur.
     *
     * @param children seçilen node'dur.
     */
    public void addSelectedNodeToList(List<TreeNode> children) {
        for (TreeNode node : children) {
            if (node.getType() == "page") {

                if (!lPages.contains(((Page) node.getData()).getId())) {
                    lPages.add(((Page) node.getData()).getId());
                }
            } else if (node.getType() == "folder") {

                if (!lFolders.contains(((Folder) node.getData()).getId())) {
                    lFolders.add(((Folder) node.getData()).getId());
                }
            } else if (node.getType() == "tab") {

                if (!lTabs.contains(((Tab) node.getData()).getId())) {
                    lTabs.add(((Tab) node.getData()).getId());
                }
            } else if (node.getType() == "button") {

                if (!lButtons.contains(((Button) node.getData()).getId())) {
                    lButtons.add(((Button) node.getData()).getId());
                }
            }
            findChildrenSelectNode(node);
        }
    }

    /**
     * Tree'deki unselect eventi ile unselect yapılan nodeları listeden silen
     * fonksiyondur.
     *
     * @param event unselect ajax eventindeki seçilen node'dur.
     */
    public void onNodeUnSelect(NodeUnselectEvent event) {
        List<TreeNode> children = new ArrayList<>();
        if (event.getTreeNode().getType() == "page") {

            if (lPages.contains(((Page) event.getTreeNode().getData()).getId())) {

                lPages.remove((Integer) ((Page) event.getTreeNode().getData()).getId());
                children = event.getTreeNode().getChildren();
                removeSelectedNodeFromList(children);
            }
        } else if (event.getTreeNode().getType() == "folder") {

            if (lFolders.contains(((Folder) event.getTreeNode().getData()).getId())) {
                lFolders.remove((Integer) ((Folder) event.getTreeNode().getData()).getId());
                children = event.getTreeNode().getChildren();
                removeSelectedNodeFromList(children);
            }

        } else if (event.getTreeNode().getType() == "tab") {

            if (lTabs.contains(((Tab) event.getTreeNode().getData()).getId())) {
                lTabs.remove((Integer) ((Tab) event.getTreeNode().getData()).getId());
                children = event.getTreeNode().getChildren();
                removeSelectedNodeFromList(children);
            }
        } else if (event.getTreeNode().getType() == "button") {

            if (lButtons.contains(((Button) event.getTreeNode().getData()).getId())) {
                lButtons.remove((Integer) ((Button) event.getTreeNode().getData()).getId());
                children = event.getTreeNode().getChildren();
                removeSelectedNodeFromList(children);
            }
        }

    }

    /**
     * Seçilen node'un tüm çocuklarını bularak, listeden silen fonksiyondur.
     *
     * @param treeNode seçilen node'dur.
     */
    public void findChildrenUnSelectNode(TreeNode treeNode) {
        List<TreeNode> children = new ArrayList<>();
        children = treeNode.getChildren();
        if (!children.isEmpty()) {
            for (TreeNode node : children) {

                if (node.getType() == "page") {
                    if (lPages.contains(((Page) node.getData()).getId())) {
                        lPages.remove((Integer) ((Page) node.getData()).getId());
                    }
                } else if (node.getType() == "folder") {
                    if (lFolders.contains(((Folder) node.getData()).getId())) {
                        lFolders.remove((Integer) ((Folder) node.getData()).getId());
                    }
                } else if (node.getType() == "tab") {
                    if (lTabs.contains(((Tab) node.getData()).getId())) {
                        lTabs.remove((Integer) ((Tab) node.getData()).getId());
                    }
                } else if (node.getType() == "button") {
                    if (lButtons.contains(((Button) node.getData()).getId())) {
                        lButtons.remove((Integer) ((Button) node.getData()).getId());
                    }
                }

                findChildrenUnSelectNode(node);
            }
        }
    }

    /**
     * onNodeUnSelect fonksiyonuyla seçilen node'un tipini belirleyerek uygun
     * olan listeden silinmesini sağlayan fonksiyondur.
     *
     * @param children seçilen node^dur.
     */
    public void removeSelectedNodeFromList(List<TreeNode> children) {
        for (TreeNode node : children) {

            if (node.getType() == "page") {

                if (lPages.contains(((Page) node.getData()).getId())) {
                    lPages.remove((Integer) ((Page) node.getData()).getId());
                }
            } else if (node.getType() == "folder") {
                if (lFolders.contains(((Folder) node.getData()).getId())) {
                    lFolders.remove((Integer) ((Folder) node.getData()).getId());
                }

            } else if (node.getType() == "tab") {

                if (lTabs.contains(((Tab) node.getData()).getId())) {
                    lTabs.remove((Integer) ((Tab) node.getData()).getId());
                }
            } else if (node.getType() == "button") {
                if (lButtons.contains(((Button) node.getData()).getId())) {
                    lButtons.remove((Integer) ((Button) node.getData()).getId());
                }
            }
            findChildrenUnSelectNode(node);
        }
    }

    /**
     * Bulunan treedeki tüm nodelara yetki verip/ kaldırmayı sağlayan methoddur.
     *
     * @param treeType göre işlem,kart,rapor,tanım treelerine uygun işlem
     * yapmayı sağlar
     */
    public void selectAll(int treeType) {
        List<TreeNode> children = new ArrayList<>();
        if (treeType == 0) {
            if (isSelectAllProcess) {
                children = rootProcess.getChildren();
                for (TreeNode treeNode : children) {
                    treeNode.setSelected(true);

                }
                selectAllChildren(treeType);
            } else if (!isSelectAllProcess) {
                children = rootProcess.getChildren();
                for (TreeNode treeNode : children) {
                    treeNode.setSelected(false);

                }
                unSelectAllChildren(treeType);
            }
        } else if (treeType == 1) {
            if (isSelectAllCard) {
                children = rootCard.getChildren();
                for (TreeNode treeNode : children) {
                    treeNode.setSelected(true);

                }
                selectAllChildren(treeType);
            } else if (!isSelectAllCard) {
                children = rootCard.getChildren();
                for (TreeNode treeNode : children) {
                    if ("folder".equals(treeNode.getType())) {
                        if (((Folder) treeNode.getData()).getId() == 23) {
                            for (TreeNode t : treeNode.getChildren()) {
                                if ("page".equals(t.getType())) {
                                    if (((Page) t.getData()).getId() == 3) {
                                        if (t.isSelectable()) {
                                            t.setSelected(false);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (treeNode.isSelectable()) {
                        treeNode.setSelected(false);
                    }

                }
                unSelectAllChildren(treeType);
            }
        } else if (treeType == 2) {
            if (isSelectAllReport) {
                children = rootReport.getChildren();
                for (TreeNode treeNode : children) {
                    treeNode.setSelected(true);

                }
                selectAllChildren(treeType);
            } else if (!isSelectAllReport) {
                children = rootReport.getChildren();
                for (TreeNode treeNode : children) {
                    treeNode.setSelected(false);

                }
                unSelectAllChildren(treeType);
            }
        } else if (treeType == 3) {
            if (isSelectAllDefinition) {
                children = rootDefinition.getChildren();
                for (TreeNode treeNode : children) {
                    treeNode.setSelected(true);

                }
                selectAllChildren(treeType);

            } else if (!isSelectAllDefinition) {
                children = rootDefinition.getChildren();
                for (TreeNode treeNode : children) {
                    treeNode.setSelected(false);

                }
                unSelectAllChildren(treeType);
            }
        }

        isSelectAllProcess = false;
        isSelectAllCard = false;
        isSelectAllReport = false;
        isSelectAllDefinition = false;
    }

    /**
     * selectAll fonksiyonuyla seçilen tüm node'ların listeye eklenemsini
     * sağlayan fonksiyondur
     *
     * @param treeType selectAll eventinin çalıştığı tree tipini alan
     * parametredir.
     */
    public void selectAllChildren(int treeType) {

        for (Folder folder : module.getFolders()) {

            if (folder.getType() == treeType) {
                if (!lFolders.contains(folder.getId())) {
                    lFolders.add(folder.getId());
                }
                if (folder.getPages() != null) {
                    for (Page page : folder.getPages()) {
                        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()
                                  || (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && (page.getId() != 75 && page.getId() != 115))) {
                            if (sessionBean.getUser().getLastBranchSetting().getParoUrl() == null && page.getId() != 233
                                      || sessionBean.getUser().getLastBranchSetting().getParoUrl() != null) {

                                if (!lPages.contains(page.getId())) {
                                    lPages.add(page.getId());
                                }
                                if (page.getSubPages() != null) {
                                    for (Page p : page.getSubPages()) {
                                        if (!lPages.contains(p.getId())) {
                                            lPages.add(p.getId());
                                        }
                                        if (p.getTabs() != null) {
                                            for (Tab tab : p.getTabs()) {
                                                if (!lTabs.contains(tab.getId())) {
                                                    lTabs.add(tab.getId());
                                                }
                                                if (tab.getListOfButtons() != null) {
                                                    for (Button b : tab.getListOfButtons()) {
                                                        if (!lButtons.contains(b.getId())) {
                                                            lButtons.add(b.getId());
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                        if (p.getButtons() != null) {
                                            for (Button button : p.getButtons()) {
                                                if (!lButtons.contains(button.getId())) {
                                                    lButtons.add(button.getId());
                                                }
                                            }
                                        }

                                    }
                                }
                                if (page.getTabs() != null) {
                                    for (Tab t : page.getTabs()) {
                                        if (!lTabs.contains(t.getId())) {
                                            lTabs.add(t.getId());
                                        }
                                        if (t.getListOfButtons() != null) {
                                            for (Button b : t.getListOfButtons()) {
                                                if (!lButtons.contains(b.getId())) {
                                                    lButtons.add(b.getId());
                                                }
                                            }
                                        }

                                    }
                                }
                                if (page.getButtons() != null) {
                                    for (Button button : page.getButtons()) {
                                        if (!lButtons.contains(button.getId())) {
                                            lButtons.add(button.getId());
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * selectAll fonksiyonuyla seçilen tüm node'ların listeden silinmesini
     * sağlayan fonksiyondur.
     *
     * @param treeType selectAll eventinin çalıştığı tree tipini alan
     * parametredir.
     */
    public void unSelectAllChildren(int treeType) {

        for (Folder folder : module.getFolders()) {
            if (folder.getType() == treeType) {
                if (!((Integer) folder.getId() == 23 && authorize.isIsAdmin())) {
                    lFolders.remove((Integer) folder.getId());
                }

                if (folder.getPages() != null) {
                    for (Page page : folder.getPages()) {
                        if (!(page.getId() == 15 && authorize.isIsAdmin())) {
                            lPages.remove((Integer) page.getId());
                            if (page.getSubPages() != null) {
                                for (Page p : page.getSubPages()) {
                                    lPages.remove((Integer) p.getId());
                                    if (p.getTabs() != null) {
                                        for (Tab tab : p.getTabs()) {
                                            lTabs.remove((Integer) tab.getId());
                                            for (Button b : tab.getListOfButtons()) {
                                                lButtons.remove((Integer) b.getId());
                                            }
                                        }
                                    }
                                    if (p.getButtons() != null) {
                                        for (Button button : p.getButtons()) {
                                            lButtons.remove((Integer) button.getId());
                                        }
                                    }

                                }
                            }
                            if (page.getTabs() != null) {
                                for (Tab t : page.getTabs()) {
                                    lTabs.remove((Integer) t.getId());
                                    if (t.getListOfButtons() != null) {
                                        for (Button b : t.getListOfButtons()) {
                                            lButtons.remove((Integer) b.getId());
                                        }
                                    }

                                }
                            }

                            if (page.getButtons() != null) {
                                for (Button button : page.getButtons()) {
                                    lButtons.remove((Integer) button.getId());
                                }
                            }
                        }

                    }

                }

            }
        }
    }

    /**
     * Bu method button taglarını dile göre çekmemiz gerektiği için; button
     * tagını properties dosyasından set etmeye yarar.
     *
     */
    public String btnNameToPropertiesFile(String name) {
        return sessionBean.loc.getString(name);
    }

}
