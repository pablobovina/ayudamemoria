package com.example.pbovinax.ayudamemoria;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ListaActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private final int SPEECH_RECOGNITION_CODE = 1;
    private TextView txtOutput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //preferences
        //get data from table and inflate
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String tableString = prefs.getString("table",null);
        if (tableString!=null) {
            try {
                JSONArray jsonArray = (JSONArray) new JSONArray(tableString);
                TableLayout ll = (TableLayout) findViewById(R.id.tabla1);
                for(int i=0; i < jsonArray.length(); i++) {
                    TableRow row = new TableRow(ListaActivity.this);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(lp);

                    JSONObject jsonObject = (JSONObject) new JSONObject(String.valueOf(jsonArray.getJSONObject(i)));
                    Boolean check = Boolean.parseBoolean(jsonObject.getString("tick"));
                    String severity = jsonObject.getString("severity");
                    String description = jsonObject.getString("description");
                    String date_time = jsonObject.getString("datetime");

                    CheckBox checkBox = new CheckBox(ListaActivity.this);
                    checkBox.setChecked(check);
                    TextView tv = new TextView(ListaActivity.this);
                    tv.setText(description);
                    TextView dt = new TextView(ListaActivity.this);
                    dt.setText(date_time);
                    if (severity.equals("Critical")) {
                        row.setBackgroundResource(R.color.red);
                    }
                    if (severity.equals("High")) {
                        row.setBackgroundResource(R.color.orange);
                    }
                    if (severity.equals("Medium")) {
                        row.setBackgroundResource(R.color.yellow);
                    }
                    if (severity.equals("Low")) {
                        row.setBackgroundResource(R.color.green);
                    }

                    row.addView(checkBox);
                    row.addView(tv);
                    row.addView(dt);
                    ll.addView(row);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ListaActivity.this);
                // ...Irrelevant code for customizing the buttons and title
                LayoutInflater inflater = ListaActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_item, null);
                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                Button btnMicrophone = (Button) alertDialog.findViewById(R.id.button2);
                txtOutput = (TextView) alertDialog.findViewById(R.id.editText);
                btnMicrophone.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startSpeechToText();
                    }
                });

                Button btnsubmit = (Button) alertDialog.findViewById(R.id.button);
                btnsubmit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //set row
                        TableLayout ll = (TableLayout) findViewById(R.id.tabla1);
                        TableRow row= new TableRow(ListaActivity.this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        row.setLayoutParams(lp);

                        //get severity from radiobuttons
                        Boolean critical = ((RadioButton) alertDialog.findViewById(R.id.radioButton)).isChecked();
                        Boolean high = ((RadioButton) alertDialog.findViewById(R.id.radioButton2)).isChecked();
                        Boolean medium = ((RadioButton) alertDialog.findViewById(R.id.radioButton3)).isChecked();
                        Boolean low = ((RadioButton) alertDialog.findViewById(R.id.radioButton4)).isChecked();

                        String severity = "";
                        if (critical){
                            row.setBackgroundResource(R.color.red);
                            severity = "Critical";
                        }
                        if (high){
                            row.setBackgroundResource(R.color.orange);
                            severity = "High";
                        }
                        if (medium){
                            row.setBackgroundResource(R.color.yellow);
                            severity = "Medium";
                        }
                        if (low){
                            row.setBackgroundResource(R.color.green);
                            severity = "Low";
                        }


                        CheckBox checkBox = new CheckBox(ListaActivity.this);

                        TextView tv = new TextView(ListaActivity.this);
                        EditText mEdit = (EditText) alertDialog.findViewById(R.id.editText);
                        String description = (String) mEdit.getText().toString();
                        tv.setText(description);

                        String datetime = DateFormat.getDateTimeInstance().format(new Date());
                        TextView date_time = new TextView(ListaActivity.this);
                        date_time.setText(datetime);

                        row.addView(checkBox);
                        row.addView(tv);
                        row.addView(date_time);
                        ll.addView(row);

                        //preferences
                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        //csvList.append("False"+"-"+severity+"-"+description+"-"+datetime);
                        String tabla = prefs.getString("table","[]");
                        try {
                            JSONArray jsonArray = (JSONArray) new JSONArray(tabla);
                            JSONObject elem = new JSONObject();
                            elem.put("tick","false");
                            elem.put("severity",severity);
                            elem.put("description",description);
                            elem.put("datetime",datetime);
                            jsonArray.put(elem);
                            editor.putString("table",jsonArray.toString());
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        alertDialog.cancel();

                    }
                });

            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                TableLayout layout = (TableLayout) findViewById(R.id.tabla1);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    layout.removeAllViews();
                }
                editor.putString("table",null);
                editor.commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     * */
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtOutput.setText(text);
                }
                break;
            }

        }
    }
}
