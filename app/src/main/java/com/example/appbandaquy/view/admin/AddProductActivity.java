package com.example.appbandaquy.view.admin;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appbandaquy.R;
import com.example.appbandaquy.Service.APIUltil;
import com.example.appbandaquy.Service.CheckConnection;
import com.example.appbandaquy.databinding.ActivityAddProductBinding;

import com.example.appbandaquy.view.adapter.AdapterSanPham;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    ActivityAddProductBinding binding;
    private String tensp, giasp, motasp, loaisp;
    int Image = 123;
    String realpath = "";
    File file;
    String file_path;
    AdapterSanPham adapterSanPham;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product);
        initView();

        CheckConnect();
    }

    private void CheckConnect() {
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            Listenner();
        } else {
            CheckConnection.showToast_short(getApplicationContext(), "Kết Nối Thất Bại");
        }
    }

    private void initView() {

        binding.toolbar.setTitle("Thêm Sản Phẩm");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Listenner() {
        binding.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Image);
            }
        });
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tensp = binding.edAddname.getText().toString();
                giasp = binding.edAddPrice.getText().toString();
                motasp = binding.edAddDetail.getText().toString();
                loaisp = binding.edAddType.getText().toString();

                file = new File(realpath);
                file_path = file.getAbsolutePath();
                Log.d("///", file.getAbsolutePath());
                String[] mangtenfile = file_path.split("\\.");
                if (tensp.isEmpty() ||  tensp.length()<=5 ) {
                    Toast.makeText(getApplicationContext(), "Tên sản phẩm ít nhất phải có 5 kí tự", Toast.LENGTH_SHORT).show();
                }
                else if (giasp.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nhập giá sản phẩm", Toast.LENGTH_SHORT).show();
                }
                else if (motasp.length()<=10) {
                    Toast.makeText(getApplicationContext(), "Mô tả sản phẩm ít nhất phải có 10 kí tự!", Toast.LENGTH_SHORT).show();
                }
                else if (loaisp.length()<=5) {
                    Toast.makeText(getApplicationContext(), "Loại sản phẩm ít nhất phải có 5 kí tự", Toast.LENGTH_SHORT).show();
                }
                else if ( binding.imgAdd.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Mời bạn chọn ảnh!", Toast.LENGTH_SHORT).show();
                }else {



                    file_path = mangtenfile[0] + System.currentTimeMillis() + "." + mangtenfile[0];


                    final RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("uploaded_file", file_path, requestBody);

                    APIUltil.getData().UploadPhot(part).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response != null) {
                                String message = response.body();
                                if (message.length() > 0) {
                                    APIUltil.getData().insertProduct(tensp, giasp, APIUltil.baseUrl + "image/" + message, motasp, loaisp).enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            String result = response.body();
                                            if (result.equals("Success")) {
                                                Toast.makeText(getApplicationContext(), "Thêm Thành công", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(AddProductActivity.this, AdminActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(i);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.d("failed", t.getMessage());
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Image && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            realpath = getRealPathFromURI(uri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                binding.imgAdd.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }
}
