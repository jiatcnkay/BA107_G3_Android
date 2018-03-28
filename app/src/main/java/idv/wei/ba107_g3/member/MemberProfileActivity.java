package idv.wei.ba107_g3.member;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.main.Util;

public class MemberProfileActivity extends AppCompatActivity {
    private ImageView memPhoto;
    private TextView memName,memAge,memGenger,memCounty,memIntro,memHeight,memWeight,memBloodType,memInterest,memContact,memEmotion;
    private MemberVO member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberprofile);
        find();
        show();
    }

    private void find() {
        memPhoto = findViewById(R.id.memPhoto);
        memName = findViewById(R.id.memName);
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
    }

    private void show() {
        Bundle bundle = getIntent().getExtras();
        member = (MemberVO) bundle.getSerializable("member");
        byte[] photo = member.getMemPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        memPhoto.setImageBitmap(bitmap);
        memName.setText(member.getMemName());
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
