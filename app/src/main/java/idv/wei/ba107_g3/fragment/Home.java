package idv.wei.ba107_g3.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.friends.FriendsListVO;

public class Home extends Fragment {

    private List<FriendsListVO> friendList;
    private RecyclerView recyclerView;
    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.carousel1,R.drawable.carousel2,R.drawable.carousel3};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.home, null);
        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        friendList = getFriendList();
        recyclerView.setAdapter(new PopularAdapter(inflater));
        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(final int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private List<FriendsListVO> getFriendList() {
        friendList = new ArrayList<>();
        return friendList;
    }

    private class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
        private LayoutInflater inflater;

        public PopularAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView photo;
            private TextView name, gender, county;

            public ViewHolder(View itemView) {
                super(itemView);
                photo = itemView.findViewById(R.id.photo);
                name = itemView.findViewById(R.id.name);
                gender = itemView.findViewById(R.id.gender);
                county = itemView.findViewById(R.id.county);
            }
        }

        @Override
        public int getItemCount() {
            return friendList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = inflater.inflate(R.layout.recycleview_home, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewholder, int position) {
            viewholder.photo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }
    }
}

