// Name:- Athif Ahamed Mohamed Shaffy
// Student Id:- A00256229

package com.athif.diceroller;

import java.util.Objects;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String DIE_OPTIONS = "DIE_OPTIONS";
    private static final String HISTORY_ROLL_RESULTS = "HISTORY_ROLL_RESULTS";
    private static final int TRUE_10_SIDED_DIE_TYPE = 10;

    int minValue = 1, maxValue = 1;

    CheckBox checkBoxSpecialRolls;
    Button btnRollOnce;
    Button btnRollTwice;
    Button btnAddNewDie;
    EditText inputNumOfSides;
    TextView txtRollResult;
    TextView txtHistory;
    Spinner spinnerDie;

    List<Integer> historyRollResults = new ArrayList();
    List<Integer> spinnerDieOptions = new ArrayList();
    ArrayAdapter<CharSequence> spinnerDieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRollResult = findViewById(R.id.txtRollResult);
        spinnerDie = findViewById(R.id.spinnerDie);
        checkBoxSpecialRolls = findViewById(R.id.checkBoxSpecialRolls);
        btnRollOnce = findViewById(R.id.btnRollOnce);
        btnRollTwice = findViewById(R.id.btnRollTwice);
        inputNumOfSides = findViewById(R.id.inputNumOfSides);
        btnAddNewDie = findViewById(R.id.btnAddNewDie);
        txtHistory = findViewById(R.id.txtHistory);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        // Get value from SharedPreferences if exist
        String dieOptions = pref.getString(DIE_OPTIONS, "");
        // If SharedPreferences values exists
        if (!dieOptions.equalsIgnoreCase("")) {
            // Convert literal string to string list of integer
            List<String> options = Arrays.asList(dieOptions.split("\\s*,\\s*"));

            // Convert string list to integer list
            for (String option : options) {
                spinnerDieOptions.add(parseInt(option));
            }
        } else {
            // Initialize SharedPreferences if empty with spinnerDieOptions
            int[] dieOptionArray = getResources().getIntArray(R.array.die_options);

            // Convert dieOptionArray int array to list
            for (int option : dieOptionArray) {
                spinnerDieOptions.add(option);
            }
            // Save to SharedPreferences for faster loading time later
            saveToSharedPrefs(spinnerDieOptions, DIE_OPTIONS);
        }

        // get history rolls from shared pref if exist
        String historyResults = pref.getString(HISTORY_ROLL_RESULTS, "");
        // If exist
        if (!historyResults.equalsIgnoreCase("")) {
            // Convert literal string to string list of integer
            List<String> results = Arrays.asList(historyResults.split("\\s*,\\s*"));

            // Convert string list to integer list
            for (String result : results) {
                historyRollResults.add(parseInt(result));
            }
            // Show stored history roll results
            String historyResultsToShow = buildHistoryRollResults(historyRollResults, getResources());
            txtHistory.setText(historyResultsToShow);
        }

        initCheckBoxSpecialRolls();
        initSpinner();
        initButtonsRoll();
        initInputNumOfSides();
        initButtonAddNewDie();
    }

    private String buildHistoryRollResults(List<Integer> rollResults, Resources resources) {
        String listStr = rollResults.toString();
        return resources.getString(
                R.string.history,
                listStr.substring(1, listStr.length() - 1)  // Array remove start & end square brackets []
        );
    }

    private void initSpinner() {
        List<String> options = new ArrayList();
        for (int option : spinnerDieOptions) {
            options.add(String.valueOf(option));
        }

        spinnerDieAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        spinnerDie.setAdapter(spinnerDieAdapter);

        minValue = 1;
        maxValue = spinnerDieOptions.get(0);

        spinnerDie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                minValue = 1;
                maxValue = spinnerDieOptions.get(i);

                if (maxValue == TRUE_10_SIDED_DIE_TYPE) {
                    checkBoxSpecialRolls.setVisibility(View.VISIBLE);
                } else {
                    checkBoxSpecialRolls.setVisibility(View.GONE);
                    checkBoxSpecialRolls.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void initCheckBoxSpecialRolls() {
        checkBoxSpecialRolls.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                minValue = 0;
                maxValue -= 1;
            } else {
                minValue = 1;
                maxValue += 1;
            }
        });
    }

    private void initButtonsRoll() {
        btnRollOnce.setOnClickListener(view -> {
            int randomVal = generateRandomNumber(minValue, maxValue);
            historyRollResults.add(randomVal);

            String rollResult = getResources()
                    .getQuantityString(R.plurals.roll_results, 1, randomVal);
            txtRollResult.setText(rollResult);

            String historyResults = buildHistoryRollResults(historyRollResults, getResources());
            txtHistory.setText(historyResults);

            saveToSharedPrefs(historyRollResults, HISTORY_ROLL_RESULTS);
        });

        btnRollTwice.setOnClickListener(view -> {
            int randomVal1 = generateRandomNumber(minValue, maxValue);
            int randomVal2 = generateRandomNumber(minValue, maxValue);
            historyRollResults.add(randomVal1);
            historyRollResults.add(randomVal2);

            String rollResult = getResources()
                    .getQuantityString(R.plurals.roll_results, 2, randomVal1, randomVal2);
            txtRollResult.setText(rollResult);

            String historyResults = buildHistoryRollResults(historyRollResults, getResources());
            txtHistory.setText(historyResults);

            saveToSharedPrefs(historyRollResults, HISTORY_ROLL_RESULTS);
        });
    }

    private void initInputNumOfSides() {
        inputNumOfSides.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("0")) {
                    // 0 sided die is not allowed
                    inputNumOfSides.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        inputNumOfSides.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                btnAddNewDie.callOnClick();
                return true;
            }
            return false;
        });
    }

    private void initButtonAddNewDie() {
        btnAddNewDie.setOnClickListener(view -> {
            // Close keyboard
            hideSoftKeyboard((Activity) view.getContext(), true);

            String dieTypeText = inputNumOfSides.getText().toString();
            int dieTypeInt = parseInt(dieTypeText);

            if (!spinnerDieOptions.contains(dieTypeInt)) {
                // Add new item to spinner
                spinnerDieAdapter.insert(dieTypeText,spinnerDieAdapter.getCount());
                spinnerDieAdapter.notifyDataSetChanged();

                spinnerDie.setSelection(spinnerDieAdapter.getCount() - 1);

                // Add same new item to options list
                spinnerDieOptions.add(dieTypeInt);

                // Save to SharedPreferences
                saveToSharedPrefs(spinnerDieOptions, DIE_OPTIONS);
            } else {
                int duplicatedPos = spinnerDieOptions.indexOf(dieTypeInt);
                spinnerDie.setSelection(duplicatedPos);
            }

            // Reset edit text
            inputNumOfSides.setText("");
        });
    }

    private int generateRandomNumber(int min, int max) {
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private void hideSoftKeyboard(Activity activity, boolean clearCurrentFocus) {
        Objects.requireNonNull(activity, "Cannot Hide Soft Keyboard with NULL Activity/Context!");
        // Check if no view has focused.
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (clearCurrentFocus) {
                view.clearFocus();
            }
        }
    }

    private int parseInt(String str) {
        try{
            return Integer.valueOf(str);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        return -1;
    }

    // Utility function to save shared pref
    private void saveToSharedPrefs(List<Integer> list, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        String listStr = list.toString();
        listStr = listStr.substring(1, listStr.length() - 1);

        editor.putString(key, listStr);
        editor.apply();
    }
}