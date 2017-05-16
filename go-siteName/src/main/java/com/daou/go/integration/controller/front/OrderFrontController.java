package com.daou.go.integration.controller.front;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import com.daou.go.integration.domain.model.OrderModel;
import com.daou.go.integration.domain.model.SuppliesModel;
import com.daou.go.integration.service.OrderService;
import com.ibm.icu.text.DecimalFormat;

@Controller
public class OrderFrontController extends FrontController{
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private ApprFormService apprFormService;
	
	@Autowired
	private DeptService deptService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@RequestMapping(value="/app/approval/order")
	public String test(@ModelAttribute OrderModel orderModel, HttpServletRequest request){
		Long companyId = getSessionCompanyId();
 		Long userId = getSessionUserId();
		
 		
		Department department = deptService.getDeptByUser(userService.get(userId)).get(0);
		
		Form form = apprFormService.getByCode(companyId, "samwooO");
		
		Map<String, String> params = new HashMap<String, String>();
		
		
		
		List<OrderModel> resultlist = orderService.getErpData(orderModel);  //여러개의 레코드 조회
		
		params.put("frDate", orderModel.getFrDate());
		params.put("toDate", orderModel.getToDate());
		params.put("orderType", orderModel.getOrderType());
		params.put("customCodeP", orderModel.getCustomCodeP());
		params.put("itemCode", orderModel.getItemCode());
		params.put("frdrDate", orderModel.getFrdrDate());
		params.put("todrDate", orderModel.getTodrDate());
		params.put("whCodeP", orderModel.getWhCodeP());
		params.put("controlStatus", orderModel.getControlStatus());
		params.put("userId", orderModel.getUserId());
		params.put("orderNumP", orderModel.getOrderNumP());
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		String orderNum = "";
		String orderDate = "";
		String companyNum = "";
		String divName = "";
		String korAddr = "";
		String telePhone = "";
		String faxNum = "";
		
		String companyNum1 = "";
		String customCode = "";
		String customFullName = "";
		String korAddr1 = "";
		String custTel = "";
		String custFax = "";
		
		if (resultlist.size() >= 1) {
			orderNum = resultlist.get(0).getOrderNum();
			orderDate = resultlist.get(0).getOrderDate();
			companyNum = resultlist.get(0).getCompanyNum();
			divName = resultlist.get(0).getDivName();
			korAddr = resultlist.get(0).getKorAddr();
			telePhone = resultlist.get(0).getTelePhone();
			faxNum = resultlist.get(0).getFaxNum();
						
			companyNum1 = resultlist.get(0).getCompanyNum1();
			customCode = resultlist.get(0).getCustomCode();
			customFullName = resultlist.get(0).getCustomFullName();
			korAddr1 = resultlist.get(0).getKorAddr1();
			custTel = resultlist.get(0).getCustTel();
			custFax = resultlist.get(0).getCustFax();
		}
		
		long sum = 0;
		long tax = 0;
		long total = 0;
		
		for (OrderModel orderSum : resultlist) {
			sum += orderSum.getSumOrderO();
			
		}
		tax = sum/10;
		total = sum + tax;
		
		String sumT = String.valueOf(sum);
		String taxT = String.valueOf(tax);
		String totalT = String.valueOf(total);
		
		DecimalFormat df = new DecimalFormat("###,###");
		String valueOfSupply = df.format(Long.parseLong(sumT));
		String taxAmount = df.format(Long.parseLong(taxT));
		String totalAmount = df.format(Long.parseLong(totalT));
		
		
		map.put("item", resultlist);
		map.put("list", resultlist);
		map.put("orderNum", orderNum);
		map.put("orderDate", orderDate);
		map.put("companyNum", companyNum);
		map.put("divName", divName);
		map.put("korAddr", korAddr);
		map.put("telePhone", telePhone);
		map.put("faxNum", faxNum);
		map.put("companyNum1", companyNum1);
		map.put("customCode", customCode);
		map.put("customFullName", customFullName);
		map.put("korAddr1", korAddr1);
		map.put("custTel", custTel);
		map.put("custFax", custFax);
		map.put("valueOfSupply", valueOfSupply);
		map.put("taxAmount", taxAmount);
		map.put("totalAmount", totalAmount);
		
		
		map.put("erpInfo", orderModel);
		
		String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "Order.vm", "UTF-8", map);
		params.put("itemBody", body);
		
		Document document = documentService.createDraft(form.getId(), userService.get(userId), department, params);
		
		String url = "/app/approval/document/"+document.getId()+"/integration";
		
		
		return "redirect:"+url;
	}
	
	

}
