package controllers;

import models.TicGame;

import play.api.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;

import scala.App;

import java.util.UUID;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }
	
	public static Result wsJs() {
		return ok(views.js.ws.render());
	}
	
	public static WebSocket<String> wsInterface() {
		return new WebSocket<String>() {            
			public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
				UUID uuid = UUID.randomUUID();
				TicGame.start(in, out, uuid);
			}
    	};
	}
}