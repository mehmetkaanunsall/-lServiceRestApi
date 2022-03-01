/**
 * Bu sınıf ApplicationBean ile ApplicationDao sınıfı arasında iletişim sağlar.
 * Ayrıca ülkeleri, modüllerü, dilleri, statüleri, tipleri ve para birimlerini applicationscope da saklar.
 *
 *
 * @author Salem walaa Abdulhadie
 *
 * @date   20.01.2018 17:01:16
 */
package com.mepsan.marwiz.general.appllication.business;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.appllication.dao.ApplicationDao;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.model.admin.Button;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.wot.ApplicationList;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.admin.Tab;
import com.mepsan.marwiz.general.model.admin.TabDictionary;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.ScheduledJobTrigger;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Language;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.Dictionary;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ApplicationService implements IApplicationService {

    private final Gson gson = new Gson();

    @Autowired
    private ApplicationDao applicationDao;

    public void setApplicationDao(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
    }

    /**
     * Parametre olarak gelen json veriyi Status listesine çevirir.
     *
     * @param xml
     * @return Status listesi
     */
    @Override
    public List<Status> customizeStatusXml(String xml) {
        List<Status> listofStatus = new ArrayList<>();

        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("status");
            for (int s = 0; s < list.getLength(); s++) {
                Status status = new Status();
                NodeList elements = list.item(s).getChildNodes();
                status.setId(Integer.valueOf(elements.item(0).getTextContent()));
                status.setItem(new Item(Integer.valueOf(elements.item(1).getTextContent())));

                NodeList langList = elements.item(2).getChildNodes();
                Map<Integer, Dictionary<Status>> nameMap = new HashMap<>();
                for (int c = 0; c < langList.getLength(); c++) {
                    Dictionary<Status> dictionary = new Dictionary<>();
                    NodeList countryLangElements = langList.item(c).getChildNodes();
                    dictionary.setObject(status);
                    dictionary.setLanguage(new Language(Integer.valueOf(countryLangElements.item(0).getTextContent())));
                    dictionary.setName(countryLangElements.item(1).getTextContent());
                    nameMap.put(Integer.valueOf(countryLangElements.item(0).getTextContent()), dictionary);
                }
                status.setNameMap(nameMap);
                listofStatus.add(status);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listofStatus;
    }

    /**
     * Parametre olarak gelen json veriyi Type listesine çevirir.
     *
     * @param xml
     * @return Type listesi
     */
    @Override
    public List<Type> customizeTypeXml(String xml) {
        List<Type> listofTypes = new ArrayList<>();

        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("type");
            for (int s = 0; s < list.getLength(); s++) {
                Type type = new Type();
                NodeList elements = list.item(s).getChildNodes();
                type.setId(Integer.valueOf(elements.item(0).getTextContent()));
                type.setItem(new Item(Integer.valueOf(elements.item(1).getTextContent())));

                NodeList langList = elements.item(2).getChildNodes();
                Map<Integer, Dictionary<Type>> nameMap = new HashMap<>();
                for (int c = 0; c < langList.getLength(); c++) {
                    Dictionary<Type> dictionary = new Dictionary<>();
                    NodeList countryLangElements = langList.item(c).getChildNodes();
                    dictionary.setObject(type);
                    dictionary.setLanguage(new Language(Integer.valueOf(countryLangElements.item(0).getTextContent())));
                    dictionary.setName(countryLangElements.item(1).getTextContent());
                    nameMap.put(Integer.valueOf(countryLangElements.item(0).getTextContent()), dictionary);
                }
                type.setNameMap(nameMap);
                listofTypes.add(type);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listofTypes;
    }

    /**
     * Parametre olarak gelen json veriyi Language listesine çevirir.
     *
     * @param xml
     * @return Language listesi
     */
    @Override
    public List<Language> customizeLangXml(String xml) {
        List<Language> listofLanguages = new ArrayList<>();

        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("language");
            for (int s = 0; s < list.getLength(); s++) {
                Language language = new Language();
                NodeList elements = list.item(s).getChildNodes();
                language.setId(Integer.valueOf(elements.item(0).getTextContent()));
                language.setCode(elements.item(1).getTextContent());

                NodeList langList = elements.item(2).getChildNodes();
                Map<Integer, Dictionary<Language>> nameMap = new HashMap<>();
                for (int c = 0; c < langList.getLength(); c++) {
                    Dictionary<Language> dictionary = new Dictionary<>();
                    NodeList countryLangElements = langList.item(c).getChildNodes();
                    dictionary.setObject(language);
                    dictionary.setLanguage(new Language(Integer.valueOf(countryLangElements.item(0).getTextContent())));
                    dictionary.setName(countryLangElements.item(1).getTextContent());
                    nameMap.put(Integer.valueOf(countryLangElements.item(0).getTextContent()), dictionary);
                }
                language.setNameMap(nameMap);
                listofLanguages.add(language);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listofLanguages;
    }

    /**
     * Parametre olarak gelen json veriyi Currency listesine çevirir.
     *
     * @param xml
     * @return Currency listesi
     */
    @Override
    public List<Currency> customizeCurrencyXml(String xml) {
        List<Currency> listofCurrencies = new ArrayList<>();
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("currency");
            for (int s = 0; s < list.getLength(); s++) {
                Currency currency = new Currency();
                NodeList elements = list.item(s).getChildNodes();
                currency.setId(Integer.valueOf(elements.item(0).getTextContent()));
                currency.setCode(elements.item(1).getTextContent());
                currency.setSign(elements.item(2).getTextContent());
                currency.setInternationalCode(elements.item(3).getTextContent());

                NodeList langList = elements.item(4).getChildNodes();
                Map<Integer, Dictionary<Currency>> nameMap = new HashMap<>();
                for (int c = 0; c < langList.getLength(); c++) {
                    Dictionary<Currency> dictionary = new Dictionary<Currency>();
                    NodeList countryLangElements = langList.item(c).getChildNodes();
                    dictionary.setObject(currency);
                    dictionary.setLanguage(new Language(Integer.valueOf(countryLangElements.item(0).getTextContent())));
                    dictionary.setName(countryLangElements.item(1).getTextContent());
                    nameMap.put(Integer.valueOf(countryLangElements.item(0).getTextContent()), dictionary);
                }
                currency.setNameMap(nameMap);
                listofCurrencies.add(currency);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listofCurrencies;
    }

    @Override
    public List<Parameter> parameterList(String xml) {
        List<Parameter> result = new ArrayList<>();
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("parameter");
            for (int s = 0; s < list.getLength(); s++) {
                Parameter parameter = new Parameter();
                NodeList elements = list.item(s).getChildNodes();
                parameter.setId(Integer.valueOf(elements.item(0).getTextContent()));
                parameter.setKeyword(elements.item(1).getTextContent());
                parameter.setName(elements.item(3).getTextContent());
                parameter.setValue(elements.item(2).getTextContent());
                parameter.setDescription(elements.item(4).getTextContent());

                result.add(parameter);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        // System.out.printFln("*** "+result.get(0).getName());
        return result;
    }

    @Override
    public List<Country> countryListXml() {
        List<Country> countries = new ArrayList<>();
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(applicationDao.countryListXml()));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();
            NodeList countriesList = doc.getElementsByTagName("country");
            for (int s = 0; s < countriesList.getLength(); s++) {
                Country country = new Country();
                NodeList countryElements = countriesList.item(s).getChildNodes();
                country.setId(Integer.valueOf(countryElements.item(0).getTextContent()));
                country.setCode(countryElements.item(1).getTextContent());
                country.setLatitude(countryElements.item(2).getTextContent());
                country.setLongitude(countryElements.item(3).getTextContent());
                country.setZoom(Integer.valueOf(countryElements.item(4).getTextContent()));
                country.setTelephoneCode(countryElements.item(5).getTextContent());
                if (countryElements.item(6) != null) {
                    NodeList countryLangList = countryElements.item(6).getChildNodes();
                    Map<Integer, Dictionary<Country>> nameMap = new HashMap<>();
                    for (int c = 0; c < countryLangList.getLength(); c++) {
                        Dictionary<Country> countryDictionary = new Dictionary<>();
                        NodeList countryLangElements = countryLangList.item(c).getChildNodes();
                        countryDictionary.setObject(country);
                        countryDictionary.setLanguage(new Language(Integer.valueOf(countryLangElements.item(0).getTextContent())));
                        countryDictionary.setName(countryLangElements.item(1).getTextContent());
                        nameMap.put(Integer.valueOf(countryLangElements.item(0).getTextContent()), countryDictionary);
                    }

                    country.setNameMap(nameMap);
                }
                if (countryElements.item(7) != null) {
                    NodeList citiesList = countryElements.item(7).getChildNodes();
                    List<City> cities = new ArrayList<>();
                    for (int a = 0; a < citiesList.getLength(); a++) {
                        City city = new City();
                        NodeList cityElements = citiesList.item(a).getChildNodes();
                        city.setId(Integer.valueOf(cityElements.item(0).getTextContent()));
                        city.setPlateCode(cityElements.item(1).getTextContent());
                        city.setTelephoneCode(cityElements.item(2).getTextContent());
                        if (cityElements.item(3) != null) {
                            NodeList cityLangList = cityElements.item(3).getChildNodes();
                            Map<Integer, Dictionary<City>> nameMap1 = new HashMap<>();
                            for (int c = 0; c < cityLangList.getLength(); c++) {
                                Dictionary<City> cityDictionary = new Dictionary<>();
                                NodeList cityLangElements = cityLangList.item(c).getChildNodes();
                                cityDictionary.setObject(city);
                                cityDictionary.setLanguage(new Language(Integer.valueOf(cityLangElements.item(0).getTextContent())));
                                cityDictionary.setName(cityLangElements.item(1).getTextContent());
                                nameMap1.put(cityDictionary.getLanguage().getId(), cityDictionary);
                            }

                            city.setNameMap(nameMap1);
                        }
                        if (cityElements.item(4) != null) {

                            NodeList countiesList = cityElements.item(4).getChildNodes();
                            List<County> counties = new ArrayList<>();
                            for (int b = 0; b < countiesList.getLength(); b++) {
                                County county = new County();
                                NodeList countyElements = countiesList.item(b).getChildNodes();
                                county.setId(Integer.valueOf(countyElements.item(0).getTextContent()));
                                county.setName(countyElements.item(1).getTextContent());
                                counties.add(county);
                            }
                            city.setListOfCounty(counties);
                            cities.add(city);
                        }
                    }
                    country.setListOfCities(cities);
                }
                countries.add(country);

            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return countries;

    }

    @Override
    public ApplicationList appListXml() {
        return applicationDao.appListXml();
    }

    @Override
    public List<Module> customizeModules() {
        List<Module> result = new ArrayList<>();
        JsonArray jsonArray = gson.fromJson(applicationDao.modules(), JsonArray.class);
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Module m = new Module();
            m.setId(gson.fromJson(jsonObject.get("id"), Integer.class));
            m.setName(gson.fromJson(jsonObject.get("name"), String.class));
            m.setCode(gson.fromJson(jsonObject.get("code"), String.class));
            m.setOrder(gson.fromJson(jsonObject.get("order"), Integer.class));
            m.setIcon(gson.fromJson(jsonObject.get("icon"), String.class));

            if (!jsonObject.get("langs").isJsonNull()) {
                JsonArray jsonArrayLang = gson.fromJson(jsonObject.get("langs"), JsonArray.class);
                m.setNameMap(new HashMap<>());
                for (JsonElement jsonElementLang : jsonArrayLang) {
                    JsonObject jsonObjectLang = jsonElementLang.getAsJsonObject();
                    Dictionary<Module> dict = new Dictionary<>();
                    dict.setId(gson.fromJson(jsonObjectLang.get("id"), Integer.class));
                    dict.setLanguage(new Language(gson.fromJson(jsonObjectLang.get("language_id"), Integer.class)));
                    dict.setName(gson.fromJson(jsonObjectLang.get("name"), String.class));
                    dict.setObject(m);
                    m.getNameMap().put(dict.getLanguage().getId(), dict);
                }
            }
            //  System.out.println("folder "+jsonObject.get("folders"));
            if (!jsonObject.get("folders").isJsonNull()) {
                m.setFolders(customizeFolder(gson.fromJson(jsonObject.get("folders"), JsonArray.class)));
            }
            result.add(m);
        }
        return result;

    }

    public List<Folder> customizeFolder(JsonArray jsonArray) {
        List<Folder> result = new ArrayList<>();
        //  JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Folder f = new Folder();
            f.setId(gson.fromJson(jsonObject.get("id"), Integer.class));
            f.setName(gson.fromJson(jsonObject.get("name"), String.class));
            f.setType(gson.fromJson(jsonObject.get("type"), Integer.class));
            f.setOrder(gson.fromJson(jsonObject.get("order"), Integer.class));

            if (!jsonObject.get("langs").isJsonNull()) {
                JsonArray jsonArrayLang = gson.fromJson(jsonObject.get("langs"), JsonArray.class);
                f.setNameMap(new HashMap<>());
                for (JsonElement jsonElementLang : jsonArrayLang) {
                    JsonObject jsonObjectLang = jsonElementLang.getAsJsonObject();
                    Dictionary<Folder> dict = new Dictionary<>();
                    dict.setId(gson.fromJson(jsonObjectLang.get("id"), Integer.class));
                    dict.setLanguage(new Language(gson.fromJson(jsonObjectLang.get("language_id"), Integer.class)));
                    dict.setName(gson.fromJson(jsonObjectLang.get("name"), String.class));
                    dict.setObject(f);
                    f.getNameMap().put(dict.getLanguage().getId(), dict);
                }
            }
            if (!jsonObject.get("pages").isJsonNull()) {
                f.setPages(customizePage(gson.fromJson(jsonObject.get("pages"), JsonArray.class)));
            }
            result.add(f);
        }
        return result;
    }

    public List<Page> customizePage(JsonArray jsonArray) {
        List<Page> result = new ArrayList<>();
        // JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Page p = new Page();
            p.setId(gson.fromJson(jsonObject.get("id"), Integer.class));
            p.setUrl(gson.fromJson(jsonObject.get("url"), String.class));

            if (!jsonObject.get("langs").isJsonNull()) {
                JsonArray jsonArrayLang = gson.fromJson(jsonObject.get("langs"), JsonArray.class);
                p.setNameMap(new HashMap<>());
                for (JsonElement jsonElementLang : jsonArrayLang) {
                    JsonObject jsonObjectLang = jsonElementLang.getAsJsonObject();
                    Dictionary<Page> pageDict = new Dictionary<>();
                    pageDict.setId(gson.fromJson(jsonObjectLang.get("id"), Integer.class));
                    pageDict.setLanguage(new Language(gson.fromJson(jsonObjectLang.get("language_id"), Integer.class)));
                    pageDict.setDescription(gson.fromJson(jsonObjectLang.get("description"), String.class));
                    pageDict.setName(gson.fromJson(jsonObjectLang.get("name"), String.class));
                    pageDict.setObject(p);
                    p.getNameMap().put(pageDict.getLanguage().getId(), pageDict);
                }
            }

            if (!jsonObject.get("subpages").isJsonNull()) {
                JsonArray jsonArraySubPage = gson.fromJson(jsonObject.get("subpages"), JsonArray.class);
                p.setSubPages(new ArrayList<>());

                for (JsonElement jsonElementSubPage : jsonArraySubPage) {
                    JsonObject jsonObjectSubPage = jsonElementSubPage.getAsJsonObject();
                    Page subPage = new Page();
                    subPage.setId(gson.fromJson(jsonObjectSubPage.get("id"), Integer.class));
                    subPage.setUrl(gson.fromJson(jsonObjectSubPage.get("url"), String.class));
                    //  subPage.setType(p.getType());
                    subPage.setParent_id(new Page(gson.fromJson(jsonObjectSubPage.get("parent_id"), Integer.class), null));
                    if (!jsonObjectSubPage.get("langs").isJsonNull()) {
                        JsonArray jsonArraySubPageLang = gson.fromJson(jsonObjectSubPage.get("langs"), JsonArray.class);
                        subPage.setNameMap(new HashMap<>());
                        for (JsonElement jsonElementLang : jsonArraySubPageLang) {
                            JsonObject jsonObjectLang = jsonElementLang.getAsJsonObject();
                            Dictionary<Page> pageDict = new Dictionary<>();
                            pageDict.setId(gson.fromJson(jsonObjectLang.get("id"), Integer.class));
                            pageDict.setLanguage(new Language(gson.fromJson(jsonObjectLang.get("language_id"), Integer.class)));
                            pageDict.setDescription(gson.fromJson(jsonObjectLang.get("description"), String.class));
                            pageDict.setName(gson.fromJson(jsonObjectLang.get("name"), String.class));
                            pageDict.setObject(p);
                            subPage.getNameMap().put(pageDict.getLanguage().getId(), pageDict);
                        }
                    }
                    if (!jsonObjectSubPage.get("page_tabs").isJsonNull()) {
                        subPage.setTabs(customizeTab(gson.fromJson(jsonObjectSubPage.get("page_tabs"), JsonArray.class)));
                    } else {
                        subPage.setTabs(new ArrayList<>());
                    }

                    if (!jsonObjectSubPage.get("page_buttons").isJsonNull()) {
                        subPage.setButtons(customizeButton(gson.fromJson(jsonObjectSubPage.get("page_buttons"), JsonArray.class)));
                    } else {
                        subPage.setButtons(new ArrayList<>());
                    }

                    p.getSubPages().add(subPage);

                }
            }

            if (!jsonObject.get("page_tabs").isJsonNull()) {
                p.setTabs(customizeTab(gson.fromJson(jsonObject.get("page_tabs"), JsonArray.class)));
            } else {
                p.setTabs(new ArrayList<>());
            }
            if (!jsonObject.get("page_buttons").isJsonNull()) {
                p.setButtons(customizeButton(gson.fromJson(jsonObject.get("page_buttons"), JsonArray.class)));
            } else {
                p.setButtons(new ArrayList<>());
            }
            result.add(p);
        }

        return result;
    }

    public List<Button> customizeButton(JsonArray jsonArray) {
        List<Button> list = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Button button = new Button();

            button.setId(gson.fromJson(jsonObject.get("id"), Integer.class));
            button.setPage(new Page(Integer.valueOf(gson.fromJson(jsonObject.get("page_id"), String.class)), null));
            button.setTab(new Tab(Integer.valueOf(gson.fromJson(jsonObject.get("tab_id"), String.class))));
            button.setName(gson.fromJson(jsonObject.get("name"), String.class));

            list.add(button);
        }
        return list;

    }

    public List<Tab> customizeTab(JsonArray jsonArray) {
        List<Tab> list = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Tab tab = new Tab();
            tab.setId(gson.fromJson(jsonObject.get("id"), Integer.class));
            tab.setPage(new Page(Integer.valueOf(gson.fromJson(jsonObject.get("page_id"), String.class)), null));

            if (!jsonObject.get("langs").isJsonNull()) {
                JsonArray jsonArrayLang = gson.fromJson(jsonObject.get("langs"), JsonArray.class);
                tab.setNameMap(new HashMap<>());
                for (JsonElement jsonElementLang : jsonArrayLang) {
                    JsonObject jsonObjectLang = jsonElementLang.getAsJsonObject();
                    TabDictionary tabDict = new TabDictionary();
                    tabDict.setLanguage(new Language(gson.fromJson(jsonObjectLang.get("language_id"), Integer.class)));
                    tabDict.setName(gson.fromJson(jsonObjectLang.get("name"), String.class));
                    tabDict.setTab(tab);
                    tab.getNameMap().put(tabDict.getLanguage().getId(), tabDict);
                }
            }

            if (!jsonObject.get("tab_buttons").isJsonNull()) {
                tab.setListOfButtons(customizeButton(gson.fromJson(jsonObject.get("tab_buttons"), JsonArray.class)));
            } else {
                tab.setListOfButtons(new ArrayList<>());
            }

            list.add(tab);
        }

        return list;

    }

    @Override
    public HashMap<Integer, Boolean> customizeBranchShiftPaymentXml(String xml) {
        HashMap<Integer, Boolean> result = new HashMap<>();
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("branchshiftpayment");
            for (int s = 0; s < list.getLength(); s++) {
                NodeList elements = list.item(s).getChildNodes();
                result.put(Integer.valueOf(elements.item(0).getTextContent()), elements.item(1).getTextContent().equals("true"));
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    @Override
    public Map<Integer, BranchSetting> bringBranchSettings() {
        return applicationDao.bringBranchSettings();
    }

    @Override
    public int controlAutomationSettingBranch(int automationId) {
        return applicationDao.controlAutomationSettingBranch(automationId);
    }

    @Override
    public int controlStarbucksSettingBranch() {
        return applicationDao.controlStarbucksSettingBranch();
    }

    @Override
    public Map<Integer, ScheduledJobTrigger> scheduledJob() {
        return applicationDao.scheduledJob();
    }

    @Override
    public ScheduledJobTrigger findScheduledJob(int id) {
        return applicationDao.findScheduledJob(id);
    }

    @Override
    public int controlBranchIntegration() {
        return applicationDao.controlBranchIntegration();
    }

}
