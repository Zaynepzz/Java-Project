package gitlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LogHandler {

    public static void info(Log log) {
        Utils.writeContentsAppend(Config.LOG, log.toString());
    }

    public static List<String> findIdsByMessage(String message) {
        List<String> ids = new ArrayList<>();
        for (Log log : loadLogMap().values()) {
            if (log.getMessage().contains(message)) {
                String commitId = log.getCommitId();
                ids.add(commitId);
                System.out.println(commitId);
            }
        }
        return ids;
    }

    public static void printAll() {
        for (Log log:loadLogMap().values()) {
            printLog(log);
        }

    }

    public static void print(String commitId) {
        Map<String, Log> logMap = loadLogMap();
        Log log = logMap.get(commitId);
        printLog(log);
        printParentLog(log, logMap);

    }

    private static void printParentLog(Log log, Map<String, Log> logMap) {
        String firstParentId = log.getFirstParentId();
        if (!firstParentId.equals("0000000000000000000000000000000000000000")) {
            Log parentLog = logMap.get(firstParentId);
            printLog(parentLog);
            printParentLog(parentLog, logMap);
        }
    }

    private static Formatter createFormatter(){
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        return formatter;
    }

    public static void printLog(Log log) {
        Formatter formatter = createFormatter();
        formatter.format("===\n");
        formatter.format("commit %1$s\n", log.getCommitId());
        String firstId = log.getFirstParentId();
        String secondId = log.getSecondParentId();
        if (secondId != null
                && !"".equals(secondId)) {
            formatter.format("Merge: %1$s %2$s\n",
                    firstId.substring(0, 7),
                    secondId.substring(0, 7));
        }
        formatter.format("Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz\n",
                new Date(log.getTime()));
        String message = log.getMessage();
        formatter.format("%1$s\n", message);
        if (!firstId.equals
                ("0000000000000000000000000000000000000000")) {
            formatter.format("\n");
        }
        System.out.print(formatter.toString());
    }

    public static Map<String, Log> loadLogMap() {
        HashMap<String, Log> logMap = new HashMap<>();
        List<String> list = null;
        try {
            Path logPath = Config.LOG.toPath();
            list = Files.readAllLines(logPath,
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String logStr : list) {
            String[] strs = logStr.split("\t");
            if (strs.length == 5) {
                String str0 = strs[0];
                String str1 = strs[1];
                String str2 = strs[2];
                String str3 = strs[3];
                String str4 = strs[4];
                logMap.put(str1, new Log(str0, str1,
                        Long.parseLong(str2), str3, str4));
            } else {
                String str0 = strs[0];
                String str1 = strs[1];
                String str2 = strs[2];
                String str3 = strs[3];
                logMap.put(str1, new Log(str0,
                        str1, Long.parseLong(str2), str3));
            }
        }
        return logMap;
    }

    public static LogTree getLogTreeNodeByCommitId(String commitId) {
        Map<String, Log> logMap = loadLogMap();
        return makeLogTreeNode(logMap, logMap.get(commitId), 0);
    }

    private static LogTree makeLogTreeNode(Map<String,
            Log> logMap, Log rootLog, int depth) {
        LogTree rootNode = createRootNode(rootLog,depth);
        depth += 1;

        String firstId = rootLog.getFirstParentId();
        String secondId = rootLog.getSecondParentId();
        if (!firstId.equals
                ("0000000000000000000000000000000000000000")) {
            rootNode.setFirstParentNode(makeLogTreeNode(logMap,
                    logMap.get(firstId), depth));
        }

        if (secondId != null) {
            rootNode.setSecondParentNode(makeLogTreeNode(logMap,
                    logMap.get(secondId), depth));
        }
        return rootNode;
    }

    private static LogTree createRootNode(Log rootLog, int depth){
        LogTree rootNode = new LogTree();
        rootNode.setLog(rootLog);
        rootNode.setDepth(depth);
        return rootNode;
    }
}
