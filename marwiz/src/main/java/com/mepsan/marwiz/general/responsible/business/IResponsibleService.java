/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.11.2019 10:39:57
 */
package com.mepsan.marwiz.general.responsible.business;

import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IResponsibleService extends ICrudService<Responsible> {

    public List<Responsible> findResponsible(Responsible responsible, int type);

    public int delete(Responsible obj);

    public Responsible findCommunications(Responsible obj);

}
