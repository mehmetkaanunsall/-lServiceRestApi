/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.08.2018 03:46:48
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.system.userdata.business.IUserDataService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

@ManagedBean
@ViewScoped
public class UserBookCheckboxFilterBean extends BookFilterBean<UserData> {

    @ManagedProperty(value = "#{userDataService}")
    private IUserDataService userDataService;

    List<UserData> tempSelectedDataList, tempUnselectedDataList;
    String selectedCount;

    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public List<UserData> getTempSelectedDataList() {
        return tempSelectedDataList;
    }

    public void setTempSelectedDataList(List<UserData> tempSelectedDataList) {
        this.tempSelectedDataList = tempSelectedDataList;
    }

    public List<UserData> getTempUnselectedDataList() {
        return tempUnselectedDataList;
    }

    public void setTempUnselectedDataList(List<UserData> tempUnselectedDataList) {
        this.tempUnselectedDataList = tempUnselectedDataList;
    }

    public String getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(String selectedCount) {
        this.selectedCount = selectedCount;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------UserBookCheckboxFilterBean--------------------");
        tempSelectedDataList = new ArrayList<>();
        tempUnselectedDataList = new ArrayList<>();
        selectedDataList = new ArrayList<>();
        dataList = new ArrayList<>();
    }

    @Override
    public List<UserData> callService(List<Object> param, String type) {
        dataList = userDataService.findAll();
        selectedDataList.clear();
        selectedDataList.addAll(tempSelectedDataList);
        return dataList;

    }

    public void changeSelected() {
        List<UserData> temp = new ArrayList();

        for (UserData user : dataList) {
            for (Iterator<UserData> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                UserData next = iterator.next();
                if (next.getId() == user.getId()) {
                    iterator.remove();
                    temp.add(next);
                    break;
                }
            }
        }

        tempSelectedDataList.addAll(selectedDataList);
        for (UserData stock : temp) {
            if (!tempSelectedDataList.contains(stock)) {
                tempUnselectedDataList.add(stock);
            }
        }
        for (UserData u : tempSelectedDataList) {
            if (tempUnselectedDataList.contains(u)) {
                tempUnselectedDataList.remove(u);
            }
        }
    }

    public void hideDialog() {

        selectedDataList.clear();
        tempSelectedDataList = new ArrayList<>();
        tempUnselectedDataList = new ArrayList<>();
    }

    @Override
    public LazyDataModel<UserData> callServiceLazyLoading(String where, List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter(String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void clearSelected() {
        tempSelectedDataList.clear();
        selectedDataList.clear();
        RequestContext.getCurrentInstance().update(getUpdate() + ":dtbFilter");
    }

}
