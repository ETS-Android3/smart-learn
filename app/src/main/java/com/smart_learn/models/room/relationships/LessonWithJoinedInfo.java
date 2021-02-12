package com.smart_learn.models.room.relationships;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.smart_learn.models.room.entities.Expression;
import com.smart_learn.models.room.entities.Lesson;
import com.smart_learn.models.room.entities.Sentence;
import com.smart_learn.models.room.entities.Word;

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
    @Relation(parentColumn = "lessonId", entityColumn = "fkLessonId")
    private List<Word> wordList;

    // one to-many relationship
    @Relation(parentColumn = "lessonId", entityColumn = "fkLessonId")
    private List<Sentence> sentenceList;

    // one to-many relationship
    @Relation(parentColumn = "lessonId", entityColumn = "fkLessonId")
    private List<Expression> expressionList;

}
