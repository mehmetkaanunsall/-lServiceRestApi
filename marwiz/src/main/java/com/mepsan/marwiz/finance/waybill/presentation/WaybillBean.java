/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 25.01.2018 11:28:51
 */
package com.mepsan.marwiz.finance.waybill.presentation;

import com.mepsan.marwiz.finance.waybill.business.GFWaybillService;
import com.mepsan.marwiz.finance.waybill.business.IWaybillService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Waybill;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class WaybillBean extends GeneralBean<Waybill> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{waybillService}")
    public IWaybillService waybillService;

    @ManagedProperty(value = "#{gfWaybillService}")
    public GFWaybillService gfWaybillService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    public class WaybillParam {

        private List<Branch> selectedBranchList;
        private int type;//satış,satınalma hepsi

        public WaybillParam() {
            this.selectedBranchList = new ArrayList();
        }

        public List<Branch> getSelectedBranchList() {
            return selectedBranchList;
        }

        public void setSelectedBranchList(List<Branch> selectedBranchList) {
            this.selectedBranchList = selectedBranchList;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }

    private WaybillParam searchObject;

    private List<Branch> listOfBranch;
    String createWhere;

    public WaybillParam getSearchObject() {
        return searchObject;
    }

    public void setSearchObject(WaybillParam searchObject) {
        this.searchObject = searchObject;
    }

    public void setWaybillService(IWaybillService waybillService) {
        this.waybillService = waybillService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setGfWaybillService(GFWaybillService gfWaybillService) {
        this.gfWaybillService = gfWaybillService;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public List<Branch> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<Branch> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    @Override
    @PostConstruct
    public void init() {

        searchObject = new WaybillParam();
        searchObject.setType(2);
        listOfBranch = new ArrayList<>();

        listOfBranch = branchService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        toogleList = createToggleList(sessionBean.getUser());
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);
        }

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof WaybillParam) {
                    searchObject = (WaybillParam) (((ArrayList) sessionBean.parameter).get(i));
                    
                    List<Branch> temp = new ArrayList<>();
                    temp.addAll(searchObject.getSelectedBranchList());
                    searchObject.getSelectedBranchList().clear();
                    for (Branch br : listOfBranch) {
                        for (Branch sbr : temp) {
                            if (br.getId() == sbr.getId()) {
                                searchObject.getSelectedBranchList().add(br);
                            }
                        }
                    }
                    break;
                }
            }
        } else {
            for (Branch br : listOfBranch) {
                if (br.getId() == sessionBean.getUser().getLastBranch().getId()) {
                    searchObject.getSelectedBranchList().add(br);
                    break;
                }
            }
        }

        find();
        setListBtn(sessionBean.checkAuthority(new int[]{11}, 0));
    }

    @Override
    public void create() {
        List<Object> list = new ArrayList<>();
        Waybill waybill = new Waybill();
        waybill.setIsPurchase(searchObject.getType() == 1);
        list.add(searchObject);
        list.add(waybill);
        marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 0, 41);

    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        String where = "";

        switch (searchObject.getType()) {
            case 0://şatış faturaları
                where = " AND wb.is_purchase=false ";
                break;
            case 1://satın alma faturaları
                where = " AND wb.is_purchase=true ";
                break;
            case 2://hepsi
                where = " ";
                break;
            default:
                break;
        }
        where = where + createWhere;

        if (autoCompleteValue == null) {
            listOfObjects = findall(where);
        } else {
            gfWaybillService.makeSearch(autoCompleteValue, where);
            listOfObjects = gfWaybillService.searchResult;
        }
    }

    @Override
    public LazyDataModel<Waybill> findall(String where) {
        return new CentrowizLazyDataModel<Waybill>() {
            @Override
            public List<Waybill> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {

                String where1 = "";
                switch (searchObject.getType()) {
                    case 0://şatış faturaları
                        where1 = where + " AND wb.is_purchase=false ";
                        break;
                    case 1://satın alma faturaları
                        where1 = where + " AND wb.is_purchase=true ";
                        break;
                    case 2://hepsi
                        where1 = where + " ";
                        break;
                    default:
                        break;
                }

                List<Waybill> result = waybillService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                int count = waybillService.count(where1);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void find() {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmWaybill:dtbWaybill");
        dataTable.setFirst(0);

        createWhere = waybillService.createWhere(searchObject, listOfBranch);
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update("frmWaybill:dtbWaybill");
    }

    public void goToPage() {

        List<Object> list = new ArrayList<>();
        list.add(searchObject);
        list.add(selectedObject);
        marwiz.goToPage("/pages/finance/waybill/waybillprocess.xhtml", list, 0, 41);
    }
}
