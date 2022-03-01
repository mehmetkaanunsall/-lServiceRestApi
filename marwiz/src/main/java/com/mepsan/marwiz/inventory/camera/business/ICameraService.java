/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 26.03.2019 10:53:38
 */
package com.mepsan.marwiz.inventory.camera.business;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.Camera;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface ICameraService extends ICrudService<Camera> {

    public List<Camera> findAll();

    public int delete(Camera camera);
}
