package com.abocode.jfaster.core.platform.view.widgets.easyui;

import com.abocode.jfaster.core.common.constants.Globals;
import com.abocode.jfaster.core.common.util.JspWriterUtils;
import com.abocode.jfaster.core.common.util.StrUtils;
import com.abocode.jfaster.core.platform.LanguageContainer;
import com.abocode.jfaster.core.platform.SystemContainer;
import com.abocode.jfaster.core.platform.utils.LanguageUtils;
import com.abocode.jfaster.core.platform.view.TypeView;
import com.abocode.jfaster.core.platform.view.interactions.easyui.ColumnValue;
import com.abocode.jfaster.core.platform.view.interactions.easyui.DataGridColumn;
import com.abocode.jfaster.core.platform.view.interactions.easyui.DataGridUrl;
import com.abocode.jfaster.core.platform.view.interactions.easyui.OptTypeDirection;
import com.abocode.jfaster.core.repository.TagUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.text.MessageFormat;
import java.util.*;
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class DataGridTag extends TagSupport {
    protected String fields = "";// 显示字段
    protected String searchFields = "";// 添加对区间查询的支持
    protected String name;// 表格标示
    protected String title;// 表格标示
    protected String idField = "id";// 主键字段
    protected boolean treeGrid = false;// 是否是树形列表
    protected List<DataGridUrl> urlList = new ArrayList<>();// 列表操作显示
    protected List<DataGridUrl> toolBarList = new ArrayList<>();// 工具条列表
    protected List<DataGridColumn> columnList = new ArrayList<>();// 列表操作显示
    protected List<ColumnValue> columnValueList = new ArrayList<>();// 值替换集合
    protected List<ColumnValue> columnStyleList = new ArrayList<>();// 颜色替换集合
    public Map<String, Object> map;// 封装查询条件
    private String actionUrl;// 分页提交路径
    public int allCount;
    public int curPageNo;
    public int pageSize = 10;
    public boolean pagination = true;// 是否显示分页
    private String width;
    private String height;
    private boolean checkbox = false;// 是否显示复选框
    private boolean showPageList = true;// 定义是否显示页面列表
    private boolean openFirstNode = false;//是不是展开第一个节点
    private boolean fit = true;// 是否允许表格自动缩放，以适应父容器
    private boolean fitColumns = true;// 当为true时，自动展开/合同列的大小，以适应的宽度，防止横向滚动.
    private String sortName;//定义的列进行排序
    private String sortOrder = "asc";//定义列的排序顺序，只能是"递增"或"降序".
    private boolean showRefresh = true;// 定义是否显示刷新按钮
    private boolean showText = true;// 定义是否显示刷新按钮
    private String style = "easyui";// 列表样式easyui,datatables
    private String onLoadSuccess;// 数据加载完成调用方法
    private String onClick;// 单击事件调用方法
    private String onDblClick;// 双击事件调用方法
    private String queryMode = "single";//查询模式
    private String entityName;//对应的实体对象
    private String rowStyler;//rowStyler函数
    private String extendParams;//扩展参数,easyui有的,但是jeecg没有的参数进行扩展
    private boolean autoLoadData = true; // 列表是否自动加载数据
    private String langArg;

    private boolean queryBuilder = false;// 高级查询器

    //json转换中的系统保留字
    private final static Map<String, String> code = new HashMap();

    static {
        code.put("class", "clazz");
    }


    /**
     * 设置询问操作URL
     */
    public void setConfUrl(String url, String title, String message, String exp, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.Confirm);
        dataGridUrl.setMessage(message);
        dataGridUrl.setExp(exp);
        installOperationCode(dataGridUrl, operationCode, urlList);
    }

    /**
     * 设置删除操作URL
     */
    public void setDelUrl(String url, String title, String message, String exp, String function, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.Del);
        dataGridUrl.setMessage(message);
        dataGridUrl.setExp(exp);
        dataGridUrl.setFunction(function);
        installOperationCode(dataGridUrl, operationCode, urlList);
    }

    /**
     * 设置默认操作URL
     */
    public void setDefUrl(String url, String title, String exp, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.Deff);
        dataGridUrl.setExp(exp);
        installOperationCode(dataGridUrl, operationCode, urlList);

    }

    /**
     * 设置工具条
     *
     * @param height2
     * @param width2
     */
    public void setToolbar(String url, String title, String icon, String exp, String onclick, String function, String operationCode, String width2, String height2) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setType(OptTypeDirection.ToolBar);
        dataGridUrl.setIcon(icon);
        dataGridUrl.setOnclick(onclick);
        dataGridUrl.setExp(exp);
        dataGridUrl.setFunction(function);
        dataGridUrl.setWidth(String.valueOf(width2));
        dataGridUrl.setHeight(String.valueOf(height2));
        installOperationCode(dataGridUrl, operationCode, toolBarList);

    }

    /**
     * 设置自定义函数操作URL
     */
    public void setFunUrl(String title, String exp, String function, String operationCode) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setType(OptTypeDirection.Fun);
        dataGridUrl.setExp(exp);
        dataGridUrl.setFunction(function);
        installOperationCode(dataGridUrl, operationCode, urlList);

    }

    /**
     * 设置自定义函数操作URL
     */
    public void setOpenUrl(String url, String title, String width, String height, String exp, String operationCode, String openModel) {
        DataGridUrl dataGridUrl = new DataGridUrl();
        dataGridUrl.setTitle(title);
        dataGridUrl.setUrl(url);
        dataGridUrl.setWidth(width);
        dataGridUrl.setHeight(height);
        dataGridUrl.setType(OptTypeDirection.valueOf(openModel));
        dataGridUrl.setExp(exp);
        installOperationCode(dataGridUrl, operationCode, urlList);

    }

    /**
     * <b>Summary: </b> setColumn(设置字段)
     *
     * @param title
     * @param field
     * @param width
     */
    public void setColumn(String title, String field, Integer width, String rowspan,
                          String colspan, String align, boolean sortable, boolean checkbox,
                          String formatter, boolean hidden, String replace,
                          String treefield, boolean image, String imageSize,
                          boolean query, String url, String function,
                          String arg, String queryMode, String dictionary, boolean popup,
                          boolean frozenColumn, String extend,
                          String style, String downloadName, boolean isAuto, String extendParams) {
        DataGridColumn dataGridColumn = new DataGridColumn();
        dataGridColumn.setAlign(align);
        dataGridColumn.setCheckbox(checkbox);
        dataGridColumn.setColspan(colspan);
        dataGridColumn.setField(field);
        dataGridColumn.setFormatter(formatter);
        dataGridColumn.setHidden(hidden);
        dataGridColumn.setRowspan(rowspan);
        dataGridColumn.setSortable(sortable);
        dataGridColumn.setTitle(title);
        dataGridColumn.setWidth(width);
        dataGridColumn.setTreeField(treefield);
        dataGridColumn.setImage(image);
        dataGridColumn.setImageSize(imageSize);
        dataGridColumn.setReplace(replace);
        dataGridColumn.setQuery(query);
        dataGridColumn.setUrl(url);
        dataGridColumn.setFunction(function);
        dataGridColumn.setArg(arg);
        dataGridColumn.setQueryMode(queryMode);
        dataGridColumn.setDictionary(dictionary);
        dataGridColumn.setPopup(popup);
        dataGridColumn.setFrozenColumn(frozenColumn);
        dataGridColumn.setExtend(extend);
        dataGridColumn.setStyle(style);
        dataGridColumn.setDownloadName(downloadName);
        dataGridColumn.setAutocomplete(isAuto);
        dataGridColumn.setExtendParams(extendParams);
        columnList.add(dataGridColumn);
        Set<String> operationCodes = (Set<String>) super.pageContext.getRequest().getAttribute(Globals.OPERATION_CODES);
        if (null != operationCodes) {
            for (String MyoperationCode : operationCodes) {
                System.out.println("【DatagridTag】，operationCodes该部分功能未完善");
            /*	if (oConvertUtils.isEmpty(MyoperationCode))
					break;
				systemService = ApplicationContextUtil.getContext().getBean(
							SystemService.class);
				TSOperation operation = systemService.getEntity(TSOperation.class, MyoperationCode);
				if(operation.getOperationCode().equals(field)){
					columnList.remove(dataGridColumn);
				}*/
            }
        }


        if (field != "opt") {
            fields += field + ",";
            if ("group".equals(queryMode)) {
                searchFields += field + "," + field + "_begin," + field + "_end,";
            } else {
                searchFields += field + ",";
            }
        }
        if (replace != null) {
            String[] test = replace.split(",");
            String lang_key = "";
            String text = "";
            String value = "";
            for (String string : test) {
                lang_key = string.substring(0, string.indexOf("_"));
                text += LanguageUtils.getLang(lang_key) + ",";

                value += string.substring(string.indexOf("_") + 1) + ",";
            }
            setColumn(field, text, value);

        }
        if (!StrUtils.isEmpty(dictionary) && (!popup)) {
            if (dictionary.contains(",")) {
                System.out.println("--【DataGridTag】--绘制出错-,getDictionary不能包含','号--");
			/*	String[] dic = dictionary.split(",");
				String text = "";
				String value = "";
				String sql = "select " + dic[1] + " as field," + dic[2]
						+ " as text from " + dic[0];
				List<Map<String, Object>> list = systemService.findForJdbc(sql);
				for (Map<String, Object> map : list){
					text += map.get("text") + ",";
					value += map.get("field") + ",";
				}
				if(list.size()>0)
					setColumn(field, text, value);*/
            } else {
                String text = "";
                String value = "";
                List<TypeView> typeList = SystemContainer.TypeGroupContainer.getTypeMap().get(dictionary.toLowerCase());
                if (typeList != null && !typeList.isEmpty()) {
                    for (TypeView type : typeList) {
                        text += LanguageUtils.doLang(type.getTypeName(), "") + ",";
                        value += type.getTypeCode() + ",";
                    }
                    setColumn(field, text, value);
                }
            }
        }
        if (!StrUtils.isEmpty(style)) {
            String[] temp = style.split(",");
            String text = "";
            String value = "";
            if (temp.length == 1 && temp[0].indexOf("_") == -1) {
                text = temp[0];
            } else {
                for (String string : temp) {
                    text += string.substring(0, string.indexOf("_")) + ",";
                    value += string.substring(string.indexOf("_") + 1) + ",";
                }
            }
            setColumn(field, text, value);
        }
    }

    /**
     * <b>Summary: </b> setColumn(设置字段替换值)
     *
     * @param name
     * @param text
     * @param value
     */
    public void setColumn(String name, String text, String value) {
        ColumnValue columnValue = new ColumnValue();
        columnValue.setName(name);
        columnValue.setText(text);
        columnValue.setValue(value);
        columnValueList.add(columnValue);
    }

    public int doStartTag() throws JspTagException {
        // 清空资源
        urlList.clear();
        toolBarList.clear();
        columnValueList.clear();
        columnStyleList.clear();
        columnList.clear();
        fields = "";
        searchFields = "";
        return EVAL_PAGE;
    }

    public int doEndTag() throws JspTagException {
        title = LanguageUtils.doLang(title, langArg);
        JspWriter out = this.pageContext.getOut();
        String text = "";
        if (style.equals("easyui")) {
            text = end().toString();
            //不显示iocn
//			text=buildTableNoIcon().toString();
        } else {
            text = datatables().toString();
        }
        JspWriterUtils.write(out, text);
        return EVAL_PAGE;
    }

    /**
     * datatables构造方法
     *
     * @return
     */
    public StringBuffer datatables() {
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\">");
        sb.append("$(document).ready(function() {");
        sb.append("var oTable = $(\'#userList\').dataTable({");
        // sb.append(
        // "\"sDom\" : \"<\'row\'<\'span6\'l><\'span6\'f>r>t<\'row\'<\'span6\'i><\'span6\'p>>\",");
        sb.append("\"bProcessing\" : true,");// 当datatable获取数据时候是否显示正在处理提示信息"
        sb.append("\"bPaginate\" : true,"); // 是否分页"
        sb.append("\"sPaginationType\" : \"full_numbers\",");// 分页样式full_numbers,"
        sb.append("\"bFilter\" : true,");// 是否使用内置的过滤功能"
        sb.append("\"bSort\" : true, ");// 排序功能"
        sb.append("\"bAutoWidth\" : true,");// 自动宽度"
        sb.append("\"bLengthChange\" : true,");// 是否允许用户自定义每页显示条数"
        sb.append("\"bInfo\" : true,");// 页脚信息"
        sb.append("\"sAjaxSource\" : \"userController.do?test\",");
        sb.append("\"bServerSide\" : true,");// 指定从服务器端获取数据
        sb.append("\"oLanguage\" : {" + "\"sLengthMenu\" : \" _MENU_ 条记录\"," + "\"sZeroRecords\" : \"没有检索到数据\"," + "\"sInfo\" : \"第 _START_ 至 _END_ 条数据 共 _TOTAL_ 条\"," + "\"sInfoEmtpy\" : \"没有数据\"," + "\"sProcessing\" : \"正在加载数据...\"," + "\"sSearch\" : \"搜索\"," + "\"oPaginate\" : {" + "\"sFirst\" : \"首页\"," + "\"sPrevious\" : \"前页\", " + "\"sNext\" : \"后页\"," + "\"sLast\" : \"尾页\"" + "}" + "},"); // 汉化
        // 获取数据的处理函数 \"data\" : {_dt_json : JSON.stringify(aoData)},
        sb.append("\"fnServerData\" : function(sSource, aoData, fnCallback, oSettings) {");
        // + "\"data\" : {_dt_json : JSON.stringify(aoData)},"
        sb.append("oSettings.jqXHR = $.ajax({" + "\"dataType\" : \'json\'," + "\"type\" : \"POST\"," + "\"url\" : sSource," + "\"data\" : aoData," + "\"success\" : fnCallback" + "});},");
        sb.append("\"aoColumns\" : [ ");
        int i = 0;
        for (DataGridColumn column : columnList) {
            i++;
            sb.append("{");
            sb.append("\"sTitle\":\"" + column.getTitle() + "\"");
            if (column.getField().equals("opt")) {
                sb.append(",\"mData\":\"" + idField + "\"");
                sb.append(",\"sWidth\":\"20%\"");
                sb.append(",\"bSortable\":false");
                sb.append(",\"bSearchable\":false");
                sb.append(",\"mRender\" : function(data, type, rec) {");
                this.getOptUrl(sb);
                sb.append("}");
            } else {
                int colwidth = (column.getWidth() == null) ? column.getTitle().length() * 15 : column.getWidth();
                sb.append(",\"sName\":\"" + column.getField() + "\"");
                sb.append(",\"mDataProp\":\"" + column.getField() + "\"");
                sb.append(",\"mData\":\"" + column.getField() + "\"");
                sb.append(",\"sWidth\":\"" + colwidth + "\"");
                sb.append(",\"bSortable\":" + column.isSortable() + "");
                sb.append(",\"bVisible\":" + !column.isHidden() + "");
                sb.append(",\"bSearchable\":" + column.isQuery() + "");
            }
            sb.append("}");
            if (i < columnList.size())
                sb.append(",");
        }

        sb.append("]" + "});" + "});" + "</script>");
        sb.append("<table width=\"100%\"  class=\"" + style + "\" id=\"" + name + "\" toolbar=\"#" + name + "tb\"></table>");
        return sb;

    }

    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * easyui构造方法
     *
     * @return
     */
    public StringBuffer end() {
        StringBuffer sb = new StringBuffer();
        width = (width == null) ? "auto" : width;
        height = (height == null) ? "auto" : height;
        sb.append("<script type=\"text/javascript\">");
        sb.append("$(function(){  storage=$.localStorage;if(!storage)storage=$.cookieStorage;");
        sb.append(this.getNoAuthOperButton());
        String grid;
        if (treeGrid) {
            grid = "treegrid";
            sb.append("$(\'#" + name + "\').treegrid({");
            sb.append("idField:'id',");
            sb.append("treeField:'text',");
        } else {
            grid = "datagrid";
            sb.append("$(\'#" + name + "\').datagrid({");
            sb.append("idField: '" + idField + "',");
        }
        if (title != null) {
            sb.append("title: \'" + title + "\',");
        }

        if (autoLoadData)
            sb.append("url:\'" + actionUrl + "&field=" + fields + "\',");
        else
            sb.append("url:\'',");
        if (!StrUtils.isEmpty(rowStyler)) {
            sb.append("rowStyler: function(index,row){ return " + rowStyler + "(index,row);},");
        }
        if (!StrUtils.isEmpty(extendParams)) {
            sb.append(extendParams);
        }
        if (fit) {
            sb.append("fit:true,");
        } else {
            sb.append("fit:false,");
        }
        sb.append(StrUtils.replaceAll("loadMsg: \'{0}\',", "{0}", LanguageUtils.getLang("common.data.loading")));
        sb.append("pageSize: " + pageSize + ",");
        sb.append("pagination:" + pagination + ",");
        sb.append("pageList:[" + pageSize * 1 + "," + pageSize * 2 + "," + pageSize * 3 + "],");
        if (!StrUtils.isEmpty(sortName)) {
            sb.append("sortName:'" + sortName + "',");
        }
        sb.append("sortOrder:'" + sortOrder + "',");
        sb.append("rownumbers:true,");
        sb.append("singleSelect:" + !checkbox + ",");
        if (fitColumns) {
            sb.append("fitColumns:true,");
        } else {
            sb.append("fitColumns:false,");
        }
        sb.append("striped:true,showFooter:true,");
        sb.append("frozenColumns:[[");
        this.getField(sb, 0);
        sb.append("]],");

        sb.append("columns:[[");
        this.getField(sb);
        sb.append("]],");
        sb.append("onLoadSuccess:function(data){$(\"#" + name + "\")." + grid + "(\"clearSelections\");");
        if (openFirstNode && treeGrid) {
            sb.append(" if(data==null){");
            sb.append(" var firstNode = $(\'#" + name + "\').treegrid('getRoots')[0];");
            sb.append(" $(\'#" + name + "\').treegrid('expand',firstNode.id)}");
        }
        if (!StrUtils.isEmpty(onLoadSuccess)) {
            sb.append(onLoadSuccess + "(data);");
        }
        sb.append("},");
        if (!StrUtils.isEmpty(onDblClick)) {
            sb.append("onDblClickRow:function(rowIndex,rowData){" + onDblClick + "(rowIndex,rowData);},");
        }
        if (treeGrid) {
            sb.append("onClickRow:function(rowData){");
        } else {
            sb.append("onClickRow:function(rowIndex,rowData){");
        }
        /**行记录赋值*/
        sb.append("rowid=rowData.id;");
        sb.append("gridname=\'" + name + "\';");
        if (!StrUtils.isEmpty(onClick)) {
            if (treeGrid) {
                sb.append("" + onClick + "(rowData);");
            } else {
                sb.append("" + onClick + "(rowIndex,rowData);");
            }
        }
        sb.append("}");
        sb.append("});");
        this.setPager(sb, grid);
        sb.append("try{restoreheader();}catch(ex){}");
        sb.append("});");
        sb.append("function reloadTable(){");
        sb.append("try{");
        sb.append("	$(\'#\'+gridname).datagrid(\'reload\');");
        sb.append("	$(\'#\'+gridname).treegrid(\'reload\');");
        sb.append("}catch(ex){}");
        sb.append("}");
        sb.append("function reload" + name + "(){" + "$(\'#" + name + "\')." + grid + "(\'reload\');" + "}");
        sb.append("function get" + name + "Selected(field){return getSelected(field);}");
        sb.append("function getSelected(field){" + "var row = $(\'#\'+gridname)." + grid + "(\'getSelected\');" + "if(row!=null)" + "{" + "value= row[field];" + "}" + "else" + "{" + "value=\'\';" + "}" + "return value;" + "}");
        sb.append("function get" + name + "Selections(field){" + "var ids = [];" + "var rows = $(\'#" + name + "\')." + grid + "(\'getSelections\');" + "for(var i=0;i<rows.length;i++){" + "ids.push(rows[i][field]);" + "}" + "ids.join(\',\');" + "return ids" + "};");
        sb.append("function getSelectRows(){");
        sb.append("	return $(\'#" + name + "\').datagrid('getChecked');");
        sb.append("}");
        sb.append(" function saveHeader(){");
        sb.append(" var columnsFields =null;var easyextends=false;try{columnsFields = $('#" + name + "').datagrid('getColumns');easyextends=true;");
        sb.append("}catch(e){columnsFields =$('#" + name + "').datagrid('getColumnFields');}");
        sb.append("	var cols = storage.get( '" + name + "hiddenColumns');var init=true;	if(cols){init =false;} " +
                "var hiddencolumns = [];for(var i=0;i< columnsFields.length;i++) {if(easyextends){");
        sb.append("hiddencolumns.push({field:columnsFields[i].field,hidden:columnsFields[i].hidden});}else{");
        sb.append(" var columsDetail = $('#" + name + "').datagrid(\"getColumnOption\", columnsFields[i]); ");
        sb.append("if(init){hiddencolumns.push({field:columsDetail.field,hidden:columsDetail.hidden,visible:(columsDetail.hidden==true?false:true)});}else{");
        sb.append("for(var j=0;j<cols.length;j++){");
        sb.append("		if(cols[j].field==columsDetail.field){");
        sb.append("					hiddencolumns.push({field:columsDetail.field,hidden:columsDetail.hidden,visible:cols[j].visible});");
        sb.append("		}");
        sb.append("}");
        sb.append("}} }");
        sb.append("storage.set( '" + name + "hiddenColumns',JSON.stringify(hiddencolumns));");
        sb.append("}");
        sb.append("function restoreheader(){");
        sb.append("var cols = storage.get( '" + name + "hiddenColumns');if(!cols)return;");
        sb.append("for(var i=0;i<cols.length;i++){");
        sb.append("	try{");
        sb.append("if(cols.visible!=false)$('#" + name + "').datagrid((cols[i].hidden==true?'hideColumn':'showColumn'),cols[i].field);");
        sb.append("}catch(e){");
        sb.append("}");
        sb.append("}");
        sb.append("}");
        sb.append("function resetheader(){");
        sb.append("var cols = storage.get( '" + name + "hiddenColumns');if(!cols)return;");
        sb.append("for(var i=0;i<cols.length;i++){");
        sb.append("	try{");
        sb.append("  $('#" + name + "').datagrid((cols.visible==false?'hideColumn':'showColumn'),cols[i].field);");
        sb.append("}catch(e){");
        sb.append("}");
        sb.append("}");
        sb.append("}");
        if (columnList.size() > 0) {
            sb.append("function " + name + "search(){");
            sb.append("var queryParams=$(\'#" + name + "\').datagrid('options').queryParams;");
            sb.append("$(\'#" + name + "tb\').find('*').each(function(){queryParams[$(this).attr('name')]=$(this).val();});");
            sb.append("$(\'#" + name + "\')." + grid + "({url:'" + actionUrl + "&field=" + searchFields + "',pageNumber:1});" + "}");

            //高级查询执行方法
            sb.append("function dosearch(params){");
            sb.append("var jsonparams=$.parseJSON(params);");
            sb.append("$(\'#" + name + "\')." + grid + "({url:'" + actionUrl + "&field=" + searchFields + "',queryParams:jsonparams});" + "}");

            //searchbox框执行方法
            searchboxFun(sb, grid);
            //生成重置按钮功能js

            //回车事件
            sb.append("function EnterPress(e){");
            sb.append("var e = e || window.event;");
            sb.append("if(e.keyCode == 13){ ");
            sb.append(name + "search();");
            sb.append("}}");

            sb.append("function searchReset(name){");
            sb.append(" $(\"#\"+name+\"tb\").find(\":input\").val(\"\");");
            String func = name.trim() + "search();";
            sb.append(func);
            sb.append("}");
        }
        sb.append("</script>");
        sb.append("<table width=\"100%\"   id=\"" + name + "\" toolbar=\"#" + name + "tb\"></table>");
        sb.append("<div id=\"" + name + "tb\" style=\"padding:3px; height: auto\">");
        if (hasQueryColum(columnList)) {
            sb.append("<div name=\"searchColums\">");
            //-----longjb1 增加用于高级查询的参数项
            sb.append("<input  id=\"_sqlbuilder\" name=\"sqlbuilder\"   type=\"hidden\" />");
            //如果表单是组合查询
            if ("group".equals(getQueryMode())) {
                for (DataGridColumn col : columnList) {
                    if (col.isQuery()) {
                        sb.append("<span style=\"display:-moz-inline-box;display:inline-block;\">");
                        sb.append("<span style=\"vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 80px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; \" title=\"" + col.getTitle() + "\">" + col.getTitle() + "：</span>");
                        if ("single".equals(col.getQueryMode())) {
                            if (!StrUtils.isEmpty(col.getReplace())) {
                                sb.append("<select name=\"" + col.getField().replaceAll("_", "\\.") + "\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                sb.append(StrUtils.replaceAll("<option value =\"\" >{0}</option>", "{0}", LanguageContainer.getLang("common.please.select")));
                                String[] test = col.getReplace().split(",");
                                String text = "";
                                String value = "";
                                for (String string : test) {
                                    String lang_key = string.split("_")[0];
                                    text = LanguageContainer.getLang(lang_key);
                                    value = string.split("_")[1];
                                    sb.append("<option value =\"" + value + "\">" + text + "</option>");
                                }
                                sb.append("</select>");
                            } else if (!StrUtils.isEmpty(col.getDictionary())) {


                                if (col.getDictionary().contains(",") && col.isPopup()) {
                                    String[] dic = col.getDictionary().split(",");
                                    //	<input type="text" name="order_code"  style="width: 100px"  class="searchbox-inputtext" value="" onClick="inputClick(this,'account','user_msg');" />
                                    sb.append("<input type=\"text\" name=\"" + col.getField().replaceAll("_", "\\.") + "\" style=\"width: 100px\" class=\"searchbox-inputtext\" value=\"\" onClick=\"inputClick(this,'" + dic[1] + "','" + dic[0] + "');\" /> ");
                                } else if (col.getDictionary().contains(",") && (!col.isPopup())) {
                                    System.out.println("--【DataGridTag】--绘制出错-,getDictionary不能包含','号--");
                                    String[] dic = col.getDictionary().split(",");
                                    String sql = "select " + dic[1] + " as field," + dic[2]
                                            + " as text from " + dic[0];
                                    //TODO
//									List<Map<String, Object>> list = systemService.queryForListMap(sql);
                                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                                    sb.append("<select name=\"" + col.getField().replaceAll("_", "\\.") + "\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                    sb.append(StrUtils.replaceAll("<option value =\"\" >{0}</option>", "{0}", LanguageContainer.getLang("common.please.select")));
                                    for (Map<String, Object> map : list) {
                                        sb.append(" <option value=\"" + map.get("field") + "\">");
                                        sb.append(map.get("text"));
                                        sb.append(" </option>");
                                    }
                                    sb.append("</select>");
                                } else {
                                    Map<String, List<TypeView>> typedatas = SystemContainer.TypeGroupContainer.getTypeMap();
                                    if (!typedatas.isEmpty()){
                                        List<TypeView> types = typedatas.get(col.getDictionary().toLowerCase());
                                        sb.append("<select name=\"" + col.getField().replaceAll("_", "\\.") + "\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                        sb.append(StrUtils.replaceAll("<option value =\"\" >{0}</option>", "{0}", LanguageContainer.getLang("common.please.select")));
                                        for (TypeView type : types) {
                                            sb.append(" <option value=\"" + type.getTypeCode() + "\">");
                                            sb.append(LanguageContainer.getLang(type.getTypeName()));
                                            sb.append(" </option>");
                                        }
                                        sb.append("</select>");
                                    }

                                }

                            } else if (col.isAutocomplete()) {
                                sb.append(getAutoSpan(col.getField().replaceAll("_", "\\."), extendAttribute(col.getExtend())));
                            } else {

                                sb.append("<input class=\"inuptxt\" onkeypress=\"EnterPress(event)\" onkeydown=\"EnterPress()\"  type=\"text\" name=\"" + col.getField().replaceAll("_", "\\.") + "\"  " + extendAttribute(col.getExtend()) + " style=\"width: 100px\" />");
                            }
                        } else if ("group".equals(col.getQueryMode())) {
                            sb.append("<input class=\"inuptxt\" type=\"text\" name=\"" + col.getField() + "_begin\"   " + extendAttribute(col.getExtend()) + "/>");
                            sb.append("<span style=\"display:-moz-inline-box;display:inline-block;width: 8px;text-align:right;\">~</span>");
                            sb.append("<input class=\"inuptxt\" type=\"text\" name=\"" + col.getField() + "_end\"  " + extendAttribute(col.getExtend()) + "/>");
                        }
                        sb.append("</span>");
                    }
                }
            }
            sb.append("</div>");
        }
        if (toolBarList.size() == 0 && !hasQueryColum(columnList)) {
            sb.append("<div style=\"height:0px;\" >");
        } else {
            sb.append("<div style=\"height:30px;\" class=\"datagrid-toolbar\">");
        }
        sb.append("<span style=\"float:left;\" >");
        if (toolBarList.size() > 0) {
            for (DataGridUrl toolBar : toolBarList) {
                sb.append("<a href=\"#\" class=\"easyui-linkbutton\" plain=\"true\" icon=\"" + toolBar.getIcon() + "\" ");
                if (!StrUtils.isEmpty(toolBar.getOnclick())) {
                    sb.append("onclick=" + toolBar.getOnclick() + "");
                } else {
                    sb.append("onclick=\"" + toolBar.getFunction() + "(");
                    if (!toolBar.getFunction().equals("doSubmit")) {
                        sb.append("\'" + toolBar.getTitle() + "\',");
                    }
                    String width = toolBar.getWidth().contains("%") ? "'" + toolBar.getWidth() + "'" : toolBar.getWidth();
                    String height = toolBar.getHeight().contains("%") ? "'" + toolBar.getHeight() + "'" : toolBar.getHeight();
                    sb.append("\'" + toolBar.getUrl() + "\',\'" + name + "\'," + width + "," + height + ")\"");
                }
                sb.append(">" + toolBar.getTitle() + "</a>");
            }
        }
        sb.append("</span>");
        if ("group".equals(getQueryMode()) && hasQueryColum(columnList)) {//如果表单是组合查询
            sb.append("<span style=\"float:right\">");
            sb.append("<a href=\"#\" class=\"easyui-linkbutton\" iconCls=\"icon-search\" onclick=\"" + name + StrUtils.replaceAll("search()\">{0}</a>", "{0}", LanguageUtils.getLang("common.query")));
            sb.append("<a href=\"#\" class=\"easyui-linkbutton\" iconCls=\"icon-reload\" onclick=\"searchReset('" + name + StrUtils.replaceAll("')\">{0}</a>", "{0}", LanguageUtils.getLang("common.reset")));
            if (queryBuilder) {
                sb.append("<a href=\"#\" class=\"easyui-linkbutton\" iconCls=\"icon-search\" onclick=\"queryBuilder('" + StrUtils.replaceAll("')\">{0}</a>", "{0}", LanguageUtils.getLang("common.querybuilder")));
            }
            sb.append("</span>");
        } else if ("single".equals(getQueryMode()) && hasQueryColum(columnList)) {//如果表单是单查询
            sb.append("<span style=\"float:right\">");
            sb.append("<input id=\"" + name + "searchbox\" class=\"easyui-searchbox\"  data-options=\"searcher:" + name + StrUtils.replaceAll("searchbox,prompt:\'{0}\',menu:\'#", "{0}", LanguageUtils.getLang("common.please.input.keyword")) + name + "mm\'\"></input>");
            sb.append("<div id=\"" + name + "mm\" style=\"width:120px\">");
            for (DataGridColumn col : columnList) {
                if (col.isQuery()) {
                    sb.append("<div data-options=\"name:\'" + col.getField().replaceAll("_", "\\.") + "\',iconCls:\'icon-ok\' " + extendAttribute(col.getExtend()) + " \">" + col.getTitle() + "</div>  ");
                }
            }
            sb.append("</div>");
            sb.append("</span>");
        }
        sb.append("</div>");
        if (queryBuilder) {
            addQueryBuilder(sb, "button");
//			addQueryBuilder(sb,"easyui-linkbutton");
        }
        return sb;
    }

    /**
     * 生成扩展属性
     *
     * @param field
     * @return
     */
    private String extendAttribute(String field) {
        if (StrUtils.isEmpty(field)) {
            return "";
        }
        field = dealSyscode(field, 1);
        StringBuilder re = new StringBuilder();
        try {
           JSONObject obj = JSONObject.fromObject(field);
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                JSONObject nextObj = ((JSONObject) obj.get(key));
                Iterator itvalue = nextObj.keys();
                re.append(key + "=" + "\"");
                if (nextObj.size() <= 1) {
                    String onlykey = String.valueOf(itvalue.next());
                    if ("value".equals(onlykey)) {
                        re.append(nextObj.get(onlykey) + "");
                    } else {
                        re.append(onlykey + ":" + nextObj.get(onlykey) + "");
                    }
                } else {
                    while (itvalue.hasNext()) {
                        String multkey = String.valueOf(itvalue.next());
                        String multvalue = nextObj.getString(multkey);
                        re.append(multkey + ":" + multvalue + ",");
                    }
                    re.deleteCharAt(re.length() - 1);
                }
                re.append("\" ");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "";
        }
        return dealSyscode(re.toString(), 2);
    }

    /**
     * 处理否含有json转换中的保留字
     *
     * @param field
     * @param flag  1:转换 2:还原
     * @return
     */
    private String dealSyscode(String field, int flag) {
        String change = field;
        Iterator it = code.keySet().iterator();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            String value = String.valueOf(code.get(key));
            if (flag == 1) {
                change = field.replaceAll(key, value);
            } else if (flag == 2) {
                change = field.replaceAll(value, key);
            }
        }
        return change;
    }

    /**
     * 判断是否存在查询字段
     *
     * @return hasQuery true表示有查询字段,false表示没有
     */
    protected boolean hasQueryColum(List<DataGridColumn> columnList) {
        boolean hasQuery = false;
        for (DataGridColumn col : columnList) {
            if (col.isQuery()) {
                hasQuery = true;
            }
        }
        return hasQuery;
    }

    /**
     * 拼装操作地址
     *
     * @param sb
     */
    protected void getOptUrl(StringBuffer sb) {
        //注：操作列表会带入合计列中去，故加此判断
        sb.append("if(!rec.id){return '';}");
        List<DataGridUrl> list = urlList;
        sb.append("var href='';");
        for (DataGridUrl dataGridUrl : list) {
            String url = dataGridUrl.getUrl();
            MessageFormat formatter = new MessageFormat("");
            if (dataGridUrl.getValue() != null) {
                String[] testvalue = dataGridUrl.getValue().split(",");
                List value = new ArrayList<Object>();
                for (String string : testvalue) {
                    value.add("\"+rec." + string + " +\"");
                }
                url = formatter.format(url, value.toArray());
            }
            if (url != null && dataGridUrl.getValue() == null) {

                url = formatUrl(url);
            }
            String exp = dataGridUrl.getExp();// 判断显示表达式
            if (!StrUtils.isEmpty(exp)) {
                String[] ShowbyFields = exp.split("&&");
                for (String ShowbyField : ShowbyFields) {
                    int beginIndex = ShowbyField.indexOf("#");
                    int endIndex = ShowbyField.lastIndexOf("#");
                    String exptype = ShowbyField.substring(beginIndex + 1, endIndex);// 表达式类型
                    String field = ShowbyField.substring(0, beginIndex);// 判断显示依据字段
                    String[] values = ShowbyField.substring(endIndex + 1, ShowbyField.length()).split(",");// 传入字段值
                    String value = "";
                    for (int i = 0; i < values.length; i++) {
                        value += "'" + "" + values[i] + "" + "'";
                        if (i < values.length - 1) {
                            value += ",";
                        }
                    }
                    if ("eq".equals(exptype)) {
                        sb.append("if($.inArray(rec." + field + ",[" + value + "])>=0){");
                    }
                    if ("ne".equals(exptype)) {
                        sb.append("if($.inArray(rec." + field + ",[" + value + "])<0){");
                    }
                    if ("empty".equals(exptype) && value.equals("'true'")) {
                        sb.append("if(rec." + field + "==''){");
                    }
                    if ("empty".equals(exptype) && value.equals("'false'")) {
                        sb.append("if(rec." + field + "!=''){");
                    }
                }
            }

            if (OptTypeDirection.Confirm.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=confirm(\'" + url + "\',\'" + dataGridUrl.getMessage() + "\',\'" + name + "\')> \";");
            }
            if (OptTypeDirection.Del.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=delObj(\'" + url + "\',\'" + name + "\')>\";");
            }
            if (OptTypeDirection.Fun.equals(dataGridUrl.getType())) {
                String name = TagUtil.getFunction(dataGridUrl.getFunction());
                String parmars = TagUtil.getFunParams(dataGridUrl.getFunction());
                sb.append("href+=\"[<a href=\'#\' onclick=" + name + "(" + parmars + ")>\";");
            }
            if (OptTypeDirection.OpenWin.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=openwindow('" + dataGridUrl.getTitle() + "','" + url + "','" + name + "'," + dataGridUrl.getWidth() + "," + dataGridUrl.getHeight() + ")>\";");
            }
            if (OptTypeDirection.Deff.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'" + url + "' title=\'" + dataGridUrl.getTitle() + "\'>\";");
            }
            if (OptTypeDirection.OpenTab.equals(dataGridUrl.getType())) {
                sb.append("href+=\"[<a href=\'#\' onclick=addOneTab('" + dataGridUrl.getTitle() + "','" + url + "')>\";");
            }
            sb.append("href+=\"" + dataGridUrl.getTitle() + "</a>]\";");

            if (!StrUtils.isEmpty(exp)) {
                for (int i = 0; i < exp.split("&&").length; i++) {
                    sb.append("}");
                }

            }
        }
        sb.append("return href;");
    }

    /**
     * 列自定义函数
     *
     * @param sb
     * @param column
     */
    protected void getFun(StringBuffer sb, DataGridColumn column) {
        String url = column.getUrl();
        url = formatUrl(url);
        sb.append("var href=\"<a style=\'color:red\' href=\'#\' onclick=" + column.getFunction() + "('" + column.getTitle() + "','" + url + "')>\";");
        sb.append("return href+value+\'</a>\';");

    }

    /**
     * 格式化URL
     *
     * @return
     */
    protected String formatUrl(String url) {
        MessageFormat formatter = new MessageFormat("");
        String parurlvalue = "";
        if (url.indexOf("&") >= 0) {
            String beforeurl = url.substring(0, url.indexOf("&"));// 截取请求地址
            String parurl = url.substring(url.indexOf("&") + 1, url.length());// 截取参数
            String[] pras = parurl.split("&");
            List value = new ArrayList<Object>();
            int j = 0;
            for (int i = 0; i < pras.length; i++) {
                if (pras[i].indexOf("{") >= 0 || pras[i].indexOf("#") >= 0) {
                    String field = pras[i].substring(pras[i].indexOf("{") + 1, pras[i].lastIndexOf("}"));
                    parurlvalue += "&" + pras[i].replace("{" + field + "}", "{" + j + "}");
                    value.add("\"+rec." + field + " +\"");
                    j++;
                } else {
                    parurlvalue += "&" + pras[i];
                }
            }
            url = formatter.format(beforeurl + parurlvalue, value.toArray());
        }
        return url;

    }

    /**
     * 拼接字段  普通列
     *
     * @param sb
     */
    protected void getField(StringBuffer sb) {
        getField(sb, 1);
    }

    /**
     * 拼接字段
     *
     * @param sb
     * @frozen 0 冰冻列    1 普通列
     */
    protected void getField(StringBuffer sb, int frozen) {
        // 复选框
        if (checkbox && frozen == 0) {
            sb.append("{field:\'ck\',checkbox:\'true\'},");
        }
        int i = 0;
        for (DataGridColumn column : columnList) {
            i++;
            if ((column.isFrozenColumn() && frozen == 0) || (!column.isFrozenColumn() && frozen == 1)) {
                String field;
                if (treeGrid) {
                    field = column.getTreeField();
                } else {
                    field = column.getField();
                }
                sb.append("{field:\'" + field + "\',title:\'" + column.getTitle() + "\'");
                if (column.getWidth() != null) {
                    sb.append(",width:" + column.getWidth());
                }
                if (column.getAlign() != null) {
                    sb.append(",align:\'" + column.getAlign() + "\'");
                }
                if (!StrUtils.isEmpty(column.getExtendParams())) {
                    sb.append("," + column.getExtendParams().substring(0,
                            column.getExtendParams().length() - 1));
                }
                // 隐藏字段
                if (column.isHidden()) {
                    sb.append(",hidden:true");
                }
                if (!treeGrid) {
                    // 字段排序
                    if ((column.isSortable()) && (field.indexOf("_") <= 0 && field != "opt")) {
                        sb.append(",sortable:" + column.isSortable() + "");
                    }
                }
                // 显示图片
			/*if (column.isImage()) {
				sb.append(",formatter:function(value,rec,index){");
				sb.append(" return '<img border=\"0\" src=\"'+value+'\"/>';}");
			}*/

                // 自定义链接
                if (column.getUrl() != null) {
                    sb.append(",formatter:function(value,rec,index){");
                    this.getFun(sb, column);
                    sb.append("}");
                }

                //如果定义的是图片
                if (column.isImage()) {
                    sb.append(",formatter:function(value,rec,index){");
                    if (column.getImageSize() != null) {//自定义显示图片的大小
                        String[] tld = column.getImageSize().split(",");
                        sb.append("var href=\"<a style=\'color:red\' href=\'#\'  onclick=" + column.getFunction() + "('" + column.getTitle() + "','\"+value+\"','\"+rec.id+\"')>\";");
                        sb.append("return href+'<img width=\"" + tld[0] + "\" height=\"" + tld[1] + "\" border=\"0\" src=\"'+value+'\"/>'+\'</a>\';");
                        tld = null;
                    } else {
                        sb.append("var href=\"<a style=\'color:red\' href=\'#\'  onclick=" + column.getFunction() + "('" + column.getTitle() + "','\"+value+\"','\"+rec.id+\"')>\";");
                        sb.append("return href+'<img border=\"0\" src=\"'+value+'\"/>'+\'</a>\';");
                    }
                    sb.append("}");
                }

                if (column.getDownloadName() != null) {
                    sb.append(",formatter:function(value,rec,index){");
                    sb.append(" return '<a target=\"_blank\" href=\"'+value+'\">"
                            + column.getDownloadName() + "</a>';}");
                }

                if (column.getFormatter() != null) {
                    sb.append(",formatter:function(value,rec,index){");
                    sb.append(" return new Date().format('" + column.getFormatter() + "',value);}");
                }
                // 加入操作
                if (column.getField().equals("opt")) {
                    sb.append(",formatter:function(value,rec,index){");
                    // sb.append("return \"");
                    this.getOptUrl(sb);
                    sb.append("}");
                }
                // 值替換
                if (columnValueList.size() > 0 && !column.getField().equals("opt")) {
                    String testString = "";
                    for (ColumnValue columnValue : columnValueList) {
                        if (columnValue.getName().equals(column.getField())) {
                            String[] value = columnValue.getValue().split(",");
                            String[] text = columnValue.getText().split(",");
                            sb.append(",formatter:function(value,rec,index){");
                            sb.append("var valArray = value.split(\",\");");
                            sb.append("if(valArray.length > 1){");
                            sb.append("var checkboxValue = \"\";");
                            sb.append("for(var k=0; k<valArray.length; k++){");
                            for (int j = 0; j < value.length; j++) {
                                sb.append("if(valArray[k] == '" + value[j] + "'){ checkboxValue = checkboxValue + \'" + text[j] + "\' + ','}");
                            }
                            sb.append("}");
                            sb.append("return checkboxValue.substring(0,checkboxValue.length-1);");
                            sb.append("}");
                            sb.append("else{");
                            for (int j = 0; j < value.length; j++) {
                                testString += "if(value=='" + value[j] + "'){return \'" + text[j] + "\'}";
                            }
                            sb.append(testString);
                            sb.append("else{return value}");
                            sb.append("}");
                            sb.append("}");
                        }
                    }

                }
                // 背景设置
                if (columnStyleList.size() > 0 && !column.getField().equals("opt")) {
                    String testString = "";
                    for (ColumnValue columnValue : columnStyleList) {
                        if (columnValue.getName().equals(column.getField())) {
                            String[] value = columnValue.getValue().split(",");
                            String[] text = columnValue.getText().split(",");
                            sb.append(",styler:function(value,rec,index){");
                            if ((value.length == 0 || StrUtils.isEmpty(value[0])) && text.length == 1) {
                                if (text[0].indexOf("(") > -1) {
                                    testString = " return \'" + text[0].replace("(", "(value,rec,index") + "\'";
                                } else {
                                    testString = " return \'" + text[0] + "\'";
                                }
                            } else {
                                for (int j = 0; j < value.length; j++) {
                                    testString += "if(value=='" + value[j] + "'){return \'" + text[j] + "\'}";
                                }
                            }
                            sb.append(testString);
                            sb.append("}");
                        }
                    }

                }
                sb.append("}");
                // 去除末尾,
                if (i < columnList.size()) {
                    sb.append(",");
                }
            }
        }
    }

    /**
     * 设置分页条信息
     *
     * @param sb
     */
    protected void setPager(StringBuffer sb, String grid) {
        sb.append("$(\'#" + name + "\')." + grid + "(\'getPager\').pagination({");
        sb.append("beforePageText:\'\'," + "afterPageText:\'/{pages}\',");
        if (showText) {
            sb.append("displayMsg:\'{from}-{to}" + LanguageUtils.getLang("common.total") + " {total}" + LanguageUtils.getLang("common.item") + "\',");
        } else {
            sb.append("displayMsg:\'\',");
        }
        if (showPageList == true) {
            sb.append("showPageList:true,");
        } else {
            sb.append("showPageList:false,");
        }
        sb.append("showRefresh:" + showRefresh + "");
        sb.append("});");// end getPager
        sb.append("$(\'#" + name + "\')." + grid + "(\'getPager\').pagination({");
        sb.append("onBeforeRefresh:function(pageNumber, pageSize){ $(this).pagination(\'loading\');$(this).pagination(\'loaded\'); }");
        sb.append("});");
    }

    //列表查询框函数
    protected void searchboxFun(StringBuffer sb, String grid) {
        sb.append("function " + name + "searchbox(value,name){");
        sb.append("var queryParams=$(\'#" + name + "\').datagrid('options').queryParams;");
        sb.append("queryParams[name]=value;queryParams.searchfield=name;$(\'#" + name + "\')." + grid + "(\'reload\');}");
        sb.append("$(\'#" + name + "searchbox\').searchbox({");
        sb.append("searcher:function(value,name){");
        sb.append("" + name + "searchbox(value,name);");
        sb.append("},");
        sb.append("menu:\'#" + name + "mm\',");
        sb.append(StrUtils.replaceAll("prompt:\'{0}\'", "{0}", LanguageUtils.getLang("common.please.input.query.keyword")));
        sb.append("});");
    }

    public String getNoAuthOperButton() {
        StringBuffer sb = new StringBuffer();
        if (!Globals.AUTHORITY_BUTTON_CHECK) {
        } else {
            Set<String> operationCodes = (Set<String>) super.pageContext.getRequest().getAttribute(Globals.OPERATION_CODES);
            if (null != operationCodes) {
                for (String MyoperationCode : operationCodes) {
                    System.out.println("Operation code未完善");
					/*
					if (oConvertUtils.isEmpty(MyoperationCode))
						break;
					systemService = ApplicationContextUtil.getContext().getBean(
								SystemService.class);
					TSOperation operation = systemService.getEntity(TSOperation.class, MyoperationCode);
					if (operation.getOperationCode().startsWith(".") || operation.getOperationCode().startsWith("#")){
						if (operation.getOperationType().intValue()==Globals.OPERATION_TYPE_HIDE){
							//out.append("$(\""+name+"\").find(\"#"+operation.getOperationCode().replaceAll(" ", "")+"\").hide();");
							sb.append("$(\""+operation.getOperationCode().replaceAll(" ", "")+"\").hide();");
						}else {
							//out.append("$(\""+name+"\").find(\"#"+operation.getOperationCode().replaceAll(" ", "")+"\").find(\":input\").attr(\"disabled\",\"disabled\");");
							sb.append("$(\""+operation.getOperationCode().replaceAll(" ", "")+"\").attr(\"disabled\",\"disabled\");");
							sb.append("$(\""+operation.getOperationCode().replaceAll(" ", "")+"\").find(\":input\").attr(\"disabled\",\"disabled\");");
						}
					}
				*/
                }
            }

        }
        return sb.toString();
    }

    /**
     * 描述：组装菜单按钮操作权限
     * dateGridUrl：url
     * operationCode：操作码
     * optList： 操作列表
     *
     * @version 1.0
     */
    private void installOperationCode(DataGridUrl dataGridUrl, String operationCode, List optList) {
        if (Globals.AUTHORITY_IS_OPEN) {
            optList.add(dataGridUrl);
        } else if (!StrUtils.isEmpty(operationCode)) {
            Set<String> operationCodes = (Set<String>) super.pageContext.getRequest().getAttribute(Globals.OPERATION_CODES);
            if (null != operationCodes) {
                List<String> operationCodesStr = new ArrayList();
			/*	for (String MyoperationCode : operationCodes) {
					if (oConvertUtils.isEmpty(MyoperationCode))
						break;
					systemService = ApplicationContextUtil.getContext().getBean(
								SystemService.class);
					TSOperation operation = systemService.find(TSOperation.class, MyoperationCode);
					operationCodesStr.add(operation.getOperationCode());

				}*/
                if (!operationCodesStr.contains(operationCode)) {
                    optList.add(dataGridUrl);
                }
            }
        } else {
            optList.add(dataGridUrl);
        }
    }

    /**
     * 获取自动补全的panel
     *
     * @param filed
     * @return
     * @author JueYue
     */
    private String getAutoSpan(String filed, String extend) {
        String id = filed.replaceAll("\\.", "_");
        StringBuffer nsb = new StringBuffer();
        nsb.append("<script type=\"text/javascript\">");
        nsb.append("$(document).ready(function() {")
                .append("$(\"#" + getEntityName() + "_" + id + "\").autocomplete(\"systemController.do?getAutoList\",{")
                .append("max: 5,minChars: 2,width: 200,scrollHeight: 100,matchContains: true,autoFill: false,extraParams:{")
                .append("featureClass : \"P\",style : \"full\",	maxRows : 10,labelField : \"" + filed + "\",valueField : \"" + filed + "\",")
                .append("searchField : \"" + filed + "\",entityName : \"" + getEntityName() + "\",trem: function(){return $(\"#" + getEntityName() + "_" + id + "\").val();}}");
        nsb.append(",parse:function(data){return jeecgAutoParse.call(this,data);}");
        nsb.append(",formatItem:function(row, i, max){return row['" + filed + "'];} ");
        nsb.append("}).result(function (event, row, formatted) {");
        nsb.append("$(\"#" + getEntityName() + "_" + id + "\").val(row['" + filed + "']);}); });")
                .append("</script>")
                .append("<input class=\"inuptxt\"  type=\"text\" id=\"" + getEntityName() + "_" + id + "\" name=\"" + filed + "\" datatype=\"*\" " + extend + StrUtils.replaceAll(" nullmsg=\"\" errormsg=\"{0}\"/>", "{0}", LanguageUtils.getLang("input.error")));
        return nsb.toString();
    }

    /**
     * 获取实体类名称,没有这根据规则设置
     *
     * @return
     */
    private String getEntityName() {
        if (StrUtils.isEmpty(entityName)) {
            entityName = actionUrl.substring(0, actionUrl.indexOf("Controller"));
            entityName = (entityName.charAt(0) + "").toUpperCase() + entityName.substring(1) + "Entity";
        }
        return entityName;
    }

    public boolean isFitColumns() {
        return fitColumns;
    }

    public void setFitColumns(boolean fitColumns) {
        this.fitColumns = fitColumns;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    public boolean isAutoLoadData() {
        return autoLoadData;
    }

    public void setAutoLoadData(boolean autoLoadData) {
        this.autoLoadData = autoLoadData;
    }

    public void setOpenFirstNode(boolean openFirstNode) {
        this.openFirstNode = openFirstNode;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setRowStyler(String rowStyler) {
        this.rowStyler = rowStyler;
    }

    public void setIconExtendParams(String extendParams) {
        this.extendParams = extendParams;
    }

    public void setLangArg(String langArg) {
        this.langArg = langArg;
    }


    private void appendLine(StringBuffer sb, String str) {
        String format = "\r\n"; //调试  格式化
        sb.append(str).append(format);
    }

    /**
     * TODO 语言做成多语翻译，保留历史查询记录
     *
     * @param sb
     */
    private void addQueryBuilder(StringBuffer sb, String buttonSytle) {

        appendLine(sb, "<div style=\"position:relative;overflow:auto;\">");
        appendLine(sb, "<div id=\"" + name + "_qbwin\" class=\"easyui-window\" data-options=\"closed:true,title:'高级查询构造器'\" style=\"width:600px;height:370px;padding:0px\">");
        appendLine(sb, "	<div class=\"easyui-layout\" data-options=\"fit:true\">");
        appendLine(sb, "		<div data-options=\"region:'east',split:true\" style=\"width:130px\"><div class=\"easyui-accordion\" style=\"width:120px;height:300px;\">");
        appendLine(sb, "<div title=\"查询历史\" data-options=\"iconCls:'icon-search'\" style=\"padding:0px;\">");
        appendLine(
                sb,
                "	<ul id=\""
                        + name
                        + "tt\" class=\"easyui-tree\" data-options=\"onClick:function(node){");
        appendLine(sb, "historyQuery( node.id);  ");
        appendLine(sb, "},ondbClick: function(node){");
        appendLine(sb, "$(this).tree('beginEdit',node.target);");
        appendLine(sb, "},onContextMenu: function(e,node){");
        appendLine(sb, "		e.preventDefault();");
        appendLine(sb, "		$(this).tree('select',node.target);");
        appendLine(sb, "		$('#" + name + "mmTree').menu('show',{");
        appendLine(sb, "			left: e.pageX,");
        appendLine(sb, "			top: e.pageY");
        appendLine(sb, "		});");
        appendLine(sb, "	},  ");
        appendLine(sb, " onAfterEdit:function(node){  ");
        appendLine(sb,
                "    if(node.text!=''){ " + name + "his[node.id].name=node.text; saveHistory();}	}");
        appendLine(sb, "\">");
        appendLine(sb, "	</ul>");
        appendLine(sb, "</div>");
        appendLine(sb, "</div></div>");
        appendLine(sb, "		<div data-options=\"region:'center'\" style=\"padding:0px;\">");
        appendLine(sb, "			<table id=\"" + name + "tg\" class=\"easyui-treegrid\" title=\"查询条件编辑\" style=\"width:450px;height:300px;\"");
        appendLine(sb, "		data-options=\"");
        appendLine(sb, "			iconCls: 'icon-ok',");
        appendLine(sb, "			rownumbers: true,");
        appendLine(sb, "			animate: true,");
        appendLine(sb, "			fitColumns: true,");
        appendLine(sb, "			//url: 'sqlbuilder.json',//可以预加载条件\r\n");
        appendLine(sb, "			method: 'get',");
        appendLine(sb, "			idField: 'id',");
        appendLine(sb, "autoEditing: true,  ");
        appendLine(sb, "extEditing: false, ");
        appendLine(sb, "singleEditing: false ,");
        appendLine(sb, "			treeField: 'field',toolbar:toolbar,onContextMenu: onContextMenu");
        appendLine(sb, "		\">");
        appendLine(sb, "<thead>");
        appendLine(sb, "	<tr>");
        sb
                .append("	<th data-options=\"field:'relation',width:18,formatter:function(value,row){");
        appendLine(sb, "				return value=='and'?'并且':'或者';");
        appendLine(sb, "			},editor:{");
        appendLine(sb, "				type:'combobox',");
        appendLine(sb, "				options:{");
        appendLine(sb, "				valueField:'relationId',");
        appendLine(sb, "						textField:'relationName',");
        appendLine(sb, "						data:  ");
        appendLine(sb, "						[  ");
        appendLine(sb, "						{'relationId':'and','relationName':'并且'},  ");
        appendLine(sb, "						{'relationId':'or','relationName':'或者'}  ");
        appendLine(sb, "						],  ");
        appendLine(sb, "						required:true");
        appendLine(sb, "					}}\">关系</th>");
        sb
                .append("		<th data-options=\"field:'field',width:30,formatter:function(value,row){");
        appendLine(sb, "			var data= ");
        StringBuffer fieldArray = new StringBuffer();
        fieldArray.append("	[  ");
        for (int i = 0; i < columnList.size(); i++) {
            DataGridColumn col = columnList.get(i);
            if ("opt".equals(col.getField())) continue;//忽略操作虚拟字段
            fieldArray.append("	{'fieldId':'" + getDBFieldName(col.getField()) + "','fieldName':'" + col.getTitle() + "'}");
            if (i < columnList.size() - 1) {
                fieldArray.append(",");
            }
        }
//		appendLine(sb,"				{'fieldId':'office_Phone','fieldName':'办公电话'},");
        fieldArray.append("]");
        sb.append(fieldArray).append(";");
        appendLine(sb, "for(var i=0;i<data.length;i++){");
        appendLine(sb, "if(value == data[i]['fieldId']){");
        appendLine(sb, "return data[i]['fieldName'];");
        appendLine(sb, "}");
        appendLine(sb, "}");
        appendLine(sb, "return value;");
        appendLine(sb, "},editor:{");
        appendLine(sb, "type:'combobox',");
        appendLine(sb, "	options:{");
        appendLine(sb, "valueField:'fieldId',");
        appendLine(sb, "textField:'fieldName',");
        appendLine(sb, "data:  ");
        sb.append(fieldArray);
        appendLine(sb, " , ");
        appendLine(sb, "							required:true");
        appendLine(sb, "				}}\">字段</th>");
        sb.append("<th data-options=\"field:'condition',width:20,align:'right',formatter:function(value,row){");
        appendLine(sb, "							var data=  ");
        appendLine(sb, "					[  ");
        Map<String, List<TypeView>> typedatas = SystemContainer.TypeGroupContainer.getTypeMap();
        List<TypeView> types = typedatas.get("rulecon");
        buildCheckType(sb, types);
        appendLine(sb, "];");
        appendLine(sb, "	for(var i=0;i<data.length;i++){");
        appendLine(sb, "			if(value == data[i]['conditionId']){");
        appendLine(sb, "			return data[i]['conditionName'];");
        appendLine(sb, "			}");
        appendLine(sb, "		}");
        appendLine(sb, "		return value;");
        appendLine(sb, "		},editor:{");
        appendLine(sb, "		type:'combobox',");
        appendLine(sb, "		options:{");
        appendLine(sb, "			valueField:'conditionId',");
        appendLine(sb, "				textField:'conditionName',	");
        appendLine(sb, "			data:  ");
        appendLine(sb, "[");
        buildCheckType(sb, types);
        appendLine(sb, "				],  ");
        appendLine(sb, "				required:true");
        appendLine(sb, "			}}\">条件</th>");
        sb
                .append("	<th data-options=\"field:'value',width:30,editor:'text'\">值</th>");
        appendLine(sb, "<th data-options=\"field:'opt',width:30,formatter:function(value,row){");
        appendLine(sb, "	return '<a  onclick=\\'removeIt('+row.id+')\\' >删除</a>';}\">操作</th>");
        appendLine(sb, "		</tr>");
        appendLine(sb, "	</thead>");
        appendLine(sb, "	</table>");
        appendLine(sb, "</div>");
        appendLine(sb, "<div data-options=\"region:'south',border:false\" style=\"text-align:right;padding:5px 0 0;\">");
        appendLine(sb, "<a class=\"" + buttonSytle + "\" data-options=\"iconCls:'icon-ok'\" href=\"javascript:void(0)\" onclick=\"javascript:queryBuilderSearch()\">确定</a>");
        appendLine(sb, "<a class=\"" + buttonSytle + "\" data-options=\"iconCls:'icon-cancel'\" href=\"javascript:void(0)\" onclick=\"javascript:$('#" + name + "_qbwin').window('close')\">取消</a>");
        appendLine(sb, "		</div>");
        appendLine(sb, "	</div>	");
        appendLine(sb, "</div>		");
        appendLine(sb, "</div>");
        appendLine(sb, "<div id=\"mm\" class=\"easyui-menu\" style=\"width:120px;\">");
        appendLine(sb, "	<div onclick=\"append()\" data-options=\"iconCls:'icon-add'\">添加</div>");
        appendLine(sb, "	<div onclick=\"edit()\" data-options=\"iconCls:'icon-edit'\">编辑</div>");
        appendLine(sb, "	<div onclick=\"save()\" data-options=\"iconCls:'icon-save'\">保存</div>");
        appendLine(sb, "	<div onclick=\"removeIt()\" data-options=\"iconCls:'icon-remove'\">删除</div>");
        appendLine(sb, "	<div class=\"menu-sep\"></div>");
        appendLine(sb, "	<div onclick=\"cancel()\">取消</div>");
        appendLine(sb, "<div onclick=\"expand()\">Expand</div>");
        appendLine(sb, "</div><div id=\"" + name + "mmTree\" class=\"easyui-menu\" style=\"width:100px;\">");
        appendLine(sb, "<div onclick=\"editTree()\" data-options=\"iconCls:'icon-edit'\">编辑</div>");
        appendLine(sb, "<div onclick=\"deleteTree()\" data-options=\"iconCls:'icon-remove'\">删除</div></div>");
        //已在baseTag中引入
        appendLine(sb, "<script type=\"text/javascript\">");
        appendLine(sb, "var toolbar = [{");
        appendLine(sb, "	text:'',");
        appendLine(sb, "	iconCls:'icon-add',");
        appendLine(sb, "	handler:function(){append();}");
        appendLine(sb, "},{");
        appendLine(sb, "	text:'',");
        appendLine(sb, "	iconCls:'icon-edit',");
        appendLine(sb, "	handler:function(){edit();}");
        appendLine(sb, "},{");
        appendLine(sb, "	text:'',");
        appendLine(sb, "	iconCls:'icon-remove',");
        appendLine(sb, "	handler:function(){removeIt();}");
        appendLine(sb, "},'-',{");
        appendLine(sb, "	text:'',");
        appendLine(sb, "	iconCls:'icon-save',");
        appendLine(sb, "	handler:function(){save();}");
        appendLine(sb, "	}];");
        appendLine(sb, "function onContextMenu(e,row){");
        appendLine(sb, "	e.preventDefault();");
        appendLine(sb, "	$(this).treegrid('select', row.id);");
        appendLine(sb, "	$('#mm').menu('show',{");
        appendLine(sb, "		left: e.pageX,");
        appendLine(sb, "		top: e.pageY");
        appendLine(sb, "	});");
        appendLine(sb, "}");
        appendLine(sb, "	var idIndex = 100;");
        appendLine(sb, "function append(){");
        appendLine(sb, "	idIndex++;");
        appendLine(sb, "	var node = $('#" + name + "tg').treegrid('getSelected');");
        appendLine(sb, "	$('#" + name + "tg').treegrid('append',{");
        appendLine(sb, "		data: [{");
        appendLine(sb, "			id: idIndex,");
        appendLine(sb, "			field: '',");
        appendLine(sb, "		condition:'like',");
        appendLine(sb, "		value: '%a%',");
        appendLine(sb, "		relation: 'and'");
        appendLine(sb, "				}]");
        appendLine(sb, "});$('#" + name + "tg').datagrid('beginEdit',idIndex);");
        appendLine(sb, "}");
        appendLine(sb, "		function removeIt(id){");
        appendLine(sb, "var node = $('#" + name + "tg').treegrid('getSelected');");
        appendLine(sb, "if(id){");
        appendLine(sb, "$('#" + name + "tg').treegrid('remove', id);");
        appendLine(sb, "}else if(node){	$('#" + name + "tg').treegrid('remove', node.id);");
        appendLine(sb, "}");
        appendLine(sb, "}");
        appendLine(sb, "function collapse(){");
        appendLine(sb, "	var node = $('#" + name + "tg').treegrid('getSelected');");
        appendLine(sb, "if(node){");
        appendLine(sb, "	$('#" + name + "tg').treegrid('collapse', node.id);");
        appendLine(sb, "}");
        appendLine(sb, "}");
        appendLine(sb, "function expand(){");
        appendLine(sb, "var node = $('#" + name + "tg').treegrid('getSelected');");
        appendLine(sb, "if(node){");
        appendLine(sb, "	$('#" + name + "tg').treegrid('expand', node.id);");
        appendLine(sb, "}");
        appendLine(sb, "}");
        appendLine(sb, "var editingId;");
        appendLine(sb, "function edit(id){");
        appendLine(sb, "var row = $('#" + name + "tg').treegrid('getSelected');");
        appendLine(sb, "if(id){	$('#" + name + "tg').treegrid('beginEdit', id);}else if(row){");
        appendLine(sb, "	$('#" + name + "tg').treegrid('beginEdit', row.id);");
        appendLine(sb, "}");
        appendLine(sb, "}");
        appendLine(sb, "function save(){");
        appendLine(sb, "	var t = $('#" + name + "tg');");
        appendLine(sb, "	var nodes = t.treegrid('getRoots');");
        appendLine(sb, "	for (var i = 0; i < nodes.length; i++) {");
        appendLine(sb, "	t.treegrid('endEdit',nodes[i].id);}");
        appendLine(sb, "	}");
        appendLine(sb, "function cancel(){");
        appendLine(sb, "	var t = $('#" + name + "tg');");
        appendLine(sb, "var nodes = t.treegrid('getRoots');for (var i = 0; i < nodes.length; i++) {t.treegrid('cancelEdit',nodes[i].id);}");
        appendLine(sb, "}");
        appendLine(sb, "var " + name + "his=new Array();");
        appendLine(sb, " function historyQuery(index) {");
        appendLine(sb, "	  var data  = { rows:JSON.parse(" + name + "his[index].json)};  ");
        appendLine(sb, "	    var t = $('#" + name + "tg');");
        appendLine(sb, "		var data = t.treegrid('loadData',data);");
        appendLine(sb, "		$('#_sqlbuilder').val( " + name + "his[index].json);   ");
        appendLine(sb, "		" + name + "search();");
        appendLine(sb, "	}");
        appendLine(sb, "function view(){");
        appendLine(sb, "save();");
        appendLine(sb, "var t = $('#" + name + "tg');");
        appendLine(sb, "var data = t.treegrid('getData');");
        appendLine(sb, "return   JSON.stringify(data) ;");
        appendLine(sb, "}");
        appendLine(sb, "	 function queryBuilder() {");
        appendLine(sb, "	$('#" + name + "_qbwin').window('open');");
        appendLine(sb, "}");

        appendLine(sb, "function queryBuilderSearch() {");
        appendLine(sb, "         var json =  view();");
        appendLine(sb, "	$('#_sqlbuilder').val(json);  ");
        appendLine(sb, "	var isnew=true;");
        appendLine(sb, "for(var i=0;i< " + name + "his.length;i++){");
        appendLine(sb, "	if(" + name + "his[i]&&" + name + "his[i].json==json){");
        appendLine(sb, "		isnew=false;");
        appendLine(sb, "	}");
        appendLine(sb, "}");
        appendLine(sb, "if(isnew){");
        appendLine(sb, " " + name + "his.push({name:'Query'+" + name + "his.length,json:json});saveHistory();");
        appendLine(sb, "var name= 'Query'+( " + name + "his.length-1);");
        appendLine(sb, "	var name= 'Query'+(" + name + "his.length-1);");
        appendLine(sb, "appendTree(" + name + "his.length-1,name);");
        appendLine(sb, "}");
        appendLine(sb, "	" + name + "search();");
        appendLine(sb, " }");
        appendLine(sb, " $(document).ready(function(){ ");
        appendLine(sb, " storage=$.localStorage;if(!storage)storage=$.cookieStorage;");
        appendLine(sb, "	var _qhistory = storage.get('" + name + "_query_history');");
        appendLine(sb, " if(_qhistory){");
        appendLine(sb, " " + name + "his=_qhistory;");
        // appendLine(sb, " 	var data  = { rows:his[0]};");
        appendLine(sb, " 	for(var i=0;i< " + name + "his.length;i++){");
        appendLine(sb, " 		if(" + name + "his[i])appendTree(i," + name + "his[i].name);");
        appendLine(sb, " 	}restoreheader();");
        appendLine(sb, " }});");
        appendLine(sb, "function saveHistory(){");
        appendLine(sb, "	var history=new Array();");
        appendLine(sb, "	for(var i=0;i<" + name + "his.length;i++){");
        appendLine(sb, "		if(" + name + "his[i]){");
        appendLine(sb, "			history.push(" + name + "his[i]);");
        appendLine(sb, "		}");
        appendLine(sb, "	}");
        appendLine(sb, "	storage.set( '" + name + "_query_history',JSON.stringify(history));");
        appendLine(sb, "}");
        appendLine(sb, "function deleteTree(){");
        appendLine(sb, "	var tree = $('#" + name
                + "tt');var node= tree.tree('getSelected');");
        appendLine(sb, "	" + name + "his[node.id]=null;saveHistory();");
        appendLine(sb, "	tree.tree('remove', node.target);");
        appendLine(sb, "}");
        appendLine(sb, "function editTree(){");
        appendLine(sb, "	var node = $('#" + name + "tt').tree('getSelected');");
        appendLine(sb, "	$('#" + name + "tt').tree('beginEdit',node.target);");
        appendLine(sb, "	saveHistory();");
        appendLine(sb, "}");
        appendLine(sb, "function appendTree(id,name){");
        appendLine(sb, "	$('#" + name + "tt').tree('append',{");
        appendLine(sb, "	data:[{");
        appendLine(sb, "id : id,");
        appendLine(sb, "text :name");
        appendLine(sb, "	}]");
        appendLine(sb, "});");
        appendLine(sb, "}");


        appendLine(sb, "</script>");
    }

    private void buildCheckType(StringBuffer sb, List<TypeView> types) {
        if (types != null) {
            for (int i = 0; i < types.size(); i++) {
                TypeView type = types.get(i);
                appendLine(sb, " {'conditionId':'" + type.getTypeCode() + "','conditionName':'"
                        + LanguageUtils.getLang(type.getTypeName()) + "'}");
                if (i < types.size() - 1) {
                    appendLine(sb, ",");
                }
            }
        }
    }

    /**
     * hibernate字段名转换为数据库名称，只支持标准命名
     * 否则转换错误
     *
     * @param fieldName
     * @return
     */
    String getDBFieldName(String fieldName) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (c <= 'Z' && c >= 'A') {
                sb.append("_").append((char) ((int) c + 32));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Deprecated
    public StringBuffer buildTableNoIcon() {
        String grid = "";
        StringBuffer sb = new StringBuffer();
        width = (width == null) ? "auto" : width;
        height = (height == null) ? "auto" : height;
        sb.append("<link rel=\"stylesheet\" href=\"plug-in/easyui/themes/ace/main.css\" /><script type=\"text/javascript\">");
        sb.append("$(function(){  storage=$.localStorage;if(!storage)storage=$.cookieStorage;");
        sb.append(this.getNoAuthOperButton());
        if (treeGrid) {
            grid = "treegrid";
            sb.append("$(\'#" + name + "\').treegrid({");
            sb.append("idField:'id',");
            sb.append("treeField:'text',");
        } else {
            grid = "datagrid";
            sb.append("$(\'#" + name + "\').datagrid({");
            sb.append("idField: '" + idField + "',");
        }
        if (title != null) {
            sb.append("title: \'" + title + "\',");
        }

        if (autoLoadData)
            sb.append("url:\'" + actionUrl + "&field=" + fields + "\',");
        else
            sb.append("url:\'',");
        if (!StrUtils.isEmpty(rowStyler)) {
            sb.append("rowStyler: function(index,row){ return " + rowStyler + "(index,row);},");
        }
        if (!StrUtils.isEmpty(extendParams)) {
            sb.append(extendParams);
        }
        if (fit) {
            sb.append("fit:true,");
        } else {
            sb.append("fit:false,");
        }
        sb.append(StrUtils.replaceAll("loadMsg: \'{0}\',", "{0}", LanguageUtils.getLang("common.data.loading")));
        sb.append("striped:true,pageSize: " + pageSize + ",");
        sb.append("pagination:" + pagination + ",");
        sb.append("pageList:[" + pageSize * 1 + "," + pageSize * 2 + "," + pageSize * 3 + "],");
        if (!StrUtils.isEmpty(sortName)) {
            sb.append("sortName:'" + sortName + "',");
        }
        sb.append("sortOrder:'" + sortOrder + "',");
        sb.append("rownumbers:true,");
        sb.append("singleSelect:" + !checkbox + ",");
        if (fitColumns) {
            sb.append("fitColumns:true,");
        } else {
            sb.append("fitColumns:false,");
        }
        sb.append("showFooter:true,");
        sb.append("frozenColumns:[[");
        this.getField(sb, 0);
        sb.append("]],");

        sb.append("columns:[[");
        this.getField(sb);
        sb.append("]],");
        sb.append("onLoadSuccess:function(data){$(\"#" + name + "\")." + grid + "(\"clearSelections\");");
        if (openFirstNode && treeGrid) {
            sb.append(" if(data==null){");
            sb.append(" var firstNode = $(\'#" + name + "\').treegrid('getRoots')[0];");
            sb.append(" $(\'#" + name + "\').treegrid('expand',firstNode.id)}");
        }
        if (!StrUtils.isEmpty(onLoadSuccess)) {
            sb.append(onLoadSuccess + "(data);");
        }
        sb.append("},");
        if (!StrUtils.isEmpty(onDblClick)) {
            sb.append("onDblClickRow:function(rowIndex,rowData){" + onDblClick + "(rowIndex,rowData);},");
        }
        if (treeGrid) {
            sb.append("onClickRow:function(rowData){");
        } else {
            sb.append("onClickRow:function(rowIndex,rowData){");
        }
        /**行记录赋值*/
        sb.append("rowid=rowData.id;");
        sb.append("gridname=\'" + name + "\';");
        if (!StrUtils.isEmpty(onClick)) {
            if (treeGrid) {
                sb.append("" + onClick + "(rowData);");
            } else {
                sb.append("" + onClick + "(rowIndex,rowData);");
            }
        }
        sb.append("}");
        sb.append("});");
        this.setPager(sb, grid);
        sb.append("try{restoreheader();}catch(ex){}");
        sb.append("});");
        sb.append("function reloadTable(){");
        sb.append("try{");
        sb.append("	$(\'#\'+gridname).datagrid(\'reload\');");
        sb.append("	$(\'#\'+gridname).treegrid(\'reload\');");
        sb.append("}catch(ex){}");
        sb.append("}");
        sb.append("function reload" + name + "(){" + "$(\'#" + name + "\')." + grid + "(\'reload\');" + "}");
        sb.append("function get" + name + "Selected(field){return getSelected(field);}");
        sb.append("function getSelected(field){" + "var row = $(\'#\'+gridname)." + grid + "(\'getSelected\');" + "if(row!=null)" + "{" + "value= row[field];" + "}" + "else" + "{" + "value=\'\';" + "}" + "return value;" + "}");
        sb.append("function get" + name + "Selections(field){" + "var ids = [];" + "var rows = $(\'#" + name + "\')." + grid + "(\'getSelections\');" + "for(var i=0;i<rows.length;i++){" + "ids.push(rows[i][field]);" + "}" + "ids.join(\',\');" + "return ids" + "};");
        sb.append("function getSelectRows(){");
        sb.append("	return $(\'#" + name + "\').datagrid('getChecked');}");
        sb.append(" function saveHeader(){");
        sb.append(" var columnsFields =null;var easyextends=false;try{columnsFields = $('#" + name + "').datagrid('getColumns');easyextends=true;");
        sb.append("}catch(e){columnsFields =$('#" + name + "').datagrid('getColumnFields');}");
        sb.append("	var cols = storage.get( '" + name + "hiddenColumns');var init=true;	if(cols){init =false;} " +
                "var hiddencolumns = [];for(var i=0;i< columnsFields.length;i++) {if(easyextends){");
        sb.append("hiddencolumns.push({field:columnsFields[i].field,hidden:columnsFields[i].hidden});}else{");
        sb.append(" var columsDetail = $('#" + name + "').datagrid(\"getColumnOption\", columnsFields[i]); ");
        sb.append("if(init){hiddencolumns.push({field:columsDetail.field,hidden:columsDetail.hidden,visible:(columsDetail.hidden==true?false:true)});}else{");
        sb.append("for(var j=0;j<cols.length;j++){");
        sb.append("		if(cols[j].field==columsDetail.field){");
        sb.append("					hiddencolumns.push({field:columsDetail.field,hidden:columsDetail.hidden,visible:cols[j].visible});");
        sb.append("		}");
        sb.append("}");
        sb.append("}} }");
        sb.append("storage.set( '" + name + "hiddenColumns',JSON.stringify(hiddencolumns));");
        sb.append("}");
        sb.append("function restoreheader(){");
        sb.append("var cols = storage.get( '" + name + "hiddenColumns');if(!cols)return;");
        sb.append("for(var i=0;i<cols.length;i++){");
        sb.append("	try{");
        sb.append("if(cols.visible!=false)$('#" + name + "').datagrid((cols[i].hidden==true?'hideColumn':'showColumn'),cols[i].field);");
        sb.append("}catch(e){");
        sb.append("}");
        sb.append("}");
        sb.append("}");
        sb.append("function resetheader(){");
        sb.append("var cols = storage.get( '" + name + "hiddenColumns');if(!cols)return;");
        sb.append("for(var i=0;i<cols.length;i++){");
        sb.append("	try{");
        sb.append("  $('#" + name + "').datagrid((cols.visible==false?'hideColumn':'showColumn'),cols[i].field);");
        sb.append("}catch(e){");
        sb.append("}");
        sb.append("}");
        sb.append("}");
        if (columnList.size() > 0) {
            sb.append("function " + name + "search(){");
            sb.append("var queryParams=$(\'#" + name + "\').datagrid('options').queryParams;");
            sb.append("$(\'#" + name + "tb\').find('*').each(function(){queryParams[$(this).attr('name')]=$(this).val();});");
            sb.append("$(\'#" + name + "\')." + grid + "({url:'" + actionUrl + "&field=" + searchFields + "',pageNumber:1});" + "}");

            //高级查询执行方法
            sb.append("function dosearch(params){");
            sb.append("var jsonparams=$.parseJSON(params);");
            sb.append("$(\'#" + name + "\')." + grid + "({url:'" + actionUrl + "&field=" + searchFields + "',queryParams:jsonparams});" + "}");
            //searchbox框执行方法
            searchboxFun(sb, grid);
            //回车事件
            sb.append("function EnterPress(e){");
            sb.append("var e = e || window.event;");
            sb.append("if(e.keyCode == 13){ ");
            sb.append(name + "search();");
            sb.append("}}");

            sb.append("function searchReset(name){");
            sb.append(" $(\"#" + name + "tb\").find(\":input\").val(\"\");");
            String func = name.trim() + "search();";
            sb.append(func);
            sb.append("}");
        }
        sb.append("</script>");
        sb.append("<table width=\"100%\"   id=\"" + name + "\" toolbar=\"#" + name + "tb\"></table>");
        sb.append("<div id=\"" + name + "tb\" style=\"padding:3px; height: auto\">");
        if (hasQueryColum(columnList)) {
            sb.append("<div name=\"searchColums\">");
            sb.append("<input  id=\"_sqlbuilder\" name=\"sqlbuilder\"   type=\"hidden\" />");
            //如果表单是组合查询
            if ("group".equals(getQueryMode())) {
                for (DataGridColumn col : columnList) {
                    if (col.isQuery()) {
                        sb.append("<span style=\"display:-moz-inline-box;display:inline-block;\">");
                        sb.append("<span style=\"vertical-align:middle;display:-moz-inline-box;display:inline-block;width: 80px;text-align:right;text-overflow:ellipsis;-o-text-overflow:ellipsis; overflow: hidden;white-space:nowrap; \" title=\"" + col.getTitle() + "\">" + col.getTitle() + "：</span>");
                        if ("single".equals(col.getQueryMode())) {
                            if (!StrUtils.isEmpty(col.getReplace())) {
                                sb.append("<select name=\"" + col.getField().replaceAll("_", "\\.") + "\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                sb.append(StrUtils.replaceAll("<option value =\"\" >{0}</option>", "{0}", LanguageUtils.getLang("common.please.select")));
                                String[] test = col.getReplace().split(",");
                                String text = "";
                                String value = "";


                                for (String string : test) {
                                    String lang_key = string.split("_")[0];
                                    text = LanguageUtils.getLang(lang_key);
                                    value = string.split("_")[1];
                                    sb.append("<option value =\"" + value + "\">" + text + "</option>");
                                }
                                sb.append("</select>");
                            } else if (!StrUtils.isEmpty(col.getDictionary())) {
                                if (col.getDictionary().contains(",")) {
                                    System.out.println("getDictionary 部分未完善");
									/*
									String[] dic = col.getDictionary().split(",");
									String sql = "select " + dic[1] + " as field," + dic[2]
											+ " as text from " + dic[0];
									systemService = ApplicationContextUtil.getContext().getBean(
											SystemService.class);
									List<Map<String, Object>> list = systemService.findForJdbc(sql);
									sb.append("<select name=\""+col.getField().replaceAll("_","\\.")+"\" WIDTH=\"100\" style=\"width: 104px\"> ");
									sb.append(StringUtils.replaceAll("<option value =\"\" >{0}</option>", "{0}", MutiLangUtil.getLang("common.please.select")));
									for (Map<String, Object> map : list){
										sb.append(" <option value=\""+map.get("field")+"\">");
										sb.append(map.get("text"));
										sb.append(" </option>");
									}
									sb.append("</select>");
								*/
                                } else {
                                    Map<String, List<TypeView>> typedatas = SystemContainer.TypeGroupContainer.getTypeMap();
                                    List<TypeView> types = typedatas.get(col.getDictionary().toLowerCase());
                                    sb.append("<select name=\"" + col.getField().replaceAll("_", "\\.") + "\" WIDTH=\"100\" style=\"width: 104px\"> ");
                                    sb.append(StrUtils.replaceAll("<option value =\"\" >{0}</option>", "{0}", LanguageUtils.getLang("common.please.select")));
                                    if (!StrUtils.isEmpty(types)) {
                                        for (TypeView type : types) {
                                            sb.append(" <option value=\"" + type.getTypeCode() + "\">");
                                            sb.append(LanguageUtils.getLang(type.getTypeName()));
                                            sb.append(" </option>");
                                        }
                                    }
                                    sb.append("</select>");
                                }
                            } else if (col.isAutocomplete()) {
                                sb.append(getAutoSpan(col.getField().replaceAll("_", "\\."), extendAttribute(col.getExtend())));
                            } else {
                                sb.append("<input class=\"inuptxt\" onkeypress=\"EnterPress(event)\" onkeydown=\"EnterPress()\"  type=\"text\" name=\"" + col.getField().replaceAll("_", "\\.") + "\"  " + extendAttribute(col.getExtend()) + "  />");
                            }
                        } else if ("group".equals(col.getQueryMode())) {
                            sb.append("<input class=\"inuptxt\" type=\"text\" name=\"" + col.getField() + "_begin\"   " + extendAttribute(col.getExtend()) + "/>");
                            sb.append("<span style=\"display:-moz-inline-box;display:inline-block;width: 8px;text-align:right;\">~</span>");
                            sb.append("<input class=\"inuptxt\" type=\"text\" name=\"" + col.getField() + "_end\"   " + extendAttribute(col.getExtend()) + "/>");

                        }
                        sb.append("</span>");
                    }
                }
            }
            sb.append("</div>");
        }
        if (toolBarList.size() == 0 && !hasQueryColum(columnList)) {
            sb.append("<div style=\"height:0px;\" >");
        } else {
            sb.append("<div style=\"height:30px;\" class=\"datagrid-toolbar\">");
        }
        sb.append("<span style=\"float:left;\" >");
        if (toolBarList.size() > 0) {
            for (DataGridUrl toolBar : toolBarList) {

                sb.append("<a href=\"#\" class=\"button\" plain=\"true\" icon=\"" + toolBar.getIcon() + "\" ");
                if (!StrUtils.isEmpty(toolBar.getOnclick())) {
                    sb.append("onclick=" + toolBar.getOnclick() + "");
                } else {
                    sb.append("onclick=\"" + toolBar.getFunction() + "(");
                    if (!toolBar.getFunction().equals("doSubmit")) {
                        sb.append("\'" + toolBar.getTitle() + "\',");
                    }
                    String width = toolBar.getWidth().contains("%") ? "'" + toolBar.getWidth() + "'" : toolBar.getWidth();
                    String height = toolBar.getHeight().contains("%") ? "'" + toolBar.getHeight() + "'" : toolBar.getHeight();
                    sb.append("\'" + toolBar.getUrl() + "\',\'" + name + "\'," + width + "," + height + ")\"");
                }
                sb.append(">" + toolBar.getTitle() + "</a>");
            }
        }
        sb.append("</span>");
        if ("group".equals(getQueryMode()) && hasQueryColum(columnList)) {//如果表单是组合查询
            sb.append("<span style=\"float:right\">");
            sb.append("<a href=\"#\" class=\"button\" iconCls=\"icon-search\" onclick=\"" + name + StrUtils.replaceAll("search()\">{0}</a>", "{0}", LanguageUtils.getLang("common.query")));
            sb.append("<a href=\"#\" class=\"button\" iconCls=\"icon-reload\" onclick=\"searchReset('" + name + StrUtils.replaceAll("')\">{0}</a>", "{0}", LanguageUtils.getLang("common.reset")));
            if (queryBuilder) {
                sb.append("<a href=\"#\" class=\"button\" iconCls=\"icon-search\" onclick=\"queryBuilder('" + StrUtils.replaceAll("')\">{0}</a>", "{0}", LanguageUtils.getLang("common.querybuilder")));
            }
            sb.append("</span>");
        } else if ("single".equals(getQueryMode()) && hasQueryColum(columnList)) {//如果表单是单查询
            sb.append("<span style=\"float:right\">");
            sb.append("<input id=\"" + name + "searchbox\" class=\"easyui-searchbox\"  data-options=\"searcher:" + name + StrUtils.replaceAll("searchbox,prompt:\'{0}\',menu:\'#", "{0}", LanguageUtils.getLang("common.please.input.keyword")) + name + "mm\'\"></input>");
            sb.append("<div id=\"" + name + "mm\" style=\"width:120px\">");
            for (DataGridColumn col : columnList) {
                if (col.isQuery()) {
                    sb.append("<div data-options=\"name:\'" + col.getField().replaceAll("_", "\\.") + "\',iconCls:\'icon-ok\' " + extendAttribute(col.getExtend()) + " \">" + col.getTitle() + "</div>  ");
                }
            }
            sb.append("</div>");
            sb.append("</span>");
        }
        sb.append("</div>");
        if (queryBuilder) {
            addQueryBuilder(sb, "button");
        }
        return sb;
    }
}
