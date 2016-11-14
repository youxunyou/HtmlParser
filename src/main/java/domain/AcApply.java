package domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by CrazyHorse on 14/11/2016.
 */
public class AcApply {

    public Set findWordsInArray(String[] keyWords, String str) {
        Set<String> set = new HashSet<>();
        for (String keyWord : keyWords) {
            if (str.indexOf(keyWord) >= 0) {
                set.add(keyWord);
            }
        }
        return set;
    }
}
