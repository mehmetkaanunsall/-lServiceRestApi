/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.01.2018 12:57:33
 */

package com.mepsan.marwiz.system.authorize.dao;

import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IAuthorizeDao extends ICrud<Authorize> {

    public List<Authorize> selectAuthorize();    
    
    public List<Authorize> findAll();
    
    public int updateModuleTab(Authorize authorize);
    
    public int updatePageTab(Authorize authorize);
    
    public List<Authorize> selectAuthorizeToTheBranch(Branch branch); 
    
    public int delete(Authorize authorize);
    
    public int testBeforeDelete(Authorize authorize);
    
}
