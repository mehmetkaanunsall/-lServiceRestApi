package com.mepsan.marwiz.general.salarypayment.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.BankAccountService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.safe.business.SafeService;
import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.EmployeeInfo;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.salarypayment.business.ISalaryPaymentService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 * Bu class maaş ödemelerinin yapılmasını, personel borçluysa personelin
 * borcunun tahsil edilmesi veya alacaklıysa alacağının verilmesini sağlar.
 *
 * @author Samet Dağ
 */
@ManagedBean
@ViewScoped
public class SalaryPaymentBean extends GeneralDefinitionBean<EmployeeInfo> {

    @ManagedProperty(value = "#{salaryPaymentService}")
    public ISalaryPaymentService salaryPaymentService;

    @ManagedProperty(value = "#{bankAccountService}")
    private BankAccountService bankAccountService;

    @ManagedProperty(value = "#{safeService}")
    private SafeService safeService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{accountService}")
    private IAccountService accountService;

    List<EmployeeInfo> listEmpInfo;
    List<Type> listPaymentType;
    List<BankAccount> listBankAccount;
    List<Safe> listSafe;
    List<EmployeeInfo> listPayableEmployee;
    List<EmployeeInfo> listHasDebtEmployee;
    List<EmployeeInfo> listHasPayeeEmployee;

    private BankAccount bankAccount;
    private Safe safe;
    Type debitingtype;

    public FinancingDocument financingDocument;

    BigDecimal totalPaidSalary, totalAccrual;
    int whichAction;//hangi butonun save methodu calsıtıgını bulmak için

    public BigDecimal getTotalAccrual() {
        return totalAccrual;
    }

    public void setTotalAccrual(BigDecimal totalAccrual) {
        this.totalAccrual = totalAccrual;
    }

    public IAccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public int getWhichAction() {
        return whichAction;
    }

