package net.ddns.morigg.homesweethomeapp.dialog_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.LoginActivity;
import net.ddns.morigg.homesweethomeapp.recycleradapter.MultiSpinnerSearch;
import net.ddns.morigg.homesweethomeapp.structures.KeyPairBoolData;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerFriends;
import net.ddns.morigg.homesweethomeapp.utilities.PackageManagerUtils;
import net.ddns.morigg.homesweethomeapp.utilities.PermissionUtils;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.google.common.reflect.Reflection.getPackageName;
import static net.ddns.morigg.homesweethomeapp.activities.ExpenseActivity.mExpenseProgress;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

/**
 * Created by MoriartyGG on 27.05.2018.
 */

@SuppressLint("ValidFragment")
public class ExpenseAdd extends DialogFragment {

    public interface AddExpenseDialogListener{

        void onSaveButtonClick(DialogFragment dialog);
        //void onCancelButtonClick(DialogFragment dialog);
    }

    ImageView ImgCamera;
    int type;
    EditText mTxtTitle, mTxtCost;
    private static EditText mTxtContent;
    TextView mTxtHint,mTxtDate,mTxtAuthor;
    Spinner spinner;
    AddExpenseDialogListener addExpenseDialog;
    Context context;
    Activity activity;
    MultiSpinnerSearch searchMultiSpinnerUnlimited;
    DBHandlerFriends database;
    List<UserInformation> friends;
    List<KeyPairBoolData> listArray0;

