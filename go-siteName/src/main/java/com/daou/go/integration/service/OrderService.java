package com.daou.go.integration.service;

import java.util.List;
import java.util.Map;

import com.daou.go.integration.domain.model.OrderModel;

public interface OrderService {

	public List<OrderModel> getErpData(OrderModel orderModel);

	public void orderCompleted(String[] params);

	public void orderDrafted(String[] params);

	public void orderReturned(String[] params);

}
