package com.abocode.jfaster.system.entity;

import com.abocode.jfaster.core.AbstractIdEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用类型字典表
 *
 * @author 张代浩
 */
@Entity
@Table(name = "t_s_type")
@Data
public class Type extends AbstractIdEntity implements java.io.Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_group_id")
    private TypeGroup typeGroup;//类型分组
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typep_id")
    private Type pType;//父类型
    @Column(name = "type_name", length = 50)
    private String typeName;//类型名称
    @Column(name = "type_code", length = 50)
    private String typeCode;//类型编码

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pType")
    private List<Type> types = new ArrayList<>();
}