/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.08.2020 02:20:45
 */
package com.mepsan.marwiz.general.printer.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Printer;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IPrinterDao extends ICrud<Printer> {
    
    public Printer listOfPrinterAccordingToType(int type, Branch branch);

    public List<Printer> listOfPrinter();

    public int delete(Printer obj);

}
