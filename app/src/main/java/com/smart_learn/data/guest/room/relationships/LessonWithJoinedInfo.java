package com.smart_learn.data.guest.room.relationships;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.data.guest.room.entitites.Word;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains all info`s about a Lesson including objects relationship data.
 * */
@Getter
@Setter
public class LessonWithJoinedInfo {

    @Embedded
    private Lesson lesson;

    // one to-many relationship
    @Relation(parentColumn = "id", entityColumn = "fk_lesson_id")
    private List<Word> wordList;

    // one to-many relationship
    @Relation(parentColumn = "id", entityColumn = "fk_lesson_id")
    private List<Expression> expressionList;

}
