package gitlet;

import java.io.File;
import java.util.Date;
import java.util.Map;

public class CommitHandler {

    public static Commit createCommit(String message){
        Date timestamp = new Date();
        Commit commit = null;
        if(getCurrentCommit()==null){
            commit =createCommit(timestamp,message);
        }else{
            commit =new Commit(timestamp,message,getCurrentCommit().getBlobMap()
                    ,CommitHandler.getCurrentCommitId());
        }
        return commit;
    }

    private static Commit createCommit(Date timestamp,String message){
        timestamp.setTime(0);
        Commit commit =new Commit(timestamp,message);
        return commit;
    }

    public static void save(Commit commit){
        File parentFile = Utils.blobJoin(Config.BLOB_DIR,Utils.sha1(Utils.serialize(commit))).getParentFile();
        if(!parentFile.exists()){
            parentFile.mkdir();
        }
        if(!Utils.blobJoin(Config.BLOB_DIR,Utils.sha1(Utils.serialize(commit))).exists()) {
            Utils.writeObject(Utils.blobJoin(Config.BLOB_DIR,Utils.sha1(Utils.serialize(commit))), commit);
        }
    }

    public static Commit getCurrentCommit(){
        Branch curBranch = BranchHandler.getCurrentBranch();
        if(curBranch!=null){
            String commitId = curBranch.getCommitId();
            return getCommit(commitId);
        }
        return null;
    }

    public static String getCurrentCommitId(){
        Branch curBranch = BranchHandler.getCurrentBranch();
        if(curBranch!=null){
            String commitId = curBranch.getCommitId();
            return commitId;
        }
        return null;
    }

    public static Commit getCommit(String commitId){
        if(commitId.length()!=40){
            for(String key:LogHandler.loadLogMap().keySet()){
                if(key.startsWith(commitId)){
                    commitId = key;
                    break;
                }
            }
        }
        Commit commit = null;
        File file = Utils.blobJoin(Config.BLOB_DIR,commitId);
        if(file.exists()) {
            commit = Utils.readObject(
                    file, Commit.class);
        }
        return commit;
    }

    public static String getCommitId(Commit commit){
        String str = Utils.sha1(Utils.serialize(commit));
        return str;
    }
}
