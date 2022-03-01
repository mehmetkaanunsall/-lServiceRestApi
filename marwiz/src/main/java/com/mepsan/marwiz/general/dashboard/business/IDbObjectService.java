/**
 * @author Mehmet ERGÜLCÜ
 * @date 02.03.2017 08:18:10
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.model.admin.DbObject;
import com.mepsan.marwiz.general.model.general.Widget;
import java.util.List;

public interface IDbObjectService {

    public DbObject findByTag(String tag);

    public List<DbObject> findAll();

}
