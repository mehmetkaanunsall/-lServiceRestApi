/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.03.2019 01:48:21
 */
package com.mepsan.marwiz.automat.report.automatsalesreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatSales;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AutomatSalesReportDao extends JdbcDaoSupport implements IAutomatSalesReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AutomatSalesReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String subTotal) {
        String fields = "";
        String order = "";

        if (!subTotal.isEmpty()) {
            if (subTotal.equals("asl.saledatetime")) {
                subTotal = " date(asl.saledatetime) ";
                order = " asl.saledatetime ";
            } else {
                order = subTotal + " ,asl.saledatetime ";
            }
            //Alt Toplam Var İse Alanları Toplayarak Çek
            fields = "SUM(asl.totalmoney) OVER(PARTITION BY " + subTotal + " ) AS totalmoney,\n"
                    + "	COUNT(asl.id) OVER(PARTITION BY " + subTotal + " ) AS aslidcount,";
        } else {
            order = " asl.saledatetime ";
        }

        String sql = "SELECT\n"
                + "          asl.id AS aslid,\n"
                + "          asl.saledatetime AS aslsaledatetime,\n"
                + "          date(asl.saledatetime) AS aslsaledate,\n"
                + "          asl.paymenttype_id AS aslpaymenttype_id,\n"
                + "          asl.shift_id AS aslshift_id,\n"
                + "          asl.shiftno AS aslshiftno,\n"
                + "          asl.washingmachine_id AS aslwashingmachine_id,\n"
                + "          asl.macaddress AS aslmacaddress,\n"
                + "          asl.is_online  As aslis_online,\n"
                + "          asl.operationtime AS asloperationtime,\n"
                + "          asl.customerrfid As aslcustomerrfid,\n"
                + "          asl.barcodeno as aslbarcodeno,\n"
                + "          asl.account_id as aslaccount_id,\n"
                + "          asl.mobileno as aslmobileno,\n"
                + "          wsh.name AS wshname,\n"
                + "          COALESCE(asl.discountprice,0) AS asltotaldiscount,\n"
                + "          COALESCE(asl.taxprice,0) AS asltotaltax,\n"
                + "          asl.currency_id AS aslcurrency_id,\n"
                + "          asl.macaddress AS aslmacaddress,\n"
                + "          asl.platform_id AS aslplatform_id,\n"
                + "          asl.platformno AS aslplatformno,\n"
                + "          asl.stock_id AS aslstock_id,\n"
                + "          stck.name AS stckname,\n"
                + fields
                + "          COALESCE(asl.totalprice,0) AS asltotalprice,\n"
                + "          COALESCE(asl.totalmoney,0) AS asltotalmoney\n"
                + "      FROM wms.sale asl\n"
                + "      LEFT JOIN wms.washingmachine wsh ON(wsh.id = asl.washingmachine_id)\n"
                + "      LEFT JOIN inventory.stock stck ON(stck.id=asl.stock_id AND stck.deleted=FALSE)"
                + "       WHERE asl.deleted=FALSE AND asl.branch_id=?\n"
                + where + "\n"
                + "ORDER BY " + order + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<AutomatSalesReport> result = getJdbcTemplate().query(sql, param, new AutomatSalesReportMapper());

        return result;
    }

    @Override
    public List<AutomatSalesReport> totals(String where) {
        String sql = "SELECT \n"
                + "	COUNT(asl.id) AS aslid,\n"
                + "	COALESCE(SUM(asl.totalmoney),0) AS asltotalmoney,\n"
                + "	asl.currency_id AS  aslcurrency_id\n"
                + "FROM wms.sale asl\n"
                + "WHERE asl.deleted=FALSE AND asl.branch_id=?\n"
                + where + "\n"
                + "GROUP BY asl.currency_id";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<AutomatSalesReport> result = getJdbcTemplate().query(sql, param, new AutomatSalesReportMapper());

        return result;
    }

    @Override
    public String exportData(String where, String subTotal) {
        String fields = "";
        String order = "";

        if (!subTotal.isEmpty()) {
            if (subTotal.equals("asl.saledatetime")) {
                subTotal = " date(asl.saledatetime) ";
                order = " asl.saledatetime ";
            } else {
                order = subTotal + " ,asl.saledatetime ";
            }
            //Alt Toplam Var İse Alanları Toplayarak Çek
            fields = "SUM(asl.totalmoney) OVER(PARTITION BY " + subTotal + " ) AS totalmoney,\n"
                    + "	COUNT(asl.id) OVER(PARTITION BY " + subTotal + " ) AS aslidcount,";
        } else {
            order = " asl.saledatetime ";
        }
        String sql = "SELECT\n"
                + "          asl.id AS aslid,\n"
                + "          asl.saledatetime AS aslsaledatetime,\n"
                + "          date(asl.saledatetime) AS aslsaledate,\n"
                + "          asl.paymenttype_id AS aslpaymenttype_id,\n"
                + "          asl.shift_id AS aslshift_id,\n"
                + "          asl.shiftno AS aslshiftno,\n"
                + "          asl.operationtime AS asloperationtime,\n"
                + "          asl.washingmachine_id AS aslwashingmachine_id,\n"
                + "          asl.macaddress AS aslmacaddress,\n"
                + "          asl.is_online  As aslis_online,\n"
                + "          asl.customerrfid As aslcustomerrfid,\n"
                + "          asl.barcodeno as aslbarcodeno,\n"
                + "          asl.account_id as aslaccount_id,\n"
                + "          asl.mobileno as aslmobileno,\n"
                + "          wsh.name AS wshname,\n"
                + "          COALESCE(asl.discountprice,0) AS asltotaldiscount,\n"
                + "          COALESCE(asl.taxprice,0) AS asltotaltax,\n"
                + "          asl.currency_id AS aslcurrency_id,\n"
                + "          asl.platform_id AS aslplatform_id,\n"
                + "          asl.platformno AS aslplatformno,\n"
                + "          asl.stock_id AS aslstock_id,\n"
                + "          stck.name AS stckname,\n"
                + fields + "\n"
                + "    COALESCE(asl.totalprice,0) AS asltotalprice,\n"
                + "    COALESCE(asl.totalmoney,0) AS asltotalmoney\n"
                + "FROM wms.sale asl\n"
                + "   LEFT JOIN wms.washingmachine wsh ON(wsh.id = asl.washingmachine_id)\n"
                + "      LEFT JOIN inventory.stock stck ON(stck.id=asl.stock_id AND stck.deleted=FALSE)"
                + " WHERE asl.deleted=FALSE AND asl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + "\n"
                + where + "\n"
                + "ORDER BY " + order;

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
