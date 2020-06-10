package com.mobile.chickenavailabilityapplication.datamodel;

import java.io.Serializable;

public class SecurityQuestion implements Serializable {
    public int QuestionId;
    public String Answer;
    public SecurityQuestion(int QuestionId, String Answer){
        this.QuestionId = QuestionId;
        this.Answer = Answer;
    }

    public SecurityQuestion(){

    }
}
