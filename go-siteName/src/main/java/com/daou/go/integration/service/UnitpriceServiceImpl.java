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

import com.daou.go.integration.domain.model.SuppliesModel;
import com.daou.go.integration.domain.model.UnitpriceModel;


@Service
public class UnitpriceServiceImpl implements UnitpriceService {
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
	public List<UnitpriceModel> getAccountingRows(Map<String, String> params) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		List<UnitpriceModel> lists;
		// final String query = "select '홍길동' as name ";

		
		
		final String query = "select TOP 1 A.COMP_CODE as compCode, A.DIV_CODE as divCode, A.DISCOUNT_NUM as discountNum, "
				+ "A.CUSTOM_CODE as customCode, B.CUSTOM_NAME, "
				+ "A.ITEM_CODE as itemCode, C.ITEM_NAME as itemName, C.SPEC as spec, C.SALE_UNIT as saleUnit "
				+ "from BPR400T_TEMP A "
				+ "INNER JOIN BCM100T B ON A.COMP_CODE = B.COMP_CODE "
				+ "AND A.CUSTOM_CODE = B.CUSTOM_CODE "
				+ "INNER JOIN BPR100T C ON A.COMP_CODE = C.COMP_CODE "
				+ "AND A.ITEM_CODE = C.ITEM_CODE "
				+ "WHERE A.DISCOUNT_NUM =:discountNum ";
				
