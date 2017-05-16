package com.daou.go.integration.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuppliesModel {
	
	private String compCode;	//법인코드
	private String divCode;		//사업장코드
	private String preqNum;		//요청번호
	private String subject;		//제목
	private String preqSeq;		//순번
	private String customName;	//구매처
	private String pitemName;	//품목명
	private String pSpec;		//규격
	private String subCode;		//단위
	private String preqQ;		//수량
	private String pitemP;		//단가
	private String price;			//금액
	private long sumPrice;
	private String preqInDate;	//입고요청일
	private String memo;		//비고
	private long priceSum;		//총금액
}
