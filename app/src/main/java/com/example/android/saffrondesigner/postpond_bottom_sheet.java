package com.example.android.saffrondesigner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class postpond_bottom_sheet extends BottomSheetDialogFragment {
//    public static postpond_bottom_sheet newInstance() {
//
//        Bundle args = new Bundle();
//
//        postpond_bottom_sheet fragment = new postpond_bottom_sheet();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.postpond_bottom_sheet,container,false);
        return view;
    }
}
