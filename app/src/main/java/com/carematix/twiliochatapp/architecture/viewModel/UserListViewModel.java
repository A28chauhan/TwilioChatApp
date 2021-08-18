package com.carematix.twiliochatapp.architecture.viewModel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.repository.UserListRepository;
import com.carematix.twiliochatapp.architecture.table.UserAllList;

import java.util.List;

public class UserListViewModel extends AndroidViewModel {
    private UserListRepository userListRepository;

    public UserListViewModel(Application application){
        super(application);
        userListRepository = new UserListRepository(application);
    }

    public void insert(UserAllList userChat){
        userListRepository.insert(userChat);
    }

    public void delete(){
        userListRepository.delete();
    }



    public LiveData<List<UserAllList>> getAllList(){
       return userListRepository.getAllData();
    }

    public LiveData<List<UserAllList>> getUserByID(String programUserId){
        return userListRepository.getUserByID(programUserId);
    }
}
