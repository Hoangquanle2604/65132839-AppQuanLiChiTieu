package lhq.cntt2.quanlychitieu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private UserRepository repository = new UserRepository();
    private MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<UserModel> getUserLiveData() { return userLiveData; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchUser(String uid) {
        repository.getUserData(uid, new UserRepository.UserDataCallback() {
            @Override
            public void onSuccess(UserModel userModel) { userLiveData.setValue(userModel); }
            @Override
            public void onFailure(Exception e) { errorMessage.setValue(e.getMessage()); }
        });
    }
}