/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.05.2019 09:31:59
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.account.dao.IVehicleDao;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Vehicle;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class VehicleService implements IVehicleService {

    @Autowired
    public IVehicleDao vehicleDao;

    public void setVehicleDao(IVehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    @Override
    public List<Vehicle> findVehicle(Account account) {
       return vehicleDao.findVehicle(account);
    }

    @Override
    public int create(Vehicle obj) {
       return vehicleDao.create(obj);
    }

    @Override
    public int update(Vehicle obj) {
       return vehicleDao.update(obj);
    }

    @Override
    public int delete(Vehicle obj) {
        return vehicleDao.delete(obj);
    }

}
