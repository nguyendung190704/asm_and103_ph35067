package com.example.restapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.restapi.Adapter.SinhVienAdapter;
import com.example.restapi.Api.ApiService;
import com.example.restapi.Modal.SinhVien;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;


public class Home extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    List<SinhVien> list = new ArrayList<>();

    FloatingActionButton fltadd;
    ImageView imgAvatarSV;
    EditText edtSearch;
    private Uri mUri;
    EditText edtAvatar;

    public RecyclerView rcvSV ;
    public SinhVienAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fltadd = findViewById(R.id.fltadd);
        rcvSV = findViewById(R.id.rcvSV);
        edtSearch = findViewById(R.id.ed_search);

        // Load data initially
        loadData();

        // Set up search action listener
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Call search function when text changes
                String keyword = charSequence.toString().trim();
                searchStudents(keyword);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No implementation needed
            }
        });
        findViewById(R.id.student_tang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortStudentsByPointAscending();
            }
        });
        findViewById(R.id.student_giam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortStudentsByPointDescending();
            }
        });



        fltadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(Home.this,new SinhVien(),1,list);
            }
        });
    }

    private void searchStudents(String keyword) {
        Call<List<SinhVien>> call = ApiService.apiService.searchStudents(keyword);
        call.enqueue(new Callback<List<SinhVien>>() {
            @Override
            public void onResponse(Call<List<SinhVien>> call, Response<List<SinhVien>> response) {
                if (response.isSuccessful()) {
                    List<SinhVien> searchResults = response.body();
                    if (searchResults != null) {
                        updateRecyclerView(searchResults);
                    }
                } else {
                    Toast.makeText(Home.this, "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SinhVien>> call, Throwable t) {
                Toast.makeText(Home.this, "Search failed loi", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateRecyclerView(List<SinhVien> searchResults) {
        adapter = new SinhVienAdapter(Home.this, searchResults);
        rcvSV.setLayoutManager(new LinearLayoutManager(Home.this));
        rcvSV.setAdapter(adapter);
    }
    public void loadData() {
        Call<List<SinhVien>> call = ApiService.apiService.getData();

        call.enqueue(new Callback<List<SinhVien>>() {
            @Override
            public void onResponse(Call<List<SinhVien>> call, Response<List<SinhVien>> response) {
                if (response.isSuccessful()) {
                    List<SinhVien> responseData = response.body();
                    if (responseData != null) {
                        list = responseData;
                        adapter = new SinhVienAdapter(Home.this, list);
                        rcvSV.setLayoutManager(new LinearLayoutManager(Home.this));
                        rcvSV.setAdapter(adapter);
                    } else {
                        Toast.makeText(Home.this, "Response body is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Home.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SinhVien>> call, Throwable t) {
                Toast.makeText(Home.this, "Failed to fetch data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }










    public void showDialog (Context context, SinhVien sinhVien, Integer type, List<SinhVien> list){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflater= ((Activity) context).getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_add_sinhvien,null);
        builder.setView(view);
        Dialog dialog=builder.create();
        dialog.show();

        EditText edtMaSV = view.findViewById(R.id.edtMaSV);
        EditText edtNameSV = view.findViewById(R.id.edtNameSV);
        EditText edtDiemTB = view.findViewById(R.id.edtDiemTB);
        edtAvatar = view.findViewById(R.id.edtAvatar);
        imgAvatarSV = view.findViewById(R.id.imgAvatarSV);
        Button btnChonAnh =view.findViewById(R.id.btnChonAnh);
        Button btnSave =view.findViewById(R.id.btnSave);
        Button btnBack = view.findViewById(R.id.btnBack);

        if (type == 0){
            edtMaSV.setText(sinhVien.getMasv());
            edtNameSV.setText(sinhVien.getName());
            edtDiemTB.setText(sinhVien.getPoint()+"");
            edtAvatar.setText(sinhVien.getAvatar());
            Glide.with(view).load(sinhVien.getAvatar()).into(imgAvatarSV);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String masv = edtMaSV.getText().toString().trim();
                String name = edtNameSV.getText().toString().trim();
                String diemTB = edtDiemTB.getText().toString();
                String avatar = edtAvatar.getText().toString().trim();
                if (masv.isEmpty() || name.isEmpty()|| diemTB.isEmpty()){
                    Toast.makeText(context, "Không được bỏ trống", Toast.LENGTH_SHORT).show();
                } else if (!isDouble(diemTB)) {
                    Toast.makeText(context, "Điểm trung bình phải là số", Toast.LENGTH_SHORT).show();
                } else {
                    Double point = Double.parseDouble(diemTB);
                    if (point < 0 || point > 10){
                        Toast.makeText(context, "Điểm phải từ 0-10", Toast.LENGTH_SHORT).show();
                    }else {
                        SinhVien sv = new SinhVien(masv,name,point,avatar);

                        Call<SinhVien> call = ApiService.apiService.addStudent(sv);

                        if (type == 0){
                            call = ApiService.apiService.updateStudent(sinhVien.get_id(), sv);
                        }

                        call.enqueue(new Callback<SinhVien>() {
                            @Override
                            public void onResponse(Call<SinhVien> call, Response<SinhVien> response) {
                                if (response.isSuccessful()){
                                    String msg = "Add success";
                                    if (type == 0){
                                        msg = "Update success";
                                    }
                                    loadData();
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<SinhVien> call, Throwable t) {
                                String msg = "Add fail";
                                if (type == 0){
                                    msg = "update fail";
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        if (type == 1){
            btnChonAnh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermission();
                }
            });
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openImagePicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(Home.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TedPermission.create()
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("\n" +
                            "Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\n\nVui lòng bật quyền tại [Cài đặt] > [Quyền]")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();
        } else {
            openImagePicker();
        }
    }


    private ActivityResultLauncher<Intent> mActivityRequestLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data == null){
                            return;
                        }

                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgAvatarSV.setImageBitmap(bitmap);
                            String imagePath = getImagePath(uri);
                            if (imagePath != null) {
                                edtAvatar.setText(imagePath);
                            }
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });


    public void openImagePicker(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mActivityRequestLauncher.launch(intent);
    }
    private String getImagePath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        } else {
            return null;
        }
    }
    private void sortStudentsByPointAscending() {
        Call<List<SinhVien>> call = ApiService.apiService.getStudentsSortedByPointAscending();
        call.enqueue(new Callback<List<SinhVien>>() {
            @Override
            public void onResponse(Call<List<SinhVien>> call, Response<List<SinhVien>> response) {
                if (response.isSuccessful()) {
                    List<SinhVien> sortedStudents = response.body();
                    if (sortedStudents != null) {
                        updateRecyclerView(sortedStudents);
                    }
                } else {
                    Toast.makeText(Home.this, "Sorting failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SinhVien>> call, Throwable t) {
                Toast.makeText(Home.this, "Sorting failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortStudentsByPointDescending() {
        Call<List<SinhVien>> call = ApiService.apiService.getStudentsSortedByPointDescending();
        call.enqueue(new Callback<List<SinhVien>>() {
            @Override
            public void onResponse(Call<List<SinhVien>> call, Response<List<SinhVien>> response) {
                if (response.isSuccessful()) {
                    List<SinhVien> sortedStudents = response.body();
                    if (sortedStudents != null) {
                        updateRecyclerView(sortedStudents);
                    }
                } else {
                    Toast.makeText(Home.this, "Sorting failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SinhVien>> call, Throwable t) {
                Toast.makeText(Home.this, "Sorting failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}