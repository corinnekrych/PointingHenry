package org.feedhenry.pointinghenry;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHCloudRequest;

import org.feedhenry.pointinghenry.model.Session;
import org.feedhenry.pointinghenry.model.User;
import org.json.fh.JSONArray;
import org.json.fh.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    List<Session> sessions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.sessionList);
        displaySessions(listView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }

    private void displaySessions(final ListView listView) {
        try {

            FH.init(this, new FHActCallback() {
                @Override
                public void success(FHResponse fhResponse) {
                    try {
                        FHCloudRequest request = FH.buildCloudRequest("poker", "GET", null, null);
                        request.executeAsync(new FHActCallback() {
                            @Override
                            public void success(FHResponse fhResponse) {
                                JSONArray objects = fhResponse.getArray();
                                MainActivity.this.sessions = new ArrayList<Session>();
                                for (int i =0; i < objects.length(); i++) {
                                    JSONObject object = (JSONObject)objects.get(i);
                                    String name = object.getString("Name");
                                    String userName = object.getJSONObject("CreatedBy").getString("Name");
                                    User user = new User(userName);
                                    MainActivity.this.sessions.add(new Session(name, user));

                                }
                                System.out.println("REPSONSE" + objects);
                                String raw = fhResponse.getRawResponse();

                                final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, MainActivity.this.sessions);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, final View view,
                                                            int position, long id) {
                                        final Session item = (Session) parent.getItemAtPosition(position);
                                        view.animate().setDuration(2000).alpha(0)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sessions.remove(item);
                                                        adapter.notifyDataSetChanged();
                                                        view.setAlpha(1);
                                                    }
                                                });
                                    }
                                });
                            }

                            @Override
                            public void fail(FHResponse fhResponse) {
                                Log.d(TAG, "cloud call - fail");
                                Log.e(TAG, fhResponse.getErrorMessage(), fhResponse.getError());
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e.getCause());
                    }
                }

                @Override
                public void fail(FHResponse fhResponse) {
                    Log.d(TAG, "init - fail");
                    Log.e(TAG, fhResponse.getErrorMessage(), fhResponse.getError());
                }
            });

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.getCause());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
