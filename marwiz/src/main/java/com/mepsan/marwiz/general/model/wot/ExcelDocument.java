/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * May 3, 2018 12:25:03 PM
 */
package com.mepsan.marwiz.general.model.wot;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelDocument {

    private SXSSFRow row;
    private CellStyle dateFormatStyle;
    private CellStyle styleHeader;
    private SXSSFSheet sheet;
    private SXSSFWorkbook workbook;

    public SXSSFRow getRow() {
        return row;
    }

    public void setRow(SXSSFRow row) {
        this.row = row;
    }

    public CellStyle getDateFormatStyle() {
        return dateFormatStyle;
    }

    public void setDateFormatStyle(CellStyle dateFormatStyle) {
        this.dateFormatStyle = dateFormatStyle;
    }

    public SXSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(SXSSFSheet sheet) {
        this.sheet = sheet;
    }

    public SXSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(SXSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public CellStyle getStyleHeader() {
        return styleHeader;
    }

    public void setStyleHeader(CellStyle styleHeader) {
        this.styleHeader = styleHeader;
    }

}
