/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 09.04.2019 08:50:22
 */
package com.mepsan.marwiz.general.model.wot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

public class LineFormatter {

    private final DecimalFormat decimalFormat;
    private final SimpleDateFormat dateFormat;
    private static LineFormatter formatter;

    private LineFormatter() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(',');
        decimalFormat = new DecimalFormat("#.####", decimalFormatSymbols);
        decimalFormat.setGroupingUsed(false);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public static LineFormatter getInstance() {
        if (formatter == null) {
            formatter = new LineFormatter();
        }
        return formatter;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

}
