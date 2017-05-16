package com.daou.go.integration.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.daou.go.integration.domain.model.OrderModel;


@Service
public class OrderServiceImpl implements OrderService {
	// JDBC TEMPLATE
	// 동일한 타입의 빈 객체들 중 특정 ERPJdbcTemplate 사용
	@Autowired
	@Qualifier("ERPJdbcTemplate")
	// @Qualifier은 사용할 의존 객체를 선택할 수 있도록 해준다.
	NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<OrderModel> getErpData(OrderModel orderModel) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		List<OrderModel> lists;

		final String query = "BEGIN "
				+ " "
				+ "    DECLARE @DIV_CODE        NVARCHAR(08) "//--(필)사업장코드    1 
				+ "          , @COMP_CODE       NVARCHAR(08) "//--(필)법인코드      15 

				+ "          , @FR_DATE         NVARCHAR(08) "//--(선)발주일(FROM)  2
				+ "          , @TO_DATE         NVARCHAR(08) "//--(선)발주일(TO)    3
				+ "          , @ORDER_TYPE      NVARCHAR(08) "//--(선)발주형태      4
				+ "          , @CUSTOM_CODE     NVARCHAR(08) "//--(선)발주처코드    5
				+ "          , @CUSTOM_NAME     NVARCHAR(20) "//   --(선)발주처명      6
				+ " "
				+ "          , @ITEM_CODE       NVARCHAR(20) "// --(선)품목코드      7
				+ "          , @ITEM_NAME       NVARCHAR(40) "// --(선)품목명        8
				+ "          , @FR_DR_DATE      NVARCHAR(08) "//--(선)납기일(FROM)  9
				+ "          , @TO_DR_DATE      NVARCHAR(08) "//--(선)납기일(TO)    10 
				+ " "
				+ "          , @WH_CODE         NVARCHAR(08) "//--(선)창고코드      11
				+ "          , @CONTROL_STATUS  NVARCHAR(01) "//--(선)진행상태      12
				+ "          , @USER_ID         NVARCHAR(10) "//--(필)USER-ID       13
				+ "          , @ORDER_NUM       NVARCHAR(20) "//--(필)발주번호      14
				+ " "
				+ "        SET @DIV_CODE           = N'01' "
				+ "        SET @COMP_CODE          = N'MASTER' "
				+ "        SET @FR_DATE            =:frDate "
				+ "        SET @TO_DATE            =:toDate " 
				+ "        SET @ORDER_TYPE         =:orderType "
				+ "        SET @CUSTOM_CODE        =:customCodeP "
				+ "        SET @ITEM_CODE          =:itemCode "
				+ " "
				+ "        SET @FR_DR_DATE         =:frdrDate "
				+ "        SET @TO_DR_DATE         =:todrDate "
				+ " "
				+ "        SET @WH_CODE            =:whCodeP "
				+ "        SET @CONTROL_STATUS     =:controlStatus "
				+ "        SET @USER_ID            =:userId "
				+ "        SET @ORDER_NUM          =:orderNumP "
				+ " "
				+ "    SELECT ROW_NUMBER() OVER(PARTITION BY E.ORDER_NUM ORDER BY A.ITEM_CODE) as no "
				+ "		  , A.ORDER_SEQ "
				+ "          , ISNULL(X.CUSTOM_ITEM_CODE, A.ITEM_CODE) as itemCode "//--품목코드 
				+ "          , ISNULL(X.CUSTOM_ITEM_NAME, B.ITEM_NAME) as itemName	"//--품목명 
				+ "          , ISNULL(X.CUSTOM_ITEM_SPEC, B.SPEC) as spec "//--규격
				+ "          , REPLACE(CONVERT(NVARCHAR(30), CAST(A.ORDER_UNIT_Q AS MONEY), 1), '.00', '') as orderUnitQ								"//--수량
				+ "          , A.ORDER_UNIT as orderUnit							"//--단위
				+ "          , REPLACE(CONVERT(NVARCHAR(30), CAST(A.ORDER_UNIT_P AS MONEY), 1), '.00', '') as orderUnitP								"//--단가
				+ "          , REPLACE(CONVERT(NVARCHAR(30), CAST(A.ORDER_O AS MONEY), 1), '.00', '') as orderO										"//--금액
				+ "          , A.ORDER_O as sumOrderO     " // 공급가액
				+ "          , CASE A.DVRY_DATE "
				+ "                WHEN '' THEN '' "
				+ "                ELSE SUBSTRING(A.DVRY_DATE, 3, 2) + '/' + SUBSTRING(A.DVRY_DATE, 5, 2) + '/' + SUBSTRING(A.DVRY_DATE,7,2) "
				+ "             END as dvryDate				"//--납기일
				+ "          , CASE E.ORDER_DATE "
				+ "                WHEN '' THEN '' "
				+ "                ELSE SUBSTRING(E.ORDER_DATE, 1, 4) + '년' + SUBSTRING(E.ORDER_DATE, 5, 2) + '월' + SUBSTRING(E.ORDER_DATE,7,2) + '일' "
				+ "             END as orderDate				"//--발주일자
				+ "          , E.ORDER_NUM as orderNum								"//--발주번호
				+ "          , D.DIV_FULL_NAME as divfullName				"//--상호(발주) 
				+ "          , D.ADDR as korAddr							"//--주소(발주)
				+ "          , D.TELEPHON as telePhone										"//--전화(발주)
				+ "          , D.FAX_NUM as faxNum										"//--팩스(발주)
				+ "          , D.DIV_NAME as divName					"//--상호(발주)
				+ "          , SUBSTRING(D.COMPANY_NUM, 1,3) + '-' + SUBSTRING(D.COMPANY_NUM, 4,2) "
				+ "		 + '-' + SUBSTRING(D.COMPANY_NUM, 6,5) as companyNum "//--등록번호(발주)	
				+ "          , C.CUSTOM_FULL_NAME as customFullName												 "//--상호(공급)
				+ "          , C.ADDR1 as korAddr1												 "//--주소(공급)
				+ "          , SUBSTRING(C.COMPANY_NUM , 1,3) + '-' + SUBSTRING(C.COMPANY_NUM , 4,2) "
				+ "		 + '-' + SUBSTRING(C.COMPANY_NUM , 6,5) as companyNum1 "//--등록번호(공급)
				+ "          , C.CUSTOM_CODE as customCode													 "//--거래처코드
				+ "          , C.TELEPHON as custTel											 "//--전화번호(공급)
				+ "          , C.FAX_NUM as custFax											 "//--팩스(공급)
				+ "          , G.TREE_NAME as whCode									 "//--창고명
				+ "          , E.REMARK as remark														 "//--비고
				+ "      FROM       MPO200T AS A WITH(NOLOCK) "
				+ "      INNER JOIN BPR100T AS B WITH(NOLOCK) ON B.COMP_CODE   = A.COMP_CODE "
				+ "                                          AND B.ITEM_CODE   = A.ITEM_CODE "
				+ "      INNER JOIN BPR200T AS F WITH(NOLOCK) ON F.COMP_CODE   = A.COMP_CODE "
				+ "                                          AND F.DIV_CODE    = A.DIV_CODE "
				+ "                                          AND F.ITEM_CODE   = A.ITEM_CODE "
				+ "      INNER JOIN MPO100T AS E WITH(NOLOCK) ON E.COMP_CODE   = A.COMP_CODE "
				+ "                                          AND E.DIV_CODE    = A.DIV_CODE "
				+ "                                          AND E.ORDER_NUM   = A.ORDER_NUM "
				+ "       LEFT JOIN BCM100T AS C WITH(NOLOCK) ON C.COMP_CODE   = A.COMP_CODE "
				+ "                                          AND C.CUSTOM_CODE = E.CUSTOM_CODE "
				+ "      INNER JOIN BOR120T AS D WITH(NOLOCK) ON D.COMP_CODE   = A.COMP_CODE "
				+ "                                          AND D.DIV_CODE    = A.DIV_CODE "
				+ "      INNER JOIN BSA220T AS G WITH(NOLOCK) ON G.COMP_CODE   = A.COMP_CODE "
				+ "                                          AND G.TYPE_LEVEL  = A.DIV_CODE "
				+ "                                          AND G.TREE_CODE   = A.WH_CODE "
				+ " "
				+ "       LEFT JOIN BPR300T AS X WITH(NOLOCK) ON X.COMP_CODE   = C.COMP_CODE "
				+ "                                          AND X.CUSTOM_CODE = C.CUSTOM_CODE "
				+ "                                          AND X.ITEM_CODE   = A.ITEM_CODE "
				+ "                                          AND X.TYPE        = '1' "
				+ "                                          AND X.APLY_START_DATE = (SELECT MAX(APLY_START_DATE) "
				+ "                                                                     FROM BPR300T WITH(NOLOCK) "
				+ "                                                                    WHERE TYPE             = X.TYPE "
				+ "                                                                      AND COMP_CODE        = X.COMP_CODE "
				+ "                                                                      AND DIV_CODE         = X.DIV_CODE "
				+ "                                                                      AND ITEM_CODE        = X.ITEM_CODE "
				+ "                                                                      AND CUSTOM_CODE      = X.CUSTOM_CODE "
				+ "                                                                      AND APLY_START_DATE <=  CONVERT(NVARCHAR(8), GETDATE(), 112)) "
				+ " "
				+ "       LEFT JOIN BSA100T AS Y1 WITH(NOLOCK) ON Y1.COMP_CODE = C.COMP_CODE "
				+ "                                           AND Y1.MAIN_CODE = 'M001' "
				+ "                                           AND Y1.SUB_CODE <> '$' "
				+ "                                           AND Y1.SUB_CODE  = E.ORDER_TYPE "
				+ " "
				+ "       LEFT JOIN BSA100T AS Y2 WITH(NOLOCK) ON Y2.COMP_CODE = C.COMP_CODE "
				+ "                                           AND Y2.MAIN_CODE = 'B038' "
				+ "                                           AND Y2.SUB_CODE <> '$' "
				+ "                                           AND Y2.SUB_CODE  = E.RECEIPT_TYPE "
				+ " "
				+ "       LEFT JOIN BSA100T AS Y3 WITH(NOLOCK) ON Y3.COMP_CODE = C.COMP_CODE "
				+ "                                           AND Y3.MAIN_CODE = 'M201' "
				+ "                                           AND Y3.SUB_CODE <> '$' "
				+ "                                           AND Y3.SUB_CODE  = E.ORDER_PRSN "
				+ " "
				+ " "
				+ "     WHERE A.COMP_CODE    = @COMP_CODE "
				+ "       AND A.DIV_CODE     = @DIV_CODE "
				+ "       AND E.AGREE_STATUS = N'2'    "//--승인 
				+ "       AND G.USE_YN       = N'Y' "
				+ "       AND E.ORDER_TYPE  <> N'4'    "//--외주 
				+ "       AND A.CONTROL_STATUS NOT IN ('8', '9') "
				+ " "
				+ "       AND ((E.ORDER_DATE    >= @FR_DATE AND @FR_DATE <> '') OR (@FR_DATE = '')) "
				+ "       AND ((E.ORDER_DATE    <= @TO_DATE AND @TO_DATE <> '') OR (@TO_DATE = '')) "
				+ "       AND ((E.ORDER_TYPE     = @ORDER_TYPE AND @ORDER_TYPE <> '') OR (@ORDER_TYPE = '')) "
				+ "       AND ((E.CUSTOM_CODE    = @CUSTOM_CODE AND @CUSTOM_CODE <> '') OR (@CUSTOM_CODE = '')) "
				+ "       AND ((A.ITEM_CODE      = @ITEM_CODE AND @ITEM_CODE <> '') OR (@ITEM_CODE = '')) "
				+ "       AND ((A.DVRY_DATE     >= @FR_DR_DATE AND @FR_DR_DATE <> '') OR (@FR_DR_DATE = '')) "
				+ "       AND ((A.DVRY_DATE     <= @TO_DR_DATE AND @TO_DR_DATE <> '') OR (@TO_DR_DATE = '')) "
				+ "       AND ((A.WH_CODE        = @WH_CODE AND @WH_CODE <> '') OR (@WH_CODE = '')) "
				+ "       AND ((A.CONTROL_STATUS = @CONTROL_STATUS AND @CONTROL_STATUS <> '') OR (@CONTROL_STATUS = '')) "
				+ "       AND ((A.ORDER_NUM      = @ORDER_NUM AND @ORDER_NUM <> '') OR (@ORDER_NUM = '')) "
				+ " "
				+ "     ORDER BY A.ITEM_CODE,  E.ORDER_DATE, A.DVRY_DATE, A.ORDER_SEQ "
				+ " "
				+ "END";
		
