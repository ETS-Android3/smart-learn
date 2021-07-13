package com.smart_learn.core.services;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.data.repository.GuestExpressionRepository;
import com.smart_learn.data.room.entities.Expression;

public class GuestExpressionService extends BasicRoomService<Expression> {

    private final GuestExpressionRepository guestExpressionRepository;

    public GuestExpressionService(Application application){
        guestExpressionRepository = new GuestExpressionRepository(application);

        // set super repository
        super.basicRoomRepository = guestExpressionRepository;
    }

    public LiveData<Integer> getLiveNumberOfExpressions(){
        return guestExpressionRepository.getLiveNumberOfExpressions();
    }
}
