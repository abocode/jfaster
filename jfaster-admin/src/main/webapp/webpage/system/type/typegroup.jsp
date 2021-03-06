<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title><t:language langKey="common.add.param"/></title>
<t:base type="jquery,easyui,tools"></t:base>
</head>
<body style="overflow-y: hidden" scroll="no">
<t:formvalid formid="formobj" layout="div" dialog="true" action="typeController.do?saveTypeGroup">
	<input name="id" type="hidden" value="${typeGroupView.id }">
	<fieldset class="step">
	<div class="form">
	<label class="Validform_label"> <t:language langKey="common.name"/>: </label>
	<input name="typegroupname" class="inputxt" value="${typeGroupView.typegroupname }" datatype="s2-10"> <span class="Validform_checktip"><t:language langKey="common.range" langArg="common.name,common.range2to10"/></span></div>

	<div class="form">
	<label class="Validform_label"> <t:language langKey="common.code"/>: </label>
	<input name="typegroupcode" class="inputxt" validType="t_s_typegroup,typegroupcode,id" value="${typeGroupView.typegroupcode }" datatype="s2-10"> <span class="Validform_checktip"><t:language langKey="common.range" langArg="common.code,common.range2to8"/></span></div>
	</fieldset>
</t:formvalid>
</body>
</html>
