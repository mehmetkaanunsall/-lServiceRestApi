package com.mepsan.marwiz.system.einvoiceintegration.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.log.SendEInvoice;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IEInvoiceIntegrationDao;
import com.mepsan.marwiz.system.einvoiceintegration.dao.EInvoice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import static java.util.Collections.list;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author elif.mart
 */
public class EInvoiceIntegrationService implements IEInvoiceIntegrationService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IEInvoiceIntegrationDao eInvoiceIntegrationDao;

    private List<EInvoice> listOfInvoices;

    public List<EInvoice> getListOfInvoices() {
        return listOfInvoices;
    }

    public void setListOfInvoices(List<EInvoice> listOfInvoices) {
        this.listOfInvoices = listOfInvoices;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void seteInvoiceIntegrationDao(IEInvoiceIntegrationDao eInvoiceIntegrationDao) {
        this.eInvoiceIntegrationDao = eInvoiceIntegrationDao;
    }

    @Override
    public String createWhere(Date beginDate, Date endDate, int isSend, int processType, List<Account> accountList, String invoiceNo) {
        String where = "";
        String accounts = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where = where + " AND inv.invoicedate BETWEEN '" + sdf.format(beginDate) + "' AND '" + sdf.format(endDate) + "' ";
        if (processType == 1) {
            where = where + " AND inv.taxpayertype_id = 1 ";

        } else {
            where = where + " AND inv.taxpayertype_id = 2 ";
        }

        if (isSend == 1) {
            where = where + " AND lsei.is_send = true ";
        } else if (isSend == 2) {
            where = where + " AND lsei.is_send = false ";
        }

        if (!accountList.isEmpty()) {
            for (Account acc : accountList) {
                   accounts = accounts + "," + acc.getId();
                if (acc.getId() == 0) {
                    accounts = "";
                break;
                }
                
             

            }
        }

        if (!accounts.isEmpty()) {
            accounts = accounts.substring(1, accounts.length());
        }

        if (!accounts.isEmpty()) {

            where = where + " AND inv.account_id IN (" + accounts + ")";

        }

        if (invoiceNo != null && !invoiceNo.isEmpty()) {
            where = where + " AND ( inv.documentserial ilike '%" + invoiceNo + "%' OR inv.documentnumber ilike '%" + invoiceNo + "%' ) \n";
        }

        return where;

    }

    // E-Faturaları Innova web servisine göndermek için xml data hazırlar
    @Override
    public boolean sendEInvoice(List<EInvoice> listEInvoice, BranchSetting obj) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();

        String data = "";
        String items = "";
        String accountingCustomerParty = "";
        String pricingCurrency = "";
        Date date = new Date();

        if (listOfInvoices == null) {
            listOfInvoices = new ArrayList<>();
        }
        listOfInvoices.clear();
        listOfInvoices.addAll(listEInvoice);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator('.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        ((DecimalFormat) formatter).setGroupingUsed(false);

        for (EInvoice eii : listOfInvoices) {
            String itemList = "";

            for (InvoiceItem iItem : eii.getListInvoiceItem()) {

                itemList = itemList + "                                <pay4:InvoiceLine>\n"
                        + "                                    <pay4:AllowanceCharges>\n"
                        + "                                        <pay4:AllowanceCharge>\n"
                        + "                                            <pay4:Amount>\n"
                        + "                                                <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                                <pay4:Value>" + (iItem.getDiscountPrice() == null ? 0.00 : iItem.getDiscountPrice()) + "</pay4:Value>\n"
                        + "                                            </pay4:Amount>\n"
                        + "                                            <pay4:ChargeIndicator>false</pay4:ChargeIndicator>\n"
                        + "                                            <pay4:MultiplierFactorNumeric>" + (iItem.getDiscountRate() == null ? 0.00 : iItem.getDiscountRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN)) + "</pay4:MultiplierFactorNumeric>\n"
                        + "                                        </pay4:AllowanceCharge>\n"
                        + "                                    </pay4:AllowanceCharges>"
                        + "                                    <pay4:InvoicedQuantityUnitCode>" + iItem.getUnit().getInternationalCode() + "</pay4:InvoicedQuantityUnitCode>\n"
                        + "                                    <pay4:InvoicedQuantityValue>" + iItem.getQuantity() + "</pay4:InvoicedQuantityValue>\n"
                        + "                                    <pay4:Item>\n"
                        + "                                        <pay4:Name>" + iItem.getStock().getName() + "</pay4:Name>\n"
                        + "                                    </pay4:Item>\n"
                        + "                                    <pay4:LineExtensionAmount>\n"
                        + "                                        <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                        <pay4:Value>" + (iItem.getTotalPrice() == null ? 0.00 : formatter.format(iItem.getTotalPrice())) + "</pay4:Value>\n"
                        + "                                    </pay4:LineExtensionAmount>\n"
                        + "                                    <pay4:Price>\n"
                        + "                                        <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                        <pay4:Value>" + (iItem.getUnitPrice() == null ? 0.00 : formatter.format(iItem.getUnitPrice())) + "</pay4:Value>\n"
                        + "                                    </pay4:Price>\n"
                        + "                                       <pay4:TaxTotal>\n"
                        + "                                        <pay4:TaxAmount>\n"
                        + "                                            <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                            <pay4:Value>" + (iItem.getTotalTax() == null ? 0.00 : formatter.format(iItem.getTotalTax())) + "</pay4:Value>\n"
                        + "                                        </pay4:TaxAmount>\n"
                        + "                                        <pay4:TaxSubtotal>\n"
                        + "                                            <pay4:TaxSubtotal>\n"
                        + "                                                   <pay4:Percent>" + (iItem.getTaxRate() == null ? 0.00 : formatter.format(iItem.getTaxRate())) + "</pay4:Percent>"
                        + "                                                <pay4:TaxAmount>\n"
                        + "                                                    <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                                    <pay4:Value>" + (iItem.getTotalTax() == null ? 0.00 : iItem.getTotalTax()) + "</pay4:Value>\n"
                        + "                                                </pay4:TaxAmount>\n"
                        + "                                                <pay4:TaxCategorySchemeCode>0015</pay4:TaxCategorySchemeCode>\n"
                        + "                                                <pay4:TaxCategorySchemeName>KATMA DEĞER VERGİSİ</pay4:TaxCategorySchemeName>\n"
                        + "                                            </pay4:TaxSubtotal>\n"
                        + "                                        </pay4:TaxSubtotal>\n"
                        + "                                    </pay4:TaxTotal>"
                        + "                                </pay4:InvoiceLine>\n";

            }

            if (eii.getAccount().getIsPerson()) {

                String nameSurname = eii.getAccount().getName();
                String[] words = nameSurname.split(" ");

                accountingCustomerParty = accountingCustomerParty + "<pay4:AccountingCustomerParty>\n"
                        + "                                <pay4:Party>\n"
                        + "                                      <pay4:Contact>\n"
                        + "                                        <pay4:ElectronicMail>" + eii.getAccount().getEmail() + "</pay4:ElectronicMail>\n"
                        + "                                        <pay4:Telephone>" + eii.getAccount().getPhone() + "</pay4:Telephone>"
                        + "                                    </pay4:Contact>\n"
                        + "                                    <pay4:PartyIdentifications>\n"
                        + "                                        <pay4:PartyIdentification>\n"
                        + "                                            <pay4:SchemeID>TCKN</pay4:SchemeID>\n"
                        + "                                            <pay4:Value>" + eii.getAccount().getTaxNo() + "</pay4:Value>\n"
                        + "                                        </pay4:PartyIdentification>\n"
                        + "                                    </pay4:PartyIdentifications>\n"
                        + "                                    <pay4:PartyName>" + eii.getAccount().getTitle() + "</pay4:PartyName>\n"
                        + "                                    <pay4:Person>\n"
                        + "                                        <pay4:FamilyName>" + words[1] + "</pay4:FamilyName>\n"
                        + "                                        <pay4:FirstName>" + words[0] + "</pay4:FirstName>\n"
                        + "                                        <pay4:Title>" + eii.getAccount().getTitle() + "</pay4:Title>\n"
                        + "                                    </pay4:Person>"
                        + "                                    <pay4:PostalAddress>\n"
                        + "                                        <pay4:CityName>" + eii.getAccount().getCity().getTag() + "</pay4:CityName>\n"
                        + "                                        <pay4:CitySubdivisionName>" + eii.getAccount().getCounty().getName() + "</pay4:CitySubdivisionName>\n"
                        + "                                        <pay4:Country>\n"
                        + "                                            <pay4:IdentificationCode>" + eii.getAccount().getCountry().getCode() + "</pay4:IdentificationCode>\n"
                        + "                                            <pay4:Name>" + eii.getAccount().getCountry().getTag() + "</pay4:Name>\n"
                        + "                                        </pay4:Country>\n"
                        + "                                    </pay4:PostalAddress>\n"
                        + "                                </pay4:Party>\n"
                        + "                            </pay4:AccountingCustomerParty>\n";
            } else {

                accountingCustomerParty = accountingCustomerParty + "<pay4:AccountingCustomerParty>\n"
                        + "                                <pay4:Party>\n"
                        + "                                      <pay4:Contact>\n"
                        + "                                        <pay4:ElectronicMail>" + eii.getAccount().getEmail() + "</pay4:ElectronicMail>\n"
                        + "                                        <pay4:Telephone>" + eii.getAccount().getPhone() + "</pay4:Telephone>"
                        + "                                    </pay4:Contact>\n"
                        + "                                    <pay4:PartyIdentifications>\n"
                        + "                                        <pay4:PartyIdentification>\n"
                        + "                                            <pay4:SchemeID>VKN</pay4:SchemeID>\n"
                        + "                                            <pay4:Value>" + eii.getAccount().getTaxNo() + "</pay4:Value>\n"
                        + "                                        </pay4:PartyIdentification>\n"
                        + "                                    </pay4:PartyIdentifications>\n"
                        + "                                    <pay4:PartyName>" + eii.getAccount().getTitle() + "</pay4:PartyName>\n"
                        + "                                    <pay4:PartyTax>" + eii.getAccount().getTaxOffice() + "</pay4:PartyTax>"
                        + "                                    <pay4:PostalAddress>\n"
                        + "                                        <pay4:CityName>" + eii.getAccount().getCity().getTag() + "</pay4:CityName>\n"
                        + "                                        <pay4:CitySubdivisionName>" + eii.getAccount().getCounty().getName() + "</pay4:CitySubdivisionName>\n"
                        + "                                        <pay4:Country>\n"
                        + "                                            <pay4:IdentificationCode>" + eii.getAccount().getCountry().getCode() + "</pay4:IdentificationCode>\n"
                        + "                                            <pay4:Name>" + eii.getAccount().getCountry().getTag() + "</pay4:Name>\n"
                        + "                                        </pay4:Country>\n"
                        + "                                    </pay4:PostalAddress>\n"
                        + "                                </pay4:Party>\n"
                        + "                            </pay4:AccountingCustomerParty>\n";

            }

            if (eii.getCurrency().getId() != eii.getListInvoiceItem().get(0).getCurrency().getId()) {
                pricingCurrency = pricingCurrency + "<pay4:PricingCurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:PricingCurrencyCode>\n"
                        + "                            <pay4:PricingExchangeRate>\n"
                        + "                                <pay4:CalculationRate>" + eii.getListInvoiceItem().get(0).getExchangeRate() + "</pay4:CalculationRate>\n"
                        + "                                <pay4:Date>" + sdf.format(eii.getInvoiceDate()) + "</pay4:Date>\n"
                        + "                                <pay4:SourceCurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:SourceCurrencyCode>\n"
                        + "                                <pay4:TargetCurrencyCode>" + sessionBean.getUser().getLastBranchSetting().getBranch().getCurrency().getInternationalCode() + "</pay4:TargetCurrencyCode>\n"
                        + "                            </pay4:PricingExchangeRate>";

            }

            items = items + " <pay3:OutgoingInvoice>\n"
                    + "                        <pay3:Body>\n"
                    + accountingCustomerParty
                    + "                            <pay4:AccountingSupplierParty>\n"
                    + "                                <pay4:Party>\n"
                    + "                                      <pay4:Contact>\n"
                    + "                                        <pay4:ElectronicMail>" + obj.getBranch().getMail() + "</pay4:ElectronicMail>\n"
                    + "                                    </pay4:Contact>\n"
                    + "                                    <pay4:PartyIdentifications>\n"
                    + "                                        <pay4:PartyIdentification>\n"
                    + "                                            <pay4:SchemeID>VKN</pay4:SchemeID>\n"
                    + "                                            <pay4:Value>" + brSetting.getBranch().getTaxNo() + "</pay4:Value>\n"
                    + "                                        </pay4:PartyIdentification>\n"
                    + "                                    </pay4:PartyIdentifications>\n"
                    + "                                    <pay4:PartyName>" + brSetting.getBranch().getName() + "</pay4:PartyName>\n"
                    + "                                    <pay4:PostalAddress>\n"
                    + "                                        <pay4:CityName>" + obj.getBranch().getCity().getTag() + "</pay4:CityName>\n"
                    + "                                        <pay4:CitySubdivisionName>" + obj.getBranch().getCounty().getName() + "</pay4:CitySubdivisionName>\n"
                    + "                                        <pay4:Country>\n"
                    + "                                            <pay4:IdentificationCode>" + obj.getBranch().getCountry().getCode() + "</pay4:IdentificationCode>\n"
                    + "                                            <pay4:Name>" + obj.getBranch().getCountry().getTag() + "</pay4:Name>\n"
                    + "                                        </pay4:Country>\n"
                    + "                                    </pay4:PostalAddress>\n"
                    + "                                </pay4:Party>\n"
                    + "                            </pay4:AccountingSupplierParty>\n"
                    + "                             <pay4:AllowanceCharges>\n"
                    + "                                <pay4:AllowanceCharge>\n"
                    + "                                    <pay4:Amount>\n"
                    + "                                        <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                        <pay4:Value>" + (eii.getDiscountPrice() == null ? 0.00 : formatter.format(eii.getDiscountPrice())) + "</pay4:Value>\n"
                    + "                                    </pay4:Amount>\n"
                    + "                                    <pay4:ChargeIndicator>false</pay4:ChargeIndicator>\n"
                    + "                                    <pay4:MultiplierFactorNumeric>" + (eii.getDiscountRate() == null ? 0.00 : formatter.format(eii.getDiscountRate())) + "</pay4:MultiplierFactorNumeric>\n"
                    + "                                </pay4:AllowanceCharge>\n"
                    + "                            </pay4:AllowanceCharges>"
                    + "                            <pay4:CopyIndicator>false</pay4:CopyIndicator>\n"
                    + "                            <pay4:DocumentCurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:DocumentCurrencyCode>\n"
                    + "                            <pay4:InvoiceLine>\n"
                    + itemList
                    + "                            </pay4:InvoiceLine>\n"
                    + "                            <pay4:InvoiceTypeCode>" + (eii.getType().getId() == 23 || eii.getType().getId() == 59 ? 1 : eii.getType().getId() == 27 ? 2 : 0) + "</pay4:InvoiceTypeCode>\n"
                    + "                            <pay4:IssueDateTime>" + sdf.format(eii.getInvoiceDate()) + "</pay4:IssueDateTime>\n"
                    + "                            <pay4:LegalMonetaryTotal>\n"
                    + "                                 <pay4:AllowanceTotalAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalDiscount() == null ? 0.00 : formatter.format(eii.getTotalDiscount())) + "</pay4:Value>\n"
                    + "                                </pay4:AllowanceTotalAmount>"
                    + "                                <pay4:LineExtensionAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalPrice() == null ? 0.00 : formatter.format(eii.getTotalPrice())) + "</pay4:Value>\n"
                    + "                                </pay4:LineExtensionAmount>\n"
                    + "                                <pay4:PayableAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalMoney() == null ? 0.00 : formatter.format(eii.getTotalMoney())) + "</pay4:Value>\n"
                    + "                                </pay4:PayableAmount>\n"
                    + "                                 <pay4:PayableRoundingAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getRoundingPrice() == null ? 0.00 : formatter.format(eii.getRoundingPrice())) + "</pay4:Value>\n"
                    + "                                </pay4:PayableRoundingAmount>"
                    + "                                <pay4:TaxExclusiveAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalPrice() == null ? 0.00 : formatter.format(eii.getTotalPrice())) + "</pay4:Value>\n"
                    + "                                </pay4:TaxExclusiveAmount>\n"
                    + "                                <pay4:TaxInclusiveAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalMoney() == null ? 0.00 : formatter.format(eii.getTotalMoney())) + "</pay4:Value>\n"
                    + "                                </pay4:TaxInclusiveAmount>\n"
                    + "                            </pay4:LegalMonetaryTotal>\n"
                    + "                            <pay4:Notes>\n"
                    + "                                <arr:string>" + eii.getDescription() + "</arr:string>\n"
                    + "                            </pay4:Notes> \n"
                    + pricingCurrency
                    + "                            <pay4:ProfileID>" + (eii.getInvoiceScenarioId() == 1 ? 1 : 2) + "</pay4:ProfileID>\n"
                    + "                            <pay4:TaxTotal>\n"
                    + "                                <pay4:TaxTotal>\n"
                    + "                                    <pay4:TaxAmount>\n"
                    + "                                        <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                        <pay4:Value>" + (eii.getTotalTax() == null ? 0.00 : formatter.format(eii.getTotalTax())) + "</pay4:Value>\n"
                    + "                                    </pay4:TaxAmount>\n"
                    + "                                    <pay4:TaxSubtotal>\n"
                    + "                                        <pay4:TaxSubtotal>\n"
                    + "                                            <pay4:TaxAmount>\n"
                    + "                                                <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                                <pay4:Value>" + (eii.getTotalTax() == null ? 0.00 : formatter.format(eii.getTotalTax())) + "</pay4:Value>\n"
                    + "                                            </pay4:TaxAmount>\n"
                    + "                                            <pay4:TaxCategorySchemeCode>0015</pay4:TaxCategorySchemeCode>\n"
                    + "                                            <pay4:TaxCategorySchemeName>Katma Değer Vergisi</pay4:TaxCategorySchemeName>\n"
                    + "                                        </pay4:TaxSubtotal>\n"
                    + "                                    </pay4:TaxSubtotal>\n"
                    + "                                </pay4:TaxTotal>\n"
                    + "                            </pay4:TaxTotal>\n"
                    + "                        </pay3:Body>\n"
                    + "                        <pay3:Header>\n"
                    + "                            <pay4:InvoiceMembershipType>" + eii.getTaxPayerTypeId() + "</pay4:InvoiceMembershipType>\n"
                    + "                            <pay4:InvoiceNumberPrefix>" + brSetting.geteInvoicePrefix() + "</pay4:InvoiceNumberPrefix>\n"
                    + "                            <pay4:InvoiceSendType>" + (eii.getDeliveryTypeId() == 1 ? 2 : 1) + "</pay4:InvoiceSendType>\n"
                    + "                            <pay4:InvoiceTypeCode>" + (eii.getType().getId() == 23 || eii.getType().getId() == 59 ? 1 : eii.getType().getId() == 27 ? 2 : 0) + "</pay4:InvoiceTypeCode>\n"
                    + "                            <pay4:IsDraft>false</pay4:IsDraft>\n"
                    + "                            <pay4:IsWayBill>true</pay4:IsWayBill>\n"
                    + "                            <pay4:ReceiverAlias>" + eii.getAccount().getEmail() + "</pay4:ReceiverAlias>\n"
                    + "                            <pay4:ReceiverIdentityNumber>" + eii.getAccount().getTaxNo() + "</pay4:ReceiverIdentityNumber>\n"
                    + "                            <pay4:SenderAlias>" + brSetting.geteInvoiceTagInfo() + "</pay4:SenderAlias>\n"
                    + "                            <pay4:TrackNumber>" + eii.getId() + "</pay4:TrackNumber>\n"
                    + "                        </pay3:Header>\n"
                    + "                    </pay3:OutgoingInvoice>";
        }

        data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:pay1=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO\" xmlns:pay3=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.SendInvoices\" xmlns:pay4=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.Shared\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:SendInvoices>\n"
                + "            <tem:request>\n"
                + "                <pay1:Header>\n"
                + "                    <pay1:InstitutionId>" + brSetting.geteInvoiceAccountCode() + "</pay1:InstitutionId>\n"
                + "                    <pay1:OriginatorUserId>0</pay1:OriginatorUserId>\n"
                + "                    <pay1:Password>" + brSetting.geteInvoicePassword() + "</pay1:Password>\n"
                + "                    <pay1:Username>" + brSetting.geteInvoiceUserName() + "</pay1:Username>\n"
                + "                </pay1:Header>\n"
                + "                <pay3:AcceptanceDateTime>" + sdf.format(date) + "</pay3:AcceptanceDateTime> \n"
                + "                <pay3:Invoices> \n"
                + items
                + " </pay3:Invoices> \n"
                + " </tem:request>\n"
                + "        </tem:SendInvoices>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";
        sendEInvoicewebService(data, listOfInvoices);
        return true;

    }

    //Hazırlanan xml datayı Innova web servise gönderir
    @Override
    public void sendEInvoicewebService(String data, List<EInvoice> listOfInvoices) {
        List<SendEInvoice> listResult = new ArrayList<>();
        List<SendEInvoice> listResult1 = new ArrayList<>();
        Date begin = new Date();
        BranchSetting brSetting = sessionBean.getLastBranchSetting();

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());

            try {

//                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
//                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IClientInterfaceService/SendInvoices");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                int returnCode = httpClient.executeMethod(methodPost);

                System.out.println("---data---" + data);

                br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;

                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }

                String result = sb.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                Document document = builder.parse(inputSource);
                if (returnCode == 200) {
                    System.out.println("----result----" + result);

                    NodeList returnHeader = document.getElementsByTagName("SendInvoicesResult").item(0).getChildNodes().item(0).getChildNodes();

                    if (document.getElementsByTagName("ResponseCode").item(0).getTextContent().equalsIgnoreCase("0000") && document.getElementsByTagName("Status").item(0).getTextContent().equalsIgnoreCase("0")) {
                        NodeList returnList = document.getElementsByTagName("SendInvoicesResult").item(0).getChildNodes().item(1).getChildNodes();

                        for (int j = 0; j < returnList.getLength(); j++) {
                            Element node = (Element) returnList.item(j);

                            if (node.getElementsByTagName("b:ResponseStatus").item(0).getTextContent().equalsIgnoreCase("0") && node.getElementsByTagName("b:ResponseCode").item(0).getTextContent().equalsIgnoreCase("0000")) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation") + " " + "Fatura NO=" + node.getElementsByTagName("b:InvoiceNumber").item(0).getTextContent()));
                                RequestContext.getCurrentInstance().update("grwProcessMessage");
                                SendEInvoice sei = new SendEInvoice();
                                sei.setResponseCode(node.getElementsByTagName("b:ResponseCode").item(0).getTextContent());
                                sei.setSendBeginDate(begin);
                                sei.setSendEndDate(new Date());
                                sei.setSendData(data);
                                sei.setResponseDescription("Gönderildi");
                                sei.setIntegrationInvoice(node.getElementsByTagName("b:ReferenceNumber").item(0).getTextContent());
                                sei.setGibInvoice(node.getElementsByTagName("b:InvoiceNumber").item(0).getTextContent());
                                sei.setInvoiceId(Integer.parseInt(node.getElementsByTagName("b:TrackNumber").item(0).getTextContent()));

                                listResult.add(sei);

                            } else if (node.getElementsByTagName("b:ResponseStatus").item(0).getTextContent().equalsIgnoreCase("0") && !node.getElementsByTagName("b:ResponseCode").item(0).getTextContent().equalsIgnoreCase("0000")) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + node.getElementsByTagName("b:ResponseDescription").item(0).getTextContent()));
                                RequestContext.getCurrentInstance().update("grwProcessMessage");
                                SendEInvoice sei = new SendEInvoice();
                                sei.setResponseCode(node.getElementsByTagName("b:ResponseCode").item(0).getTextContent());
                                sei.setSendBeginDate(begin);
                                sei.setSendEndDate(new Date());
                                sei.setSendData(data);
                                sei.setGibInvoice(node.getElementsByTagName("b:InvoiceNumber").item(0).getTextContent());
                                sei.setIntegrationInvoice(node.getElementsByTagName("b:ReferenceNumber").item(0).getTextContent());
                                sei.setResponseDescription(node.getElementsByTagName("b:ResponseDescription").item(0).getTextContent());
                                sei.setInvoiceId(Integer.parseInt(node.getElementsByTagName("b:TrackNumber").item(0).getTextContent()));

                                listResult1.add(sei);

                            } else {

                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + node.getElementsByTagName("b:ResponseDescription").item(0).getTextContent()));
                                RequestContext.getCurrentInstance().update("grwProcessMessage");

                                for (int i = 0; i < listOfInvoices.size(); i++) {

                                    SendEInvoice sei = new SendEInvoice();
                                    sei.setResponseCode(node.getElementsByTagName("b:ResponseCode").item(0).getTextContent());
                                    sei.setSendBeginDate(begin);
                                    sei.setSendEndDate(new Date());
                                    sei.setSendData(data);
                                    sei.setResponseDescription(node.getElementsByTagName("b:ResponseDescription").item(0).getTextContent());
                                    sei.setInvoiceId(listOfInvoices.get(i).getId());
                                    sei.setIsSend(false);
                                    listResult.add(sei);

                                }

                            }
                        }
                    } else {

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("Message").item(0).getTextContent()));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");

                    }
                } else {

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    System.out.println("------returncode---sendEInvoicewebService-" + returnCode);

                    for (EInvoice eii : listOfInvoices) {
                        SendEInvoice sei = new SendEInvoice();
                        sei.setResponseCode("false");
                        sei.setResponseDescription("Http Status :" + returnCode);
                        sei.setSendBeginDate(begin);
                        sei.setSendEndDate(new Date());
                        sei.setSendData(data);
                        sei.setInvoiceId(eii.getId());
                        sei.setIsSend(false);

                        listResult.add(sei);
                    }

                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("-----CATCH---sendEInvoicewebService-11--" + e.getMessage());
                for (int i = 0; i < listOfInvoices.size(); i++) {

                    SendEInvoice sei = new SendEInvoice();
                    sei.setResponseCode("Error");
                    sei.setSendBeginDate(begin);
                    sei.setSendEndDate(new Date());
                    sei.setSendData(data);
                    sei.setResponseDescription("Error:" + e.getMessage());
                    sei.setInvoiceId(listOfInvoices.get(i).getId());
                    sei.setIsSend(false);
                    listResult.add(sei);

                }
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("-----CATCH---sendEInvoicewebService-22--" + fe.getMessage());
                    }
                }

            }

        } catch (Exception ex) {
            System.out.println("-----CATCH---sendEInvoicewebService-3333--" + ex.getMessage());
            for (int i = 0; i < listOfInvoices.size(); i++) {

                SendEInvoice sei = new SendEInvoice();
                sei.setResponseCode("Error");
                sei.setSendBeginDate(begin);
                sei.setSendEndDate(new Date());
                sei.setSendData(data);
                sei.setResponseDescription("Error:" + ex.getMessage());
                sei.setInvoiceId(listOfInvoices.get(i).getId());
                sei.setIsSend(false);
                listResult.add(sei);

            }
        }
        if (!listResult.isEmpty()) {//liste loglanacak
            webServiceLog(listResult);
            //fatura durumları sorgulanacak
            invoiceStatusInquiry(listResult);
        }
        if (!listResult1.isEmpty()) {
            webServiceLog(listResult1);
            invoiceStatusInquiry(listResult1);
        }
    }

    //Hazırlanan xml datayı Uyumsoft web servise gönderir
    @Override
    public void sendEInvoiceUWebservice(String data, List<EInvoice> listOfInvoices) {
        List<SendEInvoice> listResult = new ArrayList<>();
        Date begin = new Date();
        BranchSetting brSetting = sessionBean.getLastBranchSetting();
        boolean requestStatus = false;

        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());

            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/SendInvoice");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                int returnCode = httpClient.executeMethod(methodPost);

                br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;

                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }

                String result = sb.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                Document document = builder.parse(inputSource);
                System.out.println("------data----" + data);
                System.out.println("----result----" + result);

                if (returnCode == 200) {

                    if (document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true") && document.getElementsByTagName("SendInvoiceResult").item(0).getChildNodes().getLength() != 0) {
                        NodeList returnList = document.getElementsByTagName("SendInvoiceResult").item(0).getChildNodes();

                        for (int i = 0; i < returnList.getLength(); i++) {
                            Element chieldInvoice = (Element) returnList.item(i);

                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation") + " " + "Fatura NO=" + chieldInvoice.getAttributes().getNamedItem("Number").getTextContent()));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");

                            SendEInvoice sei = new SendEInvoice();
                            sei.setResponseCode(document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent());
                            sei.setSendBeginDate(begin);
                            sei.setSendEndDate(new Date());
                            sei.setSendData(data);
                            sei.setResponseDescription("Gönderildi");
                            sei.setIntegrationInvoice(chieldInvoice.getAttributes().getNamedItem("Id").getTextContent());
                            sei.setGibInvoice(chieldInvoice.getAttributes().getNamedItem("Number").getTextContent());
                            sei.setInvoiceId(listOfInvoices.get(i).getId());
                            if (chieldInvoice.getAttributes().getNamedItem("InvoiceScenario").getTextContent().equalsIgnoreCase("eArchive")) {
                                sei.setIsSend(true);
                            }
                            listResult.add(sei);
                            requestStatus = true;

                        }
                    } else if (document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("false")) {

                        if (document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("Message").getTextContent().equalsIgnoreCase("Lütfen fatura alıcısını en az iki karakter olarak belirtiniz.") || document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("Message").getTextContent().equalsIgnoreCase("Lütfen fatura alıcısını en az iki karakter olarak belirtiniz..EFATURASRV")) {

                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sessionBean.loc.getString("ifthecurrenttypeisindividualpleaseentertheelevendigittcidnumberandifthecurrenttypeiscorporatepleaseenterthetendigittaxidentificationnumber")));
                            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

                        } else if (document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("Message").getTextContent().equalsIgnoreCase("e-fatura tipinde olan faturaların PROFILEID alanı EARSIV olamaz.") || document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("Message").getTextContent().equalsIgnoreCase("e-fatura tipinde olan faturaların PROFILEID alanı EARSIV olamaz..EFATURASRV")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sessionBean.loc.getString("currenttypeandinvoicetypedonotmatchsincetheeinvoiceisthetaxpayerinvoicetypeshouldbeeinvoice")));
                            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

                        } else if (document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("Message").getTextContent().equalsIgnoreCase("Verilen alias sistem kullanıcıları listesinde bulunmuyor..EFATURASRV")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + sessionBean.loc.getString("aliasisnotincludedinthelistofsystemuserspleasechecktheinvoicealiasinformation")));
                            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

                        } else {

                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + "  " + document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("Message").getTextContent()));
                            RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

                        }

                        for (int i = 0; i < this.listOfInvoices.size(); i++) {

                            SendEInvoice sei = new SendEInvoice();
                            sei.setResponseCode(document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent());
                            sei.setResponseDescription(document.getElementsByTagName("SendInvoiceResult").item(0).getAttributes().getNamedItem("Message").getTextContent());
                            sei.setSendBeginDate(begin);
                            sei.setSendEndDate(new Date());
                            sei.setSendData(data);
                            sei.setInvoiceId(listOfInvoices.get(i).getId());
                            sei.setIsSend(false);

                            listResult.add(sei);

                        }

                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                        RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("faultstring").item(0).getTextContent()));
                    RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");
                    for (EInvoice eii : listOfInvoices) {
                        SendEInvoice sei = new SendEInvoice();
                        sei.setResponseCode("false");
                        sei.setResponseDescription("Http Status :" + returnCode);
                        sei.setSendBeginDate(begin);
                        sei.setSendEndDate(new Date());
                        sei.setSendData(data);
                        sei.setInvoiceId(eii.getId());
                        sei.setIsSend(false);

                        listResult.add(sei);
                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                for (EInvoice eii : listOfInvoices) {

                    SendEInvoice sei = new SendEInvoice();
                    sei.setResponseCode("Error");
                    sei.setResponseDescription("Error :" + e.getMessage());
                    sei.setSendBeginDate(begin);
                    sei.setSendEndDate(new Date());
                    sei.setSendData(data);
                    sei.setInvoiceId(eii.getId());
                    sei.setIsSend(false);

                    listResult.add(sei);
                }
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                    }
                }
            }
        } catch (Exception ex) {
            for (EInvoice eii : listOfInvoices) {
                SendEInvoice sei = new SendEInvoice();
                sei.setResponseCode("Error");
                sei.setResponseDescription("Error :" + ex.getMessage());
                sei.setSendBeginDate(begin);
                sei.setSendEndDate(new Date());
                sei.setSendData(data);
                sei.setInvoiceId(eii.getId());
                sei.setIsSend(false);

                listResult.add(sei);
            }
        }
        if (!listResult.isEmpty()) {//liste loglanacak

            webServiceLog(listResult);
            if (requestStatus) {
                //fatura durumları sorgulanacak
                uInvoiceStatusInquiry(listResult);
            }
        }
    }

    public void webServiceLog(List<SendEInvoice> listResult) {

        Boolean isStatus = false;
        eInvoiceIntegrationDao.insertOrUpdateLog(listResult, isStatus);
    }

    // Innova web servisine gönderilen faturaların durumunu sorgular
    @Override
    public void invoiceStatusInquiry(List<SendEInvoice> listSendEInvoice) {
        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        Boolean isStatus;
        String data = "";

        List<SendEInvoice> listStatusResult;
        listStatusResult = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator('.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

        for (SendEInvoice sei : listSendEInvoice) {

            if (sei.getIntegrationInvoice() != null) {

                data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:pay1=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO\" xmlns:pay=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.InvoiceInquiry\">\n"
                        + "    <x:Header/>\n"
                        + "    <x:Body>\n"
                        + "        <tem:InvoiceInquiry>\n"
                        + "            <tem:request>\n"
                        + "                <pay1:Header>\n"
                        + "                    <pay1:InstitutionId>" + brSetting.geteInvoiceAccountCode() + "</pay1:InstitutionId>\n"
                        + "                    <pay1:OriginatorUserId>0</pay1:OriginatorUserId>\n"
                        + "                    <pay1:Password>" + brSetting.geteInvoicePassword() + "</pay1:Password>\n"
                        + "                    <pay1:Username>" + brSetting.geteInvoiceUserName() + "</pay1:Username>\n"
                        + "                </pay1:Header>\n"
                        + "                <pay:GetEmailDetailStatus>false</pay:GetEmailDetailStatus>\n"
                        + "                <pay:GetSmsDetailStatus>false</pay:GetSmsDetailStatus>\n"
                        + "                <pay:ReferenceNumber>" + sei.getIntegrationInvoice() + "</pay:ReferenceNumber>\n"
                        + "            </tem:request>\n"
                        + "        </tem:InvoiceInquiry>\n"
                        + "    </x:Body>\n"
                        + "</x:Envelope>";

            }

            try {

                HttpClient httpClient = new HttpClient();
                httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
                httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
                BufferedReader br = null;
                PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());

                try {

                    byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                    String authHeader = "Basic " + new String(encodedAuth);
                    methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IClientInterfaceService/InvoiceInquiry");
                    methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                    int returnCode = httpClient.executeMethod(methodPost);
                    System.out.println("-----data-----" + data);

                    if (returnCode == 200) {

                        br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                        StringBuilder sb = new StringBuilder();
                        String readLine;

                        while (((readLine = br.readLine()) != null)) {
                            sb.append(readLine);
                        }

                        String result = sb.toString();
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder;
                        builder = factory.newDocumentBuilder();
                        InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                        Document document = builder.parse(inputSource);
                        System.out.println("-----result----" + result);
                        NodeList returnHeader = document.getElementsByTagName("InvoiceInquiryResult").item(0).getChildNodes();

                        if (document.getElementsByTagName("ResponseCode").item(0).getTextContent().equalsIgnoreCase("0000") && document.getElementsByTagName("Status").item(0).getTextContent().equalsIgnoreCase("0")) {

                            sei.setInvoiceStatus(Integer.parseInt(document.getElementsByTagName("a:InvoiceStatus").item(0).getTextContent()));
                            if (Integer.parseInt(document.getElementsByTagName("a:InvoiceStatus").item(0).getTextContent()) == 3 || Integer.parseInt(document.getElementsByTagName("a:InvoiceStatus").item(0).getTextContent()) == 4 || Integer.parseInt(document.getElementsByTagName("a:InvoiceStatus").item(0).getTextContent()) == 5 || Integer.parseInt(document.getElementsByTagName("a:InvoiceStatus").item(0).getTextContent()) == 6) {
                                sei.setIsSend(true);
                                sei.setResponseDescription("İşlem Başarılı");

                            } else {
                                sei.setIsSend(false);
                                if (Integer.parseInt(document.getElementsByTagName("a:InvoiceStatus").item(0).getTextContent()) == 1) {
                                    sei.setResponseDescription("İşlem Başarısız");
                                }
                            }
                            listStatusResult.add(sei);

                        } else {
                            sei.setIsSend(false);
                            sei.setResponseCode(document.getElementsByTagName("ResponseCode").item(0).getTextContent());
                            sei.setResponseDescription(document.getElementsByTagName("Message").item(0).getTextContent());
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("Message").item(0).getTextContent()));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");

                        }
                    } else {

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        System.out.println("=====returncode=====" + returnCode);
                    }

                } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                    System.out.println("-----catch---invoiceStatusInquiry-11-" + e.getMessage());
                } finally {
                    methodPost.releaseConnection();
                    if (br != null) {
                        try {
                            br.close();
                        } catch (Exception fe) {
                            System.out.println("------catch---invoiceStatusInquiry-22--" + fe.getMessage());
                        }
                    }

                }

            } catch (Exception ex) {
                System.out.println("------catch---invoiceStatusInquiry--33--" + ex.getMessage());

            }

        }

        isStatus = true;

        eInvoiceIntegrationDao.insertOrUpdateLog(listStatusResult, isStatus);

    }

    // Uyumsoft web servisine gönderilen faturaların durumunu sorgular
    @Override
    public void uInvoiceStatusInquiry(List<SendEInvoice> listSendEInvoice) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        Boolean isStatus;
        String data = "";
        String invoiceNumber = "";

        List<SendEInvoice> listStatusResult;
        listStatusResult = new ArrayList<>();

        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator('.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        for (SendEInvoice sei : listSendEInvoice) {

            if (sei.getIntegrationInvoice() != null) {

                invoiceNumber = invoiceNumber + "\n"
                        + "          <tem:string>" + sei.getIntegrationInvoice() + "</tem:string>\n";

            }

        }

        data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:QueryOutboxInvoiceStatus>\n"
                + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                + "            <tem:invoiceIds>\n"
                + invoiceNumber
                + "            </tem:invoiceIds>\n"
                + "        </tem:QueryOutboxInvoiceStatus>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";
        System.out.println("-----data------" + data);
        try {

            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());

            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/QueryOutboxInvoiceStatus");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                int returnCode = httpClient.executeMethod(methodPost);

                br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;

                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }

                String result = sb.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                Document document = builder.parse(inputSource);
                System.out.println("-----result----" + result);
                if (returnCode == 200) {
                    if (document.getElementsByTagName("QueryOutboxInvoiceStatusResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true") && document.getElementsByTagName("QueryOutboxInvoiceStatusResult").item(0).getChildNodes().getLength() > 0) {

                        NodeList statusChields = document.getElementsByTagName("QueryOutboxInvoiceStatusResult").item(0).getChildNodes();

                        for (int i = 0; i < statusChields.getLength(); i++) {

                            Element chieldStatus = (Element) statusChields.item(i);

                            for (int j = 0; j < listSendEInvoice.size(); j++) {
                                if (listSendEInvoice.get(j).getIntegrationInvoice() != null) {

                                    if (listSendEInvoice.get(j).getIntegrationInvoice().equalsIgnoreCase(chieldStatus.getAttributes().getNamedItem("InvoiceId").getTextContent())) {
                                        listSendEInvoice.get(j).setInvoiceStatus(Integer.valueOf(chieldStatus.getAttributes().getNamedItem("StatusCode").getTextContent()));

                                        if (Integer.parseInt(chieldStatus.getAttributes().getNamedItem("StatusCode").getTextContent()) == 1000 || Integer.parseInt(chieldStatus.getAttributes().getNamedItem("StatusCode").getTextContent()) == 1100 || Integer.parseInt(chieldStatus.getAttributes().getNamedItem("StatusCode").getTextContent()) == 1200 || Integer.parseInt(chieldStatus.getAttributes().getNamedItem("StatusCode").getTextContent()) == 1300 || Integer.parseInt(chieldStatus.getAttributes().getNamedItem("StatusCode").getTextContent()) == 1400) {

                                            listSendEInvoice.get(j).setIsSend(true);
                                            listSendEInvoice.get(j).setResponseDescription("İşlem Başarılı");
                                        } else if (Integer.parseInt(chieldStatus.getAttributes().getNamedItem("StatusCode").getTextContent()) == 2000) {

                                            listSendEInvoice.get(j).setIsSend(false);
                                            listSendEInvoice.get(j).setResponseDescription("ERROR");
//                                            if (chieldStatus.getAttributes().getNamedItem("Message").getTextContent() == null) {
//                                                listSendEInvoice.get(j).setResponseDescription("ERROR");
//
//                                            } else {
//
//                                                listSendEInvoice.get(j).setResponseDescription(chieldStatus.getAttributes().getNamedItem("Message").getTextContent());
//
//                                            }

                                        } else {
                                            listSendEInvoice.get(j).setIsSend(false);
                                        }
                                        listStatusResult.add(listSendEInvoice.get(j));
                                    }
                                }
                            }
                        }
                    } else if (document.getElementsByTagName("QueryOutboxInvoiceStatusResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("false")) {

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("QueryOutboxInvoiceStatusResult").item(0).getAttributes().getNamedItem("Message").getTextContent()));
                        RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

                    }
                } else {

                    System.out.println("----returncode---" + returnCode);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("faultstring").item(0).getTextContent()));
                    RequestContext.getCurrentInstance().update("grwEInvoiceProcessMessage");

                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("-------catch--uInvoiceStatusInquiry--111-" + e.getMessage());

            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("------catch---uInvoiceStatusInquiry--222-" + fe.getMessage());
                    }
                }

            }

        } catch (Exception ex) {
            System.out.println("------catch----uInvoiceStatusInquiry---333--" + ex.getMessage());
        }

        isStatus = true;

        eInvoiceIntegrationDao.insertOrUpdateLog(listStatusResult, isStatus);

    }

    @Override
    public List<EInvoice> listOfEInvoices(String where, int operationType) {
        return eInvoiceIntegrationDao.listOfEInvoices(where, operationType);
    }

    @Override
    public List<SendEInvoice> listSendEInvocie() {
        return eInvoiceIntegrationDao.listSendEInvocie();
    }

    //E-Faturaları Innova web servisine göndermek için xml data hazırlar
    @Override
    public boolean sendEArchive(List<EInvoice> listEInvoice, BranchSetting obj) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();

        String data = "";
        String items = "";
        String accountingCustomerParty = "";
        String pricingCurrency = "";
        Date date = new Date();

        if (listOfInvoices == null) {
            listOfInvoices = new ArrayList<>();
        }
        listOfInvoices.clear();
        listOfInvoices.addAll(listEInvoice);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator('.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        ((DecimalFormat) formatter).setGroupingUsed(false);

        for (EInvoice eii : listOfInvoices) {
            String itemList = "";

            for (InvoiceItem iItem : eii.getListInvoiceItem()) {

                itemList = itemList + "                                <pay4:InvoiceLine>\n"
                        + "                                    <pay4:AllowanceCharges>\n"
                        + "                                        <pay4:AllowanceCharge>\n"
                        + "                                            <pay4:Amount>\n"
                        + "                                                <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                                <pay4:Value>" + (iItem.getDiscountPrice() == null ? 0.00 : iItem.getDiscountPrice()) + "</pay4:Value>\n"
                        + "                                            </pay4:Amount>\n"
                        + "                                            <pay4:ChargeIndicator>false</pay4:ChargeIndicator>\n"
                        + "                                            <pay4:MultiplierFactorNumeric>" + (iItem.getDiscountRate() == null ? 0.00 : iItem.getDiscountRate()) + "</pay4:MultiplierFactorNumeric>\n"
                        + "                                        </pay4:AllowanceCharge>\n"
                        + "                                    </pay4:AllowanceCharges>"
                        + "                                    <pay4:InvoicedQuantityUnitCode>" + iItem.getStock().getUnit().getInternationalCode() + "</pay4:InvoicedQuantityUnitCode>\n"
                        + "                                    <pay4:InvoicedQuantityValue>" + iItem.getQuantity() + "</pay4:InvoicedQuantityValue>\n"
                        + "                                    <pay4:Item>\n"
                        + "                                        <pay4:Name>" + iItem.getStock().getName() + "</pay4:Name>\n"
                        + "                                    </pay4:Item>\n"
                        + "                                    <pay4:LineExtensionAmount>\n"
                        + "                                        <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                        <pay4:Value>" + (iItem.getTotalPrice() == null ? 0.00 : formatter.format(iItem.getTotalPrice())) + "</pay4:Value>\n"
                        + "                                    </pay4:LineExtensionAmount>\n"
                        + "                                    <pay4:Price>\n"
                        + "                                        <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                        <pay4:Value>" + (iItem.getUnitPrice() == null ? 0.00 : formatter.format(iItem.getUnitPrice())) + "</pay4:Value>\n"
                        + "                                    </pay4:Price>\n"
                        + "                                       <pay4:TaxTotal>\n"
                        + "                                        <pay4:TaxAmount>\n"
                        + "                                            <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                            <pay4:Value>" + (iItem.getTotalTax() == null ? 0.00 : formatter.format(iItem.getTotalTax())) + "</pay4:Value>\n"
                        + "                                        </pay4:TaxAmount>\n"
                        + "                                        <pay4:TaxSubtotal>\n"
                        + "                                            <pay4:TaxSubtotal>\n"
                        + "                                                   <pay4:Percent>" + (iItem.getTaxRate() == null ? 0.00 : formatter.format(iItem.getTaxRate())) + "</pay4:Percent>"
                        + "                                                <pay4:TaxAmount>\n"
                        + "                                                    <pay4:CurrencyCode>" + iItem.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                        + "                                                    <pay4:Value>" + (iItem.getTotalTax() == null ? 0.00 : iItem.getTotalTax()) + "</pay4:Value>\n"
                        + "                                                </pay4:TaxAmount>\n"
                        + "                                                <pay4:TaxCategorySchemeCode>0015</pay4:TaxCategorySchemeCode>\n"
                        + "                                                <pay4:TaxCategorySchemeName>KATMA DEĞER VERGİSİ</pay4:TaxCategorySchemeName>\n"
                        + "                                            </pay4:TaxSubtotal>\n"
                        + "                                        </pay4:TaxSubtotal>\n"
                        + "                                    </pay4:TaxTotal>"
                        + "                                </pay4:InvoiceLine>\n";

            }

            if (eii.getAccount().getIsPerson()) {

                String nameSurname = eii.getAccount().getName();

                String[] words = nameSurname.split(" ");

                accountingCustomerParty = accountingCustomerParty + "<pay4:AccountingCustomerParty>\n"
                        + "                                <pay4:Party>\n"
                        + "                                      <pay4:Contact>\n"
                        + "                                        <pay4:ElectronicMail>" + eii.getAccount().getEmail() + "</pay4:ElectronicMail>\n"
                        + "                                        <pay4:Telephone>" + eii.getAccount().getPhone() + "</pay4:Telephone>"
                        + "                                    </pay4:Contact>\n"
                        + "                                    <pay4:PartyIdentifications>\n"
                        + "                                        <pay4:PartyIdentification>\n"
                        + "                                            <pay4:SchemeID>TCKN</pay4:SchemeID>\n"
                        + "                                            <pay4:Value>" + eii.getAccount().getTaxNo() + "</pay4:Value>\n"
                        + "                                        </pay4:PartyIdentification>\n"
                        + "                                    </pay4:PartyIdentifications>\n"
                        + "                                    <pay4:PartyName>" + eii.getAccount().getTitle() + "</pay4:PartyName>\n"
                        + "                                    <pay4:Person>\n"
                        + "                                        <pay4:FamilyName>" + words[1] + "</pay4:FamilyName>\n"
                        + "                                        <pay4:FirstName>" + words[0] + "</pay4:FirstName>\n"
                        + "                                        <pay4:Title>" + eii.getAccount().getTitle() + "</pay4:Title>\n"
                        + "                                    </pay4:Person>"
                        + "                                    <pay4:PostalAddress>\n"
                        + "                                        <pay4:CityName>" + eii.getAccount().getCity().getTag() + "</pay4:CityName>\n"
                        + "                                        <pay4:CitySubdivisionName>" + eii.getAccount().getCounty().getName() + "</pay4:CitySubdivisionName>\n"
                        + "                                        <pay4:Country>\n"
                        + "                                            <pay4:IdentificationCode>" + eii.getAccount().getCountry().getCode() + "</pay4:IdentificationCode>\n"
                        + "                                            <pay4:Name>" + eii.getAccount().getCountry().getTag() + "</pay4:Name>\n"
                        + "                                        </pay4:Country>\n"
                        + "                                    </pay4:PostalAddress>\n"
                        + "                                </pay4:Party>\n"
                        + "                            </pay4:AccountingCustomerParty>\n";
            } else {

                accountingCustomerParty = accountingCustomerParty + "<pay4:AccountingCustomerParty>\n"
                        + "                                <pay4:Party>\n"
                        + "                                      <pay4:Contact>\n"
                        + "                                        <pay4:ElectronicMail>" + eii.getAccount().getEmail() + "</pay4:ElectronicMail>\n"
                        + "                                        <pay4:Telephone>" + eii.getAccount().getPhone() + "</pay4:Telephone>"
                        + "                                    </pay4:Contact>\n"
                        + "                                    <pay4:PartyIdentifications>\n"
                        + "                                        <pay4:PartyIdentification>\n"
                        + "                                            <pay4:SchemeID>VKN</pay4:SchemeID>\n"
                        + "                                            <pay4:Value>" + eii.getAccount().getTaxNo() + "</pay4:Value>\n"
                        + "                                        </pay4:PartyIdentification>\n"
                        + "                                    </pay4:PartyIdentifications>\n"
                        + "                                    <pay4:PartyName>" + eii.getAccount().getTitle() + "</pay4:PartyName>\n"
                        + "                                    <pay4:PartyTax>" + eii.getAccount().getTaxOffice() + "</pay4:PartyTax>"
                        + "                                    <pay4:PostalAddress>\n"
                        + "                                        <pay4:CityName>" + eii.getAccount().getCity().getTag() + "</pay4:CityName>\n"
                        + "                                        <pay4:CitySubdivisionName>" + eii.getAccount().getCounty().getName() + "</pay4:CitySubdivisionName>\n"
                        + "                                        <pay4:Country>\n"
                        + "                                            <pay4:IdentificationCode>" + eii.getAccount().getCountry().getCode() + "</pay4:IdentificationCode>\n"
                        + "                                            <pay4:Name>" + eii.getAccount().getCountry().getTag() + "</pay4:Name>\n"
                        + "                                        </pay4:Country>\n"
                        + "                                    </pay4:PostalAddress>\n"
                        + "                                </pay4:Party>\n"
                        + "                            </pay4:AccountingCustomerParty>\n";

            }

            if (eii.getCurrency().getId() != eii.getListInvoiceItem().get(0).getCurrency().getId()) {
                pricingCurrency = pricingCurrency + "<pay4:PricingCurrencyCode>" + eii.getListInvoiceItem().get(0).getCurrency().getInternationalCode() + "</pay4:PricingCurrencyCode>\n"
                        + "                            <pay4:PricingExchangeRate>\n"
                        + "                                <pay4:CalculationRate>" + eii.getListInvoiceItem().get(0).getExchangeRate() + "</pay4:CalculationRate>\n"
                        + "                                <pay4:Date>" + sdf.format(eii.getInvoiceDate()) + "</pay4:Date>\n"
                        + "                                <pay4:SourceCurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:SourceCurrencyCode>\n"
                        + "                                <pay4:TargetCurrencyCode>" + brSetting.getBranch().getCurrency().getInternationalCode() + "</pay4:TargetCurrencyCode>\n"
                        + "                            </pay4:PricingExchangeRate>";

            }

            items = items + " <pay3:OutgoingInvoice>\n"
                    + "                        <pay3:Body>\n"
                    + accountingCustomerParty
                    + "                            <pay4:AccountingSupplierParty>\n"
                    + "                                <pay4:Party>\n"
                    + "                                      <pay4:Contact>\n"
                    + "                                        <pay4:ElectronicMail>" + brSetting.getBranch().getMail() + "</pay4:ElectronicMail>\n"
                    + "                                    </pay4:Contact>\n"
                    + "                                    <pay4:PartyIdentifications>\n"
                    + "                                        <pay4:PartyIdentification>\n"
                    + "                                            <pay4:SchemeID>VKN</pay4:SchemeID>\n"
                    + "                                            <pay4:Value>" + brSetting.getBranch().getTaxNo() + "</pay4:Value>\n"
                    + "                                        </pay4:PartyIdentification>\n"
                    + "                                    </pay4:PartyIdentifications>\n"
                    + "                                    <pay4:PartyName>" + brSetting.getBranch().getTitle() + "</pay4:PartyName>\n"
                    + "                                    <pay4:PostalAddress>\n"
                    + "                                        <pay4:CityName>" + brSetting.getBranch().getCity().getTag() + "</pay4:CityName>\n"
                    + "                                        <pay4:CitySubdivisionName>" + brSetting.getBranch().getCounty().getName() + "</pay4:CitySubdivisionName>\n"
                    + "                                        <pay4:Country>\n"
                    + "                                            <pay4:IdentificationCode>" + brSetting.getBranch().getCountry().getCode() + "</pay4:IdentificationCode>\n"
                    + "                                            <pay4:Name>" + brSetting.getBranch().getCountry().getTag() + "</pay4:Name>\n"
                    + "                                        </pay4:Country>\n"
                    + "                                    </pay4:PostalAddress>\n"
                    + "                                </pay4:Party>\n"
                    + "                            </pay4:AccountingSupplierParty>\n"
                    + "                             <pay4:AllowanceCharges>\n"
                    + "                                <pay4:AllowanceCharge>\n"
                    + "                                    <pay4:Amount>\n"
                    + "                                        <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                        <pay4:Value>" + (eii.getDiscountPrice() == null ? 0.00 : formatter.format(eii.getDiscountPrice())) + "</pay4:Value>\n"
                    + "                                    </pay4:Amount>\n"
                    + "                                    <pay4:ChargeIndicator>false</pay4:ChargeIndicator>\n"
                    + "                                    <pay4:MultiplierFactorNumeric>" + (eii.getDiscountRate() == null ? 0.00 : formatter.format(eii.getDiscountRate())) + "</pay4:MultiplierFactorNumeric>\n"
                    + "                                </pay4:AllowanceCharge>\n"
                    + "                            </pay4:AllowanceCharges>"
                    + "                            <pay4:CopyIndicator>false</pay4:CopyIndicator>\n"
                    + "                            <pay4:DocumentCurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:DocumentCurrencyCode>\n"
                    + "                            <pay4:InvoiceLine>\n"
                    + itemList
                    + "                            </pay4:InvoiceLine>\n"
                    + "                            <pay4:InvoiceTypeCode>" + (eii.getType().getId() == 23 || eii.getType().getId() == 59 ? 1 : eii.getType().getId() == 27 ? 2 : 0) + "</pay4:InvoiceTypeCode>\n"
                    + "                            <pay4:IssueDateTime>" + sdf.format(eii.getInvoiceDate()) + "</pay4:IssueDateTime>\n"
                    + "                            <pay4:LegalMonetaryTotal>\n"
                    + "                                 <pay4:AllowanceTotalAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalDiscount() == null ? 0.00 : formatter.format(eii.getTotalDiscount())) + "</pay4:Value>\n"
                    + "                                </pay4:AllowanceTotalAmount>"
                    + "                                <pay4:LineExtensionAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalPrice() == null ? 0.00 : formatter.format(eii.getTotalPrice())) + "</pay4:Value>\n"
                    + "                                </pay4:LineExtensionAmount>\n"
                    + "                                <pay4:PayableAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalMoney() == null ? 0.00 : formatter.format(eii.getTotalMoney())) + "</pay4:Value>\n"
                    + "                                </pay4:PayableAmount>\n"
                    + "                                 <pay4:PayableRoundingAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getRoundingPrice() == null ? 0.00 : formatter.format(eii.getRoundingPrice())) + "</pay4:Value>\n"
                    + "                                </pay4:PayableRoundingAmount>"
                    + "                                <pay4:TaxExclusiveAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalPrice() == null ? 0.00 : formatter.format(eii.getTotalMoney())) + "</pay4:Value>\n"
                    + "                                </pay4:TaxExclusiveAmount>\n"
                    + "                                <pay4:TaxInclusiveAmount>\n"
                    + "                                    <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                    <pay4:Value>" + (eii.getTotalMoney() == null ? 0.00 : formatter.format(eii.getTotalMoney())) + "</pay4:Value>\n"
                    + "                                </pay4:TaxInclusiveAmount>\n"
                    + "                            </pay4:LegalMonetaryTotal>\n"
                    + "                            <pay4:Notes>\n"
                    + "                                <arr:string>" + eii.getDescription() + "</arr:string>\n"
                    + "                            </pay4:Notes>"
                    + pricingCurrency
                    + "                            <pay4:ProfileID>" + (eii.getInvoiceScenarioId() == 1 ? 1 : 2) + "</pay4:ProfileID>\n"
                    + "                            <pay4:TaxTotal>\n"
                    + "                                <pay4:TaxTotal>\n"
                    + "                                    <pay4:TaxAmount>\n"
                    + "                                        <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                        <pay4:Value>" + (eii.getTotalTax() == null ? 0.00 : formatter.format(eii.getTotalTax())) + "</pay4:Value>\n"
                    + "                                    </pay4:TaxAmount>\n"
                    + "                                    <pay4:TaxSubtotal>\n"
                    + "                                        <pay4:TaxSubtotal>\n"
                    + "                                            <pay4:TaxAmount>\n"
                    + "                                                <pay4:CurrencyCode>" + eii.getCurrency().getInternationalCode() + "</pay4:CurrencyCode>\n"
                    + "                                                <pay4:Value>" + (eii.getTotalTax() == null ? 0.00 : formatter.format(eii.getTotalTax())) + "</pay4:Value>\n"
                    + "                                            </pay4:TaxAmount>\n"
                    + "                                            <pay4:TaxCategorySchemeCode>0015</pay4:TaxCategorySchemeCode>\n"
                    + "                                            <pay4:TaxCategorySchemeName>Katma Değer Vergisi</pay4:TaxCategorySchemeName>\n"
                    + "                                        </pay4:TaxSubtotal>\n"
                    + "                                    </pay4:TaxSubtotal>\n"
                    + "                                </pay4:TaxTotal>\n"
                    + "                            </pay4:TaxTotal>\n"
                    + "                        </pay3:Body>\n"
                    + "                        <pay3:Header>\n"
                    + "                            <pay4:InvoiceMembershipType>" + eii.getTaxPayerTypeId() + "</pay4:InvoiceMembershipType>\n"
                    + "                            <pay4:InvoiceNumberPrefix>" + brSetting.geteArchivePrefix() + "</pay4:InvoiceNumberPrefix>\n"
                    + "                            <pay4:InvoiceSaleType>1</pay4:InvoiceSaleType>"
                    + "                            <pay4:InvoiceSendType>" + (eii.getDeliveryTypeId() == 1 ? 2 : 1) + "</pay4:InvoiceSendType>\n"
                    + "                            <pay4:InvoiceTypeCode>" + (eii.getType().getId() == 23 || eii.getType().getId() == 59 ? 1 : eii.getType().getId() == 27 ? 2 : 0) + "</pay4:InvoiceTypeCode>\n"
                    + "                            <pay4:IsDraft>false</pay4:IsDraft>\n"
                    + "                            <pay4:IsWayBill>true</pay4:IsWayBill>\n"
                    + "                            <pay4:ReceiverIdentityNumber>" + eii.getAccount().getTaxNo() + "</pay4:ReceiverIdentityNumber>\n"
                    + "                            <pay4:SenderAlias>" + brSetting.getBranch().getMail() + "</pay4:SenderAlias>\n"
                    + "                            <pay4:TrackNumber>" + eii.getId() + "</pay4:TrackNumber>\n"
                    + "                        </pay3:Header>\n"
                    + "                    </pay3:OutgoingInvoice>";
        }

        data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:pay1=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO\" xmlns:pay3=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.SendInvoices\" xmlns:pay4=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.Shared\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:SendInvoices>\n"
                + "            <tem:request>\n"
                + "                <pay1:Header>\n"
                + "                    <pay1:InstitutionId>" + brSetting.geteInvoiceAccountCode() + "</pay1:InstitutionId>\n"
                + "                    <pay1:OriginatorUserId>0</pay1:OriginatorUserId>\n"
                + "                    <pay1:Password>" + brSetting.geteInvoicePassword() + "</pay1:Password>\n"
                + "                    <pay1:Username>" + brSetting.geteInvoiceUserName() + "</pay1:Username>\n"
                + "                </pay1:Header>\n"
                + "                <pay3:AcceptanceDateTime>" + sdf.format(date) + "</pay3:AcceptanceDateTime> \n"
                + "                <pay3:Invoices> \n"
                + items
                + " </pay3:Invoices> \n"
                + " </tem:request>\n"
                + "        </tem:SendInvoices>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";
        sendEInvoicewebService(data, listOfInvoices);
        return true;

    }

    //E-Faturaları Uyumsoft web servisine göndermek için xml data hazırlar
    @Override
    public void sendUEInvoice(List<EInvoice> listEInvoice, BranchSetting obj) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        List<Exchange> pricingList = new ArrayList<>();
        String data = "";
        String items = "";
        String itemList = "";
        String scenario = "";
        String invoiceTypeCode = "";
        String partyIdentificationScheme = "";
        boolean isTaxAssessment = false;
        String[] names = new String[2];
        String familyName = "";

        if (listOfInvoices == null) {
            listOfInvoices = new ArrayList<>();
        }
        listOfInvoices.clear();
        listOfInvoices.addAll(listEInvoice);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator('.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        ((DecimalFormat) formatter).setGroupingUsed(false);

        int eInvoiceCount = obj.geteInvoiceCount();
        int eArchiveCount = obj.geteArchiveCount();

        for (EInvoice eii : listOfInvoices) {
            String alias = "";
            String ID = "";
            itemList = "";

            int year = 0;
            String years = "";
            String number = "";
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR); // yıl
            years = String.valueOf(year);

            int countNumber = 0; //Basamak sayısı

            int difference = 0;

            if (eii.getTaxPayerTypeId() == 1) {//E-Fatura

                if (eInvoiceCount != 0 && brSetting.geteInvoicePrefix() != null) {
                    int tempEInvoiceCount = eInvoiceCount;
                    while (tempEInvoiceCount != 0) {
                        // num = num/10
                        tempEInvoiceCount = tempEInvoiceCount / 10;
                        ++countNumber;
                    }
                    difference = 9 - countNumber;
                    for (int i = 1; i <= difference; i++) {
                        number = number + "0";
                    }
                    number = number + Integer.toString(eInvoiceCount);
                    ID = brSetting.geteInvoicePrefix() + years + number;
                    eInvoiceCount++;

                } else {
                    ID = "";
                }

            }
            if (eii.getAccount().getTagInfo() != null) {
                alias = eii.getAccount().getTagInfo();
            }
            String tax = "";
            partyIdentificationScheme = eii.getAccount().getTaxNo().length() == 10 ? "VKN" : eii.getAccount().getTaxNo().length() == 11 ? "TCKN" : "VKN";

            isTaxAssessment = false;
            int itemNo = 1;

            invoiceTypeCode = eii.getType().getId() == 27 ? "IADE" : "SATIS";

            if (eii.getType().getId() == 27) {
                scenario = "TEMELFATURA";
            } else {

                if (eii.getInvoiceScenarioId() == 1) {
                    scenario = "TEMELFATURA";
                } else if (eii.getInvoiceScenarioId() == 2) {

                    scenario = "TICARIFATURA";
                }
            }

            BigDecimal lineExtensionAmount = (eii.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? eii.getTotalPrice() : BigDecimal.ZERO).add(eii.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) == 1 ? eii.getTotalDiscount() : BigDecimal.ZERO);
            lineExtensionAmount = lineExtensionAmount.compareTo(BigDecimal.valueOf(0)) == 1 ? lineExtensionAmount : BigDecimal.ZERO;

            List<InvoiceItem> flattened = new ArrayList<>();
            flattened = eii.getListInvoiceItem().stream()
                    .collect(Collectors.groupingBy((invoiceitem) -> invoiceitem.getCurrency().getInternationalCode(),
                            Collectors.groupingBy(
                                    InvoiceItem::getTaxRate,
                                    Collectors.reducing(
                                            BigDecimal.ZERO,
                                            InvoiceItem::getTotalTax,
                                            BigDecimal::add))))
                    .entrySet()
                    .stream()
                    .flatMap(e1 -> e1.getValue()
                    .entrySet()
                    .stream()
                    .map(e2 -> new InvoiceItem(e2.getValue(), e2.getKey(), new Currency(e1.getKey()))))
                    .collect(Collectors.toList());

            for (InvoiceItem item : flattened) {

                System.out.println("-----xml data item for ");

                tax = tax + "\n"
                        + "                                <urn6:TaxSubtotal>\n"
                        + "                                    <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + item.getCurrency().getInternationalCode() + "\">" + formatter.format(item.getTotalTax()) + "</urn2:TaxAmount>\n"
                        + "                                    <urn2:Percent xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + formatter.format(item.getTaxRate()) + "</urn2:Percent>\n"
                        + "                                    <urn6:TaxCategory>\n";
                if ((item.getTaxRate().compareTo(BigDecimal.valueOf(0)) <= 0 || item.getTotalTax().compareTo(BigDecimal.valueOf(0)) <= 0) && eii.getType().getId() != 27) {
                    isTaxAssessment = true;
                    tax = tax + "\n"
                            + " <urn2:TaxExemptionReasonCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806</urn2:TaxExemptionReasonCode>\n"
                            + " <urn2:TaxExemptionReason xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806 Tütün mamülleri ve bazı alkollü içkiler</urn2:TaxExemptionReason>";
                }
                tax = tax + "\n"
                        + "                                        <urn6:TaxScheme>\n"
                        + "                                            <urn2:Name  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">KATMA DEĞER VERGİSİ</urn2:Name>\n"
                        + "                                            <urn2:TaxTypeCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">0015</urn2:TaxTypeCode>\n"
                        + "                                        </urn6:TaxScheme>\n"
                        + "                                    </urn6:TaxCategory>\n"
                        + "                                </urn6:TaxSubtotal>\n";
            }

            for (InvoiceItem iItem : eii.getListInvoiceItem()) {
                System.out.println("---liste item for---");
                BigDecimal lineExtensionAmountItem = (iItem.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalPrice() : BigDecimal.ZERO).add(iItem.getDiscountPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getDiscountPrice() : BigDecimal.ZERO);
                lineExtensionAmountItem = (lineExtensionAmountItem.compareTo(BigDecimal.valueOf(0)) == 1 ? lineExtensionAmountItem : BigDecimal.ZERO).add(iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalTax() : BigDecimal.ZERO);
                lineExtensionAmountItem = lineExtensionAmountItem.compareTo(BigDecimal.valueOf(0)) == 1 ? lineExtensionAmountItem : BigDecimal.ZERO;

                BigDecimal taxTotal = (iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalTax() : BigDecimal.ZERO).divide(iItem.getQuantity(), 4, RoundingMode.HALF_EVEN);

                BigDecimal unitPrice = (iItem.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getUnitPrice() : BigDecimal.ZERO).subtract(taxTotal.compareTo(BigDecimal.valueOf(0)) == 1 ? taxTotal : BigDecimal.ZERO);
                unitPrice = unitPrice.compareTo(BigDecimal.valueOf(0)) == 1 ? unitPrice : BigDecimal.ZERO;

                itemList = itemList + "               <urn6:InvoiceLine xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                        + "                            <urn2:ID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + itemNo + "</urn2:ID>\n"
                        + "                            <urn2:InvoicedQuantity  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" unitCode=\"" + iItem.getUnit().getInternationalCode() + "\">" + formatter.format(iItem.getQuantity()) + "</urn2:InvoicedQuantity>\n"
                        + "                            <urn2:LineExtensionAmount  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (lineExtensionAmountItem == null ? BigDecimal.ZERO : formatter.format(lineExtensionAmountItem)) + "</urn2:LineExtensionAmount>\n"
                        + "                            <urn6:AllowanceCharge>\n"
                        + "                                <urn2:ChargeIndicator xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">false</urn2:ChargeIndicator>\n"
                        + "                                <urn2:AllowanceChargeReason xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"></urn2:AllowanceChargeReason>\n"
                        + "                                <urn2:MultiplierFactorNumeric xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (iItem.getDiscountRate() == null ? BigDecimal.ZERO : formatter.format(iItem.getDiscountRate())) + "</urn2:MultiplierFactorNumeric>\n"
                        + "                                <urn2:Amount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (iItem.getDiscountPrice() == null ? BigDecimal.ZERO : formatter.format(iItem.getDiscountPrice())) + "</urn2:Amount>\n"
                        + "                            </urn6:AllowanceCharge>\n"
                        + "                            <urn6:TaxTotal>\n"
                        + "                                <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (iItem.getTotalTax() == null ? BigDecimal.ZERO : formatter.format(iItem.getTotalTax())) + "</urn2:TaxAmount>\n"
                        + "                                <urn6:TaxSubtotal>\n"
                        + "                                    <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (iItem.getTotalTax() == null ? BigDecimal.ZERO : formatter.format(iItem.getTotalTax())) + "</urn2:TaxAmount>\n"
                        + "                                    <urn2:Percent xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (iItem.getTaxRate() == null ? BigDecimal.ZERO : formatter.format(iItem.getTaxRate())) + "</urn2:Percent>\n"
                        + "                                    <urn6:TaxCategory>\n";
                if ((iItem.getTaxRate().compareTo(BigDecimal.valueOf(0)) <= 0 || iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) <= 0) && eii.getType().getId() != 27) {
                    itemList = itemList + "\n"
                            + " <urn2:TaxExemptionReasonCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806</urn2:TaxExemptionReasonCode>\n"
                            + " <urn2:TaxExemptionReason xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806 Tütün mamülleri ve bazı alkollü içkiler</urn2:TaxExemptionReason>";
                }
                itemList = itemList + "\n"
                        + "                                        <urn6:TaxScheme>\n"
                        + "                                            <urn2:Name  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">KATMA DEĞER VERGİSİ</urn2:Name>\n"
                        + "                                            <urn2:TaxTypeCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">0015</urn2:TaxTypeCode>\n"
                        + "                                        </urn6:TaxScheme>\n"
                        + "                                    </urn6:TaxCategory>\n"
                        + "                                </urn6:TaxSubtotal>\n"
                        + "                            </urn6:TaxTotal>\n"
                        + "                            <urn6:Item>\n"
                        + "                                <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + iItem.getStock().getName() + "</urn2:Name>\n"
                        + "                            </urn6:Item>\n"
                        + "                            <urn6:Price>\n"
                        + "                                <urn2:PriceAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + formatter.format(unitPrice) + "</urn2:PriceAmount>\n"
                        + "                            </urn6:Price>\n"
                        + "                        </urn6:InvoiceLine>";

                if (iItem.getCurrency().getId() != eii.getCurrency().getId()) {
                    Exchange exchange = new Exchange();
                    exchange.getCurrency().setInternationalCode(iItem.getCurrency().getInternationalCode());
                    exchange.setBuying(iItem.getExchangeRate() == null ? BigDecimal.ZERO : iItem.getExchangeRate());
                    pricingList.add(exchange);
                }

                itemNo++;
            }

            String pricing = "";
            String pricingExchangeRate = "";
            for (Exchange exc : pricingList) {
                pricing = pricing + "\n"
                        + "<urn2:PricingCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + exc.getCurrency().getInternationalCode() + "</urn2:PricingCurrencyCode>\n";
                pricingExchangeRate = pricingExchangeRate + "\n"
                        + "                        <urn6:PricingExchangeRate xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                        + "                            <urn2:SourceCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + exc.getCurrency().getInternationalCode() + "</urn2:SourceCurrencyCode>\n"
                        + "                            <urn2:TargetCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getCurrency().getInternationalCode() + "</urn2:TargetCurrencyCode>\n"
                        + "                            <urn2:CalculationRate xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (exc.getBuying() == null ? BigDecimal.ZERO : formatter.format(exc.getBuying())) + "</urn2:CalculationRate>\n"
                        + "                            <urn2:Date xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + sdf.format(eii.getInvoiceDate()) + "</urn2:Date>\n"
                        + "                        </urn6:PricingExchangeRate>\n";
            }

            if (isTaxAssessment) {
                invoiceTypeCode = "OZELMATRAH";
            }

            items = items + "\n "
                    + "<tem:InvoiceInfo LocalDocumentId=\"" + eii.getId() + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                    + "                    <tem:Invoice xmlns=\"http://tempuri.org/\">\n"
                    + "                        <urn2:UBLVersionID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">2.1</urn2:UBLVersionID>\n"
                    + "                        <urn2:CustomizationID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">TR1.2</urn2:CustomizationID>\n"
                    + "                        <urn2:ProfileID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + scenario + "</urn2:ProfileID>\n"
                    + "                        <urn2:ID schemeID=\"?\" schemeName=\"?\" schemeAgencyID=\"?\" schemeAgencyName=\"?\" schemeVersionID=\"?\" schemeDataURI=\"?\" schemeURI=\"?\">" + ID + "</urn2:ID>\n"
                    + "                        <urn2:CopyIndicator xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">false</urn2:CopyIndicator>\n"
                    + "                        <urn2:UUID schemeID=\"?\" schemeName=\"?\" schemeAgencyID=\"?\" schemeAgencyName=\"?\" schemeVersionID=\"?\" schemeDataURI=\"?\" schemeURI=\"?\"></urn2:UUID>\n"
                    + "                        <urn2:IssueDate xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + sdf.format(eii.getInvoiceDate()) + "</urn2:IssueDate>\n"
                    + "                        <urn2:IssueTime xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + sdfTime.format(eii.getInvoiceDate()) + "</urn2:IssueTime>\n"
                    + "                        <urn2:InvoiceTypeCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + invoiceTypeCode + "</urn2:InvoiceTypeCode>\n";
            if (eii.getDescription() != null) {
                items = items + "\n"
                        + "                        <urn2:Note xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getDescription() + "</urn2:Note>\n";
            }
            items = items + "\n"
                    + "                        <urn2:DocumentCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getCurrency().getInternationalCode() + "</urn2:DocumentCurrencyCode>\n"
                    + "                        <urn2:TaxCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getCurrency().getInternationalCode() + "</urn2:TaxCurrencyCode>\n"
                    + "                        <urn2:PaymentCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getCurrency().getInternationalCode() + "</urn2:PaymentCurrencyCode>\n"
                    + pricing
                    + "                        <urn2:LineCountNumeric  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getListInvoiceItem().size() + "</urn2:LineCountNumeric>\n"
                    + "                        <urn6:AccountingSupplierParty xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn6:Party>\n"
                    + "                                <urn6:PartyIdentification>\n"
                    + "                                    <urn2:ID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" schemeID=\"VKN\">" + brSetting.getBranch().getTaxNo() + "</urn2:ID>\n"
                    + "                                </urn6:PartyIdentification>\n"
                    + "                                <urn6:PartyName>\n"
                    + "                                    <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getName() + "</urn2:Name>\n"
                    + "                                </urn6:PartyName>\n"
                    + "                                <urn6:PostalAddress>\n"
                    + "                                    <urn2:StreetName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getAddress() + "</urn2:StreetName>\n"
                    + "                                    <urn2:CitySubdivisionName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + obj.getBranch().getCounty().getName() + "</urn2:CitySubdivisionName>\n"
                    + "                                    <urn2:CityName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + obj.getBranch().getCity().getTag() + "</urn2:CityName>\n"
                    + "                                    <urn2:PostalZone xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"></urn2:PostalZone>\n"
                    + "                                    <urn6:Country>\n"
                    + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + obj.getBranch().getCountry().getTag() + "</urn2:Name>\n"
                    + "                                    </urn6:Country>\n"
                    + "                                </urn6:PostalAddress>\n";
            if (brSetting.getBranch().getTaxOffice() != null) {
                items = items + "\n"
                        + "                                <urn6:PartyTaxScheme>\n"
                        + "                                    <urn6:TaxScheme>\n"
                        + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getTaxOffice() + "</urn2:Name>\n"
                        + "                                    </urn6:TaxScheme>\n"
                        + "                                </urn6:PartyTaxScheme>\n";

            }
            items = items + "\n"
                    + "                                <urn6:Contact>\n";
            if (brSetting.getBranch().getPhone() != null) {
                items = items + "\n"
                        + "                                 <urn2:Telephone xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getPhone() + "</urn2:Telephone>\n";
            }
            if (brSetting.getBranch().getMail() != null) {
                items = items + "\n"
                        + "                                 <urn2:ElectronicMail xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getMail() + "</urn2:ElectronicMail>\n";

            }

            items = items + "\n"
                    + "                                </urn6:Contact>\n"
                    + "                            </urn6:Party>\n"
                    + "                        </urn6:AccountingSupplierParty>\n"
                    + "                        <urn6:AccountingCustomerParty xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn6:Party>\n"
                    + "                                <urn6:PartyIdentification>\n"
                    + "                                    <urn2:ID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" schemeID=\"" + partyIdentificationScheme + "\">" + eii.getAccount().getTaxNo() + "</urn2:ID>\n"
                    + "                                </urn6:PartyIdentification>\n";

            if (!eii.getAccount().getIsPerson()) {

                items = items + "\n"
                        + "                                <urn6:PartyName>\n"
                        + "                                    <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getTitle() + "</urn2:Name>\n"
                        + "                                </urn6:PartyName>\n";
            }
            items = items + "\n"
                    + "                                <urn6:PostalAddress>\n";
            if (eii.getAccount().getAddress() != null) {
                items = items + "\n"
                        + "                                    <urn2:StreetName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getAddress() + "</urn2:StreetName>";

            }
            items = items + "\n"
                    + "                                    <urn2:CitySubdivisionName  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getCounty().getName() + "</urn2:CitySubdivisionName>\n"
                    + "                                    <urn2:CityName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getCity().getTag() + "</urn2:CityName>\n"
                    + "                                    <urn6:Country>\n"
                    + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getCountry().getTag() + "</urn2:Name>\n"
                    + "                                    </urn6:Country>\n"
                    + "                                </urn6:PostalAddress>\n";
            if (eii.getAccount().getTaxOffice() != null) {
                items = items + "\n"
                        + "                                <urn6:PartyTaxScheme>\n"
                        + "                                    <urn6:TaxScheme>\n"
                        + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getTaxOffice() + "</urn2:Name>\n"
                        + "                                    </urn6:TaxScheme>\n"
                        + "                                </urn6:PartyTaxScheme>\n";

            }
            items = items + "\n"
                    + "                                <urn6:Contact>\n";
            if (eii.getAccount().getPhone() != null) {
                items = items + "\n"
                        + "                               <urn2:Telephone xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getPhone() + "</urn2:Telephone>\n";
            }
            if (eii.getAccount().getEmail() != null) {
                items = items + "\n"
                        + "                               <urn2:ElectronicMail  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getEmail() + "</urn2:ElectronicMail>\n";
            }
            items = items + "\n"
                    + "                                </urn6:Contact>\n";

            if (eii.getAccount().getIsPerson()) {
                String nameSurname = eii.getAccount().getName();
                if (nameSurname.contains(" ")) {
                    names = nameSurname.split(" ");
                } else {
                    names[0] = eii.getAccount().getName();
                }

                int a = names.length;
                if (names.length > 1 && names[1] != null) {
                    for (int i = 1; i < names.length; i++) {
                        familyName = familyName + " " + names[i];
                    }
                } else {
                    familyName = names[0];
                }

                items = items + "\n"
                        + "<urn6:Person>\n"
                        + " <urn2:FirstName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + names[0] + "</urn2:FirstName>\n"
                        + " <urn2:FamilyName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + familyName + "</urn2:FamilyName>\n"
                        + " </urn6:Person>";
            }

            items = items + "\n"
                    + "                            </urn6:Party>\n"
                    + "                        </urn6:AccountingCustomerParty>\n"
                    + "                        <urn6:AllowanceCharge>\n"
                    + "                            <urn2:ChargeIndicator xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">false</urn2:ChargeIndicator>\n"
                    + "                            <urn2:MultiplierFactorNumeric xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (eii.getDiscountRate() == null ? BigDecimal.ZERO : formatter.format(eii.getDiscountRate())) + "</urn2:MultiplierFactorNumeric>\n"
                    + "                            <urn2:Amount currencyID=\"" + eii.getCurrency().getInternationalCode() + "\" xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (eii.getDiscountPrice() == null ? BigDecimal.ZERO : formatter.format(eii.getDiscountPrice())) + "</urn2:Amount>\n"
                    + "                        </urn6:AllowanceCharge> \n"
                    + pricingExchangeRate
                    + "                        <urn6:TaxTotal xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalTax() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalTax())) + "</urn2:TaxAmount>\n"
                    + tax
                    + "                        </urn6:TaxTotal>\n"
                    + "                        <urn6:LegalMonetaryTotal xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn2:LineExtensionAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (lineExtensionAmount == null ? BigDecimal.ZERO : formatter.format(lineExtensionAmount)) + "</urn2:LineExtensionAmount>\n"
                    + "                            <urn2:TaxExclusiveAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalPrice() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalPrice())) + "</urn2:TaxExclusiveAmount>\n"
                    + "                            <urn2:TaxInclusiveAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalMoney() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalMoney())) + "</urn2:TaxInclusiveAmount>\n"
                    + "                            <urn2:AllowanceTotalAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalDiscount() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalDiscount())) + "</urn2:AllowanceTotalAmount>\n"
                    + "                            <urn2:PayableAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalMoney() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalMoney())) + "</urn2:PayableAmount>\n"
                    + "                        </urn6:LegalMonetaryTotal>\n"
                    + itemList
                    + "                    </tem:Invoice>\n"
                    + "                    <tem:TargetCustomer VknTckn=\"" + eii.getAccount().getTaxNo() + "\" Alias=\"" + alias + "\" Title=\"" + eii.getAccount().getTitle() + "\"></tem:TargetCustomer>\n"
                    + "                    <tem:Scenario>Automated</tem:Scenario>\n"
                    + "                </tem:InvoiceInfo>";
        }

        data = data + "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:dsi=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:urn2=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:urn6=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:SendInvoice>\n"
                + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                + "            <tem:invoices>\n"
                + items
                + "            </tem:invoices>\n"
                + "        </tem:SendInvoice>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";

        data = data.replace("'", " ");
        data = data.replace("&", " ");

        sendEInvoiceUWebservice(data, listOfInvoices);
    }

    //E-Arşiv Faturaları Uyumsoft web servisine göndermek için xml data hazırlar
    @Override
    public void sendUEArchive(List<EInvoice> listEInvoice, BranchSetting obj) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        List<Exchange> pricingList = new ArrayList<>();
        String data = "";
        String items = "";
        String scenario = "";
        String invoiceTypeCode = "";
        String partyIdentificationScheme = "";
        String deliveryType = "";
        boolean isTaxAssessment = false;
        String[] names = new String[2];
        String familyName = "";

        if (listOfInvoices == null) {
            listOfInvoices = new ArrayList<>();
        }
        listOfInvoices.clear();
        listOfInvoices.addAll(listEInvoice);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator('.');
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        ((DecimalFormat) formatter).setGroupingUsed(false);

        int eInvoiceCount = obj.geteInvoiceCount();
        int eArchiveCount = obj.geteArchiveCount();

        for (EInvoice eii : listOfInvoices) {
            String ID = "";

            int year = 0;
            String years = "";
            String number = "";
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR); // yıl
            years = String.valueOf(year);

            int countNumber = 0; //Basamak sayısı

            int difference = 0;

            if (eii.getTaxPayerTypeId() == 2) {//E-Arşiv

                if (eArchiveCount != 0 && brSetting.geteArchivePrefix() != null) {
                    int tempEArchiveCount = eArchiveCount;
                    while (tempEArchiveCount != 0) {
                        // num = num/10
                        tempEArchiveCount = tempEArchiveCount / 10;
                        ++countNumber;
                    }
                    difference = 9 - countNumber;
                    for (int i = 1; i <= difference; i++) {
                        number = number + "0";
                    }
                    number = number + Integer.toString(eArchiveCount);
                    ID = brSetting.geteArchivePrefix() + years + number;
                    eArchiveCount++;

                } else {
                    ID = "";
                }

            }

            isTaxAssessment = false;
            String itemList = "";
            String tax = "";

            int itemNo = 1;
            scenario = "EARSIVFATURA";
            invoiceTypeCode = eii.getType().getId() == 27 ? "IADE" : "SATIS";
            partyIdentificationScheme = eii.getAccount().getTaxNo().length() == 10 ? "VKN" : eii.getAccount().getTaxNo().length() == 11 ? "TCKN" : "VKN";
            deliveryType = eii.getDeliveryTypeId() == 1 ? "Paper" : "Electronic";
            BigDecimal lineExtensionAmount = (eii.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? eii.getTotalPrice() : BigDecimal.ZERO).add(eii.getTotalDiscount().compareTo(BigDecimal.valueOf(0)) == 1 ? eii.getTotalDiscount() : BigDecimal.ZERO);
            lineExtensionAmount = lineExtensionAmount.compareTo(BigDecimal.valueOf(0)) == 1 ? lineExtensionAmount : BigDecimal.ZERO;

            List<InvoiceItem> flattened = new ArrayList<>();
            flattened = eii.getListInvoiceItem().stream()
                    .collect(Collectors.groupingBy((invoiceitem) -> invoiceitem.getCurrency().getInternationalCode(),
                            Collectors.groupingBy(
                                    InvoiceItem::getTaxRate,
                                    Collectors.reducing(
                                            BigDecimal.ZERO,
                                            InvoiceItem::getTotalTax,
                                            BigDecimal::add))))
                    .entrySet()
                    .stream()
                    .flatMap(e1 -> e1.getValue()
                    .entrySet()
                    .stream()
                    .map(e2 -> new InvoiceItem(e2.getValue(), e2.getKey(), new Currency(e1.getKey()))))
                    .collect(Collectors.toList());

            for (InvoiceItem item : flattened) {

                tax = tax + "\n"
                        + "                                <urn6:TaxSubtotal>\n"
                        + "                                    <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + item.getCurrency().getInternationalCode() + "\">" + formatter.format(item.getTotalTax()) + "</urn2:TaxAmount>\n"
                        + "                                    <urn2:Percent xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + formatter.format(item.getTaxRate()) + "</urn2:Percent>\n"
                        + "                                    <urn6:TaxCategory>\n";
                if ((item.getTaxRate().compareTo(BigDecimal.valueOf(0)) <= 0 || item.getTotalTax().compareTo(BigDecimal.valueOf(0)) <= 0) && eii.getType().getId() != 27) {
                    isTaxAssessment = true;
                    tax = tax + "\n"
                            + " <urn2:TaxExemptionReasonCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806</urn2:TaxExemptionReasonCode>\n"
                            + " <urn2:TaxExemptionReason xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806 Tütün mamülleri ve bazı alkollü içkiler</urn2:TaxExemptionReason>";
                }
                tax = tax + "\n"
                        + "                                        <urn6:TaxScheme>\n"
                        + "                                            <urn2:Name  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">KATMA DEĞER VERGİSİ</urn2:Name>\n"
                        + "                                            <urn2:TaxTypeCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">0015</urn2:TaxTypeCode>\n"
                        + "                                        </urn6:TaxScheme>\n"
                        + "                                    </urn6:TaxCategory>\n"
                        + "                                </urn6:TaxSubtotal>\n";
            }

            for (InvoiceItem iItem : eii.getListInvoiceItem()) {

                BigDecimal lineExtensionAmountItem = (iItem.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalPrice() : BigDecimal.ZERO).add(iItem.getDiscountPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getDiscountPrice() : BigDecimal.ZERO);
                lineExtensionAmountItem = (lineExtensionAmountItem.compareTo(BigDecimal.valueOf(0)) == 1 ? lineExtensionAmountItem : BigDecimal.ZERO).add(iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalTax() : BigDecimal.ZERO);
                lineExtensionAmountItem = lineExtensionAmountItem.compareTo(BigDecimal.valueOf(0)) == 1 ? lineExtensionAmountItem : BigDecimal.ZERO;

                BigDecimal taxTotal = (iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalTax() : BigDecimal.ZERO).divide(iItem.getQuantity(), 4, RoundingMode.HALF_EVEN);

                BigDecimal unitPrice = (iItem.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getUnitPrice() : BigDecimal.ZERO).subtract(taxTotal.compareTo(BigDecimal.valueOf(0)) == 1 ? taxTotal : BigDecimal.ZERO);
                unitPrice = unitPrice.compareTo(BigDecimal.valueOf(0)) == 1 ? unitPrice : BigDecimal.ZERO;

                itemList = itemList + "               <urn6:InvoiceLine xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                        + "                            <urn2:ID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + itemNo + "</urn2:ID>\n"
                        + "                            <urn2:InvoicedQuantity  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" unitCode=\"" + iItem.getUnit().getInternationalCode() + "\">" + formatter.format(iItem.getQuantity()) + "</urn2:InvoicedQuantity>\n"
                        + "                            <urn2:LineExtensionAmount  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (lineExtensionAmountItem == null ? BigDecimal.ZERO : formatter.format(lineExtensionAmountItem)) + "</urn2:LineExtensionAmount>\n"
                        + "                            <urn6:AllowanceCharge>\n"
                        + "                                <urn2:ChargeIndicator xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">false</urn2:ChargeIndicator>\n"
                        + "                                <urn2:AllowanceChargeReason xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"></urn2:AllowanceChargeReason>\n"
                        + "                                <urn2:MultiplierFactorNumeric xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (iItem.getDiscountRate() == null ? BigDecimal.ZERO : formatter.format(iItem.getDiscountRate())) + "</urn2:MultiplierFactorNumeric>\n"
                        + "                                <urn2:Amount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (iItem.getDiscountPrice() == null ? BigDecimal.ZERO : formatter.format(iItem.getDiscountPrice())) + "</urn2:Amount>\n"
                        + "                            </urn6:AllowanceCharge>\n"
                        + "                            <urn6:TaxTotal>\n"
                        + "                                <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (iItem.getTotalTax() == null ? BigDecimal.ZERO : formatter.format(iItem.getTotalTax())) + "</urn2:TaxAmount>\n"
                        + "                                <urn6:TaxSubtotal>\n"
                        + "                                    <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + (iItem.getTotalTax() == null ? BigDecimal.ZERO : formatter.format(iItem.getTotalTax())) + "</urn2:TaxAmount>\n"
                        + "                                    <urn2:Percent xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (iItem.getTaxRate() == null ? BigDecimal.ZERO : formatter.format(iItem.getTaxRate())) + "</urn2:Percent>\n"
                        + "                                    <urn6:TaxCategory>\n";
                if (iItem.getTaxRate().compareTo(BigDecimal.valueOf(0)) <= 0 || iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) <= 0) {
                    itemList = itemList + "\n"
                            + " <urn2:TaxExemptionReasonCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806</urn2:TaxExemptionReasonCode>\n"
                            + " <urn2:TaxExemptionReason xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">806 Tütün mamülleri ve bazı alkollü içkiler</urn2:TaxExemptionReason>";
                }
                itemList = itemList + "\n"
                        + "                                        <urn6:TaxScheme>\n"
                        + "                                            <urn2:Name  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">KATMA DEĞER VERGİSİ</urn2:Name>\n"
                        + "                                            <urn2:TaxTypeCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">0015</urn2:TaxTypeCode>\n"
                        + "                                        </urn6:TaxScheme>\n"
                        + "                                    </urn6:TaxCategory>\n"
                        + "                                </urn6:TaxSubtotal>\n"
                        + "                            </urn6:TaxTotal>\n"
                        + "                            <urn6:Item>\n"
                        + "                                <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + iItem.getStock().getName() + "</urn2:Name>\n"
                        + "                            </urn6:Item>\n"
                        + "                            <urn6:Price>\n"
                        + "                                <urn2:PriceAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + iItem.getCurrency().getInternationalCode() + "\">" + formatter.format(unitPrice) + "</urn2:PriceAmount>\n"
                        + "                            </urn6:Price>\n"
                        + "                        </urn6:InvoiceLine>";

                if (iItem.getCurrency().getId() != eii.getCurrency().getId()) {
                    Exchange exchange = new Exchange();
                    exchange.getCurrency().setInternationalCode(iItem.getCurrency().getInternationalCode());
                    exchange.setBuying(iItem.getExchangeRate() == null ? BigDecimal.ZERO : iItem.getExchangeRate());
                    pricingList.add(exchange);
                }

                itemNo++;
            }

            String pricing = "";
            String pricingExchangeRate = "";
            for (Exchange exc : pricingList) {
                pricing = pricing + "\n"
                        + "<urn2:PricingCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + exc.getCurrency().getInternationalCode() + "</urn2:PricingCurrencyCode>\n";

                pricingExchangeRate = pricingExchangeRate + "\n"
                        + "                        <urn6:PricingExchangeRate xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                        + "                            <urn2:SourceCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + exc.getCurrency().getInternationalCode() + "</urn2:SourceCurrencyCode>\n"
                        + "                            <urn2:TargetCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getCurrency().getInternationalCode() + "</urn2:TargetCurrencyCode>\n"
                        + "                            <urn2:CalculationRate xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (exc.getBuying() == null ? BigDecimal.ZERO : formatter.format(exc.getBuying())) + "</urn2:CalculationRate>\n"
                        + "                            <urn2:Date xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + sdf.format(eii.getInvoiceDate()) + "</urn2:Date>\n"
                        + "                        </urn6:PricingExchangeRate>\n";

            }

            if (isTaxAssessment) {
                invoiceTypeCode = "OZELMATRAH";
            }

            items = items + "\n"
                    + " <tem:InvoiceInfo LocalDocumentId=\"" + eii.getId() + "\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
                    + "                    <tem:Invoice xmlns=\"http://tempuri.org/\">\n"
                    + "                        <urn2:UBLVersionID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">2.1</urn2:UBLVersionID>\n"
                    + "                        <urn2:CustomizationID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">TR1.2</urn2:CustomizationID>\n"
                    + "                        <urn2:ProfileID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + scenario + "</urn2:ProfileID>\n"
                    + "                        <urn2:ID schemeID=\"?\" schemeName=\"?\" schemeAgencyID=\"?\" schemeAgencyName=\"?\" schemeVersionID=\"?\" schemeDataURI=\"?\" schemeURI=\"?\">" + ID + "</urn2:ID>\n"
                    + "                        <urn2:CopyIndicator xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">false</urn2:CopyIndicator>\n"
                    + "                        <urn2:UUID schemeID=\"?\" schemeName=\"?\" schemeAgencyID=\"?\" schemeAgencyName=\"?\" schemeVersionID=\"?\" schemeDataURI=\"?\" schemeURI=\"?\"></urn2:UUID>\n"
                    + "                        <urn2:IssueDate xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + sdf.format(eii.getInvoiceDate()) + "</urn2:IssueDate>\n"
                    + "                        <urn2:IssueTime xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + sdfTime.format(eii.getInvoiceDate()) + "</urn2:IssueTime>\n"
                    + "                        <urn2:InvoiceTypeCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + invoiceTypeCode + "</urn2:InvoiceTypeCode>\n";
            if (eii.getDescription() != null) {
                items = items + "\n"
                        + "                        <urn2:Note xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getDescription() + "</urn2:Note>\n";
            }
            items = items + "\n"
                    + "                        <urn2:DocumentCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getCurrency().getInternationalCode() + "</urn2:DocumentCurrencyCode>\n"
                    + "                        <urn2:TaxCurrencyCode xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getCurrency().getInternationalCode() + "</urn2:TaxCurrencyCode>\n"
                    + pricing
                    + "                        <urn2:LineCountNumeric  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getListInvoiceItem().size() + "</urn2:LineCountNumeric>\n"
                    + "                        <urn6:AccountingSupplierParty xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn6:Party>\n"
                    + "                                <urn6:PartyIdentification>\n"
                    + "                                    <urn2:ID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" schemeID=\"VKN\">" + brSetting.getBranch().getTaxNo() + "</urn2:ID>\n"
                    + "                                </urn6:PartyIdentification>\n"
                    + "                                <urn6:PartyName>\n"
                    + "                                    <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getName() + "</urn2:Name>\n"
                    + "                                </urn6:PartyName>\n"
                    + "                                <urn6:PostalAddress>\n"
                    + "                                    <urn2:StreetName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getAddress() + "</urn2:StreetName>\n"
                    + "                                    <urn2:CitySubdivisionName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + obj.getBranch().getCounty().getName() + "</urn2:CitySubdivisionName>\n"
                    + "                                    <urn2:CityName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + obj.getBranch().getCity().getTag() + "</urn2:CityName>\n"
                    + "                                    <urn2:PostalZone xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"></urn2:PostalZone>\n"
                    + "                                    <urn6:Country>\n"
                    + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + obj.getBranch().getCountry().getTag() + "</urn2:Name>\n"
                    + "                                    </urn6:Country>\n"
                    + "                                </urn6:PostalAddress>\n";
            if (brSetting.getBranch().getTaxOffice() != null) {
                items = items + "\n"
                        + "                                <urn6:PartyTaxScheme>\n"
                        + "                                    <urn6:TaxScheme>\n"
                        + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getTaxOffice() + "</urn2:Name>\n"
                        + "                                    </urn6:TaxScheme>\n"
                        + "                                </urn6:PartyTaxScheme>\n";

            }
            items = items + "\n"
                    + "                                <urn6:Contact>\n";
            if (brSetting.getBranch().getPhone() != null) {
                items = items + "\n"
                        + "                                 <urn2:Telephone xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getPhone() + "</urn2:Telephone>\n";
            }
            if (brSetting.getBranch().getMail() != null) {
                items = items + "\n"
                        + "                                 <urn2:ElectronicMail xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + brSetting.getBranch().getMail() + "</urn2:ElectronicMail>\n";

            }
            items = items + "\n"
                    + "                                </urn6:Contact>\n"
                    + "                            </urn6:Party>\n"
                    + "                        </urn6:AccountingSupplierParty>\n"
                    + "                        <urn6:AccountingCustomerParty xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn6:Party>\n"
                    + "                                <urn6:PartyIdentification>\n"
                    + "                                    <urn2:ID xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" schemeID=\"" + partyIdentificationScheme + "\">" + eii.getAccount().getTaxNo() + "</urn2:ID>\n"
                    + "                                </urn6:PartyIdentification>\n";

            if (!eii.getAccount().getIsPerson()) {

                items = items + "\n"
                        + "                                <urn6:PartyName>\n"
                        + "                                    <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getTitle() + "</urn2:Name>\n"
                        + "                                </urn6:PartyName>\n";
            }
            items = items + "\n"
                    + "                                <urn6:PostalAddress>\n";
            if (eii.getAccount().getAddress() != null) {
                items = items + "\n"
                        + "                <urn2:StreetName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getAddress() + "</urn2:StreetName>";
            }
            items = items + "\n"
                    + "                                    <urn2:CitySubdivisionName  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getCounty().getName() + "</urn2:CitySubdivisionName>\n"
                    + "                                    <urn2:CityName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getCity().getTag() + "</urn2:CityName>\n"
                    + "                                    <urn6:Country>\n"
                    + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getCountry().getTag() + "</urn2:Name>\n"
                    + "                                    </urn6:Country>\n"
                    + "                                </urn6:PostalAddress>\n";
            if (eii.getAccount().getTaxOffice() != null) {
                items = items + "\n"
                        + "                                <urn6:PartyTaxScheme>\n"
                        + "                                    <urn6:TaxScheme>\n"
                        + "                                        <urn2:Name xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getTaxOffice() + "</urn2:Name>\n"
                        + "                                    </urn6:TaxScheme>\n"
                        + "                                </urn6:PartyTaxScheme>\n";

            }
            items = items + "\n"
                    + "                                <urn6:Contact>\n";
            if (eii.getAccount().getPhone() != null) {
                items = items + "\n"
                        + "                               <urn2:Telephone xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getPhone() + "</urn2:Telephone>\n";
            }
            if (eii.getAccount().getEmail() != null) {
                items = items + "\n"
                        + "                               <urn2:ElectronicMail  xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + eii.getAccount().getEmail() + "</urn2:ElectronicMail>\n";
            }
            items = items + "\n"
                    + "                                </urn6:Contact>\n";
            if (eii.getAccount().getIsPerson()) {
                String nameSurname = eii.getAccount().getName();
                if (nameSurname.contains(" ")) {
                    names = nameSurname.split(" ");
                } else {
                    names[0] = eii.getAccount().getName();
                }
                if (names.length > 1 && names[1] != null) {
                    for (int i = 1; i < names.length; i++) {
                        familyName = familyName + " " + names[i];
                    }
                } else {

                    familyName = names[0];
                }

                items = items + "\n"
                        + "<urn6:Person>\n"
                        + " <urn2:FirstName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + names[0] + "</urn2:FirstName>\n"
                        + " <urn2:FamilyName xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + familyName + "</urn2:FamilyName>\n"
                        + " </urn6:Person>";
            }
            items = items + "\n"
                    + "                            </urn6:Party>\n"
                    + "                        </urn6:AccountingCustomerParty>\n"
                    + "                        <urn6:AllowanceCharge>\n"
                    + "                            <urn2:ChargeIndicator xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">false</urn2:ChargeIndicator>\n"
                    + "                            <urn2:MultiplierFactorNumeric xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (eii.getDiscountRate() == null ? BigDecimal.ZERO : formatter.format(eii.getDiscountRate())) + "</urn2:MultiplierFactorNumeric>\n"
                    + "                            <urn2:Amount currencyID=\"" + eii.getCurrency().getInternationalCode() + "\" xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" + (eii.getDiscountPrice() == null ? BigDecimal.ZERO : formatter.format(eii.getDiscountPrice())) + "</urn2:Amount>\n"
                    + "                        </urn6:AllowanceCharge> \n"
                    + pricingExchangeRate
                    + "                        <urn6:TaxTotal xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn2:TaxAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalTax() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalTax())) + "</urn2:TaxAmount>\n"
                    + tax
                    + "                        </urn6:TaxTotal>\n"
                    + "                        <urn6:LegalMonetaryTotal xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                    + "                            <urn2:LineExtensionAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (lineExtensionAmount == null ? BigDecimal.ZERO : formatter.format(lineExtensionAmount)) + "</urn2:LineExtensionAmount>\n"
                    + "                            <urn2:TaxExclusiveAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalPrice() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalPrice())) + "</urn2:TaxExclusiveAmount>\n"
                    + "                            <urn2:TaxInclusiveAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalMoney() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalMoney())) + "</urn2:TaxInclusiveAmount>\n"
                    + "                            <urn2:AllowanceTotalAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalDiscount() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalDiscount())) + "</urn2:AllowanceTotalAmount>\n"
                    + "                            <urn2:PayableAmount xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" currencyID=\"" + eii.getCurrency().getInternationalCode() + "\">" + (eii.getTotalMoney() == null ? BigDecimal.ZERO : formatter.format(eii.getTotalMoney())) + "</urn2:PayableAmount>\n"
                    + "                        </urn6:LegalMonetaryTotal>\n"
                    + itemList
                    + "                    </tem:Invoice>\n"
                    + "                    <tem:TargetCustomer VknTckn=\"" + eii.getAccount().getTaxNo() + "\" Title =\"" + eii.getAccount().getTitle() + "\"></tem:TargetCustomer>\n"
                    + "                   <tem:EArchiveInvoiceInfo DeliveryType=\"" + deliveryType + "" + "\">\n"
                    + "                    </tem:EArchiveInvoiceInfo>\n"
                    + "                    <tem:Scenario>Automated</tem:Scenario>\n"
                    + "                </tem:InvoiceInfo>";

        }

        data = data + "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:dsi=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:urn2=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:urn6=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:SendInvoice>\n"
                + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                + "            <tem:invoices>\n"
                + items
                + "            </tem:invoices>\n"
                + "        </tem:SendInvoice>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";

        data = data.replace("'", " ");
        data = data.replace("&", " ");

        sendEInvoiceUWebservice(data, listOfInvoices);

    }

    @Override
    public BranchSetting bringBranchAdress() {
        BranchSetting br = eInvoiceIntegrationDao.bringBranchAdress();
        return br;

    }

    @Override
    public int updateArchive(String ids, int updateType) {

        return eInvoiceIntegrationDao.updateArchive(ids, updateType);

    }

    public int createLogForArchive(List<EInvoice> listOfInsert) {
        return eInvoiceIntegrationDao.createLogForArchive(listOfInsert);
    }

}
