package com.smart_learn.data.entities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionMetadata {

    public interface Users {
        int NO_USER = 0;
        int GUEST = 1;
        int USER_LOGGED_IN = 2;
    }

    // Mark that question is for user or for guest. Is used to know from which DB to get data.
    private final int userType;

    @NonNull @NotNull
    private final ArrayList<QuestionIdentifier> identifiers;
    @NonNull @NotNull
    private final ArrayList<QuestionIdentifier> reversedIdentifiers;

    public QuestionMetadata(int userType, ArrayList<QuestionIdentifier> identifiers, ArrayList<QuestionIdentifier> reversedIdentifiers) {
        if(userType != Users.GUEST && userType != Users.USER_LOGGED_IN){
            this.userType = Users.NO_USER;
        }
        else{
            this.userType = userType;
        }

        this.identifiers = identifiers == null ? new ArrayList<>() : identifiers;
        this.reversedIdentifiers = reversedIdentifiers == null ? new ArrayList<>() : reversedIdentifiers;
    }

    public static QuestionMetadata generateEmptyObject(){
        return new QuestionMetadata(
                Users.NO_USER,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @NonNull @NotNull
    public ArrayList<QuestionIdentifier> getQuestionIdentifiers(boolean isReversed){
        if(isReversed){
            return reversedIdentifiers;
        }
        return identifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionMetadata)) return false;

        QuestionMetadata metadata = (QuestionMetadata) o;

        if (getUserType() != metadata.getUserType()) return false;
        if (!getIdentifiers().equals(metadata.getIdentifiers())) return false;
        return getReversedIdentifiers().equals(metadata.getReversedIdentifiers());
    }

    @Override
    public int hashCode() {
        int result = getUserType();
        result = 31 * result + getIdentifiers().hashCode();
        result = 31 * result + getReversedIdentifiers().hashCode();
        return result;
    }
}
