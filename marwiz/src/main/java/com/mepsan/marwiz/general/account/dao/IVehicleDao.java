/**
 * This interface ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.05.2019 09:32:17
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Vehicle;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IVehicleDao extends ICrud<Vehicle> {

    public List<Vehicle> findVehicle(Account account);

    public int delete(Vehicle obj);

}
