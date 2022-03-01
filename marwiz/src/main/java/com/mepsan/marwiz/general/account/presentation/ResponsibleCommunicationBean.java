/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.10.2019 03:33:46
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.wot.Address;
import com.mepsan.marwiz.general.model.wot.Internet;
import com.mepsan.marwiz.general.model.wot.Phone;
import com.mepsan.marwiz.general.pattern.CommunicationBean;
import com.mepsan.marwiz.general.pattern.ICommunicationService;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@ViewScoped
public class ResponsibleCommunicationBean extends CommunicationBean<Responsible> {

    @ManagedProperty(value = "#{responsibleAddressService}") //address spring
    private ICommunicationService<Address<Responsible>, Responsible> addressService1;

    @ManagedProperty(value = "#{responsiblePhoneService}") //phone spring
    private ICommunicationService<Phone<Responsible>, Responsible> phoneService1;

    @ManagedProperty(value = "#{responsibleInternetService}") //internet spring
    private ICommunicationService<Internet<Responsible>, Responsible> internetService1;

    public void setAddressService1(ICommunicationService<Address<Responsible>, Responsible> addressService) {
        this.addressService = addressService;
    }

    public void setPhoneService1(ICommunicationService<Phone<Responsible>, Responsible> phoneService) {
        this.phoneService = phoneService;
    }

    public void setInternetService1(ICommunicationService<Internet<Responsible>, Responsible> internetService) {
        this.internetService = internetService;
    }

    @PostConstruct
    public void init() {
        System.out.println("---ResponsibleCommunicationBean------------");

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Long pageProcess = (Long) request.getAttribute("whichPage");
        int type = (int) (long) pageProcess;

        if (type == 1) {
            setListBtn(sessionBean.checkAuthority(new int[]{75, 76, 77}, 0));
        } else if (type == 2) {
            setListBtn(sessionBean.checkAuthority(new int[]{277, 278, 279}, 0));
        } else if (type == 3) {
            setListBtn(sessionBean.checkAuthority(new int[]{283, 284, 285}, 0));
        }
    }

}
