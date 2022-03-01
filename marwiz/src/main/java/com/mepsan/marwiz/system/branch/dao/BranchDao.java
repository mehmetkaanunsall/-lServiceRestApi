/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.02.2018 10:51:54
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Type;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class BranchDao extends JdbcDaoSupport implements IBranchDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Branch> findAll(String where) {
        String sql = "SELECT \n"
                + "      br.id AS brid,\n"
                + "      br.name AS brname,\n"
                + "      br.title AS brtitle,\n"
                + "      br.taxno AS brtaxno,\n"
                + "      br.taxoffice AS brtaxoffice,\n"
                + "      br.status_id AS brstatus_id,\n"
                + "      sttd.name AS sttdname,\n"
                + "      br.type_id AS brtype_id,\n"
                + "      typd.name AS typdname,\n"
                + "      br.currency_id AS brcurrency_id,\n"
                + "      crrd.name AS crrdname,\n"
                + "      br.currencyrounding AS brcurrencyrounding,\n"
                + "      br.dateformat AS brdateformat,\n"
                + "      br.decimalsymbol AS brdecimalsymbol,\n"
                + "      br.phone AS brphone,\n"
                + "      br.mobile AS brmobile,\n"
                + "      br.email AS bremail,\n"
                + "      br.address AS braddress,\n"
                + "      br.latitude AS brlatitude,\n"
                + "      br.longitude AS brlongitude,\n"
                + "      br.county_id AS brcounty_id,\n"
                + "      br.city_id AS brcity_id,\n"
                + "      br.country_id AS brcountry_id,\n"
                + "      br.language_id AS brlanguage_id,\n"
                + "      br.licencecode AS brlicencecode,\n"
                + "      br.mersisno AS brmersisno,\n"
                + "      br.webaddress AS brwebaddress,\n"
                + "      br.traderegisterno AS brtraderegisterno,\n"
                + "      br.is_central AS bris_central,\n"
                + "      br.is_licencecheck AS bris_licencecheck,\n"
                + "      br.is_agency AS bris_agency,\n"
                + "      br.is_takeaway AS bris_takeaway,\n"
                + "      br.is_vehicledelivery AS bris_vehicledelivery,\n"
                + "      br.concepttype_id as brconcepttype_id,\n"
                + "      usd.name as usdname,\n"
                + "      usd.surname as usdsurname,\n"
                + "      usd.username as usdusername,\n"
                + "      br.c_time as brc_time, \n"
                + "      br.c_id AS brc_id,\n"
                + "      brs.specialitem AS brsspecialitem,\n"
                + "      brs.is_centralintegration AS brsis_centralintegration\n"
                + "FROM general.branch br \n"
                + "LEFT JOIN general.branchsetting brs ON(brs.branch_id=br.id)\n"
                + "LEFT JOIN general.userdata usd ON(usd.id=br.c_id)\n"
                + "INNER JOIN system.status_dict sttd on (sttd.status_id=br.status_id and sttd.language_id=?)\n"
                + "INNER JOIN system.type_dict typd on (typd.type_id=br.type_id and typd.language_id=?)\n"
                + "INNER JOIN system.currency_dict crrd ON (crrd.currency_id = br.currency_id AND crrd.language_id = ?)\n"
                + "WHERE br.deleted=FALSE \n" + where + " "
                + "ORDER BY br.id DESC";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(),
            sessionBean.getUser().getLanguage().getId()};
        return getJdbcTemplate().query(sql, param, new BranchMapper());
    }

    @Override
    public int create(Branch obj) {
        int processType = 0;
        String typeList = "";
        for (Type type : sessionBean.getTypes(15)) {
            typeList = typeList + "," + String.valueOf(type.getId());
        }
        typeList = typeList.substring(1, typeList.length());

        String sql = "SELECT r_branch_id FROM general.process_branch (?, ?, ? , ? , ?, ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{processType, obj.getId(), obj.getName(), obj.getTitle(), obj.getLicenceCode(), obj.getTaxNo(), obj.getTaxOffice(), obj.getStatus().getId(),
            obj.getType().getId(), obj.getCurrency().getId(), obj.getCurrencyrounding(), obj.getDateFormat(), obj.getDecimalsymbol(), obj.getPhone(),
            obj.getMobile(), obj.getMail(), obj.getAddress(), obj.getLatitude(), obj.getLongitude(), obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getLanguage().getId(), obj.getListOfAuthorizes().get(0).getName(), typeList, obj.getMersisNo(), obj.getWebAddress(), obj.getTradeRegisterNo(), obj.isIsCentral(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.isIsCenterInteg()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Branch obj) {
        int processType = 1;
        String sql = " SELECT r_branch_id FROM general.process_branch (?, ?, ?, ?, ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{processType, obj.getId(), obj.getName(), obj.getTitle(), obj.getLicenceCode(), obj.getTaxNo(), obj.getTaxOffice(), obj.getStatus().getId(),
            obj.getType().getId(), obj.getCurrency().getId(), obj.getCurrencyrounding(), obj.getDateFormat(), obj.getDecimalsymbol(), obj.getPhone(),
            obj.getMobile(), obj.getMail(), obj.getAddress(), obj.getLatitude(), obj.getLongitude(), obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getLanguage().getId(), null, null, obj.getMersisNo(), obj.getWebAddress(), obj.getTradeRegisterNo(), obj.isIsCentral(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.isIsCenterInteg()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Branch> selectBranchs() {
        String sql = "SELECT \n"
                + "      br.id AS brid,\n"
                + "      br.name AS brname\n"
                + "FROM general.branch br \n"
                + "WHERE br.deleted=FALSE";

        List<Branch> result = getJdbcTemplate().query(sql, new BranchMapper());
        return result;
    }

    @Override
    public List<Branch> findUserAuthorizeBranch() {
        String sql = "SELECT \n"
                + "   br.id AS brid,\n"
                + "   br.name AS brname\n"
                + "FROM general.branch br \n"
                + "   INNER JOIN general.authorize aut ON(aut.branch_id = br.id AND aut.deleted = FALSE)\n"
                + "   INNER JOIN general.userdata_authorize_con usda ON(usda.authorize_id = aut.id AND usda.deleted=FALSE)\n"
                + "   WHERE br.deleted =FALSE AND usda.userdata_id = ?";

        Object[] param = new Object[]{sessionBean.getUser().getId()};
        List<Branch> result = getJdbcTemplate().query(sql, param, new BranchMapper());

        return result;

    }

    @Override
    public List<Branch> findUserAuthorizeBranchForBankAccount() {
        String sql = "SELECT \n"
                + "   br.id AS brid,\n"
                + "   br.name AS brname\n"
                + "FROM general.branch br \n"
                + "   INNER JOIN general.authorize aut ON(aut.branch_id = br.id AND aut.deleted = FALSE)\n"
                + "   INNER JOIN general.userdata_authorize_con usda ON(usda.authorize_id = aut.id AND usda.deleted=FALSE)\n"
                + "   WHERE br.deleted =FALSE AND usda.userdata_id = ? \n"
                + "   AND br.id IN (SELECT bbc.branch_id FROM finance.bankaccount_branch_con bbc \n"
                + "        WHERE bbc.deleted =FALSE)";

        Object[] param = new Object[]{sessionBean.getUser().getId()};
        List<Branch> result = getJdbcTemplate().query(sql, param, new BranchMapper());

        return result;

    }

    @Override
    public int delete(Branch obj) {
        int processType = 3;
        String sql = " SELECT r_branch_id FROM general.process_branch (?, ?, ?, ?, ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{processType, obj.getId(), obj.getName(), obj.getTitle(), obj.getLicenceCode(), obj.getTaxNo(), obj.getTaxOffice(), obj.getStatus().getId(),
            obj.getType().getId(), obj.getCurrency().getId(), obj.getCurrencyrounding(), obj.getDateFormat(), obj.getDecimalsymbol(), obj.getPhone(),
            obj.getMobile(), obj.getMail(), obj.getAddress(), obj.getLatitude(), obj.getLongitude(), obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getLanguage().getId(), null, null, obj.getMersisNo(), obj.getWebAddress(), obj.getTradeRegisterNo(), obj.isIsCentral(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), false};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }
}