		lists = jdbc.query(query, params, new RowMapper<UnitpriceModel>() {
			@Override
			public UnitpriceModel mapRow(ResultSet resultSet, int i)
					throws SQLException {
				return convertToAccountModel(resultSet);
			}
		});
		return lists;
	}

	@Override
	public List<UnitpriceModel> getErpData(UnitpriceModel unitpriceModel) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		List<UnitpriceModel> lists;

		final String query = "SELECT TOP 2000 A.CUSTOM_CODE "
				+ "	, B.CUSTOM_NAME as customName"
				+ "	, ( SELECT NAME "
				+ "		  FROM HUM100T WITH(NOLOCK) "
				+ "		 WHERE COMP_CODE = 'MASTER' "
				+ "		   AND DEPT_CODE = '1013' "
				+ "		   AND POST_CODE = '10' "
				+ "		   AND RETR_DATE = '00000000' "
				+ "		) AS SAMWOO_MATRL "
				+ "	, B.TOP_NAME as topName"
				+ "	, ISNULL(UNILITE.FNGETUSERDATE(A.COMP_CODE, A.APLY_START_DATE), '') as startDate" // 적용시점
				+ "	, A.DISCOUNT_NUM as discountNum" 
				+ " , A.CONFIRM_FLAG as confirmFlag"
				+ "	, ROW_NUMBER() OVER(PARTITION BY A.CUSTOM_CODE ORDER BY A.ITEM_CODE) as no "
				+ "	, A.ITEM_CODE as itemCode "
				+ "	, C.ITEM_NAME as itemName "
				+ "	, C.SPEC as spec "
				+ "	, REPLACE(CONVERT(NVARCHAR(30), CAST(A.ITEM_P AS MONEY), 1), '.00', '') as itemPrice " //현재가
				+ "	, REPLACE(CONVERT(NVARCHAR(30), CAST(A.CALC_P AS MONEY), 1), '.00', '') as calcPrice " //적용단가
				+ "	, REPLACE(CONVERT(NVARCHAR(30), CAST(A.DISCOUNT_P AS MONEY), 1), '.00', '') as discountPrice " //인하단가
				+ "	, CONVERT(FLOAT,A.REDUCED_RATES) as reducedRates " //인하율
				+ "	, '' as memo " //비고
				+ "  FROM BPR400T_TEMP A WITH(NOLOCK) "
				+ " INNER JOIN BCM100T B WITH(NOLOCK) ON A.COMP_CODE = B.COMP_CODE AND A.CUSTOM_CODE = B.CUSTOM_CODE "
				+ " INNER JOIN BPR100T C WITH(NOLOCK) ON A.COMP_CODE = C.COMP_CODE AND A.ITEM_CODE = C.ITEM_CODE "
				+ " WHERE A.COMP_CODE = 'MASTER' "
				+ "   AND A.DIV_CODE = '01' "
				+ "   AND A.DISCOUNT_NUM =:discountNum " //key
				+ "   AND A.CONFIRM_FLAG =:confirmFlag "//flag
				+ "ORDER BY A.DISCOUNT_NUM, A.ITEM_CODE";
		
		
		
		
		
		BeanPropertySqlParameterSource beanProps = new BeanPropertySqlParameterSource(unitpriceModel);
		lists = jdbc.query(query, beanProps, new RowMapper<UnitpriceModel>() {
			@Override
			public UnitpriceModel mapRow(ResultSet resultSet, int i)
			
			
					throws SQLException {
				return convertToAccountModel(resultSet);
			}
		});
		
		if (null == lists) {
			lists = new ArrayList<UnitpriceModel>();
		}

		return lists;
	}

	/*@Override
	public TestModel getTest(Map<String, String> params) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		// TestModel map;

		final String query = "select TOP 1 'esis' as itemName "
				+ "from BPR400T_TEMP A "
				+ "INNER JOIN BCM100T B ON A.C
				OMP_CODE = B.COMP_CODE "
				+ "AND A.CUSTOM_CODE = B.CUSTOM_CODE "
				+ "INNER JOIN BPR100T C ON A.COMP_CODE = C.COMP_CODE "
				+ "AND A.ITEM_CODE = C.ITEM_CODE "
				+ "WHERE A.DISCOUNT_NUM = :discountNum";

		TestModel map = jdbc.queryForObject(query, params,
				new RowMapper<TestModel>() {
					@Override
					public TestModel mapRow(ResultSet resultSet, int i)
							throws SQLException {
						return convertToAccountModel2(resultSet);
					}
				});

		return map;
	}*/
	
	@Override
	@Deprecated
	public UnitpriceModel getTest(Map<String, String> params) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		// TestModel map;
		
		final String query = "SELECT A.PREQ_SEQ as rowNum, A.PCUSTOM_CODE, C.CUSTOM_NAME as customName, A.PITEM_CODE, B.PITEM_NAME as pitemName, B.PSPEC, "
				+ "B.PORDER_UNIT, D.SUB_CODE, A.PREQ_Q as preqQ, B.PITEM_P, A.PREQ_Q * B.PITEM_P as price, A.PREQ_IN_DATE, A.[DESC] "
				//+ "A.COMP_CODE as compCode, A.DIV_CODE as divCode , A.PREQ_NUM as preqNum "
				+ "FROM MCS200_B154 A WITH(NOLOCK) "
				+ "INNER JOIN MCS100_B154 B WITH(NOLOCK) "
				+ "ON B.COMP_CODE = A.COMP_CODE "
				+ "AND B.DIV_CODE = A.DIV_CODE "
				+ "AND B.PITEM_CODE = A.PITEM_CODE "
				+ "LEFT OUTER JOIN BCM100T C WITH(NOLOCK) "
				+ "ON C.COMP_CODE = A.COMP_CODE "
				+ "AND C.CUSTOM_CODE = A.PCUSTOM_CODE "
				+ "LEFT OUTER JOIN BSA100T D WITH(NOLOCK) "
				+ "ON D.COMP_CODE = A.COMP_CODE "
				+ "AND D.MAIN_CODE = 'B013' "
				+ "AND D.SUB_CODE = B.PORDER_UNIT "
				+ "WHERE A.COMP_CODE = 'MASTER' "
				+ "AND A.DIV_CODE = '01' "
				+ "AND A.PREQ_NUM = 'C20160518001' "
				+ "ORDER BY A.PREQ_NUM ";
		
		
