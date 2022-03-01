/**
 * Bu Sınıf ...Download and Upload and delete documents or images
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   28.09.2016 14:40:51
 */
package com.mepsan.marwiz.general.ftpConnection.business;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.ftpConnection.presentation.FtpConnectionBean;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

public class FtpConnectionService implements IFtpConnectionService {

    @Autowired
    private ApplicationBean applicationBean;
    
    @Autowired
    private SessionBean sessionBean;

    private String ftpPath;

    private final List<String> imageMime = Arrays.asList("image/jpeg", "image/png");
    private final List<String> fileMime = Arrays.asList("image/jpeg", "image/png", "image/gif", "video/avi", "audio/mpeg", "video/mpeg", "video/x-flv", "video/x-ms-EXT",
            "video/mp4", "video/quicktime", "video/x-ms-asf", "image/bmp", "image/tiff", "application/octet-stream", "image/pict", "audio/mpeg3", "audio/x-mpeg-3",
            "audio/aac", "audio/x-wav", "audio/x-aiff", "audio/aiff",
            "text/plain", "text/richtext", "application/msword", "application/vnd.ms-excel", "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/zip", "application/x-rar-compressed", "application/pdf", "application/x-tika-msoffice", "application/x-tika-ooxml");

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
    
    

    public void init() {

        ftpPath = applicationBean.getParameterMap().get("ftp_path").getValue();
    }

    @Override
    public Boolean uploadImage(String name, InputStream inputStream, String folder, String extension) {
        System.out.println("inpuyt stream" + inputStream);
        if (inputStream != null) {
            String newUrl = String.format(ftpPath + "/" + folder + "/" + name + "." + extension);
            System.out.println("----- newftpUrl " + newUrl);
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            try {
                Thumbnails.of(inputStream)
                        .size(500, 500)
                        .toOutputStream(arrayOutputStream);
            } catch (Exception ex) {
                Logger.getLogger(FtpConnectionService.class
                        .getName()).log(Level.SEVERE, null, ex);
                 FacesMessage message = new FacesMessage();

                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                message.setSummary(sessionBean.loc.getString("error"));
                message.setDetail(sessionBean.loc.getString("invalidfiletype") );

                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("grwProcessMessage");
                return false;
            }
            return upload(newUrl, arrayOutputStream.toInputStream());
        } else {
            return false;
        }
    }

    @Override
    public Boolean deleteFile(String url) {
        File newfile = new File(url);
        return newfile.delete();
    }

    @Override
    public boolean upload(String url, InputStream inputStream) {
        try {
            File newfile = new File(url);
            Files.copy(inputStream, newfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FtpConnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean exists(String URLName) {
        File f = new File(URLName);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean validateFile(String mimetype, String type) {
        System.out.println(mimetype + " ***** " + type);
        if (type.equals("img")) {
            return imageMime.contains(mimetype);
        } else if (type.equals("file")) {
            return fileMime.contains(mimetype);
        }
        return false;
    }

    @Override
    public String getBaseTypeOfFile(InputStream inputStream) {
        try {
            AutoDetectParser detectParser = new AutoDetectParser();
            Detector detector = detectParser.getDetector();
            Metadata metadata = new Metadata();
            MediaType mediaType = detector.detect(new BufferedInputStream(inputStream), metadata);
            return mediaType.getBaseType().toString();
        } catch (IOException ex) {
            Logger.getLogger(FtpConnectionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
