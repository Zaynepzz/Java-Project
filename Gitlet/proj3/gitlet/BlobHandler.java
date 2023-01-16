package gitlet;

import java.io.File;

/**
 * the handler of Blob object that implements its function.
 * @author ZhengPeng
 */
public class BlobHandler {

    /**
     * create a new blob given the file input and serialize it.
     * @param file is the file input
     * @return a new blob object with according path.
     */
    public static Blob createBlob(File file) {
        if (!Utils.blobJoin(Config.BLOB_DIR,
                        Utils.sha1(Utils.readContents(file)))
                .getParentFile().exists()) {
            Utils.blobJoin(Config.BLOB_DIR, Utils
                    .sha1(Utils.readContents(file))).getParentFile().mkdir();
        }
        if (!Utils.blobJoin(Config.BLOB_DIR,
                Utils.sha1(Utils.readContents(file))).exists()) {
            Utils.writeContents(Utils.blobJoin(Config.BLOB_DIR,
                            Utils.sha1(Utils.readContents(file))),
                    Utils.readContents(file));
        }
        return new Blob(Utils.relativePath(file),
                Utils.sha1(Utils.readContents(file)));
    }
}
