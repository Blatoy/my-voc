package cifom_et.myvoc.logic;

import android.content.Context;

import cifom_et.myvoc.adapters.AdapterSelect;
import cifom_et.myvoc.data.api.logic.ApiManager;
import cifom_et.myvoc.data.api.objects.Category;

/**
 * Created by Loïck Jeanneret
 * (C) CIFOM-ET
 */


public abstract class LogicSelect {
    public static final String INTENT_EXTRA_CATEGORY_ID = "categoryId";

    /**
     * Check if there's categories, add them into the main layout
     *
     * @param adapterSelect The current select adapter
     */
    public static void refreshCategoryList(AdapterSelect adapterSelect) {
        adapterSelect.clearList();
        String searchText = adapterSelect.getSearchText();

        // Display categories
        for (int i = 0; i < ApiManager.categories.size(); i++) {
            Category cat = ApiManager.categories.get(i);
            if (cat.getName().toLowerCase().contains(searchText.toLowerCase()) || searchText.equals("")) {
                if (AdapterSelect.selectedLanguage1 == -1 || cat.getLanguageIdBase() == AdapterSelect.selectedLanguage1 || cat.getLanguageIdTranslation() == AdapterSelect.selectedLanguage1)
                    if (AdapterSelect.selectedLanguage2 == -1 || cat.getLanguageIdBase() == AdapterSelect.selectedLanguage2 || cat.getLanguageIdTranslation() == AdapterSelect.selectedLanguage2)
                        adapterSelect.addCategoryCard(cat.getName(), cat.getId(), cat.getLanguageBaseName(), cat.getLanguageTranslationName());
            }
        }

        // Hide or show the "No list" textview
        if (ApiManager.categories.size() + ApiManager.languages.size() == 0)
            adapterSelect.setNoListVisibility(true);
        else
            adapterSelect.setNoListVisibility(false);

        adapterSelect.setLoadingVisibility(false);
    }

    /**
     * Query the API, update the category and refresh the spinner when it's finished
     *
     * @param ctx           The current context
     * @param adapterSelect The current select adapter
     */
    public static void updateCategoriesAndLanguages(Context ctx, final AdapterSelect adapterSelect) {
        ApiManager.loadLanguageAndCategories(ctx, new Runnable() {
            @Override
            public void run() {
                refreshCategoryList(adapterSelect);
                adapterSelect.updateSpinners();
            }
        });
    }
}
