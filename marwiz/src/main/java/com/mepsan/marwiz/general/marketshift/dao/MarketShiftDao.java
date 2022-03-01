/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2018 10:41:50
 */
package com.mepsan.marwiz.general.marketshift.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pointofsale.dao.PointOfSaleMapper;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MarketShiftDao extends JdbcDaoSupport implements IMarketShiftDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Shift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        if (sortField == null) {
            sortField = "shf.begindate";
            sortOrder = "desc";
        }
        String sql = "WITH saledataa AS \n"
                  + "(\n"
                  + "	SELECT \n"
                  + "    sl.userdata_id,\n"
                  + "        COALESCE(usr.name,'') AS usrname,\n"
                  + "        COALESCE(usr.surname,'') AS usrsurname,\n"
                  + "        sl.is_return,\n"
                  + "        sl.totalmoney,\n"
                  + "        sl.currency_id,\n"
                  + "        sl.shift_id\n"
                  + "        ,sl.id AS slid\n"
                  + "        ,usr.type_id\n"
                  + "        \n"
                  + "       \n"
                  + "    FROM \n"
                  + "    	general.sale sl\n"
                  + "        INNER JOIN general.userdata usr ON(usr.id=sl.userdata_id)\n"
                  + "        INNER JOIN general.shift shf on (shf.id = sl.shift_id and shf.deleted=False  AND shf.branch_id=?\n"
                  + " )\n"
                  + "        INNER JOIN system.status_dict sttd ON (sttd.status_id = shf.status_id AND sttd.language_id = ?)\n"
                  + "    WHERE \n"
                  + "    	sl.branch_id=? \n"
                  + where + "\n"
                  + "        AND sl.deleted=FALSE \n"
                  + "        GROUP by sl.id,usr.name,usr.surname,sl.is_return,\n"
                  + "        sl.totalmoney,\n"
                  + "        sl.currency_id,\n"
                  + "        sl.shift_id,usr.type_id\n"
                  + ")   \n"
                  + "SELECT \n"
                  + "    shf.id AS shfid,\n"
                  + "    shf.name AS  shfname,\n"
                  + "    shf.shiftno AS shfshiftno,\n"
                  + "    shf.begindate AS shfbegindate,\n"
                  + "    shf.enddate AS shfenddate,\n"
                  + "    shf.status_id AS shfstatus_id,\n"
                  + "    sttd.name AS sttdname,\n"
                  + "    shf.is_confirm AS shfis_confirm,\n"
                  + "   (SELECT \n"
                  + "          xmlelement(\n"
                  + "            name shifttotal,\n"
                  + "            xmlagg(\n"
                  + "              xmlelement(\n"
                  + "                  name shiftsaletotal,\n"
                  + "                  xmlforest (\n"
                  + "                  t.totalsaleamount AS totalsaleamount,\n"
                  + "                  t.currency_id AS currency_id ))))\n"
                  + "      FROM \n"
                  + "        (SELECT \n"
                  + "    		COALESCE(SUM(CASE WHEN sl.is_return THEN -sl.totalmoney ELSE sl.totalmoney END),0) AS totalsaleamount,\n"
                  + "                   COALESCE(sl.currency_id,0) AS currency_id\n"
                  + "    	FROM saledataa sl\n"
                  + "           WHERE \n"
                  + "            sl.type_id = 2\n"
                  + "           AND sl.shift_id=shf.id\n"
                  + "           group by sl.currency_id\n"
                  + "        ) as t\n"
                  + "    ) AS totalsaleamount,\n"
                  + "    COALESCE(remove.sumremove,0) AS removedstock,\n"
                  + "    CASE WHEN  actualprice.xmlactual IS NULL THEN  '<shifttotal/>' ELSE actualprice.xmlactual END as actualprice,\n"
                  + "    (SELECT CASE WHEN (SELECT count(slid) FROM saledataa sl\n"
                  + "         		    WHERE  sl.shift_id=shf.id\n"
                  + "                   ) > 0 THEN TRUE ELSE FALSE END\n"
                  + " )AS isAvailableSale,\n"
                  + "(SELECT \n"
                  + "          xmlelement(\n"
                  + "            name usershift,\n"
                  + "            xmlagg(\n"
                  + "              xmlelement(\n"
                  + "                  name usersaleshift,\n"
                  + "                  xmlforest (\n"
                  + "                  a.usrname AS usrname,\n"
                  + "                  a.usrsurname AS usrsurname\n"
                  + "                )\n"
                  + "              )\n"
                  + "            )\n"
                  + "          )\n"
                  + "      FROM \n"
                  + "        (SELECT \n"
                  + "    		 DISTINCT sl.userdata_id,\n"
                  + "             COALESCE(sl.usrname,'') AS usrname,\n"
                  + "             COALESCE(sl.usrsurname,'') AS usrsurname\n"
                  + "    	FROM saledataa sl\n"
                  + "           WHERE  sl.shift_id=shf.id AND sl.type_id = 2) as a\n"
                  + ") AS shiftperson\n"
                  + "FROM general.shift shf\n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = shf.status_id AND sttd.language_id = ?)\n"
                  + "\n"
                  + "LEFT JOIN \n"
                  + " (SELECT \n"
                  + "          xmlelement(\n"
                  + "            name shifttotal,\n"
                  + "            xmlagg(\n"
                  + "              xmlelement(\n"
                  + "                  name shiftsaletotal,\n"
                  + "                  xmlforest (\n"
                  + "                  c.totalsaleamount AS totalsaleamount,\n"
                  + "                  c.currency_id AS currency_id\n"
                  + "                )\n"
                  + "              )\n"
                  + "            )\n"
                  + "          ) as xmlactual,\n"
                  + "          c.shift_id as shift_id\n"
                  + "      FROM \n"
                  + "        (SELECT \n"
                  + "    		COALESCE(SUM(shp.actualprice),0) AS totalsaleamount,\n"
                  + "            COALESCE(shp.currency_id,0) AS currency_id,\n"
                  + "            shp.shift_id as shift_id\n"
                  + "    	FROM general.shiftpayment shp\n"
                  + "           WHERE shp.deleted=False\n"
                  + "        GROUP BY shp.currency_id,shp.shift_id \n"
                  + "        ) as c\n"
                  + "        GROUP BY  c.shift_id\n"
                  + "    ) \n"
                  + "     actualprice ON (actualprice.shift_id = shf.id)\n"
                  + "     LEFT JOIN(\n"
                  + "  SELECT\n"
                  + "       SUM(rs.removedvalue) as sumremove,\n"
                  + "       rs.shift_id as shift_id\n"
                  + "	  FROM \n"
                  + "       log.removedstock rs\n"
                  + "	  WHERE rs.branch_id = ?\n"
                  + "      GROUP BY  rs.shift_id) remove ON(remove.shift_id = shf.id )\n"
                  + "\n"
                  + "WHERE shf.deleted=False  AND shf.branch_id=?\n"
                  + where + "\n"
                  + "ORDER BY " + sortField + " " + sortOrder + "\n"
                  + " limit " + pageSize + " offset " + first;
        System.out.println("--------market shift----"+sql);
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Shift> result = getJdbcTemplate().query(sql, param, new MarketShiftMapper());
        return result;

    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                  + "COUNT(shf.id) AS shfid \n"
                  + "FROM  general.shift shf  \n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = shf.status_id AND sttd.language_id = ?)\n"
                  + "WHERE shf.branch_id=? AND shf.deleted=FALSE" + where + "\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int create(Shift obj, Boolean isOfflineControl) {
        String sql = "SELECT r_shift_id FROM general.process_shift (?, ?, ?, ?,?,?);";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), 1, null, sessionBean.getUser().getId(), obj.getName(), isOfflineControl};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Shift obj) {
        String sql = "SELECT r_shift_id FROM general.process_shift (?, ?, ?, ?,?,?);";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), 2, null, sessionBean.getUser().getId(), obj.getName(), false};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Açık vardiya olup olamdığını kontrol eden fonksiyondur. 0 -> Açık yok 1
     * -> Açık Vardiya var.
     *
     * @return
     */
    @Override
    public Shift controlHaveOpenShift() {
        String sql = "SELECT \n"
                  + "shf.id AS shfid,\n"
                  + "shf.shiftno AS shfshiftno\n"
                  + "FROM general.shift shf WHERE shf.status_id = 7 AND shf.branch_id = ? AND shf.deleted = False";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, new MarketShiftMapper());
        } catch (EmptyResultDataAccessException e) {
            return new Shift();
        }
    }

    @Override
    public int delete(Shift shift) {
        String sql = "UPDATE general.shift SET deleted=TRUE, u_id=? , d_time=NOW() WHERE deleted=False AND id=?;";

        Object[] param = new Object[]{sessionBean.getUser().getId(), shift.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int updateShift(Shift shift) {
        String sql = "UPDATE general.shift SET name=?, u_id=? , u_time=NOW() WHERE deleted=False AND id=?;";

        Object[] param = new Object[]{shift.getName(), sessionBean.getUser().getId(), shift.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String shiftStockDetail(Shift shift) {
        String sql = "SELECT \n"
                  + "stck.id AS stckid,\n"
                  + "stck.name AS stckname,\n"
                  + "stck.barcode AS stckbarcode,\n"
                  + "gunt.unitrounding AS guntunitrounding,\n"
                  + "gunt.sortname AS guntsortname, \n"
                  + "SUM((CASE WHEN sl.is_return = TRUE THEN 0 ELSE 1 END) * sli.quantity) as totalsalecount,\n"
                  + "SUM(CASE WHEN sl.is_return = TRUE THEN sli.quantity ELSE 0 END) totalreturncount,\n"
                  + "SUM((CASE WHEN sl.is_return = TRUE THEN 0 ELSE 1 END) * ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,1))) totalsalemoney,\n"
                  + "SUM(CASE WHEN sl.is_return = TRUE THEN (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,1) ELSE 0 END) totalreturnmoney,\n"
                  + "sli.taxrate AS slitaxrate,\n"
                  + " SUM(CASE WHEN sl.is_return = TRUE THEN  0  ELSE (sli.totaltax)* COALESCE(sli.exchangerate,1) END) AS totaltax,\n"
                  + " sli.unitprice * COALESCE(sli.exchangerate,1) AS sliunitprice,\n"
                  + " sl.currency_id\n"
                  + "FROM\n"
                  + "	 general.saleitem sli\n"
                  + "    INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted = FALSE )\n"
                  + "    INNER JOIN system.currency cr ON(cr.id = sl.currency_id)\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id = sli.stock_id AND stck.deleted = FALSE)\n"
                  + "    INNER JOIN general.unit gunt ON(gunt.id  = stck.unit_id AND gunt.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE\n"
                  + "sli.deleted = FALSE AND usr.type_id = 2 AND sl.shift_id = " + shift.getId() + " \n"
                  + "GROUP BY stck.id,stck.name,stck.unit_id,gunt.sortname,sli.taxrate, sli.unitprice, sl.currency_id,gunt.unitrounding,sli.exchangerate;";
        return sql;
    }

    @Override
    public String shiftTaxRateDetail(Shift shift) {
        String sql = "SELECT \n"
                  + "sli.taxrate AS slitaxrate,\n"
                  + "COALESCE(SUM((CASE WHEN sl.is_return=FALSE THEN 1 ELSE 0 END) * sli.quantity),0) totalsalecount,\n"
                  + "COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN sli.quantity ELSE 0 END),0) totalreturncount,\n"
                  + "COALESCE(SUM((CASE WHEN sl.is_return=FALSE THEN 1 ELSE 0 END) * ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,0))),0) totalsalemoney,\n"
                  + "COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,0) ELSE 0 END),0) totalreturnmoney,\n"
                  + "COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN  0  ELSE (sli.totaltax * COALESCE(sli.exchangerate,0)) END),0) AS totaltax,\n"
                  + " sl.currency_id\n"
                  + "FROM\n"
                  + "	 general.saleitem sli\n"
                  + "    INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.shift_id = " + shift.getId() + " AND sl.deleted = FALSE)\n"
                  + "    INNER JOIN system.currency cr ON(cr.id = sl.currency_id)\n"
                  + "    INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE\n"
                  + "sli.deleted = FALSE AND usr.type_id = 2\n"
                  + "GROUP BY sli.taxrate,  sl.currency_id;";
        return sql;
    }

    @Override
    public String shiftCurrencyDetail(Shift shift) {
        String sql = "SELECT \n"
                  + "SUM(CASE WHEN sl.is_return=FALSE THEN sl.totalmoney ELSE -sl.totalmoney END) totalsalemoney,\n"
                  + " sl.currency_id AS slcurrency_id,\n"
                  + "crd.name AS crdname,\n"
                  + "cr.code AS crcode\n"
                  + "FROM\n"
                  + "	 general.sale sl \n"
                  + "    INNER JOIN system.currency cr ON(cr.id = sl.currency_id)\n"
                  + "    INNER JOIN system.currency_dict crd ON(crd.currency_id = cr.id AND crd.language_id = " + sessionBean.getUser().getLanguage().getId() + " )"
                  + "    INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE\n"
                  + "sl.shift_id =" + shift.getId() + " AND sl.deleted = FALSE AND usr.type_id = 2\n"
                  + "GROUP BY sl.currency_id,crd.name,cr.code;";
        return sql;
    }

    @Override
    public String shiftCashierPaymentCashDetail(Shift shift) {
        String sql = "SELECT\n"
                  + "      	 shp.id AS shpid,  \n"
                  + "          shp.account_id AS shpaccount_id,  \n"
                  + "          acc.name AS accname,  \n"
                  + "          acc.title AS acctitle,  \n"
                  + "           shp.actualprice AS accualprice,  \n"
                  + "          (shp.actualprice*COALESCE( shp.exchangerate,1))AS shpaccualprice,  \n"
                  + "          shp.currency_id AS shpcurrency_id,  \n"
                  + "          cr.code   AS crcode , \n  "
                  + "         COALESCE( shp.exchangerate,1) AS shpexchangerate,  \n"
                  + "          shp.safe_id AS shpsafe_id,  \n"
                  + "          sf.name AS sfname,\n"
                  + "          sf.code AS   sfcode , \n"
                  + "          shp.saleprice AS shpsaleprice  \n"
                  + "      FROM general.shiftpayment shp  \n"
                  + "          INNER JOIN system.currency cr ON(cr.id = shp.currency_id)\n"
                  + "          INNER JOIN general.account acc ON(acc.id=shp.account_id)  \n"
                  + "          INNER JOIN system.type_dict typd ON (typd.type_id = shp.saletype_id AND typd.language_id =" + sessionBean.getUser().getLanguage().getId() + ")  \n"
                  + "          LEFT JOIN finance.bankaccount ba ON(ba.id=shp.bankaccount_id)  \n"
                  + "          LEFT JOIN finance.safe sf ON(sf.id=shp.safe_id)  \n"
                  + "      WHERE shp.deleted=False AND shp.saletype_id = 17 AND shp.shift_id= " + shift.getId() + " AND shp.actualprice > 0 AND EXISTS(SELECT usr.id FROM general.userdata usr WHERE usr.account_id = acc.id)";
        return sql;
    }

    @Override
    public String shiftCashierPaymentBankDetail(Shift shift) {
        String sql = "SELECT\n"
                  + "      	 shp.id AS shpid,  \n"
                  + "          shp.account_id AS shpaccount_id,  \n"
                  + "          acc.name AS accname,  \n"
                  + "          acc.title AS acctitle,  \n"
                  + "          shp.actualprice AS accualprice,\n"
                  + "          (shp.actualprice*COALESCE( shp.exchangerate,1))AS shpaccualprice,  \n"
                  + "          shp.bankaccount_id AS shpbankaccount_id,  \n"
                  + "          ba.name AS baname,\n "
                  + "           bnk.code AS bnkcode, \n"
                  + "           bnk.name AS bnkname, \n"
                  + "          shp.currency_id AS shpcurrency_id,  \n"
                  + "         cr.code AS crcode,\n"
                  + "         COALESCE( shp.exchangerate,1) AS shpexchangerate,  \n"
                  + "          shp.saleprice AS shpsaleprice,  \n"
                  + "          shp.saletype_id AS shpsaletype_id  \n"
                  + "      FROM general.shiftpayment shp  \n"
                  + "          INNER JOIN system.currency cr ON(cr.id = shp.currency_id)\n"
                  + "          INNER JOIN general.account acc ON(acc.id=shp.account_id)  \n"
                  + "          INNER JOIN system.type_dict typd ON (typd.type_id = shp.saletype_id AND typd.language_id =" + sessionBean.getUser().getLanguage().getId() + ")  \n"
                  + "          LEFT JOIN finance.bankaccount ba ON(ba.id=shp.bankaccount_id)  \n"
                  + "          LEFT JOIN finance.bankbranch bbr ON(bbr.id = ba.bankbranch_id)  \n"
                  + "          LEFT JOIN finance.bank bnk ON(bnk.id =bbr.bank_id)  \n"
                  + "          LEFT JOIN finance.safe sf ON(sf.id=shp.safe_id)  \n"
                  + "      WHERE shp.deleted=False AND shp.saletype_id IN (18,75) AND shp.shift_id= " + shift.getId() + " AND shp.actualprice > 0 AND EXISTS(SELECT usr.id FROM general.userdata usr WHERE usr.account_id = acc.id)";
        return sql;
    }

    @Override
    public String shiftDeficitGiveMoney(Shift shift) {
        String sql = "SELECT\n"
                  + "	subtable.name AS name, \n"
                  + "    SUM (CASE WHEN subtable.fiemis_direction  = TRUE THEN subtable.money ELSE 0 END ) income, \n"
                  + "    SUM(CASE WHEN  subtable.fiemis_direction  = FALSE THEN subtable.money ELSE 0 END  ) expense \n"
                  + "FROM \n"
                  + "  (SELECT \n"
                  + "  			fie.name, \n"
                  + "           fiem.is_direction AS fiemis_direction,--(-)eksi gider (+)Gelir \n"
                  + "           SUM(fiem.price * COALESCE( fiem.exchangerate,0)) AS money  \n"
                  + "       FROM general.shiftpayment shp   \n"
                  + "           INNER JOIN general.account acc ON(acc.id=shp.account_id)   \n"
                  + "           INNER JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.shiftpayment_id = shp.id AND shpcon.deleted=FALSE)  \n"
                  + "           INNER JOIN finance.financingdocument fdoc ON(fdoc.id=shpcon.financingdocument_id AND fdoc.deleted=False)\n"
                  + "           INNER JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False) \n"
                  + "           INNER JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id AND fie.deleted = FALSE)\n"
                  + "       WHERE shp.deleted=False AND EXISTS(SELECT usr.id FROM general.userdata usr WHERE usr.account_id = acc.id) AND shpcon.is_inherited = FALSE AND shp.shift_id= " + shift.getId() + "\n"
                  + " GROUP BY  fiem.is_direction ,fie.name\n"
                  + " )subtable GROUP BY subtable.name";
        return sql;
    }

    @Override
    public String shiftDeficitGiveMoneyEmployee(Shift shift) {
        String sql = "SELECT \n"
                  + "	SUM (CASE WHEN subtable.fdoctype_id  = 50 THEN subtable.money ELSE 0 END ) fazla,\n"
                  + "    SUM(CASE WHEN ( subtable.fdoctype_id = 48 OR subtable.fdoctype_id = 56 OR subtable.fdoctype_id = 74) THEN subtable.money ELSE 0 END  ) borc,\n"
                  + "    subtable.accname AS accountname,\n"
                  + "    subtable.acctitle AS accountsurname\n"
                  + " FROM\n"
                  + " (\n"
                  + "         SELECT\n"
                  + "             shp.account_id AS shpaccount_id,   \n"
                  + "             acc.name AS accname,   \n"
                  + "            acc.title AS acctitle,  \n"
                  + "             fdoc.type_id AS fdoctype_id, \n"
                  + "            sum( CASE WHEN fiem.id IS NULL THEN  fdoc.price * COALESCE(fdoc.exchangerate,0)  ELSE 0 END) AS money\n"
                  + "         FROM general.shiftpayment shp   \n"
                  + "         	INNER JOIN general.account acc ON(acc.id=shp.account_id)   \n"
                  + "            INNER JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.shiftpayment_id = shp.id AND shpcon.deleted=FALSE)\n"
                  + "            INNER JOIN finance.financingdocument fdoc ON(fdoc.id=shpcon.financingdocument_id AND fdoc.deleted=False)   \n"
                  + "            LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False) \n"
                  + "\n"
                  + "         WHERE shp.deleted=False AND EXISTS(SELECT usr.id FROM general.userdata usr WHERE usr.account_id = acc.id) AND shpcon.is_inherited = FALSE AND shp.shift_id=" + shift.getId() + " \n"
                  + "         GROUP BY   acc.name , acc.title , fdoc.type_id,shp.account_id\n"
                  + ")subtable\n"
                  + "GROUP BY  subtable.accname, subtable.acctitle";
        return sql;
    }

    @Override
    public String shiftSummary(Shift shift) {
        String sql = "SELECT\n"
                  + "COALESCE( subsale.totalsalemoney,0) AS totalsalemoney,\n"
                  + "COALESCE( subsale.totalreturnmoney,0) AS totalreturnmoney,\n"
                  + "COALESCE( shfp.gelir,0) AS gelir,  \n"
                  + "COALESCE( shfp.gider,0) AS gider,\n"
                  + "COALESCE( ptype.banka,0) AS banka, \n"
                  + "COALESCE( ptype.nakit,0) AS nakit ,\n"
                  + "COALESCE( nktt.nakittahsilat,0) AS nakittahsilat,\n"
                  + "COALESCE( bnk.bankatahsilat,0) AS bankatahsilat, \n"
                  + "COALESCE( crd.veresiye,0) AS veresiye ,\n"
                  + "COALESCE( open.acık,0) AS acık, \n"
                  + "COALESCE( employee.income,0) AS  employeeincome,\n"
                  + "COALESCE( employee.expense,0) AS employeeexpense,\n"
                  + "( COALESCE( subsale.totalsalemoney,0)+ COALESCE( employee.income,0) +  COALESCE( shfp.gelir,0)) AS girentoplam,   \n"
                  + "(COALESCE( subsale.totalreturnmoney,0)+COALESCE(  nktt.nakittahsilat,0)+ COALESCE(bnk.bankatahsilat,0) +  COALESCE( employee.expense,0) +  COALESCE( shfp.gider,0)+ COALESCE( crd.veresiye,0)+ COALESCE( open.acık,0) ) AS cıkantoplam \n"
                  // + "(COALESCE( subsale.totalsalemoney,0)-( COALESCE( subsale.totalsalemoney,0)+ COALESCE( employee.income,0) +  COALESCE( shfp.gelir,0))) AS  diffin,\n"
                  // + "(COALESCE( subsale.totalsalemoney,0)-(COALESCE( subsale.totalreturnmoney,0)+COALESCE(  bnk.bankatahsilat,0)+ COALESCE( nktt.nakittahsilat,0) +  COALESCE( employee.expense,0) +  COALESCE( shfp.gider,0) + COALESCE( crd.veresiye,0)+ COALESCE( open.acık,0) ) ) AS  diffout \n"
                  + " \n"
                  + "FROM\n"
                  + "	general.shift shf\n"
                  + "    LEFT JOIN (\n"
                  + "    (SELECT \n"
                  + "    	SUM((CASE WHEN sl.is_return = TRUE THEN 0 ELSE 1 END) * sl.totalmoney) totalsalemoney,\n"
                  + "		SUM(CASE WHEN sl.is_return = TRUE THEN sl.totalmoney ELSE 0 END) totalreturnmoney,\n"
                  + "        sl.shift_id AS shift\n"
                  + "		FROM general.sale sl    \n"
                  + "           INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + " 	WHERE  sl.deleted = FALSE AND usr.type_id = 2\n"
                  + "        GROUP BY sl.shift_id\n"
                  + "    )\n"
                  + "    )  subsale ON(subsale.shift = shf.id)\n"
                  + "    LEFT JOIN\n"
                  + "    (\n"
                  + "    	SELECT\n"
                  + "          shp.shift_id,\n"
                  + "          SUM(CASE WHEN  fiem.is_direction = TRUE THEN fiem.price*fiem.exchangerate ELSE 0 END) gelir,\n"
                  + "          SUM(CASE WHEN  fiem.is_direction = FALSE THEN fiem.price*fiem.exchangerate ELSE 0 END) gider\n"
                  + "      FROM general.shiftpayment shp  \n"
                  + "          INNER JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.shiftpayment_id = shp.id AND shpcon.deleted=FALSE)\n"
                  + "          INNER JOIN finance.financingdocument fdoc ON(fdoc.id=shpcon.financingdocument_id AND fdoc.deleted=False)  \n"
                  + "          INNER JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n"
                  + "      WHERE shp.deleted=False AND shpcon.is_inherited = FALSE\n"
                  + "         GROUP BY shp.shift_id\n"
                  + "    ) shfp  ON( shfp.shift_id=shf.id )\n"
                  + "    LEFT JOIN (\n"
                  + "          SELECT\n"
                  + "            sl.shift_id\n"
                  + "           ,SUM(((CASE WHEN  sl.is_return=FALSE THEN 1 ELSE -1 END) * CASE  WHEN  sl.invoice_id IS NOT NULL AND sp.type_id = 17 THEN  COALESCE  (ip.price,0) * COALESCE (ip.exchangerate,0)   \n"
                  + "          	WHEN  sl.invoice_id IS NULL  AND  sp.type_id = 17 THEN  COALESCE (sp.price,0) * COALESCE (sp.exchangerate,0) END)) nakit\n"
                  + "           ,SUM(CASE    WHEN   sl.invoice_id IS NOT NULL  AND (sp.type_id = 18 OR sp.type_id = 75) THEN  COALESCE  (ip.price,0) * COALESCE (ip.exchangerate,0)   \n"
                  + "          	WHEN  sl.invoice_id IS  NULL AND  (  sp.type_id = 18  OR sp.type_id = 75) THEN  COALESCE (sp.price,0) * COALESCE (sp.exchangerate,0) END) banka \n"
                  + "          FROM\n"
                  + "        	general.sale sl\n"
                  + "            LEFT JOIN general.salepayment sp ON(sp.sale_id = sl.id AND sl.deleted = FALSE)\n"
                  + "            LEFT JOIN finance.invoicepayment ip ON(ip.invoice_id = sl.invoice_id AND ip.deleted = FALSE)\n"
                  + "            INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "        WHERE\n"
                  + "       sl.deleted = FALSE AND usr.type_id = 2 AND (sp.type_id IN(17,18,75) OR ip.type_id IN (17,18,75))\n"
                  + "          GROUP BY sl.shift_id\n"
                  + "    ) ptype  ON(  ptype.shift_id = shf.id )\n"
                  + "    LEFT JOIN (\n"
                  + "          SELECT\n"
                  + "             shp.shift_id,\n"
                  + "               SUM(COALESCE(shp.actualprice,0) * COALESCE (shp.exchangerate,1.0)) AS nakittahsilat \n"
                  + "            FROM general.shiftpayment shp  \n"
                  + "            WHERE shp.deleted=False AND  shp.saletype_id = 17 \n"
                  + "            GROUP BY shp.shift_id\n"
                  + "    \n"
                  + "    ) nktt ON(nktt.shift_id = shf.id) \n"
                  + " LEFT JOIN (\n"
                  + "          SELECT\n"
                  + "             shp.shift_id,\n"
                  + "               SUM(COALESCE(shp.actualprice,0) * COALESCE (shp.exchangerate,1.0)) AS bankatahsilat \n"
                  + "            FROM general.shiftpayment shp  \n"
                  + "            WHERE shp.deleted=False AND  shp.saletype_id IN( 18,75) \n"
                  + "            GROUP BY shp.shift_id\n"
                  + "    \n"
                  + "    ) bnk ON(bnk.shift_id = shf.id)"
                  + "  LEFT JOIN (SELECT\n"
                  + "subtable.shift_id,\n"
                  + "SUM(subtable.veresiye) AS  veresiye\n"
                  + " FROM\n"
                  + " (\n"
                  + "    SELECT\n"
                  + "      COALESCE(sl.shift_id) AS shift_id\n"
                  + "      ,SUM(((CASE WHEN  sl.is_return=FALSE THEN 1 ELSE -1 END) * COALESCE(crd.money,0) * COALESCE( crd.exchangerate, 0))) veresiye\n"
                  + "     FROM\n"
                  + "      finance.credit crd\n"
                  + "      LEFT JOIN general.salepayment slp ON(slp.credit_id = crd.id AND slp.deleted = FALSE)\n"
                  + "        LEFT JOIN general.sale sl ON(sl.id = slp.sale_id AND sl.deleted = FALSE  )\n"
                  + "      INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "    WHERE \n"
                  + "    crd.deleted=FALSE  AND usr.type_id= 2\n"
                  + "    GROUP BY sl.shift_id\n"
                  + "\n"
                  + ") subtable  GROUP BY subtable.shift_id \n"
                  + " ) crd  ON(  crd.shift_id = shf.id) \n"
                  + "LEFT JOIN (\n"
                  + "         SELECT\n"
                  + "           sl.shift_id\n"
                  + "          ,SUM(CASE  WHEN  sl.invoice_id IS NOT NULL AND ip.id IS NULL THEN  COALESCE  (sl.totalmoney,0)  \n"
                  + "            WHEN  sl.invoice_id IS NULL  AND sp.id IS NULL  THEN  COALESCE (sl.totalmoney,0)   END) AS  acık\n"
                  + "         FROM\n"
                  + "        general.sale sl\n"
                  + "        LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "           LEFT JOIN general.salepayment sp ON(sp.sale_id = sl.id AND sl.deleted = FALSE)\n"
                  + "           LEFT JOIN finance.invoicepayment ip ON(ip.invoice_id = sl.invoice_id AND ip.deleted = FALSE)\n"
                  + "           INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "       WHERE\n"
                  + "      sl.deleted = FALSE AND  COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  AND   sl.is_return=FALSE AND usr.type_id = 2\n"
                  + "         GROUP BY sl.shift_id\n"
                  + "   ) open  ON(  open.shift_id = shf.id )"
                  + " LEFT JOIN( SELECT \n"
                  + "   	  employee2.shift_id,\n"
                  + "      SUM (CASE WHEN employee2.fdoctype_id  = 50 THEN employee2.money ELSE 0 END ) income, \n"
                  + "      SUM(CASE WHEN ( employee2.fdoctype_id = 48 OR employee2.fdoctype_id = 56 OR employee2.fdoctype_id = 74) THEN employee2.money ELSE 0 END  ) expense\n"
                  + "     FROM \n"
                  + "     ( \n"
                  + "             SELECT \n"
                  + "             shp.shift_id,\n"
                  + "                 fdoc.type_id AS fdoctype_id,  \n"
                  + "                sum( CASE WHEN fiem.id IS NULL THEN  fdoc.price * COALESCE(fdoc.exchangerate,0)  ELSE 0 END) AS money \n"
                  + "             FROM general.shiftpayment shp    \n"
                  + "              INNER JOIN general.account acc ON(acc.id=shp.account_id)    \n"
                  + "                INNER JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.shiftpayment_id = shp.id AND shpcon.deleted=FALSE)\n"
                  + "                INNER JOIN finance.financingdocument fdoc ON(fdoc.id=shpcon.financingdocument_id AND fdoc.deleted=False)    \n"
                  + "                LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)  \n"
                  + "           \n"
                  + "             WHERE shp.deleted=False AND shpcon.is_inherited = FALSE AND EXISTS(SELECT usr.id FROM general.userdata usr WHERE usr.account_id = acc.id)\n"
                  + "             GROUP BY   fdoc.type_id, shp.shift_id \n"
                  + "    )employee2 GROUP BY employee2.shift_id\n"
                  + ") employee  ON( employee.shift_id = shf.id )\n"
                  + "WHERE\n"
                  + "   shf.id = " + shift.getId() + "";
        return sql;
    }

    @Override
    public String shiftGeneral(Shift shift) {
        String sql = "SELECT\n"
                  + "    shf.name AS name, \n"
                  + "    COALESCE( subtable.totalsalemoney,0) AS totalsalemoney,\n"
                  + "    COALESCE( subtable.totalreturnmoney,0) AS totalreturnmoney,\n"
                  + "    COALESCE (ptype.nakit,0) AS nakit,\n"
                  + "    COALESCE (ptype.banka,0) AS banka,\n"
                  + "    COALESCE (credit.creditprice,0) AS veresiye, \n"
                  + "    COALESCE (open.acık,0) AS acık \n "
                  + "FROM general.shift shf\n"
                  + "LEFT JOIN (SELECT\n"
                  + "              SUM((CASE WHEN sl.is_return = TRUE THEN 0 ELSE 1 END) * sl.totalmoney) totalsalemoney,\n"
                  + "              SUM(CASE WHEN sl.is_return = TRUE THEN sl.totalmoney ELSE 0 END) totalreturnmoney,\n"
                  + "              sl.shift_id AS shift\n"
                  + "            FROM general.sale sl    \n"
                  + "            INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "            WHERE  sl.deleted = FALSE AND usr.type_id = 2\n"
                  + "  GROUP BY sl.shift_id\n"
                  + "  )subtable ON (subtable.shift = shf.id)\n"
                  + "  LEFT JOIN (\n"
                  + "         SELECT\n"
                  + "           sl.shift_id\n"
                  + "          ,SUM(CASE  WHEN  sl.invoice_id IS NOT NULL AND sp.type_id = 17 THEN  COALESCE  (ip.price,0) * COALESCE (ip.exchangerate,0)   \n"
                  + "            WHEN  sl.invoice_id IS NULL  AND  sp.type_id = 17 THEN  COALESCE (sp.price,0) * COALESCE (sp.exchangerate,0) END) nakit \n"
                  + "          ,SUM(CASE    WHEN   sl.invoice_id IS NOT NULL  AND (sp.type_id = 18 OR sp.type_id = 75 )THEN  COALESCE  (ip.price,0) * COALESCE (ip.exchangerate,0)   \n"
                  + "            WHEN  sl.invoice_id IS  NULL AND   ( sp.type_id = 18 OR sp.type_id = 75 ) THEN  COALESCE (sp.price,0) * COALESCE (sp.exchangerate,0) END) banka \n"
                  + "         FROM\n"
                  + "        general.sale sl\n"
                  + "        LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "        LEFT JOIN general.salepayment sp ON(sp.sale_id = sl.id AND sl.deleted = FALSE)\n"
                  + "        LEFT JOIN finance.invoicepayment ip ON(ip.invoice_id = sl.invoice_id AND ip.deleted = FALSE)\n"
                  + "        INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "       WHERE\n"
                  + "      sl.deleted = FALSE AND usr.type_id = 2 AND  COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND   sl.is_return=FALSE AND (sp.type_id IN(17,18,75) OR ip.type_id IN (17,18,75))\n"
                  + "         GROUP BY sl.shift_id\n"
                  + "   ) ptype  ON(  ptype.shift_id = shf.id )\n"
                  + "  LEFT JOIN (\n"
                  + "     SELECT \n"
                  + "         sl.shift_id,\n"
                  + "          SUM(crd.money*COALESCE(crd.exchangerate,0)) AS creditprice \n"
                  + "     FROM \n"
                  + "         finance.credit crd \n"
                  + "         INNER JOIN general.account acc ON(acc.id = crd.account_id)\n"
                  + "         LEFT JOIN general.salepayment sp ON(sp.credit_id = crd.id AND sp.deleted = FALSE)\n"
                  + "         LEFT JOIN general.sale sl  ON(sl.id = sp.sale_id ANd sp.deleted = FALSE) \n"
                  + "         LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False)\n"
                  + "         LEFT JOIN finance.invoicepayment ip ON(ip.credit_id  = crd.id AND ip.deleted = FALSE)\n"
                  + "         LEFT JOIN finance.invoice inv ON(inv.id = ip.invoice_id AND inv.deleted = FALSE)\n"
                  + "         INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "     WHERE\n"
                  + "             crd.deleted = FALSE AND usr.type_id = 2 AND  COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND   sl.is_return=FALSE\n"
                  + "     GROUP BY\n"
                  + "         sl.shift_id\n"
                  + "   ) credit  ON(  credit.shift_id = shf.id )\n"
                  + "LEFT JOIN (\n"
                  + "         SELECT\n"
                  + "           sl.shift_id\n"
                  + "          ,SUM(CASE  WHEN  sl.invoice_id IS NOT NULL AND ip.id IS NULL THEN  COALESCE  (sl.totalmoney,0)  \n"
                  + "            WHEN  sl.invoice_id IS NULL  AND sp.id IS NULL  THEN  COALESCE (sl.totalmoney,0)   END) AS  acık \n"
                  + "         FROM\n"
                  + "        general.sale sl\n"
                  + "        LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "           LEFT JOIN general.salepayment sp ON(sp.sale_id = sl.id AND sl.deleted = FALSE)\n"
                  + "           LEFT JOIN finance.invoicepayment ip ON(ip.invoice_id = sl.invoice_id AND ip.deleted = FALSE)\n"
                  + "           INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "       WHERE\n"
                  + "      sl.deleted = FALSE AND usr.type_id = 2 AND  COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)  AND   sl.is_return=FALSE \n"
                  + "         GROUP BY sl.shift_id\n"
                  + "   ) open  ON(  open.shift_id = shf.id )"
                  + "               \n"
                  + "   WHERE shf.id = " + shift.getId() + " \n";
        return sql;
    }

    @Override
    public String shiftCreditPaymentDetail(Shift shift) {
        String sql = "SELECT\n"
                  + "	acc.id,    \n"
                  + "    CASE WHEN acc.is_employee THEN acc.name || ' ' ||acc.title ELSE acc.name  END AS name,\n"
                  + "    crd.currency_id AS crdcurrency_id,\n"
                  + "    cr.code AS crcode,\n"
                  + "    crd.exchangerate AS exchangerate,\n"
                  + "    COALESCE(SUM(crd.money),0) AS price,\n"
                  + "     COALESCE(SUM(crd.money*COALESCE(crd.exchangerate,0)),0) AS totalprice \n"
                  + "FROM \n"
                  + "	finance.credit crd \n"
                  + "    INNER JOIN system.currency cr ON(cr.id = crd.currency_id)\n"
                  + "	INNER JOIN general.account acc ON(acc.id = crd.account_id)\n"
                  + "    LEFT JOIN general.salepayment sp ON(sp.credit_id = crd.id AND sp.deleted = FALSE)\n"
                  + "    LEFT JOIN general.sale sl  ON(sl.id = sp.sale_id ANd sp.deleted = FALSE) \n"
                  + "    LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False)\n"
                  + "    LEFT JOIN finance.invoicepayment ip ON(ip.credit_id  = crd.id AND ip.deleted = FALSE)\n"
                  + "    LEFT JOIN finance.invoice inv ON(inv.id = ip.invoice_id AND inv.deleted = FALSE)\n"
                  + "    INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE\n"
                  + "	crd.deleted = FALSE AND usr.type_id = 2 AND sl.shift_id =  " + shift.getId() + "  AND  COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND   sl.is_return=FALSE\n"
                  + "GROUP BY\n"
                  + "acc.id,crd.exchangerate,crd.currency_id,cr.code  \n";
        return sql;
    }

    @Override
    public List<Invoice> controlOpenAmountInvoice(Shift shift) {

        String sql = "SELECT\n"
                  + "    inv.id AS invid,\n"
                  + "    COALESCE(inv.documentserial,'') || inv.documentnumber AS documentno\n"
                  + "FROM general.sale sl\n"
                  + "	LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted=False AND sll.shift_id = sl.shift_id)\n"
                  + "	LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted=False)\n"
                  + "   INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE sl.branch_id=? AND sl.shift_id=?\n"
                  + "	AND sl.deleted=False AND sl.is_return=False AND usr.type_id = 2\n"
                  + "	AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                  + "GROUP BY inv.id\n"
                  + "HAVING COALESCE(SUM(inv.remainingmoney),0) > 0";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), shift.getId()};

        List<Invoice> result = getJdbcTemplate().query(sql, param, new MarketShiftInvoiceMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftStockGroupDetail(Shift shift) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        int branchId = sessionBean.getUser().getLastBranch().getId();

        String sql = " Select * from general.list_marketshiftcategorysales (?, ?, ?, ?)";

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId(), shift.getBeginDate(), shift.getEndDate()};

        try {
            return getJdbcTemplate().query(sql, param, new MarketShiftPreviewMapper());
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }

    }

    @Override
    public String shiftStockGroupDetailWithoutCategories(Shift shift) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                  + "    -1 AS categoryid,\n"
                  + "    '' AS category,\n"
                  + "    COALESCE(SUM(t.salesAmount),0) AS salestotal,\n"
                  + "    COALESCE(SUM(t.salesQuantity),0) AS salesquantity,\n"
                  + "    COALESCE(SUM(t.previoussalequantity),0) AS previoussalequantity,\n"
                  + "    COALESCE(SUM(t.previoussaletotal),0) AS previoussaletotal,\n"
                  + "    COALESCE(SUM(t.entryQuantity),0) AS girismiktar,\n"
                  + "    COALESCE(SUM(t.exitQuantity),0) AS cikismiktar,\n"
                  + "    COALESCE(SUM(t.remainingAmount),0) AS remainingPrice,\n"
                  + "    COALESCE(SUM(t.previousamountbeforeshift),0) AS previousamountbeforeshift,\n"
                  + "    COALESCE(SUM(t.previouspricebeforeshift),0) AS previouspricebeforeshift\n"
                  + "FROM(\n"
                  + "SELECT\n"
                  + "    COALESCE(SUM((CASE WHEN sl.is_return = false THEN 1 ELSE -1 END) *  ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,1))),0) AS salesAmount,\n"
                  + "    COALESCE(SUM(CASE WHEN sl.is_return = false THEN  sli.quantity ELSE -sli.quantity  END),0) AS salesQuantity,\n"
                  + "	(\n"
                  + "    	SELECT\n"
                  + "        	COALESCE(SUM((CASE WHEN  iwr.is_canceled = TRUE THEN  -wm.quantity ELSE wm.quantity END)),0) AS entry\n"
                  + "        FROM inventory.warehousemovement wm \n"
                  + "        	INNER JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "            ------Fatura------------\n"
                  + "            LEFT JOIN finance.waybill_warehousereceipt_con wrc ON(wrc.warehousereceipt_id = iwr.id AND wrc.deleted = FALSE)\n"
                  + "            LEFT JOIN finance.waybill wb ON(wb.id = wrc.waybill_id AND wb.deleted = FALSE)\n"
                  + "            LEFT JOIN finance.waybill_invoice_con wbc ON(wbc.waybill_id = wb.id ANd wbc.deleted = FALSE)\n"
                  + "            LEFT JOIN  finance.invoice inv ON(inv.id = wbc.invoice_id AND inv.deleted = FALSE AND inv.is_purchase = FALSE )\n"
                  + "            LEFT JOIN general.sale sl2 ON(sl2.invoice_id = inv.id AND sl2.deleted = FALSE AND sl2.is_return = iwr.is_direction )\n"
                  + "            LEFT JOIN general.userdata usr ON(usr.id = sl2.userdata_id)\n"
                  + "             ------Satış Fişi--------\n"
                  + "            LEFT JOIN finance.receipt_warehousereceipt_con rwc ON(rwc.warehousereceipt_id = iwr.id AND rwc.deleted = FALSE )\n"
                  + "            LEFT JOIN  finance.receipt rc ON(rc.id = rwc.receipt_id AND rc.deleted = FALSE)              \n"
                  + "            LEFT JOIN general.sale sl1 ON(sl1.receipt_id = rc.id AND sl1.deleted = FALSE  )\n"
                  + "        WHERE wm.deleted = FALSE AND ((usr.type_id = 2 and sl1.id IS NULL) OR (sl2.id IS NULL)) AND iwr.type_id IN (60,61)  AND wm.stock_id = sli.stock_id AND ( ( sl1.shift_id  < " + shift.getId() + ") OR ( sl2.shift_id  < " + shift.getId() + ") ) \n"
                  + "    ) AS previoussalequantity,\n"
                  + "    (\n"
                  + "    	SELECT\n"
                  + "        	COALESCE(SUM((CASE WHEN iwr.is_canceled = TRUE THEN  -1 ELSE 1 END) *  (CASE WHEN rc.id > 0 THEN ((sli1.totalmoney - ((sli1.totalmoney * COALESCE(sl1.discountrate, 0))/100)) * COALESCE(sli1.exchangerate,1)) WHEN inv.id > 0 THEN  (invii.totalmoney * COALESCE(invii.exchangerate,1) * COALESCE(inv.exchangerate, 1)) END)),0) AS entry\n"
                  + "        FROM inventory.warehousemovement wm \n"
                  + "        	  INNER JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "              ------Fatura------------\n"
                  + "              LEFT JOIN finance.waybill_warehousereceipt_con wrc ON(wrc.warehousereceipt_id = iwr.id AND wrc.deleted = FALSE)\n"
                  + "              LEFT JOIN finance.waybill wb ON(wb.id = wrc.waybill_id AND wb.deleted = FALSE)\n"
                  + "              LEFT JOIN finance.waybill_invoice_con wbc ON(wbc.waybill_id = wb.id ANd wbc.deleted = FALSE)\n"
                  + "              LEFT JOIN  finance.invoice inv ON(inv.id = wbc.invoice_id AND inv.deleted = FALSE)\n"
                  + "              LEFT JOIN finance.invoiceitem invii ON(invii.invoice_id = inv.id AND invii.stock_id = sli.stock_id AND invii.deleted = FALSE)\n"
                  + "              LEFT JOIN general.sale sl2 ON(sl2.invoice_id = inv.id AND sl2.deleted = FALSE AND sl2.is_return = iwr.is_direction )\n"
                  + "              LEFT JOIN general.userdata usr ON(usr.id = sl2.userdata_id)\n"
                  + "              ------Satış Fişi--------\n"
                  + "              LEFT JOIN finance.receipt_warehousereceipt_con rwc ON(rwc.warehousereceipt_id = iwr.id AND rwc.deleted = FALSE )\n"
                  + "              LEFT JOIN  finance.receipt rc ON(rc.id = rwc.receipt_id AND rc.deleted = FALSE)          \n"
                  + "              LEFT JOIN general.sale sl1 ON(sl1.receipt_id = rc.id AND sl1.deleted = FALSE AND sl1.is_return = iwr.is_direction)\n"
                  + "              LEFT JOIN general.saleitem sli1 ON(sli1.sale_id = sl1.id AND sli1.stock_id = sli.stock_id AND sli1.deleted = FALSE)\n"
                  + "        WHERE wm.deleted = FALSE AND ((usr.type_id = 2 and sl1.id IS NULL) OR (sl2.id IS NULL)) AND iwr.type_id IN (60,61)  AND wm.stock_id = sli.stock_id AND ( ( sl1.shift_id  < " + shift.getId() + ") OR ( sl2.shift_id  < " + shift.getId() + ") ) \n"
                  + "    ) AS previoussaletotal,\n"
                  + "    (\n"
                  + "    	SELECT\n"
                  + "			COALESCE(SUM(CASE WHEN wm.is_direction = true THEN wm.quantity ELSE 0  END),0) AS entry	\n"
                  + "        FROM inventory.warehousemovement wm \n"
                  + "        	INNER JOIN inventory.warehousereceipt iwr ON (iwr.id =wm.warehousereceipt_id AND iwr.deleted=FALSE)\n"
                  + "            INNER JOIN inventory.warehouse wrh ON(wrh.id = iwr.warehouse_id AND wrh.deleted = FALSE AND wrh.branch_id = " + sessionBean.getUser().getLastBranch().getId() + ")\n"
                  + "            LEFT JOIN finance.waybill_warehousereceipt_con wrc ON(wrc.warehousereceipt_id = iwr.id AND wrc.deleted = FALSE)\n"
                  + "            LEFT JOIN finance.waybill wb ON(wb.id = wrc.waybill_id AND wb.deleted = FALSE)\n"
                  + "            LEFT JOIN finance.waybill_invoice_con wbc ON(wbc.waybill_id = wb.id ANd wbc.deleted = FALSE)\n"
                  + "            LEFT JOIN  finance.invoice inv ON(inv.id = wbc.invoice_id AND inv.deleted = FALSE)\n"
                  + "    \n"
                  + "        WHERE wm.deleted = FALSE AND wm.stock_id = sli.stock_id AND inv.invoicedate BETWEEN '" + sd.format(shift.getBeginDate()) + "' AND '" + sd.format(shift.getEndDate()) + "'\n"
                  + "    ) AS entryQuantity,\n"
                  + "    (\n"
                  + "    	SELECT\n"
                  + "			COALESCE(SUM(CASE WHEN wm.is_direction = false THEN  wm.quantity ELSE 0  END),0) AS entry\n"
                  + "        FROM inventory.warehousemovement wm \n"
                  + "           INNER JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "           ------Fatura------------\n"
                  + "           LEFT JOIN finance.waybill_warehousereceipt_con wrc ON(wrc.warehousereceipt_id = iwr.id AND wrc.deleted = FALSE)\n"
                  + "           LEFT JOIN finance.waybill wb ON(wb.id = wrc.waybill_id AND wb.deleted = FALSE)          \n"
                  + "           LEFT JOIN finance.waybill_invoice_con wbc ON(wbc.waybill_id = wb.id ANd wbc.deleted = FALSE)\n"
                  + "           LEFT JOIN  finance.invoice inv ON(inv.id = wbc.invoice_id AND inv.deleted = FALSE)\n"
                  + "           LEFT JOIN general.sale sl2 ON(sl2.invoice_id = inv.id AND sl2.deleted = FALSE AND sl2.is_return = iwr.is_direction )\n"
                  + "           LEFT JOIN general.userdata usr ON(usr.id = sl2.userdata_id)\n"
                  + "           ------Satış Fişi--------\n"
                  + "   		   LEFT JOIN finance.receipt_warehousereceipt_con rwc ON(rwc.warehousereceipt_id = iwr.id AND rwc.deleted = FALSE  )\n"
                  + "           LEFT JOIN  finance.receipt rc ON(rc.id = rwc.receipt_id AND rc.deleted = FALSE)              \n"
                  + "           LEFT JOIN general.sale sl1 ON(sl1.receipt_id = rc.id AND sl1.deleted = FALSE AND sl1.is_return = iwr.is_direction )\n"
                  + "    \n"
                  + "        WHERE wm.deleted = FALSE AND ((usr.type_id = 2 and sl1.id IS NULL) OR (sl2.id IS NULL)) AND wm.stock_id = sli.stock_id  AND ( ( sl1.shift_id  = " + shift.getId() + ") OR ( sl2.shift_id  = " + shift.getId() + ") ) \n"
                  + "    ) AS exitQuantity,\n"
                  + "    (\n"
                  + "    	SELECT\n"
                  + "        	COALESCE(SUM(COALESCE((CASE WHEN  iwr.is_canceled = TRUE or iwr.is_direction=FALSE THEN  -wm.quantity ELSE wm.quantity END),0)*prli.price),0) AS entry\n"
                  + "        FROM\n"
                  + "        inventory.warehousemovement wm\n"
                  + "            INNER JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "            LEFT JOIN inventory.warehouse wr ON(wr.id = wm.warehouse_id AND wr.branch_id =  " + sessionBean.getUser().getLastBranch().getId() + "  AND wr.deleted = FALSE )\n"
                  + "            LEFT JOIN inventory.pricelist prl ON(prl.branch_id = " + sessionBean.getUser().getLastBranch().getId() + "  AND prl.is_default = TRUE AND prl.is_purchase = FALSE AND prl.deleted=FALSE)\n"
                  + "            LEFT JOIN inventory.pricelistitem prli ON(prli.stock_id = wm.stock_id AND prli.deleted = FALSE AND prli.pricelist_id = prl.id)\n"
                  + "        WHERE wm.stock_id = sli.stock_id AND wm.deleted = FALSE AND iwr.processdate > '" + sd.format(shift.getBeginDate()) + "' AND iwr.processdate < '" + sd.format(shift.getEndDate()) + "'\n"
                  + "    ) AS remainingAmount,\n"
                  + "   (SELECT\n"
                  + "       COALESCE(SUM((CASE WHEN  iwr.is_canceled = TRUE or iwr.is_direction=FALSE THEN  -wm.quantity ELSE wm.quantity END)),0) AS entry\n"
                  + "    FROM\n"
                  + "      inventory.warehousemovement wm\n"
                  + "      INNER JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "      LEFT JOIN inventory.warehouse wr ON(wr.id = wm.warehouse_id AND wr.branch_id =  " + sessionBean.getUser().getLastBranch().getId() + "  AND wr.deleted = FALSE )\n"
                  + "      WHERE  wm.stock_id = sli.stock_id AND wm.deleted=FALSE AND iwr.processdate < '" + sd.format(shift.getBeginDate()) + "'\n"
                  + "    ) AS previousamountbeforeshift,\n"
                  + "    (\n"
                  + "    	SELECT\n"
                  + "        	COALESCE(SUM(COALESCE((CASE WHEN  iwr.is_canceled = TRUE or iwr.is_direction=FALSE THEN  -wm.quantity ELSE wm.quantity END),0)*prli.price),0) AS entry\n"
                  + "        FROM\n"
                  + "        inventory.warehousemovement wm\n"
                  + "            INNER JOIN inventory.warehousereceipt iwr ON (wm.warehousereceipt_id=iwr.id AND iwr.deleted=FALSE)\n"
                  + "            LEFT JOIN inventory.warehouse wr ON(wr.id = wm.warehouse_id AND wr.branch_id =  " + sessionBean.getUser().getLastBranch().getId() + "  AND wr.deleted = FALSE )\n"
                  + "            LEFT JOIN inventory.pricelist prl ON(prl.branch_id = " + sessionBean.getUser().getLastBranch().getId() + "  AND prl.is_default = TRUE AND prl.is_purchase = FALSE AND prl.deleted=FALSE)\n"
                  + "            LEFT JOIN inventory.pricelistitem prli ON(prli.stock_id = wm.stock_id AND prli.deleted = FALSE AND prli.pricelist_id = prl.id)\n"
                  + "        WHERE wm.stock_id = sli.stock_id AND wm.deleted = FALSE AND iwr.processdate < '" + sd.format(shift.getBeginDate()) + "'\n"
                  + "    ) AS previouspricebeforeshift\n"
                  + "FROM\n"
                  + "	general.saleitem sli\n"
                  + "    INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted = FALSE)\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id = sli.stock_id)\n"
                  + "    INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                  + "WHERE \n"
                  + "	sli.deleted=FALSE AND usr.type_id = 2\n"
                  + "    AND sl.shift_id = " + shift.getId() + "\n"
                  + "    AND NOT EXISTS (SELECT stccsub.stock_id FROM inventory.stock_categorization_con stccsub INNER JOIN general.categorization ctg ON(ctg.id = stccsub.categorization_id AND ctg.deleted=FALSE) WHERE stccsub.deleted =FALSE  AND stccsub.stock_id=sli.stock_id)\n"
                  + "GROUP BY sli.stock_id) t\n"
                  + "HAVING COALESCE(SUM(t.salesAmount),0) <> 0";

        return sql;

    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public List<MarketShiftPreview> shiftStockDetailList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftStockDetail(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftTaxRateDetailList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftTaxRateDetail(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftCurrencyDetailList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftCurrencyDetail(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftCashierPaymentCashDetailList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftCashierPaymentCashDetail(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftCashierPaymentBankDetailList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftCashierPaymentBankDetail(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftDeficitGiveMoneyList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftDeficitGiveMoney(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftDeficitGiveMoneyEmployeeList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftDeficitGiveMoneyEmployee(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftSummaryList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftSummary(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftGeneralList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftGeneral(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftCreditPaymentDetailList(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftCreditPaymentDetail(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public List<MarketShiftPreview> shiftStockGroupList(Shift shift) {
        return shiftStockGroupDetail(shift);
    }

    @Override
    public List<MarketShiftPreview> shiftStockGroupListWithoutCategories(Shift shift) {
        List<MarketShiftPreview> result = getJdbcTemplate().query(shiftStockGroupDetailWithoutCategories(shift), new MarketShiftPreviewMapper());
        return result;
    }

    @Override
    public int controlReopenShift(Shift shift) {
        String sql = "SELECT \n"
                  + "	CASE WHEN EXISTS (SELECT shf.id FROM general.shift shf WHERE shf.deleted=FALSE AND shf.begindate >= ? AND shf.branch_id=?) THEN 1\n"
                  + "        WHEN EXISTS (SELECT shp.id FROM general.shiftpayment shp WHERE shp.deleted=FALSE AND shp.actualprice IS NOT NULL AND shp.actualprice <> 0 AND shp.saletype_id <>77 AND shp.saletype_id <>19 AND shp.shift_id=?) THEN 2\n"
                  + "        ELSE 0 END";

        Object[] param = new Object[]{shift.getEndDate(), sessionBean.getUser().getLastBranch().getId(), shift.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int reopenShift(Shift shift) {
        String sql = "UPDATE general.shiftpayment SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND shift_id=?;\n"
                  + " UPDATE general.shift_safe_con SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND shift_id=?;\n"
                  + " UPDATE general.shift SET status_id =?, enddate= ?, u_id=?, u_time=NOW(), is_check=FALSE WHERE id=?;";

        Object[] param = new Object[]{sessionBean.getUser().getId(), shift.getId(), sessionBean.getUser().getId(), shift.getId(), 7, null, sessionBean.getUser().getId(), shift.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    public List<Shift> shiftBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        List<Shift> result = new ArrayList<>();
        String whereBranch = "";

        if (type.equals("officialAccounting")) {
            where = where + "  AND shf.status_id=8 ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                whereBranch = whereBranch + " AND shf.branch_id IN(" + branchID + ") ";
            }

        }

        String sql = "SELECT \n"
                  + "    shf.id AS shfid,\n"
                  + "    shf.name AS  shfname,\n"
                  + "    shf.shiftno AS shfshiftno,\n"
                  + "    shf.begindate AS shfbegindate,\n"
                  + "    shf.enddate AS shfenddate\n"
                  + "FROM general.shift shf\n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = shf.status_id AND sttd.language_id = ?)\n"
                  + "WHERE shf.deleted=False  " + whereBranch + "\n"
                  + where + "\n"
                  + "ORDER BY shf.name\n"
                  + " limit " + pageSize + " offset " + first;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId()};

        result = getJdbcTemplate().query(sql, params, new MarketShiftMapper());
        return result;
    }

    @Override
    public int shiftBookCount(String where, String type, List<Object> param) {

        String whereBranch = "";

        if (type.equals("officialAccounting")) {
            where = where + "  AND shf.status_id=8 ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                whereBranch = whereBranch + " AND shf.branch_id IN(" + branchID + ") ";
            }

        }

        String sql = "SELECT \n"
                  + "   COUNT(shf.id) AS shfid\n"
                  + "FROM general.shift shf\n"
                  + "WHERE shf.deleted=False  " + whereBranch + "\n"
                  + where + "\n";

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public List<PointOfSale> listPointOfSale() {
        String sql = "SELECT \n"
                  + "pos.id AS posid,\n"
                  + "pos.name AS posname,\n"
                  + "pos.code AS poscode,\n"
                  + "pos.localipaddress AS poslocalipaddress\n"
                  + "FROM general.pointofsale pos\n"
                  + "   WHERE pos.deleted=FALSE AND pos.branch_id=? AND pos.status_id=9 AND pos.is_offline =TRUE";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<PointOfSale> result = getJdbcTemplate().query(sql, param, new PointOfSaleMapper());
        return result;

    }

    @Override
    public int create(Shift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String lastUserShiftReport() {
        String sql = "SELECt \n"
                  + "lastshiftreport\n"
                  + "FROM general.userdata usr\n "
                  + "WHERE usr.deleted=FALSE AND usr.id=?";
        Object[] param = new Object[]{sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public String updateLastUserShiftReport(String str) {
        String sql = "UPDATE general.userdata SET lastshiftreport=? , u_id=?, u_time=NOW() WHERE deleted=False and id=?\n";

        Object[] param = new Object[]{str, sessionBean.getUser().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public List<MarketShiftPreview> shiftAccountGroupList(Shift shift) {

        String sql = "";

        sql = "SELECT\n"
                  + "        sl.account_id AS slaccount_id,\n"
                  + "        acc.name AS accname,\n"
                  + "        acc.title AS acctitle,\n"
                  + "       sum(CASE WHEN sl.is_return = TRUE THEN COALESCE(-sl.totalmoney,0) ELSE COALESCE(sl.totalmoney,0) END) AS sltotalmoney,\n"
                  + "        sl.currency_id AS slcurrency_id,\n"
                  + "        crr.code as crrcode\n"
                  + "FROM general.sale sl\n"
                  + "LEFT JOIN general.account acc ON(acc.id=sl.account_id)\n"
                  + "LEFT JOIN general.userdata us ON(us.id=sl.userdata_id)\n"
                  + "LEFT JOIN system.currency crr ON(crr.id = sl.currency_id AND crr.deleted =FALSE)\n"
                  + "WHERE sl.deleted=False AND sl.shift_id=?\n"
                  + "AND sl.branch_id=? AND us.type_id = 2\n"
                  + "GROUP BY sl.account_id, acc.name, acc.title,sl.currency_id, crr.code\n";

        Object[] param = new Object[]{shift.getId(), sessionBean.getUser().getLastBranch().getId()};
        List<MarketShiftPreview> result = getJdbcTemplate().query(sql, param, new MarketShiftPreviewMapper());
        return result;

    }

    @Override
    public List<MarketShiftPreview> shiftSafeTransferList(Shift shift) {

        String sql = "";

        sql = "SELECT \n"
                  + "sf.id AS sfid,\n"
                  + "sf.name AS sfname,\n"
                  + "ssc.balance AS sscbalance,\n"
                  + "sf.currency_id AS sfcurrency_id,\n"
                  + "crr.code AS crrcode\n"
                  + "FROM general.shift_safe_con ssc\n"
                  + "INNER JOIN finance.safe sf ON(sf.id=ssc.safe_id AND sf.deleted = FALSE )\n"
                  + "LEFT JOIN system.currency crr ON(crr.id=sf.currency_id AND crr.deleted =FALSE)\n"
                  + "WHERE ssc.deleted =FALSE \n"
                  + " AND ssc.safe_id IN (\n"
                  + "               SELECT\n"
                  + "                sf.id\n"
                  + "               FROM general.shiftpayment shp  \n"
                  + "                 LEFT JOIN finance.safe sf ON(sf.id=shp.safe_id AND sf.deleted =FALSE)\n"
                  + "                 WHERE shp.deleted =FALSE AND shp.shift_id= ?\n"
                  + "               GROUP BY sf.id  \n"
                  + "               )\n"
                  + " AND ssc.shift_id = (\n"
                  + "            SELECT\n"
                  + "               shf.id\n"
                  + "            FROM general.shift  shf\n"
                  + "            WHERE shf.deleted = FALSE AND shf.id < ? AND shf.branch_id = ? \n"
                  + "            ORDER BY shf.id DESC\n"
                  + "            LIMIT 1\n"
                  + " )              \n"
                  + " AND sf.branch_id = ? \n"
                  + "UNION ALL\n"
                  + " SELECT \n"
                  + "    sf.id AS sfid,\n"
                  + "    sf.name AS sfname,\n"
                  + "    (\n"
                  + "     SELECT \n"
                  + "     COALESCE(  SUM(CASE WHEN sfm.is_direction = TRUE THEN COALESCE(sfm.price,0) ELSE -COALESCE(sfm.price,0) END) ,0)\n"
                  + "     FROM finance.safemovement sfm \n"
                  + "     WHERE sfm.deleted = FALSE \n"
                  + "      AND sfm.safe_id = sf.id \n"
                  + "      AND sfm.branch_id = ?\n"
                  + "      AND sfm.movementdate < ? \n"
                  + "    ) AS sscbalance,"
                  + "    sf.currency_id AS sfcurrency_id,\n"
                  + "    crr.code AS crrcode \n"
                  + " FROM finance.safe sf \n"
                  + " LEFT JOIN system.currency crr ON(crr.id=sf.currency_id AND crr.deleted =FALSE)\n"
                  + " WHERE sf.deleted = FALSE AND sf.id IN(\n"
                  + "              SELECT\n"
                  + "                sf.id\n"
                  + "               FROM general.shiftpayment shp  \n"
                  + "                 LEFT JOIN finance.safe sf ON(sf.id=shp.safe_id AND sf.deleted =FALSE)\n"
                  + "                 WHERE shp.deleted =FALSE AND shp.shift_id= ?\n"
                  + "               GROUP BY sf.id  \n"
                  + " )\n"
                  + " AND sf.id NOT IN (\n"
                  + "                    SELECT \n"
                  + "                      sf.id AS sfid\n"
                  + "                    FROM general.shift_safe_con ssc\n"
                  + "                    INNER JOIN finance.safe sf ON(sf.id=ssc.safe_id AND sf.deleted = FALSE )\n"
                  + "                    LEFT JOIN system.currency crr ON(crr.id=sf.currency_id AND crr.deleted =FALSE)\n"
                  + "                    WHERE ssc.deleted = FALSE \n"
                  + "                     AND ssc.shift_id = (\n"
                  + "                     SELECT\n"
                  + "                     shf.id\n"
                  + "                     FROM general.shift  shf\n"
                  + "                     WHERE shf.deleted = FALSE AND shf.id < ? AND shf.branch_id = ?\n"
                  + "                     ORDER BY shf.id DESC\n"
                  + "                     LIMIT 1\n"
                  + "                     )\n"
                  + "                     AND sf.branch_id = ?\n"
                  + " )\n"
                  + "  AND sf.branch_id = ?";

        Object[] param = new Object[]{shift.getId(), shift.getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getLastBranch().getId(), shift.getBeginDate(), shift.getId(),
            shift.getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<MarketShiftPreview> result = getJdbcTemplate().query(sql, param, new MarketShiftPreviewMapper());
        return result;

    }

    @Override
    public boolean controlIsCheck() {
        String sql = "SELECT * FROM general.check_shift(?);";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, boolean.class);
        } catch (DataAccessException e) {
            return true;
        }
    }

    @Override
    public String updateIsCheck(Shift shift) {
        String sql = "UPDATE general.shift SET is_check = FALSE, u_id=?, u_time=NOW() WHERE id = ?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), shift.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

}
