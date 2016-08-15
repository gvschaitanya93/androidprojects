package com.app.crazyheads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by Messi10 on 04-Jan-15.
 */

public class Dashboard extends ActionBarActivity {

    String username;
    PagerSlidingTabStrip mTabs;
    ViewPager mPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar=(Toolbar)findViewById(R.id.app_bar2);
        setSupportActionBar(toolbar);
        Bundle extras= getIntent().getExtras();
//        if(extras!=null){
//            username=extras.getString("username");
//            TextView tv=(TextView)findViewById(R.id.username_tv);
//            tv.setText("Welcome "+username+" !");
//        }
        mPager= (ViewPager) findViewById(R.id.dashboard_pager);
        mTabs= (PagerSlidingTabStrip) findViewById(R.id.tabs);

        mPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mTabs.setViewPager(mPager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mPager.setPageMargin(pageMargin);
        mTabs.setBackgroundColor(getResources().getColor(R.color.green));

        mPager.setBackgroundColor(getResources().getColor(R.color.green));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

           startActivity(new Intent(Dashboard.this,Settings.class));
            return true;
        }
        if(id==R.id.action_addTicket){
            Intent intent=new Intent(this,AddTicket.class);
            intent.putExtra("username",username);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class PagerAdapter extends FragmentPagerAdapter{

        private final String[] TITLES={"Posted","Ongoing","Completed"};
        PagerAdapter(FragmentManager fm){
            super(fm);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return StatusFragment.newInstance(position) ;
        }
    }
}
