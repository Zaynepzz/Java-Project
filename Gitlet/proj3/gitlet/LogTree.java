package gitlet;

public class LogTree {

    private Log log0;

    private LogTree firstParentNode;

    private LogTree secondParentNode;

    private int depth0;

    public void setLog(Log log) {
        this.log0 = log;
    }
    public void setFirstParentNode(LogTree firstParentNode) {
        this.firstParentNode = firstParentNode;
    }
    public void setSecondParentNode(LogTree secondParentNode) {
        this.secondParentNode = secondParentNode;
    }
    public void setDepth(int depth) {
        this.depth0 = depth;
    }

    public Log getLog() {
        return log0;
    }
    public LogTree getFirstParentTreeNode() {
        return firstParentNode;
    }
    public LogTree getSecondParentNode() {
        return secondParentNode;
    }
    public int getDepth() {
        return depth0;
    }

    public LogTree() {
    }

    public LogTree(Log log, LogTree firstParentNode,
                   LogTree secondParentNode, int depth) {
        this.log0 = log;
        this.firstParentNode = firstParentNode;
        this.secondParentNode = secondParentNode;
        this.depth0 = depth;
    }




}