package com.smart_learn.data.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.PresenterHelpers;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Leave the ForeignKey declaration in @Entity. If you put declaration on field propriety inside
 * the class constraint will not work.
 * */
@Getter
@Setter
@ToString
@Entity(tableName = AppRoomDatabase.EXPRESSIONS_TABLE,
        foreignKeys = {
                        @ForeignKey(entity = Lesson.class,
                                parentColumns = "id",
                                childColumns = "fk_lesson_id",
                                onDelete = ForeignKey.CASCADE)
        })
public class Expression extends LessonEntrance implements PresenterHelpers.SelectionHelper, PresenterHelpers.DiffUtilCallbackHelper<Expression> {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int expressionId;

    @ColumnInfo(name = "expression")
    private String expression;

    public Expression(String notes, boolean isSelected, BasicInfo basicInfo, Integer fkLessonId,
                      boolean isFavourite, String language, ArrayList<Translation> translations, String expression) {
        super(notes, isSelected, basicInfo, fkLessonId, isFavourite, language, translations);
        this.expression = expression;
    }

    @Override
    public boolean areItemsTheSame(Expression newItem) {
        if(newItem == null){
            return false;
        }
        return this.expressionId == newItem.getExpressionId();
    }

    @Override
    public boolean areContentsTheSame(Expression newItem){
        if(newItem == null){
            return false;
        }
        return super.areContentsTheSame(newItem) &&
                CoreUtilities.General.areObjectsTheSame(this.expression, newItem.getExpression());
    }

    @Override
    public int getId() {
        return expressionId;
    }

    public static Expression generateEmptyObject(){
        return new Expression("", false, BasicInfo.generateEmptyObject(), null,
                false, "", new ArrayList<>(), "");
    }
}
