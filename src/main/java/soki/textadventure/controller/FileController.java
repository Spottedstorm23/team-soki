package soki.textadventure.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class FileController {

    private String pathToPlayerDateJsonFile = "src/main/java/soki/textadventure/controller/playerdata.json";

    public void changeLocation(int iD){
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        playerdataObject.replace("id",iD);
        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    public void addObject(String object, Boolean isVisible) {
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);

        JSONObject objectPrototype = new JSONObject();
        objectPrototype.put("name",object);
        objectPrototype.put("isVisible",isVisible);

        JSONArray objectList = (JSONArray) playerdataObject.get("objects");
        objectList.add(objectPrototype);

        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    public void setPlaythrough(int playthrough) {
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        playerdataObject.replace("playthrough",playthrough);
        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    public void changeVisible(String object) {
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        JSONArray objectArray = (JSONArray) playerdataObject.get("objects");

        for (JSONObject innerObject : (Iterable<JSONObject>) objectArray) {
            String objectName = (String) innerObject.get("name");

            if (objectName.equals(object)) {
                boolean visibleStatus = (boolean) innerObject.get("isVisible");
                innerObject.replace("isVisible",!visibleStatus);
            }
        }

        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    private void writeContent(JSONObject objectForJSON, String pathToJsonFile) {

        try (FileWriter file = new FileWriter(pathToJsonFile)) {
            file.write(objectForJSON.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONObject readContent(String pathToJsonFile) {

        JSONObject jsonFileObject = null;
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(pathToJsonFile)) {
            jsonFileObject = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return jsonFileObject;
    }
}
