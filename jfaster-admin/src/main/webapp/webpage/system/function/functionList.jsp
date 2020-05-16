<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<t:base type="jquery,easyui,tools,DatePicker"></t:base>
<div id="system_function_functionList" class="easyui-layout" fit="true">
<div region="center" style="padding: 1px;">
    <t:datagrid name="functionList" title="menu.manage"
                actionUrl="functionController.do?functionGrid" idField="id" treegrid="true" pagination="false">
        <t:dgCol title="common.id" field="id" treefield="id" hidden="true"></t:dgCol>
        <t:dgCol title="menu.name" field="functionName" treefield="text"></t:dgCol>
        <t:dgCol title="common.icon" field="Icon_iconPath" treefield="code" image="true"  funname="viewImage"></t:dgCol>
        <t:dgCol title="funcType" field="functionType" treefield="functionType" replace="funcType.page_0,funcType.from_1"></t:dgCol>
        <t:dgCol title="menu.url" field="functionUrl" treefield="src" hidden="true"></t:dgCol>
        <t:dgCol title="menu.order" field="functionOrder" treefield="order"></t:dgCol>
        <t:dgCol title="common.operation" field="opt"></t:dgCol>
        <t:dgDelOpt url="functionController.do?del&id={id}" title="common.delete"></t:dgDelOpt>
        <t:dgFunOpt funname="operationDetail(id)" title="button.setting"></t:dgFunOpt>
        <t:dgFunOpt funname="operationData(id)" title="数据规则"></t:dgFunOpt>
        <t:dgToolBar title="common.add.param" langArg="common.menu" icon="icon-add" url="functionController.do?addorupdate" funname="addFun"></t:dgToolBar>
        <t:dgToolBar title="common.edit.param" langArg="common.menu" icon="icon-edit" url="functionController.do?addorupdate" funname="update"></t:dgToolBar>
		<t:dgToolBar title="common.view" icon="icon-search" url="functionController.do?addorupdate" funname="detail"></t:dgToolBar>
    </t:datagrid>
</div>
</div>
<div data-options="region:'east',
	title:'<t:mutiLang langKey="operate.button.list"/>',
	collapsed:true,
	split:true,
	border:false,
	onExpand : function(){
		li_east = 1;
	},
	onCollapse : function() {
	    li_east = 0;
	}"
	style="width: 420px; overflow: hidden;">
<div class="easyui-panel" style="padding: 1px;" fit="true" border="false" id="operationDetailpanel"></div>
</div>
</div>

<script type="text/javascript">
$(function() {
	var li_east = 0;
});
//数据规则权数
function  operationData(fucntionId){
	if(li_east == 0){
	   $('#system_function_functionList').layout('expand','east'); 
	}
	$('#operationDetailpanel').panel("refresh", "functionController.do?dataRule&functionId=" +fucntionId);
}
function operationDetail(functionId)
{
	if(li_east == 0){
	   $('#system_function_functionList').layout('expand','east'); 
	}
	$('#operationDetailpanel').panel("refresh", "functionController.do?operation&functionId=" +functionId);
}
function addFun(title,url, id) {
	var rowData = $('#'+id).datagrid('getSelected');
	if (rowData) {
		url += '&TSFunction.id='+rowData.id;
	}
	add(title,url,'functionList');
}
</script>

<script type="text/javascript">
	function viewImage(title,id,url){
		return false;
	}
</script>