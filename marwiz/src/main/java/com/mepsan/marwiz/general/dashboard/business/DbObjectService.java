/**
 * @author Mehmet ERGÜLCÜ
 * @date 02.03.2017 08:26:30
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.dashboard.dao.IDbObjectDao;
import com.mepsan.marwiz.general.model.admin.DbObject;
import com.mepsan.marwiz.general.model.general.Widget;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class DbObjectService implements IDbObjectService {

    @Autowired
    IDbObjectDao dbObjectDao;

    @Override
    public DbObject findByTag(String tag) {
        return dbObjectDao.findByTag(tag);
    }

    @Override
    public List<DbObject> findAll() {
        return dbObjectDao.findAll();
    }

 

}
