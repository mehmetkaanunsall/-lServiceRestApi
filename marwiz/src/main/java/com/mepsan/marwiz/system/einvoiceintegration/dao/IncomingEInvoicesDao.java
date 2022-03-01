/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.dao.ExchangeMapper;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.log.IncomingEInvoice;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.unit.dao.UnitMapper;
import com.mepsan.marwiz.inventory.stock.dao.StockMapper;
import com.mepsan.marwiz.inventory.warehouse.dao.WarehouseMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class IncomingEInvoicesDao extends JdbcDaoSupport implements IIncomingEInvoicesDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<IncomingEInvoice> getInvoicesData(String result) {

        String sql = "";
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {
            sql = "       \n"
                    + "SELECT unnest (xpath('/n:Envelope/n:Body/y:GetInvoicesResponse/y:GetInvoicesResult/z:Invoices/z:IncomingInvoice', \n"
                    + "'" + result + "'::xml\n"
                    + ", '{{n,http://schemas.xmlsoap.org/soap/envelope/},{y,http://tempuri.org/},{z,http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.GetInvoices}}')) AS lgeigetdata ";
        } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {
            sql = "SELECT unnest (xpath('/n:Envelope/n:Body/y:GetInboxInvoicesResponse/y:GetInboxInvoicesResult/y:Value/y:Items', \n"
                    + "'" + result + "'::xml\n"
                    + "               , '{{n,http://schemas.xmlsoap.org/soap/envelope/},{y,http://tempuri.org/}}')) AS lgeigetdata ";
        }
        Object[] params = new Object[]{};

        return getJdbcTemplate().query(sql, params, new IncomingEInvoicesMapper());
    }

    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<Integer> create(List<IncomingEInvoice> listOfInsert) {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement prep = null;
        List<Integer> ids = new ArrayList<>();
        try {

            connection = getDatasource().getConnection();
            String sql = " INSERT INTO log.geteinvoice \n"
                    + "   (\n"
                    + "     invoice_id,\n"
                    + "     request_id,\n"
                    + "     getdata,\n"
                    + "     gibinvoice,\n"
                    + "     gibdate,\n"
                    + "     gibaccountname,\n"
                    + "     gibtaxno,\n"
                    + "     invoicedate,\n"
                    + "     processdate,\n"
                    + "     is_success,\n"
                    + "     responsecode,\n"
                    + "     responsedescription,\n"
                    + "     approvalstatus_id,\n"
                    + "     approvaldescription,\n"
                    + "     branch_id,\n"
                    + "     c_id \n"
                    + "   )\n"
                    + "   VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

            prep = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (IncomingEInvoice incomei : listOfInsert) {
                if (incomei.getInvoiceId() == 0) {
                    prep.setNull(1, java.sql.Types.INTEGER);

                } else {

                    prep.setInt(1, incomei.getInvoiceId());
                }

                if (incomei.getRequestId() == 0) {
                    prep.setNull(2, java.sql.Types.INTEGER);

                } else {

                    prep.setInt(2, incomei.getRequestId());
                }

                prep.setString(3, incomei.getGetData());
                prep.setString(4, incomei.getGibInvoice());
                prep.setTimestamp(5, new java.sql.Timestamp(incomei.getGibDate().getTime()));
                prep.setString(6, incomei.getGibAccountName());
                prep.setString(7, incomei.getGibTaxNo());
                prep.setTimestamp(8, new java.sql.Timestamp(incomei.getInvoiceDate().getTime()));
                prep.setTimestamp(9, new java.sql.Timestamp(incomei.getProcessDate().getTime()));
                prep.setBoolean(10, incomei.isIsSuccess());
                prep.setString(11, incomei.getResponseCode());
                prep.setString(12, incomei.getResponseDescription());
                if (incomei.getApprovalStatusId() == 0) {
                    prep.setNull(13, java.sql.Types.INTEGER);

                } else {

                    prep.setInt(13, incomei.getApprovalStatusId());
                }
                prep.setString(14, incomei.getApprovalDescription());
                prep.setInt(15, sessionBean.getUser().getLastBranch().getId());
                prep.setInt(16, sessionBean.getUser().getId());

                prep.addBatch();
            }

            prep.executeBatch();

            ResultSet rs1 = prep.getGeneratedKeys();

            while (rs1.next()) {
                ids.add(rs1.getInt(1));

            }

        } catch (SQLException ex) {
            Logger.getLogger(IncomingEInvoicesDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }

    @Override
    public int update(IncomingEInvoice obj) {
        String sql = "UPDATE log.geteinvoice SET invoice_id= ?, request_id=?, is_success = ?, responsecode = ?, responsedescription=?, approvalstatus_id=?, approvaldescription=?, branch_id=?, u_id=?,u_time=NOW() WHERE id = ? ";
        Object[] param = new Object[]{obj.getInvoiceId() == 0 ? null : obj.getInvoiceId(), obj.getRequestId() == 0 ? null : obj.getRequestId(), obj.isIsSuccess(), obj.getResponseCode(), obj.getResponseDescription(), obj.getApprovalStatusId(), obj.getApprovalDescription(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public IncomingEInvoice bringEInvoiceItem(Invoice obj) {
        String sql = "   Select \n"
                + "      lgei.getdata AS lgeigetdata\n"
                + "   From log.geteinvoice lgei where  lgei.invoice_id=? AND lgei.branch_id = ? \n"
                + "   ";
        Object[] param = new Object[]{obj.getId(), sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, new IncomingEInvoicesMapper());
        } catch (EmptyResultDataAccessException e) {
            return new IncomingEInvoice();
        }
    }

    @Override
    public int createInvoice(EInvoice obj, String invoiceItems, String waybillItems, Integer ieInvoiceId, Integer ieInvoiceApprovalStatusId, String ieInvoiceApprovalDescription) {

        String sql = "SELECT r_insert_einvoice_id FROM finance.insert_einvoice(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{obj.getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId(), obj.isIsPurchase(), obj.getAccount().getId(), obj.getdNumber().getId() == 0 ? null : obj.getdNumber().getId(),
            obj.getDocumentSerial(), obj.getDocumentNumber(), new Timestamp(obj.getInvoiceDate().getTime()),
            new Timestamp(obj.getDueDate().getTime()), new Timestamp(obj.getDispatchDate().getTime()),
            obj.getDispatchAddress(), obj.getDescription(), obj.getStatus().getId(), obj.getType().getId(), false, 0, 0,
            obj.getRoundingPrice() == null ? 0 : obj.getRoundingPrice(), obj.getCurrency().getId(),
            (obj.getExchangeRate() == null || obj.getExchangeRate() == BigDecimal.ZERO) ? 1 : obj.getExchangeRate(), obj.getDeliveryPerson() == null ? null : obj.getDeliveryPerson(), obj.getJsonWarehouses(), invoiceItems,
            obj.getTaxPayerTypeId(), obj.getDeliveryTypeId(), obj.getInvoiceScenarioId(),
            80, (obj.getTotalDiscount() == null || obj.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) != 1) ? null : 82, null, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription, waybillItems, obj.isIsEInvoice(), obj.isIsFuel()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int updateRequestNumber() {
        String sql = "SELECT nextval('log.einvoicerequest');";

        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public Exchange bringExchangeRate(Currency currency, Currency resCurrency) {

        String sql = "SELECT COALESCE(exc.buying,1) AS excbuying\n"
                + "      FROM finance.exchange exc\n"
                + "         WHERE exc.currency_id = ? AND exc.responsecurrency_id = ? AND deleted=FALSE\n"
                + "         ORDER BY exc.c_time DESC\n"
                + "         LIMIT 1 ";
        Object[] param = new Object[]{currency.getId(), resCurrency.getId()};
        List<Exchange> list = getJdbcTemplate().query(sql, param, new ExchangeMapper(currency, resCurrency));

        if (list.size() > 0) {
            return list.get(0);
        } else {
            return new Exchange();
        }
    }

    @Override
    public List<Unit> bringUnit(Stock stock) {
        String where = " ";

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND isuc.is_otherbranch = FALSE ";
        }

        String sql = "SELECT \n"
                + " gunt.id AS guntid, "
                + "gunt.name AS guntname, "
                + "gunt.sortname AS guntsortname,"
                + "gunt.unitrounding as guntunitrounding,\n"
                + "gunt.internationalcode as guntinternationalcode \n"
                + "FROM \n"
                + "  inventory.stock_unit_con isuc \n"
                + "  INNER JOIN general.unit gunt ON (gunt.id = isuc.unit_id)"
                + "WHERE \n"
                + "  isuc.stock_id = ? AND isuc.deleted = FALSE\n"
                + where;
        Object[] param = new Object[]{stock.getId()};

        return getJdbcTemplate().query(sql, param, new UnitMapper());
    }

    @Override
    public int create(IncomingEInvoice obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IncomingEInvoice> findall(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Date beginDate, Date endDate, boolean isLazy) {
        if (sortField == null) {
            sortField = "lgei.gibdate";
            sortOrder = " DESC ";
        }

        String sql = "Select \n"
                + "      lgei.id AS lgeiid,\n"
                + "      lgei.invoice_id AS lgeiinvoice_id,\n"
                + "      lgei.getdata AS lgeigetdata,\n"
                + "      lgei.processdate AS lgeiprocessdate,\n"
                + "      lgei.is_success AS lgeiis_success,\n"
                + "      lgei.responsecode AS lgeiresponsecode,\n"
                + "      lgei.responsedescription AS lgeiresponsedescription,\n"
                + "      lgei.approvalstatus_id AS lgeiapprovalstatus_id,\n"
                + "      lgei.approvaldescription AS lgeiapprovaldescription\n"
                + "   From log.geteinvoice lgei  where lgei.is_success=false AND lgei.branch_id=? AND lgei.deleted=false AND (lgei.approvalstatus_id != 3 or lgei.approvalstatus_id Is Null )  \n"
                + where;
       
        if (isLazy) {
            sql = sql + "    ORDER BY " + sortField + " " + sortOrder + "  \n"
                    + " limit " + pageSize + " offset " + first;

        }

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new IncomingEInvoicesMapper());

    }

    @Override
    public int count(String where, Date beginDate, Date endDate) {
        String sql = "Select \n"
                + "      COUNT(lgei.id) AS lgeiid\n"
                + "   From log.geteinvoice lgei  where lgei.is_success=false AND lgei.branch_id = ? AND lgei.deleted=false AND (lgei.approvalstatus_id != 3 or lgei.approvalstatus_id Is Null )  \n"
                + where;
       

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<IncomingEInvoice> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem) {

        String sql = "SELECT \n"
                + "iw.id as iwid,\n"
                + "iw.name as iwname,\n"
                + "si.maxstocklevel as simaxstocklevel,\n"
                + "COALESCE(si.balance,0) as sibalance,\n"
                + "COALESCE(iwi.quantity,0) as availablequantity,\n"
                + "si.is_minusstocklevel AS siis_minusstocklevel\n"
                + "FROM inventory.warehouse iw\n"
                + "                  INNER JOIN inventory.warehouseitem iwi ON(iwi.warehouse_id = iw.id AND iwi.deleted = FALSE) \n"
                + "                  INNER JOIN inventory.stock stck ON(stck.id = iwi.stock_id AND stck.deleted = FALSE)\n"
                + "                  INNER JOIN inventory.stockinfo si ON(si.stock_id =stck.id AND si.deleted = FALSE AND si.branch_id=?)"
                + "                  LEFT JOIN system.status_dict std ON (iw.status_id=std.status_id and std.language_id= ? )\n"
                + "                  WHERE iw.deleted=FALSE AND iw.branch_id= ? AND stck.id = ? AND iw.is_fuel= TRUE ";

        Object[] params = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), invoiceItem.getStock().getId()};

        List<Warehouse> result = getJdbcTemplate().query(sql, params, new WarehouseMapper());
        return result;

    }

    @Override
    public List<Stock> listStock(String stockEInvoiceIntegrationCodeList, EInvoice selectedObject) {
        List<Stock> result = new ArrayList<>();
        String where = "";
        String whereAlter = "";

        if (!stockEInvoiceIntegrationCodeList.isEmpty()) {
            where = where + " AND si.einvoiceintegrationcode IN ( " + stockEInvoiceIntegrationCodeList + " )";
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND si.is_valid  = TRUE   ";
            whereAlter = whereAlter + " AND sab.is_otherbranch = FALSE ";
        } else {
            where = where + " AND stck.is_otherbranch = TRUE ";
        }
        if (selectedObject.isIsFuel()) {
            where = where + " AND si.is_fuel = TRUE ";

        }

        String sql = "SELECT \n"
                + "   DISTINCT stck.id AS stckid,\n"
                + "   stck.barcode as stckbarcode,\n"
                + "   stck.centerproductcode AS stckcenterproductcode,\n"
                + "   stck.name AS stckname,\n"
                + "   stck.code AS stckcode,\n"
                + "   stck.supplier_id AS stcksupplier_id,\n"
                + "   acc.name AS accname, \n"
                + "   stck.supplierproductcode AS stcksupplierproductcode, \n"
                + "   stck.centralsupplier_id AS stckcentralsupplier_id, \n"
                + "   cspp.name AS csppname,\n"
                + "   stck.centralsupplierproductcode AS stckcentralsupplierproductcode, \n"
                + "   sttd.status_id AS sttdid,\n"
                + "   sttd.name AS sttdname,\n"
                + "   si.recommendedprice as sirecommendedprice,\n"
                + "   si.currency_id as sicurrency_id,\n"
                + "   si.minprofitrate as siminprofitrate,\n"
                + "   si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                + "   si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                + "   COALESCE(si.currentsaleprice,0) as sicurrentsaleprice,\n"
                + "   si.currentsalecurrency_id as sicurrentsalecurrency_id,\n"
                + "   si.purchasecontroldate as sipurchasecontroldate,\n"
                + "   COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                + "  COALESCE(  si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id,\n"
                + "   si.is_fuel as siis_fuel,\n"
                + "   COALESCE(si.purchaserecommendedprice,0) AS sipurchaserecommendedprice,\n"
                + "   si.purchasecurrency_id AS sipurchasecurrency_id,\n"
                + "   gunt.id AS guntid,\n"
                + "   gunt.name AS guntname,\n"
                + "   gunt.sortname AS guntsortname,\n"
                + "   gunt.unitrounding as guntunitrounding,\n"
                + "   gunt.centerunit_id as guntcenterunit_id,\n"
                + "   stck.centerstock_id as stckcenterstock_id,\n"
                + "    seiuc.id as seiucid,\n"
                + "    seiuc.stockintegrationcode as seiucstockintegrationcode,\n"
                + "    seiuc.quantity as seiucquantity,\n"
                + "    seiuc.stock_id as seiucstock_id, \n"
                + "    gunt.internationalcode as guntinternationalcode, \n"
                + "    si.einvoiceintegrationcode as sieinvoiceintegrationcode, \n"
                + "   si.maxstocklevel as simaxstocklevel,\n"
                + "    COALESCE(si.balance,0) as sibalance \n"
                + "FROM inventory.stock stck   \n"
                + "LEFT JOIN general.unit gunt   ON (gunt.id = stck.unit_id AND gunt.deleted = False)\n"
                + "LEFT JOIN general.account acc ON (acc.id = stck.supplier_id)\n"
                + "LEFT JOIN general.centralsupplier cspp ON (cspp.id = stck.centralsupplier_id)\n"
                + "INNER JOIN system.status_dict sttd   ON (sttd.status_id = stck.status_id AND sttd.language_id = ?)  \n"
                + "LEFT JOIN inventory.stockalternativebarcode sab ON(sab.stock_id = stck.id AND sab.deleted = FALSE " + whereAlter + " )\n"
                + "LEFT JOIN inventory.stock_einvoice_unit_con seiuc ON(seiuc.stock_id = stck.id AND seiuc.branch_id = ? AND seiuc.deleted = FALSE) \n"
                + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                + "WHERE stck.deleted = false  " + where + " AND EXISTS (SELECT pli.id FROM inventory.pricelistitem pli WHERE pli.deleted=FALSE AND pli.stock_id = stck.id\n"
                + "AND pli.pricelist_id IN (SELECT pl.id FROM inventory.pricelist pl WHERE pl.deleted=FALSE AND pl.branch_id=? AND\n"
                + "  pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.status_id=11 LIMIT 1))   AND stck.status_id <> 4  AND stck.is_service = FALSE AND si.is_passive = FALSE \n"
                + "ORDER BY stck.name ";

        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        result = getJdbcTemplate().query(sql, params, new StockMapper());

        return result;

    }

    @Override
    public int updateStockIntegrationCode(IncomingInvoicesItem obj, String stockInfoIds) {
        String sql2 = "";
        if (!stockInfoIds.isEmpty()) {
            sql2 = "UPDATE inventory.stockinfo SET einvoiceintegrationcode = null , u_id = " + sessionBean.getUser().getLastBranch().getId() + ", u_time = now() where id IN ( " + stockInfoIds + ");";
        }

        String sql = sql2 + " UPDATE \n"
                + "       inventory.stockinfo\n"
                + "    SET\n"
                + "        einvoiceintegrationcode = ?,\n"
                + "        u_id = ?,\n"
                + "        u_time = now()\n"
                + "    WHERE branch_id=? AND stock_id=?;";
        Object[] param = new Object[]{obj.getOldStockEntegrationCode(), sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId(), obj.getStock().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Stock> findStockInfo(String stockEInvoiceIntegrationCode) {
        List<Stock> result = new ArrayList<>();

        String sql = "SELECT \n"
                + " si.id as siid\n"
                + "FROM inventory.stockinfo si \n"
                + "WHERE si.deleted = FALSE AND si.branch_id = ? AND si.einvoiceintegrationcode = ?";

        Object[] params = new Object[]{sessionBean.getUser().getLastBranch().getId(), stockEInvoiceIntegrationCode};

        result = getJdbcTemplate().query(sql, params, new StockMapper());

        return result;

    }

    @Override
    public int updateArchive(String ids, int updateType) {
        String sql = "UPDATE log.geteinvoice SET is_archive=?, u_id=?,u_time=NOW() WHERE id IN( " + ids + ")";
        Object[] param = new Object[]{updateType == 0 ? false : true, sessionBean.getUser().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    public List<IncomingEInvoice> findArchive(int processType) {
        String sql = "";
        if (processType == 1) {
            sql = "Select \n"
                    + "      lgei.id AS lgeiid,\n"
                    + "      lgei.invoice_id AS lgeiinvoice_id,\n"
                    + "      lgei.getdata AS lgeigetdata,\n"
                    + "      lgei.processdate AS lgeiprocessdate,\n"
                    + "      lgei.is_success AS lgeiis_success,\n"
                    + "      lgei.responsecode AS lgeiresponsecode,\n"
                    + "      lgei.responsedescription AS lgeiresponsedescription,\n"
                    + "      lgei.approvalstatus_id AS lgeiapprovalstatus_id,\n"
                    + "      lgei.approvaldescription AS lgeiapprovaldescription\n"
                    + "   From log.geteinvoice lgei  where ";

        } else {
        }

        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new IncomingEInvoicesMapper());

    }

    @Override
    public List<IncomingEInvoice> findGIBIncomingInvoices(String ids) {

        String sql = "Select \n"
                + "      lgei.id AS lgeiid,\n"
                + "      lgei.invoice_id AS lgeiinvoice_id,\n"
                + "      lgei.getdata AS lgeigetdata,\n"
                + "      lgei.processdate AS lgeiprocessdate,\n"
                + "      lgei.is_success AS lgeiis_success,\n"
                + "      lgei.responsecode AS lgeiresponsecode,\n"
                + "      lgei.responsedescription AS lgeiresponsedescription,\n"
                + "      lgei.approvalstatus_id AS lgeiapprovalstatus_id,\n"
                + "      lgei.approvaldescription AS lgeiapprovaldescription\n"
                + "   From log.geteinvoice lgei  where lgei.id IN ( " + ids + " )";

        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new IncomingEInvoicesMapper());

    }

}
