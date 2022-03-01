/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   04.01.2019 14:09:47
 */
package com.mepsan.marwiz.general.gridproperties.service;

import com.mepsan.marwiz.general.gridproperties.dao.IGridOrderColumnDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class GridOrderColumnService implements IGridOrderColumnService {

    @Autowired
    private IGridOrderColumnDao gridOrderColumnDao;

    public void setGridOrderColumnDao(IGridOrderColumnDao gridOrderColumnDao) {
        this.gridOrderColumnDao = gridOrderColumnDao;
    }

    @Override
    public int reorder(int pageId, String gridId, String reorder) {
        return gridOrderColumnDao.reorder(pageId, gridId, reorder);
    }

    @Override
    public String bringOrder(int pageId, String gridId) {
        return gridOrderColumnDao.bringOrder(pageId, gridId);
    }

    @Override
    public int update(int pageId, String gridId, String reorder) {
        return gridOrderColumnDao.update(pageId, gridId, reorder);
    }

}
