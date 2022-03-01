package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.marketshift.business.GFMarketShiftService;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author elif.mart
 */
@ManagedBean
@ViewScoped
public class ShiftBookFilterBean extends BookFilterBean<Shift> {

    @ManagedProperty(value = "#{gfMarketShiftService}")
    private GFMarketShiftService gfMarketShiftService;

    @ManagedProperty(value = "#{marketShiftService}")
    private IMarketShiftService marketShiftService;

    public void setGfMarketShiftService(GFMarketShiftService gfMarketShiftService) {
        this.gfMarketShiftService = gfMarketShiftService;
    }

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    @Override
    public List<Shift> callService(List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<Shift> callServiceLazyLoading(String where, List<Object> param, String type) {
        return new CentrowizLazyDataModel<Shift>() {
            @Override
            public List<Shift> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                int count = 0;
                String whereSatment = " ";
                dataList = marketShiftService.shiftBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, whereSatment, type, param);
                count = marketShiftService.shiftBookCount(whereSatment, type, param);
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
            gfMarketShiftService.makeSearchForbook(autoCompleteValue, type, param);
            dataListLazyLoading = gfMarketShiftService.searchResult;
        }
    }

}
