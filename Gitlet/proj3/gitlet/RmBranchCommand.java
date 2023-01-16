package gitlet;

public class RmBranchCommand implements Command {

    private String branchName;

    public RmBranchCommand() {
    }

    public RmBranchCommand(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public boolean execute() throws GitletException {
        Branch branch = BranchHandler.getBranchByName(branchName);
        checkBranchIsExist(branch);
        checkCannotRemove(branch);
        Utils.join(Config.BRANCH_DIR,branchName).delete();
        return true;
    }

    private void checkCannotRemove(Branch branch){
        Branch curBranch = BranchHandler.getCurrentBranch();
        if(branch.getBranchName().equals(curBranch.getBranchName())){
            throw new GitletException("Cannot remove the current branch.");
        }
    }

    private void checkBranchIsExist(Branch branch){
        if(branch==null){
            throw new GitletException("A branch with that name does not exist.");
        }
    }
}
