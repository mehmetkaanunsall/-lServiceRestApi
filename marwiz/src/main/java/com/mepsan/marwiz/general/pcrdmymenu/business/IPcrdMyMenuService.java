/**
 * Bu interface ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   17.10.2016 11:01:13
 */
package com.mepsan.marwiz.general.pcrdmymenu.business;

import com.mepsan.marwiz.general.model.general.UserDataMenuConnection;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IPcrdMyMenuService extends ICrudService<UserDataMenuConnection> {

    public List<UserDataMenuConnection> findMyModules();

    public int delete(UserDataMenuConnection obj);

    public int reOrder(List<UserDataMenuConnection> list);

}
