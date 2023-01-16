package gitlet;

import java.io.File;
import java.util.*;

public class CheckoutCommand implements Command {

    private String[] args;



    @Override
    public boolean execute() {
        int length = args.length;
        if(length == 2){
            return checkoutBranch(args);
        }else if(length == 3){
            return checkoutFilename(args);
        }else if(length == 4){
            return checkoutCommitIdFilename(args);
        }
        return true;
    }

    private boolean checkoutBranch(String... args){
        Branch branch = BranchHandler.getBranchByName(args[1]);
        checkBranch(branch);
        Map<String, File> workspacesFile = getWorkSpaceFiles();
        Commit curCommit = CommitHandler.getCurrentCommit();
        Set<String> untrackedSet = getUntrackedSet(curCommit,workspacesFile);
        Commit toCommit = CommitHandler.getCommit(branch.getCommitId());
        checkUntracked(untrackedSet,toCommit);
        deleteCurCommitFile(curCommit);
        checkoutToCommitFile(toCommit);
        HeadPointHandler.moveTo(branch);
        return true;
    }
    private void checkoutToCommitFile(Commit toCommit){
        Map<String, Blob> toBlobMap = toCommit.getBlobMap();
        for(String filename:toBlobMap.keySet()){
            File file = new File(filename);
            Utils.writeContents(file,
                    Utils.readContents(Utils.blobJoin(Config.BLOB_DIR,
                            toBlobMap.get(filename).getRef())));
        }
    }

    private void deleteCurCommitFile(Commit curCommit){
        Set<String> filenames = curCommit.getBlobMap().keySet();
        for(String filename:filenames){
            File file = new File(filename);
            if(file.exists()){
                file.delete();
            }
        }
    }


    private void checkBranch(Branch branch){
        if(branch==null){
            throw new GitletException("No such branch exists.");
        }
        if(branch.getBranchName().equals(BranchHandler.getCurrentBranch().getBranchName())){
            throw new GitletException("No need to checkout the current branch.");
        }
    }

    private void checkUntracked(Set<String> untrackedSet,Commit toCommit){
        for(String filename:untrackedSet){
            if(toCommit.isContain(new File(filename))){
                throw new GitletException("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }
    }

    private Map<String, File> getWorkSpaceFiles(){
        Map<String, File> workspacesFile = new HashMap<>();
        String[] list = Config.WORKSPACE.list();
        for(String filename : list){
            File file = new File(filename);
            if(file.isFile()){
                workspacesFile.put(Utils.relativePath(file),file);
            }
        }
        return workspacesFile;
    }

    private Set<String> getUntrackedSet(Commit curCommit,Map<String, File> workspacesFile){
        Set<String> untrackedSet = new HashSet<>();
        for(File file :workspacesFile.values()){
            if(!curCommit.isContain(file)){
                untrackedSet.add(Utils.relativePath(file));
            }
        }
        return untrackedSet;
    }

    private boolean checkoutFilename(String... args){
        if(!args[1].equals("--")){
            throw new GitletException("Incorrect operands.");
        }
        return executeCheckoutByCommitId(CommitHandler.getCurrentCommitId(),args[2]);
    }

    private boolean checkoutCommitIdFilename(String... args){
        if(!args[2].equals("--")){
            throw new GitletException("Incorrect operands.");
        }
        return executeCheckoutByCommitId(args[1],args[3]);
    }

    private boolean executeCheckoutByCommitId(String commitId,String filename){
        if(CommitHandler.getCommit(commitId)==null){
            throw new GitletException("No commit with that id exists.");
        }
        File file = new File(filename);
        String path = Utils.relativePath(file);
        Blob blob = CommitHandler.getCommit(commitId).getBlobMap().get(path);
        if(blob == null){
            throw new GitletException("File does not exist in that commit.");
        }
        byte[] bs = Utils.readContents(
                Utils.blobJoin(Config.BLOB_DIR,blob.getRef()));
        Utils.writeContents(file,bs);
        return true;
    }

    public CheckoutCommand(String[] args) {
        this.args = args;
    }

}