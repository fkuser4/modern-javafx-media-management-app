package com.mydigitalmedia.mediaapp.generics;

import com.mydigitalmedia.mediaapp.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SerializedPair<T, R> implements Serializable {

    private User changesMadeBy;
    private LocalDateTime changesMadeAt;
    private final T first;
    private final R second;

    public SerializedPair(T first, R second, User changesMadeBy) {
        this.first = first;
        this.second = second;
        this.changesMadeBy = changesMadeBy;
        this.changesMadeAt = LocalDateTime.now();
    }

    public T getFirst() {
        return first;
    }

    public R getSecond() {
        return second;
    }

    public User getChangesMadeBy() {
        return changesMadeBy;
    }

    public void setChangesMadeBy(User changesMadeBy) {
        this.changesMadeBy = changesMadeBy;
    }

    public LocalDateTime getChangesMadeAt() {
        return changesMadeAt;
    }

    public void setChangesMadeAt(LocalDateTime changesMadeAt) {
        this.changesMadeAt = changesMadeAt;
    }
}
