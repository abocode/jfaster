<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:datagrid name="operationList" title="rule.manage" actionUrl="functionController.do?ruledategrid&functionId=${functionId}" idField="id">
	<t:dgCol title="common.id" field="id" hidden="true"></t:dgCol>
	<t:dgCol title="规则名称" field="ruleName" width="50"></t:dgCol>
    <t:dgCol title="规则字段" field="ruleColumn" width="50"></t:dgCol>
    <t:dgCol title="规则条件" field="ruleConditions" width="50"></t:dgCol>
    <t:dgCol title="规则值" field="ruleValue" width="50"></t:dgCol>
    <t:dgCol title="common.operation" field="opt" width="100"></t:dgCol>
	<t:dgDelOpt url="functionController.do?delrule&id={id}" title="common.delete"></t:dgDelOpt>
	<t:dgFunOpt function="editoperation(id,operationname)" title="common.edit"></t:dgFunOpt>
	<t:dgToolBar title="common.add.param" langArg="common.rule" icon="icon-add" url="functionController.do?detailrule&functionId=${functionId}" function="add"></t:dgToolBar>
	</t:datagrid>
<script type="text/javascript">
function editoperation(operationId,operationname)
{
	createwindow("<t:language langKey="common.edit.param" langArg="common.rule"/>","functionController.do?detailrule&functionId=${functionId}&id="+operationId);
}
</script>
