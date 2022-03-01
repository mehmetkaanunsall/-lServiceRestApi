/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 04.01.2017 17:27:30
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.finance.bank.business.GFBankService;
import com.mepsan.marwiz.finance.bank.business.IBankService;
import com.mepsan.marwiz.general.model.finance.Bank;
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
public class BankBookFilterBean extends BookFilterBean<Bank> {

    @ManagedProperty(value = "#{bankService}")
    public IBankService bankService;

    @ManagedProperty(value = "#{gFBankService}")
    public GFBankService gFBankService;

    public void setBankService(IBankService bankService) {
        this.bankService = bankService;
    }

    public void setgFBankService(GFBankService gFBankService) {
        this.gFBankService = gFBankService;
    }

    @Override
    public void generalFilter(String type, List<Object> param) {

        if (autoCompleteValue == null) {
            refresh();
        } else {
            gFBankService.makeSearchForbook(autoCompleteValue, type, param);
            dataListLazyLoading = gFBankService.searchResult;
        }
    }

    @Override
    public LazyDataModel<Bank> callServiceLazyLoading(String where, List<Object> param, String type) {
        return new CentrowizLazyDataModel<Bank>() {

            @Override
            public List<Bank> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                String whereSatment = " ";
                dataList = bankService.bankBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, whereSatment, type, param);
                int count = bankService.bankBookCount(whereSatment, type, param);
                dataListLazyLoading.setRowCount(count);
                return dataList;
            }
        };
    }

    @Override
    public List<Bank> callService(List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
