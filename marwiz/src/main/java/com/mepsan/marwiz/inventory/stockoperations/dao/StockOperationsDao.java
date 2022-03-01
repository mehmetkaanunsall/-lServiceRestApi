/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 17.01.2019 11:24:28
 */
package com.mepsan.marwiz.inventory.stockoperations.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockOperationsDao extends JdbcDaoSupport implements IStockOperationsDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockOperations> findAll(String where, int process) {
        String where1 = "";
        String sql = "";
        if (process == 1) {
            sql = "SELECT\n"
                    + "* \n"
                    + "FROM(\n"
                    + "SELECT\n"
                    + "DISTINCT\n"
                    + "	hst.id AS id,\n"
                    + "   (   CASE WHEN hst.newvalue = '' THEN 0  ELSE   COALESCE( hst.newvalue::numeric,0)  END)  as price, \n"
                    + "     (   CASE WHEN hst.oldvalue = '' THEN 0  ELSE   COALESCE( hst.oldvalue::numeric,0)  END)  as oldprice,\n"
                    + "    stck.id as stckid, \n"
                    + "    stck.name as stckname, \n"
                    + "    stck.barcode AS stckbarcode,\n"
                    + "    stck.centerproductcode AS stckcenterproductcode,\n"
                    + "    stck.code AS stckcode,\n"
                    + "    stck.unit_id AS stckunit_id,\n"
                    + "    stck.country_id AS stckcountry_id,\n"
                    + "    cond.name AS condname, \n"
                    + "    gunt.sortname AS guntsortname,\n"
                    + "    COALESCE(cry.old::integer,pli.currency_id) as oldcurrency_id, \n"
                    + "    COALESCE(cry.new::integer,pli.currency_id) as currency_id,     \n"
                    + "    hst.processdate as processdate  ,\n"
                    + "    si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                    + "    si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                    + "    si.currentsaleprice AS sicurrentsaleprice,\n"
                    + "    si.currentsalecurrency_id AS  sicurrentsalecurrency_id,\n"
                    + "    si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                    + "  COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                    + "  COALESCE(  si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id,\n"
                    + "   si.recommendedprice as sirecommendedprice,\n"
                    + "   si.weight as siweight,\n"
                    + "   si.weightunit_id as siweightunit_id,\n"
                    + "   wu.name AS wuname,\n"
                    + "   wu.sortname AS wusortname,\n"
                    + "   COALESCE(wu.mainweight,0) AS wumainweight,\n"
                    + "   wu.mainweightunit_id AS wumainweightunit_id,\n"
                    + "   mwu.name AS mwuname,\n"
                    + "   mwu.sortname AS mwusortname,\n"
                    + "    ROW_NUMBER () OVER (PARTITION BY stck.id ORDER BY hst.processdate DESC) \n"
                    + "FROM general.history hst \n"
                    + "    INNER JOIN inventory.pricelistitem pli ON(pli.id=hst.row_id AND pli.deleted=FALSE)  \n"
                    + "    INNER JOIN inventory.pricelist pl ON(pl.id=pli.pricelist_id AND pl.deleted=FALSE AND pl.is_purchase = FALSE) \n"
                    + "    INNER JOIN inventory.stock stck ON(stck.id=pli.stock_id AND stck.deleted=FALSE) \n"
                    + "    LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                    + "    LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                    + "    LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                    + "    LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)  \n"
                    + "    LEFT JOIN  system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? ) \n "
                    + "    LEFT JOIN general.centralsupplier gcs ON (gcs.id = stck.centralsupplier_id) \n"
                    + "    LEFT JOIN ( \n"
                    + "     SELECT \n"
                    + "        hst.processdate as pdate, \n"
                    + "        hst.oldvalue as old, \n"
                    + "        hst.newvalue as new,  \n"
                    + "        hst.row_id as hstrow   \n"
                    + "  	FROM general.history hst  \n"
                    + "  	WHERE  \n"
                    + "  		hst.tablename='inventory.pricelistitem' AND  hst.columnname='currency_id' \n"
                    + "      ) cry ON (cry.pdate=hst.processdate) \n"
                    + "WHERE  \n"
                    + "    hst.tablename='inventory.pricelistitem' AND (hst.columnname='price') AND pl.branch_id=?\n"
                    + "      AND pl.is_purchase=FALSE \n" + where + " "
                    + "    ORDER BY hst.processdate DESC,stck.name )subtable WHERE subtable.ROW_NUMBER = 1;";

        } else if (process == 2) {
            sql = "SELECT\n"
                    + "      * \n"
                    + "      FROM(\n"
                    + "      SELECT\n"
                    + "((SELECT value -> 'centerstock_id' from json_array_elements(ntf.description::json) LIMIT 1)::VARCHAR)  AS centerstock_id,\n"
                    + "((SELECT value -> 'currency_id' from json_array_elements(ntf.description::json) LIMIT 1)::VARCHAR)  AS currency_id,\n"
                    + "((SELECT value -> 'processdate' from json_array_elements(ntf.description::json) LIMIT 1)::VARCHAR)  AS processdate,\n"
                    + "((SELECT value -> 'price' from json_array_elements(ntf.description::json) LIMIT 1)::VARCHAR)  AS price,\n"
                    + "ntf.description AS ntfdescription,\n"
                    + "ntf.id AS id ,\n"
                    + "stck.name AS  stckname,\n"
                    + "stck.id AS stckid,\n"
                    + "stck.barcode AS stckbarcode,\n"
                    + "stck.centerproductcode AS stckcenterproductcode,\n"
                    + "stck.code AS stckcode,\n"
                    + "stck.unit_id AS stckunit_id,\n"
                    + "stck.country_id AS stckcountry_id,\n"
                    + "cond.name AS condname, \n"
                    + "gunt.sortname AS guntsortname,\n"
                    + "ntf.c_time AS processdate, \n"
                    + "si.currentpurchaseprice as sicurrentpurchaseprice,\n"
                    + "si.currentpurchasecurrency_id as sicurrentpurchasecurrency_id,\n"
                    + "si.currentsaleprice AS sicurrentsaleprice,\n"
                    + "si.currentsalecurrency_id AS  sicurrentsalecurrency_id,\n"
                    + "si.lastsalepricechangedate AS silastsalepricechangedate, \n"
                    + "si.salemandatoryprice  AS sisalemandatoryprice , \n "
                    + "  COALESCE( si.salemandatoryprice ,0) AS sisalemandatoryprice,\n"
                    + "  COALESCE(  si.salemandatorycurrency_id,0) AS sisalemandatorycurrency_id,\n"
                    + "   si.recommendedprice as sirecommendedprice,\n"
                    + "   si.weight as siweight,\n"
                    + "   si.weightunit_id as siweightunit_id,\n"
                    + "   wu.name AS wuname,\n"
                    + "   wu.sortname AS wusortname,\n"
                    + "   COALESCE(wu.mainweight,0) AS wumainweight,\n"
                    + "   wu.mainweightunit_id AS wumainweightunit_id,\n"
                    + "   mwu.name AS mwuname,\n"
                    + "   mwu.sortname AS mwusortname,\n"
                    + " ROW_NUMBER () OVER (PARTITION BY stck.id ORDER BY ntf.c_time DESC) \n"
                    + "FROM\n"
                    + "	general.notification ntf\n"
                    + "    INNER JOIN inventory.stock stck ON(stck.centerstock_id = ((SELECT value -> 'centerstock_id' from json_array_elements(ntf.description::json) LIMIT 1)::VARCHAR)::integer )\n"
                    + "    LEFT JOIN inventory.stockinfo si ON (stck.id = si.stock_id AND si.branch_id=? AND si.deleted = False)\n"
                    + "    INNER JOIN system.currency cr ON (cr.id = ((SELECT value -> 'currency_id' from json_array_elements(ntf.description::json) LIMIT 1)::VARCHAR)::integer)\n"
                    + "    LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                    + "    LEFT JOIN general.unit wu ON(wu.id = si.weightunit_id AND wu.deleted = FALSE)\n"
                    + "    LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)  \n"
                    + "    LEFT JOIN system.country_dict cond ON(cond.country_id = stck.country_id AND cond.deleted = FALSE AND cond.language_id = ? )  \n"
                    + "    LEFT JOIN general.centralsupplier gcs ON (gcs.id = stck.centralsupplier_id) \n"
                    + "WHERE\n"
                    + " ntf.branch_id = ? AND( (SELECT value -> 'is_updateprice' from json_array_elements(ntf.description::json) LIMIT 1)::VARCHAR = 'false'::VARCHAR )"
                    + " AND ntf.deleted = FALSE AND ntf.centerwarningtype_id = 190 \n" + where + "  ORDER BY ntf.c_time DESC )subtable WHERE subtable.ROW_NUMBER = 1;"
                    + " \n";

        }
        Object[] param;
        param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<StockOperations> result = getJdbcTemplate().query(sql, param, new StockOperationsMapper());
        return result;
    }

    @Override
    public int processPriceList(String operaionsJson) {

        String sql = "SELECT * FROM inventory.process_recommendedpricelistitem(?, ?, ?);";
        Object[] param = new Object[]{operaionsJson, sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId()};

        try {
            System.out.println("getJdbcTemplate().queryForObject(sql, param, Integer.class)=" + getJdbcTemplate().queryForObject(sql, param, Integer.class));
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            if (((SQLException) e.getCause()) == null) {//Varsa default değeri -1 döndürmek için 
                return -1;
            } else {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }

        }
    }

}
