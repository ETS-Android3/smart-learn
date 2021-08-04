package com.smart_learn.data.firebase.firestore.entities;

import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDocument extends BasicProfileDocument {

    public interface Fields {
        String RECEIVED_REQUESTS_FIELD_NAME = "receivedRequest";
        String PENDING_FRIENDS_FIELD_NAME = "pendingFriends";
        String FRIENDS_FIELD_NAME = "friends";
        String NR_OF_UNREAD_NOTIFICATIONS_FIELD_NAME = "nrOfUnreadNotifications";
        String NR_OF_LESSONS_FIELD_NAME = "nrOfLessons";
        String NR_OF_WORDS_FIELD_NAME = "nrOfWords";
        String NR_OF_EXPRESSIONS_FIELD_NAME = "nrOfExpressions";
        String NR_OF_ONLINE_IN_PROGRESS_TESTS_FIELD_NAME = "nrOfOnlineInProgressTests";
        String NR_OF_ONLINE_FINISHED_TESTS_FIELD_NAME = "nrOfOnlineFinishedTests";
        String NR_OF_LOCAL_UNSCHEDULED_IN_PROGRESS_TESTS_FIELD_NAME = "nrOfLocalUnscheduledInProgressTests";
        String NR_OF_LOCAL_UNSCHEDULED_FINISHED_TESTS_FIELD_NAME = "nrOfLocalUnscheduledFinishedTests";
        String NR_OF_LOCAL_SCHEDULED_TESTS_FIELD_NAME = "nrOfLocalScheduledTests";
        String TOTAL_SUCCESS_RATE_FIELD_NAME = "totalSuccessRate";
    }

    private ArrayList<String> receivedRequest;
    private ArrayList<String> pendingFriends;
    private ArrayList<String> friends;

    // counters
    private long nrOfUnreadNotifications;
    private long nrOfLessons;
    private long nrOfWords;
    private long nrOfExpressions;

    private long nrOfOnlineInProgressTests;
    private long nrOfOnlineFinishedTests;
    private long nrOfLocalUnscheduledInProgressTests;
    private long nrOfLocalUnscheduledFinishedTests;
    private long nrOfLocalScheduledTests;
    private double totalSuccessRate;

    public UserDocument() {
        // needed for Firestore
    }

    public UserDocument(DocumentMetadata documentMetadata, String email, String displayName, String profilePhotoUrl,
                        ArrayList<String> receivedRequest, ArrayList<String> pendingFriends, ArrayList<String> friends) {
        super(documentMetadata, email, displayName, profilePhotoUrl);
        this.receivedRequest = receivedRequest;
        this.pendingFriends = pendingFriends;
        this.friends = friends;
    }
}