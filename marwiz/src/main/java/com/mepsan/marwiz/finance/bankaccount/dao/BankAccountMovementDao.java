/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bankaccount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankAccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BankAccountMovementDao extends JdbcDaoSupport implements IBankAccountMovementDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<BankAccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId) {
        String sql;
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        Object[] params;
        String whereType = "";

        if (financingTypeId > 0) {
            whereType = " AND fdoc.type_id = " + financingTypeId + " \n";
        }

        switch (opType) {
            case 1:
                //sadece gelen
                sql = "SELECT\n"
                        + "fdoc.id as fdocid,\n"
                        + "fdoc.documentnumber as fdocdocumentnumber, \n"
                        + "fdoc.description as fdocdescription, \n"
                        + "fiem.incomeexpense_id as fiemincomeexpense_id, \n"
                        + "fie.name as fiename, \n"
                        + "fdoc.type_id as fdoctype_id,\n"
                        + "typd.name AS typdname,\n"
                        + "fdoc.c_id as fdocc_id,\n"
                        + "fdoc.c_time as fdocc_time,\n"
                        + "fdoc.u_id as fdocu_id,\n"
                        + "fdoc.u_time as fdocu_time,\n"
                        + "fdoc.documentdate AS fdocdocumnetdate,\n"
                        + "usr.name as usrname,\n"
                        + "usr.surname as usrsurname,\n"
                        + "usr1.name as usr1name,\n"
                        + "usr1.surname as usr1surname,\n"
                        + "bkam.id as bkamid,\n"
                        + "bkam.price as bkamprice,\n"
                        + "bkam.is_direction as bkamis_direction, \n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	bam2.price as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "                   LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bam2.financingdocument_id AND fdoc.deleted=False)\n"
                        + "    		WHERE bam2.bankaccount_id = bka.id\n"
                        + whereType + "\n"
                        + "             AND bam2.deleted=false AND bam2.is_direction=true AND bam2.branch_id = bkam.branch_id \n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY fdoc.id DESC NULLS LAST, bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ")+SUM(bkam.price) \n"
                        + "OVER(ORDER BY fdoc.id NULLS FIRST, bkam.movementdate,bkam.id \n"
                        + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS bkabalance,\n"
                        + "bkam.branch_id AS bkambranch_id,\n"
                        + "br.name AS brname,\n"
                        + "bka.currency_id AS bkacurrency_id,\n"
                        + "bac.id AS bacid\n"
                        + "FROM   \n"
                        + "finance.bankaccountmovement bkam \n"
                        + "INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False)  \n"
                        + "LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)   \n"
                        + "LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                        + "LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "LEFT JOIN general.branch br ON(br.id = bkam.branch_id AND br.deleted=FALSE)\n"
                        + "LEFT JOIN finance.bankaccountcommission bac ON((bac.financingdocument_id = fdoc.id OR bac.commissionfinancingdocument_id=fdoc.id) AND bac.deleted=FALSE)\n"
                        + "WHERE   \n"
                        + "bkam.bankaccount_id=? AND bkam.deleted = false AND bkam.is_direction=true AND bkam.branch_id IN ( " + branchList + " )\n"
                        + " AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' \n" //where 
                        + where + "\n"
                        + whereType + "\n"
                        + "ORDER BY fdoc.id DESC NULLS LAST, bkam.movementdate DESC, bkam.id DESC\n"
                        + " limit " + pageSize + " offset " + first;
                params = new Object[]{sessionBean.getUser().getLanguage().getId(), bankAcount.getId()};
                break;
            //sadece giden
            case 2:
                sql = "SELECT\n"
                        + "fdoc.id as fdocid,\n"
                        + "fdoc.documentnumber as fdocdocumentnumber, \n"
                        + "fdoc.description as fdocdescription, \n"
                        + "fiem.incomeexpense_id as fiemincomeexpense_id, \n"
                        + "fie.name as fiename, \n"
                        + "fdoc.type_id as fdoctype_id,\n"
                        + "typd.name AS typdname,\n"
                        + "fdoc.c_id as fdocc_id,\n"
                        + "fdoc.c_time as fdocc_time,\n"
                        + "fdoc.u_id as fdocu_id,\n"
                        + "fdoc.u_time as fdocu_time,\n"
                        + "fdoc.documentdate AS fdocdocumnetdate,\n"
                        + "usr.name as usrname,\n"
                        + "usr.surname as usrsurname,\n"
                        + "usr1.name as usr1name,\n"
                        + "usr1.surname as usr1surname,\n"
                        + "bkam.id as bkamid,\n"
                        + "bkam.price as bkamprice,\n"
                        + "bkam.is_direction as bkamis_direction, \n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	-(bam2.price) as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "                   LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bam2.financingdocument_id AND fdoc.deleted=False)\n"
                        + "    		WHERE bam2.bankaccount_id = bka.id\n"
                        + whereType + "\n"
                        + "             AND bam2.deleted=false AND bam2.is_direction=false AND bam2.branch_id = bkam.branch_id \n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY fdoc.id DESC NULLS LAST, bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ")+SUM((-bkam.price)) \n"
                        + "OVER(ORDER BY fdoc.id NULLS FIRST, bkam.movementdate,bkam.id \n"
                        + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS bkabalance,\n"
                        + "bkam.branch_id AS bkambranch_id,\n"
                        + "br.name AS brname,\n"
                        + "bka.currency_id AS bkacurrency_id,\n"
                        + "bac.id AS bacid\n"
                        + "FROM   \n"
                        + "finance.bankaccountmovement bkam \n"
                        + "INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False)  \n"
                        + "LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)   \n"
                        + "LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                        + "LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "LEFT JOIN general.branch br ON(br.id = bkam.branch_id AND br.deleted=FALSE)\n"
                        + "LEFT JOIN finance.bankaccountcommission bac ON((bac.financingdocument_id = fdoc.id OR bac.commissionfinancingdocument_id=fdoc.id) AND bac.deleted=FALSE)\n"
                        + "WHERE   \n"
                        + "bkam.bankaccount_id=? AND bkam.deleted = false AND bkam.is_direction=false AND bkam.branch_id IN ( " + branchList + " )\n"
                        + "AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' \n" //where
                        + where + "\n"
                        + whereType + "\n"
                        + "ORDER BY fdoc.id DESC NULLS LAST, bkam.movementdate DESC, bkam.id DESC\n"
                        + " limit " + pageSize + " offset " + first;
                params = new Object[]{sessionBean.getUser().getLanguage().getId(), bankAcount.getId()};
                break;
            default:
                //hepsi
                sql = "SELECT\n"
                        + "fdoc.id as fdocid,\n"
                        + "fdoc.documentnumber as fdocdocumentnumber, \n"
                        + "fdoc.description as fdocdescription, \n"
                        + "fiem.incomeexpense_id as fiemincomeexpense_id, \n"
                        + "fie.name as fiename, \n"
                        + "fdoc.type_id as fdoctype_id,\n"
                        + "typd.name AS typdname,\n"
                        + "fdoc.c_id as fdocc_id,\n"
                        + "fdoc.c_time as fdocc_time,\n"
                        + "fdoc.u_id as fdocu_id,\n"
                        + "fdoc.u_time as fdocu_time,\n"
                        + "fdoc.documentdate AS fdocdocumnetdate,\n"
                        + "usr.name as usrname,\n"
                        + "usr.surname as usrsurname,\n"
                        + "usr1.name as usr1name,\n"
                        + "usr1.surname as usr1surname,\n"
                        + "bkam.id as bkamid,\n"
                        + "bkam.price as bkamprice,\n"
                        + "bkam.is_direction as bkamis_direction, \n"
                        + "bka.balance AS balance,\n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	CASE bam2.is_direction WHEN true THEN (bam2.price) ELSE -(bam2.price) END as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "                   LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bam2.financingdocument_id AND fdoc.deleted=False)\n"
                        + "    		WHERE bam2.bankaccount_id = bka.id AND bam2.branch_id = bkam.branch_id \n"
                        + "             AND bam2.deleted=false \n"
                        + whereType + "\n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY fdoc.id DESC NULLS LAST, bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ")+SUM(CASE WHEN bkam.is_direction=true THEN  bkam.price\n"
                        + "ELSE (-bkam.price) END \n"
                        + ") \n"
                        + "OVER(ORDER BY fdoc.id NULLS FIRST, bkam.movementdate,bkam.id \n"
                        + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS bkabalance,\n"
                        + "bkam.branch_id AS bkambranch_id,\n"
                        + "br.name AS brname,\n"
                        + "bka.currency_id AS bkacurrency_id,\n"
                        + "bac.id AS bacid\n"
                        + "FROM   \n"
                        + "finance.bankaccountmovement bkam \n"
                        + "INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False)  \n"
                        + "LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)   \n"
                        + "LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                        + "LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "LEFT JOIN general.branch br ON(br.id = bkam.branch_id AND br.deleted=FALSE)\n"
                        + "LEFT JOIN finance.bankaccountcommission bac ON((bac.financingdocument_id = fdoc.id OR bac.commissionfinancingdocument_id=fdoc.id) AND bac.deleted=FALSE)\n"
                        + "WHERE   \n"
                        + "bkam.bankaccount_id=? AND bkam.deleted = false AND bkam.branch_id IN ( " + branchList + " )\n"
                        + "AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' \n" //where
                        + where + "\n"
                        + whereType + "\n"
                        + "ORDER BY fdoc.id DESC NULLS LAST, bkam.movementdate DESC, bkam.id DESC\n"
                        + " limit " + pageSize + " offset " + first;
                params = new Object[]{sessionBean.getUser().getLanguage().getId(), bankAcount.getId()};
                break;
        }

        return getJdbcTemplate().query(sql, params, new BankAccountMovementMapper());
    }

    @Override
    public BankAccountMovement count(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId) {
        String sql;
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        Object[] params;
        String whereType = "";

        if (financingTypeId > 0) {
            whereType = " AND fdoc.type_id = " + financingTypeId + " \n";
        }

        switch (opType) {
            case 1:
                sql = "SELECT \n"
                        + "COUNT(bkam.id) AS bkamid,\n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	bam2.price as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "    		WHERE bam2.bankaccount_id = ?\n"
                        + "             AND bam2.deleted=false AND bam2.is_direction=true AND bam2.branch_id IN ( " + branchList + " ) \n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ") AS transferringbalance, \n"
                        + "(SELECT \n"
                        + "            COALESCE(SUM(bkam1.price),0)\n"
                        + "            FROM\n"
                        + "            finance.bankaccountmovement bkam1 \n"
                        + "            LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bkam1.financingdocument_id AND fdoc.deleted=False)\n"
                        + "            WHERE bkam1.is_direction=true AND\n"
                        + "            bkam1.bankaccount_id=? AND bkam1.deleted = false AND bkam1.branch_id IN ( " + branchList + " )\n"
                        + whereType + "\n"
                        + "            AND bkam1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                        + ") AS sumincoming,\n"
                        + "0 AS sumoutcoming\n"
                        + " FROM \n"
                        + "    finance.bankaccountmovement bkam \n"
                        + "    INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False) \n"
                        + "    LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)  \n"
                        + "    LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "    LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "    LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                        + "    LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "    LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "WHERE \n"
                        + "	bkam.bankaccount_id=? AND bkam.deleted = false and bkam.is_direction=true AND bkam.branch_id IN ( " + branchList + " ) \n"
                        + whereType + "\n"
                        + "AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'" + where; //where
                params = new Object[]{bankAcount.getId(), bankAcount.getId(), sessionBean.getUser().getLanguage().getId(), bankAcount.getId()};
                break;
            case 2:
                sql = "SELECT \n"
                        + " COUNT(bkam.id) AS bkamid,\n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	-bam2.price as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "    		WHERE bam2.bankaccount_id = ?\n"
                        + "             AND bam2.deleted=false AND bam2.is_direction=false AND bam2.branch_id IN ( " + branchList + " )\n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ") AS transferringbalance, \n"
                        + "0 AS sumincoming,\n"
                        + "(SELECT \n"
                        + "		COALESCE(SUM(bkam1.price),0)\n"
                        + "		FROM\n"
                        + "		finance.bankaccountmovement bkam1 \n"
                        + "             LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bkam1.financingdocument_id AND fdoc.deleted=False)\n"
                        + "             WHERE bkam1.is_direction=false AND\n"
                        + "             bkam1.bankaccount_id=? AND bkam1.deleted = false AND bkam1.branch_id IN ( " + branchList + " )\n"
                        + whereType + "\n"
                        + "             AND bkam1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                        + ") AS sumoutcoming\n"
                        + " FROM \n"
                        + "    finance.bankaccountmovement bkam \n"
                        + "    INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False) \n"
                        + "    LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)  \n"
                        + "    LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "    LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "    LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                        + "    LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "    LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "WHERE \n"
                        + "	bkam.bankaccount_id=? AND bkam.deleted = false and bkam.is_direction=false AND bkam.branch_id IN ( " + branchList + " )\n"
                        + whereType + "\n"
                        + "AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'" + where; //where
                params = new Object[]{bankAcount.getId(), bankAcount.getId(), sessionBean.getUser().getLanguage().getId(), bankAcount.getId()};
                break;
            default:
                sql = "SELECT \n"
                        + " COUNT(bkam.id) AS bkamid,\n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	CASE bam2.is_direction WHEN true THEN (bam2.price) ELSE -(bam2.price) END as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "    		WHERE bam2.bankaccount_id = ?\n"
                        + "             AND bam2.deleted=false AND bam2.branch_id IN ( " + branchList + " )\n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ") AS transferringbalance, \n"
                        + "(SELECT \n"
                        + "		COALESCE(SUM(bkam1.price),0)\n"
                        + "             FROM\n"
                        + "		finance.bankaccountmovement bkam1 \n"
                        + "             LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bkam1.financingdocument_id AND fdoc.deleted=False)\n"
                        + "             WHERE bkam1.is_direction=true AND\n"
                        + "             bkam1.bankaccount_id=? AND bkam1.deleted = false AND bkam1.branch_id IN ( " + branchList + " )\n"
                        + whereType + "\n"
                        + "             AND bkam1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                        + ") AS sumincoming,\n"
                        + "(SELECT \n"
                        + "             COALESCE(SUM(bkam2.price),0)\n"
                        + "		FROM\n"
                        + "		finance.bankaccountmovement bkam2 \n"
                        + "             LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bkam2.financingdocument_id AND fdoc.deleted=False)\n"
                        + "             WHERE bkam2.is_direction=false AND\n"
                        + "             bkam2.bankaccount_id=? AND bkam2.deleted = false AND bkam2.branch_id IN ( " + branchList + " )\n"
                        + whereType + "\n"
                        + "             AND bkam2.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                        + ") AS sumoutcoming\n"
                        + " FROM \n"
                        + "    finance.bankaccountmovement bkam \n"
                        + "    INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False) \n"
                        + "    LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)  \n"
                        + "    LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "    LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "    LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                        + "    LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "    LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "WHERE \n"
                        + "	bkam.bankaccount_id=? AND bkam.deleted = false AND bkam.branch_id IN ( " + branchList + " )\n"
                        + whereType + "\n"
                        + "AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'" + where; //where
                params = new Object[]{bankAcount.getId(), bankAcount.getId(), bankAcount.getId(), sessionBean.getUser().getLanguage().getId(), bankAcount.getId()};
                break;
        }

        List<BankAccountMovement> result = getJdbcTemplate().query(sql, params, new BankAccountMovementMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new BankAccountMovement();
        }
    }

    @Override
    public int count(String where, BankAccount bankAcount, Branch branch) {
        String sql;

        sql = "SELECT COUNT(bkam.id)"
                + " FROM \n"
                + "    finance.bankaccountmovement bkam \n"
                + "    INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False) \n"
                + "    LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)  \n"
                + "WHERE \n"
                + "	bkam.bankaccount_id=? AND bkam.deleted = false AND bkam.branch_id = ? " + where;

        Object[] params = new Object[]{bankAcount.getId(), branch.getId()};
        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return result;
    }

    @Override
    public String exportData(String where, BankAccount bankAcount, int opType, Date beginDate, Date endDate, String branchList, int financingTypeId) {
        String sql;
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String whereType = "";

        if (financingTypeId > 0) {
            whereType = " AND fdoc.type_id = " + financingTypeId + " \n";
        }

        switch (opType) {
            case 1:
                //sadece gelen
                sql = "SELECT\n"
                        + "fdoc.id as fdocid,\n"
                        + "fdoc.documentnumber as fdocdocumentnumber, \n"
                        + "fdoc.description as fdocdescription, \n"
                        + "fiem.incomeexpense_id as fiemincomeexpense_id, \n"
                        + "fie.name as fiename, \n"
                        + "fdoc.type_id as fdoctype_id,\n"
                        + "typd.name AS typdname,\n"
                        + "fdoc.c_id as fdocc_id,\n"
                        + "fdoc.c_time as fdocc_time,\n"
                        + "fdoc.u_id as fdocu_id,\n"
                        + "fdoc.u_time as fdocu_time,\n"
                        + "fdoc.documentdate AS fdocdocumnetdate,\n"
                        + "usr.name as usrname,\n"
                        + "usr.surname as usrsurname,\n"
                        + "usr1.name as usr1name,\n"
                        + "usr1.surname as usr1surname,\n"
                        + "bkam.id as bkamid,\n"
                        + "bkam.price as bkamprice,\n"
                        + "bkam.is_direction as bkamis_direction, \n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	bam2.price as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "                   LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bam2.financingdocument_id AND fdoc.deleted=False)\n"
                        + "    		WHERE bam2.bankaccount_id = bka.id\n"
                        + whereType + "\n"
                        + "             AND bam2.deleted=false AND bam2.is_direction=true AND bam2.branch_id = bkam.branch_id\n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY fdoc.id DESC NULLS LAST, bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ")+SUM(bkam.price) \n"
                        + "OVER(ORDER BY fdoc.id NULLS FIRST, bkam.movementdate,bkam.id \n"
                        + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS bkabalance,\n"
                        + "bkam.branch_id AS bkambranch_id,\n"
                        + "br.name AS brname,\n"
                        + "bka.currency_id AS bkacurrency_id\n"
                        + "FROM   \n"
                        + "finance.bankaccountmovement bkam \n"
                        + "INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False)  \n"
                        + "LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)   \n"
                        + "LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + " )\n"
                        + "LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "LEFT JOIN general.branch br ON(br.id = bkam.branch_id AND br.deleted=FALSE)\n"
                        + "WHERE   \n"
                        + "bkam.bankaccount_id=" + bankAcount.getId() + " AND bkam.deleted = false AND bkam.is_direction=true AND bkam.branch_id IN ( " + branchList + " )\n"
                        + " AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' \n" //where 
                        + where + "\n"
                        + whereType + "\n"
                        + "ORDER BY fdoc.id DESC NULLS LAST, bkam.movementdate DESC, bkam.id DESC";
                break;
            //sadece giden
            case 2:
                sql = "SELECT\n"
                        + "fdoc.id as fdocid,\n"
                        + "fdoc.documentnumber as fdocdocumentnumber, \n"
                        + "fdoc.description as fdocdescription, \n"
                        + "fiem.incomeexpense_id as fiemincomeexpense_id, \n"
                        + "fie.name as fiename, \n"
                        + "fdoc.type_id as fdoctype_id,\n"
                        + "typd.name AS typdname,\n"
                        + "fdoc.c_id as fdocc_id,\n"
                        + "fdoc.c_time as fdocc_time,\n"
                        + "fdoc.u_id as fdocu_id,\n"
                        + "fdoc.u_time as fdocu_time,\n"
                        + "fdoc.documentdate AS fdocdocumnetdate,\n"
                        + "usr.name as usrname,\n"
                        + "usr.surname as usrsurname,\n"
                        + "usr1.name as usr1name,\n"
                        + "usr1.surname as usr1surname,\n"
                        + "bkam.id as bkamid,\n"
                        + "bkam.price as bkamprice,\n"
                        + "bkam.is_direction as bkamis_direction, \n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	(-bam2.price) as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "                   LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bam2.financingdocument_id AND fdoc.deleted=False)\n"
                        + "    		WHERE bam2.bankaccount_id = bka.id AND bam2.branch_id = bkam.branch_id\n"
                        + whereType + "\n"
                        + "             AND bam2.deleted=false AND bam2.is_direction=false \n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY fdoc.id DESC NULLS LAST, bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ")+SUM((-bkam.price)) \n"
                        + "OVER(ORDER BY fdoc.id NULLS FIRST, bkam.movementdate,bkam.id \n"
                        + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS bkabalance,\n"
                        + "bkam.branch_id AS bkambranch_id,\n"
                        + "br.name AS brname,\n"
                        + "bka.currency_id AS bkacurrency_id\n"
                        + "FROM   \n"
                        + "finance.bankaccountmovement bkam \n"
                        + "INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False)  \n"
                        + "LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)   \n"
                        + "LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + ")\n"
                        + "LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "LEFT JOIN general.branch br ON(br.id = bkam.branch_id AND br.deleted=FALSE)\n"
                        + "WHERE   \n"
                        + "bkam.bankaccount_id=" + bankAcount.getId() + " AND bkam.deleted = false AND bkam.is_direction=false AND bkam.branch_id IN ( " + branchList + " )\n"
                        + "AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' \n" //where
                        + where + "\n"
                        + whereType + "\n"
                        + "ORDER BY fdoc.id DESC NULLS LAST, bkam.movementdate DESC, bkam.id DESC";
                break;
            default:
                //hepsi
                sql = "SELECT\n"
                        + "fdoc.id as fdocid,\n"
                        + "fdoc.documentnumber as fdocdocumentnumber, \n"
                        + "fdoc.description as fdocdescription, \n"
                        + "fiem.incomeexpense_id as fiemincomeexpense_id, \n"
                        + "fie.name as fiename, \n"
                        + "fdoc.type_id as fdoctype_id,\n"
                        + "typd.name AS typdname,\n"
                        + "fdoc.c_id as fdocc_id,\n"
                        + "fdoc.c_time as fdocc_time,\n"
                        + "fdoc.u_id as fdocu_id,\n"
                        + "fdoc.u_time as fdocu_time,\n"
                        + "fdoc.documentdate AS fdocdocumnetdate,\n"
                        + "usr.name as usrname,\n"
                        + "usr.surname as usrsurname,\n"
                        + "usr1.name as usr1name,\n"
                        + "usr1.surname as usr1surname,\n"
                        + "bkam.id as bkamid,\n"
                        + "bkam.price as bkamprice,\n"
                        + "bkam.is_direction as bkamis_direction, \n"
                        + "(\n"
                        + "    	SELECT \n"
                        + "    		COALESCE(SUM (S.price), 0) \n"
                        + "    	FROM ( \n"
                        + "    		SELECT \n"
                        + "                	CASE bam2.is_direction WHEN true THEN (bam2.price) ELSE -(bam2.price) END as price \n"
                        + "    		FROM  finance.bankaccountmovement AS bam2 \n"
                        + "                   LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = bam2.financingdocument_id AND fdoc.deleted=False)\n"
                        + "    		WHERE bam2.bankaccount_id = bka.id AND bam2.branch_id = bkam.branch_id\n"
                        + whereType + "\n"
                        + "             AND bam2.deleted=false \n"
                        + "             AND bam2.movementdate < '" + format.format(beginDate) + "' \n"
                        + "    		ORDER BY fdoc.id DESC NULLS LAST, bam2.movementdate DESC, bam2.id DESC\n"
                        + "        ) S \n"
                        + ")+SUM(CASE WHEN bkam.is_direction=true THEN  bkam.price\n"
                        + "ELSE (-bkam.price) END \n"
                        + ") \n"
                        + "OVER(ORDER BY fdoc.id NULLS FIRST, bkam.movementdate,bkam.id \n"
                        + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS bkabalance,"
                        + "bkam.branch_id AS bkambranch_id,\n"
                        + "br.name AS brname,\n"
                        + "bka.currency_id AS bkacurrency_id\n"
                        + "FROM   \n"
                        + "finance.bankaccountmovement bkam \n"
                        + "INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False)  \n"
                        + "LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)   \n"
                        + "LEFT JOIN finance.incomeexpensemovement fiem ON(fiem.financingdocument_id = fdoc.id AND fiem.deleted=False)\n "
                        + "LEFT JOIN finance.incomeexpense fie ON(fie.id = fiem.incomeexpense_id)\n "
                        + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + ")\n"
                        + "LEFT JOIN general.userdata usr ON (usr.id=fdoc.c_id)\n"
                        + "LEFT JOIN general.userdata usr1 ON (usr1.id=fdoc.u_id)\n"
                        + "LEFT JOIN general.branch br ON(br.id = bkam.branch_id AND br.deleted=FALSE)\n"
                        + "WHERE   \n"
                        + "bkam.bankaccount_id=" + bankAcount.getId() + " AND bkam.deleted = false AND bkam.branch_id IN ( " + branchList + " )\n"
                        + "AND bkam.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' \n" //where
                        + where + "\n"
                        + whereType + "\n"
                        + "ORDER BY fdoc.id DESC NULLS LAST, bkam.movementdate DESC, bkam.id DESC";
                break;
        }

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public int controlMovement(String where, BankAccount bankAcount, Branch branch) {

        if (branch.getId() != 0 && !branch.isIsCentral()) {
            where = where + " AND bkam.branch_id = " + branch.getId() + "\n";
        }

        String sql = "SELECT COUNT(bkam.id)"
                + " FROM \n"
                + "    finance.bankaccountmovement bkam \n"
                + "    INNER JOIN finance.bankaccount bka ON(bka.id=bkam.bankaccount_id AND bka.deleted = False) \n"
                + "    LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False)  \n"
                + "WHERE \n"
                + "	bkam.bankaccount_id=? AND bkam.deleted = false  " + where;

        Object[] params = new Object[]{bankAcount.getId()};
        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
        return result;
    }

}
