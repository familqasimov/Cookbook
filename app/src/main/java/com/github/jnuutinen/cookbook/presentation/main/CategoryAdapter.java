package com.github.jnuutinen.cookbook.presentation.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;

import java.util.List;
import java.util.Map;

public class CategoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> groups;
    private Map<String, List<String>> data;

    CategoryAdapter(Context context, List<String> groups, Map<String, List<String>> data) {
        this.context = context;
        this.groups = groups;
        this.data = data;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return data.get(groups.get(groupPosition)).size();
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {String groupString = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.item_category_group, null);
            }
        }
        TextView categoryText = null;
        if (convertView != null) {
            categoryText = convertView.findViewById(R.id.text_category_group);
        }
        if (categoryText != null) {
            categoryText.setText(groupString);
        }
        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final String childString = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.item_exp_recipe, null);
            }
        }
        TextView recipeText = null;
        if (convertView != null) {
            recipeText = convertView.findViewById(R.id.text_expanded_recipe);
        }
        if (recipeText != null) {
            recipeText.setText(childString);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
