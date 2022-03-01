/**
 * Bu sınıf, UserDataDao ve UserDataBean arasında bağlantı sağlar ve gerekli kontrolleri yapar.
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date 12.01.2018 08:45
 *
 */
package com.mepsan.marwiz.system.userdata.business;

import com.mepsan.marwiz.general.common.HashPassword;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.system.userdata.dao.IUserDataDao;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDataService implements IUserDataService {

    @Autowired
    private IUserDataDao userDataDao;

    @Autowired
    private SessionBean sessionBean;

    HashPassword hashPassword = new HashPassword();

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setUserDataDao(IUserDataDao userDataDao) {
        this.userDataDao = userDataDao;
    }

    @Override
    public int create(UserData obj) {

        obj.setPassword(hashPassword.encodePassword(obj.getPassword()));
        return userDataDao.create(obj);

    }

    @Override
    public int update(UserData obj) {
        if (obj.getPassword() != null) {
            obj.setPassword(hashPassword.encodePassword(obj.getPassword()));
        }
        return userDataDao.update(obj);

    }

    /**
     * Bu metot, şubeye atanmış kullanıcıları listeleyen dao metodunu cagırır
     *
     * @param groupBranch groupbranch nesnesi
     * @return şubeye atanmış kullanıcılar listesi
     *//*
    @Override
    public List<AuthorizeUserDataConnection> findAllBranchUsers(GroupBranch groupBranch) {
        return userDataDao.findAllBranchUsers(groupBranch);
    }*/

    /**
     * Bu metot, şubeye ve yetkiye kullanıcı ekleyen userdata dao metodunu
     * çağırır
     *
     * @param authorizeUserDataConnection yetki ve kullanıcı bilgisi(şube
     * içinde)
     * @return 0: hata 1:başarılı işlem -1:yetki nesnesi yok -2:yetkinin şubesi
     * yok -3:user nesnesi yok
     *//*
    @Override
    public int addAuthorizeToUser(AuthorizeUserDataConnection authorizeUserDataConnection) {

        if (authorizeUserDataConnection.getAuthorize() == null) {
            return -1;
        } else if (authorizeUserDataConnection.getAuthorize().getGroupBranch() == null) {
            return -2;
        } else if (authorizeUserDataConnection.getUserData() == null) {
            return -3;
        }

        int result = userDataDao.addUserToAuthorize(authorizeUserDataConnection.getAuthorize(), authorizeUserDataConnection.getUserData());

        if (result > 0) {
            return result;
        } else {
            return 0;
        }
    }*/

    @Override
    public List<UserData> findAll() {

        return userDataDao.findAll();

    }

    @Override
    public List<UserData> selectUserDataWithoutAuthorizeConn(Authorize authorize) {
        return userDataDao.selectUserDataWithoutAuthorizeConn(authorize);
    }

    @Override
    public List<UserData> listOfCashierUsers() {
        return userDataDao.listOfCashierUsers();
    }

    /**
     * Bu metot, userdata dao daki kullanıcı yetkisini güncelleyen metodu
     * çağırır. --kullanıcı sayfası yetki tabından ve şirketler sayfası
     * kullanıcılar tabından
     *
     * @param authorizeUserDataConnection kullanıcı-yetki ilişki nesnesi
     */
    @Override/*
    public int updateUserAuthorize(AuthorizeUserDataConnection authorizeUserDataConnection) {
        return userDataDao.updateUserAuthorize(authorizeUserDataConnection);
    }*/

    /**
     * Bu metot, yetki grubundaki kullanıcıları listeleyen userdata dao metodunu
     * tetikler
     *
     * @param authorize yetki grubu
     * @return yetki grubundaki kullanıcı listesi
     *//*
    @Override
    public List<AuthorizeUserDataConnection> findAllAuthorizeUsers(Authorize authorize) {
        return userDataDao.findAllAuthorizeUsers(authorize);
    }
     */


    public List<UserData> userDataBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param) {

        where = bringWhereBook(where);

        return userDataDao.userDataBook(first, pageSize, sortField, sortOrder, filters, where, type, param);
    }

    @Override
    public int userDataBookCount(String where, String type, List<Object> param) {
        where = bringWhereBook(where);
        return userDataDao.userDataBookCount(where, type, param);
    }

    /**
     * bu metot sessiondaki kullanıcı tipine göre görebileceği kullanıcıları
     * çeken where sartını değiştirir.
     *
     * @param where
     * @return
     */
    public String bringWhere(String where) {
        /*// (Merkez Sayfası) Merkez Kullanıcı VE Is_stationSupport False İse
        if (sessionBean.getUser().getUserDataType().getId() == 150 && sessionBean.isIs_stationSupport() == false) {
            return where + " AND acc.parent_id IS NULL "; //İstasyon Müşterilerinin Parent Id'leri Dolu Olur.
        } // (SSH Sayfası) Merkez Kullanıcı VE Is_stationSupport True İse -> SupportStation Account ID'sini Ata. 
        else if (sessionBean.getUser().getUserDataType().getId() == 150 && sessionBean.isIs_stationSupport() == true) {
            return where + " AND (us.userdatatype_id = 151 OR us.userdatatype_id = 152) "
                    + "AND (us.account_id = " + sessionBean.getUser().getSupportStation().getAccount().getId() + " OR acc.parent_id=" + sessionBean.getUser().getSupportStation().getAccount().getId() + " ) ";
        } // (İstasyon Sayfası) İstasyon Kullanıcısı İse -> Kullanıcının Account ID'sini Ata. 
        else if (sessionBean.getUser().getUserDataType().getId() == 151) {
            return where + " AND (us.userdatatype_id = 151 OR us.userdatatype_id = 152) "
                    + "AND (us.account_id = " + sessionBean.getUser().getAccount().getId() + " OR acc.parent_id=" + sessionBean.getUser().getAccount().getId() + " ) ";
        } // Müşteri Sadece Kendi Kullanıcılarını Görür
        else if (sessionBean.getUser().getUserDataType().getId() == 152) {
            return where + " AND (us.userdatatype_id=152) "
                    + "AND us.account_id = " + sessionBean.getUser().getAccount().getId();
        } else {
            return where;
        }*/
        return "";
    }

    /**
     * bu metot sessiondaki kullanıcı tipine göre görebileceği kullanıcıları
     * çeken where sartını değiştirir.
     *
     * @param where
     * @return
     */
    public String bringWhereBook(String where) {
        /*// (Merkez Sayfası) Merkez Kullanıcı VE Is_stationSupport False İse
        if (sessionBean.getUser().getUserDataType().getId() == 150 && sessionBean.isIs_stationSupport() == false) {
            return where + " AND us.userdatatype_id = 150 ";
        } // (SSH Sayfası) Merkez Kullanıcı VE Is_stationSupport True İse -> SupportStation Account ID'sini Ata. 
        else if (sessionBean.getUser().getUserDataType().getId() == 150 && sessionBean.isIs_stationSupport() == true) {
            return where + " AND (us.userdatatype_id=151) "
                    + "AND us.account_id = " + sessionBean.getUser().getSupportStation().getAccount().getId();
        } // (İstasyon Sayfası) İstasyon Kullanıcısı İse -> Kullanıcının Account ID'sini Ata. 
        else if (sessionBean.getUser().getUserDataType().getId() == 151) {
            return where + " AND (us.userdatatype_id=151) "
                    + "AND us.account_id = " + sessionBean.getUser().getAccount().getId();
        } // Müşteri Sadece Kendi Kullanıcılarını Görür 
        else if (sessionBean.getUser().getUserDataType().getId() == 152) {
            return where + " AND (us.userdatatype_id=152) "
                    + "AND us.account_id = " + sessionBean.getUser().getAccount().getId();
        } else {
            return where;
        }*/
        return "";
    }

    @Override
    public int updatePassword(UserData userData) {
        return userDataDao.updatePassword(userData);
    }

    @Override
    public int delete(UserData userData) {
        return userDataDao.delete(userData);
    }

}
