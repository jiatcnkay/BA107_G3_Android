package idv.wei.ba107_g3.member;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.main.Util;

public class LoginActivity extends AppCompatActivity {
    private EditText account,password;
    private Button btnlogin;
    private MemberSelect memberSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        find();
        setResult(RESULT_CANCELED);
    }

    private void find() {
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btnlogin);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        boolean login = pref.getBoolean("login",false);
        if(login){
            String name = pref.getString("user","");
            String password = pref.getString("password","");
            if (isMember(name, password)) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private boolean isMember(final String account, final String password) {
        boolean isMember;
        try {
            isMember = new AsyncTask<String,Void,Boolean>(){
                @Override
                protected Boolean doInBackground(String... strings) {
                    MemberDAO_interface memberDAO = new MemberDAO();
                    return memberDAO.isMember(account,password);
                }
            }.execute().get();
        }  catch (Exception e) {
            isMember = false;
        }
        return isMember;
    }


    public void onClickLogin(View view){
        String mem_account = account.getText().toString();
        String mem_password = password.getText().toString();
        if(mem_account.length() <= 0 || mem_password.length() <= 0){
            Util.showMessage(this,getString(R.string.loginerror1));
            return;
        }
        if(isMember(mem_account,mem_password)){
            try {
                SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                memberSelect = new MemberSelect();
                MemberVO member = memberSelect.execute(mem_account).get();
                String memJson = new Gson().toJson(member);
                pref.edit().putBoolean("login", true)
                        .putString("account", mem_account)
                        .putString("password", mem_password)
                        .putString("loginMem",memJson)
                        .apply();
                setResult(RESULT_OK);
                finish();
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }else {
            account.setText("");
            password.setText("");
            Util.showMessage(this,getString(R.string.loginerror2));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MemberSelect extends AsyncTask<String, Void, MemberVO> {
        @Override
        protected MemberVO doInBackground(String... params) {
            MemberDAO_interface dao = new MemberDAO();
            return dao.getOneByAccount(params[0]);
        }
    }
}
