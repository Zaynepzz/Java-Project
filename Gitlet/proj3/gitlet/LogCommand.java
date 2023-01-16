package gitlet;

public class LogCommand implements Command {
    @Override
    public boolean execute() {
        String curCommitId = BranchHandler.getCurrentBranch().getCommitId();
        LogHandler.print(curCommitId);
        return true;
    }
}