    //boolean isMainUserNeed;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetPasswordDialogListener so we can send events to the host
            addExpenseDialog = (AddExpenseDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddStudentDialogListener");
        }
    }

    private static ProgressDialog progressDialog;

    public String CLOUD_VISION_API_KEY = "";
    String ANDROID_CERT_HEADER = "";
    String ANDROID_PACKAGE_HEADER = "";
    private static final int MAX_DIMENSION = 1200;

    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final String FILE_NAME = "temp.jpg";
    static final String TAG = "ExpensePhoto";
    private boolean firstLoop = true;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //type = 1;
        CLOUD_VISION_API_KEY = getString(R.string.CLOUD_VISION_API_KEY);
        ANDROID_CERT_HEADER = getString(R.string.ANDROID_CERT_HEADER);
        ANDROID_PACKAGE_HEADER = getString(R.string.ANDROID_PACKAGE_HEADER);
        //isMainUserNeed = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_expense,null);



        mTxtContent = (EditText) view.findViewById(R.id.expensedialog_content);
        ImgCamera = view.findViewById(R.id.expensedialog_camera);
        ImgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        Bundle mArgs = getArguments();
        final String hint = mArgs.getString("hint");
        mTxtHint = (TextView) view.findViewById(R.id.expensedialog_hint);
        mTxtHint.setText(hint);

        searchMultiSpinnerUnlimited = (MultiSpinnerSearch) view.findViewById(R.id.expense_userList);

        /*KeyPairBoolData h = new KeyPairBoolData();
        h.setId(mainUser.USER_ID);
        h.setName(mainUser.FIRST_NAME + " " + mainUser.LAST_NAME);
        h.setSelected(false);
        listArray0.add(h);*/

        {
            database = new DBHandlerFriends(context);
            friends = database.getAllInformation();
            listArray0 = new ArrayList<>();
            for(int x = 0;x<friends.size();x++)
            {
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(friends.get(x).USER_ID);
                h.setName(friends.get(x).FIRST_NAME + " " + friends.get(x).LAST_NAME);
                h.setSelected(false);
                listArray0.add(h);
            }

            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(mainUser.USER_ID);
            h.setName(mainUser.FIRST_NAME + " " + mainUser.LAST_NAME);
            h.setSelected(false);
            listArray0.add(h);
        }


        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new MultiSpinnerSearch.SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        //Log.i("Selected", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        }, getString(R.string.Expense_participants));


        spinner = (Spinner) view.findViewById(R.id.expensedialog_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.expense_actions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(!firstLoop)
                {
                    int position = spinner.getSelectedItemPosition();

                    database = new DBHandlerFriends(context);
                    friends = database.getAllInformation();
                    listArray0 = new ArrayList<>();
                    for(int x = 0;x<friends.size();x++)
                    {
                        KeyPairBoolData h = new KeyPairBoolData();
                        h.setId(friends.get(x).USER_ID);
                        h.setName(friends.get(x).FIRST_NAME + " " + friends.get(x).LAST_NAME);
                        h.setSelected(false);
                        listArray0.add(h);
                    }

                    if(position == 0 || position == 1 || position == 2 || position == 4)
                    {
                        KeyPairBoolData h = new KeyPairBoolData();
                        h.setId(mainUser.USER_ID);
                        h.setName(mainUser.FIRST_NAME + " " + mainUser.LAST_NAME);
                        h.setSelected(false);
                        listArray0.add(h);

                        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new MultiSpinnerSearch.SpinnerListener() {
                            @Override
                            public void onItemsSelected(List<KeyPairBoolData> items) {

                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).isSelected()) {
                                        //Log.i("Selected", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                    }
                                }
                            }
                        }, getString(R.string.Expense_participants));

                        searchMultiSpinnerUnlimited.selected = 0;

                        searchMultiSpinnerUnlimited.setLimit(-1, new MultiSpinnerSearch.LimitExceedListener() {
                            @Override
                            public void onLimitListener(KeyPairBoolData data) {
                                //Toast.makeText(context, "Limit exceed ", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new MultiSpinnerSearch.SpinnerListener() {
                            @Override
                            public void onItemsSelected(List<KeyPairBoolData> items) {

                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).isSelected()) {
                                        //Log.i("Selected", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                    }
                                }
                            }
                        }, getString(R.string.Expense_participants));

                        searchMultiSpinnerUnlimited.selected = 0;

                        searchMultiSpinnerUnlimited.setLimit(1, new MultiSpinnerSearch.LimitExceedListener() {
                            @Override
                            public void onLimitListener(KeyPairBoolData data) {
                                //Toast.makeText(context, "Limit exceed ", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
                else
                {
                    firstLoop = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        if(Objects.equals(hint, getString(R.string.Expense_AddHint)))
        {
            builder.setView(view)
                    .setNegativeButton(getString(R.string.Expense_DialogCancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setPositiveButton( getString(R.string.Expense_DialogAdd) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            addExpenseDialog.onSaveButtonClick(ExpenseAdd.this);
                        }
                    });
        }
        else if(Objects.equals(hint, getString(R.string.Expense_EditHint)))
        {


            mTxtTitle = (EditText) view.findViewById(R.id.expensedialog_title);
            mTxtCost = (EditText) view.findViewById(R.id.expensedialog_cost);
            mTxtDate = (TextView) view.findViewById(R.id.expensedialog_date);
            mTxtAuthor = (TextView) view.findViewById(R.id.expensedialog_author);


            Date date = new Date(Long.valueOf(mArgs.getString("date")));
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            Log.i("TIME :",dateFormat.format(date));

            mTxtAuthor.setText(mArgs.getString("author"));
            mTxtTitle.setText(mArgs.getString("title"));

            mTxtDate.setText(dateFormat.format(date));
            mTxtContent.setText(mArgs.getString("content"));
            //Log.i("participantsString ",mArgs.getString("participants"));
            String[] participantsString = (mArgs.getString("participants")).split(",");
            List<Integer> participants = new ArrayList<>();
            int i = 0;
            for(String item : participantsString)
            {
                participants.add(Integer.valueOf(item));
            }
            Double cost = Double.valueOf(mArgs.getString("cost"));
            cost *= participants.size();
            mTxtCost.setText((String.valueOf(cost)));

            mTxtAuthor.setVisibility(View.VISIBLE);
            mTxtDate.setVisibility(View.VISIBLE);

            String typeString = (mArgs.getString("e_type"));
            String[] items = getResources().getStringArray(R.array.expense_actions);
            for(int x = 0;x < items.length; x++)
                if(Objects.equals(items[x], typeString))
                    type = x;
            Log.i("type ",type + " "+ typeString);
            firstLoop = true;
            spinner.setSelection(type);



            listArray0 = new ArrayList<>();
            database = new DBHandlerFriends(context);
            friends = database.getAllInformation();
            for(int x = 0;x<friends.size();x++)
            {
                int friendId = friends.get(x).USER_ID;
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(friendId);
                h.setName(friends.get(x).FIRST_NAME + " " + friends.get(x).LAST_NAME);
                h.setSelected(false);
                for(int j = 0; j < participants.size();j++)
                {
                    //Log.i("friend " + j +"id", String.valueOf(participants.get(j)));
                    if(friendId == participants.get(j))
                        h.setSelected(true);
                }
                listArray0.add(h);
            }

            if(type == 0 || type == 1 || type == 2 || type == 4)
            {
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(mainUser.USER_ID);
                h.setName(mainUser.FIRST_NAME + " " + mainUser.LAST_NAME);
                h.setSelected(false);
                for(int j = 0; j < participants.size();j++)
                {
                    //Log.i("friend " + j +"id", String.valueOf(participants.get(j)));
                    if(mainUser.USER_ID == participants.get(j))
                        h.setSelected(true);
                }
                listArray0.add(h);

                searchMultiSpinnerUnlimited.setLimit(-1, new MultiSpinnerSearch.LimitExceedListener() {
                    @Override
                    public void onLimitListener(KeyPairBoolData data) {
                        //Toast.makeText(context, "Limit exceed ", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else
            {
                searchMultiSpinnerUnlimited.setLimit(1, new MultiSpinnerSearch.LimitExceedListener() {
                    @Override
                    public void onLimitListener(KeyPairBoolData data) {
                        //Toast.makeText(context, "Limit exceed ", Toast.LENGTH_LONG).show();
                    }
                });
            }

            /*for(KeyPairBoolData oneItem : listArray0)
            {
                Log.i("KeyPair",oneItem.getName() + oneItem.getId() + oneItem.isSelected());
            }*/

            searchMultiSpinnerUnlimited.selected = participants.size();
            searchMultiSpinnerUnlimited.setItems(listArray0, -1, new MultiSpinnerSearch.SpinnerListener() {
                @Override
                public void onItemsSelected(List<KeyPairBoolData> items) {

                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).isSelected()) {

                            Log.i("Selected", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        }
                    }
                }
            }, getString(R.string.Expense_participants));






            boolean author = Objects.equals(mainUser.USER_NAME, mArgs.getString("author"));
            if(!author)
            {
                mTxtHint.setText(getString(R.string.Expense_ShowOnly));
                mTxtTitle.setFocusableInTouchMode(false);
                mTxtTitle.setFocusable(false);
                mTxtContent.setFocusableInTouchMode(false);
                mTxtContent.setFocusable(false);
                mTxtCost.setFocusableInTouchMode(false);
                mTxtCost.setFocusable(false);

                spinner.setEnabled(false);


                searchMultiSpinnerUnlimited.setEnabled(false);
                builder.setView(view)
                        .setNegativeButton("Çıkış", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
            }
            else
            {
                builder.setView(view)
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .setPositiveButton("Düzenle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                addExpenseDialog.onSaveButtonClick(ExpenseAdd.this);
                            }
                        });
            }


            //mArgs.putString("e_type",item.E_TYPE);
            //mArgs.putString("id", String.valueOf(item.ID));
        }





        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        return builder.create();
    }

    @SuppressLint("ValidFragment")
    public ExpenseAdd(Context context,Activity activity)
    {
        this.context = context;
        this.activity = activity;
    }


    public void startCamera() {
        if (PermissionUtils.requestPermission(
                activity,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(context, activity.getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }


    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri),
                                MAX_DIMENSION);

                callCloudVision(bitmap);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(context, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(context, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        //mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway


        progressDialog = new ProgressDialog(activity,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.Expense_ImageToText));
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = context.getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(context.getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                /** (done) TODO Select GoogleVision API option */
                labelDetection.setType("DOCUMENT_TEXT_DETECTION");
                //labelDetection.setType("TEXT_DETECTION");

                //labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<ExpenseAdd> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(ExpenseAdd activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            ExpenseAdd activity = mActivityWeakReference.get();
            if (activity != null
                //&& !activity.isFinishing()
                    ) {
                //TODO expense to textbox

                mTxtContent.setText(result);
                progressDialog.dismiss();
            }
        }
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder();
        //new StringBuilder("I found these things:\n\n");

        /*List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing");
        }*/

        final TextAnnotation text = response.getResponses().get(0).getFullTextAnnotation();
        if(text != null)
            message.append("\n" + text.getText());

        return message.toString();
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}
