package com.daou.go.integration.controller.front;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.daou.go.appr.domain.Document;
import com.daou.go.appr.domain.Form;
import com.daou.go.appr.service.ApprFormService;
import com.daou.go.appr.service.DocumentService;
import com.daou.go.core.controller.front.FrontController;
import com.daou.go.core.domain.Department;
import com.daou.go.core.service.DeptService;
import com.daou.go.core.service.UserService;
import com.daou.go.integration.domain.model.UnitpriceModel;
import com.daou.go.integration.service.UnitpriceService;
import com.ibm.icu.text.DecimalFormat;

@Controller
public class UnitpriceFrontController extends FrontController{
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private ApprFormService apprFormService;
	
	@Autowired
	private DeptService deptService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UnitpriceService unitpriceService;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@RequestMapping(value="/app/approval/unitprice")
	public String test(@ModelAttribute UnitpriceModel unitpriceModel){
		Long companyId = getSessionCompanyId();
 		Long userId = getSessionUserId();
		
 		
		Department department = deptService.getDeptByUser(userService.get(userId)).get(0);
		
		Form form = apprFormService.getByCode(companyId, "samwooU");
		
		Map<String, String> params = new HashMap<String, String>();
		
		
		
		
		
	/*	testModel.setCompCode(compCode);
		testModel.setDivCode("");
		testModel.setPreqNum(""); */
		//suppliesModel result = testService.getErpData(params); // 1개의 레코드 조회
		
		List<UnitpriceModel> resultlist = unitpriceService.getErpData(unitpriceModel);  //여러개의 레코드 조회
		
		params.put("discountNum", unitpriceModel.getDiscountNum());
		params.put("confirmFlag", unitpriceModel.getConfirmFlag());
		
		//List<suppliesModel> list = testService.getAccountingRows(params);
		
		//params.setVariable("list", list.toString());
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		String customNam = "";
		String topName = "";
		String startDate = "";
		
		if (resultlist.size() >= 1) {
			customNam = resultlist.get(0).getCustomName();
			topName = resultlist.get(0).getTopName();
			startDate = resultlist.get(0).getStartDate();
		}
		
		map.put("item", resultlist);
		map.put("list", resultlist);
		map.put("startDate", startDate);
		map.put("customName", customNam);
		map.put("topName", topName);
		map.put("erpInfo", unitpriceModel);
		
		String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "Unitprice.vm", "UTF-8", map);
		params.put("itemBody", body);
		
		Document document = documentService.createDraft(form.getId(), userService.get(userId), department, params);
		
		String url = "/app/approval/document/"+document.getId()+"/integration";
		
		
		return "redirect:"+url;
	}
	
	

}
