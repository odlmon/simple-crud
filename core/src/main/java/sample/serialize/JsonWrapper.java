package sample.serialize;

import java.io.Serializable;

public class JsonWrapper implements Serializable {

    private String className;

    private String value;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JsonWrapper(String className, String value) {
        this.className = className;
        this.value = value;
    }
}
