/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.07.2019 02:26:27
 */
package com.mepsan.marwiz.service.waste.business;

import com.mepsan.marwiz.general.model.log.SendWaste;
import java.util.List;

public interface ISendWasteService {

    public void sendWaste(SendWaste sendWaste);

    public void sendWasteAsync();

    public void executeSendWaste(List<SendWaste> sendWastes);

    public int updateSendWasteResult(SendWaste sendWaste);
}
