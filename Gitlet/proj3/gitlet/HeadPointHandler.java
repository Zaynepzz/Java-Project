package gitlet;

import java.io.File;

public class HeadPointHandler {


    public static void moveTo(Branch branch){
        Utils.writeContents(Config.HEAD,"ref: "+Utils
                .join("refs","heads",branch.getBranchName()).getPath());
    }

    public static HeadPoint getHeadPoint(){
        if(Config.HEAD.exists()) {
            String commitId = Utils.readContentsAsString(
                    Utils.join(Config.GIT_DIR
                            ,Utils.readContentsAsString(Config.HEAD)
                                    .substring(5,Utils.readContentsAsString(Config.HEAD).length())));
            return new HeadPoint(new Branch(Utils.readContentsAsString(Config.HEAD)
                    .substring(16,Utils.readContentsAsString(Config.HEAD).length())
                    ,commitId));
        }
        return null;
    }
}