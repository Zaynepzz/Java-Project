package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class MergeCommand implements Command {

    private String giveBranchName;

    public MergeCommand() {
    }

    public MergeCommand(String giveBranchName) {
        this.giveBranchName = giveBranchName;
    }

    @Override
    public boolean execute() throws GitletException {
        Stage stage = StageHandler.getStage();
        checkStage(stage);

        Branch giveBranch = BranchHandler.getBranchByName(giveBranchName);
        checkGiveBranch(giveBranch);
        Branch curBranch = BranchHandler.getCurrentBranch();

        checkCurBranch(curBranch,giveBranch);

        Commit curCommit = CommitHandler.getCurrentCommit();
        Commit giveCommit = CommitHandler.getCommit(giveBranch.getCommitId());

        String splitCommitId = getSplitTreeNode(curBranch,giveBranch).getLog().getCommitId();
        Commit splitCommit = CommitHandler.getCommit(splitCommitId);

        checkIsAncestor(splitCommit,giveCommit);

        checkFastForwarded(splitCommit,curCommit);

        Set<String> noSameHashFilenames = getNoSameHashFilename(curCommit,giveCommit);

        Set<String> onlyCurFilenames = CreateOnlyCurFilenames(curCommit,giveCommit);

        Set<String> onlyGiveFilenames = CreateOnlyGiveFilenames(curCommit,giveCommit);

        checkHasUntrackedFile(onlyGiveFilenames,splitCommit,giveCommit);

        boolean isConflict = false;

        for(String filename:onlyGiveFilenames){
            if(splitCommit.isContain(filename)){
                if(!giveCommit.isSameHash(filename,splitCommit)){
                    isConflict = true;
                    dealConflictFile(filename,null,giveCommit);
                    new AddCommand(new File(filename)).execute();
                }
            }else{
                new CheckoutCommand(new String[]{"checkout",giveBranch.getCommitId(),"--",filename})
                        .execute();
                new AddCommand(new File(filename)).execute();
            }
        }

        for(String filename:onlyCurFilenames){
            if(splitCommit.isContain(filename)){
                if(curCommit.isSameHash(filename,splitCommit)){
                    new RmCommand(new File(filename)).execute();
                }else{
                    isConflict = true;
                    dealConflictFile(filename,curCommit,null);
                    File file = new File(filename);
                    new AddCommand(file).execute();
                }
            }else{

            }
        }

        for(String filename:noSameHashFilenames){
            boolean isContainFile = splitCommit.isContain(filename);
            if(isContainFile){
                if(!curCommit.isSameHash(filename,splitCommit)&&!giveCommit.isSameHash(filename,splitCommit)){
                    isConflict = true;
                    dealConflictFile(filename,curCommit,giveCommit);
                    File file = new File(filename);
                    new AddCommand(file).execute();
                }
                boolean curIsSameHash = curCommit.isSameHash(filename,splitCommit);
                boolean giveIsSameHash = giveCommit.isSameHash(filename,splitCommit);
                if(!curIsSameHash&&giveIsSameHash){

                }
                if(curIsSameHash&&!giveIsSameHash){
                    new CheckoutCommand(new String[]{"checkout",giveBranch.getCommitId(),"--",filename})
                            .execute();
                    File file = new File(filename);
                    new AddCommand(file).execute();
                }
            }else{
                isConflict = true;
                dealConflictFile(filename,curCommit,giveCommit);
                new AddCommand(new File(filename)).execute();
            }
        }

        if(isConflict){
            Utils.message("Encountered a merge conflict.");
        }

        return mergeCommit(curBranch,giveBranch);
    }

    private void checkHasUntrackedFile(Set<String> onlyGiveFilenames,Commit splitCommit,Commit giveCommit){
        for(String filename:onlyGiveFilenames){
            if(splitCommit.isContain(filename)){
                if(!giveCommit.isSameHash(filename,splitCommit)){
                    checkFilenameisExists(filename);
                }
            }else{
                checkFilenameisExists(filename);
            }
        }
    }

    private void checkFilenameisExists(String filename){
        if(Utils.join(Config.WORKSPACE,filename).exists()){
            throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
        }
    }


    private Set<String> CreateOnlyGiveFilenames(Commit curCommit,Commit giveCommit){
        Set<String> onlyGiveFilenames = new HashSet<>();
        onlyGiveFilenames.addAll(giveCommit.getBlobMap().keySet());
        onlyGiveFilenames.removeAll(curCommit.getBlobMap().keySet());
        return onlyGiveFilenames;
    }

    private Set<String> CreateOnlyCurFilenames(Commit curCommit,Commit giveCommit){
        Set<String> onlyCurFilenames = new HashSet<>();
        onlyCurFilenames.addAll(curCommit.getBlobMap().keySet());
        onlyCurFilenames.removeAll(giveCommit.getBlobMap().keySet());
        return onlyCurFilenames;
    }

    private void checkFastForwarded(Commit splitCommit,Commit curCommit){
        if(splitCommit.equals(curCommit)){
            if(new CheckoutCommand(new String[]{"checkout",giveBranchName}).execute()){
                throw new GitletException("Current branch fast-forwarded.");
            }
        }
    }

    private void checkIsAncestor(Commit splitCommit,Commit giveCommit){
        if(splitCommit.equals(giveCommit)){
            throw new GitletException("Given branch is an ancestor of the current branch.");
        }
    }


    private void checkStage(Stage stage){
        if(stage.isUpdate()){
            throw new GitletException("You have uncommitted changes.");
        }
    }

    private void checkGiveBranch(Branch giveBranch){
        if(giveBranch==null){
            throw new GitletException("A branch with that name does not exist.");
        }
    }

    private void checkCurBranch(Branch curBranch,Branch giveBranch){
        if(curBranch.getBranchName().equals(giveBranch.getBranchName())){
            throw new GitletException("Cannot merge a branch with itself.");
        }
    }

    private boolean mergeCommit(Branch curBranch,Branch giveBranch){
        String curCommitId = curBranch.getCommitId();
        String giveCommitId = giveBranch.getCommitId();
        String message = "Merged "+giveBranch.getBranchName();
        message += " into "+curBranch.getBranchName();
        message += curBranch.getBranchName();
        message += ".";
        Stage stage = StageHandler.getStage();
        Commit commit = CommitHandler.createCommit(message);
        commit.setSecondParentId(giveBranch.getCommitId());
        commit.add(stage.getAddMap());
        commit.remove(stage.getRmSet());
        CommitHandler.save(commit);

        clearStage(stage);

        curBranch.setCommitId(CommitHandler.getCommitId(commit));
        BranchHandler.save(curBranch);
        LogHandler.info(new Log(curCommitId,curBranch.getCommitId(),message,giveCommitId));
        return true;
    }

    private void removeStage(Commit commit,Stage stage){
        commit.remove(stage.getRmSet());
        CommitHandler.save(commit);
    }

    private void clearStage(Stage stage){
        stage.clear();
        StageHandler.save(stage);
    }

    private String getCurContents(String filename,Commit curCommit){
        String curContents = "";
        if(curCommit!=null){
            File file = Utils.blobJoin(Config.BLOB_DIR,
                    curCommit.getBlobMap().get(filename).getRef());
            curContents = Utils.readContentsAsString(file);

        }
        return curContents;
    }

    private String getGiveContents(String filename,Commit giveCommit){
        String giveContents = "";

        if(giveCommit!=null){
            File file = Utils.blobJoin(Config.BLOB_DIR,
                    giveCommit.getBlobMap().get(filename).getRef());
            giveContents = Utils.readContentsAsString(file);
        }
        return giveContents;
    }

    private void dealConflictFile(String filename,Commit curCommit,Commit giveCommit){
        String curContents = getCurContents(filename,curCommit);
        String giveContents = getGiveContents(filename,giveCommit);

        StringBuilder sb = new StringBuilder();
        sb.append("<<<<<<< HEAD\n")
                .append(curContents+"=======\n")
                .append(giveContents+">>>>>>>\n");
        String str = sb.toString();
        File file = Utils.join(Config.WORKSPACE,filename);
        Utils.writeContents(file,str);
    }


    /**
     * @param curBranch current split
     * @param giveBranch given split
     * @return split commit
     */
    public LogTree getSplitTreeNode(Branch curBranch,Branch giveBranch){
        Set<LogTree> unionSet = new HashSet<>();
        addUnionLogTreeNode(unionSet,LogHandler.getLogTreeNodeByCommitId(curBranch.getCommitId())
                ,LogHandler.getLogTreeNodeByCommitId(giveBranch.getCommitId()));

        LogTree minDepthNode = null;
        for(LogTree node:unionSet){
            if(minDepthNode!=null){
                int minDepth = minDepthNode.getDepth();
                int depth = node.getDepth();
                if(minDepth > depth){
                    minDepthNode = node;
                }
            }else {
                minDepthNode = node;
            }
        }
        return minDepthNode;
    }

    public void addUnionLogTreeNode(Set<LogTree> unionSet,LogTree curNode,LogTree giveTree){
        boolean isContain = isContain(curNode,giveTree);
        if(isContain){
            unionSet.add(curNode);
        }
        LogTree first = curNode.getFirstParentTreeNode();
        LogTree second = curNode.getSecondParentNode();
        if(first!=null){
            addUnionLogTreeNode(unionSet,first,giveTree);
        }

        if(second!=null){
            addUnionLogTreeNode(unionSet,second,giveTree);
        }
    }

    public boolean isContain(LogTree a,LogTree tree){
        boolean flag = a.getLog().getCommitId().equals(tree.getLog().getCommitId());
        if(flag){
            return true;
        }
        boolean flag2 = tree.getFirstParentTreeNode()!=null&&isContain(a,tree.getFirstParentTreeNode());
        if(flag2){
            return true;
        }
        boolean flag3 = tree.getSecondParentNode()!=null&&isContain(a,tree.getSecondParentNode());
        if(flag3){
            return true;
        }
        return false;
    }


    public Set<String> getNoSameHashFilename(Commit curCommit,Commit mergeCommit){
        Set<String> set = new HashSet<>();
        Set<String> keySet = curCommit.getBlobMap().keySet();
        for(String filename:keySet){
            Blob merge = mergeCommit.getBlobMap().get(filename);
            boolean mergeisNotNull = merge!=null;
            if(mergeisNotNull&&!curCommit.getBlobMap().get(filename).getRef().equals(merge.getRef())){
                set.add(filename);
            }
        }
        return set;
    }
}
