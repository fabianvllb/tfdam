package com.example.tfdam;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecoration extends RecyclerView.ItemDecoration {
    public GridItemDecoration() {}
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int numberOfColumns = getSpanCount(parent);
        if (numberOfColumns == 2){
            if(position%numberOfColumns == 0){
                outRect.left = 0;
                outRect.right = 3;
            }else{
                outRect.left = 3;
                outRect.right = 0;
            }
        } else if(numberOfColumns == 3){
            if(position%(numberOfColumns-1) == 0){
                outRect.left = 0;
                outRect.right = 3;
            }else if ((position+2)%(numberOfColumns-1) == 0){
                outRect.left = 3;
                outRect.right = 0;
            }else{
                outRect.left = 3;
                outRect.right = 3;
            }
        }
        outRect.top = 0;
        outRect.bottom = 6;
    }

    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }
}
