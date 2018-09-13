package com.miracas.espresso.activity.contacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import com.miracas.R;
import com.miracas.espresso.utils.ProgressDialogHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class ContactListCursor extends AsyncTask<Cursor, Object, ArrayList<User>> {

    ProgressDialogHelper progressDialogHelper;
    Context context;
    ContactListCursorInterface contactListCursorInterface;
    ArrayList<User> contactListDetails;

    public ContactListCursor(Context context,ContactListCursorInterface contactListCursorInterface){

        this.context = context;
        this.contactListCursorInterface = contactListCursorInterface;

        contactListDetails = new ArrayList<User>();
        progressDialogHelper = new ProgressDialogHelper(context,
                context.getString(R.string.getting_contacts_message),
                context.getString(R.string.getting_contacts_title));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogHelper.show();
        }

        @Override
        protected ArrayList<User> doInBackground(Cursor... cursors) {

                ArrayList<User> list = new ArrayList<>();

                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                            Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                            Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                            while (cursorInfo.moveToNext()) {

                                int type = cursorInfo.getInt(cursorInfo.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.TYPE));

                                if(type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE){
                                    User info = new User();
                                    info.setId(id);
                                    info.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                                    info.setPhone(cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                    info.setUrl(pURI.toString());

                                    list.add(info);
                                }


                            }

                            cursorInfo.close();
                        }
                    }
                    cursor.close();
                }

                return list;
        }

        @Override
        protected void onPostExecute(ArrayList<User> number) {
            super.onPostExecute(number);
            Log.d("CONTACTS","onPostExecute: "+number.size());

            sortByName();
            progressDialogHelper.dismiss();
            contactListCursorInterface.onGetContactList(number);
        }

        public void sortByName() {
            Collections.sort(contactListDetails, new Comparator<User>() {
                @Override
                public int compare(final User object1, final User object2) {
                    return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
                }
            });
        }

}


