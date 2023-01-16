package gitlet;

public class CommitCommand implements Command {

    private String message;

    public CommitCommand(String message) {
        this.message = message;
    }

    @Override
    public boolean execute() {
        checkMessage();

        Branch branch = BranchHandler.getCurrentBranch();
        Stage stage = StageHandler.getStage();
        String curCommitId = branch.getCommitId();
        checkStage(stage);
        Commit commit = createCommit(stage);
        branch.setCommitId(CommitHandler.getCommitId(commit));
        BranchHandler.save(branch);
        LogHandler.info(new Log(curCommitId,branch.getCommitId(),message));
        return true;
    }

    private Commit createCommit(Stage stage){
        Commit commit = CommitHandler.createCommit(message);
        commit.add(stage.getAddMap());
        removeStage(commit,stage);
        clearStage(stage);
        return commit;
    }

    private void removeStage(Commit commit,Stage stage){
        commit.remove(stage.getRmSet());
        CommitHandler.save(commit);
    }

    private void clearStage(Stage stage){
        stage.clear();
        StageHandler.save(stage);
    }


    private void checkMessage(){
        if(message.isEmpty()||message.trim().isEmpty()){
            throw new GitletException("Please enter a commit message.");
        }
    }

    private void checkStage(Stage stage){
        if(!stage.isUpdate()){
            throw new GitletException("No changes added to the commit.");
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}