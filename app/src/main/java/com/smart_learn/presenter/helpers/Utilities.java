package com.smart_learn.presenter.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.data.room.entities.helpers.IndexRange;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * Main utilities class.
 *
 * https://projectlombok.org/features/experimental/UtilityClass
 * https://stackoverflow.com/questions/25223553/how-can-i-create-an-utility-class
 * */
public final class Utilities {

    /** Use a private constructor in order to avoid instantiation. */
    private Utilities(){
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    /** All utilities related to activities or fragments. */
    public static final class Activities {

        /**
         * Use to set navigation graph.
         *
         * @param activity Activity where is the navigation fragment.
         * @param fragmentId Navigation fragment (graph) resource id.
         *
         * @return NavController object which can be used to navigate if navigation graph was set, or null otherwise.
         * */
        public static NavController setNavigationGraph(@NonNull FragmentActivity activity, @IdRes int fragmentId){
            NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(fragmentId);
            if(navHostFragment == null){
                return null;
            }
            return navHostFragment.getNavController();
        }


        /**
         * Use to set navigation graph with a bottom menu.
         *
         * @param activity Activity where is the navigation fragment.
         * @param fragmentId Navigation fragment (graph) resource id.
         * @param bottomNavigationView Bottom navigation menu to be set.
         * @param selectedListener Selected listener which will set options for navigation between
         *                        fragments or null if is no need for listener.
         * @param destinationChangedListener Destination listener which will set options for
         *                                   navigation when graph destinations are reached, or null
         *                                   if is no need for listener.
         *
         * @return NavController object which can be used to navigate if navigation graph was set
         * and the bottom menu attached, or null otherwise.
         * */
        public static NavController setNavigationGraphWithBottomMenu(@NonNull FragmentActivity activity, @IdRes int fragmentId,
                                                                     @NonNull BottomNavigationView bottomNavigationView,
                                                                     @Nullable BottomNavigationView.OnNavigationItemSelectedListener selectedListener,
                                                                     @Nullable NavController.OnDestinationChangedListener destinationChangedListener){
            // set navigation graph
            NavHostFragment navHostFragment = (NavHostFragment) activity.getSupportFragmentManager().findFragmentById(fragmentId);
            if(navHostFragment == null){
                return null;
            }

            NavController navController = navHostFragment.getNavController();
            if(destinationChangedListener != null){
                navController.addOnDestinationChangedListener(destinationChangedListener);
            }

            // here navigation graph was set ==> add bottom menu to the navigation graph
            NavigationUI.setupWithNavController(bottomNavigationView, navController);

            if(selectedListener != null){
                bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
            }

            // disable reselect
            bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
                    // Nothing here to disable reselect
                }
            });

