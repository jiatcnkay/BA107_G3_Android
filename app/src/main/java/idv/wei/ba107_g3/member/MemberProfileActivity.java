package idv.wei.ba107_g3.member;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.main.Util;

public class MemberProfileActivity extends AppCompatActivity {
    private ImageView memPhoto;
    private TextView memAge, memGenger, memCounty, memIntro, memHeight, memWeight, memBloodType, memInterest, memContact, memEmotion;
    private MemberVO member;
    private CollapsingToolbarLayout toolbar_layout;
    private Toolbar toolbar;
    private FloatingActionButton addfab,giftfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberprofile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        find();
        show();
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
    }

    private void show() {
        Bundle bundle = getIntent().getExtras();
        member = (MemberVO) bundle.getSerializable("member");
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        String json = pref.getString("friendsList","");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<MemberVO>>(){
        }.getType();
        List<MemberVO> friendList = gson.fromJson(json.toString(),listType);
        if(friendList.size()!=0) {
            for (int i = 0; i < friendList.size(); i++) {
                if (friendList.get(i).getMemAccount().equals(member.getMemAccount())) {
                    addfab.setVisibility(View.INVISIBLE);
                }
            }
        }else
            addfab.setVisibility(View.VISIBLE);
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
}
