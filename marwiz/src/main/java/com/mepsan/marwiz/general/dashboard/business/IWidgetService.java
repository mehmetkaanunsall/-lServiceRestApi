/**
 *
 *
 *
 * @author Emine Eser
 *
 * @date   22.08.2016 17:45:31
 */
package com.mepsan.marwiz.general.dashboard.business;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.Widget;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IWidgetService extends ICrudService<Widget> {

    public Widget find(Widget obj);

    public List<Widget> findAll();

}
