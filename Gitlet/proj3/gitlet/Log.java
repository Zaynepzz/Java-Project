package gitlet;

import java.util.Date;

public class Log {
    private String firstParentId;
    private String commitId;
    private long time;
    private String message;
    private String secondParentId;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.firstParentId).append("\t");
        sb.append(this.commitId).append("\t");
        sb.append(this.time).append("\t");
        sb.append(this.message);
        if(secondParentId!=null){
            sb.append("\t").append(this.secondParentId);
        }
        String str = sb.toString();
        return str;
    }

    public void setFirstParentId(String firstParentId) {
        this.firstParentId = firstParentId;
    }
    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setSecondParentId(String secondParentId) {
        this.secondParentId = secondParentId;
    }
    public String getFirstParentId() {
        return firstParentId;
    }
    public String getCommitId() {
        return commitId;
    }
    public long getTime() {
        return time;
    }
    public String getMessage() {
        return message;
    }
    public String getSecondParentId() {
        return secondParentId;
    }


    public Log(String firstParentId, String commitId, String message) {
        this.firstParentId = firstParentId;
        this.commitId = commitId;
        long t = new Date().getTime();
        this.time = t;
        this.message = message;
    }

    public Log(String firstParentId, String commitId, String message,String secondParentId) {
        this(firstParentId,commitId,message);
        this.secondParentId = secondParentId;
    }

    public Log(String firstParentId, String commitId,long time, String message) {
        this(firstParentId,commitId,message);
        this.time = time;
    }

    public Log(String firstParentId, String commitId, long time, String message, String secondParentId) {
        this(firstParentId,commitId,time,message);
        this.secondParentId = secondParentId;
    }

}