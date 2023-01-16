package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StageHandler {
    public static void save(Stage stage){
        Utils.writeObject(Config.STAGE,stage);
    }

    public static Stage getStage(){
        Stage stage = null;
        File stageFile = Config.STAGE;

        if(!stageFile.exists()){
            Map<String, Blob> map = new HashMap<>();
            Set<String> set = new HashSet<>();
            stage = new Stage(map,set);
            return stage;
        }
        return Utils.readObject(Config.STAGE,Stage.class);
    }


}
