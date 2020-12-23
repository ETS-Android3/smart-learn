package com.smart_learn.activities.ui.test;

import lombok.Getter;

@Getter
public class ParticipantModel {

    private final String participantId;
    private final int viewType;

    public ParticipantModel(String participantId, int viewType) {
        this.participantId = participantId;
        this.viewType = viewType;
    }
}
