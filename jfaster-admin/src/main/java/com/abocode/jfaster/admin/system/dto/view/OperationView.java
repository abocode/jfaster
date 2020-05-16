package com.abocode.jfaster.admin.system.dto.view;

import com.abocode.jfaster.core.AbstractIdEntity;

import javax.persistence.*;

/**
 * 权限操作
 *  @author  guanxf
 */
public class OperationView extends AbstractIdEntity implements java.io.Serializable {
	private String operationname;
	private String operationcode;
	private String operationicon;
	private Short status;

	
	private Short operationType;
	
	@Column(name = "operationtype")
	public Short getOperationType() {
		return operationType;
	}

	public void setOperationType(Short operationType) {
		this.operationType = operationType;
	}

	@Column(name = "operationname", length = 50)
	public String getOperationName() {
		return this.operationname;
	}

	public void setOperationName(String operationname) {
		this.operationname = operationname;
	}

	@Column(name = "operationcode", length = 50)
	public String getOperationCode() {
		return this.operationcode;
	}

	public void setOperationCode(String operationcode) {
		this.operationcode = operationcode;
	}

	@Column(name = "operationicon", length = 100)
	public String getOperationicon() {
		return this.operationicon;
	}

	public void setOperationIcon(String operationicon) {
		this.operationicon = operationicon;
	}

	@Column(name = "status")
	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}
}