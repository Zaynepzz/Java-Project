package gitlet;

/**
 * the branch class.
 * @author ZhengPeng
 */
public class Branch {
    /**
     * the name of the branch.
     */
    private String branchName;
    /**
     * the commitID of the branch.
     */
    private String commitId;

    /**
     * empty constructor.
     */
    public Branch() {
    }

    /**
     * constuctor of branch object.
     *
     * @param branch is the branchName.
     * @param iD is the commitID.
     */
    public Branch(String branch, String iD) {
        this.branchName = branch;
        this.commitId = iD;
    }

    /**
     * getter of commitID.
     *
     * @return commitID.
     */
    public String getCommitId() {
        return commitId;
    }

    /**
     * setter of commitID.
     *
     * @param iD is the commitID.
     */
    public void setCommitId(String iD) {
        this.commitId = iD;
    }

    /**
     * getter of branchName.
     *
     * @return the branchName
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * setter of branchName.
     *
     * @param branch is the branchName.
     */
    public void setBranchName(String branch) {
        this.branchName = branch;
    }
}