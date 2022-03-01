/**
 * Bu sınıf, GroupCompanyBean ve AddressDao sınıfları arasında bağlantı sağlar.
 *
 *
 * @author Ali Kurt
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.pattern;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import org.springframework.beans.factory.annotation.Autowired;

public class ComServiceT<S, T> implements ICommunicationService<S, T> {

    @Autowired
    private ICommunicationDao<S, T> communicationDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCommunicationDao(ICommunicationDao<S, T> communicationDao) {
        this.communicationDao = communicationDao;
    }

    @Override
    public int create(S obj) {

        return communicationDao.create(obj);
    }

    @Override
    public int update(S obj) {
        return communicationDao.update(obj);
    }

    @Override
    public int delete(S obj) {
        return communicationDao.delete(obj);
    }

}
