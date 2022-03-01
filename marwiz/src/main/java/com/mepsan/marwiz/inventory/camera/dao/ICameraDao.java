/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 26.03.2019 10:57:01
 */
package com.mepsan.marwiz.inventory.camera.dao;

import com.mepsan.marwiz.general.model.inventory.Camera;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface ICameraDao extends ICrud<Camera> {

    public List<Camera> findAll();

    public int delete(Camera camera);
}
