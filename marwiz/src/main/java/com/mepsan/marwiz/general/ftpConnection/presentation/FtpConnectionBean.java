/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   28.09.2016 14:40:02
 */
package com.mepsan.marwiz.general.ftpConnection.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.ftpConnection.business.IFtpConnectionService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class FtpConnectionBean {

    @ManagedProperty(value = "#{ftpConnectionService}")
    private IFtpConnectionService ftpConnectionService;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    private String folder;
    private String newName;
    private String path;
    private String otherNewName;

    public String getPath() {
        return path;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFtpConnectionService(IFtpConnectionService ftpConnectionService) {
        this.ftpConnectionService = ftpConnectionService;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
    
    public String getOtherNewName() {
        return otherNewName;
    }

    public void setOtherNewName(String otherNewName) {
        this.otherNewName = otherNewName;
    }

    public void handleFileUploadImage(FileUploadEvent event) throws IOException {
        System.out.println("upload ");
        if (ftpConnectionService.validateFile(ftpConnectionService.getBaseTypeOfFile(event.getFile().getInputstream()), "img")) {
            System.out.println("*-*-*-*-*-*- " + path);
            try {
                if (ftpConnectionService.uploadImage(newName, event.getFile().getInputstream(), folder, "png")) {
                    path = "../upload/" + folder + "/" + newName + "." + "png" + "?" + new Date().getTime();
                    System.out.println("--- " + path);
                }
            } catch (IOException ex) {
                Logger.getLogger(FtpConnectionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FacesMessage message = new FacesMessage();

            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            message.setSummary(sessionBean.loc.getString("error"));
            message.setDetail(sessionBean.loc.getString("invalidfiletype") );

            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("grwProcessMessage");

        }
    }

    public boolean exists(String name, String folderName) {
        if (ftpConnectionService.exists(applicationBean.getParameterMap().get("ftp_path").getValue() + "/" + folderName + "/" + name + "." + "png")) {
            return true;
        }
        return false;
    }

    public void initializeImage(String folder, String newName) {
        this.folder = folder;
        this.newName = newName;

        if (!folder.equals("stock") || !sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            path = "../upload/" + folder + "/" + newName + "." + "png";
            System.out.println("path ---  :  " + applicationBean.getParameterMap().get("ftp_path").getValue() + "/" + folder + "/" + newName + "." + "png");
            if (!ftpConnectionService.exists(applicationBean.getParameterMap().get("ftp_path").getValue() + "/" + folder + "/" + newName + "." + "png")) {
                path = "/resources/primefaces-sentinel/images/add_photo.png";
            }
        }

    }

    public void showImageStock(String folder, String newName) {
        String wsEndPoint = "";
        if (ftpConnectionService.exists(applicationBean.getParameterMap().get("ftp_path").getValue() + "/" + folder + "/" + newName + "." + "png")) {
            path = "../upload/" + folder + "/" + newName + "." + "png";
        } else {
            newName = getMD5EncryptedValue(newName);
            if (sessionBean.getUser().getLastBranchSetting().getwSendPoint() != null && !sessionBean.getUser().getLastBranchSetting().getwSendPoint().equals("") && sessionBean.getUser().getLastBranchSetting().getwSendPoint().indexOf("Stawiz") > 0) {
                wsEndPoint = sessionBean.getUser().getLastBranchSetting().getwSendPoint().substring(0, sessionBean.getUser().getLastBranchSetting().getwSendPoint().indexOf("Stawiz"));
            }
            path = wsEndPoint + "document/" + folder + "/" + newName + "." + "png";
            if (newName.equals("0")) {
                path = "/resources/primefaces-sentinel/images/add_photo.png";
            }
        }
    }

    public void deleteImg() {
        System.out.println("delete Image");
        if (ftpConnectionService.deleteFile(applicationBean.getParameterMap().get("ftp_path").getValue() + "/" + folder + "/" + newName + "." + "png")) {
            path = "/resources/primefaces-sentinel/images/add_photo.png";
            if (folder.equals("stock") && sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                if (!otherNewName.equals("0")) {
                    String wsEndPoint = "";
                    if (sessionBean.getUser().getLastBranchSetting().getwSendPoint() != null && !sessionBean.getUser().getLastBranchSetting().getwSendPoint().equals("") && sessionBean.getUser().getLastBranchSetting().getwSendPoint().indexOf("Stawiz") > 0) {
                        wsEndPoint = sessionBean.getUser().getLastBranchSetting().getwSendPoint().substring(0, sessionBean.getUser().getLastBranchSetting().getwSendPoint().indexOf("Stawiz"));
                    }
                    path = wsEndPoint + "document/" + folder + "/" + getMD5EncryptedValue(otherNewName) + "." + "png";
                    if (getMD5EncryptedValue(otherNewName).equals("0")) {
                        path = "/resources/primefaces-sentinel/images/add_photo.png";
                    }
                }

            }
        }
    }

    public String getMD5EncryptedValue(String password) {
        final byte[] defaultBytes = password.getBytes();
        try {
            final MessageDigest md5MsgDigest = MessageDigest.getInstance("MD5");
            md5MsgDigest.reset();
            md5MsgDigest.update(defaultBytes);
            final byte messageDigest[] = md5MsgDigest.digest();
            final StringBuffer hexString = new StringBuffer();
            for (final byte element : messageDigest) {
                final String hex = Integer.toHexString(0xFF & element);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            password = hexString + "";
        } catch (final NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return password;
    }

}
