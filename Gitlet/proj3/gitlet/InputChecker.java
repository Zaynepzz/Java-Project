package gitlet;

import java.util.HashSet;
import java.util.Set;

public class InputChecker {

    private Set<String> commandNameSet = null;

    public InputChecker(){
        this.commandNameSet = new HashSet<>();
        commandNameSet.add("init");
        commandNameSet.add("add");
        commandNameSet.add("commit");
        commandNameSet.add("checkout");
        commandNameSet.add("log");
        commandNameSet.add("rm");
        commandNameSet.add("global-log");
        commandNameSet.add("find");
        commandNameSet.add("status");
        commandNameSet.add("branch");
        commandNameSet.add("rm-branch");
        commandNameSet.add("reset");
        commandNameSet.add("merge");
        commandNameSet.add("add-remote");
        commandNameSet.add("rm-remote");
        commandNameSet.add("push");
        commandNameSet.add("fetch");
        commandNameSet.add("pull");
    }

    public boolean isEmptyCommand(String... args){
        return args.length == 0;
    }

    public boolean isNotExists(String commandName){
        return !commandNameSet.contains(commandName);
    }

    public boolean isIncorrectOperands(String... args){
        String commandName = args[0];
        if(commandName.equals("init")
                ||commandName.equals("log")
                ||commandName.equals("global-log")
                ||commandName.equals("status")){
            return args.length != 1;
        }
        if(commandName.equals("add")
                ||commandName.equals("commit")
                ||commandName.equals("rm")
                ||commandName.equals("find")
                ||commandName.equals("branch")
                ||commandName.equals("rm-branch")
                ||commandName.equals("reset")
                ||commandName.equals("merge")
                ||commandName.equals("rm-remote")){
            return args.length != 2;
        }
        if(commandName.equals("add-remote")
                ||commandName.equals("push")
                ||commandName.equals("fetch")
                ||commandName.equals("pull")){
            return args.length != 3;
        }
        if(commandName.equals("checkout")){
            if(args.length == 2){
                return false;
            }
            if(args.length == 3&&args[1].equals("--")){
                return false;
            }
            if(args.length == 4&&args[2].equals("--")){
                return false;
            }
        }
        return true;
    }

    public boolean isNotInit(String commandName){
        if(Config.GIT_DIR.isDirectory()
                || commandName.equals("init")){
            return false;
        }
        return true;
    }


}
