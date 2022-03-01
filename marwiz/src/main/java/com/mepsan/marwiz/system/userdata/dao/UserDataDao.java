/**
 * Bu sınıf, UserData tablosunda created, updated, deleted ve listing işlemleri yapar.
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date   12.01.2018 08:45
 *
 *
 */
package com.mepsan.marwiz.system.userdata.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserDataDao extends JdbcDaoSupport implements IUserDataDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<UserData> findAll() {
        String sql = "SELECT \n"
                
                + "  us.id AS usid,\n"
                + "  us.username AS ususername,\n"
                + "  us.type_id AS ustype_id,\n"
                + "  typd.name AS typdname,\n"
                + "  us.name AS usname,\n"
                + "  us.surname AS ussurname,\n"
                + "  us.is_admin AS isadmin,\n"//
                + "  us.phone AS usphone,\n"
                + "  us.mail AS usmail,\n"
                + "  us.address AS usaddress,\n"
                + "  us.lastauthorize_id AS uslastauthorize_id,\n"
                + "  auth.name AS authname,\n"
                + "  us.language_id AS uslanguage_id,\n"
                + "  lngd.name AS lngdname,\n"
                + "  us.is_rightnumeric AS usis_rightnumeric,\n"
                + "  us.county_id AS uscounty_id,\n"
                + "  us.city_id AS uscity_id,\n"
                + "  us.country_id AS uscountry_id,\n"
                + "  us.status_id AS usstatus_id,\n"
                + "  stcd.name AS stcdname,\n"
                + "  us2.username AS us2username,\n"
                + "  us2.name AS us2name,\n"
                + "  us2.surname AS us2surname, \n"
                + "  b.id AS bid, \n"
                + "  b.name AS bname, \n"
                + "  us.account_id AS usaccount_id,\n"
                + "  acc.name AS accname,\n"
                + "  acc.title AS acctitle,\n"
                + "  acc.is_employee AS accis_employee,\n"
                + "  us.is_authorized AS usis_authorized,\n"
                + "  us.is_cashieraddsalesbasket AS usis_cashieraddsalesbasket,\n"
                + "  us.mpospages AS usmpospages, \n"
                + "  us.c_time AS usc_time\n"
                + "FROM \n"
                + "    general.userdata us\n"
                + "    INNER JOIN system.language_dict lngd ON (lngd.lang_id = us.language_id AND lngd.language_id = ?)\n"
                + "    INNER JOIN system.status_dict stcd ON (stcd.status_id = us.status_id AND stcd.language_id = ?)\n"
                + "    INNER JOIN system.type_dict typd ON (typd.type_id = us.type_id AND typd.language_id = ?)\n"
                + "    LEFT JOIN general.userdata us2 ON (us2.id = us.c_id AND us2.deleted = false)\n"
                + "    LEFT JOIN general.authorize auth ON (auth.id = us.lastauthorize_id AND auth.deleted = false)\n"
                + "    LEFT JOIN general.branch b ON (b.id=us.lastbranch_id and b.deleted=false)\n"
                + "    LEFT JOIN general.account acc ON (acc.id=us.account_id and acc.deleted=false)\n"
                + "WHERE \n"
                + " us.deleted = FALSE\n"
                + " ORDER BY us.id ";
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLanguage().getId()};
        List<UserData> result = getJdbcTemplate().query(sql, param, new UserDataMapper());
        return result;
    }

    /**
     * Yetki sayfasında Şubede daha önce yetkisi olmayan kullanıcılar comboboxa
     * çekilir.
     *
     * @param authorize
     * @return
     */
    @Override
    public List<UserData> selectUserDataWithoutAuthorizeConn(Authorize authorize) {
        String sql = "select \n"
                + "us.id as usid,\n"
                + "us.name as usname,\n"
                + "us.surname as ussurname\n"
                + "from general.userdata us\n"
                + "where us.deleted=false and \n"
                + "us.id not in \n"
                + "(\n"
                + "select uac.userdata_id \n"
                + "from general.userdata_authorize_con uac\n"
                + "left join general.authorize auth  on (uac.authorize_id=auth.id and auth.deleted=false)\n"
                + "where auth.branch_id= ? and uac.deleted=false \n"
                + " )";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<UserData> result = getJdbcTemplate().query(sql, param, new UserDataMapper());
        return result;
    }

    /**
     * Satış İade Alınırken Açık Vardiyada kasiyer kullanıcıları comboboxa 
     * getirilir.
     * @return
     */
    @Override
    public List<UserData> listOfCashierUsers() {
        String sql = "SELECT\n"
                + "                    		 DISTINCT sl.userdata_id as usid,\n"
                + "                             COALESCE(usr.name,'') AS usname,\n"
                + "                             COALESCE(usr.surname,'') AS ussurname\n"
                + "                    	FROM general.sale sl\n"
                + "                           INNER JOIN general.userdata usr ON(usr.id=sl.userdata_id)\n"
                + "                           WHERE sl.branch_id=? AND sl.deleted=False AND usr.type_id = 2\n"
                + "                           AND sl.shift_id=(SELECT gs.id FROM general.shift gs WHERE gs.branch_id=? AND gs.deleted=FALSE AND gs.status_id=7 limit 1)";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<UserData> result = getJdbcTemplate().query(sql, param, new UserDataMapper());
        return result;
    }

    /**
     * bu metot Merkez görünümünde kullanıcı eklerken obje içerisinde seçilen
     * account ın id sini döndürür; İstasyon görünümünde kullanıcı eklerken
     * sessiondaki userın account id sini döndürür.
     *
     * @param userData
     * @return
     */
    public int getAccountId(UserData userData) {
        /*// (Merkez Sayfası) Merkez Kullanıcı İse Ve Is_stationSupport False İse -> Seçilen Account Ata.
        if (sessionBean.getUser().getUserDataType().getId() == 150 && sessionBean.isIs_stationSupport() == false) {//merke kullanıcısı veya ist müşterisi ise
            return userData.getAccount().getId();
        } // (SSH Sayfası) Merkez Kullanıcı İse VE Is_stationSupport True İse -> SupportStation Account ID'sini Ata.  
        else if (sessionBean.getUser().getUserDataType().getId() == 150 && sessionBean.isIs_stationSupport() == true) {
            if (userData.getUserDataType().getId() == 152) {//istasyon görünümünde isttasyonun müşterisi ekleniyorsa
                return userData.getAccount().getId();//secilen müşteriyi gönderdik
            }
            return sessionBean.getUser().getSupportStation().getAccount().getId();
        } // (İstasyon Sayfası) İstasyon Kullanıcısı İse 
        else if (sessionBean.getUser().getUserDataType().getId() == 151) {
            if (userData.getUserDataType().getId() == 152) {//istasyon görünümünde isttasyonun müşterisi ekleniyorsa
                return userData.getAccount().getId();//secilen müşteriyi gönderdik
            }
            return sessionBean.getUser().getAccount().getId();
        }

        return 0;*/
        return 1;
    }

    @Override
    public int create(UserData obj) {

        String sql = " SELECT r_userdata_id FROM general.process_userdata(?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ? ,?);";

        Object[] param = new Object[]{0, obj.getId(), obj.getUsername(), obj.getPassword(), obj.getType().getId(), obj.getName(), obj.getSurname(),
            obj.getPhone(), obj.getMail(), obj.getAddress(),
            obj.getLastBranch().getId(), obj.getLastAuthorize().getId(), obj.getLanguage().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(), obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(),
            obj.getStatus().getId(), obj.getAccount().getId() == 0 ? null : obj.getAccount().getId(), obj.isIsAuthorized(), sessionBean.getUser().getId(), obj.isIsCashierAddSalesBasket(),obj.getMposPages() };

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int update(UserData obj) {

        String sql = " SELECT r_userdata_id FROM general.process_userdata(?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ? );";

        Object[] param = new Object[]{1, obj.getId(), obj.getUsername(), obj.getPassword(), obj.getType().getId(), obj.getName(), obj.getSurname(),
            obj.getPhone(), obj.getMail(), obj.getAddress(),
            obj.getLastBranch().getId(), obj.getLastAuthorize().getId(), obj.getLanguage().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(), obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(),
            obj.getStatus().getId(), obj.getAccount().getId() == 0 ? null : obj.getAccount().getId(), obj.isIsAuthorized(), sessionBean.getUser().getId(), obj.isIsCashierAddSalesBasket(), obj.getMposPages()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int controlUsername(String username) {
        int userDataId;
        String sql = "SELECT id as usid FROM general.userdata us  with (NOLOCK) WHERE username = ? AND deleted=0";

        List<Integer> list = getJdbcTemplate().queryForList(sql, Integer.class, username);
        if (list.size() > 0) {
            userDataId = list.get(0);
        } else {
            userDataId = 0;
        }
        return userDataId;
    }

    /*
    /**
     * Bu metot verilen şubeye atanmış kullanıcıları veritabanından çeker ve
     * listeler
     *
     * @param groupBranch kullanıcıları listelenecek şube
     * @return şubeye atanmış kullanıcı listesi(kullanıcı ve yetki bilgisi)
     *//*
    @Override
    public List<AuthorizeUserDataConnection> findAllBranchUsers(GroupBranch groupBranch) {
        String sql = "SELECT \n"
                + "us.id as usid,\n"
                + "prs.id AS prsid,\n"
                + "prs.name AS prsname,\n"
                + "prs.surname AS prssurname,\n"
                + "auth.id AS authid,\n"
                + "autc.id AS autcid,\n"
                + "auth.name AS authname \n"
                + "FROM general.userdata us  with (NOLOCK) \n"
                + "INNER JOIN general.person prs  with (NOLOCK) ON(prs.id = us.person_id)\n"
                + "INNER JOIN general.userdata_authorize_con autc  with (NOLOCK) ON(us.id=autc.userdata_id)\n"
                + "INNER JOIN general.authorize auth  with (NOLOCK) ON(autc.auth_id=auth.id)\n"
                + "WHERE auth.groupbranch_id= ? \n"
                + "AND us.deleted=0\n"
                + "AND prs.deleted=0\n"
                + "AND autc.deleted=0\n"
                + "AND auth.deleted=0\n";

        Object[] param = new Object[]{groupBranch.getId()};
        List<AuthorizeUserDataConnection> result = getJdbcTemplate().query(sql, param, new UserDataAuthorizeMapper());

        return result;
    }*/

    /**
     * Bu metot verilen yetki grubuna atanmış kullanıcıları veritabanından
     * listeler
     *
     *
     * @param authorize kullanıcıları listelenecek yetki grubu
     * @return yetki grubuna atanmış kullanıcı listesi
     *//*
    @Override
    public List<AuthorizeUserDataConnection> findAllAuthorizeUsers(Authorize authorize) {
        String sql = "SELECT \n"
                + "    us.id as usid,\n"
                + "    prs.name AS prsname,\n"
                + "    prs.surname AS prssurname,\n"
                + "    autc.id AS autcid\n"
                + "FROM \n"
                + "	general.userdata us  with (NOLOCK) \n"
                + "    INNER JOIN general.person prs  with (NOLOCK) ON (prs.id = us.person_id)    \n"
                + "    INNER JOIN general.userdata_authorize_con autc  with (NOLOCK) ON (autc.userdata_id = us.id AND autc.auth_id = ?) \n"
                + "WHERE us.deleted=0 \n"
                + "AND prs.deleted=0 \n"
                + "AND autc.deleted=0";

        Object[] param = new Object[]{authorize.getId()};
        List<AuthorizeUserDataConnection> result = getJdbcTemplate().query(sql, param, new UserDataAuthorizeMapper());
        return result;
    }*/

    /**
     * Bu metot yetki grubuna kullanıcı ekler
     *
     * @param authorize kullanıcının ekleneceği yetki grubu
     * @param userdata yetki grubuna eklenecek kullanıcı
     * @return 0>: kullnıcı-yetki ilişki tablosuna eklenen kayıt id
     *//*
    @Override
    public int addUserToAuthorize(Authorize authorize, UserData userdata) {
        String sql = "INSERT INTO general.userdata_authorize_con(userdata_id,auth_id,c_id,u_id) OUTPUT Inserted.id VALUES (?, ?, ?, ?) ";

        Object[] param = new Object[]{userdata.getId(), authorize.getId(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }*/

    /**
     * Bu metot, kullanıcı ve yetki grubunu günceller.
     *
     * @param authorizeUserDataConnection yetki ve kullanıcı ilişki nesnesi
     *//*
    @Override
    public int updateUserAuthorize(AuthorizeUserDataConnection authorizeUserDataConnection) {

        String sql = "UPDATE \n"
                + "  general.userdata_authorize_con  \n"
                + "SET \n"
                + "  userdata_id = ?,\n"
                + "  auth_id = ?,\n"
                + "  u_id = ? ,\n"
                + "  u_time=GETDATE()"
                + "WHERE \n"
                + "  id = ? \n"
                + " AND deleted=0";

        Object[] param = new Object[]{
            authorizeUserDataConnection.getUserData().getId(),
            authorizeUserDataConnection.getAuthorize().getId(),
            sessionBean.getUser().getId(), authorizeUserDataConnection.getId()
        };

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }*/

    /**
     * Bu metot kullanıcının yetkisi olduğu yetki grublarını çeker.
     *
     * @param userData yetki bilgisi çekilecek kullanıcı nesnesi
     * @return yetki grubu listesi(şirket ve şube bilgisi içerisinde)
     */
    /*
    @Override
    public List<AuthorizeUserDataConnection> findAllUserAuthorizes(UserData userData) {
        String sql = "SELECT\n"
                + "    autc.id AS autcid,\n"
                + "    auth.account_id AS authaccount_id,\n"
                + "    gcmp.id AS gcmpid,\n"
                + "    gcmp.name AS gcmpname,\n"
                + "    gb.id AS gbid,\n"
                + "    gb.name AS gbname,\n"
                + "    auth.id AS authid,\n"
                + "    auth.name AS authname\n"
                + "FROM\n"
                + "    general.userdata_authorize_con autc  with (NOLOCK) \n"
                + "    LEFT JOIN general.authorize auth  with (NOLOCK) ON (auth.id=autc.auth_id)\n"
                + "    LEFT JOIN general.groupbranch gb  with (NOLOCK) ON (gb.id=auth.groupbranch_id)\n"
                + "    LEFT JOIN general.groupcompany gcmp  with (NOLOCK) ON (gcmp.id=gb.groupcompany_id)\n"
                + "WHERE\n"
                + "    autc.userdata_id= ? AND autc.deleted=0";

        Object[] param = new Object[]{userData.getId()};

        List<AuthorizeUserDataConnection> result = getJdbcTemplate().query(sql, param, new UserDataAuthorizeMapper());
        return result;
    }
     */
//    @Override
//    public List<UserData> userDataBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
//        /*
//        if (type.equals("groupcompanyuser")) {
//            where = where + " AND NOT EXISTS \n"
//                    + "    ( SELECT \n"
//                    + "            autc.id \n"
//                    + "        FROM \n"
//                    + "            general.userdata_authorize_con autc  with (NOLOCK) \n"
//                    + "            INNER JOIN general.authorize auth  with (NOLOCK) on (auth.id = autc.auth_id AND auth.groupbranch_id = " + ((GroupBranch) param.get(0)).getId() + ")\n"
//                    + "         WHERE\n"
//                    + "            autc.userdata_id = us.id \n"
//                    + "         AND autc.deleted=0\n"
//                    + "         AND auth.deleted=0\n"
//                    + "    ) ";
//        } else if (type.equals("authorizationuser")){//merkez için şubeye bak
//            where = where + " AND NOT EXISTS \n"
//                    + "    ( SELECT \n"
//                    + "            autc.id \n"
//                    + "        FROM \n"
//                    + "            general.userdata_authorize_con autc  with (NOLOCK) \n"
//                    + "            INNER JOIN general.authorize auth  with (NOLOCK) on (auth.id = autc.auth_id AND auth.groupbranch_id = " + ((GroupBranch) param.get(0)).getId() + ")\n"
//                    + "         WHERE\n"
//                    + "            autc.userdata_id = us.id \n"
//                    + "         AND autc.deleted=0\n"
//                    + "         AND auth.deleted=0\n"
//                    + "    ) ";
//        }else if (type.equals("authorizationuserstation")) {// istasyon tarafında şubeye bakma
//            where = where + " AND NOT EXISTS \n"
//                    + "    ( SELECT \n"
//                    + "            autc.id \n"
//                    + "        FROM \n"
//                    + "            general.userdata_authorize_con autc  with (NOLOCK) \n"
//                    + "            INNER JOIN general.authorize auth  with (NOLOCK) on (auth.id = autc.auth_id)\n"
//                    + "         WHERE\n"
//                    + "            autc.userdata_id = us.id \n"
//                    + "         AND autc.deleted=0\n"
//                    + "         AND auth.deleted=0\n"
//                    + "    ) ";
//        }*/
//
//        String sql = "SELECT \n"
//                  + "    us.id as usid, \n"
//                  + "    us.person_id as usperson_id,"
//                  + "    prs.name as prsname,\n"
//                  + "    prs.surname as prssurname,\n"
//                  + "     us.username as ususername\n"
//                  + "FROM \n"
//                  + "	general.userdata us  with (NOLOCK) \n"
//                  + "	INNER JOIN general.person prs  with (NOLOCK) ON(prs.id = us.person_id)\n"
//                  + "     LEFT JOIN general.account acc  with (NOLOCK) ON (acc.id = us.account_id)\n"
//                  + "WHERE us.deleted=0  AND prs.deleted=0 " + where + "\n"
//                  + " ORDER BY us.id DESC OFFSET " + first + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY \n";
//
//        Object[] params = new Object[]{};
//        List<UserData> result = getJdbcTemplate().query(sql, params, new UserDataMapper());
//
//        return result;
//    }
//
//    /**
//     * Bu metot şubeye atanmamış kullanıcı listesinin sayısını verir
//     *
//     * @param where
//     * @param type
//     * @param param
//     * @param branch
//     * @return şubedeye atanmayan kullanıcı sayısı
//     */
//    @Override
//    public int userDataBookCount(String where, String type, List<Object> param) {
//        /*
//        if (type.equals("groupcompanyuser")) {
//            where = where + " AND NOT EXISTS \n"
//                    + "    ( SELECT \n"
//                    + "            autc.id \n"
//                    + "        FROM \n"
//                    + "            general.userdata_authorize_con autc  with (NOLOCK) \n"
//                    + "            INNER JOIN general.authorize auth  with (NOLOCK) on (auth.id = autc.auth_id AND auth.groupbranch_id = " + ((GroupBranch) param.get(0)).getId() + ")\n"
//                    + "         WHERE\n"
//                    + "            autc.userdata_id = us.id \n"
//                    + "         AND autc.deleted=0\n"
//                    + "         AND auth.deleted=0\n"
//                    + "    ) ";
//        } else if (type.equals("authorizationuser")){//merkez için şubeye bak
//            where = where + " AND NOT EXISTS \n"
//                    + "    ( SELECT \n"
//                    + "            autc.id \n"
//                    + "        FROM \n"
//                    + "            general.userdata_authorize_con autc  with (NOLOCK) \n"
//                    + "            INNER JOIN general.authorize auth  with (NOLOCK) on (auth.id = autc.auth_id AND auth.groupbranch_id = " + ((GroupBranch) param.get(0)).getId() + ")\n"
//                    + "         WHERE\n"
//                    + "            autc.userdata_id = us.id \n"
//                    + "         AND autc.deleted=0\n"
//                    + "         AND auth.deleted=0\n"
//                    + "    ) ";
//        }else if (type.equals("authorizationuserstation")) {// istasyon tarafında şubeye bakma
//            where = where + " AND NOT EXISTS \n"
//                    + "    ( SELECT \n"
//                    + "            autc.id \n"
//                    + "        FROM \n"
//                    + "            general.userdata_authorize_con autc  with (NOLOCK) \n"
//                    + "            INNER JOIN general.authorize auth  with (NOLOCK) on (auth.id = autc.auth_id)\n"
//                    + "         WHERE\n"
//                    + "            autc.userdata_id = us.id \n"
//                    + "         AND autc.deleted=0\n"
//                    + "         AND auth.deleted=0\n"
//                    + "    ) ";
//        }
//         */
//        String sql = "SELECT \n"
//                  + "   COUNT(us.id) \n"
//                  + "FROM \n"
//                  + "	general.userdata us  with (NOLOCK) \n"
//                  + "	INNER JOIN general.person prs  with (NOLOCK) ON(prs.id = us.person_id)\n"
//                  + "     LEFT JOIN general.account acc  with (NOLOCK) ON (acc.id = us.account_id)\n"
//                  + "WHERE us.deleted=0  AND prs.deleted=0 " + where;
//        Object[] params = new Object[]{};
//        int result = getJdbcTemplate().queryForObject(sql, params, Integer.class);
//        return result;
//    }
    /**
     * gelen kullanıcının şifresini günceller
     *
     * @param userData
     * @return
     */
    @Override
    public int updatePassword(UserData userData) {
        String sql = "UPDATE general.userdata SET password = ?, u_id=?, u_time=now() WHERE id = ? ";

        Object[] param = new Object[]{userData.getPassword(), sessionBean.getUser().getId(), userData.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(UserData obj) {
        String sql = " SELECT r_userdata_id FROM general.process_userdata(?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ? ,?);";

        Object[] param = new Object[]{2, obj.getId(), obj.getUsername(), obj.getPassword(), obj.getType().getId(), obj.getName(), obj.getSurname(),
            obj.getPhone(), obj.getMail(), obj.getAddress(),
            obj.getLastBranch().getId(), obj.getLastAuthorize().getId(), obj.getLanguage().getId(),
            obj.getCountry().getId() == 0 ? null : obj.getCountry().getId(), obj.getCity().getId() == 0 ? null : obj.getCity().getId(), obj.getCounty().getId() == 0 ? null : obj.getCounty().getId(),
            obj.getStatus().getId(), obj.getAccount().getId() == 0 ? null : obj.getAccount().getId(), obj.isIsAuthorized(), sessionBean.getUser().getId(), obj.isIsCashierAddSalesBasket(),obj.getMposPages()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public List<UserData> userDataBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int userDataBookCount(String where, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
