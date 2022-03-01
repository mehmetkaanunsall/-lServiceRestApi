/**
 * @author Mehmet ERGÜLCÜ
 * @date 01.03.2017 05:01:19
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.admin.DbObject;
import com.mepsan.marwiz.general.model.general.Widget;
import java.util.List;

public interface IDbObjectDao {

    DbObject findByTag(String tag);

    List<DbObject> findAll();

}
