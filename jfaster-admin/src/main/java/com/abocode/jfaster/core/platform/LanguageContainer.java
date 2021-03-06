package com.abocode.jfaster.core.platform;

import com.abocode.jfaster.core.common.util.BrowserUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/***
 * 语言容器
 */
public class LanguageContainer {
    private LanguageContainer() {
    }

    private static Map<String, String> languageMap = new HashMap<>();
    //FunctionName的keymap
    private static Map<String, String> languageKeyCodeMap = new HashMap<>();

    public static Map<String, String> getLanguageMap() {
        return languageMap;
    }

    public static Map<String, String> getLanguageKeyCodeMap() {
        return languageKeyCodeMap;
    }

    /**
     * 获取语言
     *
     * @param lanKey
     * @param langArg
     * @return
     */
    public static String getLang(String lanKey, String langArg) {
        String langContext;
        if (StringUtils.isEmpty(langArg)) {
            langContext = getLang(lanKey);
        } else {
            String[] argArray = langArg.split(",");
            langContext = getLang(lanKey);
            for (int i = 0; i < argArray.length; i++) {
                String langKeyArg = argArray[i].trim();
                String langKeyContext = getLang(langKeyArg);
                langContext = StringUtils.replace(langContext, "{" + i + "}", langKeyContext);
            }
        }
        return langContext;
    }

    /**
     * 获取语言
     *
     * @param langKey
     * @return
     */
    public static String getLang(String langKey) {
        String language = BrowserUtils.getBrowserLanguage();
        String langContext = LanguageContainer.getLanguageMap().get(langKey + "_" + language);
        if (StringUtils.isEmpty(langContext)) {
          return  langKey;
        }
        return langContext;
    }



    /***
     * 判断语言内容是否存在
     *
     * @param langContext
     * @return
     */
    public static boolean existLangContext(String langContext) {
        String map = languageKeyCodeMap.get(langContext);
        return  !StringUtils.isEmpty(map);
    }


}
