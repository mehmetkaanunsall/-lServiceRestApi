/**
 * Bu interface ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   28.09.2016 14:41:36
 */
package com.mepsan.marwiz.general.ftpConnection.business;

import java.io.InputStream;

public interface IFtpConnectionService {


    public Boolean uploadImage(String name, InputStream inputStream, String folder, String extension);

    public Boolean deleteFile(String url);

    public boolean upload(String ftpUrl, InputStream inputStream);

    public boolean exists(String URLName);

    public boolean validateFile(String mimetype, String type);

    public String getBaseTypeOfFile(InputStream inputStream);

}
