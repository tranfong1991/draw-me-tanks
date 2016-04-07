package core.db;

public class DatabaseError extends Error {
    public DatabaseError(String msg) {
        super("DATABASE ERROR: " + msg);
    }
}
