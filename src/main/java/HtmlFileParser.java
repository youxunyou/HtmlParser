import domain.AcApply;
import domain.FileAndEncoding;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.*;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CrazyHorse on 14/11/2016.
 */
public class HtmlFileParser {

    /**
     * html文件路径
     */
    String filePath = new String();

    /**
     * 关键词列表
     */
    private static String[] keyWords = {"曼联", "穆里尼奥", "伊布", "博格巴"};

    public HtmlFileParser(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 得到页面标题
     *
     * @return
     */
    public String getTitle() {
        FileAndEncoding fae = readHtmlFile();
        int i = 0;
        try {
            //实例化一个本地html文件的Parser
            Parser titleParser = Parser.createParser(fae.getFile(), fae.getEncoding());
            NodeClassFilter titleFilter = new NodeClassFilter(TitleTag.class);
            NodeList titleList = titleParser.extractAllNodesThatMatch(titleFilter);
            //实际上一个网页应该只有一个<title>标签，但extractAllNodesThatMatch方法返回的只能是一个NodeList
            for (i = 0; i < titleList.size(); i++) {
                TitleTag title_tag = (TitleTag) titleList.elementAt(i);
                return title_tag.getTitle();
            }
        } catch (ParserException e) {
            return null;
        }
        return null;
    }


    /**
     * 计算网页的主题相关度
     *
     * @return
     */
    public float getRelatGrade() {
        FileAndEncoding fae = readHtmlFile();
        String file = fae.getFile();
        String enC = fae.getEncoding();
        String curString;
        //当前关键词权重
        int curWordWei = 1;
        //当前标签权重
        float curTagWei = 0;
        //总相关度分
        float totalGra = 0;
        int i;
        //实例化ac自动机
        AcApply obj = new AcApply();
        Pattern p = null;
        Matcher m = null;
        try {
            //根据不同标签依次进行相关度计算
            //title tag <title>
            curTagWei = 5;
            Parser titleParser = Parser.createParser(file, enC);
            NodeClassFilter titleFilter = new NodeClassFilter(TitleTag.class);
            NodeList titleList = titleParser.extractAllNodesThatMatch(titleFilter);
            for (i = 0; i < titleList.size(); i++) {
                TitleTag titleTag = (TitleTag) titleList.elementAt(i);
                curString = titleTag.getTitle();
                //ac自动机的方法返回匹配的词的表
                Set result = obj.findWordsInArray(keyWords, curString);
                //计算相关度
                totalGra = totalGra + result.size() * curTagWei;
            }
            //meta tag of description and keyword <meta>
            curTagWei = 4;
            Parser metaParser = Parser.createParser(file, enC);
            NodeClassFilter metaFilter = new NodeClassFilter(MetaTag.class);
            NodeList metaList = metaParser.extractAllNodesThatMatch(metaFilter);
            p = Pattern.compile("\\b(description|keywords)\\b", Pattern.CASE_INSENSITIVE);
            for (i = 0; i < metaList.size(); i++) {
                MetaTag metaTag = (MetaTag) metaList.elementAt(i);
                curString = metaTag.getMetaTagName();
                if (curString == null) {
                    continue;
                }
                //正则匹配name是description或keyword的<meta>标签
                m = p.matcher(curString);
                if (m.find()) {
                    //提取其content
                    curString = metaTag.getMetaContent();
                    Set result = obj.findWordsInArray(keyWords, curString);
                    totalGra = totalGra + result.size() * curTagWei;
                } else {
                    curString = metaTag.getMetaContent();
                    Set result = obj.findWordsInArray(keyWords, curString);
                    totalGra = totalGra + result.size() * 2;
                }
            }
            //heading tag <h*>
            curTagWei = 3;
            Parser headingParser = Parser.createParser(file, enC);
            NodeClassFilter headingFilter = new NodeClassFilter(HeadingTag.class);
            NodeList headingList = headingParser.extractAllNodesThatMatch(headingFilter);
            for (i = 0; i < headingList.size(); i++) {
                HeadingTag headingTag = (HeadingTag) headingList.elementAt(i);
                curString = headingTag.toPlainTextString();//得到<h*>标签中的纯文本
                if (curString == null) {
                    continue;
                }
                Set result = obj.findWordsInArray(keyWords, curString);
                totalGra = totalGra + result.size() * curTagWei;
            }
            //paragraph tag <p>
            curTagWei = (float) 2.5;
            Parser paraParser = Parser.createParser(file, enC);
            NodeClassFilter paraFilter = new NodeClassFilter(ParagraphTag.class);
            NodeList paraList = paraParser.extractAllNodesThatMatch(paraFilter);
            for (i = 0; i < paraList.size(); i++) {
                ParagraphTag paraTag = (ParagraphTag) paraList.elementAt(i);
                curString = paraTag.toPlainTextString();
                if (curString == null) {
                    continue;
                }
                Set result = obj.findWordsInArray(keyWords, curString);
                totalGra = totalGra + result.size() * curTagWei;
            }
            //link tag <a>
            curTagWei = (float) 0.25;
            Parser linkParser = Parser.createParser(file, enC);
            NodeClassFilter linkFilter = new NodeClassFilter(LinkTag.class);
            NodeList linkList = linkParser.extractAllNodesThatMatch(linkFilter);
            for (i = 0; i < linkList.size(); i++) {
                LinkTag linkTag = (LinkTag) linkList.elementAt(i);
                curString = linkTag.toPlainTextString();
                if (curString == null) {
                    continue;
                }
                Set result = obj.findWordsInArray(keyWords, curString);
                totalGra = totalGra + result.size() * curTagWei;
            }
        } catch (ParserException e) {
            return 0;
        }
        return totalGra;
    }

    /**
     * 读取html文件，返回字符串格式的文件与其编码
     *
     * @return
     */
    private FileAndEncoding readHtmlFile() {
        StringBuffer stringBuffer = new StringBuffer();
        FileAndEncoding fileAndEncoding = new FileAndEncoding();
        try {
            //实例化默认编码方式的BufferReader
            BufferedReader enCReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            String temp = null;
            //得到字符串格式的文件
            while ((temp = enCReader.readLine()) != null) {
                stringBuffer.append(temp);
                stringBuffer.append("\r\n");
            }
            String result = stringBuffer.toString();
            fileAndEncoding.setFile(result);
            String encoding = getEncoding(result);
            //得到页面编码
            fileAndEncoding.setEncoding(encoding);
            //根据得到的编码方式实例化BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), encoding));
            StringBuffer stringBufferT = new StringBuffer();
            while ((temp = reader.readLine()) != null) {
                stringBufferT.append(temp);
                stringBufferT.append("\r\n");
            }
            result = stringBufferT.toString();
            //得到真正的页面内容
            fileAndEncoding.setFile(result);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            fileAndEncoding = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fileAndEncoding = null;
        } finally {
            return fileAndEncoding;
        }
    }

    /**
     * 获得页面编码
     *
     * @return
     */
    public String getEncoding() {
        FileAndEncoding fileAndEncoding = readHtmlFile();
        return fileAndEncoding.getEncoding();
    }

    /**
     * 根据正则匹配得到页面编码
     *
     * @param file
     * @return
     */
    private String getEncoding(String file) {
        String enCoding = "utf-8";
        Pattern p = Pattern.compile("(charset|Charset|CHARSET)\\s*=\\s*\"?\\s*([-\\w]*?)[^-\\w]");
        Matcher m = p.matcher(file);
        if (m.find()) {
            //m.group(g) and s.substring(m.start(g), m.end(g)) are equivalent.
            enCoding = m.group(2);
        }
        return enCoding;
    }
}
