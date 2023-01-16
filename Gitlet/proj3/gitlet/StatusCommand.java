package gitlet;

import java.io.File;
import java.util.*;

public class StatusCommand implements Command {

    private Formatter getFormatter(){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        return formatter;
    }

    private void formaterBranches(Formatter formatter){
        formatter.format("=== Branches ===\n");
        formatter.format("*%1$s\n",BranchHandler.getCurrentBranch().getBranchName());
        for(String branchname:sortFilename(BranchHandler.getOtherBranch())){
            formatter.format("%1$s\n",branchname);
        }

        formatter.format("\n");
    }

    private void formaterStage(Formatter formatter,Stage stage){
        formatter.format("=== Staged Files ===\n");
        for(String filename:sortFilename(stage.getAddMap().keySet())){
            formatter.format("%1$s\n",filename);
        }
        formatter.format("\n");
    }

    private void formaterRemovedFiles(Formatter formatter,Stage stage){
        formatter.format("=== Removed Files ===\n");
        for(String filename:sortFilename(stage.getRmSet())){
            formatter.format("%1$s\n",filename);
        }
        formatter.format("\n");
    }

    private void formaterModifiNotStaged(Formatter formatter,Commit commit,Map<String,File> workspacesFile,Stage stage){
        formatter.format("=== Modifications Not Staged For Commit ===\n");
        Set<String> modifySet = new HashSet<>();
        Set<String> keySet = commit.getBlobMap().keySet();
        for(String filename :keySet){
            File wsFile = workspacesFile.get(filename);
            if(wsFile!=null){
                if(!commit.isSameHash(wsFile)){
                    if(stage.isContain(wsFile)){
                        if(!stage.isSameHash(wsFile)){
                            String file = filename+" (modified)";
                            modifySet.add(file);
                        }
                    }else{
                        String file = filename+" (modified)";
                        modifySet.add(file);
                    }
                }
            }else {
                if(!stage.getRmSet().contains(filename)){
                    String file = filename+" (deleted)";
                    modifySet.add(file);
                }
            }
        }
        for(String filename:sortFilename(modifySet)){
            formatter.format("%1$s\n",filename);
        }
        formatter.format("\n");
    }

    private void formatterUntrackedFiles(Formatter formatter,Map<String,File> workspacesFile,Stage stage,Commit commit){
        formatter.format("=== Untracked Files ===\n");
        Set<String> untrackedSet = new HashSet<>();
        for(File file :workspacesFile.values()){
            boolean stageIsContainFile = stage.isContain(file);
            boolean commitIsContainFile = commit.isContain(file);
            if(!stageIsContainFile&&!commitIsContainFile){
                String path = Utils.relativePath(file);
                untrackedSet.add(path);
            }
        }
        untrackedSet = sortFilename(untrackedSet);
        for(String filename : untrackedSet){
            formatter.format("%1$s\n",filename);
        }
        formatter.format("\n");
    }

    @Override
    public boolean execute() throws GitletException {
        Formatter formatter = getFormatter();
        formaterBranches(formatter);

        Stage stage = StageHandler.getStage();
        formaterStage(formatter,stage);
        formaterRemovedFiles(formatter,stage);

        Map<String,File> workspacesFile = new HashMap<>();
        File gitDir = Config.GIT_DIR;
        for(String filename :new File(Config.GIT_DIR.getAbsolutePath()
                .substring(0,gitDir.getAbsolutePath()
                        .indexOf(gitDir.getName()))).list()){
            File file = new File(filename);
            if(file.isFile()){
                String path = Utils.relativePath(file);
                workspacesFile.put(path,file);
            }
        }

        Commit commit = CommitHandler.getCurrentCommit();
        formaterModifiNotStaged(formatter,commit,workspacesFile,stage);
        formatterUntrackedFiles(formatter,workspacesFile,stage,commit);

        System.out.print(formatter.toString());
        return true;
    }


    private Set<String> addAll(Set<String> set,Set<String> filenames){
        set.addAll(filenames);
        return set;
    }

    private Set<String> sortFilename(Set<String> filenames){
        Set<String> set = new TreeSet<>((o1, o2) -> o1.compareTo(o2));
        return addAll(set,filenames);
    }
}