/*		final String query = "select TOP 1 'esis' as itemName "
				+ "from BPR400T_TEMP A "
				+ "INNER JOIN BCM100T B ON A.COMP_CODE = B.COMP_CODE "
				+ "AND A.CUSTOM_CODE = B.CUSTOM_CODE "
				+ "INNER JOIN BPR100T C ON A.COMP_CODE = C.COMP_CODE "
				+ "AND A.ITEM_CODE = C.ITEM_CODE "
				+ "WHERE A.DISCOUNT_NUM = :discountNum";*/
		
		UnitpriceModel map = jdbc.queryForObject(query, params,
				new RowMapper<UnitpriceModel>() {
			@Override
			public UnitpriceModel mapRow(ResultSet resultSet, int i)
					throws SQLException {
				return convertToAccountModel(resultSet);
			}
		});
		
		return map;
	}

	private UnitpriceModel convertToAccountModel(ResultSet resultSet)
			throws SQLException {
		UnitpriceModel data = new UnitpriceModel();
		
		data.setDiscountNum(resultSet.getString("discountNum"));
		data.setConfirmFlag(resultSet.getString("confirmFlag"));
		data.setStartDate(resultSet.getString("startDate"));
		data.setNo(resultSet.getString("no"));
		data.setItemCode(resultSet.getString("itemCode"));
		data.setItemName(resultSet.getString("itemName"));
		data.setSpec(resultSet.getString("spec"));
		data.setItemPrice(resultSet.getString("itemPrice"));
		data.setDiscountPrice(resultSet.getString("discountPrice"));
		data.setCalcPrice(resultSet.getString("calcPrice"));
		data.setReducedRates(resultSet.getString("reducedRates"));
		data.setMemo(resultSet.getString("memo"));
		data.setCustomName(resultSet.getString("customName"));
		data.setTopName(resultSet.getString("topName"));
		
		
		
		return data;
	}
	
/*	private TestModel convertToAccountModel(ResultSet resultSet)
			throws SQLException {
		TestModel data = new TestModel();
		data.setCompCode(resultSet.getString("compCode"));
		data.setDivCode(resultSet.getString("divCode"));
		data.setDiscountNum(resultSet.getString("discountNum"));
		data.setCustomCode(resultSet.getString("customCode"));
		data.setCustomName(resultSet.getString("customName"));
		data.setItemCode(resultSet.getString("itemCode"));
		data.setItemName(resultSet.getString("itemName"));
		data.setSpec(resultSet.getString("spec"));
		data.setSaleUnit(resultSet.getString("saleUnit"));
		
		return data;
	}
*/
	/*private TestModel convertToAccountModel2(ResultSet resultSet)//test
			throws SQLException {
		TestModel data = new TestModel();
		data.setItemName(resultSet.getString("itemName")); 	
		return data;
	}*/

	@Override
	public void unitpriceCompleted(String[] params) { //결재 완료시 update
		
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE BPR400T_TEMP " 
				+ "SET CONFIRM_FLAG = 'C' "
				+ " WHERE COMP_CODE = 'MASTER' "
				+ "   AND DIV_CODE = '01' "
				+ "   AND DISCOUNT_NUM =:discountNum " //key
				+ "	  AND CONFIRM_FLAG = 'Y' ";
		
		int resultCnt = jdbc.update(SQL, new MapSqlParameterSource()
		.addValue("discountNum", params[0]));
		
		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}
	
	@Override
	public void unitpriceDrafted(String[] params) { //결재 기안시 update
		
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE BPR400T_TEMP " 
				+ "SET CONFIRM_FLAG = 'Y' "
				+ " WHERE COMP_CODE = 'MASTER' "
				+ "   AND DIV_CODE = '01' "
				+ "   AND DISCOUNT_NUM =:discountNum " //key
				+ "	  AND CONFIRM_FLAG = 'N' ";
		
		int resultCnt = jdbc.update(SQL, new MapSqlParameterSource()
		.addValue("discountNum", params[0])
		.addValue("confirmFlag", params[1]));
		
		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}
	
	@Override
	public void unitpriceReturned(String[] params) { //결재 반려시 update
		
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE BPR400T_TEMP " 
				+ "SET CONFIRM_FLAG = 'N' "
				+ " WHERE COMP_CODE = 'MASTER' "
				+ "   AND DIV_CODE = '01' "
				+ "   AND DISCOUNT_NUM =:discountNum " //key
				+ "	  AND CONFIRM_FLAG = 'Y' ";
		
		int resultCnt = jdbc.update(SQL, new MapSqlParameterSource()
		.addValue("discountNum", params[0]));
		
		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}





	

	
}
