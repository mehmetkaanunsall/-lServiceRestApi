/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.11.2019 10:40:05
 */
package com.mepsan.marwiz.general.responsible.business;

import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.responsible.dao.IResponsibleDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ResponsibleService implements IResponsibleService {

    @Autowired
    public IResponsibleDao responsibleDao;

    public void setResponsibleDao(IResponsibleDao responsibleDao) {
        this.responsibleDao = responsibleDao;
    }

    @Override
    public List<Responsible> findResponsible(Responsible responsible, int type) {
        return responsibleDao.findResponsible(responsible, type);
    }

    @Override
    public int create(Responsible obj) {
        return responsibleDao.create(obj);
    }

    @Override
    public int update(Responsible obj) {
        return responsibleDao.update(obj);
    }

    @Override
    public int delete(Responsible obj) {
        return responsibleDao.delete(obj);
    }

    @Override
    public Responsible findCommunications(Responsible obj) {
        return responsibleDao.findCommunications(obj);
    }

}
