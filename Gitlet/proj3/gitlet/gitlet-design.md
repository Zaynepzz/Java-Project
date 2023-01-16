# Gitlet Design Document

**Zheng,Peng**:

## Classes and Data Structures
# Commit
This class represents a single snapshot of the content needed to be saved, which makes it
available to retrieve later on.

**Fields**
1. String id: represents the commit id of this single commit in form of String.
2. String parent: a string format parent ID indicates the parent of this commit.
3. String message: A string that records the message of each commit.
4. Date: a Java Date format data that represents the timestamp of commit.
5. HashMap<String, String>: A HashMap that stores the content of each commit.

#Repo
This class represents the repo that Gitlet could run init command and make it trackable and 
available for further Gitlet command such as commit, checkout, add, and etc.

**Fields**
1. String CWD: String that represents the currrent working directory.
2. String File_Sep String for the file spererator.
3. String GitLet_Directory: Working_Directory combines with the seperator and ".gitlet"
4. String Commit_Dir: New path that combines Gitlet_Dir + File_Sep + ".commits'
5. String Version_Dir: New path that combines Gitlet_Dir + File_Sep + ".Version"
6. String Staging_Area: New path that combiens Gitlet_Dir + File-Sep + ".Stage"
7. Long serialVersionUID = 12345678901L;
8. HashMap<String, String> branches: HashMap of all branches in repo with sting name
    as key and SHA-1 ID of the branch head as values.
9. HashMap<String, Commmit> tree: Hash Map of all commits in repo with the SHA-1 ID of commit
at key and pointer to commit object as values
10. String head: the head pointer
11. String Master: the master pointer

## Algorithms
#Commit
1. String getId() return the ID of current commit
2. String getParent() return the parent
3. String getMessage() return the message
4. Date getCommitDate() return the timestamp
5. HashMap<String, String> getContents() return the content contained in a new HashMap
6. toString() format into a similar layout as the git interface displayed in the terminal 
7. String getHash(): 

#Repo
1. Repo init(): Create a new gitlet repo by making directory from Gitlet_Dir, Staging_Area,
Commit_Dir, Version_Dir, and then run the repo constructor to make the current directory to
become repo.

2. void add(String filename): search through the CWD to see if the file exists. If yes, then hash
the file and put move the original head and master pointer pointing to it. Then create the path under
the Staging_Area directory.

3. void commit(String message): After making sure that there is a valid commit message, search through
the staging area to see whether there is file to be committed. If yes, then we should hash the content,
and move the head and master pointer to the latest one. Last step will be creating a new commit using the 
constructor and take the hashed content as parameter and put it under the correct branch.

4. status(): first use system.out.println to display the layout that is similar to the git product
, which includes Branches, Staged Files, Removed Files, and Modification Not Staged for Commit. Under
each section, we should print out the corresponding file that is already been created before using add, rm,
commit,and checkout().

5. Checkout(): Plan not ready


## Persistence
In order to persist the settings of the repo, we will need to save the state of the commit 
after each call to the gitlet.main. To do this:

First, Write the repo HashMaps to disk.
We can serialize them into bytes that we can eventually write to a specially named file on disk. 
This can be done with writeObject method from the Utils class.

Second, Write all the Commit objects to disk. We can serialize the Commit objects and write them to files on disk 
(for example, “myCommit” file, “myCommit1” file, etc.). 
This can be done with the writeObject method from the Utils class. 
We will make sure that our Commit class implements the Serializable interface.

In order to retrieve our state, before executing any code, we need to search for the saved files in the 
working directory (folder in which our program exists) and load the objects that we saved in them. 
Since we set on a file naming convention, our program always knows which files it should look for. 
We can use the readObject method from the Utils class to read the data of files as and deserialize the objects 
we previously wrote to these files.
