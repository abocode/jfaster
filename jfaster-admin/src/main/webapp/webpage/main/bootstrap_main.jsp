<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/context/mytags.jsp"%>
<!DOCTYPE html >
<html>
<head>
<title><t:language langKey="system.title"/></title>
<t:base type="jquery,easyui,tools,DatePicker,autocomplete"></t:base>
<link rel="stylesheet" href="plug-in/bootstrap/css/bootstrap.css" type="text/css"></link>
<style type="text/css">
.bootstrap-menu i {
	display: inline-block;
	width: 14px;
	height: 14px;
	margin-top: 1px;
	*margin-right: .3em;
	line-height: 14px;
	vertical-align: text-top;
	background-image: url("plug-in/bootstrap/img/glyphicons-halflings.png");
	background-repeat: no-repeat;
}

.bootstrap-center {
	height: 530px;
	overflow-y: auto;
	margin-top: -20px;
	margin-left: 2px;
	margin-right: 2px;
}

.bootstrap-icon {
	display: inline-block;
	width: 16px;
	height: 16px;
	line-height: 16px;
	vertical-align: text-top;
	background-repeat: no-repeat;
	background-image: url("plug-in/accordion/images/pictures.png");
}

.footer {
	margin-top: 10px;
}
</style>
</head>
<body>
<!-- 头部菜单导航-->
<div id="header">
<div class="navbar">
<div class="navbar-inner">
<div class="container-fluid"><a class="brand" href="http://www.hulasou.com" target="_blank"><t:language langKey="system.title"/> &nbsp;&nbsp;<span class="slogan"></span></a>
<div class="nav-no-collapse bootstrap-menu">

<ul class="nav pull-right usernav">
	<li style="line-height: 43px;"><span style="color: #CC33FF"><t:language langKey="common.user"/>:</span><span style="color: #666633">(${username })</span> <span style="color: #CC33FF"><t:language langKey="common.role"/></span>:<span style="color: #666633">${roleName
	}</span></li>
	<li class="dropdown"><a href="#" class="dropdown-toggle avatar" data-toggle="dropdown"> <i class="icon-wrench"></i> <span class="txt"><t:language langKey="common.control.panel"/></span> <b class="caret"></b> </a>
	<ul class="dropdown-menu">
		<li onclick="openwindow('<t:language langKey="common.profile"/>','userController.do?userinfo')"><a href="javascript:;"><i class="icon-user"></i> <t:language langKey="common.profile"/></a></li>
		<li onclick="add('<t:language langKey="common.change.password"/>','userController.do?changepassword')"><a href="javascript:;"><i class="icon-pencil"></i> <t:language langKey="common.change.password"/></a></li>
	</ul>
	</li>
	<li class="dropdown"><a href="#" class="dropdown-toggle avatar" data-toggle="dropdown"> <i class="icon-arrow-left"></i> <span class="txt"><t:language langKey="common.logout"/></span> <b class="caret"></b> </a>
	<ul class="dropdown-menu">
		<li onclick="exit('loginController.do?logout','<t:language langKey="common.exit.confirm"/>',1);"><a href="javascript:;"><i class="icon-off"></i><t:language langKey="common.exit"/></a></li>
	</ul>
	</li>
</ul>
</div>
<!-- /.nav-collapse --></div>
</div>
<!-- /navbar-inner --></div>
<!-- /navbar --></div>
<!-- End #header -->

<!-- 中间 -->
<div id="wrapper" class="bootstrap-center"></div>

<!-- 底部 -->
<footer class="footer">
<div class="container">
<ul class="footer-links">
	<li style="display: inline;">&copy; <t:language langKey="common.copyright"/><a href="http://www.hulasou.com" title="<t:language langKey="system.name" />"><t:language langKey="system.name" /></a></li>
</ul>
</div>
</footer>
<script type="text/javascript" src="plug-in/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="plug-in/accordion/js/bootstrap_main.js"></script>
</body>
</html>