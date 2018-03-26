package idv.wei.ba107_g3.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.fragment.Home;
import idv.wei.ba107_g3.member.LoginActivity;
import idv.wei.ba107_g3.member.MemberDAO;
import idv.wei.ba107_g3.member.MemberDAO_interface;
import idv.wei.ba107_g3.member.MemberVO;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Menu toolBarMenu;
    private MenuItem btnlogout, btnlogin;
    private static final int REQUEST_LOGIN = 1;
    private MemberSelect memberSelect;
    private RelativeLayout navi_layout;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find();
        setNavigation();
        changeFragment(new Home());
        setToolbar();
//        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
//        account = pref.getString("account", "");
//        if (account != null) {
//            memberSelect = new MemberSelect();
//            memberSelect.execute();
//        }
    }

    private void find() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
    }

    public void setNavigation() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setCheckable(true);
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navi_item_home:
                        changeFragment(new Home());
                        toolbar.setTitle("Toast");
                        break;
                    case R.id.navi_item_search:
                        // changeFragment(new SearchFragment());
                        toolbar.setTitle("交友搜尋");
                        break;
                    case R.id.navi_item_chat:
                        // changeFragment(new ChatFragment());
                        toolbar.setTitle("聊天室");
                        break;
                    case R.id.navi_item_shop:
                        // changeFragment(new ShopFragment());
                        toolbar.setTitle("禮物商城");
                        break;
                }
                return true;
            }
        });
    }

    public void changeFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerview, fragment).commit();
    }

    private void setToolbar() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.notify:
                        Toast.makeText(MainActivity.this, "按下了通知鍵", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnlogin:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, REQUEST_LOGIN);
                        break;
                    case R.id.btnlogout:
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        pref.edit().putBoolean("login", false)
                                .putString("account", "")
                                .putString("password", "")
                                .apply();
                        btnlogout.setVisible(false);
                        btnlogin.setVisible(true);
                        navi_layout = navigationView.findViewById(R.id.navi_layout);
                        navi_layout.setVisibility(View.INVISIBLE);
                        logo = navigationView.findViewById(R.id.logo);
                        logo.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        toolBarMenu = menu;
        btnlogin = toolBarMenu.findItem(R.id.btnlogin);
        btnlogout = toolBarMenu.findItem(R.id.btnlogout);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOGIN:
                    SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                    boolean login = pref.getBoolean("login", false);
                    if (!login) {
                        Util.showMessage(MainActivity.this,R.string.loginfailed);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, REQUEST_LOGIN);
                    } else {
                        btnlogin.setVisible(false);
                        btnlogout.setVisible(true);
                        Util.showMessage(MainActivity.this,R.string.welcome);
                        memberSelect = new MemberSelect();
                        memberSelect.execute();
                    }
            }
        }
    }

    class MemberSelect extends AsyncTask<Void, Void, MemberVO> {
        @Override
        protected MemberVO doInBackground(Void... voids) {
            SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
            String account = pref.getString("account", "");
            Log.e(TAG,"OOOOOOOOOOOOOO = "+account);
            MemberDAO_interface dao = new MemberDAO();
            return dao.memberSelect(account);
        }

        @Override
        protected void onPostExecute(MemberVO memberVO) {
            super.onPostExecute(memberVO);
            showMember(memberVO);
        }
    }

    private void showMember(MemberVO memberVO) {
        ImageView logo = navigationView.findViewById(R.id.logo);
        ImageView memPhoto = navigationView.findViewById(R.id.memPhoto);
        TextView memName = navigationView.findViewById(R.id.memName);
        TextView memDeposit = navigationView.findViewById(R.id.memDeposit);
        TextView memBonus = navigationView.findViewById(R.id.memBonus);
        RelativeLayout navi_layout = navigationView.findViewById(R.id.navi_layout);
        navi_layout.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        byte[] photo = memberVO.getMemPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        memPhoto.setImageBitmap(bitmap);
        memName.setText(memberVO.getMemName());
        memDeposit.setText(memberVO.getMemDeposit().toString()+" 元");
        memBonus.setText(memberVO.getMemBonus().toString()+" 點");
    }

    @Override
    protected void onPause() {
        if (memberSelect != null) {
            memberSelect.cancel(true);
        }
        super.onPause();
    }
}
