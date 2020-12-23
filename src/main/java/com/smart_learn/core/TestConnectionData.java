package com.smart_learn.core;

import com.smart_learn.models.Participant;
import com.smart_learn.models.Response;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Scope("prototype")
@Getter
@Setter
public class TestConnectionData {

    private String testCode;
    private JSONArray testQuestions;
    private int maxParticipants;
    private final List<Participant> participants = new ArrayList<>();

    private boolean testCanceled = false;
    private AtomicBoolean stopTest = new AtomicBoolean(false);

    private final HashMap<String,List<Response>> responses = new HashMap<>();


}
