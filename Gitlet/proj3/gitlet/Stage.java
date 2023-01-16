package gitlet;

import gitlet.Blob;
import gitlet.Dumpable;
import gitlet.Utils;
import gitlet.BlobHandler;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Stage implements Serializable, Dumpable {

    private Map<String, Blob> addMap;

    private Set<String> rmSet;

    public void setAddMap(Map<String, Blob> addMap) {
        this.addMap = addMap;
    }
    public void setRmSet(Set<String> rmSet) {
        this.rmSet = rmSet;
    }
    public Map<String, Blob> getAddMap() {
        return addMap;
    }
    public Set<String> getRmSet() {
        return rmSet;
    }

    @Override
    public void dump() {

    }

    public Stage(Map<String, Blob> addMap, Set<String> rmSet) {
        this.addMap = addMap;
        this.rmSet = rmSet;
    }

    public boolean isUpdate(){
        boolean addMapSize = addMap.size()!=0;
        boolean rmSetSize = rmSet.size()!=0;
        return addMapSize||rmSetSize;
    }



    public boolean isSameHash(File file){
        return addMap.get(Utils.relativePath(file)).getRef()
                .equals(Utils.sha1(Utils.readContents(file)));
    }

    public boolean isContain(File file){
        return addMap.containsKey(Utils.relativePath(file));
    }

    public void add(File file){
        String fileName = Utils.relativePath(file);
        this.addMap.put(fileName,BlobHandler.createBlob(file));
        this.rmSet.remove(fileName);
    }

    public void rm(File file){
        this.addMap.remove(Utils.relativePath(file));
        this.rmSet.add(Utils.relativePath(file));
    }

    public void clear(File file){
        addMap.remove(Utils.relativePath(file));
        rmSet.remove(Utils.relativePath(file));
    }

    public void clear(){
        this.addMap.clear();
        this.rmSet.clear();
    }

}
