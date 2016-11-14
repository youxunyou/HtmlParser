package domain;

/**
 * Created by CrazyHorse on 14/11/2016.
 */
public class FileAndEncoding {

    /**
     * 页面真正的文件内容
     */
    private String file;

    /**
     * 页面真正的文件编码
     */
    private String encoding;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
