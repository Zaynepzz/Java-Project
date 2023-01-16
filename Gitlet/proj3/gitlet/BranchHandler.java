package gitlet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class BranchHandler {

    public static Branch createBranch(String branchName, String commitId){
        if(Utils.join(Config.BRANCH_DIR,branchName).exists()){
            return null;
        }
        Utils.writeContents(Utils.join(Config.BRANCH_DIR,branchName),commitId);
        Branch branch = new Branch(branchName,commitId);
        return branch;
    }

    public static Set<String> getOtherBranch(){
        Set<String> set = getSet();
        Branch curBranch = BranchHandler.getCurrentBranch();
        set.remove(curBranch.getBranchName());
        return set;
    }

    private static Set<String> getSet(){
        Set<String> set = new HashSet<>();
        for(File file:Config.BRANCH_DIR.listFiles()){
            set.add(file.getName());
        }
        return set;
    }


    public static Branch getCurrentBranch(){
        if(HeadPointHandler.getHeadPoint()!=null){
            return HeadPointHandler.getHeadPoint().getBranch();
        }
        return null;
    }

    public static Branch getBranchByName(String branchName){
        if(Utils.join(Config.BRANCH_DIR,branchName).exists()){
            String commitId = Utils.readContentsAsString(Utils.join(Config.BRANCH_DIR,branchName));
            return new Branch(branchName,commitId);
        }else{
            return null;
        }
    }

    public static void save(Branch branch){
        Utils.writeContents(Utils.join(Config.BRANCH_DIR
                ,branch.getBranchName()),branch.getCommitId());
    }
}
