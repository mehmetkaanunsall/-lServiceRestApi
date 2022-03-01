/**
 * * Bu Sınıf Centrowiz işlemşeri içindir.
 * (güncelleme Şube )
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 05:28:55
 */
package com.mepsan.marwiz.general.core.business;

import com.mepsan.marwiz.general.core.dao.IMarwizDao;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class MarwizService implements IMarwizService {

    @Autowired
    private IMarwizDao marwizDao;

    public void setMarwizDao(IMarwizDao marwizDao) {
        this.marwizDao = marwizDao;
    }

    /**
     * this method for update the barnch of user
     *
     * @param username username in this session
     * @param groupBranchId new branchId
     * @return updated userdata object
     */
    @Override
    public UserData updateBranch(String username, int groupBranchId) {
        return marwizDao.updateBranch(username, groupBranchId);
    }

    

}
