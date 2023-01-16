package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResetCommand implements Command {

    private String commitId;

    public ResetCommand() {
    }

    public ResetCommand(String commitId) {
        this.commitId = commitId;
    }

    private void checkNoCommit(Commit toCommit){
        if(toCommit==null){
            throw new GitletException("No commit with that id exists.");
        }
    }

    private void checkHasUntrackedFile(Commit toCommit,String filename){
        if(toCommit.isContain(new File(filename))){
            throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
        }
    }

    @Override
    public boolean execute() throws GitletException {
        Commit toCommit = CommitHandler.getCommit(commitId);
        checkNoCommit(toCommit);

        Map<String,File> workspacesFile = new HashMap<>();
        for(String filename :new File(Config.GIT_DIR.getAbsolutePath()
                .substring(0,Config.GIT_DIR.
                        getAbsolutePath().indexOf(Config.GIT_DIR.getName()))).list()){
            File file = new File(filename);
            if(file.isFile()){
                String path = Utils.relativePath(file);
                workspacesFile.put(path,file);
            }
        }
        Commit curCommit = CommitHandler.getCurrentCommit();
        Set<String> untrackedSet = new HashSet<>();
        for(File file :workspacesFile.values()){
            boolean isContain = curCommit.isContain(file);
            if(!isContain){
                untrackedSet.add(Utils.relativePath(file));
            }
        }
        for(String filename:untrackedSet){
            checkHasUntrackedFile(toCommit,filename);
        }

        Set<String> keySet = curCommit.getBlobMap().keySet();
        for(String filename:keySet){
            deleteFile(filename);
        }
        Map<String, Blob> toBlobMap = toCommit.getBlobMap();
        Set<String> keySet2 = toBlobMap.keySet();
        for(String filename:keySet2){
            File file = new File(filename);
            File file2 = Utils.blobJoin(Config.BLOB_DIR,
                    toBlobMap.get(filename).getRef());
            Utils.writeContents(file,
                    Utils.readContents(file2));
        }

        saveStage();

        saveBranch();
        return true;
    }

    private void deleteFile(String filename){
        File file = new File(filename);
        if(file.exists()){
            file.delete();
        }
    }

    private void saveBranch(){
        Branch curBranch = BranchHandler.getCurrentBranch();
        curBranch.setCommitId(commitId);
        BranchHandler.save(curBranch);
    }

    private void saveStage(){
        Stage stage = StageHandler.getStage();
        stage.clear();
        StageHandler.save(stage);
    }
}
