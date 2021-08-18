package com.carematix.twiliochatapp.listener;

public interface TaskCompletionListener<T, U> {

  void onSuccess(T t);

  void onError(U u);
}
