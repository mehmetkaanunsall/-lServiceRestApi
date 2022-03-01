/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bank.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BankDao extends JdbcDaoSupport implements IBankDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Bu metot sessiondaki sirketin tüm bankalarını ve bankaya baglı şubelerini
     * listeler
     *
     * @return banka listesi
     */
    @Override
    public List<Bank> findAll() {
        String sql = "SELECT \n"
                + "    bnk.id as bnkid,  \n"
                + "    bnk.name as bnkname,  \n"
                + "    bnk.code as bnkcode,  \n"
                + "    bnk.status_id as bnkstatus_id,  \n"
                + "    sttd.name as sttdname, \n"
                + "    bnk.phone as bnkphone,  \n"
                + "    bnk.mail as bnkmail,  \n"
                + "    bnk.address as bnkaddress,  \n"
                + "    bnk.city_id as bnkcity_id,  \n"
                + "    ctyd.name as ctydname, \n"
                + "    bnk.country_id as bnkcountry_id,\n"
                + "    ctrd.name as ctrdname,\n"
                + "    bnk.county_id as bnkcounty_id,\n"
                + "    cnty.name as cntyname,\n"
                + "	(select array_to_json(array_agg(row_to_json(t)))\n"
                + "from (SELECT  \n"
                + "       		bkb.id as bkbid,  \n"
                + "       		bkb.name as bkbname,  \n"
                + "       		bkb.code as bkbcode,  \n"
                + "       		bkb.status_id as bkbstatus_id,  \n"
                + "       		sttd2.name as bkbsttdname, \n"
                + "       		bkb.phone as bkbphone,  \n"
                + "       		bkb.email as bkbemail,  \n"
                + "       		bkb.address as bkbaddress,  \n"
                + "       		bkb.city_id as bkbcity_id,  \n"
                + "       		ctyd2.name as bkbctydname, \n"
                + "			bkb.country_id as bkbctycountry_id,\n"
                + "			ctrd2.name as bkbctrdname,\n"
                + "                     bkb.county_id as bkbcounty_id,\n"
                + "                     cnty.name as bkbcntyname \n"
                + "		FROM finance.bankbranch bkb  \n"
                + "       		INNER JOIN system.status_dict sttd2 ON (sttd2.status_id=bkb.status_id AND sttd2.language_id=" + sessionBean.getUser().getLanguage().getId() + ") \n"
                + "       		INNER JOIN system.city_dict ctyd2 ON (ctyd2.city_id=bkb.city_id AND ctyd2.language_id=" + sessionBean.getUser().getLanguage().getId() + ") \n"
                + "			INNER JOIN system.country_dict ctrd2 ON(ctrd2.country_id=bkb.country_id AND ctrd2.language_id=" + sessionBean.getUser().getLanguage().getId() + ") \n"
                + "                     INNER JOIN system.county cnty ON(cnty.id=bkb.county_id) \n"
                + "		WHERE bkb.bank_id = bnk.id AND bkb.deleted = false)t) as bnkbranchs \n"
                + "FROM finance.bank bnk  \n"
                + "    INNER JOIN system.status_dict sttd ON (sttd.status_id=bnk.status_id AND sttd.language_id=?) \n"
                + "    INNER JOIN system.city_dict ctyd ON (ctyd.city_id=bnk.city_id AND ctyd.language_id=?) \n"
                + "    INNER JOIN system.country_dict ctrd  ON(ctrd.country_id=bnk.country_id AND ctrd.language_id=?) \n"
                + "    INNER JOIN system.county cnty ON(cnty.id=bnk.county_id) \n"
                + "WHERE bnk.deleted=false";
        Object[] params = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId()};
        return getJdbcTemplate().query(sql, params, new BankMapper());
    }

    @Override
    public List<Bank> bankBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> obj) {
        String whereBranch = "";
        if (type.equals("bankaccount")) {//hesaplar sayfasında banka secmek ıcın
            where = where + " and bnk.status_id=17 ";
            whereBranch += " and bkb.status_id = 19";
        }

        String sql = "SELECT \n"
                + "         bnk.id as bnkid,  \n"
                + "         bnk.name as bnkname,  \n"
                + "         bnk.code as bnkcode,\n"
                + "         (select array_to_json(array_agg(row_to_json(t))) from (SELECT  \n"
                + "                      		bkb.id as bkbid,  \n"
                + "       		                bkb.name as bkbname,  \n"
                + "                                     bkb.code as bkbcode,  \n"
                + "                                     bkb.status_id as bkbstatus_id,  \n"
                + "                                     sttd2.name as bkbsttdname, \n"
                + "                                     bkb.phone as bkbphone,  \n"
                + "                                     bkb.email as bkbemail,  \n"
                + "                                     bkb.address as bkbaddress,  \n"
                + "                                     bkb.city_id as bkbcity_id,  \n"
                + "                                     ctyd2.name as bkbctydname, \n"
                + "                                     bkb.country_id as bkbctycountry_id,\n"
                + "                                     ctrd2.name as bkbctrdname,\n"
                + "                                     bkb.county_id as bkbcounty_id,\n"
                + "                                     cnty.name as bkbcntyname\n"
                + "               		FROM finance.bankbranch bkb \n"
                + "                                    INNER JOIN system.status_dict sttd2 ON (sttd2.status_id=bkb.status_id AND sttd2.language_id=" + sessionBean.getUser().getLanguage().getId() + ") \n"
                + "                                    INNER JOIN system.city_dict ctyd2 ON (ctyd2.city_id=bkb.city_id AND ctyd2.language_id=" + sessionBean.getUser().getLanguage().getId() + ") \n"
                + "                                    INNER JOIN system.country_dict ctrd2 ON(ctrd2.country_id=bkb.country_id AND ctrd2.language_id=" + sessionBean.getUser().getLanguage().getId() + ") \n"
                + "               		       INNER JOIN system.county cnty ON(cnty.id=bkb.county_id) \n"
                + "WHERE bkb.bank_id = bnk.id AND bkb.deleted = false" + whereBranch + ")t) as bnkbranchs\n"
                + "         FROM finance.bank bnk  \n"
                + "         WHERE bnk.deleted=false" + where + "\n"
                + "         ORDER BY bnk.id DESC \n"
                + " limit " + pageSize + " offset " + first;

        return getJdbcTemplate().query(sql, new BankMapper());
    }

    @Override
    public int bankBookCount(String where, String type, List<Object> obj) {
        if (type.equals("bankaccount")) {//hesaplar sayfasında banka secmek ıcın
            where = where + " and bnk.status_id=17 ";
        }
        String sql = "SELECT COUNT(bnk.id)\n"
                + "FROM finance.bank bnk  \n"
                + "WHERE bnk.deleted=false " + where + "\n";

        int result = getJdbcTemplate().queryForObject(sql, Integer.class);
        return result;
    }

    @Override
    public int create(Bank obj) {
        String sql = "INSERT INTO finance.bank (name,code,status_id,phone,mail,address,county_id,city_id,country_id,c_id) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?) RETURNING id ;";
        Object[] param = new Object[]{obj.getName(), obj.getCode(), obj.getStatus().getId(),
            obj.getPhone(), obj.getEmail(), obj.getAddress(), obj.getCounty().getId(), obj.getCity().getId(), obj.getCountry().getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Bank obj) {
        String sql = "UPDATE finance.bank "
                + "SET "
                + "name= ?, "
                + "code= ? ,"
                + "status_id= ? ,"
                + "phone= ? ,"
                + "mail= ? ,"
                + "address= ? ,"
                + "county_id= ? ,"
                + "city_id= ? ,"
                + "country_id= ? ,"
                + "u_id= ? ,"
                + "u_time= now() "
                + "WHERE id= ? ";
        Object[] param = new Object[]{obj.getName(), obj.getCode(), obj.getStatus().getId(),
            obj.getPhone(), obj.getEmail(), obj.getAddress(), obj.getCounty().getId(), obj.getCity().getId(), obj.getCountry().getId(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Bank> selectBank() {
        String sql = "SELECT  \n"
                + "     bnk.id AS bnkid, \n"
                + "     bnk.name AS bnkname \n"
                + " FROM finance.bank bnk \n"
                + " WHERE bnk.deleted =false "
                + " AND bnk.status_id=17 "
                + " ORDER BY bnk.name";

        return getJdbcTemplate().query(sql, new BankMapper());
    }

    @Override
    public int testBeforeDelete(String branchList) {

        String sql = "SELECT CASE WHEN EXISTS (SELECT bankbranch_id FROM finance.bankaccount WHERE bankbranch_id IN (" + branchList + ") AND deleted=False) THEN 1 ELSE 0 END";
        
        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Bank bank) {
        String sql = "UPDATE finance.bank SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n"
                + "UPDATE finance.bankbranch SET deleted=TRUE, u_id=?, d_time=NOW() WHERE deleted=False AND bank_id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), bank.getId(), sessionBean.getUser().getId(), bank.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
