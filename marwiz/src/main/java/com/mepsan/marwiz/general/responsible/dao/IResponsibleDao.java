/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.11.2019 10:40:16
 */
package com.mepsan.marwiz.general.responsible.dao;

import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IResponsibleDao extends ICrud<Responsible> {

    public List<Responsible> findResponsible(Responsible responsible, int type);

    public int delete(Responsible obj);

    public Responsible findCommunications(Responsible obj);

}
