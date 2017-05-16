package com.daou.go.integration.component;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daou.go.appr.domain.context.DocumentContext;
import com.daou.go.appr.integration.component.ApprIntegration;
import com.daou.go.integration.service.OrderService;
import com.daou.go.integration.service.UnitpriceService;

@Component
public class OrderIntegration implements ApprIntegration {
	
	final Pattern orderNumP = Pattern.compile("(?i)<span codeType=\"orderNumP\">([^<]*)</span>");
	
	@Autowired
	private OrderService orderService;
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "발주서연동";
	}

	@Override
	public DocumentContext onActCopyCreated(DocumentContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentContext onActivityCompleted(DocumentContext arg0) {
		// TODO Auto-generated method stub 최종승인
		
		return null;
	}

	@Override
	public DocumentContext onActivityGroupCompleted(DocumentContext arg0) {
		// TODO Auto-generated method stub 마지막 결재자 승인, 발려 등 
		
		return null;
	}

	@Override
	public DocumentContext onDocumentCancelDrafted(DocumentContext arg0) {
		
		return null;
	}

	@Override
	public DocumentContext onDocumentCompleted(DocumentContext arg0) {
		orderService.orderCompleted(splitCode(arg0.getBody()));
		return null;
	}

	@Override
	public DocumentContext onDocumentCreated(DocumentContext arg0) {
		
		return null;
	}

	@Override
	public DocumentContext onDocumentDeleted(DocumentContext arg0) {
		return null;
	}

	@Override
	public DocumentContext onDocumentDrafted(DocumentContext arg0) {
		orderService.orderDrafted(splitCode(arg0.getBody()));
		return null;
	}

	@Override
	public DocumentContext onDocumentReturned(DocumentContext arg0) {
		orderService.orderReturned(splitCode(arg0.getBody()));
		return null;
	}

	@Override
	public DocumentContext onDocumentTempSaved(DocumentContext arg0) {
		return null;
	}

	@Override
	public DocumentContext onDocumentUpdated(DocumentContext arg0) {
		
		return null;
	}
	
	
	private String[] splitCode(String docBody) {
		String[] ret = new String[1];
		Matcher m = orderNumP.matcher(docBody);
		if (m.find()) {
			ret[0] = m.group(1);
		}
				
		return ret;
	}
	
}
