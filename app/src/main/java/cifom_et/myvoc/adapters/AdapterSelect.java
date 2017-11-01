package cifom_et.myvoc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import cifom_et.myvoc.R;
import cifom_et.myvoc.activities.ActivityQuiz;
import cifom_et.myvoc.data.api.logic.ApiManager;
import cifom_et.myvoc.data.settings.SettingsHelper;
import cifom_et.myvoc.data.settings.SettingsStructure;
import cifom_et.myvoc.logic.LogicQuiz;
import cifom_et.myvoc.logic.LogicSelect;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */


public class AdapterSelect {
    public static int selectedLanguage1;
    public static int selectedLanguage2;
    private Context ctx;
    private Spinner searchSpinner1;
    private Spinner searchSpinner2;
    private TextView searchText;
    private AdapterSelect adapterSelect;
    private TableLayout wordListContainer;

    // Handle current item
    private AdapterView.OnItemSelectedListener itemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // Check if "no language is selected"
            if (position == 0) {
                // Get the right spinner
                if (parent.getId() == R.id.select_search_language_1)
                    selectedLanguage1 = -1;
                else
                    selectedLanguage2 = -1;
            } else {
                // Check if the language list is empty, prevent OOB exception
                if (ApiManager.languages.size() == 0) {
                    if (parent.getId() == R.id.select_search_language_1)
                        selectedLanguage1 = 0;
                    else
                        selectedLanguage2 = 0;
                } else {
                    if (parent.getId() == R.id.select_search_language_1)
                        selectedLanguage1 = ApiManager.languages.get(position - 1).getId();
                    else
                        selectedLanguage2 = ApiManager.languages.get(position - 1).getId();
                }
            }

            // Filter the list
            LogicSelect.refreshCategoryList(adapterSelect);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * @param ctx -
     */
    public AdapterSelect(Context ctx) {
        this.ctx = ctx;
        this.adapterSelect = this;

        // Set default value to spinner
        searchSpinner1 = ((Spinner) ((Activity) ctx).findViewById(R.id.select_search_language_1));
        searchSpinner2 = ((Spinner) ((Activity) ctx).findViewById(R.id.select_search_language_2));
        searchText = ((TextView) ((Activity) ctx).findViewById(R.id.select_search_category));
        wordListContainer = (TableLayout) ((Activity) ctx).findViewById(R.id.select_category_list);
        // Create the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item);
        // add the empty element
        adapter.add(ctx.getResources().getString(R.string.spinner_empty));
        searchSpinner1.setAdapter(adapter);
        searchSpinner2.setAdapter(adapter);
    }

    /**
     * @return The text from the search TextView
     */
    public String getSearchText() {
        return searchText.getText().toString();
    }

