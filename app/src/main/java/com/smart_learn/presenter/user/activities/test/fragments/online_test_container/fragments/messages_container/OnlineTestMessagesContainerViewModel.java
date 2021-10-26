package com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.messages_container;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.core.user.services.UserService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.user.firebase.firestore.entities.GroupChatMessageDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OnlineTestMessagesContainerViewModel extends BasicAndroidViewModel {

    private String testId;
    private final MutableLiveData<String> liveMessage;

    public OnlineTestMessagesContainerViewModel(@NonNull @NotNull Application application) {
        super(application);
        liveMessage = new MutableLiveData<>("");
        testId = "";
    }

    public void sendMessage(){
        final String message = liveMessage.getValue();
        if(message == null || message.isEmpty()){
            return;
        }

        // clear view field
        liveMessage.setValue("");

        GroupChatMessageDocument messageDocument = new GroupChatMessageDocument(
                new DocumentMetadata(UserService.getInstance().getUserUid(), System.currentTimeMillis(), new ArrayList<>()),
                message,
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserEmail(),
                UserService.getInstance().getUserPhotoUrl()
        );

        TestService.getInstance().sendOnlineTestMessage(testId, messageDocument);
    }

}
