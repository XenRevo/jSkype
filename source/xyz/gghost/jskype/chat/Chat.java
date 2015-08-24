package xyz.gghost.jskype.chat;

import org.apache.commons.lang3.StringEscapeUtils;

public class Chat {

    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String bold(String text) {
        return "<b raw_pre=\"*\" raw_post=\"*\">" + text + "</b>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String italic(String text) {
        return "<i raw_pre=\"_\" raw_post=\"_\">" + text + "</i>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String strikethrough(String text) {
        return "<s raw_pre=\"~\" raw_post=\"~\">" + text + "</s>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String blink(String text) {
        return "<blink>" + text + "</blink>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String underline(String text) {
        return "<u>" + text + "</u>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String code(String text) {
        return "<pre>" + text + "</pre>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String link(String url) {
        return "<a href=\"" + url + "\">" + url + "</a>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String size(String text, int size) {
        return "<font size=\"" + size + "\">" + String.valueOf(text) + "</font>";
    }
    /**
     * Format text
     * @param
     * @return Formatted text
     */
    public static String encodeText(String text){
        return StringEscapeUtils.escapeHtml4(text);
    }
    /**
     * Add emoji to text
     */
    public static String emoji(String emoji){
        return "<ss type=\""+ emoji.replace("(", "").replace(")", "") + "\">"+ emoji + "</ss>";
    }
}
