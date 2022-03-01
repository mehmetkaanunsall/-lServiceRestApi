/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 10:45:36
 */
package com.mepsan.marwiz.system.filetransfer.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.wot.Document;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.system.filetransfer.business.IFileTranferService;
import com.mepsan.marwiz.system.filetransfer.dao.FileTransfer;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

@ManagedBean
@ViewScoped
public class FileTransferBean extends GeneralReportBean<FileTransfer> {

    @ManagedProperty(value = "#{fileTranferService}")
    private IFileTranferService fileTranferService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marketShiftService}")
    public IMarketShiftService marketShiftService;

    public void setFileTranferService(IFileTranferService fileTranferService) {
        this.fileTranferService = fileTranferService;
    }

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    private int transferType;
    private int fileExtention;  
    private int reportType;
    private Document document;
    private List<Shift> listOfShift;
    private String fileNameFormat;
    private List<String> listOfFileNameFormat;

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public List<Shift> getListOfShift() {
        return listOfShift;
    }

    public void setListOfShift(List<Shift> listOfShift) {
        this.listOfShift = listOfShift;
    }

       public int getFileExtention() {
        return fileExtention;
    }  

    public void setFileExtention(int fileExtention) {
        this.fileExtention = fileExtention;
    }

    public String getFileNameFormat() {
        return fileNameFormat;
    }

    public void setFileNameFormat(String fileNameFormat) {
        this.fileNameFormat = fileNameFormat;
    }

    public List<String> getListOfFileNameFormat() {
        return listOfFileNameFormat;
    }

    public void setListOfFileNameFormat(List<String> listOfFileNameFormat) {
        this.listOfFileNameFormat = listOfFileNameFormat;
    }

    @PostConstruct
    public void init() {
        transferType = 1;
        reportType = 1;
        selectedObject = new FileTransfer();

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        
        listOfFileNameFormat = new ArrayList<>();
        SimpleDateFormat dateFormatWithTime = new SimpleDateFormat("ddMMyyyHHmmss");
        SimpleDateFormat dateFormatWihoutTime = new SimpleDateFormat("ddMMyyy");
        Date date = new Date();
        listOfFileNameFormat.add(dateFormatWithTime.format(date));
        listOfFileNameFormat.add(dateFormatWihoutTime.format(date));
        
        listOfShift = fileTranferService.listOfShift("");

    }

    @Override
    public void find() {
        document = fileTranferService.listOfSale(selectedObject,reportType,fileExtention);
        if (document != null) {
            RequestContext.getCurrentInstance().execute("downloadfile();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("filecreationerror")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void download() throws IOException {
        byte[] strToBytes = document.getString().getBytes();
       // System.out.println("" + document.getString());
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        
        SimpleDateFormat dateFormat;
        if (fileNameFormat.equals(listOfFileNameFormat.get(0))) {
            dateFormat = new SimpleDateFormat("ddMMyyyHHmmss");
        } else {
            dateFormat = new SimpleDateFormat("ddMMyyy");
        }
        
        Date date = new Date();
        String fileName = dateFormat.format(date) +".GTF"; //(fileExtention == 1 ? ".GTF" : ".GTF");
        ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        ec.setResponseContentType("application/octet-stream"); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
        ec.setResponseContentLength(strToBytes.length); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

        OutputStream output = ec.getResponseOutputStream();

        output.write(strToBytes);
        output.flush();
        output.close();
        fc.responseComplete();

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<FileTransfer> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
