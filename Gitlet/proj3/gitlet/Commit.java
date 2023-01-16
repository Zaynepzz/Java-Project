package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class Commit implements Serializable, Dumpable {
    private Date timestamp;
    private String message;
    private Map<String, Blob> blobMap;
    private String firstParentId;
    private String secondParentId;

    public void setSecondParentId(String secondParentId) {
        this.secondParentId = secondParentId;
    }
    public void setFirstParentId(String firstParentId) {
        this.firstParentId = firstParentId;
    }
    public void setBlobMap(Map<String, Blob> blobMap) {
        this.blobMap = blobMap;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public String getMessage() {
        return message;
    }
    public Map<String, Blob> getBlobMap() {
        return blobMap;
    }
    public String getFirstParentId() {
        return firstParentId;
    }
    public String getSecondParentId() {
        return secondParentId;
    }

    @Override
    public boolean equals(Object o) {
        Commit obj = (Commit) o;
        boolean blobMapFlag = this.getBlobMap().size()==obj.getBlobMap().size();
        boolean messageFlag = this.message.equals(obj.getMessage());
        boolean timeFlag = this.getTimestamp().equals(obj.getTimestamp());
        boolean firstParentIdFlag = this.firstParentId.equals(obj.firstParentId);

        return blobMapFlag
                &&messageFlag
                &&timeFlag
                &&firstParentIdFlag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, message, blobMap, firstParentId, secondParentId);
    }

    @Override
    public void dump() {

    }

    public boolean isSameHash(File file){
        Blob blob = blobMap.get(Utils.relativePath(file));
        return blob.getRef().equals(Utils.sha1(Utils.readContents(file)));
    }


    public boolean isContain(File file){
        return blobMap.containsKey(Utils.relativePath(file));
    }

    public int add(Map<String,Blob> updateMap){
        blobMap.putAll(updateMap);
        return updateMap.size();
    }

    public boolean isContain(String filename){
        return blobMap.containsKey(filename);
    }

    public int remove(Set<String> rmSet){
        for(String key:rmSet){
            blobMap.remove(key);
        }
        return rmSet.size();
    }

    public boolean isSameHash(String filename,Commit splitCommit){
        String splitRef = splitCommit.getBlobMap().get(filename).getRef();
        if(this.getBlobMap().get(filename).getRef().equals(splitRef)){
            return true;
        }
        return false;
    }

    public Commit() {}

    public Commit(Date timestamp, String message,Map<String,Blob> blobMap,String firstParentId,String secondParentId) {
        this(timestamp,message,blobMap,firstParentId);
        this.secondParentId = secondParentId;
    }

    public Commit(Date timestamp, String message,Map<String,Blob> blobMap,String firstParentId) {
        this(timestamp,message);
        this.blobMap = blobMap;
        this.firstParentId = firstParentId;
    }

    public Commit(Date timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
        Map<String, Blob> map = new HashMap<>();
        this.blobMap = map;
        this.firstParentId = "0000000000000000000000000000000000000000";
    }


}