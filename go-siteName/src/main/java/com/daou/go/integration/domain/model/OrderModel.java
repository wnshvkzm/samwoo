package com.daou.go.integration.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderModel {
	
	private String frDate = "";	//발주일(from)
	private String toDate = "";	//발주일(to)
	private String orderType = "";//발주형태
	private String customCodeP = "";//발주처코드
	//private String customName = "";//발주처명
	private String itemCode = "";//품목코드
	private String itemName;//품목명
	private String frdrDate = "";//납기일(from)
	private String todrDate = "";//납기일(to)
	private String whCodeP = "";//창고코드
	private String controlStatus = "";//진행상태
	private String userId = "";
	private String orderNumP = "";//발주번호
	
	private String no; // No.
	private String spec;	//규격
	private String orderUnitQ;	//수량
	private String orderUnit;	//단위
	private String orderUnitP;	//단가
	private String orderO;		//금액
	private String dvryDate;	//납기일
	private String orderDate;	//발주일자
	private String orderNum;	//발주번호
	private String divfullName;	//상호(발주)
	private String korAddr;		//주소(발주)
	private String telePhone;	//전화(발주)
	private String faxNum;		//팩스(발주)
	private String divName;		//상호(발주)
	private String companyNum;	//등록번호(발주)
	private String customFullName;//상호(공급)
	private String korAddr1;	//주소(공급)
	private String companyNum1;	//등록번호(공급)
	private String customCode;	//거래처코드
	private String custTel;		//전화번호(공급)
	private String custFax;		//팩스(공급)
	private String whCode;		//창고명
	private String remark;		//비고
	private long sumOrderO;
}
