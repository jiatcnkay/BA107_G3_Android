package idv.wei.ba107_g3.member;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import idv.wei.ba107_g3.R;

public class BasicSearchFragment extends Fragment {

    private RecyclerView recyclerView_basicsearch;
    private List<MemberVO> allMemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_basicsearch, null);
        recyclerView_basicsearch = view.findViewById(R.id.recyclerview_basicsearch);
        recyclerView_basicsearch.setHasFixedSize(true);
        recyclerView_basicsearch.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        try {
            GetALLMember getALLMember = new GetALLMember();
            allMemList = getALLMember.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        recyclerView_basicsearch.setAdapter(new BasicSearchAdapter(inflater));
        return view;
    }

    private class GetALLMember extends AsyncTask<Void, Void, List<MemberVO>> {

        @Override
        protected List<MemberVO> doInBackground(Void... voids) {
            MemberDAO_interface dao = new MemberDAO();
            return dao.getAll();
        }
    }

    private class BasicSearchAdapter extends RecyclerView.Adapter<BasicSearchAdapter.ViewHolder> {
        private LayoutInflater inflater;

        public BasicSearchAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView photo;
            private TextView name, gender, county;
            private CardView cardview;

            public ViewHolder(View itemView) {
                super(itemView);
                photo = itemView.findViewById(R.id.photo);
                name = itemView.findViewById(R.id.name);
                gender = itemView.findViewById(R.id.gender);
                county = itemView.findViewById(R.id.county);
                cardview = itemView.findViewById(R.id.cardview);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = inflater.inflate(R.layout.cardview_basicfragment, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewholder, int position) {
            MemberVO member = allMemList.get(position);
            byte[] memPhoto = member.getMemPhoto();
            Bitmap bitmap = BitmapFactory.decodeByteArray(memPhoto, 0, memPhoto.length);
            viewholder.photo.setImageBitmap(bitmap);
            viewholder.name.setText(member.getMemName());
            viewholder.gender.setText(member.getMemGender());
            viewholder.county.setText(member.getMemCounty());
            viewholder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(),MemberProfileActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return allMemList.size();
        }
    }
}

