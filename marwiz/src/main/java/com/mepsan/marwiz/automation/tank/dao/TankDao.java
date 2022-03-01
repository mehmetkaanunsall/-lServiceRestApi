/**
 * This class ...
 *
 *
 * @author Emrullah YAKIŞAN
 *
 * @date    06.02.2019 08:40:05
 */
package com.mepsan.marwiz.automation.tank.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.inventory.WarehouseItem;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class TankDao extends JdbcDaoSupport implements ITankDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Warehouse> findAll() {
        String sql = " SELECT\n"
                  + "    iw.id as iwid\n"
                  + "    ,iw.name AS iwname \n"
                  + "    ,iw.code AS iwcode \n"
                  + "    ,iw.description AS iwdescription \n"
                  + "    ,iw.status_id AS iwstatus_id \n"
                  + "    ,iw.is_fuel AS iwis_fuel \n"
                  + "    ,iw.capacity AS iwcapacity \n"
                  + "    ,iw.mincapacity AS iwmincapacity \n"
                  + "    ,iw.concantrationrate AS iwconcantrationrate  \n"
                  + "    ,std.name AS stdname  \n"
                  + "     ,iwi.stock_id AS iwistock_id \n"
                  + "     ,stck.name AS stckname \n"
                  + "     ,stck.unit_id AS stckunit_id\n"
                  + "     ,gunt.name AS guntname \n"
                  + "     ,gunt.sortname AS guntsortname \n"
                  + "     ,gunt.unitrounding as guntunitrounding \n"
                  + "   ,COALESCE((SELECT SUM(tt.price) FROM\n"
                  + "          (SELECT\n"
                  + "    	CASE WHEN inv2.differentinvoice_id IS NOT NULL THEN\n"
                  + "               SUM ((inv2.totalprice)*COALESCE(inv2.exchangerate ,1))\n"
                  + "           ELSE\n"
                  + "               SUM ((invi.totalprice)*COALESCE(invi.exchangerate ,1))\n"
                  + "           END AS price\n"
                  + "  	FROM  inventory.warehousemovement wm \n"
                  + "      INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "      INNER JOIN finance.waybill_warehousereceipt_con wwhrc ON(wwhrc.warehousereceipt_id=wr.id AND wwhrc.deleted=FALSE)\n"
                  + "      INNER JOIN finance.waybill wy ON(wy.id = wwhrc.waybill_id AND wy.deleted = FALSE)\n"
                  + "      INNER JOIN finance.waybill_invoice_con wyic ON(wyic.waybill_id = wy.id AND wyic.deleted = FALSE)\n"
                  + "      INNER JOIN finance.invoice invi ON(invi.id = wyic.invoice_id AND invi.deleted = FALSE)\n"
                  + "      LEFT JOIN finance.invoice inv2 ON(inv2.differentinvoice_id = invi.id AND inv2.deleted=FALSE)\n"
                  + "    WHERE\n"
                  + "     wm.warehouse_id = iw.id AND wm.stock_id = iwi.stock_id AND wm.deleted = FALSE\n"
                  + "    GROUP BY inv2.differentinvoice_id) tt\n"
                  + "    ) ,0)AS purchasetotalprice\n"
                  + "    ,COALESCE((SELECT SUM(tt.price) FROM\n"
                  + "          (SELECT\n"
                  + "    	CASE WHEN inv2.differentinvoice_id IS NOT NULL THEN\n"
                  + "               SUM ((inv2.totalmoney)*COALESCE(inv2.exchangerate, 1))\n"
                  + "           ELSE\n"
                  + "               SUM ((invi.totalmoney)*COALESCE(invi.exchangerate, 1))\n"
                  + "           END AS price\n"
                  + "  	FROM  inventory.warehousemovement wm \n"
                  + "      INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "      INNER JOIN finance.waybill_warehousereceipt_con wwhrc ON(wwhrc.warehousereceipt_id=wr.id AND wwhrc.deleted=FALSE)\n"
                  + "      INNER JOIN finance.waybill wy ON(wy.id = wwhrc.waybill_id AND wy.deleted = FALSE)\n"
                  + "      INNER JOIN finance.waybill_invoice_con wyic ON(wyic.waybill_id = wy.id AND wyic.deleted = FALSE)\n"
                  + "      INNER JOIN finance.invoice invi ON(invi.id = wyic.invoice_id AND invi.deleted = FALSE)\n"
                  + "      LEFT JOIN finance.invoice inv2 ON(inv2.differentinvoice_id = invi.id AND inv2.deleted=FALSE)\n"
                  + "    WHERE\n"
                  + "     wm.warehouse_id = iw.id  AND wm.stock_id = iwi.stock_id AND wm.deleted = FALSE\n"
                  + "    GROUP BY inv2.differentinvoice_id) tt\n"
                  + "    ),0) AS purchasetotalmoney\n"
                  + "    ,COALESCE((SELECT\n"
                  + "    	SUM (wm.quantity)\n"
                  + "  	FROM  inventory.warehousemovement wm \n"
                  + "    WHERE\n"
                  + "     wm.warehouse_id = iw.id AND wm.deleted = FALSE AND wm.is_direction = TRUE\n"
                  + "    ),0) AS purchasetotalliter\n"
                  + "    ,COALESCE((SELECT\n"
                  + "    	SUM (wm.quantity)\n"
                  + "  	FROM  inventory.warehousemovement wm \n"
                  + "    WHERE\n"
                  + "     wm.warehouse_id = iw.id AND wm.stock_id = iwi.stock_id AND wm.deleted = FALSE AND wm.is_direction = FALSE\n"
                  + "    ),0) AS salestotalliter\n"
                  + "      ,COALESCE((SELECT\n"
                  + "    	SUM (shs.totalmoney)\n"
                  + "  	FROM  inventory.warehousemovement wm \n"
                  + "      INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "      INNER JOIN automation.shiftsale shs ON(shs.warehousereceipt_id = wr.id AND shs.deleted = FALSE)\n"
                  + "\n"
                  + "    WHERE\n"
                  + "     wm.warehouse_id = iw.id AND wm.stock_id = iwi.stock_id AND wm.deleted = FALSE\n"
                  + "    ),0) AS salestotalmoney\n"
                  + "     ,COALESCE((SELECT\n"
                  + "    	SUM (shs.totalmoney)\n"
                  + "  	FROM  inventory.warehousemovement wm \n"
                  + "      INNER JOIN inventory.warehousereceipt wr ON(wr.id = wm.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "      INNER JOIN automation.shiftsale shs ON(shs.warehousereceipt_id = wr.id AND shs.deleted = FALSE)\n"
                  + "\n"
                  + "    WHERE\n"
                  + "     wm.warehouse_id = iw.id AND wm.stock_id = iwi.stock_id AND wm.deleted = FALSE\n"
                  + "    ),0) AS salestotalprice\n"
                  + "    ,COALESCE(iwi.quantity,0) AS lastquantity\n"
                  + "    ,COALESCE((\n"
                  + "    SELECT \n"
                  + "    	CASE WHEN invii.is_calcincluded = TRUE THEN\n"
                  + "               (invi2.totalmoney * COALESCE(invi2.exchangerate,1)* COALESCE(inv.exchangerate,1))/invi2.quantity\n"
                  + "           ELSE\n"
                  + "               (invii.totalmoney * COALESCE(invii.exchangerate,1)* COALESCE(inv.exchangerate,1))/invii.quantity\n"
                  + "           END\n"
                  + "        FROM\n"
                  + "        finance.waybill fw\n"
                  + "        LEFT JOIN finance.waybill_warehousereceipt_con wwhrc ON(wwhrc.waybill_id=fw.id AND wwhrc.deleted=FALSE)\n"
                  + "         LEFT JOIN inventory.warehousereceipt wr ON(wr.id = wwhrc.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.waybill_invoice_con fwi ON(fwi.waybill_id = fw.id AND fwi.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.invoice inv ON(inv.id = fwi.invoice_id AND inv.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.invoiceitem invii ON(invii.invoice_id = inv.id AND invii.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.invoiceitem invi2 ON(invi2.differentinvoiceitem_id = invii.id AND invi2.deleted=FALSE)\n"
                  + "        WHERE\n"
                  + "        fw.deleted = FALSE AND invii.stock_id = iwi.stock_id AND wr.warehouse_id = iw.id ORDER BY invii.id DESC LIMIT 1\n"
                  + "    ),0) AS purchaseunitpricewithtax\n"
                  + "    ,COALESCE((\n"
                  + "    SELECT \n"
                  + "    	CASE WHEN invii.is_calcincluded = TRUE THEN\n"
                  + "               (invi2.totalprice * COALESCE(invi2.exchangerate,1)* COALESCE(inv.exchangerate,1))/invi2.quantity\n"
                  + "           ELSE\n"
                  + "               (invii.totalprice * COALESCE(invii.exchangerate,1)* COALESCE(inv.exchangerate,1))/invii.quantity\n"
                  + "           END\n"
                  + "        FROM\n"
                  + "        finance.waybill fw\n"
                  + "         LEFT JOIN finance.waybill_warehousereceipt_con wwhrc ON(wwhrc.waybill_id=fw.id AND wwhrc.deleted=FALSE)\n"
                  + "         LEFT JOIN inventory.warehousereceipt wr ON(wr.id = wwhrc.warehousereceipt_id AND wr.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.waybill_invoice_con fwi ON(fwi.waybill_id = fw.id AND fwi.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.invoice inv ON(inv.id = fwi.invoice_id AND inv.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.invoiceitem invii ON(invii.invoice_id = inv.id AND invii.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.invoiceitem invi2 ON(invi2.differentinvoiceitem_id = invii.id AND invi2.deleted=FALSE)\n"
                  + "        WHERE\n"
                  + "        fw.deleted = FALSE AND invii.stock_id = iwi.stock_id AND wr.warehouse_id = iw.id  ORDER BY invii.id DESC LIMIT 1\n"
                  + "    ),0) AS purchaseunitpricewithouttax\n"
                  + ",COALESCE((\n"
                  + "    SELECT \n"
                  + "        shs.price \n"
                  + "        FROM\n"
                  + "        automation.shiftsale shs\n"
                  + "        WHERE\n"
                  + "        shs.deleted = FALSE AND shs.stock_id = iwi.stock_id AND shs.warehouse_id = iw.id ORDER BY shs.id DESC LIMIT 1\n"
                  + "    ),0) AS salesunitpricewithouttax\n"
                  + "    ,COALESCE((\n"
                  + "    SELECT \n"
                  + "         shs.price \n"
                  + "        FROM\n"
                  + "        automation.shiftsale shs\n"
                  + "        WHERE\n"
                  + "        shs.deleted = FALSE AND shs.stock_id = iwi.stock_id AND shs.warehouse_id = iw.id ORDER BY shs.id DESC LIMIT 1\n"
                  + "    ),0) AS salesunitpricewithtax	\n"
                  + "FROM inventory.warehouse iw\n"
                  + "INNER JOIN inventory.warehouseitem iwi ON(iwi.warehouse_id = iw.id AND iwi.deleted = FALSE) \n"
                  + "INNER JOIN inventory.stock stck ON(stck.id = iwi.stock_id AND stck.deleted = FALSE)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id AND gunt.deleted = FALSE) \n"
                  + "LEFT JOIN system.status_dict std ON (iw.status_id=std.status_id and std.language_id=?)\n"
                  + "WHERE iw.deleted=FALSE and iw.is_fuel=TRUE AND iw.branch_id= ? \n"
                  + " ORDER BY iw.status_id ASC ";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new TankMapper());
        return result;
    }

    @Override
    public int create(Warehouse obj) {
        String sql = "insert into inventory.warehouse\n"
                  + "(name,code, capacity, mincapacity,concantrationrate,branch_id,status_id,description,is_fuel,c_id,u_id)\n"
                  + "values(?,?,?,?,?,?,?,?,?,?,?)"
                  + " RETURNING id ;";

        Object[] param = {obj.getName(), obj.getCode(), obj.getCapacity(), obj.getMinCapacity(), obj.getConcentrationRate(), sessionBean.getUser().getLastBranch().getId(), obj.getStatus().getId(), obj.getDescription(), true, sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Warehouse obj) {
        String sql = "update inventory.warehouse\n"
                  + "set\n"
                  + "name=?,\n"
                  + "code=?,\n"
                  + "capacity=?,\n"
                  + "mincapacity=?,\n"
                  + "concantrationrate=?,\n"
                  + "status_id=?,\n"
                  + "description=?,\n"
                  + "u_id=?,\n"
                  + "u_time= now()"
                  + "where id=?;";

        Object[] param = {obj.getName(), obj.getCode(), obj.getCapacity(), obj.getMinCapacity(), obj.getConcentrationRate(), obj.getStatus().getId(), obj.getDescription(), sessionBean.getUser().getId(), obj.getId()};
        System.out.println("" + Arrays.toString(param));

        try {

            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Warehouse warehouse) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT warehouse_id FROM inventory.warehousemovement WHERE warehouse_id=? AND deleted=False) THEN 1 ELSE 0 END ";

        Object[] param = new Object[]{warehouse.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Warehouse warehouse) {
        String sql = "UPDATE inventory.warehouse SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), warehouse.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<WarehouseItem> selectListWareHouseItem(Warehouse warehouse) {
        String sql = "SELECT\n"
                  + "  stck.id AS stckid,\n"
                  + "  stck.name AS stckname,\n"
                  + "  stck.code AS stckcode,\n"
                  + "  stck.barcode AS stckbarcode,\n"
                  + "  stck.centerproductcode AS stckcenterproductcode,\n"
                  + "  iwi.id AS iwid\n"
                  + "FROM\n"
                  + "  inventory.stock stck\n"
                  + "  INNER JOIN inventory.stockinfo sti ON(sti.stock_id = stck.id AND sti.deleted = FALSE AND sti.is_fuel = TRUE AND sti.branch_id = ?)\n"
                  + "  LEFT JOIN inventory.warehouseitem iwi ON(iwi.stock_id = sti.stock_id AND iwi.warehouse_id = ? AND iwi.deleted = FALSE)\n"
                  + "WHERE stck.deleted = FALSE";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), warehouse.getId()};

        List<WarehouseItem> result = getJdbcTemplate().query(sql, param, new TankItemMapper());
        return result;
    }

    @Override
    public int createWareHouseItem(Stock stock, Warehouse warehouse) {
        String sql = "insert into inventory.warehouseitem\n"
                  + "(warehouse_id,stock_id, quantity,c_id,u_id)\n"
                  + "values(?,?,?,?, ?)"
                  + " RETURNING id ;";

        Object[] param = {warehouse.getId(), stock.getId(), 0, sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {

            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateWareHouseItem(WarehouseItem warehouseItem, Warehouse warehouse) {
        String sql = "UPDATE inventory.warehouseitem SET stock_id=?, u_id=? , u_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{warehouseItem.getStock().getId(), sessionBean.getUser().getId(), warehouseItem.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Warehouse> findTankList() {
        String sql = " SELECT\n"
                  + "    iw.id as iwid\n"
                  + "    ,iw.name AS iwname \n"
                  + "    ,iw.code AS iwcode \n"
                  + "    ,iw.description AS iwdescription \n"
                  + "    ,iw.status_id AS iwstatus_id \n"
                  + "    ,iw.is_fuel AS iwis_fuel \n"
                  + "    ,iw.capacity AS iwcapacity \n"
                  + "    ,iw.mincapacity AS iwmincapacity \n"
                  + "    ,iw.concantrationrate AS iwconcantrationrate \n"
                  + "FROM inventory.warehouse iw\n"
                  + "WHERE iw.deleted=FALSE and iw.is_fuel=TRUE AND iw.branch_id= ? \n "
                  + "ORDER BY iw.status_id ASC ";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<Warehouse> result = getJdbcTemplate().query(sql, param, new TankMapper());
        return result;
    }

}
