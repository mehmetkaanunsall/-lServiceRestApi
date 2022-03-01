package com.mepsan.marwiz.general.report.trialbalancereport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Samet Dağ
 */
public class TrialBalanceDao extends JdbcDaoSupport implements ITrialBalanceDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<TrialBalance> findDetail(Date date, Date firstPeriod, List<Boolean> chkBoxList, int typeStock, String whereBranch) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "";
        List<Object> params = new ArrayList<>();
        String whereB = "";
        String whereFincancingDoc = "";
        String whereBankAccountCon = "";
        // Ticari Tipindeki Banka Hesaplarının Gelir Ve Gider
        if (chkBoxList.get(2)) {
            whereB = "";

            if (!whereBranch.isEmpty()) {
                //     whereB = " AND bka.branch_id IN(" + whereBranch + ")";
                whereFincancingDoc = " AND (fdoc.branch_id IN(" + whereBranch + " ) OR fdoc.transferbranch_id IN ( " + whereBranch + " ))";
                whereBankAccountCon = " AND bkacon.branch_id IN(" + whereBranch + ")";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + "brn.id as brnid,\n"
                    + "brn.name as brnname,\n"
                    + "	0 as accname,\n"
                    + "	bka.name as name,\n"
                    + "       	COALESCE(SUM(CASE WHEN  bkam.is_direction = TRUE AND bka.type_id = 14 \n"
                    + "        			      THEN COALESCE(bkam.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=bka.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id =" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                          ELSE 0 \n"
                    + "                          END),0) AS sumincoming, \n"
                    + "       	COALESCE(SUM(CASE WHEN  bkam.is_direction = FALSE \n"
                    + "        			      THEN COALESCE(bkam.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=bka.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                          ELSE 0 \n"
                    + "                          END),0) AS sumoutcoming \n"
                    + "FROM \n"
                    + "	finance.bankaccount bka \n"
                    + " INNER JOIN finance.bankaccount_branch_con bkacon ON(bkacon.bankaccount_id=bka.id " + whereBankAccountCon + " ) \n"
                    + " INNER JOIN general.branch brn ON(brn.id=bkacon.branch_id AND brn.deleted=FALSE)\n"
                    + "	LEFT JOIN finance.bankaccountmovement bkam  ON(bka.id = bkam.bankaccount_id AND bkam.deleted = False AND bkam.movementdate BETWEEN ? AND ?)\n"
                    + "	LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False " + whereFincancingDoc + ")\n"
                    + "WHERE \n"
                    + "	bka.deleted = false\n"
                    //     + whereB + "\n"
                    + "GROUP BY \n"
                    + "	bka.name,brn.id,brn.name \n"
                    + "ORDER BY brn.id,brn.name,bka.name)\n";

            params.add(firstPeriod);
            params.add(date);

        }

        // Kredikartı Tipindeki Banka Hesaplarının Gelir Ve Gider    
        if (chkBoxList.get(11)) {
            whereB = "";
            whereFincancingDoc = "";
            if (!whereBranch.isEmpty()) {
                //      whereB = " AND bka.branch_id IN(" + whereBranch + ")\n";
                whereFincancingDoc = " AND (fdoc.branch_id IN(" + whereBranch + " ) OR fdoc.transferbranch_id IN ( " + whereBranch + " ))";
                whereBankAccountCon = " AND bkacon.branch_id IN(" + whereBranch + ")";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + "brn.id as brnid,\n"
                    + "brn.name as brnname,\n"
                    + "	8 as accname,\n"
                    + "	bka.name as name,\n"
                    + "		COALESCE(SUM(CASE WHEN bkam.is_direction = TRUE \n"
                    + "        		          THEN COALESCE(bkam.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=bka.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                          ELSE 0 \n"
                    + "                          END),0) AS sumincoming,\n"
                    + "	0 AS sumoutcoming\n"
                    + "FROM \n"
                    + "	finance.bankaccount bka \n"
                    + " INNER JOIN finance.bankaccount_branch_con bkacon ON(bkacon.bankaccount_id=bka.id " + whereBankAccountCon + " ) \n"
                    + " INNER JOIN general.branch brn ON(brn.id=bkacon.branch_id AND brn.deleted=FALSE)\n"
                    + "	LEFT JOIN finance.bankaccountmovement bkam  ON(bka.id = bkam.bankaccount_id AND bkam.deleted = False AND bkam.movementdate BETWEEN ? AND ?)\n"
                    + "	LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = FALSE " + whereFincancingDoc + ")\n"
                    + "WHERE \n"
                    + "	bka.deleted = FALSE\n"
                    + "    AND bka.type_id = 16 \n"
                    //     + whereB + "\n"
                    + "GROUP BY bka.name,brn.id,brn.name \n"
                    + "ORDER BY brn.id,brn.name,bka.name)\n";

            params.add(firstPeriod);
            params.add(date);
        }

        // Kasanın Gelir Ve Gider
        String whereSafeMovement = "";
        if (chkBoxList.get(3)) {
            whereB = "";
            if (!whereBranch.isEmpty()) {
                whereB = " AND sf.branch_id IN(" + whereBranch + ")\n";
                whereSafeMovement = " AND sfm.branch_id IN(" + whereBranch + ") ";
            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + "    brn.id as brnid,\n"
                    + "    brn.name as brnname,\n"
                    + "    1 as accname, \n"
                    + "    sf.name as name,\n"
                    + "               COALESCE(SUM(CASE WHEN  sfm.is_direction=true \n"
                    + "                             THEN COALESCE(sfm.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=sf.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                             ELSE 0 \n"
                    + "                             END),0) AS sumincoming, \n"
                    + "           COALESCE(SUM(CASE WHEN  sfm.is_direction=false \n"
                    + "           					 THEN COALESCE(sfm.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=sf.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                             ELSE 0 \n"
                    + "                             END),0) AS sumoutcoming   \n"
                    + "FROM \n"
                    + "	finance.safe sf \n"
                    + " INNER JOIN general.branch brn ON(brn.id=sf.branch_id AND brn.deleted=FALSE)\n"
                    + "	LEFT JOIN finance.safemovement sfm ON(sf.id = sfm.safe_id AND sfm.deleted=FALSE AND sfm.movementdate BETWEEN ? AND ? " + whereSafeMovement + ")\n"
                    + "WHERE \n"
                    + "	sf.deleted = FALSE\n"
                    + whereB + "\n"
                    + "GROUP BY \n"
                    + "	sf.name , brn.id ,brn.name\n"
                    + "ORDER BY brn.id,brn.name,sf.name)\n";

            params.add(firstPeriod);
            params.add(date);

        }

        // Carinin Gelir Ve Gider
        String whereAccountMov = "";
        String whereAccountCon = "";
        if (chkBoxList.get(4)) {

            if (!whereBranch.isEmpty()) {
                whereAccountMov = " AND accm.branch_id IN( " + whereBranch + " ) ";
                whereAccountCon = " AND acccon.branch_id IN( " + whereBranch + " ) ";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + "    -1 as brnid,\n"
                    + "    '' as brnname,\n"
                    + "    2 as accname,\n"
                    + "    acc.name as name,\n"
                    + "          COALESCE(SUM(CASE WHEN  accm.is_direction=true \n"
                    + "           					 THEN COALESCE(accm.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                             ELSE 0 \n"
                    + "                             END),0) AS sumincoming, \n"
                    + "           COALESCE(SUM(CASE WHEN  accm.is_direction=false \n"
                    + "           					 THEN COALESCE(accm.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                             ELSE 0 \n"
                    + "                             END),0) AS sumoutcoming \n"
                    + "FROM \n"
                    + "	general.account acc\n"
                    + " INNER JOIN general.account_branch_con acccon ON(acccon.account_id=acc.id " + whereAccountCon + " ) \n"
                    + "	LEFT JOIN general.accountmovement accm ON(acc.id = accm.account_id  AND  accm.deleted=FALSE AND accm.movementdate BETWEEN ? AND ? AND accm.branch_id =acccon.branch_id)\n"
                    + "WHERE\n"
                    + "	acc.deleted = FALSE \n"
                    + "    AND acc.is_employee = FALSE AND acc.id<>1\n"
                    + "GROUP BY acc.name \n"
                    + "ORDER BY acc.name)\n";

            params.add(firstPeriod);
            params.add(date);

        }

        // Çek/Senet Gelir Ve Gider
        String whereChequebill = "";
        if (chkBoxList.get(5)) {

            if (!whereBranch.isEmpty()) {
                whereChequebill = " AND cb.branch_id IN(" + whereBranch + ") \n";
            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + "    -1 as brnid,\n"
                    + "    '' as brnname,\n"
                    + "    3 as accname,\n"
                    + "    acc.name as name,\n"
                    + "    COALESCE(SUM(CASE WHEN  cbp.is_direction=true \n"
                    + "           					 THEN COALESCE(cbp.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=cbp.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id =" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                             ELSE 0 \n"
                    + "                             END),0) AS sumincoming, \n"
                    + "           COALESCE(SUM(CASE WHEN  cbp.is_direction=false \n"
                    + "           					 THEN COALESCE(cbp.price,0)  * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=cbp.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id =" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                             ELSE 0 \n"
                    + "                             END),0) AS sumoutcoming \n"
                    + "FROM \n"
                    + "	finance.chequebill cb\n"
                    + "	INNER JOIN finance.chequebillpayment cbp ON(cb.id = cbp.chequebill_id AND cbp.deleted = FALSE AND cbp.processdate BETWEEN ? AND ?)\n"
                    + "	LEFT JOIN general.account acc ON(acc.id = cb.account_id AND acc.deleted = FALSE)\n"
                    + "WHERE \n"
                    + "	cb.deleted = FALSE	\n" + whereChequebill + " \n"
                    + "GROUP BY acc.name \n"
                    + "ORDER BY acc.name)\n";

            params.add(firstPeriod);
            params.add(date);

        }

        // Personel Gelir Ve Gider
        if (chkBoxList.get(6)) {
            whereB = "";
            whereAccountMov = "";
            whereAccountCon = "";
            if (!whereBranch.isEmpty()) {
                whereB = "  AND empi.branch_id IN(" + whereBranch + ")\n";
                whereAccountMov = "  AND accm.branch_id IN(" + whereBranch + ") ";
                whereAccountCon = " AND acccon.branch_id IN( " + whereBranch + " ) ";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + " brn.id as brnid,\n"
                    + " brn.name as brnname,\n"
                    + "	4 as accname,\n"
                    + "	acc.name as name,\n"
                    + "	 	COALESCE(SUM(CASE WHEN  accm.is_direction=true \n"
                    + "        				  THEN COALESCE(accm.price,0)* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) \n"
                    + "                          ELSE 0 \n"
                    + "                          END),0) AS sumincoming, \n"
                    + "       	COALESCE(SUM(CASE WHEN  accm.is_direction=false \n"
                    + "        				  THEN COALESCE(accm.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) \n"
                    + "                          ELSE 0 \n"
                    + "                          END),0) AS sumoutcoming \n"
                    + "FROM \n"
                    + "	general.account acc\n"
                    + " INNER JOIN general.account_branch_con acccon ON(acccon.account_id=acc.id " + whereAccountCon + " ) \n"
                    + "	LEFT JOIN general.accountmovement accm ON(acc.id = accm.account_id  AND  accm.deleted=FALSE AND accm.movementdate BETWEEN ? AND ? AND accm.branch_id =acccon.branch_id)\n"
                    + "	LEFT JOIN general.employeeinfo empi ON(empi.account_id = acc.id AND empi.deleted=FALSE)\n"
                    + " LEFT JOIN general.branch brn ON(brn.id=empi.branch_id AND brn.deleted=FALSE)\n"
                    + "WHERE \n"
                    + "	acc.deleted = FALSE \n"
                    + "    AND acc.is_employee = TRUE \n"
                    + whereB + "\n"
                    + "GROUP BY acc.name,brn.id ,brn.name \n"
                    + "ORDER BY brn.id,brn.name,acc.name)\n";

            params.add(firstPeriod);
            params.add(date);

        }

        // Kredi Gelir Ve Gider 
        String whereCredit = "";
        if (chkBoxList.get(9)) {

            if (!whereBranch.isEmpty()) {
                whereCredit = "  AND crdt.branch_id IN(" + whereBranch + ")\n";
            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + "    -1 as brnid,\n"
                    + "    '' as brnname,\n"
                    + "    6 as accname,\n"
                    + "    acc.name as name,\n"
                    + "   COALESCE(SUM(CASE WHEN crdt.is_customer=TRUE \n"
                    + "                     THEN crdt.money * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=crdt.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                             ELSE 0 \n"
                    + "                             END),0) as sumincoming, \n"
                    + "           COALESCE(SUM(CASE WHEN crdt.is_customer=FALSE \n"
                    + "                             THEN crdt.money* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=crdt.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                              ELSE 0 \n"
                    + "                              END),0) as sumoutcoming \n"
                    + "FROM \n"
                    + "	finance.credit crdt \n"
                    + "	INNER JOIN general.account acc ON(acc.id = crdt.account_id AND acc.deleted=FALSE)\n"
                    + "WHERE \n"
                    + "	crdt.deleted=FALSE\n " + whereCredit + " \n"
                    + "	AND crdt.is_paid = FALSE\n"
                    + "	AND crdt.is_cancel = FALSE\n"
                    + "	AND crdt.processdate BETWEEN ? AND ?\n"
                    + "GROUP BY acc.name)\n";

            params.add(firstPeriod);
            params.add(date);

        }

        // Gelir ve Gider
        String whereIncomeExpense = "";
        String whereIncomeExpenseMov = "";
        if (chkBoxList.get(10)) {

            if (!whereBranch.isEmpty()) {
                whereIncomeExpense = "  AND ie.branch_id IN(" + whereBranch + ")\n";
                whereIncomeExpenseMov = "  AND iem.branch_id IN(" + whereBranch + ") ";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "(SELECT \n"
                    + "    -1 as brnid,\n"
                    + "    '' as brnname,\n"
                    + "    7 as accname, \n"
                    + "    ie.name,\n"
                    + "     CASE WHEN ie.is_income=TRUE \n"
                    + "               THEN COALESCE(SUM(iem.price* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=iem.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)),0)\n"
                    + "               ELSE -1 \n"
                    + "               END AS sumincoming, \n"
                    + "          CASE WHEN ie.is_income=FALSE \n"
                    + "               THEN COALESCE(SUM(iem.price* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=iem.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)) ,0)\n"
                    + "               ELSE -2 \n"
                    + "               END AS sumoutcoming \n"
                    + "FROM \n"
                    + "	finance.incomeexpense ie \n"
                    + "	LEFT JOIN finance.incomeexpensemovement iem ON(ie.id=iem.incomeexpense_id AND iem.deleted = FALSE AND iem.movementdate BETWEEN ? AND ? " + whereIncomeExpenseMov + ")\n"
                    + "WHERE \n"
                    + "	ie.deleted = FALSE\n" + whereIncomeExpense + "\n"
                    + "	AND ie.parent_id IS NOT NULL \n"
                    + "GROUP BY \n"
                    + "	ie.name,\n"
                    + "	ie.id,\n"
                    + "   ie.is_income)\n";

            params.add(firstPeriod);
            params.add(date);

        }

        // Ürün Gelir ve Gider
        if (chkBoxList.get(1)) {
            String whereWareHouse = "";
            String ware = "";
            if (!whereBranch.isEmpty()) {
                whereWareHouse = " AND wr.branch_id IN(" + whereBranch + ")\n";
                ware = " AND w.branch_id IN(" + whereBranch + ")";
            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }

            sql += "(SELECT \n"
                    + " brn.id as brnid,\n"
                    + " brn.name as brnname,\n"
                    + "	5 as accname,\n"
                    + " '[{\"warehouse\":\"'||COALESCE(w.name,'')||'\"},{\"brand\":\"'||COALESCE(brnd.name,'')||'\"},{\"stock\":\"'||COALESCE(stck.name,'')||'\"}]'  as name,\n "
                    + "	 CASE ? \n"
                    + "             WHEN 1 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentpurchasecurrency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) ),0)--kdvli satın alma     \n"
                    + "             WHEN 2 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentpurchasecurrency_id AND exc.exchangedate < '" + sd.format(date) + "'  AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)  ),0)--kdv siz satın alma\n"
                    + "             WHEN 3 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * COALESCE(si.currentsaleprice,0)* (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentsalecurrency_id AND exc.exchangedate < '" + sd.format(date) + "'  AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) ),0)--kdv li satış\n"
                    + "             WHEN 4 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentsaleprice,0)/(1+(COALESCE(tg.rate,0)/100)))* (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentsalecurrency_id AND exc.exchangedate < '" + sd.format(date) + "'  AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) ),0)--kdvsiz satış\n"
                    + "            END as sumincoming,\n"
                    + "	0 as sumoutcoming\n"
                    + "FROM \n"
                    + "	inventory.warehouse w \n"
                    + " INNER JOIN general.branch brn ON(brn.id=w.branch_id AND brn.deleted=FALSE)\n"
                    + "	INNER JOIN inventory.warehouseitem wi ON(w.id = wi.warehouse_id AND wi.deleted = FALSE)\n"
                    + "	LEFT JOIN inventory.stockinfo si ON(si.stock_id=wi.stock_id AND si.deleted=FALSE AND si.branch_id = w.branch_id)\n"
                    + " LEFT JOIN (SELECT \n"
                    + "          txg.rate AS rate,\n"
                    + "          stc.stock_id AS stock_id \n"
                    + "          FROM inventory.stock_taxgroup_con stc  \n"
                    + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                    + "          WHERE stc.deleted = false\n"
                    + "          AND txg.type_id = 10 --kdv grubundan \n"
                    + "          AND stc.is_purchase = ?) tg ON(tg.stock_id = si.stock_id)\n"
                    + "	LEFT JOIN inventory.stock stck ON(stck.id=wi.stock_id)\n"
                    + "	LEFT JOIN general.brand brnd ON(brnd.id=stck.brand_id)\n"
                    + "    LEFT JOIN (\n"
                    + "            SELECT \n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id,\n"
                    + "                COALESCE(SUM(wrm.quantity),0) AS quantity\n"
                    + "            FROM\n"
                    + "            	inventory.warehouse wr\n"
                    + "                INNER JOIN inventory.warehousemovement wrm ON (\n"
                    + "                	wrm.warehouse_id = wr.id\n"
                    + "                	AND wrm.is_direction = TRUE \n"
                    + "                    AND wrm.deleted = FALSE\n"
                    + "                )\n"
                    + "                INNER JOIN inventory.warehousereceipt wrp ON(wrp.id = wrm.warehousereceipt_id AND wrp.deleted=FALSE AND wrp.processdate BETWEEN ? AND ?)\n  "
                    + "            WHERE\n"
                    + "                wr.deleted = FALSE\n"
                    + whereWareHouse + "\n"
                    + "            GROUP BY\n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id\n"
                    + "    ) inn ON (inn.stock_id = wi.stock_id AND inn.warehouse_id = w.id)\n"
                    + "    LEFT JOIN (\n"
                    + "            SELECT \n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id,\n"
                    + "                COALESCE(SUM(wrm.quantity),0) AS quantity\n"
                    + "            FROM\n"
                    + "            	inventory.warehouse wr\n"
                    + "                INNER JOIN inventory.warehousemovement wrm ON (\n"
                    + "                	wrm.warehouse_id = wr.id\n"
                    + "                	AND wrm.is_direction = FALSE \n"
                    + "                    AND wrm.deleted = FALSE\n"
                    + "                )\n"
                    + "                INNER JOIN inventory.warehousereceipt wrp ON(wrp.id = wrm.warehousereceipt_id AND wrp.deleted=FALSE AND wrp.processdate BETWEEN ? AND ?)\n  "
                    + "            WHERE\n"
                    + "                wr.deleted = FALSE\n"
                    + whereWareHouse + "\n"
                    + "            GROUP BY\n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id\n"
                    + "    ) out ON (out.stock_id = wi.stock_id AND out.warehouse_id = w.id)\n"
                    + "WHERE \n"
                    + "	w.deleted=FALSE\n"
                    + ware + "	 \n"
                    + "GROUP BY \n"
                    + "	brnd.name,\n"
                    + "    w.name,\n"
                    + "    stck.name,\n"
                    + "    wi.quantity,\n"
                    + "    inn.quantity,\n"
                    + "    out.quantity, \n"
                    + "	   brn.id,\n"
                    + "    brn.name\n"
                    + "ORDER BY \n"
                    + "brn.id,brn.name,\n"
                    + "	w.name,\n"
                    + "    brnd.name,\n"
                    + "    stck.name)\n";

            params.add(typeStock);
            params.add(typeStock == 1 || typeStock == 2);
            params.add(firstPeriod);
            params.add(date);
            params.add(firstPeriod);
            params.add(date);

        }

        List<TrialBalance> result = getJdbcTemplate().query(sql, params.toArray(), new TrialBalanceMapper());

        return result;
    }

    @Override
    public List<TrialBalance> findAll(Date date, Date firstPeriod, List<Boolean> chkBoxList, int typeStock, String whereBranch) {
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String sql = "";
        List<Object> params = new ArrayList<>();
        String whereB = "";
        // Ürünler
        if (chkBoxList.get(1)) {

            String whereWareHouse = "";
            String ware = "";
            if (!whereBranch.isEmpty()) {
                whereWareHouse = " AND wr.branch_id IN(" + whereBranch + ")\n";
                ware = " AND w.branch_id IN(" + whereBranch + ")";
            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "SELECT \n"
                    + "     7 AS accname,\n"
                    + "      CASE ? \n"
                    + "             WHEN 1 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentpurchasecurrency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) ),0)--kdvli satın alma     \n"
                    + "             WHEN 2 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentpurchasecurrency_id AND exc.exchangedate < '" + sd.format(date) + "'  AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)  ),0)--kdv siz satın alma\n"
                    + "             WHEN 3 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * COALESCE(si.currentsaleprice,0)* (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentsalecurrency_id AND exc.exchangedate < '" + sd.format(date) + "'  AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) ),0)--kdv li satış\n"
                    + "             WHEN 4 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentsaleprice,0)/(1+(COALESCE(tg.rate,0)/100)))* (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=si.currentsalecurrency_id AND exc.exchangedate < '" + sd.format(date) + "'  AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) ),0)--kdvsiz satış\n"
                    + "            END as sumincoming,\n"
                    + "     0 as sumoutcoming\n"
                    + "FROM \n"
                    + "	inventory.warehouse w \n"
                    + " INNER JOIN general.branch brn ON(brn.id=w.branch_id AND brn.deleted=FALSE)\n"
                    + "	INNER JOIN inventory.warehouseitem wi ON(w.id = wi.warehouse_id AND wi.deleted = FALSE)\n"
                    + "	LEFT JOIN inventory.stockinfo si ON(si.stock_id=wi.stock_id AND si.deleted=FALSE AND si.branch_id = w.branch_id)\n"
                    + " LEFT JOIN (SELECT \n"
                    + "          txg.rate AS rate,\n"
                    + "          stc.stock_id AS stock_id \n"
                    + "          FROM inventory.stock_taxgroup_con stc  \n"
                    + "          INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                    + "          WHERE stc.deleted = false\n"
                    + "          AND txg.type_id = 10 --kdv grubundan \n"
                    + "          AND stc.is_purchase = ?) tg ON(tg.stock_id = si.stock_id)\n"
                    + "    LEFT JOIN (\n"
                    + "            SELECT \n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id,\n"
                    + "                COALESCE(SUM(wrm.quantity),0) AS quantity\n"
                    + "            FROM\n"
                    + "            	inventory.warehouse wr\n"
                    + "                INNER JOIN inventory.warehousemovement wrm ON (\n"
                    + "                	wrm.warehouse_id = wr.id\n"
                    + "                	AND wrm.is_direction = TRUE \n"
                    + "                    AND wrm.deleted = FALSE\n"
                    + "                )\n"
                    + "                INNER JOIN inventory.warehousereceipt wrp ON(wrp.id = wrm.warehousereceipt_id AND wrp.deleted=FALSE AND wrp.processdate BETWEEN ? AND ?)\n  "
                    + "            WHERE\n"
                    + "                wr.deleted = FALSE\n"
                    + whereWareHouse + "\n"
                    + "            GROUP BY\n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id\n"
                    + "    ) inn ON (inn.stock_id = wi.stock_id AND inn.warehouse_id = w.id)\n"
                    + "    LEFT JOIN (\n"
                    + "            SELECT \n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id,\n"
                    + "                COALESCE(SUM(wrm.quantity),0) AS quantity\n"
                    + "            FROM\n"
                    + "            	inventory.warehouse wr\n"
                    + "                INNER JOIN inventory.warehousemovement wrm ON (\n"
                    + "                	wrm.warehouse_id = wr.id\n"
                    + "                	AND wrm.is_direction = FALSE \n"
                    + "                    AND wrm.deleted = FALSE\n"
                    + "                )\n"
                    + "                INNER JOIN inventory.warehousereceipt wrp ON(wrp.id = wrm.warehousereceipt_id AND wrp.deleted=FALSE AND wrp.processdate BETWEEN ? AND ?)\n  "
                    + "            WHERE\n"
                    + "                wr.deleted = FALSE\n"
                    + whereWareHouse + "\n"
                    + "            GROUP BY\n"
                    + "                wrm.stock_id,\n"
                    + "                wrm.warehouse_id\n"
                    + "    ) out ON (out.stock_id = wi.stock_id AND out.warehouse_id = w.id)   \n"
                    + "WHERE \n"
                    + "	w.deleted=FALSE \n"
                    + ware + " \n";

            params.add(typeStock);
            params.add(typeStock == 1 || typeStock == 2);
            params.add(firstPeriod);
            params.add(date);
            params.add(firstPeriod);
            params.add(date);

          
        }

        // Ticari Tipindeki Banka Hesaplarının Gelir Ve Gider
        String whereBankAccountMov = "";
        String whereFinancingDocument = "";
        String whereBankAccountCon = "";
        if (chkBoxList.get(2)) {
            whereB = "";
            if (!whereBranch.isEmpty()) {
                //  whereB = " AND bka.branch_id IN (" + whereBranch + ")\n";
                whereBankAccountMov = " AND bkam.branch_id IN(" + whereBranch + ") \n";
                whereFinancingDocument = " AND (fdoc.branch_id IN(" + whereBranch + " ) OR fdoc.transferbranch_id IN ( " + whereBranch + " ))";
                whereBankAccountCon = " AND bkacon.branch_id IN(" + whereBranch + ")\n";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += " SELECT \n"
                    + "               tt.accname as accname,\n"
                    + "               SUM(tt.sumincoming) as sumincoming,\n"
                    + "               SUM(tt.sumoutcoming) as sumoutcoming          \n"
                    + "              FROM (SELECT\n"
                    + "                  	0 as accname,\n"
                    + "                  	COALESCE(SUM(CASE WHEN  bkam.is_direction = FALSE THEN COALESCE(bkam.price,0) ELSE 0 END),0)* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=bka.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) AS sumoutcoming,\n"
                    + "                    COALESCE(SUM(CASE WHEN  (bkam.is_direction = TRUE AND bka.type_id=14)  THEN COALESCE(bkam.price,0) ELSE 0 END),0)* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=bka.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id =" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)  AS sumincoming\n"
                    + "                  FROM \n"
                    + "                  	finance.bankaccount bka\n"
                    + " INNER JOIN finance.bankaccount_branch_con bkacon ON(bkacon.bankaccount_id=bka.id " + whereBankAccountCon + " ) \n"
                    + "                  	LEFT JOIN finance.bankaccountmovement bkam  ON(bka.id = bkam.bankaccount_id AND bkam.deleted = FALSE AND bkam.movementdate BETWEEN ? AND ? AND bkacon.branch_id=bkam.branch_id)\n"
                    + "                  	LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = FALSE AND (fdoc.branch_id=bkacon.branch_id OR fdoc.transferbranch_id = bkacon.branch_id))\n"
                    + "                  WHERE \n"
                    + "                  bka.deleted = false\n"
                    + whereB + "\n"
                    + "                  GROUP BY bka.currency_id) tt\n"
                    + "                  GROUP BY tt.accname\n";

            params.add(firstPeriod);
            params.add(date);
            

        }

        // Kasanın Gelir Ve Gider
        String whereSafeMov = "";
        if (chkBoxList.get(3)) {

            whereB = "";
            if (!whereBranch.isEmpty()) {
                whereB = " AND sf.branch_id IN(" + whereBranch + ")\n";
                whereSafeMov = " AND sfm.branch_id IN(" + whereBranch + ")\n";
            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "SELECT \n"
                    + "tt.accname as accname,\n"
                    + "SUM(tt.sumincoming) as sumincoming,\n"
                    + "SUM(tt.sumoutcoming) as sumoutcoming\n"
                    + "FROM\n"
                    + " (SELECT\n"
                    + "      	1 AS accname,\n"
                    + "        sf.currency_id ,\n"
                    + "      	COALESCE(SUM(CASE WHEN  sfm.is_direction=true THEN COALESCE(sfm.price,0) ELSE 0 END),0)*COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=sf.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) AS sumincoming,\n"
                    + "      	COALESCE(SUM(CASE WHEN  sfm.is_direction=false THEN COALESCE(sfm.price,0) ELSE 0 END),0)*COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=sf.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) AS sumoutcoming\n"
                    + "      FROM \n"
                    + "      	finance.safe sf \n"
                    + "       INNER JOIN general.branch brn ON(brn.id=sf.branch_id AND brn.deleted=FALSE)\n"
                    + "       LEFT JOIN finance.safemovement sfm ON(sf.id = sfm.safe_id AND sfm.deleted=FALSE AND sfm.movementdate BETWEEN ? AND ? " + whereSafeMov + ")\n"
                    + "      WHERE \n"
                    + "      	sf.deleted = FALSE\n"
                    + whereB + "\n"
                    + "        GROUP BY sf.currency_id )tt \n"
                    + "        GROUP BY tt.accname\n";

            params.add(firstPeriod);
            params.add(date);

          

        }

        // Cari Üst Toplam
        String whereAccountMov = "";
        String whereAccountCon = "";
        if (chkBoxList.get(4)) {
            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }

            if (!whereBranch.isEmpty()) {
                whereAccountMov = " AND accm.branch_id IN(" + whereBranch + ")\n";
                whereAccountCon = " AND acccon.branch_id IN(" + whereBranch + ")\n";

            }

            sql += "SELECT \n"
                    + "2 as accname,\n"
                    + "COALESCE(SUM(CASE WHEN  accm.is_direction=true \n"
                    + "          	THEN COALESCE(accm.price,0)* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                 ELSE 0 \n"
                    + "                 END)\n"
                    + "                            ,0) AS sumincoming,\n"
                    + "COALESCE(SUM(CASE WHEN  accm.is_direction=false \n"
                    + "          	THEN COALESCE(accm.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                 ELSE 0 \n"
                    + "                 END),0) AS sumoutcoming\n"
                    + "FROM \n"
                    + "	general.account acc\n"
                    + " INNER JOIN general.account_branch_con acccon ON(acccon.account_id=acc.id " + whereAccountCon + " ) \n"
                    + "	LEFT JOIN general.accountmovement accm ON(acc.id = accm.account_id  AND  accm.deleted=FALSE AND accm.movementdate BETWEEN ? AND ? AND acccon.branch_id=accm.branch_id)\n"
                    + "WHERE acc.deleted = FALSE \n"
                    + "AND acc.is_employee = FALSE AND acc.id<>1 \n";//Parekende müşterinin hareketleri gelmesin istendi

            params.add(firstPeriod);
            params.add(date);
           

        }

        // Çek/Senet Gelir Ve Gider
        String whereChequebill = "";
        if (chkBoxList.get(5)) {
            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }

            if (!whereBranch.isEmpty()) {
                whereChequebill = " AND cb.branch_id IN(" + whereBranch + ")\n";
            }

            sql += "SELECT\n"
                    + "	3 AS accname,\n"
                    + "	COALESCE(SUM(CASE WHEN  cbp.is_direction=true \n"
                    + "        		THEN COALESCE(cbp.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=cbp.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                 ELSE 0 \n"
                    + "                 END),0) AS sumincoming,\n"
                    + " COALESCE(SUM(CASE WHEN  cbp.is_direction=false \n"
                    + "        		THEN COALESCE(cbp.price,0)* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=cbp.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                 ELSE 0 \n"
                    + "                 END),0) AS sumoutcoming\n"
                    + "FROM \n"
                    + "	finance.chequebill cb\n"
                    + "	INNER JOIN finance.chequebillpayment cbp ON(cb.id=cbp.chequebill_id AND cbp.deleted=FALSE AND cbp.processdate BETWEEN ? AND ?)\n"
                    + "WHERE \n"
                    + "	cb.deleted = FALSE \n" + whereChequebill + "\n";

            params.add(firstPeriod);
            params.add(date);

          

        }

        // Personel Gelir Ve Gider
        if (chkBoxList.get(6)) {
            whereAccountMov = "";
            whereB = "";
            whereAccountCon = "";
            if (!whereBranch.isEmpty()) {
                whereB = " AND empi.branch_id IN(" + whereBranch + ")\n";
                whereAccountMov = " AND accm.branch_id IN(" + whereBranch + ")\n";
                whereAccountCon = " AND acccon.branch_id IN(" + whereBranch + ")\n";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "SELECT\n"
                    + "      	4 AS accname,\n"
                    + "      	COALESCE(SUM(CASE WHEN  accm.is_direction=true \n"
                    + "        			 THEN COALESCE(accm.price,0)* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) \n"
                    + "                     ELSE 0 \n"
                    + "                     END),0) AS sumincoming,\n"
                    + "      	COALESCE(SUM(CASE WHEN  accm.is_direction=false \n"
                    + "                     THEN COALESCE(accm.price,0) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=accm.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) \n"
                    + "                     ELSE 0 \n"
                    + "                     END),0) AS sumoutcoming\n"
                    + "      FROM \n"
                    + "      	general.account acc\n"
                    + " INNER JOIN general.account_branch_con acccon ON(acccon.account_id=acc.id " + whereAccountCon + " ) \n"
                    + "      	LEFT JOIN general.accountmovement accm ON(acc.id = accm.account_id  AND  accm.deleted=FALSE AND accm.movementdate BETWEEN ? AND ? AND accm.branch_id=acccon.branch_id)\n"
                    + "      	LEFT JOIN general.employeeinfo empi ON(empi.account_id = acc.id AND empi.deleted=FALSE)\n"
                    + "       INNER JOIN general.branch brn ON(empi.branch_id=brn.id AND brn.deleted=FALSE)\n"
                    + "      WHERE \n"
                    + "      	acc.deleted = FALSE  \n"
                    + "      	AND acc.is_employee = TRUE AND empi.branch_id=acccon.branch_id\n"
                    + whereB + "\n";

            params.add(firstPeriod);
            params.add(date);

          

        }

        // Gelir ve Gider
        String whereIncomeExpenseMov = "";
        if (chkBoxList.get(7) || chkBoxList.get(8)) {
            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }

            if (!whereBranch.isEmpty()) {
                whereIncomeExpenseMov = " AND iem.branch_id IN(" + whereBranch + ") \n";
            }

            sql += "SELECT \n"
                    + "       	5 AS accname, \n"
                    + "       	COALESCE(SUM(CASE WHEN ie.is_income=TRUE \n"
                    + "        		          THEN iem.price* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=iem.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id =" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) \n"
                    + "                          ELSE 0 \n"
                    + "                          END),0) AS sumincoming,\n"
                    + "       	COALESCE(SUM(CASE WHEN ie.is_income=FALSE \n"
                    + "        			      THEN iem.price*COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=iem.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1)\n"
                    + "                          ELSE 0 \n"
                    + "                          END),0) AS sumoutcoming\n"
                    + "       FROM \n"
                    + "       	finance.incomeexpense ie \n"
                    + "       	LEFT JOIN finance.incomeexpensemovement iem ON(ie.id = iem.incomeexpense_id AND iem.deleted = FALSE AND iem.movementdate BETWEEN ? AND ?" + whereIncomeExpenseMov + ")\n"
                    + "       WHERE \n"
                    + "       	ie.deleted = FALSE\n"
                    + "       	AND ie.parent_id IS NOT NULL";

            params.add(firstPeriod);
            params.add(date);

           

        }

        //  Kredi Gelir Ve Gider 
        String whereCredit = "";
        if (chkBoxList.get(9)) {
            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }

            if (!whereBranch.isEmpty()) {
                whereCredit = " AND crdt.branch_id IN(" + whereBranch + ") \n";
            }

            sql += "SELECT\n"
                    + "          6 AS accname,\n"
                    + "          COALESCE(SUM(CASE WHEN crdt.is_customer=true \n"
                    + "          		     		THEN (crdt.money) * COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=crdt.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id =" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) \n"
                    + "                            ELSE 0 \n"
                    + "                            END),0) AS sumincoming,\n"
                    + "          COALESCE(SUM(CASE WHEN crdt.is_customer=false \n"
                    + "          				    THEN (crdt.money)* COALESCE((SELECT exc.buying  FROM finance.exchange exc WHERE exc.currency_id=crdt.currency_id AND exc.exchangedate < '" + sd.format(date) + "' AND exc.responsecurrency_id =" + sessionBean.getUser().getLastBranch().getCurrency().getId() + " AND exc.deleted=FALSE ORDER BY exc.c_time DESC LIMIT 1),1) \n"
                    + "                            ELSE 0 \n"
                    + "                            END),0) AS sumoutcoming\n"
                    + "      FROM  \n"
                    + "      	finance.credit crdt \n"
                    + "      WHERE \n"
                    + "      	crdt.deleted=FALSE\n" + whereCredit + "\n"
                    + "      	AND crdt.is_paid = FALSE\n"
                    + "      	AND crdt.is_cancel = FALSE\n"
                    + "      	AND crdt.processdate BETWEEN ? AND ? ";

            params.add(firstPeriod);
            params.add(date);
           

        }

        // Kredikartı Tipindeki Banka Hesaplarının Gelir Ve Gider
        if (chkBoxList.get(11)) {
            whereBankAccountMov = "";
            whereFinancingDocument = "";
            whereB = "";
            whereBankAccountCon = "";
            if (!whereBranch.isEmpty()) {
                //     whereB = " AND bka.branch_id IN(" + whereBranch + ")\n";
                whereBankAccountMov = " AND bkam.branch_id IN(" + whereBranch + ")\n";
                whereFinancingDocument = " AND (fdoc.branch_id IN(" + whereBranch + " ) OR fdoc.transferbranch_id IN ( " + whereBranch + " ))";
                whereBankAccountCon = " AND bkacon.branch_id IN(" + whereBranch + ")\n";

            }

            if (!sql.equals("")) {
                sql += " UNION ALL \n";
            }
            sql += "SELECT\n"
                    + "	8 as accname,\n"
                    + "	COALESCE(SUM(CASE WHEN  bkam.is_direction = TRUE THEN COALESCE(bkam.price,0) ELSE 0 END),0) AS sumincoming,\n"
                    + "	0 AS sumoutcoming\n"
                    + "FROM \n"
                    + "	finance.bankaccount bka \n"
                    + " INNER JOIN finance.bankaccount_branch_con bkacon ON(bkacon.bankaccount_id=bka.id " + whereBankAccountCon + " ) \n"
                    + " INNER JOIN general.branch brn ON(brn.id=bkacon.branch_id AND brn.deleted=FALSE)\n"
                    + "	LEFT JOIN finance.bankaccountmovement bkam  ON(bka.id = bkam.bankaccount_id AND bkam.deleted = False AND bkam.movementdate BETWEEN ? AND ? AND bkam.branch_id=brn.id)\n"
                    + "	LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False " + whereFinancingDocument + ")\n"
                    + "WHERE \n"
                    + "	bka.deleted = FALSE\n"
                    + "	AND bka.type_id = 16  \n";
            //    + whereB + "\n";

            params.add(firstPeriod);
            params.add(date);
           

        }

      
        if (!sql.equals("")) {
            return getJdbcTemplate().query(sql, params.toArray(), new TrialBalanceMapper());
        } else {
            return new ArrayList<>();
        }
    }

}
