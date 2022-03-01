/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.06.2020 05:58:46
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.business;

import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import com.mepsan.marwiz.inventory.centralpriceprocesses.dao.CentralPriceProcess;
import com.mepsan.marwiz.inventory.centralpriceprocesses.presentation.CentralPriceProcessesBean;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFCentralPriceProcessService extends GeneralFilterService<CentralPriceProcess> {

    @Autowired
    private ICentralPriceProcessService centralPriceProcessService;

    public void setCentralPriceProcessService(ICentralPriceProcessService centralPriceProcessService) {
        this.centralPriceProcessService = centralPriceProcessService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "stck.barcode" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.name" + " ilike '%" + value + "%' ";
        where = where + ")";
        return where;
    }

    public void makeSearch(String value, String where, int branchStock, String branchID) {
        searchResult = new CentrowizLazyDataModel<CentralPriceProcess>() {
            @Override
            public List<CentralPriceProcess> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<CentralPriceProcess> result;
                String where1 = createWhere(value);
                switch (branchStock) {
                    case 1://merkezi
                        where1 = where1 + where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                                  + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                                  + "AND  si1.is_valid  =TRUE)\n";
                        break;
                    case 2://merkezi olmayan
                        where1 = where1 + where + " AND stck.is_otherbranch = TRUE ";
                        break;
                    default://hepsi
                        where1 = where1 + where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                                  + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                                  + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                                  + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE ELSE stck.is_otherbranch = TRUE END)) \n";
                        break;
                }
                int count = callDaoCount(where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                CentralPriceProcessesBean centralPriceProcessesBean = (CentralPriceProcessesBean) viewMap.get("centralPriceProcessesBean");

                for (CentralPriceProcess cp : result) {
                    for (CentralPriceProcess cpsel : centralPriceProcessesBean.getSelectedCentralPriceList()) {
                        if (cp.getPriceListItem().getStock().getId() == cpsel.getPriceListItem().getStock().getId()) {
                            cp.getPriceListItem().setPrice(cpsel.getPriceListItem().getPrice());
                            cp.getPriceListItem().getCurrency().setId(cpsel.getPriceListItem().getCurrency().getId());
                            cp.getPriceListItem().getCurrency().setTag(cpsel.getPriceListItem().getCurrency().getTag());
                            cp.getPriceListItem().setIs_taxIncluded(cpsel.getPriceListItem().isIs_taxIncluded());
                        }
                    }
                }
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CentralPriceProcess> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return centralPriceProcessService.findAll(first, pageSize, sortField, sortOrder, filters, where, 0,"");
    }

    @Override
    public int callDaoCount(String where) {
        return centralPriceProcessService.count(where, 0,"");
    }

}
