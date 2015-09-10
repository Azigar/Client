package ua.azigar.client.Resources;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;

/**
 * Created by Azigar on 08.09.2015.
 * адаптер для фрагментов для активити Квестов
 */
public class FragmentPagerAdapterQuest extends FragmentPagerAdapter {

    Context ctxt=null;
    Database database;

    public FragmentPagerAdapterQuest(Context ctxt, FragmentManager mgr, Database db) {
        super(mgr);
        this.ctxt=ctxt;
        this.database = db;
    }
    @Override
    public int getCount() {
        return(2);
    }
    @Override
    public Fragment getItem(int position) {
        return(PageFragmentQuests.newInstance(position, database));
    }
    @Override
    public String getPageTitle(int position) {
        return(PageFragmentQuests.getTitle(ctxt, position));
    }
}
