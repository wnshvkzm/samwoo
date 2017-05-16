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
import com.thoughtworks.xstream.io.json.JsonWriter.Format;

@Service
public class SuppliesServiceImpl implements SuppliesService {
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
	public List<SuppliesModel> getAccountingRows(Map<String, String> params) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		List<SuppliesModel> lists;
		// final String query = "select '홍길동' as name ";

		final String query = "select TOP 1 A.COMP_CODE as compCode, A.DIV_CODE as divCode, A.DISCOUNT_NUM as discountNum, "
				+ "A.CUSTOM_CODE as customCode, B.CUSTOM_NAME as customName, "
				+ "A.ITEM_CODE as itemCode, C.ITEM_NAME as itemName, C.SPEC as spec, C.SALE_UNIT as saleUnit "
				+ "from BPR400T_TEMP A "
				+ "INNER JOIN BCM100T B ON A.COMP_CODE = B.COMP_CODE "
				+ "AND A.CUSTOM_CODE = B.CUSTOM_CODE "
				+ "INNER JOIN BPR100T C ON A.COMP_CODE = C.COMP_CODE "
				+ "AND A.ITEM_CODE = C.ITEM_CODE "
				+ "WHERE A.DISCOUNT_NUM =:discountNum ";

		lists = jdbc.query(query, params, new RowMapper<SuppliesModel>() {
			@Override
			public SuppliesModel mapRow(ResultSet resultSet, int i)
					throws SQLException {
				return convertToAccountModel(resultSet);
			}
		});
		return lists;
	}

	@Override
	public List<SuppliesModel> getErpData(SuppliesModel suppliesModel) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		List<SuppliesModel> lists;

		final String query = "SELECT A.PREQ_SEQ as preqSeq, A.PCUSTOM_CODE, C.CUSTOM_NAME as customName, "
				+ "A.PITEM_CODE, B.PITEM_NAME as pitemName, A.TEMPC_01 as pSpec, "
				+ "B.PORDER_UNIT, D.SUB_CODE as subCode, "
				+ "REPLACE(CONVERT(NVARCHAR(30), CAST(A.PREQ_Q AS MONEY), 1), '.00', '') as preqQ, "
				+ "REPLACE(CONVERT(NVARCHAR(30), CAST(A.TEMPN_01 AS MONEY), 1), '.00', '') as pitemP, "
				+ "REPLACE(CONVERT(NVARCHAR(30), CAST(A.PREQ_Q * A.TEMPN_01 AS MONEY), 1), '.00', '') as price, "
				+ "A.PREQ_Q * A.TEMPN_01 as sumPrice, "
				+ "ISNULL(UNILITE.FNGETUSERDATE(A.COMP_CODE, A.PDVRY_DATE), '') as preqInDate, "
				+ "A.[DESC] as memo, "
				+ "A.COMP_CODE as compCode, A.DIV_CODE as divCode , A.PREQ_NUM as preqNum "
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
				+ "WHERE A.COMP_CODE =:compCode "
				+ "AND A.DIV_CODE =:divCode "
				+ "AND A.PREQ_NUM =:preqNum "
				+ "ORDER BY A.PREQ_NUM";
		
		BeanPropertySqlParameterSource beanProps = new BeanPropertySqlParameterSource(suppliesModel);
		lists = jdbc.query(query, beanProps, new RowMapper<SuppliesModel>() {
			@Override
			public SuppliesModel mapRow(ResultSet resultSet, int i)
					throws SQLException {
				return convertToAccountModel(resultSet);
			}
		});
		
		
		if (null == lists) {
			lists = new ArrayList<SuppliesModel>();
		}

		return lists;
	}

	/*
	 * @Override public TestModel getTest(Map<String, String> params) {
	 * NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate)
	 * applicationContext .getBean("ERPJdbcTemplate"); // TestModel map;
	 * 
	 * final String query = "select TOP 1 'esis' as itemName " +
	 * "from BPR400T_TEMP A " +
	 * "INNER JOIN BCM100T B ON A.COMP_CODE = B.COMP_CODE " +
	 * "AND A.CUSTOM_CODE = B.CUSTOM_CODE " +
	 * "INNER JOIN BPR100T C ON A.COMP_CODE = C.COMP_CODE " +
	 * "AND A.ITEM_CODE = C.ITEM_CODE " + "WHERE A.DISCOUNT_NUM = :discountNum";
	 * 
	 * TestModel map = jdbc.queryForObject(query, params, new
	 * RowMapper<TestModel>() {
	 * 
	 * @Override public TestModel mapRow(ResultSet resultSet, int i) throws
	 * SQLException { return convertToAccountModel2(resultSet); } });
	 * 
	 * return map; }
	 */

	@Override
	@Deprecated
	public SuppliesModel getTest(Map<String, String> params) {
		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		// TestModel map;

		final String query = "SELECT A.PREQ_SEQ as rowNum, A.PCUSTOM_CODE, C.CUSTOM_NAME as customName, A.PITEM_CODE, B.PITEM_NAME as pitemName, B.PSPEC, "
				+ "B.PORDER_UNIT, D.SUB_CODE, A.PREQ_Q as preqQ, B.PITEM_P, A.PREQ_Q * B.PITEM_P as price, A.PREQ_IN_DATE, A.[DESC] "
				// +
				// "A.COMP_CODE as compCode, A.DIV_CODE as divCode , A.PREQ_NUM as preqNum "
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
				+ "AND A.PREQ_NUM = 'C20160518001' " + "ORDER BY A.PREQ_NUM ";

		/*
		 * final String query = "select TOP 1 'esis' as itemName " +
		 * "from BPR400T_TEMP A " +
		 * "INNER JOIN BCM100T B ON A.COMP_CODE = B.COMP_CODE " +
		 * "AND A.CUSTOM_CODE = B.CUSTOM_CODE " +
		 * "INNER JOIN BPR100T C ON A.COMP_CODE = C.COMP_CODE " +
		 * "AND A.ITEM_CODE = C.ITEM_CODE " +
		 * "WHERE A.DISCOUNT_NUM = :discountNum";
		 */

		SuppliesModel map = jdbc.queryForObject(query, params,
				new RowMapper<SuppliesModel>() {
					@Override
					public SuppliesModel mapRow(ResultSet resultSet, int i)
							throws SQLException {
						return convertToAccountModel(resultSet);
					}
				});

		return map;
	}

	private SuppliesModel convertToAccountModel(ResultSet resultSet)
			throws SQLException {
		SuppliesModel data = new SuppliesModel();
		data.setCompCode(resultSet.getString("compCode"));
		data.setDivCode(resultSet.getString("divCode"));
		data.setPreqNum(resultSet.getString("preqNum"));
		data.setPreqSeq(resultSet.getString("preqSeq"));
		data.setCustomName(resultSet.getString("customName"));
		data.setPitemName(resultSet.getString("pitemName"));
		data.setPSpec(resultSet.getString("pSpec"));
		data.setSubCode(resultSet.getString("subCode"));
		data.setPreqQ(resultSet.getString("preqQ"));
		data.setPitemP(resultSet.getString("pitemP"));
		data.setPreqInDate(resultSet.getString("preqInDate"));
		data.setMemo(resultSet.getString("memo"));
		data.setPrice(resultSet.getString("price"));
		data.setSumPrice(resultSet.getLong("sumPrice"));

		return data;
	}

	/*
	 * private TestModel convertToAccountModel(ResultSet resultSet) throws
	 * SQLException { TestModel data = new TestModel();
	 * data.setCompCode(resultSet.getString("compCode"));
	 * data.setDivCode(resultSet.getString("divCode"));
	 * data.setDiscountNum(resultSet.getString("discountNum"));
	 * data.setCustomCode(resultSet.getString("customCode"));
	 * data.setCustomName(resultSet.getString("customName"));
	 * data.setItemCode(resultSet.getString("itemCode"));
	 * data.setItemName(resultSet.getString("itemName"));
	 * data.setSpec(resultSet.getString("spec"));
	 * data.setSaleUnit(resultSet.getString("saleUnit"));
	 * 
	 * return data; }
	 */
	/*
	 * private TestModel convertToAccountModel2(ResultSet resultSet)//test
	 * throws SQLException { TestModel data = new TestModel();
	 * data.setItemName(resultSet.getString("itemName")); return data; }
	 */

	@Override
	public void suppliesCompleted(String[] params) { // 결재 완료시 update

		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE MCS200_B154 "
				+ "SET APPROVAL_YN = 'C' "
				+ "WHERE COMP_CODE = :COMP_CODE AND DIV_CODE = :DIV_CODE AND PREQ_NUM = :PREQ_NUM";

		int resultCnt = jdbc.update(
				SQL,
				new MapSqlParameterSource().addValue("COMP_CODE", params[0])
						.addValue("DIV_CODE", params[1])
						.addValue("PREQ_NUM", params[2]));

		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}

	@Override
	public void suppliesDrafted(String[] params) { // 결재 기안시 update

		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE MCS200_B154 "
				+ "SET APPROVAL_YN = 'Y' "
				+ "WHERE COMP_CODE = :COMP_CODE AND DIV_CODE = :DIV_CODE AND PREQ_NUM = :PREQ_NUM";

		int resultCnt = jdbc.update(
				SQL,
				new MapSqlParameterSource().addValue("COMP_CODE", params[0])
						.addValue("DIV_CODE", params[1])
						.addValue("PREQ_NUM", params[2]));

		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}

	@Override
	public void suppliesReturned(String[] params) { // 결재 반려시 update

		NamedParameterJdbcTemplate jdbc = (NamedParameterJdbcTemplate) applicationContext
				.getBean("ERPJdbcTemplate");
		String SQL = "UPDATE MCS200_B154 "
				+ "SET APPROVAL_YN = 'N' "
				+ "WHERE COMP_CODE = :COMP_CODE AND DIV_CODE = :DIV_CODE AND PREQ_NUM = :PREQ_NUM";

		int resultCnt = jdbc.update(
				SQL,
				new MapSqlParameterSource().addValue("COMP_CODE", params[0])
						.addValue("DIV_CODE", params[1])
						.addValue("PREQ_NUM", params[2]));

		System.out.println(resultCnt == 1);
		System.out.println(resultCnt > 1);
		System.out.println(resultCnt == 0);
	}

}
