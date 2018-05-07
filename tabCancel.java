/*
 * Created by Signals Buddy on Aug 29, 2017.
 * Copyright (c) Aug 29, 2017. All right reserved.
 *
 * Last modified 4/25/18 2:44 PM
 */

package com.signalsbuddy.signalsbuddy.TabActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.signalsbuddy.R;
import com.signalsbuddy.signalsbuddy.AdapterList.AdapterList1;
import com.signalsbuddy.signalsbuddy.ServerConnection.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Qiid on 3/12/2018.
 */

public class tabCancel extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    ProgressDialog pDialog;
    String url = Database.cancelUrl;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    int success,status;
    ArrayList<HashMap<String, String>> list_data;
    Context context;


    public static tabCancel newInstance(int status){
        tabCancel second = new tabCancel();
        Bundle args = new Bundle();
        args.putInt("STATUS", status);
        second.setArguments(args);
        return second;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        status = getArguments().getInt("STATUS",0);
        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("FragmentView Cancel","created");
        View v =inflater.inflate(R.layout.tabmenu2,container,false);
        recyclerView = (RecyclerView) v.findViewById(R.id.rvtab2);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh2);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        list_data = new ArrayList<HashMap<String, String>>();
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        context = getActivity();

        if(status ==1){
                    getJsonCancel();

        }

        return v;


    }

    @Override
    public void onRefresh() {
        list_data.clear();
        AdapterList1 adapterlist = new AdapterList1(tabCancel.this, list_data);
        recyclerView.setAdapter(adapterlist);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                getJsonCancel();
//                getsignalCancel = new getCancel();
//                getsignalCancel.execute();

            }
        }, 3000);
    }


    public void getJsonCancel() {
        list_data.clear();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        showDialog();

        MediaType MEDIA_TYPE =
                MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Failed fetch data from server, please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
                hideDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {
                    String mMessage = response.body().string();
                    Log.w("Response", mMessage );
                    hideDialog();
                    try {
                        final JSONObject JsonObj = new JSONObject(mMessage);
                        success = JsonObj.getInt(TAG_SUCCESS);
                        if(success == 1){
                            try {
                                JSONArray jsonArray = JsonObj.getJSONArray("listcancel");
                                for (int a = 0; a < jsonArray.length(); a++) {
                                    JSONObject json = jsonArray.getJSONObject(a);
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("SignalsId", json.getString("SignalsId"));
                                    map.put("Icon", json.getString("Icon"));
                                    map.put("PairName", json.getString("PairName"));
                                    map.put("SignalsPosition", json.getString("SignalsPosition"));
                                    map.put("AreaOpenPrice1", json.getString("AreaOpenPrice1"));
                                    map.put("AreaOpenPrice2", json.getString("AreaOpenPrice2"));
                                    map.put("TargetProfit1", json.getString("TargetProfit1"));
                                    map.put("TargetProfit2", json.getString("TargetProfit2"));
                                    map.put("TargetProfit3", json.getString("TargetProfit3"));
                                    map.put("StopLoss", json.getString("StopLoss"));
                                    map.put("Note", json.getString("Note"));
                                    map.put("AddDate", json.getString("AddDate"));
                                    map.put("Chart", json.getString("Chart"));
                                    list_data.add(map);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                        AdapterList1 adapterlist = new AdapterList1(tabCancel.this, list_data);
                                        recyclerView.setAdapter(adapterlist);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getActivity(), JsonObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
//
                        Log.d("Server Response", String.valueOf(success));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        });


    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    class getCancel extends AsyncTask<String,Void,String> {


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }



        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (s == null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Failed fetch data from server, please try again!", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                });
            }
            try {
                final JSONObject JsonObj = new JSONObject(s);
                success = JsonObj.getInt(TAG_SUCCESS);
                if(success ==1){
                    JSONArray jsonArray = JsonObj.getJSONArray("listcancel");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("SignalsId", json.getString("SignalsId"));
                        map.put("Icon", json.getString("Icon"));
                        map.put("PairName", json.getString("PairName"));
                        map.put("SignalsPosition", json.getString("SignalsPosition"));
                        map.put("AreaOpenPrice1", json.getString("AreaOpenPrice1"));
                        map.put("AreaOpenPrice2", json.getString("AreaOpenPrice2"));
                        map.put("TargetProfit1", json.getString("TargetProfit1"));
                        map.put("TargetProfit2", json.getString("TargetProfit2"));
                        map.put("TargetProfit3", json.getString("TargetProfit3"));
                        map.put("StopLoss", json.getString("StopLoss"));
                        map.put("Note", json.getString("Note"));
                        map.put("AddDate", json.getString("AddDate"));
                        list_data.add(map);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                AdapterList1 adapterlist = new AdapterList1(tabCancel.this, list_data);
                                recyclerView.setAdapter(adapterlist);
                            }
                        });
                    }
                }else{
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(getActivity(), JsonObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }


            } catch(JSONException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}