/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.07.2019 02:26:49
 */
package com.mepsan.marwiz.service.waste.dao;

import com.mepsan.marwiz.general.model.log.SendWaste;
import java.util.List;

public interface ISendWasteDao {

    public List<SendWaste> findNotSendedAll();

    public int updateSendWasteResult(SendWaste sendWaste);
}
