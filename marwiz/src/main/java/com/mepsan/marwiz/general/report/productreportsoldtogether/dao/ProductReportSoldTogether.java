/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 12:27:41 PM
 */
package com.mepsan.marwiz.general.report.productreportsoldtogether.dao;

import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.HourInterval;
import java.math.BigDecimal;
import java.util.Date;

public class ProductReportSoldTogether {

    private int id;
    private int timeInterval;
    private Date processDate;
    private int year;
    private int month;
    private String hour;
    private Date firstWeekDay;
    private Date endWeekDay;
    private int selectedYear;
    private int weekDay;
    private HourInterval hourInterval;
    private int timezone;
    private Date beginDate;
    private Date endDate;
    private Stock stock1;
    private Stock stock2;
    private BigDecimal quantity;
    private int reportType;
    private String shiftNo;

    public ProductReportSoldTogether() {
        this.stock1 = new Stock();
        this.stock2 = new Stock();
        this.hourInterval = new HourInterval();
    }

    public int getId() {
        return id;
    }

    public Stock getStock1() {
        return stock1;
    }

    public Stock getStock2() {
        return stock2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStock1(Stock stock1) {
        this.stock1 = stock1;
    }

    public void setStock2(Stock stock2) {
        this.stock2 = stock2;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public Date getFirstWeekDay() {
        return firstWeekDay;
    }

    public void setFirstWeekDay(Date firstWeekDay) {
        this.firstWeekDay = firstWeekDay;
    }

    public Date getEndWeekDay() {
        return endWeekDay;
    }

    public void setEndWeekDay(Date endWeekDay) {
        this.endWeekDay = endWeekDay;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public HourInterval getHourInterval() {
        return hourInterval;
    }

    public void setHourInterval(HourInterval hourInterval) {
        this.hourInterval = hourInterval;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }

    @Override
    public String toString() {
        return this.getStock1().getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
