/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 02:27:38
 */
package com.mepsan.marwiz.automation.fuelshift.presentation;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.mepsan.marwiz.automation.fuelshift.business.GFFuelShiftService;
import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.automation.fuelshift.dao.FuelShiftControlFile;
import com.mepsan.marwiz.automation.report.fuelshiftreport.business.IFuelShiftService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.service.automation.business.ICheckAutomationItemService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@ManagedBean
@ViewScoped
public class FuelShiftBean extends GeneralBean<FuelShift> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{fuelShiftService}")
    public IFuelShiftService fuelShiftService;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    public IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{branchSettingService}")
    public IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{gfFuelShiftService}")
    public GFFuelShiftService gfFuelShiftService;

    @ManagedProperty(value = "#{checkAutomationItemService}")
    public ICheckAutomationItemService checkAutomationItemService;

    public List<FuelShiftSales> rowsSales;
    private UploadedFile uploadedFile = null;
    private BranchSetting branchSetting;
    private FuelShift selectedShift;
    private boolean isFailure;
    private FuelShift resultFuelShift;
    private boolean isCheck, isCheck2;
    private String stockNameList;
    private String title;
    private List<SmbFile> listOfSamba;
    private SmbFile selectedSambaFile;
    private Date beginDate;
    private Date endDate;
    private String createWhere;
    private List<FuelShift> listOfTotals;
    private FuelShiftParam fuelShiftParam;

    public List<FuelShiftSales> getRowsSales() {
        return rowsSales;
    }

    public void setRowsSales(List<FuelShiftSales> rowsSales) {
        this.rowsSales = rowsSales;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setFuelShiftService(IFuelShiftService fuelShiftService) {
        this.fuelShiftService = fuelShiftService;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public void setGfFuelShiftService(GFFuelShiftService gfFuelShiftService) {
        this.gfFuelShiftService = gfFuelShiftService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public FuelShift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(FuelShift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public boolean isIsFailure() {
        return isFailure;
    }

    public void setIsFailure(boolean isFailure) {
        this.isFailure = isFailure;
    }

    public boolean isIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isIsCheck2() {
        return isCheck2;
    }

    public void setIsCheck2(boolean isCheck2) {
        this.isCheck2 = isCheck2;
    }

    public void setCheckAutomationItemService(ICheckAutomationItemService checkAutomationItemService) {
        this.checkAutomationItemService = checkAutomationItemService;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public List<SmbFile> getListOfSamba() {
        return listOfSamba;
    }

    public void setListOfSamba(List<SmbFile> listOfSamba) {
        this.listOfSamba = listOfSamba;
    }

    public SmbFile getSelectedSambaFile() {
        return selectedSambaFile;
    }

    public void setSelectedSambaFile(SmbFile selectedSambaFile) {
        this.selectedSambaFile = selectedSambaFile;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public List<FuelShift> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<FuelShift> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    //Sayfada tutulan filtreleme parametreleri için class oluşturuldu
    public class FuelShiftParam {

        // private BranchSetting selectedBranch;
        private Date beginDate;
        private Date endDate;
        private boolean isCheck, isCheck2;

        public FuelShiftParam() {

        }

        public Date getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(Date beginDate) {
            this.beginDate = beginDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public boolean isIsCheck() {
            return isCheck;
        }

        public void setIsCheck(boolean isCheck) {
            this.isCheck = isCheck;
        }

        public boolean isIsCheck2() {
            return isCheck2;
        }

        public void setIsCheck2(boolean isCheck2) {
            this.isCheck2 = isCheck2;
        }

    }

    public FuelShiftParam getFuelShiftParam() {
        return fuelShiftParam;
    }

    public void setFuelShiftParam(FuelShiftParam fuelShiftParam) {
        this.fuelShiftParam = fuelShiftParam;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------FuelShiftBean");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        setBeginDate(calendar.getTime());
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        setEndDate(calendar.getTime());
        listOfTotals = new ArrayList<>();
        fuelShiftParam = new FuelShiftParam();
        boolean isBack = false;
        String where = "";
        if (sessionBean.parameter instanceof ArrayList) {

            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof FuelShiftParam) {
                    isBack = true;
                    beginDate = ((FuelShiftParam) ((ArrayList) sessionBean.parameter).get(i)).getBeginDate();
                    endDate = ((FuelShiftParam) ((ArrayList) sessionBean.parameter).get(i)).getEndDate();
                    isCheck = ((FuelShiftParam) ((ArrayList) sessionBean.parameter).get(i)).isIsCheck();
                    isCheck2 = ((FuelShiftParam) ((ArrayList) sessionBean.parameter).get(i)).isIsCheck2();

                    break;
                }
            }

        }

        createWhere = fuelShiftTransferService.createWhere(beginDate, endDate);

        if (isCheck2) {
            where = createWhere + " AND shf.is_confirm= FALSE ";
        } else {
            where = where + createWhere;

        }

        listOfObjects = findall(where);

        selectedObject = new FuelShift();
        selectedShift = new FuelShift();
        branchSetting = sessionBean.getLastBranchSetting();
        isFailure = false;
        resultFuelShift = new FuelShift();
        rowsSales = new ArrayList<>();
        listOfSamba = new ArrayList<>();

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{52, 53}, 0));
    }

    public void find() {
        createWhere = fuelShiftTransferService.createWhere(beginDate, endDate);
        listOfObjects = findall(createWhere);
    }

    @Override
    public LazyDataModel<FuelShift> findall(String where) {
        return new CentrowizLazyDataModel<FuelShift>() {
            @Override
            public List<FuelShift> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<FuelShift> result = fuelShiftTransferService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, isCheck);
                listOfTotals = fuelShiftTransferService.count(where, isCheck);

                int count = 0;
                for (FuelShift fuelShift : listOfTotals) {

                    count += fuelShift.getId();

                }

                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void goToShiftDetail() {

        List<Object> list = new ArrayList<>();

        fuelShiftParam.setBeginDate(beginDate);
        fuelShiftParam.setEndDate(endDate);
        fuelShiftParam.setIsCheck(isCheck);
        fuelShiftParam.setIsCheck2(isCheck2);

        list.add(fuelShiftParam);
        list.add(selectedShift);

        marwiz.goToPage("/pages/automation/report/fuelshiftreport/fuelshiftsalereport.xhtml", list, 0, 150);
    }

    public void goToShiftTransferProcess() {
        if (getRendered(53, 0)) {
            List<Object> list = new ArrayList<>();

            fuelShiftParam.setBeginDate(beginDate);
            fuelShiftParam.setEndDate(endDate);
            fuelShiftParam.setIsCheck(isCheck);
            fuelShiftParam.setIsCheck2(isCheck2);

            list.add(fuelShiftParam);
            list.add(selectedShift);
            marwiz.goToPage("/pages/automation/fuelshift/fuelshifttransferprocesses.xhtml", list, 0, 108);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("youarenotallowedforthisprocess")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    @Override
    public void save() {

        resultFuelShift = fuelShiftService.insertShiftAndShiftSales(rowsSales);

        if (resultFuelShift.getId() == -1) {

            RequestContext context = RequestContext.getCurrentInstance();
            try {
                convertJsonToObject(2);
            } catch (ParseException ex) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            context.execute("PF('shiftPreviewPF').hide();");
            context.execute("PF('dlg_shiftfileupload').hide();");
            context.update("dlgShiftAvailable");
            context.execute("PF('dlg_ShiftAvailable').show();");
        } else if (resultFuelShift.getId() == -2) {
            isFailure = true;
            RequestContext context = RequestContext.getCurrentInstance();
            try {
                convertJsonToObject(1);
            } catch (ParseException ex) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            context.update("formDialog");

        }  else {
            if (resultFuelShift.getId() > 0) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('shiftPreviewPF').hide();");
                context.execute("PF('dlg_shiftfileupload').hide();");
                context.update("frmFuelShift");

            }

            sessionBean.createUpdateMessage(resultFuelShift.getId());
        }

    }

    /**
     * Bu metot Turpak için xml dosyadan akaryakıt vardiyası yükler
     *
     * @param event
     */
    public void importShiftFromXml() {
        selectedObject = new FuelShift();
        rowsSales.clear();

        if (uploadedFile.getFileName().toLowerCase().contains(".zip")) {//.zip dosyası olarak vardiya sisteme ekleme

            try {
                File destFile = new File(uploadedFile.getFileName());
                FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

                ZipFile zipFile = new ZipFile(destFile);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().toLowerCase().contains(".xml")) {
                        String temp = "";
                        if (entry.getName().contains("/")) {
                            temp = entry.getName().split("/")[1];
                        } else {
                            temp = entry.getName();
                        }
                        String shiftNo = temp.split("\\.")[0].substring(0, 8)
                                + "/"
                                + Integer.parseInt(temp.split("\\.")[0].substring(8, 10));
                        try {
                            InputStream stream = zipFile.getInputStream(entry);
                            rowsSales.addAll(fuelShiftService.importFuelShiftFromXml(stream, shiftNo, branchSetting));
                        } catch (IOException ex) {
                            Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                findStockAttendantName(rowsSales);
            } catch (IOException ex) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (uploadedFile.getFileName().toLowerCase().contains(".rar")) {//.rar dosyası olarak vardiya sisteme ekleme

            try {
                File destFile = new File(uploadedFile.getFileName());
                FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

                Archive archive = new Archive(destFile);
                archive.getMainHeader().print();
                FileHeader fh = archive.nextFileHeader();
                while (fh != null) {
                    File fileEntry = new File(fh.getFileNameString().trim());
                    if (fileEntry.getName().toLowerCase().contains(".xml")) {
                        String shiftNo = fileEntry.getName().split("\\.")[0].substring(0, 8)
                                + "/"
                                + Integer.parseInt(fileEntry.getName().split("\\.")[0].substring(8, 10));
                        InputStream stream = archive.getInputStream(fh);
                        rowsSales.addAll(fuelShiftService.importFuelShiftFromXml(stream, shiftNo, branchSetting));

                    }
                    fh = archive.nextFileHeader();
                }
                findStockAttendantName(rowsSales);
            } catch (IOException ex) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RarException ex) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {//Normal tek xml ekleme kısmı
            String name = uploadedFile.getFileName();

            String shiftNo = name.split("\\.")[0].substring(0, 8)
                    + "/"
                    + Integer.parseInt(name.split("\\.")[0].substring(8, 10));

            selectedObject.setShiftNo(shiftNo);
            try {
                rowsSales = fuelShiftService.importFuelShiftFromXml(uploadedFile.getInputstream(), shiftNo, branchSetting);
                findStockAttendantName(rowsSales);
            } catch (IOException ex) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        title = sessionBean.getLoc().getString("fuelshiftreport") + " " + (selectedObject.getShiftNo() != null ? selectedObject.getShiftNo() : "");
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgPreviewDtb");
        context.execute("PF('shiftPreviewPF').show();");
        context.update("formDialog");
    }

    /**
     * Txt Dosyasındaki değerler ile ön izleme ekranı oluşturuluyor.
     */
    public void importShiftFromTxt() {
        selectedObject = new FuelShift();
        String name = uploadedFile.getFileName();

        String whichShift = "";
        if (name.split("\\.")[1].substring(2).charAt(0) - 64 < 10) {
            whichShift = String.valueOf(name.split("\\.")[1].substring(2).charAt(0) - 64);
        } else {
            whichShift = "" + (char) (name.split("\\.")[1].substring(2).charAt(0) - 9);
        }

        String shiftNo = name.split("\\.")[0].replace("RS", "").substring(4, 8)
                + name.split("\\.")[0].replace("RS", "").substring(2, 4)
                + name.split("\\.")[0].replace("RS", "").substring(0, 2)
                + "/"
                + whichShift;

        selectedObject.setShiftNo(shiftNo);

        try {
            rowsSales = fuelShiftService.importFuelShiftFromTxt(uploadedFile.getInputstream(), shiftNo);
            findStockAttendantName(rowsSales);
            RequestContext context = RequestContext.getCurrentInstance();
            title = sessionBean.getLoc().getString("fuelshiftreport") + " " + (selectedObject.getShiftNo() != null ? selectedObject.getShiftNo() : "");
            context.update("dlgPreviewDtb");
            context.execute("PF('shiftPreviewPF').show();");
            context.update("formDialog");

        } catch (IOException ex) {
            Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Asisle Entegrasyonu Sağlar
    public void importShiftFromAsis() {
        selectedObject = new FuelShift();
        String name = uploadedFile.getFileName();

        String whichShift = "";
        if (name.split("\\.")[1].substring(2).charAt(0) - 96 < 10) {
            whichShift = String.valueOf(name.split("\\.")[1].substring(2).charAt(0) - 96);
        } else {
            whichShift = "" + (char) (name.split("\\.")[1].substring(2).charAt(0) - 9);
        }
        String shiftNo = name.split("\\.")[0].substring(0, 8)
                + "/"
                + whichShift;
        selectedObject.setShiftNo(shiftNo);

        try {
            rowsSales = fuelShiftService.importFuelShiftFromAsis(uploadedFile.getInputstream(), shiftNo);
            findStockAttendantName(rowsSales);
            RequestContext context = RequestContext.getCurrentInstance();
            title = sessionBean.getLoc().getString("fuelshiftreport") + " " + (selectedObject.getShiftNo() != null ? selectedObject.getShiftNo() : "");
            context.update("dlgPreviewDtb");
            context.execute("PF('shiftPreviewPF').show();");
            context.update("formDialog");

        } catch (IOException ex) {
            Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //Eski Stawizle Entegrasyonu Sağlar
    public void importShiftFromStawiz() {
        selectedObject = new FuelShift();
        String name = uploadedFile.getFileName();

        String whichShift = "";
        if (name.split("\\.")[1].substring(2).charAt(0) - 64 < 10) {
            whichShift = String.valueOf(name.split("\\.")[1].substring(2).charAt(0) - 64);
        } else {
            whichShift = "" + (char) (name.split("\\.")[1].substring(2).charAt(0) - 9);
        }

        String shiftNo = name.split("\\.")[0].replace("RS", "").substring(4, 8)
                + name.split("\\.")[0].replace("RS", "").substring(2, 4)
                + name.split("\\.")[0].replace("RS", "").substring(0, 2)
                + "/"
                + whichShift;

        selectedObject.setShiftNo(shiftNo);

        try {
            rowsSales = fuelShiftService.importFuelShiftFromTxtForStawiz(uploadedFile.getInputstream(), shiftNo);
            findStockAttendantName(rowsSales);
            RequestContext context = RequestContext.getCurrentInstance();
            title = sessionBean.getLoc().getString("fuelshiftreport") + " " + (selectedObject.getShiftNo() != null ? selectedObject.getShiftNo() : "");
            context.update("dlgPreviewDtb");
            context.execute("PF('shiftPreviewPF').show();");
            context.update("formDialog");

        } catch (IOException ex) {
            Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Bu metod Txt Dosyası Yükle butonuna basılınca açılır.
     *
     * @param event
     */
    public void handleFileUploadFile(FileUploadEvent event) {
        uploadedFile = event.getFile();
    }

    /**
     * Bu metot Dosya yüklemek istenildiğinde tekrar dosya yükle butonuna
     * tıklanıldığında verileri sıfırlar.
     */
    public void clearData() {
        uploadedFile = null;

    }

    @Override
    public void generalFilter() {

        String where = " ";
        if (isCheck2) {
            where = " AND shf.is_confirm= FALSE ";
        }
        where = where + createWhere;
        if (autoCompleteValue == null) {
            listOfObjects = findall(where);
        } else {
            gfFuelShiftService.makeSearch(where, isCheck, autoCompleteValue);
            listOfObjects = gfFuelShiftService.searchResult;
            where = where + gfFuelShiftService.createWhere(autoCompleteValue);

            listOfTotals = gfFuelShiftService.callDaoCount(where, isCheck);
        }

        RequestContext.getCurrentInstance().update("frmFuelShift:dtbFuelShift");

    }

    public String readXml(String shiftAttendant) {
        String attendant = "";
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(shiftAttendant));
            src.setEncoding("UTF-8");
            org.w3c.dom.Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();
            int count = 0;
            NodeList list = doc.getElementsByTagName("attendantsaleshift");
            if (list.getLength() == 0) {
                attendant = "";
            }
            for (int s = 0; s < list.getLength(); s++) {
                NodeList elements = list.item(s).getChildNodes();
                if (count == 0) {
                    attendant += elements.item(0).getTextContent() + " " + elements.item(1).getTextContent();
                } else {
                    attendant += " , " + elements.item(0).getTextContent() + " " + elements.item(1).getTextContent();
                }
                count = 1;

            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return attendant;

    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void convertJsonToObject(int type) throws ParseException {
        rowsSales.clear();
        JSONArray jsonArr = new JSONArray(resultFuelShift.getIncorrectRecord());

        if (type == 1) {//Entegrasyon olmayanları döndürür
            for (int m = 0; m < jsonArr.length(); m++) {
                FuelShiftSales item = new FuelShiftSales();

                item.getFuelShift().setShiftNo(jsonArr.getJSONObject(m).getString("shiftno"));
                item.setProcessDateString(jsonArr.getJSONObject(m).getString("processdate"));
                item.setPumpno(jsonArr.getJSONObject(m).getString("pumpno"));
                item.setNozzleNo(jsonArr.getJSONObject(m).getString("nozzleno"));
                item.setStockName(jsonArr.getJSONObject(m).optString("stockname"));//Sistemde yoksa null geldiğinde patlıyordu o yüzden optString methodu kullanıldı.
                item.setLiter(jsonArr.getJSONObject(m).getBigDecimal("liter"));
                item.setPrice(jsonArr.getJSONObject(m).getBigDecimal("price"));
                item.setDiscountTotal(jsonArr.getJSONObject(m).getBigDecimal("discounttotal"));
                item.setTotalMoney(jsonArr.getJSONObject(m).getBigDecimal("totalmoney"));
                item.setPlate(jsonArr.getJSONObject(m).getString("plate"));
                item.setAttendant(jsonArr.getJSONObject(m).getString("attendant"));

                item.setSalteType(jsonArr.getJSONObject(m).getInt("saletype"));
                item.setFuelCardType(jsonArr.getJSONObject(m).getInt("cardtype"));
                item.setPaymentType(jsonArr.getJSONObject(m).getInt("paymenttype"));
                item.setAttendantCode(jsonArr.getJSONObject(m).getString("attendantcode"));
                item.setStockCode(jsonArr.getJSONObject(m).getString("stockcode"));
                item.setAccountCode(jsonArr.getJSONObject(m).getString("accountcode"));
                item.setIsErrorAttendant(jsonArr.getJSONObject(m).getBoolean("is_attendant"));
                item.setIsErrorStock(jsonArr.getJSONObject(m).getBoolean("is_stock"));
                item.setIsErrorAccount(jsonArr.getJSONObject(m).getBoolean("is_account"));
                item.setIsErrorNozzle(jsonArr.getJSONObject(m).getBoolean("is_nozzle"));
                item.setIsSaleType(jsonArr.getJSONObject(m).getBoolean("is_saletype"));
                item.setIsCardType(jsonArr.getJSONObject(m).getBoolean("is_cardtype"));

                rowsSales.add(item);
            }
        } else {//Kayıtlı olan vardiyaları 
            for (int m = 0; m < jsonArr.length(); m++) {
                FuelShiftSales item = new FuelShiftSales();

                item.getFuelShift().setShiftNo(jsonArr.getJSONObject(m).getString("shiftno"));

                rowsSales.add(item);
            }
        }
    }

    public BigDecimal differenceTotal(BigDecimal sale, BigDecimal payment) {
        BigDecimal diff = BigDecimal.valueOf(0);
        if (sale == null) {
            sale = BigDecimal.valueOf(0);
        }
        if (payment == null) {
            payment = BigDecimal.valueOf(0);
        }
        diff = sale.subtract(payment);

        return diff;
    }

    public int compareTotal(BigDecimal sale, BigDecimal payment) {

        if (sale == null) {
            sale = BigDecimal.valueOf(0);
        }
        if (payment == null) {
            payment = BigDecimal.valueOf(0);
        }
        switch (sale.compareTo(payment)) {
            case 1:
                return 1;
            case -1:
                return -1;
            default:
                return 0;
        }

    }

    public void showList() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmFuelShift:dtbFuelShift");
        dataTable.setFirst(0);
        generalFilter();
        RequestContext.getCurrentInstance().update("frmFuelShift:dtbFuelShift");
    }

    public void showList2() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmFuelShift:dtbFuelShift");
        dataTable.setFirst(0);
        generalFilter();
        RequestContext.getCurrentInstance().update("frmFuelShift:dtbFuelShift");
    }

    public void findStockAttendantName(List<FuelShiftSales> sales) {
        if (branchSetting.getAutomationId() == 4) {
            stockNameList = fuelShiftService.jsonArrayIntegrationName(sales, 1);
        } else {
            stockNameList = fuelShiftService.jsonArrayIntegrationName(sales, 0);
        }

        if (stockNameList != null && !stockNameList.equals("[]") && !stockNameList.equals("")) {
            JSONArray jsonArr = new JSONArray(stockNameList);

            for (FuelShiftSales s : sales) {
                for (int m = 0; m < jsonArr.length(); m++) {
                    if (branchSetting.getAutomationId() == 4) {
                        if (s.getPumpno().equals(jsonArr.getJSONObject(m).getString("pumpno")) && s.getNozzleNo().equals(jsonArr.getJSONObject(m).getString("nozzleno"))) {
                            s.setStockName(jsonArr.getJSONObject(m).getString("stock_name") != null ? jsonArr.getJSONObject(m).getString("stock_name") : "");
                            s.setStockCode(jsonArr.getJSONObject(m).getString("fuel_integration_code") != null ? jsonArr.getJSONObject(m).getString("fuel_integration_code") : "");
                        }
                        if (s.getAttendantCode().equals(jsonArr.getJSONObject(m).getString("attendant_code"))) {
                            s.setAttendant(jsonArr.getJSONObject(m).getString("attendant_name") != null ? jsonArr.getJSONObject(m).getString("attendant_name") : "");
                        }
                    } else {
                        if (s.getStockCode().equals(jsonArr.getJSONObject(m).getString("fuel_integration_code"))) {
                            s.setStockName(jsonArr.getJSONObject(m).getString("stock_name") != null ? jsonArr.getJSONObject(m).getString("stock_name") : "");
                        }
                        if (s.getAttendantCode().equals(jsonArr.getJSONObject(m).getString("attendant_code"))) {
                            s.setAttendant(jsonArr.getJSONObject(m).getString("attendant_name") != null ? jsonArr.getJSONObject(m).getString("attendant_name") : "");
                        }
                    }

                }
            }
        }

    }

    public void bringShift() {
        checkAutomationItemService.listAutomationShift(branchSettingService.findAutomationSetting(sessionBean.getUser().getLastBranch()));
        fuelShiftTransferService.reSendErrorShift();
        RequestContext.getCurrentInstance().update("frmFuelShift:dtbFuelShift");

    }

    public void showNonTransferableShift() throws ParseException {
        isFailure = true;
        List<FuelShift> listOfNonTransferShift = new ArrayList<>();
        rowsSales.clear();

        listOfNonTransferShift = fuelShiftTransferService.nonTransferableShift();
        for (FuelShift fuelShift : listOfNonTransferShift) {
            JSONArray jsonArr = new JSONArray(fuelShift.getIncorrectRecord());
            for (int m = 0; m < jsonArr.length(); m++) {
                FuelShiftSales item = new FuelShiftSales();

                item.getFuelShift().setShiftNo(jsonArr.getJSONObject(m).getString("shiftno"));
                item.setProcessDateString(jsonArr.getJSONObject(m).getString("processdate"));
                item.setPumpno(jsonArr.getJSONObject(m).getString("pumpno"));
                item.setNozzleNo(jsonArr.getJSONObject(m).getString("nozzleno"));
                item.setStockName(jsonArr.getJSONObject(m).optString("stockname"));//Sistemde yoksa null geldiğinde patlıyordu o yüzden optString methodu kullanıldı.
                item.setLiter(jsonArr.getJSONObject(m).getBigDecimal("liter"));
                item.setPrice(jsonArr.getJSONObject(m).getBigDecimal("price"));
                item.setDiscountTotal(jsonArr.getJSONObject(m).getBigDecimal("discounttotal"));
                item.setTotalMoney(jsonArr.getJSONObject(m).getBigDecimal("totalmoney"));
                item.setPlate(jsonArr.getJSONObject(m).getString("plate"));
                item.setAttendant(jsonArr.getJSONObject(m).getString("attendant"));

                item.setSalteType(jsonArr.getJSONObject(m).getInt("saletype"));
                item.setPaymentType(jsonArr.getJSONObject(m).getInt("paymenttype"));
                item.setAttendantCode(jsonArr.getJSONObject(m).getString("attendantcode"));
                item.setStockCode(jsonArr.getJSONObject(m).getString("stockcode"));
                item.setAccountCode(jsonArr.getJSONObject(m).getString("accountcode"));
                item.setIsErrorAttendant(jsonArr.getJSONObject(m).getBoolean("is_attendant"));
                item.setIsErrorStock(jsonArr.getJSONObject(m).getBoolean("is_stock"));
                item.setIsErrorAccount(jsonArr.getJSONObject(m).getBoolean("is_account"));
                item.setIsErrorNozzle(jsonArr.getJSONObject(m).getBoolean("is_nozzle"));
                item.setIsSaleType(jsonArr.getJSONObject(m).getBoolean("is_saletype"));

                rowsSales.add(item);
            }
        }

        findStockAttendantName(rowsSales);
        RequestContext context = RequestContext.getCurrentInstance();
        title = sessionBean.getLoc().getString("nontransferableshifts");
        context.update("formDialog");
        context.execute("PF('shiftPreviewPF').show();");

    }

    public void readFileNameForTurpak() throws Exception {

        String urlString = branchSetting.getAutomationUrl();
        String username = branchSetting.getAutomationUserName();
        String password = branchSetting.getAutomationPassword();
        SmbFile smbFile = null;
        String url = "smb://" + urlString + "/";
        System.out.println("url++++" + url);
        listOfSamba.clear();
        selectedSambaFile = null;
        if (username != null && password != null) {

            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, username, password);
            smbFile = new SmbFile(url, auth);
            List<FuelShiftControlFile> uploadedFileList = new ArrayList<>();
            List<FuelShiftControlFile> shiftControlList = new ArrayList<>();

            try {
                SmbFile files[] = smbFile.listFiles();

                for (SmbFile file : files) {
                    if (file.getName().toLowerCase().contains(".zip")) {
                        String temp = "";
                        String s = "";
                        FuelShiftControlFile controlFile = new FuelShiftControlFile();
                        if (file.getName().contains("/")) {
                            temp = file.getName().split("/")[1];
                        } else {
                            temp = file.getName();
                        }
                        if (temp.split("\\.")[0].length() >= 10) {
                            s = temp.split("\\.")[0].substring(0, 8)
                                    + "/"
                                    + Integer.parseInt(temp.split("\\.")[0].substring(8, 10));
                            if (s != null) {
                                controlFile.setShiftNo(s);
                                controlFile.setFileName(file.getName());
                                shiftControlList.add(controlFile);
                            }
                        }
                    }

                }
                String shifts = null;
                shifts = fuelShiftTransferService.jsonArrayShiftControl(shiftControlList);
                if (shifts != null && !shifts.isEmpty()) {
                    uploadedFileList = fuelShiftTransferService.controlShiftNo(shifts);
                }

                for (FuelShiftControlFile controlFile : uploadedFileList) {
                    for (SmbFile file : files) {
                        if (controlFile.getFileName().equals(file.getName())) {
                            listOfSamba.add(file);
                            break;
                        }
                    }
                }
            } catch (SmbException e) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        RequestContext.getCurrentInstance().update("dlgShiftListSamba");
        RequestContext.getCurrentInstance().execute("PF('dlg_ShiftListSamba').show();");

    }

    public void uploadSambaFile() throws IOException {
        rowsSales.clear();
        InputStream inputStream = null;
        inputStream = selectedSambaFile.getInputStream();

        File destFile = new File(selectedSambaFile.getName());
        FileUtils.copyInputStreamToFile(inputStream, destFile);

        if (branchSetting.getAutomationId() == 2) {
            String shiftNo = "";
            try {
                ZipFile zipFile = new ZipFile(destFile);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().toLowerCase().contains(".xml")) {
                        String temp = "";
                        if (entry.getName().contains("/")) {
                            temp = entry.getName().split("/")[1];
                        } else {
                            temp = entry.getName();
                        }
                        shiftNo = temp.split("\\.")[0].substring(0, 8)
                                + "/"
                                + Integer.parseInt(temp.split("\\.")[0].substring(8, 10));
                        try {
                            InputStream stream = zipFile.getInputStream(entry);
                            rowsSales.addAll(fuelShiftService.importFuelShiftFromXml(stream, shiftNo, branchSetting));
                        } catch (IOException ex) {
                            Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                findStockAttendantName(rowsSales);
            } catch (IOException ex) {
                Logger.getLogger(FuelShiftBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            RequestContext.getCurrentInstance().execute("PF('dlg_ShiftListSamba').hide();");
            title = sessionBean.getLoc().getString("fuelshiftreport") + " " + (shiftNo != null ? shiftNo : "");
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("dlgPreviewDtb");
            context.execute("PF('shiftPreviewPF').show();");
            context.update("formDialog");
        }

    }
}
