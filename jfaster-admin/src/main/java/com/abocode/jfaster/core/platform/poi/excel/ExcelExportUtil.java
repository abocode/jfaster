package com.abocode.jfaster.core.platform.poi.excel;

import com.abocode.jfaster.core.common.util.ContextHolderUtils;
import com.abocode.jfaster.core.platform.poi.excel.annotation.Excel;
import com.abocode.jfaster.core.platform.poi.excel.annotation.ExcelCollection;
import com.abocode.jfaster.core.platform.poi.excel.annotation.ExcelTarget;
import com.abocode.jfaster.core.platform.poi.excel.entity.ExcelExportEntity;
import com.abocode.jfaster.core.platform.poi.excel.entity.ExcelTitle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel 导出工具类
 *
 * @author jueyue
 * @version 1.0
 * @date 2013-10-17
 */
@Slf4j
public final class ExcelExportUtil {

    public static final String ONE_WRAP = "oneWrap";
    public static final String ONE = "one";
    public static final String TWO = "two";
    public static final String TWO_WRAP = "twoWrap";

    /**
     * @param entity    表格标题属性
     * @param pojoClass Excel对象Class
     * @param dataSet   Excel对象数据List
     */
    public static HSSFWorkbook exportExcel(ExcelTitle entity,
                                           Class<?> pojoClass, Collection<?> dataSet) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        createSheetInUserModel2File(workbook, entity, pojoClass, dataSet);
        return workbook;
    }


    private static void createSheetInUserModel2File(HSSFWorkbook workbook,
                                                    ExcelTitle entity, Class<?> pojoClass, Collection<?> dataSet) {
        try {
            Sheet sheet = workbook.createSheet(entity.getSheetName());
            //创建表格属性
            Map<String, HSSFCellStyle> styles = createStyles(workbook);
            Drawing patriarch = sheet.createDrawingPatriarch();
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            // 得到所有字段
            Field fileds[] = ExcelPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
            String targetId = null;
            if (etarget != null) {
                targetId = etarget.id();
            }
            getAllExcelField(targetId, fileds, excelParams, pojoClass, null);
            sortAllParams(excelParams);
            int index = 0;
            int feildWidth = getFieldWidth(excelParams);
            if (entity.getTitle() != null) {
                int i = createHeaderRow(entity, sheet, workbook, feildWidth);
                sheet.createFreezePane(0, 2 + i, 0, 2 + i);
                index += i;
            } else {
                sheet.createFreezePane(0, 2, 0, 2);
            }
            createTitleRow(entity, sheet, workbook, index, excelParams);
            index += 2;
            setCellWith(excelParams, sheet);
            Iterator<?> its = dataSet.iterator();
            while (its.hasNext()) {
                Object data = its.next();
                index += createCells(patriarch, index, data, excelParams, sheet, styles);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 对字段根据用户设置排序
     */
    private static void sortAllParams(List<ExcelExportEntity> excelParams) {
        Collections.sort(excelParams, new ExcelComparator());
        for (ExcelExportEntity entity : excelParams) {
            if (entity.getRows() != null) {
                Collections.sort(entity.getRows(), new ExcelComparator());
            }
        }
    }

    /**
     * 创建 最主要的 Cells
     *
     * @param styles
     * @throws Exception
     */
    private static int createCells(Drawing patriarch, int index, Object data,
                                   List<ExcelExportEntity> excelParams, Sheet sheet,
                                   Map<String, HSSFCellStyle> styles) throws Exception {
        ExcelExportEntity entity;
        Row row = sheet.createRow(index);
        row.setHeight((short) 350);
        int maxHeight = 1, cellNum = 0;
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            if (entity.getRows() != null) {
                Collection<?> list = (Collection<?>) entity.getGetMethod()
                        .invoke(data, new Object[]{});
                int listC = 0;
                if (list != null) {
                    for (Object obj : list) {
                        createListCells(patriarch, index + listC, cellNum, obj,
                                entity.getRows(), sheet, styles);
                        listC++;
                    }
                    cellNum += entity.getRows().size();
                    if (list.size() > maxHeight) {
                        maxHeight = list.size();
                    }
                }

            } else {
                Object value = getCellValue(entity, data);
                if (entity.getType() == 1) {

                    CellStyle cellStyle = index % 2 == 0 ? getStyles(styles, false, entity.isWrap()) : getStyles(styles, true, entity.isWrap());
                    Assert.isTrue(cellStyle != null, "styles is null");
                    createStringCell(row, cellNum++, value.toString(), cellStyle, entity);
                } else {
                    createImageCell(patriarch, entity, row, cellNum++, value.toString(), data);
                }
            }
        }
        //合并需要合并的单元格
        cellNum = 0;
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            if (entity.getRows() != null) {
                cellNum += entity.getRows().size();
            } else if (entity.isNeedMerge()) {
                sheet.addMergedRegion(new CellRangeAddress(index, index + maxHeight - 1, cellNum,
                        cellNum));
                cellNum++;
            }
        }
        return maxHeight;

    }

    /**
     * 获取填如这个cell的值,提供一些附加功能
     *
     * @param entity
     * @param obj
     * @return
     * @throws Exception
     */
    private static Object getCellValue(ExcelExportEntity entity, Object obj) throws Exception {
        Object value = entity.getGetMethods() != null ? getFieldBySomeMethod(
                entity.getGetMethods(), obj) : entity.getGetMethod()
                .invoke(obj, new Object[]{});
        //step 1 判断是不是日期,需不需要格式化
        if (StringUtils.isNotEmpty(entity.getExportFormat())) {
            Date temp = null;
            if (value instanceof String) {
                SimpleDateFormat format = new SimpleDateFormat(entity.getDatabaseFormat());
                temp = format.parse(value.toString());
            } else if (value instanceof Date) {
                temp = (Date) value;
            }
            if (temp != null) {
                SimpleDateFormat format = new SimpleDateFormat(entity.getExportFormat());
                value = format.format(temp);
            }
        }
        return value == null ? "" : value.toString();
    }

    /**
     * 创建List之后的各个Cells
     *
     * @param styles
     */
    private static void createListCells(Drawing patriarch, int index, int cellNum, Object obj,
                                        List<ExcelExportEntity> excelParams, Sheet sheet,
                                        Map<String, HSSFCellStyle> styles) throws Exception {
        ExcelExportEntity entity;
        Row row;
        if (sheet.getRow(index) == null) {
            row = sheet.createRow(index);
            row.setHeight((short) 350);
        } else {
            row = sheet.getRow(index);
        }
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            Object value = getCellValue(entity, obj);
            if (entity.getType() == 1) {
                createStringCell(row, cellNum++, value.toString(),
                        row.getRowNum() % 2 == 0 ? getStyles(styles, false, entity.isWrap())
                                : getStyles(styles, true, entity.isWrap()), entity);
            } else {
                createImageCell(patriarch, entity, row, cellNum++, value.toString(), obj);
            }
        }
    }

    /**
     * 多个反射获取值
     *
     * @param list
     * @param t
     * @return
     * @throws Exception
     */
    private static Object getFieldBySomeMethod(List<Method> list, Object t)
            throws Exception {
        for (Method m : list) {
            if (t == null) {
                t = "";
                break;
            }
            t = m.invoke(t, new Object[]{});
        }
        return t;
    }

    private static void setCellWith(List<ExcelExportEntity> excelParams,
                                    Sheet sheet) {
        int index = 0;
        for (int i = 0; i < excelParams.size(); i++) {
            if (excelParams.get(i).getRows() != null) {
                List<ExcelExportEntity> list = excelParams.get(i).getRows();
                for (int j = 0; j < list.size(); j++) {
                    sheet.setColumnWidth(index, 256 * list.get(j).getWidth());
                    index++;
                }
            } else {
                sheet.setColumnWidth(index, 256 * excelParams.get(i).getWidth());
                index++;
            }
        }
    }

    /**
     * 创建表头
     *
     * @param index
     */
    private static void createTitleRow(ExcelTitle title, Sheet sheet, HSSFWorkbook workbook,
                                       int index, List<ExcelExportEntity> excelParams) {
        Row row = sheet.createRow(index);
        Row row1 = sheet.createRow(index + 1);
        row.setHeight((short) 450);
        int cellIndex = 0;
        CellStyle titleStyle = getTitleStyle(workbook, title);
        for (int i = 0, exportFieldTitleSize = excelParams.size(); i < exportFieldTitleSize; i++) {
            ExcelExportEntity entity = excelParams.get(i);
            createStringCell(row, cellIndex, entity.getName(), titleStyle, entity);
            if (entity.getRows() != null) {
                List<ExcelExportEntity> sTitel = entity.getRows();
                sheet.addMergedRegion(new CellRangeAddress(index, index, cellIndex, cellIndex
                        + sTitel.size() - 1));
                for (int j = 0, size = sTitel.size(); j < size; j++) {
                    createStringCell(row1, cellIndex, sTitel.get(j).getName(),
                            titleStyle, entity);
                    cellIndex++;
                }
            } else {
                sheet.addMergedRegion(new CellRangeAddress(index, index + 1, cellIndex,
                        cellIndex));
                cellIndex++;
            }
        }

    }

    /**
     * 创建文本类型的Cell
     *
     * @param row
     * @param index
     * @param text
     * @param style
     * @param entity
     */
    private static void createStringCell(Row row, int index, String text,
                                         CellStyle style, ExcelExportEntity entity) {
        Cell cell = row.createCell(index);
        RichTextString Rtext = new HSSFRichTextString(text);
        cell.setCellValue(Rtext);
        cell.setCellStyle(style);
    }

    /**
     * 图片类型的Cell
     *
     * @param patriarch
     * @param entity
     * @param row
     * @param i
     * @param string
     * @param data
     * @throws Exception
     */
    private static void createImageCell(Drawing patriarch, ExcelExportEntity entity, Row row,
                                        int i, String string, Object data) throws Exception {
        Assert.isTrue(patriarch != null, "patriarch is null");
        row.setHeight((short) (50 * entity.getHeight()));
        row.createCell(i);
        HSSFClientAnchor anchor = new HSSFClientAnchor(
                0, 0, 0, 0, (short) i, row.getRowNum(),
                (short) (i + 1), row.getRowNum() + 1);
        if (StringUtils.isEmpty(string)) {
            return;
        }
        if (entity.getExportImageType() == 1) {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            BufferedImage bufferImg;
            try {
                String path = ContextHolderUtils.getRequest().getSession().getServletContext().getRealPath("\\") + string;
                path = path.replace("WEB-INF/classes/", "");
                path = path.replace("file:/", "");
                bufferImg = ImageIO.read(
                        new File(path));
                ImageIO.write(bufferImg, string.substring(string.indexOf(".") + 1, string.length()), byteArrayOut);
                byte[] value = byteArrayOut.toByteArray();
                patriarch.createPicture(anchor,
                        row.getSheet().getWorkbook().addPicture(value, getImageType(value)));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            byte[] value = (byte[]) (entity.getGetMethods() != null ? getFieldBySomeMethod(
                    entity.getGetMethods(), data) : entity.getGetMethod()
                    .invoke(data, new Object[]{}));
            if (value != null) {
                patriarch.createPicture(anchor,
                        row.getSheet().getWorkbook().addPicture(value, getImageType(value)));
            }
        }

    }

    /**
     * 获取图片类型,设置图片插入类型
     *
     * @param value
     * @return
     * @Author JueYue
     * @date 2013年11月25日
     */
    private static int getImageType(byte[] value) {
        String type = ExcelPublicUtil.getFileExtendName(value);
        if (type.equalsIgnoreCase("JPG")) {
            return HSSFWorkbook.PICTURE_TYPE_JPEG;
        } else if (type.equalsIgnoreCase("PNG")) {
            return HSSFWorkbook.PICTURE_TYPE_PNG;
        }
        return HSSFWorkbook.PICTURE_TYPE_JPEG;
    }

    /**
     * 创建 表头
     *
     * @param entity
     * @param sheet
     * @param workbook
     * @param feildWidth
     */
    private static int createHeaderRow(ExcelTitle entity, Sheet sheet,
                                       HSSFWorkbook workbook, int feildWidth) {
        Row row = sheet.createRow(0);
        row.setHeight((short) 900);
        createStringCell(row, 0, entity.getTitle(), getHeaderStyle(workbook, entity), null);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, feildWidth));
        if (entity.getSecondTitle() != null) {
            row = sheet.createRow(1);
            HSSFCellStyle style = workbook.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            createStringCell(row, 0, entity.getSecondTitle(), style, null);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, feildWidth));
            return 2;
        }
        return 1;
    }

    /**
     * 获取导出报表的字段总长度
     *
     * @return
     */
    private static int getFieldWidth(List<ExcelExportEntity> excelParams) {
        int length = -1;// 从0开始计算单元格的
        for (ExcelExportEntity entity : excelParams) {
            length += entity.getRows() != null ? entity.getRows().size() : 1;
        }
        return length;
    }

    /**
     * 获取需要导出的全部字段
     *
     * @param targetId 目标ID
     * @throws Exception
     */
    private static void getAllExcelField(String targetId, Field[] fields,
                                         List<ExcelExportEntity> excelParams, Class<?> pojoClass,
                                         List<Method> getMethods) throws Exception {
        // 遍历整个filed
        ExcelExportEntity excelEntity;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            // 先判断是不是collection,在判断是不是java自带对象,之后就是我们自己的对象了
            if (ExcelPublicUtil.isNotUserExcelUserThis(field, targetId)) {
                continue;
            }
            if (ExcelPublicUtil.isCollection(field.getType())) {
                ExcelCollection excel = field
                        .getAnnotation(ExcelCollection.class);
                ParameterizedType pt = (ParameterizedType) field
                        .getGenericType();
                Class<?> clz = (Class<?>) pt.getActualTypeArguments()[0];
                List<ExcelExportEntity> list = new ArrayList<ExcelExportEntity>();
                getExcelFieldList(targetId, ExcelPublicUtil.getClassFields(clz), clz,
                        list, null);
                excelEntity = new ExcelExportEntity();
                excelEntity.setName(getExcelName(excel.exportName(),
                        targetId));
                excelEntity.setOrderNum(getCellOrder(excel.orderNum(), targetId));
                excelEntity.setGetMethod(ExcelPublicUtil.getMethod(field.getName(),
                        pojoClass));
                excelEntity.setRows(list);
                excelParams.add(excelEntity);
            } else if (ExcelPublicUtil.isJavaClass(field)) {
                Excel excel = field.getAnnotation(Excel.class);
                excelEntity = new ExcelExportEntity();
                excelEntity.setType(excel.exportType());
                getExcelField(targetId, field, excelEntity, excel,
                        pojoClass);
                if (getMethods != null) {
                    List<Method> newMethods = new ArrayList<Method>();
                    newMethods.addAll(getMethods);
                    newMethods.add(excelEntity.getGetMethod());
                    excelEntity.setGetMethods(newMethods);
                }
                excelParams.add(excelEntity);
            } else {
                List<Method> newMethods = new ArrayList<Method>();
                if (getMethods != null) {
                    newMethods.addAll(getMethods);
                }
                newMethods.add(ExcelPublicUtil.getMethod(field.getName(), pojoClass));
                getAllExcelField(targetId, ExcelPublicUtil.getClassFields(field.getType()), excelParams, field.getType(),
                        newMethods);
            }
        }
    }

    /**
     * 判断在这个单元格显示的名称
     *
     * @param exportName
     * @param targetId
     * @return
     */
    private static String getExcelName(String exportName, String targetId) {
        if (exportName.indexOf(",") < 0) {
            return exportName;
        }
        String[] arr = exportName.split(",");
        for (String str : arr) {
            if (str.indexOf(targetId) != -1) {
                return str.split("_")[0];
            }
        }
        return null;
    }

    /**
     * 获取这个字段的顺序
     *
     * @param orderNum
     * @param targetId
     * @return
     */
    private static int getCellOrder(String orderNum, String targetId) {
        if (StringUtils.isNotEmpty(orderNum)) {
            try {
                return Integer.parseInt(orderNum);
            } catch (NumberFormatException e) {
            }
            if (StringUtils.isNotEmpty(targetId)) {
                String[] arr = orderNum.split(",");
                for (String str : arr) {
                    if (str.indexOf(targetId) != -1) {
                        String res = str.split("_")[0];
                        if (StringUtils.isNotEmpty(res)) {
                            return Integer.parseInt(res);
                        }

                    }
                }
            }
        }
        return 0;
    }


    /**
     * @param targetId
     * @param fields
     * @param pojoClass
     * @param list
     * @param getMethods
     * @throws Exception
     */
    private static void getExcelFieldList(String targetId, Field[] fields,
                                          Class<?> pojoClass, List<ExcelExportEntity> list,
                                          List<Method> getMethods) throws Exception {
        ExcelExportEntity excelEntity;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (ExcelPublicUtil.isNotUserExcelUserThis(field, targetId)) {
                continue;
            }
            if (ExcelPublicUtil.isJavaClass(field)) {
                Excel excel = field.getAnnotation(Excel.class);
                excelEntity = new ExcelExportEntity();
                getExcelField(targetId, field, excelEntity, excel,
                        pojoClass);
                excelEntity.setType(excel.exportType());
                if (getMethods != null) {
                    List<Method> newMethods = new ArrayList<Method>();
                    newMethods.addAll(getMethods);
                    newMethods.add(excelEntity.getGetMethod());
                    excelEntity.setGetMethods(newMethods);
                }
                list.add(excelEntity);
            } else {
                List<Method> newMethods = new ArrayList<Method>();
                if (getMethods != null) {
                    newMethods.addAll(getMethods);
                }
                newMethods.add(ExcelPublicUtil.getMethod(field.getName(), pojoClass));
                getExcelFieldList(targetId, ExcelPublicUtil.getClassFields(field.getType()), field.getType(), list,
                        newMethods);
            }
        }
    }

    /**
     * @param targetId
     * @param field
     * @param excelEntity
     * @param excel
     * @param pojoClass
     * @throws Exception
     */
    public static void getExcelField(String targetId, Field field,
                                     ExcelExportEntity excelEntity, Excel excel, Class<?> pojoClass)
            throws Exception {
        excelEntity.setName(getExcelName(excel.exportName(), targetId));
        excelEntity.setWidth(excel.exportFieldWidth());
        excelEntity.setHeight(excel.exportFieldHeight());
        excelEntity.setNeedMerge(excel.needMerge());
        excelEntity.setOrderNum(getCellOrder(excel.orderNum(), targetId));
        excelEntity.setWrap(excel.isWrap());
        excelEntity.setExportImageType(excel.imageType());
        excelEntity.setExportFormat(StringUtils.isNotEmpty(excel.exportFormat()) ?
                excel.exportFormat() : excel.imExFormat());
        String fieldname = field.getName();
        excelEntity.setGetMethod(ExcelPublicUtil.getMethod(fieldname, pojoClass));
        if (excel.exportConvertSign() == 1 || excel.imExConvert() == 1) {
            StringBuffer getConvertMethodName = new StringBuffer("convertGet");
            getConvertMethodName
                    .append(fieldname.substring(0, 1).toUpperCase());
            getConvertMethodName.append(fieldname.substring(1));
            Method getConvertMethod = pojoClass.getMethod(
                    getConvertMethodName.toString(), new Class[]{});
            excelEntity.setGetMethod(getConvertMethod);
        }
    }

    /**
     * 字段说明的Style
     *
     * @param workbook
     * @return
     */
    public static HSSFCellStyle getTitleStyle(HSSFWorkbook workbook, ExcelTitle entity) {
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFillForegroundColor(entity.getHeaderColor()); // 填充的背景颜色
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 填充图案
        titleStyle.setWrapText(true);
        return titleStyle;
    }

    /**
     * 表明的Style
     *
     * @param workbook
     * @return
     */
    public static HSSFCellStyle getHeaderStyle(HSSFWorkbook workbook, ExcelTitle entity) {
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 24);
        titleStyle.setFont(font);
        titleStyle.setFillForegroundColor(entity.getColor());
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return titleStyle;
    }

    public static HSSFCellStyle getTwoStyle(HSSFWorkbook workbook, boolean isWarp) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderLeft((short) 1); // 左边框
        style.setBorderRight((short) 1); // 右边框
        style.setBorderBottom((short) 1);
        style.setBorderTop((short) 1);
        style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index); // 填充的背景颜色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 填充图案
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        if (isWarp) {
            style.setWrapText(true);
        }
        return style;
    }

    public static HSSFCellStyle getOneStyle(HSSFWorkbook workbook, boolean isWarp) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setBorderLeft((short) 1); // 左边框
        style.setBorderRight((short) 1); // 右边框
        style.setBorderBottom((short) 1);
        style.setBorderTop((short) 1);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        if (isWarp) {
            style.setWrapText(true);
        }
        return style;
    }

    private static Map<String, HSSFCellStyle> createStyles(HSSFWorkbook workbook) {
        Map<String, HSSFCellStyle> map = new HashMap<String, HSSFCellStyle>();
        map.put(ONE, getOneStyle(workbook, false));
        map.put(ONE_WRAP, getOneStyle(workbook, true));
        map.put(TWO, getTwoStyle(workbook, false));
        map.put(TWO_WRAP, getTwoStyle(workbook, true));
        return map;
    }

    private static CellStyle getStyles(Map<String, HSSFCellStyle> map, boolean needOne, boolean isWrap) {
        if (needOne && isWrap) {
            return map.get(ONE_WRAP);
        } else if (needOne) {
            return map.get(ONE);
        } else if (!needOne && isWrap) {
            return map.get(TWO_WRAP);
        } else{
            return map.get(TWO);
        }
    }
}

class ExcelComparator implements Comparator<ExcelExportEntity>, Serializable {
    public int compare(ExcelExportEntity prev, ExcelExportEntity next) {
        return prev.getOrderNum() - next.getOrderNum();
    }
}