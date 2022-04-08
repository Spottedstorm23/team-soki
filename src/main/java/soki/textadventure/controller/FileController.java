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

    private String pathToJsonFile = "src/main/java/soki/textadventure/controller/text.json";

    public String getTextFromJson(int rowRequestet, int columnRequestet) {

        JSONObject jsonFileObject = readContent();

        String returnString = "ERROR: Konnte requested String nicht finden";

        JSONArray msg = (JSONArray) jsonFileObject.get("text");
        for (JSONObject innerObject : (Iterable<JSONObject>) msg) {
            long rowJSON = (Long) innerObject.get("row");
            long columnJSON = (Long) innerObject.get("column");

            if (rowRequestet == rowJSON & columnRequestet == columnJSON) {
                returnString = (String) innerObject.get("contents");
            }
        }

        return returnString;

    }

    private JSONObject readContent() {

        JSONObject jsonFileObject = null;
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(pathToJsonFile)) {
            jsonFileObject = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return jsonFileObject;
    }

    public void writeSavedStatsToFile(int row, int column) {

        JSONObject jsonFileObject = readContent();
        jsonFileObject.replace("save-row",row);
        jsonFileObject.replace("save-column",column);

        writeContent(jsonFileObject);

    }

    private void writeContent(JSONObject objectForJSON) {

        try (FileWriter file = new FileWriter(pathToJsonFile)) {
            file.write(objectForJSON.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
