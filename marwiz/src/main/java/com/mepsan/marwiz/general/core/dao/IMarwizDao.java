/**
 * This interface ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 05:25:41
 */
package com.mepsan.marwiz.general.core.dao;

import com.mepsan.marwiz.general.model.general.UserData;

public interface IMarwizDao {

    public UserData updateBranch(String username,  int groupBranchId);
}
