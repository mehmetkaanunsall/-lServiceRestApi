/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.mepsan.marwiz.finance.order.dao.OrderReport;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IOrderService extends ICrudService<Order>, ILazyGridService<Order> {

    public int exportExcel(Order order);

    public String createWhere(OrderReport order, List<BranchSetting> listOfBranch);

    public int updateStatus(Order order);

    public int sendOrderCenter(Order order);

    public int delete(Order order);

    public CheckDelete testBeforeDelete(Order order);

    public Order findOrder(int orderId);

}
