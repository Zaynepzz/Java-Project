package gitlet;

public class BranchCommand implements Command {

    private String branchName;

    public BranchCommand() {
    }

    public BranchCommand(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public boolean execute() throws GitletException {
        Branch branch = BranchHandler.createBranch(branchName,CommitHandler.getCurrentCommitId());
        if(branch==null){
            throw new GitletException("A branch with that name already exists.");
        }
        return true;
    }

}
