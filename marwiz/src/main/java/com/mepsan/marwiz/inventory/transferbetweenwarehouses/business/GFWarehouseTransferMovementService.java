package com.mepsan.marwiz.inventory.transferbetweenwarehouses.business;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.presentation.TransferBetweenWarehouseProcessBean;
import com.mepsan.marwiz.inventory.warehousereceipt.business.IWarehouseMovementService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sinem.arslan
 */
public class GFWarehouseTransferMovementService extends GeneralFilterService<WarehouseMovement> {

    @Autowired
    private IWarehouseMovementService warehouseMovementService;

    public IWarehouseMovementService getWarehouseMovementService() {
        return warehouseMovementService;
    }

    public void setWarehouseMovementService(IWarehouseMovementService warehouseMovementService) {
        this.warehouseMovementService = warehouseMovementService;
    }

    public String createWhere(String value, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer selectedWarehouseTransfer) {
        value = value.replace("'", "");
        String where = " and (";

        where = " " + where + "stck.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.code" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.centerproductcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "( SELECT \n"
                + "   iwig.quantity \n"
                + "   FROM inventory.warehouseitem iwig\n"
                + "      WHERE iwig.stock_id=stck.id AND iwig.warehouse_id= " + entryWarehouse.getId() + "  AND iwig.deleted=FALSE\n"
                + ")" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "( SELECT \n"
                + "   iwic.quantity \n"
                + "   FROM inventory.warehouseitem iwic\n"
                + "      WHERE iwic.stock_id=stck.id AND iwic.warehouse_id= " + exitWarehouse.getId() + "  AND iwic.deleted=FALSE\n"
                + ")" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";

        if (type == 0) {
            where = where + "or " + "to_char(" + "0" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";

        } else if (type == 1) {
            where = where + "or " + "to_char(" + "whm.quantity" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        }
        where = where + ")";
        return where;
    }

    public void makeSearch(String value, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer selectedWarehouseTransfer) {
        searchResult = new CentrowizLazyDataModel<WarehouseMovement>() {
            @Override
            public List<WarehouseMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<WarehouseMovement> result;
                String where = createWhere(value, exitWarehouse, entryWarehouse, type, selectedWarehouseTransfer);
                int count = callDaoCount(where, exitWarehouse, entryWarehouse, type, selectedWarehouseTransfer);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, exitWarehouse, entryWarehouse, type, selectedWarehouseTransfer);
                System.out.println("makeSearchResult" + result.size());
                searchResult.setRowCount(count);
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                TransferBetweenWarehouseProcessBean transferBetweenWarehouseProcessBean = (TransferBetweenWarehouseProcessBean) viewMap.get("transferBetweenWarehouseProcessBean");
                result = transferBetweenWarehouseProcessBean.changeQuantity(result);

                return result;

            }
        };
    }

    public List<WarehouseMovement> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer selectedWarehouseTransfer) {
        return warehouseMovementService.findAllAccordingToWarehouse(first, pageSize, sortField, sortOrder, filters, where, exitWarehouse, entryWarehouse, type, selectedWarehouseTransfer);

    }

    public int callDaoCount(String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer) {
        return warehouseMovementService.count(where, exitWarehouse, entryWarehouse, type, warehouseTransfer);
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
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<WarehouseMovement> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String createWhere(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
