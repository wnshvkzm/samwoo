package com.daou.go.integration.component;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daou.go.appr.domain.context.DocumentContext;
import com.daou.go.appr.integration.component.ApprIntegration;
import com.daou.go.integration.service.UnitpriceService;

@Component
public class UnitpriceIntegration implements ApprIntegration {
	
	final Pattern discountNum = Pattern.compile("(?i)<span codeType=\"discountNum\">([^<]*)</span>");
	final Pattern confirmFlag = Pattern.compile("(?i)<span codeType=\"confirmFlag\">([^<]*)</span>");
	//final Pattern preqReg = Pattern.compile("(?i)<span codeType=\"preqNum\">([^<]*)</span>");
	
	@Autowired
	private UnitpriceService unitpriceService;
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "소모품연동";
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
		unitpriceService.unitpriceCompleted(splitCode(arg0.getBody()));
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
		unitpriceService.unitpriceDrafted(splitCode(arg0.getBody()));
		return null;
	}

	@Override
	public DocumentContext onDocumentReturned(DocumentContext arg0) {
		unitpriceService.unitpriceReturned(splitCode(arg0.getBody()));
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
		String[] ret = new String[2];
		Matcher m = discountNum.matcher(docBody);
		if (m.find()) {
			ret[0] = m.group(1);
		}
		
		m = confirmFlag.matcher(docBody);
		if (m.find()) {
			ret[1] = m.group(1);
		}
		/*
		m = preqReg.matcher(docBody);
		if (m.find()) {
			ret[2] = m.group(1);
		}*/
		
		return ret;
	}
	
}
