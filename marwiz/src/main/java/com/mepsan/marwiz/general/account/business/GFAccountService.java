/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   26.03.2018 09:38:48
 */

package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;


public class GFAccountService extends GeneralFilterService<Account> {

    @Autowired 
    private IAccountService accountService;

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }
    
    
    
    @Override
    public String createWhere(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createWhereForBook(String value) {
        value = value.replace("'", "");
        String where = "and (";
        
        where=" " + where + "acc.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "acc.taxno" + " ilike '%" + value + "%'  ";
         where = where + "or " + "acc.code" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        searchResult = new CentrowizLazyDataModel<Account>() {
            @Override
            public List<Account> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                
                int count = accountService.accountBookCount(createWhereForBook(value), type, param);
                searchDataList = accountService.accountBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhereForBook(value), type, param);
                searchResult.setRowCount(count);
                if (type.equals("reportcheckbox") || type.equals("accountextractreportcheckbox") || type.equals("employeeextractreportcheckbox") || type.equals("supplierCheckboxReport")
                          || type.equals("accountCategoryCheckBox") || type.equals("invoiceCheckBox") || type.equals("orderCheckBox") || type.equals("personelCategoryCheckBox") || type.equals("customeragreement") || type.equals("eInvoiceCheckBox")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    AccountBookCheckboxFilterBean accountBookCheckboxFilterBean = (AccountBookCheckboxFilterBean) viewMap.get("accountBookCheckboxFilterBean");
                    for (Account account : accountBookCheckboxFilterBean.getTempSelectedDataList()) {
                        if (!accountBookCheckboxFilterBean.getSelectedDataList().contains(account)) {
                            accountBookCheckboxFilterBean.getSelectedDataList().add(account);
                        }

                    }
                }
                return searchDataList;
            }
        };
    }

    @Override
    public List<Account> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
