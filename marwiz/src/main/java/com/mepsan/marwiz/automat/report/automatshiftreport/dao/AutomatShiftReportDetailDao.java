/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   26.03.2019 05:39:43
 */
package com.mepsan.marwiz.automat.report.automatshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomatShiftReportDetailDao extends JdbcDaoSupport implements IAutomatShiftReportDetailDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomatSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, AutomatShift shift) {
        String sql = "SELECT\n"
                + "        asl.id AS aslid,\n"
                + "        asl.saledatetime AS aslsaledatetime,\n"
                + "        COALESCE(asl.totalprice,0) AS asltotalprice,\n"
                + "        COALESCE(asl.totalmoney,0) AS asltotalmoney,\n"
                + "        COALESCE(asl.discountprice,0) AS asltotaldiscount,\n"
                + "        COALESCE(asl.taxprice,0) AS asltotaltax,\n"
                + "        asl.currency_id AS aslcurrency_id,\n"
                + "        asl.platform_id AS aslplatform_id,\n"
                + "        asl.platformno AS aslplatformno,\n"
                + "        asl.washingmachine_id AS aslwashingmachine_id,\n"
                + "        wsh.name AS wshname,\n"
                + "        asl.paymenttype_id AS aslpaymenttype_id,\n"
                + "        asl.macaddress AS aslmacaddress\n"
                + "FROM wms.sale asl\n"
                + "     LEFT JOIN wms.washingmachine wsh ON(wsh.id = asl.washingmachine_id)\n"
                + "WHERE asl.deleted=False AND asl.shift_id=?\n"
                + "AND asl.branch_id=?\n"
                + where + "\n"
                + "ORDER BY asl.saledatetime ASC, asl.id ASC\n"
                + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId()};

        List<AutomatSales> result = getJdbcTemplate().query(sql, param, new AutomatShiftReportDetailMapper());
        return result;
    }

    @Override
    public String exportData(AutomatShift shift, String where) {
        String sql = "SELECT\n"
                + "        asl.id AS aslid,\n"
                + "        asl.saledatetime AS aslsaledatetime,\n"
                + "        COALESCE(asl.totalprice,0) AS asltotalprice,\n"
                + "        COALESCE(asl.totalmoney,0) AS asltotalmoney,\n"
                + "        COALESCE(asl.discountprice,0) AS asltotaldiscount,\n"
                + "        COALESCE(asl.taxprice,0) AS asltotaltax,\n"
                + "        asl.currency_id AS aslcurrency_id,\n"
                + "        asl.platform_id AS aslplatform_id,\n"
                + "        asl.platformno AS aslplatformno,\n"
                + "        asl.washingmachine_id AS aslwashingmachine_id,\n"
                + "        wsh.name AS wshname,\n"
                + "        asl.paymenttype_id AS aslpaymenttype_id,\n"
                + "        asl.macaddress AS aslmacaddress\n"
                + "FROM wms.sale asl\n"
                + "     LEFT JOIN wms.washingmachine wsh ON(wsh.id = asl.washingmachine_id)\n"
                + "WHERE asl.deleted=False AND asl.shift_id=" + shift.getId() + "\n"
                + "AND asl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + "\n"
                + where + "\n"
                + "ORDER BY asl.saledatetime ASC, asl.id ASC\n";

        return sql;
    }

    @Override
    public List<AutomatSales> find(AutomatSales obj) {
        String sql = "SELECT \n"
                + "           asli.id AS asliid,\n"
                + "           asli.saledatetime AS aslisaledatetime,\n"
                + "           stck.unit_id AS asliunit_id,\n"
                + "           gunt.sortname AS guntsortname,\n"
                + "           gunt.unitrounding AS guntunitsorting,\n"
                + "           COALESCE(asli.operationamount,0) AS asliquantity,\n"
                + "           COALESCE(asli.unitprice,0) AS asliunitprice,\n"
                + "           COALESCE(asli.totalprice,0) AS aslitotalprice,\n"
                + "           COALESCE(asli.totalmoney,0) AS aslitotalmoney,\n"
                + "           asli.stock_id AS aslistock_id,\n"
                + "           stck.name AS stckname,\n"
                + "           stck.code AS stckcode,\n"
                + "           stck.centerproductcode AS stckcenterproductcode,\n"
                + "           stck.barcode AS stckbarcode,\n"
                + "           asli.tank_id AS aslitank_id,\n"
                + "           asli.tankno AS aslitankno,\n"
                + "           asli.nozzle_id AS aslinozzle_id,\n"
                + "           asli.nozzleno AS aslinozzleno\n"
                + "       FROM wms.sale asli\n"
                + "       INNER JOIN inventory.stock stck ON(stck.id=asli.stock_id AND stck.deleted=FALSE)\n"
                + "       	 LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                + "       WHERE asli.deleted=FALSE AND asli.id=?\n"
                + "       ORDER BY asli.saledatetime ASC";

        Object[] param = new Object[]{obj.getId()};
        List<AutomatSales> result = getJdbcTemplate().query(sql, param, new AutomatShiftSaleItemMapper());
        return result;
    }

    @Override
    public List<AutomatSales> totals(String where, AutomatShift shift) {
        String sql = "SELECT \n"
                + "  	 COUNT(asl.id) AS aslid ,\n"
                + "    asl.currency_id AS aslcurrency_id,\n"
                + "    COALESCE(SUM(asl.totalmoney),0) AS asltotalmoney\n"
                + " FROM wms.sale asl\n"
                + "     LEFT JOIN wms.washingmachine wsh ON(wsh.id = asl.washingmachine_id)\n"
                + "WHERE asl.deleted=False AND asl.shift_id=?\n"
                + "AND asl.branch_id=?\n"
                + where + "\n"
                + "GROUP BY asl.currency_id";

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId()};

        List<AutomatSales> result = getJdbcTemplate().query(sql, param, new AutomatShiftReportDetailMapper());
        return result;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
