package org.wordpress.android.ui.themes;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.wordpress.android.R;
import org.wordpress.android.WordPress;

/**
 * A fragment for display the results of a theme search
 */
public class ThemeSearchFragment extends ThemeTabFragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {
    public static final String TAG = ThemeSearchFragment.class.getName();
    private static final String BUNDLE_LAST_SEARCH = "BUNDLE_LAST_SEARCH";

    public static ThemeSearchFragment newInstance() {
        ThemeSearchFragment fragment = new ThemeSearchFragment();

        Bundle args = new Bundle();
        args.putInt(ARGS_SORT, ThemeSortType.POPULAR.ordinal());
        fragment.setArguments(args);

        return fragment;
    }

    private String mLastSearch = "";
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        restoreState(savedInstanceState);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(BUNDLE_LAST_SEARCH)) {
                mLastSearch = savedInstanceState.getString(BUNDLE_LAST_SEARCH);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState(outState);
    }

    private void saveState(Bundle outState) {
        outState.putString(BUNDLE_LAST_SEARCH, mLastSearch);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.removeItem(R.id.menu_search);

        mSearchMenuItem = menu.findItem(R.id.menu_theme_search);
        mSearchMenuItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, this);

        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQuery(mLastSearch, true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        clearFocus(mSearchView);
    }

    private void clearFocus(View view) {
        if (view != null) {
            view.clearFocus();;
        }
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        if (item.getItemId() == R.id.menu_theme_search) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        getActivity().getFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        clearFocus(mSearchView);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        search(newText);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.theme_search, menu);
    }

    public void search(String searchTerm) {
        mLastSearch = searchTerm;
        if (mAdapter == null || WordPress.getCurrentBlog() == null) {
            return;
        }
        String blogId = String.valueOf(WordPress.getCurrentBlog().getRemoteBlogId());
        Cursor cursor = WordPress.wpDB.getThemes(blogId, searchTerm);

        mAdapter.changeCursor(cursor);
        mGridView.invalidateViews();

        if (cursor == null || cursor.getCount() == 0) {
            mNoResultText.setVisibility(View.VISIBLE);
        } else {
            mNoResultText.setVisibility(View.GONE);
        }
    }
}
