package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        // FILL THIS IN
        //
        InputChecker checker = new InputChecker();
        if(checker.isEmptyCommand(args)){
         Utils.message("Please enter a command.");
         System.exit(0);
        }

        if(checker.isNotExists(args[0])){
         Utils.message("No command with that name exists.");
         System.exit(0);
        }

        if(checker.isIncorrectOperands(args)){
         Utils.message("Incorrect operands.");
         System.exit(0);
        }

        if(checker.isNotInit(args[0])){
         Utils.message("Not in an initialized Gitlet directory.");
         System.exit(0);
        }

        Command command = CommandFactory.getCommand(args);

        try {
            command.execute();
        } catch (GitletException e) {
            System.err.printf(e.getMessage());
            System.err.println();
        }
        System.exit(0);

    }



}
