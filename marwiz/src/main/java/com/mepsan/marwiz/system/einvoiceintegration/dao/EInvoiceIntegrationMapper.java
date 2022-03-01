package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.UserData;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.jdbc.core.RowMapper;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author elif.mart
 */
public class EInvoiceIntegrationMapper implements RowMapper<EInvoice> {
    
    @Override
    public EInvoice mapRow(ResultSet rs, int i) throws SQLException {
        EInvoice eInvoiceIntegration = new EInvoice();
        
        eInvoiceIntegration.setId(rs.getInt("invid"));
        eInvoiceIntegration.setIsPurchase(rs.getBoolean("invis_purchase"));
        eInvoiceIntegration.getAccount().setName(rs.getString("accname"));
        eInvoiceIntegration.getAccount().setTitle(rs.getString("acctitle"));
        eInvoiceIntegration.getdNumber().setId(rs.getInt("invdocumentnumber_id"));
        eInvoiceIntegration.setDocumentNumber(rs.getString("invdocumentnumber"));
        eInvoiceIntegration.setInvoiceDate(rs.getTimestamp("invinvoicedate"));
        eInvoiceIntegration.setTotalMoney(rs.getBigDecimal("invtotalmoney"));
        eInvoiceIntegration.getType().setTag(rs.getString("typdname"));
        eInvoiceIntegration.getCurrency().setId(rs.getInt("invcurrency_id"));
        eInvoiceIntegration.getCurrency().setTag(rs.getString("crrdname"));
        eInvoiceIntegration.getCurrency().setCode(rs.getString("crrcode"));
        eInvoiceIntegration.getCurrency().setInternationalCode(rs.getString("crrinternationalcode"));
        
        if (eInvoiceIntegration.getdNumber().getId() > 0) {
            eInvoiceIntegration.getdNumber().setActualNumber(rs.getInt("invdocumentnumber"));
            eInvoiceIntegration.setDocumentSerial(rs.getString("invdocumentserial"));
        } else {
            eInvoiceIntegration.setDocumentSerial(rs.getString("invdocumentserial"));
        }
        
        try {
            eInvoiceIntegration.setDateCreated(rs.getTimestamp("invc_time"));
        } catch (Exception e) {
        }
        
        try {
            eInvoiceIntegration.setDueDate(rs.getTimestamp("invduedate"));
            eInvoiceIntegration.setTotalPrice(rs.getBigDecimal("invtotalprice"));
            eInvoiceIntegration.setExchangeRate(rs.getBigDecimal("invexchangerate"));
            
            eInvoiceIntegration.getAccount().setId(rs.getInt("invaccount_id"));
            eInvoiceIntegration.getAccount().setIsPerson(rs.getBoolean("accis_person"));
            eInvoiceIntegration.getAccount().setPhone(rs.getString("accphone"));
            eInvoiceIntegration.getAccount().setEmail(rs.getString("accemail"));
            
            eInvoiceIntegration.getAccount().setAddress(rs.getString("accaddress"));
            eInvoiceIntegration.getAccount().setTaxNo(rs.getString("acctaxno"));
            eInvoiceIntegration.getAccount().setTaxOffice(rs.getString("acctaxoffice"));
            eInvoiceIntegration.getAccount().setBalance(rs.getBigDecimal("accbalance"));
            eInvoiceIntegration.getAccount().setIsEmployee(rs.getBoolean("accis_employee"));
            
            eInvoiceIntegration.getAccount().getCity().setId(rs.getInt("acccity_id"));
            eInvoiceIntegration.getAccount().getCity().setTag(rs.getString("ctydname"));
            eInvoiceIntegration.getAccount().getCountry().setId(rs.getInt("acccountry_id"));
            eInvoiceIntegration.getAccount().getCountry().setTag(rs.getString("ctrdname"));
            eInvoiceIntegration.getAccount().getCounty().setId(rs.getInt("acccounty_id"));
            eInvoiceIntegration.getAccount().getCounty().setName(rs.getString("cntyname"));
            eInvoiceIntegration.getAccount().setTagInfo(rs.getString("acctaginfo"));
            
            eInvoiceIntegration.setDescription(rs.getString("invdescription"));
            eInvoiceIntegration.setTotalDiscount(rs.getBigDecimal("invtotaldiscount"));
            eInvoiceIntegration.setDiscountRate(rs.getBigDecimal("invdiscountrate"));
            eInvoiceIntegration.setDiscountPrice(rs.getBigDecimal("invdiscountprice"));
            eInvoiceIntegration.setDispatchAddress(rs.getString("invdispatchaddress"));
            eInvoiceIntegration.setDispatchDate(rs.getTimestamp("invdispatchdate"));
            
            eInvoiceIntegration.getType().setId(rs.getInt("invtype_id"));
            eInvoiceIntegration.getStatus().setId(rs.getInt("invstatus_id"));
            eInvoiceIntegration.getStatus().setTag(rs.getString("sttdname"));
            eInvoiceIntegration.setTotalTax(rs.getBigDecimal("invtotaltax"));
            
            eInvoiceIntegration.setIsPeriodInvoice(rs.getBoolean("invis_periodinvoice"));
            
            eInvoiceIntegration.setRemainingMoney(rs.getBigDecimal("invremainingmoney"));
            eInvoiceIntegration.setRoundingPrice(rs.getBigDecimal("invroundingprice"));
            eInvoiceIntegration.setIsDiscountRate(rs.getBoolean("invis_discountrate"));
            
            eInvoiceIntegration.setInvoiceItemString(rs.getString("invoiceitem"));
            
            DocumentBuilder builder;
            try {
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource src = new InputSource();
                src.setCharacterStream(new StringReader(eInvoiceIntegration.getInvoiceItemString()));
                src.setEncoding("UTF-8");
                org.w3c.dom.Document doc = builder.parse(src);
                doc.getDocumentElement().normalize();
                
                NodeList list = doc.getElementsByTagName("item");
                for (int s = 0; s < list.getLength(); s++) {
                    InvoiceItem item = new InvoiceItem();
                    NodeList elements = list.item(s).getChildNodes();
                    
                    item.setId(Integer.valueOf(elements.item(0).getTextContent()));
                    item.getInvoice().setId(Integer.valueOf(elements.item(1).getTextContent()));
                    item.getStock().setId(Integer.valueOf(elements.item(2).getTextContent()));
                    BigDecimal bigDecimal = new BigDecimal(elements.item(3).getTextContent());
                    item.setQuantity(bigDecimal);
                    BigDecimal unitPrice = new BigDecimal(elements.item(4).getTextContent());
                    item.setUnitPrice(unitPrice);
                    BigDecimal totalPrice = new BigDecimal(elements.item(5).getTextContent());
                    item.setTotalPrice(totalPrice);
                    BigDecimal discountRate = new BigDecimal(elements.item(6).getTextContent());
                    item.setDiscountRate(discountRate);
                    BigDecimal discountPrice = new BigDecimal(elements.item(7).getTextContent());
                    item.setDiscountPrice(discountPrice);
                    
                    BigDecimal taxRate = new BigDecimal(elements.item(8).getTextContent());
                    item.setTaxRate(taxRate);
                    
                    BigDecimal totalTax = new BigDecimal(elements.item(9).getTextContent());
                    item.setTotalTax(totalTax);
                    
                    BigDecimal exchangeRate = new BigDecimal(elements.item(10).getTextContent());
                    item.setExchangeRate(exchangeRate);
                    
                    item.getStock().setName(elements.item(11).getTextContent());
                    item.getUnit().setId(Integer.valueOf(elements.item(12).getTextContent()));
                    item.getUnit().setName(elements.item(13).getTextContent());
                    item.getUnit().setInternationalCode(elements.item(14).getTextContent());
                    item.getCurrency().setId(Integer.valueOf(elements.item(15).getTextContent()));
                    item.getCurrency().setCode(elements.item(16).getTextContent());
                    item.getCurrency().setInternationalCode(elements.item(17).getTextContent());
                    item.getCurrency().setTag(elements.item(18).getTextContent());
                    
                    eInvoiceIntegration.getListInvoiceItem().add(item);
                    
                }
            } catch (ParserConfigurationException ex) {
            } catch (SAXException | IOException ex) {
            }
            
        } catch (Exception e) {
        }
        
        try {
            
            eInvoiceIntegration.setTaxPayerTypeId(rs.getInt("invtaxpayertype_id"));
            eInvoiceIntegration.setDeliveryTypeId(rs.getInt("invdeliverytype_id"));
            eInvoiceIntegration.setInvoiceScenarioId(rs.getInt("invinvoicescenario_id"));
            eInvoiceIntegration.getSendEInvoice().setGibInvoice(rs.getString("lseigibinvoice"));
            eInvoiceIntegration.getSendEInvoice().setIsSend(rs.getBoolean("lseiis_send"));
            eInvoiceIntegration.getSendEInvoice().setResponseDescription(rs.getString("lseiresponsedescription"));
            eInvoiceIntegration.getSendEInvoice().setId(rs.getInt("lseiid"));
            eInvoiceIntegration.getSendEInvoice().setSendData(rs.getString("lseisenddata"));
            eInvoiceIntegration.getSendEInvoice().setInvoiceId(rs.getInt("lseiinvoiceid"));
            eInvoiceIntegration.getSendEInvoice().setSendBeginDate(rs.getTimestamp("lseisendbegindate"));
            eInvoiceIntegration.getSendEInvoice().setSendEndDate(rs.getTimestamp("lseisendenddate"));
            eInvoiceIntegration.getSendEInvoice().setResponseCode(rs.getString("lseiresponsecode"));
            eInvoiceIntegration.getSendEInvoice().setIntegrationInvoice(rs.getString("lseiintegrationinvoice"));
            eInvoiceIntegration.getSendEInvoice().setInvoiceStatus(rs.getInt("lseiinvoicestatus"));
            
        } catch (Exception e) {
        }
        
        try {
            
            eInvoiceIntegration.setDateCreated(rs.getTimestamp("invc_time"));
        } catch (Exception e) {
        }
        
        return eInvoiceIntegration;
    }
    
}
