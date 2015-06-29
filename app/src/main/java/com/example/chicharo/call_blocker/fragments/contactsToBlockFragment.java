package com.example.chicharo.call_blocker.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.ContactsContract;

import com.example.chicharo.call_blocker.R;
import com.example.chicharo.call_blocker.activities.myBlackList;
import com.example.chicharo.call_blocker.adapters.ContactAdapter;
import com.example.chicharo.call_blocker.dataBases.PhonesDataSource;
import com.example.chicharo.call_blocker.models.contactModel;

import java.util.ArrayList;
import java.util.List;

public class contactsToBlockFragment extends Fragment implements ContactAdapter.onItemClickListener{
    ContactAdapter contactsToBlockAdapter;
    List<contactModel> contacts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_choose_recent_calls, container, false);
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_blocked_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactsToBlockAdapter = new ContactAdapter(getAllContacts());
        contactsToBlockAdapter.SetOnItemClickListener(this);
        recyclerView.setAdapter(contactsToBlockAdapter);
        return rootView;
    }

    public List<contactModel> getAllContacts() {
        contacts = new ArrayList<contactModel>();
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){
            contactModel contactModel = ContactToOwnContactModel(cursor);
            if(contactModel != null){
                contacts.add(contactModel);
            }
        }
        cursor.close();
        return contacts;
    }

    public contactModel ContactToOwnContactModel(Cursor cursor){
        contactModel contactModel = new contactModel();
        int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        if (hasPhoneNumber == 1) {
            contactModel.setContactName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contactModel.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        } else {
            return null;
        }
        return contactModel;
    }

    public void addContactToBlockedContacts(int position){
        contactModel contact = contacts.get(position);
        PhonesDataSource phonesDataSource = new PhonesDataSource(getActivity());
        phonesDataSource.open();
        phonesDataSource.createBlockedContact(contact.getPhoneNumber(), contact.getContactName());
        phonesDataSource.close();
    }

    @Override
    public void onItemClick(View v, int position) {
        addContactToBlockedContacts(position);
        Intent startMyBlackList = new Intent(getActivity(), myBlackList.class);
        startActivity(startMyBlackList);
        getActivity().finish();
    }
}
