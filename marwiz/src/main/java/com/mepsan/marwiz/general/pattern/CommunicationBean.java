/**
 *
 *
 *
 * @author Salem walaa abdulhadie
 *
 * @date   15.12.2016 09:55:29
 */
package com.mepsan.marwiz.general.pattern;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.Address;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.model.wot.Internet;
import com.mepsan.marwiz.general.model.wot.Phone;
import com.mepsan.marwiz.general.model.wot.WotCommunication;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

public class CommunicationBean<T> extends AuthenticationLists {

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    public ICommunicationService<Address<T>, T> addressService;

    public ICommunicationService<Phone<T>, T> phoneService;

    public ICommunicationService<Internet<T>, T> internetService;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    private T object;
    private List<WotCommunication> communicationList;
    private WotCommunication selectedObject;
    private int communicationProcessType;
    private List<Type> listOfCommunicationType;
    private Phone<T> phone;
    private Address<T> address;
    private Internet<T> internet;
    private List<Phone<T>> phones;
    private List<Address<T>> addresses;
    private List<Internet<T>> internets;
    private String addressHelp;
    private boolean phoneControl;
    private String dlgWidgetVar;
    private boolean rendered;
    private String messege;

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }

    public String getMessege() {
        return messege;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }

    public List<WotCommunication> getCommunicationList() {
        return communicationList;
    }

    public void setCommunicationList(List<WotCommunication> communicationList) {
        this.communicationList = communicationList;
    }

    public WotCommunication getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WotCommunication selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getCommunicationProcessType() {
        return communicationProcessType;
    }

    public void setCommunicationProcessType(int communicationProcessType) {
        this.communicationProcessType = communicationProcessType;
    }

    public List<Type> getListOfCommunicationType() {
        return listOfCommunicationType;
    }

    public void setListOfCommunicationType(List<Type> listOfCommunicationType) {
        this.listOfCommunicationType = listOfCommunicationType;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Phone<T> getPhone() {
        return phone;
    }

    public void setPhone(Phone<T> phone) {
        this.phone = phone;
    }

    public Address<T> getAddress() {
        return address;
    }

    public void setAddress(Address<T> address) {
        this.address = address;
    }

    public Internet<T> getInternet() {
        return internet;
    }

    public void setInternet(Internet<T> internet) {
        this.internet = internet;
    }

    public String getAddressHelp() {
        return addressHelp;
    }

    public void setAddressHelp(String addressHelp) {
        this.addressHelp = addressHelp;
    }

    public boolean isPhoneControl() {
        return phoneControl;
    }

    public void setPhoneControl(boolean phoneControl) {
        this.phoneControl = phoneControl;
    }

    public String getDlgWidgetVar() {
        if (dlgWidgetVar == null) {
            return "dlg_communication";
        }
        return dlgWidgetVar;
    }

    public void setDlgWidgetVar(String dlgWidgetVar) {
        this.dlgWidgetVar = dlgWidgetVar;
    }

    /**
     * Bu methot şube iletişim işlemleri dialoğunun içeriğini ayarlayarak
     * dialoğu açar.
     *
     * @param t Bu parametre işlem tipidir. 1 ise iletişim eklemek için, 2 ise
     * güncellemek için ayarlamalar yapılır.
     */
    public void createDialog(int t) {  //reset communicationDialog for branch

        communicationProcessType = t;
        RequestContext context = RequestContext.getCurrentInstance();
        listOfCommunicationType = sessionBean.getCommunicationList();

        if (communicationProcessType == 1) {
            selectedObject = new WotCommunication();
            selectedObject.setType(new Type(1));
            citiesAndCountiesBean.reset();
            phone = new Phone<>();
            phone.getCountry().setCode("choose");
            address = new Address<>();
            internet = new Internet<>();
            addressHelp = new String();
            Parameter parameter = applicationBean.getParameterMap().get("default_country");
            address.getCountry().setId(Integer.parseInt(parameter.getValue()));
            citiesAndCountiesBean.updateCity(address.getCountry(), address.getCity(), address.getCounty());

        } else if (communicationProcessType == 2) {
            if (selectedObject.getType().getId() == 1) {
                address = (Address<T>) updateCommunication(selectedObject);
                citiesAndCountiesBean.updateCityAndCounty(address.getCountry(), address.getCity());
                addressHelp = address.getFulladdress();
                if (addressHelp.lastIndexOf(",") > 0) {
                    addressHelp = addressHelp.substring(0, addressHelp.lastIndexOf(","));
                }

            } else if (selectedObject.getType().getItem().getId() == 35) {
                phone = (Phone<T>) updateCommunication(selectedObject);
                bringCountry();
            } else if (selectedObject.getType().getItem().getId() == 34) {
                internet = (Internet<T>) updateCommunication(selectedObject);
            }

        }

        context.execute("PF('" + getDlgWidgetVar() + "').show();");
    }

    /**
     * bu methot şube iletişim işlemlerinin kaydet butonunda çağrılır. Ekleme ya
     * da güncelleme yapar
     */
    public void save() {

        if (selectedObject.getType().getId() == 88 || selectedObject.getType().getId() == 89 || selectedObject.getType().getId() == 90) {
            if (phoneControl) {
                saveCommunication();

            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thisphonenumberformatisnotrecognised")));
            }

        } else {
            saveCommunication();

        }

    }

    /**
     * Telefondaki ülkeyi comboboxa nesne olarak atmak için kullanılmaktadır.
     *
     */
    public void bringCountry() {
        for (Country s : sessionBean.getCountries()) {
            if (s.getId() == phone.getCountry().getId()) {
                phone.getCountry().setCode(s.getCode());
                phone.setCountry(s);

            }
        }
    }

    public void saveCommunication() {
        int defaultCommunication = 0;
        int result = 0;
        if (communicationProcessType == 1) {//create
            // WotCommunication commmunication = new WotCommunication();
            switch (selectedObject.getType().getId()) {
                case 1:
                    //For Address
                    for (WotCommunication communication : communicationList) {
                        if (communication.getType().getId() == 1 && communication.isDefaultValue()) {
                            defaultCommunication = 1;
                        }
                    }
                    if (defaultCommunication == 1) {
                        address.setDefaultValue(false);
                    } else {
                        address.setDefaultValue(true);
                    }

                    address.setFulladdress(addressHelp);
                    address.setObject(object);
                    result = addressService.create(address);
                    address.setId(result);
                    if (result > 0) {
                        addresses.add(address);
                        selectedObject.setId(address.getId());
                        bringCountyName();
                        bringCityName();
                        bringCountryName();
                        selectedObject.setTag(address.getFulladdress() + " ," + address.getCounty().getName() + " - " + address.getCity().getTag() + " - " + address.getCountry().getTag());
                        selectedObject.setType(new Type(1, sessionBean.loc.getString("address")));
                        selectedObject.setDefaultValue(address.isDefaultValue());
                        communicationList.add(selectedObject);
                    }
                    break;
                case 85:
                case 86:
                case 87:
                    //For Internet
                    for (WotCommunication communication : communicationList) {
                        if (communication.getType().getId() != 1) {
                            if (communication.getType().getId() == selectedObject.getType().getId() && communication.isDefaultValue()) {
                                defaultCommunication = 1;
                            }
                        }
                    }
                    if (defaultCommunication == 1) {
                        internet.setDefaultValue(false);
                    } else {
                        internet.setDefaultValue(true);
                    }

                    internet.setInternetType(new Type(selectedObject.getType().getId()));
                    internet.setObject(object);
                    result = internetService.create(internet);
                    if (result > 0) {
                        internet.setId(result);
                        internet.setInternetType(bringTypeOfCommunication(selectedObject.getType().getId()));
                        internets.add(internet);
                        selectedObject.setId(internet.getId());
                        selectedObject.setTag(internet.getTag());
                        selectedObject.setType(internet.getInternetType());
                        selectedObject.setDefaultValue(internet.isDefaultValue());
                        communicationList.add(selectedObject);
                    }
                    break;

                case 88:
                case 89:
                case 90:
                    // For Phone
                    for (WotCommunication communication : communicationList) {
                        if (communication.getType().getId() != 1) {
                            if (communication.getType().getId() == selectedObject.getType().getId() && communication.isDefaultValue()) {
                                defaultCommunication = 1;
                            }
                        }
                    }
                    if (defaultCommunication == 1) {
                        phone.setDefaultValue(false);
                    } else {
                        phone.setDefaultValue(true);
                    }

                    phone.setPhoneType(new Type(selectedObject.getType().getId()));
                    phone.setObject(object);
                    phone.setTag(phone.getTag().replaceAll("[^0-9+]", ""));
                    result = phoneService.create(phone);
                    if (result > 0) {
                        phone.setId(result);
                        phone.setPhoneType(bringTypeOfCommunication(selectedObject.getType().getId()));
                        phones.add(phone);
                        selectedObject.setId(phone.getId());
                        selectedObject.setTag(phone.getTag());
                        selectedObject.setType(phone.getPhoneType());
                        selectedObject.setDefaultValue(phone.isDefaultValue());
                        communicationList.add(selectedObject);
                    }
                    break;

            }

        } else if (communicationProcessType == 2) {//update

            if (selectedObject.getType().getId() == 1) {
                address.setFulladdress(addressHelp);
                result = addressService.update(address);
                if (result > 0) {

                    bringCountyName();
                    bringCityName();
                    bringCountryName();
                    selectedObject.setTag(address.getFulladdress() + " ," + address.getCounty().getName() + " - " + address.getCity().getTag() + " - " + address.getCountry().getTag());

                    //  wotCommunication.setType(new Type(1, sessionBean.loc.getString("address")));
                    selectedObject.setDefaultValue(address.isDefaultValue());
                }
            } else if (selectedObject.getType().getItem().getId() == 34) {
                //For Internet
                internet.setInternetType(new Type(selectedObject.getType().getId()));
                result = internetService.update(internet);
                if (result > 0) {
                    selectedObject.setTag(internet.getTag());
                    // wotCommunication.setType(internet.getInternetType());
                    selectedObject.setDefaultValue(internet.isDefaultValue());
                }
            } else if (selectedObject.getType().getItem().getId() == 35) {
                // For Phone
                phone.setTag(phone.getTag().replaceAll("[^0-9+]", ""));
                phone.setPhoneType(new Type(selectedObject.getType().getId()));
                result = phoneService.update(phone);
                if (result > 0) {
                    selectedObject.setTag(phone.getTag());
                    // wotCommunication.setType(phone.getPhoneType());
                    selectedObject.setDefaultValue(phone.isDefaultValue());
                }
            }
        }
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('" + getDlgWidgetVar() + "').hide();");
        }

        sessionBean.createUpdateMessage(result);

    }

    public Type bringTypeOfCommunication(int id) {
        for (Type type : sessionBean.getCommunicationList()) {
            if (id == type.getId()) {
                type.setTag(type.getNameMap().get(sessionBean.getLangId()).getName());
                return type;
            }
        }
        return null;
    }

    /**
     * Default communication değeri atamak için her iletişim tipinde yalnız 1
     * tane default olmasını kontrol eden fonksiyondur.
     *
     * @param communication default atanmak için seçilen communication
     */
    public void controlDefaultCommunication(WotCommunication communication) {
        List<WotCommunication> defaultList = new ArrayList<>();

        for (WotCommunication wotCommunication1 : communicationList) {
            if (!wotCommunication1.equals(communication)) {
                if (wotCommunication1.isDefaultValue()) {
                    defaultList.add(wotCommunication1);
                }
            }

        }
        for (WotCommunication a : defaultList) {
            if (a.getType().getId() == 1 && communication.getType().getId() == 1) {
                communication.setDefaultValue(false);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanotherdefaultcommunicationforthiscommunicationtype")));

            } else if (a.getType().getId() != 1 && communication.getType().getId() != 1) {
                if (a.getType().getId() == communication.getType().getId()) {
                    communication.setDefaultValue(false);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thereisanotherdefaultcommunicationforthiscommunicationtype")));

                }
            }
        }
        if (communication.getType().getId() == 1) {
            address = (Address<T>) updateCommunication(communication);
            addressHelp = address.getFulladdress();
            if (addressHelp.lastIndexOf(",") > 0) {
                addressHelp = addressHelp.substring(0, addressHelp.lastIndexOf(","));
            }
            address.setFulladdress(addressHelp);
            address.setDefaultValue(communication.isDefaultValue());
            addressService.update(address);
        } else if (communication.getType().getItem().getId() == 34) {
            //For Internet

            internet = (Internet<T>) updateCommunication(communication);
            internet.setInternetType(new Type(communication.getType().getId()));
            internet.setDefaultValue(communication.isDefaultValue());

            internetService.update(internet);
        } else if (communication.getType().getItem().getId() == 35) {
            // For Phone
            phone = (Phone<T>) updateCommunication(communication);
            phone.setTag(phone.getTag().replaceAll("[^0-9+]", ""));
            phone.setPhoneType(new Type(communication.getType().getId()));
            phone.setDefaultValue(communication.isDefaultValue());
            phoneService.update(phone);
        }

    }

    public void setLists() {
        addresses = new ArrayList<>();
        phones = new ArrayList<>();
        internets = new ArrayList<>();

        if (object.getClass().getSimpleName().equals("Responsible")) {
            Responsible responsible = (Responsible) object;
            responsible.getListOfAddresses().stream().forEach((a) -> {
                addresses.add((Address<T>) a);
            });
            responsible.getListOfPhones().stream().forEach((a) -> {
                phones.add((Phone<T>) a);
            });
            responsible.getListOfinternet().stream().forEach((a) -> {
                internets.add((Internet<T>) a);
            });

        }

    }

    public Object updateCommunication(WotCommunication commmunication) {

        if (commmunication.getType().getId() == 1) {
            for (Address<T> address1 : addresses) {
                if (address1.getId() == commmunication.getId()) {
                    if (commmunication.isDefaultValue()) {
                        address1.setDefaultValue(true);
                    }
                    return address1;
                }
            }

        } else {
            if (commmunication.getType().getItem().getId() == 35) {
                for (Phone<T> phone1 : phones) {
                    if (phone1.getId() == commmunication.getId()) {
                        if (commmunication.isDefaultValue()) {
                            phone1.setDefaultValue(true);
                        }
                        return phone1;
                    }
                }
            }
            if (commmunication.getType().getItem().getId() == 34) {
                for (Internet<T> internet1 : internets) {
                    if (internet1.getId() == commmunication.getId()) {
                        if (commmunication.isDefaultValue()) {
                            internet1.setDefaultValue(true);
                        }
                        return internet1;
                    }
                }
            }
        }

        return null;
    }

    public List<WotCommunication> convertToWotCommunication(Object object) {
        setLists();
        List<WotCommunication> communications = new ArrayList<>();

        for (Phone<T> phone : phones) {
            WotCommunication commmunication = new WotCommunication();
            commmunication.setId(phone.getId());
            commmunication.setTag(phone.getTag());
            commmunication.setType(phone.getPhoneType());
            commmunication.setDefaultValue(phone.isDefaultValue());
            communications.add(commmunication);
        }
        for (Address<T> address : addresses) {
            WotCommunication commmunication = new WotCommunication();
            commmunication.setId(address.getId());
            commmunication.setTag(address.getFulladdress());
            commmunication.setType(new Type(1, sessionBean.loc.getString("address")));
            commmunication.setDefaultValue(address.isDefaultValue());
            communications.add(commmunication);
        }
        for (Internet<T> internet : internets) {
            WotCommunication commmunication = new WotCommunication();
            commmunication.setId(internet.getId());
            commmunication.setTag(internet.getTag());
            commmunication.setType(internet.getInternetType());
            commmunication.setDefaultValue(internet.isDefaultValue());
            communications.add(commmunication);
        }
        communicationList = communications;
        return communications;

    }

    public void delete(String dialog) {
        switch (selectedObject.getType().getId()) {
            case 1:
                //For Address
                addressService.delete((Address<T>) updateCommunication(selectedObject));
                break;
            case 85:
            case 86:
            case 87:
                internetService.delete((Internet<T>) updateCommunication(selectedObject));
                break;
            case 88:
            case 89:
            case 90:
                phoneService.delete((Phone<T>) updateCommunication(selectedObject));
                break;

        }

        if (!"".equals(dialog)) {
            RequestContext.getCurrentInstance().execute("PF('" + dialog + "').hide()");
        } else {

            RequestContext.getCurrentInstance().execute("PF('dlg_communication').hide()");
        }

        communicationList.remove(selectedObject);
        RequestContext.getCurrentInstance().update("dtbCommunication");
    }

    public void bringCountryName() {
        for (Country s : sessionBean.getCountries()) {
            if (s.getId() == address.getCountry().getId()) {
                address.getCountry().setTag(s.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
    }

    public void bringCityName() {
        for (City c : citiesAndCountiesBean.getCities()) {
            if (c.getId() == address.getCity().getId()) {
                address.getCity().setTag(c.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
    }

    public void bringCountyName() {
        for (County c : citiesAndCountiesBean.getCounties()) {
            if (c.getId() == address.getCounty().getId()) {
                address.getCounty().setName(c.getName());
                break;
            }
        }
    }

    public void changeType() {
        if (selectedObject.getType().getId() == 88 || selectedObject.getType().getId() == 89 || selectedObject.getType().getId() == 90) {
            Parameter parameter = applicationBean.getParameterMap().get("default_country");
            phone.getCountry().setId(Integer.parseInt(parameter.getValue()));
            bringCountry();
        }
    }
}
