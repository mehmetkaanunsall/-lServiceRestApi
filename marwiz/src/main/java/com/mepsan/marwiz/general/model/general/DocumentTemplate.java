/**
 * This class ...
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date   07.03.2018 03:44:46
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;

public class DocumentTemplate extends WotLogging {

    private int id;
    private String name;
    private boolean isDefault;
    private Type type;
    private int paperSize;
    private BigDecimal width;
    private BigDecimal height;
    private boolean isVertical;
    private String json;
    private boolean isUseTemplate;
    private int margin_top = 10;
    private int margin_bottom = 10;
    private int margin_left = 10 ;
    private int margin_right = 10;

    public DocumentTemplate() {
        this.type = new Type();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(int paperSize) {
        this.paperSize = paperSize;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public boolean isIsVertical() {
        return isVertical;
    }

    public void setIsVertical(boolean isVertical) {
        this.isVertical = isVertical;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public boolean isIsUseTemplate() {
        return isUseTemplate;
    }

    public void setIsUseTemplate(boolean isUseTemplate) {
        this.isUseTemplate = isUseTemplate;
    }

    public int getMargin_top() {
        return margin_top;
    }

    public void setMargin_top(int margin_top) {
        this.margin_top = margin_top;
    }

    public int getMargin_bottom() {
        return margin_bottom;
    }

    public void setMargin_bottom(int margin_bottom) {
        this.margin_bottom = margin_bottom;
    }

    public int getMargin_left() {
        return margin_left;
    }

    public void setMargin_left(int margin_left) {
        this.margin_left = margin_left;
    }

    public int getMargin_right() {
        return margin_right;
    }

    public void setMargin_right(int margin_right) {
        this.margin_right = margin_right;
    }

    
    
    @Override
    public String toString() {
        return this.getJson();
    }

    @Override
    public int hashCode() {
        return this.getId();

    }
    
    
}
