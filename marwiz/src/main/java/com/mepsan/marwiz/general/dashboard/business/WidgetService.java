/**
 *
 *
 *
 * @author Emine Eser
 *
 * @date   22.08.2016 18:04:13
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.dashboard.dao.IWidgetDao;
import com.mepsan.marwiz.general.model.general.Widget;
import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WidgetService implements IWidgetService {

    @Autowired
    IWidgetDao widgetDao;

    public void setWidgetDao(IWidgetDao widgetDao) {
        this.widgetDao = widgetDao;
    }

    @Override
    public int create(Widget obj) {
        return widgetDao.create(obj);
    }

    @Override
    public int update(Widget obj) {
        return widgetDao.update(obj);
    }

    @Override
    public Widget find(Widget obj) {
        return widgetDao.find(obj);
    }

    @Override
    public List<Widget> findAll() {
        return widgetDao.findAll();
    }

}
