/**
 * This class ...
 *
 *
 * @author Ali Kurt
 *
 * @date   13.01.2018 01:26:15
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.account.business.GFAccountService;
import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class AccountBookFilterBean extends BookFilterBean<Account> {

    @ManagedProperty(value = "#{accountService}")
    private IAccountService accountService;
    
    @ManagedProperty(value = "#{gfAccountService}")
    private GFAccountService gfAccountService;

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public void setGfAccountService(GFAccountService gfAccountService) {
        this.gfAccountService = gfAccountService;
    }

    @Override
    public LazyDataModel<Account> callServiceLazyLoading(String where, List<Object> param, String type) {

        return new CentrowizLazyDataModel<Account>() {

            @Override
            public List<Account> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                
                String whereSatment = " ";
                int count = 0;
                dataList = accountService.accountBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, whereSatment, type, param);
                count = accountService.accountBookCount(whereSatment, type, param);
                dataListLazyLoading.setRowCount(count);
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

    @Override
    public List<Account> callService(List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
    
}
