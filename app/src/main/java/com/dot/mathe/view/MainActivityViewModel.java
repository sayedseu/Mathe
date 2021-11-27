package com.dot.mathe.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dot.mathe.data.AppDao;
import com.dot.mathe.data.Resource;
import com.dot.mathe.model.Comment;
import com.dot.mathe.model.RowData;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivityViewModel extends ViewModel {

    private final AppDao appDao;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<Resource<List<RowData>>> rowDataLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Resource<Comment>> commentLiveData = new MediatorLiveData<>();

    public MainActivityViewModel(AppDao appDao) {
        this.appDao = appDao;
    }

    protected LiveData<Resource<List<RowData>>> observeRowData() {
        return rowDataLiveData;
    }

    protected LiveData<Resource<Comment>> observeComment() {
        return commentLiveData;
    }

    protected void insertRowData(RowData rowData) {
        appDao.insertRowData(rowData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    protected void retrieveAllRowData() {
        rowDataLiveData.setValue(Resource.<List<RowData>>loading(null));
        appDao.retrieveAllRowData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<RowData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<RowData> rowData) {
                        rowDataLiveData.setValue(Resource.success(rowData));
                    }

                    @Override
                    public void onError(Throwable e) {
                        rowDataLiveData.setValue(Resource.<List<RowData>>error("", null));
                    }
                });
    }

    protected void insertComment(Comment comment) {
        appDao.insertComment(comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    protected void deleteAllComment() {
        appDao.deleteAllComment()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    protected void retrieveComment(int id) {
        commentLiveData.setValue(Resource.<Comment>loading(null));
        appDao.retrieveCommentById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Comment>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<Comment> comments) {
                        if (comments.size() > 0) {
                            commentLiveData.setValue(Resource.success(comments.get(0)));
                        } else {
                            commentLiveData.setValue(Resource.success(new Comment(1, "")));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        commentLiveData.setValue(Resource.<Comment>error("", null));
                    }
                });
    }

    @Override
    protected void onCleared() {
        disposable.clear();
    }
}
