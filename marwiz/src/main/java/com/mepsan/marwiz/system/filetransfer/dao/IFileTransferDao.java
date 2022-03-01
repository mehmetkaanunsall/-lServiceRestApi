/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 10:16:22
 */
package com.mepsan.marwiz.system.filetransfer.dao;

import com.mepsan.marwiz.general.model.general.Shift;
import java.util.List;

public interface IFileTransferDao {

    public String listOfSale(String where);

    public List<Shift> listOfShift(String where);
}
