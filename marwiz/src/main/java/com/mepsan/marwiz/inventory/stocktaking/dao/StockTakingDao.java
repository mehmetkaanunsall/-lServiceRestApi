/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   14.02.2018 11:24:22
 */
package com.mepsan.marwiz.inventory.stocktaking.dao;

import com.mepsan.marwiz.general.account.dao.AccountMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.inventory.stock.dao.StockMapper;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockTakingDao extends JdbcDaoSupport implements IStockTakingDao {

    @Autowired
    private SessionBean sessionBean;

    @Override
    public List<StockTaking> findAll(String where) {
        String sql = "SELECT\n"
                + "  ist.id AS istid,\n"
                + "  ist.name AS istname,\n"
                + "  ist.warehouse_id AS istwarehouse_id,\n"
                + "  ist.is_control AS istis_control,\n"
                + "  ist.is_retrospective AS istis_retrospective,\n"
                + "  iw.name AS iwname,\n"
                + "  ist.begindate AS istbegindate,\n"
                + "  ist.enddate AS istenddate,\n"
                + "  ist.status_id AS iststatus_id,\n"
                + "  sttd.name AS sttdname,\n"
                + "  ist.description AS istdescription,\n"
                + "  ist.takingemployee_id AS isttakingemployee_id,\n"
                + "  acctak.name AS acctakname,\n"
                + "  acctak.title AS acctaktitle,\n"
                + "  ist.pricelist_id AS istpricelist_id,\n"
                + "  prl.name AS prlname,\n"
                + "  ist.approvalemployee_id AS istapprovalemployee_id,\n"
                + "  accappr.name AS accapprname,\n"
                + "  accappr.title AS accapprtitle,\n"
                + "  ist.is_taxincluded AS istis_taxincluded,\n"
                + "  (\n"
                + "    SELECT\n"
                + "    STRING_AGG (CAST(scc.categorization_id AS VARCHAR), ',') \n"
                + "    FROM inventory.stocktaking_categorization_con scc\n"
                + "    WHERE scc.stocktaking_id=ist.id AND scc.deleted=false\n"
                + "  ) AS categories,\n"
                + "  quantitys.systemquantity AS systemquantity,\n"
                + "  quantitys.realquantity AS realquantity,\n"
                + "  quantitys.diffquantity AS difftakingquantity,\n"
                + "  prices.systemquantityprice AS systemquantityprice,\n"
                + "  prices.realquantityprice AS realquantityprice,\n"
                + "  prices.difftakingprice AS difftakingprice,\n"
                + " ist.c_time as istc_time,\n"
                + "    usd.id as usdid,\n"
                + "    usd.name as usdname,\n"
                + "    usd.surname as usdsurname,\n"
                + "    usd.username as usdusername\n"
                + "\n"
                + " FROM inventory.stocktaking ist \n"
                + " LEFT JOIN general.userdata usd ON(usd.id=ist.c_id)\n"
                + " LEFT JOIN inventory.pricelist prl ON(prl.id = ist.pricelist_id AND prl.deleted = FALSE)\n"
                + " LEFT JOIN inventory.warehouse iw ON (ist.warehouse_id=iw.id and iw.deleted=false)\n"
                + " INNER JOIN system.status_dict sttd ON (sttd.status_id = ist.status_id AND sttd.language_id = ?)\n"
                + " LEFT JOIN general.account acctak ON(acctak.id = ist.takingemployee_id )\n"
                + " LEFT JOIN general.account accappr ON(accappr.id = ist.approvalemployee_id )\n"
                + " LEFT JOIN\n"
                + " (\n"
                + "    SELECT\n"
                + "     subTable.stocktaking_id,\n"
                + "     string_agg(CAST(concat(subTable.systemquantity,concat(' ', subTable.sortname)) as varchar), ', ') AS systemquantity ,\n"
                + "     string_agg(CAST(concat(subTable.realquantity,concat(' ', subTable.sortname)) as varchar), ', ') AS realquantity,\n"
                + "     string_agg(CAST(concat(subTable.diffquantity,concat(' ', subTable.sortname)) as varchar), ', ') AS diffquantity\n"
                + "    FROM \n"
                + "    (\n"
                + "      SELECT\n"
                + "      sti.stocktaking_id, \n"
                + "         unt.sortname AS sortname,\n"
                + "         round(sum( COALESCE(sti.systemquantity,0)),COALESCE( unt.unitrounding,0)) AS systemquantity,\n"
                + "         round(sum( COALESCE(sti.realquantity,0)),COALESCE( unt.unitrounding,0)) AS realquantity,\n"
                + "         round(sum( COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0) ),COALESCE( unt.unitrounding,0)) AS diffquantity\n"
                + "      FROM \n"
                + "          inventory.stocktakingitem sti\n"
                + "          INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)\n"
                + "          INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)\n"
                + "          INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE) \n"
                + "      WHERE \n"
                + "          sti.deleted = FALSE AND st.branch_id=? AND st.deleted=FALSE\n"
                + "          GROUP BY sti.stocktaking_id, unt.id\n"
                + "   ) AS subTable\n"
                + "   GROUP BY subTable.stocktaking_id\n"
                + " ) quantitys ON (quantitys.stocktaking_id=ist.id)\n"
                + " \n"
                + " LEFT JOIN\n"
                + " (\n"
                + "      SELECT\n"
                + "        subTable.stocktaking_id,\n"
                + "        string_agg(CAST(concat(subTable.systemquantityprice,concat(' ', subTable.currency)) as varchar), ', ') AS systemquantityprice,\n"
                + "        string_agg(CAST(concat(subTable.realquantityprice,concat(' ', subTable.currency)) as varchar), ', ') AS realquantityprice,\n"
                + "        string_agg(CAST(concat(subTable.difftakingprice,concat(' ', subTable.currency)) as varchar), ', ') AS difftakingprice\n"
                + "     FROM \n"
                + "      (\n"
                + "     SELECT \n"
                + "           sti.stocktaking_id,\n"
                + "           cr.code AS currency,\n"
                + "            round(sum(\n"
                + "                     CASE \n"
                + "                     WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)* COALESCE(sti.systemquantity,0) \n"
                + "                     WHEN prli.id IS NULL THEN 0\n"
                + "                     WHEN st.is_taxincluded = TRUE THEN \n"
                + "                               CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)\n"
                + "                               ELSE COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)*(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                      ELSE \n"
                + "                               CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)\n"
                + "                               ELSE COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)/(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                       END\n"
                + "                       ),COALESCE( ? ,0)) AS systemquantityprice,\n"
                + "             round(sum(\n"
                + "                    CASE \n"
                + "                    WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)* COALESCE(sti.realquantity,0)\n"
                + "                    WHEN prli.id IS NULL THEN 0\n"
                + "                    WHEN st.is_taxincluded = TRUE THEN \n"
                + "                             CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)\n"
                + "                             ELSE COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)*(1+(COALESCE(stg.rate,0)/100))\n"
                + "                             END\n"
                + "                    ELSE \n"
                + "                             CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)\n"
                + "                             ELSE COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)/(1+(COALESCE(stg.rate,0)/100))\n"
                + "                             END\n"
                + "                     END\n"
                + "                     ),COALESCE( ? ,0))  AS realquantityprice,\n"
                + "              round(sum( \n"
                + "                     CASE \n"
                + "                     WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)* ( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) )\n"
                + "                     WHEN prli.id IS NULL THEN 0\n"
                + "                     WHEN st.is_taxincluded = TRUE THEN \n"
                + "                               CASE WHEN prli.is_taxincluded = TRUE THEN (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )\n"
                + "                               ELSE (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )*(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                      ELSE \n"
                + "                               CASE WHEN prli.is_taxincluded = FALSE THEN (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )\n"
                + "                               ELSE (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )/(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                       END\n"
                + "                       ),COALESCE( ?,0)) AS difftakingprice\n"
                + "        FROM \n"
                + "            inventory.stocktakingitem sti\n"
                + "            INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)\n"
                + "            INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)\n"
                + "            LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = st.pricelist_id AND prli.stock_id = stck.id AND prli.deleted = FALSE)\n"
                + "            LEFT JOIN inventory.pricelist prl ON (prli.pricelist_id = prl.id AND prl.deleted = FALSE)\n"
                + "            LEFT JOIN (SELECT \n"
                + "              txg.rate AS rate,\n"
                + "              stc.stock_id AS stock_id ,\n"
                + "              stc.is_purchase AS is_purchase\n"
                + "              FROM inventory.stock_taxgroup_con stc  \n"
                + "              INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "              WHERE stc.deleted = false\n"
                + "              AND txg.type_id = 10 --kdv grubundan \n"
                + "              ) stg ON(stg.stock_id = prli.stock_id AND stg.is_purchase = prl.is_purchase)\n"
                + "            LEFT JOIN system.currency cr ON(cr.id = (CASE WHEN st.status_id = 16 THEN sti.currentpricelistcurrency_id ELSE prli.currency_id END))\n"
                + "            INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE) \n"
                + "        WHERE \n"
                + "            sti.deleted = FALSE  AND st.branch_id=? AND st.deleted=FALSE\n"
                + "            GROUP BY sti.stocktaking_id,cr.id\n"
                + "     ) AS subTable\n"
                + "   GROUP BY subTable.stocktaking_id\n"
                + " )  prices ON (prices.stocktaking_id=ist.id)\n"
                + "\n"
                + " WHERE ist.branch_id=? AND ist.deleted=FALSE \n"
                + where + " \n"
                + " ORDER BY ist.begindate DESC";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getCurrencyrounding(), sessionBean.getUser().getLastBranch().getCurrencyrounding(), sessionBean.getUser().getLastBranch().getCurrencyrounding(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<StockTaking> result = getJdbcTemplate().query(sql, param, new StockTakingMapper());
        return result;
    }

    @Override
    public List<StockTaking> selectStockTakingByWarehouse(Warehouse warehouse) {
        String sql = "SELECT \n"
                + "    ist.id AS istid,\n"
                + "    ist.name AS istname,\n"
                + "    ist.begindate AS istbegindate\n"
                + "FROM inventory.stocktaking ist \n"
                + "WHERE ist.deleted=False AND ist.warehouse_id=? ORDER BY ist.id DESC";

        Object[] param = {warehouse.getId()};
        List<StockTaking> result = getJdbcTemplate().query(sql, param, new StockTakingMapper());
        return result;
    }

    @Override
    public int create(StockTaking obj, String categories) {
        String sql = " SELECT r_stocktaking_id FROM inventory.create_stocktaking(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] param = {sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getWarehouse().getId(), obj.isIsControl(), obj.getStatus().getId(), obj.getDescription(),
            obj.getPriceList().getId() <= 0 ? null : obj.getPriceList().getId(), obj.getTakingEmployee().getId() <= 0 ? null : obj.getTakingEmployee().getId(), obj.getApprovalEmployee().getId() <= 0 ? null : obj.getApprovalEmployee().getId(), obj.isIsTaxIncluded(), sessionBean.getUser().getId(), categories,
            obj.isIsRetrospective(), obj.getBeginDate(), obj.getEndDate()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(StockTaking obj) {
        String sql = "UPDATE inventory.stocktaking SET\n"
                + "name=?,\n"
                + "is_control = ?,\n"
                + "description=?,\n"
                + "pricelist_id = ?,\n"
                + "takingemployee_id = ?,\n"
                + "approvalemployee_id = ?,\n"
                + "is_taxincluded = ?,\n"
                + "u_id=?,\n"
                + "u_time=NOW()\n"
                + "WHERE id=?";
        Object[] param = {obj.getName(), obj.isIsControl(), obj.getDescription(), obj.getPriceList().getId() <= 0 ? null : obj.getPriceList().getId(), obj.getTakingEmployee().getId() <= 0 ? null : obj.getTakingEmployee().getId(), obj.getApprovalEmployee().getId() <= 0 ? null : obj.getApprovalEmployee().getId(), obj.isIsTaxIncluded(), sessionBean.getUser().getId(), obj.getId()};

        try {

            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(StockTaking obj, String deletedCategories, String insertedCategories, String items) {
        String sql = "UPDATE inventory.stocktaking SET\n"
                + "name=?,\n"
                + "is_control = ?,\n"
                + "description=?,\n"
                + "pricelist_id = ?,\n"
                + "takingemployee_id = ?,\n"
                + "approvalemployee_id = ?,\n"
                + "is_taxincluded = ?,\n"
                + "u_id=?,\n"
                + "u_time=NOW()\n"
                + "WHERE id=?;\n";

        if (!deletedCategories.equals("")) {
            sql += "UPDATE inventory.stocktaking_categorization_con set deleted=TRUE ,u_id=" + sessionBean.getUser().getId() + " , d_time=NOW()  WHERE stocktaking_id=" + obj.getId() + " AND categorization_id IN (" + deletedCategories + ") AND deleted=FALSE;\n";
        }

        if (!insertedCategories.equals("")) {
            sql += "INSERT INTO inventory.stocktaking_categorization_con\n"
                    + "(stocktaking_id,categorization_id,c_id,u_id)\n"
                    + "SELECT " + obj.getId() + " ,t.c," + sessionBean.getUser().getId() + "," + sessionBean.getUser().getId() + " FROM (SELECT unnest(ARRAY[" + insertedCategories + "]) AS c) t;\n";
        }

        if (!items.equals("")) {
            sql += "UPDATE inventory.stocktakingitem set deleted=TRUE ,u_id=" + sessionBean.getUser().getId() + " , d_time=NOW()  WHERE id IN ( " + items + " )\n;";
        }

        Object[] param = {obj.getName(), obj.isIsControl(), obj.getDescription(), obj.getPriceList().getId() <= 0 ? null : obj.getPriceList().getId(), obj.getTakingEmployee().getId() <= 0 ? null : obj.getTakingEmployee().getId(), obj.getApprovalEmployee().getId() <= 0 ? null : obj.getApprovalEmployee().getId(), obj.isIsTaxIncluded(), sessionBean.getUser().getId(), obj.getId()};

        try {

            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(StockTaking stockTaking) {
        String sql = "UPDATE inventory.stocktaking set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE id=?;\n"
                + "UPDATE inventory.stocktakingitem set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE stocktaking_id=?;\n"
                + "UPDATE inventory.stocktaking_categorization_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE stocktaking_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getId(), stockTaking.getId(), sessionBean.getUser().getId(), stockTaking.getId(), sessionBean.getUser().getId(), stockTaking.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Account> employeList() {
        String sql = "SELECT\n"
                + "acc.id AS accid,\n"
                + "acc.title AS acctitle,\n"
                + "acc.name AS accname\n"
                + "FROM\n"
                + "	general.account acc\n"
                + "LEFT JOIN general.employeeinfo empi ON(empi.account_id = acc.id AND empi.deleted=FALSE)\n"
                + "INNER JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.branch_id=? AND abc.deleted=FALSE)\n"
                + "WHERE\n"
                + "	 acc.is_person =  TRUE \n"
                + "    AND acc.is_employee = TRUE\n"
                + "    AND empi.branch_id = ?\n"
                + "    AND acc.status_id = 5 AND acc.deleted = FALSE";

        Object[] param = {sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Account> result = getJdbcTemplate().query(sql, param, new AccountMapper());
        return result;
    }

    @Override
    public List<StockTaking> stockTakingDetail(StockTaking stockTaking) {
        String sql = "SELECT\n"
                + "  ist.id AS istid,\n"
                + "  ist.name AS istname,\n"
                + "  ist.warehouse_id AS istwarehouse_id,\n"
                + "  ist.is_control AS istis_control,\n"
                + "  ist.is_retrospective AS istis_retrospective,\n"
                + "  iw.name AS iwname,\n"
                + "  ist.begindate AS istbegindate,\n"
                + "  ist.enddate AS istenddate,\n"
                + "  ist.status_id AS iststatus_id,\n"
                + "  sttd.name AS sttdname,\n"
                + "  ist.description AS istdescription,\n"
                + "  ist.takingemployee_id AS isttakingemployee_id,\n"
                + "  acctak.name AS acctakname,\n"
                + "  acctak.title AS acctaktitle,\n"
                + "  ist.pricelist_id AS istpricelist_id,\n"
                + "  prl.name AS prlname,\n"
                + "  ist.approvalemployee_id AS istapprovalemployee_id,\n"
                + "  accappr.name AS accapprname,\n"
                + "  accappr.title AS accapprtitle,\n"
                + "  ist.is_taxincluded AS istis_taxincluded,\n"
                + "  (\n"
                + "    SELECT\n"
                + "    STRING_AGG (CAST(scc.categorization_id AS VARCHAR), ',') \n"
                + "    FROM inventory.stocktaking_categorization_con scc\n"
                + "    WHERE scc.stocktaking_id=ist.id AND scc.deleted=false\n"
                + "  ) AS categories,\n"
                + "  quantitys.systemquantity AS systemquantity,\n"
                + "  quantitys.realquantity AS realquantity,\n"
                + "  quantitys.diffquantity AS difftakingquantity,\n"
                + "  prices.systemquantityprice AS systemquantityprice,\n"
                + "  prices.realquantityprice AS realquantityprice,\n"
                + "  prices.difftakingprice AS difftakingprice,\n"
                + " ist.c_time as istc_time,\n"
                + "    usd.id as usdid,\n"
                + "    usd.name as usdname,\n"
                + "    usd.surname as usdsurname,\n"
                + "    usd.username as usdusername\n"
                + "\n"
                + " FROM inventory.stocktaking ist \n"
                + " LEFT JOIN general.userdata usd ON(usd.id=ist.c_id)\n"
                + " LEFT JOIN inventory.pricelist prl ON(prl.id = ist.pricelist_id AND prl.deleted = FALSE)\n"
                + " LEFT JOIN inventory.warehouse iw ON (ist.warehouse_id=iw.id and iw.deleted=false)\n"
                + " INNER JOIN system.status_dict sttd ON (sttd.status_id = ist.status_id AND sttd.language_id = ?)\n"
                + " LEFT JOIN general.account acctak ON(acctak.id = ist.takingemployee_id )\n"
                + " LEFT JOIN general.account accappr ON(accappr.id = ist.approvalemployee_id )\n"
                + " LEFT JOIN\n"
                + " (\n"
                + "    SELECT\n"
                + "     subTable.stocktaking_id,\n"
                + "     string_agg(CAST(concat(subTable.systemquantity,concat(' ', subTable.sortname)) as varchar), ', ') AS systemquantity ,\n"
                + "     string_agg(CAST(concat(subTable.realquantity,concat(' ', subTable.sortname)) as varchar), ', ') AS realquantity,\n"
                + "     string_agg(CAST(concat(subTable.diffquantity,concat(' ', subTable.sortname)) as varchar), ', ') AS diffquantity\n"
                + "    FROM \n"
                + "    (\n"
                + "      SELECT\n"
                + "      sti.stocktaking_id, \n"
                + "         unt.sortname AS sortname,\n"
                + "         round(sum( COALESCE(sti.systemquantity,0)),COALESCE( unt.unitrounding,0)) AS systemquantity,\n"
                + "         round(sum( COALESCE(sti.realquantity,0)),COALESCE( unt.unitrounding,0)) AS realquantity,\n"
                + "         round(sum( COALESCE(sti.realquantity,0) - COALESCE(sti.systemquantity,0) ),COALESCE( unt.unitrounding,0)) AS diffquantity\n"
                + "      FROM \n"
                + "          inventory.stocktakingitem sti\n"
                + "          INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)\n"
                + "          INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)\n"
                + "          INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE) \n"
                + "      WHERE \n"
                + "          sti.deleted = FALSE AND st.branch_id=? AND st.deleted=FALSE\n"
                + "          GROUP BY sti.stocktaking_id, unt.id\n"
                + "   ) AS subTable\n"
                + "   GROUP BY subTable.stocktaking_id\n"
                + " ) quantitys ON (quantitys.stocktaking_id=ist.id)\n"
                + " \n"
                + " LEFT JOIN\n"
                + " (\n"
                + "      SELECT\n"
                + "        subTable.stocktaking_id,\n"
                + "        string_agg(CAST(concat(subTable.systemquantityprice,concat(' ', subTable.currency)) as varchar), ', ') AS systemquantityprice,\n"
                + "        string_agg(CAST(concat(subTable.realquantityprice,concat(' ', subTable.currency)) as varchar), ', ') AS realquantityprice,\n"
                + "        string_agg(CAST(concat(subTable.difftakingprice,concat(' ', subTable.currency)) as varchar), ', ') AS difftakingprice\n"
                + "     FROM \n"
                + "      (\n"
                + "     SELECT \n"
                + "           sti.stocktaking_id,\n"
                + "           cr.code AS currency,\n"
                + "            round(sum(\n"
                + "                     CASE \n"
                + "                     WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)* COALESCE(sti.systemquantity,0) \n"
                + "                     WHEN prli.id IS NULL THEN 0\n"
                + "                     WHEN st.is_taxincluded = TRUE THEN \n"
                + "                               CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)\n"
                + "                               ELSE COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)*(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                      ELSE \n"
                + "                               CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)\n"
                + "                               ELSE COALESCE(prli.price,0)* COALESCE(sti.systemquantity,0)/(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                       END\n"
                + "                       ),COALESCE( ? ,0)) AS systemquantityprice,\n"
                + "             round(sum(\n"
                + "                    CASE \n"
                + "                    WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)* COALESCE(sti.realquantity,0)\n"
                + "                    WHEN prli.id IS NULL THEN 0\n"
                + "                    WHEN st.is_taxincluded = TRUE THEN \n"
                + "                             CASE WHEN prli.is_taxincluded = TRUE THEN COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)\n"
                + "                             ELSE COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)*(1+(COALESCE(stg.rate,0)/100))\n"
                + "                             END\n"
                + "                    ELSE \n"
                + "                             CASE WHEN prli.is_taxincluded = FALSE THEN COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)\n"
                + "                             ELSE COALESCE(prli.price,0)* COALESCE(sti.realquantity,0)/(1+(COALESCE(stg.rate,0)/100))\n"
                + "                             END\n"
                + "                     END\n"
                + "                     ),COALESCE( ? ,0))  AS realquantityprice,\n"
                + "              round(sum( \n"
                + "                     CASE \n"
                + "                     WHEN st.status_id = 16 THEN COALESCE(sti.currentpricelistprice,0)* ( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) )\n"
                + "                     WHEN prli.id IS NULL THEN 0\n"
                + "                     WHEN st.is_taxincluded = TRUE THEN \n"
                + "                               CASE WHEN prli.is_taxincluded = TRUE THEN (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )\n"
                + "                               ELSE (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )*(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                      ELSE \n"
                + "                               CASE WHEN prli.is_taxincluded = FALSE THEN (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )\n"
                + "                               ELSE (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )/(1+(COALESCE(stg.rate,0)/100))\n"
                + "                               END\n"
                + "                       END\n"
                + "                       ),COALESCE( ?,0)) AS difftakingprice\n"
                + "        FROM \n"
                + "            inventory.stocktakingitem sti\n"
                + "            INNER JOIN inventory.stocktaking st ON(st.id = sti.stocktaking_id AND st.deleted = FALSE)\n"
                + "            INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE)\n"
                + "            LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = st.pricelist_id AND prli.stock_id = stck.id AND prli.deleted = FALSE)\n"
                + "            LEFT JOIN inventory.pricelist prl ON (prli.pricelist_id = prl.id AND prl.deleted = FALSE)\n"
                + "            LEFT JOIN (SELECT \n"
                + "              txg.rate AS rate,\n"
                + "              stc.stock_id AS stock_id ,\n"
                + "              stc.is_purchase AS is_purchase\n"
                + "              FROM inventory.stock_taxgroup_con stc  \n"
                + "              INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "              WHERE stc.deleted = false\n"
                + "              AND txg.type_id = 10 --kdv grubundan \n"
                + "              ) stg ON(stg.stock_id = prli.stock_id AND stg.is_purchase = prl.is_purchase)\n"
                + "            LEFT JOIN system.currency cr ON(cr.id = (CASE WHEN st.status_id = 16 THEN sti.currentpricelistcurrency_id ELSE prli.currency_id END))\n"
                + "            INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE) \n"
                + "        WHERE \n"
                + "            sti.deleted = FALSE  AND st.branch_id=? AND st.deleted=FALSE\n"
                + "            GROUP BY sti.stocktaking_id,cr.id\n"
                + "     ) AS subTable\n"
                + "   GROUP BY subTable.stocktaking_id\n"
                + " )  prices ON (prices.stocktaking_id=ist.id)\n"
                + "\n"
                + " WHERE ist.branch_id=? AND ist.deleted=FALSE AND  ist.id = " + stockTaking.getId() + " \n";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getCurrencyrounding(), sessionBean.getUser().getLastBranch().getCurrencyrounding(), sessionBean.getUser().getLastBranch().getCurrencyrounding(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<StockTaking> result = getJdbcTemplate().query(sql, param, new StockTakingMapper());
        return result;
    }

    @Override
    public List<StockTaking> findStockTakingDifference(StockTaking stockTaking) {
        String sql = "SELECT \n"
                + "  cr.id AS crid, \n"
                + "  cr.code AS currency, \n"
                + "        round(sum( \n"
                + "                 CASE WHEN prli.id IS NULL THEN 0\n"
                + "                 WHEN ist.is_taxincluded = TRUE THEN \n"
                + "                           CASE WHEN prli.is_taxincluded = TRUE THEN (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )\n"
                + "                           ELSE (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )*(1+(COALESCE(stg.rate,0)/100))\n"
                + "                           END\n"
                + "                  ELSE \n"
                + "                           CASE WHEN prli.is_taxincluded = FALSE THEN (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )\n"
                + "                           ELSE (COALESCE(prli.price,0)*( COALESCE(sti.realquantity,0) -  COALESCE(sti.systemquantity,0) ) )/(1+(COALESCE(stg.rate,0)/100))\n"
                + "                           END\n"
                + "                   END\n"
                + "                   ),COALESCE( ?,0)) AS diffprice\n"
                + "FROM  \n"
                + "   inventory.stocktakingitem sti \n"
                + "   INNER JOIN inventory.stocktaking ist ON(sti.stocktaking_id=ist.id AND ist.deleted=FALSE)\n"
                + "   INNER JOIN inventory.stock stck ON(stck.id = sti.stock_id AND stck.deleted = FALSE) \n"
                + "   LEFT JOIN inventory.pricelist prl ON(prl.id = ist.pricelist_id AND prl.deleted = FALSE)\n"
                + "   LEFT JOIN inventory.pricelistitem prli ON (prli.pricelist_id = prl.id AND prli.stock_id = stck.id AND prli.deleted = FALSE) \n"
                + "   LEFT JOIN (SELECT \n"
                + "          txg.rate AS rate,\n"
                + "          stc.is_purchase AS is_purchase, \n"
                + "          stc.stock_id AS stock_id \n"
                + "          FROM inventory.stock_taxgroup_con stc  \n"
                + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                + "          WHERE stc.deleted = false\n"
                + "          AND txg.type_id = 10 --kdv grubundan \n"
                + "          ) stg ON(stg.stock_id = prli.stock_id AND stg.is_purchase = prl.is_purchase)\n"
                + "   LEFT JOIN system.currency cr ON( cr.id = prli.currency_id) \n"
                + "   INNER JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE)  \n"
                + "WHERE  \n"
                + "   sti.stocktaking_id = ? AND sti.deleted = FALSE \n"
                + "   GROUP BY cr.id ";
        Object[] param = {sessionBean.getUser().getLastBranch().getCurrencyrounding(), stockTaking.getId()};
        List<StockTaking> result = getJdbcTemplate().query(sql, param, new StockTakingMapper());
        return result;
    }

    /**
     * depo sayımı bittikten sonra giriş çıkış fişlerini oluşturarak depo
     * sayımını kapatır.
     *
     * @param stockTaking
     * @param entryMovements
     * @param exitMovements
     * @param obj
     * @param accounts
     * @return
     */
    @Override
    public int finisStockTaking(StockTaking stockTaking, FinancingDocument obj, String accounts) {
        String sql = " SELECT r_stocktaking_id FROM inventory.process_stocktaking(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] param = {1, stockTaking.getId(), stockTaking.getWarehouse().getId(), stockTaking.getApprovalEmployee().getId() == 0 ? null : stockTaking.getApprovalEmployee().getId(),
            sessionBean.getUser().getId(),
            obj.getFinancingType().getId(), obj.getIncomeExpense().getId() == 0 ? null : obj.getIncomeExpense().getId(), obj.getDocumentNumber(),
            obj.getPrice(), obj.getCurrency().getId(), obj.getExchangeRate(), obj.getDocumentDate(), obj.getDescription(), accounts};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int openStockTaking(StockTaking stockTaking) {
        String sql = " SELECT r_stocktaking_id FROM inventory.process_stocktaking(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        FinancingDocument obj = new FinancingDocument();
        Object[] param = {2, stockTaking.getId(), stockTaking.getWarehouse().getId(), stockTaking.getApprovalEmployee().getId() == 0 ? null : stockTaking.getApprovalEmployee().getId(),
            sessionBean.getUser().getId(),
            obj.getFinancingType().getId(), obj.getIncomeExpense().getId() == 0 ? null : obj.getIncomeExpense().getId(), obj.getDocumentNumber(),
            obj.getPrice(), obj.getCurrency().getId(), obj.getExchangeRate(), obj.getDocumentDate(), obj.getDescription(), null};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {

            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public StockTaking findOpenStock(StockTaking stockTaking) {
        String sql = "SELECT \n"
                + "    ist.id AS istid\n"
                + "FROM inventory.stocktaking ist \n"
                + "WHERE ist.deleted=False AND ist.warehouse_id=? AND ist.begindate > ? ";
        Object[] param = {stockTaking.getWarehouse().getId(), stockTaking.getEndDate()};
        List<StockTaking> result = getJdbcTemplate().query(sql, param, new StockTakingMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new StockTaking();
        }
    }

    @Override
    public int findCategories(StockTaking obj, String where) {

        String sql = "SELECT\n"
                + "CASE WHEN EXISTS(\n"
                + "SELECT \n"
                + "ist.id as istid, \n"
                + "ist.warehouse_id as istwarehouse_id  \n"
                + "FROM inventory.stocktaking ist\n"
                + "LEFT JOIN inventory.stocktaking_categorization_con iscc ON(iscc.stocktaking_id = ist.id AND iscc.deleted=false)\n"
                + "LEFT JOIN inventory.warehouse iw ON (iw.id = ist.warehouse_id) \n"
                + "WHERE ist.deleted = false AND iscc.categorization_id IN(" + obj.getCategories() + ") " + where + " AND ist.status_id = 15 AND ist.warehouse_id = " + obj.getWarehouse().getId() + "\n"
                + ")\n"
                + "THEN 1\n"
                + "ELSE 0\n"
                + "END;";

        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class);
        } catch (Exception e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Stock> categoryOfStock(StockTaking obj, String where) {

        String whereStock = "";
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            whereStock = whereStock + " AND si.is_valid = TRUE ";
        } else {
            whereStock = whereStock + " AND stck.is_otherbranch = TRUE ";
        }

        String sql = "";
        try {
            sql = "SELECT \n"
                    + "   ins.id as stckid, \n"
                    + "   ins.name as stckname,\n"
                    + "   ins.barcode as stckbarcode, \n"
                    + "   inscc.categorization_id as category,\n"
                    + "   gc.name as gcname \n"
                    + "FROM inventory.stock ins\n"
                    + "LEFT JOIN inventory.stock_categorization_con inscc ON (inscc.stock_id = ins.id AND inscc.deleted = FALSE)\n"
                    + "LEFT JOIN general.categorization gc ON (gc.id = inscc.categorization_id AND gc.deleted = FALSE) \n"
                    + "LEFT JOIN inventory.stockinfo si ON (si.stock_id=ins.id AND si.deleted=False AND si.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                    + "WHERE ins.deleted=FALSE \n"
                    + "      AND inscc.categorization_id IN(\n"
                    + "                   SELECT --SEÇİLEN DEPO İÇİN BÜTÜN AÇIK KATEGORİLİ SAYIMLARIN KATEGORİLERİ \n"
                    + "                       iscc.categorization_id\n"
                    + "                   FROM inventory.stocktaking ist\n"
                    + "                   INNER JOIN inventory.stocktaking_categorization_con iscc ON (iscc.stocktaking_id = ist.id AND iscc.deleted = FALSE)\n"
                    + "                   WHERE ist.deleted = FALSE AND ist.warehouse_id = " + obj.getWarehouse().getId() + "AND ist.status_id = 15" + where + " \n"
                    + "                   )\n"
                    + "                  AND inscc.stock_id IN( \n"
                    + "                                       SELECT \n"
                    + "                                           inscc.stock_id\n"
                    + "                                       FROM inventory.stock_categorization_con inscc\n"
                    + "                                       WHERE inscc.deleted = FALSE AND inscc.categorization_id IN(" + obj.getCategories() + ")\n"
                    + "                                       )\n --GELEN STOKLARLA BENİM KATEGORİLERİMİN STOKLARI AYNI MI"
                    + whereStock;

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Stock> result = getJdbcTemplate().query(sql, new StockMapper());
        return result;

    }

    @Override
    public List<StockTaking> stockTakingProcessList() {
        String sql = "SELECT\n"
                + "ist.warehouse_id as istwarehouse_id, \n"
                + "ist.id as istid \n"
                + "FROM inventory.stocktaking ist\n"
                + "Where ist.deleted = FALSE AND ist.status_id = 15 AND ist.branch_id = ?";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};

        List<StockTaking> result = getJdbcTemplate().query(sql, param, new StockTakingMapper());
        return result;
    }

}
