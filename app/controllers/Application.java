package controllers;

import models.TicGame;
import org.apache.pekko.stream.Materializer;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;

import javax.inject.Inject;
import java.util.UUID;

public class Application extends Controller {

    private final Materializer materializer;

    @Inject
    public Application(Materializer materializer) {
        this.materializer = materializer;
    }

    public Result index() {
        return ok(index.render());
    }

    public Result wsJs(play.mvc.Http.Request request) {
    	return ok(views.js.ws.render(request));
    }

    public WebSocket wsInterface() {
        return WebSocket.Text.accept(request -> {
            UUID uuid = UUID.randomUUID();
            return TicGame.start(uuid, materializer);
        });
    }
}
