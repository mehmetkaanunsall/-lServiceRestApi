/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   29.01.2018 03:44:42
 */
package com.mepsan.marwiz.inventory.warehousereceipt.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.unit.dao.UnitMapper;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WarehouseMovementDao extends JdbcDaoSupport implements IWarehouseMovementDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WarehouseMovement> findAll(WarehouseReceipt warehouseReceipt) {
        String sql = "select \n"
                + "whm.id as whmid,\n"
                + "whm.stock_id as whmstock_id,\n"
                + "stck.name as stckname,\n"
                + "stck.code as stckcode,\n"
                + "stck.centerproductcode AS stckcenterproductcode,\n"
                + "stck.barcode as stckbarcode,\n"
                + "gunt.id as guntid,\n"
                + "gunt.name as guntname,\n"
                + "gunt.sortname as guntsortname,\n"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "whm.quantity as whmquantity,\n"
                + "wsi.description as wsidescription,\n"
                + "wsi.expirationdate as wsiexpirationdate,\n"
                + "COALESCE(wsi.totalmoney, 0) AS wsitotalmoney,\n"
                + "wsi.wastereason_id as wastereasonid,\n"
                + "COALESCE(wsi.currentprice, 0) as wsicurrentprice,\n"
                + "COALESCE(wsi.currency_id, 0) as wsicurrency_id,\n"
                + "COALESCE(wsi.taxrate, 0) as wsitaxrate,\n"
                + "COALESCE(wsi.alternativeunitquantity,0) as wsialternativeunitquantity,\n"
                + "wsi.unit_id as wsiunit_id,\n"
                + "guntw.name as wguntname,\n"
                + "guntw.sortname as wguntsortname,\n"
                + "guntw.unitrounding as wguntunitrounding,\n"
                + "COALESCE(ptg.rate,0) as taxrate,\n"
                + "COALESCE(si.currentpurchaseprice,0) AS sicurrentpurchaseprice,\n"
                + "si.is_minusstocklevel as siis_minusstocklevel,\n"
                + "si.maxstocklevel as simaxstocklevel,\n"
                + "si.balance as sibalance,\n"
                + "COALESCE(whi.quantity,0) as availablequantity\n"
                + "from inventory.warehousemovement whm\n"
                + "inner join inventory.stock stck on (whm.stock_id=stck.id and stck.deleted=false)\n"
                + "left join general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                + "left join inventory.wasteiteminfo wsi ON(wsi.warehousemovement_id=whm.id and wsi.deleted=false)\n"
                + "left join general.unit guntw ON (guntw.id = wsi.unit_id and guntw.deleted=false)\n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id = " + sessionBean.getUser().getLastBranch().getId() + ")\n"
                + "left join inventory.warehouseitem whi ON (whi.stock_id=stck.id AND whi.deleted=FALSE AND whi.warehouse_id=?)\n"
                + "left join (SELECT \n"
                + "                              txg.rate AS rate,\n"
                + "                              stc.stock_id AS stock_id \n"
                + "                              FROM inventory.stock_taxgroup_con stc  \n"
                + "                              INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "                              WHERE stc.deleted = false\n"
                + "                              AND txg.type_id = 10 --kdv grubundan \n"
                + "                              AND stc.is_purchase = TRUE) ptg ON(ptg.stock_id = stck.id) \n"
                + "where whm.deleted=false and whm.warehousereceipt_id=? \n"
                + "Order By whm.stock_id";
        Object[] param = {warehouseReceipt.getWarehouse().getId(), warehouseReceipt.getId()};
        List<WarehouseMovement> result = getJdbcTemplate().query(sql, param, new WarehouseMovementMapper());

        return result;

    }

    @Override
    public int create(WarehouseReceipt obj) {
        String sql = " SELECT r_receiptitem_id FROM inventory.process_warehousereceiptitem(?, ?, ?, ?, ?, ? );";
        Object[] param = {0, obj.getId(), obj.getWarehouse().getId(), obj.isIsDirection(), sessionBean.getUser().getId(), obj.getJsonMovements() == null ? null : obj.getJsonMovements().equals("") ? null : obj.getJsonMovements()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WarehouseReceipt obj) {
        String sql = " SELECT r_receiptitem_id FROM inventory.process_warehousereceiptitem(?, ?, ?, ?, ?, ? );";
        Object[] param = {1, obj.getId(), obj.getWarehouse().getId(), obj.isIsDirection(), sessionBean.getUser().getId(), obj.getJsonMovements() == null ? null : obj.getJsonMovements().equals("") ? null : obj.getJsonMovements()};

        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<CheckDelete> testBeforeDelete(WarehouseReceipt warehouseReceipt) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {2, warehouseReceipt.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int delete(WarehouseMovement warehouseMovement) {
        String sql = " SELECT r_receiptitem_id FROM inventory.process_warehousereceiptitem(?, ?, ?, ?, ?, ? );";
        Object[] param = {2, warehouseMovement.getId(), warehouseMovement.getWarehouse().getId(), warehouseMovement.isIsDirection(), sessionBean.getUser().getId(), null};
        try {
            getJdbcTemplate().queryForObject(sql, param, Integer.class);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int createWasteInfo(WarehouseReceipt obj, WarehouseMovement warehouseMovement) {
        String sql = "INSERT INTO \n"
                + "inventory.wasteiteminfo \n"
                + "(wastereason_id,warehousemovement_id,expirationdate,alternativeunitquantity, unit_id,taxrate,currentprice,currency_id,exchangerate,totalmoney,description,c_id,u_id) \n"
                + "VALUES (?,(SELECT whm.id FROM inventory.warehousemovement whm WHERE whm.warehousereceipt_id=? AND whm.stock_id=? AND whm.deleted=FALSE),?,?,?,?,?,?,?,?,?,?,?) RETURNING id;";

        Object[] param = new Object[]{warehouseMovement.getWasteItemInfo().getWasteReason().getId() == 0 ? null : warehouseMovement.getWasteItemInfo().getWasteReason().getId(), obj.getId(), warehouseMovement.getStock().getId(), warehouseMovement.getWasteItemInfo().getExpirationDate(), warehouseMovement.getWasteItemInfo().getAlternativeUnitQuantity(), warehouseMovement.getWasteItemInfo().getUnit().getId(), warehouseMovement.getWasteItemInfo().getTaxRate(), warehouseMovement.getWasteItemInfo().getCurrentPurchasePrice(), warehouseMovement.getWasteItemInfo().getCurrency().getId() == 0 ? null : warehouseMovement.getWasteItemInfo().getCurrency().getId(), warehouseMovement.getWasteItemInfo().getExchangeRate(), warehouseMovement.getWasteItemInfo().getTotalMoney(), warehouseMovement.getWasteItemInfo().getDescription(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateWasteInfo(WarehouseMovement warehouseMovement) {

        String sql = "UPDATE inventory.wasteiteminfo SET wastereason_id = ?, warehousemovement_id= ?, expirationdate = ?, alternativeunitquantity = ?, unit_id = ?, taxrate = ?, currentprice = ?, currency_id = ?, exchangerate = ?, totalmoney = ?, description = ? , u_id = ?, u_time = now() WHERE warehousemovement_id= ? ";
        Object[] param = new Object[]{warehouseMovement.getWasteItemInfo().getWasteReason().getId() == 0 ? null : warehouseMovement.getWasteItemInfo().getWasteReason().getId(), warehouseMovement.getId(), warehouseMovement.getWasteItemInfo().getExpirationDate(), warehouseMovement.getWasteItemInfo().getAlternativeUnitQuantity(), warehouseMovement.getWasteItemInfo().getUnit().getId(), warehouseMovement.getWasteItemInfo().getTaxRate(), warehouseMovement.getWasteItemInfo().getCurrentPurchasePrice(), warehouseMovement.getWasteItemInfo().getCurrency().getId() == 0 ? null : warehouseMovement.getWasteItemInfo().getCurrency().getId(), warehouseMovement.getWasteItemInfo().getExchangeRate(), warehouseMovement.getWasteItemInfo().getTotalMoney(), warehouseMovement.getWasteItemInfo().getDescription(), sessionBean.getUser().getId(), warehouseMovement.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int deleteWasteInfo(WarehouseMovement warehouseMovement) {
        String sql = "UPDATE inventory.wasteiteminfo SET deleted=TRUE, u_id = ?, d_time = now() WHERE warehousemovement_id=? ";
        Object[] param = new Object[]{sessionBean.getUser().getId(), warehouseMovement.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    public List<Unit> findUnitStock(Stock stock) {

        String where = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {

            where = where + " AND suc.centerstock_id = FALSE ";

        } else {
            where = where + " AND suc.is_otherbranch = TRUE  ";
        }

        String sql = "SELECT \n"
                + "gunt.id as guntid,\n"
                + "gunt.name as guntname\n"
                + "FROM inventory.stock stc \n"
                + "LEFT JOIN inventory.stock_unit_con suc ON(suc.stock_id=stc.id AND suc.deleted=FALSE)\n"
                + "LEFT JOIN general.unit as gunt ON(gunt.id=suc.unit_id AND gunt.deleted=FALSE)\n"
                + "WHERE stc.deleted=FALSE and stc.id= ? " + where + "/n";
        Object[] param = {stock.getId()};
        List<Unit> result = getJdbcTemplate().query(sql, param, new UnitMapper());
        return result;
    }

    /**
     * Depolar arası transfer sayfasında çıkış deposu seçildiğinde seçilen
     * depodaki stoklar listeye hareket oluşturmak için listeye çekilir
     *
     * @param exitWarehouse
     * @param entryWarehouse
     * @return
     */
    @Override
    public List<WarehouseMovement> findAllAccordingToWarehouse(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer) {
        String sql = "";
        Object[] param = null;
        String sort = " Order By ";
        if (sortField == null) {
            sortField = " stck.name ";
            sortOrder = " desc ";
        } else if (sortField.equals("iwicquantity")) {
            sortField = " ( SELECT \n"
                    + "   iwic.quantity \n"
                    + "   FROM inventory.warehouseitem iwic\n"
                    + "      WHERE iwic.stock_id=stck.id AND iwic.warehouse_id= " + exitWarehouse.getId() + "  AND iwic.deleted=FALSE\n"
                    + ") ";
        } else if (sortField.equals("iwigquantity")) {
            sortField = " ( SELECT \n"
                    + "   iwig.quantity \n"
                    + "   FROM inventory.warehouseitem iwig\n"
                    + "      WHERE iwig.stock_id=stck.id AND iwig.warehouse_id= " + entryWarehouse.getId() + "  AND iwig.deleted=FALSE\n"
                    + ") ";
        } else if (sortField.equals("quantity")) {
            if (type == 0) {
                sort = "";
                sortField = "";
                sortOrder = "";
            } else {
                sortField = "whm.quantity";
            }
        }
        if (type == 0) {
            sql = "select \n"
                    + "stck.id as whmstock_id,\n"
                    + "stck.name as stckname,\n"
                    + "stck.code as stckcode,\n"
                    + "stck.centerproductcode AS stckcenterproductcode,\n"
                    + "stck.barcode as stckbarcode,\n"
                    + "gunt.name as guntname,\n"
                    + "gunt.sortname as guntsortname,\n"
                    + "gunt.unitrounding as guntunitrounding,\n"
                    + "0 as whmquantity,\n"
                    + "0 as whmid,\n"
                    + "( SELECT \n"
                    + "   SUM(iwig.quantity) \n"
                    + "   FROM inventory.warehouseitem iwig\n"
                    + "  INNER JOIN inventory.warehouse whg ON(whg.id=iwig.warehouse_id AND whg.deleted=FALSE and whg.branch_id=?) \n"
                    + "      WHERE iwig.stock_id=stck.id AND iwig.warehouse_id=?  AND iwig.deleted=FALSE\n"
                    + ") as iwigquantity,\n"
                    + "( SELECT \n"
                    + "   SUM(iwic.quantity) \n"
                    + "   FROM inventory.warehouseitem iwic\n"
                    + "  INNER JOIN inventory.warehouse whc ON(whc.id=iwic.warehouse_id AND whc.deleted=FALSE and whc.branch_id=?) \n"
                    + "      WHERE iwic.stock_id=stck.id AND iwic.warehouse_id=?  AND iwic.deleted=FALSE\n"
                    + ") as iwicquantity\n"
                    + "from inventory.stock stck \n"
                    + "left join general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + "where stck.deleted=false and exists (SELECT iwi.stock_id FROM inventory.warehouseitem iwi WHERE iwi.warehouse_id = ? and iwi.deleted = FALSE and stck.id = iwi.stock_id )\n"
                    + "and exists (select si.id from inventory.stockinfo si where si.deleted=false and si.stock_id = stck.id and si.branch_id= ?)\n"
                    + where + "\n"
                    + sort + sortField + " " + sortOrder + " limit " + pageSize + " offset " + first;
            param = new Object[]{entryWarehouse.getBranch().getId(), entryWarehouse.getId(), sessionBean.getUser().getLastBranch().getId(), exitWarehouse.getId(), exitWarehouse.getId(), entryWarehouse.getBranch().getId()};

            
        } else if (type == 1) {
            sql = "select \n"
                    + "whm.id as whmid,\n"
                    + "whm.stock_id as whmstock_id,\n"
                    + "stck.name as stckname,\n"
                    + "stck.code as stckcode,\n"
                    + "stck.centerproductcode AS stckcenterproductcode,\n"
                    + "stck.barcode as stckbarcode,\n"
                    + "gunt.name as guntname,\n"
                    + "gunt.sortname as guntsortname,\n"
                    + "gunt.unitrounding as guntunitrounding,\n"
                    + "( SELECT \n"
                    + "   SUM(iwig.quantity) \n"
                    + "   FROM inventory.warehouseitem iwig\n"
                    + "  INNER JOIN inventory.warehouse whg ON(whg.id=iwig.warehouse_id AND whg.deleted=FALSE and whg.branch_id=?) \n"
                    + "      WHERE iwig.stock_id=stck.id AND iwig.warehouse_id=?  AND iwig.deleted=FALSE\n"
                    + ") as iwigquantity,\n"
                    + "( SELECT \n"
                    + "   SUM(iwic.quantity) \n"
                    + "   FROM inventory.warehouseitem iwic\n"
                    + "  INNER JOIN inventory.warehouse whc ON(whc.id=iwic.warehouse_id AND whc.deleted=FALSE and whc.branch_id=?) \n"
                    + "      WHERE iwic.stock_id=stck.id AND iwic.warehouse_id=?  AND iwic.deleted=FALSE\n"
                    + ") as iwicquantity,\n"
                    + "whm.quantity as whmquantity\n"
                    + "from inventory.warehousemovement whm\n"
                    + "inner join inventory.stock stck on (whm.stock_id=stck.id and stck.deleted=false)\n"
                    + "left join general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + "where whm.deleted=false and whm.warehousereceipt_id=? \n"
                    + where
                    + " Order By " + sortField + " " + sortOrder
                    + " limit " + pageSize + " offset " + first;

            param = new Object[]{entryWarehouse.getBranch().getId(), entryWarehouse.getId(), exitWarehouse.getBranch().getId(), exitWarehouse.getId(), warehouseTransfer.getWarehouseReceipt().getId()};
        }

        List<WarehouseMovement> result = getJdbcTemplate().query(sql, param, new WarehouseMovementMapper());
        return result;
    }

    @Override
    public int count(String where, Warehouse exitWarehouse, Warehouse entryWarehouse, int type, WarehouseTransfer warehouseTransfer) {
        String sql = "";
        Object[] param = null;
        if (type == 0) {
            sql = "SELECT \n"
                    + "	COUNT(DISTINCT stck.id) as stckidcount FROM inventory.stock stck     \n"
                    + "      LEFT JOIN general.unit gunt ON (gunt.id = stck.unit_id AND gunt.deleted=false) WHERE stck.deleted = FALSE \n"
                    + "        AND EXISTS (SELECT iwi.stock_id FROM inventory.warehouseitem iwi WHERE iwi.warehouse_id = ? and iwi.deleted = FALSE and stck.id = iwi.stock_id )\n"
                    + "	       AND EXISTS (SELECT si.id FROM inventory.stockinfo si WHERE si.deleted= FALSE AND si.stock_id = stck.id and si.branch_id= ?) " + where;

            param = new Object[]{exitWarehouse.getId(), entryWarehouse.getBranch().getId()};

        } else if (type == 1) {
            sql = "select \n"
                    + "             COUNT(DISTINCT whm.id ) as whmidcount\n"
                    + "                    from inventory.warehousemovement whm\n"
                    + "                    inner join inventory.stock stck on (whm.stock_id=stck.id and stck.deleted=false)\n"
                    + "                    left join general.unit gunt ON (gunt.id = stck.unit_id and gunt.deleted=false)\n"
                    + "                    where whm.deleted=false and whm.warehousereceipt_id=?" + where;

            param = new Object[]{warehouseTransfer.getWarehouseReceipt().getId()};
        }
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

}
