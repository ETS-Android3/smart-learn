package com.smart_learn.data.entities;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionIdentifier {

    public interface Identifiers {
        int NO_IDENTIFIER = 0;
        int WORD = 1;
        int EXPRESSION = 2;
    }

    public static final int NO_IDENTIFIER_ID_INTEGER = -1;

    // Mark that question belong to an word or expression.
    private final int type;

    // Used to identify word/expression in DB.
    @NonNull @NotNull
    private final String id;

    // Translations id's can be long because are not stored directly in DB. GSON is used to
    // convert and store translations as a string array in every word/expression. Also a question
    // can have multiple translations so record all id's.
    @NonNull @NotNull
    private final HashSet<Long> translationsIds;

    public QuestionIdentifier(int type, String id, HashSet<Long> translationsIds) {
        if(type != Identifiers.WORD && type != Identifiers.EXPRESSION){
            this.type = Identifiers.NO_IDENTIFIER;
        }
        else{
            this.type = type;
        }

        this.id = id == null ? "" : id;
        this.translationsIds = translationsIds == null ? new HashSet<>() : translationsIds;
    }

    public static QuestionIdentifier generateEmptyObject(){
        return new QuestionIdentifier(
                Identifiers.NO_IDENTIFIER,
                "",
                new HashSet<>()
        );
    }

    public int getIdInteger(){
        try {
            return Integer.parseInt(id);
        }
        catch (NumberFormatException ex){
            return NO_IDENTIFIER_ID_INTEGER;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionIdentifier)) return false;

        QuestionIdentifier that = (QuestionIdentifier) o;

        if (getType() != that.getType()) return false;
        if (!getId().equals(that.getId())) return false;
        return getTranslationsIds().equals(that.getTranslationsIds());
    }

    @Override
    public int hashCode() {
        int result = getType();
        result = 31 * result + getId().hashCode();
        result = 31 * result + getTranslationsIds().hashCode();
        return result;
    }
}
