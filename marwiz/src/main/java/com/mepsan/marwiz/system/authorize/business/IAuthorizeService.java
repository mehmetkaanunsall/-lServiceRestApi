/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.01.2018 01:16:05
 */

package com.mepsan.marwiz.system.authorize.business;

import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IAuthorizeService extends ICrud<Authorize>{

    public List<Authorize> selectAuthorize();
    public List<Authorize> findAll();
    public int updateModuleTab(Authorize authorize);
    public int updatePageTab(Authorize authorize);
    public List<Authorize> selectAuthorizeToTheBranch(Branch branch); 
    public int delete(Authorize authorize);
    public int testBeforeDelete(Authorize authorize);
    
} 
