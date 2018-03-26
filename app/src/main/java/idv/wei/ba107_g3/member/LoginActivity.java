package idv.wei.ba107_g3.member;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.main.Util;

public class LoginActivity extends AppCompatActivity {
    private EditText account,password;
    private Button btnlogin;

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
            Util.showMessage(this,R.string.loginerror1);
            return;
        }
        if(isMember(mem_account,mem_password)){
            SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
            pref.edit().putBoolean("login",true)
                    .putString("account",mem_account)
                    .putString("password",mem_password)
                    .apply();
            setResult(RESULT_OK);
            finish();
        }else {
            account.setText("");
            password.setText("");
            Util.showMessage(this,R.string.loginerror2);
        }
    }
}
