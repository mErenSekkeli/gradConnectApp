// Generated by view binder compiler. Do not edit!
package com.erensekkeli.gradconnect.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.erensekkeli.gradconnect.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class AnnouncementItemBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView content;

  @NonNull
  public final TextView creator;

  @NonNull
  public final TextView deadline;

  @NonNull
  public final NestedScrollView nestedScrollView;

  @NonNull
  public final CardView textCardView;

  @NonNull
  public final TextView textView13;

  @NonNull
  public final TextView textView16;

  @NonNull
  public final TextView title;

  private AnnouncementItemBinding(@NonNull ConstraintLayout rootView, @NonNull TextView content,
      @NonNull TextView creator, @NonNull TextView deadline,
      @NonNull NestedScrollView nestedScrollView, @NonNull CardView textCardView,
      @NonNull TextView textView13, @NonNull TextView textView16, @NonNull TextView title) {
    this.rootView = rootView;
    this.content = content;
    this.creator = creator;
    this.deadline = deadline;
    this.nestedScrollView = nestedScrollView;
    this.textCardView = textCardView;
    this.textView13 = textView13;
    this.textView16 = textView16;
    this.title = title;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static AnnouncementItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static AnnouncementItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.announcement_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static AnnouncementItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.content;
      TextView content = ViewBindings.findChildViewById(rootView, id);
      if (content == null) {
        break missingId;
      }

      id = R.id.creator;
      TextView creator = ViewBindings.findChildViewById(rootView, id);
      if (creator == null) {
        break missingId;
      }

      id = R.id.deadline;
      TextView deadline = ViewBindings.findChildViewById(rootView, id);
      if (deadline == null) {
        break missingId;
      }

      id = R.id.nestedScrollView;
      NestedScrollView nestedScrollView = ViewBindings.findChildViewById(rootView, id);
      if (nestedScrollView == null) {
        break missingId;
      }

      id = R.id.textCardView;
      CardView textCardView = ViewBindings.findChildViewById(rootView, id);
      if (textCardView == null) {
        break missingId;
      }

      id = R.id.textView13;
      TextView textView13 = ViewBindings.findChildViewById(rootView, id);
      if (textView13 == null) {
        break missingId;
      }

      id = R.id.textView16;
      TextView textView16 = ViewBindings.findChildViewById(rootView, id);
      if (textView16 == null) {
        break missingId;
      }

      id = R.id.title;
      TextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      return new AnnouncementItemBinding((ConstraintLayout) rootView, content, creator, deadline,
          nestedScrollView, textCardView, textView13, textView16, title);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}