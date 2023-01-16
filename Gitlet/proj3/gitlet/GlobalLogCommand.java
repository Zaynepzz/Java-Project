package gitlet;

public class GlobalLogCommand implements Command {

    @Override
    public boolean execute() throws GitletException {
        LogHandler.printAll();
        return false;
    }
}
