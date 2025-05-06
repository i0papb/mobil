package com.example.hellojava;     // ← change this to match your app’s package

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    /** Tracks which layout is currently displayed */
    private int currentLayoutRes;

    /** Holds the set of all discovered activity_ layouts */
    private final Set<Integer> validLayoutIds = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick your default here (must match one of your activity_*.xml files)
        currentLayoutRes = R.layout.activity_main;
        setContentView(currentLayoutRes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Reflectively find every layout in R.layout whose name starts with "activity_"
        for (Field f : R.layout.class.getFields()) {
            String name = f.getName();
            if (name.startsWith("activity_")) {
                try {
                    int id = f.getInt(null);
                    // Turn "activity_camera_view" → "Camera View"
                    String title = toTitleCase(
                            name
                                    .substring("activity_".length())  // strip off prefix
                                    .replace('_', ' ')
                    );
                    menu.add(Menu.NONE, id, Menu.NONE, title);
                    validLayoutIds.add(id);
                } catch (IllegalAccessException ignored) {}
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (validLayoutIds.contains(id)) {
            swapLayout(id);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Swap in a new layout resource */
    private void swapLayout(int layoutRes) {
        if (layoutRes == currentLayoutRes) return;
        currentLayoutRes = layoutRes;
        setContentView(currentLayoutRes);
        // …and if you have any view-lookups (findViewById) do them here…
    }

    /** Simple helper to capitalize each word */
    private String toTitleCase(String input) {
        StringBuilder out = new StringBuilder();
        for (String word : input.split(" ")) {
            if (word.length() > 0) {
                out.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return out.toString().trim();
    }
}
