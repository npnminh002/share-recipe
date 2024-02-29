package doan.npnm.sharerecipe.interfaces;

public interface DataEventListener<T>{
    void onSuccess(T data);
    void onErr(Object err);
}
