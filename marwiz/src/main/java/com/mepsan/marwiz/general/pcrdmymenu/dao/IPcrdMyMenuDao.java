/**
 * Bu interface ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   17.10.2016 11:01:13
 */
package com.mepsan.marwiz.general.pcrdmymenu.dao;

import com.mepsan.marwiz.general.model.general.UserDataMenuConnection;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IPcrdMyMenuDao extends ICrud<UserDataMenuConnection> {

    public List<UserDataMenuConnection> findMyModules();

    public int delete(UserDataMenuConnection obj);

    public int reOrder(List<UserDataMenuConnection> list);

}
