package com.example.administrator.qlda;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 10/3/2016.
 */
public class MyFragmentPageAdapter extends FragmentStatePagerAdapter{

    ArrayList<Fragment> listFragment;
    public MyFragmentPageAdapter(FragmentManager fm, ArrayList<Fragment> listFragment) {
        super(fm);
        this.listFragment = listFragment;
    }

    public MyFragmentPageAdapter(FragmentManager fm){
        super(fm);
        listFragment = new ArrayList<>();
    }

    public void add(Fragment fragment){
        listFragment.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }

    @Override
    public int getItemPosition(Object object) {
        Fragment fragment = (Fragment)object;
        int position = listFragment.indexOf(fragment);

        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }

    }

    public void replace(Fragment f,int position){
        listFragment.set(position,f);
    }

}
