// Generated by view binder compiler. Do not edit!
package com.erensekkeli.gradconnect.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.erensekkeli.gradconnect.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentSearchBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button button;

  @NonNull
  public final EditText citySearch;

  @NonNull
  public final Spinner countrySpinner;

  @NonNull
  public final EditText graduationDateSearch;

  @NonNull
  public final EditText nameOrSurnameSearch;

  @NonNull
  public final ScrollView scrollView3;

  @NonNull
  public final ConstraintLayout searchFragment;

  @NonNull
  public final TextView textView3;

  private FragmentSearchBinding(@NonNull ConstraintLayout rootView, @NonNull Button button,
      @NonNull EditText citySearch, @NonNull Spinner countrySpinner,
      @NonNull EditText graduationDateSearch, @NonNull EditText nameOrSurnameSearch,
      @NonNull ScrollView scrollView3, @NonNull ConstraintLayout searchFragment,
      @NonNull TextView textView3) {
    this.rootView = rootView;
    this.button = button;
    this.citySearch = citySearch;
    this.countrySpinner = countrySpinner;
    this.graduationDateSearch = graduationDateSearch;
    this.nameOrSurnameSearch = nameOrSurnameSearch;
    this.scrollView3 = scrollView3;
    this.searchFragment = searchFragment;
    this.textView3 = textView3;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentSearchBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentSearchBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_search, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentSearchBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button;
      Button button = ViewBindings.findChildViewById(rootView, id);
      if (button == null) {
        break missingId;
      }

      id = R.id.citySearch;
      EditText citySearch = ViewBindings.findChildViewById(rootView, id);
      if (citySearch == null) {
        break missingId;
      }

      id = R.id.countrySpinner;
      Spinner countrySpinner = ViewBindings.findChildViewById(rootView, id);
      if (countrySpinner == null) {
        break missingId;
      }

      id = R.id.graduationDateSearch;
      EditText graduationDateSearch = ViewBindings.findChildViewById(rootView, id);
      if (graduationDateSearch == null) {
        break missingId;
      }

      id = R.id.nameOrSurnameSearch;
      EditText nameOrSurnameSearch = ViewBindings.findChildViewById(rootView, id);
      if (nameOrSurnameSearch == null) {
        break missingId;
      }

      id = R.id.scrollView3;
      ScrollView scrollView3 = ViewBindings.findChildViewById(rootView, id);
      if (scrollView3 == null) {
        break missingId;
      }

      ConstraintLayout searchFragment = (ConstraintLayout) rootView;

      id = R.id.textView3;
      TextView textView3 = ViewBindings.findChildViewById(rootView, id);
      if (textView3 == null) {
        break missingId;
      }

      return new FragmentSearchBinding((ConstraintLayout) rootView, button, citySearch,
          countrySpinner, graduationDateSearch, nameOrSurnameSearch, scrollView3, searchFragment,
          textView3);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}