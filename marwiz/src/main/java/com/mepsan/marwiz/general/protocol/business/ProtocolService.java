/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 23.11.2016 16:12:14
 */
package com.mepsan.marwiz.general.protocol.business;

import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.general.Protocol;
import com.mepsan.marwiz.general.protocol.dao.IProtocolDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ProtocolService implements IProtocolService {

    @Autowired
    private IProtocolDao protocolDao;

    public void setProtocolDao(IProtocolDao protocolDao) {
        this.protocolDao = protocolDao;
    }

    @Override
    public List<Protocol> findAll(Item item) {
        return protocolDao.findAll(item);
    }

    @Override
    public int create(Protocol obj) {
        return protocolDao.create(obj);
    }

    @Override
    public int update(Protocol obj) {
        return protocolDao.update(obj);
    }

    @Override
    public int testBeforeDelete(Protocol protocol) {
       return protocolDao.testBeforeDelete(protocol);
    }

    @Override
    public int delete(Protocol protocol) {
       return protocolDao.delete(protocol);
    }

}
