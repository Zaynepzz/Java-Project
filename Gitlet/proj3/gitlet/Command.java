package gitlet;

import gitlet.GitletException;

public interface Command {

    boolean execute() throws GitletException;
}
