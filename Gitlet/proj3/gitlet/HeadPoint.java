package gitlet;

import gitlet.Branch;

public class HeadPoint {

    private Branch branch;

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Branch getBranch() {
        return branch;
    }

    public HeadPoint(Branch branch) {
        this.branch = branch;
    }

}