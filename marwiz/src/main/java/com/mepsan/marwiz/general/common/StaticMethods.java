/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   06.09.2016 17:28:25
 */
package com.mepsan.marwiz.general.common;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.wot.ExcelDocument;
import com.mepsan.marwiz.general.model.wot.PdfDocument;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StaticMethods {

    public static String convertToDateFormat(String format, Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        }
        return null;
    }

    public static String convertToDateFormatWithSeconds(String format, Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format + " HH:mm:ss");
            return sdf.format(date);
        }
        return null;
    }

    public static String convertToDateFormatWithMinute(String format, Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format + " HH:mm");
            return sdf.format(date);
        }
        return null;
    }

    public static Date convertStringToDate(String format, String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        if (!stringDate.isEmpty()) {
            try {
                date = sdf.parse(stringDate);
            } catch (ParseException ex) {
                Logger.getLogger(StaticMethods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return date;
    }

    public static List<Integer> getItemId(String tableName) {

        switch (tableName) {
            case "general.account":
                return new ArrayList<>(Arrays.asList(new Integer[]{3}));
            case "inventory.stock":
                return new ArrayList<>(Arrays.asList(new Integer[]{2}));
            case "inventory.pricelist":
                return new ArrayList<>(Arrays.asList(new Integer[]{6}));
            default:
                return new ArrayList<>(Arrays.asList(new Integer[]{0}));
        }
    }

    /**
     *
     * @param document
     * @throws IOException
     * @throws BadElementException
     * @throws DocumentException
     */
    public static void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {

        Document pdf = (Document) document;
        pdf.setPageSize(PageSize.A3);

        ///  HeaderFooter headerName = new HeaderFooter(Phrase.getInstance("sirketler"), true);
        //  pdf.setHeader(headerName);
        pdf.open();

    }

    /**
     *
     * @param document
     */
    public static void postProcessXLS(Object document) {

//        HSSFWorkbook wb = (HSSFWorkbook) document;
//        HSSFSheet sheet = wb.getSheetAt(0);
//
//        HSSFRow header = sheet.getRow(0);
//
//        HSSFCellStyle cellStyle = wb.createCellStyle();
//        HSSFFont font = wb.createFont();
//        font.setBold(true);
//        
//        cellStyle.setBorderRight(BorderStyle.MEDIUM);
//        cellStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        sheet.setDefaultColumnWidth(23);
//
//        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
//            HSSFCell cell = header.getCell(i);
//            cell.setCellStyle(cellStyle);
//         
//        }
    }

    /**
     * Export edilen dosyanın adındaki türkçe karakterlerin de yazılmasını
     * sağlayan methodtur.
     *
     * @param filename
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeFileNameForExportData(String filename) throws UnsupportedEncodingException {

        String title = URLDecoder.decode(filename, "UTF-8");
        System.out.println("t" + title);

        StringBuilder fileName = new StringBuilder(title);

        if (title.contains("+")) {
            for (int i = 0; i < title.length(); i++) {
                if (title.charAt(i) == '+') {
                    fileName.setCharAt(i, ' ');
                }
            }
        }
        return fileName.toString();

    }

    public static String encodeFileNameForExportDataPdf(String filename) throws UnsupportedEncodingException {

        String title = URLEncoder.encode(filename, "UTF-8");

        StringBuilder fileName = new StringBuilder(title);

        if (title.contains("+")) {
            for (int i = 0; i < title.length(); i++) {
                if (title.charAt(i) == '+') {
                    fileName.setCharAt(i, ' ');
                }
            }
        }
        filename = new String(fileName);

        return clearTurkishChars(filename);
    }

    public static String convertToDateFormatWithSecondsForFtp(String format, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format + "_HH_mm_ss");
        return sdf.format(date);
    }

    public static String clearTurkishChars(String str) {
        String ret = str;
        char[] turkishChars = new char[]{0x131, 0x130, 0xFC, 0xDC, 0xF6, 0xD6, 0x15F, 0x15E, 0xE7, 0xC7, 0x11F, 0x11E};
        char[] englishChars = new char[]{'i', 'I', 'u', 'U', 'o', 'O', 's', 'S', 'c', 'C', 'g', 'G'};
        for (int i = 0; i < turkishChars.length; i++) {
            ret = ret.replaceAll(new String(new char[]{turkishChars[i]}), new String(new char[]{englishChars[i]}));
        }
        return ret;
    }

    /**
     *
     * @param locale ayların adlarının döneceği dil
     * @param style Calendar #Calendar.SHORT_FORMAT, Calendar.LONG_FORMAT,
     * Calendar.NARROW_FORMAT
     * @return ayların tamsayı değerleriyle anahtarlanmış haritası
     */
    public static Map<Integer, String> getMonths(Locale locale, int style) {
        HashMap<Integer, String> months = new HashMap<>();
        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
        int actualMaximum = calendar.getActualMaximum(Calendar.MONTH);
        int actualMinimum = calendar.getActualMinimum(Calendar.MONTH);
        for (int i = actualMinimum; i < (actualMaximum + 1); i++) {
            calendar.set(Calendar.MONTH, i);
            String monthString = calendar.getDisplayName(Calendar.MONTH, style, locale);
            months.put(i, monthString);
        }
        return months;
    }

    /**
     *
     * @param locale haftanın günlerinin adlarının döneceği dil
     * @param style Calendar #Calendar.SHORT_FORMAT, Calendar.LONG_FORMAT,
     * Calendar.NARROW_FORMAT
     * @return haftanın günlerinin tamsayı değerleriyle anahtarlanmış haritası
     */
    public static Map<Integer, String> getWeekDays(Locale locale, int style) {
        HashMap<Integer, String> daysOfWeek = new HashMap<>();
        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_WEEK);
        int actualMinimum = calendar.getActualMinimum(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        for (int i = firstDayOfWeek; i < actualMaximum + firstDayOfWeek; i++) {
            System.out.println(i);
            int dayofweek = ((i - actualMinimum) % actualMaximum) + actualMinimum;
            calendar.set(Calendar.DAY_OF_WEEK, dayofweek);
            String dayOfWeekString = calendar.getDisplayName(Calendar.DAY_OF_WEEK, style, locale);
            daysOfWeek.put(dayofweek, dayOfWeekString);
        }
        return daysOfWeek;
    }

    public static int getFileType(String extention) {
        String[] resim = {"jpg", "jpeg", "png", "gıf", "bmp", "tıff", "psd", "eps", "raw", "pıct"};
        String[] ses = {"mp3", "wma", "aac", "vorbis", "pcm", "wav", "aıff", "flac", "alac", "ape"};
        String[] video = {"avı", "divx", "mpg", "mpeg", "dat", "flv", "wmv", "mp4", "mov", "asf", "mkv"};

        if (Arrays.asList(resim).contains(extention)) {
            return 0;
        } else if (Arrays.asList(ses).contains(extention)) {
            return 3;
        } else if (Arrays.asList(video).contains(extention)) {
            return 2;
        } else {
            return 1;
        }

    }

    public static List<Page> delete(JdbcTemplate jdbcTemplate, String sql, int type) {
        List<Page> list = new ArrayList<>();
        if (type == 0) {
            list = jdbcTemplate.query(sql, new RowMapper<Page>() {
                @Override
                public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Page page = new Page();
                    page.setId(rs.getInt("id"));
                    page.setParent_id(new Page(rs.getInt("parent_id"), null));
                    // page.setNameMap(rs.getString("name"));
                    return page;
                }
            });

        } else {
            try {
                jdbcTemplate.update(sql);
                list.add(new Page(1, null));
            } catch (Exception e) {
                list.add(new Page(-Integer.valueOf(((SQLException) e.getCause()).getSQLState()), null));
            }
        }
        return list;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static BigDecimal round(BigDecimal value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        value = value.setScale(places, RoundingMode.HALF_EVEN);
        return value;

    }

    public static String formatXml(String xml) {
        String result = "";
        String[] lines = xml.split("\\s*\\r?\\n\\s*");
        for (String line : lines) {
            result += line;
        }
        return result;
    }

    public static List<Integer> stringToList(String list, String sep) {
        List<Integer> pages = new ArrayList<>();
        if (list != null) {
            if (list.contains(sep)) {
                String[] slist = list.split(sep);
                for (String a : slist) {
                    if (a.length() > 0) {
                        pages.add(Integer.valueOf(a));
                    }
                }
            } else if (list.length() > 0) {
                pages.add(Integer.valueOf(list));
            }

        }
        return pages;
    }

    public static List<String> iconList() {
        List<String> availableIconStrings = new ArrayList<>();
        availableIconStrings.addAll(Arrays
                .asList(("icon-laptop,icon-tablet,icon-mobile,icon-inbox\n"
                        + ",icon-globe,icon-sun,icon-cloud,icon-flash\n"
                        + ",icon-moon,icon-umbrella,icon-flight,icon-fighter-jet\n"
                        + ",icon-paper-plane,icon-paper-plane-empty,icon-space-shuttle,icon-leaf\n"
                        + ",icon-font,icon-bold,icon-italic,icon-header\n"
                        + ",icon-paragraph,icon-text-height,icon-text-width,icon-align-left\n"
                        + ",icon-align-center,icon-align-right,icon-align-justify,icon-list\n"
                        + ",icon-indent-left,icon-indent-right,icon-list-bullet,icon-list-numbered\n"
                        + ",icon-strike,icon-underline,icon-superscript,icon-subscript\n"
                        + ",icon-table,icon-columns,icon-crop,icon-scissors\n"
                        + ",icon-paste,icon-briefcase,icon-suitcase,icon-ellipsis\n"
                        + ",icon-ellipsis-vert,icon-off,icon-road,icon-list-alt\n"
                        + ",icon-qrcode,icon-barcode,icon-book,icon-ajust\n"
                        + ",icon-tint,icon-check,icon-check-empty,icon-circle\n"
                        + ",icon-circle-empty,icon-circle-thin,icon-circle-notch,icon-dot-circled\n"
                        + ",icon-asterisk,icon-gift,icon-fire,icon-magnet\n"
                        + ",icon-chart-bar,icon-ticket,icon-credit-card,icon-floppy\n"
                        + ",icon-megaphone,icon-hdd,icon-key,icon-fork\n"
                        + ",icon-rocket,icon-bug,icon-certificate,icon-tasks\n"
                        + ",icon-filter,icon-beaker,icon-magic,icon-cab\n"
                        + ",icon-taxi,icon-truck,icon-money,icon-euro\n"
                        + ",icon-pound,icon-dollar,icon-rupee,icon-yen\n"
                        + ",icon-rouble,icon-try,icon-won,icon-bitcoin\n"
                        + ",icon-sort,icon-sort-down,icon-sort-up,icon-sort-alt-up\n"
                        + ",icon-sort-alt-down,icon-sort-name-up,icon-sort-name-down,icon-sort-number-up\n"
                        + ",icon-sort-number-down,icon-hammer,icon-gauge,icon-sitemap\n"
                        + ",icon-spinner,icon-coffee,icon-food,icon-beer\n"
                        + ",icon-user-md,icon-stethoscope,icon-ambulance,icon-medkit\n"
                        + ",icon-h-sigh,icon-hospital,icon-building,icon-building-filled\n"
                        + ",icon-bank,icon-smile,icon-frown,icon-meh\n"
                        + ",icon-anchor,icon-terminal,icon-eraser,icon-puzzle\n"
                        + ",icon-shield,icon-extinguisher,icon-bullseye,icon-wheelchair\n"
                        + ",icon-language,icon-graduation-cap,icon-paw,icon-spoon\n"
                        + ",icon-cube,icon-cubes,icon-recycle,icon-tree\n"
                        + ",icon-database,icon-lifebuoy,icon-rebel,icon-empire\n"
                        + ",icon-bomb,icon-adn,icon-android,icon-apple\n"
                        + ",icon-behance,icon-behance-squared,icon-bitbucket,icon-bitbucket-squared\n"
                        + ",icon-codeopen,icon-css3,icon-delicious,icon-deviantart\n"
                        + ",icon-digg,icon-dribbble,icon-dropbox,icon-drupal\n"
                        + ",icon-facebook,icon-facebook-squared,icon-flickr,icon-foursquare\n"
                        + ",icon-git-squared,icon-git,icon-github,icon-github-squared\n"
                        + ",icon-github-circled,icon-gittip,icon-google,icon-gplus\n"
                        + ",icon-gplus-squared,icon-hacker-news,icon-html5,icon-instagramm\n"
                        + ",icon-joomla,icon-jsfiddle,icon-linkedin-squared,icon-linux\n"
                        + ",icon-linkedin,icon-maxcdn,icon-openid,icon-pagelines\n"
                        + ",icon-pied-piper-squared,icon-pied-piper-alt,icon-pinterest-circled,icon-pinterest-squared\n"
                        + ",icon-qq,icon-reddit,icon-reddit-squared,icon-renren\n"
                        + ",icon-skype,icon-slack,icon-soundclowd,icon-spotify\n"
                        + ",icon-stackexchange,icon-stackoverflow,icon-steam,icon-steam-squared\n"
                        + ",icon-stumbleupon,icon-stumbleupon-circled,icon-tencent-weibo,icon-trello\n"
                        + ",icon-tumblr,icon-tumblr-squared,icon-twitter-squared,icon-twitter\n"
                        + ",icon-vimeo-squared,icon-vine,icon-vkontakte,icon-wechat\n"
                        + ",icon-weibo,icon-windows,icon-wordpress,icon-xing\n"
                        + ",icon-xing-squared,icon-youtube,icon-yahoo,icon-youtube-squared\n"
                        + ",icon-youtube-play,icon-blank,icon-lemon,icon-glass\n"
                        + ",icon-music,icon-search,icon-mail,icon-mail-alt\n"
                        + ",icon-mail-squared,icon-heart,icon-heart-empty,icon-star\n"
                        + ",icon-star-empty,icon-star-half,icon-star-half-alt,icon-user\n"
                        + ",icon-users,icon-male,icon-female,icon-child\n"
                        + ",icon-video,icon-videocam,icon-picture,icon-camera\n"
                        + ",icon-camera-alt,icon-th-large,icon-th,icon-th-list\n"
                        + ",icon-ok,icon-ok-circled,icon-ok-circled2,icon-ok-squared\n"
                        + ",icon-cancel,icon-cancel-circled,icon-cancel-circled2,icon-plus\n"
                        + ",icon-plus-circled,icon-plus-squared,icon-plus-squared-alt,icon-minus\n"
                        + ",icon-minus-circled,icon-minus-squared,icon-minus-squared-alt,icon-help\n"
                        + ",icon-help-circled,icon-info-circled,icon-info,icon-home\n"
                        + ",icon-link,icon-unlink,icon-link-ext,icon-link-ext-alt\n"
                        + ",icon-attach,icon-lock,icon-lock-open,icon-lock-open-alt\n"
                        + ",icon-pin,icon-eye,icon-eye-off,icon-tag\n"
                        + ",icon-tags,icon-bookmark,icon-bookmark-empty,icon-flag\n"
                        + ",icon-flag-empty,icon-flag-checkered,icon-thumbs-up,icon-thumbs-down\n"
                        + ",icon-thumbs-up-alt,icon-thumbs-down-alt,icon-download,icon-upload\n"
                        + ",icon-download-cloud,icon-upload-cloud,icon-reply,icon-reply-all\n"
                        + ",icon-forward,icon-quote-left,icon-quote-right,icon-code\n"
                        + ",icon-export,icon-export-alt,icon-share,icon-share-squared\n"
                        + ",icon-pencil,icon-pencil-squared,icon-edit,icon-print\n"
                        + ",icon-retweet,icon-keyboard,icon-gamepad,icon-comment\n"
                        + ",icon-chat,icon-comment-empty,icon-chat-empty,icon-bell\n"
                        + ",icon-bell-alt,icon-attention-alt,icon-attention,icon-attention-circled\n"
                        + ",icon-location,icon-direction,icon-compass,icon-trash\n"
                        + ",icon-doc,icon-docs,icon-doc-text,icon-doc-inv\n"
                        + ",icon-doc-text-inv,icon-file-pdf,icon-file-word,icon-file-excel\n"
                        + ",icon-file-powerpoint,icon-file-image,icon-file-archive,icon-file-audio\n"
                        + ",icon-file-video,icon-file-code,icon-folder,icon-folder-open\n"
                        + ",icon-folder-empty,icon-folder-open-empty,icon-box,icon-rss\n"
                        + ",icon-rss-squared,icon-phone,icon-phone-squared,icon-fax\n"
                        + ",icon-menu,icon-cog,icon-cog-alt,icon-wrench\n"
                        + ",icon-sliders,icon-basket,icon-calendar,icon-calendar-empty\n"
                        + ",icon-login,icon-logout,icon-mic,icon-mute\n"
                        + ",icon-volume-off,icon-volume-down,icon-volume-up,icon-headphones\n"
                        + ",icon-clock,icon-lightbulb,icon-block,icon-resize-full\n"
                        + ",icon-resize-full-alt,icon-resize-small,icon-resize-vertical,icon-resize-horizontal\n"
                        + ",icon-move,icon-zoom-in,icon-zoom-out,icon-down-circled2\n"
                        + ",icon-up-circled2,icon-left-circled2,icon-right-circled2,icon-down-dir\n"
                        + ",icon-up-dir,icon-left-dir,icon-right-dir,icon-down-open\n"
                        + ",icon-left-open,icon-right-open,icon-up-open,icon-angle-left\n"
                        + ",icon-angle-right,icon-angle-up,icon-angle-down,icon-angle-circled-left\n"
                        + ",icon-angle-circled-right,icon-angle-circled-up,icon-angle-circled-down,icon-angle-double-left\n"
                        + ",icon-angle-double-right,icon-angle-double-up,icon-angle-double-down,icon-down\n"
                        + ",icon-left,icon-right,icon-up,icon-down-big\n"
                        + ",icon-left-big,icon-right-big,icon-up-big,icon-right-hand\n"
                        + ",icon-left-hand,icon-up-hand,icon-down-hand,icon-left-circled\n"
                        + ",icon-right-circled,icon-up-circled,icon-down-circled,icon-cw\n"
                        + ",icon-ccw,icon-arrows-cw,icon-level-up,icon-level-down\n"
                        + ",icon-shuffle,icon-exchange,icon-history,icon-expand\n"
                        + ",icon-collapse,icon-expand-right,icon-collapse-left,icon-play\n"
                        + ",icon-play-circled,icon-play-circled2,icon-stop,icon-pause\n"
                        + ",icon-to-end,icon-to-end-alt,icon-to-start,icon-to-start-alt\n"
                        + ",icon-fast-fw,icon-fast-bw,icon-eject,icon-target\n"
                        + ",icon-signal,icon-award,icon-desktop,icon-music-outline\n"
                        + ",icon-music-1,icon-search-outline,icon-search-1,icon-mail-1\n"
                        + ",icon-heart-1,icon-heart-filled,icon-star-1,icon-star-filled\n"
                        + ",icon-user-outline,icon-user-1,icon-users-outline,icon-users-1\n"
                        + ",icon-user-add-outline,icon-user-add,icon-user-delete-outline,icon-user-delete\n"
                        + ",icon-video-1,icon-videocam-outline,icon-videocam-1,icon-picture-outline\n"
                        + ",icon-picture-1,icon-camera-outline,icon-camera-1,icon-th-outline\n"
                        + ",icon-th-1,icon-th-large-outline,icon-th-large-1,icon-th-list-outline\n"
                        + ",icon-th-list-1,icon-ok-outline,icon-ok-1,icon-cancel-outline\n"
                        + ",icon-cancel-1,icon-cancel-alt,icon-cancel-alt-filled,icon-cancel-circled-outline\n"
                        + ",icon-cancel-circled-1,icon-plus-outline,icon-plus-1,icon-minus-outline\n"
                        + ",icon-minus-1,icon-divide-outline,icon-divide,icon-eq-outline\n"
                        + ",icon-eq,icon-info-outline,icon-info-1,icon-home-outline\n"
                        + ",icon-home-1,icon-link-outline,icon-link-1,icon-attach-outline\n"
                        + ",icon-attach-1,icon-lock-1,icon-lock-filled,icon-lock-open-1\n"
                        + ",icon-lock-open-filled,icon-pin-outline,icon-pin-1,icon-eye-outline\n"
                        + ",icon-eye-1,icon-tag-1,icon-tags-1,icon-bookmark-1\n"
                        + ",icon-flag-1,icon-flag-filled,icon-thumbs-up-1,icon-thumbs-down-1\n"
                        + ",icon-download-outline,icon-download-1,icon-upload-outline,icon-upload-1\n"
                        + ",icon-upload-cloud-outline,icon-upload-cloud-1,icon-reply-outline,icon-reply-1\n"
                        + ",icon-forward-outline,icon-forward-1,icon-code-outline,icon-code-1\n"
                        + ",icon-export-outline,icon-export-1,icon-pencil-1,icon-pen\n"
                        + ",icon-feather,icon-edit-1,icon-print-1,icon-comment-1\n"
                        + ",icon-chat-1,icon-chat-alt,icon-bell-1,icon-attention-1\n"
                        + ",icon-attention-filled,icon-warning-empty,icon-warning,icon-contacts\n"
                        + ",icon-vcard,icon-address,icon-location-outline,icon-location-1\n"
                        + ",icon-map,icon-direction-outline,icon-direction-1,icon-compass-1\n"
                        + ",icon-trash-1,icon-doc-1,icon-doc-text-1,icon-doc-add\n"
                        + ",icon-doc-remove,icon-news,icon-folder-1,icon-folder-add\n"
                        + ",icon-folder-delete,icon-archive,icon-box-1,icon-rss-outline\n"
                        + ",icon-rss-1,icon-phone-outline,icon-phone-1,icon-menu-outline\n"
                        + ",icon-menu-1,icon-cog-outline,icon-cog-1,icon-wrench-outline\n"
                        + ",icon-wrench-1,icon-basket-1,icon-calendar-outlilne,icon-calendar-1\n"
                        + ",icon-mic-outline,icon-mic-1,icon-volume-off-1,icon-volume-low\n"
                        + ",icon-volume-middle,icon-volume-high,icon-headphones-1,icon-clock-1\n"
                        + ",icon-wristwatch,icon-stopwatch,icon-lightbulb-1,icon-block-outline\n"
                        + ",icon-block-1,icon-resize-full-outline,icon-resize-full-1,icon-resize-normal-outline\n"
                        + ",icon-resize-normal,icon-move-outline,icon-move-1,icon-popup\n"
                        + ",icon-zoom-in-outline,icon-zoom-in-1,icon-zoom-out-outline,icon-zoom-out-1\n"
                        + ",icon-popup-1,icon-left-open-outline,icon-left-open-1,icon-right-open-outline\n"
                        + ",icon-right-open-1,icon-down-1,icon-left-1,icon-right-1\n"
                        + ",icon-up-1,icon-down-outline,icon-left-outline,icon-right-outline\n"
                        + ",icon-up-outline,icon-down-small,icon-left-small,icon-right-small\n"
                        + ",icon-up-small,icon-cw-outline,icon-cw-1,icon-arrows-cw-outline\n"
                        + ",icon-arrows-cw-1,icon-loop-outline,icon-loop,icon-loop-alt-outline\n"
                        + ",icon-loop-alt,icon-shuffle-1,icon-play-outline,icon-play-1\n"
                        + ",icon-stop-outline,icon-stop-1,icon-pause-outline,icon-pause-1\n"
                        + ",icon-fast-fw-outline,icon-fast-fw-1,icon-rewind-outline,icon-rewind\n"
                        + ",icon-record-outline,icon-record,icon-eject-outline,icon-eject-1\n"
                        + ",icon-eject-alt-outline,icon-eject-alt,icon-bat1,icon-bat2\n"
                        + ",icon-bat3,icon-bat4,icon-bat-charge,icon-plug\n"
                        + ",icon-target-outline,icon-target-1,icon-wifi-outline,icon-wifi\n"
                        + ",icon-desktop-1,icon-laptop-1,icon-tablet-1,icon-mobile-1\n"
                        + ",icon-contrast,icon-globe-outline,icon-globe-1,icon-globe-alt-outline\n"
                        + ",icon-globe-alt,icon-sun-1,icon-sun-filled,icon-cloud-1\n"
                        + ",icon-flash-outline,icon-flash-1,icon-moon-1,icon-waves-outline\n"
                        + ",icon-waves,icon-rain,icon-cloud-sun,icon-drizzle\n"
                        + ",icon-snow,icon-cloud-flash,icon-cloud-wind,icon-wind\n"
                        + ",icon-plane-outline,icon-plane,icon-leaf-1,icon-lifebuoy-1\n"
                        + ",icon-briefcase-1,icon-brush,icon-pipette,icon-power-outline\n"
                        + ",icon-power,icon-check-outline,icon-check-1,icon-gift-1\n"
                        + ",icon-temperatire,icon-chart-outline,icon-chart,icon-chart-alt-outline\n"
                        + ",icon-chart-alt,icon-chart-bar-outline,icon-chart-bar-1,icon-chart-pie-outline\n"
                        + ",icon-chart-pie,icon-ticket-1,icon-credit-card-1,icon-clipboard\n"
                        + ",icon-database-1,icon-key-outline,icon-key-1,icon-flow-split\n"
                        + ",icon-flow-merge,icon-flow-parallel,icon-flow-cross,icon-certificate-outline\n"
                        + ",icon-certificate-1,icon-scissors-outline,icon-scissors-1,icon-flask\n"
                        + ",icon-wine,icon-coffee-1,icon-beer-1,icon-anchor-outline\n"
                        + ",icon-anchor-1,icon-puzzle-outline,icon-puzzle-1,icon-tree-1\n"
                        + ",icon-calculator,icon-infinity-outline,icon-infinity,icon-pi-outline\n"
                        + ",icon-pi,icon-at,icon-at-circled,icon-looped-square-outline\n"
                        + ",icon-looped-square-interest,icon-sort-alphabet-outline,icon-sort-alphabet,icon-sort-numeric-outline\n"
                        + ",icon-sort-numeric,icon-dribbble-circled,icon-dribbble-1,icon-facebook-circled\n"
                        + ",icon-facebook-1,icon-flickr-circled,icon-flickr-1,icon-github-circled-1\n"
                        + ",icon-github-1,icon-lastfm-circled,icon-lastfm,icon-linkedin-circled\n"
                        + ",icon-linkedin-1,icon-pinterest-circled-1,icon-pinterest,icon-skype-outline\n"
                        + ",icon-skype-1,icon-tumbler-circled,icon-tumbler,icon-twitter-circled\n"
                        + ",icon-twitter-1,icon-vimeo-circled,icon-vimeo,icon-search-2\n"
                        + ",icon-mail-2,icon-heart-2,icon-heart-broken,icon-star-2\n"
                        + ",icon-star-empty-1,icon-star-half-1,icon-star-half_empty,icon-user-2\n"
                        + ",icon-user-male,icon-user-female,icon-users-2,icon-movie\n"
                        + ",icon-ok-2,icon-ok-circled-1,icon-cancel-2,icon-cancel-circled-2\n"
                        + ",icon-plus-2,icon-help-circled-1,icon-help-circled-alt,icon-info-circled-1\n"
                        + ",icon-info-circled-alt,icon-home-2,icon-link-2,icon-attach-2\n"
                        + ",icon-lock-2,icon-upload-cloud-2,icon-reply-2,icon-pencil-2\n"
                        + ",icon-export-2,icon-print-2,icon-retweet-1,icon-comment-2\n"
                        + ",icon-chat-2,icon-bell-2,icon-attention-2,icon-attention-alt-1\n"
                        + ",icon-location-2,icon-trash-2,icon-calendar-2,icon-login-1\n"
                        + ",icon-logout-1,icon-mic-2,icon-mic-off,icon-clock-2\n"
                        + ",icon-stopwatch-1,icon-hourglass,icon-zoom-in-2,icon-zoom-out-2\n"
                        + ",icon-down-open-1,icon-left-open-2,icon-right-open-2,icon-right-bold\n"
                        + ",icon-up-bold,icon-down-fat,icon-left-fat,icon-right-fat\n"
                        + ",icon-up-fat,icon-ccw-1,icon-shuffle-2,icon-play-2\n"
                        + ",icon-pause-2,icon-stop-2,icon-to-end-1,icon-to-start-1\n"
                        + ",icon-data-science-inv,icon-inbox-1,icon-globe-2,icon-globe-inv\n"
                        + ",icon-flash-2,icon-cloud-2,icon-coverflow,icon-coverflow-empty\n"
                        + ",icon-math,icon-math-circled,icon-math-circled-empty,icon-paper-plane-1\n"
                        + ",icon-paper-plane-alt,icon-ruler,icon-vector,icon-vector-pencil\n"
                        + ",icon-at-1,icon-hash,icon-female-1,icon-male-1\n"
                        + ",icon-spread,icon-king,icon-anchor-2,icon-joystick\n"
                        + ",icon-spinner1,icon-spinner2,icon-videocam-2,icon-isight\n"
                        + ",icon-camera-2,icon-menu-2,icon-th-thumb,icon-th-thumb-empty\n"
                        + ",icon-th-list-2,icon-lock-alt,icon-lock-open-2,icon-lock-open-alt-1\n"
                        + ",icon-eye-2,icon-download-2,icon-upload-2,icon-download-cloud-1\n"
                        + ",icon-doc-2,icon-newspaper,icon-folder-2,icon-folder-open-1\n"
                        + ",icon-folder-empty-1,icon-folder-open-empty-1,icon-cog-2,icon-up-open-1\n"
                        + ",icon-down-2,icon-left-2,icon-right-2,icon-up-2\n"
                        + ",icon-down-bold,icon-left-bold,icon-fast-forward,icon-fast-backward\n"
                        + ",icon-trophy,icon-monitor,icon-tablet-2,icon-mobile-2\n"
                        + ",icon-data-science,icon-paper-plane-alt2,icon-fontsize,icon-color-adjust\n"
                        + ",icon-fire-1,icon-chart-bar-2,icon-hdd-1,icon-connected-object\n"
                        + ",icon-windy-rain-inv,icon-snow-inv,icon-snow-heavy-inv,icon-hail-inv\n"
                        + ",icon-clouds-inv,icon-clouds-flash-inv,icon-temperature,icon-compass-2\n"
                        + ",icon-na,icon-celcius,icon-fahrenheit,icon-clouds-flash-alt\n"
                        + ",icon-sun-inv,icon-moon-inv,icon-cloud-sun-inv,icon-cloud-moon-inv\n"
                        + ",icon-cloud-inv,icon-cloud-flash-inv,icon-drizzle-inv,icon-rain-inv\n"
                        + ",icon-windy-inv,icon-sunrise,icon-sun-2,icon-moon-2\n"
                        + ",icon-eclipse,icon-mist,icon-wind-1,icon-snowflake\n"
                        + ",icon-cloud-sun-1,icon-cloud-moon,icon-fog-sun,icon-fog-moon\n"
                        + ",icon-fog-cloud,icon-fog,icon-cloud-3,icon-cloud-flash-1\n"
                        + ",icon-cloud-flash-alt,icon-drizzle-1,icon-rain-1,icon-windy\n"
                        + ",icon-windy-rain,icon-snow-1,icon-snow-alt,icon-snow-heavy\n"
                        + ",icon-hail,icon-clouds,icon-clouds-flash").split(",")));

        return availableIconStrings;
    }

    public static PdfDocument preparePdf(List<Boolean> toogleList, int orientation) {
        PdfDocument pdfdoc = new PdfDocument();
        int numberOfColumns = 0;

        for (boolean b : toogleList) {
            if (b) {
                numberOfColumns++;
            }
        }
        pdfdoc.setDocument(new Document());
        if (orientation == 0) {
            pdfdoc.getDocument().setPageSize(PageSize.A4.rotate());
        } else {
            pdfdoc.getDocument().setPageSize(PageSize.A4);
        }
        pdfdoc.setBaos(new ByteArrayOutputStream());
        try {
            PdfWriter.getInstance(pdfdoc.getDocument(), pdfdoc.getBaos());
        } catch (DocumentException ex) {
            Logger.getLogger(StaticMethods.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!pdfdoc.getDocument().isOpen()) {
            pdfdoc.getDocument().open();
        }
        pdfdoc.setPdfTable(new PdfPTable(numberOfColumns));
        pdfdoc.getPdfTable().setWidthPercentage(100);
        pdfdoc.getPdfTable().setHorizontalAlignment(Element.ALIGN_RIGHT);
        pdfdoc.getPdfTable().setSplitRows(false);
        pdfdoc.getPdfTable().setComplete(false);

        ServletContext servletContext = (ServletContext) FacesContext
                .getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("/WEB-INF/resources/fonts/bpg-arial-2009.ttf");
        FontFactory.register(path, "bpg-arial-2009");
        pdfdoc.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10, Font.BOLD, new Color(60, 60, 60)));
        pdfdoc.setFont(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10));
        pdfdoc.setHeader(new PdfPCell());
        pdfdoc.getHeader().setColspan(numberOfColumns);
        pdfdoc.getHeader().setBorder(0);
        pdfdoc.getHeader().setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfdoc.getHeader().setVerticalAlignment(Element.ALIGN_MIDDLE);
        //  pdfdoc.getHeader().setExtraParagraphSpace(30f);

        pdfdoc.setCell(new PdfPCell());
        pdfdoc.getCell().setColspan(numberOfColumns);
        pdfdoc.getCell().setBorder(0);
        pdfdoc.getCell().setVerticalAlignment(Element.ALIGN_CENTER);

        pdfdoc.setTableHeader(new PdfPCell());
        pdfdoc.getTableHeader().setVerticalAlignment(Element.ALIGN_CENTER);

        pdfdoc.setRightCell(new PdfPCell());
        pdfdoc.getRightCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        pdfdoc.setDataCell(new PdfPCell());

        return pdfdoc;
    }

    public static ExcelDocument prepareExcel(String dateformat) {
        ExcelDocument excelDocument = new ExcelDocument();
        excelDocument.setWorkbook(new SXSSFWorkbook());
        excelDocument.setSheet(excelDocument.getWorkbook().createSheet());
        excelDocument.getSheet().setDefaultColumnWidth(23);

        CellStyle cellStyle1 = excelDocument.getWorkbook().createCellStyle();
        cellStyle1.setAlignment(HorizontalAlignment.RIGHT);

        excelDocument.setStyleHeader(excelDocument.getWorkbook().createCellStyle());
        org.apache.poi.ss.usermodel.Font font1 = excelDocument.getWorkbook().createFont();
        font1.setBold(true);
        excelDocument.getStyleHeader().setFont(font1);

        excelDocument.setDateFormatStyle(excelDocument.getWorkbook().createCellStyle());
        CreationHelper createHelper = excelDocument.getWorkbook().getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat(dateformat);
        excelDocument.getDateFormatStyle().setDataFormat(dateFormat);
        excelDocument.getDateFormatStyle().setAlignment(HorizontalAlignment.LEFT);
        return excelDocument;
    }

    public static void createHeaderPdf(String dataTableId, List<Boolean> toogleList, String cssName, PdfDocument pdfDocument) {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(dataTableId);

        for (int x = 0; x < toogleList.size(); x++) {
            if (toogleList.get(x)) {
                createCellStylePdf(cssName, pdfDocument, pdfDocument.getTableHeader());
                pdfDocument.getTableHeader().setPhrase(new Phrase(dataTable.getColumns().get(x).getHeaderText(), pdfDocument.getFontColumnTitle()));
                pdfDocument.getPdfTable().addCell(pdfDocument.getTableHeader());

            }
        }

    }

    /**
     *
     * @param dataTableId Kolonların isimleri alınacak datatable
     * nesnesi(primefaces)
     * @param toogleList Kolonların görünürlük listesi
     * @param cssName başlık için kullanılacak css in ismi
     * @param workbook excel için gerekli poarametre
     */
    public static void createHeaderExcel(String dataTableId, List<Boolean> toogleList, String cssName, SXSSFWorkbook workbook) {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(dataTableId);
        int a = 0;
        CellStyle cellStyle = createCellStyleExcel(cssName, workbook);
        SXSSFRow row = null;
        row = workbook.getSheetAt(0).createRow(workbook.getSheetAt(0).getLastRowNum() + 1);

        for (int x = 0; x < toogleList.size(); x++) {
            if (toogleList.get(x)) {
                SXSSFCell cell = row.createCell((short) a++);
                cell.setCellValue(dataTable.getColumns().get(x).getHeaderText());
                cell.setCellStyle(cellStyle);
            }

        }

    }

    /**
     *
     * @param dataTableId Kolonların isimleri alınacak datatable
     * nesnesi(primefaces)
     * @param toogleList Kolonların görünürlük listesi
     * @param cssName başlık için kullanılacak css in ismi
     * @param sb print için gerekli parametre
     */
    public static void createHeaderPrint(String dataTableId, List<Boolean> toogleList, String cssName, StringBuilder sb) {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(dataTableId);

        sb.append(" <tr>  ");
        for (int x = 0; x < toogleList.size(); x++) {
            if (toogleList.get(x)) {
                sb.append("<th>").append(dataTable.getColumns().get(x).getHeaderText()).append("</th>");
            }
        }
        sb.append(" </tr>  ");

    }

    public static void createCellStylePdf(String styleType, PdfDocument pdfDocument, PdfPCell pdfCell) {
        switch (styleType) {
            case "headerBlack":
                pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, 0, Color.WHITE));
                pdfCell.setBackgroundColor(Color.BLACK);
                pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                break;
            case "headerDarkRedBold":
                pdfDocument.setFontHeader(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD, new Color(113, 0, 0)));
                pdfCell.setBackgroundColor(Color.WHITE);
                pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                break;
            case "headerDarkRed":
                pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10, 0, new Color(113, 0, 0)));
                pdfCell.setBackgroundColor(Color.WHITE);
                pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                break;
            case "headerWhite":
                pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10, 0, Color.BLACK));
                pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                break;
            case "footer":
                pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10, 0, new Color(60, 60, 60)));
                pdfCell.setBackgroundColor(Color.LIGHT_GRAY);
                pdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                break;
            case "headerWhiteBold":
                pdfDocument.setFontColumnTitle(FontFactory.getFont("bpg-arial-2009", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD, Color.DARK_GRAY));
                pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                break;

        }

    }

    public static CellStyle createCellStyleExcel(String styleType, SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        switch (styleType) {
            case "headerBlack":
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setFillForegroundColor(IndexedColors.BLACK.index);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                font.setColor(IndexedColors.WHITE.index);
                cellStyle.setFont(font);
                break;
            case "headerWhite":
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setBorderBottom(BorderStyle.MEDIUM);
                cellStyle.setBorderTop(BorderStyle.MEDIUM);
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setBorderLeft(BorderStyle.MEDIUM);
                org.apache.poi.ss.usermodel.Font font2 = workbook.createFont();
                font2.setBold(true);
                cellStyle.setFont(font2);
                break;
            case "headerDarkRed":
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setBorderRight(BorderStyle.NONE);
                cellStyle.setBorderLeft(BorderStyle.NONE);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                org.apache.poi.ss.usermodel.Font font3 = workbook.createFont();
                font3.setBold(true);
                font3.setColor(IndexedColors.DARK_RED.index);
                cellStyle.setFont(font3);
                break;

            case "headerDarkGray":
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setBorderRight(BorderStyle.NONE);
                cellStyle.setBorderLeft(BorderStyle.NONE);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                org.apache.poi.ss.usermodel.Font font5 = workbook.createFont();
                font5.setBold(true);
                font5.setColor(IndexedColors.BLACK.index);
                cellStyle.setFont(font5);
                break;
            case "footerBlack":
                cellStyle.setBorderRight(BorderStyle.MEDIUM);
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                cellStyle.setBorderRight(BorderStyle.NONE);
                cellStyle.setBorderLeft(BorderStyle.NONE);
                org.apache.poi.ss.usermodel.Font font4 = workbook.createFont();
                font4.setFontHeightInPoints((short) 13);
                font4.setBold(true);
                font4.setColor(IndexedColors.BLACK.index);
                cellStyle.setFont(font4);
                break;
            case "footer":
                cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                break;

        }

        return cellStyle;
    }

    public static void pdfAddCell(PdfDocument pdfDocument, ResultSet rs, List<Boolean> toogleList, String[] colums, UserData user, NumberFormat numberFormat, String[] extension) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();

            for (int c = 0; c < toogleList.size(); c++) {
                if (toogleList.get(c)) {//kolon visible ise

                    for (int j = 0; j < rsmd.getColumnCount(); j++) {//gelen kolonun indexsini bul

                        if (rsmd.getColumnName(j + 1).equals(colums[c])) {//gelen arrayde sıradki kolonun indexini bulduk                         
                            switch (rsmd.getColumnType(j + 1)) {
                                case java.sql.Types.NULL:
                                    pdfDocument.getDataCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
                                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                                    break;
                                case java.sql.Types.VARCHAR:
                                case java.sql.Types.NVARCHAR:
                                case java.sql.Types.LONGNVARCHAR:
                                case java.sql.Types.LONGVARCHAR:
                                    if (rs.getString(j + 1) != null) {
                                        pdfDocument.getDataCell().setPhrase(new Phrase(rs.getString(j + 1) + " " + extension[c], pdfDocument.getFont()));
                                    } else {
                                        pdfDocument.getDataCell().setPhrase(new Phrase(" ", pdfDocument.getFont()));
                                    }
                                    pdfDocument.getDataCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                                    break;
                                case java.sql.Types.DATE:
                                case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
                                case java.sql.Types.TIMESTAMP:
                                case java.sql.Types.TIME:
                                case java.sql.Types.TIME_WITH_TIMEZONE:
                                    if (extension[c].equals("HH:mm")) {
                                        pdfDocument.getDataCell().setPhrase(new Phrase(convertToDateFormatWithMinute(user.getLastBranch().getDateFormat(), rs.getTimestamp(j + 1)), pdfDocument.getFont()));
                                    } else if (extension[c].equals("HH:mm:ss")) {
                                        pdfDocument.getDataCell().setPhrase(new Phrase(convertToDateFormatWithSeconds(user.getLastBranch().getDateFormat(), rs.getTimestamp(j + 1)), pdfDocument.getFont()));
                                    } else {
                                        pdfDocument.getDataCell().setPhrase(new Phrase(convertToDateFormat(user.getLastBranch().getDateFormat(), rs.getTimestamp(j + 1)), pdfDocument.getFont()));
                                    }
                                    pdfDocument.getDataCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());
                                    break;
                                case java.sql.Types.BOOLEAN:
                                case java.sql.Types.BIT:
                                    break;
                                case java.sql.Types.BIGINT:
                                case java.sql.Types.INTEGER:
                                case java.sql.Types.SMALLINT:
                                    pdfDocument.getDataCell().setPhrase(new Phrase(String.valueOf(rs.getInt(j + 1)) + " " + extension[c], pdfDocument.getFont()));
                                    pdfDocument.getPdfTable().addCell(pdfDocument.getDataCell());

                                    break;
                                case java.sql.Types.DOUBLE:
                                case java.sql.Types.NUMERIC:
                                    pdfDocument.getRightCell().setPhrase(new Phrase(numberFormat.format((rs.getDouble(j + 1))) + " " + extension[c], pdfDocument.getFont()));
                                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                                    break;
                                case java.sql.Types.FLOAT:
                                    pdfDocument.getRightCell().setPhrase(new Phrase(numberFormat.format((rs.getFloat(j + 1))) + " " + extension[c], pdfDocument.getFont()));
                                    pdfDocument.getPdfTable().addCell(pdfDocument.getRightCell());
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
        }

        //return pdfTable;
    }

    /**
     *
     * @param row yeni oluşturulan excel satırı
     * @param rs veritabanından gelen sorgu satırı
     * @param toogleList datatable toogle lisetesi
     * @param colums veri tabanından gelen kolonların isimleri sıralı şekilde
     * array
     * @param dateFormatStyle tarih format stili
     * @param createHelper yeni tarih formatı için createhelper
     * @param user tarih formatını ve yuvarlama değerlerini almak için userdata
     * objesi
     * @param numberFormat number formatı
     * @param extension
     */
    public static void excelAddCell(SXSSFRow row, ResultSet rs, List<Boolean> toogleList, String[] colums, CellStyle dateFormatStyle, CreationHelper createHelper, UserData user, NumberFormat numberFormat, String[] extension) {

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int col = row.getLastCellNum();
            if (col < 0) {
                col = 0;
            }

            for (int c = 0; c < toogleList.size(); c++) {
                if (toogleList.get(c)) {//kolon visible ise

                    for (int j = 0; j < rsmd.getColumnCount(); j++) {//gelen kolonun indexsini bul

                        if (rsmd.getColumnName(j + 1).equals(colums[c])) {//gelen arrayde sıradki kolonun indexini bulduk
                            switch (rsmd.getColumnType(j + 1)) {
                                case java.sql.Types.NULL:
                                    SXSSFCell cell0 = row.createCell((short) col++);
                                    cell0.setCellType(CellType.STRING);
                                    cell0.setCellValue("");
                                    break;
                                case java.sql.Types.VARCHAR:
                                case java.sql.Types.NVARCHAR:
                                case java.sql.Types.LONGNVARCHAR:
                                case java.sql.Types.LONGVARCHAR:
                                    SXSSFCell cell = row.createCell((short) col++);
                                    cell.setCellType(CellType.STRING);
                                    cell.setCellValue(rs.getString(j + 1));
                                    break;
                                case java.sql.Types.DATE:
                                case java.sql.Types.TIME:
                                case java.sql.Types.TIME_WITH_TIMEZONE:
                                case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
                                case java.sql.Types.TIMESTAMP:
                                    dateFormatStyle.setAlignment(HorizontalAlignment.LEFT);
                                    dateFormatStyle.setAlignment(HorizontalAlignment.LEFT);
                                    if (extension[c].equals("HH:mm")) {
                                        dateFormatStyle.setDataFormat(createHelper.createDataFormat().getFormat(user.getLastBranch().getDateFormat() + " HH:mm"));
                                    } else if (extension[c].equals("HH:mm:ss")) {
                                        dateFormatStyle.setDataFormat(createHelper.createDataFormat().getFormat(user.getLastBranch().getDateFormat() + " HH:mm:ss"));
                                    } else {
                                        dateFormatStyle.setDataFormat(createHelper.createDataFormat().getFormat(user.getLastBranch().getDateFormat()));
                                    }
                                    SXSSFCell cell2 = row.createCell((short) col++);
                                    cell2.setCellValue(rs.getTimestamp(j + 1));
                                    cell2.setCellStyle(dateFormatStyle);
                                    break;
                                case java.sql.Types.BOOLEAN:
                                case java.sql.Types.BIT:
                                    break;
                                case java.sql.Types.BIGINT:
                                case java.sql.Types.INTEGER:
                                case java.sql.Types.SMALLINT:
                                    SXSSFCell cell5 = row.createCell((short) col++);
                                    cell5.setCellType(CellType.NUMERIC);
                                    cell5.setCellValue(rs.getInt(j + 1));
                                    break;
                                case java.sql.Types.DOUBLE:
                                case java.sql.Types.NUMERIC:
                                    SXSSFCell cell6 = row.createCell((short) col++);
                                    cell6.setCellType(CellType.NUMERIC);
                                    cell6.setCellValue(round(rs.getDouble(j + 1), user.getLastBranch().getCurrencyrounding()));
                                    break;
                                case java.sql.Types.FLOAT:
                                    SXSSFCell cell7 = row.createCell((short) col++);
                                    cell7.setCellType(CellType.NUMERIC);
                                    cell7.setCellValue(round(rs.getFloat(j + 1), user.getLastBranch().getCurrencyrounding()));
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {

        }
    }

    /**
     *
     * @param sb yazdırma için oluşturulan string
     * @param rs veritabanından gelen sorgu satırı
     * @param toogleList datatable toogle lisetesi
     * @param colums veri tabanından gelen kolonların isimleri sıralı şekilde
     * array
     * @param user tarih formatını ve yuvarlama değerlerini almak için userdata
     * objesi
     * @param numberFormat number formatı
     * @param extension
     */
    public static void printAddCell(StringBuilder sb, ResultSet rs, List<Boolean> toogleList, String[] colums, UserData user, NumberFormat numberFormat, String[] extension) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int c = 0; c < toogleList.size(); c++) {
                if (toogleList.get(c)) {//kolon visible ise

                    for (int j = 0; j < rsmd.getColumnCount(); j++) {//gelen kolonun indexsini bul

                        if (rsmd.getColumnName(j + 1).equals(colums[c])) {//gelen arrayde sıradki kolonun indexini bulduk
                            switch (rsmd.getColumnType(j + 1)) {
                                case java.sql.Types.NULL:
                                    sb.append("<td></td>");
                                    break;
                                case java.sql.Types.VARCHAR:
                                case java.sql.Types.NVARCHAR:
                                case java.sql.Types.LONGNVARCHAR:
                                case java.sql.Types.LONGVARCHAR:
                                    sb.append("<td>").append(rs.getString(j + 1) != null ? rs.getString(j + 1) + " " + extension[c] : "").append("</td>");
                                    break;
                                case java.sql.Types.DATE:
                                case java.sql.Types.TIME:
                                case java.sql.Types.TIME_WITH_TIMEZONE:
                                case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
                                case java.sql.Types.TIMESTAMP:
                                    if (extension[c].equals("HH:mm")) {
                                        sb.append("<td>").append(rs.getDate(j + 1) == null ? ""
                                                : convertToDateFormatWithMinute(user.getLastBranch().getDateFormat(), rs.getTimestamp(j + 1))).append("</td>");
                                    } else if (extension[c].equals("HH:mm:ss")) {
                                        sb.append("<td>").append(rs.getDate(j + 1) == null ? ""
                                                : convertToDateFormatWithSeconds(user.getLastBranch().getDateFormat(), rs.getTimestamp(j + 1))).append("</td>");
                                    } else {
                                        sb.append("<td>").append(rs.getDate(j + 1) == null ? ""
                                                : convertToDateFormat(user.getLastBranch().getDateFormat(), rs.getTimestamp(j + 1))).append("</td>");
                                    }
                                    break;
                                case java.sql.Types.BOOLEAN:
                                case java.sql.Types.BIT:
                                    break;
                                case java.sql.Types.BIGINT:
                                case java.sql.Types.INTEGER:
                                case java.sql.Types.SMALLINT:
                                    sb.append("<td>").append(String.valueOf(rs.getInt(j + 1))).append(" ").append(extension[c]).append("</td>");
                                    break;
                                case java.sql.Types.DOUBLE:
                                case java.sql.Types.NUMERIC:
                                    sb.append("<td style=\"text-align: right\">").append(numberFormat.format((rs.getDouble(j + 1)))).append(" ").append(extension[c]).append("</td>");
                                    break;
                                case java.sql.Types.FLOAT:
                                    sb.append("<td style=\"text-align: right\">").append(numberFormat.format((rs.getFloat(j + 1)))).append(" ").append(extension[c]).append("</td>");
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {

        }
    }

    public static void writePDFToResponse(PdfDocument pdfDocument, String fileName) {
        FacesContext context = FacesContext.getCurrentInstance();
        pdfDocument.getPdfTable().setComplete(true);
        try {
            pdfDocument.getDocument().add(pdfDocument.getPdfTable());
        } catch (DocumentException ex) {
            Logger.getLogger(StaticMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        pdfDocument.getDocument().close();
        try {
            String name = URLEncoder.encode(fileName, "UTF-8");
            StringBuilder f = new StringBuilder(name);
            if (name.contains("+")) {
                for (int i = 0; i < name.length(); i++) {
                    if (name.charAt(i) == '+') {
                        f.setCharAt(i, ' ');
                    }
                }
            }

            context.getExternalContext().responseReset();
            context.getExternalContext().setResponseContentType("application/pdf");
            context.getExternalContext().setResponseHeader("Expires", "0");
            context.getExternalContext().setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            context.getExternalContext().setResponseHeader("Pragma", "public");
            context.getExternalContext().setResponseHeader("Content-disposition", "attachment;filename=" + f + ".pdf");
            context.getExternalContext().setResponseContentLength(pdfDocument.getBaos().size());
            OutputStream out = context.getExternalContext().getResponseOutputStream();
            pdfDocument.getBaos().writeTo(out);
            context.getExternalContext().responseFlushBuffer();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        context.responseComplete();
    }

    public static void writeExcelToResponse(SXSSFWorkbook workbook, String fileName) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        fileName = URLEncoder.encode(fileName, "UTF-8");//FileName Karakter Sorunu İçin
        StringBuilder f = new StringBuilder(fileName);
        if (fileName.contains("+")) {
            for (int i = 0; i < fileName.length(); i++) {
                if (fileName.charAt(i) == '+') {
                    f.setCharAt(i, ' ');
                }
            }
        }
        fileName = new String(f);
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");

        workbook.write(externalContext.getResponseOutputStream());
        facesContext.responseComplete();
    }

    public static String escapeStringForHtml(String html) {
        return html.replace("'", "\\'");
    }

    public static String findCategory(List<Categorization> listOfCategorization) {
        String categoryName = "";

        for (Categorization c : listOfCategorization) {
            if (c.getDepth() == 1) {
                categoryName = c.getName() + " , " + categoryName;
                categoryName = findCategoryParent(listOfCategorization, c, categoryName);
            }
        }
        if (!categoryName.isEmpty()) {
            categoryName = categoryName.substring(0, categoryName.length() - 2);
        }
        return categoryName;
    }

    public static String findCategoryParent(List<Categorization> listCategorization, Categorization categorization, String categoryName) {
        for (Categorization c : listCategorization) {
            if (c.getId() == categorization.getParentId().getId()) {
                categoryName = c.getName() + " ; " + categoryName;
                categoryName = findCategoryParent(listCategorization, c, categoryName);
                break;
            }
        }
        return categoryName;
    }

    public static String findCategories(String category) {
        String categoryName = "";
        DocumentBuilder builder;
        List<Categorization> categoryList = new ArrayList<>();
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(category));
            src.setEncoding("UTF-8");
            org.w3c.dom.Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("category");
            for (int s = 0; s < list.getLength(); s++) {
                Categorization categorization = new Categorization();
                NodeList elements = list.item(s).getChildNodes();
                categorization.setId(Integer.valueOf(elements.item(0).getTextContent()));
                categorization.setName(elements.item(1).getTextContent());
                categorization.setParentId(new Categorization());
                categorization.getParentId().setId(Integer.valueOf(elements.item(2).getTextContent()));
                categorization.setDepth(Integer.valueOf(elements.item(3).getTextContent()));

                categoryList.add(categorization);
            }
            categoryName = findCategory(categoryList);
        } catch (ParserConfigurationException ex) {
        } catch (SAXException | IOException ex) {
        }
        return categoryName;

    }

}
