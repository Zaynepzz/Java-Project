package gitlet;



import java.io.Serializable;

/**
 * class representing the blob object.
 * @author ZhengPeng
 */
public class Blob implements Serializable, Dumpable {

    /**
     * filename of the blob.
     */
    private String filename;
    /**
     * the reference in string.
     */
    private String ref;

    @Override
    public void dump() {

    }

    /**
     * setter of the class.
     * @param reference is in string format.
     */
    public void setRef(String reference) {
        this.ref = ref;
    }

    /**
     * setter of the class.
     * @param file is the filename.
     */
    public void setFilename(String file) {
        this.filename = file;
    }

    /**
     * getter of the class.
     * @return filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * getter of the class.
     * @return ref.
     */
    public String getRef() {
        return ref;
    }

    /**
     * the constructor of the class.
     * @param file is the filename.
     * @param reference is the ref in string.
     */
    public Blob(String file, String reference) {
        this.filename = file;
        this.ref = reference;
    }

}
