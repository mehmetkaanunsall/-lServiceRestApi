/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.08.2020 02:41:29
 */
package com.mepsan.marwiz.general.printer.business;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Printer;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IPrinterService extends ICrudService<Printer> {

    public Printer listOfPrinterAccordingToType(int type, Branch branch);

    public List<Printer> listOfPrinter();

    public int delete(Printer obj);

    public String sendPrinterDevice(String json, Printer printer, int type);
}
