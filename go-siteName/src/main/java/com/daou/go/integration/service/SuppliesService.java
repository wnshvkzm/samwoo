package com.daou.go.integration.service;

import java.util.List;
import java.util.Map;

import com.daou.go.integration.domain.model.SuppliesModel;

public interface SuppliesService {
	public List<SuppliesModel> getAccountingRows(Map<String, String> params);

	public List<SuppliesModel> getErpData(SuppliesModel suppliesModel);

	@Deprecated
	public SuppliesModel getTest(Map<String, String> params);

	public void suppliesCompleted(String[] params);

	public void suppliesDrafted(String[] params);

	public void suppliesReturned(String[] params);

}
