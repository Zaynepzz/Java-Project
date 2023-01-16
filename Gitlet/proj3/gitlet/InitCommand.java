package gitlet;

public class InitCommand implements Command {

    @Override
    public boolean execute() throws GitletException {
        checkIsExists();
        createDir();

        Commit commit = createCommit();

        createBranch(commit);

        LogHandler.info(new Log(commit.getFirstParentId()
                ,CommitHandler.getCommitId(commit),commit.getTimestamp().getTime()
                ,commit.getMessage()));
        return true;
    }

    private void checkIsExists(){
        if(Config.GIT_DIR.exists()){
            GitletException exception = Utils.error("A Gitlet version-control system already exists in the current directory.");
            if(exception!=null){
                throw exception;
            }
        }
    }

    private void createDir(){
        Config.GIT_DIR.mkdir();
        Config.BLOB_DIR.mkdir();
        Config.BRANCH_DIR.mkdirs();
        Config.LOG_DIR.mkdir();
        Config.REPOSITORY_DIR.mkdir();
    }

    private Commit createCommit(){
        Commit commit = CommitHandler.createCommit("initial commit");
        CommitHandler.save(commit);
        return commit;
    }

    private void createBranch(Commit commit){
        String commitId = CommitHandler.getCommitId(commit);
        Branch branch = BranchHandler.createBranch(Config.DEFAULT_BRANCH,commitId);
        HeadPointHandler.moveTo(branch);
    }


}
