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
import com.daou.go.integration.domain.model.SuppliesModel;
import com.daou.go.integration.service.SuppliesService;
import com.ibm.icu.text.DecimalFormat;

@Controller
public class SuppliesFrontController extends FrontController{
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private ApprFormService apprFormService;
	
	@Autowired
	private DeptService deptService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SuppliesService suppliesService;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@RequestMapping(value="/app/approval/supplies")
	public String test(@ModelAttribute SuppliesModel suppliesModel){
		Long companyId = getSessionCompanyId();
 		Long userId = getSessionUserId();
		
 		
		Department department = deptService.getDeptByUser(userService.get(userId)).get(0);
		
		Form form = apprFormService.getByCode(companyId, "samwooS");
		
		Map<String, String> params = new HashMap<String, String>();
		
		
		params.put("compCode", suppliesModel.getCompCode());
		
		
		
	/*	testModel.setCompCode(compCode);
		testModel.setDivCode("");
		testModel.setPreqNum(""); */
		//suppliesModel result = testService.getErpData(params); // 1개의 레코드 조회
		
		List<SuppliesModel> resultlist = suppliesService.getErpData(suppliesModel);  //여러개의 레코드 조회
		
		//List<suppliesModel> list = testService.getAccountingRows(params);
		
		//params.setVariable("list", list.toString());
		Map<String, Object> map = new HashMap<String, Object>();
		
		long Sum = 0;

		String subject = "";
		
		for (SuppliesModel suppliesSum : resultlist) {
			Sum += suppliesSum.getSumPrice();
		}
		
		if (resultlist.size() > 0) {
			subject = resultlist.get(0).getMemo();
		}
		
		String priceSum = String.valueOf(Sum);
		
		DecimalFormat df = new DecimalFormat("###,###");
		String currencySum = df.format(Long.parseLong(priceSum));
		
		map.put("subject", subject);
		map.put("priceSum", currencySum);
		map.put("item", resultlist);
		map.put("list", resultlist);
		map.put("erpInfo", suppliesModel);
		
	
		
		String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "Supplies.vm", "UTF-8", map);
		params.put("itemBody", body);
		
		Document document = documentService.createDraft(form.getId(), userService.get(userId), department, params);
		
		String url = "/app/approval/document/"+document.getId()+"/integration";
		
		
		return "redirect:"+url;
	}
	
	

}
