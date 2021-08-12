package com.smart_learn.data.entities;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Statistics {

    public interface Fields {
        String STATISTICS_FIELD_NAME = "statistics";
        String CORRECT_ANSWERS_FIELD_NAME = "correctAnswers";
        String TOTAL_EXTRACTIONS_FIELD_NAME = "totalExtractions";
        String TOTAL_TIME_FOR_CORRECT_ANSWERS_FIELD_NAME = "totalTimeForCorrectAnswers";
        String TOTAL_TIME_FOR_WRONG_ANSWERS_FIELD_NAME = "totalTimeForWrongAnswers";
        String SCORE_FIELD_NAME = "score";
    }

    private int correctAnswers = 0;
    private int totalExtractions = 0;
    private long totalTimeForCorrectAnswers = 0;
    private long totalTimeForWrongAnswers = 0;
    private double score = 0.0d;

    public Statistics() {
    }

    public void updateScore(boolean correctAnswer, long answerTime){
        if(correctAnswer){
            correctAnswers++;
            totalTimeForCorrectAnswers += answerTime;
        }
        else {
            totalTimeForWrongAnswers += answerTime;
        }
        totalExtractions++;
        score = totalExtractions == 0 ? 0 : ((double) correctAnswers / (double)totalExtractions) * 100;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(Statistics statistics){
        if(statistics == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(Fields.CORRECT_ANSWERS_FIELD_NAME, statistics.getCorrectAnswers());
        data.put(Fields.TOTAL_EXTRACTIONS_FIELD_NAME, statistics.getTotalExtractions());
        data.put(Fields.TOTAL_TIME_FOR_CORRECT_ANSWERS_FIELD_NAME, statistics.getTotalTimeForCorrectAnswers());
        data.put(Fields.TOTAL_TIME_FOR_WRONG_ANSWERS_FIELD_NAME, statistics.getTotalTimeForWrongAnswers());
        data.put(Fields.SCORE_FIELD_NAME, statistics.getScore());

        return data;
    }

    public Statistics makeDeepCopy(){
        Statistics tmp = new Statistics();
        tmp.setCorrectAnswers(correctAnswers);
        tmp.setTotalExtractions(totalExtractions);
        tmp.setTotalTimeForCorrectAnswers(totalTimeForCorrectAnswers);
        tmp.setTotalTimeForWrongAnswers(totalTimeForWrongAnswers);
        tmp.setScore(score);

        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistics)) return false;

        Statistics that = (Statistics) o;

        if (getCorrectAnswers() != that.getCorrectAnswers()) return false;
        if (getTotalExtractions() != that.getTotalExtractions()) return false;
        if (getTotalTimeForCorrectAnswers() != that.getTotalTimeForCorrectAnswers()) return false;
        if (getTotalTimeForWrongAnswers() != that.getTotalTimeForWrongAnswers()) return false;
        return Double.compare(that.getScore(), getScore()) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getCorrectAnswers();
        result = 31 * result + getTotalExtractions();
        result = 31 * result + (int) (getTotalTimeForCorrectAnswers() ^ (getTotalTimeForCorrectAnswers() >>> 32));
        result = 31 * result + (int) (getTotalTimeForWrongAnswers() ^ (getTotalTimeForWrongAnswers() >>> 32));
        temp = Double.doubleToLongBits(getScore());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
