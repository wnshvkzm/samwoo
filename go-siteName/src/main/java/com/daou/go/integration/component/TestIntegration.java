package com.daou.go.integration.component;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daou.go.appr.domain.context.DocumentContext;
import com.daou.go.appr.integration.component.ApprIntegration;
import com.daou.go.integration.service.SuppliesService;

@Component
public class TestIntegration implements ApprIntegration {
	
	final Pattern compReg = Pattern.compile("(?i)<span codeType=\"compCode\">([^<]*)</span>");
	final Pattern divReg = Pattern.compile("(?i)<span codeType=\"divCode\">([^<]*)</span>");
	final Pattern preqReg = Pattern.compile("(?i)<span codeType=\"preqNum\">([^<]*)</span>");
	
	@Autowired
	private SuppliesService suppliesService;
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ERP연동";
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
		suppliesService.suppliesCompleted(splitCode(arg0.getBody()));
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
		suppliesService.suppliesDrafted(splitCode(arg0.getBody()));
		return null;
	}

	@Override
	public DocumentContext onDocumentReturned(DocumentContext arg0) {
		suppliesService.suppliesReturned(splitCode(arg0.getBody()));
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
		String[] ret = new String[3];
		Matcher m = compReg.matcher(docBody);
		if (m.find()) {
			ret[0] = m.group(1);
		}
		
		m = divReg.matcher(docBody);
		if (m.find()) {
			ret[1] = m.group(1);
		}
		
		m = preqReg.matcher(docBody);
		if (m.find()) {
			ret[2] = m.group(1);
		}
		
		return ret;
	}
	
}
