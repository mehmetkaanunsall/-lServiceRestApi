/**
 * This interface ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.05.2019 09:30:28
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Vehicle;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IVehicleService extends ICrudService<Vehicle> {

    public List<Vehicle> findVehicle(Account account);
    
    public int delete(Vehicle obj);

}
