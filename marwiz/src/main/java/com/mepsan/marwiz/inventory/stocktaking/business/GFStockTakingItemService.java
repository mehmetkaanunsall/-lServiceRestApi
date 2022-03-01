/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stocktaking.business;

import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import com.mepsan.marwiz.inventory.stocktaking.presentation.StockTakingStockTabBean;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author elif.mart
 */
public class GFStockTakingItemService extends GeneralFilterService<StockTakingItem> {

    @Autowired
    private StockTakingItemService stockTakingItemService;

    public StockTakingItemService getStockTakingItemService() {
        return stockTakingItemService;
    }

    public void setStockTakingItemService(StockTakingItemService stockTakingItemService) {
        this.stockTakingItemService = stockTakingItemService;
    }

    public String createWhere(String value, StockTaking obj) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "stck.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.code" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.centerproductcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sab.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "gunt.sortname" + " ilike '%" + value + "%'  ";

        if (obj.getStatus().getId() == 16) { //sistem miktarı
            where = where + "or " + "to_char(" + "sti.systemquantity" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        } else if (obj.getStatus().getId() == 15) {
            where = where + "or " + "to_char(" + "("
                    + "CASE WHEN st.is_retrospective = FALSE THEN iwi.quantity ELSE iwi.quantity+COALESCE((select st.quantity from stock_temp st where st.stock_id = stck.id limit 1),0) \n"
                    + "END \n"
                    + ") " + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";

        }
        where = where + "or " + "to_char(" + "sti.realquantity" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";//Girilen miktar

        if (obj.getStatus().getId() == 16) { // Fark
            where = where + "or " + "to_char(" + "(sti.realquantity-sti.systemquantity)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        } else if (obj.getStatus().getId() == 15) {
            where = where + "or " + "to_char(" + "(sti.realquantity - ("
                    + "CASE WHEN st.is_retrospective = FALSE THEN iwi.quantity ELSE iwi.quantity+COALESCE((select st.quantity from stock_temp st where st.stock_id = stck.id limit 1),0) \n"
                    + "END \n"
                    + ") )" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";

        }

        if (obj.getStatus().getId() == 16) { //Sistem Tutarı
            where = where + "or " + "to_char(" + "(sti.systemquantity * sti.currentpricelistprice) " + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        } else if (obj.getStatus().getId() == 15) {
            where = where + "or " + "to_char(" + "(CASE WHEN sti.id IS NULL THEN "
                    + "CASE WHEN st.is_retrospective = FALSE THEN iwi.quantity ELSE iwi.quantity+COALESCE((select st.quantity from stock_temp st where st.stock_id = stck.id limit 1),0) \n"
                    + "END \n"
                    + "ELSE sti.systemquantity END) * "
                    + "(CASE WHEN prli.id IS NULL THEN 0\n"
                    + "WHEN st.is_taxincluded = TRUE THEN \n"
                    + "     CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)*(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)*(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END\n"
                    + "ELSE\n"
                    + "     CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)/(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)/(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END  \n"
                    + "\n"
                    + "END \n"
                    + ")" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        }

        if (obj.getStatus().getId() == 16) { //Girilen tutar
            where = where + "or " + "to_char(" + "((CASE WHEN sti.realquantity IS NULL THEN 0 ELSE sti.realquantity END) * sti.currentpricelistprice) " + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        } else if (obj.getStatus().getId() == 15) {
            where = where + "or " + "to_char(" + "(CASE WHEN sti.realquantity IS NULL THEN 0 ELSE sti.realquantity END) * "
                    + "(CASE WHEN prli.id IS NULL THEN 0\n"
                    + "WHEN st.is_taxincluded = TRUE THEN \n"
                    + "     CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)*(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)*(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END\n"
                    + "ELSE\n"
                    + "     CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)/(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)/(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END  \n"
                    + "\n"
                    + "END \n"
                    + ")" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        }

        if (obj.getStatus().getId() == 16) { //Fark Tutarı
            where = where + "or " + "to_char(" + "((sti.realquantity-sti.systemquantity) * sti.currentpricelistprice) " + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        } else if (obj.getStatus().getId() == 15) {
            where = where + "or " + "to_char(" + "(sti.realquantity - ("
                    + "CASE WHEN st.is_retrospective = FALSE THEN iwi.quantity ELSE iwi.quantity+COALESCE((select st.quantity from stock_temp st where st.stock_id = stck.id limit 1),0) \n"
                    + "END \n"
                    + ") ) * "
                    + "(CASE WHEN prli.id IS NULL THEN 0\n"
                    + "WHEN st.is_taxincluded = TRUE THEN \n"
                    + "     CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)*(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)*(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END\n"
                    + "ELSE\n"
                    + "     CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)\n"
                    + "     ELSE CASE WHEN prl.is_purchase=TRUE THEN COALESCE(prli.price,0)/(1+(COALESCE(ptg.rate,0)/100)) ELSE COALESCE(prli.price,0)/(1+(COALESCE(stg.rate,0)/100)) END\n"
                    + "     END  \n"
                    + "\n"
                    + "END \n"
                    + ")" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        }

        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void makeSearch(String value, String where, StockTaking obj) {
        searchResult = new CentrowizLazyDataModel<StockTakingItem>() {

            @Override
            public List<StockTakingItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                String where1 = createWhere(value, obj);
                where1 = where1 + where;

                List<StockTakingItem> result = stockTakingItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1, obj);
                int count = stockTakingItemService.count(where1, obj);
                searchResult.setRowCount(count);
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                StockTakingStockTabBean stockTakingStockTabBean = (StockTakingStockTabBean) viewMap.get("stockTakingStockTabBean");
                result = stockTakingStockTabBean.changeIsOpenUpdate(result);
                result = stockTakingStockTabBean.changeAmountentered(result);

                return result;
            }
        };
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StockTakingItem> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createWhere(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
