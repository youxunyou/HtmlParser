package domain;

import org.htmlparser.filters.LinkStringFilter;

/**
 * Created by CrazyHorse on 11/11/2016.
 */
public class LinkFilter extends LinkStringFilter {

    public LinkFilter(String pattern) {
        super(pattern);
    }

    public LinkFilter(String pattern, boolean caseSensitive) {
        super(pattern, caseSensitive);
    }

    public boolean accept(String link) {
        boolean ret;

        ret = false;
        if (mCaseSensitive) {
            if (link.indexOf(mPattern) > -1)
                ret = true;
        } else {
            if (link.toUpperCase().indexOf(mPattern.toUpperCase()) > -1)
                ret = true;
        }

        return (ret);
    }

}