    /**
     * Update the content of the spinners
     */
    public void updateSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item);
        // Add default value to the adapter
        adapter.add(ctx.getResources().getString(R.string.select_spinner_all));

        // Add languages into the adapter
        for (int i = 0; i < ApiManager.languages.size(); i++) {
            adapter.add(ApiManager.languages.get(i).getName());
        }

        // Use the same adapter for ever spinner
        searchSpinner1.setAdapter(adapter);
        searchSpinner2.setAdapter(adapter);

        // Retrieve the default value from settings
        searchSpinner1.setSelection(SettingsHelper.getInt(ctx, SettingsStructure.SEARCH_BASE_LANGUAGE_ID) + 1);
        searchSpinner2.setSelection(SettingsHelper.getInt(ctx, SettingsStructure.SEARCH_TRANSLATION_LANGUAGE_ID) + 1);

        // Set item click listener
        searchSpinner1.setOnItemSelectedListener(itemListener);
        searchSpinner2.setOnItemSelectedListener(itemListener);
    }

    /**
     * Call the update when the text is changed
     *
     * @param adapterSelect The adapter for the current activity
     */
    public void addChangeListener(final AdapterSelect adapterSelect) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Refresh list
                LogicSelect.refreshCategoryList(adapterSelect);
            }
        };
        searchText.addTextChangedListener(watcher);
    }

    /**
     * Add a list into TableLayout
     *
     * @param categoryName        The name of the category
     * @param categoryId          The id of the category
     * @param baseLanguage        The name of the base language
     * @param translationLanguage The name of the translation language
     */
    public void addCategoryCard(String categoryName, final int categoryId, final String baseLanguage, final String translationLanguage) {
        // Create the elements
        CardView cardContainer = new CardView(ctx);
        TextView textView = new TextView(ctx);
        Space space = new Space(ctx);
        int padding = (int) ctx.getResources().getDimension(R.dimen.card_margin);

        // Set on click action
        cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if need to ask quiz way
                if (SettingsHelper.getInt(ctx, SettingsStructure.DEFAULT_QUIZ_ORDER) == SettingsStructure.QUIZ_ORDER_ASK) {
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1);

                    // Add the 2 languages to the list
                    arrayAdapter.add(String.format(ctx.getString(R.string.select_order), baseLanguage, translationLanguage));
                    arrayAdapter.add(String.format(ctx.getString(R.string.select_order), translationLanguage, baseLanguage));

                    // Display the choose dialog
                    new AlertDialog.Builder(ctx)
                            .setTitle(ctx.getString(R.string.select_list_order))
                            .setNegativeButton(ctx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Set order
                                    LogicQuiz.reverseOrder = which == 1;
                                    // Give to the intent the category ID
                                    Intent intent = new Intent(ctx, ActivityQuiz.class).putExtra(LogicSelect.INTENT_EXTRA_CATEGORY_ID, categoryId);
                                    ctx.startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    // Set quiz order from settings
                    LogicQuiz.reverseOrder = SettingsHelper.getInt(ctx, SettingsStructure.DEFAULT_QUIZ_ORDER) == SettingsStructure.QUIZ_ORDER_TRANSLATION_BASE;
                    // Give to the intent the category ID
                    Intent intent = new Intent(ctx, ActivityQuiz.class).putExtra(LogicSelect.INTENT_EXTRA_CATEGORY_ID, categoryId);
                    ctx.startActivity(intent);
                }
            }
        });


        // Set heigth, width, marginLeft and marginRight
        TableLayout.LayoutParams cardParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        cardParams.setMargins((int) ctx.getResources().getDimension(R.dimen.card_margin), 0, (int) ctx.getResources().getDimension(R.dimen.card_margin), 0);

        // Assign the params to the card
        cardContainer.setLayoutParams(cardParams);

        // Parameter textview
        textView.setText(categoryName);
        textView.setTextColor(ctx.getResources().getColor(R.color.black));
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimension(R.dimen.select_card_text_size));
        textView.setPadding(padding, padding, padding, padding);

        // Set space height
        space.setMinimumHeight((int) ctx.getResources().getDimension(R.dimen.select_space_height));

        // Add views into other views
        cardContainer.addView(textView);
        wordListContainer.addView(cardContainer);
        wordListContainer.addView(space);
    }

    /**
     * Set the visibility for the loading
     *
     * @param visible True if visible
     */
    public void setLoadingVisibility(boolean visible) {
        if (visible)
            ((Activity) ctx).findViewById(R.id.select_loading).setVisibility(View.VISIBLE);
        else
            ((Activity) ctx).findViewById(R.id.select_loading).setVisibility(View.GONE);
    }

    /**
     * Set the visibility for the "no list" textview
     *
     * @param visible True if visible
     */
    public void setNoListVisibility(boolean visible) {
        // ((Activity)ctx).findViewById(R.id.selectNoList).setVisibility(View.VISIBLE);
        if (visible)
            ((Activity) ctx).findViewById(R.id.select_no_list).setVisibility(View.VISIBLE);
        else
            ((Activity) ctx).findViewById(R.id.select_no_list).setVisibility(View.GONE);
    }

    /**
     * Remove all list from the main layout
     */
    // Remove element from the layout
    public void clearList() {
        wordListContainer.removeAllViews();
    }
}
