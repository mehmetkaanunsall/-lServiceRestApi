/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.03.2018 16:54:40
 */
package com.mepsan.marwiz.general.report.safeextract.dao;

import com.mepsan.marwiz.general.model.finance.SafeMovement;
import java.util.List;


public interface ISafeExtractDao {

    public List<SafeMovement> findAll(String where);

}
