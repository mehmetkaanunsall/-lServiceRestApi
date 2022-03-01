/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 09:58:27
 */
package com.mepsan.marwiz.system.filetransfer.business;

import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.wot.Document;
import com.mepsan.marwiz.system.filetransfer.dao.FileTransfer;
import java.util.List;

public interface IFileTranferService {

    public Document listOfSale(FileTransfer obj, int reportType,int extentionType);

    public List<Shift> listOfShift(String where);

}
