package com.smart_learn.presenter.activities.ui.test;

import lombok.Getter;

@Getter
public class ParticipantModel {

    private final boolean isTestAdmin;
    private final String participantId;
    private final int viewType;

    public ParticipantModel(boolean isTestAdmin, String participantId, int viewType) {
        this.isTestAdmin = isTestAdmin;
        this.participantId = participantId;
        this.viewType = viewType;
    }
}
