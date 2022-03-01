/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 14:24:04
 */
package com.mepsan.marwiz.finance.discount.presentation;

import com.mepsan.marwiz.finance.discount.business.GFDiscountService;
import com.mepsan.marwiz.finance.discount.business.IDiscountService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class DiscountBean extends GeneralBean<Discount> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{discountService}")
    private IDiscountService discountService;

    @ManagedProperty(value = "#{gfDiscountService}")
    private GFDiscountService gfDiscountService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setDiscountService(IDiscountService discountService) {
        this.discountService = discountService;
    }

    public void setGfDiscountService(GFDiscountService gfDiscountService) {
        this.gfDiscountService = gfDiscountService;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfObjects = findall(" ");
        toogleList = createToggleList(sessionBean.getUser());
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true,true, true, true, true, true);
        }

        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, false, true);
        }
        
        setListBtn(sessionBean.checkAuthority(new int[]{95}, 0));
    }

    @Override
    public LazyDataModel<Discount> findall(String where) {
        return new CentrowizLazyDataModel<Discount>() {
            @Override
            public List<Discount> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<Discount> result = null;
                int count = 0;
                result = discountService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                count = discountService.count(where);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void create() {
        Object object = new Object();
        marwiz.goToPage("/pages/finance/discount/discountprocess.xhtml", object, 0, 127);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void update() {

        marwiz.goToPage("/pages/finance/discount/discountprocess.xhtml", selectedObject, 0, 127);

    }

    @Override
    public void generalFilter() {
        String where = "";
        if (autoCompleteValue == null) {
            listOfObjects = findall(where);
        } else {
            gfDiscountService.makeSearch(autoCompleteValue);
            listOfObjects = gfDiscountService.searchResult;
        }
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
