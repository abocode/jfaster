<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>部门信息</title>
<t:base type="jquery,easyui,tools"></t:base>
<script type="text/javascript">
	$(function() {
		$('#cc').combotree({
			url : 'departController.do?setPFunction&selfId=${departView.id}',
            width: 155,
            onSelect : function(node) {
//                alert(node.text);
                changeOrgType();
            }
        });
        if(!$('#cc').val()) { // 第一级，只显示公司选择项
            var orgTypeSelect = $("#orgType");
            var companyOrgType = '<option value="1" <c:if test="${orgType=='1'}">selected="selected"</c:if>><t:language langKey="common.company"/></option>';
            orgTypeSelect.empty();
            orgTypeSelect.append(companyOrgType);
        } else { // 非第一级，不显示公司选择项
            $("#orgType option:first").remove();
        }
        if($("#id").val()) {
            $('#cc').combotree('disable');
        }
        if('${empty pid}' == 'false') { // 设置新增页面时的父级
            $('#cc').combotree('setValue', '${pid}');
        }
	});
    function changeOrgType() { // 处理组织类型，不显示公司选择项
        var orgTypeSelect = $("#orgType");
        var optionNum = orgTypeSelect.get(0).options.length;
        if(optionNum == 1) {
            $("#orgType option:first").remove();
            var bumen = '<option value="2" <c:if test="${orgType=='2'}">selected="selected"</c:if>><t:language langKey="common.department"/></option>';
            var gangwei = '<option value="3" <c:if test="${orgType=='3'}">selected="selected"</c:if>><t:language langKey="common.position"/></option>';
            orgTypeSelect.append(bumen).append(gangwei);
        }
    }
</script>
</head>
<body style="overflow-y: hidden" scroll="no">
<t:formvalid formid="formobj" layout="div" dialog="true" action="systemController.do?saveDepart">
	<input id="id" name="id" type="hidden" value="${departView.id }">
	<fieldset class="step">
        <div class="form">
            <label class="Validform_label"> <t:language langKey="common.department.name"/>: </label>
            <input name="departname" class="inputxt" value="${departView.departname }"  datatype="s1-20">
            <span class="Validform_checktip"><t:language langKey="departmentname.rang1to20"/></span>
        </div>
        <div class="form">
            <label class="Validform_label"> <t:language langKey="position.desc"/>: </label>
            <input name="description" class="inputxt" value="${departView.description }">
        </div>
        <div class="form">
            <label class="Validform_label"> <t:language langKey="parent.depart"/>: </label>
            <input id="cc" name="PDepart.id" value="${departView.TSPDepart.id}">
        </div>
        <div class="form">
            <input type="hidden" name="orgCode" value="${departView.orgCode }">
            <label class="Validform_label"> <t:language langKey="common.org.type"/>: </label>
            <select name="orgType" id="orgType">
                <option value="1" <c:if test="${orgType=='1'}">selected="selected"</c:if>><t:language langKey="common.company"/></option>
                <option value="2" <c:if test="${orgType=='2'}">selected="selected"</c:if>><t:language langKey="common.department"/></option>
                <option value="3" <c:if test="${orgType=='3'}">selected="selected"</c:if>><t:language langKey="common.position"/></option>
            </select>
        </div>
	</fieldset>
</t:formvalid>
</body>
</html>
