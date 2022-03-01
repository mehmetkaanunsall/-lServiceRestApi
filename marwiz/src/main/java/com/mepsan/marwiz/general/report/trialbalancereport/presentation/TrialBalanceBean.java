package com.mepsan.marwiz.general.report.trialbalancereport.presentation;

import com.google.gson.Gson;
import com.mepsan.marwiz.finance.credit.business.ICreditService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.trialbalancereport.business.ITrialBalanceService;
import com.mepsan.marwiz.general.report.trialbalancereport.dao.TrialBalance;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author Samet Dağ
 * @date 10.12.2018
 */
@ManagedBean
@ViewScoped
public class TrialBalanceBean extends GeneralReportBean<TrialBalance> {

    @ManagedProperty(value = "#{trialBalanceService}")
    public ITrialBalanceService trialBalanceService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    public IBranchSettingService branchSettingService;

    private HashMap<Integer, TrialBalance> listOfBalance;
    private Date endDate, beginDate;
    private BigDecimal totalIncome, totalOutcome, totalBalance;
    private PieChartModel pieChartModelIncome, pieChartModelExpense;
    private TreeNode root;
    List<TrialBalance> detailTrialList;
    boolean fuelCard, marketCard, safeChkBox, bankChkBox, currentChkBox, employeeChkBox, chequeBillChkBox, incomeChkBox, expenseChkBox, postPaidChkBox, incomeexpenseSubgroupChkBox, waitingSlipsChkBox;
    private int marketAssign;
    private List<BranchSetting> listOfBranch;
    String whereBranch;

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public int getMarketAssign() {
        return marketAssign;
    }

    public void setMarketAssign(int marketAssign) {
        this.marketAssign = marketAssign;
    }

    public boolean isIncomeexpenseSubgroupChkBox() {
        return incomeexpenseSubgroupChkBox;
    }

    public void setIncomeexpenseSubgroupChkBox(boolean incomeexpenseSubgroupChkBox) {
        this.incomeexpenseSubgroupChkBox = incomeexpenseSubgroupChkBox;
    }

    public boolean isPostPaidChkBox() {
        return postPaidChkBox;
    }

    public void setPostPaidChkBox(boolean postPaidChkBox) {
        this.postPaidChkBox = postPaidChkBox;
    }

    public boolean isFuelCard() {
        return fuelCard;
    }

    public void setFuelCard(boolean fuelCard) {
        this.fuelCard = fuelCard;
    }

    public boolean isMarketCard() {
        return marketCard;
    }

    public void setMarketCard(boolean marketCard) {
        this.marketCard = marketCard;
    }

    public boolean isSafeChkBox() {
        return safeChkBox;
    }

    public void setSafeChkBox(boolean safeChkBox) {
        this.safeChkBox = safeChkBox;
    }

    public boolean isBankChkBox() {
        return bankChkBox;
    }

    public void setBankChkBox(boolean bankChkBox) {
        this.bankChkBox = bankChkBox;
    }

    public boolean isCurrentChkBox() {
        return currentChkBox;
    }

    public void setCurrentChkBox(boolean currentChkBox) {
        this.currentChkBox = currentChkBox;
    }

    public boolean isEmployeeChkBox() {
        return employeeChkBox;
    }

    public void setEmployeeChkBox(boolean employeeChkBox) {
        this.employeeChkBox = employeeChkBox;
    }

    public boolean isChequeBillChkBox() {
        return chequeBillChkBox;
    }

    public void setChequeBillChkBox(boolean chequeBillChkBox) {
        this.chequeBillChkBox = chequeBillChkBox;
    }

    public boolean isIncomeChkBox() {
        return incomeChkBox;
    }

    public void setIncomeChkBox(boolean incomeChkBox) {
        this.incomeChkBox = incomeChkBox;
    }

    public boolean isExpenseChkBox() {
        return expenseChkBox;
    }

    public void setExpenseChkBox(boolean expenseChkBox) {
        this.expenseChkBox = expenseChkBox;
    }

    public List<TrialBalance> getDetailTrialList() {
        return detailTrialList;
    }

