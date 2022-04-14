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

    //todo: mithilfe von Callback/Interface besser zusammenfassen, weniger Dopplung, aber dafür komplizierter

    private String pathToTestJsonFile = "src/main/java/soki/textadventure/controller/text.json";
    private String pathToPlayerDateJsonFile = "src/main/java/soki/textadventure/controller/playerdata.json";

    public void addPlayer(String name) {
        JSONObject playerPrototype = new JSONObject();
        playerPrototype.put("name",name);
        playerPrototype.put("id",0);
        playerPrototype.put("playthrough",0);
        playerPrototype.put("objects",new JSONArray());

        JSONObject playerdateObject = readContent(pathToPlayerDateJsonFile);

        JSONArray playerList = (JSONArray) playerdateObject.get("player");
        playerList.add(playerPrototype);

        writeContent(playerdateObject, pathToPlayerDateJsonFile);
    }

    public void changeLocation(String name, int iD){
        JSONObject playerdateObject = readContent(pathToPlayerDateJsonFile);
        JSONObject playerObject = findObjectFromPlayerInFileObject(playerdateObject, name);
        playerObject.replace("id",iD);
        writeContent(playerdateObject, pathToPlayerDateJsonFile);
    }

    public void addObject(String name, String object, Boolean isVisible) {
        JSONObject playerdateObject = readContent(pathToPlayerDateJsonFile);
        JSONObject playerObject = findObjectFromPlayerInFileObject(playerdateObject, name);

        JSONObject objectPrototype = new JSONObject();
        objectPrototype.put("name",object);
        objectPrototype.put("isVisible",isVisible);

        JSONArray objectList = (JSONArray) playerObject.get("objects");
        objectList.add(objectPrototype);

        writeContent(playerdateObject, pathToPlayerDateJsonFile);
    }

    public void setPlaythrough(String name, int playthrough) {
        JSONObject playerdateObject = readContent(pathToPlayerDateJsonFile);
        JSONObject playerObject = findObjectFromPlayerInFileObject(playerdateObject, name);
        playerObject.replace("playthrough",playthrough);
        writeContent(playerdateObject, pathToPlayerDateJsonFile);
    }

    public void changeVisible(String name, String object) {
        JSONObject playerdateObject = readContent(pathToPlayerDateJsonFile);
        JSONObject playerObject = findObjectFromPlayerInFileObject(playerdateObject, name);

        JSONArray objectArray = (JSONArray) playerObject.get("objects");

        for (JSONObject innerObject : (Iterable<JSONObject>) objectArray) {
            String objectName = (String) innerObject.get("name");

            if (objectName.equals(object)) {
                boolean visibleStatus = (boolean) innerObject.get("isVisible");
                innerObject.replace("isVisible",!visibleStatus);
            }
        }

        writeContent(playerdateObject, pathToPlayerDateJsonFile);
    }

    private JSONObject findObjectFromPlayerInFileObject(JSONObject fileObject, String name) {
        JSONArray objectArray = (JSONArray) fileObject.get("player");

        for (JSONObject innerObject : (Iterable<JSONObject>) objectArray) {
            String listName = (String) innerObject.get("name");

            if (listName.equals(name)) {
                return innerObject;
            }
        }
        return null;
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
