package homework.com.bingeeatingproject;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sindhuja on 11/2/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {
   /** int mNumOfTabs;
    android.support.v4.app.Fragment fragment;
    public PagerAdapter(FragmentManager fm, int NumOfTabs)
    {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                  DailyActivityFragment tabl=new DailyActivityFragment();
                return tabl;
            case 1:
                WeeklyActivityFragment tab2=new WeeklyActivityFragment();
                return tab2;
            case 2:
                AppointmentFragment tab3=new AppointmentFragment();
                return tab3;
            case 3:
                NotificationsFragment tab4=new NotificationsFragment();
                return tab4;
            default:
                return null;
        }
       // return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }**/

   private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public PagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public CharSequence getTabTitle(int position) {

        return mFragmentTitleList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        //return mFragmentTitleList.get(position);
        return null;
    }



}
