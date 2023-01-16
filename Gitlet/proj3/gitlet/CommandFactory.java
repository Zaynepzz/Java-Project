package gitlet;

import java.io.File;

public class CommandFactory {

    public static Command getCommand(String... args){
        if("init".equals(args[0])){
            return new InitCommand();
        }
        if("add".equals(args[0])){
            File addFile = new File(args[1]);
            return new AddCommand(addFile);
        }
        if("commit".equals(args[0])){
            return new CommitCommand(args[1]);
        }
        if("checkout".equals(args[0])){
            return new CheckoutCommand(args);
        }
        if("log".equals(args[0])){
            return new LogCommand();
        }
        if("rm".equals(args[0])){
            return new RmCommand(new File(args[1]));
        }
        if("global-log".equals(args[0])){
            return new GlobalLogCommand();
        }
        if("find".equals(args[0])){
            return new FindCommand(args[1]);
        }
        if("status".equals(args[0])){
            return new StatusCommand();
        }
        if("branch".equals(args[0])){
            return new BranchCommand(args[1]);
        }
        if("rm-branch".equals(args[0])){
            return new RmBranchCommand(args[1]);
        }
        if("reset".equals(args[0])){
            return new ResetCommand(args[1]);
        }
        if("merge".equals(args[0])){
            return new MergeCommand(args[1]);
        }
        return null;
    }
}
