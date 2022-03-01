/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 18.04.2017 09:10:08
 */
package com.mepsan.marwiz.general.profile.business;

import com.mepsan.marwiz.general.model.general.HotKey;
import com.mepsan.marwiz.general.profile.dao.IHotKeyDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class HotKeyService implements IHotKeyService {
    
    @Autowired
    IHotKeyDao hotKeyDao;
    
    public void setHotKeyDao(IHotKeyDao hotKeyDao) {
        this.hotKeyDao = hotKeyDao;
    }
    
    @Override
    public List<HotKey> listHotKeys() {
        return hotKeyDao.listHotKeys();
    }
    
    @Override
    public int create(HotKey obj) {
        return hotKeyDao.create(obj);
    }
    
    @Override
    public int update(HotKey obj) {
        return hotKeyDao.update(obj);
    }
    
    @Override
    public int delete(HotKey hotKey) {
        return hotKeyDao.delete(hotKey);
    }
    
}
