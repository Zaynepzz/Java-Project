package gitlet;

import java.io.File;

/**
 * AddCommand that executes the implementation of Add.
 * @author ZhengPeng
 */
public class AddCommand implements Command {

    /**
     * the file to be added.
     */
    private File addFile;

    /**
     * setter of the class.
     * @param addF is the file to be added.
     */
    public void setAddFile(File addF) {
        this.addFile = addF;
    }

    /**
     * getter of the class.
     * @return the file
     */
    public File getAddFile() {
        return addFile;
    }

    @Override
    public boolean execute() {
        Stage stage = StageHandler.getStage();
        Commit commit = CommitHandler.getCurrentCommit();
        boolean addFileIsExists = addFile.exists();
        boolean commitContainAddFile = commit.isContain(addFile);
        boolean stageContainAddFile = stage.isContain(addFile);
        if (!addFileIsExists) {

            if (!commitContainAddFile && !stageContainAddFile) {
                throw new GitletException("File does not exist.");
            } else if (!commitContainAddFile && stageContainAddFile) {
                clearStage(stage);
            } else if (commitContainAddFile) {
                rmStage(stage);
            }
        } else {
            if (commitContainAddFile && commit.isSameHash(addFile)) {
                clearStage(stage);
            } else if (commitContainAddFile && !commit.isSameHash(addFile)) {
                addStage(stage);
            } else if (!commitContainAddFile) {
                addStage(stage);
            }
        }
        return true;
    }

    /**
     * add the file to the stage.
     * @param stage is the staging area.
     */
    private void addStage(Stage stage) {
        stage.add(addFile);
        StageHandler.save(stage);
    }

    /**
     * clear the stage.
     * @param stage is the staging area.
     */
    private void clearStage(Stage stage) {
        stage.clear(addFile);
        StageHandler.save(stage);
    }

    /**
     * remove the stage.
     * @param stage is the staging area.
     */
    private void rmStage(Stage stage) {
        stage.rm(addFile);
        StageHandler.save(stage);
    }


    /**
     * set the file to be the input.
     * @param addF is the file to be added.
     */
    public AddCommand(File addF) {
        this.addFile = addF;
    }
}