		BeanPropertySqlParameterSource beanProps = new BeanPropertySqlParameterSource(orderModel);
		lists = jdbc.query(query, beanProps, new RowMapper<OrderModel>() {
			@Override
			public OrderModel mapRow(ResultSet resultSet, int i)
			
			
					throws SQLException {
				return convertToAccountModel(resultSet);
			}
		});
		
		if (null == lists) {
			lists = new ArrayList<OrderModel>();
		}

		return lists;
	}


	private OrderModel convertToAccountModel(ResultSet resultSet)
			throws SQLException {
		OrderModel data = new OrderModel();
		
		data.setNo(resultSet.getString("no"));
		data.setItemCode(resultSet.getString("itemCode"));
		data.setItemName(resultSet.getString("itemName"));
		data.setSpec(resultSet.getString("spec"));
		data.setOrderUnitQ(resultSet.getString("orderUnitQ"));
		data.setOrderUnit(resultSet.getString("orderUnit"));
		data.setOrderUnitP(resultSet.getString("orderUnitP"));
		data.setOrderO(resultSet.getString("orderO"));
		data.setDvryDate(resultSet.getString("dvryDate"));
		data.setOrderDate(resultSet.getString("orderDate"));
		data.setOrderNum(resultSet.getString("orderNum"));
		data.setDivfullName(resultSet.getString("divfullName"));
		data.setKorAddr(resultSet.getString("korAddr"));
		data.setTelePhone(resultSet.getString("telePhone"));
		data.setFaxNum(resultSet.getString("faxNum"));
		data.setDivName(resultSet.getString("divName"));
		data.setCompanyNum(resultSet.getString("companyNum"));
		data.setCustomFullName(resultSet.getString("customFullName"));
		data.setKorAddr1(resultSet.getString("korAddr1"));
		data.setCompanyNum1(resultSet.getString("companyNum1"));
		data.setCustomCode(resultSet.getString("customCode"));
		data.setCustTel(resultSet.getString("custTel"));
		data.setCustFax(resultSet.getString("custFax"));
		data.setWhCode(resultSet.getString("whCode"));
		data.setRemark(resultSet.getString("remark"));
		data.setSumOrderO(resultSet.getLong("sumOrderO"));
		
		
		return data;
	}
	
	@Override
	public void orderCompleted(String[] params) { //결재 완료시 update
		
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE MPO100T " 
				+ "SET TEMPC_03 = 'C' "
				+ " WHERE ORDER_NUM =:orderNumP "// key
				+ "   AND COMP_CODE = 'MASTER' "
				+ "   AND DIV_CODE = '01' ";
		
		int resultCnt = jdbc.update(SQL, new MapSqlParameterSource()
		.addValue("orderNumP", params[0]));
		
		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}
	
	@Override
	public void orderDrafted(String[] params) { //결재 기안시 update
		
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE MPO100T " 
				+ "SET TEMPC_03 = 'Y' "
				+ " WHERE ORDER_NUM =:orderNumP "
				+ "   AND COMP_CODE = 'MASTER' "
				+ "   AND DIV_CODE = '01' ";
		
		int resultCnt = jdbc.update(SQL, new MapSqlParameterSource()
		.addValue("orderNumP", params[0]));
		
		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}
	
	@Override
	public void orderReturned(String[] params) { //결재 반려시 update
		
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE MPO100T " 
				+ "SET TEMPC_03 = 'N' "
				+ " WHERE ORDER_NUM =:orderNumP " //key
				+ "   AND COMP_CODE = 'MASTER' "
				+ "   AND DIV_CODE = '01' ";
		
		int resultCnt = jdbc.update(SQL, new MapSqlParameterSource()
		.addValue("orderNumP", params[0]));
		
		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}





	

	
}
