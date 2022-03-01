/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 26.03.2019 10:56:22
 */
package com.mepsan.marwiz.inventory.camera.business;

import com.mepsan.marwiz.general.model.inventory.Camera;
import com.mepsan.marwiz.inventory.camera.dao.ICameraDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class CameraService implements ICameraService {

    @Autowired
    private ICameraDao cameraDao;

    @Override
    public List<Camera> findAll() {
        return cameraDao.findAll();
    }

    @Override
    public int delete(Camera camera) {
        return cameraDao.delete(camera);
    }

    @Override
    public int create(Camera obj) {
        return cameraDao.create(obj);
    }

    @Override
    public int update(Camera obj) {
        return cameraDao.update(obj);
    }

}
