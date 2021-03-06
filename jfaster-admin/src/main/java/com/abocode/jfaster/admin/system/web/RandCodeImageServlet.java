/**
 *
 */
package com.abocode.jfaster.admin.system.web;

import com.abocode.jfaster.core.common.util.ConfigUtils;
import com.abocode.jfaster.core.common.util.IdUtils;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 随机生成验证图片
 */
@Slf4j
public class RandCodeImageServlet extends HttpServlet {
    private static final String SESSION_KEY_OF_RAND_CODE = "randCode";
    /**
     *
     */
    private static final int COUNT = 200;

    /**
     * 定义图形大小
     */
    private static final int WIDTH = 105;
    /**
     * 定义图形大小
     */
    private static final int HEIGHT = 35;
    /**
     * 干扰线的长度=1.414*lineWidth
     */
    private static final int LINE_WIDTH = 2;

    @Override
    public void doGet(final HttpServletRequest request,
                      final HttpServletResponse response) {
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 在内存中创建图象
        final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        final Graphics2D graphics = (Graphics2D) image.getGraphics();

        // 设定背景颜色
        graphics.setColor(Color.WHITE); // ---1
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        // 设定边框颜色
//		graphics.setColor(getRandColor(100, 200)); // ---2
        graphics.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        // 随机产生干扰线，使图象中的认证码不易被其它程序探测到
        for (int i = 0; i < COUNT; i++) {
            graphics.setColor(getRandColor(150, 200)); // ---3

            final int x = IdUtils.nextInt(WIDTH - LINE_WIDTH - 1) + 1; // 保证画在边框之内
            final int y = IdUtils.nextInt(HEIGHT - LINE_WIDTH - 1) + 1;
            final int xl = IdUtils.nextInt(LINE_WIDTH);
            final int yl = IdUtils.nextInt(LINE_WIDTH);
            graphics.drawLine(x, y, x + xl, y + yl);
        }

        // 取随机产生的认证码(4位数字)
        String resultCode = getRandomCode();
        for (int i = 0; i < resultCode.length(); i++) {
            // 将认证码显示到图象中,调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            // 设置字体颜色
            graphics.setColor(Color.BLACK);
            // 设置字体样式
            graphics.setFont(new Font("Times New Roman", Font.BOLD, 24));
            // 设置字符，字符间距，上边距
            graphics.drawString(String.valueOf(resultCode.charAt(i)), (23 * i) + 8, 26);
        }

        // 将认证码存入SESSION
        request.getSession().setAttribute(SESSION_KEY_OF_RAND_CODE, resultCode);
        // 图象生效
        graphics.dispose();

        // 输出图象到页面
        try {
            ImageIO.write(image, "JPEG", response.getOutputStream());
        } catch (IOException e) {
            log.error("JPEG write not successful",e);
        }
    }

    @Override
    public void doPost(final HttpServletRequest request,
                       final HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    /**
     * @return 随机码
     */
    private String getRandomCode() {
        String randomCodeType = ConfigUtils.getRandCodeType();
        RandCodeImageEnum randCodeImageEnum = RandCodeImageEnum.getRandCodeImage(randomCodeType);
        int randCodeLength = Integer.parseInt(ConfigUtils.getRandCodeLength());
        return randCodeImageEnum.generateStr(randCodeLength);
    }

    /**
     * 描述：
     *
     * @param fc
     *            描述：
     * @param bc
     *            描述：
     *
     * @return 描述：
     */
    private Color getRandColor(int fc, int bc) { // 取得给定范围随机颜色
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }

        final int r = fc + IdUtils.nextInt(bc - fc);
        final int g = fc + IdUtils.nextInt(bc - fc);
        final int b = fc + IdUtils.nextInt(bc - fc);

        return new Color(r, g, b);
    }
}

/**
 * 验证码辅助类
 *
 * @author 张国明 guomingzhang2008@gmail.com <br/>
 *         2012-2-28 下午2:15:14
 *
 */
enum RandCodeImageEnum {
    /**
     * 混合字符串
     */
    ALL_CHAR("0123456789abcdefghijkmnpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"), // 去除小写的l和o这个两个不容易区分的字符；
    /**
     * 字符
     */
    LETTER_CHAR("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    /**
     * 小写字母
     */
    LOWER_CHAR("abcdefghijklmnopqrstuvwxyz"),
    /**
     * 数字
     */
    NUMBER_CHAR("0123456789"),
    /**
     * 大写字符
     */
    UPPER_CHAR("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    /**
     * 待生成的字符串
     */
    private String charStr;

    /**
     * @param charStr
     */
    RandCodeImageEnum(final String charStr) {
        this.charStr = charStr;
    }

    public static RandCodeImageEnum getRandCodeImage(String randomCodeType) {
        if (randomCodeType != null) {
            switch (randomCodeType.charAt(0)) {
                case '2':
                    return RandCodeImageEnum.LOWER_CHAR;
                case '3':
                    return RandCodeImageEnum.UPPER_CHAR;
                case '4':
                    return RandCodeImageEnum.LETTER_CHAR;
                case '5':
                    return RandCodeImageEnum.ALL_CHAR;
                default:
                    return RandCodeImageEnum.NUMBER_CHAR;
            }
        }
        return RandCodeImageEnum.NUMBER_CHAR;
    }

    /**
     * 生产随机验证码
     *
     * @param codeLength
     *            验证码的长度
     * @return 验证码
     */
    public String generateStr(final int codeLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(charStr.charAt(IdUtils.nextInt(charStr.length())));
        }
        return sb.toString();
    }

}