/**
 * Bu sınıf, UserDataService sınıfına arayüz oluşturur.
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date  12.01.2018 08:45
 *
 */
package com.mepsan.marwiz.system.userdata.business;

import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;
import java.util.Map;

public interface IUserDataService extends ICrudService<UserData> {

    /*  public List<AuthorizeUserDataConnection> findAllBranchUsers(GroupBranch groupBranch);

    public int addAuthorizeToUser(AuthorizeUserDataConnection authorizeUserDataConnection);

    public int updateUserAuthorize(AuthorizeUserDataConnection authorizeUserDataConnection);

    public List<AuthorizeUserDataConnection> findAllAuthorizeUsers(Authorize authorize);

    public List<AuthorizeUserDataConnection> findAllUserAuthorizes(UserData userData);*/
    public List<UserData> userDataBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int userDataBookCount(String where, String type, List<Object> param);

    public int updatePassword(UserData userData);

    public int delete(UserData userData);

    public List<UserData> findAll();

    public List<UserData> selectUserDataWithoutAuthorizeConn(Authorize authorize);

    public List<UserData> listOfCashierUsers();
}
