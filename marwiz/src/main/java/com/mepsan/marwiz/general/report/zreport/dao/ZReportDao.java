/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:38:24 PM
 */
package com.mepsan.marwiz.general.report.zreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ZReportDao extends JdbcDaoSupport implements IZReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ZReport> listOfTaxList(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                + "          sli.taxrate AS slitaxrate,\n"
                + "          COALESCE(SUM((CASE WHEN sl.is_return=FALSE THEN 1 ELSE 0 END) * sli.quantity),0) salesquantity,\n"
                + "          COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN sli.quantity ELSE 0 END),0) returnquantity,\n"
                + "          COALESCE(SUM((CASE WHEN sl.is_return=FALSE THEN 1 ELSE 0 END) * ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,0))),0) salestotal,\n"
                + "          COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))* COALESCE(sli.exchangerate,0) ELSE 0 END),0) returntotalprice,\n"
                + "          COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN  0  ELSE (sli.totaltax * COALESCE(sli.exchangerate,0)) END),0) AS totaltax\n"
                + "          FROM\n"
                + "          	 general.saleitem sli\n"
                + "              INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND  sl.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' AND sl.deleted = FALSE AND sl.branch_id=?)\n"
                + "          	  LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + sd.format(endDate) + "')\n"
                + "             INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "          WHERE\n"
                + "          sli.deleted = FALSE AND usr.type_id = 2 AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "          GROUP BY sli.taxrate;";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

    @Override
    public List<ZReport> listOfCashierList(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String sql = "SELECT \n"
                + "          usr.name AS usrname,\n"
                + "          usr.surname as usrsurname,\n"
                + "          COALESCE(SUM((CASE WHEN sl.is_return=FALSE THEN 1 ELSE 0 END) * sli.quantity),0) salesquantity,\n"
                + "          COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN sli.quantity ELSE 0 END),0) returnquantity,\n"
                + "          COALESCE(SUM((CASE WHEN sl.is_return=FALSE THEN 1 ELSE 0 END) * ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,0))),0) salestotal,\n"
                + "          COALESCE(SUM(CASE WHEN sl.is_return = TRUE THEN (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100))* COALESCE(sli.exchangerate,0) ELSE 0 END),0) returntotalprice\n"
                + "          FROM\n"
                + "          	 general.saleitem sli\n"
                + "              INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND  sl.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' AND sl.deleted = FALSE AND sl.branch_id=?)\n"
                + "          	  LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + sd.format(endDate) + "')\n"
                + "             INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "          WHERE\n"
                + "          sli.deleted = FALSE AND usr.type_id = 2 AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "          GROUP BY usr.name, usr.surname";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

    @Override
    public List<ZReport> listOfCateroyList(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "WITH recursive ctTree AS(\n"
                + "     SELECT\n"
                + "         gc.id,\n"
                + "         gc.name,\n"
                + "         COALESCE(gc.parent_id,0) AS parent_id,\n"
                + "         1 AS depth,\n"
                + "         (CASE WHEN EXISTS (SELECT\n"
                + "                                 scac.categorization_id\n"
                + "                             FROM\n"
                + "                                 inventory.stock_categorization_con scac\n"
                + "                             WHERE\n"
                + "                                 scac.categorization_id = gc.id\n"
                + "                                 AND scac.deleted=False\n"
                + "                             ) THEN 1 ELSE 0 END\n"
                + "         ) AS isAvailable\n"
                + "     FROM\n"
                + "         general.categorization gc\n"
                + "     WHERE\n"
                + "         gc.deleted = FALSE\n"
                + "         AND gc.item_id = 2\n"
                + "\n"
                + "UNION ALL\n"
                + "\n"
                + "     SELECT\n"
                + "         gc2.id,\n"
                + "         gc2.name,\n"
                + "         COALESCE(gc2.parent_id,0) AS parent_id,\n"
                + "         ct.depth+1 AS depth,\n"
                + "         (CASE WHEN EXISTS(SELECT\n"
                + "                                 scac.categorization_id\n"
                + "                             FROM\n"
                + "                                 inventory.stock_categorization_con scac\n"
                + "                             WHERE\n"
                + "                                 scac.categorization_id = gc2.id\n"
                + "                                 AND scac.deleted = FALSE\n"
                + "                           ) THEN 1 ELSE 0 END\n"
                + "         ) AS isAvailable\n"
                + "     FROM\n"
                + "         general.categorization gc2\n"
                + "         JOIN ctTree AS ct ON (ct.id = gc2.parent_id)\n"
                + "     WHERE\n"
                + "         gc2.deleted = FALSE\n"
                + "         AND gc2.item_id = 2\n"
                + "),  ctTree2 AS(\n"
                + "     SELECT\n"
                + "         sli.totalmoney,\n"
                + "           sl.discountrate,\n"
                + "           sli.exchangerate,\n"
                + "           sli.quantity,\n"
                + "           sli.deleted,\n"
                + "           sli.stock_id,\n"
                + "           sl.is_return,\n"
                + "           sl.branch_id,\n"
                + "           sll.id,\n"
                + "           sll.returnparent_id,\n"
                + "           sli.processdate\n"
                + "     FROM\n"
                + "           general.saleitem sli\n"
                + "           INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted = FALSE )\n"
                + "           LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = FALSE AND sll.processdate < '" + sd.format(endDate) + "')\n"
                + "           INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "     WHERE\n"
                + "           sli.deleted= FALSE AND usr.type_id = 2\n"
                + "           AND sl.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' AND sl.branch_id =" + sessionBean.getUser().getLastBranch().getId() + " \n"
                + ") ,ctTree3 AS\n"
                + "(\n"
                + "     SELECT\n"
                + "         stccsub.stock_id ,\n"
                + "         stccsub.categorization_id\n"
                + "     FROM\n"
                + "         inventory.stock_categorization_con stccsub\n"
                + "     WHERE\n"
                + "         stccsub.deleted = FALSE\n"
                + ")\n"
                + "SELECT\n"
                + "     sub.*,\n"
                + "     (\n"
                + "         SELECT\n"
                + "             COALESCE(SUM(((pp.totalmoney - ((pp.totalmoney *\n"
                + "             COALESCE(pp.discountrate, 0))/100)) * COALESCE(pp.exchangerate,1))),0) AS entry\n"
                + "         FROM\n"
                + "             ctTree2 pp\n"
                + "\n"
                + "         WHERE\n"
                + "             pp.is_return=FALSE\n"
                + "             AND  COALESCE(pp.id,0) = COALESCE(pp.returnparent_id,0)\n"
                + "             AND pp.stock_id IN\n"
                + "             (\n"
                + "                 SELECT yy.stock_id from ctTree3 yy WHERE\n"
                + "                   yy.categorization_id IN\n"
                + "                     (\n"
                + "                         SELECT\n"
                + "                             ctr.id\n"
                + "                         FROM\n"
                + "                             ctTree ctr\n"
                + "                         WHERE\n"
                + "                             (ctr.id = sub.categorization_id OR\n"
                + "                             ctr.parent_id = sub.categorization_id)\n"
                + "                             AND ctr.isAvailable =1\n"
                + "                     )\n"
                + "             )\n"
                + "     ) AS salestotal,\n"
                + "    (\n"
                + "         SELECT\n"
                + "              COALESCE(SUM(pp.quantity),0) AS entry\n"
                + "         FROM\n"
                + "             ctTree2 pp\n"
                + "         WHERE\n"
                + "             pp.is_return=FALSE\n"
                + "               AND  COALESCE(pp.id,0) = COALESCE(pp.returnparent_id,0)\n"
                + "             AND pp.stock_id IN\n"
                + "             (\n"
                + "                 SELECT yy.stock_id from ctTree3 yy WHERE\n"
                + "                 yy.categorization_id IN\n"
                + "                 (\n"
                + "                       SELECT\n"
                + "                          ctr.id\n"
                + "                       FROM\n"
                + "                          ctTree ctr\n"
                + "                        WHERE (ctr.id = sub.categorization_id OR ctr.parent_id = sub.categorization_id)\n"
                + "                        AND ctr.isAvailable =1\n"
                + "                  )\n"
                + "             )\n"
                + "     ) AS salesquantity,\n"
                + "     (\n"
                + "         SELECT\n"
                + "              COALESCE(SUM(pp.quantity),0) AS entry\n"
                + "         FROM\n"
                + "             ctTree2 pp\n"
                + "         WHERE\n"
                + "             pp.is_return= TRUE\n"
                + "             AND pp.stock_id IN\n"
                + "             (\n"
                + "                 SELECT yy.stock_id from ctTree3 yy WHERE\n"
                + "                   yy.categorization_id IN\n"
                + "                   (\n"
                + "                      SELECT ctr.id\n"
                + "                      FROM\n"
                + "                          ctTree ctr\n"
                + "                      WHERE (ctr.id = sub.categorization_id OR ctr.parent_id = sub.categorization_id)\n"
                + "                      AND ctr.isAvailable =1\n"
                + "                    )\n"
                + "             )\n"
                + "     ) AS returnquantity,\n"
                + "     (\n"
                + "         SELECT\n"
                + "             COALESCE(SUM(((pp.totalmoney - ((pp.totalmoney * COALESCE(pp.discountrate, 0))/100)) * COALESCE(pp.exchangerate,1))),0) AS entry\n"
                + "         FROM\n"
                + "             ctTree2 pp\n"
                + "         WHERE\n"
                + "             pp.is_return= TRUE\n"
                + "             AND pp.stock_id IN\n"
                + "             (SELECT yy.stock_id from ctTree3 yy WHERE\n"
                + "                   yy.categorization_id IN\n"
                + "                   (\n"
                + "                     SELECT ctr.id\n"
                + "                     FROM ctTree ctr\n"
                + "                     WHERE (ctr.id = sub.categorization_id OR ctr.parent_id = sub.categorization_id)\n"
                + "                     AND ctr.isAvailable =1\n"
                + "                     )\n"
                + "                 )\n"
                + "     ) AS returntotalprice\n"
                + "FROM\n"
                + "     (\n"
                + "         WITH RECURSIVE category (id, parent_id, name) AS\n"
                + "         (\n"
                + "             SELECT\n"
                + "                 id,\n"
                + "                 parent_id,\n"
                + "                 name\n"
                + "             FROM\n"
                + "                 general.categorization\n"
                + "             WHERE\n"
                + "                 parent_id is null\n"
                + "                 AND deleted = FALSE\n"
                + "                 AND item_id = 2\n"
                + "             UNION ALL\n"
                + "             SELECT\n"
                + "                 p.id,\n"
                + "                 COALESCE(t0.parent_id,p.parent_id),\n"
                + "                 t0.name\n"
                + "             FROM\n"
                + "                 general.categorization p\n"
                + "                 INNER JOIN category t0 ON (t0.id = p.parent_id)\n"
                + "             WHERE\n"
                + "                 p.deleted = FALSE\n"
                + "                 AND p.item_id = 2\n"
                + "         )\n"
                + "         SELECT\n"
                + "             DISTINCT (CASE WHEN parent_id IS NULL THEN id ELSE parent_id END ) AS categorization_id,\n"
                + "             name AS category\n"
                + "         FROM\n"
                + "             category\n"
                + "         WHERE\n"
                + "             id IN (\n"
                + "                     SELECT\n"
                + "                         stcc.categorization_id\n"
                + "                     FROM\n"
                + "                         inventory.stock_categorization_con stcc\n"
                + "                         INNER JOIN general.categorization ctr ON(ctr.id = stcc.categorization_id AND ctr.deleted = FALSE AND ctr.item_id = 2)\n"
                + "                         INNER JOIN inventory.stock stck ON(stck.id = stcc.stock_id AND stck.deleted = FALSE  AND stck.status_id = 3)\n"
                + "                         LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                + "                     WHERE\n"
                + "                         stcc.deleted = FALSE AND si.is_passive = FALSE\n"
                + "                     )\n"
                + "     ) as sub;";

        Object[] param = {};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

    @Override
    public List<ZReport> listOfSalesTypeList(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = " SELECT\n"
                + "	slp.type_id as slptype_id,\n"
                + "    typd.name as typdname,\n"
                + "  	COALESCE(SUM((CASE WHEN (sl.is_return=FALSE AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)) THEN 1 ELSE 0 END) * COALESCE(COALESCE(slp.price,0)*COALESCE(slp.exchangerate,1))),0) AS salestotal,\n"
                + "    COALESCE(SUM((CASE WHEN sl.is_return=TRUE THEN 1 ELSE 0 END) * COALESCE(COALESCE(slp.price,0)*COALESCE(slp.exchangerate,1))),0) AS returntotalprice\n"
                + "FROM general.sale sl\n"
                + "LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + sd.format(endDate) + "')\n"
                + "INNER JOIN general.salepayment slp ON(sl.id=slp.sale_id AND slp.deleted=FALSE)\n"
                + "INNER JOIN system.type_dict typd ON(typd.type_id=slp.type_id AND typd.language_id=?)\n"
                + "INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "WHERE sl.processdate  BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "'\n"
                + "AND sl.branch_id=? AND sl.deleted=FALSE AND usr.type_id = 2\n"
                + "GROUP BY slp.type_id,typd.name";

        Object[] param = {sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

    @Override
    public int create(ZReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(ZReport obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ZReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ZReport> listOfStockGroupWithoutCategoies(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                + "                 -1 AS categoryid,\n"
                + "                 '' AS category,\n"
                + "                 COALESCE(SUM(t.salesAmount),0) AS salestotal,\n"
                + "                 COALESCE(SUM(t.salesQuantity),0) AS salesquantity,\n"
                + "                   COALESCE(SUM(t.returnamount),0) AS returnquantity,\n"
                + "                 COALESCE(SUM(t.returntotalprice),0) AS returntotalprice\n"
                + "             FROM(\n"
                + "             SELECT\n"
                + "                 COALESCE(SUM((CASE WHEN sl.is_return = false THEN ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,1)) ELSE 0 END) ),0) AS salesamount,\n"
                + "                 COALESCE(SUM(CASE WHEN sl.is_return = false THEN  sli.quantity ELSE 0  END),0) AS salesQuantity,\n"
                + "                 COALESCE(SUM((CASE WHEN sl.is_return = true THEN ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) * COALESCE(sli.exchangerate,1)) ELSE 0 END) ),0) AS returntotalprice,\n"
                + "                 COALESCE(SUM(CASE WHEN sl.is_return = true THEN  sli.quantity ELSE 0  END),0) AS returnamount\n"
                + "             	\n"
                + "             FROM\n"
                + "             	general.saleitem sli\n"
                + "                 INNER JOIN general.sale sl ON(sl.id = sli.sale_id AND sl.deleted = FALSE AND sl.branch_id=?)\n"
                + "                 INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "                 INNER JOIN inventory.stock stck ON(stck.id = sli.stock_id)\n"
                + "             WHERE \n"
                + "             	sli.deleted=FALSE AND usr.type_id = 2\n"
                + "                 AND sl.processdate  BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "'\n"
                + "                 AND NOT EXISTS (SELECT stccsub.stock_id FROM inventory.stock_categorization_con stccsub INNER JOIN general.categorization ctg ON(ctg.id = stccsub.categorization_id AND ctg.deleted=FALSE) WHERE stccsub.deleted =FALSE AND stccsub.stock_id=sli.stock_id)\n"
                + "             GROUP BY sli.stock_id) t\n"
                + "             HAVING COALESCE(SUM(t.salesAmount),0) <> 0";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

    @Override
    public List<ZReport> listOfOpenPayment(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT\n"
                + "           COALESCE(SUM(tt.totalmoney*tt.exchangerate),0) AS salestotal,\n"
                + "           -1 AS slptype_id,\n"
                + "           '' AS typdname\n"
                + "       FROM\n"
                + "       (    \n"
                + "         SELECT \n"
                + "             (sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)) AS totalmoney,\n"
                + "             sli.exchangerate,\n"
                + "             (SELECT count(slp.id) FROM general.salepayment slp WHERE slp.sale_id = sli.sale_id) AS paymentcount\n"
                + "         FROM \n"
                + "             general.saleitem sli   \n"
                + "             INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                + "             LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + sd.format(endDate) + "')\n"
                + "             INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "             WHERE sl.branch_id=? AND sli.deleted=False AND sl.is_return=False AND usr.type_id = 2\n"
                + "             AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                + "             AND sli.processdate  BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' \n"
                + "       ) tt\n"
                + "       WHERE\n"
                + "       	tt.paymentcount < 1\n"
                + "       ORDER BY SUM(tt.totalmoney*tt.exchangerate) DESC";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

    @Override
    public List<ZReport> listReceiptCount(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "SELECT \n"
                + "          SUM(CASE WHEN sl.is_return =FALSE THEN 1 ELSE 0 END) as salereceiptcount,\n"
                + "          SUM(CASE WHEN sl.is_return =TRUE THEN 1 ELSE 0 END) as salereturnreceiptcount\n"
                + "        FROM general.sale sl \n"
                + "        LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + sd.format(endDate) + "')\n"
                + "        INNER JOIN general.account acc ON(acc.id = sl.account_id)\n"
                + "        LEFT JOIN finance.receipt rcp ON(rcp.id=sl.receipt_id AND rcp.deleted = False)\n"
                + "        INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "        AND sl.deleted=False AND sl.branch_id= ? AND usr.type_id = 2 AND sl.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' \n"
                + "        AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

    @Override
    public List<ZReport> listReceiptTotal(Date beginDate, Date endDate) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "    SELECT \n"
                + "                        SUM(((CASE WHEN sl.is_return =FALSE THEN 1 ELSE 0 END)* COALESCE((sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0)) + (CASE WHEN sl.is_return = TRUE AND sl2.id IS NULL THEN -1 * (COALESCE((sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0)) ELSE 0 END )) as totalsaleprice,\n"
                + "                        SUM(((CASE WHEN sl.is_return =FALSE THEN 1 ELSE 0 END)* COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0)) + (CASE WHEN sl.is_return = TRUE AND sl2.id IS NULL THEN -1 * (COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0)) ELSE 0 END ))  as totalsalemoney,\n"
                + "                        SUM((CASE WHEN sl.is_return =TRUE THEN 1 ELSE 0 END)* COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0))  as totalreturnprice,\n"
                + "                        SUM((COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0)) + (CASE WHEN sl.is_return = TRUE AND sl2.id IS NULL THEN -1 * (COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) * COALESCE(sli.exchangerate,0)) ELSE 0 END ))  as totalmoneyincludereturn\n"
                + "                      FROM general.saleitem sli \n"
                + "                      INNER JOIN general.sale sl ON (sl.id = sli.sale_id AND sl.deleted = False)\n"
                + "                      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False AND sll.processdate < '" + sd.format(endDate) + "')\n"
                + "                      LEFT JOIN general.sale sl2 ON(sl.returnparent_id = sl2.id AND sl2.deleted = FALSE AND sl2.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' AND sl2.branch_id = sl.branch_id)\n"
                + "                      INNER JOIN general.userdata usr ON(usr.id = sl.userdata_id)\n"
                + "                      WHERE sl.branch_id=? AND sli.deleted=False  AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND usr.type_id = 2\n"
                + "                      AND sl.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' \n";

        Object[] param = {sessionBean.getUser().getLastBranch().getId()};
        List<ZReport> result = getJdbcTemplate().query(sql, param, new ZReportMapper());

        return result;
    }

}
