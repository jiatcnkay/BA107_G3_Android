package idv.wei.ba107_g3.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.activity.Gift;
import idv.wei.ba107_g3.activity.Home;
import idv.wei.ba107_g3.activity.Search;
import idv.wei.ba107_g3.activity.Talk;
import idv.wei.ba107_g3.member.LoginActivity;
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
    private MenuItem btnlogout, btnlogin, notify;
    private static final int REQUEST_LOGIN = 1;
    private RelativeLayout navi_layout;
    private ImageView logo;
    private MemberVO memberVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find();
        setNavigation();
        setToolbar();
        changeFragment(new Home());
    }


    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.this.invalidateOptionsMenu();
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        String memJson = pref.getString("loginMem","");
        if(memJson.length()!=0){
            memberVO = new Gson().fromJson(memJson.toString(),MemberVO.class);
            showMember(memberVO);
        }
    }

    public void find() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
    }

    public void setNavigation() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setCheckable(false);
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.navi_item_home:
                        changeFragment(new Home());
                        toolbar.setTitle("Toast");
                        break;
                    case R.id.navi_item_search:
                        Intent searchIntent = new Intent(MainActivity.this, Search.class);
                        startActivity(searchIntent);
                        break;
                    case R.id.navi_item_chat:
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                        if(!pref.getBoolean("login",false)){
                            Toast.makeText(MainActivity.this,"請先登入",Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(loginIntent,REQUEST_LOGIN);
                        }else {
                            Intent talkIntent = new Intent(MainActivity.this, Talk.class);
                            startActivity(talkIntent);
                        }
                        break;
                    case R.id.navi_item_shop:
                        Intent giftIntent = new Intent(MainActivity.this, Gift.class);
                        startActivity(giftIntent);
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
                        Toast.makeText(MainActivity.this, memberVO.getMemName(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnlogin:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.btnlogout:
                        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                        pref.edit().putBoolean("login", false)
                                .remove("account")
                                .remove("password")
                                .remove("loginMem")
                                .remove("advanced")
                                .remove("friendsList")
                                .remove("sendList")
                                .apply();
                        btnlogout.setVisible(false);
                        btnlogin.setVisible(true);
                        notify.setVisible(false);
                        navi_layout = navigationView.getHeaderView(0).findViewById(R.id.navi_layout);
                        navi_layout.setVisibility(View.INVISIBLE);
                        logo = navigationView.getHeaderView(0).findViewById(R.id.logo);
                        logo.setVisibility(View.VISIBLE);
                        memberVO = null;
                        Util.CART = new ArrayList<>();
                        Util.count = 0;
                        break;
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
        notify = toolBarMenu.findItem(R.id.notify);
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        if(pref.getBoolean("login",false)) {
            btnlogout.setVisible(true);
            btnlogin.setVisible(false);
            notify.setVisible(true);
        }else {
            btnlogout.setVisible(false);
            btnlogin.setVisible(true);
            notify.setVisible(false);
        }
        return true;
    }

    public void showMember(MemberVO memberVO) {
        ImageView logo = navigationView.getHeaderView(0).findViewById(R.id.logo);
        ImageView memPhoto = navigationView.getHeaderView(0).findViewById(R.id.memPhoto);
        TextView memName = navigationView.getHeaderView(0).findViewById(R.id.memName);
        TextView memDeposit = navigationView.getHeaderView(0).findViewById(R.id.memDeposit);
        TextView memBonus = navigationView.getHeaderView(0).findViewById(R.id.memBonus);
        RelativeLayout navi_layout = navigationView.getHeaderView(0).findViewById(R.id.navi_layout);
        navi_layout.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        byte[] photo = memberVO.getMemPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        memPhoto.setImageBitmap(bitmap);
        memName.setText(memberVO.getMemName());
        memDeposit.setText(memberVO.getMemDeposit().toString() + getString(R.string.dollar));
        memBonus.setText(memberVO.getMemBonus().toString() + getString(R.string.point));
    }
}
