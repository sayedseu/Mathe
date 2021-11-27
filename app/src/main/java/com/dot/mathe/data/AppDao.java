package com.dot.mathe.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dot.mathe.model.Comment;
import com.dot.mathe.model.RowData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRowData(RowData rowData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertComment(Comment comment);

    @Query("SELECT * FROM RowData")
    Single<List<RowData>> retrieveAllRowData();

    @Query("SELECT * FROM Comment WHERE id = :id")
    Single<List<Comment>> retrieveCommentById(int id);

    @Query("SELECT * FROM Comment")
    Single<List<Comment>> retrieveAllComment();

    @Query("DELETE FROM Comment")
    Completable deleteAllComment();
}
