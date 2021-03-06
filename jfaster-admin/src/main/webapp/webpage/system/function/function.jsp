<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>菜单信息</title>
<t:base type="jquery,easyui,tools"></t:base>
<script type="text/javascript">
		
	$(function() {
		$('#cc').combotree({
			url : 'functionController.do?setPFunction&selfId=${functionView.id}',
			panelHeight: 200,
			width: 157,
			onClick: function(node){
				$("#functionId").val(node.id);
			}
		});
		
		if($('#functionLevel').val()=='1'){
			$('#pfun').show();
		}else{
			$('#pfun').hide();
		}
		
		
		$('#functionLevel').change(function(){
			if($(this).val()=='1'){
				$('#pfun').show();
				var t = $('#cc').combotree('tree');
				var nodes = t.tree('getRoots');
				if(nodes.length>0){
					$('#cc').combotree('setValue', nodes[0].id);
					$("#functionId").val(nodes[0].id);
				}
			}else{
				var t = $('#cc').combotree('tree');
				var node = t.tree('getSelected');
				if(node){
					$('#cc').combotree('setValue', null);
				}
                $("#functionId").val(null);
				$('#pfun').hide();
			}
		});
	});
</script>
</head>
<body>
<t:formvalid formid="formobj" layout="div" dialog="true" refresh="true" action="functionController.do?saveFunction">
	<input name="id" type="hidden" value="${functionView.id}">
	<fieldset class="step">
	<div class="form">
        <label class="Validform_label"> <t:language langKey="menu.name"/>: </label>
        <input name="functionName" class="inputxt" value="${functionView.functionName}" datatype="s4-15">
        <span class="Validform_checktip"> <t:language langKey="menuname.rang4to15"/> </span>
    </div>
    <div class="form">
        <label class="Validform_label"> <t:language langKey="funcType"/>: </label>
        <select name="functionType" id="functionType" datatype="*">
            <option value="0" <c:if test="${functionView.functionType eq 0}">selected="selected"</c:if>>
                <t:language langKey="funcType.page"/>
            </option>
            <option value="1" <c:if test="${functionView.functionType eq 1}"> selected="selected"</c:if>>
                <t:language langKey="funcType.from"/>
            </option>
        </select>
        <span class="Validform_checktip"></span>
    </div>
	<div class="form">
        <label class="Validform_label"> <t:language langKey="menu.level"/>: </label>
        <select name="functionLevel" id="functionLevel" datatype="*">
            <option value="0" <c:if test="${functionView.functionLevel eq 0}">selected="selected"</c:if>>
                <t:language langKey="main.function"/>
            </option>
            <option value="1" <c:if test="${functionView.functionLevel>0}"> selected="selected"</c:if>>
                <t:language langKey="sub.function"/>
            </option>
        </select>
        <span class="Validform_checktip"></span>
    </div>
	<div class="form" id="pfun">
        <label class="Validform_label"> <t:language langKey="parent.function"/>: </label>
        <input id="cc" <c:if test="${functionView.TSFunction.functionLevel eq 0}"> value="${functionView.TSFunction.id}"</c:if>
		<c:if test="${functionView.TSFunction.functionLevel > 0}"> value="${functionView.TSFunction.functionName}"</c:if>>
        <input id="functionId" name="Function.id" style="display: none;" value="${functionView.TSFunction.id}">
    </div>
	<div class="form" id="funurl">
        <label class="Validform_label">
            <t:language langKey="menu.url"/>:
        </label>
        <input name="functionUrl" class="inputxt" value="${functionView.functionUrl}">
    </div>
    <div class="form">
        <label class="Validform_label"> <t:language langKey="common.icon"/>: </label>
        <select name="Icon.id">
            <c:forEach items="${iconlist}" var="icon">
                <option value="${icon.id}" <c:if test="${icon.id==function.TSIcon.id || (function.id eq null && icon.iconClas eq 'pictures') }">selected="selected"</c:if>>
                    <t:language langKey="${icon.iconName}"/>
                </option>
            </c:forEach>
        </select>
    </div>
    <div class="form">
        <label class="Validform_label"> <t:language langKey="desktop.icon"/>: </label>
        <select name="IconDesk.id">
            <c:forEach items="${iconDeskList}" var="icon">
                <option value="${icon.id}" <c:if test="${icon.id==function.TSIconDesk.id || (function.id eq null && icon.iconClas eq 'System Folder') }">selected="selected"</c:if>>
                    <t:language langKey="${icon.iconName}"/>
                </option>
            </c:forEach>
        </select>
    </div>
	<div class="form" id="funorder"><label class="Validform_label"> <t:language langKey="menu.order"/>: </label> <input name="functionOrder" class="inputxt" value="${functionView.functionOrder}" datatype="n1-3"></div>
	</fieldset>
</t:formvalid> 
</body>
</html>
