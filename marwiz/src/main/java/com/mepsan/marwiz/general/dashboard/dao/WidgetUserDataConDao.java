/**
 * @author Esra ÇABUK
 * @date 21.06.2018 10:25:33
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.WidgetUserDataCon;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WidgetUserDataConDao extends JdbcDaoSupport implements IWidgetUserDataConDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<WidgetUserDataCon> findAll() {
        String sql = "SELECT \n"
                  + "	wd.id,\n"
                  + "    wd.name,\n"
                  + "    wduc.id as wducid,\n"
                  + "    wduc.col,\n"
                  + "    wduc.row,\n"
                  + "    wduc.userdata_id\n"
                  + "FROM general.widget wd\n"
                  + "	INNER JOIN general.userdata_widget_con wduc ON wduc.widget_id=wd.id\n"
                  + "WHERE wduc.userdata_id= ? AND wd.deleted=False AND wduc.deleted=False ORDER BY wduc.row";

        Object[] param = new Object[]{sessionBean.getUser().getId()};
        List<WidgetUserDataCon> widgetUserDataCon = getJdbcTemplate().query(sql, param, new WidgetUserDataConMapper());
        return widgetUserDataCon;
    }

    @Override
    public List<ChartItem> getMostSoldStocks(String where, boolean isAllBranches, int changeStock) {

        String whereAllBranches = "";
        String whereLımıt = "LIMIT 5";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (changeStock == 2) {
            whereLımıt = "";
        }

        String sql = "SELECT \n"
                  + "    sli.stock_id AS slistock_id, \n"
                  + "    concat(concat(sli.stock_id,'-'),stck.name) AS stckname, \n"
                  + "    unt.sortname AS untsortname,\n"
                  + "     sli.unit_id AS sli_unitid,\n"
                  + "     sli.currency_id AS sli_currencyid,"
                  + "    SUM(sli.quantity) AS sliquantity,\n"
                  + "     SUM(sli.totalmoney) AS slitotalmoney \n"
                  + "FROM general.saleitem sli \n"
                  + "	INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False) \n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False) \n"
                  + "	INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id AND stck.deleted = False) \n"
                  + "  LEFT JOIN general.unit unt ON(unt.id=sli.unit_id AND unt.deleted=False)\n"
                  + "  LEFT JOIN system.currency crncy ON(crncy.id=sli.currency_id AND crncy.deleted=FALSE)\n"
                  + "WHERE \n"
                  + " sl.is_return=False \n" + whereAllBranches + " \n"
                  + "    AND sli.deleted=FALSE \n"
                  + "    AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + where + "\n"
                  + "GROUP BY sli.stock_id,stck.name,unt.sortname,sli.currency_id,sli.unit_id\n"
                  + "ORDER BY SUM(sli.quantity) DESC\n"
                  + whereLımıt
                  + "\n";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper(50));
        return chartItem;
    }

    @Override
    public List<ChartItem> getMostCustomersBySale(String where, boolean isAllBranches, int changeCustomerPurchases) {
        String whereAllBranches = "";
        String whereLımıt = "LIMIT 5";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (changeCustomerPurchases == 2) {
            whereLımıt = "";
        }

        String sql = "SELECT \n"
                  + "    sl.account_id AS slaccount_id, \n"
                  + "    concat(concat(sl.account_id,'-'),acc.name) AS accname, \n"
                  + "    SUM(sl.totalmoney) AS totalmoney,\n"
                  + "     sl.currency_id AS sl_currencyid\n"
                  + "FROM general.sale sl\n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False) \n"
                  + "	INNER JOIN general.account acc ON(acc.id=sl.account_id AND acc.deleted = False) \n"
                  + "  LEFT JOIN system.currency crncy ON(crncy.id=sl.currency_id AND crncy.deleted=FALSE)\n"
                  + "WHERE \n"
                  + "	 sl.is_return=False \n" + whereAllBranches + "\n"
                  + "    AND sl.deleted=FALSE \n"
                  + "	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + where + "\n"
                  + "GROUP BY sl.account_id, acc.name, sl.currency_id \n"
                  + "HAVING SUM(sl.totalmoney) > 0\n"
                  + "ORDER BY SUM(sl.totalmoney) DESC\n"
                  + whereLımıt + "\n";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper(50));
        return chartItem;
    }

    @Override
    public List<ChartItem> getSalesByCategorization(String whereChange, String where, String whereLımıt, boolean isAllBranches, int type2) {
        String whereAllBranches = "";
        String field = "";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (type2 == 0) {
            field = " SUM(sli.quantity) AS sliquantity, \n";
        } else {
            field = " SUM(sli.totalmoney) AS sliquantity, \n";
        }

        String sql = "SELECT\n"
                  + field
                  + "          scc.categorization_id AS scccategorization_id,\n"
                  + "          cg.name AS cgname\n"
                  + "      FROM general.saleitem sli \n"
                  + "      	INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False) \n"
                  + "      	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False) \n"
                  + "          INNER JOIN inventory.stock_categorization_con scc ON(scc.stock_id=sli.stock_id AND scc.deleted = False)\n"
                  + "          INNER JOIN general.categorization cg ON(cg.id=scc.categorization_id AND cg.deleted = False)\n"
                  + "      WHERE \n"
                  + "      	sl.is_return=False \n" + whereAllBranches + "\n"
                  + "          AND sli.deleted=FALSE \n"
                  + where + "\n"
                  + "      	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + whereChange + "\n"
                  + "      GROUP BY scc.categorization_id, cg.name\n"
                  + "      ORDER BY SUM(sli.quantity) DESC \n"
                  + whereLımıt;

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper(25));
        return chartItem;
    }

    @Override
    public List<ChartItem> getSalesByBrand(String where, boolean isAllBranches, int changeBrand) {

        String fields = "";
        String groupby = "";
        String whereAllBranches = "";
        String whereLımıt = "LIMIT 5";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (changeBrand == 2) {
            fields = ",sli.unit_id AS sli_unitid,\n"
                      + "sli.currency_id AS sli_currencyid,\n"
                      + "unt.sortname as untsortname \n";

            groupby = ",sli.currency_id,sli.unit_id ,unt.sortname  \n";

            whereLımıt = "";
        }

        String sql = "SELECT\n"
                  + "    SUM(sli.quantity) AS sliquantity,\n"
                  + "    concat(concat(stck.brand_id,'-'),br.name) AS brname,\n"
                  + "    stck.brand_id AS stckbrand_id,\n"
                  + "    SUM(sli.totalmoney) AS slitotalmoney\n"
                  + fields + "\n"
                  + "FROM general.saleitem sli \n"
                  + "	INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False) \n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False) \n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id AND stck.deleted=False)\n"
                  + "    INNER JOIN general.brand br ON(br.id=stck.brand_id AND br.deleted = False)\n"
                  + "    LEFT JOIN general.unit unt ON(unt.id=sli.unit_id AND unt.deleted = FALSE)\n"
                  + "WHERE \n"
                  + "	sl.is_return=False \n" + whereAllBranches + "\n"
                  + "    AND sli.deleted=FALSE \n"
                  + "	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + where + "\n"
                  + "GROUP BY stck.brand_id, br.name \n"
                  + groupby + "\n"
                  + "ORDER BY SUM(sli.quantity) DESC\n"
                  + whereLımıt + "\n";


        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper(50));
        return chartItem;
    }

    @Override
    public List<ChartItem> getDecreasingStocks(boolean isAllBranches) {
        String whereAllBranches = "";
        String whereAllBranchesForStockInfo = "";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND iw.branch_id = " + sessionBean.getUser().getLastBranch().getId();
            whereAllBranchesForStockInfo = whereAllBranchesForStockInfo + " AND stcki.branch_id =" + +sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "SELECT\n"
                  + "          iwi.stock_id AS iwistock_id,\n"
                  + "          stck.name AS stckname,\n"
                  + "          unt.id AS untid,\n"
                  + "          unt.sortname AS untsortname,\n"
                  + "          COALESCE(SUM(iwi.quantity),0) AS sliquantity,\n"
                  + "          COALESCE(stcki.minstocklevel,0) AS stckiminstocklevel\n"
                  + "       FROM inventory.warehouseitem iwi\n"
                  + "      	INNER JOIN inventory.stock stck ON(stck.id=iwi.stock_id AND stck.deleted = False)\n"
                  + "      	INNER JOIN inventory.warehouse iw ON(iw.id=iwi.warehouse_id AND iw.deleted = False)\n"
                  + "        LEFT JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=False)\n"
                  + "        LEFT JOIN inventory.stockinfo stcki ON(stcki.stock_id=stck.id AND stcki.deleted=False " + whereAllBranchesForStockInfo + ")\n"
                  + "       WHERE iwi.deleted=False \n" + whereAllBranches + "\n"
                  + "       GROUP BY iwi.stock_id, stck.name, stcki.minstocklevel,unt.id,unt.sortname\n"
                  + "       HAVING SUM(iwi.quantity) < stcki.minstocklevel\n"
                  + "       ORDER BY  SUM(iwi.quantity)\n"
                  + "       LIMIT 5";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;
    }

    @Override
    public List<ChartItem> getSalesByCashier(String where, boolean isAllBranches, int changeCashier) {
        String whereAllBranches = "";
        String whereLımıt = "LIMIT 5";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (changeCashier == 2) {
            whereLımıt = "";
        }

        String sql = "SELECT \n"
                  + "    sl.userdata_id AS sluserdate_id, \n"
                  + "    concat(concat(sl.userdata_id,'-'),us.name) AS usname,\n"
                  + "    us.surname AS ussurname, \n"
                  + "    SUM(sl.totalmoney) AS totalmoney,\n"
                  + "    sl.currency_id AS sl_currencyid \n"
                  + "FROM general.sale sl\n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False) \n"
                  + "	INNER JOIN general.userdata us ON(us.id=sl.userdata_id) \n"
                  + "  LEFT JOIN system.currency crncy ON(crncy.id=sl.currency_id AND crncy.deleted=FALSE)\n"
                  + "WHERE \n"
                  + "	sl.is_return=False \n" + whereAllBranches + " \n"
                  + "    AND sl.deleted=FALSE \n"
                  + "	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + where + "\n"
                  + "     AND us.type_id= 2 \n"
                  + "GROUP BY sl.userdata_id, us.name, us.surname, sl.currency_id \n"
                  + "ORDER BY SUM(sl.totalmoney) DESC\n";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper(50));
        return chartItem;
    }

    @Override
    public List<ChartItem> getSalesByPumper(String where, boolean isAllBranches, int changePumper) {
        String whereAllBranches = "";
        String whereLımıt = "LIMIT 5";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND shft.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (changePumper == 2) {
            whereLımıt = "";
        }

        String sql = "SELECT\n"
                  + "COALESCE(ssl.attendantname,'') AS sslattendantname,\n"
                  + "sum(COALESCE(ssl.totalmoney,0)) AS ssltotalmoney,\n"
                  + "br.currency_id AS br_currency \n"
                  + "FROM automation.shiftsale ssl\n"
                  + "LEFT JOIN automation.shift shft ON(shft.id=ssl.shift_id AND shft.deleted = FALSE)"
                  + "LEFT JOIN general.branch br ON(br.id=shft.branch_id AND br.deleted=FALSE) \n"
                  + "WHERE\n"
                  + "ssl.attendant_id IS NOT NULL "
                  + "AND ssl.deleted=FALSE \n"
                  + where + "\n"
                  + whereAllBranches + "\n"
                  + "GROUP BY ssl.attendantname,shft.branch_id,br.currency_id\n"
                  + "ORDER BY sum(COALESCE(ssl.totalmoney,0)) DESC\n"
                  + whereLımıt + "\n";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;

    }

    @Override
    public List<ChartItem> getSalesBySaleType(String where, boolean isAllBranches) {
        String whereAllBranches = "";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }
        String sql = "SELECT \n"
                  + "	COALESCE(SUM(slp.price * slp.exchangerate),0) AS slpprice,\n"
                  + "   sl.currency_id AS slcurrency_id,\n"
                  + "   slp.type_id AS slptype_id,\n"
                  + "   typd.name AS typdname\n"
                  + "FROM general.sale sl \n"
                  + "	LEFT JOIN general.salepayment slp ON(sl.id=slp.sale_id AND slp.deleted = False) \n"
                  + "   LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False) \n"
                  + "   LEFT JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id =?)\n"
                  + "WHERE \n"
                  + "    sl.is_return=False \n" + whereAllBranches + "\n"
                  + "    AND sl.deleted=FALSE \n"
                  + "    AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + where + "\n"
                  + "GROUP BY slp.type_id, typd.name, sl.currency_id\n"
                  + "HAVING SUM(slp.price * slp.exchangerate) > 0 \n"
                  + "\n"
                  + "UNION ALL \n"
                  + "\n"
                  + "SELECT \n"
                  + "	COALESCE(SUM(inv.remainingmoney),0) AS slpprice,\n"
                  + "   inv.currency_id AS slcurrency_id,\n"
                  + "   0,\n"
                  + "   NULL\n"
                  + "FROM general.sale sl \n"
                  + "    LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False) \n"
                  + "    LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted=False)\n"
                  + "WHERE \n"
                  + "    sl.is_return=False \n" + whereAllBranches + "\n"
                  + "    AND sl.deleted=FALSE \n"
                  + "    AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + where + "\n"
                  + "GROUP BY inv.currency_id\n"
                  + "HAVING SUM(inv.remainingmoney) > 0 \n"
                  + "\n"
                  + "ORDER BY slpprice DESC\n"
                  + "LIMIT 5";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        List<ChartItem> chartItem = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return chartItem;
    }

    @Override
    public List<ChartItem> getDuePayments(String where, boolean isAllBranches) {
        String whereAllBranches = "";

        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + "AND inv.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "SELECT \n"
                  + "acnt.name AS acntname,\n"
                  + "sum(inv.remainingmoney) AS inv_remainingmoney,\n"
                  + "inv.currency_id AS inv_currencyid\n"
                  + "FROM finance.invoice inv\n"
                  + "LEFT JOIN general.account acnt ON(inv.account_id=acnt.id AND acnt.deleted=FALSE)\n"
                  + "WHERE \n"
                  + "(inv.remainingmoney)>0\n"
                  + whereAllBranches + "\n"
                  + where + "\n"
                  + "GROUP BY acnt.name,inv.currency_id\n"
                  + "ORDER BY inv_remainingmoney DESC\n"
                  + " LIMIT 20";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;

    }

    @Override
    public List<ChartItem> getFuelShiftSales(String where, boolean isAllBranches) {

        String whereAllBranches = "";

        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + "AND ashft.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "SELECT\n"
                  + "ashft.shiftno AS shftno,\n"
                  + "SUM(sl.totalmoney) AS totalmoney\n"
                  + "FROM automation.shift ashft\n"
                  + "LEFT JOIN automation.shiftsale sl ON(sl.shift_id = ashft.id AND sl.deleted=FALSE)\n"
                  + "WHERE ashft.deleted = FALSE\n"
                  + whereAllBranches + "\n"
                  + where + "\n"
                  + "GROUP BY ashft.shiftno\n"
                  + "ORDER BY sum(sl.totalmoney) DESC\n"
                  + "LIMIT 10\n";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;
    }

    @Override
    public List<ChartItem> getReturnedStock(boolean isAllBranches) {
        String whereAllBranches = "";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "SELECT\n"
                  + "           sli.stock_id AS slistock_id,\n"
                  + "           stck.name AS stckname,\n"
                  + "           sli.unit_id AS sli_unitid,\n"
                  + "           unt.sortname as untsortname,\n"
                  + "           SUM(sli.quantity) AS sliquantity\n"
                  + "       FROM general.saleitem sli\n"
                  + "       	INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = False) \n"
                  + "           INNER JOIN inventory.stock stck ON(stck.id=sli.stock_id AND stck.deleted = False)\n"
                  + "           INNER JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                  + "       WHERE \n"
                  + "       	sl.is_return=True \n" + whereAllBranches + "\n"
                  + "            AND sli.deleted=FALSE \n"
                  + "       	AND date_part('month',  sl.processdate) =date_part('month',  CURRENT_DATE)\n"
                  + "       GROUP BY  sli.stock_id, stck.name,stck.unit_id,unt.sortname, sli.unit_id \n"
                  + "       ORDER BY SUM(sli.quantity) DESC\n"
                  + "       LIMIT 5";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper(30));
        return chartItem;
    }

    @Override
    public List<ChartItem> getFuelStock(boolean isAllBranches) {

        String whereAllBranches = "";
        if (!isAllBranches) {
            whereAllBranches = whereAllBranches + " AND stcki.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "SELECT\n"
                  + "stck.name AS stckname,\n"
                  + "stcki.balance AS stckibalance,\n"
                  + "unt.sortname as untsortname\n"
                  + "FROM inventory.stockinfo stcki \n"
                  + "LEFT JOIN inventory.stock stck ON(stck.id=stcki.stock_id AND stck.deleted=FALSE)\n"
                  + "INNER JOIN general.unit unt ON(unt.id=stck.unit_id AND unt.deleted=FALSE)\n"
                  + "WHERE\n"
                  + "stcki.is_fuel=TRUE\n"
                  + "AND \n"
                  + "stcki.deleted = FALSE\n"
                  + whereAllBranches + "\n"
                  + "GROUP BY unt.sortname ,stcki.balance,stck.name\n";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;

    }

    @Override
    public int create(WidgetUserDataCon obj) {
        String sql = "INSERT INTO general.userdata_widget_con "
                  + "("
                  + "widget_id "
                  + ",userdata_id "
                  + ",col "
                  + ",row "
                  + ",c_id"
                  + ",u_id"
                  + ") VALUES ( ? , ? , ? , ? , ?, ? ) \n"
                  + "RETURNING id ;";
        Object[] param = new Object[]{obj.getWidget().getId(), obj.getUserData().getId(),
            obj.getCol(), obj.getRow(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<WelcomeWidget> getWelcome(boolean isAllBranches) {
        String where = "";
        String fields = "";
        String groupBy = "";
        if (!isAllBranches) {
            where = where + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
            groupBy = " , shf.shiftno";
            fields = " shf.shiftno AS shfshiftno,";
        }

        String sql = "SELECT  \n"
                  + "  	SUM(CASE WHEN sl.is_return=False THEN slp.price ELSE -slp.price END) AS slpprice,\n"
                  + "   slp.type_id AS slptype_id,\n"
                  + "   typd.name AS typdname,\n"
                  + fields + "\n"
                  + "   slp.currency_id  AS slcurrency_id\n"
                  + "FROM general.sale sl\n"
                  + "	LEFT JOIN general.salepayment slp ON(slp.sale_id=sl.id AND slp.deleted = False) \n"
                  + "   LEFT JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id = ?)\n"
                  + "   INNER JOIN general.shift shf ON(shf.id = sl.shift_id AND shf.deleted = False)\n"
                  + "   INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE \n"
                  + "    sl.deleted=FALSE AND usr.type_id = 2 \n" + where + "\n"
                  + "   AND shf.status_id = 7\n"
                  + "GROUP BY  slp.type_id, typd.name, slp.currency_id" + groupBy + "\n"
                  + "HAVING SUM(slp.price) <> 0 \n"
                  + "\n"
                  + "UNION ALL \n"
                  + " \n"
                  + "SELECT \n"
                  + "	 SUM(inv.remainingmoney) AS slpprice,\n"
                  + "    0 AS slptype_id,\n"
                  + "    NULL AS typdname,\n"
                  + fields + "\n"
                  + "    inv.currency_id AS slcurrency_id\n"
                  + "FROM general.sale sl\n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.shift_id = sl.shift_id) \n"
                  + "   INNER JOIN general.shift shf ON(shf.id = sl.shift_id AND shf.deleted = False)\n"
                  + "   LEFT JOIN finance.invoice inv ON(inv.id=sl.invoice_id AND inv.deleted=False)\n"
                  + "   INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE \n"
                  + "	sl.is_return=False AND usr.type_id = 2 \n" + where + "\n"
                  + "   AND sl.deleted=FALSE \n"
                  + "	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  \n"
                  + "   AND shf.status_id = 7\n"
                  + "GROUP BY  inv.currency_id\n" + groupBy + " \n"
                  + "HAVING SUM(inv.remainingmoney) > 0";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        List<WelcomeWidget> welcomeWidgets = getJdbcTemplate().query(sql, param, new WelcomeWidgetMapper());
        return welcomeWidgets;
    }

    @Override
    public int delete(WidgetUserDataCon obj) {
        String sql = "UPDATE \n"
                  + "general.userdata_widget_con set deleted=True ,u_id= ? ,u_time=now(), d_time = now()\n"
                  + "WHERE id=? AND deleted=False";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(WidgetUserDataCon obj) {
        String sql = "UPDATE 	\n"
                  + "	general.userdata_widget_con \n"
                  + "SET col=?\n"
                  + "	,row=?\n"
                  + "     ,u_id=?\n"
                  + "     ,u_time=now()\n"
                  + "WHERE userdata_id= ? \n"
                  + "AND widget_id= ? \n"
                  + "AND deleted=False";
        Object[] param = new Object[]{obj.getCol(), obj.getRow(),
            sessionBean.getUser().getId(), obj.getUserData().getId(), obj.getWidget().getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<ChartItem> getRecoveries(boolean isAllBranches) {
        String whereAllBranchesForCredit = "", whereAllBranchesForChequeBill = "", whereAllBranchesForInvoice = "";
        if (!isAllBranches) {
            whereAllBranchesForCredit = whereAllBranchesForCredit + " AND cr.branch_id =" + sessionBean.getUser().getLastBranch().getId();
            whereAllBranchesForChequeBill = whereAllBranchesForChequeBill + " AND ch.branch_id =" + sessionBean.getUser().getLastBranch().getId();
            whereAllBranchesForInvoice = whereAllBranchesForInvoice + " AND ivc.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "(SELECT \n"
                  + "              	CASE WHEN cr.duedate < now()::date THEN 1 ELSE 2 END  AS expiredate,\n"
                  + "                  cr.currency_id AS currency_id,\n"
                  + "                 3 AS type,\n"
                  + "                 SUM(COALESCE(cr.remainingmoney,0)) remainingmoney\n"
                  + "              FROM\n"
                  + "               	finance.credit cr \n"
                  + "              WHERE \n"
                  + "               	cr.deleted = FALSE AND cr.is_paid = FALSE AND cr.is_cancel = FALSE AND cr.is_customer=TRUE\n" + whereAllBranchesForCredit + "\n"
                  + "               GROUP BY   cr.duedate ,cr.currency_id \n"
                  + "              )\n"
                  + "              UNION ALL\n"
                  + "              (SELECT \n"
                  + "              	CASE WHEN ch.expirydate < now()::date THEN 1 ELSE 2 END  AS expiredate,\n"
                  + "                   ch.currency_id AS currency_id,\n"
                  + "              	CASE WHEN ch.is_cheque = TRUE   THEN  1  ELSE 2  END AS type,\n"
                  + "                  SUM(COALESCE(ch.remainingmoney,0)*COALESCE(ch.exchangerate,1)) AS remainingmoney\n"
                  + "              FROM\n"
                  + "                	finance.chequebill ch \n"
                  + "              WHERE \n"
                  + "               	ch.deleted = FALSE AND ch.status_id  IN (31,33,34) AND ch.is_customer = true\n" + whereAllBranchesForChequeBill + " \n"
                  + "               GROUP BY ch.is_cheque ,ch.expirydate,ch.currency_id \n"
                  + "              )\n"
                  + "               UNION ALL\n"
                  + "              (    SELECT \n"
                  + "              						0  AS expiredate,\n"
                  + "                                   ivc.currency_id AS currency_id,\n"
                  + "                                   4 AS type,\n"
                  + "                                  COALESCE(SUM(ivc.remainingmoney*ivc.exchangerate),0) AS remainingmoney\n"
                  + "                             \n"
                  + "                     FROM finance.invoice ivc \n"
                  + "                     WHERE \n"
                  + "                     ivc.deleted = false AND ivc.status_id =28 AND  ivc.remainingmoney > 0  AND ivc.is_purchase=FALSE\n" + whereAllBranchesForInvoice + "\n"
                  + "                     GROUP BY ivc.currency_id\n"
                  + "                     )";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;

    }

    @Override
    public List<ChartItem> getPayments(boolean isAllBranches) {
        String whereAllBranchesForCredit = "", whereAllBranchesForChequeBill = "", whereAllBranchesForInvoice = "";
        if (!isAllBranches) {
            whereAllBranchesForCredit = whereAllBranchesForCredit + " AND cr.branch_id =" + sessionBean.getUser().getLastBranch().getId();
            whereAllBranchesForChequeBill = whereAllBranchesForChequeBill + " AND ch.branch_id =" + sessionBean.getUser().getLastBranch().getId();
            whereAllBranchesForInvoice = whereAllBranchesForInvoice + " AND ivc.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "    (SELECT\n"
                  + "                    	CASE WHEN cr.duedate < now()::date THEN 1 ELSE 2 END  AS expiredate,\n"
                  + "                        cr.currency_id AS currency_id,\n"
                  + "                       3 AS type,\n"
                  + "                       SUM(cr.remainingmoney) remainingmoney\n"
                  + "                    FROM\n"
                  + "                     	 finance.credit cr \n"
                  + "                    WHERE \n"
                  + "                     	cr.deleted = FALSE AND cr.is_paid = FALSE AND cr.is_cancel = FALSE AND cr.is_customer=FALSE\n" + whereAllBranchesForCredit + "\n"
                  + "                     GROUP BY   cr.duedate ,cr.currency_id \n"
                  + "        \n"
                  + "          )\n"
                  + "           UNION ALL\n"
                  + "                   (\n"
                  + "                   SELECT \n"
                  + "                   	CASE WHEN ch.expirydate < now()::date THEN 1 ELSE 2 END  AS expiredate,\n"
                  + "                        ch.currency_id AS currency_id,\n"
                  + "                   	CASE WHEN ch.is_cheque = TRUE   THEN  1  ELSE 2  END AS type,\n"
                  + "                       SUM(ch.remainingmoney*ch.exchangerate) AS remainingmoney\n"
                  + "                   FROM\n"
                  + "                     	finance.chequebill ch \n"
                  + "                   WHERE \n"
                  + "                    	ch.deleted = FALSE AND ch.status_id  IN (31,33,34) AND ch.is_customer = FALSE\n" + whereAllBranchesForChequeBill + "\n"
                  + "                    GROUP BY ch.is_cheque ,ch.expirydate,ch.currency_id \n"
                  + "                   )\n"
                  + "                    UNION ALL\n"
                  + "          (    SELECT \n"
                  + "          						0  AS expiredate,\n"
                  + "                               ivc.currency_id AS currency_id,\n"
                  + "                               4 AS type,\n"
                  + "                              COALESCE(SUM(ivc.remainingmoney*ivc.exchangerate),0) AS remainingmoney\n"
                  + "                         \n"
                  + "                 FROM finance.invoice ivc \n"
                  + "                 WHERE \n"
                  + "                 ivc.deleted = false AND ivc.status_id =28 AND  ivc.remainingmoney > 0  AND ivc.is_purchase=TRUE \n" + whereAllBranchesForInvoice
                  + "                 GROUP BY ivc.currency_id\n"
                  + "          )";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;

    }

    @Override
    public List<ChartItem> getWeeklyCashFlow(boolean isAllBranches) {
        String whereAllBranchesForCredit = "", whereAllBranchesForChequeBill = "", whereAllBranchesForInvoice = "";
        if (!isAllBranches) {
            whereAllBranchesForCredit = whereAllBranchesForCredit + " AND cr.branch_id =" + sessionBean.getUser().getLastBranch().getId();
            whereAllBranchesForChequeBill = whereAllBranchesForChequeBill + " AND ch.branch_id =" + sessionBean.getUser().getLastBranch().getId();
            whereAllBranchesForInvoice = whereAllBranchesForInvoice + " AND ivc.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        String sql = "            SELECT\n"
                  + "                    type.startdate AS typestartdate,\n"
                  + "                    type.enddate AS typeenddate,\n"
                  + "                	  type.chequebillEntry + type.creditEntry + type.invoiceEntry As totalentry,\n"
                  + "                    type.chequebillOut + type.invoiceOut + type.creditOut as totaloutflow\n"
                  + "                FROM\n"
                  + "                (\n"
                  + "                    SELECT \n"
                  + "                    	dt.startdate,\n"
                  + "                        dt.enddate,\n"
                  + "                        COALESCE((\n"
                  + "                          SELECT \n"
                  + "                             SUM(ch.remainingmoney*ch.exchangerate)\n"
                  + "                          FROM\n"
                  + "                            finance.chequebill ch \n"
                  + "                          WHERE \n"
                  + "                            ch.deleted = FALSE AND ch.status_id  IN (31,33,34) AND ch.is_customer = true and ch.expirydate BETWEEN dt.startdate AND dt.enddate\n" + whereAllBranchesForChequeBill + "\n"
                  + "                         ),0) \n"
                  + "                                  AS chequebillEntry,\n"
                  + "                       \n"
                  + "                    	COALESCE((\n"
                  + "                          SELECT \n"
                  + "                             SUM(ch.remainingmoney*ch.exchangerate)\n"
                  + "                          FROM\n"
                  + "                            finance.chequebill ch \n"
                  + "                          WHERE \n"
                  + "                            ch.deleted = FALSE AND ch.status_id  IN (31,33,34) AND ch.is_customer = false and ch.expirydate BETWEEN dt.startdate AND dt.enddate\n" + whereAllBranchesForChequeBill + " \n"
                  + "                         ),0) \n"
                  + "                                  AS chequebillOut,\n"
                  + "                                  \n"
                  + "                        COALESCE((   \n"
                  + "                       SELECT\n"
                  + "                       COALESCE(SUM(ivc.remainingmoney*ivc.exchangerate),0) AS remainingmoney\n"
                  + "                         FROM finance.invoice ivc \n"
                  + "                                WHERE \n"
                  + "                                ivc.deleted = false AND ivc.status_id =28 AND  ivc.remainingmoney > 0 and ivc.is_purchase=false  AND ivc.duedate BETWEEN dt.startdate AND dt.enddate" + whereAllBranchesForInvoice + "),0)\n"
                  + "                                  AS invoiceEntry,\n"
                  + "                                 \n"
                  + "                       COALESCE((   \n"
                  + "                       SELECT\n"
                  + "                       COALESCE(SUM(ivc.remainingmoney*ivc.exchangerate),0) AS remainingmoney\n"
                  + "                         FROM finance.invoice ivc \n"
                  + "                                WHERE \n"
                  + "                                ivc.deleted = false AND ivc.status_id =28 AND  ivc.remainingmoney > 0 and ivc.is_purchase=true  AND ivc.duedate BETWEEN dt.startdate AND dt.enddate" + whereAllBranchesForInvoice + "),0)\n"
                  + "                                  AS invoiceOut,\n"
                  + "                                  \n"
                  + "                        COALESCE((\n"
                  + "                         SELECT \n"
                  + "                           COALESCE( SUM(cr.remainingmoney*cr.exchangerate),0) remainingmoney\n"
                  + "                         FROM\n"
                  + "                          	 finance.credit cr \n"
                  + "                         WHERE \n"
                  + "                          	cr.deleted = FALSE AND cr.is_paid = FALSE AND cr.is_cancel = FALSE AND cr.is_customer=TRUE AND cr.duedate BETWEEN dt.startdate AND dt.enddate" + whereAllBranchesForCredit + "),0)\n"
                  + "                                 AS creditEntry,\n"
                  + "                                 \n"
                  + "                       COALESCE((\n"
                  + "                         SELECT \n"
                  + "                           COALESCE( SUM(cr.remainingmoney*cr.exchangerate),0) remainingmoney\n"
                  + "                         FROM\n"
                  + "                          	 finance.credit cr \n"
                  + "                         WHERE \n"
                  + "                          	cr.deleted = FALSE AND cr.is_paid = FALSE AND cr.is_cancel = FALSE AND cr.is_customer=FALSE AND cr.duedate BETWEEN dt.startdate AND dt.enddate" + whereAllBranchesForCredit + "),0)\n"
                  + "                                 AS creditOut\n"
                  + "                              \n"
                  + "                    \n"
                  + "                    FROM\n"
                  + "                    (\n"
                  + "                      SELECT\n"
                  + "                date_trunc('week', dates) AS startdate,\n"
                  + "                (date_trunc('week', dates) + interval '6 day') + (24*60*60-1) * interval '1 second' AS enddate\n"
                  + "            FROM \n"
                  + "                generate_series(now(), now() + interval '11 week', interval '1 day') AS dates\n"
                  + "            GROUP BY 1\n"
                  + "            ORDER BY 1\n"
                  + "            \n"
                  + "                    ) dt\n"
                  + "                ) type";

        List<ChartItem> chartItem = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItem;
    }

    @Override
    public List<ChartItem> getPricesVaryingProducts(boolean isAllBranches) {
        String where = "";
        String join = "";
        String field = "";
        String sub = "";
        if (!isAllBranches) {
            where = where + "  AND pl.branch_id  = " + sessionBean.getUser().getLastBranch().getId();
        } else {
            join = "INNER JOIN general.branch br ON(br.id=pl.branch_id AND br.deleted=FALSE) \n";
            field = "br.name as brsname,\n";
            sub = "tt.brsname,\n";
        }

        String sql = "WITH historytemp as\n"
                  + "(\n"
                  + "SELECT\n"
                  + "	hst2.tablename,  \n"
                  + "	hst2.newvalue,\n"
                  + "	hst2.row_id,\n"
                  + "	hst2.id,\n"
                  + "	hst2.columnname\n"
                  + "FROM \n"
                  + "	general.history hst2\n"
                  + "WHERE\n"
                  + "	 hst2.tablename = 'inventory.pricelistitem'   \n"
                  + "    AND hst2.columnname IN ('price','currency_id')\n"
                  + "GROUP by \n"
                  + "	hst2.tablename,  \n"
                  + "    hst2.newvalue,\n"
                  + "    hst2.id,\n"
                  + "    hst2.columnname\n"
                  + "ORDER BY \n"
                  + "	hst2.id DESC\n"
                  + ")\n"
                  + "SELECT\n"
                  + "    tt.stckid,\n"
                  + "    tt.stckname,\n"
                  + "    tt.hstprocessdate,\n"
                  + "     CASE WHEN tt.columnname = 'price'  AND tt.oldvalue = '' OR  tt.oldvalue IS NULL THEN '0'  WHEN tt.columnname = 'price' THEN tt.oldvalue::NUMERIC(18,4)\n"
                  + "    ELSE COALESCE(tt.lastprice::NUMERIC(18,4),tt.price) END as oldprice,\n"
                  + "    CASE WHEN tt.columnname = 'price'  AND tt.newvalue = ''  OR  tt.newvalue IS NULL THEN '0'  WHEN tt.columnname = 'price' THEN tt.newvalue::NUMERIC(18,4)\n"
                  + "    ELSE COALESCE(tt.lastprice::NUMERIC(18,4),tt.price) END as newprice,\n"
                  + "    CASE WHEN tt.columnname = 'currency_id' THEN tt.oldvalue::INTEGER ELSE COALESCE(REPLACE(tt.lastcurrency_id,null,'0')::INTEGER,tt.currency_id) END as oldcurrency_id,\n"
                  + "    CASE WHEN tt.columnname = 'currency_id' THEN tt.newvalue::INTEGER ELSE COALESCE(REPLACE(tt.lastcurrency_id,null,'0')::INTEGER,tt.currency_id) END as newcurrency_id,"
                  + "    tt.usname,\n"
                  + sub + " "
                  + "    tt.ussurname\n"
                  + "FROM(\n"
                  + "    SELECT\n"
                  + "            stck.id as stckid,\n"
                  + "            stck.name as stckname,\n"
                  + "            hst.processdate as hstprocessdate,\n"
                  + "            hst.columnname,\n"
                  + "            hst.oldvalue,\n"
                  + "            hst.newvalue,\n"
                  + "            (\n"
                  + "                SELECT\n"
                  + "                    hst2.newvalue\n"
                  + "                FROM historytemp hst2\n"
                  + "                WHERE hst2.row_id = hst.row_id\n"
                  + "                AND hst2.columnname = 'price'\n"
                  + "                AND hst2.id < hst.id\n"
                  + "                ORDER BY hst2.id DESC LIMIT 1\n"
                  + "            ) as lastprice,\n"
                  + "            (\n"
                  + "              SELECT\n"
                  + "                  hst2.newvalue\n"
                  + "                FROM historytemp hst2\n"
                  + "              WHERE hst2.row_id = hst.row_id\n"
                  + "              AND hst2.columnname = 'currency_id'\n"
                  + "              AND hst2.id < hst.id\n"
                  + "              ORDER BY hst2.id DESC LIMIT 1\n"
                  + "            ) as lastcurrency_id,\n"
                  + "            pli.price,\n"
                  + "            pli.currency_id,\n"
                  + "            us.name as usname,\n"
                  + field + " \n"
                  + "            us.surname as ussurname\n"
                  + "    FROM general.history hst\n"
                  + "    INNER JOIN inventory.pricelistitem pli ON(pli.id=hst.row_id AND pli.deleted=FALSE)\n"
                  + "    INNER JOIN inventory.pricelist pl ON(pl.id=pli.pricelist_id AND pl.deleted=FALSE " + where + " AND pl.is_purchase = FALSE)\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id=pli.stock_id AND stck.deleted=FALSE)\n"
                  + join + "\n"
                  + "    INNER JOIN general.userdata us ON (us.id = hst.userdata_id)\n"
                  + "    WHERE hst.tablename='inventory.pricelistitem'\n"
                  + "    AND (hst.columnname='price' OR hst.columnname='currency_id')\n"
                  + "    AND hst.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL'1 month'\n"
                  + "    ORDER BY hst.processdate DESC,stck.name,hst.columnname DESC\n"
                  + "LIMIT 10 \n"
                  + ") tt";

        List<ChartItem> chartItems = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItems;

    }

    @Override
    public List<ChartItem> getPurchasePriceHighProducts(boolean isAllBranches) {
        String where = "";
        String sql = "";

        if (isAllBranches) {
            sql = "SELECT * FROM\n"
                      + "(\n"
                      + "  SELECT \n"
                      + "    stck.id as stckid,\n"
                      + "    stck.name as itemname,\n"
                      + "    ppli.price as purcprice ,\n"
                      + "    spli.price as saleprice,\n"
                      + "    ppli.currency_id as purccurid,\n"
                      + "    spli.currency_id as salecurid,\n"
                      + "    br.name as brsname,\n"
                      + "    COALESCE((\n"
                      + "            SELECT \n"
                      + "                COALESCE(ex.buying,1)\n"
                      + "            FROM \n"
                      + "                finance.exchange ex \n"
                      + "            WHERE \n"
                      + "                ex.responsecurrency_id = ppli.currency_id \n"
                      + "                AND ex.currency_id = spli.currency_id\n"
                      + "                AND ex.deleted = FALSE \n"
                      + "            ORDER BY id DESC LIMIT 1 \n"
                      + "    ),1) as buying \n"
                      + "  FROM inventory.stock stck \n"
                      + "  INNER JOIN inventory.pricelistitem ppli ON(ppli.stock_id =stck.id AND ppli.deleted=FALSE )\n"
                      + "  LEFT JOIN inventory.pricelist prl1  ON(prl1.id = ppli.pricelist_id AND prl1.deleted = FALSE AND prl1.is_default=TRUE AND prl1.is_purchase=TRUE)\n"
                      + "  INNER JOIN general.branch br ON(br.id = prl1.branch_id )\n"
                      + "  LEFT JOIN inventory.pricelistitem spli ON(spli.stock_id =stck.id AND spli.deleted=FALSE AND spli.pricelist_id = (SELECT pl.id FROM inventory.pricelist pl WHERE pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.branch_id=br.id AND pl.deleted=FALSE))\n"
                      + "  WHERE stck.deleted=FALSE   " + where + "\n"
                      + ") as tt\n"
                      + " WHERE tt.purcprice*COALESCE(tt.buying,1)>= tt.saleprice*COALESCE(tt.buying,1)";
        } else {

            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                where = where + " AND si.is_valid = TRUE  ";
            } else {
                where = where + " AND stck.is_otherbranch=TRUE  ";
            }

            sql = " SELECT * FROM\n"
                      + "(\n"
                      + "  SELECT \n"
                      + "    stck.id as stckid,\n"
                      + "    stck.name as itemname,\n"
                      + "    ppli.price as purcprice ,\n"
                      + "    spli.price as saleprice,\n"
                      + "    ppli.currency_id as purccurid,\n"
                      + "    spli.currency_id as salecurid,\n"
                      + "    COALESCE((\n"
                      + "            SELECT \n"
                      + "                COALESCE(ex.buying,1)\n"
                      + "            FROM \n"
                      + "                finance.exchange ex \n"
                      + "            WHERE \n"
                      + "                ex.responsecurrency_id = ppli.currency_id \n"
                      + "                AND ex.currency_id = spli.currency_id\n"
                      + "                AND ex.deleted = FALSE \n"
                      + "            ORDER BY id DESC LIMIT 1 \n"
                      + "    ),1) as buying \n"
                      + "  FROM inventory.stock stck \n"
                      + "  LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id = " + sessionBean.getUser().getLastBranch().getId() + ")\n"
                      + "  INNER JOIN inventory.pricelistitem ppli ON(ppli.stock_id =stck.id AND ppli.deleted=FALSE AND ppli.pricelist_id = (SELECT id FROM inventory.pricelist pl WHERE pl.is_default=TRUE AND pl.is_purchase=TRUE AND pl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND pl.deleted=FALSE))\n"
                      + "  INNER JOIN inventory.pricelistitem spli ON(spli.stock_id =stck.id AND spli.deleted=FALSE AND spli.pricelist_id = (SELECT id FROM inventory.pricelist pl WHERE pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + "  AND pl.deleted=FALSE))\n"
                      + "  WHERE stck.deleted=FALSE " + where + "\n"
                      + ") as tt\n"
                      + " WHERE tt.purcprice*COALESCE(tt.buying,1)>= tt.saleprice*COALESCE(tt.buying,1)";
        }
        List<ChartItem> chartItems = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItems;
    }

    @Override
    public List<ChartItem> getProductProfitalibility(boolean isAllBranches) {
        String where = "";
        String sql = "";

        if (isAllBranches) {
            sql = sql = "--Güncel currency_id bazında döviz bilgilerini al\n"
                      + "WITH ranked_messages AS \n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "				COALESCE(ex.buying,1) as buying,\n"
                      + "				ex.currency_id,\n"
                      + "				ROW_NUMBER() OVER (PARTITION BY currency_id ORDER BY ex.id DESC) AS rn\n"
                      + "			FROM \n"
                      + "				finance.exchange ex \n"
                      + "			WHERE \n"
                      + "				ex.responsecurrency_id = 1\n"
                      + "                AND ex.deleted = FALSE \n"
                      + "			GROUP BY ex.currency_id,ex.buying,ex.id\n"
                      + "		)mm\n"
                      + "	WHERE \n"
                      + "    	mm.rn = 1\n"
                      + ")\n"
                      + "--Şube bazlı Fiyat Listesindeki ürün bilgilerini al\n"
                      + ", pricelistitemtemp AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		spli.pricelist_id,\n"
                      + "		spli.stock_id AS stckid,\n"
                      + "		brn.id AS branchid,\n"
                      + "		brn.name AS branchname,\n"
                      + "		spli.is_taxincluded AS salesistax,\n"
                      + "        spl.is_purchase as is_purchase,\n"
                      + "		CASE WHEN spli.is_taxincluded = TRUE THEN (COALESCE(spli.price,0)/(1+(COALESCE(taxgroup.rate,0)/100))) ELSE COALESCE(spli.price,0) END as price,\n"
                      + "		COALESCE((SELECT COALESCE(rms.buying,1) FROM ranked_messages  rms where  rms.currency_id = spli.currency_id LIMIT 1 ),1) AS buying \n"
                      + "	FROM \n"
                      + "    	inventory.pricelistitem spli\n"
                      + "		INNER JOIN inventory.pricelist spl ON(spl.id=spli.pricelist_id AND spl.deleted=FALSE AND spl.is_default=TRUE )\n"
                      + "		INNER JOIN general.branch brn ON(brn.id=spl.branch_id AND brn.deleted=FALSE)\n"
                      + "		LEFT JOIN (\n"
                      + "        			SELECT \n"
                      + "						txg.rate AS rate,\n"
                      + "         				stc.stock_id AS stock_id \n"
                      + "                    FROM \n"
                      + "                    	inventory.stock_taxgroup_con stc  \n"
                      + "                    	INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = FALSE)\n"
                      + "					WHERE \n"
                      + "                    	stc.deleted = FALSE\n"
                      + "						AND txg.type_id = 10 --kdv grubundan \n"
                      + "    					AND stc.is_purchase = FALSE\n"
                      + "        		 )taxgroup ON(taxgroup.stock_id = spli.stock_id)\n"
                      + "	WHERE  \n"
                      + "    	spli.deleted=FALSE\n"
                      + ")\n"
                      + "--Ürün ve Şube bazında son ve bir önceki fatura bilgilerini al\n"
                      + ",invoices AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	*\n"
                      + "	FROM \n"
                      + "    (\n"
                      + "    	SELECT\n"
                      + "        	invi.id  as inid,\n"
                      + "          	invi.stock_id,\n"
                      + "          	invi.c_time,\n"
                      + "          	inv.branch_id as invbranch,\n"
                      + "          	ROW_NUMBER() OVER (PARTITION BY invi.stock_id,inv.branch_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "          	CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                CASE WHEN invi.quantity >0 THEN \n"
                      + "                 (COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "               	ELSE  \n"
                      + "                 (COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "            ELSE\n"
                      + "            CASE WHEN invi.quantity >0 THEN \n"
                      + "              COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "              ELSE    \n"
                      + "              1 * COALESCE(invi.exchangerate,0) END\n"
                      + "            END AS unitprice\n"
                      + "		FROM \n"
                      + "			finance.invoiceitem invi\n"
                      + "            INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "        WHERE \n"
                      + "            inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "            AND invi.deleted = FALSE \n"
                      + "            AND inv.type_id  <> 27 \n"
                      + "            AND inv.status_id <> 30\n"
                      + "            ORDER BY invi.c_time DESC\n"
                      + "    )zz\n"
                      + "	WHERE  \n"
                      + "    	rnm in(1,2) \n"
                      + ")\n"
                      + "--Ürün ve Şube bazında son ve bir önceki satış bilgilerni al\n"
                      + ",sales AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "         		sli.id  as sliid,    \n"
                      + "         		sli.stock_id,\n"
                      + "         		sli.c_time,\n"
                      + "         		sl.branch_id AS slbranch,\n"
                      + "         		ROW_NUMBER() OVER (PARTITION BY sli.stock_id,sl.branch_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "        		CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "         			(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "        		ELSE\n"
                      + "            		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "        		END  AS unitprice\n"
                      + "  			FROM \n"
                      + "      			general.saleitem sli \n"
                      + "      			INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "            WHERE \n"
                      + "                sli.deleted = FALSE\n"
                      + "                AND sli.is_calcincluded =FALSE\n"
                      + "            ORDER BY sli.c_time DESC \n"
                      + "    	)ll \n"
                      + "	WHERE ll.rnm2 IN( 1,2)\n"
                      + ")\n"
                      + "--Tek fatura olanları(bir önceki fatura bilgisi olmayan kayıtlar) ve bir önceki fatura bilgisi olan kayıtları birleştir\n"
                      + ",lastinvoicepricetemp AS \n"
                      + "	(\n"
                      + "    	SELECT	\n"
                      + "    		inv1.unitprice as price,\n"
                      + "            inv1.stock_id,\n"
                      + "            inv1.c_time,\n"
                      + "        	inv1.inid,\n"
                      + "            inv1.invbranch\n"
                      + "		FROM  \n"
                      + "			invoices inv1\n"
                      + "			LEFT JOIN invoices inv2 on (inv1.stock_id = inv2.stock_id and inv1.invbranch = inv2.invbranch  AND inv2.rnm=2)\n"
                      + "		WHERE \n"
                      + "        	inv1.rnm = 1 AND inv2.stock_id is null\n"
                      + "    	\n"
                      + "        UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "            COALESCE(tt.unitprice,0) as price,\n"
                      + "            tt.stock_id,\n"
                      + "            tt.c_time,\n"
                      + "            tt.inid,\n"
                      + "            tt.invbranch\n"
                      + "    	FROM \n"
                      + "        	(\n"
                      + "              SELECT \n"
                      + "                  * \n"
                      + "              FROM \n"
                      + "                  (\n"
                      + "                    SELECT\n"
                      + "                        invi.id  as inid,\n"
                      + "                        invi.stock_id,\n"
                      + "                        invi.c_time,\n"
                      + "                        inv.branch_id as invbranch,\n"
                      + "                        ROW_NUMBER() OVER (PARTITION BY invi.stock_id,inv.branch_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "                        CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                              CASE WHEN invi.quantity >0 THEN \n"
                      + "                               (COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "                             ELSE  \n"
                      + "                               (COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "                          ELSE\n"
                      + "                          CASE WHEN invi.quantity >0 THEN \n"
                      + "                            COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "                            ELSE    \n"
                      + "                            1 * COALESCE(invi.exchangerate,0) END\n"
                      + "                          END AS unitprice\n"
                      + "                        FROM \n"
                      + "                            finance.invoiceitem invi\n"
                      + "                            INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "                        WHERE \n"
                      + "                            inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "                            AND invi.deleted = FALSE \n"
                      + "                            AND inv.type_id  <> 27 \n"
                      + "                            AND inv.status_id <> 30\n"
                      + "                    	ORDER BY invi.c_time DESC\n"
                      + "                    )mm \n"
                      + "              WHERE \n"
                      + "                  mm.rnm = 2\n"
                      + "                                                	\n"
                      + "			)tt\n"
                      + "	)\n"
                      + "    --Tek satış olanları(bir önceki satış bilgisi olmayan kayıtlar) ve bir önceki satış bilgisi olan kayıtları birleştir\n"
                      + "    ,lastsalepricetemp AS \n"
                      + "	(\n"
                      + "		SELECT\n"
                      + "    		sl1.unitprice,\n"
                      + "            sl1.stock_id,\n"
                      + "            sl1.c_time,\n"
                      + "        	sl1.sliid,\n"
                      + "            sl1.slbranch\n"
                      + "    	FROM  \n"
                      + "			sales sl1\n"
                      + "			LEFT JOIN sales sl2 on (sl1.stock_id = sl2.stock_id and sl1.slbranch = sl2.slbranch  AND sl2.rnm2=2)\n"
                      + "		WHERE \n"
                      + "        	sl1.rnm2 = 1 AND sl2.stock_id is null\n"
                      + "    	\n"
                      + "        UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "            COALESCE(tt.unitprice,0) as unitprice ,\n"
                      + "            tt.stock_id,\n"
                      + "            tt.c_time,\n"
                      + "            tt.sliid,\n"
                      + "            tt.slbranch\n"
                      + "		FROM (\n"
                      + "            	SELECT \n"
                      + "                	* \n"
                      + "            	FROM \n"
                      + "                	(\n"
                      + "                		SELECT \n"
                      + "                           	sli.id  as sliid,    \n"
                      + "                           	sli.stock_id,\n"
                      + "                           	sli.c_time,\n"
                      + "                           	sl.branch_id as slbranch,\n"
                      + "                           	ROW_NUMBER() OVER (PARTITION BY sli.stock_id,sl.branch_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "                           	CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "                           		(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "                           	ELSE\n"
                      + "                           		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "                           	END  AS unitprice\n"
                      + "                        FROM \n"
                      + "                        	general.saleitem sli \n"
                      + "                        	INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "                        WHERE \n"
                      + "                        	sli.deleted = FALSE\n"
                      + "                        	AND sli.is_calcincluded =FALSE\n"
                      + "                        ORDER BY sli.c_time DESC \n"
                      + "                    )ll \n"
                      + "		WHERE \n"
                      + "        	ll.rnm2 = 2\n"
                      + "            \n"
                      + "            ) tt\n"
                      + "	)\n"
                      + "SELECT * FROM \n"
                      + "	(\n"
                      + "      SELECT	\n"
                      + "      		tb1.id stckid,\n"
                      + "      		tb1.name as name, \n"
                      + "      		tb1.brsname as brsname,                              \n"
                      + "         	CASE WHEN COALESCE(tb1.lastinvoiceprice2,0) = 0 THEN 0\n"
                      + "          	ELSE\n"
                      + "        		(COALESCE(tb1.lastsaleprice2,0)-COALESCE(tb1.lastinvoiceprice2,0))/COALESCE(tb1.lastinvoiceprice2,0)*100 \n"
                      + "          	END AS onceki_kar,\n"
                      + "                                        \n"
                      + "          	CASE WHEN COALESCE(tb1.lastinvoiceprice,0) = 0 THEN 0\n"
                      + "          	ELSE\n"
                      + "            (COALESCE(tb1.lastsaleprice,0)-COALESCE(tb1.lastinvoiceprice,0))/COALESCE(tb1.lastinvoiceprice,0)*100 \n"
                      + "          	END AS simdiki_kar                               \n"
                      + "      FROM\n"
                      + "      		(\n"
                      + "      			SELECT\n"
                      + "                	stck.name,\n"
                      + "          			stck.id,\n"
                      + "          			br.name as brsname,\n"
                      + "          			br.id AS branchid,\n"
                      + "         			COALESCE((SELECT price FROM	lastinvoicepricetemp WHERE stock_id =stck.id AND invbranch = br.id),0) as lastinvoiceprice2,\n"
                      + "          			COALESCE((SELECT unitprice FROM lastsalepricetemp WHERE stock_id = stck.id AND slbranch= br.id),0) AS lastsaleprice2, \n"
                      + "          			COALESCE((salepricelisttable.price * salepricelisttable.buying),0) AS lastsaleprice,\n"
                      + "      				COALESCE((purchasepricelisttable.price * purchasepricelisttable.buying),0) AS lastinvoiceprice \n"
                      + "          		FROM \n"
                      + "                    inventory.stock stck \n"
                      + "                    INNER JOIN inventory.stockinfo stcki ON(stck.id=stcki.stock_id AND stcki.deleted=FALSE)\n"
                      + "                    INNER JOIN general.branch br ON (br.id=stcki.branch_id AND br.deleted=FALSE)\n"
                      + "                  \n"
                      + "                    LEFT JOIN (\n"
                      + "                    			SELECT \n"
                      + "                                	* \n"
                      + "                                FROM  \n"
                      + "                                	pricelistitemtemp spli \n"
                      + "                                WHERE \n"
                      + "                                	spli.is_purchase = FALSE\n"
                      + "                    		   )salepricelisttable ON(salepricelisttable.stckid =stck.id AND salepricelisttable.branchid= br.id)\n"
                      + "          			LEFT JOIN (\n"
                      + "                    			SELECT \n"
                      + "                                	* \n"
                      + "                                FROM  \n"
                      + "                                	pricelistitemtemp spli \n"
                      + "                                WHERE  \n"
                      + "                                	spli.is_purchase = TRUE\n"
                      + "            		   		  )purchasepricelisttable ON(purchasepricelisttable.stckid =stck.id AND purchasepricelisttable.branchid= br.id)\n"
                      + "          		WHERE \n"
                      + "                	stck.deleted=FALSE  \n"
                      + where + " \n"
                      + "          		ORDER BY stck.name \n"
                      + "            ) tb1 \n"
                      + "	) as gprice\n"
                      + "	WHERE \n"
                      + "    	gprice.onceki_kar <> gprice.simdiki_kar\n"
                      + " LIMIT 5 ";

        } else {

            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                where = where + " AND si.is_valid = TRUE  ";
            } else {
                where = where + " AND stck.is_otherbranch=TRUE  ";
            }
            sql = "--Fiyat Listesi Bilgilerini Al.\n"
                      + "WITH pricelisttemp as\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		pl.id,\n"
                      + "        pl.is_purchase\n"
                      + "	FROM \n"
                      + "    	inventory.pricelist pl \n"
                      + "	WHERE \n"
                      + "    	pl.is_default=TRUE \n"
                      + "        AND pl.branch_id= " + sessionBean.getUser().getLastBranch().getId() + "\n"
                      + "        AND pl.deleted=FALSE\n"
                      + ")\n"
                      + "--Güncel currency_id bazında döviz bilgilerini al\n"
                      + ",ranked_messages AS (\n"
                      + "	SELECT \n"
                      + "		COALESCE(ex.buying,1) as buying,\n"
                      + "		ex.currency_id,\n"
                      + "		ROW_NUMBER() OVER (PARTITION BY currency_id ORDER BY ex.id DESC) AS rn\n"
                      + "	FROM \n"
                      + "		finance.exchange ex \n"
                      + "	WHERE \n"
                      + "		ex.responsecurrency_id = 1 \n"
                      + "        AND ex.deleted = FALSE \n"
                      + "	GROUP BY ex.currency_id,ex.buying,ex.id\n"
                      + ")\n"
                      + "--Fiyat Listesindeki ürün bilgilerini al\n"
                      + ",pricelistitemtemp as\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		spli.pricelist_id,\n"
                      + "        spli.stock_id as stckid,\n"
                      + "        spli.is_taxincluded as salesistax,\n"
                      + "        CASE WHEN spli.is_taxincluded = TRUE THEN (COALESCE(spli.price,0)/(1+(COALESCE(taxgroup.rate,0)/100))) ELSE COALESCE(spli.price,0) END as price,\n"
                      + "        COALESCE(\n"
                      + "          			( \n"
                      + "                    	SELECT \n"
                      + "                        	COALESCE(rms.buying,1) \n"
                      + "                        FROM \n"
                      + "                  			ranked_messages  rms \n"
                      + "                        WHERE \n"
                      + "                        	rms.rn = 1 \n"
                      + "                            AND rms.currency_id = spli.currency_id\n"
                      + "                   		ORDER BY id DESC LIMIT 1 \n"
                      + "           			),1\n"
                      + "        	    ) as buying \n"
                      + "	FROM \n"
                      + "    	inventory.pricelistitem spli\n"
                      + "        LEFT JOIN (	SELECT \n"
                      + "          				txg.rate AS rate,\n"
                      + "                   		stc.stock_id AS stock_id \n"
                      + "      				FROM \n"
                      + "                    	inventory.stock_taxgroup_con stc  \n"
                      + "  						INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                      + "      				WHERE \n"
                      + "                    	stc.deleted = false\n"
                      + "          				AND txg.type_id = 10 --kdv grubundan \n"
                      + "              			AND stc.is_purchase = FALSE\n"
                      + "        		 )taxgroup ON(taxgroup.stock_id = spli.stock_id)\n"
                      + "	WHERE  \n"
                      + "    	spli.deleted = FALSE\n"
                      + "        AND spli.pricelist_id IN (SELECT plt.id FROM pricelisttemp plt)\n"
                      + ")\n"
                      + "--Ürün bazında son ve bir önceki fatura bilgilerini al\n"
                      + ",invoices AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	* \n"
                      + "    FROM \n"
                      + "		(\n"
                      + "        	SELECT\n"
                      + "          		invi.id  AS inid,\n"
                      + "          		invi.stock_id,\n"
                      + "          		invi.c_time,\n"
                      + "          		ROW_NUMBER() OVER (PARTITION BY invi.stock_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "          		CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                	CASE WHEN invi.quantity >0 THEN \n"
                      + "                 		(COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "               		ELSE  \n"
                      + "                 		(COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "            	ELSE\n"
                      + "            		CASE WHEN invi.quantity >0 THEN \n"
                      + "              			COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "              		ELSE 1 * COALESCE(invi.exchangerate,0) END\n"
                      + "            	END AS unitprice\n"
                      + "          	FROM \n"
                      + "              	finance.invoiceitem invi\n"
                      + "              	INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "          	WHERE \n"
                      + "              	inv.is_purchase = TRUE \n"
                      + "                AND invi.is_calcincluded = FALSE\n"
                      + "              	AND invi.deleted = FALSE \n"
                      + "              	AND inv.type_id  <> 27 \n"
                      + "              	AND inv.status_id <> 30\n"
                      + "            ORDER BY invi.c_time DESC\n"
                      + "    \n"
                      + "    )xx\n"
                      + "	WHERE \n"
                      + "    	rnm in (1,2)\n"
                      + ")\n"
                      + "--Ürün bazında son ve bir önceki satış bilgilerni al\n"
                      + ",sales AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "         		sli.id  as sliid,    \n"
                      + "         		sli.stock_id,\n"
                      + "         		sli.c_time,\n"
                      + "         		ROW_NUMBER() OVER (PARTITION BY sli.stock_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "        		CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "         			(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "        		ELSE\n"
                      + "            		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "        		END  AS unitprice\n"
                      + "  			FROM \n"
                      + "      			general.saleitem sli \n"
                      + "      			INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "  			WHERE \n"
                      + "      			sli.deleted = FALSE\n"
                      + "      			AND sli.is_calcincluded =FALSE\n"
                      + "  			ORDER BY sli.c_time DESC \n"
                      + "        )ww \n"
                      + "	WHERE \n"
                      + "    	ww.rnm2 IN (1,2)\n"
                      + ")\n"
                      + "--Tek fatura olanları(bir önceki fatura bilgisi olmayan kayıtlar) ve bir önceki fatura bilgisi olan kayıtları birleştir\n"
                      + ",lastinvoicepricetemp AS \n"
                      + "(\n"
                      + "		SELECT	\n"
                      + "			inv1.unitprice as price,\n"
                      + "            inv1.stock_id,\n"
                      + "            inv1.c_time,\n"
                      + "        	inv1.inid\n"
                      + "		FROM  \n"
                      + "			invoices inv1\n"
                      + "			LEFT JOIN invoices inv2 on (inv1.stock_id = inv2.stock_id  AND inv2.rnm=2)\n"
                      + "		WHERE \n"
                      + "        	inv1.rnm = 1 AND inv2.stock_id is null\n"
                      + "    \n"
                      + "		UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "    		COALESCE(tt.unitprice,0) as price,\n"
                      + "    		tt.stock_id,\n"
                      + "    		tt.c_time,\n"
                      + "    		tt.inid\n"
                      + "        FROM \n"
                      + "        	(\n"
                      + "        		SELECT \n"
                      + "            		* \n"
                      + "        		FROM \n"
                      + "            		(\n"
                      + "            			SELECT\n"
                      + "                  			invi.id  as inid,\n"
                      + "                  			invi.stock_id,\n"
                      + "                  			invi.c_time,\n"
                      + "                  			ROW_NUMBER() OVER (PARTITION BY invi.stock_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "                  			CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                        		CASE WHEN invi.quantity >0 THEN \n"
                      + "                         			(COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "                       			ELSE  \n"
                      + "                         			(COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "                    		ELSE\n"
                      + "                    		CASE WHEN invi.quantity >0 THEN \n"
                      + "                      				COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "                      		ELSE 1 * COALESCE(invi.exchangerate,0) END\n"
                      + "                    		END AS unitprice\n"
                      + "                  		FROM \n"
                      + "                      		finance.invoiceitem invi\n"
                      + "                      		INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "                  		WHERE \n"
                      + "                      		inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "                      		AND invi.deleted = FALSE \n"
                      + "                      		AND inv.type_id  <> 27 \n"
                      + "                      		AND inv.status_id <> 30\n"
                      + "						ORDER BY invi.c_time DESC\n"
                      + "             	 )mm \n"
                      + "		WHERE \n"
                      + "			mm.rnm = 2\n"
                      + "        ) tt                                            \n"
                      + ")\n"
                      + "--Tek satış olanları(bir önceki satış bilgisi olmayan kayıtlar) ve bir önceki satış bilgisi olan kayıtları birleştir\n"
                      + ",lastsalepricetemp AS \n"
                      + "(\n"
                      + "	SELECT\n"
                      + "		sl1.unitprice,\n"
                      + "		sl1.stock_id,\n"
                      + "		sl1.c_time,\n"
                      + "		sl1.sliid\n"
                      + "	FROM  \n"
                      + "		sales sl1\n"
                      + "		LEFT JOIN sales sl2 on (sl1.stock_id = sl2.stock_id   AND sl2.rnm2=2)\n"
                      + "	WHERE \n"
                      + "		sl1.rnm2 = 1 \n"
                      + "        AND sl2.stock_id IS NULL \n"
                      + "        \n"
                      + "	UNION ALL\n"
                      + "    \n"
                      + "    SELECT\n"
                      + "		COALESCE(tt.unitprice,0) as unitprice ,\n"
                      + "     	tt.stock_id,\n"
                      + "     	tt.c_time,\n"
                      + "     	tt.sliid\n"
                      + "    FROM \n"
                      + "    	(\n"
                      + "			SELECT \n"
                      + "				* \n"
                      + "      		FROM \n"
                      + "          		(\n"
                      + "          			SELECT \n"
                      + "       					sli.id  as sliid,    \n"
                      + "       					sli.stock_id,\n"
                      + "       					sli.c_time,\n"
                      + "       					ROW_NUMBER() OVER (PARTITION BY sli.stock_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "      					CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN (COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "      					ELSE COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "      					END  AS unitprice\n"
                      + "					FROM \n"
                      + "    					general.saleitem sli \n"
                      + "    					INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "					WHERE \n"
                      + "    					sli.deleted = FALSE\n"
                      + "    					AND sli.is_calcincluded =FALSE\n"
                      + "					ORDER BY sli.c_time DESC \n"
                      + "          		)ll \n"
                      + "            WHERE \n"
                      + "            	ll.rnm2 = 2\n"
                      + "        ) tt\n"
                      + ")\n"
                      + "\n"
                      + "SELECT \n"
                      + "	* \n"
                      + "FROM\n"
                      + "	(\n"
                      + "		SELECT	\n"
                      + "			tb1.id stckid,\n"
                      + "			tb1.name as name,                               \n"
                      + "      		CASE WHEN COALESCE(tb1.lastinvoiceprice2,0) = 0 THEN 0 ELSE (COALESCE(tb1.lastsaleprice2,0)-COALESCE(tb1.lastinvoiceprice2,0))/COALESCE(tb1.lastinvoiceprice2,0)*100 END AS onceki_kar,\n"
                      + "            CASE WHEN COALESCE(tb1.lastinvoiceprice,0) = 0 THEN 0 ELSE (COALESCE(tb1.lastsaleprice,0)-COALESCE(tb1.lastinvoiceprice,0))/COALESCE(tb1.lastinvoiceprice,0)*100 END AS simdiki_kar                               \n"
                      + "		FROM\n"
                      + "        	(\n"
                      + "				SELECT\n"
                      + "                	stck.name,\n"
                      + "      				stck.id,\n"
                      + "                    COALESCE((SELECT price FROM lastinvoicepricetemp WHERE stock_id =stck.id),0) AS lastinvoiceprice2,\n"
                      + "      				COALESCE((SELECT unitprice FROM lastsalepricetemp WHERE stock_id = stck.id),0) AS lastsaleprice2, \n"
                      + "      				COALESCE((salepricelisttable.price * salepricelisttable.buying),0) AS lastsaleprice,\n"
                      + "					COALESCE((purchasepricelisttable.price * purchasepricelisttable.buying),0) AS lastinvoiceprice \n"
                      + "      			FROM \n"
                      + "                	inventory.stock stck\n"
                      + "                    LEFT JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted=FALSE AND si.branch_id = " + sessionBean.getUser().getLastBranch().getId() + ")\n"
                      + "                    LEFT JOIN (\n"
                      + "                    				SELECT * FROM  pricelistitemtemp spli WHERE spli.pricelist_id = (SELECT plt.id FROM  pricelisttemp plt where plt.is_purchase = FALSE)\n"
                      + "        			  		   )salepricelisttable ON(salepricelisttable.stckid =stck.id)\n"
                      + "      				LEFT JOIN ( \n"
                      + "                      				SELECT * FROM  pricelistitemtemp spli where  spli.pricelist_id = (SELECT plt.id FROM  pricelisttemp plt where plt.is_purchase = TRUE)\n"
                      + "        			           ) purchasepricelisttable ON(purchasepricelisttable.stckid =stck.id)\n"
                      + "      			WHERE \n"
                      + "                	stck.deleted=FALSE  \n"
                      + where + " \n"
                      + "      			ORDER BY stck.name \n"
                      + "            ) tb1 \n"
                      + "	) AS gprice\n"
                      + "WHERE \n"
                      + "	gprice.onceki_kar <> gprice.simdiki_kar"
                      + "  LIMIT 5  ";

        }
        List<ChartItem> chartItems = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItems;
    }

    @Override
    public List<ChartItem> getStationBySalesForWashingMachicne(Date beginDate, Date endDate, boolean isAllBranches) {
        Object[] param = null;
        String sql = "";
        String where = "";
        String whereUnit = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        if (!isAllBranches) {
            where = where + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereUnit = whereUnit + " AND unt.centerunit_id IS NOT NULL  ";
        } else {
            whereUnit = whereUnit + " AND unt.is_otherbranch=TRUE  ";
        }

        sql = "SELECT \n"
                  + "            sl.stock_id as slstock_id,\n"
                  + "            stck.name as stckname,\n"
                  + "            COUNT(sl.id) as quantitiy,\n"
                  + "            SUM(COALESCE(sl.operationtime,0)) as sloperationtime,\n"
                  + "            ROUND(SUM(COALESCE(sl.totalmoney,0)),2) as income,\n"
                  + "            ROUND(SUM(((COALESCE(sl.operationamount,0)))*COALESCE(sl.expenseunitprice,0)),2) as expense,\n"
                  + "            ROUND(COALESCE( SUM(COALESCE(sl.totalmoney,0))-SUM(((COALESCE(sl.operationamount,0)))*COALESCE(sl.expenseunitprice,0)),0),2) as winngins,\n"
                  + "            COUNT(sl.id) as electricquantitiy,\n"
                  + "            ROUND(SUM(COALESCE(sl.electricamount,0)),2) as elecquantity,\n"
                  + "            SUM(COALESCE(sl.operationtime,0)) as slelectricoperationtime,\n"
                  + "            ROUND(SUM(((COALESCE(sl.electricamount,0)))*COALESCE(sl.electricunitprice,0)),2) as electricexpense,\n"
                  + "            ROUND(SUM(COALESCE(sl.operationamount,0)),2) as waste,\n"
                  + "            ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN 1 ELSE 0 END),2) as waterworkingamount,\n"
                  + "	         ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.operationtime,0) ELSE 0 END),2) as waterworkingtime,\n"
                  + "            ROUND(SUM(CASE WHEN COALESCE(sl.wateramount,0)>0 THEN COALESCE(sl.wateramount,0) ELSE 0 END),2) as waterwase,\n"
                  + "            ROUND(SUM(((COALESCE(sl.wateramount,0)))*COALESCE(sl.waterunitprice,0)),2) as waterexpense,\n"
                  + "            stck.unit_id as stckuntid,\n"
                  + "            unt.sortname as untsrotname\n"
                  + "            FROM wms.sale sl \n"
                  + "            INNER JOIN inventory.stock stck  ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                  + "            INNER JOIN general.unit unt  ON(unt.id=stck.unit_id AND unt.deleted=FALSE " + whereUnit + ")\n"
                  + "            WHERE sl.deleted=FALSE \n" + where + "\n";

        if (endDate != null) {
            sql += " AND sl.saledatetime BETWEEN ? AND ? ";
            param = new Object[]{beginDate, endDate};
        } else {
            sql += "    AND sl.saledatetime > ? \n";
            param = new Object[]{beginDate};
        }
        sql += "GROUP BY sl.stock_id,stck.name,stck.unit_id,unt.sortname\n";
        //  System.out.println("---Arraysss--" + Arrays.toString(param));
        //  System.out.println("****sql***" + sql);
        
        
        
        System.out.println("----yıkama karlılığı sql---"+sql);
        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;

    }

    @Override
    public List<ChartItem> getWashingSalesByQuantity(Date beginDate, Date endDate, boolean isAllBranches) {
        Object[] param = null;
        String sql = "";
        String where = "";
        String whereUnit = "";
        if (!isAllBranches) {
            where = where + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereUnit = whereUnit + " AND unt.centerunit_id IS NOT NULL  ";
        } else {
            whereUnit = whereUnit + " AND unt.is_otherbranch=TRUE  ";
        }

        sql = "SELECT \n"
                  + "            sl.stock_id as slstock_id,\n"
                  + "            stck.name as stckname,\n"
                  + "            COUNT(sl.id) as quantitiy,\n"
                  + "            unt.sortname as untsrotname\n"
                  + "            FROM wms.sale sl \n"
                  + "            INNER JOIN inventory.stock stck  ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                  + "            INNER JOIN general.unit unt  ON(unt.id=stck.unit_id AND unt.deleted=FALSE " + whereUnit + ")\n"
                  + "            WHERE sl.deleted=FALSE \n" + where + "\n";

        if (endDate != null) {
            sql += " AND sl.saledatetime BETWEEN ? AND ? ";
            param = new Object[]{beginDate, endDate};
        } else {
            sql += "    AND sl.saledatetime > ? \n";
            param = new Object[]{beginDate};
        }
        sql += "GROUP BY sl.stock_id,stck.name,stck.unit_id,unt.sortname\n";

        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;
    }

    @Override
    public List<ChartItem> getWashingSalesByTurnover(Date beginDate, Date endDate, boolean isAllBranches) {
        Object[] param = null;
        String sql = "";
        String where = "", whereUnit = "";

        if (!isAllBranches) {
            where = where + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereUnit = whereUnit + " AND unt.centerunit_id IS NOT NULL  ";
        } else {
            whereUnit = whereUnit + " AND unt.is_otherbranch=TRUE  ";
        }

        sql = "SELECT \n"
                  + "            sl.stock_id as slstock_id,\n"
                  + "            stck.name as stckname,\n"
                  + "            ROUND(SUM(COALESCE(sl.totalmoney,0)),2) as income\n"
                  + "            FROM wms.sale sl \n"
                  + "            INNER JOIN inventory.stock stck  ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                  + "            INNER JOIN general.unit unt  ON(unt.id=stck.unit_id AND unt.deleted=FALSE " + whereUnit + ")\n"
                  + "            WHERE sl.deleted=FALSE \n" + where + "\n";

        if (endDate != null) {
            sql += " AND sl.saledatetime BETWEEN ? AND ? ";
            param = new Object[]{beginDate, endDate};
        } else {
            sql += "    AND sl.saledatetime > ? \n";
            param = new Object[]{beginDate};
        }
        sql += "GROUP BY sl.stock_id,stck.name,stck.unit_id,unt.sortname\n";

        List<ChartItem> result = getJdbcTemplate().query(sql, param, new ChartItemMapper());
        return result;
    }

    @Override
    public List<ChartItem> getWashingSales(Date beginDate, Date endDate, int washingSales, boolean isAllBranches) {
        Object[] param = null;
        String sql = "";
        String select = "";
        String where = "";
        String groupBy = "";
        String whereUnit = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        if (!isAllBranches) {
            where = where + " AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId();
        }

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereUnit = whereUnit + " AND unt.centerunit_id IS NOT NULL  ";
        } else {
            whereUnit = whereUnit + " AND unt.is_otherbranch=TRUE  ";
        }

        if (washingSales == 3) {
            select = " DATE_PART('day', sl.saledatetime::TIMESTAMP) AS groupName";
            where = where + " AND sl.saledatetime BETWEEN '" + dateFormat.format(beginDate) + "' AND NOW()";
            groupBy = "DATE_PART('day', sl.saledatetime::TIMESTAMP)";

            sql = "SELECT \n"
                      + select + ",\n"
                      + "            sl.stock_id as stckid,\n"
                      + "            stck.name as stckname,\n"
                      + "            COUNT(sl.id) as quantitiy\n"
                      + "            FROM wms.sale sl \n"
                      + "            INNER JOIN inventory.stock stck  ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                      + "            INNER JOIN general.unit unt  ON(unt.id=stck.unit_id AND unt.deleted=FALSE" + whereUnit + ")\n"
                      + "            WHERE sl.deleted=FALSE " + where + "\n";

            sql += "GROUP BY sl.stock_id,stck.name ," + groupBy + " ";
        } else {
            select = "DATE_PART('dow', sl.saledatetime::TIMESTAMP) AS groupName";
            where = where + " AND sl.saledatetime  BETWEEN '" + dateFormat.format(beginDate) + "' AND NOW()";
            groupBy = "DATE_PART('dow', sl.saledatetime::TIMESTAMP)";

            sql = "SELECT \n"
                      + select + ",\n"
                      + "            sl.stock_id as stckid,\n"
                      + "            stck.name as stckname,\n"
                      + "            COUNT(sl.id) as quantitiy\n"
                      + "            FROM wms.sale sl \n"
                      + "            INNER JOIN inventory.stock stck  ON(sl.stock_id=stck.id AND stck.deleted=FALSE)\n"
                      + "            INNER JOIN general.unit unt  ON(unt.id=stck.unit_id AND unt.deleted=FALSE " + whereUnit + ")\n"
                      + "            WHERE sl.deleted=FALSE " + where + "\n";

            sql += "GROUP BY sl.stock_id,stck.name ," + groupBy + " ";
        }

        List<ChartItem> result = getJdbcTemplate().query(sql, new ChartItemMapper());
        return result;
    }

    @Override
    public List<ChartItem> getLazyPricesVaryingProductsList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, boolean isAllBranches) {
        String where = "";
        String join = "";
        String field = "";
        String sub = "";
        if (!isAllBranches) {
            where = where + "  AND pl.branch_id  = " + sessionBean.getUser().getLastBranch().getId();
        } else {
            join = "INNER JOIN general.branch br ON(br.id=pl.branch_id AND br.deleted=FALSE) \n";
            field = "br.name as brsname,\n";
            sub = "tt.brsname,\n";
        }

        String sql = "WITH historytemp as\n"
                  + "(\n"
                  + "SELECT\n"
                  + "	hst2.tablename,  \n"
                  + "	hst2.newvalue,\n"
                  + "	hst2.row_id,\n"
                  + "	hst2.id,\n"
                  + "	hst2.columnname\n"
                  + "FROM \n"
                  + "	general.history hst2\n"
                  + "WHERE\n"
                  + "	 hst2.tablename = 'inventory.pricelistitem'   \n"
                  + "    AND hst2.columnname IN ('price','currency_id')\n"
                  + "GROUP by \n"
                  + "	hst2.tablename,  \n"
                  + "    hst2.newvalue,\n"
                  + "    hst2.id,\n"
                  + "    hst2.columnname\n"
                  + "ORDER BY \n"
                  + "	hst2.id DESC\n"
                  + ")\n"
                  + "SELECT\n"
                  + "    tt.stckid,\n"
                  + "    tt.stckname,\n"
                  + "    tt.hstprocessdate,\n"
                  + "     CASE WHEN tt.columnname = 'price'  AND tt.oldvalue = '' OR  tt.oldvalue IS NULL THEN '0'  WHEN tt.columnname = 'price' THEN tt.oldvalue::NUMERIC(18,4)\n"
                  + "    ELSE COALESCE(tt.lastprice::NUMERIC(18,4),tt.price) END as oldprice,\n"
                  + "    CASE WHEN tt.columnname = 'price'  AND tt.newvalue = ''  OR  tt.newvalue IS NULL THEN '0'  WHEN tt.columnname = 'price' THEN tt.newvalue::NUMERIC(18,4)\n"
                  + "    ELSE COALESCE(tt.lastprice::NUMERIC(18,4),tt.price) END as newprice,\n"
                  + "    CASE WHEN tt.columnname = 'currency_id' THEN tt.oldvalue::INTEGER ELSE COALESCE(REPLACE(tt.lastcurrency_id,null,'0')::INTEGER,tt.currency_id) END as oldcurrency_id,\n"
                  + "    CASE WHEN tt.columnname = 'currency_id' THEN tt.newvalue::INTEGER ELSE COALESCE(REPLACE(tt.lastcurrency_id,null,'0')::INTEGER,tt.currency_id) END as newcurrency_id,"
                  + "    tt.usname,\n"
                  + sub + " "
                  + "    tt.ussurname\n"
                  + "FROM(\n"
                  + "    SELECT\n"
                  + "            stck.id as stckid,\n"
                  + "            stck.name as stckname,\n"
                  + "            hst.processdate as hstprocessdate,\n"
                  + "            hst.columnname,\n"
                  + "            hst.oldvalue,\n"
                  + "            hst.newvalue,\n"
                  + "            (\n"
                  + "                SELECT\n"
                  + "                    hst2.newvalue\n"
                  + "                FROM historytemp hst2\n"
                  + "                WHERE hst2.row_id = hst.row_id\n"
                  + "                AND hst2.columnname = 'price'\n"
                  + "                AND hst2.id < hst.id\n"
                  + "                ORDER BY hst2.id DESC LIMIT 1\n"
                  + "            ) as lastprice,\n"
                  + "            (\n"
                  + "              SELECT\n"
                  + "                  hst2.newvalue\n"
                  + "                FROM historytemp hst2\n"
                  + "              WHERE hst2.row_id = hst.row_id\n"
                  + "              AND hst2.columnname = 'currency_id'\n"
                  + "              AND hst2.id < hst.id\n"
                  + "              ORDER BY hst2.id DESC LIMIT 1\n"
                  + "            ) as lastcurrency_id,\n"
                  + "            pli.price,\n"
                  + "            pli.currency_id,\n"
                  + "            us.name as usname,\n"
                  + field + " \n"
                  + "            us.surname as ussurname\n"
                  + "    FROM general.history hst\n"
                  + "    INNER JOIN inventory.pricelistitem pli ON(pli.id=hst.row_id AND pli.deleted=FALSE)\n"
                  + "    INNER JOIN inventory.pricelist pl ON(pl.id=pli.pricelist_id AND pl.deleted=FALSE " + where + " AND pl.is_purchase = FALSE)\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id=pli.stock_id AND stck.deleted=FALSE)\n"
                  + join + "\n"
                  + "    INNER JOIN general.userdata us ON (us.id = hst.userdata_id)\n"
                  + "    WHERE hst.tablename='inventory.pricelistitem'\n"
                  + "    AND (hst.columnname='price' OR hst.columnname='currency_id')\n"
                  + "    AND hst.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL'1 month'\n"
                  + "    ORDER BY hst.processdate DESC,stck.name,hst.columnname DESC\n"
                  + ") tt"
                  + " LIMIT " + pageSize + " OFFSET " + first;

        List<ChartItem> chartItems = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItems;
    }

    @Override
    public int countPriceVarying(boolean isAllBranches) {
        String where = "";
        String join = "";
        String field = "";
        String sub = "";
        if (!isAllBranches) {
            where = where + "  AND pl.branch_id  = " + sessionBean.getUser().getLastBranch().getId();
        } else {
            join = "INNER JOIN general.branch br ON(br.id=pl.branch_id AND br.deleted=FALSE) \n";
            field = "br.name as brsname,\n";
            sub = "tt.brsname,\n";
        }

        String sql = "WITH historytemp as\n"
                  + "(\n"
                  + "SELECT\n"
                  + "	hst2.tablename,  \n"
                  + "	hst2.newvalue,\n"
                  + "	hst2.row_id,\n"
                  + "	hst2.id,\n"
                  + "	hst2.columnname\n"
                  + "FROM \n"
                  + "	general.history hst2\n"
                  + "WHERE\n"
                  + "	 hst2.tablename = 'inventory.pricelistitem'   \n"
                  + "    AND hst2.columnname IN ('price','currency_id')\n"
                  + "GROUP by \n"
                  + "	hst2.tablename,  \n"
                  + "    hst2.newvalue,\n"
                  + "    hst2.id,\n"
                  + "    hst2.columnname\n"
                  + "ORDER BY \n"
                  + "	hst2.id DESC\n"
                  + ")\n"
                  + "SELECT\n"
                  + " count(tt.stckid)"
                  + "FROM(\n"
                  + "    SELECT\n"
                  + "            stck.id as stckid,\n"
                  + "            stck.name as stckname,\n"
                  + "            hst.processdate as hstprocessdate,\n"
                  + "            hst.columnname,\n"
                  + "            hst.oldvalue,\n"
                  + "            hst.newvalue,\n"
                  + "            (\n"
                  + "                SELECT\n"
                  + "                    hst2.newvalue\n"
                  + "                FROM historytemp hst2\n"
                  + "                WHERE hst2.row_id = hst.row_id\n"
                  + "                AND hst2.columnname = 'price'\n"
                  + "                AND hst2.id < hst.id\n"
                  + "                ORDER BY hst2.id DESC LIMIT 1\n"
                  + "            ) as lastprice,\n"
                  + "            (\n"
                  + "              SELECT\n"
                  + "                  hst2.newvalue\n"
                  + "                FROM historytemp hst2\n"
                  + "              WHERE hst2.row_id = hst.row_id\n"
                  + "              AND hst2.columnname = 'currency_id'\n"
                  + "              AND hst2.id < hst.id\n"
                  + "              ORDER BY hst2.id DESC LIMIT 1\n"
                  + "            ) as lastcurrency_id,\n"
                  + "            pli.price,\n"
                  + "            pli.currency_id,\n"
                  + "            us.name as usname,\n"
                  + field + " \n"
                  + "            us.surname as ussurname\n"
                  + "    FROM general.history hst\n"
                  + "    INNER JOIN inventory.pricelistitem pli ON(pli.id=hst.row_id AND pli.deleted=FALSE)\n"
                  + "    INNER JOIN inventory.pricelist pl ON(pl.id=pli.pricelist_id AND pl.deleted=FALSE " + where + " AND pl.is_purchase = FALSE)\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id=pli.stock_id AND stck.deleted=FALSE)\n"
                  + join + "\n"
                  + "    INNER JOIN general.userdata us ON (us.id = hst.userdata_id)\n"
                  + "    WHERE hst.tablename='inventory.pricelistitem'\n"
                  + "    AND (hst.columnname='price' OR hst.columnname='currency_id')\n"
                  + "    AND hst.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL'1 month'\n"
                  + "    ORDER BY hst.processdate DESC,stck.name,hst.columnname DESC\n"
                  + ") tt";

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public List<ChartItem> tempProductProfitalibility(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, boolean isAllBranches) {

        String where = "";
        String sql = "";

        if (isAllBranches) {
            sql = "--Güncel currency_id bazında döviz bilgilerini al\n"
                      + "WITH ranked_messages AS \n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "				COALESCE(ex.buying,1) as buying,\n"
                      + "				ex.currency_id,\n"
                      + "				ROW_NUMBER() OVER (PARTITION BY currency_id ORDER BY ex.id DESC) AS rn\n"
                      + "			FROM \n"
                      + "				finance.exchange ex \n"
                      + "			WHERE \n"
                      + "				ex.responsecurrency_id = 1\n"
                      + "                AND ex.deleted = FALSE \n"
                      + "			GROUP BY ex.currency_id,ex.buying,ex.id\n"
                      + "		)mm\n"
                      + "	WHERE \n"
                      + "    	mm.rn = 1\n"
                      + ")\n"
                      + "--Şube bazlı Fiyat Listesindeki ürün bilgilerini al\n"
                      + ", pricelistitemtemp AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		spli.pricelist_id,\n"
                      + "		spli.stock_id AS stckid,\n"
                      + "		brn.id AS branchid,\n"
                      + "		brn.name AS branchname,\n"
                      + "		spli.is_taxincluded AS salesistax,\n"
                      + "        spl.is_purchase as is_purchase,\n"
                      + "		CASE WHEN spli.is_taxincluded = TRUE THEN (COALESCE(spli.price,0)/(1+(COALESCE(taxgroup.rate,0)/100))) ELSE COALESCE(spli.price,0) END as price,\n"
                      + "		COALESCE((SELECT COALESCE(rms.buying,1) FROM ranked_messages  rms where  rms.currency_id = spli.currency_id LIMIT 1 ),1) AS buying \n"
                      + "	FROM \n"
                      + "    	inventory.pricelistitem spli\n"
                      + "		INNER JOIN inventory.pricelist spl ON(spl.id=spli.pricelist_id AND spl.deleted=FALSE AND spl.is_default=TRUE )\n"
                      + "		INNER JOIN general.branch brn ON(brn.id=spl.branch_id AND brn.deleted=FALSE)\n"
                      + "		LEFT JOIN (\n"
                      + "        			SELECT \n"
                      + "						txg.rate AS rate,\n"
                      + "         				stc.stock_id AS stock_id \n"
                      + "                    FROM \n"
                      + "                    	inventory.stock_taxgroup_con stc  \n"
                      + "                    	INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = FALSE)\n"
                      + "					WHERE \n"
                      + "                    	stc.deleted = FALSE\n"
                      + "						AND txg.type_id = 10 --kdv grubundan \n"
                      + "    					AND stc.is_purchase = FALSE\n"
                      + "        		 )taxgroup ON(taxgroup.stock_id = spli.stock_id)\n"
                      + "	WHERE  \n"
                      + "    	spli.deleted=FALSE\n"
                      + ")\n"
                      + "--Ürün ve Şube bazında son ve bir önceki fatura bilgilerini al\n"
                      + ",invoices AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	*\n"
                      + "	FROM \n"
                      + "    (\n"
                      + "    	SELECT\n"
                      + "        	invi.id  as inid,\n"
                      + "          	invi.stock_id,\n"
                      + "          	invi.c_time,\n"
                      + "          	inv.branch_id as invbranch,\n"
                      + "          	ROW_NUMBER() OVER (PARTITION BY invi.stock_id,inv.branch_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "          	CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                CASE WHEN invi.quantity >0 THEN \n"
                      + "                 (COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "               	ELSE  \n"
                      + "                 (COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "            ELSE\n"
                      + "            CASE WHEN invi.quantity >0 THEN \n"
                      + "              COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "              ELSE    \n"
                      + "              1 * COALESCE(invi.exchangerate,0) END\n"
                      + "            END AS unitprice\n"
                      + "		FROM \n"
                      + "			finance.invoiceitem invi\n"
                      + "            INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "        WHERE \n"
                      + "            inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "            AND invi.deleted = FALSE \n"
                      + "            AND inv.type_id  <> 27 \n"
                      + "            AND inv.status_id <> 30\n"
                      + "            ORDER BY invi.c_time DESC\n"
                      + "    )zz\n"
                      + "	WHERE  \n"
                      + "    	rnm in(1,2) \n"
                      + ")\n"
                      + "--Ürün ve Şube bazında son ve bir önceki satış bilgilerni al\n"
                      + ",sales AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "         		sli.id  as sliid,    \n"
                      + "         		sli.stock_id,\n"
                      + "         		sli.c_time,\n"
                      + "         		sl.branch_id AS slbranch,\n"
                      + "         		ROW_NUMBER() OVER (PARTITION BY sli.stock_id,sl.branch_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "        		CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "         			(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "        		ELSE\n"
                      + "            		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "        		END  AS unitprice\n"
                      + "  			FROM \n"
                      + "      			general.saleitem sli \n"
                      + "      			INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "            WHERE \n"
                      + "                sli.deleted = FALSE\n"
                      + "                AND sli.is_calcincluded =FALSE\n"
                      + "            ORDER BY sli.c_time DESC \n"
                      + "    	)ll \n"
                      + "	WHERE ll.rnm2 IN( 1,2)\n"
                      + ")\n"
                      + "--Tek fatura olanları(bir önceki fatura bilgisi olmayan kayıtlar) ve bir önceki fatura bilgisi olan kayıtları birleştir\n"
                      + ",lastinvoicepricetemp AS \n"
                      + "	(\n"
                      + "    	SELECT	\n"
                      + "    		inv1.unitprice as price,\n"
                      + "            inv1.stock_id,\n"
                      + "            inv1.c_time,\n"
                      + "        	inv1.inid,\n"
                      + "            inv1.invbranch\n"
                      + "		FROM  \n"
                      + "			invoices inv1\n"
                      + "			LEFT JOIN invoices inv2 on (inv1.stock_id = inv2.stock_id and inv1.invbranch = inv2.invbranch  AND inv2.rnm=2)\n"
                      + "		WHERE \n"
                      + "        	inv1.rnm = 1 AND inv2.stock_id is null\n"
                      + "    	\n"
                      + "        UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "            COALESCE(tt.unitprice,0) as price,\n"
                      + "            tt.stock_id,\n"
                      + "            tt.c_time,\n"
                      + "            tt.inid,\n"
                      + "            tt.invbranch\n"
                      + "    	FROM \n"
                      + "        	(\n"
                      + "              SELECT \n"
                      + "                  * \n"
                      + "              FROM \n"
                      + "                  (\n"
                      + "                    SELECT\n"
                      + "                        invi.id  as inid,\n"
                      + "                        invi.stock_id,\n"
                      + "                        invi.c_time,\n"
                      + "                        inv.branch_id as invbranch,\n"
                      + "                        ROW_NUMBER() OVER (PARTITION BY invi.stock_id,inv.branch_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "                        CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                              CASE WHEN invi.quantity >0 THEN \n"
                      + "                               (COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "                             ELSE  \n"
                      + "                               (COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "                          ELSE\n"
                      + "                          CASE WHEN invi.quantity >0 THEN \n"
                      + "                            COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "                            ELSE    \n"
                      + "                            1 * COALESCE(invi.exchangerate,0) END\n"
                      + "                          END AS unitprice\n"
                      + "                        FROM \n"
                      + "                            finance.invoiceitem invi\n"
                      + "                            INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "                        WHERE \n"
                      + "                            inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "                            AND invi.deleted = FALSE \n"
                      + "                            AND inv.type_id  <> 27 \n"
                      + "                            AND inv.status_id <> 30\n"
                      + "                    	ORDER BY invi.c_time DESC\n"
                      + "                    )mm \n"
                      + "              WHERE \n"
                      + "                  mm.rnm = 2\n"
                      + "                                                	\n"
                      + "			)tt\n"
                      + "	)\n"
                      + "    --Tek satış olanları(bir önceki satış bilgisi olmayan kayıtlar) ve bir önceki satış bilgisi olan kayıtları birleştir\n"
                      + "    ,lastsalepricetemp AS \n"
                      + "	(\n"
                      + "		SELECT\n"
                      + "    		sl1.unitprice,\n"
                      + "            sl1.stock_id,\n"
                      + "            sl1.c_time,\n"
                      + "        	sl1.sliid,\n"
                      + "            sl1.slbranch\n"
                      + "    	FROM  \n"
                      + "			sales sl1\n"
                      + "			LEFT JOIN sales sl2 on (sl1.stock_id = sl2.stock_id and sl1.slbranch = sl2.slbranch  AND sl2.rnm2=2)\n"
                      + "		WHERE \n"
                      + "        	sl1.rnm2 = 1 AND sl2.stock_id is null\n"
                      + "    	\n"
                      + "        UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "            COALESCE(tt.unitprice,0) as unitprice ,\n"
                      + "            tt.stock_id,\n"
                      + "            tt.c_time,\n"
                      + "            tt.sliid,\n"
                      + "            tt.slbranch\n"
                      + "		FROM (\n"
                      + "            	SELECT \n"
                      + "                	* \n"
                      + "            	FROM \n"
                      + "                	(\n"
                      + "                		SELECT \n"
                      + "                           	sli.id  as sliid,    \n"
                      + "                           	sli.stock_id,\n"
                      + "                           	sli.c_time,\n"
                      + "                           	sl.branch_id as slbranch,\n"
                      + "                           	ROW_NUMBER() OVER (PARTITION BY sli.stock_id,sl.branch_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "                           	CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "                           		(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "                           	ELSE\n"
                      + "                           		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "                           	END  AS unitprice\n"
                      + "                        FROM \n"
                      + "                        	general.saleitem sli \n"
                      + "                        	INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "                        WHERE \n"
                      + "                        	sli.deleted = FALSE\n"
                      + "                        	AND sli.is_calcincluded =FALSE\n"
                      + "                        ORDER BY sli.c_time DESC \n"
                      + "                    )ll \n"
                      + "		WHERE \n"
                      + "        	ll.rnm2 = 2\n"
                      + "            \n"
                      + "            ) tt\n"
                      + "	)\n"
                      + "SELECT * FROM \n"
                      + "	(\n"
                      + "      SELECT	\n"
                      + "      		tb1.id stckid,\n"
                      + "      		tb1.name as name, \n"
                      + "      		tb1.brsname as brsname,                              \n"
                      + "         	CASE WHEN COALESCE(tb1.lastinvoiceprice2,0) = 0 THEN 0\n"
                      + "          	ELSE\n"
                      + "        		(COALESCE(tb1.lastsaleprice2,0)-COALESCE(tb1.lastinvoiceprice2,0))/COALESCE(tb1.lastinvoiceprice2,0)*100 \n"
                      + "          	END AS onceki_kar,\n"
                      + "                                        \n"
                      + "          	CASE WHEN COALESCE(tb1.lastinvoiceprice,0) = 0 THEN 0\n"
                      + "          	ELSE\n"
                      + "            (COALESCE(tb1.lastsaleprice,0)-COALESCE(tb1.lastinvoiceprice,0))/COALESCE(tb1.lastinvoiceprice,0)*100 \n"
                      + "          	END AS simdiki_kar                               \n"
                      + "      FROM\n"
                      + "      		(\n"
                      + "      			SELECT\n"
                      + "                	stck.name,\n"
                      + "          			stck.id,\n"
                      + "          			br.name as brsname,\n"
                      + "          			br.id AS branchid,\n"
                      + "         			COALESCE((SELECT price FROM	lastinvoicepricetemp WHERE stock_id =stck.id AND invbranch = br.id),0) as lastinvoiceprice2,\n"
                      + "          			COALESCE((SELECT unitprice FROM lastsalepricetemp WHERE stock_id = stck.id AND slbranch= br.id),0) AS lastsaleprice2, \n"
                      + "          			COALESCE((salepricelisttable.price * salepricelisttable.buying),0) AS lastsaleprice,\n"
                      + "      				COALESCE((purchasepricelisttable.price * purchasepricelisttable.buying),0) AS lastinvoiceprice \n"
                      + "          		FROM \n"
                      + "                    inventory.stock stck \n"
                      + "                    INNER JOIN inventory.stockinfo stcki ON(stck.id=stcki.stock_id AND stcki.deleted=FALSE)\n"
                      + "                    INNER JOIN general.branch br ON (br.id=stcki.branch_id AND br.deleted=FALSE)\n"
                      + "                  \n"
                      + "                    LEFT JOIN (\n"
                      + "                    			SELECT \n"
                      + "                                	* \n"
                      + "                                FROM  \n"
                      + "                                	pricelistitemtemp spli \n"
                      + "                                WHERE \n"
                      + "                                	spli.is_purchase = FALSE\n"
                      + "                    		   )salepricelisttable ON(salepricelisttable.stckid =stck.id AND salepricelisttable.branchid= br.id)\n"
                      + "          			LEFT JOIN (\n"
                      + "                    			SELECT \n"
                      + "                                	* \n"
                      + "                                FROM  \n"
                      + "                                	pricelistitemtemp spli \n"
                      + "                                WHERE  \n"
                      + "                                	spli.is_purchase = TRUE\n"
                      + "            		   		  )purchasepricelisttable ON(purchasepricelisttable.stckid =stck.id AND purchasepricelisttable.branchid= br.id)\n"
                      + "          		WHERE \n"
                      + "                	stck.deleted=FALSE  \n"
                      + where + " \n"
                      + "          		ORDER BY stck.name \n"
                      + "            ) tb1 \n"
                      + "	) as gprice\n"
                      + "	WHERE \n"
                      + "    	gprice.onceki_kar <> gprice.simdiki_kar\n"
                      + " LIMIT " + pageSize + " OFFSET " + first;

        } else {

            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                where = where + " AND si.is_valid = TRUE  ";
            } else {
                where = where + " AND stck.is_otherbranch=TRUE  ";
            }
            sql = "--Fiyat Listesi Bilgilerini Al.\n"
                      + "WITH pricelisttemp as\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		pl.id,\n"
                      + "        pl.is_purchase\n"
                      + "	FROM \n"
                      + "    	inventory.pricelist pl \n"
                      + "	WHERE \n"
                      + "    	pl.is_default=TRUE \n"
                      + "        AND pl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + "  \n"
                      + "        AND pl.deleted=FALSE\n"
                      + ")\n"
                      + "--Güncel currency_id bazında döviz bilgilerini al\n"
                      + ",ranked_messages AS (\n"
                      + "	SELECT \n"
                      + "		COALESCE(ex.buying,1) as buying,\n"
                      + "		ex.currency_id,\n"
                      + "		ROW_NUMBER() OVER (PARTITION BY currency_id ORDER BY ex.id DESC) AS rn\n"
                      + "	FROM \n"
                      + "		finance.exchange ex \n"
                      + "	WHERE \n"
                      + "		ex.responsecurrency_id =1 \n"
                      + "        AND ex.deleted = FALSE \n"
                      + "	GROUP BY ex.currency_id,ex.buying,ex.id\n"
                      + ")\n"
                      + "--Fiyat Listesindeki ürün bilgilerini al\n"
                      + ",pricelistitemtemp as\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		spli.pricelist_id,\n"
                      + "        spli.stock_id as stckid,\n"
                      + "        spli.is_taxincluded as salesistax,\n"
                      + "        CASE WHEN spli.is_taxincluded = TRUE THEN (COALESCE(spli.price,0)/(1+(COALESCE(taxgroup.rate,0)/100))) ELSE COALESCE(spli.price,0) END as price,\n"
                      + "        COALESCE(\n"
                      + "          			( \n"
                      + "                    	SELECT \n"
                      + "                        	COALESCE(rms.buying,1) \n"
                      + "                        FROM \n"
                      + "                  			ranked_messages  rms \n"
                      + "                        WHERE \n"
                      + "                        	rms.rn = 1 \n"
                      + "                            AND rms.currency_id = spli.currency_id\n"
                      + "                   		ORDER BY id DESC LIMIT 1 \n"
                      + "           			),1\n"
                      + "        	    ) as buying \n"
                      + "	FROM \n"
                      + "    	inventory.pricelistitem spli\n"
                      + "        LEFT JOIN (	SELECT \n"
                      + "          				txg.rate AS rate,\n"
                      + "                   		stc.stock_id AS stock_id \n"
                      + "      				FROM \n"
                      + "                    	inventory.stock_taxgroup_con stc  \n"
                      + "  						INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                      + "      				WHERE \n"
                      + "                    	stc.deleted = false\n"
                      + "          				AND txg.type_id = 10 --kdv grubundan \n"
                      + "              			AND stc.is_purchase = FALSE\n"
                      + "        		 )taxgroup ON(taxgroup.stock_id = spli.stock_id)\n"
                      + "	WHERE  \n"
                      + "    	spli.deleted = FALSE\n"
                      + "        AND spli.pricelist_id IN (SELECT plt.id FROM pricelisttemp plt)\n"
                      + ")\n"
                      + "--Ürün bazında son ve bir önceki fatura bilgilerini al\n"
                      + ",invoices AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	* \n"
                      + "    FROM \n"
                      + "		(\n"
                      + "        	SELECT\n"
                      + "          		invi.id  AS inid,\n"
                      + "          		invi.stock_id,\n"
                      + "          		invi.c_time,\n"
                      + "          		ROW_NUMBER() OVER (PARTITION BY invi.stock_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "          		CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                	CASE WHEN invi.quantity >0 THEN \n"
                      + "                 		(COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "               		ELSE  \n"
                      + "                 		(COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "            	ELSE\n"
                      + "            		CASE WHEN invi.quantity >0 THEN \n"
                      + "              			COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "              		ELSE 1 * COALESCE(invi.exchangerate,0) END\n"
                      + "            	END AS unitprice\n"
                      + "          	FROM \n"
                      + "              	finance.invoiceitem invi\n"
                      + "              	INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "          	WHERE \n"
                      + "              	inv.is_purchase = TRUE \n"
                      + "                AND invi.is_calcincluded = FALSE\n"
                      + "              	AND invi.deleted = FALSE \n"
                      + "              	AND inv.type_id  <> 27 \n"
                      + "              	AND inv.status_id <> 30\n"
                      + "            ORDER BY invi.c_time DESC\n"
                      + "    \n"
                      + "    )xx\n"
                      + "	WHERE \n"
                      + "    	rnm in (1,2)\n"
                      + ")\n"
                      + "--Ürün bazında son ve bir önceki satış bilgilerni al\n"
                      + ",sales AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "         		sli.id  as sliid,    \n"
                      + "         		sli.stock_id,\n"
                      + "         		sli.c_time,\n"
                      + "         		ROW_NUMBER() OVER (PARTITION BY sli.stock_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "        		CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "         			(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "        		ELSE\n"
                      + "            		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "        		END  AS unitprice\n"
                      + "  			FROM \n"
                      + "      			general.saleitem sli \n"
                      + "      			INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "  			WHERE \n"
                      + "      			sli.deleted = FALSE\n"
                      + "      			AND sli.is_calcincluded =FALSE\n"
                      + "  			ORDER BY sli.c_time DESC \n"
                      + "        )ww \n"
                      + "	WHERE \n"
                      + "    	ww.rnm2 IN (1,2)\n"
                      + ")\n"
                      + "--Tek fatura olanları(bir önceki fatura bilgisi olmayan kayıtlar) ve bir önceki fatura bilgisi olan kayıtları birleştir\n"
                      + ",lastinvoicepricetemp AS \n"
                      + "(\n"
                      + "		SELECT	\n"
                      + "			inv1.unitprice as price,\n"
                      + "            inv1.stock_id,\n"
                      + "            inv1.c_time,\n"
                      + "        	inv1.inid\n"
                      + "		FROM  \n"
                      + "			invoices inv1\n"
                      + "			LEFT JOIN invoices inv2 on (inv1.stock_id = inv2.stock_id  AND inv2.rnm=2)\n"
                      + "		WHERE \n"
                      + "        	inv1.rnm = 1 AND inv2.stock_id is null\n"
                      + "    \n"
                      + "		UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "    		COALESCE(tt.unitprice,0) as price,\n"
                      + "    		tt.stock_id,\n"
                      + "    		tt.c_time,\n"
                      + "    		tt.inid\n"
                      + "        FROM \n"
                      + "        	(\n"
                      + "        		SELECT \n"
                      + "            		* \n"
                      + "        		FROM \n"
                      + "            		(\n"
                      + "            			SELECT\n"
                      + "                  			invi.id  as inid,\n"
                      + "                  			invi.stock_id,\n"
                      + "                  			invi.c_time,\n"
                      + "                  			ROW_NUMBER() OVER (PARTITION BY invi.stock_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "                  			CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                        		CASE WHEN invi.quantity >0 THEN \n"
                      + "                         			(COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "                       			ELSE  \n"
                      + "                         			(COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "                    		ELSE\n"
                      + "                    		CASE WHEN invi.quantity >0 THEN \n"
                      + "                      				COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "                      		ELSE 1 * COALESCE(invi.exchangerate,0) END\n"
                      + "                    		END AS unitprice\n"
                      + "                  		FROM \n"
                      + "                      		finance.invoiceitem invi\n"
                      + "                      		INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "                  		WHERE \n"
                      + "                      		inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "                      		AND invi.deleted = FALSE \n"
                      + "                      		AND inv.type_id  <> 27 \n"
                      + "                      		AND inv.status_id <> 30\n"
                      + "						ORDER BY invi.c_time DESC\n"
                      + "             	 )mm \n"
                      + "		WHERE \n"
                      + "			mm.rnm = 2\n"
                      + "        ) tt                                            \n"
                      + ")\n"
                      + "--Tek satış olanları(bir önceki satış bilgisi olmayan kayıtlar) ve bir önceki satış bilgisi olan kayıtları birleştir\n"
                      + ",lastsalepricetemp AS \n"
                      + "(\n"
                      + "	SELECT\n"
                      + "		sl1.unitprice,\n"
                      + "		sl1.stock_id,\n"
                      + "		sl1.c_time,\n"
                      + "		sl1.sliid\n"
                      + "	FROM  \n"
                      + "		sales sl1\n"
                      + "		LEFT JOIN sales sl2 on (sl1.stock_id = sl2.stock_id   AND sl2.rnm2=2)\n"
                      + "	WHERE \n"
                      + "		sl1.rnm2 = 1 \n"
                      + "        AND sl2.stock_id IS NULL \n"
                      + "        \n"
                      + "	UNION ALL\n"
                      + "    \n"
                      + "    SELECT\n"
                      + "		COALESCE(tt.unitprice,0) as unitprice ,\n"
                      + "     	tt.stock_id,\n"
                      + "     	tt.c_time,\n"
                      + "     	tt.sliid\n"
                      + "    FROM \n"
                      + "    	(\n"
                      + "			SELECT \n"
                      + "				* \n"
                      + "      		FROM \n"
                      + "          		(\n"
                      + "          			SELECT \n"
                      + "       					sli.id  as sliid,    \n"
                      + "       					sli.stock_id,\n"
                      + "       					sli.c_time,\n"
                      + "       					ROW_NUMBER() OVER (PARTITION BY sli.stock_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "      					CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN (COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "      					ELSE COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "      					END  AS unitprice\n"
                      + "					FROM \n"
                      + "    					general.saleitem sli \n"
                      + "    					INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "					WHERE \n"
                      + "    					sli.deleted = FALSE\n"
                      + "    					AND sli.is_calcincluded =FALSE\n"
                      + "					ORDER BY sli.c_time DESC \n"
                      + "          		)ll \n"
                      + "            WHERE \n"
                      + "            	ll.rnm2 = 2\n"
                      + "        ) tt\n"
                      + ")\n"
                      + "\n"
                      + "SELECT \n"
                      + "	* \n"
                      + "FROM\n"
                      + "	(\n"
                      + "		SELECT	\n"
                      + "			tb1.id stckid,\n"
                      + "			tb1.name as name,                               \n"
                      + "      		CASE WHEN COALESCE(tb1.lastinvoiceprice2,0) = 0 THEN 0 ELSE (COALESCE(tb1.lastsaleprice2,0)-COALESCE(tb1.lastinvoiceprice2,0))/COALESCE(tb1.lastinvoiceprice2,0)*100 END AS onceki_kar,\n"
                      + "            CASE WHEN COALESCE(tb1.lastinvoiceprice,0) = 0 THEN 0 ELSE (COALESCE(tb1.lastsaleprice,0)-COALESCE(tb1.lastinvoiceprice,0))/COALESCE(tb1.lastinvoiceprice,0)*100 END AS simdiki_kar                               \n"
                      + "		FROM\n"
                      + "        	(\n"
                      + "				SELECT\n"
                      + "                	stck.name,\n"
                      + "      				stck.id,\n"
                      + "                    COALESCE((SELECT price FROM lastinvoicepricetemp WHERE stock_id =stck.id),0) AS lastinvoiceprice2,\n"
                      + "      				COALESCE((SELECT unitprice FROM lastsalepricetemp WHERE stock_id = stck.id),0) AS lastsaleprice2, \n"
                      + "      				COALESCE((salepricelisttable.price * salepricelisttable.buying),0) AS lastsaleprice,\n"
                      + "					COALESCE((purchasepricelisttable.price * purchasepricelisttable.buying),0) AS lastinvoiceprice \n"
                      + "      			FROM \n"
                      + "                	inventory.stock stck\n"
                      + "                    LEFT JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted=FALSE AND si.branch_id = " + sessionBean.getUser().getLastBranch().getId() + ")\n"
                      + "                    LEFT JOIN (\n"
                      + "                    				SELECT * FROM  pricelistitemtemp spli WHERE spli.pricelist_id = (SELECT plt.id FROM  pricelisttemp plt where plt.is_purchase = FALSE)\n"
                      + "        			  		   )salepricelisttable ON(salepricelisttable.stckid =stck.id)\n"
                      + "      				LEFT JOIN ( \n"
                      + "                      				SELECT * FROM  pricelistitemtemp spli where  spli.pricelist_id = (SELECT plt.id FROM  pricelisttemp plt where plt.is_purchase = TRUE)\n"
                      + "        			           ) purchasepricelisttable ON(purchasepricelisttable.stckid =stck.id)\n"
                      + "      			WHERE \n"
                      + "                	stck.deleted=FALSE  \n"
                      + where + " \n"
                      + "      			ORDER BY stck.name \n"
                      + "            ) tb1 \n"
                      + "	) AS gprice\n"
                      + "WHERE \n"
                      + "	gprice.onceki_kar <> gprice.simdiki_kar"
                      + " LIMIT " + pageSize + " OFFSET " + first;
        }

        List<ChartItem> chartItems = getJdbcTemplate().query(sql, new ChartItemMapper());
        return chartItems;
    }

    @Override
    public int count(boolean isAllBranches) {
        String where = "";
        String sql = "";

        if (isAllBranches) {
            sql = "--Güncel currency_id bazında döviz bilgilerini al\n"
                      + "WITH ranked_messages AS \n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "				COALESCE(ex.buying,1) as buying,\n"
                      + "				ex.currency_id,\n"
                      + "				ROW_NUMBER() OVER (PARTITION BY currency_id ORDER BY ex.id DESC) AS rn\n"
                      + "			FROM \n"
                      + "				finance.exchange ex \n"
                      + "			WHERE \n"
                      + "				ex.responsecurrency_id = 1\n"
                      + "                AND ex.deleted = FALSE \n"
                      + "			GROUP BY ex.currency_id,ex.buying,ex.id\n"
                      + "		)mm\n"
                      + "	WHERE \n"
                      + "    	mm.rn = 1\n"
                      + ")\n"
                      + "--Şube bazlı Fiyat Listesindeki ürün bilgilerini al\n"
                      + ", pricelistitemtemp AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		spli.pricelist_id,\n"
                      + "		spli.stock_id AS stckid,\n"
                      + "		brn.id AS branchid,\n"
                      + "		brn.name AS branchname,\n"
                      + "		spli.is_taxincluded AS salesistax,\n"
                      + "        spl.is_purchase as is_purchase,\n"
                      + "		CASE WHEN spli.is_taxincluded = TRUE THEN (COALESCE(spli.price,0)/(1+(COALESCE(taxgroup.rate,0)/100))) ELSE COALESCE(spli.price,0) END as price,\n"
                      + "		COALESCE((SELECT COALESCE(rms.buying,1) FROM ranked_messages  rms where  rms.currency_id = spli.currency_id LIMIT 1 ),1) AS buying \n"
                      + "	FROM \n"
                      + "    	inventory.pricelistitem spli\n"
                      + "		INNER JOIN inventory.pricelist spl ON(spl.id=spli.pricelist_id AND spl.deleted=FALSE AND spl.is_default=TRUE )\n"
                      + "		INNER JOIN general.branch brn ON(brn.id=spl.branch_id AND brn.deleted=FALSE)\n"
                      + "		LEFT JOIN (\n"
                      + "        			SELECT \n"
                      + "						txg.rate AS rate,\n"
                      + "         				stc.stock_id AS stock_id \n"
                      + "                    FROM \n"
                      + "                    	inventory.stock_taxgroup_con stc  \n"
                      + "                    	INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = FALSE)\n"
                      + "					WHERE \n"
                      + "                    	stc.deleted = FALSE\n"
                      + "						AND txg.type_id = 10 --kdv grubundan \n"
                      + "    					AND stc.is_purchase = FALSE\n"
                      + "        		 )taxgroup ON(taxgroup.stock_id = spli.stock_id)\n"
                      + "	WHERE  \n"
                      + "    	spli.deleted=FALSE\n"
                      + ")\n"
                      + "--Ürün ve Şube bazında son ve bir önceki fatura bilgilerini al\n"
                      + ",invoices AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	*\n"
                      + "	FROM \n"
                      + "    (\n"
                      + "    	SELECT\n"
                      + "        	invi.id  as inid,\n"
                      + "          	invi.stock_id,\n"
                      + "          	invi.c_time,\n"
                      + "          	inv.branch_id as invbranch,\n"
                      + "          	ROW_NUMBER() OVER (PARTITION BY invi.stock_id,inv.branch_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "          	CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                CASE WHEN invi.quantity >0 THEN \n"
                      + "                 (COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "               	ELSE  \n"
                      + "                 (COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "            ELSE\n"
                      + "            CASE WHEN invi.quantity >0 THEN \n"
                      + "              COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "              ELSE    \n"
                      + "              1 * COALESCE(invi.exchangerate,0) END\n"
                      + "            END AS unitprice\n"
                      + "		FROM \n"
                      + "			finance.invoiceitem invi\n"
                      + "            INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "        WHERE \n"
                      + "            inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "            AND invi.deleted = FALSE \n"
                      + "            AND inv.type_id  <> 27 \n"
                      + "            AND inv.status_id <> 30\n"
                      + "            ORDER BY invi.c_time DESC\n"
                      + "    )zz\n"
                      + "	WHERE  \n"
                      + "    	rnm in(1,2) \n"
                      + ")\n"
                      + "--Ürün ve Şube bazında son ve bir önceki satış bilgilerni al\n"
                      + ",sales AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "         		sli.id  as sliid,    \n"
                      + "         		sli.stock_id,\n"
                      + "         		sli.c_time,\n"
                      + "         		sl.branch_id AS slbranch,\n"
                      + "         		ROW_NUMBER() OVER (PARTITION BY sli.stock_id,sl.branch_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "        		CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "         			(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "        		ELSE\n"
                      + "            		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "        		END  AS unitprice\n"
                      + "  			FROM \n"
                      + "      			general.saleitem sli \n"
                      + "      			INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "            WHERE \n"
                      + "                sli.deleted = FALSE\n"
                      + "                AND sli.is_calcincluded =FALSE\n"
                      + "            ORDER BY sli.c_time DESC \n"
                      + "    	)ll \n"
                      + "	WHERE ll.rnm2 IN( 1,2)\n"
                      + ")\n"
                      + "--Tek fatura olanları(bir önceki fatura bilgisi olmayan kayıtlar) ve bir önceki fatura bilgisi olan kayıtları birleştir\n"
                      + ",lastinvoicepricetemp AS \n"
                      + "	(\n"
                      + "    	SELECT	\n"
                      + "    		inv1.unitprice as price,\n"
                      + "            inv1.stock_id,\n"
                      + "            inv1.c_time,\n"
                      + "        	inv1.inid,\n"
                      + "            inv1.invbranch\n"
                      + "		FROM  \n"
                      + "			invoices inv1\n"
                      + "			LEFT JOIN invoices inv2 on (inv1.stock_id = inv2.stock_id and inv1.invbranch = inv2.invbranch  AND inv2.rnm=2)\n"
                      + "		WHERE \n"
                      + "        	inv1.rnm = 1 AND inv2.stock_id is null\n"
                      + "    	\n"
                      + "        UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "            COALESCE(tt.unitprice,0) as price,\n"
                      + "            tt.stock_id,\n"
                      + "            tt.c_time,\n"
                      + "            tt.inid,\n"
                      + "            tt.invbranch\n"
                      + "    	FROM \n"
                      + "        	(\n"
                      + "              SELECT \n"
                      + "                  * \n"
                      + "              FROM \n"
                      + "                  (\n"
                      + "                    SELECT\n"
                      + "                        invi.id  as inid,\n"
                      + "                        invi.stock_id,\n"
                      + "                        invi.c_time,\n"
                      + "                        inv.branch_id as invbranch,\n"
                      + "                        ROW_NUMBER() OVER (PARTITION BY invi.stock_id,inv.branch_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "                        CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                              CASE WHEN invi.quantity >0 THEN \n"
                      + "                               (COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "                             ELSE  \n"
                      + "                               (COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "                          ELSE\n"
                      + "                          CASE WHEN invi.quantity >0 THEN \n"
                      + "                            COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "                            ELSE    \n"
                      + "                            1 * COALESCE(invi.exchangerate,0) END\n"
                      + "                          END AS unitprice\n"
                      + "                        FROM \n"
                      + "                            finance.invoiceitem invi\n"
                      + "                            INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "                        WHERE \n"
                      + "                            inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "                            AND invi.deleted = FALSE \n"
                      + "                            AND inv.type_id  <> 27 \n"
                      + "                            AND inv.status_id <> 30\n"
                      + "                    	ORDER BY invi.c_time DESC\n"
                      + "                    )mm \n"
                      + "              WHERE \n"
                      + "                  mm.rnm = 2\n"
                      + "                                                	\n"
                      + "			)tt\n"
                      + "	)\n"
                      + "    --Tek satış olanları(bir önceki satış bilgisi olmayan kayıtlar) ve bir önceki satış bilgisi olan kayıtları birleştir\n"
                      + "    ,lastsalepricetemp AS \n"
                      + "	(\n"
                      + "		SELECT\n"
                      + "    		sl1.unitprice,\n"
                      + "            sl1.stock_id,\n"
                      + "            sl1.c_time,\n"
                      + "        	sl1.sliid,\n"
                      + "            sl1.slbranch\n"
                      + "    	FROM  \n"
                      + "			sales sl1\n"
                      + "			LEFT JOIN sales sl2 on (sl1.stock_id = sl2.stock_id and sl1.slbranch = sl2.slbranch  AND sl2.rnm2=2)\n"
                      + "		WHERE \n"
                      + "        	sl1.rnm2 = 1 AND sl2.stock_id is null\n"
                      + "    	\n"
                      + "        UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "            COALESCE(tt.unitprice,0) as unitprice ,\n"
                      + "            tt.stock_id,\n"
                      + "            tt.c_time,\n"
                      + "            tt.sliid,\n"
                      + "            tt.slbranch\n"
                      + "		FROM (\n"
                      + "            	SELECT \n"
                      + "                	* \n"
                      + "            	FROM \n"
                      + "                	(\n"
                      + "                		SELECT \n"
                      + "                           	sli.id  as sliid,    \n"
                      + "                           	sli.stock_id,\n"
                      + "                           	sli.c_time,\n"
                      + "                           	sl.branch_id as slbranch,\n"
                      + "                           	ROW_NUMBER() OVER (PARTITION BY sli.stock_id,sl.branch_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "                           	CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "                           		(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "                           	ELSE\n"
                      + "                           		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "                           	END  AS unitprice\n"
                      + "                        FROM \n"
                      + "                        	general.saleitem sli \n"
                      + "                        	INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "                        WHERE \n"
                      + "                        	sli.deleted = FALSE\n"
                      + "                        	AND sli.is_calcincluded =FALSE\n"
                      + "                        ORDER BY sli.c_time DESC \n"
                      + "                    )ll \n"
                      + "		WHERE \n"
                      + "        	ll.rnm2 = 2\n"
                      + "            \n"
                      + "            ) tt\n"
                      + "	)\n"
                      + "SELECT "
                      + "count(gprice.stckid) \n"
                      + " FROM \n"
                      + "	(\n"
                      + "      SELECT	\n"
                      + "      		tb1.id stckid,\n"
                      + "      		tb1.name as name, \n"
                      + "      		tb1.brsname as brsname,                              \n"
                      + "         	CASE WHEN COALESCE(tb1.lastinvoiceprice2,0) = 0 THEN 0\n"
                      + "          	ELSE\n"
                      + "        		(COALESCE(tb1.lastsaleprice2,0)-COALESCE(tb1.lastinvoiceprice2,0))/COALESCE(tb1.lastinvoiceprice2,0)*100 \n"
                      + "          	END AS onceki_kar,\n"
                      + "                                        \n"
                      + "          	CASE WHEN COALESCE(tb1.lastinvoiceprice,0) = 0 THEN 0\n"
                      + "          	ELSE\n"
                      + "            (COALESCE(tb1.lastsaleprice,0)-COALESCE(tb1.lastinvoiceprice,0))/COALESCE(tb1.lastinvoiceprice,0)*100 \n"
                      + "          	END AS simdiki_kar                               \n"
                      + "      FROM\n"
                      + "      		(\n"
                      + "      			SELECT\n"
                      + "                	stck.name,\n"
                      + "          			stck.id,\n"
                      + "          			br.name as brsname,\n"
                      + "          			br.id AS branchid,\n"
                      + "         			COALESCE((SELECT price FROM	lastinvoicepricetemp WHERE stock_id =stck.id AND invbranch = br.id),0) as lastinvoiceprice2,\n"
                      + "          			COALESCE((SELECT unitprice FROM lastsalepricetemp WHERE stock_id = stck.id AND slbranch= br.id),0) AS lastsaleprice2, \n"
                      + "          			COALESCE((salepricelisttable.price * salepricelisttable.buying),0) AS lastsaleprice,\n"
                      + "      				COALESCE((purchasepricelisttable.price * purchasepricelisttable.buying),0) AS lastinvoiceprice \n"
                      + "          		FROM \n"
                      + "                    inventory.stock stck \n"
                      + "                    INNER JOIN inventory.stockinfo stcki ON(stck.id=stcki.stock_id AND stcki.deleted=FALSE)\n"
                      + "                    INNER JOIN general.branch br ON (br.id=stcki.branch_id AND br.deleted=FALSE)\n"
                      + "                  \n"
                      + "                    LEFT JOIN (\n"
                      + "                    			SELECT \n"
                      + "                                	* \n"
                      + "                                FROM  \n"
                      + "                                	pricelistitemtemp spli \n"
                      + "                                WHERE \n"
                      + "                                	spli.is_purchase = FALSE\n"
                      + "                    		   )salepricelisttable ON(salepricelisttable.stckid =stck.id AND salepricelisttable.branchid= br.id)\n"
                      + "          			LEFT JOIN (\n"
                      + "                    			SELECT \n"
                      + "                                	* \n"
                      + "                                FROM  \n"
                      + "                                	pricelistitemtemp spli \n"
                      + "                                WHERE  \n"
                      + "                                	spli.is_purchase = TRUE\n"
                      + "            		   		  )purchasepricelisttable ON(purchasepricelisttable.stckid =stck.id AND purchasepricelisttable.branchid= br.id)\n"
                      + "          		WHERE \n"
                      + "                	stck.deleted=FALSE  \n"
                      + where + " \n"
                      + "          		ORDER BY stck.name \n"
                      + "            ) tb1 \n"
                      + "	) as gprice\n"
                      + "	WHERE \n"
                      + "    	gprice.onceki_kar <> gprice.simdiki_kar\n";

        } else {

            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                where = where + " AND si.is_valid = TRUE  ";
            } else {
                where = where + " AND stck.is_otherbranch=TRUE  ";
            }
            sql = "--Fiyat Listesi Bilgilerini Al.\n"
                      + "WITH pricelisttemp as\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		pl.id,\n"
                      + "        pl.is_purchase\n"
                      + "	FROM \n"
                      + "    	inventory.pricelist pl \n"
                      + "	WHERE \n"
                      + "    	pl.is_default=TRUE \n"
                      + "        AND pl.branch_id= " + sessionBean.getUser().getLastBranch().getId() + "\n"
                      + "        AND pl.deleted=FALSE\n"
                      + ")\n"
                      + "--Güncel currency_id bazında döviz bilgilerini al\n"
                      + ",ranked_messages AS (\n"
                      + "	SELECT \n"
                      + "		COALESCE(ex.buying,1) as buying,\n"
                      + "		ex.currency_id,\n"
                      + "		ROW_NUMBER() OVER (PARTITION BY currency_id ORDER BY ex.id DESC) AS rn\n"
                      + "	FROM \n"
                      + "		finance.exchange ex \n"
                      + "	WHERE \n"
                      + "		ex.responsecurrency_id = 1\n"
                      + "        AND ex.deleted = FALSE \n"
                      + "	GROUP BY ex.currency_id,ex.buying,ex.id\n"
                      + ")\n"
                      + "--Fiyat Listesindeki ürün bilgilerini al\n"
                      + ",pricelistitemtemp as\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		spli.pricelist_id,\n"
                      + "        spli.stock_id as stckid,\n"
                      + "        spli.is_taxincluded as salesistax,\n"
                      + "        CASE WHEN spli.is_taxincluded = TRUE THEN (COALESCE(spli.price,0)/(1+(COALESCE(taxgroup.rate,0)/100))) ELSE COALESCE(spli.price,0) END as price,\n"
                      + "        COALESCE(\n"
                      + "          			( \n"
                      + "                    	SELECT \n"
                      + "                        	COALESCE(rms.buying,1) \n"
                      + "                        FROM \n"
                      + "                  			ranked_messages  rms \n"
                      + "                        WHERE \n"
                      + "                        	rms.rn = 1 \n"
                      + "                            AND rms.currency_id = spli.currency_id\n"
                      + "                   		ORDER BY id DESC LIMIT 1 \n"
                      + "           			),1\n"
                      + "        	    ) as buying \n"
                      + "	FROM \n"
                      + "    	inventory.pricelistitem spli\n"
                      + "        LEFT JOIN (	SELECT \n"
                      + "          				txg.rate AS rate,\n"
                      + "                   		stc.stock_id AS stock_id \n"
                      + "      				FROM \n"
                      + "                    	inventory.stock_taxgroup_con stc  \n"
                      + "  						INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                      + "      				WHERE \n"
                      + "                    	stc.deleted = false\n"
                      + "          				AND txg.type_id = 10 --kdv grubundan \n"
                      + "              			AND stc.is_purchase = FALSE\n"
                      + "        		 )taxgroup ON(taxgroup.stock_id = spli.stock_id)\n"
                      + "	WHERE  \n"
                      + "    	spli.deleted = FALSE\n"
                      + "        AND spli.pricelist_id IN (SELECT plt.id FROM pricelisttemp plt)\n"
                      + ")\n"
                      + "--Ürün bazında son ve bir önceki fatura bilgilerini al\n"
                      + ",invoices AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "    	* \n"
                      + "    FROM \n"
                      + "		(\n"
                      + "        	SELECT\n"
                      + "          		invi.id  AS inid,\n"
                      + "          		invi.stock_id,\n"
                      + "          		invi.c_time,\n"
                      + "          		ROW_NUMBER() OVER (PARTITION BY invi.stock_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "          		CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                	CASE WHEN invi.quantity >0 THEN \n"
                      + "                 		(COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "               		ELSE  \n"
                      + "                 		(COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "            	ELSE\n"
                      + "            		CASE WHEN invi.quantity >0 THEN \n"
                      + "              			COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "              		ELSE 1 * COALESCE(invi.exchangerate,0) END\n"
                      + "            	END AS unitprice\n"
                      + "          	FROM \n"
                      + "              	finance.invoiceitem invi\n"
                      + "              	INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "          	WHERE \n"
                      + "              	inv.is_purchase = TRUE \n"
                      + "                AND invi.is_calcincluded = FALSE\n"
                      + "              	AND invi.deleted = FALSE \n"
                      + "              	AND inv.type_id  <> 27 \n"
                      + "              	AND inv.status_id <> 30\n"
                      + "            ORDER BY invi.c_time DESC\n"
                      + "    \n"
                      + "    )xx\n"
                      + "	WHERE \n"
                      + "    	rnm in (1,2)\n"
                      + ")\n"
                      + "--Ürün bazında son ve bir önceki satış bilgilerni al\n"
                      + ",sales AS\n"
                      + "(\n"
                      + "	SELECT \n"
                      + "		* \n"
                      + "	FROM \n"
                      + "		(\n"
                      + "			SELECT \n"
                      + "         		sli.id  as sliid,    \n"
                      + "         		sli.stock_id,\n"
                      + "         		sli.c_time,\n"
                      + "         		ROW_NUMBER() OVER (PARTITION BY sli.stock_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "        		CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN\n"
                      + "         			(COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "        		ELSE\n"
                      + "            		COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "        		END  AS unitprice\n"
                      + "  			FROM \n"
                      + "      			general.saleitem sli \n"
                      + "      			INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "  			WHERE \n"
                      + "      			sli.deleted = FALSE\n"
                      + "      			AND sli.is_calcincluded =FALSE\n"
                      + "  			ORDER BY sli.c_time DESC \n"
                      + "        )ww \n"
                      + "	WHERE \n"
                      + "    	ww.rnm2 IN (1,2)\n"
                      + ")\n"
                      + "--Tek fatura olanları(bir önceki fatura bilgisi olmayan kayıtlar) ve bir önceki fatura bilgisi olan kayıtları birleştir\n"
                      + ",lastinvoicepricetemp AS \n"
                      + "(\n"
                      + "		SELECT	\n"
                      + "			inv1.unitprice as price,\n"
                      + "            inv1.stock_id,\n"
                      + "            inv1.c_time,\n"
                      + "        	inv1.inid\n"
                      + "		FROM  \n"
                      + "			invoices inv1\n"
                      + "			LEFT JOIN invoices inv2 on (inv1.stock_id = inv2.stock_id  AND inv2.rnm=2)\n"
                      + "		WHERE \n"
                      + "        	inv1.rnm = 1 AND inv2.stock_id is null\n"
                      + "    \n"
                      + "		UNION ALL\n"
                      + "        \n"
                      + "        SELECT\n"
                      + "    		COALESCE(tt.unitprice,0) as price,\n"
                      + "    		tt.stock_id,\n"
                      + "    		tt.c_time,\n"
                      + "    		tt.inid\n"
                      + "        FROM \n"
                      + "        	(\n"
                      + "        		SELECT \n"
                      + "            		* \n"
                      + "        		FROM \n"
                      + "            		(\n"
                      + "            			SELECT\n"
                      + "                  			invi.id  as inid,\n"
                      + "                  			invi.stock_id,\n"
                      + "                  			invi.c_time,\n"
                      + "                  			ROW_NUMBER() OVER (PARTITION BY invi.stock_id ORDER BY invi.c_time DESC) AS rnm,\n"
                      + "                  			CASE WHEN COALESCE(inv.discountrate,0) > 0 THEN \n"
                      + "                        		CASE WHEN invi.quantity >0 THEN \n"
                      + "                         			(COALESCE((COALESCE(invi.totalprice,0) / COALESCE(invi.quantity,0)) * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) \n"
                      + "                       			ELSE  \n"
                      + "                         			(COALESCE(0 * (1-(COALESCE(inv.discountrate,0)/100)),0)) * COALESCE(invi.exchangerate,0) END \n"
                      + "                    		ELSE\n"
                      + "                    		CASE WHEN invi.quantity >0 THEN \n"
                      + "                      				COALESCE((COALESCE(invi.totalprice,1) / COALESCE(invi.quantity,0)),1) * COALESCE(invi.exchangerate,0)   \n"
                      + "                      		ELSE 1 * COALESCE(invi.exchangerate,0) END\n"
                      + "                    		END AS unitprice\n"
                      + "                  		FROM \n"
                      + "                      		finance.invoiceitem invi\n"
                      + "                      		INNER JOIN finance.invoice inv ON (invi.invoice_id = inv.id AND inv.deleted = FALSE)\n"
                      + "                  		WHERE \n"
                      + "                      		inv.is_purchase = TRUE AND invi.is_calcincluded = FALSE\n"
                      + "                      		AND invi.deleted = FALSE \n"
                      + "                      		AND inv.type_id  <> 27 \n"
                      + "                      		AND inv.status_id <> 30\n"
                      + "						ORDER BY invi.c_time DESC\n"
                      + "             	 )mm \n"
                      + "		WHERE \n"
                      + "			mm.rnm = 2\n"
                      + "        ) tt                                            \n"
                      + ")\n"
                      + "--Tek satış olanları(bir önceki satış bilgisi olmayan kayıtlar) ve bir önceki satış bilgisi olan kayıtları birleştir\n"
                      + ",lastsalepricetemp AS \n"
                      + "(\n"
                      + "	SELECT\n"
                      + "		sl1.unitprice,\n"
                      + "		sl1.stock_id,\n"
                      + "		sl1.c_time,\n"
                      + "		sl1.sliid\n"
                      + "	FROM  \n"
                      + "		sales sl1\n"
                      + "		LEFT JOIN sales sl2 on (sl1.stock_id = sl2.stock_id   AND sl2.rnm2=2)\n"
                      + "	WHERE \n"
                      + "		sl1.rnm2 = 1 \n"
                      + "        AND sl2.stock_id IS NULL \n"
                      + "        \n"
                      + "	UNION ALL\n"
                      + "    \n"
                      + "    SELECT\n"
                      + "		COALESCE(tt.unitprice,0) as unitprice ,\n"
                      + "     	tt.stock_id,\n"
                      + "     	tt.c_time,\n"
                      + "     	tt.sliid\n"
                      + "    FROM \n"
                      + "    	(\n"
                      + "			SELECT \n"
                      + "				* \n"
                      + "      		FROM \n"
                      + "          		(\n"
                      + "          			SELECT \n"
                      + "       					sli.id  as sliid,    \n"
                      + "       					sli.stock_id,\n"
                      + "       					sli.c_time,\n"
                      + "       					ROW_NUMBER() OVER (PARTITION BY sli.stock_id ORDER BY sli.c_time DESC) AS rnm2,\n"
                      + "      					CASE WHEN COALESCE(sli.taxrate,0) > 0 THEN (COALESCE(sli.unitprice,0) / (1+(COALESCE(sli.taxrate,0)/100))) * COALESCE(sli.exchangerate,0) \n"
                      + "      					ELSE COALESCE(sli.unitprice,0) * COALESCE(sli.exchangerate,0) \n"
                      + "      					END  AS unitprice\n"
                      + "					FROM \n"
                      + "    					general.saleitem sli \n"
                      + "    					INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.branch_id=" + sessionBean.getUser().getLastBranch().getId() + " AND sl.deleted=FALSE AND sl.is_return=FALSE)\n"
                      + "					WHERE \n"
                      + "    					sli.deleted = FALSE\n"
                      + "    					AND sli.is_calcincluded =FALSE\n"
                      + "					ORDER BY sli.c_time DESC \n"
                      + "          		)ll \n"
                      + "            WHERE \n"
                      + "            	ll.rnm2 = 2\n"
                      + "        ) tt\n"
                      + ")\n"
                      + "\n"
                      + "SELECT \n"
                      + "count(gprice.stckid) \n"
                      + "FROM\n"
                      + "	(\n"
                      + "		SELECT	\n"
                      + "			tb1.id stckid,\n"
                      + "			tb1.name as name,                               \n"
                      + "      		CASE WHEN COALESCE(tb1.lastinvoiceprice2,0) = 0 THEN 0 ELSE (COALESCE(tb1.lastsaleprice2,0)-COALESCE(tb1.lastinvoiceprice2,0))/COALESCE(tb1.lastinvoiceprice2,0)*100 END AS onceki_kar,\n"
                      + "            CASE WHEN COALESCE(tb1.lastinvoiceprice,0) = 0 THEN 0 ELSE (COALESCE(tb1.lastsaleprice,0)-COALESCE(tb1.lastinvoiceprice,0))/COALESCE(tb1.lastinvoiceprice,0)*100 END AS simdiki_kar                               \n"
                      + "		FROM\n"
                      + "        	(\n"
                      + "				SELECT\n"
                      + "                	stck.name,\n"
                      + "      				stck.id,\n"
                      + "                    COALESCE((SELECT price FROM lastinvoicepricetemp WHERE stock_id =stck.id),0) AS lastinvoiceprice2,\n"
                      + "      				COALESCE((SELECT unitprice FROM lastsalepricetemp WHERE stock_id = stck.id),0) AS lastsaleprice2, \n"
                      + "      				COALESCE((salepricelisttable.price * salepricelisttable.buying),0) AS lastsaleprice,\n"
                      + "					COALESCE((purchasepricelisttable.price * purchasepricelisttable.buying),0) AS lastinvoiceprice \n"
                      + "      			FROM \n"
                      + "                	inventory.stock stck\n"
                      + "                    LEFT JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted=FALSE AND si.branch_id = " + sessionBean.getUser().getLastBranch().getId() + ")\n"
                      + "                    LEFT JOIN (\n"
                      + "                    				SELECT * FROM  pricelistitemtemp spli WHERE spli.pricelist_id = (SELECT plt.id FROM  pricelisttemp plt where plt.is_purchase = FALSE)\n"
                      + "        			  		   )salepricelisttable ON(salepricelisttable.stckid =stck.id)\n"
                      + "      				LEFT JOIN ( \n"
                      + "                      				SELECT * FROM  pricelistitemtemp spli where  spli.pricelist_id = (SELECT plt.id FROM  pricelisttemp plt where plt.is_purchase = TRUE)\n"
                      + "        			           ) purchasepricelisttable ON(purchasepricelisttable.stckid =stck.id)\n"
                      + "      			WHERE \n"
                      + "                	stck.deleted=FALSE  \n"
                      + where + "\n"
                      + "      			ORDER BY stck.name \n"
                      + "            ) tb1 \n"
                      + "	) AS gprice\n"
                      + "WHERE \n"
                      + "	gprice.onceki_kar <> gprice.simdiki_kar";

        }

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;

    }

}
