/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 12.12.2018 09:07:34
 */
package com.mepsan.marwiz.general.report.removedstockreport.presentation;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.log.RemovedStock;

public class RemovedStockReport {

    private int id;
    private RemovedStock removedStockReport;
    private int january;
    private int february;
    private int march;
    private int april;
    private int may;
    private int june;
    private int july;
    private int august;
    private int september;
    private int october;
    private int november;
    private int december;
    private Branch branch;
   

    public RemovedStockReport() {
        removedStockReport = new RemovedStock();
        this.branch = new Branch();
    }

    public RemovedStock getRemovedStockReport() {
        return removedStockReport;
    }

    public void setRemovedStockReport(RemovedStock removedStockReport) {
        this.removedStockReport = removedStockReport;
    }

    public int getJanuary() {
        return january;
    }

    public void setJanuary(int january) {
        this.january = january;
    }

    public int getFebruary() {
        return february;
    }

    public void setFebruary(int february) {
        this.february = february;
    }

    public int getMarch() {
        return march;
    }

    public void setMarch(int march) {
        this.march = march;
    }

    public int getApril() {
        return april;
    }

    public void setApril(int april) {
        this.april = april;
    }

    public int getMay() {
        return may;
    }

    public void setMay(int may) {
        this.may = may;
    }

    public int getJune() {
        return june;
    }

    public void setJune(int june) {
        this.june = june;
    }

    public int getJuly() {
        return july;
    }

    public void setJuly(int july) {
        this.july = july;
    }

    public int getAugust() {
        return august;
    }

    public void setAugust(int august) {
        this.august = august;
    }

    public int getSeptember() {
        return september;
    }

    public void setSeptember(int september) {
        this.september = september;
    }

    public int getOctober() {
        return october;
    }

    public void setOctober(int october) {
        this.october = october;
    }

    public int getNovember() {
        return november;
    }

    public void setNovember(int november) {
        this.november = november;
    }

    public int getDecember() {
        return december;
    }

    public void setDecember(int december) {
        this.december = december;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    

}
