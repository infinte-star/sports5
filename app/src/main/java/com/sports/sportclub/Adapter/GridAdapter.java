package com.sports.sportclub.Adapter;

import android.support.v4.app.Fragment;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sports.sportclub.UI.UI.fragment.ImagePagerFragment;
import com.sports.sportclub.R;
import com.sports.sportclub.UI.UI.activity.navigationActivity;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.sports.sportclub.Data.ImageData.IMAGE_DRAWABLES;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {


    private interface ViewHolderListener {

        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition);
    }

    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;

    public GridAdapter(android.support.v4.app.Fragment fragment) {
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListernerImp(fragment);
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);
        return new ImageViewHolder(view, requestManager, viewHolderListener);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.onBind();
    }

    @Override
    public int getItemCount() {
        return IMAGE_DRAWABLES.length;
    }

    private class ViewHolderListernerImp implements ViewHolderListener{

        private Fragment fragment;
        private AtomicBoolean enterTransitionStarted;

        public ViewHolderListernerImp(Fragment fragment){
            this.fragment = fragment;
            enterTransitionStarted = new AtomicBoolean();
        }


        @Override
        public void onLoadCompleted(ImageView view, int adapterPosition) {
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if(navigationActivity.currentPosition != adapterPosition){
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();


        }

        /**
         * Handles a view click by setting the current position to the given {@code position} and
         * starting a {@link  ImagePagerFragment} which displays the image at the position.
         *
         * @param view the clicked {@link ImageView} (the shared element view will be re-mapped at the
         * GridFragment's SharedElementCallback)
         * @param position the selected view position
         */
        @Override
        public void onItemClicked(View view, int position) {
            // Update the position.
            navigationActivity.currentPosition = position;

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);

            ImageView transitioningView = view.findViewById(R.id.card_image);
            fragment.getFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true) // Optimize for shared element transition
                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
                    .replace(R.id.frame_content, new ImagePagerFragment(), ImagePagerFragment.class
                            .getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }


        }

    static class ImageViewHolder extends RecyclerView.ViewHolder
            implements android.view.View.OnClickListener{

        private final ImageView image;
        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;

        public ImageViewHolder(View itemView, RequestManager manager,
                               ViewHolderListener listener) {
            super(itemView);
            this.image = itemView.findViewById(R.id.card_image);
            this.requestManager = manager;
            this.viewHolderListener = listener;
            itemView.findViewById(R.id.card_view).setOnClickListener(this);
        }

        void onBind(){
            int adapterPosintion = getAdapterPosition();
            setImage(adapterPosintion);
            image.setTransitionName(String.valueOf(IMAGE_DRAWABLES[adapterPosintion]));
        }

        void setImage(final int adapterPosition){
            requestManager
                    .load(IMAGE_DRAWABLES[adapterPosition])
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(image, adapterPosition);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(image, adapterPosition);
                            return false;
                        }
                    })
                    .into(image);
        }


        @Override
        public void onClick(View v) {
            // Let the listener start the ImagePagerFragment.
            viewHolderListener.onItemClicked(v, getAdapterPosition());
        }
    }



}

