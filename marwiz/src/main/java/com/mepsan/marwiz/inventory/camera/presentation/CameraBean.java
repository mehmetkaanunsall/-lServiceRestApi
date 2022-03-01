/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 26.03.2019 11:25:56
 */
package com.mepsan.marwiz.inventory.camera.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.Camera;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceItemService;
import com.mepsan.marwiz.inventory.automationdevice.presentation.AutomationDeviceTabBean;
import com.mepsan.marwiz.inventory.camera.business.ICameraService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class CameraBean extends GeneralDefinitionBean<Camera> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{cameraService}")
    public ICameraService cameraService;

    private int processType;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCameraService(ICameraService cameraService) {
        this.cameraService = cameraService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("----------CameraBean----------------");
        find();
        toogleList = Arrays.asList(true, true, true, true, true);
        
        setListBtn(sessionBean.checkAuthority(new int[]{232, 233, 234}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new Camera();
        RequestContext.getCurrentInstance().execute("PF('dlg_CameraProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        if (processType == 1) {
            result = cameraService.create(selectedObject);

        } else if (processType == 2) {
            result = cameraService.update(selectedObject);

        }

        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_CameraProcess').hide();");
            find();
        }
        sessionBean.createUpdateMessage(result);
    }

    public void update() {
        processType = 2;
        RequestContext.getCurrentInstance().execute("PF('dlg_CameraProcess').show();");
    }

    @Override
    public List<Camera> findall() {
        return cameraService.findAll();
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = cameraService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.execute("PF('dlg_CameraProcess').hide();");
        }
        sessionBean.createUpdateMessage(result);
        find();
    }

    public void find() {
        listOfObjects = new ArrayList();
        listOfObjects = findall();
        RequestContext.getCurrentInstance().update("frmCamera");

    }

}
