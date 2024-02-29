package doan.npnm.sharerecipe.interfaces;

public interface FetchByID <T>{
    void onSuccess(T data);
    void onErr(Object err);
}
