package com.github.jnuutinen.cookbook.presentation.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.Utils;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AllRecipesFragment extends Fragment {
    //private static final String TAG = AllRecipesFragment.class.getSimpleName();

    private OnRecipeSelectedListener mListener;
    private List<Recipe> recipes;
    private List<CombineDao.combinedRecipe> combinedRecipes;
    private TextView noRecipesText;
    private ListView recipeList;

    public AllRecipesFragment() {
    }

    public static AllRecipesFragment newInstance() {
        return new AllRecipesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        noRecipesText = view.findViewById(R.id.text_no_recipes);
        recipeList = view.findViewById(R.id.list_recipes);
        recipeList.setOnItemClickListener((parent, view1, position, id) -> {
            if (mListener != null) {
                CombineDao.combinedRecipe combined = combinedRecipes.get(position);
                if (recipes != null) {
                    Recipe foundRecipe = null;
                    for (Recipe recipe : recipes) {
                        if (recipe.getName().equals(combined.recipeName)) {
                            foundRecipe = recipe;
                            break;
                        }
                    }
                    if (foundRecipe != null) {
                        mListener.onRecipeSelected(foundRecipe);
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeSelectedListener) {
            mListener = (OnRecipeSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        observe();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void observe() {
        MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, data -> recipes = data);
        viewModel.getCombinedRecipes().observe(this, data -> {
            if (data == null || data.size() == 0) {
                noRecipesText.setVisibility(VISIBLE);
            } else {
                noRecipesText.setVisibility(GONE);
            }
            data = Utils.sortCombinedRecipesByName(data);
            combinedRecipes = data;
            recipeList.setAdapter(new RecipeAdapter(getContext(), data));
        });
    }

    public interface OnRecipeSelectedListener {
        void onRecipeSelected(Recipe recipe);
    }
}
