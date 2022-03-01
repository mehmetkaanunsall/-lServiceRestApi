/**
 * This class ...
 *
 *
 * @author Ali Kurt
 *
 * @date   12.01.2018 05:45:37
 */
package com.mepsan.marwiz.general.unit.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.stock.presentation.StockAlternativeUnitsTabBean;
import com.mepsan.marwiz.inventory.stock.presentation.StockProcessBean;
import com.mepsan.marwiz.inventory.stockrequest.presentation.NewStockRequestBean;
import com.mepsan.marwiz.inventory.stockrequest.presentation.StockRequestBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class NewUnitBean extends GeneralDefinitionBean<Unit> {

    @ManagedProperty(value = "#{unitService}")
    public IUnitService unitService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int processType, type;
    private String whichPage, whichPage2;
    private List<Unit> unitList;
    List<Unit> listInternationalCode;
    private Unit selectedUnitCode;

    private boolean isAvailableUnit;
    private String oldName;
    private int oldId;
    private int newId;

    private List<Unit> unitControlList;
    private boolean isShowEquivalent;

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Unit> getListInternationalCode() {
        return listInternationalCode;
    }

    public void setListInternationalCode(List<Unit> listInternationalCode) {
        this.listInternationalCode = listInternationalCode;
    }

    public Unit getSelectedUnitCode() {
        return selectedUnitCode;
    }

    public void setSelectedUnitCode(Unit selectedUnitCode) {
        this.selectedUnitCode = selectedUnitCode;
    }

    public boolean isIsAvailableUnit() {
        return isAvailableUnit;
    }

    public void setIsAvailableUnit(boolean isAvailableUnit) {
        this.isAvailableUnit = isAvailableUnit;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public List<Unit> getUnitControlList() {
        return unitControlList;
    }

    public void setUnitControlList(List<Unit> unitControlList) {
        this.unitControlList = unitControlList;
    }

    public boolean isIsShowEquivalent() {
        return isShowEquivalent;
    }

    public void setIsShowEquivalent(boolean isShowEquivalent) {
        this.isShowEquivalent = isShowEquivalent;
    }

    @PostConstruct
    @Override
    public void init() {
        unitList = new ArrayList<>();
        unitControlList = new ArrayList<>();
        unitList = unitService.findAll();
        setListBtn(sessionBean.checkAuthority(new int[]{251, 252}, 0));
        listInternationalCode = new ArrayList<>();
        listInternationalCode = listInternationalCode();
    }

    @Override
    public void create() {
        isShowEquivalent = false;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        whichPage = (String) request.getAttribute("whichPage");
        whichPage2 = (String) request.getAttribute("whichPage2");
        oldName = "";
        oldId = 0;
        isAvailableUnit = false;
        System.out.println("------NewUnitBean-----whichPage----" + whichPage + "--------whichPage2------" + whichPage2);
    }

    public void create(int t, List<Unit> list) {
        unitControlList = list;
        selectedObject = new Unit();
        selectedObject.setMainWeightUnit(new Unit());
        isShowEquivalent = false;
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        whichPage = (String) request.getAttribute("whichPage");
        whichPage2 = (String) request.getAttribute("whichPage2");
        System.out.println("------NewUnitBean-----whichPage----" + whichPage + "--------whichPage2------" + whichPage2);
        type = t;
        processType = 1;
        oldName = "";
        oldId = 0;
        isAvailableUnit = false;
        RequestContext context1 = RequestContext.getCurrentInstance();
        if (type == 1) {

            context1.execute("PF('dlg_unitdeffinitionproc').show();");
        } else if (type == 2) {
            context1.execute("PF('dlg_alternativeunitdeffinitionproc').show();");
        }
    }

    /**
     * Entegrasyonu olmayan şubede isim değişikliği yaptığında o birim sistemde
     * var mı diye kontrol edilir. Merkezi entegrasyonu olan şubede birim zaten
     * varsa direk bilgileri getirilir ve değiştirilmeye izin verilmez.
     */
    public void findUnitAccordingToName() {
        if (oldName != null && !oldName.equals(selectedObject.getName())) {
            isAvailableUnit = false;
            if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                Unit foundUnit = new Unit();
                foundUnit = unitService.findUnitAccordingToName(selectedObject);
                if (foundUnit.getId() > 0) {
                    newId = foundUnit.getId();
                    selectedObject = foundUnit;
                    oldName = selectedObject.getName();
                    selectedObject.setId(oldId);
                    isAvailableUnit = true;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisunitisavailablealreadyincentralintegrationbranches")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                } else {
                    selectedObject.setCenterunit_id(0);
                    oldName = selectedObject.getName();
                }

                if (whichPage2.equals("stockAlternativeUnitsTabBean") && type == 2) {
                    RequestContext.getCurrentInstance().update("tbvStokProc:frmUnitDefinitionProcess:pgrUnitDefinitionProcess");
                } else {
                    RequestContext.getCurrentInstance().update("frmUnitDefinitionProcess:pgrUnitDefinitionProcess");
                }

            }
        }
    }

    @Override
    public void save() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        System.out.println("------NewUnitBean-----whichPage----" + whichPage + "--------whichPage2------" + whichPage2 + "--type----" + type);
        if (processType == 1) {
            for (Unit unit : unitControlList) {
                if (unit.getName().equalsIgnoreCase(selectedObject.getName()) && unit.getId() != selectedObject.getId()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("unitalreadyavailable")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }

            // birim merkezi entegrasyonu olan şubelerde var update yapılcak sadece
            if (isAvailableUnit) {
                selectedObject.setId(newId);
                result = unitService.update(selectedObject);
            } else {
                result = unitService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                }
            }
            if (result > 0) {

                if (whichPage.equals("stockProcessBean") && type == 1) {
                    StockProcessBean stockProcessBean = (StockProcessBean) viewMap.get("stockProcessBean");

                    stockProcessBean.getUnitList().add(selectedObject);
                    stockProcessBean.getSelectedObject().getUnit().setId(selectedObject.getId());
                    context.update("slcUnit");
                    context.execute("PF('dlg_unitdeffinitionproc').hide();");
                    stockProcessBean.changeUnit();
                } else if (whichPage2.equals("stockAlternativeUnitsTabBean") && type == 2) {
                    StockAlternativeUnitsTabBean stockAlternativeUnitsTabBean = (StockAlternativeUnitsTabBean) viewMap.get("stockAlternativeUnitsTabBean");

                    stockAlternativeUnitsTabBean.getUnitList().add(selectedObject);
                    stockAlternativeUnitsTabBean.getSelectedObject().setUnit(selectedObject);
                    context.update("frmAlternativeUnitsProcess:slAlternativeUnit");
                    context.execute("PF('dlg_alternativeunitdeffinitionproc').hide();");
                }
            }

        }
        sessionBean.createUpdateMessage(result);

    }

    @Override
    public List<Unit> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void InternationalCodeView() {

        RequestContext context = RequestContext.getCurrentInstance();

        if (whichPage.equals("stockProcessBean") && type == 1) {
            context.execute("PF('dlg_unitinternationalcode').show();");
        } else if (whichPage2.equals("stockAlternativeUnitsTabBean") && type == 2) {
            context.execute("PF('dlg_stckunitinternationalcode').show();");
        }

    }

    public void updateUnitCode() {

        RequestContext context = RequestContext.getCurrentInstance();

        if (whichPage.equals("stockProcessBean") && type == 1) {
            System.out.println("------WİCHPAGE2 ********************************************");
            selectedObject.setInternationalCode(selectedUnitCode.getInternationalCode());
            context.update("frmUnitDefinitionProcess:pgrUnitDefinitionProcess");
            context.execute("PF('dlg_unitinternationalcode').hide();");
        } else if (whichPage2.equals("stockAlternativeUnitsTabBean") && type == 2) {
            selectedObject.setInternationalCode(selectedUnitCode.getInternationalCode());
            System.out.println("------WİCHPAGE2 İFFFFFF********************************************");
            System.out.println("------WİCHPAGE2 SELECTED-----" + selectedObject.getInternationalCode());
            context.update("tbvStokProc:frmUnitDefinitionProcess:pgrUnitDefinitionProcess");
            context.execute("PF('dlg_stckunitinternationalcode').hide();");

        }

    }

    /**
     * Bu metot birim değiştiğinde çalışır.
     */
    public void changeUnit() {
        bringUnit();
        if (selectedObject.getMainWeightUnit().getId() == 0) {
            selectedObject.setMainWeight(null);
            isShowEquivalent = false;
        } else if (selectedObject.getId() == selectedObject.getMainWeightUnit().getId()) {
            selectedObject.setMainWeight(BigDecimal.ONE);
            isShowEquivalent = false;
        } else if (selectedObject.getId() != selectedObject.getMainWeightUnit().getId()) {
            selectedObject.setMainWeight(null);
            isShowEquivalent = true;
        }
    }

    public void bringUnit() {

        for (Unit unit : unitList) {
            if (unit.getId() == selectedObject.getMainWeightUnit().getId()) {
                selectedObject.getMainWeightUnit().setName(unit.getName());
            }
        }
    }

    public List<Unit> listInternationalCode() {

        Unit obj = new Unit();
        obj.setId(1);
        obj.setName(sessionBean.loc.getString("afifunitprice"));
        obj.setInternationalCode("AFF");
        listInternationalCode.add(obj);

        Unit obj1 = new Unit();
        obj1.setId(2);
        obj1.setName(sessionBean.loc.getString("atvunitprice"));
        obj1.setInternationalCode("AKQ");
        listInternationalCode.add(obj1);

        Unit obj2 = new Unit();
        obj2.setId(3);
        obj2.setName(sessionBean.loc.getString("goldstandard"));
        obj2.setInternationalCode("AYR");
        listInternationalCode.add(obj2);

        Unit obj3 = new Unit();
        obj3.setId(4);
        obj3.setName(sessionBean.loc.getString("kilogrammetresquared"));
        obj3.setInternationalCode("B32");
        listInternationalCode.add(obj3);

        Unit obj4 = new Unit();
        obj4.setId(5);
        obj4.setName(sessionBean.loc.getString("bas"));
        obj4.setInternationalCode("BAS");
        listInternationalCode.add(obj4);

        Unit obj5 = new Unit();
        obj5.setId(6);
        obj5.setName(sessionBean.loc.getString("one"));
        obj5.setInternationalCode("C62");
        listInternationalCode.add(obj5);

        Unit obj6 = new Unit();
        obj6.setId(7);
        obj6.setName(sessionBean.loc.getString("carryingcapacityperton"));
        obj6.setInternationalCode("CCT");
        listInternationalCode.add(obj6);

        Unit obj7 = new Unit();
        obj7.setId(8);
        obj7.setName(sessionBean.loc.getString("piecedouble"));
        obj7.setInternationalCode("CPR");
        listInternationalCode.add(obj7);

        Unit obj8 = new Unit();
        obj8.setId(9);
        obj8.setName(sessionBean.loc.getString("grosscalorievalue"));
        obj8.setInternationalCode("D30");
        listInternationalCode.add(obj8);

        Unit obj9 = new Unit();
        obj9.setId(10);
        obj9.setName(sessionBean.loc.getString("kiloliter"));
        obj9.setInternationalCode("D40");
        listInternationalCode.add(obj9);

        Unit obj10 = new Unit();
        obj10.setId(11);
        obj10.setName(sessionBean.loc.getString("fissileisotopegram"));
        obj10.setInternationalCode("GFI");
        listInternationalCode.add(obj10);

        Unit obj11 = new Unit();
        obj11.setId(12);
        obj11.setName(sessionBean.loc.getString("silver"));
        obj11.setInternationalCode("GMS");
        listInternationalCode.add(obj11);

        Unit obj12 = new Unit();
        obj12.setId(13);
        obj12.setName(sessionBean.loc.getString("gram"));
        obj12.setInternationalCode("GRM");
        listInternationalCode.add(obj12);

        Unit obj13 = new Unit();
        obj13.setId(14);
        obj13.setName(sessionBean.loc.getString("grosston"));
        obj13.setInternationalCode("GT");
        listInternationalCode.add(obj13);

        Unit obj14 = new Unit();
        obj14.setId(15);
        obj14.setName(sessionBean.loc.getString("hundredpieces"));
        obj14.setInternationalCode("CEN");
        listInternationalCode.add(obj14);

        Unit obj16 = new Unit();
        obj16.setId(17);
        obj16.setName(sessionBean.loc.getString("driednetweightkilogram"));
        obj16.setInternationalCode("K58");
        listInternationalCode.add(obj16);

        Unit obj17 = new Unit();
        obj17.setId(18);
        obj17.setName(sessionBean.loc.getString("kilogrampieces"));
        obj17.setInternationalCode("K62");
        listInternationalCode.add(obj17);

        Unit obj19 = new Unit();
        obj19.setId(20);
        obj19.setName(sessionBean.loc.getString("kilogram"));
        obj19.setInternationalCode("KGM");
        listInternationalCode.add(obj19);

        Unit obj20 = new Unit();
        obj20.setId(21);
        obj20.setName(sessionBean.loc.getString("perkilogram"));
        obj20.setInternationalCode("KH6");
        listInternationalCode.add(obj20);

        Unit obj21 = new Unit();
        obj21.setId(22);
        obj21.setName(sessionBean.loc.getString("hydrogenperoxidekilogram"));
        obj21.setInternationalCode("KHY");
        listInternationalCode.add(obj21);

        Unit obj23 = new Unit();
        obj23.setId(24);
        obj23.setName(sessionBean.loc.getString("nitrogenkilogram"));
        obj23.setInternationalCode("KNI");
        listInternationalCode.add(obj23);

        Unit obj25 = new Unit();
        obj25.setId(26);
        obj25.setName(sessionBean.loc.getString("kilogrampotassiumhydroxide"));
        obj25.setInternationalCode("KPH");
        listInternationalCode.add(obj25);

        Unit obj26 = new Unit();
        obj26.setId(27);
        obj26.setName(sessionBean.loc.getString("kilogramdouble"));
        obj26.setInternationalCode("KPR");
        listInternationalCode.add(obj26);

        Unit obj27 = new Unit();
        obj27.setId(28);
        obj27.setName(sessionBean.loc.getString("90%dryproductkilogram"));
        obj27.setInternationalCode("KSD");
        listInternationalCode.add(obj27);

        Unit obj28 = new Unit();
        obj28.setId(29);
        obj28.setName(sessionBean.loc.getString("sodiumhydroxidekilogram"));
        obj28.setInternationalCode("KSH");
        listInternationalCode.add(obj28);

        Unit obj29 = new Unit();
        obj29.setId(30);
        obj29.setName(sessionBean.loc.getString("uraniumkilogram"));
        obj29.setInternationalCode("KUR");
        listInternationalCode.add(obj29);

        Unit obj30 = new Unit();
        obj30.setId(31);
        obj30.setName(sessionBean.loc.getString("kilowatthour"));
        obj30.setInternationalCode("KWH");
        listInternationalCode.add(obj30);

        Unit obj31 = new Unit();
        obj31.setId(32);
        obj31.setName(sessionBean.loc.getString("kilowatt"));
        obj31.setInternationalCode("KWT");
        listInternationalCode.add(obj31);

        Unit obj32 = new Unit();
        obj32.setId(33);
        obj32.setName(sessionBean.loc.getString("literofpurealcohol"));
        obj32.setInternationalCode("LPA");
        listInternationalCode.add(obj32);

        Unit obj33 = new Unit();
        obj33.setId(34);
        obj33.setName(sessionBean.loc.getString("liter"));
        obj33.setInternationalCode("LTR");
        listInternationalCode.add(obj33);

        Unit obj34 = new Unit();
        obj34.setId(35);
        obj34.setName(sessionBean.loc.getString("squaremetre"));
        obj34.setInternationalCode("MTK");
        listInternationalCode.add(obj34);

        Unit obj36 = new Unit();
        obj36.setId(37);
        obj36.setName(sessionBean.loc.getString("cubicmetre"));
        obj36.setInternationalCode("MTQ");
        listInternationalCode.add(obj36);

        Unit obj37 = new Unit();
        obj37.setId(38);
        obj37.setName(sessionBean.loc.getString("metre"));
        obj37.setInternationalCode("MTR");
        listInternationalCode.add(obj37);

        Unit obj38 = new Unit();
        obj38.setId(39);
        obj38.setName(sessionBean.loc.getString("numberofcells"));
        obj38.setInternationalCode("NCL");
        listInternationalCode.add(obj38);

        Unit obj39 = new Unit();
        obj39.setId(40);
        obj39.setName(sessionBean.loc.getString("carat"));
        obj39.setInternationalCode("NCR");
        listInternationalCode.add(obj39);

        Unit obj42 = new Unit();
        obj42.setId(43);
        obj42.setName(sessionBean.loc.getString("double"));
        obj42.setInternationalCode("PR");
        listInternationalCode.add(obj42);

        Unit obj43 = new Unit();
        obj43.setId(44);
        obj43.setName(sessionBean.loc.getString("thousandcubicmeters"));
        obj43.setInternationalCode("R9");
        listInternationalCode.add(obj43);

        Unit obj44 = new Unit();
        obj44.setId(45);
        obj44.setName(sessionBean.loc.getString("set"));
        obj44.setInternationalCode("SET");
        listInternationalCode.add(obj44);

        Unit obj45 = new Unit();
        obj45.setId(46);
        obj45.setName(sessionBean.loc.getString("thousandpieces"));
        obj45.setInternationalCode("T3");
        listInternationalCode.add(obj45);

        Unit obj46 = new Unit();
        obj46.setId(47);
        obj46.setName(sessionBean.loc.getString("piece"));
        obj46.setInternationalCode("NIU");
        listInternationalCode.add(obj46);

        Unit obj47 = new Unit();
        obj47.setId(48);
        obj47.setName(sessionBean.loc.getString("package"));
        obj47.setInternationalCode("PA");
        listInternationalCode.add(obj47);

        Unit obj48 = new Unit();
        obj48.setId(49);
        obj48.setName(sessionBean.loc.getString("box"));
        obj48.setInternationalCode("BX");
        listInternationalCode.add(obj48);

        Unit obj49 = new Unit();
        obj49.setId(50);
        obj49.setName(sessionBean.loc.getString("milligram"));
        obj49.setInternationalCode("MGM");
        listInternationalCode.add(obj49);

        Unit obj50 = new Unit();
        obj50.setId(51);
        obj50.setName(sessionBean.loc.getString("ton"));
        obj50.setInternationalCode("26");
        listInternationalCode.add(obj50);

        Unit obj51 = new Unit();
        obj51.setId(52);
        obj51.setName(sessionBean.loc.getString("nettone"));
        obj51.setInternationalCode("NT");
        listInternationalCode.add(obj51);

        Unit obj52 = new Unit();
        obj52.setId(53);
        obj52.setName(sessionBean.loc.getString("millimetre"));
        obj52.setInternationalCode("MMT");
        listInternationalCode.add(obj52);

        Unit obj53 = new Unit();
        obj53.setId(54);
        obj53.setName(sessionBean.loc.getString("centimeter"));
        obj53.setInternationalCode("CMT");
        listInternationalCode.add(obj53);

        Unit obj54 = new Unit();
        obj54.setId(55);
        obj54.setName(sessionBean.loc.getString("kilometer"));
        obj54.setInternationalCode("KTM");
        listInternationalCode.add(obj54);

        Unit obj55 = new Unit();
        obj55.setId(56);
        obj55.setName(sessionBean.loc.getString("millilitre"));
        obj55.setInternationalCode("MLT");
        listInternationalCode.add(obj55);

        Unit obj58 = new Unit();
        obj58.setId(59);
        obj58.setName(sessionBean.loc.getString("squaredecimetre"));
        obj58.setInternationalCode("DMK");
        listInternationalCode.add(obj58);

        Unit obj59 = new Unit();
        obj59.setId(60);
        obj59.setName(sessionBean.loc.getString("standardcubicmetre"));
        obj59.setInternationalCode("SM3");
        listInternationalCode.add(obj59);

        return listInternationalCode;
    }

}
