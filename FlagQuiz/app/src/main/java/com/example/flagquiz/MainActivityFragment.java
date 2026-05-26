package com.example.flagquiz;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivityFragment extends Fragment {
    private static final String TAG = "FlagQuiz";
    private static final int FLAGS_IN_QUIZ = 10;

    private TextView questionNumberTextView;
    private ImageView flagImageView;
    private TextView answerTextView;
    private LinearLayout[] guessLinearLayouts;

    private List<String> fileNameList;
    private List<String> quizCountriesList;
    private String correctAnswer;
    private int correctAnswers;
    private int totalGuesses;
    private int guessRows = 2;
    private SecureRandom random;
    private Handler handler;
    private Animation shakeAnimation;

    public MainActivityFragment() {
        random = new SecureRandom();
        handler = new Handler();
        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        flagImageView = view.findViewById(R.id.flagImageView);
        answerTextView = view.findViewById(R.id.answerTextView);

        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);

        for (int row = 0; row < 4; row++) {
            if (row < guessRows) {
                guessLinearLayouts[row].setVisibility(View.VISIBLE);
            } else {
                guessLinearLayouts[row].setVisibility(View.GONE);
            }
        }

        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        // Загружаем список всех флагов из assets
        loadAllFlags();

        return view;
    }

    private void loadAllFlags() {
        AssetManager am = getActivity().getAssets();
        String[] regions = {"Africa", "Asia", "Europe", "North_America", "Oceania", "South_America"};

        for (String region : regions) {
            try {
                String[] files = am.list(region);
                if (files != null) {
                    for (String file : files) {
                        if (file.endsWith(".png")) {
                            String flagName = region + "-" + file.replace(".png", "");
                            fileNameList.add(flagName);
                            Log.d(TAG, "Found flag: " + flagName);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error loading flags from " + region, e);
            }
        }

        if (fileNameList.isEmpty()) {
            Log.e(TAG, "No flags found! Using test data.");
            // Если флагов нет, используем тестовые данные
            String[] testCountries = {"Egypt", "France", "Japan", "USA", "Germany", "China", "Brazil", "India", "Italy", "Spain"};
            for (String country : testCountries) {
                fileNameList.add(country);
            }
        }

        resetQuiz();
    }

    public void resetQuiz() {
        Log.d(TAG, "resetQuiz called, total flags: " + fileNameList.size());

        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();

        // Перемешиваем все флаги
        Collections.shuffle(fileNameList);

        // Берём первые FLAGS_IN_QUIZ флагов
        int count = Math.min(FLAGS_IN_QUIZ, fileNameList.size());
        for (int i = 0; i < count; i++) {
            quizCountriesList.add(fileNameList.get(i));
        }

        Log.d(TAG, "Quiz countries: " + quizCountriesList.size());

        if (!quizCountriesList.isEmpty()) {
            loadNextFlag();
        }
    }

    private void loadNextFlag() {
        if (quizCountriesList.isEmpty()) {
            double percent = (double) correctAnswers / totalGuesses * 100;
            answerTextView.setText(String.format("Quiz complete!\nScore: %d/%d (%.0f%%)",
                    correctAnswers, FLAGS_IN_QUIZ, percent));
            return;
        }

        correctAnswer = quizCountriesList.remove(0);
        answerTextView.setText("");

        questionNumberTextView.setText("Question " + (correctAnswers + 1) + " of " + FLAGS_IN_QUIZ);

        // Загружаем изображение флага
        loadFlagImage(correctAnswer);

        // Генерируем варианты ответов
        List<String> options = new ArrayList<>();
        options.add(getCountryName(correctAnswer));

        for (int i = 0; i < 3; i++) {
            String randomCountry;
            do {
                String randomFlag = fileNameList.get(random.nextInt(fileNameList.size()));
                randomCountry = getCountryName(randomFlag);
            } while (options.contains(randomCountry));
            options.add(randomCountry);
        }

        Collections.shuffle(options);

        // Создаём кнопки
        for (int row = 0; row < guessRows; row++) {
            LinearLayout rowLayout = guessLinearLayouts[row];
            rowLayout.removeAllViews();

            for (int col = 0; col < 2; col++) {
                int index = row * 2 + col;
                if (index >= options.size()) break;

                Button btn = new Button(getActivity());
                btn.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                btn.setText(options.get(index));
                btn.setTextSize(14);

                final String selectedAnswer = options.get(index);

                btn.setOnClickListener(v -> {
                    totalGuesses++;
                    Button clickedBtn = (Button) v;

                    if (selectedAnswer.equals(getCountryName(correctAnswer))) {
                        answerTextView.setText("✓ Correct! " + getCountryName(correctAnswer));
                        answerTextView.setTextColor(getResources().getColor(R.color.correct_answer));
                        disableButtons();
                        correctAnswers++;

                        if (correctAnswers == FLAGS_IN_QUIZ) {
                            double percent = (double) correctAnswers / totalGuesses * 100;
                            answerTextView.setText(String.format("Quiz complete!\nScore: %d/%d (%.0f%%)",
                                    correctAnswers, FLAGS_IN_QUIZ, percent));
                        } else {
                            handler.postDelayed(this::loadNextFlag, 1500);
                        }
                    } else {
                        answerTextView.setText("✗ Incorrect! Правильный ответ: " + getCountryName(correctAnswer));
                        answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer));
                        clickedBtn.setEnabled(false);
                        clickedBtn.setAlpha(0.5f);
                        flagImageView.startAnimation(shakeAnimation);
                    }
                });

                rowLayout.addView(btn);
            }
        }
    }

    private void loadFlagImage(String flagName) {
        String region = flagName.substring(0, flagName.indexOf('-'));
        String imageName = flagName.substring(flagName.indexOf('-') + 1) + ".png";

        AssetManager am = getActivity().getAssets();

        try (InputStream stream = am.open(region + "/" + imageName)) {
            Drawable flag = Drawable.createFromStream(stream, flagName);
            flagImageView.setImageDrawable(flag);
        } catch (IOException e) {
            Log.e(TAG, "Error loading flag: " + flagName, e);
            flagImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private String getCountryName(String flagName) {
        int dashIndex = flagName.indexOf('-');
        if (dashIndex != -1) {
            return flagName.substring(dashIndex + 1).replace('_', ' ');
        }
        return flagName;
    }

    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            for (int i = 0; i < guessLinearLayouts[row].getChildCount(); i++) {
                guessLinearLayouts[row].getChildAt(i).setEnabled(false);
            }
        }
    }
}