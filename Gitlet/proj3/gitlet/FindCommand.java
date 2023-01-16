package gitlet;

import java.util.List;

public class FindCommand implements Command {

    private String findMessage;

    public FindCommand() {
    }

    public FindCommand(String findMessage) {
        this.findMessage = findMessage;
    }



    @Override
    public boolean execute() throws GitletException {
        if(LogHandler.findIdsByMessage(findMessage).size()==0){
            throw new GitletException("Found no commit with that message.");
        }
        return true;
    }

}
