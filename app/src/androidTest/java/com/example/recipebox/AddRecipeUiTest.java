package com.example.recipebox;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.example.recipebox.ui.MainActivity;
import com.recipebox.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddRecipeUiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAddRecipeFormValidation() {
        onView(withId(R.id.myRecipesFragment)).perform(click());

        onView(withId(R.id.fab_add_recipe)).perform(click());

        onView(withId(R.id.btn_save)).perform(click());

        onView(withId(R.id.edit_name)).check(matches(isDisplayed()));
    }

    @Test
    public void testFillRecipeName() {
        onView(withId(R.id.myRecipesFragment)).perform(click());
        onView(withId(R.id.fab_add_recipe)).perform(click());

        onView(withId(R.id.edit_name)).perform(typeText("Espresso Test Cake"), closeSoftKeyboard());

        onView(withId(R.id.edit_name)).check(matches(withText("Espresso Test Cake")));
    }
}
