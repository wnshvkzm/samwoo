package com.daou.go.integration.service;

import java.util.List;
import java.util.Map;

import com.daou.go.integration.domain.model.UnitpriceModel;

public interface UnitpriceService {
	public List<UnitpriceModel> getAccountingRows(Map<String, String> params);

	public List<UnitpriceModel> getErpData(UnitpriceModel unitpriceModel);

	@Deprecated
	public UnitpriceModel getTest(Map<String, String> params);

	public void unitpriceCompleted(String[] params);

	public void unitpriceDrafted(String[] params);

	public void unitpriceReturned(String[] params);

}
