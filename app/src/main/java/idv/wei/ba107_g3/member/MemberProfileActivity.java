package idv.wei.ba107_g3.member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.friends.FriendsListDAO;
import idv.wei.ba107_g3.friends.FriendsListDAO_interface;
import idv.wei.ba107_g3.main.Util;

public class MemberProfileActivity extends AppCompatActivity {
    private ImageView memPhoto;
    private TextView memAge, memGenger, memCounty, memIntro, memHeight, memWeight, memBloodType, memInterest, memContact, memEmotion;
    private MemberVO member;
    private CollapsingToolbarLayout toolbar_layout;
    private Toolbar toolbar;
    private FloatingActionButton addfab, giftfab,alreadyfab;
    private List<MemberVO> friendList = new ArrayList<>();
    private static final int REQUEST_LOGIN = 1;
    private String mem_no_self;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberprofile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        find();
        show();
        try {
            pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
            String memJson = pref.getString("loginMem","");
            mem_no_self = new Gson().fromJson(memJson.toString(),MemberVO.class).getMemNo();
            HaveWait haveWait = new HaveWait();
            if(haveWait.execute(mem_no_self,member.getMemNo()).get()){
                addfab.setVisibility(View.INVISIBLE);
                alreadyfab.setVisibility(View.VISIBLE);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        giftfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                if (!pref.getBoolean("login", false)) {
                    Toast.makeText(MemberProfileActivity.this, "請先登入", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(MemberProfileActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    show();
                }else {
                    String memJson = pref.getString("loginMem","");
                    mem_no_self = new Gson().fromJson(memJson.toString(),MemberVO.class).getMemNo();
                    InsertFriend insertFriend = new InsertFriend();
                    insertFriend.execute(mem_no_self,member.getMemNo());
                    addfab.setVisibility(View.INVISIBLE);
                    alreadyfab.setVisibility(View.VISIBLE);
                    Toast.makeText(MemberProfileActivity.this,getString(R.string.sendadd),Toast.LENGTH_SHORT).show();
                }
            }
        });
        alreadyfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MemberProfileActivity.this,getString(R.string.alreadyadd),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void find() {
        memPhoto = findViewById(R.id.memPhoto);
        memAge = findViewById(R.id.memAge);
        memGenger = findViewById(R.id.memGender);
        memCounty = findViewById(R.id.memCounty);
        memIntro = findViewById(R.id.memIntro);
        memHeight = findViewById(R.id.memHeight);
        memWeight = findViewById(R.id.memWeight);
        memBloodType = findViewById(R.id.memBloodType);
        memInterest = findViewById(R.id.memInterest);
        memContact = findViewById(R.id.memContact);
        memEmotion = findViewById(R.id.memEmotion);
        toolbar_layout = findViewById(R.id.toolbar_layout);
        giftfab = findViewById(R.id.giftfab);
        addfab = findViewById(R.id.addfab);
        alreadyfab = findViewById(R.id.alreadyfab);
    }

    private void show() {
        Bundle bundle = getIntent().getExtras();
        member = (MemberVO) bundle.getSerializable("member");

        SharedPreferences pref = this.getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        MemberVO memberVO = new Gson().fromJson(pref.getString("loginMem", ""), MemberVO.class);
        if (memberVO != null) {
            try {
                SharedPreferences friendListpref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                String json = friendListpref.getString("friendsList", "");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MemberVO>>() {
                }.getType();
                friendList = gson.fromJson(json.toString(), listType);
                if (friendList == null) {
                    String mem_no = memberVO.getMemNo();
                    GetFriendsList getFriendsList = new GetFriendsList();
                    friendList = getFriendsList.execute(mem_no).get();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (friendList != null) {
                for (int i = 0; i < friendList.size(); i++) {
                    if (friendList.get(i).getMemAccount().equals(member.getMemAccount())) {
                        addfab.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }

        byte[] photo = member.getMemPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        memPhoto.setImageBitmap(bitmap);
        toolbar_layout.setTitle(member.getMemName());
        memGenger.setText(member.getMemGender());
        memAge.setText(Util.getAge(member.getMemAge()));
        memCounty.setText(member.getMemCounty());
        memIntro.setText(member.getMemIntro());
        memHeight.setText(getString(R.string.memheight) + (member.getMemHeight().toString()));
        memWeight.setText(getString(R.string.memWeight) + member.getMemWeight().toString());
        memBloodType.setText(getString(R.string.memBloodType) + member.getMemBloodType());
        memInterest.setText(getString(R.string.memInterest) + member.getMemInterest());
        memContact.setText(getString(R.string.memContact) + member.getMemContact());
        memEmotion.setText(getString(R.string.memEmotion) + member.getMemEmotion());
    }

    class GetFriendsList extends AsyncTask<String, Void, List<MemberVO>> {
        @Override
        protected List<MemberVO> doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            return dao.getMemberFriends(params[0]);
        }
    }

    class HaveWait extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            return dao.havewait(params[0],params[1]);
        }
    }

    class InsertFriend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
             dao.insert(params[0],params[1]);
             return null;
        }
    }
}





