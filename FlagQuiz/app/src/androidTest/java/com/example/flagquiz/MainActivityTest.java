package com.example.flagquiz;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testActivityIsDisplayed() {
        // Проверяем, что activity отображается
        onView(withId(R.id.fragmentContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void testQuestionNumberIsDisplayed() {
        // Проверяем, что TextView с номером вопроса отображается
        onView(withId(R.id.questionNumberTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void testFlagImageViewIsDisplayed() {
        // Проверяем, что ImageView с флагом отображается
        onView(withId(R.id.flagImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void testAnswerTextViewIsDisplayed() {
        // Проверяем, что TextView с ответом отображается
        onView(withId(R.id.answerTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void testButtonsAreDisplayed() {
        // Проверяем, что кнопки отображаются (хотя бы одна)
        onView(withId(R.id.row1LinearLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testFirstQuestionText() {
        // Проверяем, что текст вопроса содержит "Question 1 of 10"
        onView(withId(R.id.questionNumberTextView))
                .check(matches(withText("Question 1 of 10")));
    }
}