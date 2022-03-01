/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 17.02.2017 14:11:43
 */
package com.mepsan.marwiz.general.exchange.business;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.exchange.dao.IExchangeDao;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.service.item.business.ICheckItemService;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExchangeService implements IExchangeService {

    @Autowired
    private IExchangeDao exchangeDao;

    @Autowired
    private ApplicationBean applicationBean;

    @Autowired
    private ICheckItemService checkItemService;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setExchangeDao(IExchangeDao exchangeDao) {
        this.exchangeDao = exchangeDao;
    }

    public void setCheckItemService(ICheckItemService checkItemService) {
        this.checkItemService = checkItemService;
    }

    /**
     * bu metot gelen para birimleri arasındaki kuru veritabınından getirir.
     *
     * @param currency cevrilen para (USD)
     * @param responseCurrency cevrilecek para birimiş 8TL)
     * @return USD-> TL --3.67
     */
    @Override
    public BigDecimal bringExchangeRate(Currency currency, Currency responseCurrency, UserData userdata) {
        if (currency.getId() == responseCurrency.getId()) {
            return BigDecimal.ONE;
        } else {
            Exchange exchange = exchangeDao.bringExchangeRate(currency, responseCurrency, userdata);
            if (exchange.getBuying() != null) {
                return exchange.getBuying();
            } else {
                return BigDecimal.ONE;
            }
        }

    }

    /**
     * bu metot kurları webden ektıkten sonra listeyı xml e cerıp dao katmanını
     * tetıkler
     *
     * @return
     */
    @Override
    public Exchange updateExchange() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        int controlRecord = 0;
        Date exchangeDate = new Date();
        List<Exchange> exchangeControlList = new ArrayList<>();
        Exchange returnObj = new Exchange();
        exchangeControlList = controlRecurringRecord();

        if (!exchangeControlList.isEmpty()) {
            if (exchangeControlList.get(0).getIsThereNowDate() == 1) {
                controlRecord = 1;
            }
            exchangeDate = exchangeControlList.get(0).getExchangeDate();
        }


        if (controlRecord == 0) {
            Parameter parameter = applicationBean.getParameterMap().get("exchange");
            List<Exchange> listOfExchange = null;
            switch (parameter.getValue().trim()) {
                case "1"://Türkiye tcmb
                    listOfExchange = getExchangeTR();
                    break;

                case "2"://Merkezi entegrasyonu olanlar için Stawizden çeker
                    checkItemService.listExchange();
                    break;

                case "3":
                    break;
                default:
                    break;
            }
            if (!"2".equals(parameter.getValue().trim())) {
                if (listOfExchange.size() > 0) {
                    if (dateFormat.format(listOfExchange.get(0).getExchangeDate()).equals(dateFormat.format(exchangeDate))) {
                        returnObj.setErrorCode(-2);
                        returnObj.setExchangeDate(exchangeDate);
                    } else {
                        String xml = listToXml(listOfExchange);
                        xml = xml.substring(38, xml.length());//bastaki gereksiz tagları kaldırdık
                        exchangeDao.updateExchange(xml);
                        returnObj.setErrorCode(1);
                        returnObj.setExchangeDate(exchangeDate);
                    }
                } else {
                    returnObj.setErrorCode(-1);
                }
            } else {
                returnObj.setErrorCode(1);
            }
        } else {
            returnObj.setErrorCode(0);
        }
        return returnObj;
    }

    @Override
    public List<Exchange> findAll(UserData userdata) {
        return exchangeDao.findAll(userdata);
    }

    /**
     * bu metot kurları webden okur listeye cevirir.(icerisine currency idlerini
     * set eder)
     *
     * @return
     */
    public List<Exchange> getExchangeTR() {

        System.out.println("getExchangeTR");

        List<Exchange> exchanges = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            URL u = new URL("https://www.tcmb.gov.tr/kurlar/today.xml");
            URLConnection connection = u.openConnection();
            connection.setConnectTimeout(10000);

            Document doc = builder.parse(connection.getInputStream());
            NodeList nodes = doc.getElementsByTagName("Currency");
            NodeList nodes2 = doc.getElementsByTagName("Tarih_Date");
            Element el = (Element) nodes2.item(0);
            Date date = dateFormat.parse(el.getAttribute("Tarih"));
            BigDecimal unit;

            for (int i = 0; i < nodes.getLength(); i++) {

                Element element = (Element) nodes.item(i);
                //cevirilen para birimi
                if (getDecimal(getElementValue(element, "CrossrateUSD")).doubleValue() > 0) { //USD-> ...
                    Exchange exc = new Exchange();
                    exc.getCurrency().setCode("USD");
                    exc.getCurrency().setId(getCurrencyId(exc.getCurrency().getCode()));
                    exc.getCurrency().setTag("US Dollar");

                    exc.getResponseCurrency().setCode(element.getAttribute("CurrencyCode"));
                    exc.getResponseCurrency().setId(getCurrencyId(exc.getResponseCurrency().getCode()));
                    if (exc.getResponseCurrency().getId() > 0) {//para birimi sistemde var ise 
                        exc.getResponseCurrency().setTag(getElementValue(element, "Isim"));
                        exc.setBuying(getDecimal(getElementValue(element, "CrossrateUSD")));//kur
                        exc.setExchangeDate(date);
                        exchanges.add(exc);
                        //tersini ekledik
                        Exchange exch = new Exchange();
                        exch.setCurrency(exc.getResponseCurrency());
                        exch.setResponseCurrency(exc.getCurrency());
                        exch.setBuying(BigDecimal.ONE.divide(exc.getBuying(), 4, RoundingMode.HALF_EVEN));
                        exch.setSales(exc.getSales().doubleValue() > 0 ? BigDecimal.ONE.divide(exc.getSales(), 4, RoundingMode.HALF_EVEN) : BigDecimal.ZERO);
                        exc.setExchangeDate(date);
                        exchanges.add(exch);
                    }

                } else if (getDecimal(getElementValue(element, "CrossRateOther")).doubleValue() > 0) { //...-> USD
                    Exchange exc = new Exchange();
                    exc.getCurrency().setCode(element.getAttribute("CurrencyCode"));
                    exc.getCurrency().setId(getCurrencyId(exc.getCurrency().getCode()));
                    if (exc.getCurrency().getId() > 0) {//para birimi sistemde var ise
                        exc.getCurrency().setTag(getElementValue(element, "Isim"));
                        exc.setBuying(getDecimal(getElementValue(element, "CrossRateOther")));//kur

                        exc.getResponseCurrency().setCode("USD");
                        exc.getResponseCurrency().setId(getCurrencyId(exc.getResponseCurrency().getCode()));
                        exc.getResponseCurrency().setTag("US Dollar");
                        exc.setExchangeDate(date);
                        exchanges.add(exc);

                        //tersini ekledik
                        Exchange exch = new Exchange();
                        exch.setCurrency(exc.getResponseCurrency());
                        exch.setResponseCurrency(exc.getCurrency());
                        exch.setBuying(BigDecimal.ONE.divide(exc.getBuying(), 4, RoundingMode.HALF_EVEN));
                        exch.setSales(exc.getSales() != null ? BigDecimal.ONE.divide(exc.getSales(), 4, RoundingMode.HALF_EVEN) : BigDecimal.ZERO);
                        exch.setExchangeDate(date);
                        exchanges.add(exch);
                    }
                }

                // .... -> TL
                Exchange exchange = new Exchange();
                exchange.getCurrency().setCode(element.getAttribute("CurrencyCode"));
                exchange.getCurrency().setId(getCurrencyId(exchange.getCurrency().getCode()));
                if (exchange.getCurrency().getId() > 0) {// sistemde para bırımı var ise
                    exchange.getCurrency().setTag(getElementValue(element, "Isim"));
                    exchange.getResponseCurrency().setTag("Turkish Lira");
                    exchange.getResponseCurrency().setCode("TL");
                    exchange.getResponseCurrency().setId(getCurrencyId(exchange.getResponseCurrency().getCode()));
                    exchange.setExchangeDate(date);

                    unit = new BigDecimal(getElementValue(element, "Unit"));
                    exchange.setBuying(getDecimal(getElementValue(element, "BanknoteBuying")).divide(unit, 4, RoundingMode.HALF_EVEN));
                    exchange.setSales(getDecimal(getElementValue(element, "BanknoteSelling")).divide(unit, 4, RoundingMode.HALF_EVEN));
                    if (exchange.getBuying().doubleValue() == 0) {
                        exchange.setBuying(getDecimal(getElementValue(element, "ForexBuying")).divide(unit, 4, RoundingMode.HALF_EVEN));
                        exchange.setSales(getDecimal(getElementValue(element, "ForexSelling")).divide(unit, 4, RoundingMode.HALF_EVEN));
                    }
                    exchanges.add(exchange);
                    //tersini ekledik
                    Exchange exch = new Exchange();
                    exch.setCurrency(exchange.getResponseCurrency());
                    exch.setResponseCurrency(exchange.getCurrency());
                    exch.setBuying(BigDecimal.ONE.divide(exchange.getBuying(), 4, RoundingMode.HALF_EVEN));
                    exch.setSales(exchange.getSales().doubleValue() > 0 ? BigDecimal.ONE.divide(exchange.getSales(), 4, RoundingMode.HALF_EVEN) : BigDecimal.ZERO);
                    exch.setExchangeDate(date);
                    exchanges.add(exch);
                }

            }
        } catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
            e.printStackTrace();
        }
        return exchanges;
    }

    protected String getElementValue(Element parent, String label) {
        return getCharacterDataFromElement((Element) parent.getElementsByTagName(label).item(0));
    }

    private String getCharacterDataFromElement(Element e) {
        try {
            Node child = e.getFirstChild();
            if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                return cd.getData();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    protected BigDecimal getDecimal(String value) {
        if (value != null && !value.equals("")) {
            return new BigDecimal(value);
        }
        return BigDecimal.ZERO;
    }

    /**
     * gelen currency koduna göre sistemdeki currency id bilgisini döndurur
     *
     * @param code
     * @return
     */
    public int getCurrencyId(String code) {
        for (Currency currency : applicationBean.getCurrencies()) {
            if (currency.getCode().trim().equals(code)) {
                return currency.getId();
            }
        }
        return 0;
    }

    /**
     * bu metot gelen listeyi xml veriye cevirir
     *
     * @param list
     * @return
     */
    public String listToXml(List<Exchange> list) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            Element root = doc.createElement("exchanges");
            doc.appendChild(root);

            for (int i = 0; i < list.size(); i++) {

                Element Details = doc.createElement("exchange");
                root.appendChild(Details);

                Element currency = doc.createElement("currency_id");
                currency.appendChild(doc.createTextNode(String.valueOf(list.get(i).getCurrency().getId())));
                Details.appendChild(currency);

                Element responsecurrency = doc.createElement("responsecurrency_id");
                responsecurrency.appendChild(doc.createTextNode(String.valueOf(list.get(i).getResponseCurrency().getId())));
                Details.appendChild(responsecurrency);

                Element buy = doc.createElement("buying");
                buy.appendChild(doc.createTextNode(String.valueOf(list.get(i).getBuying() == null ? 0 : list.get(i).getBuying())));
                Details.appendChild(buy);

                Element sale = doc.createElement("sales");
                sale.appendChild(doc.createTextNode(String.valueOf(list.get(i).getSales() == null ? 0 : list.get(i).getSales())));
                Details.appendChild(sale);

                Element date = doc.createElement("date");
                date.appendChild(doc.createTextNode(dateFormat.format(list.get(i).getExchangeDate())));
                Details.appendChild(date);
            }
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc.getDocumentElement());

            trans.transform(source, result);
            String s = sw.toString();
            return s;

        } catch (ParserConfigurationException ex) {
        } catch (TransformerException ex) {
        }
        return "";

    }

    @Override
    public List<Exchange> controlRecurringRecord() {
        return exchangeDao.controlRecurringRecord();
    }

}
