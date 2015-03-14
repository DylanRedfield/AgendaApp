package com.dylanredfield.agendaapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dylanredfield.agendaapp2.R;
import com.software.shell.fab.ActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClassActivity extends ActionBarActivity {
    private ListView mClassInfoListView;
    private ListView mAssignmentsListView;
    private ArrayAdapter<String> mClassInfoAdapter;
    private AssignmentAdapter mAssignmentsAdapter;
    private ActionButton mButtonClass;
    private ActionButton mButtonPicture;
    private ActionButton mButtonText;
    private String mCurrentPhotoPath;
    private boolean showFlag;
    private int index;
    private ActionBar mActionBar;
    private Window mWindow;
    public static int REQUEST_IMAGE_CAPTURE_CLASS = 2;
    public static String EXTRA_INT_ASSIGNMENT_POSTITION = "com.dylanredfield.agendaapp.int_assignment_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO wire edit info button. Should bring to activity similar to new
        // class
        // Will make a new schoolclass with new info, and replace current class
        // in list
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // Gets index extra of class
        index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);

        // Creates AssignmentAdapter, ect
        instaniateAssignmentAdapter();


        // Instaniates ActionButtons and sets properties
        declareActionButtons();


        // Adds all listeners
        setListeners();

        // Sets statusbar and actionbar
        setBars();

        // Registers for context menu for Assignments
        // TODO make/rename to add context menu for InfoList
        registerForContextMenu(mAssignmentsListView);
    }

    public void instaniateAssignmentAdapter() {
        mAssignmentsListView = (ListView) findViewById(R.id.assignments_list);
        ClassList.getInstance(getApplicationContext()).getList().get(index)
                .sortAssignmentsByCompleted();
        mAssignmentsAdapter = new AssignmentAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                ClassList.getInstance(getApplicationContext()).getList()
                        .get(index).getAssignments());
        mAssignmentsListView.setAdapter(mAssignmentsAdapter);
        mAssignmentsListView.setEmptyView(findViewById(R.id.empty_list));
    }

    public void declareActionButtons() {
        mButtonClass = (ActionButton) findViewById(R.id.action_button);

        // Call to second helper method that sets properties
        makeActionButton(mButtonClass, R.drawable.ic_note_add_white_36dp);

        mButtonPicture = (ActionButton) findViewById(R.id.action_button_picture);
        makeActionButton(mButtonPicture, R.drawable.ic_file_image_box_white_48dp);

        mButtonText = (ActionButton) findViewById(R.id.action_button_assignment);
        makeActionButton(mButtonText, R.drawable.ic_note_add_white_36dp);
    }

    public ActionButton makeActionButton(ActionButton ab, int drawable) {
        // Creates, and sets ActionButtons
        ab.setButtonColor(getResources().getColor(R.color.red_500));
        ab.setButtonColorPressed(getResources().getColor(R.color.red_900));
        ab.setImageDrawable(getResources().getDrawable(drawable));
        ab.setButtonColorPressed(getResources().getColor(R.color.red_900));

        return ab;
    }

    public void setListeners() {
        mButtonClass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!showFlag) {
                    mButtonPicture.setVisibility(View.VISIBLE);
                    mButtonText.setVisibility(View.VISIBLE);

                    mButtonClass.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_close_white_48dp));
                    showFlag = true;
                } else {
                    mButtonPicture.setVisibility(View.INVISIBLE);
                    mButtonText.setVisibility(View.INVISIBLE);
                    mButtonClass.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_note_add_white_36dp));
                    showFlag = false;
                }


            }
        });
        mButtonPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        mButtonText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // intent to NewClassActivity
                if (ClassList.getInstance(getApplicationContext()).getList().size() > 0) {
                    Intent i = new Intent(getApplicationContext(),
                            AddAssignmentActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Create a class first by clicking " +
                                    "plus above",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onItemClickListener for assignmentsList
        mAssignmentsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getApplicationContext(),
                        AssignmentActivity.class);
                i.putExtra(EXTRA_INT_ASSIGNMENT_POSTITION, position);
                i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);
                startActivity(i);

            }
        });
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBars() {
        // Changes ActionBar color
        mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_500)));

        // if able to sets statusbar to dark red
        if (21 <= Build.VERSION.SDK_INT) {
            mWindow = this.getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mWindow.setStatusBarColor(this.getResources().getColor(R.color.red_700));
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    private void dispatchTakePictureIntent() {

        // Makes intent to take pic
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                //eror
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CLASS);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_CLASS && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();

            Intent i = new Intent(getApplicationContext(), AddAssignmentActivity.class);

            i.putExtra("TEST", mCurrentPhotoPath);
            startActivity(i);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "1mind_" + timeStamp + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), imageFileName);
        mCurrentPhotoPath = photo.getAbsolutePath();
        return photo;
    }


    @Override
    protected void onResume() {
        super.onResume();

        // recreates adapters to update them
        // TODO check to see if bundle is better for this
        index = getIntent().getIntExtra(MainActivity.EXTRA_INT_POSTITION, 0);
        /*
         * mClassInfoListView = (ListView) findViewById(R.id.classinfo_list);
		 * makeListView(mClassInfoListView, mClassInfoAdapter, ClassList
		 * .getInstance(getApplicationContext()).getList().get(index)
		 * .makeList());
		 */
        instaniateAssignmentAdapter();

    }

    public void makeListView(ListView listView, ArrayAdapter<String> adapter,
                             ArrayList<String> list) {

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);

        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Creates contextMenu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assignment_context, menu);
    }

    public void updateDatabase() {
        // Deletes all information in the database
        DatabaseHandler.getInstance(getApplicationContext()).deleteAllClasses();

        // Adds all classes from ArrayList back into database
        DatabaseHandler.getInstance(getApplicationContext()).addAllClasses(
                ClassList.getInstance(getApplicationContext()).getList());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void emptyPress(View v) {
        Intent i = new Intent(getApplicationContext(),
                AddAssignmentActivity.class);
        i.putExtra(MainActivity.EXTRA_INT_POSTITION, index);

        startActivity(i);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        switch (item.getItemId()) {
            // delete assignment
            case R.id.delete_assignment:

                ClassList.getInstance(getApplicationContext()).getList().get(index)
                        .getAssignments().remove(info.position);
                // Reinstaniate the list
                instaniateAssignmentAdapter();
                updateDatabase();
                return true;
            // Rename class
            case R.id.rename_assignment:
                // Makes int the value of index spot
            default:
                return true;
        }

    }



    public class AssignmentAdapter extends ArrayAdapter<Assignment> {

        private ArrayList<Assignment> mList;
        private TextView titleTextView;
        private CheckBox isCompletedCheck;
        private TextView assignedDate;

        public AssignmentAdapter(Context context, int resource,
                                 int textViewResourceId, ArrayList<Assignment> objects) {
            super(context, resource, textViewResourceId, objects);
            mList = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Save db on assignemnt add

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.assignment_row, null);
            }
            titleTextView = (TextView) convertView
                    .findViewById(R.id.assignment_text);
            titleTextView.setText(mList.get(position).getTitle());
            assignedDate = (TextView) convertView.findViewById(R.id.assigned_text);
            if (mList.get(position).getDateAssigned() != null) {
                assignedDate.setText(calendarToString(mList.get(position).getDateAssigned()));
            } else {
                assignedDate.setVisibility(0);
            }

            isCompletedCheck = (CheckBox) convertView
                    .findViewById(R.id.is_completed_check);
            isCompletedCheck.setChecked(mList.get(position).isCompleted());

            // Sets cb tag as position
            isCompletedCheck.setTag(position);

            isCompletedCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Sets coresponding mCompleted to if the box is checked
                    ClassList.getInstance(getApplicationContext()).getList()
                            .get(index).getAssignments().get((int) v.getTag())
                            .setCompleted(((CheckBox) v).isChecked());

                    updateDatabase();

                }
            });

            return convertView;

        }


        public String calendarToString(Calendar c) {
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            return sdf.format(c.getTime());
        }

        public CheckBox getCompletedCheck() {
            return isCompletedCheck;
        }

    }

}
