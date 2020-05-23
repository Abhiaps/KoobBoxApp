package com.example.koobboxapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
RecyclerView recycler;
ProgressDialog pd;
DetailsDatabase db;
Calendar calendar;
Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pd=new ProgressDialog(this);
        getSupportActionBar().hide();
        pd.setTitle("Reloading...");
        inititaliseSQLiteDatabaseWithDefault100Values();
        recycler=findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
        final ArrayList<String> title=new ArrayList<>();
        final ArrayList<String> subtitle=new ArrayList<>();
        final ArrayList<String> date=new ArrayList<>();
        final ArrayList<String> priority=new ArrayList<>();
        final ArrayList<String> selection=new ArrayList<>();
        final ArrayList<String> type=new ArrayList<>();
        db=new DetailsDatabase(this);
        cursor=db.allData();
        int tot=cursor.getCount();
        cursor.moveToFirst();
        for(int i=0;i<tot;i++)
        {
            title.add(cursor.getString(0));
            subtitle.add(cursor.getString(1));
            date.add(cursor.getString(2));
            priority.add(cursor.getString(3));
            selection.add(cursor.getString(4));
            type.add(cursor.getString(5));
            cursor.moveToNext();
        }
        Common.title=title;
        Common.subtitle=subtitle;
        Common.date=date;
        Common.priority=priority;
        Common.selection=selection;
        Common.type=type;
        recycler.setAdapter(new DetailsAdapter(title,subtitle,date,priority,selection,type,MainActivity.this));
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialog();
            }
        });
        findViewById(R.id.filterdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDate();
            }
        });
        findViewById(R.id.filtertype).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterType();
            }
        });
        findViewById(R.id.sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort();
            }
        });
        findViewById(R.id.deleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllDialog();
            }
        });
    }

    private void deleteAllDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Are You Sure You want to delete All Data");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db=new DetailsDatabase(MainActivity.this);
                db.cleanData();
                reloadActivity();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void reloadActivity() {
        finish();
        startActivity(getIntent());
    }

    private void inititaliseSQLiteDatabaseWithDefault100Values() {
        if (Common.startIndicator == 0) {
            Common.startIndicator = 1;
            db = new DetailsDatabase(this);
            cursor = db.allData();
            Random rand = new Random();
            if (cursor.getCount() < 100) {
                for (int i = 0; i < 100; i++) {
                    db.insertData(generateRandomString(6), generateRandomString(6), generateRandomDate(), Integer.toString(1+(rand.nextInt(100))), generateRandomString(6), generateRandomString(6));
                }
            }
        }
    }
    private void filterType() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.typerange,null);
        ImageView info=view.findViewById(R.id.image);
        info.setImageResource(R.drawable.ic_launcher_foreground);
        final EditText start=view.findViewById(R.id.newtype);

        builder.setPositiveButton("Filter By Type", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Common.typefilter=start.getText().toString();
                final ArrayList<String> title=new ArrayList<>();
                final ArrayList<String> subtitle=new ArrayList<>();
                final ArrayList<String> date=new ArrayList<>();
                final ArrayList<String> priority=new ArrayList<>();
                final ArrayList<String> selection=new ArrayList<>();
                final ArrayList<String> type=new ArrayList<>();
                db=new DetailsDatabase(MainActivity.this);
                cursor=db.allData();
                int tot=cursor.getCount();
                cursor.moveToFirst();
                for(int i1=0;i1<tot;i1++)
                {

                    if(Common.typefilter.equals(cursor.getString(5))) {
                        title.add(cursor.getString(0));
                        subtitle.add(cursor.getString(1));
                        date.add(cursor.getString(2));
                        priority.add(cursor.getString(3));
                        selection.add(cursor.getString(4));
                        type.add(cursor.getString(5));

                    }
                    cursor.moveToNext();
                }

                recycler.setAdapter(new DetailsAdapter(title,subtitle,date,priority,selection,type,MainActivity.this));
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void sort() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.sortlayout,null);
        final RadioGroup sortgroup=view.findViewById(R.id.sortgroup);
        sortgroup.clearCheck();
        sortgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rdb=(RadioButton)radioGroup.findViewById(i);
            }
        });
        builder.setPositiveButton("SHOW SORTED DATA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int selectedId=sortgroup.getCheckedRadioButtonId();
                if(selectedId==-1)
                {
                    Toast.makeText(MainActivity.this, "No Radio Button is Selected", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
                else {
                    RadioButton rdb=(RadioButton)sortgroup.findViewById(selectedId);
                    Toast.makeText(MainActivity.this, rdb.getText().toString(), Toast.LENGTH_SHORT).show();
                    sortDataAndPrint(rdb.getText().toString());
                }


            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }

    private void sortDataAndPrint(final String base) {
        pd.show();
        int n=(Common.date).size();
        if(base.compareToIgnoreCase("Date")==0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date first = null, second = null;
            for (int j = 0; j < n - 1; j++) {
                for(int i=0;i<n-j-1;i++)
                {
                try {
                    first = sdf.parse(Common.date.get(i));
                    second = sdf.parse(Common.date.get(i + 1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (first.getTime() > second.getTime()) {
                    String x = Common.date.get(i);
                    Common.date.set(i, Common.date.get(i + 1));
                    Common.date.set(i + 1, x);
                    x = Common.title.get(i);
                    Common.title.set(i, Common.title.get(i + 1));
                    Common.title.set(i + 1, x);
                    x = Common.subtitle.get(i);
                    Common.subtitle.set(i, Common.subtitle.get(i + 1));
                    Common.subtitle.set(i + 1, x);
                    x = Common.priority.get(i);
                    Common.priority.set(i, Common.priority.get(i + 1));
                    Common.priority.set(i + 1, x);
                    x = Common.type.get(i);
                    Common.type.set(i, Common.type.get(i + 1));
                    Common.type.set(i + 1, x);
                    x = Common.selection.get(i);
                    Common.selection.set(i, Common.selection.get(i + 1));
                    Common.selection.set(i + 1, x);
                }
            }
        }
        }
        else if(base.compareToIgnoreCase("Priority")==0)
        {
            for(int j=0;j<n-1;j++) {
                for(int i=0;i<n-j-1;i++)
                {
                if (Integer.parseInt(Common.priority.get(i)) > Integer.parseInt(Common.priority.get(i + 1))) {
                    String x = Common.date.get(i);
                    Common.date.set(i, Common.date.get(i + 1));
                    Common.date.set(i + 1, x);
                    x = Common.title.get(i);
                    Common.title.set(i, Common.title.get(i + 1));
                    Common.title.set(i + 1, x);
                    x = Common.subtitle.get(i);
                    Common.subtitle.set(i, Common.subtitle.get(i + 1));
                    Common.subtitle.set(i + 1, x);
                    x = Common.priority.get(i);
                    Common.priority.set(i, Common.priority.get(i + 1));
                    Common.priority.set(i + 1, x);
                    x = Common.type.get(i);
                    Common.type.set(i, Common.type.get(i + 1));
                    Common.type.set(i + 1, x);
                    x = Common.selection.get(i);
                    Common.selection.set(i, Common.selection.get(i + 1));
                    Common.selection.set(i + 1, x);
                }
            }
            }
        }
        else
        {
            for(int j=0;j<n-1;j++) {
                for(int i=0;i<n-j-1;i++)
                {
                final String x1 = Common.type.get(i);
                final String x2 = Common.type.get(i + 1);
                if (x1.compareToIgnoreCase(x2) > 0) {
                    String x = Common.date.get(i);
                    Common.date.set(i, Common.date.get(i + 1));
                    Common.date.set(i + 1, x);
                    x = Common.title.get(i);
                    Common.title.set(i, Common.title.get(i + 1));
                    Common.title.set(i + 1, x);
                    x = Common.subtitle.get(i);
                    Common.subtitle.set(i, Common.subtitle.get(i + 1));
                    Common.subtitle.set(i + 1, x);
                    x = Common.priority.get(i);
                    Common.priority.set(i, Common.priority.get(i + 1));
                    Common.priority.set(i + 1, x);
                    x = Common.type.get(i);
                    Common.type.set(i, Common.type.get(i + 1));
                    Common.type.set(i + 1, x);
                    x = Common.selection.get(i);
                    Common.selection.set(i, Common.selection.get(i + 1));
                    Common.selection.set(i + 1, x);
                }
            }
            }
        }

        recycler.setAdapter(new DetailsAdapter(Common.title,Common.subtitle,Common.date,Common.priority,Common.selection,Common.type,MainActivity.this));
        pd.dismiss();
    }

    private void filterDate() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.daterange,null);
        final Button start1=view.findViewById(R.id.start);
        final Button end1=view.findViewById(R.id.end);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            public void updateLabel() {
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                start1.setText(sdf.format(myCalendar.getTime()));
            }
        };
        start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        final Calendar myCalendar1 = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            public void updateLabel() {
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                end1.setText(sdf.format(myCalendar.getTime()));
            }
        };
        end1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        builder.setPositiveButton("Filter By Date", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Common.startfilter=start1.getText().toString();
                Common.endfilter=end1.getText().toString();
                final ArrayList<String> title=new ArrayList<>();
                final ArrayList<String> subtitle=new ArrayList<>();
                final ArrayList<String> date=new ArrayList<>();
                final ArrayList<String> priority=new ArrayList<>();
                final ArrayList<String> selection=new ArrayList<>();
                final ArrayList<String> type=new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date start = null, end = null, check = null;
                try {
                    start = sdf.parse(Common.startfilter);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    end = sdf.parse(Common.endfilter);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                db=new DetailsDatabase(MainActivity.this);
                cursor=db.allData();
                int tot=cursor.getCount();
                cursor.moveToFirst();
                for(int i1=0;i1<tot;i1++)
                {
                    try {
                        check=sdf.parse(cursor.getString(2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(check.getTime()>=start.getTime()&&check.getTime()<=end.getTime()) {
                        title.add(cursor.getString(0));
                        subtitle.add(cursor.getString(1));
                        date.add(cursor.getString(2));
                        priority.add(cursor.getString(3));
                        selection.add(cursor.getString(4));
                        type.add(cursor.getString(5));

                    }
                    cursor.moveToNext();
                }

                recycler.setAdapter(new DetailsAdapter(title,subtitle,date,priority,selection,type,MainActivity.this));

            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);
        builder.show();

    }

    private void AddDialog() {
    db=new DetailsDatabase(this);
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_layout,null);
        final EditText newtitle=view.findViewById(R.id.newtitle);
        final EditText newsubtitle=view.findViewById(R.id.newsubtitle);
        final Button newdate=view.findViewById(R.id.newdate);
        final EditText newpriority=view.findViewById(R.id.newpriority);
        final EditText newselection=view.findViewById(R.id.newselection);
        final EditText newtype=view.findViewById(R.id.newtype);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
            public void updateLabel() {
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                newdate.setText(sdf.format(myCalendar.getTime()));
            }
        };
        newdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        builder.setPositiveButton("Add Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pd.show();
                boolean d=db.insertData(newtitle.getText().toString(),newsubtitle.getText().toString(),newdate.getText().toString(),
                        newpriority.getText().toString(),newselection.getText().toString(),newtype.getText().toString());

                if(d==true)
                Toast.makeText(MainActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                else Toast.makeText(MainActivity.this, "Data Not Added", Toast.LENGTH_SHORT).show();
                reloadActivity();
                pd.dismiss();
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setView(view);
        builder.show();
    }
    static String generateRandomString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
    static String generateRandomDate()
    {
        Random rand=new Random();
        int date=1+(rand.nextInt(30));
        int month=1+(rand.nextInt(12));
        int year=1900+(rand.nextInt(100));
        String res="";
        if(date<10)
        {
            res="0";
            res+=Integer.toString(date);
        }
        else res+=Integer.toString(date);
        res+="/";
        if(month<10)
        {
            res+="0";
            res+=Integer.toString(month);
        }
        else res+=Integer.toString(month);
        res+="/";
        res+=Integer.toString(year);
        return res;
    }
}
