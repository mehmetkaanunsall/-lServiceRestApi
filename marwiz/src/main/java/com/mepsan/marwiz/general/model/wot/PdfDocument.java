/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * May 3, 2018 12:25:03 PM
 */
package com.mepsan.marwiz.general.model.wot;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.io.ByteArrayOutputStream;

public class PdfDocument {

    private Document document;
    private Font font;
    private Font fontColumnTitle;
    private Font fontHeader;
    private PdfPTable pdfTable;
    private PdfPCell header;//Raporun adının yazdığı başlık için
    private PdfPCell tableHeader;//kolon başlıkları için
    private PdfPCell cell;//Üst Bilgi verileri için
    private PdfPCell rightCell;//Double veriler için
    private ByteArrayOutputStream baos;
    private PdfPCell dataCell;//Satırlardaki veriler için

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Font getFontHeader() {
        return fontHeader;
    }

    public void setFontHeader(Font fontHeader) {
        this.fontHeader = fontHeader;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public PdfPTable getPdfTable() {
        return pdfTable;
    }

    public void setPdfTable(PdfPTable pdfTable) {
        this.pdfTable = pdfTable;
    }

    public PdfPCell getHeader() {
        return header;
    }

    public void setHeader(PdfPCell header) {
        this.header = header;
    }

    public PdfPCell getCell() {
        return cell;
    }

    public void setCell(PdfPCell cell) {
        this.cell = cell;
    }

    public ByteArrayOutputStream getBaos() {
        return baos;
    }

    public void setBaos(ByteArrayOutputStream baos) {
        this.baos = baos;
    }

    public Font getFontColumnTitle() {
        return fontColumnTitle;
    }

    public void setFontColumnTitle(Font fontColumnTitle) {
        this.fontColumnTitle = fontColumnTitle;
    }

    public PdfPCell getRightCell() {
        return rightCell;
    }

    public void setRightCell(PdfPCell rightCell) {
        this.rightCell = rightCell;
    }

    public PdfPCell getDataCell() {
        return dataCell;
    }

    public void setDataCell(PdfPCell dataCell) {
        this.dataCell = dataCell;
    }

    public PdfPCell getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(PdfPCell tableHeader) {
        this.tableHeader = tableHeader;
    }

}
