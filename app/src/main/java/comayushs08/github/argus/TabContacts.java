package comayushs08.github.argus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class TabContacts extends Fragment {

    final int pickerResult = 2015;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2, MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    ListView listView;
    ArrayList<Contacts> contactsList = new ArrayList<>();
    ContactsAdapter contactsAdapter;
    FloatingActionButton fabAdd, fabSms;
    TextView helperText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        fabAdd = rootView.findViewById(R.id.fabAdd);
        fabSms = rootView.findViewById(R.id.fabSms);
        listView = rootView.findViewById(R.id.listView);
        helperText = rootView.findViewById(R.id.helperText);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContacts();
            }
        });

        fabSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMSMessage();
            }
        });

        hideHelper();

        contactsAdapter = new ContactsAdapter(TabContacts.this.getContext(), contactsList);

        listView.setAdapter(contactsAdapter);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == pickerResult && resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                Cursor cursor = getContext().getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(phoneIndex);
                contactsList.add(new Contacts(name, phone));
                contactsAdapter.notifyDataSetChanged();
                hideHelper();
                hideFab();
        }

    }


    private void hideHelper() {
        if (!contactsList.isEmpty()) {
            helperText.setVisibility(View.INVISIBLE);
        }
    }

    private void hideFab() {
        if (contactsList.size() >= 4) {
            fabAdd.hide();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            float dp = getActivity().getResources().getDisplayMetrics().density;
            params.setMargins(0, 0, (int)(24*dp), (int)(24*dp));
            fabSms.setLayoutParams(params);
        }
    }

    protected void pickContacts() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        else {
            Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(pickContact, pickerResult);
        }
    }

    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {
            } else {
               requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("7738348585", null, "CHECK CHECK", null, null);
                    Toast.makeText(getContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(pickContact, pickerResult);

                } else {
                    Toast.makeText(getContext(), "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}