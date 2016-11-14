import domain.LinkFilter;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by CrazyHorse on 11/11/2016.
 */
public class HtmlLinkParser {

    /**
     * 获取子链接
     *
     * @param url    网页url
     * @param filter 链接过滤器
     * @return 该页面子链接的HashSet
     */
    public static Set<String> extractLinks(String url, LinkFilter filter) {
        Set<String> links = new HashSet<>();
        try {
            Parser parser = new Parser(url);
            parser.setEncoding("UTF-8");

            /**
             * 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性所表示的链接
             */
            NodeFilter frameFilter = new NodeFilter() {

                @Override
                public boolean accept(Node node) {
                    if (node.getText().startsWith("frame src=")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            /**
             * OrFilter 接受<a>标签或<frame>标签，注意NodeClassFilter()可用来过滤一类标签，linkTag对应<标签>
             */
            OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);

            /**
             * 得到所有经过过滤的标签，结果为NodeList
             */
            NodeList nodeList = parser.extractAllNodesThatMatch(linkFilter);
            for (int i = 0; i < nodeList.size(); i++) {
                Node node = nodeList.elementAt(i);
                if (node instanceof LinkTag) {
                    LinkTag linkTag = (LinkTag) node;
                    /**
                     * 调用getLink()方法得到<a>标签中的链接
                     */
                    String linkUrl = linkTag.getLink();
                    /**
                     * 将符合filter过滤条件的链接加入链接表
                     */
                    if (filter.accept(linkUrl)) {
                        links.add(linkUrl);
                    }
                } else {
                    /**
                     * frame标签
                     * 提取 frame 里 src 属性的链接如 <frame src="test.html"/>
                     */
                    String frame = node.getText();
                    int start = frame.indexOf("src=");
                    frame = frame.substring(start);
                    int end = frame.indexOf(" ");
                    if (end == -1)
                        end = frame.indexOf(">");
                    String frameUrl = frame.substring(5, end - 1);
                    if (filter.accept(frameUrl))
                        links.add(frameUrl);
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return links;
    }

    public static void main(String[] args) {
        Set<String> links = HtmlLinkParser.extractLinks("http://qq.com", new LinkFilter("mail"));
        System.out.println("TOTAL SIZE: " + links.size());
        for (String link : links){
            System.out.println(link);
        }
    }
}
