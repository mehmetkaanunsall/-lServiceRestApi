/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 01:35:06
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountDao extends JdbcDaoSupport implements IAccountDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Account> findAll(String where) {
        if (!sessionBean.getUser().getLastBranchSetting().isIsShowPassiveAccount()) {
            where = where + " AND acc.status_id <> 6 ";
        }

        String sql = "SELECT \n"
                  + "               acc.id AS accid,\n"
                  + "               acc.is_person AS accis_person,\n"
                  + "               acc.name as accname,\n"
                  + "               acc.title AS acctitle,\n"
                  + "               acc.code AS acccode,\n"
                  + "               acc.taxno AS acctaxno,\n"
                  + "               acc.taxoffice AS acctaxoffice,\n"
                  + "               acc.status_id AS accstatus_id, \n"
                  + "               COALESCE(acc.maxexpirycount,1) AS accmaxexpriycount,\n"
                  + "               acc.taxpayertype_id AS acctaxpayertype_id,\n"
                  + "               acc.taginfo AS acctaginfo,\n"
                  + "               acc.description AS accdescription,\n"
                  + "               sttd.name AS sttdname,\n"
                  + "               acc.type_id AS acctype_id,\n"
                  + "               typd.name AS typdname,\n"
                  + "               abc.balance AS accbalance,\n"
                  + "               acc.creditlimit AS acccreditlimit,\n"
                  + "               acc.phone AS accphone,\n"
                  + "               acc.email AS accemail,\n"
                  + "               acc.address AS accaddress,\n"
                  + "               acc.city_id AS acccity_id,\n"
                  + "               ctyd.name AS ctydname,\n"
                  + "               acc.country_id AS acccountry_id,\n"
                  + "               ctrd.name AS ctrdname,\n"
                  + "               acc.county_id as acccounty_id,\n"
                  + "               cnty.name as cntyname,\n"
                  + "               usd.name AS usdname,\n"
                  + "               usd.surname AS  usdsurname,"
                  + "                usd.username AS  usdusername,\n"
                  + "		  acc.c_time AS accc_time,\n"
                  + "               acc.c_id AS accc_id ,\n"
                  + "               acc.is_employee AS accisemployee, \n"
                  + "               acc.centeraccount_id AS acccenteraccount_id,\n"
                  + "               acc.dueday AS accdueday,\n"
                  + "               acc.paymenttype_id AS accpaymenttye_id\n"
                  + "FROM general.account acc  \n"
                  + "INNER JOIN general.userdata usd ON(usd.id =  acc.c_id) \n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = acc.status_id AND sttd.language_id = ?) \n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = acc.type_id AND typd.language_id = ?)\n"
                  + "LEFT JOIN system.city_dict ctyd ON (ctyd.city_id=acc.city_id AND ctyd.language_id= ?)\n"
                  + "LEFT JOIN system.country_dict ctrd ON(ctrd.country_id=acc.country_id AND ctrd.language_id= ?)\n"
                  + "LEFT JOIN system.county cnty ON(cnty.id=acc.county_id) \n"
                  + "INNER JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.branch_id=? AND abc.deleted=FALSE)\n"
                  + "WHERE acc.deleted=false\n"
                  + "AND\n"
                  + "(CASE WHEN COALESCE(acc.paymenttype_id,0) <> 106\n"
                  + "THEN \n"
                  + "	TRUE \n"
                  + "WHEN EXISTS \n"
                  + "(SELECT \n"
                  + "	accm.id \n"
                  + "FROM \n"
                  + "	general.accountmovement accm \n"
                  + "WHERE \n"
                  + "	accm.deleted = FALSE \n"
                  + "    AND accm.account_id = acc.id \n"
                  + "    AND accm.branch_id =? \n"
                  + "    AND (accm.financingdocument_id IS NOT NULL OR accm.invoice_id IS NOT NULL OR accm.receipt_id IS NOT NULL OR accm.chequebill_id IS NOT NULL)\n"
                  + ") THEN\n"
                  + "	TRUE\n"
                  + "ELSE\n"
                  + "	FALSE\n"
                  + "END\n"
                  + ")\n"
                  + where
                  + " ORDER BY acc.name\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<Account> result = getJdbcTemplate().query(sql, param, new AccountMapper());
        return result;
    }

    @Override
    public List<Account> findAllAccount(int typeId) {
        String whereType = "";
        if (typeId == 35) {
            whereType = " 3, 5 ";
        } else if (typeId == 3) {
            whereType = " 3 ";
        } else if (typeId == 4) {
            whereType = " 4 ";
        } else if (typeId == 5) {
            whereType = " 5 ";
        }
        String sql = "SELECT \n"
                  + "               acc.id AS accid,\n"
                  + "               acc.is_person AS accis_person,\n"
                  + "               acc.name as accname,\n"
                  + "               acc.title AS acctitle,\n"
                  + "               acc.code AS acccode,\n"
                  + "               acc.taxno AS acctaxno,\n"
                  + "               acc.taxoffice AS acctaxoffice,\n"
                  + "               acc.status_id AS accstatus_id,\n"
                  + "               acc.taxpayertype_id AS acctaxpayertype_id,\n"
                  + "               acc.taginfo AS acctaginfo,\n"
                  + "               sttd.name AS sttdname,\n"
                  + "               acc.type_id AS acctype_id,\n"
                  + "               typd.name AS typdname,\n"
                  + "               abc.balance AS accbalance,\n"
                  + "               acc.creditlimit AS acccreditlimit,\n"
                  + "               acc.phone AS accphone,\n"
                  + "               acc.email AS accemail,\n"
                  + "               acc.address AS accaddress,\n"
                  + "               acc.city_id AS acccity_id,\n"
                  + "               ctyd.name AS ctydname,\n"
                  + "               acc.country_id AS acccountry_id,\n"
                  + "               ctrd.name AS ctrdname,\n"
                  + "               acc.county_id as acccounty_id,\n"
                  + "               cnty.name as cntyname,\n"
                  + "               usd.name AS usdname,\n"
                  + "               usd.surname AS  usdsurname,"
                  + "                 usd.username AS  usdusername,\n"
                  + "		  acc.c_time AS accc_time,\n"
                  + "               acc.c_id AS accc_id ,	\n"
                  + "                acc.is_employee AS accisemployee,\n"
                  + "               1 AS tagquantity\n"
                  + "FROM general.account acc  \n"
                  + "INNER JOIN general.userdata usd ON(usd.id =  acc.c_id) \n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = acc.status_id AND sttd.language_id = ?) \n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = acc.type_id AND typd.language_id = ?)\n"
                  + "LEFT JOIN system.city_dict ctyd ON (ctyd.city_id=acc.city_id AND ctyd.language_id= ?)\n"
                  + "LEFT JOIN system.country_dict ctrd ON(ctrd.country_id=acc.country_id AND ctrd.language_id= ?)\n"
                  + "LEFT JOIN system.county cnty ON(cnty.id=acc.county_id) \n"
                  + "INNER JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.branch_id=? AND abc.deleted=FALSE)\n"
                  + "WHERE acc.deleted=false  AND typd.type_id IN (" + whereType + ") \n"
                  + "AND \n"
                  + "(CASE WHEN COALESCE(acc.paymenttype_id,0) <> 106\n"
                  + "THEN \n"
                  + "	TRUE \n"
                  + "WHEN EXISTS \n"
                  + "(SELECT \n"
                  + "	accm.id \n"
                  + "FROM \n"
                  + "	general.accountmovement accm \n"
                  + "WHERE \n"
                  + "	accm.deleted = FALSE \n"
                  + "    AND accm.account_id = acc.id \n"
                  + "    AND accm.branch_id =? \n"
                  + "    AND (accm.financingdocument_id IS NOT NULL OR accm.invoice_id IS NOT NULL OR accm.receipt_id IS NOT NULL OR accm.chequebill_id IS NOT NULL)\n"
                  + ") THEN\n"
                  + "	TRUE\n"
                  + "ELSE\n"
                  + "	FALSE\n"
                  + "END\n"
                  + ")  \n"
                  + "ORDER BY acc.name\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<Account> result = getJdbcTemplate().query(sql, param, new AccountMapper());
        return result;
    }

    @Override
    public int create(Account obj) {
        int processType = 0;

        String sql = "SELECT r_account_id FROM general.process_account (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] param = new Object[]{processType, obj.getId(), obj.getIsPerson(), obj.getOnlyAccountName(), obj.getTitle(), obj.getCode(),
            obj.getTaxNo(), obj.getTaxOffice(), obj.getStatus().getId(), obj.getType().getId(), obj.getCreditlimit() == null ? 0 : obj.getCreditlimit(),
            obj.getTransferBalance() == null ? 0 : obj.getTransferBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? obj.getTransferBalance() : obj.getTransferBalance().multiply(BigDecimal.valueOf(-1)),
            obj.getTransferBalance() == null ? false : obj.getTransferBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? true : false, sessionBean.getUser().getLastBranch().getCurrency().getId(),
            obj.getPhone(), obj.getEmail(), obj.getAddress(),
            obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.isIsEmployee(), obj.getMaxExpiryCount() == 0 ? 1 : obj.getMaxExpiryCount(),
            null, null, obj.getTaxpayertype_id(), sessionBean.getUser().getLastBranch().getId(), null, sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getTagInfo(), obj.getDescription(),
            obj.getDueDay(), null
        };

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Account obj) {
        int processType = 1;
        String sql = " SELECT r_account_id FROM general.process_account (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{processType, obj.getId(), obj.getIsPerson(), obj.getOnlyAccountName(), obj.getTitle(), obj.getCode(),
            obj.getTaxNo(), obj.getTaxOffice(), obj.getStatus().getId(), obj.getType().getId(), obj.getCreditlimit() == null ? 0 : obj.getCreditlimit(),
            null, obj.getTransferBalance() == null ? false : obj.getTransferBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? true : false,
            sessionBean.getUser().getLastBranch().getCurrency().getId(),
            obj.getPhone(), obj.getEmail(), obj.getAddress(),
            obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.isIsEmployee(), obj.getMaxExpiryCount() == 0 ? 1 : obj.getMaxExpiryCount(),
            null, null, obj.getTaxpayertype_id(), sessionBean.getUser().getLastBranch().getId(), null, sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getTagInfo(), obj.getDescription(),
            obj.getDueDay(), null};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public List<Account> accountBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        Branch branch = new Branch();
        if (type.equals("Musteri,MTedatikçi") || type.equals("Tedarikçi,MTedatikçi") || type.equals("chequebill")
                  || type.equals("invoice") || type.equals("order") || type.equals("waybill") || type.equals("customeragreement") || type.equals("eInvoiceCheckBox")) {
            branch.setId(((Branch) param.get(0)).getId());
        } else {
            branch.setId(sessionBean.getUser().getLastBranch().getId());
        }

        //Büütn kitaplara şube yetkisi konrolü
        if (!type.equals("supplierCheckboxReport") && !type.equals("accountextractreportcheckbox") && !type.equals("employeeextractreportcheckbox") && !type.equals("report") && !type.equals("invoiceCheckBox") && !type.equals("orderCheckBox")) {
            where = where + " AND acc.id IN (Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id = " + branch.getId() + " AND abc.deleted=False) ";
        }

        if (type.equals("Musteri")) {
            where = where + "  AND acc.type_id = 4 AND acc.is_employee = FALSE ";
        } else if (type.equals("Tedarikci")) {
            where = where + "  AND acc.type_id = 3 AND acc.is_employee = FALSE ";
        } else if (type.equals("MusteriTedarikci")) {
            where = where + "  AND acc.type_id = 5 AND acc.is_employee = FALSE ";
        } else if (type.equals("Musteri,MTedatikçi")) {//finansman belgeleri para geliyor ise
            where = where + " AND acc.type_id IN (4,5) ";
        } else if (type.equals("Tedarikçi,MTedatikçi")) {//finansman belgeleri para çıkıyor ise
            where = where + "  AND acc.type_id IN (3,5) ";
        } else if (type.equals("Personel")) {
            where = where + " AND acc.is_employee=true ";
        } else if (type.equals("accountCategoryCheckBox")) {
            where = where + " AND acc.is_employee=False ";
        } else if (type.equals("invoiceCheckBox") || type.equals("orderCheckBox")) {
            where = where + " AND acc.is_employee=False ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }
        } else if (type.equals("personelCategoryCheckBox")) {
            where = where + " AND acc.is_employee=True ";
        } else if (type.equals("stockprocess")) {
            where = where + "  AND acc.type_id IN(3,5) AND acc.is_employee = FALSE ";

        } else if (type.equals("supplierCheckboxReport")) {
            where = where + "  AND acc.type_id IN(3,5) AND acc.is_employee = FALSE ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }

        } else if (type.equals("accountextractreportcheckbox")) {
            where = where + " AND acc.is_employee=False ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<Branch>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<Branch>) param.get(0)).get(i).getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }
        } else if (type.equals("employeeextractreportcheckbox")) {
            where = where + " AND acc.is_employee=True ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<Branch>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<Branch>) param.get(0)).get(i).getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }
        } else if (type.equals("fuelshiftattendant")) {
            where = where + " AND acc.is_employee = TRUE AND acc.id IN (SELECT empi.account_id FROM general.employeeinfo empi WHERE empi.deleted= FALSE AND empi.integrationcode IS NOT NULL AND empi.branch_id = " + branch.getId() + ")\n";
        } else if (type.equals("invoice")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("order")) {
            where = where + " AND acc.type_id = 3 AND acc.is_employee = FALSE ";
        } else if (type.equals("waybill")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("chequebill") || type.equals("active")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("accountCollectionPayment")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("report")) {
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
                where = where + "  AND acc.is_employee = FALSE ";
            }
        } else {
            where = where + "  AND acc.is_employee = FALSE ";
        }

        if (!sessionBean.getUser().getLastBranchSetting().isIsProcessPassiveAccount()) {
            if (type.equals("fuelshiftattendant") || type.equals("shareCreditSales") || type.equals("stockprocess") || type.equals("Personel")
                      || type.equals("accountCategoryCheckBox") || type.equals("personelCategoryCheckBox")
                      || type.equals("Musteri,MTedatikçi") || type.equals("Tedarikçi,MTedatikçi") || type.equals("invoice") || type.equals("order") || type.equals("waybill")
                      || type.equals("chequebill") || type.equals("active") || type.equals("eInvoiceCheckBox") || type.equals("accountCollectionPayment")) {
                where = where + " AND acc.status_id <> 6 ";
            }
        }

        String sql = "SELECT \n"
                  + "               acc.id AS accid,\n"
                  + "               acc.is_person AS accis_person,\n"
                  + "               acc.name as accname,\n"
                  + "               acc.title AS acctitle,\n"
                  + "               acc.code AS acccode,\n"
                  + "               acc.taxno AS acctaxno,\n"
                  + "               acc.taxoffice AS acctaxoffice,\n"
                  + "               acc.status_id AS accstatus_id,\n"
                  + "               sttd.name AS sttdname,\n"
                  + "               acc.type_id AS acctype_id,\n"
                  + "               typd.name AS typdname,\n"
                  + "               acc.balance AS accbalance,\n"
                  + "               acc.creditlimit AS acccreditlimit,\n"
                  + "               acc.phone AS accphone,\n"
                  + "               acc.email AS accemail,\n"
                  + "               acc.address AS accaddress,\n"
                  + "               acc.city_id AS acccity_id,\n"
                  + "               ctyd.name AS ctydname,\n"
                  + "               acc.country_id AS acccountry_id,\n"
                  + "               ctrd.name AS ctrdname,\n"
                  + "               acc.county_id as acccounty_id,\n"
                  + "               cnty.name as cntyname ,\n"
                  + "               acc.is_employee AS accisemployee,\n"
                  + "               acc.taxpayertype_id AS acctaxpayertype_id, \n"
                  + "               acc.dueday AS accdueday\n"
                  + "FROM general.account acc\n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = acc.status_id AND sttd.language_id = ?) \n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = acc.type_id AND typd.language_id = ?)\n"
                  + "LEFT JOIN system.city_dict ctyd ON (ctyd.city_id=acc.city_id AND ctyd.language_id= ?)\n"
                  + "LEFT JOIN system.country_dict ctrd ON(ctrd.country_id=acc.country_id AND ctrd.language_id= ?)\n"
                  + "LEFT JOIN system.county cnty ON(cnty.id=acc.county_id) \n"
                  + "WHERE acc.deleted=false "
                  + "AND \n"
                  + "(CASE WHEN COALESCE(acc.paymenttype_id,0) <> 106\n"
                  + "THEN \n"
                  + "	TRUE \n"
                  + "WHEN EXISTS \n"
                  + "(SELECT \n"
                  + "	accm.id \n"
                  + "FROM \n"
                  + "	general.accountmovement accm \n"
                  + "WHERE \n"
                  + "	accm.deleted = FALSE \n"
                  + "    AND accm.account_id = acc.id \n"
                  + "    AND accm.branch_id = ? \n"
                  + "    AND (accm.financingdocument_id IS NOT NULL OR accm.invoice_id IS NOT NULL OR accm.receipt_id IS NOT NULL OR accm.chequebill_id IS NOT NULL)\n"
                  + ") THEN\n"
                  + "	TRUE\n"
                  + "ELSE\n"
                  + "	FALSE\n"
                  + "END\n"
                  + ") \n"
                  + where + "\n"
                  + "ORDER BY acc.name \n"
                  + " limit " + pageSize + " offset " + first;
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<Account> result = getJdbcTemplate().query(sql, params, new AccountMapper());
        return result;
    }

    @Override
    public int accountBookCount(String where, String type, List<Object> param) {

        Branch branch = new Branch();
        if (type.equals("Musteri,MTedatikçi") || type.equals("Tedarikçi,MTedatikçi") || type.equals("chequebill")
                  || type.equals("invoice") || type.equals("order") || type.equals("waybill") || type.equals("customeragreement") || type.equals("eInvoiceCheckBox")) {
            branch.setId(((Branch) param.get(0)).getId());
        } else {
            branch.setId(sessionBean.getUser().getLastBranch().getId());
        }

        //Büütn kitaplara şube yetkisi konrolü
        if (!type.equals("supplierCheckboxReport") && !type.equals("accountextractreportcheckbox") && !type.equals("employeeextractreportcheckbox") && !type.equals("report") && !type.equals("invoiceCheckBox") && !type.equals("orderCheckBox")) {
            where = where + " AND acc.id IN (Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id = " + branch.getId() + " AND abc.deleted=False) ";
        }

        if (type.equals("Musteri")) {
            where = where + "  AND acc.type_id = 4 AND acc.is_employee = FALSE ";
        } else if (type.equals("Tedarikci")) {
            where = where + "  AND acc.type_id = 3 AND acc.is_employee = FALSE ";
        } else if (type.equals("MusteriTedarikci")) {
            where = where + "  AND acc.type_id = 5 AND acc.is_employee = FALSE ";
        } else if (type.equals("Musteri,MTedatikçi")) {//finansman belgeleri para geliyor ise
            where = where + " AND acc.type_id IN (4,5) ";
        } else if (type.equals("Tedarikçi,MTedatikçi")) {//finansman belgeleri para çıkıyor ise
            where = where + " AND acc.type_id IN (3,5) ";
        } else if (type.equals("Personel")) {
            where = where + " AND acc.is_employee=true ";
        } else if (type.equals("accountCategoryCheckBox")) {
            where = where + " AND acc.is_employee=False ";
        } else if (type.equals("invoiceCheckBox") || type.equals("orderCheckBox")) {
            where = where + " AND acc.is_employee=False ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }
        } else if (type.equals("personelCategoryCheckBox")) {
            where = where + " AND acc.is_employee=True ";
        } else if (type.equals("stockprocess")) {
            where = where + "  AND acc.type_id IN(3,5) AND acc.is_employee = FALSE ";
        } else if (type.equals("supplierCheckboxReport")) {
            where = where + "  AND acc.type_id IN(3,5) AND acc.is_employee = FALSE ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }
        } else if (type.equals("accountextractreportcheckbox")) {
            where = where + " AND acc.is_employee=False ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<Branch>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<Branch>) param.get(0)).get(i).getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }
        } else if (type.equals("employeeextractreportcheckbox")) {
            where = where + " AND acc.is_employee=True ";
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<Branch>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<Branch>) param.get(0)).get(i).getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
            }
        } else if (type.equals("fuelshiftattendant")) {
            where = where + " AND acc.is_employee = TRUE AND acc.id IN (SELECT empi.account_id FROM general.employeeinfo empi WHERE empi.deleted= FALSE AND empi.integrationcode IS NOT NULL AND empi.branch_id = " + branch.getId() + ")\n";

        } else if (type.equals("invoice")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("order")) {
            where = where + " AND acc.type_id = 3 AND acc.is_employee = FALSE ";
        } else if (type.equals("waybill")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("chequebill") || type.equals("active")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("accountCollectionPayment")) {
            where = where + " AND acc.is_employee = FALSE ";
        } else if (type.equals("report")) {
            if (param.size() > 0) {
                String branchID = "";
                for (int i = 0; i < ((List<BranchSetting>) param.get(0)).size(); i++) {
                    branchID = branchID + " , " + ((List<BranchSetting>) param.get(0)).get(i).getBranch().getId();
                }
                branchID = branchID.substring(3, branchID.length());
                where = where + " AND acc.id IN ( Select abc.account_id FROM general.account_branch_con abc WHERE abc.branch_id IN (" + branchID + ") AND abc.deleted=False) ";
                where = where + "  AND acc.is_employee = FALSE ";
            }
        } else {
            where = where + "  AND acc.is_employee = FALSE ";
        }

        if (!sessionBean.getUser().getLastBranchSetting().isIsProcessPassiveAccount()) {
            if (type.equals("fuelshiftattendant") || type.equals("shareCreditSales") || type.equals("stockprocess") || type.equals("Personel")
                      || type.equals("accountCategoryCheckBox") || type.equals("personelCategoryCheckBox")
                      || type.equals("Musteri,MTedatikçi") || type.equals("Tedarikçi,MTedatikçi") || type.equals("invoice") || type.equals("order") || type.equals("waybill")
                      || type.equals("chequebill") || type.equals("active") || type.equals("eInvoiceCheckBox") || type.equals("accountCollectionPayment")) {
                where = where + " AND acc.status_id <> 6 ";
            }
        }

        String sql = "SELECT \n"
                  + "	COUNT(acc.id) AS accid \n"
                  + "FROM  general.account acc  \n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = acc.status_id AND sttd.language_id = ?) \n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = acc.type_id AND typd.language_id = ?)\n"
                  + "LEFT JOIN system.city_dict ctyd ON (ctyd.city_id=acc.city_id AND ctyd.language_id= ?)\n"
                  + "LEFT JOIN system.country_dict ctrd ON(ctrd.country_id=acc.country_id AND ctrd.language_id= ?)\n"
                  + "LEFT JOIN system.county cnty ON(cnty.id=acc.county_id) \n"
                  + "WHERE acc.deleted=false\n"
                  + "AND \n"
                  + "(CASE WHEN COALESCE(acc.paymenttype_id,0) <> 106\n"
                  + "THEN \n"
                  + "	TRUE \n"
                  + "WHEN EXISTS \n"
                  + "(SELECT \n"
                  + "	accm.id \n"
                  + "FROM \n"
                  + "	general.accountmovement accm \n"
                  + "WHERE \n"
                  + "	accm.deleted = FALSE \n"
                  + "    AND accm.account_id = acc.id \n"
                  + "    AND accm.branch_id =? \n"
                  + "    AND (accm.financingdocument_id IS NOT NULL OR accm.invoice_id IS NOT NULL OR accm.receipt_id IS NOT NULL OR accm.chequebill_id IS NOT NULL)\n"
                  + ") THEN\n"
                  + "	TRUE\n"
                  + "ELSE\n"
                  + "   FALSE\n"
                  + "END\n"
                  + ")\n"
                  + where + "\n";

        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, params, Integer.class
        );
        return id;
    }

    @Override
    public int testBeforeDelete(Account account) {
        String sql = "";
        Object[] param = null;

        if (sessionBean.getUser().getLastBranch().isIsCentral() || account.isIsEmployee()) {

            sql = "SELECT CASE WHEN (SELECT \n"
                      + "    				  COUNT(account_id) \n"
                      + "    			    FROM \n"
                      + "                	general.accountmovement \n"
                      + "                       WHERE \n"
                      + "                        account_id=? AND deleted=False AND\n"
                      + "                    	( financingdocument_id IS NOT NULL \n"
                      + "                    		OR  invoice_id IS NOT NULL  \n"
                      + "                    		OR receipt_id IS NOT NULL \n"
                      + "                    		OR chequebill_id IS NOT NULL)) > 0  THEN 1 \n"
                      + "WHEN (SELECT COUNT(account_id) FROM general.shiftpayment WHERE account_id=? AND deleted=False) > 0 THEN 2 ELSE 0 END ";

            param = new Object[]{account.getId(), account.getId()};

        } else {
            sql = "SELECT CASE WHEN (SELECT \n"
                      + "    				  COUNT(account_id) \n"
                      + "    			    FROM \n"
                      + "                	general.accountmovement \n"
                      + "                       WHERE \n"
                      + "                        account_id=? AND deleted=False AND branch_id=? AND\n"
                      + "                    	( financingdocument_id IS NOT NULL \n"
                      + "                    		OR  invoice_id IS NOT NULL  \n"
                      + "                    		OR receipt_id IS NOT NULL \n"
                      + "                    		OR chequebill_id IS NOT NULL)) > 0  THEN 1 \n"
                      + "WHEN (SELECT COUNT(account_id) FROM general.shiftpayment WHERE account_id=? AND deleted=False) > 0 THEN 2 ELSE 0 END ";

            param = new Object[]{account.getId(), sessionBean.getUser().getLastBranch().getId(), account.getId()};
        }
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Account obj
    ) {
        String sql = "";
        Object[] param = null;
        int processType = 2;
        if (sessionBean.getUser().getLastBranch().isIsCentral() || obj.isIsEmployee()) {

            sql = " SELECT r_account_id FROM general.process_account (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?, ?, ?, ?, ?, ?, ?, ?);";

            param = new Object[]{processType, obj.getId(), obj.getIsPerson(), obj.getOnlyAccountName(), obj.getTitle(), obj.getCode(),
                obj.getTaxNo(), obj.getTaxOffice(), obj.getStatus().getId(), obj.getType().getId(), obj.getCreditlimit() == null ? 0 : obj.getCreditlimit(),
                null, obj.getTransferBalance() == null ? false : obj.getTransferBalance().compareTo(BigDecimal.valueOf(0)) == 1 ? true : false,
                sessionBean.getUser().getLastBranch().getCurrency().getId(),
                obj.getPhone(), obj.getEmail(), obj.getAddress(),
                obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(),
                obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.isIsEmployee(), obj.getMaxExpiryCount() == 0 ? 1 : obj.getMaxExpiryCount(),
                null, null, obj.getTaxpayertype_id(), sessionBean.getUser().getLastBranch().getId(), null, sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.getTagInfo(), obj.getDescription(),
                obj.getDueDay(), null};
            try {
                return getJdbcTemplate().queryForObject(sql, param, Integer.class);
            } catch (DataAccessException e) {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }
        } else {
            sql = "UPDATE general.account_branch_con SET deleted = TRUE, u_id	= ?, d_time= NOW()  WHERE deleted = FALSE  AND account_id = ? AND branch_id=?;"
                      + " UPDATE general.accountmovement  SET deleted= TRUE, u_id= ?, d_time= NOW()  WHERE deleted = FALSE  AND account_id = ? AND branch_id=?;"
                      + "UPDATE general.accountinfo SET deleted= TRUE, u_id= ?, d_time= NOW()  WHERE deleted = FALSE  AND account_id = ? AND branch_id=? ;";
            param = new Object[]{sessionBean.getUser().getId(), obj.getId(), sessionBean.getUser().getLastBranch().getId(),
                sessionBean.getUser().getId(), obj.getId(), sessionBean.getUser().getLastBranch().getId(),
                sessionBean.getUser().getId(), obj.getId(), sessionBean.getUser().getLastBranch().getId()};
            try {
                getJdbcTemplate().update(sql, param);
                return 1;
            } catch (DataAccessException e) {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }
        }
    }

    @Override
    public List<Account> findAllAccountToIntegrationCode() {
        String sql = "SELECT \n"
                  + "      acc.id AS accid,\n"
                  + "      acc.name as accname,\n"
                  + "      acc.title AS acctitle\n"
                  + "FROM general.account acc  \n"
                  + "INNER JOIN general.employeeinfo empi ON(empi.account_id=acc.id AND empi.deleted=False)\n"
                  + "WHERE acc.deleted=false\n"
                  + "      AND acc.is_employee = TRUE\n"
                  + "      AND empi.integrationcode IS NOT NULL\n"
                  + "      AND empi.branch_id =?\n"
                  + "ORDER BY acc.name\n";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<Account> result = getJdbcTemplate().query(sql, param, new AccountMapper());
        return result;
    }

    @Override
    public List<Account> findSupplier() {
        String sql = "SELECT \n"
                  + "		acc.id AS accid,\n"
                  + "        acc.name as accname\n"
                  + "       FROM general.account acc\n"
                  + "       WHERE acc.deleted=false  AND acc.type_id IN(3,5) AND acc.is_employee = FALSE \n"
                  + "       ORDER BY acc.id";

        Object[] param = new Object[]{};
        List<Account> result = getJdbcTemplate().query(sql, param, new AccountMapper());
        return result;
    }

    @Override
    public String saveAccount(String json
    ) {
        String sql = "SELECT r_message FROM general.excel_account(?, ?, ?, ?)";

        Object[] param = new Object[]{json, sessionBean.getUser().getLastBranch().getCurrency().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int controlCashierUser(Account account) {

        String sql = "SELECT CASE WHEN EXISTS(SELECT us.id FROM general.userdata us WHERE us.deleted=FALSE AND us.account_id = ? AND us.type_id = 2) THEN 1 ELSE 0 END ";

        Object[] param = new Object[]{account.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }
}