            return navController;
        }


        /**
         * Used for setting the activity action bar title.
         *
         * @param activity The activity that contains the action bar.
         * @param title New title to be set.
         * */
        public static void resetToolbarTitle(@NonNull AppCompatActivity activity, @Nullable String title){
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null){
                actionBar.setTitle(title);
            }
        }


        /**
         * Used for setting the action mode title.
         *
         * @param actionMode The action mode for which title must be set.
         * @param title New title to be set.
         * */
        public static void setActionModeTitle(@Nullable ActionMode actionMode, @Nullable String title){
            if(actionMode != null){
                actionMode.setTitle(title);
            }
        }


        /**
         * Used to set persistence bottom sheet which will appear when action mode is started.
         *
         * By default persistence bottom sheet is hidden.
         *
         * @param sheetLayout The bottom sheet layout which will be used.
         *
         * @return BottomSheetBehavior<LinearLayoutCompat> object if sheet was set, or null otherwise.
         * */
        public static BottomSheetBehavior<LinearLayoutCompat> setPersistentBottomSheet(@NonNull LinearLayoutCompat sheetLayout){

            BottomSheetBehavior<LinearLayoutCompat> sheet = BottomSheetBehavior.from(sheetLayout);
            sheet.setHideable(true);
            sheet.setState(BottomSheetBehavior.STATE_HIDDEN);

            sheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@androidx.annotation.NonNull View bottomSheet, int newState) {
                    // use this to disable STATE_DRAGGING option
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        sheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });

            return sheet;
        }


        /**
         * Used to initialize recycler view from fragments.
         *
         * @param context The context for which the recycler view must be set.
         * @param recyclerView The recycler view to be set.
         * @param padding The ItemDecoration padding.
         * */
        public static void initializeRecyclerView(@NonNull Context context, @NonNull RecyclerView recyclerView, int padding){

            LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.VERTICAL,false);
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new ItemDecoration(padding));

            // FIXME: on API 26 (Samsung galaxy S7 Edge) this is not working, but on API 30 is working
            // this allows the cards to fit under the rounded corners of the layout
            // https://stackoverflow.com/questions/5574212/android-view-clipping
            // https://stackoverflow.com/questions/16161448/how-to-make-layout-with-rounded-corners/30692236#30692236
            //recyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            recyclerView.setClipToOutline(true);
        }


        /**
         * Used to initialize recycler view from fragments.
         *
         * @param context The context for which the recycler view must be set.
         * @param recyclerView The recycler view to be set.
         * @param itemDecoration ItemDecoration to be set to the recycler view.
         * @param adapter Adapter which extends RecyclerView.ViewHolder in order to be set to the
         *                recycler view.
         * @param onScrollListener Listener to handle scroll action on the recycler view.
         * */
        public static void initializeRecyclerView(@NonNull @NotNull Context context,
                                                  @NonNull @NotNull RecyclerView recyclerView,
                                                  @Nullable ItemDecoration itemDecoration,
                                                  @Nullable RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter,
                                                  @Nullable RecyclerView.OnScrollListener onScrollListener){

            LinearLayoutManager manager = new LinearLayoutManager(context, RecyclerView.VERTICAL,false);
            recyclerView.setLayoutManager(manager);
            if(itemDecoration != null){
                recyclerView.addItemDecoration(itemDecoration);
            }

            // FIXME: on API 26 (Samsung galaxy S7 Edge) this is not working, but on API 30 is working
            // this allows the cards to fit under the rounded corners of the layout
            // https://stackoverflow.com/questions/5574212/android-view-clipping
            // https://stackoverflow.com/questions/16161448/how-to-make-layout-with-rounded-corners/30692236#30692236
            //recyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            recyclerView.setClipToOutline(true);

            if(adapter != null){
                recyclerView.setAdapter(adapter);
            }

            if(onScrollListener != null){
                recyclerView.addOnScrollListener(onScrollListener);
            }
        }


        /**
         * Used to set a SwipeRefreshLayout which is parent for a recycler view.
         *
         * @param swipeRefreshLayout The layout which must be set.
         * @param enabled If top pull for refresh will be enabled.
         * @param initialRefreshStatus If refresh icon will be shown by default when activity/fragment
         *                             starts.
         * @param listener Listener to manage actions when refresh is done. This must be set only if
         *                 enabled is true, otherwise will have no effect.
         * */
        public static void setSwipeRefreshLayout(@NonNull @NotNull SwipeRefreshLayout swipeRefreshLayout,
                                                 boolean enabled, boolean initialRefreshStatus,
                                                 @Nullable SwipeRefreshLayout.OnRefreshListener listener){
            // https://stackoverflow.com/questions/44454797/pull-to-refresh-recyclerview-android

            // https://stackoverflow.com/questions/30301451/how-to-disable-pull-to-refresh-action-and-use-only-indicator
            // top pull for refresh
            swipeRefreshLayout.setEnabled(enabled);

            // if refresh icon will be shown by default when activity/fragment starts
            swipeRefreshLayout.setRefreshing(initialRefreshStatus);

            // color scheme for refresh icon
            swipeRefreshLayout.setColorSchemeResources(R.color.colorARefreshIcon, R.color.colorBRefreshIcon,
                    R.color.colorCRefreshIcon, R.color.colorDRefreshIcon);

            // If pull for refresh is enabled and no listener exist a default one will be set, in order
            // to show a refresh effect.
            if(enabled && listener == null){
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                return;
            }

            // setting listener will have effect only if enabled was true
            if(listener != null){
                swipeRefreshLayout.setOnRefreshListener(listener);
            }
        }


        /**
         * Set search on the Activity ActionBar and define what to do when search is made.
         *
         * @param activity Activity where menu is inflated.
         * @param menu The menu where the search item is located.
         * @param menuId Search item id.
         * @param menuGroup The menu items group which will be hidden when search is expanded.
         * @param searchActionCallback Callback that will manage search.
         * */
        public static void setSearchMenuItem(@NonNull FragmentActivity activity, @NonNull Menu menu, @IdRes int menuId,
                                             @IdRes int menuGroup, @NonNull Callbacks.SearchActionCallback searchActionCallback){
            MenuItem searchItem = menu.findItem(menuId);
            if(searchItem == null){
                Timber.w("searchItem is null ==> search is not functionally");
                return;
            }

            // use this to show/hide a specific menu group
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    menu.setGroupVisible(menuGroup, false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    menu.setGroupVisible(menuGroup, true);

                    // Use this to reload menu items after visible was set to true.
                    // https://stackoverflow.com/questions/10692755/how-do-i-hide-a-menu-item-in-the-actionbar
                    activity.invalidateOptionsMenu();

                    return true;
                }
            });

            setSearchView(menu, menuId, searchActionCallback);
        }


        /**
         * Set search on the Activity ActionBar and define what to do when search is made.
         *
         * @param menu The menu where the search item is located.
         * @param menuId Search item id.
         * @param searchActionCallback Callback that will manage search.
         * */
        public static void setSearchMenuItem(@NonNull Menu menu, @IdRes int menuId,
                                             @NonNull Callbacks.SearchActionCallback searchActionCallback){
            setSearchView(menu, menuId, searchActionCallback);
        }

        /**
         * Helper for setSearchMenuItem(...)
         * */
        private static void setSearchView(@NonNull Menu menu, @IdRes int menuId, @NonNull Callbacks.SearchActionCallback searchActionCallback){
            MenuItem searchItem = menu.findItem(menuId);
            if(searchItem == null){
                Timber.w("searchItem is null ==> search is not functionally");
                return;
            }

            SearchView searchView = (SearchView) searchItem.getActionView();
            if(searchView == null){
                Timber.w("searchView is null ==> search is not functionally");
                return;
            }

            // use this in order to set full with for the expanded search view
            // https://stackoverflow.com/questions/18063103/searchview-in-optionsmenu-not-full-width
            //searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    searchActionCallback.onQueryTextChange(newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
            });

        }


        /**
         * Used to show (state STATE_EXPANDED will be set) the persistent bottom sheet when action mode
         * is started.
         *
         * Also in order to achieve a nice view effect, the recycler view will be resized in order to
         * fit between the ActionBar and sheetLayout.
         *
         * @param isBottomNav If RecyclerView is attached on a Fragment/Activity where bottom
         *                     navigation is active.
         * @param button The floating button for add action (this will be hidden in action mode).
         * @param mainLayout The main layout which is shown when bottom sheet is HIDDEN.
         * @param sheetLayout The layout for bottom sheet.
         * @param sheetBehaviour The behaviour for which the state BottomSheetBehavior.STATE_EXPANDED will be set.
         * */
        public static void showPersistentBottomSheet(boolean isBottomNav, @Nullable FloatingActionButton button,
                                                     @NonNull CoordinatorLayout mainLayout, @NonNull LinearLayoutCompat sheetLayout,
                                                     @NonNull BottomSheetBehavior<LinearLayoutCompat> sheetBehaviour){
            if(button != null){
                // floating button will be hidden in action mode
                button.hide();
            }

            // resize recycler view layout only if bottom navigation does not exist
            if(!isBottomNav){
                // https://stackoverflow.com/questions/6798867/android-how-to-programmatically-set-the-size-of-a-layout/6798938#6798938
                // https://stackoverflow.com/questions/10310550/set-height-of-imageview-as-matchparent-programmatically/10310612#10310612
                int mainLayoutHeight = mainLayout.getHeight();
                int sheetHeight = sheetLayout.getHeight();
                // resize only if is a positive value
                if(mainLayoutHeight - sheetHeight > 0){
                    mainLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, mainLayoutHeight - sheetHeight));
                }
            }

            // in order to be visible, sheet will be expanded
            sheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        }


        /**
         * Used to hide (state STATE_HIDDEN will be set) the persistent bottom sheet when action mode
         * is stopped.
         *
         * Also in order to achieve a nice view effect, the recycler view will be resized in order to
         * cover the sheetLayout space (which will be hidden).
         *
         * @param isBottomNav If RecyclerView is attached on a Fragment/Activity where bottom
         *                     navigation is active.
         * @param button The floating button for add action (this will be shown when action mode is stopped).
         * @param mainLayout The main layout which is shown when bottom sheet is HIDDEN.
         * @param sheetBehaviour The behaviour for which the state BottomSheetBehavior.STATE_HIDDEN will be set.
         * */
        public static void hidePersistentBottomSheet(boolean isBottomNav, @Nullable FloatingActionButton button,
                                                     @NonNull CoordinatorLayout mainLayout,
                                                     @NonNull BottomSheetBehavior<LinearLayoutCompat> sheetBehaviour){
            // in order to disappear, sheet will be expanded
            sheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

            // resize recycler view layout only if bottom navigation does not exist
            if(!isBottomNav){
                // https://stackoverflow.com/questions/6798867/android-how-to-programmatically-set-the-size-of-a-layout/6798938#6798938
                // https://stackoverflow.com/questions/10310550/set-height-of-imageview-as-matchparent-programmatically/10310612#10310612
                mainLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
            }

            if(button != null){
                // floating button will be shown when action mode is stopped
                button.show();
            }

        }


        /**
         * Use this function in order to add predefined animations and options for NavigationGraph
         * fragments when the NavigationGraph has a  bottom navigation view  attached, and this is
         * VISIBLE on fragments.
         *
         * @param destinationId Where should navigation go when back button is pressed. For fragments
         *                      where bottom navigation view is VISIBLE, on back pressed,
         *                      the navigation must go to the previous destination where bottom
         *                      navigation view is NOT VISIBLE.
         *
         * @return NavOptions object which will have the predefined options.
         * */
        public static NavOptions getVisibleBottomMenuNavOptions(@IdRes int destinationId) {
            // https://stackoverflow.com/questions/61541455/animation-for-bottomnavigation-fragments-with-architecture-navigation-components/65979864#65979864
            // https://betterprogramming.pub/everything-about-android-jetpacks-navigation-component-b550017c7354
            return new NavOptions.Builder()
                    .setEnterAnim(R.anim.anim_slide_in_right)
                    .setExitAnim(R.anim.anim_slide_out_left)
                    .setPopEnterAnim(R.anim.popup_enter)
                    .setPopExitAnim(R.anim.popup_exit)
                    // This is used whether this navigation action should launch as single-top
                    // (i.e., there will be at most one copy of a given destination on the top of the back stack)
                    .setLaunchSingleTop(true)
                    // This is important because when a bottom menu is used back button will go to previous
                    // main destination. If you do not use this back press will give previous destination
                    // (this can be a fragment from bottom menu also)
                    //
                    // Obs:
                    //      Leave 'inclusive' parameter value to 'false'. If you put 'true', then
                    //      'startDestination' will be deleted from back stack and at back press
                    //      previous destination can NOT longer be reached.
                    .setPopUpTo(destinationId, false)
                    .build();
        }

        /**
         * Use this function in order to set only where go back press should go.
         *
         * @param destinationId Where should navigation go when back button is pressed.
         *
         * @return NavOptions object which will have the predefined options.
         * */
        public static NavOptions getBottomMenuNavOptionsForOnBackPress(@IdRes int destinationId) {
            // https://stackoverflow.com/questions/61541455/animation-for-bottomnavigation-fragments-with-architecture-navigation-components/65979864#65979864
            // https://betterprogramming.pub/everything-about-android-jetpacks-navigation-component-b550017c7354
            return new NavOptions.Builder()
                    // This is used whether this navigation action should launch as single-top
                    // (i.e., there will be at most one copy of a given destination on the top of the back stack)
                    .setLaunchSingleTop(true)
                    // This is important because when a bottom menu is used back button will go to previous
                    // main destination. If you do not use this back press will give previous destination
                    // (this can be a fragment from bottom menu also)
                    //
                    // Obs:
                    //      Leave 'inclusive' parameter value to 'false'. If you put 'true', then
                    //      'startDestination' will be deleted from back stack and at back press
                    //      previous destination can NOT longer be reached.
                    .setPopUpTo(destinationId, false)
                    .build();
        }

        /**
         * Use this function in order to add predefined animations and options for NavigationGraph
         * fragments when the NavigationGraph has a  bottom navigation view  attached, and this is
         * VISIBLE on fragments.
         *
         * @return NavOptions object which will have the predefined options.
         * */
        public static NavOptions getVisibleBottomMenuNavOptions() {
            // https://stackoverflow.com/questions/61541455/animation-for-bottomnavigation-fragments-with-architecture-navigation-components/65979864#65979864
            // https://betterprogramming.pub/everything-about-android-jetpacks-navigation-component-b550017c7354
            return new NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_close_enter)
                    .setExitAnim(R.anim.fragment_close_exit)
                    .setPopEnterAnim(R.anim.popup_enter)
                    .setPopExitAnim(R.anim.popup_exit)
                    // This is used whether this navigation action should launch as single-top
                    // (i.e., there will be at most one copy of a given destination on the top of the back stack)
                    .setLaunchSingleTop(true)
                    .build();
        }


        /**
         * Use this function in order to add predefined animations and options for NavigationGraph
         * fragments when the NavigationGraph has a bottom navigation view attached.
         *
         * Also this function must be used only if navigation is made from a fragment which have the
         * bottom navigation view HIDDEN to a fragment which will have the bottom navigation
         * view VISIBLE.
         *
         * @param destinationId Fragment id from the NavigationGraph which which will have the bottom
         *                      navigation view VISIBLE.
         *
         * @return NavOptions object which will have the predefined animations.
         * */
        public static NavOptions getEnterBottomMenuNavOptions(@IdRes int destinationId) {
            // https://stackoverflow.com/questions/61541455/animation-for-bottomnavigation-fragments-with-architecture-navigation-components/65979864#65979864
            // https://betterprogramming.pub/everything-about-android-jetpacks-navigation-component-b550017c7354
            return new NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_close_enter)
                    .setExitAnim(R.anim.fragment_close_exit)
                    .setPopEnterAnim(R.anim.popup_enter)
                    .setPopExitAnim(R.anim.popup_exit)
                    // This are set only for actions from fragment which have the bottom navigation
                    // view HIDDEN to a fragment which will have the bottom navigation view VISIBLE,
                    // so leave 'single. Because of that check this to see why this options are like
                    // that (see the explanations and example).
                    // https://betterprogramming.pub/everything-about-android-jetpacks-navigation-component-b550017c7354
                    .setLaunchSingleTop(true)
                    .setPopUpTo(destinationId, true)
                    .build();
        }


        /**
         * Use this function in order to add predefined animations and options for NavigationGraph
         * fragments when the NavigationGraph has a bottom navigation view attached.
         *
         * Also this function must be used only if navigation is made from a fragment which have the
         * bottom navigation view VISIBLE to a fragment which will have the bottom navigation
         * view HIDDEN.
         *
         * @param destinationId Fragment id from the NavigationGraph which which will have the bottom
         *                      navigation view HIDDEN.
         *
         * @return NavOptions object which will have the predefined animations.
         * */
        public static NavOptions getExitBottomMenuNavOptions(@IdRes int destinationId) {
            // at this moment is ok to have same options
            return getEnterBottomMenuNavOptions(destinationId);
        }


        /**
         * Use this to hide/show a text view.
         *
         * @param value true if textView must be visible, false otherwise.
         * @param textView TextView object which will be visible/hidden.
         * */
        public static void changeTextViewStatus(boolean value, @NonNull TextView textView){
            if(value){
                textView.setVisibility(View.VISIBLE);
            }
            else{
                textView.setVisibility(View.GONE);
            }
        }


        /**
         * Use this to show R.drawable.ic_baseline_checked_circle_24 / R.drawable.ic_baseline_unchecked_circle_24
         * on selectAll button.
         *
         * @param value true if R.drawable.ic_baseline_checked_circle_24 must be set,
         *             false if R.drawable.ic_baseline_unchecked_circle_24 must be set.
         * @param button Button object which will have new icon selected.
         * */
        @SuppressLint("UseCompatLoadingForDrawables")
        public static void changeSelectAllButtonStatus(boolean value, @NonNull Button button){
            // https://stackoverflow.com/questions/11376516/change-drawable-color-programmatically/11376610#11376610
            Drawable top;
            Drawable unwrappedDrawable;
            if(value){
                unwrappedDrawable = ApplicationController.getInstance().getDrawable(R.drawable.ic_baseline_checked_circle_24);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, ApplicationController.getInstance().getColor(R.color.tint_checked_icon_action_mode));
            }
            else{
                unwrappedDrawable = ApplicationController.getInstance().getDrawable(R.drawable.ic_baseline_unchecked_circle_24);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, ApplicationController.getInstance().getColor(R.color.tint_unchecked_icon_action_mode));
            }
            top = unwrappedDrawable;
            button.setCompoundDrawablesWithIntrinsicBounds(null, top , null, null);
        }


        /**
         * Use this function in order to set CustomEditableLayout used when a value must be shown as
         * disabled and if necessary this must be shown as enabled (see LessonHomeFragment,
         * LessonHomeWord ...  for details).
         *
         * In editable mode:
         *      - Toolbar menu group R.id.group_edit_menu_options_custom_editable_layout will become
         *        HIDDEN and R.id.group_update_menu_options_custom_editable_layout will
         *        become VISIBLE.
         *      - EditText will be VISIBLE and label will be GONE.
         *
         * In non-editable mode:
         *      - Toolbar menu group R.id.group_edit_menu_options_custom_editable_layout will become
         *        VISIBLE and R.id.group_update_menu_options_custom_editable_layout will
         *         become HIDDEN.
         *      - EditText will be GONE and label will be VISIBLE.
         *
         * @param toolbar Toolbar where R.menu.menu_options_custom_editable_layout is inflated.
         * @param textInputLayout The TextInputLayout which will contain the EditText.
         * @param textView TextView object which will represent label.
         * @param callback Callback which will manage actions.
         * */
        public static void setCustomEditableLayout(@Nullable Toolbar toolbar, @Nullable TextInputLayout textInputLayout,
                                                   @Nullable TextView textView, @NonNull Callbacks.CustomEditableLayoutCallback callback){
            if(toolbar == null || textInputLayout == null || textView == null){
                return;
            }

            // by default layout is disabled
            disableCustomEditableLayout(toolbar,textInputLayout,textView);

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id){
                        case R.id.action_edit_menu_options_custom_editable_layout:
                            callback.savePreviousValue();
                            enableCustomEditableLayout(toolbar,textInputLayout,textView);
                            return true;
                        case R.id.action_cancel_menu_options_custom_editable_layout:
                            callback.revertToPreviousValue();
                            disableCustomEditableLayout(toolbar,textInputLayout,textView);
                            // reset field error
                            textInputLayout.setError(null);
                            return true;
                        case R.id.action_update_menu_options_custom_editable_layout:
                            if(callback.isCurrentValueOk()){
                                callback.saveCurrentValue();
                                disableCustomEditableLayout(toolbar,textInputLayout,textView);
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            });
        }


        /**
         * Use this function in order to enable CustomEditableLayout used when a value
         * must be shown as disabled and if necessary this must be shown as enabled
         * (see LessonHomeFragment, LessonHomeWord ...  for details).
         *
         * @param toolbar Toolbar where R.menu.menu_options_custom_editable_layout is inflated.
         *                R.id.group_edit_menu_options_custom_editable_layout will
         *                become HIDDEN and R.id.group_update_menu_options_custom_editable_layout
         *                will become VISIBLE.
         * @param textInputLayout The TextInputLayout which will become VISIBLE.
         * @param textView TextView object which will become GONE.
         * */
        private static void enableCustomEditableLayout(@NonNull Toolbar toolbar, @NonNull TextInputLayout textInputLayout,
                                                       @NonNull TextView textView){
            toolbar.getMenu().setGroupVisible(R.id.group_edit_menu_options_custom_editable_layout, false);
            toolbar.getMenu().setGroupVisible(R.id.group_update_menu_options_custom_editable_layout, true);
            textView.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.VISIBLE);
        }


        /**
         * Use this function in order to disable CustomEditableLayout used when a value
         * must be shown as disabled and if necessary this must be shown as enabled
         * (see LessonHomeFragment, LessonHomeWord ...  for details).
         *
         * @param toolbar Toolbar where R.menu.menu_options_custom_editable_layout is inflated.
         *                R.id.group_edit_menu_options_custom_editable_layout will
         *                become VISIBLE and R.id.group_update_menu_options_custom_editable_layout
         *                will become HIDDEN.
         * @param textInputLayout The TextInputLayout which will become GONE.
         * @param textView TextView object which will become VISIBLE.
         * */
        private static void disableCustomEditableLayout(@NonNull Toolbar toolbar, @NonNull TextInputLayout textInputLayout,
                                                        @NonNull TextView textView){
            toolbar.getMenu().setGroupVisible(R.id.group_edit_menu_options_custom_editable_layout, true);
            toolbar.getMenu().setGroupVisible(R.id.group_update_menu_options_custom_editable_layout, false);
            textInputLayout.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }


        /**
         * TODO: enhance this function
         * Use this in order to mark Spanned text
         * */
        @Deprecated
        public static Spanned createSpannedText(List<IndexRange> indexRangeList, String text){

            if(indexRangeList != null && !indexRangeList.isEmpty()){

                int idxStart = indexRangeList.get(0).getStart();
                int idxEnd = indexRangeList.get(0).getEnd();

                text = text.subSequence(0,idxStart).toString() +
                        "<span style=\"background-color:yellow\">" +
                        text.subSequence(idxStart,idxEnd).toString() +
                        "</span>" +
                        text.subSequence(idxEnd,text.length()).toString();
            }

            return Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY);
        }


        /**
         * Use to generate a spanned string based on index pairs.
         *
         * @param indexList Pair of indexes with format (startIndex, finalIndex) with startIndex
         *                  inclusive and finalIndex exclusive.
         * @param value String to be transformed in spanned string.
         *
         * @return SpannableString object created based on value and indexes.
         * */
        public static SpannableString generateSpannedString(List<Pair<Integer, Integer>> indexList, String value){
            // https://developer.android.com/guide/topics/text/spans
            SpannableString spannableString = new SpannableString(value);
            if(indexList == null || indexList.isEmpty()){
                return spannableString;
            }

            for(Pair<Integer, Integer> pair : indexList){
                spannableString.setSpan(new ForegroundColorSpan(ApplicationController.getInstance().getColor(R.color.colorAccent)),
                        pair.first, pair.second,
                        // pair.first is an inclusive index and because pair.second is exclusive index
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            return spannableString;
        }

        /**
         * Use to set a string as spanned.
         *
         * @param value String to be transformed in spanned string.
         *
         * @return SpannableString object created.
         * */
        public static SpannableString setStringAsSpanned(String value){
            // https://developer.android.com/guide/topics/text/spans
            SpannableString spannableString = new SpannableString(value);
            spannableString.setSpan(new ForegroundColorSpan(ApplicationController.getInstance().getColor(R.color.colorAccent)),
                    0, value.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        }


        /**
         * Use this in order to show a simple AlertDialog with callback on positive button pressed.
         *
         * @param context The context where the AlertDialog will be shown.
         * @param title   Title of the AlertDialog.
         * @param message Description of the AlertDialog. This can be a warning message, like
         *                'Are you sure you want to .... ?'.
         * @param standardAlertDialogCallback Callback which will manage the positive button press
         *                                    action.
         * */
        @Deprecated
        public static void showStandardAlertDialog(@NonNull Context context, @NonNull String title,
                                                   @NonNull String message, @NonNull Callbacks.StandardAlertDialogCallback standardAlertDialogCallback){
            //https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android/2115770#2115770
            new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                standardAlertDialogCallback.onPositiveButtonPress();
                            }
                        })
                        // No need for a listener because no action will be done when BUTTON_NEGATIVE is pressed.
                        // Dialog will be dismissed automatically.
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .show();
        }

        /**
         * Use this in order to show a simple AlertDialog with callback on positive button pressed.
         *
         * @param context The context where the AlertDialog will be shown.
         * @param title   Title of the AlertDialog.
         * @param message Description of the AlertDialog. This can be a warning message, like
         *                'Are you sure you want to .... ?'.
         * @param positiveButtonDescription Description for the positive button.
         * @param standardAlertDialogCallback Callback which will manage the positive button press
         *                                    action.
         * */
        public static void showStandardAlertDialog(@NonNull @NotNull Context context, @NonNull @NotNull String title,
                                                   @NonNull @NotNull String message, @NonNull @NotNull String positiveButtonDescription,
                                                   @NonNull Callbacks.StandardAlertDialogCallback standardAlertDialogCallback){
            //https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android/2115770#2115770
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(positiveButtonDescription, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            standardAlertDialogCallback.onPositiveButtonPress();
                        }
                    })
                    // No need for a listener because no action will be done when BUTTON_NEGATIVE is pressed.
                    // Dialog will be dismissed automatically.
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .show();
        }

        /**
         * Use this in order to show a simple InfoDialog.
         *
         * @param context The context where the InfoDialog will be shown.
         * @param title   Title of the InfoDialog.
         * @param message Description of the InfoDialog.
         * */
        public static void showStandardInfoDialog(@NonNull @NotNull Context context, @NonNull @NotNull String title,
                                                  @NonNull @NotNull String message){
            //https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android/2115770#2115770
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(R.drawable.ic_baseline_info_triangle_24)
                    .show();
        }
    }


    /**
     * Use this function in order to add predefined animations for actions from NavigationGraph.
     *
     * If you do not use this function you should set this animations manually for every action from
     * the NavigationGraph.
     *
     * @return NavOptions object which will have the predefined animations.
     * */
    public static NavOptions getNavOptions() {
        // https://stackoverflow.com/questions/50482095/how-do-i-define-default-animations-for-navigation-actions/52413868#52413868
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.anim_slide_in_right)
                .setExitAnim(R.anim.anim_slide_out_left)
                .setPopEnterAnim(R.anim.anim_slide_in_left)
                .setPopExitAnim(R.anim.anim_slide_out_right)
                .build();
    }


    /** All utilities related to activities or Authentication. */
    public abstract static class Auth {
        public static boolean isUserLoggedIn(){
            return false;
        }
    }

    /** All utilities related to recycler view adapters. */
    public static final class Adapters {

        /**
         * Use to check if adapter position is not set to NO_POSITION.
         *
         * @param position Position to be checked.
         *
         * @return true if position is not NO_POSITION, false otherwise.
         * */
        public static boolean isGoodAdapterPosition(int position){
            if(position == NO_POSITION){
                Timber.w("position is set to NO_POSITION");
                return false;
            }
            return true;
        }
    }

}
