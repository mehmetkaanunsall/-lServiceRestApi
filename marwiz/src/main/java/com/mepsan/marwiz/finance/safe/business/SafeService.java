/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 10:34:33
 */
package com.mepsan.marwiz.finance.safe.business;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.finance.safe.dao.ISafeDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class SafeService implements ISafeService {

    @Autowired
    private ISafeDao safeDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSafeDao(ISafeDao safeDao) {
        this.safeDao = safeDao;
    }

    @Override
    public int create(Safe obj) {
        return safeDao.create(obj);
    }

    @Override
    public int update(Safe obj) {
        return safeDao.update(obj);
    }

    @Override
    public List<Safe> findAll() {
        return safeDao.findAll();
    }

    @Override
    public List<Safe> selectSafe() {
        return safeDao.selectSafe();
    }

    @Override
    public List<Safe> findSafeByCurrency(String where) {
        return safeDao.findSafeByCurrency(where);
    }

    @Override
    public String createWhere(int type, List<Currency> currencyList) {
        String where = "";
        String listOfCurrency = "";
        if (type == 0) {//default safe için pos sayfasındaki
            where = "AND sf.currency_id=" + sessionBean.getUser().getLastBranch().getCurrency().getId() + "";
        } else if (type == 1) {//pos sayfasında diğer currency e sahip kasaları çekmek için
            for (Currency c : currencyList) {
                listOfCurrency = listOfCurrency + "," + String.valueOf(c.getId());
            }
            if (!listOfCurrency.equals("")) {
                listOfCurrency = listOfCurrency.substring(1, listOfCurrency.length());
                where = " AND sf.currency_id NOT IN(" + listOfCurrency + ") ";
            }
        }
        return where;
    }

    @Override
    public int delete(Safe safe) {
        return safeDao.delete(safe);
    }

    @Override
    public List<Safe> findSafeBalanceForDate(Safe safe) {
        return safeDao.findSafeBalanceForDate(safe);
    }

    @Override

    public String exportSafeReport(Safe safe) {
        StringBuilder sb = new StringBuilder();

        sb.append(" <div style=\"display:block; width:100%; height:70px; overflow:hidden;\">").append(" </div> ");

        sb.append(" <div style=\"font-family:sans-serif;margin-left:30px;font-weight:bold;text-decoration-line: underline;font-size:18px;\">").append(sessionBean.loc.getString("station")).append(" : ").append(sessionBean.getUser().getLastBranch().getName()).append(" </div> ");

        sb.append(" <div style=\"display:block; width:100%; height:70px; overflow:hidden;\">").append(" </div> ");

        sb.append(" <div style=\"font-family:sans-serif;margin-left:30px;font-size:18px;\">").append("<span style=\"font-weight:bold\">").append(StaticMethods.convertToDateFormat(sessionBean.getUser().getLastBranch().getDateFormat(), safe.getReportDate())).append("</span>").append(" ")
                  .append("tarihinde İstasyon kasasında yapılan sayımda toplam").append(" ").append("<span style=\"font-weight:bold\">").append(sessionBean.getNumberFormat().format(safe.getReportBalance())).
                  append(sessionBean.currencySignOrCode(safe.getCurrency().getId(), 0)).append("</span>").append(" ").append("bulunduğu").append(" ").append("sayım sonunda bulunan miktarın kayıtlara göre tamam olduğu görülmüştür.").append(" </div> ");

        sb.append(" <div style=\"display:block; width:100%; height:70px; overflow:hidden;\">").append(" </div> ");

        sb.append(" <div style=\"font-family:sans-serif;text-align:right;margin-right:20px;font-weight:bold;text-decoration-line: underline;font-size:18px;\">").append(sessionBean.loc.getString("yourstruly")).append(",").append(" </div> ");

        return sb.toString();
    }

    @Override
    public List<Safe> selectSafe(Branch branch) {
        return safeDao.selectSafe(branch);
    }

    @Override
    public List<Safe> selectSafe(List<Branch> branchList) {
        return safeDao.selectSafe(branchList);
    }

}
