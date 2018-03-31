package idv.wei.ba107_g3.friends;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.main.MainActivity;
import idv.wei.ba107_g3.main.Util;
import idv.wei.ba107_g3.member.MemberDAO;
import idv.wei.ba107_g3.member.MemberDAO_interface;
import idv.wei.ba107_g3.member.MemberProfileActivity;
import idv.wei.ba107_g3.member.MemberVO;

import static android.content.Context.MODE_PRIVATE;

public class FriendsListFragment extends Fragment {
    private Dialog dialog;
    private Button btnTalk,btnMem,btnBlock,btnUnBlock,btnback,btnconfirm;
    private TextView dialog_name,dialog_gender,dialog_age;
    private ImageView dialog_photo,dialog_close;
    private RecyclerView recyclerView_friendList;
    private ProgressDialog progressDialog;
    private MemberVO memberVO;
    private List<MemberVO> friendList = new ArrayList<>();
    private EditText searchfriend;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friendslist,container,false);
        searchfriend = view.findViewById(R.id.searchfriend);
//        searchfriend.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                Snackbar.make(view, searchfriend.getText().toString(), Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
//                return false;
//            }
//        });
        recyclerView_friendList = view.findViewById(R.id.recyclerview_friendlist);
        recyclerView_friendList.setHasFixedSize(true);
        SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        memberVO = new Gson().fromJson(pref.getString("loginMem", ""), MemberVO.class);
        String mem_no = memberVO.getMemNo();
        GetFriendsList getFriendsList = new GetFriendsList();
        getFriendsList.execute(mem_no);
        return view;
    }

    private class GetFriendsList extends AsyncTask<String , Void , List<MemberVO>> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<MemberVO> doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            return dao.getMemberFriends(params[0]);
        }

        @Override
        protected void onPostExecute(List<MemberVO> memberVO) {
            super.onPostExecute(memberVO);
            progressDialog.cancel();
            friendList = memberVO;
            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
            Gson gson = new Gson();
            pref.edit().putString("friendsList",gson.toJson(memberVO)).commit();
            recyclerView_friendList.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            FriendAdapter friendAdapter = new FriendAdapter();
            recyclerView_friendList.setAdapter(friendAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) friendAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView_friendList);

        }
    }

    class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> implements ItemTouchHelperAdapter{

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView friendPhoto;
            private TextView friendName;
            private CardView cardview_friendList;
            private float defaultZ;

            public ViewHolder(View itemView) {
                super(itemView);
                friendPhoto = itemView.findViewById(R.id.friendPhoto);
                friendName = itemView.findViewById(R.id.friendName);
                cardview_friendList = itemView.findViewById(R.id.cardview_friendList);
                defaultZ = itemView.getTranslationZ();
            }

//            @Override
//            public void onItemSelected() {
//                itemView.setTranslationZ(15.0f);
//            }
//
//            @Override
//            public void onItemClear() {
//                itemView.setTranslationZ(defaultZ);
//            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.cardview_friendlistfragment,parent,false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewholder, int position) {
            final MemberVO member = friendList.get(position);
            byte[] photo = member.getMemPhoto();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(photo,0,photo.length);
            viewholder.friendPhoto.setImageBitmap(Util.getCircleBitmap(bitmap,300));
            viewholder.friendName.setText(member.getMemName());
            viewholder.cardview_friendList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = new Dialog(getContext());
                    dialog.setTitle("Friend");
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_friendslist);
                    Window dw =dialog.getWindow();
                    WindowManager.LayoutParams lp = dw.getAttributes();
                    lp.alpha = 1.0f;
                    lp.width = 1000;
                    lp.height = 1350;
                    dw.setAttributes(lp);
                    dialog_photo = dialog.findViewById(R.id.dialog_photo);
                    dialog_name = dialog.findViewById(R.id.dialog_name);
                    dialog_age = dialog.findViewById(R.id.dialog_age);
                    dialog_gender = dialog.findViewById(R.id.dialog_gender);
                    dialog_close = dialog.findViewById(R.id.dialog_close);
                    dialog_photo.setImageBitmap(bitmap);
                    dialog_name.setText(member.getMemName());
                    dialog_age.setText(Util.getAge(member.getMemAge()));
                    dialog_gender.setText(member.getMemGender());
                    dialog_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });
                    btnMem = dialog.findViewById(R.id.btnMem);
                    btnMem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), MemberProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("member", member);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendList.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            // 將集合裡的資料進行交換
            Collections.swap(friendList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(final int position) {
            final MemberVO member = friendList.get(position);
            friendList.remove(position);
            dialog = new Dialog(getContext());
            dialog.setTitle("deleteConfirm");
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_friendslist_delete);
            Window dw =dialog.getWindow();
            WindowManager.LayoutParams lp = dw.getAttributes();
            lp.alpha = 1.0f;
            lp.width = 1000;
            lp.height = 650;
            dw.setAttributes(lp);
            btnback = dialog.findViewById(R.id.btnback);
            btnconfirm = dialog.findViewById(R.id.btnconfirm);
            btnconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    friendList.add(member);
                    FriendAdapter friendAdapter = new FriendAdapter();
                    recyclerView_friendList.setAdapter(friendAdapter);
                }
            });
            FriendAdapter friendAdapter = new FriendAdapter();
            recyclerView_friendList.setAdapter(friendAdapter);
            dialog.show();
            notifyItemRemoved(position);
        }
    }


    private class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private ItemTouchHelperAdapter adapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            // 也可以設ItemTouchHelper.RIGHT(向右滑)，或是 ItemTouchHelper.START | ItemTouchHelper.END (左右滑都可以)
            int swipeFlags = ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }
}
