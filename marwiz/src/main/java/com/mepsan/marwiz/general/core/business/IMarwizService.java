/**
 * Bu interface, CentrowizService sınıfına arayüz oluşturur.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 05:26:08
 */
package com.mepsan.marwiz.general.core.business;

import com.mepsan.marwiz.general.model.general.UserData;
import java.util.List;

public interface IMarwizService {

    public UserData updateBranch(String username,  int groupBranchId);
    

}