    public void setWhichAction(int whichAction) {
        this.whichAction = whichAction;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public Type getDebitingtype() {
        return debitingtype;
    }

    public void setDebitingtype(Type debitingtype) {
        this.debitingtype = debitingtype;
    }

    public List<EmployeeInfo> getListHasPayeeEmployee() {
        return listHasPayeeEmployee;
    }

    public void setListHasPayeeEmployee(List<EmployeeInfo> listHasPayeeEmployee) {
        this.listHasPayeeEmployee = listHasPayeeEmployee;
    }

    public List<EmployeeInfo> getListHasDebtEmployee() {
        return listHasDebtEmployee;
    }

    public void setListHasDebtEmployee(List<EmployeeInfo> listHasDebtEmployee) {
        this.listHasDebtEmployee = listHasDebtEmployee;
    }

    public List<EmployeeInfo> getListPayableEmployee() {
        return listPayableEmployee;
    }

    public void setListPayableEmployee(List<EmployeeInfo> listPayableEmployee) {
        this.listPayableEmployee = listPayableEmployee;
    }

    public BigDecimal getTotalPaidSalary() {
        return totalPaidSalary;
    }

    public void setTotalPaidSalary(BigDecimal totalPaidSalary) {
        this.totalPaidSalary = totalPaidSalary;
    }

    public void setBankAccountService(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void setSafeService(SafeService safeService) {
        this.safeService = safeService;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Safe getSafe() {
        return safe;
    }

    public void setSafe(Safe safe) {
        this.safe = safe;
    }

    public List<BankAccount> getListBankAccount() {
        return listBankAccount;
    }

    public void setListBankAccount(List<BankAccount> listBankAccount) {
        this.listBankAccount = listBankAccount;
    }

    public List<Safe> getListSafe() {
        return listSafe;
    }

    public void setListSafe(List<Safe> listSafe) {
        this.listSafe = listSafe;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<Type> getListPaymentType() {
        return listPaymentType;
    }

    public void setListPaymentType(List<Type> listPaymentType) {
        this.listPaymentType = listPaymentType;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public List<EmployeeInfo> getListEmpInfo() {
        return listEmpInfo;
    }

    public void setListEmpInfo(List<EmployeeInfo> listEmpInfo) {
        this.listEmpInfo = listEmpInfo;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSalaryPaymentService(ISalaryPaymentService salaryPaymentService) {
        this.salaryPaymentService = salaryPaymentService;
    }

    @PostConstruct
    @Override
    public void init() {
        totalPaidSalary = BigDecimal.ZERO;
        totalAccrual = BigDecimal.ZERO;

        listBankAccount = new ArrayList<>();
        listBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id = 14 ", sessionBean.getUser().getLastBranch());//sadece ticari hesaplardan ödeme yapabilir.

        listSafe = new ArrayList<>();
        listSafe = safeService.findSafeByCurrency(" AND sf.currency_id = " + sessionBean.getUser().getLastBranch().getCurrency().getId());

        listPaymentType = new ArrayList<>();

        List<Type> listOfTempType = sessionBean.getTypes(20);

        for (int i = 0; i < listOfTempType.size(); i++) {
            if (listOfTempType.get(i).getId() == 48 || listOfTempType.get(i).getId() == 56) {//İşlem tipi Nakit Ödeme ve Gönderilen Havaleler ise listeye at.
                listPaymentType.add(listOfTempType.get(i));
            } else if (listOfTempType.get(i).getId() == 50) {
                debitingtype = listOfTempType.get(i);
            }
        }

        financingDocument = new FinancingDocument();

        safe = new Safe();
        bankAccount = new BankAccount();
        financingDocument.setDocumentDate(new Date());

        listEmpInfo = new ArrayList<>();
        listEmpInfo = findall();//Tüm çalışanların net maaş,agi,açık/fazla(borç) ve ödenecek maaşlarının çekilmesi.
        listOfObjects = new ArrayList<>();

        selectedObject = new EmployeeInfo();

        calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplanıyor.

        setListBtn(sessionBean.checkAuthority(new int[]{46, 47, 48}, 0));

    }

    public void decideWhichAction() {
        switch (whichAction) {
            case 0:
                debitingBeforePaySalary();
                break;
            case 1:
                payJustSalary();
                break;
            default:
                debitingAndPaySalaries();
                break;
        }
    }

    /**
     * Sadece TAHAKKUK ETMEK İÇİN ÇALIŞIR.
     */
    public void openDebitingDialog() {
        resetDialog();

        if (listOfObjects.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("pleaseselectatleastoneemployee")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }

        for (int i = 0; i < listOfObjects.size(); i++) {
            if (listOfObjects.get(i).getExactsalary().compareTo(BigDecimal.ZERO) == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("pleasedefineemployeessalary")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }

        whichAction = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmPopupDialog");
        context.execute("PF('dlg_SalaryPayment').show();");
    }

    /**
     * Sadece Maaş Ödeme dialogunu açar
     */
    public void openPaymentDialog() {
        resetDialog();

        if (listOfObjects.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("pleaseselectatleastoneemployee")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }

        if (totalPaidSalary.compareTo(BigDecimal.ZERO) == 0) {//Toplam Ödenecek Tutar 0 ise kullanıcıya hata mesajı verir.
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }
        whichAction = 1;
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmPopupDialog");
        context.execute("PF('dlg_SalaryPayment').show();");
    }

    /**
     * Hem Maaş Ödeme Hem Tahakkuk Etmek için Çalışır.
     */
    public void openPaymentandDebitingDialog() {
        resetDialog();

        if (listOfObjects.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("pleaseselectatleastoneemployee")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }

        if (totalPaidSalary.compareTo(BigDecimal.ZERO) == 0) {//Toplam Ödenecek Tutar 0 ise kullanıcıya hata mesajı verir.
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("amountmustbegreaterthanzero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }

        for (int i = 0; i < listOfObjects.size(); i++) {
            if (listOfObjects.get(i).getExactsalary().compareTo(BigDecimal.ZERO) == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("pleasedefineemployeessalary")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }

        whichAction = 2;
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmPopupDialog");
        context.execute("PF('dlg_SalaryPayment').show();");

    }

    /**
     * Açılan Maaş Ödeme İşlemi Dialogunda kaydete basınca çalışır.
     */
    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dlgConf");
        context.execute("PF('confirmDlg').show();");
    }

    /**
     * Ödenecek Maaş alanı editlendiğinde borç alanını ve toplam ödenecek maaş
     * alanını günceller.
     *
     * @param event
     */
    public void onCellEdit(CellEditEvent event) {

        BigDecimal fixedPrice = listEmpInfo.get(event.getRowIndex()).getExactsalary() == null ? BigDecimal.ZERO : listEmpInfo.get(event.getRowIndex()).getExactsalary();//net maaş
        int agi = listEmpInfo.get(event.getRowIndex()).getAgi();//agi
        BigDecimal salaryToPaid = listEmpInfo.get(event.getRowIndex()).getAccountMovement().getPrice() == null ? BigDecimal.ZERO : listEmpInfo.get(event.getRowIndex()).getAccountMovement().getPrice();//ödenecek maaş

        listEmpInfo.get(event.getRowIndex()).getAccountMovement().setBalance(salaryToPaid.subtract(fixedPrice.add(new BigDecimal(agi))));//yeni Açık/Fazla(Borç) = ödenecek maaş-netmaaş+agi

        calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplama
    }

    /**
     * Tahakkuk etme işlemi
     */
    public void debitingBeforePaySalary() {
        int result = 0;

        if (listOfObjects.size() > 0) {
            result = salaryPaymentService.createFinancingDocument(financingDocument, null, null, listOfObjects, false, listOfObjects, whichAction);
        } else {
            return;
        }

        if (result == -101) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            sessionBean.createUpdateMessage(result);
        }

        if (result > 0) {
            listEmpInfo = findall();//Tüm çalışanların net maaş,agi,açık/fazla(borç) ve ödenecek maaşlarının çekilmesi.
            listOfObjects = new ArrayList<>();
            calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplanıyor.

            RequestContext context = RequestContext.getCurrentInstance();

            context.execute("PF('salaryPaymentsPF').unselectAllRows();");
            context.update("frmSalaryPayments:dtbSalaryPayments");

            sessionBean.createUpdateMessage(result);

            context.execute("PF('confirmDlg').hide();");
            context.execute("PF('dlg_SalaryPayment').hide();");

            resetDialog();
        }
    }

    /**
     * Confirm Dialogda onaylama(YES) butonuna basınca çalışır.
     */
    public void payJustSalary() {

        int result = 0;
        if (listPayableEmployee.size() > 0) {//Maası bir + bir - diye ödememiz gerekiyor ki kullanının bakiyesi artıp gitmesin borcu yada alacagı gözüksün.!!!
            result = salaryPaymentService.createFinancingDocument(financingDocument, safe, bankAccount, listPayableEmployee, false, new ArrayList<>(), whichAction);

            if (result == -101) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                sessionBean.createUpdateMessage(result);
            }

        }

        if (result > 0) {
            listEmpInfo = findall();//Tüm çalışanların net maaş,agi,açık/fazla(borç) ve ödenecek maaşlarının çekilmesi.
            listOfObjects = new ArrayList<>();
            calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplanıyor.

            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('salaryPaymentsPF').unselectAllRows();");
            context.update("frmSalaryPayments:dtbSalaryPayments");

            sessionBean.createUpdateMessage(result);

            context.execute("PF('confirmDlg').hide();");
            context.execute("PF('dlg_SalaryPayment').hide();");

            resetDialog();
        }
    }

    /**
     * Hem Tahakkuk Hem Maaş ÖdEMESİ Yapılır.
     */
    public void debitingAndPaySalaries() {

        int result = 0;

        int tempFinId = financingDocument.getFinancingType().getId();
        if (listOfObjects.size() > 0) {
            financingDocument.getFinancingType().setId(50);
            result = salaryPaymentService.createFinancingDocument(financingDocument, null, null, listOfObjects, false, listOfObjects, whichAction);
            if (result > 0) {
                financingDocument.getFinancingType().setId(tempFinId);
                payJustSalary();
            }
            if (result == -101) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("processcannotbedonebecausemainsafedropdowntominus")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                sessionBean.createUpdateMessage(result);
            }
        }
        if (result > 0) {
            listEmpInfo = findall();//Tüm çalışanların net maaş,agi,açık/fazla(borç) ve ödenecek maaşlarının çekilmesi.
            listOfObjects = new ArrayList<>();
            calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplanıyor.

            RequestContext context = RequestContext.getCurrentInstance();

            context.execute("PF('salaryPaymentsPF').unselectAllRows();");
            context.update("frmSalaryPayments:dtbSalaryPayments");

            context.execute("PF('confirmDlg').hide();");
            context.execute("PF('dlg_SalaryPayment').hide();");

            resetDialog();
        }

    }

    /**
     * Toplam Ödenecek Maaş hesaplanıyor.
     *
     * @return
     */
    public BigDecimal calculateTotalPaidSalary() {
        totalPaidSalary = BigDecimal.ZERO;
        totalAccrual = BigDecimal.ZERO;
        BigDecimal paidSalary;

        listPayableEmployee = new ArrayList<>();//Maaşı ödenebilir olanlar
        listHasDebtEmployee = new ArrayList<>();//Borcu tahsil edilebilecek olanlar
        listHasPayeeEmployee = new ArrayList<>();//Alacaklı olanlar

        for (int i = 0; i < listOfObjects.size(); i++) {

            paidSalary = listOfObjects.get(i).getAccountMovement().getPrice();

            if (paidSalary != null && paidSalary.compareTo(BigDecimal.ZERO) > 0) {//Ödenecek Maaş alanı null degilse ve 0 dan büyükse
                listPayableEmployee.add(listOfObjects.get(i));
                if (listOfObjects.get(i).getAccountMovement().getBalance() != null && listOfObjects.get(i).getAccountMovement().getBalance().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) > 0) {
                    listHasPayeeEmployee.add(listOfObjects.get(i));
                } else if (listOfObjects.get(i).getAccountMovement().getBalance() != null && listOfObjects.get(i).getAccountMovement().getBalance().setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) < 0) {
                    listHasDebtEmployee.add(listOfObjects.get(i));
                }
            }

            totalPaidSalary = totalPaidSalary.add(paidSalary == null ? BigDecimal.ZERO : paidSalary.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : paidSalary);
            totalAccrual = totalAccrual.add(listOfObjects.get(i).getExactsalary().add(new BigDecimal(listOfObjects.get(i).getAgi())));
        }

        return totalPaidSalary;
    }

    /**
     * Maaş Ödeme İşlemi Dialoğu kapatılınca dialogu resetlemek için kullanılır.
     */
    public void resetDialog() {
        financingDocument = new FinancingDocument();
        financingDocument.setDocumentDate(new Date());
        bankAccount = new BankAccount();
        safe = new Safe();
    }

    /**
     * Tüm checkboxları tikler veya tikini kaldırır(yani tüm elemanları seçer ya
     * da seçmez)
     *
     * @param event
     */
    public void selectRows(ToggleSelectEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (event.isSelected()) {//eğer hepsini seç tiklendiyse
            listOfObjects = listEmpInfo;//paginatorden dolayı sadece bulunduğu sayfadaki tüm elemanları listeye atıyordu oyüzden daodan gelen tüm liste atandı.
            calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplama
            context.execute("PF('salaryPaymentsPF').selectAllRows();");
        } else {//eğer hepsini seç tiki kaldırılacaksa
            listOfObjects = new ArrayList<>();
            calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplama
            context.execute("PF('salaryPaymentsPF').unselectAllRows();");
        }
    }

    /**
     * Tek(Single) Checkbox select edildiğinde çalışır.
     *
     * @param event
     */
    public void onRowSelect(SelectEvent event) {
        calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplama
    }

    /**
     * Tek(Single) Checkbox selecti kaldırıldığında çalışır.
     *
     * @param event
     */
    public void onRowUnSelect(UnselectEvent event) {
        calculateTotalPaidSalary();//Toplam Ödenecek Maaş Hesaplama
    }

    @Override
    public List<EmployeeInfo> findall() {
        return salaryPaymentService.findAllEmployee();
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
