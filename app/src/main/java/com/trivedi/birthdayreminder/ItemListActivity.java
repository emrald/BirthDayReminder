package com.trivedi.birthdayreminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adapter.CustomAdapterList;
import database.DataHelper;
import dataclass.DataList;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    CustomAdapterList adapter;
    int year, month, day;
    static final int DATE_PICKER_ID = 1111;
    String formatedDate;
    TextView tv_datepicker;
    TextView tv_timepicker;
    DataHelper dbHelper;
    ListView listItems;
    ArrayList<DataList> arraylist;
    private int PICK_IMAGE_REQUEST;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView img_frnd;
    String picturePath = "";
   // TextView tv_click;
    ArrayList<String> arraylist_name;
    boolean flag = false;
    int hourOf, minuteOf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        listItems = (ListView) findViewById(R.id.listItems);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        dbHelper = new DataHelper(ItemListActivity.this);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(ItemListActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_add);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);
                    dialog.setCancelable(false);

                    final EditText edt_name = (EditText) dialog.findViewById(R.id.edt_name);
                    tv_datepicker = (TextView) dialog.findViewById(R.id.tv_datepicker);
                    tv_timepicker = (TextView) dialog.findViewById(R.id.tv_timepicker);
                    img_frnd = (ImageView) dialog.findViewById(R.id.img_frnd);
                    TextView tv_add = (TextView) dialog.findViewById(R.id.tv_add);
                    TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
                //    tv_click = (TextView) dialog.findViewById(R.id.tv_click);

                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = dialog.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            dialog.dismiss();
                        }
                    });
                    tv_datepicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = dialog.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            showDialog(DATE_PICKER_ID);
                        }
                    });
                    tv_timepicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = dialog.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(ItemListActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay,
                                                              int minute) {
                                            hourOf = hourOfDay;
                                            minuteOf = minute;
                                            tv_timepicker.setText(hourOfDay + ":" + minute);
                                        }
                                    }, hour, minute, false);
                            timePickerDialog.show();
                        }
                    });
                    tv_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = dialog.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            String name = edt_name.getText().toString();
                            Log.e("Name...", name + "........");
                            dbHelper.openToWrite();
                            if (dbHelper.retrivedata().size() > 0) {
                                arraylist = dbHelper.retrivedata();
                                for (int i = 0; i < arraylist.size(); i++) {
                                    if (arraylist.get(i).getName().equals(name)) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                            if (flag == true) {
                                Toast.makeText(ItemListActivity.this, "Name already exists.", Toast.LENGTH_SHORT).show();
                                flag = false;
                            } else {
                                flag = false;
                                String name_new = edt_name.getText().toString();
                                if (!name_new.trim().equals("")) {
                                    if (!picturePath.equals("")) {
                                        if (!tv_datepicker.getText().equals("")) {
                                            if (!tv_timepicker.getText().equals("")) {
                                                dbHelper.insert(edt_name.getText().toString(), picturePath, tv_datepicker.getText().toString(), tv_timepicker.getText().toString(), year + "", month + "", day + "", hourOf + "", minuteOf + "");
                                                arraylist = dbHelper.retrivedata();
                                                adapter = new CustomAdapterList(ItemListActivity.this, arraylist);
                                                listItems.setAdapter(adapter);
                                                Log.e("hourOf & minuteOf",hourOf+"\n"+minuteOf);
                                                addReminder(edt_name.getText().toString(), year, month, day, hourOf, minuteOf, year, month, day, hourOf, minuteOf + 5);
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(ItemListActivity.this, "Please enter Time.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(ItemListActivity.this, "Please enter Date.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(ItemListActivity.this, "Please enter Image.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ItemListActivity.this, "Please enter Name.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            dbHelper.close();
                        }
                    });
                    img_frnd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, 1);
                        }
                    });
                    dialog.show();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                }
            });
        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                /*final Dialog dialog = new Dialog(ItemListActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                //   dialog.show();
                dialog.getWindow().setAttributes(lp);
                dialog.setCancelable(false);

                DataList data = (DataList) parent.getItemAtPosition(position);
                String name = data.getName().toString();

                TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_yes);
                TextView tv_no = (TextView) dialog.findViewById(R.id.tv_no);
                TextView tv_delete_item_name = (TextView) dialog.findViewById(R.id.tv_delete_item_name);
                tv_delete_item_name.setText("Want to delete " + name + " from list?");
                tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataList data = (DataList) parent.getItemAtPosition(position);
                        String name_inner = data.getName().toString();
                        dbHelper.openToWrite();
                        dbHelper.removeFromList(name_inner);
                        arraylist = dbHelper.retrivedata();
                        adapter = new CustomAdapterList(ItemListActivity.this, arraylist);
                        listItems.setAdapter(adapter);
                        dbHelper.close();
                   //     DeleteCalendarEntry(position);
                        dialog.dismiss();
                    }
                });
                tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            */
                DataList data = (DataList) parent.getItemAtPosition(position);
                String y = data.getYear().toString();
                String m = data.getMonth().toString();
                String d = data.getDay().toString();
                String h = data.getHour().toString();
                String mm = data.getMinute().toString();

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d), Integer.parseInt(h), Integer.parseInt(mm));
                long startMillis = beginTime.getTimeInMillis();

                Calendar endTime = Calendar.getInstance();
                endTime.set(Integer.parseInt(y), Integer.parseInt(m), Integer.parseInt(d), Integer.parseInt(h), Integer.parseInt(mm) + 5);
                long endMillis = endTime.getTimeInMillis();

                Log.e("ID...", ListSelectedCalendars(data.getName()) + ":::ID");

                Uri uri = Uri.parse("content://com.android.calendar/events");
                long eventId = ListSelectedCalendars(data.getName());
                Uri newuri = ContentUris.withAppendedId(uri, eventId);
                Intent intent = new Intent(Intent.ACTION_VIEW, newuri);
                Cursor cursor = getContentResolver().query(newuri, new String[]{"dtstart", "dtend"}, null, null, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    intent.putExtra("beginTime", cursor.getLong(cursor.getColumnIndex("dtstart")));
                    intent.putExtra("endTime", cursor.getLong(cursor.getColumnIndex("dtend")));
                    //intent.putExtra("title", cursor.getString(cursor.getColumnIndex("title")));
                }
               /* dbHelper.openToWrite();
                dbHelper.updateData(cursor.getString(cursor.getColumnIndex("title")), cursor.getLong(cursor.getColumnIndex("dtstart")));
                dbHelper.close();*/
                startActivity(intent);
            }
        });
        listItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(ItemListActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                //   dialog.show();
                dialog.getWindow().setAttributes(lp);
                dialog.setCancelable(false);

                DataList data = (DataList) parent.getItemAtPosition(position);
                String name = data.getName().toString();

                TextView tv_yes = (TextView) dialog.findViewById(R.id.tv_yes);
                TextView tv_no = (TextView) dialog.findViewById(R.id.tv_no);
                TextView tv_delete_item_name = (TextView) dialog.findViewById(R.id.tv_delete_item_name);
                tv_delete_item_name.setText("Want to delete " + name + " from list?");
                tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataList data = (DataList) parent.getItemAtPosition(position);
                        String name_inner = data.getName().toString();
                        dbHelper.openToWrite();
                        dbHelper.removeFromList(name_inner);
                        arraylist = dbHelper.retrivedata();
                        adapter = new CustomAdapterList(ItemListActivity.this, arraylist);
                        listItems.setAdapter(adapter);
                        dbHelper.close();
                        int id = ListSelectedCalendars(data.getName());
                        DeleteCalendarEntry(id);
                        //     DeleteCalendarEntry(position);
                        dialog.dismiss();
                    }
                });
                tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        dbHelper.openToRead();
        arraylist = dbHelper.retrivedata();
        dbHelper.close();
        adapter = new CustomAdapterList(ItemListActivity.this, arraylist);
        listItems.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /*Uri newuri = ContentUris.withAppendedId(Uri.parse("content://com.android.calendar/events"), ListSelectedCalendars("cap"));
                        Cursor cur = getContentResolver().query(newuri, new String[]{"dtstart", "dtend", "title"}, null, null, null);
                        while (cur.moveToNext()) {
                            long calID = 0;
                            String displayName = null;
                            String accountName = null;
                            String ownerName = null;

                            // Get the field values
                            calID = cur.getLong(0);
                            displayName = cur.getString(1);
                            accountName = cur.getString(2);
                            ownerName = cur.getString(3);

                            // Do something with the values...
                            dbHelper.openToWrite();
                            dbHelper.updateData(displayName, 12);
                            dbHelper.close();
                        }*/
                        dbHelper.openToRead();
                        arraylist = dbHelper.retrivedata();
                        dbHelper.close();
                        adapter = new CustomAdapterList(ItemListActivity.this, arraylist);
                        listItems.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                DatePickerDialog dialog = new DatePickerDialog(this,
                        pickerListener,
                        year, month, day);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            // Do Stuff
                            tv_datepicker.setText("");
                        }
                    }
                });
                return dialog;
        }
        return null;
    }

    DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            //   Log.e("selected_date...",selected_date+"");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            formatedDate = sdf.format(new Date(selectedYear - 1900, selectedMonth, selectedDay));
            Date strDate = null;
            try {
                strDate = sdf.parse(formatedDate);
                Log.e("selected_date...", strDate + "");

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Date current_date = null;
            String formattedDate = sdf.format(c.getTime());
            try {
                current_date = sdf.parse(formattedDate);
                Log.e("current date...", current_date + "");
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            month = month + 1;
            String month_str = month + "";
            if (month_str.length() < 2) {
                month_str = "0" + month;
            }
            tv_datepicker.setText(new StringBuilder().append(month_str)
                    .append("-").append(day).append("-").append(year));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("requestCode", requestCode + "");

        // PICK_IMAGE_REQUEST = requestCode;
        //   Log.e("PICK_IMAGE_REQUEST", PICK_IMAGE_REQUEST + "");

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            //       Log.e("selectedImage",selectedImage+"");
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            Log.e("picturePath....", picturePath + " " + requestCode + "");
            cursor.close();
            File imgFile = new File(picturePath); // path of your file

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imgFile);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            options.inPurgeable = true;
            options.inScaled = true;
            Bitmap bm = BitmapFactory.decodeStream(fis, null,options);
            img_frnd.setImageBitmap(bm);
    //        tv_click.setVisibility(View.GONE);
            /*try {
                BitmapFactory.Options options;
                options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;
                Bitmap bitmap1 = BitmapFactory.decodeFile(picturePath,
                        options);
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                if (bitmap1 != null) {
                    bitmap1.compress(Bitmap.CompressFormat.PNG, 100, baos1);
                    byte[] b1 = baos1.toByteArray();
                    String encodedImage = Base64.encodeToString(b1,
                            Base64.DEFAULT);
                    images.add(encodedImage.replaceAll("\\s+", ""));
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        }
    }

    public void addReminder(String title, int statrYear, int startMonth, int startDay, int startHour, int startMinut, int endYear, int endMonth, int endDay, int endHour, int endMinuts) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(statrYear, startMonth, startDay, startHour, startMinut);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(endYear, endMonth, endDay, endHour, endMinuts);
        long endMillis = endTime.getTimeInMillis();

       /* Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", startMillis);
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", endMillis);
        intent.putExtra("title", "A Test Event from android app");
        startActivity(intent);*/
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        //   long event_id = CalendarUtils.getNewEventId(getContentResolver(), null);
        //  intent.putExtra("_id", event_id);
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "summary");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);
        intent.putExtra(CalendarContract.Events.ALL_DAY, "allDayFlag");
        intent.putExtra(CalendarContract.Events.STATUS, 1);
        intent.putExtra(CalendarContract.Events.VISIBLE, 0);
        intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);
        startActivity(intent);

       /* String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        eventValues.put(CalendarContract.Events.TITLE, "OCS");
        eventValues.put(CalendarContract.Events.DESCRIPTION, "Reminder App");
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        eventValues.put(CalendarContract.Events.DTSTART, startMillis);
        eventValues.put(CalendarContract.Events.DTEND, endMillis);

        //eventValues.put(Events.RRULE, "FREQ=DAILY;COUNT=2;UNTIL="+endMillis);
      *//*  eventValues.put("eventStatus", 1);
        eventValues.put("visibility", 3);
        eventValues.put("transparency", 0);*//*
        eventValues.put(CalendarContract.Events.HAS_ALARM, 1);

        Uri eventUri = getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        *//***************** Event: Reminder(with alert) Adding reminder to event *******************//*

        String reminderUriString = "content://com.android.calendar/reminders";

        ContentValues reminderValues = new ContentValues();

        reminderValues.put("event_id", eventID);
        reminderValues.put("minutes", 1);
        reminderValues.put("method", 1);

        Uri reminderUri = getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);*/
    }

    private int ListSelectedCalendars(String eventtitle) {
        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way

            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way

            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        int result = 0;
        String projection[] = {"_id", "title"};
        Cursor cursor = getContentResolver().query(eventUri, null, null, null,
                null);

        if (cursor.moveToFirst()) {

            String calName;
            String calID;

            int nameCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calName = cursor.getString(nameCol);
                calID = cursor.getString(idCol);

                if (calName != null && calName.contains(eventtitle)) {
                    result = Integer.parseInt(calID);
                }

            } while (cursor.moveToNext());
            cursor.close();
        }

        return result;

    }

    /*private int DeleteCalendarEntry(int entryID) {
        int iNumRowsDeleted = 0;

        Uri eventsUri = Uri.parse("content://calendar/events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
        iNumRowsDeleted = getContentResolver().delete(eventUri, null, null);

        Log.i("DEBUG_TAG", "Deleted " + iNumRowsDeleted + " calendar entry.");

        return iNumRowsDeleted;
    }*/
    private int DeleteCalendarEntry(int entryID) {
        int iNumRowsDeleted = 0;

        Uri eventUri = ContentUris
                .withAppendedId(Uri.parse("content://com.android.calendar/events"), entryID);
        iNumRowsDeleted = getContentResolver().delete(eventUri, null, null);

        return iNumRowsDeleted;
    }
    /*private int UpdateCalendarEntry(int entryID) {
        int iNumRowsUpdated = 0;

        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way

            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way

            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, "test");
        values.put(CalendarContract.Events.EVENT_LOCATION, "Chennai");

        Uri updateUri = ContentUris.withAppendedId(eventUri, entryID);
        iNumRowsUpdated = getContentResolver().update(updateUri, values, null,
                null);

        return iNumRowsUpdated;
    }*/
}