    public void setDetailTrialList(List<TrialBalance> detailTrialList) {
        this.detailTrialList = detailTrialList;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public PieChartModel getPieChartModelIncome() {
        return pieChartModelIncome;
    }

    public void setPieChartModelIncome(PieChartModel pieChartModelIncome) {
        this.pieChartModelIncome = pieChartModelIncome;
    }

    public PieChartModel getPieChartModelExpense() {
        return pieChartModelExpense;
    }

    public void setPieChartModelExpense(PieChartModel pieChartModelExpense) {
        this.pieChartModelExpense = pieChartModelExpense;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalOutcome() {
        return totalOutcome;
    }

    public void setTotalOutcome(BigDecimal totalOutcome) {
        this.totalOutcome = totalOutcome;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setTrialBalanceService(ITrialBalanceService trialBalanceService) {
        this.trialBalanceService = trialBalanceService;
    }

    public HashMap<Integer, TrialBalance> getListOfBalance() {
        return listOfBalance;
    }

    public void setListOfBalance(HashMap<Integer, TrialBalance> listOfBalance) {
        this.listOfBalance = listOfBalance;
    }

    public boolean isWaitingSlipsChkBox() {
        return waitingSlipsChkBox;
    }

    public void setWaitingSlipsChkBox(boolean waitingSlipsChkBox) {
        this.waitingSlipsChkBox = waitingSlipsChkBox;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    @PostConstruct
    @Override
    public void init() {
        marketCard = true;
        fuelCard = false;
        safeChkBox = true;
        bankChkBox = true;
        currentChkBox = true;
        employeeChkBox = true;
        chequeBillChkBox = true;
        incomeChkBox = true;
        expenseChkBox = true;
        postPaidChkBox = true;
        incomeexpenseSubgroupChkBox = true;
        waitingSlipsChkBox = true;

        endDate = new Date();
        selectedObject = new TrialBalance();
        listOfBranch = new ArrayList<>();
        listOfBranch = branchSettingService.findUserAuthorizeBranch();

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) { // kullanıcının default branch bilgisini seçili getirmek için kullanılır.
                    selectedObject.getSelectedBranchList().add(branchSetting);
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) { // kullanıcının default branch bilgisini seçili getirmek için kullanılır.
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedObject.getSelectedBranchList().add(branchSetting);
                    break;
                }
            }
        }

        adjustDates();
    }

    @Override
    public void find() {
        isFind = true;

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();

        List<Boolean> chkBoxList = Arrays.asList(new Boolean[]{fuelCard, marketCard, bankChkBox, safeChkBox, currentChkBox, chequeBillChkBox, employeeChkBox, incomeChkBox, expenseChkBox, postPaidChkBox, incomeexpenseSubgroupChkBox, waitingSlipsChkBox});

        if (!chkBoxList.contains(true)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", sessionBean.getLoc().getString("pleaseselectatleastonecriteria")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            isFind = false;
            return;
        }

        whereBranch = trialBalanceService.whereBranch(selectedObject.getSelectedBranchList());

        listOfBalance = trialBalanceService.findAll(endDate, beginDate, chkBoxList, marketAssign, whereBranch);

        totalIncome = BigDecimal.ZERO;
        totalOutcome = BigDecimal.ZERO;
        totalBalance = BigDecimal.ZERO;

        for (Map.Entry<Integer, TrialBalance> entry : listOfBalance.entrySet()) {
            totalIncome = totalIncome.add(entry.getValue().getIncome());
            totalOutcome = totalOutcome.add(entry.getValue().getExpense());
        }

        totalBalance = totalIncome.subtract(totalOutcome);
        createAreaModel();
        createDetailTable();

        RequestContext.getCurrentInstance().execute("Centrowiz.panelClose();");
        RequestContext.getCurrentInstance().update("frmFieldSet");
    }

    /**
     * bu metot chartları oluşturur
     */
    public void createAreaModel() {

        RequestContext context = RequestContext.getCurrentInstance();
        List<TrialBalance> listInc = new ArrayList<>();
        List<TrialBalance> listExp = new ArrayList<>();

        for (Map.Entry<Integer, TrialBalance> entry : listOfBalance.entrySet()) {
            try {
                listInc.add(entry.getValue().clone());
                listExp.add(entry.getValue().clone());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Iterator<TrialBalance> iterator = listExp.iterator(); iterator.hasNext();) {
            TrialBalance next = iterator.next();

            if (next.getAccountName().equals("8")) {//bekleyen slip giderde görünmeyecek
                iterator.remove();
            }

            if (next.getAccountName().equals("7")) {//ürün giderde görünmeyecek
                iterator.remove();
            }

            if (next.getAccountName().equals("5") && !expenseChkBox) {
                iterator.remove();
            }
        }

        for (Iterator<TrialBalance> iterator = listInc.iterator(); iterator.hasNext();) {
            TrialBalance next = iterator.next();
            if (next.getAccountName().equals("5") && !incomeChkBox) {
                iterator.remove();
            }
        }
        // gelir
        String data = new Gson().toJson(listInc);
        context.execute("income(" + "'" + data + "'" + ")");
        context.update("frmFieldSet:pnlgrpBalanceIncome");

        //gider
        String dataExp = new Gson().toJson(listExp);
        context.execute("expense(" + "'" + dataExp + "'" + ")");
        context.update("frmFieldSet:pnlgrpBalanceExpense");
    }

    /**
     * Bu metot aşağıdaki tree table a veri oluşturur
     */
    public void createDetailTable() {
        List<Boolean> chkBoxList = Arrays.asList(new Boolean[]{fuelCard, marketCard, bankChkBox, safeChkBox, currentChkBox, chequeBillChkBox, employeeChkBox, incomeChkBox, expenseChkBox, postPaidChkBox, incomeexpenseSubgroupChkBox, waitingSlipsChkBox});

        detailTrialList = trialBalanceService.findDetail(endDate, beginDate, chkBoxList, marketAssign, whereBranch);

        root = trialBalanceService.createDetailTree(detailTrialList);
    }

    /**
     * Bitiş tarih alanı değiştirildiğinde başlangıç tarihini güncellemek için
     * yapıldı.
     */
    public void adjustDates() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        endDate = cal.getTime();

        cal.set(cal.get(Calendar.YEAR), 0, 1);
        beginDate = cal.getTime();
    }

    public void createPdf() {
        List<Boolean> chkBoxList = Arrays.asList(new Boolean[]{fuelCard, marketCard, bankChkBox, safeChkBox, currentChkBox, chequeBillChkBox, employeeChkBox, incomeChkBox, expenseChkBox, postPaidChkBox, incomeexpenseSubgroupChkBox, waitingSlipsChkBox});
        trialBalanceService.exportPdf(listOfBalance, totalIncome, totalOutcome, totalBalance, endDate, beginDate, detailTrialList, chkBoxList);
    }

    public void createExcel() throws IOException {
        List<Boolean> chkBoxList = Arrays.asList(new Boolean[]{fuelCard, marketCard, bankChkBox, safeChkBox, currentChkBox, chequeBillChkBox, employeeChkBox, incomeChkBox, expenseChkBox, postPaidChkBox, incomeexpenseSubgroupChkBox, waitingSlipsChkBox});
        trialBalanceService.exportExcel(listOfBalance, totalIncome, totalOutcome, totalBalance, endDate, beginDate, detailTrialList, chkBoxList);
    }

    public void createPrinter() {
        List<Boolean> chkBoxList = Arrays.asList(new Boolean[]{fuelCard, marketCard, bankChkBox, safeChkBox, currentChkBox, chequeBillChkBox, employeeChkBox, incomeChkBox, expenseChkBox, postPaidChkBox, incomeexpenseSubgroupChkBox, waitingSlipsChkBox});
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(trialBalanceService.exportPrinter(listOfBalance, totalIncome, totalOutcome, totalBalance, endDate, beginDate, detailTrialList, chkBoxList)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<TrialBalance> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BigDecimal calcBalance(BigDecimal income, BigDecimal expense, boolean b) {

        if (income == null || expense == null) {
            return null;
        } else if (income.doubleValue() == -2121.2121 || expense.doubleValue() == -2121.2121) {
            return null;
        } else if (b) {// gelir bakiyesi hesapla
            if (income.compareTo(expense) == 1) {

                return income.subtract(expense);
            } else {
                return null;
            }
        } else// gider bakiyesi hesapla
        {
            if (expense.compareTo(income) == 1) {

                return expense.subtract(income);
            } else {
                return null;
            }
        }
    }

}
