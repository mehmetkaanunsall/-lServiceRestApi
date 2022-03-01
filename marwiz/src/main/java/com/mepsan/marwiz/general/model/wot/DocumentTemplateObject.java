/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Feb 26, 2018 3:30:30 PM
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.List;

public class DocumentTemplateObject {

    private int id;
    private int fontSize;
    private List<String> fontStyle;
    private String fontAlign;
    private double left;
    private double top;
    private double width;
    private double height;
    private String name;
    private String keyWord;
    private boolean label;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public List<String> getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(List<String> fontStyle) {
        this.fontStyle = fontStyle;
    }


    public String getFontAlign() {
        return fontAlign;
    }

    public void setFontAlign(String fontAlign) {
        this.fontAlign = fontAlign;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getName() { 
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public boolean isLabel() {
        return label;
    }

    public void setLabel(boolean label) {
        this.label = label;
    }

}
