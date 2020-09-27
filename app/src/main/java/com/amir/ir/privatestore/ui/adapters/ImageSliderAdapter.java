package com.amir.ir.privatestore.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.amir.ir.privatestore.R;
import com.amir.ir.privatestore.databinding.LayoutImageSlideBinding;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ImageSliderAdapter extends SliderViewAdapter<ImageSliderAdapter.SlideViewHolder> {

    private ImageSliderAdapter.SliderInteractions interactions;

    private List<String> images;

    public ImageSliderAdapter(List<String> images) {
        this.images = images;
    }

    public void setSlideInteractions(ImageSliderAdapter.SliderInteractions interactions) {
        this.interactions = interactions;
    }

    public void deleteItem(int position) {
        this.images.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String image) {
        this.images.add(image);
        notifyDataSetChanged();
    }

    @Override
    public ImageSliderAdapter.SlideViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutImageSlideBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_image_slide, null, false);
        return new ImageSliderAdapter.SlideViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ImageSliderAdapter.SlideViewHolder viewHolder, final int position) {
        String image = images.get(position);
        viewHolder.bind(image);
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return images.size();
    }

    class SlideViewHolder extends SliderViewAdapter.ViewHolder {
        private LayoutImageSlideBinding mBinding;

        SlideViewHolder(LayoutImageSlideBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        void bind(String image) {
            itemView.setOnClickListener(v -> interactions.onItemClicked(image));

            mBinding.setImage(image);
            mBinding.executePendingBindings();
        }
    }


    public interface SliderInteractions {
        void onItemClicked(String image);
    }

}
