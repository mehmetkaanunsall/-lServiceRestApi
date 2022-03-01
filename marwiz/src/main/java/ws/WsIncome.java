package ws;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.general.common.HashPassword;
import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import com.mepsan.marwiz.general.httpclient.business.HttpClientConnection;
import com.mepsan.marwiz.general.httpclient.business.MSA;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@WebService(serviceName = "WsIncome")
public class WsIncome {

    @Resource(name = "marwizDS")
    private DataSource marwizDS;

    @Resource
    WebServiceContext wsc;

    AESEncryptor aes = new AESEncryptor();
    MSA msa = new MSA();

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement ps = null;

    private int getID() throws SQLException {
        int userID = 0;
        MessageContext mc = wsc.getMessageContext();
        Map http_headers = (Map) mc.get(MessageContext.HTTP_REQUEST_HEADERS);
        List userList = (List) http_headers.get("Username");
        List passList = (List) http_headers.get("Password");
        String username = "";
        String password = "";
        if (passList != null && userList != null) {
            username = (String) userList.get(0);
            password = (String) passList.get(0);
        }
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("select id,password from general.userdata where username=? and deleted=false");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                HashPassword hp = new HashPassword();
                if (hp.passwordMatches(password, rs.getString("password"))) {
                    userID = rs.getInt("id");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(WsIncome.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return userID;
    }

    /*---------------------------------------------------------------------------OTOMAT MAKİNELERİ-----------------------------------------------------------------------------*/
    @WebMethod(operationName = "GetSales")
    @WebResult(name = "TransactionResult")
    public String GetSales(@WebParam(name = "Type") String type, @WebParam(name = "Sale") String sale) {

        String Sale = aes.decrypt(sale);
        String result = "";
        int Type = 0;

        if (type != null && !type.trim().isEmpty()) {
            try {
                Type = Integer.parseInt(aes.decrypt(type));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Type Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Type Value  \"}");
        }

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from automat.insert_sale (?,?)");
            ps.setEscapeProcessing(true);
            ps.setInt(1, Type);
            ps.setString(2, Sale);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    /*---------------------------------------------------------------------------OTOMAT MAKİNELERİ-----------------------------------------------------------------------------*/
 /*-------------------------------------------------------------------------------------- WASHING MACHINE -------------------------------------------------------------------------*/
    @WebMethod(operationName = "GetWMSale")
    @WebResult(name = "TransactionResult")
    public String GetWMSale(@WebParam(name = "MacAddress") String macAddress, @WebParam(name = "sale") String sale) {

        int userID = 0;
        String result = "";
        String Sale = aes.decrypt(sale);
        String MacAddress = aes.decrypt(macAddress);
        String Barcode = "";
        JSONObject saleJson = new JSONObject(Sale).getJSONArray("SaleRequest").getJSONObject(0).getJSONArray("PeronInfo").getJSONObject(0);

        if (saleJson.has("Barcode")) {
            Barcode = msa.decrypt(saleJson.getString("Barcode"));
        }

        try {
            userID = getID();
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":" + ex + ",\n"
                      + "   \"ErrorCode\":\"3001\"\n"
                      + "}";
        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = marwizDS.getConnection();
                ps = con.prepareStatement("Select * from wms.insert_sale (?,?,?,?) ");
                ps.setEscapeProcessing(true);
                ps.setString(1, MacAddress);
                ps.setString(2, Barcode);
                ps.setString(3, Sale);
                ps.setInt(4, userID);
                rs = ps.executeQuery();
                while (rs.next()) {
                    result = (rs.getString("Result"));
                }

            } catch (SQLException ex) {
                result = "{  \n"
                          + "   \"Result\":false,\n"
                          + "   \"ErrorMessage\":" + ex + ",\n"
                          + "   \"ErrorCode\":\"3001\"\n"
                          + "}";
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(WsIncome.class
                              .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":\"Wrong Username or Password\",\n"
                      + "   \"ErrorCode\":\"3000\"\n"
                      + "}";
        }
        return aes.encrypt(result);
    }

    @WebMethod(operationName = "CheckBarcode")
    @WebResult(name = "TransactionResult")
    public String CheckBarcode(@WebParam(name = "MacAddress") String macAddress, @WebParam(name = "Barcode") String barcode, @WebParam(name = "Peron") String peron) {

        int userID = 0;
        String result = "";

        System.out.println(macAddress);
        String MacAddress = aes.decrypt(macAddress);
        String Barcode = msa.decrypt(aes.decrypt(barcode));
        String Peron = aes.decrypt(peron);

        System.out.println("-Barcode---" + Barcode);

        try {
            userID = getID();
            System.out.println("---getId");
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":" + ex + ",\n"
                      + "   \"ErrorCode\":\"3001\"\n"
                      + "}";
        }
        System.out.println("---geçti" + userID);
        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                System.out.println("---try--- geldi");
                con = marwizDS.getConnection();
                ps = con.prepareStatement("SELECT * from wms.check_barcode (?,?,?) ");
                ps.setEscapeProcessing(true);
                ps.setString(1, MacAddress);
                ps.setString(2, Barcode);
                ps.setString(3, Peron);
                rs = ps.executeQuery();
                System.out.println("----query çalıştı");
                while (rs.next()) {
                    result = (rs.getString("Result"));
                }
                System.out.println("*result---" + result);
                Gson gson = new Gson();
                JsonElement resultJson = gson.fromJson(result, JsonElement.class);
                JsonObject objectJson = resultJson.getAsJsonObject();
                if (objectJson.has("Result") && objectJson.get("Result").getAsBoolean() == true) {
                    HttpClientConnection httpCon = new HttpClientConnection(objectJson.get("IpAddress").getAsString(), objectJson.get("Port").getAsString(), aes.encrypt(objectJson.get("Response").getAsJsonObject().toString()), 3000);
                    if (httpCon.connect().equals("error")) {
                        result = "{  \n"
                                  + "   \"Result\":false,\n"
                                  + "   \"ErrorMessage\":\"HTTP Connection Error\",\n"
                                  + "   \"ErrorCode\":\"3001\"\n"
                                  + "}";
                    } else {
                        result = "{  \n"
                                  + "   \"Result\":true,\n"
                                  + "   \"ErrorMessage\":\"Ok\",\n"
                                  + "   \"ErrorCode\":\"1\"\n"
                                  + "}";
                    }

                }

            } catch (SQLException ex) {
                result = "{  \n"
                          + "   \"Result\":false,\n"
                          + "   \"ErrorMessage\":" + ex + ",\n"
                          + "   \"ErrorCode\":\"3001\"\n"
                          + "}";
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(WsIncome.class
                              .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":\"Wrong Username or Password\",\n"
                      + "   \"ErrorCode\":\"3000\"\n"
                      + "}";
        }

        return aes.encrypt(result);
    }

    @WebMethod(operationName = "GetDate")
    @WebResult(name = "TransactionResult")
    public String GetDate() {

        int userID = 0;
        String result = "";

        try {
            userID = getID();
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":" + ex + ",\n"
                      + "   \"ErrorCode\":\"3001\"\n"
                      + "}";
        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = marwizDS.getConnection();
                ps = con.prepareStatement(" SELECT TO_CHAR(NOW(),'MM/DD/YYYY HH24:MI:SS'); ");
                ps.setEscapeProcessing(true);
                rs = ps.executeQuery();
                while (rs.next()) {
                    result = (rs.getString("Result"));
                }
                result = "{  \n"
                          + "   \"Result\":true,\n"
                          + "   \"ErrorMessage\":" + result + ",\n"
                          + "   \"ErrorCode\":\"\"\n"
                          + "}";

            } catch (SQLException ex) {
                result = "{  \n"
                          + "   \"Result\":false,\n"
                          + "   \"ErrorMessage\":" + ex + ",\n"
                          + "   \"ErrorCode\":\"3001\"\n"
                          + "}";
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(WsIncome.class
                              .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":\"Wrong Username or Password\",\n"
                      + "   \"ErrorCode\":\"3000\"\n"
                      + "}";
        }
        return aes.encrypt(result);
    }

    /*-------------------------------------------------------------------------------------- WASHING MACHINE -------------------------------------------------------------------------*/
 /*-----------------------------------------------------------------------------STAWIZ+ ENTEGRASYON--------------------------------------------------------------------------------*/
    // PROCESS ATTENDANT   (Stawiz+ tarafında yapılan pompacı işlemlerini Marwiz tarafına aktarır)
    @WebMethod(operationName = "ProcessAttentand")
    @WebResult(name = "TransactionResult")
    public String ProcessAttentand(@WebParam(name = "StationCode") String stationCode, @WebParam(name = "ProcessType") int processType, @WebParam(name = "AttendantData") String attendantData) {

        String result = "";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from automation.ws_attendant(?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setString(1, stationCode);
            ps.setInt(2, processType);
            ps.setString(3, attendantData);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return result;
    }

    /*-----------------------------------------------------------------------------STAWIZ+ ENTEGRASYON--------------------------------------------------------------------------------*/

 /*-----------------------------------------------------------------------------ÖN MUHASEBE ENTEGRASYON--------------------------------------------------------------------------------*/
    // PROCESS INVOICE   (FATURA İŞLEMLERİ)
    @WebMethod(operationName = "ProcessInvoice")
    @WebResult(name = "TransactionResult")
    public String ProcessInvoice(@WebParam(name = "JsonData") String jsonData) {
        System.out.println("*-*-*-*- " + jsonData);
        String result = "";
        int userID = 0;
        try {
            userID = getID();
            if (userID == 0) {
                result = "{  \n"
                          + "   \"Result\":0,\n"
                          + "   \"Response\":\" Please Control Username Or Password \"}\n"
                          + "}";
                return result;
            }
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":0,\n"
                      + "   \"Response\":" + ex + "\n"
                      + "}";
            return result;
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from finance.ws_process_invoice_integration(?,?) ");
            ps.setEscapeProcessing(true);
            ps.setString(1, jsonData);
            ps.setInt(2, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return result;
    }

    // PROCESS ACCOUNT   (CARİ İŞLEMLERİ)
    @WebMethod(operationName = "ProcessAccount")
    @WebResult(name = "TransactionResult")
    public String ProcessAccount(@WebParam(name = "JsonData") String jsonData) {

        String result = "";
        int userID = 0;
        try {
            userID = getID();
            if (userID == 0) {
                result = "{  \n"
                          + "   \"Result\":0,\n"
                          + "   \"Response\":\" Please Control Username Or Password \"}\n"
                          + "}";
                return result;
            }
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":0,\n"
                      + "   \"Response\":" + ex + "\n"
                      + "}";
            return result;
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from general.ws_process_account_integration(?,?) ");
            ps.setEscapeProcessing(true);
            ps.setString(1, jsonData);
            ps.setInt(2, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return result;
    }

    // PROCESS PRODUCT   (ÜRÜN İŞLEMLERİ)
    @WebMethod(operationName = "ProcessProduct")
    @WebResult(name = "TransactionResult")
    public String ProcessProduct(@WebParam(name = "JsonData") String jsonData) {

        String result = "";
        int userID = 0;
        try {
            userID = getID();
            if (userID == 0) {
                result = "{  \n"
                          + "   \"Result\":0,\n"
                          + "   \"Response\":\" Please Control Username Or Password \"}\n"
                          + "}";
                return result;
            }
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":0,\n"
                      + "   \"Response\":" + ex + "\n"
                      + "}";
            return result;
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from inventory.ws_process_stock_integration(?,?) ");
            ps.setEscapeProcessing(true);
            ps.setString(1, jsonData);
            ps.setInt(2, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return result;
    }

    // PROCESS PRICE LIST   (FİYAT LİSTESİ İŞLEMLERİ)
    @WebMethod(operationName = "ProcessPriceList")
    @WebResult(name = "TransactionResult")
    public String ProcessPriceList(@WebParam(name = "JsonData") String jsonData) {

        String result = "";
        int userID = 0;
        try {
            userID = getID();
            if (userID == 0) {
                result = "{  \n"
                          + "   \"Result\":0,\n"
                          + "   \"Response\":\" Please Control Username Or Password \"}\n"
                          + "}";
                return result;
            }
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":0,\n"
                      + "   \"Response\":" + ex + "\n"
                      + "}";
            return result;
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from inventory.ws_process_pricelist_integration(?,?) ");
            ps.setEscapeProcessing(true);
            ps.setString(1, jsonData);
            ps.setInt(2, userID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return result;
    }

    // PROCESS DATA LIST   (VERİ LİSTELEME İŞLEMLERİ)
    @WebMethod(operationName = "ProcessDataList")
    @WebResult(name = "TransactionResult")
    public String ProcessDataList(@WebParam(name = "JsonData") String jsonData) {

        String result = "";
        int userID = 0;
        try {
            userID = getID();
            if (userID == 0) {
                result = "{  \n"
                          + "   \"Result\":0,\n"
                          + "   \"Response\":\" Please Control Username Or Password \"}\n"
                          + "}";
                return result;
            }
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":0,\n"
                      + "   \"Response\":" + ex + "\n"
                      + "}";
            return result;
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from general.ws_process_list_integration(?) ");
            ps.setEscapeProcessing(true);
            ps.setString(1, jsonData);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return result;
    }

    /*-----------------------------------------------------------------------------ÖN MUHASEBE ENTEGRASYON--------------------------------------------------------------------------------*/
 /*-----------------------------------------------------------------------------EVA ENTEGRASYON--------------------------------------------------------------------------------*/
    private List<Fatura> customizeFatura(String xml) {
        List<Fatura> listFatura = new ArrayList<>();

        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            src.setEncoding("UTF-8");
            Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("fatura");
            for (int s = 0; s < list.getLength(); s++) {
                Fatura ft = new Fatura();
                NodeList elementsFatura = list.item(s).getChildNodes();
                ft.marwiz_id = (Integer.valueOf(elementsFatura.item(0).getTextContent()));
                ft.kod = (elementsFatura.item(1).getTextContent());
                ft.vergino = (elementsFatura.item(2).getTextContent());
                ft.unvan = (elementsFatura.item(3).getTextContent());
                ft.adres = (elementsFatura.item(4).getTextContent());
                ft.fatura_tarihi = (elementsFatura.item(5).getTextContent());
                ft.vade_tarihi = (elementsFatura.item(6).getTextContent());
                ft.fatura_numarasi = (elementsFatura.item(7).getTextContent());
                ft.fatura_tipi = (Integer.valueOf(elementsFatura.item(8).getTextContent()));
                ft.fatura_tutari = (Double.valueOf(elementsFatura.item(9).getTextContent()));
                ft.veriler_toplami = (Double.valueOf(elementsFatura.item(10).getTextContent()));
                ft.iskonto_toplami = (Double.valueOf(elementsFatura.item(11).getTextContent()));
                ft.net_tutar = (Double.valueOf(elementsFatura.item(12).getTextContent()));
                ft.aciklama = (elementsFatura.item(13).getTextContent());

                List<Kalem> listKalem = new ArrayList<>();
                NodeList langList = elementsFatura.item(14).getChildNodes();
                for (int c = 0; c < langList.getLength(); c++) {
                    Kalem kl = new Kalem();
                    NodeList elementsKalem = langList.item(c).getChildNodes();
                    kl.kdv_grubu = (Integer.valueOf(elementsKalem.item(0).getTextContent()));
                    kl.miktar = (Integer.valueOf(elementsKalem.item(1).getTextContent()));
                    kl.fiyat_matrah = (Double.valueOf(elementsKalem.item(2).getTextContent()));
                    kl.fiyat_kdv = (Double.valueOf(elementsKalem.item(3).getTextContent()));
                    kl.fiyat_net = (Double.valueOf(elementsKalem.item(4).getTextContent()));
                    listKalem.add(kl);
                }
                ft.kalemler = (listKalem);
                listFatura.add(ft);
            }
        } catch (ParserConfigurationException ex) {
        } catch (SAXException | IOException ex) {
        }
        return listFatura;
    }

    public static class GeneralResponse {

        public int result;
        public String response;
        public InvoiceResponse faturalar;
    }

    public static class InvoiceResponse {

        public List<Fatura> fatura;
    }

    public static class Fatura {

        public int marwiz_id;
        public String kod;
        public String vergino;
        public String unvan;
        public String adres;
        public String fatura_tarihi;
        public String vade_tarihi;
        public String fatura_numarasi;
        public int fatura_tipi;
        public double fatura_tutari;
        public double veriler_toplami;
        public double iskonto_toplami;
        public double net_tutar;
        public String aciklama;
        public List<Kalem> kalemler;
    }

    public static class Kalem {

        public int kdv_grubu;
        public int miktar;
        public double fiyat_matrah;
        public double fiyat_kdv;
        public double fiyat_net;
    }

    @WebMethod(operationName = "GetPurchaseInvoice")
    @WebResult(name = "TransactionResult")
    public GeneralResponse GetPurchaseInvoice(@WebParam(name = "beginDate") String beginDate, @WebParam(name = "endDate") String endDate) {

        GeneralResponse gp = new GeneralResponse();

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from finance.ws_getpurchaseinvoice (?,?)");
            ps.setEscapeProcessing(true);
            ps.setString(1, beginDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                gp.result = (rs.getInt("result"));
                if (gp.result == 0) {
                    gp.response = (rs.getString("response"));
                } else {
                    gp.response = "Success";
                    InvoiceResponse ir = new InvoiceResponse();
                    ir.fatura = customizeFatura(rs.getString("response"));
                    gp.faturalar = ir;
                }
            }

        } catch (SQLException ex) {
            gp.result = 0;
            gp.response = ex.toString();

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                gp.result = 0;
                gp.response = ex.toString();
            }
        }

        return gp;
    }

    /*-----------------------------------------------------------------------------EVA ENTEGRASYON--------------------------------------------------------------------------------*/
 /*-------------------------------------------------------------------------MOBİL UYGULAMA(MARWI)--------------------------------------------------------------------------------*/
    @WebMethod(operationName = "MobilLogin")
    @WebResult(name = "TransactionResult")
    public String MobilLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) {

        String result = "";
        String Username = aes.decrypt(username);
        String Password = aes.decrypt(password);

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from general.ws_get_userdata (?)");
            ps.setEscapeProcessing(true);
            ps.setString(1, Username);
            rs = ps.executeQuery();
            while (rs.next()) {
                HashPassword hp = new HashPassword();
                if (hp.passwordMatches(Password, rs.getString("r_password"))) {
                    result = rs.getString("r_user");
                }
            }

        } catch (SQLException ex) {
            return aes.encrypt("{\"Result\":0,\"Response\":\" An Error Occured \"}");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                return aes.encrypt("{\"Result\":0,\"Response\":\" An Error Occured \"}");
            }
        }
        return aes.encrypt(result);
    }

    // OPEN VENDING MACHINE GATE (Otomat makinelerinin kapısını açmak için komut gönderir)  DÜZENLENMELİ
    @WebMethod(operationName = "OpenVendingMachineGate")
    @WebResult(name = "TransactionResult")
    public String OpenVendingMachineGate(@WebParam(name = "Command") String command) {

        String Command = aes.decrypt(command);
        JsonParser parser = new JsonParser();
        JsonObject jsonCommand;
        HttpClientConnection connection;
        String cmd;
        String result;
        JsonObject jsonResult;

        jsonCommand = parser.parse(Command).getAsJsonObject();
        cmd = "{\"Command\":\"OpenGate\"}";
        cmd = aes.encrypt(cmd);
        connection = new HttpClientConnection(jsonCommand.getAsJsonObject().get("IpAddress").getAsString(), jsonCommand.getAsJsonObject().get("Port").getAsString(), cmd, 10);

        result = connection.connect();
        jsonResult = new JsonObject();
        if (result.equals("error")) {//bağlantı hatası ise
            jsonResult.addProperty("Result", 0);
            jsonResult.addProperty("Type", 0);
            jsonResult.addProperty("Message", "Connection Error");
        } else {
            jsonResult = parser.parse(aes.decrypt(result)).getAsJsonObject();
        }

        // LOG
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("INSERT INTO  wallet.log (command, response, type) VALUES (?,?,?) returning id; ");
            ps.setString(1, jsonCommand.toString());
            ps.setString(2, jsonResult.toString());
            ps.setInt(3, 2);
            ps.executeQuery();

        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        // LOG END

        return aes.encrypt(jsonResult.toString());

    }

    // CONFIGURE VENDING MACHINE (Otomat makinelerinin konfigürasyon komutunu gönderir)     DÜZENLENMELİ
    @WebMethod(operationName = "ConfigureVendingMachine")
    @WebResult(name = "TransactionResult")
    public String ConfigureVendingMachine(@WebParam(name = "VendingMachineID") String vendingMachineID, @WebParam(name = "IpAddress") String ipAddress, @WebParam(name = "Port") String port) {

        int VendingMachineID = 0;
        HttpClientConnection connection;
        JsonParser parser = new JsonParser();
        String command = "";
        String result = "";
        JsonObject jsonResult;

        if (vendingMachineID != null && !vendingMachineID.trim().isEmpty()) {
            try {
                VendingMachineID = Integer.parseInt(aes.decrypt(vendingMachineID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Vending Machine ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Vending Machine ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from inventory.configure_vendingmachine (?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, VendingMachineID);
            rs = ps.executeQuery();
            while (rs.next()) {
                command = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }

        command = aes.encrypt(command);
        connection = new HttpClientConnection(aes.decrypt(ipAddress), aes.decrypt(port), command, 10);

        result = connection.connect();
        jsonResult = new JsonObject();
        if (result.equals("error")) {//bağlantı hatası ise
            jsonResult.addProperty("Result", 0);
            jsonResult.addProperty("Type", 0);
            jsonResult.addProperty("Message", "Connection Error");
        } else {
            jsonResult = parser.parse(aes.decrypt(result)).getAsJsonObject();
        }

        // LOG
        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("INSERT INTO  wallet.log (command, response, type) VALUES (?,?,?) returning id; ");
            ps.setString(1, aes.decrypt(command));
            ps.setString(2, jsonResult.toString());
            ps.setInt(3, 3);
            ps.executeQuery();

        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        // LOG END

        return aes.encrypt(jsonResult.toString());
    }

    // GET VENDING MACHINE (Otomat makinelerini listeler)  DÜZENLENMELİ
    @WebMethod(operationName = "GetVendingMachine")
    @WebResult(name = "TransactionResult")
    public String GetVendingMachine(@WebParam(name = "StationID") String stationID) {

        int StationID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from wallet.list_vendingmachine (?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // GET WAREHOUSE (Tüm depoları veya sadece sayım yapılan depoları listeler)
    @WebMethod(operationName = "GetWarehouse")
    @WebResult(name = "TransactionResult")
    public String GetWarehouse(@WebParam(name = "StationID") String stationID, @WebParam(name = "is_stocktaking") String is_stocktaking) {

        int StationID = 0;
        int Isstocktaking = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (is_stocktaking != null && !is_stocktaking.trim().isEmpty()) {
            try {
                Isstocktaking = Integer.parseInt(aes.decrypt(is_stocktaking));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control İs Stocktaking Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control İs Stocktaking Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from inventory.ws_get_warehouse (?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, StationID);
            ps.setInt(2, Isstocktaking);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // SET STOCK COUNT (Tüm depoları veya sadece sayım yapılan depoları listeler)
    @WebMethod(operationName = "SetStockCount")
    @WebResult(name = "TransactionResult")
    public String SetStockCount(@WebParam(name = "ProcessType") String processType,
              @WebParam(name = "StockTakingID") String stockTakingID, @WebParam(name = "StockID") String stockID, @WebParam(name = "StockCount") String stockCount, @WebParam(name = "UserdataID") String userdataID) {

        int ProcessType = 0;
        int StockTakingID = 0;
        int StockID = 0;
        int UserdataID = 0;
        double StockCount = 0;
        String result = "";

        if (processType != null && !processType.trim().isEmpty()) {
            try {
                ProcessType = Integer.parseInt(aes.decrypt(processType));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type  Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type  Value  \"}");
        }

        if (stockTakingID != null && !stockTakingID.trim().isEmpty()) {
            try {
                StockTakingID = Integer.parseInt(aes.decrypt(stockTakingID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stocktaking ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stocktaking ID Value  \"}");
        }

        if (stockID != null && !stockID.trim().isEmpty()) {
            try {
                StockID = Integer.parseInt(aes.decrypt(stockID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock ID Value  \"}");
        }

        if (userdataID != null && !userdataID.trim().isEmpty()) {
            try {
                UserdataID = Integer.parseInt(aes.decrypt(userdataID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User Data ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User Data ID Value  \"}");
        }

        if (stockCount != null && !stockCount.trim().isEmpty()) {
            try {
                StockCount = Double.parseDouble(aes.decrypt(stockCount));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock Count Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock Count Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from inventory.ws_set_stockcount (?,?,?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, ProcessType);
            ps.setInt(2, StockTakingID);
            ps.setInt(3, StockID);
            ps.setDouble(4, StockCount);
            ps.setInt(5, UserdataID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // TRANSFER WAREHOUSE  (Depolar arası transfer yapar)
    @WebMethod(operationName = "TransferWarehouse")
    @WebResult(name = "TransactionResult")
    public String TransferWarehouse(@WebParam(name = "IncomeWarehouseID") String incomeWarehouseID,
              @WebParam(name = "OutcomeWarehouseID") String outcomeWarehouseID,
              @WebParam(name = "ReceiptNumber") String receiptNumber,
              @WebParam(name = "UserID") String userID,
              @WebParam(name = "TransferRequest") String transferRequest) {

        int IncomeWarehouseID = 0;
        int OutcomeWarehouseID = 0;
        String ReceiptNumber = aes.decrypt(receiptNumber);
        int UserID = 0;
        String TransferRequest = aes.decrypt(transferRequest);

        String result = "";

        if (incomeWarehouseID != null && !incomeWarehouseID.trim().isEmpty()) {
            try {
                IncomeWarehouseID = Integer.parseInt(aes.decrypt(incomeWarehouseID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Income Warehouse ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Income Warehouse ID Value  \"}");
        }

        if (outcomeWarehouseID != null && !outcomeWarehouseID.trim().isEmpty()) {
            try {
                OutcomeWarehouseID = Integer.parseInt(aes.decrypt(outcomeWarehouseID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Outcome Warehouse ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Outcome Warehouse ID Value  \"}");
        }

        if (userID != null && !userID.trim().isEmpty()) {
            try {
                UserID = Integer.parseInt(aes.decrypt(userID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from inventory.process_warehousetransfer (?,?,?,?,?,?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, 0);
            ps.setInt(2, 0);
            ps.setInt(3, IncomeWarehouseID);
            ps.setInt(4, OutcomeWarehouseID);
            ps.setString(5, ReceiptNumber);
            ps.setInt(6, UserID);
            ps.setString(7, TransferRequest);
            ps.setInt(8, 0);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = "{\"Result\":" + rs.getString("r_transfer_id") + "}";
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // GET MARKET PRODUCT   (İstasyonda bulunan market ürün bilgilerini verir)
    @WebMethod(operationName = "GetMarketProduct")
    @WebResult(name = "TransactionResult")
    public String GetMarketProduct(@WebParam(name = "StationID") String stationID,
              @WebParam(name = "StockTakingID") String stockTakingID,
              @WebParam(name = "Order") String order,
              @WebParam(name = "Page") String page,
              @WebParam(name = "Search") String search,
              @WebParam(name = "WarehouseID") String warehouseID,
              @WebParam(name = "IsOrder") String isOrder) {

        int StationID = 0;
        int WarehouseID = 0;
        int Order = 0;
        int Page = 0;
        int StockTakingID = 0;
        boolean IsOrder = false;
        String Search = aes.decrypt(search);
        String result = "";

        if (order != null && !order.trim().isEmpty()) {
            try {
                Order = Integer.parseInt(aes.decrypt(order));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Order Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Order Value  \"}");
        }

        if (page != null && !page.trim().isEmpty()) {
            try {
                Page = Integer.parseInt(aes.decrypt(page));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Page Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Page Value  \"}");
        }

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (warehouseID != null && !warehouseID.trim().isEmpty()) {
            try {
                WarehouseID = Integer.parseInt(aes.decrypt(warehouseID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Warehouse ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Warehouse ID Value  \"}");
        }

        if (stockTakingID != null && !stockTakingID.trim().isEmpty()) {
            try {
                StockTakingID = Integer.parseInt(aes.decrypt(stockTakingID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock Taking ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock Taking ID Value  \"}");
        }
        if (isOrder != null && !isOrder.trim().isEmpty()) {
            try {
                IsOrder = ((aes.decrypt(isOrder)).equals("true") ? true : false);
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control isOrder Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control isOrder Value  \"}");
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from inventory.ws_list_stock (?,?,?,?,?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, StationID);
            ps.setInt(2, StockTakingID);
            ps.setInt(3, Order);
            ps.setInt(4, Page);
            ps.setString(5, Search);
            ps.setInt(6, WarehouseID);
            ps.setBoolean(7, IsOrder);

            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        System.out.println("Barcode Product" + result);
        return aes.encrypt(result);
    }

    // GET MARKET PRODUCT MOVEMENT  (İstasyonda bulunan stokların son 3 aylık hareketini verir)
    @WebMethod(operationName = "GetMarketProductMovement")
    @WebResult(name = "TransactionResult")
    public String GetMarketProductMovement(@WebParam(name = "StationID") String stationID, @WebParam(name = "StockID") String stockID) {

        int StationID = 0;
        int StockID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (stockID != null && !stockID.trim().isEmpty()) {
            try {
                StockID = Integer.parseInt(aes.decrypt(stockID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "     array_to_json(array_agg(row_to_json(tt))) as r_result FROM (\n"
                      + "     SELECT\n"
                      + "          whm.quantity\n"
                      + "         ,whm.is_direction\n"
                      + "         ,to_char(whr.processdate, 'DD.MM.YYYY HH24:MI:SS') as processdate\n"
                      + "     FROM  inventory.warehousemovement whm\n"
                      + "     INNER JOIN inventory.warehouse wh ON (wh.id = whm.warehouse_id)\n"
                      + "     INNER JOIN inventory.warehousereceipt whr ON (whr.id = whm.warehousereceipt_id AND whr.deleted = FALSE)\n"
                      + "     WHERE whm.deleted = FALSE\n"
                      + "     AND wh.branch_id = ?\n"
                      + "     AND whm.stock_id = ?\n"
                      + "     AND whr.processdate > NOW()- INTERVAL'3 month'\n"
                      + ")tt");
            ps.setInt(1, StationID);
            ps.setInt(2, StockID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // GET NOTIFICATIONS  (İstasyonda bulunan bildirimleri verir)
    @WebMethod(operationName = "GetNotifications")
    @WebResult(name = "TransactionResult")
    public String GetNotifications(@WebParam(name = "StationID") String stationID, @WebParam(name = "UserID") String userID) {

        int StationID = 0;
        int UserID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (userID != null && !userID.trim().isEmpty()) {
            try {
                UserID = Integer.parseInt(aes.decrypt(userID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "    array_to_json(array_agg(to_json(nt))) AS r_result FROM (\n"
                      + "   SELECT\n"
                      + "        usnf.id,\n"
                      + "        ntf.description::JSON,\n"
                      + "        coalesce(ntf.centerwarningtype_id,ntf.type_id)\n"
                      + "   FROM general.userdata_notification_con usnf\n"
                      + "   INNER JOIN general.notification ntf ON(ntf.id=usnf.notification_id AND ntf.deleted=FALSE AND ntf.branch_id= ?)\n"
                      + "   WHERE usnf.deleted = FALSE\n"
                      + "   AND usnf.is_read = FALSE\n"
                      + "   AND usnf.userdata_id = ?\n"
                      + "   ORDER BY usnf.id DESC\n"
                      + ")nt");
            ps.setInt(1, StationID);
            ps.setInt(2, UserID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // SET NOTIFICATIONS  (İstasyonda bulunan bildirimlerin okunma durumunu günceller)
    @WebMethod(operationName = "SetNotifications")
    @WebResult(name = "TransactionResult")
    public String SetNotifications(@WebParam(name = "NotificationID") String notificationID, @WebParam(name = "UserID") String userID) {

        int NotificationID = 0;
        int UserID = 0;
        String result = "";

        if (notificationID != null && !notificationID.trim().isEmpty()) {
            try {
                NotificationID = Integer.parseInt(aes.decrypt(notificationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Notification ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Notification ID Value  \"}");
        }

        if (userID != null && !userID.trim().isEmpty()) {
            try {
                UserID = Integer.parseInt(aes.decrypt(userID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("UPDATE\n"
                      + "     general.userdata_notification_con\n"
                      + "SET is_read= True,\n"
                      + "     u_id = ?,\n"
                      + "     u_time = now(),\n"
                      + "     readdate = now()\n"
                      + "WHERE id = ? RETURNING id as r_result");
            ps.setInt(1, UserID);
            ps.setInt(2, NotificationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = String.valueOf(rs.getInt("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // PROCESS INVOICE   (Fatura ekleme silme ve güncelleme ve listeleme işlemlerini yapar)
    @WebMethod(operationName = "ProcessInvoices")
    @WebResult(name = "TransactionResult")
    public String ProcessInvoices(@WebParam(name = "StationID") String stationID,
              @WebParam(name = "ProcessType") String processType,
              @WebParam(name = "InvoiceData") String invoiceData,
              @WebParam(name = "UserID") String userID) {

        int StationID = 0;
        int ProcessType = 0;
        String InvoiceData = aes.decrypt(invoiceData);
        int UserID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (processType != null && !processType.trim().isEmpty()) {
            try {
                ProcessType = Integer.parseInt(aes.decrypt(processType));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
        }

        if (userID != null && !userID.trim().isEmpty()) {
            try {
                UserID = Integer.parseInt(aes.decrypt(userID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from finance.ws_process_invoice (?,?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, StationID);
            ps.setInt(2, ProcessType);
            ps.setString(3, InvoiceData);
            ps.setInt(4, UserID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        System.out.println("ProcessInvoice" + result);

        return aes.encrypt(result);
    }

    // PROCESS ACCOUNT   (Cari ekleme silme ve güncelleme ve listeleme işlemlerini yapar)
    @WebMethod(operationName = "ProcessAccounts")
    @WebResult(name = "TransactionResult")
    public String ProcessAccounts(@WebParam(name = "StationID") String stationID,
              @WebParam(name = "ProcessType") String processType,
              @WebParam(name = "AccountData") String accountData,
              @WebParam(name = "UserID") String userID) {

        int StationID = 0;
        int ProcessType = 0;
        String AccountData = aes.decrypt(accountData);
        int UserID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (processType != null && !processType.trim().isEmpty()) {
            try {
                ProcessType = Integer.parseInt(aes.decrypt(processType));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
        }

        if (userID != null && !userID.trim().isEmpty()) {
            try {
                UserID = Integer.parseInt(aes.decrypt(userID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from general.ws_process_account (?,?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, StationID);
            ps.setInt(2, ProcessType);
            ps.setString(3, AccountData);
            ps.setInt(4, UserID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // WIDGET PRODUCT SALES   ( Ürünlerin aylık,haftalık ve günlük olarak satışlarını listeler)
    @WebMethod(operationName = "GetSalesByProduct")
    @WebResult(name = "TransactionResult")
    public String GetSalesByProduct(@WebParam(name = "StationID") String stationID) {

        int StationID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("  SELECT\n"
                      + "    array_to_json(array_agg(to_json(saleitem))) AS r_result\n"
                      + "FROM (\n"
                      + "        SELECT\n"
                      + "            SUM(case when s.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL '1 day' then si.quantity else 0 end) as daily,\n"
                      + "            SUM(case when s.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL '1 day' then (si.totalmoney *\n"
                      + "            COALESCE(si.exchangerate,1))  else 0 end) as daily_total,\n"
                      + "            SUM(case when s.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL '1 week' then si.quantity else 0 end) as weekly,\n"
                      + "            SUM(case when s.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL '1 week' then (si.totalmoney *\n"
                      + "            COALESCE(si.exchangerate,1))  else 0 end) as weekly_total,\n"
                      + "            SUM(si.quantity) as monthly,\n"
                      + "            SUM(si.totalmoney * COALESCE(si.exchangerate,1)) as monthly_total,\n"
                      + "            stc.id as stock_id,\n"
                      + "            gunt.sortname as unit_name,\n"
                      + "            gunt.unitrounding as unitrounding,\n"
                      + "            stc.name\n"
                      + "\n"
                      + "        FROM general.sale s\n"
                      + "            LEFT JOIN general.sale sll ON(sll.returnparent_id = s.id AND sll.deleted = False)\n"
                      + "            INNER JOIN general.saleitem si ON (s.id = si.sale_id ANd si.deleted = FALSE)\n"
                      + "            INNER JOIN inventory.stock stc ON (stc.id = si.stock_id AND stc.deleted = FALSE)\n"
                      + "            INNER JOIN general.unit gunt ON (gunt.id = stc.unit_id AND gunt.deleted = FALSE)\n"
                      + "\n"
                      + "        WHERE s.deleted = FALSE AND (CASE WHEN si.is_calcincluded = TRUE AND s.differentdate  > date_trunc('day', now()) + interval '1 day' - INTERVAL '1 month' THEN FALSE ELSE TRUE END) \n"
                      + "            AND s.branch_id = ?\n"
                      + "            AND s.is_return = FALSE\n"
                      + "            AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                      + "            AND s.processdate  > date_trunc('day', now()) + interval '1 day' - INTERVAL '1 month'\n"
                      + "        GROUP BY stc.id,stc.name,gunt.sortname,gunt.unitrounding )saleitem \n"
                      + "");
            ps.setInt(1, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // WIDGET CUSTOMER SALES   ( Müşterilerin aylık,haftalık ve günlük olarak satışlarını listeler)
    @WebMethod(operationName = "GetSalesByCustomer")
    @WebResult(name = "TransactionResult")
    public String GetSalesByCustomer(@WebParam(name = "StationID") String stationID) {

        int StationID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "     array_to_json(array_agg(to_json(accountpurchase))) AS r_result FROM (\n"
                      + "   SELECT\n"
                      + "       a.id,\n"
                      + "       a.name,\n"
                      + "       SUM(case when s.processdate >  date_trunc('day', now()) + interval '1 day'  - INTERVAL '1 day' then si.quantity  else 0 end) as daily,\n"
                      + "       SUM(case when s.processdate >  date_trunc('day', now()) + interval '1 day'  - INTERVAL '1 day' then (si.totalmoney * COALESCE(si.exchangerate,1))  else 0 end) as daily_total,\n"
                      + "       SUM(case when s.processdate >  date_trunc('day', now()) + interval '1 day'  - INTERVAL '1 week' then si.quantity   else 0 end) as weekly,\n"
                      + "       SUM(case when s.processdate >  date_trunc('day', now()) + interval '1 day'  - INTERVAL '1 week' then (si.totalmoney * COALESCE(si.exchangerate,1))  else 0 end) as weekly_total,\n"
                      + "       SUM(si.quantity) as monthly,\n"
                      + "       SUM(si.totalmoney * COALESCE(si.exchangerate,1)) as monthly_total,\n"
                      + "       gunt.unitrounding as unitrounding,\n"
                      + "       gunt.sortname as unit_name\n"
                      + "   FROM general.account a\n"
                      + "   INNER JOIN general.sale s ON(s.account_id=a.id AND s.deleted=FALSE)\n"
                      + "   INNER JOIN general.saleitem si ON (si.sale_id=s.id AND si.deleted=FALSE)\n"
                      + "   INNER JOIN inventory.stock stc ON (stc.id = si.stock_id AND stc.deleted = FALSE)\n"
                      + "   INNER JOIN general.unit gunt ON (gunt.id = stc.unit_id AND gunt.deleted = FALSE)\n"
                      + "   WHERE a.deleted=FALSE AND (CASE WHEN si.is_calcincluded = TRUE AND s.differentdate BETWEEN date_trunc('day', now()) + interval '1 day'  - INTERVAL '1 month' AND NOW() THEN FALSE ELSE TRUE END) \n"
                      + "   AND s.branch_id=?\n"
                      + "   AND a.id > 1\n"
                      + "   AND s.processdate  BETWEEN  date_trunc('day', now()) + interval '1 day'  - INTERVAL '1 month' AND NOW()\n"
                      + "   GROUP BY a.id,a.name,gunt.unitrounding, gunt.sortname\n"
                      + ")accountpurchase");
            ps.setInt(1, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // WIDGET PRICE CHANGE   ( Son 1 ay içerisinde fiyatı değişen ürünleri listeler)
    @WebMethod(operationName = "PriceChange")
    @WebResult(name = "TransactionResult")
    public String PriceChange(@WebParam(name = "StationID") String stationID) {

        int StationID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "     array_to_json(array_agg(to_json(pricesvaryingproduct))) AS r_result \n"
                      + "FROM ( \n"
                      + "   SELECT \n"
                      + "     tt.stckid, \n"
                      + "     tt.stckname, \n"
                      + "     tt.processdate, \n"
                      + "     CASE WHEN tt.columnname = 'price' THEN tt.oldvalue::NUMERIC(18,4) ELSE COALESCE(tt.lastprice::NUMERIC(18,4),tt.price) END as oldprice, \n"
                      + "     CASE WHEN tt.columnname = 'price' THEN tt.newvalue::NUMERIC(18,4) ELSE COALESCE(tt.lastprice::NUMERIC(18,4),tt.price) END as newprice, \n"
                      + "     CASE WHEN tt.columnname = 'currency_id' THEN tt.oldvalue::INTEGER ELSE COALESCE(NULLIF(tt.lastcurrency_id,'')::INTEGER,tt.currency_id) END as oldcurrency_id, \n"
                      + "     CASE WHEN tt.columnname = 'currency_id' THEN tt.newvalue::INTEGER ELSE COALESCE(NULLIF(tt.lastcurrency_id,'')::INTEGER,tt.currency_id) END as newcurrency_id, \n"
                      + "     tt.changeuser \n"
                      + "             FROM( \n"
                      + "             SELECT \n"
                      + "        hst.id,\n"
                      + "             stck.id as stckid, \n"
                      + "             stck.name as stckname, \n"
                      + "             to_char(hst.processdate, 'DD.MM.YYYY HH24:MI:SS') as processdate, \n"
                      + "             hst.columnname, \n"
                      + "                 CASE WHEN hst.oldvalue::VARCHAR = '' OR  hst.oldvalue IS NULL  THEN 0::VARCHAR ELSE hst.oldvalue END AS oldvalue, \n"
                      + "                 CASE WHEN hst.newvalue::VARCHAR = '' OR  hst.newvalue IS NULL  THEN 0::VARCHAR ELSE hst.newvalue END AS newvalue, \n"
                      + "             ( \n"
                      + "                 SELECT \n"
                      + "                     hst2.newvalue \n"
                      + "                 FROM general.history hst2 \n"
                      + "                 WHERE hst2.row_id = hst.row_id \n"
                      + "                 AND hst2.columnname = 'price'\n"
                      + "                 AND hst2.tablename = 'inventory.pricelistitem'  \n"
                      + "                 AND hst2.id < hst.id \n"
                      + "                 ORDER BY hst2.id DESC LIMIT 1 \n"
                      + "             ) as lastprice, \n"
                      + "             ( \n"
                      + "               SELECT \n"
                      + "                   hst2.newvalue \n"
                      + "               FROM general.history hst2 \n"
                      + "               WHERE hst2.row_id = hst.row_id \n"
                      + "               AND hst2.columnname = 'currency_id' \n"
                      + "               AND hst2.tablename = 'inventory.pricelistitem' \n"
                      + "               AND hst2.id < hst.id \n"
                      + "               ORDER BY hst2.id DESC LIMIT 1 \n"
                      + "             ) as lastcurrency_id, \n"
                      + "             pli.price, \n"
                      + "             pli.currency_id, \n"
                      + "             us.name||' '||us.surname as changeuser \n"
                      + "       FROM general.history hst \n"
                      + "       INNER JOIN inventory.pricelistitem pli ON(pli.id=hst.row_id AND pli.deleted=FALSE) \n"
                      + "       INNER JOIN inventory.pricelist pl ON(pl.id=pli.pricelist_id AND pl.deleted=FALSE  AND pl.branch_id = ? AND pl.is_purchase = FALSE) \n"
                      + "       INNER JOIN inventory.stock stck ON(stck.id=pli.stock_id AND stck.deleted=FALSE) \n"
                      + "       INNER JOIN general.userdata us ON (us.id = hst.userdata_id) \n"
                      + "       WHERE hst.tablename='inventory.pricelistitem' \n"
                      + "       AND (hst.columnname='price' OR hst.columnname='currency_id') \n"
                      + "       AND hst.processdate > date_trunc('day', now()) + interval '1 day' - INTERVAL'1 month' \n"
                      + "       ORDER BY hst.processdate DESC,stck.name,hst.columnname DESC \n"
                      + "             ) tt \n"
                      + ")pricesvaryingproduct");
            ps.setInt(1, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // WIDGET CATEGORIZATION SALES   ( Ürünlerin kategorilere göre aylık,haftalık ve günlük olarak satışlarını listeler)
    @WebMethod(operationName = "GetSalesByCategorization")
    @WebResult(name = "TransactionResult")
    public String GetSalesByCategorization(@WebParam(name = "StationID") String stationID) {

        int StationID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT  array_to_json(array_agg(to_json(salescategorization))) AS r_result FROM( SELECT\n"
                      + "     parentcategory.*\n"
                      + "     ,(SELECT  array_to_json(array_agg(to_json(totalssub)))\n"
                      + "         FROM(\n"
                      + "             SELECT\n"
                      + "                 SUM(CASE WHEN sl.processdate > NOW() - INTERVAL '1 day' \n"
                      + "THEN sli.quantity  ELSE 0 END) AS daily,\n"
                      + "                 SUM(CASE WHEN sl.processdate > NOW() - INTERVAL '1 day' \n"
                      + "THEN (sli.totalmoney  * COALESCE(sli.exchangerate,1) ) ELSE 0 END) AS daily_total,\n"
                      + "                 SUM(CASE WHEN sl.processdate > NOW() - INTERVAL '1 week' THEN sli.quantity   ELSE 0 end) AS weekly,\n"
                      + "                 SUM(CASE WHEN sl.processdate > NOW() - INTERVAL '1 week' THEN (sli.totalmoney  * COALESCE(sli.exchangerate,1) ) ELSE 0 END) AS weekly_total,\n"
                      + "                 SUM(sli.quantity) AS monthly,\n"
                      + "                 SUM(sli.totalmoney  * COALESCE(sli.exchangerate,1) ) AS monthly_total\n"
                      + "             FROM general.saleitem sli\n"
                      + "             INNER JOIN general.sale sl ON(sl.id=sli.sale_id AND sl.deleted = FALSE)\n"
                      + "             LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = FALSE)\n"
                      + "             INNER JOIN inventory.stock stck ON(stck.id = sli.stock_id AND stck.deleted = FALSE)\n"
                      + "             WHERE\n"
                      + "                 sl.branch_id = ? AND sl.is_return=False AND (CASE WHEN sli.is_calcincluded = TRUE AND sl.differentdate BETWEEN NOW() - INTERVAL '1 month' AND NOW() THEN FALSE ELSE TRUE END) \n"
                      + "                 AND sli.deleted=FALSE\n"
                      + "                 AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                      + "                 AND sl.processdate  BETWEEN NOW() - INTERVAL '1 month' \n"
                      + "AND NOW()\n"
                      + "                 AND stck.id IN ( SELECT stcc.stock_id FROM inventory.stock_categorization_con stcc WHERE stcc.categorization_id IN\n"
                      + "                     (\n"
                      + "                     WITH recursive ctTree AS(\n"
                      + "                         SELECT\n"
                      + "                           gc.id,gc.name,\n"
                      + "                           COALESCE(gc.parent_id,0) AS parent_id , 1 AS depth\n"
                      + "                         FROM general.categorization gc\n"
                      + "                         WHERE gc.id IN(\n"
                      + "                            SELECT\n"
                      + "                               gct.id\n"
                      + "                             FROM\n"
                      + "                               inventory.stock_categorization_con scac\n"
                      + "                               LEFT JOIN general.categorization gct ON(scac.categorization_id=gct.id AND gct.deleted=False)\n"
                      + "                            WHERE\n"
                      + "                               scac.categorization_id = parentcategory.categorization_id  and scac.deleted=False\n"
                      + "                         )\n"
                      + "                         UNION ALL\n"
                      + "\n"
                      + "                             SELECT gc2.id,\n"
                      + "                               gc2.name,\n"
                      + "                               COALESCE(gc2.parent_id,0) AS parent_id,\n"
                      + "                               ct.depth+1 AS depth\n"
                      + "                             FROM general.categorization gc2\n"
                      + "                             JOIN ctTree AS ct ON ct.id = gc2.parent_id AND gc2.deleted =FALSE\n"
                      + "                     )\n"
                      + "                     SELECT\n"
                      + "                        ctr.id\n"
                      + "                     FROM\n"
                      + "                        ctTree ctr\n"
                      + "                   )\n"
                      + "               )\n"
                      + "             ) totalssub\n"
                      + "      ) AS totals\n"
                      + "FROM (WITH RECURSIVE category (id, parent_id, name) AS (\n"
                      + "     SELECT  id, parent_id, name\n"
                      + "     FROM    general.categorization\n"
                      + "     WHERE   parent_id IS NULL AND deleted = FALSE AND item_id = 2\n"
                      + "\n"
                      + "     UNION ALL\n"
                      + "\n"
                      + "     SELECT  p.id, COALESCE(t0.parent_id,p.parent_id), t0.name\n"
                      + "     FROM general.categorization p\n"
                      + "     INNER JOIN category t0 ON (t0.id = p.parent_id)\n"
                      + "     WHERE p.deleted = FALSE AND p.item_id = 2  \n"
                      + ")\n"
                      + "SELECT DISTINCT (CASE WHEN parent_id IS NULL THEN id ELSE parent_id END )\n"
                      + "     AS categorization_id,\n"
                      + "     name AS categorization_name\n"
                      + "     FROM  category\n"
                      + "     WHERE id IN ( SELECT stcc.categorization_id FROM inventory.stock_categorization_con stcc\n"
                      + "     INNER JOIN general.categorization ctr ON(ctr.id = stcc.categorization_id AND ctr.deleted = FALSE AND ctr.item_id = 2  )\n"
                      + "     INNER JOIN inventory.stock stck ON(stck.id = stcc.stock_id AND stck.deleted = FALSE  AND stck.status_id = 3)\n"
                      + "     LEFT JOIN inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id=?)\n"
                      + "     WHERE stcc.deleted = FALSE AND si.is_passive = FALSE\n"
                      + "     )\n"
                      + ") AS parentcategory)salescategorization;");
            ps.setInt(1, StationID);
            ps.setInt(2, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // REPORT ALL SALES   ( Son bir aylık bütün satışlarını listeler)
    @WebMethod(operationName = "GetMonthlySales")
    @WebResult(name = "TransactionResult")
    public String GetMonthlySales(@WebParam(name = "StationID") String stationID) {

        int StationID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                System.out.println("**** " + stationID);
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "                         array_to_json(array_agg(row_to_json(t))) as r_result FROM(\n"
                      + "                         SELECT\n"
                      + "                             s.id,\n"
                      + "                             to_char(s.processdate, 'DD.MM.YYYY HH24:MI:SS') as processdate,\n"
                      + "                             s.currency_id,\n"
                      + "                             s.totaltax,\n"
                      + "                             s.totalmoney,\n"
                      + "                             COALESCE(rc.receiptno,'') as receiptno,\n"
                      + "                             COALESCE(\n"
                      + "                                   (\n"
                      + "                                   SELECT\n"
                      + "                                       array_to_json(array_agg(row_to_json(tt)))\n"
                      + "                                   FROM\n"
                      + "                                   (\n"
                      + "                                         SELECT\n"
                      + "                                             si.id,\n"
                      + "                                             stck.id as stock_id,\n"
                      + "                                             stck.name,\n"
                      + "                                             stck.barcode,\n"
                      + "                                             si.unit_id,\n"
                      + "                                             unt.sortname as unit_name,\n"
                      + "                                             unt.unitrounding as unitrounding,\n"
                      + "                                             si.unitprice,\n"
                      + "                                             si.totaltax,\n"
                      + "                                             si.currency_id,\n"
                      + "                                             si.quantity,\n"
                      + "                                             si.totalmoney\n"
                      + "                                         FROM general.saleitem si\n"
                      + "                                         INNER JOIN general.sale sl2 ON(sl2.id = si.sale_id AND sl2.deleted=FALSE)\n"
                      + "                                         INNER JOIN inventory.stock stck ON (stck.id = si.stock_id AND stck.deleted = FALSE)\n"
                      + "                                         INNER JOIN general.unit unt ON (unt.id = si.unit_id AND unt.deleted = FALSE)\n"
                      + "                                         WHERE si.deleted = FALSE AND (CASE WHEN si.is_calcincluded = TRUE AND sl2.differentdate >  date_trunc('day', now()) + interval '1 day' THEN FALSE ELSE TRUE END) \n"
                      + "                                         AND si.sale_id = s.id\n"
                      + "                                         ORDER BY si.c_time\n"
                      + "                                    ) tt),'[]'\n"
                      + "                             ) AS items,\n"
                      + "                             COALESCE(\n"
                      + "                                   (\n"
                      + "                                   SELECT\n"
                      + "                                       array_to_json(array_agg(row_to_json(tt)))\n"
                      + "                                   FROM\n"
                      + "                                   (\n"
                      + "                                         SELECT\n"
                      + "                                             sp.type_id,\n"
                      + "                                             SUM(sp.exchangerate*sp.price) as total\n"
                      + "                                         FROM general.salepayment sp\n"
                      + "                                         WHERE sp.deleted = FALSE\n"
                      + "                                         AND sp.sale_id = s.id\n"
                      + "                                         GROUP BY sp.type_id\n"
                      + "                                   ) tt),'[]'\n"
                      + "                             ) AS payments\n"
                      + "                         FROM general.sale s\n"
                      + "                         LEFT JOIN general.sale sll ON(sll.returnparent_id = s.id AND sll.deleted = False)\n"
                      + "                         LEFT JOIN finance.receipt rc ON (rc.id = s.receipt_id AND rc.deleted = FALSE)\n"
                      + "                         WHERE s.deleted = FALSE\n"
                      + "                         AND s.is_return = FALSE\n"
                      + "                         AND s.branch_id = ?\n"
                      + "                         AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0)\n"
                      + "                         AND s.processdate >  date_trunc('day', now()) + interval '1 day' -\n"
                      + "                    INTERVAL'1 month'\n"
                      + "                         ORDER BY s.processdate DESC\n"
                      + ")t");
            ps.setInt(1, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // REPORT TRIAL BALANCE   ( Girilen tarihler arası genel mizan raporunu gösterir)
    @WebMethod(operationName = "TrialBalance")
    @WebResult(name = "TransactionResult")
    public String TrialBalance(@WebParam(name = "StationID") String stationID, @WebParam(name = "BeginDate") String beginDate, @WebParam(name = "EndDate") String endDate, @WebParam(name = "Type") String type) {

        int StationID = 0;
        int Type = 0;
        Timestamp BeginDate = null;
        Timestamp EndDate = null;
        String result = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (beginDate != null && !beginDate.trim().isEmpty()) {
            try {
                BeginDate = new Timestamp(df.parse(aes.decrypt(beginDate)).getTime());
            } catch (ParseException ex) {
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Begin Date Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Begin Date  Value  \"}");
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                EndDate = new Timestamp(df.parse(aes.decrypt(endDate)).getTime());
            } catch (ParseException ex) {
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control End Date Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control End Date Value  \"}");
        }

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (type != null && !type.trim().isEmpty()) {
            try {
                Type = Integer.parseInt(aes.decrypt(type));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Type Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Type Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT  \n"
                      + "    array_to_json(array_agg(row_to_json(tt)))as r_result \n"
                      + "FROM    \n"
                      + "( \n"
                      + "  --Ticari Tipindeki Banka Hesaplarının Gelir Ve Gider \n"
                      + "  SELECT \n"
                      + "      0 as type, \n"
                      + "      (SELECT \n"
                      + "          COALESCE(SUM(CASE WHEN  bkam.is_direction=true THEN COALESCE(bkam.price,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming \n"
                      + "       FROM \n"
                      + "          finance.bankaccount bka \n"
                      + "          INNER JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = bka.id AND bbc.deleted=FALSE AND bbc.branch_id=?)\n"
                      + "          LEFT JOIN finance.bankaccountmovement bkam  ON(bka.id = bkam.bankaccount_id AND bkam.deleted = False AND bkam.movementdate BETWEEN ? AND ? ) \n"
                      + "          LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False) \n"
                      + "       WHERE \n"
                      + "          bka.deleted = false \n"
                      + "          AND bka.type_id = 14 \n"
                      + "          AND bkam.branch_id = ? \n"
                      + "      ) AS sumincoming, \n"
                      + "      COALESCE(SUM(CASE WHEN  bkam.is_direction = FALSE THEN COALESCE(bkam.price,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumoutcoming \n"
                      + "  FROM \n"
                      + "      finance.bankaccount bka \n"
                      + "      INNER JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = bka.id AND bbc.deleted=FALSE AND bbc.branch_id=?)\n"
                      + "      LEFT JOIN finance.bankaccountmovement bkam  ON(bka.id = bkam.bankaccount_id AND bkam.deleted = FALSE AND bkam.movementdate BETWEEN ? AND ?) \n"
                      + "      LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = FALSE) \n"
                      + "  WHERE \n"
                      + "      bka.deleted = false \n"
                      + "      AND bkam.branch_id = ? \n"
                      + "      \n"
                      + "  UNION ALL \n"
                      + "  --Kasanın Gelir Ve Gider \n"
                      + "  SELECT \n"
                      + "      1 AS type, \n"
                      + "      COALESCE(SUM(CASE WHEN  sfm.is_direction=true THEN COALESCE(sfm.price,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming, \n"
                      + "      COALESCE(SUM(CASE WHEN  sfm.is_direction=false THEN COALESCE(sfm.price,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumoutcoming \n"
                      + "  FROM \n"
                      + "      finance.safe sf \n"
                      + "      LEFT JOIN finance.safemovement sfm ON(sf.id = sfm.safe_id AND sfm.deleted=FALSE AND sfm.movementdate BETWEEN ? AND ?) \n"
                      + "  WHERE \n"
                      + "      sf.deleted = FALSE \n"
                      + "      AND sf.branch_id = ? \n"
                      + "      \n"
                      + "  UNION ALL \n"
                      + "  --Cari Üst Toplam \n"
                      + "  SELECT \n"
                      + "     2 as type, \n"
                      + "     COALESCE(SUM(CASE WHEN  accm.is_direction=false THEN COALESCE(accm.price*accm.exchangerate,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming, \n"
                      + "     COALESCE(SUM(CASE WHEN  accm.is_direction=true THEN COALESCE(accm.price*accm.exchangerate,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumoutcoming \n"
                      + "  FROM \n"
                      + "      general.account acc \n"
                      + "      INNER JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.branch_id=? AND abc.deleted=FALSE)\n"
                      + "      LEFT JOIN general.accountmovement accm ON(acc.id = accm.account_id  AND  accm.deleted=FALSE AND accm.movementdate BETWEEN ? AND ?) \n"
                      + "  WHERE acc.deleted = FALSE \n"
                      + "  AND acc.is_employee = FALSE AND accm.branch_id = ? AND acc.id <> 1 \n"
                      + "  UNION ALL \n"
                      + "  --Çek/Senet Gelir Ve Gider \n"
                      + "  SELECT \n"
                      + "      3 AS type, \n"
                      + "      COALESCE(SUM(CASE WHEN  cbp.is_direction=true THEN COALESCE(cbp.price*cbp.exchangerate,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming, \n"
                      + "      COALESCE(SUM(CASE WHEN  cbp.is_direction=false THEN COALESCE(cbp.price*cbp.exchangerate,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumoutcoming \n"
                      + "  FROM \n"
                      + "      finance.chequebill cb \n"
                      + "      INNER JOIN finance.chequebillpayment cbp ON(cb.id=cbp.chequebill_id AND cbp.deleted=FALSE AND cbp.processdate BETWEEN ? AND ?) \n"
                      + "  WHERE \n"
                      + "      cb.deleted = FALSE AND cb.branch_id = ? \n"
                      + "  UNION ALL \n"
                      + "  --Personel Gelir Ve Gider \n"
                      + "  SELECT \n"
                      + "      4 AS type, \n"
                      + "      COALESCE(SUM(CASE WHEN  accm.is_direction=true THEN COALESCE(accm.price*accm.exchangerate,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming, \n"
                      + "      COALESCE(SUM(CASE WHEN  accm.is_direction=false THEN COALESCE(accm.price*accm.exchangerate,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumoutcoming \n"
                      + "  FROM \n"
                      + "      general.account acc \n"
                      + "      LEFT JOIN general.accountmovement accm ON(acc.id = accm.account_id  AND  accm.deleted=FALSE AND accm.movementdate BETWEEN ? AND ?) \n"
                      + "      LEFT JOIN general.employeeinfo empi ON(empi.account_id = acc.id AND empi.deleted=FALSE) \n"
                      + "      INNER JOIN general.account_branch_con abc ON(abc.account_id = acc.id AND abc.branch_id=? AND abc.deleted=FALSE)\n"
                      + "  WHERE \n"
                      + "      acc.deleted = FALSE  \n"
                      + "      AND acc.is_employee = TRUE AND accm.branch_id = ? \n"
                      + "      AND empi.branch_id = ? \n"
                      + "  UNION ALL \n"
                      + "  --Gelir ve Gider \n"
                      + "  SELECT \n"
                      + "      5 AS type, \n"
                      + "      COALESCE(SUM(CASE WHEN ie.is_income=TRUE THEN iem.price*iem.exchangerate ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming, \n"
                      + "      COALESCE(SUM(CASE WHEN ie.is_income=FALSE THEN iem.price*iem.exchangerate ELSE 0 END),0)::NUMERIC(18,4) AS sumoutcoming \n"
                      + "  FROM \n"
                      + "      finance.incomeexpense ie \n"
                      + "      LEFT JOIN finance.incomeexpensemovement iem ON(ie.id = iem.incomeexpense_id AND iem.deleted = FALSE AND iem.movementdate BETWEEN ? AND ?) \n"
                      + "  WHERE \n"
                      + "      ie.deleted = FALSE \n"
                      + "      AND ie.parent_id IS NOT NULL AND ie.branch_id = ? \n"
                      + "      \n"
                      + "  UNION ALL \n"
                      + "  --Kredi Gelir Ve Gider \n"
                      + "  SELECT \n"
                      + "      6 AS type, \n"
                      + "      COALESCE(SUM(CASE WHEN crdt.is_customer=true THEN (crdt.money*COALESCE(crdt.exchangerate,1)) ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming, \n"
                      + "      COALESCE(SUM(CASE WHEN crdt.is_customer=false THEN (crdt.money*COALESCE(crdt.exchangerate,1)) ELSE 0 END),0)::NUMERIC(18,4) AS sumoutcoming \n"
                      + "  FROM  \n"
                      + "      finance.credit crdt \n"
                      + "  WHERE \n"
                      + "      crdt.deleted=FALSE \n"
                      + "      AND crdt.is_paid = FALSE \n"
                      + "      AND crdt.is_cancel = FALSE AND crdt.branch_id = ?\n"
                      + "      AND crdt.processdate BETWEEN ? AND ? \n"
                      + "  UNION ALL \n"
                      + "  \n"
                      + "  --Ürünler \n"
                      + "  SELECT \n"
                      + "       7 AS type, \n"
                      + "       CASE ? \n"
                      + "        WHEN 1 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentpurchaseprice,0)*(1+(COALESCE(tg.rate,0)/100)))),0)::NUMERIC(18,4)--kdvli satÄ±n alma     \n"
                      + "        WHEN 2 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * COALESCE(si.currentpurchaseprice,0)),0)::NUMERIC(18,4)--kdv siz satÄ±n alma \n"
                      + "        WHEN 3 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * COALESCE(si.currentsaleprice,0)),0)::NUMERIC(18,4)--kdv li satÄ±ÅŸ \n"
                      + "        WHEN 4 THEN COALESCE(SUM((COALESCE(inn.quantity,0) - COALESCE(out.quantity,0)) * (COALESCE(si.currentsaleprice,0)/(1+(COALESCE(tg.rate,0)/100)))),0)::NUMERIC(18,4)--kdvsiz satÄ±ÅŸ \n"
                      + "       END as sumincoming, \n"
                      + "       0 as sumoutcoming \n"
                      + "  FROM \n"
                      + "      inventory.warehouse w \n"
                      + "      INNER JOIN inventory.warehouseitem wi ON(w.id = wi.warehouse_id AND wi.deleted = FALSE) \n"
                      + "      LEFT JOIN inventory.stockinfo si ON(si.stock_id=wi.stock_id AND si.deleted=FALSE AND si.branch_id = w.branch_id) \n"
                      + "      LEFT JOIN inventory.stock_taxgroup_con stc ON (stc.stock_id = si.stock_id AND stc.deleted = FALSE AND stc.is_purchase = true ) \n"
                      + "      LEFT JOIN inventory.taxgroup tg ON (tg.id = stc.taxgroup_id AND tg.deleted = FALSE) \n"
                      + "      LEFT JOIN ( \n"
                      + "              SELECT \n"
                      + "                  wrm.stock_id, \n"
                      + "                  wrm.warehouse_id, \n"
                      + "                  COALESCE(SUM(wrm.quantity),0) AS quantity \n"
                      + "              FROM \n"
                      + "                  inventory.warehouse wr \n"
                      + "                  INNER JOIN inventory.warehousemovement wrm ON ( \n"
                      + "                      wrm.warehouse_id = wr.id \n"
                      + "                      AND wrm.c_time BETWEEN ? AND ? \n"
                      + "                      AND wrm.is_direction = TRUE \n"
                      + "                      AND wrm.deleted = FALSE \n"
                      + "                  ) \n"
                      + "              WHERE \n"
                      + "                  wr.deleted = FALSE \n"
                      + "                  AND wr.branch_id = ? \n"
                      + "              GROUP BY \n"
                      + "                  wrm.stock_id, \n"
                      + "                  wrm.warehouse_id \n"
                      + "      ) inn ON (inn.stock_id = wi.stock_id AND inn.warehouse_id = w.id) \n"
                      + "      LEFT JOIN ( \n"
                      + "              SELECT \n"
                      + "                  wrm.stock_id, \n"
                      + "                  wrm.warehouse_id, \n"
                      + "                  COALESCE(SUM(wrm.quantity),0) AS quantity \n"
                      + "              FROM \n"
                      + "                  inventory.warehouse wr \n"
                      + "                  INNER JOIN inventory.warehousemovement wrm ON ( \n"
                      + "                      wrm.warehouse_id = wr.id \n"
                      + "                      AND wrm.c_time BETWEEN ? AND ? \n"
                      + "                      AND wrm.is_direction = FALSE \n"
                      + "                      AND wrm.deleted = FALSE \n"
                      + "                  ) \n"
                      + "              WHERE \n"
                      + "                  wr.deleted = FALSE \n"
                      + "                  AND wr.branch_id = ? \n"
                      + "              GROUP BY \n"
                      + "                  wrm.stock_id, \n"
                      + "                  wrm.warehouse_id \n"
                      + "      ) out ON (out.stock_id = wi.stock_id AND out.warehouse_id = w.id)   \n"
                      + "  WHERE w.deleted=FALSE \n"
                      + "  AND w.branch_id = ? \n"
                      + "  UNION ALL \n"
                      + "  --Kredikartı Tipindeki Banka Hesaplarının Gelir Ve Gider \n"
                      + "  SELECT \n"
                      + "      8 as type, \n"
                      + "      COALESCE(SUM(CASE WHEN  bkam.is_direction = TRUE THEN COALESCE(bkam.price,0) ELSE 0 END),0)::NUMERIC(18,4) AS sumincoming, \n"
                      + "      0 AS sumoutcoming \n"
                      + "  FROM \n"
                      + "      finance.bankaccount bka \n"
                      + "      INNER JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = bka.id AND bbc.deleted=FALSE AND bbc.branch_id=?)\n"
                      + "      LEFT JOIN finance.bankaccountmovement bkam  ON(bka.id = bkam.bankaccount_id AND bkam.deleted = False AND bkam.movementdate BETWEEN ? AND ?) \n"
                      + "      LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = bkam.financingdocument_id AND fdoc.deleted = False) \n"
                      + "  WHERE \n"
                      + "      bka.deleted = FALSE \n"
                      + "      AND bka.type_id = 16 \n"
                      + "      AND bkam.branch_id = ? \n"
                      + ") tt ");
            ps.setInt(1, StationID);
            ps.setTimestamp(2, BeginDate);
            ps.setTimestamp(3, EndDate);
            ps.setInt(4, StationID);
            ps.setInt(5, StationID);
            ps.setTimestamp(6, BeginDate);
            ps.setTimestamp(7, EndDate);
            ps.setInt(8, StationID);
            ps.setTimestamp(9, BeginDate);
            ps.setTimestamp(10, EndDate);
            ps.setInt(11, StationID);
            ps.setInt(12, StationID);
            ps.setTimestamp(13, BeginDate);
            ps.setTimestamp(14, EndDate);
            ps.setInt(15, StationID);
            ps.setTimestamp(16, BeginDate);
            ps.setTimestamp(17, EndDate);
            ps.setInt(18, StationID);
            ps.setTimestamp(19, BeginDate);
            ps.setTimestamp(20, EndDate);
            ps.setInt(21, StationID);
            ps.setInt(22, StationID);
            ps.setInt(23, StationID);
            ps.setTimestamp(24, BeginDate);
            ps.setTimestamp(25, EndDate);
            ps.setInt(26, StationID);
            ps.setInt(27, StationID);
            ps.setTimestamp(28, BeginDate);
            ps.setTimestamp(29, EndDate);
            ps.setInt(30, Type);
            ps.setTimestamp(31, BeginDate);
            ps.setTimestamp(32, EndDate);
            ps.setInt(33, StationID);
            ps.setTimestamp(34, BeginDate);
            ps.setTimestamp(35, EndDate);
            ps.setInt(36, StationID);
            ps.setInt(37, StationID);
            ps.setInt(38, StationID);
            ps.setTimestamp(39, BeginDate);
            ps.setTimestamp(40, EndDate);
            ps.setInt(41, StationID);

            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // REPORT PURCHASE SUMMARY   ( Girilen tarihler arası, satın alma özet raporunu gösterir)
    @WebMethod(operationName = "PurchaseSummary")
    @WebResult(name = "TransactionResult")
    public String PurchaseSummary(@WebParam(name = "StationID") String stationID, @WebParam(name = "BeginDate") String beginDate, @WebParam(name = "EndDate") String endDate) {

        Timestamp BeginDate = null;
        Timestamp EndDate = null;
        String result = "";
        int StationID = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (beginDate != null && !beginDate.trim().isEmpty()) {
            try {
                BeginDate = new Timestamp(df.parse(aes.decrypt(beginDate)).getTime());
            } catch (ParseException ex) {
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Begin Date Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Begin Date  Value  \"}");
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                EndDate = new Timestamp(df.parse(aes.decrypt(endDate)).getTime());
            } catch (ParseException ex) {
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control End Date Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control End Date Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "       array_to_json(array_agg(row_to_json(t))) as r_result FROM(\n"
                      + "    SELECT\n"
                      + "        invi.stock_id AS stock_id,\n"
                      + "        stck.name AS name,\n"
                      + "        stck.barcode AS barcode,\n"
                      + "        invi.unit_id AS unitid,\n"
                      + "        gunt.sortname AS unitname,\n"
                      + "        gunt.unitrounding AS unitrounding,\n"
                      + "        br.name as brandname,\n"
                      + "        invi.unitprice AS unitprice,\n"
                      + "        SUM(invi.totalmoney) AS totalmoney,\n"
                      + "        invi.currency_id AS currency,\n"
                      + "        SUM(invi.quantity) AS quantity,\n"
                      + "        COALESCE(SUM(SUM(invi.totalmoney)) OVER(PARTITION BY invi.stock_id, invi.currency_id),0) as totalmoneybystock,\n"
                      + "        COALESCE(SUM(SUM(invi.quantity)) OVER(PARTITION BY invi.stock_id,\n"
                      + "        invi.currency_id),0) as totalcountbystock\n"
                      + "    FROM finance.invoiceitem invi\n"
                      + "        INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = False)\n"
                      + "        INNER JOIN inventory.stock stck  ON(stck.id = invi.stock_id)\n"
                      + "        LEFT JOIN general.unit gunt ON(gunt.id=invi.unit_id)\n"
                      + "        LEFT JOIN general.brand br ON(br.id=stck.brand_id)\n"
                      + "    WHERE  invi.deleted = False AND (CASE WHEN invi.is_calcincluded = TRUE AND inv.differentdate BETWEEN ? AND ? THEN FALSE ELSE TRUE END) \n"
                      + "        AND inv.is_purchase = True AND inv.branch_id = ?\n"
                      + "        AND inv.invoicedate BETWEEN ? AND ?\n"
                      + "    GROUP BY\n"
                      + "        invi.stock_id, stck.name, stck.barcode,\n"
                      + "        invi.unit_id, gunt.sortname,gunt.unitrounding,\n"
                      + "        invi.unitprice, invi.currency_id,br.name\n"
                      + "    ORDER BY invi.stock_id\n"
                      + "    ) t");
            ps.setTimestamp(1, BeginDate);
            ps.setTimestamp(2, EndDate);
            ps.setInt(3, StationID);
            ps.setTimestamp(4, BeginDate);
            ps.setTimestamp(5, EndDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // PrintTags   ( Yazdırılacak Etiket Listesini Getirir)
    @WebMethod(operationName = "PrintTags")
    @WebResult(name = "TransactionResult")
    public String PrintTags(@WebParam(name = "StationID") String stationID, @WebParam(name = "UserID") String userID) {

        int StationID = 0;
        String result = "";
        int UserID = 0;

        if (stationID != null && !stationID.trim().isEmpty()) {

            StationID = Integer.valueOf(aes.decrypt(stationID));

        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID  Value  \"}");
        }
        if (userID != null && !userID.trim().isEmpty()) {
            try {
                UserID = Integer.parseInt(aes.decrypt(userID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT \n"
                      + "       array_to_json(array_agg(row_to_json(eticket))) as r_result \n"
                      + "FROM (\n"
                      + "      SELECT\n"
                      + "       stck.id,\n"
                      + "       COALESCE(stck.name,'-') as name,\n"
                      + "       COALESCE(stck.barcode,'-') as barcode,\n"
                      + "       COALESCE(gunt.sortname,'') as unit_name,\n"
                      + "       COALESCE(gunt.unitrounding,0) as unitrounding,\n"
                      + "       ptag.quantity,\n"
                      + "       ctrd.name AS countryname,\n"
                      + "       TO_CHAR(stcki.lastsalepricechangedate, 'DD.MM.yyyy') AS lastsalepricechangedate,-- Ürünün Son Satış Fiyat Değişim Tarihi\n"
                      + "       COALESCE(stcki.salemandatoryprice,0) AS salemandatoryprice,                                     -- Satışın Zorunlu Birim Fiyatı\n"
                      + "       COALESCE(stcki.salemandatorycurrency_id,0) AS salemandatorycurrency_id,         -- Satışın Zorunlu Fiyat Birimi\n"
                      + "       -- Satış Fiyatına KDV Dahil İse Direkt Bas.\n"
                      + "       COALESCE(CASE WHEN pli.is_taxincluded = TRUE THEN \n"
                      + "           pli.price\n"
                      + "       -- KDV Dahil Değil İse Hesapla.\n"
                      + "       ELSE \n"
                      + "           (COALESCE(pli.price,0)*(1+(COALESCE(stg.rate,0)/100))) \n"
                      + "       END,0) AS saleprice,    \n"
                      + "       COALESCE(pli.currency_id,1) AS salecurrency_id,    \n"
                      + "       COALESCE(wu.mainweightunit_id,0) AS mainweightunit_id,\n"
                      + "       COALESCE(wu.mainweight,0) AS mainweight,    \n"
                      + "       COALESCE(mwu.sortname,'') AS mainsortname,\n"
                      + "       COALESCE(stcki.weight,0) AS weight,\n"
                      + "       COALESCE(stcki.weightunit_id,0) AS weightunit_id\n"
                      + "FROM \n"
                      + "       log.printtag ptag \n"
                      + "       INNER JOIN inventory.stock stck ON(stck.id = ptag.stock_id AND stck.deleted = FALSE)\n"
                      + "       INNER JOIN inventory.stockinfo stcki ON(stcki.stock_id = stck.id AND stcki.branch_id = ? AND stcki.deleted = FALSE)\n"
                      + "       INNER JOIN general.unit gunt ON (gunt.id = stck.unit_id AND gunt.deleted = FALSE)\n"
                      + "       INNER JOIN general.branch br ON (br.id=ptag.branch_id AND br.deleted = FALSE)\n"
                      + "       LEFT JOIN general.userdata usr ON(usr.id = ? AND usr.deleted=FALSE)\n"
                      + "       LEFT JOIN system.country_dict ctrd ON (ctrd.country_id = stck.country_id AND ctrd.language_id = usr.language_id AND ctrd.deleted = FALSE)\n"
                      + "       LEFT JOIN general.unit wu ON(wu.id = stcki.weightunit_id AND wu.deleted = FALSE)\n"
                      + "       LEFT JOIN general.unit mwu ON(mwu.id = wu.mainweightunit_id AND mwu.deleted = FALSE)\n"
                      + "       LEFT JOIN inventory.pricelist pl ON (pl.is_default = TRUE AND pl.deleted = FALSE AND pl.branch_id = ? AND pl.is_purchase = FALSE) \n"
                      + "       LEFT JOIN inventory.pricelistitem pli ON (pli.pricelist_id = pl.id and pli.stock_id = stck.id and pli.deleted = FALSE)\n"
                      + "       LEFT JOIN (SELECT \n"
                      + "                 txg.rate AS rate,\n"
                      + "                 stc.stock_id AS stock_id \n"
                      + "               FROM \n"
                      + "                 inventory.stock_taxgroup_con stc  \n"
                      + "                 INNER JOIN inventory.taxgroup txg  ON (txg.id = stc.taxgroup_id AND txg.deleted = false)\n"
                      + "               WHERE \n"
                      + "                 stc.deleted = FALSE\n"
                      + "                 AND txg.type_id = 10 --kdv grubundan \n"
                      + "                 AND stc.is_purchase = FALSE) stg ON (stg.stock_id = stck.id)  \n"
                      + "WHERE \n"
                      + "       ptag.branch_id = ?\n"
                      + ")eticket");
            ps.setInt(1, StationID);
            ps.setInt(2, UserID);
            ps.setInt(3, StationID);
            ps.setInt(4, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    //ProcessPrintTag   (Etiket tablosuna ekleme silme ve güncelleme işlemi yapar)
    @WebMethod(operationName = "ProcessPrintTag")
    @WebResult(name = "TransactionResult")
    public String ProcessPrintTag(@WebParam(name = "StationID") String stationID, @WebParam(name = "ProcessType") String processType, @WebParam(name = "StockID") String stockID, @WebParam(name = "Quantity") String quantity) {

        int StationID = 0, ProcessType = 0, StockID = 0;
        double Quantity = 0;
        BigDecimal bQuantity;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            StationID = Integer.valueOf(aes.decrypt(stationID));
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID  Value  \"}");
        }

        if (processType != null && !processType.trim().isEmpty()) {
            ProcessType = Integer.valueOf(aes.decrypt(processType));
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type  Value  \"}");
        }

        if (stockID != null && !stockID.trim().isEmpty()) {
            StockID = Integer.valueOf(aes.decrypt(stockID));
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Stock ID  Value  \"}");
        }

        if (quantity != null && !quantity.trim().isEmpty()) {
            Quantity = Double.parseDouble(aes.decrypt(quantity));
            bQuantity = new BigDecimal(Quantity);

        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Quantity  Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT * FROM log.ws_process_printtag(?,?,?,?);");
            ps.setInt(1, ProcessType);
            ps.setInt(2, StationID);
            ps.setInt(3, StockID);
            ps.setBigDecimal(4, bQuantity);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // PROCESS WAYBILLS   (İrsaliye ekleme silme ve güncelleme ve listeleme işlemlerini yapar)
    @WebMethod(operationName = "ProcessWaybills")
    @WebResult(name = "TransactionResult")
    public String ProcessWaybills(@WebParam(name = "StationID") String stationID, @WebParam(name = "ProcessType") String processType, @WebParam(name = "WaybillData") String waybillData, @WebParam(name = "UserID") String userID) {

        int StationID = 0;
        int ProcessType = 0;
        String WaybillData = aes.decrypt(waybillData);
        int UserID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (processType != null && !processType.trim().isEmpty()) {
            try {
                ProcessType = Integer.parseInt(aes.decrypt(processType));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
        }

        if (userID != null && !userID.trim().isEmpty()) {
            try {
                UserID = Integer.parseInt(aes.decrypt(userID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from finance.ws_process_waybill (?,?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, StationID);
            ps.setInt(2, ProcessType);
            ps.setString(3, WaybillData);
            ps.setInt(4, UserID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // GET DOCUMENT NUMBERS (Fatura ve İrsaliye için Seri Sıra numaralarını çeker)
    @WebMethod(operationName = "GetDocumentNumbers")
    @WebResult(name = "TransactionResult")
    public String GetDocumentNumbers(@WebParam(name = "StationID") String stationID, @WebParam(name = "ItemID") String itemID) {

        int StationID = 0;
        int ItemID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        if (itemID != null && !itemID.trim().isEmpty()) {
            try {
                ItemID = Integer.parseInt(aes.decrypt(itemID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Item ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Item ID Value  \"}");
        }
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "         array_to_json(array_agg(to_json(documentnumbers))) AS r_result\n"
                      + "         FROM (\n"
                      + "                     SELECT\n"
                      + "                             id,\n"
                      + "                             serial,\n"
                      + "                             actualnumber\n"
                      + "                     FROM\n"
                      + "                             general.documentnumber dcn \n"
                      + "                     WHERE\n"
                      + "                             dcn.item_id=?\n"
                      + "                             AND\n"
                      + "                             dcn.branch_id=?\n"
                      + "                             AND \n"
                      + "                             dcn.deleted=FALSE\n"
                      + ")documentnumbers");
            ps.setInt(1, ItemID);
            ps.setInt(2, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }
            result = "{\"Result\":1,\"Response\":" + result + "}";

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    //GET ORDERS (Siparişler sayfası için stok kontrolü ve sipariş oluşturmaya yarar)
    @WebMethod(operationName = "GetOrders")
    @WebResult(name = "TransactionResult")
    public String GetDeneme(@WebParam(name = "ProcessType") String processType,
              @WebParam(name = "BranchID") String branchID,
              @WebParam(name = "Barcode") String barcode,
              @WebParam(name = "JsonData") String jsonData,
              @WebParam(name = "OrderID") String orderID,
              @WebParam(name = "UserID") String userID) {

        int ProcessType = 0;
        int BranchID = 0;
        int OrderID = 0;
        int UserID = 0;
        String Barcode = "";
        String JsonData = "";
        String result = "";

        if (processType != null && !processType.trim().isEmpty()) {
            try {
                ProcessType = Integer.parseInt(aes.decrypt(processType));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Process Type Value  \"}");
        }

        if (branchID != null && !branchID.trim().isEmpty()) {
            try {
                BranchID = Integer.parseInt(aes.decrypt(branchID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Branch ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Branch ID Value  \"}");
        }

        if (orderID != null && !orderID.trim().isEmpty()) {
            try {
                OrderID = Integer.parseInt(aes.decrypt(orderID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Order ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Order ID Value  \"}");
        }

        if (ProcessType == 2) {
            if (barcode != null && !barcode.trim().isEmpty()) {
                try {
                    Barcode = aes.decrypt(barcode);
                    System.out.println("Barcode" + Barcode);
                } catch (NumberFormatException ex) { // handle your exception
                    return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Barcode Value  \"}");
                }
            } else {
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Barcode Value  \"}");
            }
        }

        if (ProcessType == 3 || ProcessType == 5 || ProcessType == 6) {

            if (jsonData != null && !jsonData.trim().isEmpty()) {
                try {
                    JsonData = aes.decrypt(jsonData);
                } catch (NumberFormatException ex) { // handle your exception
                    return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Json Value  \"}");
                }
            } else {
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Json Value  \"}");
            }
        }
        if (ProcessType == 3 || ProcessType == 4 || ProcessType == 5 || ProcessType == 6) {
            if (userID != null && !userID.trim().isEmpty()) {
                try {
                    UserID = Integer.parseInt(aes.decrypt(userID));
                } catch (NumberFormatException ex) { // handle your exception
                    return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
                }
            } else {
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control User ID Value  \"}");
            }
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from finance.ws_process_order (?,?,?,?,?,?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, ProcessType);
            ps.setInt(2, BranchID);
            ps.setString(3, Barcode);
            ps.setString(4, JsonData);
            ps.setInt(5, OrderID);
            ps.setInt(6, UserID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    // GetStickerProperties (Etiket için gerekli alanları listeler)
    @WebMethod(operationName = "GetStickerProperties")
    @WebResult(name = "TransactionResult")
    public String GetStickerProperties(@WebParam(name = "StationID") String stationID) {

        int StationID = 0;
        String result = "";

        if (stationID != null && !stationID.trim().isEmpty()) {
            try {
                StationID = Integer.parseInt(aes.decrypt(stationID));
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Station ID Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from general.ws_sticker_properties (?) ");
            ps.setEscapeProcessing(true);
            ps.setInt(1, StationID);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

// GetOrderItemInvoice(Siparişten fatura ve irsaliye için)
    @WebMethod(operationName = "GetOrderItemInvoice")
    @WebResult(name = "TransactionResult")
    public String GetOrderItemInvoice(@WebParam(name = "OrderIDs") String stationID) {

        String OrderIDs = "";
        String result = "";

        if (OrderIDs != null && !OrderIDs.trim().isEmpty()) {
            try {
                OrderIDs = aes.decrypt(OrderIDs);
            } catch (NumberFormatException ex) { // handle your exception
                return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Json Value  \"}");
            }
        } else {
            return aes.encrypt("{\"Result\":0,\"Response\":\" Please Control Json Value  \"}");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = marwizDS.getConnection();
            ps = con.prepareStatement("Select * from finance.ws_orderitem_invoice (?) ");
            ps.setEscapeProcessing(true);
            ps.setString(1, OrderIDs);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = (rs.getString("r_result"));
            }

        } catch (SQLException ex) {
            result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                result = "{\"Result\":0,\"Response\":\"" + ex.toString() + " \"}";
            }
        }
        return aes.encrypt(result);
    }

    /*-------------------------------------------------------------------------MOBİL UYGULAMA(MARWI)--------------------------------------------------------------------------------*/
 /*-------------------------------------------------------------------------MAGICLICK INTEGRATION--------------------------------------------------------------------------------*/
    //Bu web servis Stawiz'den aldığı verileri transaction no ve lisans koduna göre karşılaştırıp log tablosunu update etmeye yarar
    @WebMethod(operationName = "ProcessMagiclick")
    @WebResult(name = "TransactionResult")
    public String ProcessMagiclick(@WebParam(name = "LicenseCode") String licenseCode,
              @WebParam(name = "TransactionNo") String transactionNo,
              @WebParam(name = "Result") String stawizResult,
              @WebParam(name = "ResultMessage") String stawizResultMessage,
              @WebParam(name = "MagiclickRequest") String stawizResponse) { //Magiclickten stawize gelen datanın tamamı

        int userID = 0;
        int StawizResult;
        String result = "";
        String responseMessage = "";
        Timestamp CurrentDate = null;

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = Calendar.getInstance().getTime();

        if (stawizResult != null && !stawizResult.trim().isEmpty()) {
            try {
                StawizResult = Integer.parseInt(stawizResult);
            } catch (NumberFormatException ex) { // handle your exception
                return "<Result>0</Result><Response>Please Control Stawiz Result Value</Response>";
            }
        } else {
            return "<Result>0</Result><Response>Please Control Stawiz Result Value</Response>";
        }

        try {
            userID = getID();
        } catch (SQLException ex) {
            return "<Result>0</Result><Response>" + ex + "</Response>";

        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = marwizDS.getConnection();

                con = marwizDS.getConnection();
                ps = con.prepareStatement("Select * from log.ws_process_magiclick (?,?,?,?,?,?) ");
                ps.setEscapeProcessing(true);
                ps.setString(1, transactionNo);
                ps.setString(2, licenseCode);
                ps.setInt(3, StawizResult);
                ps.setString(4, stawizResponse);
                ps.setString(5, stawizResultMessage);
                ps.setTimestamp(6, new Timestamp(date.getTime()));

                rs = ps.executeQuery();
                while (rs.next()) {
                    result = String.valueOf(rs.getInt("r_responsecode"));
                    responseMessage = String.valueOf(rs.getString("r_responsemessage"));
                }
                //result = "{\"Result\":" + result + ",\"Response\":" + responseMessage + "}";
                result = "<Result>" + result + "</Result><Response>" + responseMessage + "</Response>";

            } catch (SQLException ex) {
                result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";

            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";
                }
            }
        } else {
            result = "<Result>0</Result><Response>Wrong Username or Password</Response>";
        }

        return result;
    }
    //Bu web servis Stawiz'den aldığı verileri transaction no ve lisans koduna göre karşılaştırıp log tablosunu update etmeye yarar

    @WebMethod(operationName = "MagiclickStockList")
    @WebResult(name = "TransactionResult")
    public String MagiclickStockList(@WebParam(name = "LicenceCode") String licenceCode) {

        int userID = 0;
        String responseCode = "";
        String responseMessage = "";
        boolean isSuccess = false;
        String result = "";

        try {
            userID = getID();
        } catch (SQLException ex) {
            return "<Result>0</Result><Response>" + ex + "</Response>";

        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = marwizDS.getConnection();
                ps = con.prepareStatement("Select * from general.ws_vehicledelivery_stock (?) ");
                ps.setEscapeProcessing(true);
                ps.setString(1, licenceCode);

                rs = ps.executeQuery();
                while (rs.next()) {
                    responseCode = String.valueOf(rs.getInt("r_responsecode"));
                    responseMessage = String.valueOf(rs.getString("r_responsemessage"));
                }
                if (responseCode.equals("200")) {
                    isSuccess = true;
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else {
                    responseCode = "0";
                    responseMessage = "Operation Failed!!";
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                }
                //result = "{\"Result\":" + result + ",\"Response\":" + responseMessage + "}";

            } catch (SQLException ex) {
                responseCode = "0";
                responseMessage = ex.toString();
                result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";

            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    responseCode = "0";
                    responseMessage = ex.toString();
                    result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";
                }
            }
        } else {
            responseCode = "0";
            responseMessage = "Wrong Username or Password";
            result = "<Result>0</Result><Response>Wrong Username or Password</Response>";
        }

        InsertMagiclickRequestLog(licenceCode, responseMessage, responseCode, isSuccess, licenceCode, 2);

        return result;
    }

    @WebMethod(operationName = "MagiclickBranchInfo")
    @WebResult(name = "TransactionResult")
    public String MagiclickBranchInfo(@WebParam(name = "LicenceCode") String licenceCode) {

        int userID = 0;
        String result = "";
        String responseCode = "";
        String responseMessage = "";
        boolean isSuccess = false;

        try {
            userID = getID();
        } catch (SQLException ex) {
            return "<Result>0</Result><Response>" + ex + "</Response>";
        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = marwizDS.getConnection();

                ps = con.prepareStatement("Select * from general.ws_vehicledelivery_branch( ?)");
                ps.setEscapeProcessing(true);
                ps.setString(1, licenceCode);

                rs = ps.executeQuery();

                while (rs.next()) {
                    responseCode = String.valueOf(rs.getInt("r_responsecode"));
                    responseMessage = String.valueOf(rs.getString("r_responsemessage"));
                }
                if (responseCode.equals("200")) {
                    isSuccess = true;
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else {
                    responseCode = "0";
                    responseMessage = "Operation Failed!";
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                }
                //result = "{\"Result\":" + result + ",\"Response\":" +responseMessage + "}";

            } catch (SQLException ex) {
                responseCode = "0";
                responseMessage = ex.toString();
                result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";

            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    responseCode = "0";
                    responseMessage = ex.toString();
                    result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";
                }
            }
        } else {
            responseCode = "0";
            responseMessage = "Wrong Username or Password";
            result = "<Result>0</Result><Response>Wrong Username or Password</Response >";
        }

        InsertMagiclickRequestLog(licenceCode, responseMessage, responseCode, isSuccess, licenceCode, 1);

        return result;

    }

    //Stawiz tarafından Marwiz tarafındaki webservisleri tetiklemek için yazılmıştır.
    @WebMethod(operationName = "CallWebServiceStawiz")
    @WebResult(name = "TransactionResult")
    public String CallWebServiceStawiz(@WebParam(name = "LicenceCode") String licenceCode, @WebParam(name = "ProcessType") int processType) {
        int userID = 0;
        String result = "";

        try {
            userID = getID();
        } catch (SQLException ex) {
            return "<Result>0</Result><Response>" + ex + "</Response>";
        }

        if (userID > 0) {
            String res = null;

            if (processType == 1) {
                BranchSetting branchSetting = new BranchSetting();
                branchSetting = BringBranchInfo(licenceCode);
                try {

                    WebServiceClient webServiceClient = new WebServiceClient();
                    String data
                              = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                              + "    <SOAP-ENV:Header/>\n"
                              + "    <S:Body>\n"
                              + "        <ns2:GetLicenceCode xmlns:ns2=\"http://ws/\">\n"
                              + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                              + "        </ns2:GetLicenceCode>\n"
                              + "    </S:Body>\n"
                              + "</S:Envelope>";

                    res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
                    result = LogBranchInfo(branchSetting.getBranch().getId(), res);
                } catch (Exception ex) {
                    result = "<Result>0</Result><Response>" + ex.getMessage() + "</Response >";
                }
            } else {
                result = "<Result>0</Result><Response>Wrong Process Type</Response >";
            }

        } else {
            result = "<Result>0</Result><Response>Wrong Username or Password</Response >";
        }

        return result;

    }

    @WebMethod(operationName = "MagiclickTakeOrder")
    @WebResult(name = "TransactionResult")
    public String MagiclickTakeOrder(@WebParam(name = "LicenceCode") String licenceCode, @WebParam(name = "OrderJson") String orderJson) {

        int userID = 0;
        String responseCode = "";
        String responseMessage = "";
        boolean isSuccess = false;
        String result = "";

        try {
            userID = getID();
        } catch (SQLException ex) {
            return "<Result>0</Result><Response>" + ex + "</Response>";

        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = marwizDS.getConnection();
                ps = con.prepareStatement("Select * from general.ws_vehicledelivery_order (?, ?, ?) ");
                ps.setEscapeProcessing(true);
                ps.setString(1, licenceCode);
                ps.setString(2, orderJson);
                ps.setInt(3, userID);

                rs = ps.executeQuery();
                while (rs.next()) {
                    responseCode = String.valueOf(rs.getInt("r_responsecode"));
                    responseMessage = String.valueOf(rs.getString("r_responsemessage"));
                }
                if (responseCode.equals("200")) {
                    isSuccess = true;
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else if (responseCode.equals("-2")) {
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else {
                    responseCode = "0";
                    responseMessage = "Operation Failed!!";
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                }

            } catch (SQLException ex) {
                responseCode = "0";
                responseMessage = ex.toString();
                result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";

            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    responseCode = "0";
                    responseMessage = ex.toString();
                    result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";
                }
            }
        } else {
            responseCode = "0";
            responseMessage = "Wrong Username or Password";
            result = "<Result>0</Result><Response>Wrong Username or Password</Response>";
        }

        InsertMagiclickRequestLog(orderJson, responseMessage, responseCode, isSuccess, licenceCode, 6);
        return result;
    }

    @WebMethod(operationName = "MagiclickOrderStatus")
    @WebResult(name = "TransactionResult")
    public String MagiclickOrderStatus(@WebParam(name = "LicenceCode") String licenceCode, @WebParam(name = "ProcessType") int processType, @WebParam(name = "OrderId") String orderId) {

        int userID = 0;
        String responseCode = "";
        String responseMessage = "";
        boolean isSuccess = false;
        String result = "";

        try {
            userID = getID();
        } catch (SQLException ex) {
            return "<Result>0</Result><Response>" + ex + "</Response>";

        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = marwizDS.getConnection();
                ps = con.prepareStatement("Select * from general.ws_vehicledelivery_orderstatus (?, ?, ?, ?)");
                ps.setEscapeProcessing(true);
                ps.setString(1, licenceCode);
                ps.setInt(2, processType);
                ps.setString(3, orderId);
                ps.setInt(4, userID);

                rs = ps.executeQuery();
                while (rs.next()) {
                    responseCode = String.valueOf(rs.getInt("r_responsecode"));
                    responseMessage = String.valueOf(rs.getString("r_responsemessage"));
                }
                if (responseCode.equals("200")) {
                    isSuccess = true;
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else if (responseCode.equals("-2") || responseCode.equals("-3")) {
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else {
                    responseCode = "0";
                    responseMessage = "Operation Failed!";
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                }

            } catch (SQLException ex) {
                responseCode = "0";
                responseMessage = ex.toString();
                result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";

            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    responseCode = "0";
                    responseMessage = ex.toString();
                    result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";
                }
            }
        } else {
            responseCode = "0";
            responseMessage = "Wrong Username or Password";
            result = "<Result>0</Result><Response>Wrong Username or Password</Response>";
        }
        if (processType == 0) {//İptal
            InsertMagiclickRequestLog(orderId, responseMessage, responseCode, isSuccess, licenceCode, 4);
        } else if (processType == 1) {//Teslim edildi
            InsertMagiclickRequestLog(orderId, responseMessage, responseCode, isSuccess, licenceCode, 5);
        } else if (processType == 2) {//Müşteri Geldi
            InsertMagiclickRequestLog(orderId, responseMessage, responseCode, isSuccess, licenceCode, 3);
        }

        return result;
    }

    //Log tablosuna kayıt atmak için
    public boolean InsertMagiclickRequestLog(String request, String responseMessage, String responseCode, boolean isSuccess, String licenceCode, int processType) {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            con = marwizDS.getConnection();
            ps = con.prepareStatement("INSERT INTO log.magiclick_requests (branch_id, type_id, processdate, request, response, responsecode, is_success)\n"
                      + "VALUES((SELECT br.id FROM general.branch br WHERE br.deleted=FALSE AND UPPER(br.licencecode) = UPPER(?)), ?, ?, ?, ?, ?, ?);");
            ps.setEscapeProcessing(true);
            ps.setString(1, licenceCode);
            ps.setInt(2, processType);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(3, timestamp);
            ps.setString(4, request);
            ps.setString(5, responseMessage);
            ps.setString(6, responseCode);
            ps.setBoolean(7, isSuccess);

            rs = ps.executeQuery();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(WsIncome.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(WsIncome.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    //Gelen lisans numarasına göre çağrılan web servisten gelen veriyi DB kaydeder
    public String LogBranchInfo(int branchId, String response) {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String responseCode = "";
        String responseMessage = "";
        String result = "";

        try {

            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT * FROM log.set_licencecode (?, ?)");
            ps.setEscapeProcessing(true);
            ps.setInt(1, branchId);
            ps.setString(2, response);

            rs = ps.executeQuery();
            while (rs.next()) {
                responseCode = String.valueOf(rs.getInt("r_responsecode"));
                responseMessage = rs.getString("r_responsemessage");
            }
            result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
            return result;

        } catch (SQLException ex) {
            responseCode = "0";
            result = "<Result>" + responseCode + "</Result><Response>" + ex.toString() + "</Response>";
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                responseCode = "0";
                result = "<Result>" + responseCode + "</Result><Response>" + ex.toString() + "</Response>";
            }
        }

        return responseCode;
    }

    //Gelen lisans numarasına göre web servis çağrılacak şubenin bilgilerini bulan fonksiyon
    public BranchSetting BringBranchInfo(String licenceCode) {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BranchSetting branchSetting = new BranchSetting();

        try {

            con = marwizDS.getConnection();
            ps = con.prepareStatement("SELECT\n"
                      + "br.licencecode,\n"
                      + "br.id AS brid,\n"
                      + "brs.wsendpoint,\n"
                      + "brs.wsusername,\n"
                      + "brs.wspassword\n"
                      + "FROM general.branchsetting brs\n"
                      + "INNER JOIN general.branch br ON(br.id = brs.branch_id AND br.deleted=FALSE)\n"
                      + "WHERE brs.deleted=FALSE AND UPPER(br.licencecode) = UPPER(?)\n");
            ps.setEscapeProcessing(true);
            ps.setString(1, licenceCode);

            rs = ps.executeQuery();
            while (rs.next()) {
                branchSetting.getBranch().setLicenceCode(rs.getString("licencecode"));
                branchSetting.getBranch().setId(rs.getInt("brid"));
                branchSetting.setwSendPoint(rs.getString("wsendpoint"));
                branchSetting.setWebServiceUserName(rs.getString("wsusername"));
                branchSetting.setWebServicePassword(rs.getString("wspassword"));
            }
            return branchSetting;
        } catch (SQLException ex) {
            Logger.getLogger(WsIncome.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(WsIncome.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return branchSetting;
    }

    /*-------------------------------------------------------------------------MAGICLICK INTEGRATION--------------------------------------------------------------------------------*/
 /*--------------------------------------------------------KAHVE OTOMATI (GLORIA)------------------------------------------------------------------------------------------------*/
    @WebMethod(operationName = "CheckGloriaBarcode")
    @WebResult(name = "TransactionResult")
    public String CheckGloriaBarcode(@WebParam(name = "MacAddress") String macAddress, @WebParam(name = "Barcode") String barcode) {
        System.out.println("-----------CheckGloriaBarcode");
        int userID = 0;
        String result = "";

        String MacAddress = aes.decrypt(macAddress);
        String Barcode = aes.decrypt(barcode);
        // String MacAddress = macAddress;
        //  String Barcode = barcode;

        System.out.println("-Barcode---" + Barcode);

        try {
            userID = getID();
            System.out.println("---getId");
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":" + ex + ",\n"
                      + "   \"ErrorCode\":\"3001\"\n"
                      + "}";
        }
        System.out.println("---geçti" + userID);
        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                System.out.println("---try--- geldi");
                con = marwizDS.getConnection();
                ps = con.prepareStatement("SELECT * from log.check_barcodegloria (?,?) ");
                ps.setEscapeProcessing(true);
                ps.setString(1, MacAddress);
                ps.setString(2, Barcode);
                rs = ps.executeQuery();
                System.out.println("----query çalıştı");
                while (rs.next()) {
                    result = (rs.getString("r_result"));
                }
                System.out.println("*result---" + result);

            } catch (SQLException ex) {
                result = "{  \n"
                          + "   \"Result\":false,\n"
                          + "   \"ErrorMessage\":" + ex + ",\n"
                          + "   \"ErrorCode\":\"3001\"\n"
                          + "}";
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(WsIncome.class
                              .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":\"KULLANICI ADI VEYA ŞİFRE HATALI.\",\n"
                      + "   \"ErrorCode\":\"3000\"\n"
                      + "}";
        }

        return aes.encrypt(result);
    }

    @WebMethod(operationName = "UsedGloriaBarcode")
    @WebResult(name = "TransactionResult")
    public String UsedGloriaBarcode(@WebParam(name = "MacAddress") String macAddress, @WebParam(name = "Barcode") String barcode) {
        System.out.println("-----------UsedGloriaBarcode");
        int userID = 0;
        String result = "";

        String MacAddress = aes.decrypt(macAddress);
        String Barcode = aes.decrypt(barcode);
        //String MacAddress = macAddress;
        //String Barcode = barcode;

        System.out.println("-Barcode---" + Barcode);

        try {
            userID = getID();
            System.out.println("---getId");
        } catch (SQLException ex) {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":" + ex + ",\n"
                      + "   \"ErrorCode\":\"3001\"\n"
                      + "}";
        }
        System.out.println("---geçti" + userID);
        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                System.out.println("---try--- geldi");
                con = marwizDS.getConnection();
                ps = con.prepareStatement("SELECT * from log.usedgloriabarcode (?,?)");
                ps.setEscapeProcessing(true);
                ps.setString(1, MacAddress);
                ps.setString(2, Barcode);
                rs = ps.executeQuery();
                System.out.println("----query çalıştı");
                while (rs.next()) {
                    result = (rs.getString("r_result"));
                }
                System.out.println("*result---" + result);

            } catch (SQLException ex) {
                result = "{  \n"
                          + "   \"Result\":false,\n"
                          + "   \"ErrorMessage\":" + ex + ",\n"
                          + "   \"ErrorCode\":\"3001\"\n"
                          + "}";
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();

                    }
                } catch (SQLException ex) {
                    Logger.getLogger(WsIncome.class
                              .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            result = "{  \n"
                      + "   \"Result\":false,\n"
                      + "   \"ErrorMessage\":\"KULLANICI ADI VEYA ŞİFRE HATALI.\",\n"
                      + "   \"ErrorCode\":\"3000\"\n"
                      + "}";
        }

        return aes.encrypt(result);
    }

    /*--------------------------------------------------------KAHVE OTOMATI (GLORIA)------------------------------------------------------------------------------------------------*/

 /*----------------------------------------------------------ARAÇ TAKİP RAPORU(ŞUBE BİLGİLERİ) ------------------------------------------------------------------------------ */
 /*---------------------------------------------TORA YIKAMA----------------------------------------------------------------------------------------------------------------*/
    @WebMethod(operationName = "GetWashingMachineSale")
    @WebResult(name = "TransactionResult")
    public String GetWashingMachineSale(@WebParam(name = "LicenceCode") String licenceCode, @WebParam(name = "SaleJson") String saleJson) {

        int userID = 0;
        String responseCode = "";
        String responseMessage = "";
        String result = "";
        boolean isSuccess = false;

        try {
            userID = getID();
        } catch (SQLException ex) {
            return "<Result>0</Result><Response>" + ex + "</Response>";

        }

        if (userID > 0) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = marwizDS.getConnection();
                ps = con.prepareStatement("Select * from wms.ws_insert_washingmachinesale (?, ?, ?) ");
                ps.setEscapeProcessing(true);
                ps.setString(1, licenceCode);
                ps.setString(2, saleJson);
                ps.setInt(3, userID);

                rs = ps.executeQuery();
                while (rs.next()) {
                    responseCode = String.valueOf(rs.getInt("r_responsecode"));
                    responseMessage = String.valueOf(rs.getString("r_responsemessage"));
                }
                if (responseCode.equals("200")) {
                    isSuccess = true;
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else if (responseCode.equals("-2") || responseCode.equals("-3") || responseCode.equals("-4")) {
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                } else {
                    responseCode = "0";
                    responseMessage = "Operation Failed!!";
                    result = "<Result>" + responseCode + "</Result><Response>" + responseMessage + "</Response>";
                }

            } catch (SQLException ex) {
                responseCode = "0";
                responseMessage = ex.toString();
                result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";

            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    responseCode = "0";
                    responseMessage = ex.toString();
                    result = "<Result>0</Result><Response>" + ex.toString() + "</Response>";
                }
            }
        } else {
            responseCode = "0";
            responseMessage = "Wrong Username or Password";
            result = "<Result>0</Result><Response>Wrong Username or Password</Response>";
        }
        InsertToraRequestLog(saleJson, responseMessage, responseCode, isSuccess, licenceCode, 1);
        return result;
    }

    //Log tablosuna kayıt atmak için
    public boolean InsertToraRequestLog(String request, String responseMessage, String responseCode, boolean isSuccess, String licenceCode, int processType) {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            con = marwizDS.getConnection();
            ps = con.prepareStatement("INSERT INTO log.tora_requests (branch_id, type_id, processdate, request, response, responsecode, is_success)\n"
                      + "VALUES((SELECT br.id FROM general.branch br WHERE br.deleted=FALSE AND UPPER(br.licencecode) = UPPER(?)), ?, ?, ?, ?, ?, ?);");
            ps.setEscapeProcessing(true);
            ps.setString(1, licenceCode);
            ps.setInt(2, processType);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(3, timestamp);
            ps.setString(4, request);
            ps.setString(5, responseMessage);
            ps.setString(6, responseCode);
            ps.setBoolean(7, isSuccess);

            rs = ps.executeQuery();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(WsIncome.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(WsIncome.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    /*---------------------------------------------TORA YIKAMA----------------------------------------------------------------------------------------------------------------*/
}
