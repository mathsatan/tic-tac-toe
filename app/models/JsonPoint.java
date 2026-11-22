package models;

public class JsonPoint {
    private int x;
    private int y;
    private int status;
    private String details;

    public JsonPoint() {
        this(0, 0, 0, "");
    }
    public JsonPoint(int x, int y, int status, String details) {
        this.x = x;
        this.y = y;
        this.status = status;
        this.details = details;
    }

    public int x() {
        return this.x;
    }

    public void x(int x) {
        this.x = x;
    }

    public int y() {
        return this.y;
    }

    public void y(int y) {
        this.y = y;
    }

    public int status() {
        return status;
    }

    public void status(GameStatus status) {
        this.status = status.ordinal();
    }

    public String details() {
        return details;
    }

    public void details(String details) {
        this.details = details;
    }
}
