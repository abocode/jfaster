package com.abocode.jfaster.core.persistence.hibernate.qbc;

import java.util.List;
import java.util.Map;

import com.abocode.jfaster.core.repository.DataGridParam;
import lombok.Data;
import org.hibernate.type.Type;

@Data
public class HqlQuery {
	private int curPage =1;
	private int pageSize = 10;
	private String action;
	private String form;
	private String queryString;
	private Object[] param;
	private Type[] types;
	private Map<String, Object> map;
	private DataGridParam dataGridParam;
	private String field="";//查询需要显示的字段
	private Class class1;
	private List results;// 结果集
	private int total;
}
