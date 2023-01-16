package gitlet;

import java.io.File;

public class RmCommand implements Command {

    private File rmFile;

    public RmCommand() {
    }

    public RmCommand(File rmFile) {
        this.rmFile = rmFile;
    }

    @Override
    public boolean execute() throws GitletException {
        Stage stage = StageHandler.getStage();
        checkNoReasonToRemove(stage);
        boolean isContainRmFile = stage.isContain(rmFile);
        if(isContainRmFile){
            stage.clear(rmFile);
            StageHandler.save(stage);
            return true;
        }
        boolean commitHasRmFile = CommitHandler.getCurrentCommit().isContain(rmFile);
        if(commitHasRmFile){
            stage.rm(rmFile);
            Utils.restrictedDelete(rmFile);
            StageHandler.save(stage);
            return true;
        }
        return false;
    }

    private void checkNoReasonToRemove(Stage stage){
        if(!CommitHandler.getCurrentCommit().isContain(rmFile)&&!stage.isContain(rmFile)){
            throw new GitletException("No reason to remove the file.");
        }
    }

}
