package com.carematix.twiliochatapp.fetchchannel.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.carematix.twiliochatapp.fetchchannel.data.FetchChannelDataSource;
import com.carematix.twiliochatapp.fetchchannel.repository.FetchChannelRepository;

public class FetchChannelViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FetchChannelViewModel.class)) {
            return (T) new FetchChannelViewModel(FetchChannelRepository.getInstance(new FetchChannelDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
