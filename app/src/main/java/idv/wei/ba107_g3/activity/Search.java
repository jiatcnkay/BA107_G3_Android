package idv.wei.ba107_g3.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import idv.wei.ba107_g3.R;
import idv.wei.ba107_g3.member.BasicSearchFragment;
import idv.wei.ba107_g3.member.DistanceSearchFragment;
import idv.wei.ba107_g3.member.MemberVO;

public class Search extends AppCompatActivity {
    private TabLayout tablayout;
    private ViewPager viewPager;
    private MemberVO memberVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        tablayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewPager);
        Bundle bundle = getIntent().getExtras();
        memberVO = (MemberVO) bundle.getSerializable("SelfMem");
        viewPager.setAdapter(new MySearchAdapter(getSupportFragmentManager()));
        tablayout.setupWithViewPager(viewPager);
    }

    class MySearchAdapter extends FragmentPagerAdapter {

        public MySearchAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    return new BasicSearchFragment(memberVO);
                case 1 :
                    return new DistanceSearchFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0 :
                    return "基本搜尋";
                case 1 :
                    return "距離配對";
            }
            return null;
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
}

