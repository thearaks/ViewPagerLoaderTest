package com.example.viewpagerloadertest;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOADER_CONTACTS = 0;
    private static final int LOADER_FAVOURITES = 1;
    private static final int RC_CONTACTS = 64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ExamplePagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            f.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private static class ContactsCursorAdapter extends CursorAdapter {
        public ContactsCursorAdapter(Context context) {
            super(context, null, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((TextView) view).setText(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
        }
    }

    private static class ExamplePagerAdapter extends FragmentPagerAdapter {
        public ExamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0: return new Fragment1();
                case 1: return new Fragment2();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class Fragment1 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private ListView listView;
        private ContactsCursorAdapter adapter;

        public Fragment1() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            adapter = new ContactsCursorAdapter(getContext());
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list, container, false);
            listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(adapter);

            int grant = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
            if (PermissionChecker.PERMISSION_GRANTED != grant) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, RC_CONTACTS);
            } else {
                getLoaderManager().initLoader(LOADER_CONTACTS, null, this);
            }

            return rootView;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == RC_CONTACTS && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                getLoaderManager().initLoader(LOADER_CONTACTS, null, this);
            }
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(getContext(), ContactsContract.Contacts.CONTENT_URI,
                    new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                    null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            adapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);
        }
    }

    public static class Fragment2 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private ListView listView;
        private ContactsCursorAdapter adapter;

        public Fragment2() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            adapter = new ContactsCursorAdapter(getContext());
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list, container, false);
            listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(adapter);

            int grant = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
            if (PermissionChecker.PERMISSION_GRANTED == grant) {
                getLoaderManager().initLoader(LOADER_FAVOURITES, null, this);
            }

            return rootView;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == RC_CONTACTS && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                getLoaderManager().initLoader(LOADER_FAVOURITES, null, this);
            }
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new CursorLoader(getContext(), ContactsContract.Contacts.CONTENT_URI,
                    new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                    null, null, ContactsContract.Contacts.TIMES_CONTACTED + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            adapter.swapCursor(cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);
        }
    }

}
