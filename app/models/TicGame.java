package models;

import play.mvc.*;
import play.libs.*;
import play.libs.F.*;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class TicGame {

    private static List<Player<String>> connections = new LinkedList<>();

    public static void start(WebSocket.In<String> in, WebSocket.Out<String> out, UUID uuid) {
        connections.add(new Player<String>(out, uuid));
        printConn(connections);
        
        in.onMessage(new Callback<String>() {

            private ServiceInjector injector = new FieldWebInjector();
            private FieldWeb app = null;

            public void invoke(String event) {
            	JsonPoint p = stringToJson(event);

		        if (p.status() == GameStatus.PARSE_JSON_ERROR.ordinal()) {
		            out.write(jsonToString(p));
		            return;
		        }

		        JsonPoint res = new JsonPoint();
		        switch (p.status()) {
		            case 0:
                        if (app != null) {
                            // app already run
                        }
                        app = injector.getField(p.x(), p.y());
                        res = app.startGame();
                        break;
		            case 3: 
                        if (app == null) {
                            // app not started yet
                        }
                        res = app.nextMove(p.x(), p.y()); break;
		            /*case 11: res = app.stopGame();
                        out.write("{x:0,y:0,status:5,details:\'Game stopped by user\'}");
		                out.close();
		                return;*/
		            default:
		                res.status(GameStatus.UNKNOWN_STATUS);
		        		out.write(jsonToString(res));
		                return;
		        }
                //res.status(GameStatus.GAME_OVER);
		        out.write(jsonToString(res));
            }
        });
        
        in.onClose(new Callback0() {
            public void invoke() {
                Iterator<Player<String>> i = connections.iterator();
                while (i.hasNext()) {
                    Player<String> p = i.next();
                    if (uuid.equals(p.uuid())) {
                        i.remove();
                        break;
                    }
                }
                
                printConn(connections);
            }
        });
    }
    
	private static JsonPoint stringToJson(String str) {
        JsonPoint p = null;
        try {
            Gson gson = new GsonBuilder().create();
            p = gson.fromJson(str, JsonPoint.class);
        } catch (JsonSyntaxException e) {
            p = new JsonPoint();
            p.status(GameStatus.PARSE_STRING_ERROR);
            p.details(e.getMessage());
        }
        return p;
    }

    private static String jsonToString(JsonPoint obj) {
        String result = "{}";
        try {
            Gson gson = new GsonBuilder().create();
            result = gson.toJson(obj);
        } catch (JsonSyntaxException e) {
            result = "{x:0,y:0,status:9,details:\'" + e.getMessage() + "\'}";
        }
        return result;
    }

    private static void printConn(List<Player<String>> connections) {
        System.out.println("printConn: ");
        for (Player<String> p : connections) {
            System.out.println("player id: " + p.uuidAsString());            
        }
        System.out.println("");
    }

/*
    
    private static JsonMessage stringToJson(String str) {
        JsonMessage m = null;
        try {
            Gson gson = new GsonBuilder().create();
            m = gson.fromJson(str, JsonMessage.class);
        } catch (JsonSyntaxException e) {
            m = new JsonMessage(e.getMessage());
        }
        return m;
    }

    private static String jsonToString(JsonMessage obj) {
        String result = "{}";
        try {
            Gson gson = new GsonBuilder().create();
            result = gson.toJson(obj);
        } catch (JsonSyntaxException e) {
            result = "{message:\'" + e.getMessage() + "\'}";
        }
        return result;
    }*/
}