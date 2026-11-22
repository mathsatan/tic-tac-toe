package models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.pekko.Done;
import org.apache.pekko.NotUsed;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.stream.OverflowStrategy;
import org.apache.pekko.stream.javadsl.Flow;
import org.apache.pekko.stream.javadsl.Sink;
import org.apache.pekko.stream.javadsl.Source;
import org.apache.pekko.stream.javadsl.SourceQueueWithComplete;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;

public class TicGame {

    private static final List<Player<String>> connections = new CopyOnWriteArrayList<>();

    public static Flow<String, String, NotUsed> start(UUID uuid, Materializer materializer) {
        // 1. Создаем исходящий канал (замена WebSocket.Out)
        Source<String, SourceQueueWithComplete<String>> source =
                Source.queue(256, OverflowStrategy.dropHead());

        var pair = source.preMaterialize(materializer);
        SourceQueueWithComplete<String> out = pair.first();
        Source<String, NotUsed> outSource = pair.second();

        connections.add(new Player<>(out, uuid));
        printConn(connections);

        // Состояние сессии для конкретного игрока (как раньше внутри Callback)
        final ServiceInjector injector = new FieldWebInjector();
        final FieldWeb[] appHolder = new FieldWeb[1]; // массив из 1 элемента, чтобы менять внутри лямбды

        // 2. Обработка входящих сообщений (замена in.onMessage)
        Sink<String, CompletionStage<Done>> inSink = Sink.foreach(event -> {
            JsonPoint p = stringToJson(event);

            if (p.status() == GameStatus.PARSE_JSON_ERROR.ordinal()) {
                out.offer(jsonToString(p));
                return;
            }

            JsonPoint res = new JsonPoint();
            switch (p.status()) {
                case 0:
                    if (appHolder[0] != null) {
                        // app already run
                    }
                    appHolder[0] = injector.getField(p.x(), p.y());
                    res = appHolder[0].startGame();
                    break;
                case 3:
                    if (appHolder[0] == null) {
                        // app not started yet
                    }
                    res = appHolder[0].nextMove(p.x(), p.y());
                    break;
                /*case 11:
                    res = appHolder[0].stopGame();
                    out.offer("{x:0,y:0,status:5,details:'Game stopped by user'}");
                    out.complete();
                    return;*/
                default:
                    res.status(GameStatus.UNKNOWN_STATUS);
                    out.offer(jsonToString(res));
                    return;
            }

            // res.status(GameStatus.GAME_OVER);
            out.offer(jsonToString(res));
        });

        // 3. Обработка закрытия соединения (замена in.onClose)
        Sink<String, NotUsed> inSinkWithClose = inSink.mapMaterializedValue(done -> {
            done.whenComplete((res, ex) -> {
                connections.removeIf(p -> uuid.equals(p.uuid()));
                printConn(connections);
            });
            return NotUsed.getInstance();
        });

        // Объединяем прием и отправку в один Flow
        return Flow.fromSinkAndSource(inSinkWithClose, outSource);
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
            result = "{x:0,y:0,status:9,details:'" + e.getMessage() + "'}";
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