package com.example.truckup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddDialogFragment extends BottomSheetDialogFragment {

    public static AddDialogFragment newInstance() {
        return new AddDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add, container, false);

        Button addTruckButton = view.findViewById(R.id.add_truck_button);
        Button addPostButton = view.findViewById(R.id.add_post_button);

        addTruckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddTruckActivity or AddTruckFragment
                Intent intent = new Intent(getActivity(), AddTruckActivity.class);
                startActivity(intent);
                dismiss();
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddPostActivity or AddPostFragment
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
                dismiss();
            }
        });

        return view;
    }
}