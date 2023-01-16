package gitlet;

import java.io.File;

public class Config {

    static final File GIT_DIR = Utils.join(".gitlet");

    static final File BLOB_DIR = Utils.join(GIT_DIR, "objects");

    static final File BRANCH_DIR = Utils.join(GIT_DIR, "refs", "heads");

    static final File HEAD = Utils.join(GIT_DIR, "HEAD");

    /** The length of a complete STAGE. */
    static final File STAGE = Utils.join(GIT_DIR, "index");

    static final File LOG_DIR = Utils.join(GIT_DIR, "logs");

    static final File LOG = Utils.join(LOG_DIR, "HEAD");

    static final String DEFAULT_BRANCH = "master";

    /** The length of a complete WORKSPACE. */
    static final File WORKSPACE =
            new File(Config.GIT_DIR.getAbsolutePath().substring(0,
                    Config.GIT_DIR.getAbsolutePath().indexOf
                            (Config.GIT_DIR.getName())));

    /** The length of a complete REPOSITORY_DIR. */
    static final File REPOSITORY_DIR = Utils.join(GIT_DIR, "repository");

}
