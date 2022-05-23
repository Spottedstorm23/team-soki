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
    private String pathToPlayerDateJsonFile = "src/main/java/soki/textadventure/controller/jsonFiles/playerdata.json";
    private String pathToTextDataJsonFile = "src/main/java/soki/textadventure/controller/jsonFiles/text.json";

    /*Functions for Playerdata*/
    public void giveallHaraItemsBack(){
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        JSONArray haraArray = (JSONArray) playerdataObject.get("haraList");
        JSONArray objectList = (JSONArray) playerdataObject.get("objects");

        for (Object haraObjectName: haraArray) {
            objectList.add(constructObjectForObjectArray((String)haraObjectName, true));
        }
        haraArray.clear();

        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    public void transferItemToHara(String objectName){
        removeObject(objectName);

        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        JSONArray haraArray = (JSONArray) playerdataObject.get("haraList");
        haraArray.add(objectName);

        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    public boolean checkIfObjectExists(String objectName){
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        JSONArray objectArray = (JSONArray) playerdataObject.get("objects");

        boolean objectExists = false;

        for (JSONObject innerObject : (Iterable<JSONObject>) objectArray) {
            String objectNameinFile = (String) innerObject.get("name");

            if (objectNameinFile.equals(objectName)) {
                objectExists = true;
            }
        }

        return objectExists;
    }

    public String[] listVisibleObjects() {
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        JSONArray objectArray = (JSONArray) playerdataObject.get("objects");

        String[] listOfStringObjects = new String[objectArray.size()];

        //todo: in for(i) Schleife umbauen
        int i=0;
        for (JSONObject innerObject : (Iterable<JSONObject>) objectArray) {
            listOfStringObjects[i] = (String) innerObject.get("name");
            i++;
        }

        return listOfStringObjects;
    }

    public void removeObject(String object){
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        JSONArray objectArray = (JSONArray) playerdataObject.get("objects");

        JSONObject removeObject = null;

        for (JSONObject innerObject : (Iterable<JSONObject>) objectArray) {
            String objectName = (String) innerObject.get("name");

            if (objectName.equals(object)) {
                removeObject = innerObject; //kann nicht direkt entfernt werden,
            }                               // weil das sonst die Schleife durcheinander bringt
        }
        
        objectArray.remove(removeObject);

        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    public void changeLocation(int chapternr, int dialognr){
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        playerdataObject.replace("chapter",chapternr);
        playerdataObject.replace("dialog",dialognr);
        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    public void addObject(String object, Boolean isVisible) {
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);

        JSONArray objectList = (JSONArray) playerdataObject.get("objects");
        objectList.add(constructObjectForObjectArray(object, isVisible));

        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    private JSONObject constructObjectForObjectArray(String object, Boolean isVisible){
        JSONObject objectPrototype = new JSONObject();
        objectPrototype.put("name",object);
        objectPrototype.put("isVisible",isVisible);

        return objectPrototype;
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

    public void initializePlayerdata() {
        JSONObject playerdataObject = readContent(pathToPlayerDateJsonFile);
        playerdataObject.replace("chapter", 0);
        playerdataObject.replace("dialog", 0);
        playerdataObject.replace("objects" ,new JSONArray());
        playerdataObject.replace("name", "user");
        playerdataObject.replace("playthrough",0);
        playerdataObject.replace("haraList",new JSONArray());

        writeContent(playerdataObject, pathToPlayerDateJsonFile);
    }

    /*Functions for Textdata*/
    public int[] getNextDialogNumbersAndExecuteFunction(int oldChapternr, int oldDialognr, String playerCommand, String playerTarget){
        //Übergebe AKTUELLE Chapter und Dialog, Gibt Nummern für nächsten Dialog zurück
        JSONObject dialog = getSpezificDialogFromJSON(oldChapternr, oldDialognr);
        JSONArray commandArray = (JSONArray) dialog.get("commands");

        int newChapter, newDialog;

        for (JSONObject command:(Iterable<JSONObject>) commandArray) {
            if (playerCommand.equals((String)command.get("command"))){
                JSONArray targetArray = (JSONArray) command.get("targets");

                for (JSONObject target:(Iterable<JSONObject>) targetArray) {
                    if (playerTarget.equals((String)target.get("target"))){
                        newChapter = (int) (long) target.get("goToChapter");
                        newDialog = (int) (long) target.get("goToDialog");

                        JSONArray checkForObjects = (JSONArray) target.get("checkObject");
                        if (checkForObjects != null) {
                            for (JSONObject checkForObject:(Iterable<JSONObject>) checkForObjects) {
                                if (checkIfObjectExists((String)checkForObject.get("name"))) {
                                    newChapter = (int) (long) checkForObject.get("goToChapter");
                                    newDialog = (int) (long) checkForObject.get("goToDialog");

                                    JSONObject executeFunction = (JSONObject) checkForObject.get("executeFunction");
                                    if (executeFunction != null) {
                                        executeFunctionFromJSON(executeFunction);
                                    }

                                    return new int[]{newChapter, newDialog};
                                }
                            }
                        }
                        JSONObject executeFunction = (JSONObject) target.get("executeFunction");
                        if (executeFunction != null) {
                            executeFunctionFromJSON(executeFunction);
                        }

                        return new int[]{newChapter, newDialog};
                    }
                }
            }
        }

        return null;
    }

    public String getText (int chapternr, int dialognr) {
        JSONObject dialogObject = getSpezificDialogFromJSON(chapternr, dialognr);
        return (String) dialogObject.get("text");
    }

    private JSONObject getSpezificDialogFromJSON (int chapternr, int dialognr){
        JSONObject textDataObject = readContent(pathToTextDataJsonFile);
        JSONArray chapterArray = (JSONArray) textDataObject.get("chapters");

        for (JSONObject chapter:(Iterable<JSONObject>) chapterArray) {
            if (chapternr == (int) (long) chapter.get("number")) {
                JSONArray dialogArray = (JSONArray) chapter.get("dialogs");

                for (JSONObject dialog : (Iterable<JSONObject>) dialogArray) {
                    if (dialognr == (int) (long) dialog.get("number")) {
                        return dialog;
                    }
                }
            }
        }

        return null;
    }

    private void executeFunctionFromJSON (JSONObject functionObject) {
        int functionId = (int) (long) functionObject.get("functionId");
        Object parameter1 = functionObject.get("parameter1");
        Object parameter2 = functionObject.get("parameter2");

        switch (functionId) {
            case 0:
                giveallHaraItemsBack();
                break;
            case 1:
                transferItemToHara((String) parameter1);
                break;
            case 2:
                addObject((String) parameter1, (boolean) parameter2);
                break;
            case 3:
                removeObject((String) parameter1);
                break;
            case 4:
                changeVisible((String) parameter1);
                break;
        }
    }

    /*internal Functions*/
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