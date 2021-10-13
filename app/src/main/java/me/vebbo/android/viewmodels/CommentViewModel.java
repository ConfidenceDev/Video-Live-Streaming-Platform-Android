package me.vebbo.android.viewmodels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.util.List;

import me.vebbo.android.interfaces.CommentCheck;
import me.vebbo.android.repositories.CommentRepository;

public class CommentViewModel extends ViewModel implements CommentCheck {

    private CommentRepository commentRepository;
    private MutableLiveData<List<JSONObject>> listMutableLiveData = new MutableLiveData<>();

    public void setCommentViewModel(AppCompatActivity context){
        commentRepository = new CommentRepository(context, this);
        commentRepository.loadComment();
    }

    public LiveData<List<JSONObject>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    @Override
    public void onTask(List<JSONObject> commentModelList) {
        listMutableLiveData.setValue(commentModelList);
    }

    @Override
    public void onError(Exception e) {

    }
}
