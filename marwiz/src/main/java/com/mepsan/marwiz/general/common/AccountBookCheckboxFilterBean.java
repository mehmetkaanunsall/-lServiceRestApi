/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   04.10.2018 12:08:05
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.account.business.GFAccountService;
import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class AccountBookCheckboxFilterBean extends BookFilterBean<Account> {

    List<Account> tempSelectedDataList, tempUnselectedDataList;
    String selectedCount;

    @ManagedProperty(value = "#{accountService}")
    private IAccountService accountService;

    @ManagedProperty(value = "#{gfAccountService}")
    private GFAccountService gfAccountService;

    public String getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(String selectedCount) {
        this.selectedCount = selectedCount;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public void setGfAccountService(GFAccountService gfAccountService) {
        this.gfAccountService = gfAccountService;
    }

    public List<Account> getTempSelectedDataList() {
        return tempSelectedDataList;
    }

    public void setTempSelectedDataList(List<Account> tempSelectedDataList) {
        this.tempSelectedDataList = tempSelectedDataList;
    }

    public List<Account> getTempUnselectedDataList() {
        return tempUnselectedDataList;
    }

    public void setTempUnselectedDataList(List<Account> tempUnselectedDataList) {
        this.tempUnselectedDataList = tempUnselectedDataList;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------AccountBookCheckboxFilterBean--------------------");
        tempSelectedDataList = new ArrayList<>();
        tempUnselectedDataList = new ArrayList<>();
        selectedDataList = new ArrayList<>();
        dataList = new ArrayList<>();
    }

    @Override
    public List<Account> callService(List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<Account> callServiceLazyLoading(String where, List<Object> param, String type) {
        return new CentrowizLazyDataModel<Account>() {

            @Override
            public List<Account> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                String whereSatment = " ";
                dataList = accountService.accountBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, whereSatment, type, param);
                int count = accountService.accountBookCount(whereSatment, type, param);
                dataListLazyLoading.setRowCount(count);
                selectedDataList.clear();
                selectedDataList.addAll(tempSelectedDataList);
                return dataList;

            }
        };
    }

    @Override
    public void generalFilter(String type, List<Object> param) {
        if (autoCompleteValue == null) {
            refresh();
        } else {
            gfAccountService.makeSearchForbook(autoCompleteValue, type, param);
            dataListLazyLoading = gfAccountService.searchResult;
        }
    }

    public void changeSelected() {
        List<Account> temp = new ArrayList();
        if (autoCompleteValue == null || "".equals(autoCompleteValue)) {

            for (Account stock : dataList) {
                for (Iterator<Account> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    Account next = iterator.next();
                    if (next.getId() == stock.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        } else {

            for (Account stock : gfAccountService.searchDataList) {
                for (Iterator<Account> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    Account next = iterator.next();
                    if (next.getId() == stock.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        }

        tempSelectedDataList.addAll(selectedDataList);
        for (Account stock : temp) {
            if (!tempSelectedDataList.contains(stock)) {
                tempUnselectedDataList.add(stock);
            }
        }
        for (Account u : tempSelectedDataList) {
            if (tempUnselectedDataList.contains(u)) {
                tempUnselectedDataList.remove(u);
            }
        }
    }

    public void reset() {

        tempSelectedDataList = new ArrayList<>();
        tempUnselectedDataList = new ArrayList<>();
    }

    public void clearSelected() {
        tempSelectedDataList.clear();
        selectedDataList.clear();
        RequestContext.getCurrentInstance().update(getUpdate() + ":dtbFilter");
    }

}
