package famm.fammous.connection;

public interface Callback<T> {
    void onResponse(T t);
}