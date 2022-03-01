/**
 * Bu sınıf, WidgetDao sınıfına arayüz oluşturur.
 *
 *
 * @author Zafer Yaşar
 *
 * @date   24.08.2016 09:43
 * @edited Zafer Yaşar- findAllUsersDashboard,updateUsersDashboard,deleteUsersDashboard,insertUsersDashboard,
 * findAllWotUserDashboard metotları eklendi.
 *
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.Widget;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWidgetDao extends ICrud<Widget> {

    public Widget find(Widget obj);

    public List<Widget> findAll();
    

}
