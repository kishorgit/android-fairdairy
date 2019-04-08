package com.example.user.sunaitestingapp.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;


import com.example.user.sunaitestingapp.R;

import org.json.JSONObject;


public class Service_Call {

    private Context context;
    private String method_name;
    private String methodType;
    private JSONObject jsonObject;
    private MyServiceListener myServiceListener;
    private Dialog progressbar;

    public Service_Call(Context context, String method_name,String methodType, JSONObject jsonObject, boolean is_show_dialog, MyServiceListener myServiceListener) {
        this.context = context;
        this.method_name = method_name;
        this.methodType = methodType;
        this.jsonObject = jsonObject;
        this.myServiceListener = myServiceListener;
        if(is_show_dialog) {
            try {
                progressbar = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                progressbar.setCanceledOnTouchOutside(false);
                progressbar.setCancelable(false);
             //   progressbar.setContentView(R.layout.dialog_view);
             /*   WebView mWebView = (WebView) progressbar.findViewById(R.id.web);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.setBackgroundColor(Color.TRANSPARENT);
                mWebView.loadUrl("file:///android_asset/index.html");*/
                hideProgressbar(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new callServiceApi().execute();
    }

    private class callServiceApi extends AsyncTask<String, Void, Model_TLI_API_Response> {
      /*  @Override
        protected Model_TLI_API_Response doInBackground(String... strings) {
            return null;
        }
*/
        protected void onPreExecute() {
            if (AppUtills.showLogs)
                Log.e("method_name", "" + method_name);
        }
           @Override
        protected Model_TLI_API_Response doInBackground(String... params) {
            String response = "";
            Model_TLI_API_Response api_response = new Model_TLI_API_Response();

            try {

                //  String app_id= Application_Algn.getSharedPreferences().getString("app_id", "");
              /*  String app_version= Application_TMF.getSharedPreferences().getString("app_version", "");
                String imei_no=Application_TMF.getSharedPreferences().getString("imei_no", "");*/
                Log.d("Service call1", "jsonObject" + jsonObject);
                Log.d("Service call1", "jsonObjectString" + jsonObject.toString());
                api_response = AppUtills.callWebService(context,AppUtills.ParseData2Send(jsonObject, "" + method_name, "" + methodType), AppUtills.serviceUrlBase+method_name, ""+methodType);

                String app_version="";
                String imei_no = "";
                Log.d("test--","Service call --app_version--"+app_version+"--imei_no--"+imei_no);

             /*   if( app_version!=null && !app_version.equals("") && imei_no!=null && !imei_no.equals(""))
                {
                    Log.d("Service calljson", "" + jsonObject);
                    api_response = AppUtills.callWebService(context, AppUtills.ParseData2Send(jsonObject, "" + method_name, "" + method_name), AppUtills.serviceUrlBase+method_name, "" + "POST");
                }
                else
                {
                    api_response.response_code=404;
                    api_response.response_msg ="Something goes wrong please try login again!";
                    api_response.data = "{\"status\":\"0\",\"message\":\"Something goes wrong please try login again!\"}";
                }*/

              if (AppUtills.showLogs)

                    Log.e("test--response", response.toString());
            } catch (Exception ex) {
                Log.e("test--","Exception in response service_call Exception in response " + ex);
            }

               Log.d("api_response ", "api_response" + api_response);
             //  progressbar.dismiss();
            return api_response;
        }

        @Override
        protected void onPostExecute(Model_TLI_API_Response api_response) {
          //  progressbar.dismiss();
            try {
                if (progressbar != null && progressbar.isShowing()) {
                    progressbar.dismiss();
                }
             //   myServiceListener.getServiceData(result);
         myServiceListener.getServiceData(api_response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

        public void hideProgressbar(boolean b) {
            try {
                if (!b) {
                    if (!((Activity) context).isFinishing() && progressbar != null && !progressbar.isShowing()) {
                        progressbar.show();
                    }
                } else {
                    if (progressbar != null) {
                        if (progressbar.isShowing()) {
                            progressbar.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}