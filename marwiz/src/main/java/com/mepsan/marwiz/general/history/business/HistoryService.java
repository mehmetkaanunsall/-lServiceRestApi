/**
 *
 *
 *
 * @author SALEM WALAA ABDULHADIE
 *
 * @date 01.02.2017 13:48:00
 */
package com.mepsan.marwiz.general.history.business;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.dao.IHistoryDao;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Language;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class HistoryService implements IHistoryService {

    @Autowired
    private IHistoryDao historyDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setHistoryDao(IHistoryDao historyDao) {
        this.historyDao = historyDao;
    }

    @Override
    public List<History> findAll(int first, int pageSize, Map<String, Object> filters, String where, int rowId, String tableName, int pageId) {
        List<History> list = historyDao.findAll(first, pageSize, filters, where, rowId, tableName, pageId);
        String oldValue = "";
        String newValue = "";

        for (History h : list) {
            oldValue = "";
            newValue = "";
            String oldHistory = h.getOldValue();
            String newHistory = h.getNewValue();
            if (h.getOldValue() != null) {
                String[] result = oldHistory.split(",");
                List<Type> typeList = sessionBean.getTypes(15);
                List<Type> typeList2 = sessionBean.getTypes(32);
                for (String m : result) {
                    for (Type type : typeList) {
                        if (m.equals(String.valueOf(type.getId()))) {
                            oldValue += "," + type.getNameMap().get(sessionBean.getLangId()).getName();
                            break;
                        }
                    }
                    for (Type type : typeList2) {
                        if (m.equals(String.valueOf(type.getId()))) {
                            oldValue += "," + type.getNameMap().get(sessionBean.getLangId()).getName();
                            break;
                        }
                    }
                }
            }
            if (h.getNewValue() != null) {
                String[] result = newHistory.split(",");
                List<Type> typeList = sessionBean.getTypes(15);
                List<Type> typeList2 = sessionBean.getTypes(32);
                for (String m : result) {
                    for (Type type : typeList) {
                        if (m.equals(String.valueOf(type.getId()))) {
                            newValue += "," + type.getNameMap().get(sessionBean.getLangId()).getName();
                            break;
                        }
                    }
                    for (Type type : typeList2) {
                        if (m.equals(String.valueOf(type.getId()))) {
                            newValue += "," + type.getNameMap().get(sessionBean.getLangId()).getName();
                            break;
                        }
                    }
                }
            }

            if (!oldValue.equals("")) {
                oldValue = oldValue.substring(1, oldValue.length());
            }
            if (!newValue.equals("")) {
                newValue = newValue.substring(1, newValue.length());
            }

            if (h.getProcessType().equals("I")) {
                if (h.getTableName().equals("inventory.stock_categorization_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stockcategorys"));
                } else if (h.getTableName().equals("finance.invoiceitem")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock"));
                } else if (h.getTableName().equals("finance.orderitem")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock"));
                } else if (h.getTableName().equals("inventory.pricelistitem")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock"));
                } else if (h.getTableName().equals("general.userdata_authorize_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("authorizationtab") + "-" + sessionBean.getLoc().getString("authorization"));
                } else if (h.getTableName().equals("inventory.stock_centercategorization_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("centerstockcategorys"));
                } else if (h.getTableName().equals("inventory.stocktaking_categorization_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stockcategorys"));
                }
            } else if (h.getReferencetable() != null) {
                if (h.getReferencetable().equals("system.status")) {
                    List<Status> statuses = new ArrayList<>();
                    for (int i : StaticMethods.getItemId(h.getTableName())) {
                        statuses.addAll(sessionBean.getStatus(i));
                    }

                    for (Status s : statuses) {
                        try {
                            if (s.getId() == Integer.valueOf(h.getOldValue())) {
                                h.setfOldValue(s.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfOldValue("");
                        }
                        try {
                            if (s.getId() == Integer.valueOf(h.getNewValue())) {
                                h.setfNewValue(s.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfNewValue("");
                        }
                    }
                } else if (h.getReferencetable().equals("system.type")) {
                    List<Type> types = new ArrayList<>();
                    for (int i : StaticMethods.getItemId(h.getTableName())) {
                        types.addAll(sessionBean.getTypes(i));
                    }
                    for (Type t : types) {
                        try {
                            if (t.getId() == Integer.valueOf(h.getOldValue())) {
                                h.setfOldValue(t.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfOldValue("");
                        }
                        try {
                            if (t.getId() == Integer.valueOf(h.getNewValue())) {
                                h.setfNewValue(t.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfNewValue("");
                        }

                    }
                } else if (h.getReferencetable().equals("system.currency")) {
                    for (Currency c : sessionBean.getCurrencies()) {
                        try {
                            if (c.getId() == Integer.valueOf(h.getOldValue())) {
                                h.setfOldValue(c.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfOldValue("");
                        }
                        try {
                            if (c.getId() == Integer.valueOf(h.getNewValue())) {
                                h.setfNewValue(c.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfNewValue("");
                        }

                    }
                } else if (h.getReferencetable().equals("system.language")) {
                    for (Language l : sessionBean.getLangList()) {
                        try {
                            if (l.getId() == Integer.valueOf(h.getOldValue())) {
                                h.setfOldValue(l.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfOldValue("");
                        }
                        try {
                            if (l.getId() == Integer.valueOf(h.getNewValue())) {
                                h.setfNewValue(l.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfNewValue("");
                        }
                    }
                } else if (h.getReferencetable().equals("system.country")) {
                    for (Country c : sessionBean.getCountries()) {
                        try {
                            if (c.getId() == Integer.valueOf(h.getOldValue())) {
                                h.setfOldValue(c.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfOldValue("");
                        }
                        try {
                            if (c.getId() == Integer.valueOf(h.getNewValue())) {
                                h.setfNewValue(c.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        } catch (Exception e) {
                            h.setfNewValue("");
                        }
                    }
                } else if (h.getReferencetable().equals("system.city")) {
                    boolean b = false;
                    outerloop:
                    for (Country c : sessionBean.getCountries()) {

                        for (City city : c.getListOfCities()) {
                            try {
                                if (city.getId() == Integer.valueOf(h.getOldValue())) {
                                    h.setfOldValue(city.getNameMap().get(sessionBean.getLangId()).getName());
                                }
                            } catch (Exception e) {
                                h.setfOldValue("");
                            }
                            try {
                                if (city.getId() == Integer.valueOf(h.getNewValue())) {
                                    h.setfNewValue(city.getNameMap().get(sessionBean.getLangId()).getName());
                                }
                            } catch (Exception e) {
                                h.setfNewValue("");
                            }
                            if (b) {
                                break outerloop;
                            }
                        }
                    }
                } else if (h.getReferencetable().equals("system.county")) {
                    boolean b = false;
                    outerloop:
                    for (Country c : sessionBean.getCountries()) {
                        outerloop1:
                        for (City city : c.getListOfCities()) {

                            for (County county : city.getListOfCounty()) {
                                try {
                                    if (county.getId() == Integer.valueOf(h.getOldValue())) {
                                        h.setfOldValue(county.getName());
                                    }
                                } catch (Exception e) {
                                    h.setfOldValue("");
                                }
                                try {
                                    if (county.getId() == Integer.valueOf(h.getNewValue())) {
                                        h.setfNewValue(county.getName());
                                    }
                                } catch (Exception e) {
                                    h.setfNewValue("");
                                }
                                if (b) {
                                    break outerloop1;
                                }
                                if (b) {
                                    break outerloop;
                                }
                            }
                        }
                    }
                }
            } else if (h.getColumnName().equals("gu.password")) {
                h.setNewValue(null);
                h.setOldValue(null);
            } else if (h.getColumnName().equals("deleted")) {
                h.setOldValue(h.getfOldValue());
                if (h.getTableName().equals("inventory.pricelistitem")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock"));
                } else if (h.getTableName().equals("inventory.stock_taxgroup_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("taxrate"));
                } else if (h.getTableName().equals("inventory.stock_categorization_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stockcategorys"));
                } else if (h.getTableName().equals("finance.invoiceitem")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock"));
                } else if (h.getTableName().equals("finance.orderitem")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock"));
                } else if (h.getTableName().equals("finance.discount_account_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("customer"));
                } else if (h.getTableName().equals("finance.discount_branch_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("branch"));
                } else if (h.getTableName().equals("finance.discountitem")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("campaigndetails"));
                } else if (h.getTableName().equals("general.userdata_authorize_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("authorizationtab") + "-" + sessionBean.getLoc().getString("authorization"));
                } else if (h.getTableName().equals("inventory.stock_centercategorization_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("centerstockcategorys"));
                } else if (h.getTableName().equals("inventory.stocktaking_categorization_con")) {
                    h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stockcategorys"));
                } else if (h.getfNewValue() != null) {
                    try {
                        h.setPageOfDeleteOrInsert(sessionBean.getAuth().getString("p_" + h.getfNewValue()));
                    } catch (Exception e) {
                        // h.setNewValue(sessionBean.getAuth().getString("p_"+h.getfOldValue()));
                    }
                }
                h.setNewValue(null);
            } else if (h.getColumnName().equals("fi.is_service")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("service"));
                    h.setNewValue(sessionBean.getLoc().getString("product"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("product"));
                    h.setNewValue(sessionBean.getLoc().getString("service"));
                }
            } else if (h.getColumnName().equals("ip.is_purchase")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("purchase"));
                    h.setNewValue(sessionBean.getLoc().getString("sales"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("sales"));
                    h.setNewValue(sessionBean.getLoc().getString("purchase"));
                }
            } else if (h.getColumnName().equals("ip.is_default")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("is.is_service")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("is.is_quicksale")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("is.is_updateprice")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("ga.is_person")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("individual"));
                    h.setNewValue(sessionBean.getLoc().getString("corporate"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("corporate"));
                    h.setNewValue(sessionBean.getLoc().getString("individual"));
                }
            } else if (h.getColumnName().equals("is.is_purchase")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("purchase"));
                    h.setNewValue(sessionBean.getLoc().getString("sales"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("sales"));
                    h.setNewValue(sessionBean.getLoc().getString("purchase"));
                }
            } else if (h.getColumnName().equals("ip.is_taxincluded")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("fd.is_allcustomer")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("fd.is_invoice")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("fd.is_allbranch")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("fd.is_taxincluded")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("fd.is_retailcustomer")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("ga.taxpayertype_id")) {
                if (h.getOldValue().equals("1")) {
                    h.setOldValue(sessionBean.getLoc().getString("einvoice"));
                    h.setNewValue(sessionBean.getLoc().getString("earchive"));
                }
                if (h.getOldValue().equals("2")) {
                    h.setOldValue(sessionBean.getLoc().getString("earchive"));
                    h.setNewValue(sessionBean.getLoc().getString("einvoice"));
                } else if (h.getOldValue().equals("") || h.getOldValue().equals("0")) {
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue(sessionBean.getLoc().getString("einvoice"));
                    } else {
                        h.setNewValue(sessionBean.getLoc().getString("earchive"));
                    }
                }
            } else if (h.getColumnName().equals("fi.deliverytype_id")) {
                if (h.getOldValue().equals("1")) {
                    h.setOldValue(sessionBean.getLoc().getString("paper"));
                    h.setNewValue(sessionBean.getLoc().getString("electronic"));
                }
                if (h.getOldValue().equals("2")) {
                    h.setOldValue(sessionBean.getLoc().getString("electronic"));
                    h.setNewValue(sessionBean.getLoc().getString("paper"));
                }
            } else if (h.getColumnName().equals("fi.invoicescenario_id")) {
                if (h.getOldValue().equals("1")) {
                    h.setOldValue(sessionBean.getLoc().getString("basicinvoice"));
                    h.setNewValue(sessionBean.getLoc().getString("commercialinvoice"));
                }
                if (h.getOldValue().equals("2")) {
                    h.setOldValue(sessionBean.getLoc().getString("commercialinvoice"));
                    h.setNewValue(sessionBean.getLoc().getString("basicinvoice"));
                }
            } //            else if (h.getColumnName().equals("gb.status_id ")) {
            //                if (h.getOldValue().equals("1")) {
            //                    h.setOldValue(sessionBean.getLoc().getString("active"));
            //                    h.setNewValue(sessionBean.getLoc().getString("passive"));
            //                }
            //                if (h.getOldValue().equals("2")) {
            //                    h.setOldValue(sessionBean.getLoc().getString("passive"));
            //                    h.setNewValue(sessionBean.getLoc().getString("active"));
            //                }
            //            } 
            else if (h.getColumnName().equals("gb.type_id ")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("1")) {
                    h.setOldValue(sessionBean.getLoc().getString("corporate"));
                    h.setNewValue(sessionBean.getLoc().getString("personality"));
                }
                if (h.getOldValue().equals("2")) {
                    h.setOldValue(sessionBean.getLoc().getString("personality"));
                    h.setNewValue(sessionBean.getLoc().getString("corporate"));
                }
            } else if (h.getColumnName().equals("gb.purchaseunitpriceupdateoption_id")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("0")) {
                    h.setOldValue(sessionBean.getLoc().getString("onlypurchaseprice"));
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue(sessionBean.getLoc().getString("onlypricelist"));
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue(sessionBean.getLoc().getString("all"));
                    }
                } else if (h.getOldValue().equals("1")) {
                    h.setOldValue(sessionBean.getLoc().getString("onlypricelist"));
                    if (h.getNewValue().equals("0")) {
                        h.setNewValue(sessionBean.getLoc().getString("onlypurchaseprice"));
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue(sessionBean.getLoc().getString("all"));
                    }
                } else if (h.getOldValue().equals("2")) {
                    h.setOldValue(sessionBean.getLoc().getString("all"));
                    if (h.getNewValue().equals("0")) {
                        h.setNewValue(sessionBean.getLoc().getString("onlypurchaseprice"));
                    } else if (h.getNewValue().equals("1")) {
                        h.setNewValue(sessionBean.getLoc().getString("onlypricelist"));
                    }
                }
            } else if (h.getColumnName().equals("gb.is_showpassiveaccount")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_processpassiveaccount")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_minusmainsafe")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_shiftcontrol")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_returnwithoutreceipt")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_foreigncurrency")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_taxmandatory")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_cashierentercashshift")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_invoicestocksalepricelist")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_unitpriceaffectedbydiscount")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_purchasecontrol")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_purchaseinvoiceproductsupplierupdate")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_einvoice")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.einvoiceintegrationtype_id")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("1")) {
                    h.setOldValue("INNOVA");
                    if (h.getNewValue().equals("2")) {
                        h.setNewValue("UYUMSOFT");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("2")) {
                    h.setOldValue("UYUMSOFT");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("INNOVA");
                    } else {
                        h.setNewValue("");
                    }
                } else {
                    h.setOldValue("");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("INNOVA");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("UYUMSOFT");
                    }
                }
            } else if (h.getColumnName().equals("gb.is_erpuseshift")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.erpintegration_id")) {

                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("1")) {
                    h.setOldValue("SAP");
                    if (h.getNewValue().equals("2")) {
                        h.setNewValue("Logo");
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue("Netsis");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("2")) {
                    h.setOldValue("Logo");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("SAP");
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue("Netsis");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("3")) {
                    h.setOldValue("Netsis");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("SAP");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("Logo");
                    } else {
                        h.setNewValue("");
                    }
                } else {
                    h.setOldValue("");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("SAP");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("Logo");
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue("Netsis");
                    }
                }
            } else if (h.getColumnName().equals("gb.washingtype_id")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("1")) {
                    h.setOldValue("Blue Wash");
                    if (h.getNewValue().equals("2")) {
                        h.setNewValue("Tora");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("2")) {
                    h.setOldValue("Tora");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("Blue Wash");
                    } else {
                        h.setNewValue("");
                    }
                } else {
                    h.setOldValue("");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("Blue Wash");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("Tora");
                    }
                }
            } else if (h.getColumnName().equals("gb.is_allbranch")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.autofilecreatetype_id")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("1")) {
                    h.setOldValue("GTF");
                    if (h.getNewValue().equals("2")) {
                        h.setNewValue("GDF");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("2")) {
                    h.setOldValue("GDF");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("GTF");
                    } else {
                        h.setNewValue("");
                    }
                } else {
                    h.setOldValue("");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("GTF");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("GDF");
                    }
                }
            } else if (h.getColumnName().equals("gb.is_managerdiscount")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_managerreturn")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_managerautomatproduct")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_documentcreditnow")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("instant"));
                    h.setNewValue(sessionBean.getLoc().getString("after"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("after"));
                    h.setNewValue(sessionBean.getLoc().getString("instant"));
                }
            } else if (h.getColumnName().equals("gb.is_continuecrerror")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_productremoval")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_cashierusethesyncbutton")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_cashierenterbarcode")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_cashierenterquantity")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_licencecheck")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_central")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_agency")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_takeaway")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_vehicledelivery")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.authpaymenttype")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                h.setOldValue(oldValue);
                h.setNewValue(newValue);

            } else if (h.getColumnName().equals("gb.printpaymenttype")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                h.setOldValue(oldValue);
                h.setNewValue(newValue);

            } else if (h.getColumnName().equals("gb.printsaletype")) {

                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                h.setOldValue(oldValue);
                h.setNewValue(newValue);

            } else if (h.getColumnName().equals("gb.automation_id")) {

                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("1")) {
                    h.setOldValue("Stawiz+");
                    if (h.getNewValue().equals("2")) {
                        h.setNewValue("Turpak");
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue("Asis");
                    } else if (h.getNewValue().equals("4")) {
                        h.setNewValue("Stawiz");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("2")) {
                    h.setOldValue("Turpak");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("Stawiz+");
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue("Asis");
                    } else if (h.getNewValue().equals("4")) {
                        h.setNewValue("Stawiz");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("3")) {
                    h.setOldValue("Asis");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("Stawiz+");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("Turpak");
                    } else if (h.getNewValue().equals("4")) {
                        h.setNewValue("Stawiz");
                    } else {
                        h.setNewValue("");
                    }
                } else if (h.getOldValue().equals("4")) {
                    h.setOldValue("Stawiz");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("Stawiz+");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("Turpak");
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue("Asis");
                    } else {
                        h.setNewValue("");
                    }
                } else {
                    h.setOldValue("");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue("Stawiz+");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue("Turpak");
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue("Asis");
                    } else if (h.getNewValue().equals("4")) {
                        h.setNewValue("Stawiz");
                    }
                }
            } else if (h.getColumnName().equals("gb.uscprotocol")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("1")) {
                    h.setOldValue("USC+");
                    h.setNewValue("USC++");
                }
                if (h.getOldValue().equals("2")) {
                    h.setOldValue("USC++");
                    h.setNewValue("USC+");
                }
            } else if (h.getColumnName().equals("gb.decimalsymbol")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }
                if (h.getOldValue().equals("0")) {
                    h.setOldValue("9.999,99");
                    h.setNewValue("9,999.99");
                }
                if (h.getOldValue().equals("1")) {
                    h.setOldValue("9,999.99");
                    h.setNewValue("9.999,99");
                }
            } else if (h.getColumnName().equals("gb.is_cashierpumpscreen")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }

                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gu.is_authorized")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gu.is_cashieraddsalesbasket")) {
                if (h.getOldValue() == null) {
                    h.setOldValue("");
                } else if (h.getNewValue() == null) {
                    h.setNewValue("");
                }
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("is.is_campaign")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("is.is_get")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.is_passiveget")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
            } else if (h.getColumnName().equals("gb.concepttype_id")) {
                if (h.getOldValue().equals("0")) {
                    h.setOldValue("");
                    if (h.getNewValue().equals("1")) {
                        h.setNewValue(sessionBean.getLoc().getString("ultramarket"));
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue(sessionBean.getLoc().getString("fullmarketextra"));
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue(sessionBean.getLoc().getString("fullmarket"));
                    }
                }
                if (h.getOldValue().equals("1")) {
                    h.setOldValue(sessionBean.getLoc().getString("ultramarket"));
                    if (h.getNewValue().equals("0")) {
                        h.setNewValue("");
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue(sessionBean.getLoc().getString("fullmarketextra"));
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue(sessionBean.getLoc().getString("fullmarket"));
                    }
                }
                if (h.getOldValue().equals("2")) {
                    h.setOldValue(sessionBean.getLoc().getString("fullmarketextra"));
                    if (h.getNewValue().equals("0")) {
                        h.setNewValue("");
                    } else if (h.getNewValue().equals("1")) {
                        h.setNewValue(sessionBean.getLoc().getString("ultramarket"));
                    } else if (h.getNewValue().equals("3")) {
                        h.setNewValue(sessionBean.getLoc().getString("fullmarket"));
                    }
                }
                if (h.getOldValue().equals("3")) {
                    h.setOldValue(sessionBean.getLoc().getString("fullmarket"));
                    if (h.getNewValue().equals("0")) {
                        h.setNewValue("");
                    } else if (h.getNewValue().equals("1")) {
                        h.setNewValue(sessionBean.getLoc().getString("ultramarket"));
                    } else if (h.getNewValue().equals("2")) {
                        h.setNewValue(sessionBean.getLoc().getString("fullmarketextra"));
                    }
                }
            } else if (h.getColumnName().equals("gu.mpospages")) {
                String[] resultOld = oldHistory.split(",");
                for (String o : resultOld) {
                    if (o.equals("1")) {
                        oldValue += "," + sessionBean.getLoc().getString("cashierreport");
                    } else if (o.equals("2")) {
                        oldValue += "," + sessionBean.getLoc().getString("shiftreport");
                    } else if (o.equals("3")) {
                        oldValue += "," + sessionBean.getLoc().getString("salereport");
                    } else if (o.equals("4")) {
                        oldValue += "," + sessionBean.getLoc().getString("xreport");
                    } else if (o.equals("5")) {
                        oldValue += "," + sessionBean.getLoc().getString("zreport");
                    } else if (o.equals("6")) {
                        oldValue += "," + sessionBean.getLoc().getString("endofdayreport");
                    }
                }

                if (!oldValue.equals("")) {
                    oldValue = oldValue.substring(1, oldValue.length());
                }
                h.setOldValue(oldValue);

                String[] resultNew = newHistory.split(",");
                for (String n : resultNew) {
                    if (n.equals("1")) {
                        newValue += "," + sessionBean.getLoc().getString("cashierreport");
                    } else if (n.equals("2")) {
                        newValue += "," + sessionBean.getLoc().getString("shiftreport");
                    } else if (n.equals("3")) {
                        newValue += "," + sessionBean.getLoc().getString("salereport");
                    } else if (n.equals("4")) {
                        newValue += "," + sessionBean.getLoc().getString("xreport");
                    } else if (n.equals("5")) {
                        newValue += "," + sessionBean.getLoc().getString("zreport");
                    } else if (n.equals("6")) {
                        newValue += "," + sessionBean.getLoc().getString("endofdayreport");
                    }
                }

                if (!newValue.equals("")) {
                    newValue = newValue.substring(1, newValue.length());
                }
                h.setNewValue(newValue);
            } else if (h.getColumnName().equals("gb.is_washingsalezreport")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
                if (h.getOldValue().equals("")) {
                    if (h.getNewValue().equals("f")) {
                        h.setOldValue(sessionBean.getLoc().getString("no"));
                    } else {
                        h.setNewValue(sessionBean.getLoc().getString("yes"));
                    }
                }
            } else if (h.getColumnName().equals("is.is_control")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
                if (h.getOldValue().equals("")) {
                    if (h.getNewValue().equals("f")) {
                        h.setOldValue(sessionBean.getLoc().getString("no"));
                    } else {
                        h.setNewValue(sessionBean.getLoc().getString("yes"));
                    }
                }
            } else if (h.getColumnName().equals("is.is_taxincluded")) {
                if (h.getOldValue().equals("t")) {
                    h.setOldValue(sessionBean.getLoc().getString("yes"));
                    h.setNewValue(sessionBean.getLoc().getString("no"));
                }
                if (h.getOldValue().equals("f")) {
                    h.setOldValue(sessionBean.getLoc().getString("no"));
                    h.setNewValue(sessionBean.getLoc().getString("yes"));
                }
                if (h.getOldValue().equals("")) {
                    if (h.getNewValue().equals("f")) {
                        h.setOldValue(sessionBean.getLoc().getString("no"));
                    } else {
                        h.setNewValue(sessionBean.getLoc().getString("yes"));
                    }
                }
            }
            if (h.getColumnType() != null) {
                if (h.getColumnType().equals("numeric")) {
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
                    int currencyRounding = sessionBean.getUser().getLastBranch().getCurrencyrounding();//yuvarlama değeri
                    formatter.setMaximumFractionDigits(currencyRounding);
                    formatter.setMinimumFractionDigits(currencyRounding);
                    formatter.setRoundingMode(RoundingMode.HALF_EVEN);

                    DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
                    decimalFormatSymbols.setCurrencySymbol("");
                    ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
                    // if (h.getOldValue().isEmpty() || h.getNewValue().isEmpty()) {
                    if (h.getOldValue().isEmpty()) {
                        h.setOldValue("");
                    } else {
                        h.setOldValue((String) formatter.format(new BigDecimal(h.getOldValue())));
                    }

                    if (h.getNewValue().isEmpty()) {
                        h.setNewValue("");
                    } else {
                        h.setNewValue((String) formatter.format(new BigDecimal(h.getNewValue())));
                    }
                    // }
                    if (h.getfOldValue() != null) {
                        h.setfOldValue((String) formatter.format(new BigDecimal(h.getfOldValue())));
                    }
                    if (h.getfNewValue() != null) {
                        h.setfNewValue((String) formatter.format(new BigDecimal(h.getfNewValue())));
                    }
                } else if (h.getColumnType().equals("timestamp without time zone")) {
                    if (h.getOldValue() != null) {
                        if (h.getOldValue().contains("T")) {
                            h.setOldValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), StaticMethods.convertStringToDate("yyyy-MM-dd'T'HH:mm:ss", h.getOldValue())));
                        } else if (!h.getOldValue().contains("T")) {
                            h.setOldValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), StaticMethods.convertStringToDate("yyyy-MM-dd HH:mm:ss", h.getOldValue())));
                        }
                    }
                    if (h.getNewValue() != null) {
                        if (h.getNewValue().contains("T")) {
                            h.setNewValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), StaticMethods.convertStringToDate("yyyy-MM-dd'T'HH:mm:ss", h.getNewValue())));
                        } else if (!h.getNewValue().contains("T")) {
                            h.setNewValue(StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), StaticMethods.convertStringToDate("yyyy-MM-dd HH:mm:ss", h.getNewValue())));
                        }
                    }
                }

                if (h.getItemValue() != null) {

                    if (h.getTableName().equals("finance.invoiceitem")) {
                        h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock") + " - " + h.getItemValue());
                    }

                    if (h.getTableName().equals("finance.orderitem")) {
                        h.setPageOfDeleteOrInsert(sessionBean.getLoc().getString("stock") + " - " + h.getItemValue());
                    }

                }
            }
        }
        return list;
    }

    @Override
    public int count(String where, int rowId, String tableName,
            int pageId
    ) {
        return historyDao.count(where, rowId, tableName, pageId);
    }

    @Override
    public String bringWhereForLimit(String type, String table,
            int id
    ) {
        if ("credit".equals(type)) {
            switch (table) {
                case "fleet":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select creditlimit_id from automation.vehiclefleet WHERE id=" + id + "))";
                case "group":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select creditlimit_id from automation.vehiclegroup WHERE id=" + id + "))";
                case "vehicle":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select creditlimit_id from automation.vehicle WHERE id=" + id + "))";
                case "vehicleidunit":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select limit_id from automation.vehicleidunit WHERE id=" + id + "))";
                default:
                    break;
            }
        } else if ("prepayment".equals(type)) {
            switch (table) {
                case "fleet":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select prepaymentlimit_id from automation.vehiclefleet WHERE id=" + id + "))";
                case "group":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select prepaymentlimit_id from automation.vehiclegroup WHERE id=" + id + "))";
                case "vehicle":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select prepaymentlimit_id from automation.vehicle WHERE id=" + id + "))";
                case "vehicleidunit":
                    return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select limit_id from automation.vehicleidunit WHERE id=" + id + "))";
                default:
                    break;
            }
        } else if ("loyalty".equals(type)) {
            return " or (his.tablename =  ('automation.limit' ) AND  his.row_id =(select limit_id from automation.vehicleidunit WHERE id=" + id + "))";
        }
        return null;
    }

}
