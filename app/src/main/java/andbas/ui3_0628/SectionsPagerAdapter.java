package andbas.ui3_0628;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                AchievementFragment achievementFragment = new AchievementFragment();
                return achievementFragment;

            case 1:
                BackpacksFragment backpacksFragment = new BackpacksFragment();
                return backpacksFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "成就";

            case 1:
                return "背包";

            default:
                return null;
        }
    }
}
