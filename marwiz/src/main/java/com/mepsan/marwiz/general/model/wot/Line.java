/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 08.04.2019 17:25:37
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.Date;

public class Line {

    private final char[] lineChars;

    public Line(int size) {
        lineChars = new char[size];
        for (int i = 0; i < size; i++) {
            lineChars[i] = ' ';
        }
    }

    public void addDate(int start, int total, Date date) {
        String dateString = LineFormatter.getInstance().getDateFormat().format(date);
        addText(start, total, dateString);
    }

    public void addDouble(int start, int total, double value) {
        String dateString = LineFormatter.getInstance().getDecimalFormat().format(value);
        addText(start, total, dateString);
    }

    public void addText(int start, int total, String string) {
        if (string.length() > total) {
            throw new Error("Text longer than total size");
        }
        int index = start - 1;
        char[] stringChars = string.toCharArray();
        for (char stringChar : stringChars) {
            lineChars[index] = stringChar;
            index++;
        }
    }

    public String getString() {
        String string = new String(lineChars);
        return string;
    }

}
