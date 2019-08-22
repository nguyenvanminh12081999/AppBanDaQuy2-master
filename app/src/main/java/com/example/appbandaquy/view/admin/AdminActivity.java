package com.example.appbandaquy.view.admin;

import android.Manifest;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.databinding.DataBindingUtil;

import android.os.Build;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.appbandaquy.R;
import com.example.appbandaquy.Service.APIUltil;
import com.example.appbandaquy.Service.CheckConnection;
import com.example.appbandaquy.Service.DataClient;
import com.example.appbandaquy.databinding.ActivityAdminBinding;


import com.example.appbandaquy.databinding.DialogDeleteProductBinding;
import com.example.appbandaquy.model.home.SanPham;


import com.example.appbandaquy.presenter.home.OnItemListener;
import com.example.appbandaquy.view.adapter.AdapterSanPham;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdminActivity extends AppCompatActivity {
    ActivityAdminBinding binding;

    DataClient dataClient;
    AdapterSanPham adapterSanPham;
    ArrayList<SanPham> sanPhams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin);

        initAdapter();


        binding.toolbar2.setTitle(R.string.danhsach);
        setSupportActionBar(binding.toolbar2);


        CheckConnect();
    }

    private void CheckConnect(){
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            GetProduct();
            AddProduct();
        } else {
            CheckConnection.showToast_short(getApplicationContext(), "Kết Nối Thất Bại");
        }
    }

    public void initAdapter() {
        binding.rycyclerview.setLayoutManager(new LinearLayoutManager(AdminActivity.this));
        binding.rycyclerview.setHasFixedSize(true);
    }

    public void GetProduct() {


        dataClient = APIUltil.getData();
        Call<List<SanPham>> listCall = dataClient.getList();
        listCall.enqueue(new Callback<List<SanPham>>() {
            @Override
            public void onResponse(Call<List<SanPham>> call, Response<List<SanPham>> response) {
                sanPhams = (ArrayList<SanPham>) response.body();
                adapterSanPham = new AdapterSanPham(sanPhams, AdminActivity.this, new OnItemListener() {
                    @Override
                    public void OnItemListener(int position) {
                        Intent intent = new Intent(getApplicationContext(), EditProductActivity.class);
                        intent.putExtra("editsanpham", sanPhams.get(position));
                        startActivity(intent);
                    }

                    @Override
                    public void OnLongClickItem(int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                        DialogDeleteProductBinding binding1 = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_delete_product,null,false);
                        builder.setView(binding1.getRoot());
                        final Dialog dialog = builder.create();
                        dialog.show();

                        final SanPham sanPham = sanPhams.get(position);

                        binding1.button6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Map<String,String> map =  new HashMap<>();
                                map.put("id", String.valueOf(sanPham.getId()));

                                APIUltil.getData().DeleteProduct(map).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        String result = response.body();
                                        if(result.equals("success")){
                                            Toast.makeText(getApplicationContext(), "Xóa Thành công", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(AdminActivity.this,AdminActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                            finish();
                                            dialog.dismiss();
                                            adapterSanPham.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });
                            }
                        });
                        binding1.button7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                binding.rycyclerview.setAdapter(adapterSanPham);
            }

            @Override
            public void onFailure(Call<List<SanPham>> call, Throwable t) {
                Log.d("F", t.getMessage());
            }
        });

    }

    public void AddProduct() {
        binding.floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, AddProductActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.receipt:
                startActivity(new Intent(AdminActivity.this, DanhSachKHActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
