package com.amir.ir.privatestore.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.amir.ir.privatestore.R;
import com.amir.ir.privatestore.databinding.LayoutSlideExampleBinding;
import com.amir.ir.privatestore.models.Slide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SlideViewHolder> {

    private SliderInteractions interactions;

    private List<Slide> mSliderItems;

    public SliderAdapter(List<Slide> slides) {
        this.mSliderItems = slides;
    }

    public void setSlideInteractions(SliderInteractions interactions) {
        this.interactions = interactions;
    }

    public void renewItems(List<Slide> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Slide sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SlideViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutSlideExampleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_slide_example, null, false);
        return new SlideViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(SlideViewHolder viewHolder, final int position) {
        Slide sliderItem = mSliderItems.get(position);
        viewHolder.bind(sliderItem);
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SlideViewHolder extends SliderViewAdapter.ViewHolder {
        private LayoutSlideExampleBinding mBinding;

        SlideViewHolder(LayoutSlideExampleBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        void bind(Slide slide) {
            itemView.setOnClickListener(v -> interactions.onItemClicked(slide));

            mBinding.setSlide(slide);
            mBinding.executePendingBindings();
        }
    }


    public interface SliderInteractions {
        void onItemClicked(Slide slide);
    }


}