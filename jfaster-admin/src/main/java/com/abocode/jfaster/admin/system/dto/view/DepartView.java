package com.abocode.jfaster.admin.system.dto.view;

import com.abocode.jfaster.core.AbstractIdEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * 部门机构表
 * @author  guanxf
 */
public class DepartView extends AbstractIdEntity implements java.io.Serializable {
	private DepartView TSPDepart;//上级部门
	private String departname;//部门名称
	private String description;//部门描述
    private String orgCode;//机构编码
    private String orgType;//机构编码
	private List<DepartView> TSDeparts = new ArrayList<DepartView>();//下属部门

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentdepartid")
	public DepartView getPDepart() {
		return this.TSPDepart;
	}

	public void setPDepart(DepartView TSPDepart) {
		this.TSPDepart = TSPDepart;
	}

	@Column(name = "departname", nullable = false, length = 100)
	public String getOrgName() {
		return this.departname;
	}

	public void setDepartname(String departname) {
		this.departname = departname;
	}

	@Column(name = "description", length = 500)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "PDepart")
	public List<DepartView> getOrgs() {
		return TSDeparts;
	}

	public void setOrgs(List<DepartView> tSDeparts) {
		TSDeparts = tSDeparts;
	}

    @Column(name = "org_code", length = 64)
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "org_type", length = 1)
    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }
}