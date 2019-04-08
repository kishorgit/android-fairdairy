package com.example.user.sunaitestingapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.activity.delivery_change_activity;
import com.example.user.sunaitestingapp.activity.subscription_activity;
import com.example.user.sunaitestingapp.activity.vacation_activity;
import com.example.user.sunaitestingapp.common.AppUtills;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.common.Model_TLI_API_Response;
import com.example.user.sunaitestingapp.model.monthsummary_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class Calender_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String TAG = "Calender_Fragment", calender_activity_date = "", calender_activity_attendance_name = "", comment = "";
    private Button manage_subs;
    private TextView tv_act_date, tv_act_type, tv_no_of_act, tv_monthly_summary, tv_full_day, tv_half_day, tv_holiday, tv_on_leave, tv_no_of_hours, tv_no_of_activity;
    private int user_attendance_id = 0, calender_activity_attendance_id = 0;

    private boolean mViewCreated = false;
    public ProgressDialog progressDialog = null;


    Handler mHandler = null;

    private Model_TLI_API_Response model_TLI_api_response;
    private TextView dateTitle;

    private TextView leftButton;
    private TextView rightButton;
    private View rootView;
    private ViewGroup robotoCalendarMonthLayout;
    int clickflag =0;
    String monthkey = "";
    AlertDialog.Builder alertDialog;
    // Class
    //   private RobotoCalendarListener robotoCalendarListener;

    private Calendar currentCalendar;
    private Calendar lastSelectedDayCalendar;

    private static final String DAY_OF_THE_WEEK_TEXT = "dayOfTheWeekText";
    private static final String DAY_OF_THE_WEEK_LAYOUT = "dayOfTheWeekLayout";

    private static final String DAY_OF_THE_MONTH_LAYOUT = "dayOfTheMonthLayout";
    private static final String DAY_OF_THE_MONTH_TEXT = "dayOfTheMonthText";
    private static final String DAY_OF_THE_MONTH_BACKGROUND = "dayOfTheMonthBackground";
    private static final String DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 = "dayOfTheMonthCircleImage1";
    private static final String DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 = "dayOfTheMonthCircleImage2";
    private static final String DAY_OF_THE_MONTH_CIRCLE_IMAGE_3 = "dayOfTheMonthCircleImage3";

    private boolean shortWeekDays = false;
    private Context context;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HashMap<String, monthsummary_model> monthsummary_list = new HashMap<String, monthsummary_model>();
    public Calender_Fragment() {
        // Required empty public constructor
    }

    public static Calender_Fragment newInstance(String param1, String param2) {
        Calender_Fragment fragment = new Calender_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppUtills.resent_calender != null) {
            this.currentCalendar = AppUtills.resent_calender;
            //      updateView();
        } else {
            this.currentCalendar = Calendar.getInstance();

        }

        updateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.roboto_calendar_picker_layout, container, false);
        findViewsById(rootView);
        mHandler = new Handler();
        progressDialog = new ProgressDialog(getActivity());

        setUpEventListeners();

        mViewCreated = true;
        return rootView;
    }

    private void findViewsById(View view) {

        /*.....calender views.........*/
        robotoCalendarMonthLayout = (ViewGroup) view.findViewById(R.id.robotoCalendarDateTitleContainer);
        manage_subs = (Button) view.findViewById(R.id.manage_subs);
        GlobalClass globalvariable = (GlobalClass) getActivity().getApplicationContext();
        if(globalvariable.globalsubs==1){
            manage_subs.setVisibility(VISIBLE);
        }
        else {
            manage_subs.setVisibility(GONE);
        }
        leftButton = (TextView) view.findViewById(R.id.leftButton);
        leftButton.setText("<");
        rightButton = (TextView) view.findViewById(R.id.rightButton);
        rightButton.setText(">");
        dateTitle = (TextView) view.findViewById(R.id.monthText);

        for (int i = 0; i < 42; i++) {

            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int weekIndex = (i % 7) + 1;
            ViewGroup dayOfTheWeekLayout = (ViewGroup) view.findViewWithTag(DAY_OF_THE_WEEK_LAYOUT + weekIndex);

            // Create day of the month
            View dayOfTheMonthLayout = inflate.inflate(R.layout.roboto_calendar_day_of_the_month_layout, null);
            View dayOfTheMonthText = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_TEXT);
            View dayOfTheMonthBackground = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND);
            View dayOfTheMonthCircleImage1 = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1);
            View dayOfTheMonthCircleImage2 = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2);
            View dayOfTheMonthCircleImage3 = dayOfTheMonthLayout.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_3);

            // Set tags to identify them
            int viewIndex = i + 1;
            //   Log.d("test--",TAG+"--findViewsById()--"+DAY_OF_THE_MONTH_LAYOUT + viewIndex+"--"+DAY_OF_THE_MONTH_TEXT + viewIndex);

            dayOfTheMonthLayout.setTag(DAY_OF_THE_MONTH_LAYOUT + viewIndex);
            dayOfTheMonthText.setTag(DAY_OF_THE_MONTH_TEXT + viewIndex);
            dayOfTheMonthBackground.setTag(DAY_OF_THE_MONTH_BACKGROUND + viewIndex);
            dayOfTheMonthCircleImage1.setTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 + viewIndex);
            dayOfTheMonthCircleImage2.setTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 + viewIndex);
            dayOfTheMonthCircleImage3.setTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_3 + viewIndex);

            dayOfTheWeekLayout.addView(dayOfTheMonthLayout);

        }

        /*...../calender views.........*/

        /*.........layout views.......*/

        //    btn_edit = (Button) view.findViewById(R.id.btn_edit);
        //   tv_act_date = (TextView) view.findViewById(R.id.tv_act_date);
        ///   tv_act_type = (TextView) view.findViewById(R.id.tv_act_type);
        //   tv_no_of_act = (TextView) view.findViewById(R.id.tv_no_of_act);
        //  tv_monthly_summary = (TextView) view.findViewById(R.id.tv_monthly_summary);
        tv_full_day = (TextView) view.findViewById(R.id.tv_full_day);
        tv_half_day = (TextView) view.findViewById(R.id.tv_half_day);
      /*  tv_holiday = (TextView) view.findViewById(R.id.tv_holiday);
        tv_holiday = (TextView) view.findViewById(R.id.tv_holiday);*/
        // tv_on_leave = (TextView) view.findViewById(R.id.tv_on_leave);
        //   tv_no_of_hours = (TextView) view.findViewById(R.id.tv_no_of_hours);
        //   tv_no_of_activity = (TextView) view.findViewById(R.id.tv_no_of_activity);

        /*........./layout views.......*/

    }

    private void setUpEventListeners() {

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickflag =1;
              /*  if (robotoCalendarListener == null) {
                 //   throw new IllegalStateException("You must assign a valid RobotoCalendarListener first!");
                    Log.d("test--",TAG+"--You must assign a valid RobotoCalendarListener first!1--");
                }*/

                // Decrease month
                currentCalendar.add(Calendar.MONTH, -1);
                AppUtills.resent_calender.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                AppUtills.resent_calender.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));

                int monthofYear = currentCalendar.get(Calendar.MONTH)+1;

                monthkey=monthofYear+"-"+currentCalendar.get(Calendar.YEAR);

                lastSelectedDayCalendar = null;
                //    Log.d("test--",TAG+"--You must assign a valid RobotoCalendarListener first!1 leftButton onclick--");
                updateView();
                // robotoCalendarListener.onLeftButtonClick();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickflag =1;
               /* if (robotoCalendarListener == null) {
                  //  throw new IllegalStateException("You must assign a valid RobotoCalendarListener first!");
                    Log.d("test--",TAG+"--You must assign a valid RobotoCalendarListener first!2--");
                }*/

                // Increase month
                currentCalendar.add(Calendar.MONTH, 1);
                AppUtills.resent_calender.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                AppUtills.resent_calender.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));


                int monthofYear = currentCalendar.get(Calendar.MONTH)+1;

                monthkey=monthofYear+"-"+currentCalendar.get(Calendar.YEAR);


                lastSelectedDayCalendar = null;
                // Log.d("test--",TAG+"--You must assign a valid RobotoCalendarListener first!2 rightButton click--");
                updateView();
                //         robotoCalendarListener.onRightButtonClick();
            }
        });
        manage_subs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             /*   fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new subscription_fragment());
                fragmentTransaction.commit();*/
                Intent intent = new Intent(getActivity(),subscription_activity.class);
                startActivity(intent);

            }
        });


        setCalendar();
        updateView();
    }

    /**
     * Update the calendar view
     */
    public void updateView() {
        setUpMonthLayout();
        setUpWeekDaysLayout();
        setUpDaysOfMonthLayout();
        setUpDaysInCalendar();
        if(clickflag==1){
            Log.d(TAG, "updateView: monthkey"+monthkey);
            new MyAsyncTaskClick(monthkey).execute();
        }
        else {
            new MyAsyncTask().execute();
        }

      //  markWeekendAsSelectedDay();
        markDayAsCurrentDay();
        showDateTitle(true);
        //setActivityDetails();
    }

 /*   private void setActivityDetails() {
        Calendar cal = AppUtills.resent_calender;
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat fmt2 = new SimpleDateFormat("MMM-yyyy");
        Log.d("test--", TAG + "--" + fmt.format(cal.getTime()));
        String att_name = "N/A", count_user_activity = "N/A";
        user_attendance_id = 0;
        calender_activity_attendance_id = 0;
        calender_activity_attendance_name = "";
        comment = "";
      *//*  JSONObject today_activity_data = dbHelper.selectTodayActivityData(cal.getTime(), Integer.parseInt(Application_TMF.getSharedPreferences().getString("id", "")));
        try {
            if (today_activity_data.getString("select_row_status").toString().equals("1")) {
                Log.d("test--", TAG + "--today_activity_data--" + today_activity_data.getString("user_attendance_status").toString());
                user_attendance_id = today_activity_data.getInt("user_attendance_id");
                //att_name = dbHelper.getAttendanceName(Integer.parseInt(today_activity_data.getString("user_attendance_status").toString()));
                att_name = dbHelper.getAttendanceName(today_activity_data.getInt("user_attendance_status"));
                calender_activity_attendance_name = att_name;
                calender_activity_attendance_id = today_activity_data.getInt("user_attendance_status");
                count_user_activity = today_activity_data.getString("count_user_activity").toString();
                comment = today_activity_data.getString("comment");
            }
        } catch (JSONException e) {
            Log.d("test--", TAG + "--att_name--" + e);
            e.printStackTrace();
        }
        Log.d("test--", TAG + "-- att_name --" + att_name);*//*


        String resent_date = fmt.format(cal.getTime()).toString();
     //   tv_act_date.setText(resent_date);
        calender_activity_date = resent_date;
        ////////////////////////////////
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MMM-yyyy").parse(tv_act_date.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = new Date();
        //   Toast.makeText(getContext(),"date"+date,Toast.LENGTH_LONG).show();
        //   Toast.makeText(getContext(),"currentDate"+currentDate,Toast.LENGTH_LONG).show();

        long diff = date.getTime() - currentDate.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long months = days / 31;

        Long limit = new Long(2);
        //  Toast.makeText(getContext(),"diff"+diff,Toast.LENGTH_LONG).show();
        //   Toast.makeText(getContext(),"months"+months,Toast.LENGTH_LONG).show();
        if (Math.abs(months) >= limit) {  // Toast.makeText(getContext(),"limit",Toast.LENGTH_LONG).show();
            // Toast.makeText(getContext(),"savebutton",Toast.LENGTH_LONG).show();


            btn_edit.setText("View");
        } else {
            btn_edit.setText("Edit");


        }
        /////////////////////////////////////////////////////
        if (att_name.equals("N/A")) {
            tv_no_of_act.setVisibility(GONE);
            tv_act_type.setText("");
            //    btn_edit.setBackgroundColor(btn_edit.getContext().getResources().getColor(R.color.bg_tv_color));
            // btn_edit.setTextColor(btn_edit.getContext().getResources().getColor(R.color.view_gray_color));//view_gray_color
            //  btn_edit.setEnabled(false);

        } else if (att_name.equals("Holiday") || att_name.equals("Leave")) {
            tv_act_type.setText(att_name);
            tv_no_of_act.setText("");
            tv_no_of_act.setVisibility(VISIBLE);
            btn_edit.setBackgroundColor(btn_edit.getContext().getResources().getColor(R.color.brown3));
            btn_edit.setTextColor(btn_edit.getContext().getResources().getColor(R.color.white_color));//white_color
            btn_edit.setEnabled(true);

        } else {
            tv_act_type.setText(att_name);
            tv_no_of_act.setText("No. Of Activity- " + count_user_activity);

            String formatteddate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            //  tv_no_of_act.setText("No. Of Activity- " + dbHelper.getUserNumberOfActivity(formatteddate));


            tv_no_of_act.setVisibility(VISIBLE);
            btn_edit.setBackgroundColor(btn_edit.getContext().getResources().getColor(R.color.brown3));
            btn_edit.setTextColor(btn_edit.getContext().getResources().getColor(R.color.white_color));//white_color
            btn_edit.setEnabled(true);

        }

      //  tv_monthly_summary.setText("Monthly Summary Of " + fmt2.format(cal.getTime()).toString());

        Calendar cal_start_date = Calendar.getInstance();
        cal_start_date.setTime(cal.getTime());
        cal_start_date.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

        Calendar cal_end_date = Calendar.getInstance();
        cal_end_date.setTime(cal.getTime());
        cal_end_date.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

      *//*  tv_full_day.setText(" Full Day  - " + dbHelper.getTotalMonthFullDay(cal_start_date.getTime(), cal_end_date.getTime()));
        tv_half_day.setText(" Half Day  - " + dbHelper.getTotalMonthHalfDay(cal_start_date.getTime(), cal_end_date.getTime()));
        tv_holiday.setText(" Holiday  - " + dbHelper.getTotalMonthHoliday(cal_start_date.getTime(), cal_end_date.getTime()));
        tv_on_leave.setText(" Leave  - " + dbHelper.getTotalMonthLeave(cal_start_date.getTime(), cal_end_date.getTime()));
        //   tv_holiday.setText(" Holiday ");
        //  tv_on_leave.setText("On Leave ");
        tv_no_of_hours.setText(" Hours  - " + dbHelper.getTotalMonthNoOfHours(cal_start_date.getTime(), cal_end_date.getTime(), Integer.parseInt(Application_TMF.getSharedPreferences().getString("id", ""))));
        tv_no_of_activity.setText(" Activities - " + dbHelper.getTotalMonthActivity(cal_start_date.getTime(), cal_end_date.getTime(), Integer.parseInt(Application_TMF.getSharedPreferences().getString("id", ""))));
getTotalMonthLeave*//*

        int month = cal.get(currentCalendar.MONTH);
        do {
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
              *//*  SimpleDateFormat fmt = new SimpleDateFormat("EEE M/d/yyyy");
                Log.d("test--",TAG+"--"+fmt.format(cal.getTime()));

*//*

     *//*     int monthOffset = getMonthOffset(cal);
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                ViewGroup dayOfTheMonthBackground = (ViewGroup) rootView.findViewWithTag("dayOfTheMonthBackground" + (currentDay + monthOffset));
            dayOfTheMonthBackground.setBackgroundResource(R.drawable.circle);
                TextView dayOfTheMonth = (TextView) rootView.findViewWithTag("dayOfTheMonthText" + (currentDay + monthOffset));
                Log.d("test--",TAG+"--dayOfTheMonth markWeekendAsSelectedDay--"+dayOfTheMonth.getText().toString());
                dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));

                ImageView circleImage1 = getCircleImage1(cal);
                ImageView circleImage2 = getCircleImage2(cal);
                DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
                DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
        *//*

            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } while (cal.get(Calendar.MONTH) == month);


    }*/


    public void showDateTitle(boolean show) {
        if (show) {
            robotoCalendarMonthLayout.setVisibility(VISIBLE);
        } else {
            robotoCalendarMonthLayout.setVisibility(GONE);
        }
    }

    public void setCalendar() {

        Calendar currentCalendar = Calendar.getInstance();

        this.currentCalendar = currentCalendar;
        AppUtills.resent_calender = currentCalendar;


        // updateView();
    }

    public void setUpMonthLayout() {
        String dateText = new DateFormatSymbols(Locale.getDefault()).getMonths()[currentCalendar.get(Calendar.MONTH)];
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());
        Calendar calendar = Calendar.getInstance();
        try {
            if (currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                // dateTitle.setText(dateText);
                dateTitle.setText(String.format("%s %s", dateText, currentCalendar.get(Calendar.YEAR)));
            } else {
                dateTitle.setText(String.format("%s %s", dateText, currentCalendar.get(Calendar.YEAR)));
            }
            Log.d("setUpMonthLayout--", TAG + " --dateText" + dateText);
        } catch (Exception ex) {
            Log.e("test--", TAG + " --exception in dateTitle --" + ex);
        }
    }

    private void setUpWeekDaysLayout() {
        TextView dayOfWeek;
        String dayOfTheWeekString;
        String[] weekDaysArray = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
        for (int i = 1; i < weekDaysArray.length; i++) {
            dayOfWeek = (TextView) rootView.findViewWithTag(DAY_OF_THE_WEEK_TEXT + getWeekIndex(i, currentCalendar));
            dayOfTheWeekString = weekDaysArray[i];
            if (shortWeekDays) {
                dayOfTheWeekString = checkSpecificLocales(dayOfTheWeekString, i);
            } else {
                //  dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase() + dayOfTheWeekString.substring(1, 3);
                dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase() + dayOfTheWeekString.substring(1, 1);
            }


            dayOfWeek.setText(dayOfTheWeekString);
        }
    }

    private void setUpDaysOfMonthLayout() {

        TextView dayOfTheMonthText;
        View circleImage1;
        View circleImage2;
        View circleImage3;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthBackground;

        for (int i = 1; i < 43; i++) {

            dayOfTheMonthContainer = (ViewGroup) rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            dayOfTheMonthBackground = (ViewGroup) rootView.findViewWithTag(DAY_OF_THE_MONTH_BACKGROUND + i);
            dayOfTheMonthText = (TextView) rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            circleImage1 = rootView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1 + i);
            circleImage2 = rootView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2 + i);
            circleImage3 = rootView.findViewWithTag(DAY_OF_THE_MONTH_CIRCLE_IMAGE_3 + i);
            Log.d("test--", TAG + "setUpDaysOfMonthLayout---DAY_OF_THE_MONTH_TEXT + i" + DAY_OF_THE_MONTH_TEXT + i);
            Log.d("test--", TAG + "setUpDaysOfMonthLayout---rootView" + rootView);
            Log.d("test--", TAG + "setUpDaysOfMonthLayout---dayOfTheMonthText" + dayOfTheMonthText);
            dayOfTheMonthText.setVisibility(INVISIBLE);
            circleImage1.setVisibility(GONE);
            circleImage2.setVisibility(GONE);
            circleImage3.setVisibility(GONE);

            // Apply styles
            dayOfTheMonthText.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthText.setTypeface(null, Typeface.NORMAL);
            dayOfTheMonthText.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_day_of_the_month_font));
            //   dayOfTheMonthText.setTextColor(ContextCompat.getColor(context, R.color.black_color));

            dayOfTheMonthContainer.setBackgroundResource(android.R.color.transparent);
            dayOfTheMonthContainer.setOnClickListener(null);
            dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
        }
    }

    private void setUpDaysInCalendar() {

        Calendar auxCalendar = Calendar.getInstance(Locale.getDefault());
        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);
        TextView dayOfTheMonthText;
        ViewGroup dayOfTheMonthContainer;
        ViewGroup dayOfTheMonthLayout;

        // Calculate dayOfTheMonthIndex
        int dayOfTheMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);

        for (int i = 1; i <= auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++, dayOfTheMonthIndex++) {
            dayOfTheMonthContainer = (ViewGroup) rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + dayOfTheMonthIndex);
            dayOfTheMonthText = (TextView) rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + dayOfTheMonthIndex);
            if (dayOfTheMonthText == null) {
                break;
            }
            dayOfTheMonthContainer.setOnClickListener(onDayOfMonthClickListener);
            dayOfTheMonthContainer.setOnLongClickListener(onDayOfMonthLongClickListener);
            dayOfTheMonthText.setVisibility(VISIBLE);
            dayOfTheMonthText.setText(String.valueOf(i));


        }

        for (int i = 36; i < 43; i++) {
            dayOfTheMonthText = (TextView) rootView.findViewWithTag(DAY_OF_THE_MONTH_TEXT + i);
            dayOfTheMonthLayout = (ViewGroup) rootView.findViewWithTag(DAY_OF_THE_MONTH_LAYOUT + i);
            if (dayOfTheMonthText.getVisibility() == INVISIBLE) {
                dayOfTheMonthLayout.setVisibility(GONE);
            } else {
                dayOfTheMonthLayout.setVisibility(VISIBLE);

            }
        }
    }

    private void markDayAsCurrentDay() {
        // If it's the current month, mark current day
        Calendar nowCalendar = Calendar.getInstance();
        if (nowCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(nowCalendar.getTime());

            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(currentCalendar);
            dayOfTheMonthBackground.setBackgroundResource(R.drawable.circle_current_day);

        }

      /* int monthOffset = getMonthOffset(AppUtills.resent_calender);
       int currentDay = AppUtills.resent_calender.get(Calendar.DAY_OF_MONTH);
       ViewGroup dayOfTheMonthBackground = (ViewGroup) rootView.findViewWithTag("dayOfTheMonthBackground" + (currentDay));
       //  dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
       dayOfTheMonthBackground.setBackgroundResource(R.drawable.circle_current_day);
       TextView dayOfTheMonth = (TextView) rootView.findViewWithTag("dayOfTheMonthText" + (currentDay ));
       //      Log.d("test--",TAG+"--dayOfTheMonth markWeekendAsSelectedDay--"+dayOfTheMonth.getText().toString());
       //  dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
       dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.white_color));
*/

/*
        ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(AppUtills.resent_calender);
        dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
*/

    /*    int monthOffset = getMonthOffset(AppUtills.resent_calender);
        int currentDay = AppUtills.resent_calender.get(Calendar.DAY_OF_MONTH);
        ViewGroup dayOfTheMonthBackground = (ViewGroup) rootView.findViewWithTag("dayOfTheMonthBackground" + (currentDay + monthOffset));
      //  dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
        dayOfTheMonthBackground.setBackgroundResource(R.drawable.circle_current_day);
        TextView dayOfTheMonth = (TextView) rootView.findViewWithTag("dayOfTheMonthText" + (currentDay + monthOffset));
        //      Log.d("test--",TAG+"--dayOfTheMonth markWeekendAsSelectedDay--"+dayOfTheMonth.getText().toString());
        //  dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
      dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.white_color));

*/


        //  dayOfTheMonthBackground.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));


    }


    public void markWeekendAsSelectedDay(HashMap<String, monthsummary_model> monthsummary_list)  //ref--markDayAsSelectedDay
    {

        String dateText = new DateFormatSymbols(Locale.getDefault()).getMonths()[currentCalendar.get(Calendar.MONTH)];
        Log.d("test--", TAG + "--markWeekendAsSelectedDay23--" + dateText);


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
        String start_date = "";
        String end_date = "";


        start_date = fmt.format(cal.getTime());

        String[] start_date_words = start_date.split("/");

        end_date = cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + start_date_words[1] + "/" + start_date_words[2];

//      Log.d("test--", TAG + "--markWeekendAsSelectedDay--monthsummary_list2222222222" +     monthsummary_list.get(fmt2.format(cal.getTime())).getStatus());

      int month = cal.get(currentCalendar.MONTH);
        do {

            if (monthsummary_list.containsKey(fmt2.format(cal.getTime()))) {
                Log.d("markWeekend", "fmt2.format(cal.getTime())" + fmt2.format(cal.getTime()));


                int monthOffset = getMonthOffset(cal);
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                ViewGroup dayOfTheMonthBackground = (ViewGroup) rootView.findViewWithTag("dayOfTheMonthBackground" + (currentDay + monthOffset));
                TextView dayOfTheMonth = (TextView) rootView.findViewWithTag("dayOfTheMonthText" + (currentDay + monthOffset));
                //      Log.d("test--",TAG+"--dayOfTheMonth markWeekendAsSelectedDay--"+dayOfTheMonth.getText().toString());
                //  dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));

                dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_day_of_the_month_font));




                ImageView circleImage1 = getCircleImage1(cal);
                ImageView circleImage2 = getCircleImage2(cal);
                ImageView circleImage3 = getCircleImage3(cal);
                if(monthsummary_list.get(fmt2.format(cal.getTime()))==null){
                    circleImage1.setVisibility(GONE);
                    circleImage2.setVisibility(GONE);
                    circleImage3.setVisibility(GONE);


                }
                else {
                    Log.d("markWeekend", "monthdaystatus" + monthsummary_list.get(fmt2.format(cal.getTime())).getStatus());

                    if (monthsummary_list.get(fmt2.format(cal.getTime())).getStatus().equalsIgnoreCase("DELIVERED")) {
                        DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(context, R.color.circle_delivered));
                        circleImage1.setVisibility(VISIBLE);
                    } else if (monthsummary_list.get(fmt2.format(cal.getTime())).getStatus().equalsIgnoreCase("SCHEDULED")) {
                        DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(context, R.color.circle_upcoming));
                        circleImage2.setVisibility(VISIBLE);
                    } else if (monthsummary_list.get(fmt2.format(cal.getTime())).getStatus().equalsIgnoreCase("VACATION")) {
                        DrawableCompat.setTint(circleImage3.getDrawable(), ContextCompat.getColor(context, R.color.circle_vacations));
                        circleImage3.setVisibility(VISIBLE);
                    } else {
                        circleImage1.setVisibility(GONE);
                        circleImage2.setVisibility(GONE);
                        circleImage3.setVisibility(GONE);

                    }

                }
            }
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } while (cal.get(Calendar.MONTH) == month);


    }




    private int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();

        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {

            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }

    private String checkSpecificLocales(String dayOfTheWeekString, int i) {
        // Set Wednesday as "X" in Spanish Locale.getDefault()
        if (i == 4 && Locale.getDefault().getCountry().equals("ES")) {
            dayOfTheWeekString = "X";
        } else {
            dayOfTheWeekString = dayOfTheWeekString.substring(0, 1).toUpperCase();
        }
        return dayOfTheWeekString;
    }

    private View.OnClickListener onDayOfMonthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("test--", TAG + "--onClick onDayOfMonthClickListener--");
            GlobalClass globalvariable = (GlobalClass) getActivity().getApplicationContext();
            // Extract day selected
            ViewGroup dayOfTheMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfTheMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_THE_MONTH_LAYOUT.length(), tagId.length());
            TextView dayOfTheMonthText = (TextView) view.findViewWithTag(DAY_OF_THE_MONTH_TEXT + tagId);

            // Extract the day from the text
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfTheMonthText.getText().toString()));
            Log.d("test--", TAG + "--dayOfmonth onDayOfMonthClickListener--" + dayOfTheMonthText.getText().toString() + "--day--" + calendar.get(Calendar.DAY_OF_MONTH));
            Log.d("test--", TAG + "calendar--onDayOfMonthClickListener--" + calendar);
            String dateText = new DateFormatSymbols(Locale.getDefault()).getMonths()[calendar.get(Calendar.MONTH)];
            dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length());
            globalvariable.current_date = "< " + calendar.get(Calendar.DAY_OF_MONTH) + " " + dateText + " " + calendar.get(Calendar.YEAR) + " >";
            AppUtills.resent_calender = calendar;
            markDayAsSelectedDay(calendar);

            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
             int moonthofYear = calendar.get(Calendar.MONTH)+1;
             int dayofmonth = calendar.get(Calendar.DAY_OF_MONTH)+1;
            Date d1 = null;
            try {
                d1 = sdformat.parse(calendar.get(Calendar.YEAR)+"-" +moonthofYear+"-"+calendar.get(Calendar.DAY_OF_MONTH));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            globalvariable.scheduleChangeDate =sdformat.format(d1);

            ////////////////////////
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String todaydate = sdf.format(c.getTime());
            Log.d("test--", TAG + "validation-scheduleChangeDate" + globalvariable.scheduleChangeDate);
            Log.d("test--", TAG + "validation-todaydate" + todaydate);

            /////////////////
         //   Toast.makeText(getActivity(),"scheduleChangeDate"+  globalvariable.scheduleChangeDate,Toast.LENGTH_LONG).show();

          /*  fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new delivery_change_fragment());
            fragmentTransaction.commit();*/

          /*  Intent intent = new Intent(getActivity(), delivery_change_activity.class);
            startActivity(intent);*/


            try {
                if(sdf.parse(globalvariable.scheduleChangeDate).after(sdf.parse(todaydate))) {

                    if(monthsummary_list.get(sdf.format(calendar.getTime()))==null){}
                    else {
                        if (monthsummary_list.get(sdf.format(calendar.getTime())).getStatus().equalsIgnoreCase("VACATION")) {
                            alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setMessage("Schedule is not available during vacations.Manage your vacation.");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.show();
                          //  Toast.makeText(getActivity(), "Schedule is not available during vacations.Manage your vacation.", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(getActivity(), delivery_change_activity.class);
                            startActivity(intent);

                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            // setActivityDetails();

            // Fire event
           /* if (robotoCalendarListener == null) {
//                throw new IllegalStateException("You must assign a valid RobotoCalendarListener first!");
                Log.d("test--",TAG+"--You must assign a valid RobotoCalendarListener first!3--");
            } else {
                robotoCalendarListener.onDayClick(calendar);
            }
 */
        }
    };

    private View.OnLongClickListener onDayOfMonthLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            Log.d("test--", TAG + "--onLongClick --");
            // Extract day selected
            ViewGroup dayOfTheMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfTheMonthContainer.getTag();
            tagId = tagId.substring(DAY_OF_THE_MONTH_LAYOUT.length(), tagId.length());
            TextView dayOfTheMonthText = (TextView) view.findViewWithTag(DAY_OF_THE_MONTH_TEXT + tagId);

            // Extract the day from the text
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayOfTheMonthText.getText().toString()));
            Log.d("test--", TAG + "--dayOfmonth--" + dayOfTheMonthText.getText().toString());
            AppUtills.resent_calender = calendar;
            markDayAsSelectedDay(calendar);


            return true;
        }
    };

    public void markDayAsSelectedDay(Calendar calendar) {

        // Clear previous current day mark
        //ok    clearSelectedDay(lastSelectedDayCalendar);

        // Store current values as last values
        lastSelectedDayCalendar = calendar;

        // Mark current day as selected
        //ok  ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(calendar);
        //ok  dayOfTheMonthBackground.setBackgroundResource(R.drawable.circle);

      /*  TextView dayOfTheMonth = getDayOfMonthText(calendar);
        Log.d("test--",TAG+"--dayOfTheMonth--"+dayOfTheMonth.getText().toString());
        dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
*/
  /*    ImageView circleImage1 = getCircleImage1(calendar);
        ImageView circleImage2 = getCircleImage2(calendar);
        ImageView circleImage3 = getCircleImage3(calendar);
        DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
        DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
        DrawableCompat.setTint(circleImage3.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
        circleImage1.setVisibility(VISIBLE);
        circleImage2.setVisibility(VISIBLE);
        circleImage3.setVisibility(VISIBLE);
*/
    /*    if (circleImage1.getVisibility() == VISIBLE) {
            DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
        }

        if (circleImage2.getVisibility() == VISIBLE) {
            DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_selected_day_font));
        }*/
    }

    private void clearSelectedDay(Calendar calendar) {
        if (calendar != null) {

            ViewGroup dayOfTheMonthBackground = getDayOfMonthBackground(calendar);

            // If it's today, keep the current day style
            Calendar nowCalendar = Calendar.getInstance();
            if (nowCalendar.get(Calendar.YEAR) == lastSelectedDayCalendar.get(Calendar.YEAR) && nowCalendar.get(Calendar.DAY_OF_YEAR) == lastSelectedDayCalendar.get(Calendar.DAY_OF_YEAR)) {
                dayOfTheMonthBackground.setBackgroundResource(R.drawable.ring);
            } else {
                dayOfTheMonthBackground.setBackgroundResource(android.R.color.transparent);
            }

            TextView dayOfTheMonth = getDayOfMonthText(calendar);
            dayOfTheMonth.setTextColor(ContextCompat.getColor(context, R.color.roboto_calendar_day_of_the_month_font));

            ImageView circleImage1 = getCircleImage1(calendar);
            ImageView circleImage2 = getCircleImage2(calendar);
            if (circleImage1.getVisibility() == VISIBLE) {
                DrawableCompat.setTint(circleImage1.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_circle_1));
            }

            if (circleImage2.getVisibility() == VISIBLE) {
                DrawableCompat.setTint(circleImage2.getDrawable(), ContextCompat.getColor(context, R.color.roboto_calendar_circle_2));
            }
        }
    }

    private ViewGroup getDayOfMonthBackground(Calendar currentCalendar) {
        return (ViewGroup) getView(DAY_OF_THE_MONTH_BACKGROUND, currentCalendar);
    }

    private TextView getDayOfMonthText(Calendar currentCalendar) {
        return (TextView) getView(DAY_OF_THE_MONTH_TEXT, currentCalendar);
    }

    private ImageView getCircleImage1(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CIRCLE_IMAGE_1, currentCalendar);
    }

    private ImageView getCircleImage2(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CIRCLE_IMAGE_2, currentCalendar);
    }

    private ImageView getCircleImage3(Calendar currentCalendar) {
        return (ImageView) getView(DAY_OF_THE_MONTH_CIRCLE_IMAGE_3, currentCalendar);
    }

    private int getDayIndexByDate(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        return currentDay + monthOffset;
    }

    private int getMonthOffset(Calendar currentCalendar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {

            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private View getView(String key, Calendar currentCalendar) {
        int index = getDayIndexByDate(currentCalendar);
        return rootView.findViewWithTag(key + index);
    }

    /* private void init(View view) {
         btn_edit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Log.d("test--", TAG + "--btn_edit--");
                 // startActivity(new Intent(getContext(), Update_Activity.class));
             }
         });
         Date myDate = new Date();
         tv_act_date.setText(new SimpleDateFormat("dd-MMM-yyyy").format(myDate));
         // Set listener, in this case, the same activity
         //   robotoCalendarView.setRobotoCalendarListener((RobotoCalendarView.RobotoCalendarListener) getActivity());

    *//* robotoCalendarView.setShortWeekDays(false);
    robotoCalendarView.setShortWeekDays(false);

    robotoCalendarView.showDateTitle(true);

    robotoCalendarView.updateView();*//*
    }*/
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("test--", TAG + "--setUserVisibleHint--" + isVisibleToUser);

        if (isVisibleToUser) {
            Log.d("test--", TAG + "--Fragment is visible.");

            if (AppUtills.resent_calender != null) {
                Log.d("test--", TAG + "--if visible--" + AppUtills.resent_calender);
                this.currentCalendar = AppUtills.resent_calender;
                if (mViewCreated) {
                    Log.d("test--", TAG + "-if-mViewCreated--" + mViewCreated);
                    updateView();
                } else {
                    Log.d("test--", TAG + "-else-mViewCreated--" + mViewCreated);
                }
            } else {
                Log.d("test--", TAG + "--else visible--" + AppUtills.resent_calender);
                this.currentCalendar = Calendar.getInstance();

            }
            //   setActivityDetails();
        } else {
            if (AppUtills.resent_calender != null) {
                Log.d("test--", TAG + "--if --" + AppUtills.resent_calender);
                this.currentCalendar = AppUtills.resent_calender;
                //      updateView();
            } else {
                Log.d("test--", TAG + "--else --" + AppUtills.resent_calender);
                this.currentCalendar = Calendar.getInstance();

            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("test--", TAG + "...onActivityCreated--");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void getMonthSummary() {
        String result = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = sdf.format(c.getTime());
        final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        Model_TLI_API_Response api_response = new Model_TLI_API_Response();
     //    final HashMap<String, monthsummary_model> monthsummary_list = new HashMap<String, monthsummary_model>();
        api_response = AppUtills.callWebGetService(getActivity().getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/" + globalVariable.customerid + "/schedulesummary");

        try {
            Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
            if (api_response.response_code == HttpsURLConnection.HTTP_OK)
            {
                result = api_response.data;
                Log.d("getMonthSummary", TAG + "result" + result);
                JSONArray jsonArray = new JSONArray(result);
                if(jsonArray.length()>0){

                    for (int arrayloop = 0; arrayloop < jsonArray.length(); arrayloop++)
                    {

                        final  JSONObject json_object_root =jsonArray.getJSONObject(arrayloop);


                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    Log.d("test--", TAG + "monthsummary_list-- date--" + json_object_root.getString("date"));
                                    Log.d("test--", TAG + "monthsummary_list-- status--" + json_object_root.getString("status"));
                                    monthsummary_model monthsummary_model = new  monthsummary_model();
                                    monthsummary_model.setMonthdate(json_object_root.getString("date"));
                                    monthsummary_model.setStatus(json_object_root.getString("status"));
                                    monthsummary_list.put(json_object_root.getString("date"),monthsummary_model);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                } catch (Exception ex) {
                                    Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                                }

                            }
                        });

                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            markWeekendAsSelectedDay(monthsummary_list);
                        }
                    });



                }

                } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (progressDialog == null || progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                        }

                       // Toast.makeText(getActivity().getApplicationContext(), "No Record Found From Server", Toast.LENGTH_LONG).show();

                    }
                });

            }
        } catch (Exception ex) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (progressDialog == null || progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception ex) {
                        Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                    }
                    //  Toast.makeText(getActivity().getApplicationContext(), "No Record Found From Server", Toast.LENGTH_LONG).show();

                }
            });
            Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
        }


    }

    private class MyAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {


            getMonthSummary();
            return true;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait.......");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {


            super.onPostExecute(o);
        }
    }

    private class MyAsyncTaskClick extends AsyncTask {

        String monthkey;

        public MyAsyncTaskClick(String monthkey) {
            this.monthkey = monthkey;

        }

        @Override
        protected Object doInBackground(Object[] objects) {


            getMonthSummaryClick(monthkey);
            return true;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please wait.......");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {


            super.onPostExecute(o);
        }
    }

    private void getMonthSummaryClick(String monthkey) {

            String result = "";
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentdate = sdf.format(c.getTime());
            final GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
            Model_TLI_API_Response api_response = new Model_TLI_API_Response();
            //    final HashMap<String, monthsummary_model> monthsummary_list = new HashMap<String, monthsummary_model>();
            api_response = AppUtills.callWebGetService(getActivity().getApplicationContext(), "http://ec2-13-233-178-47.ap-south-1.compute.amazonaws.com:8080/fd/api/customer/" + globalVariable.customerid + "/schedulesummary/"+monthkey);

            try {
                Log.d("requestLeaveListData", TAG + "api_response.response_code" + api_response.response_code);
                if (api_response.response_code == HttpsURLConnection.HTTP_OK)
                {
                    result = api_response.data;
                    Log.d("getMonthSummary", TAG + "result" + result);
                    JSONArray jsonArray = new JSONArray(result);
                    if(jsonArray.length()>0){

                        for (int arrayloop = 0; arrayloop < jsonArray.length(); arrayloop++)
                        {

                            final  JSONObject json_object_root =jsonArray.getJSONObject(arrayloop);


                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Log.d("test--", TAG + "monthsummary_list-- date--" + json_object_root.getString("date"));
                                        Log.d("test--", TAG + "monthsummary_list-- status--" + json_object_root.getString("status"));
                                        monthsummary_model monthsummary_model = new  monthsummary_model();
                                        monthsummary_model.setMonthdate(json_object_root.getString("date"));
                                        monthsummary_model.setStatus(json_object_root.getString("status"));
                                        monthsummary_list.put(json_object_root.getString("date"),monthsummary_model);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (progressDialog != null && progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    } catch (Exception ex) {
                                        Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                                    }

                                }
                            });

                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                markWeekendAsSelectedDay(monthsummary_list);
                            }
                        });



                    }

                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (progressDialog == null || progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            } catch (Exception ex) {
                                Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                            }

                            // Toast.makeText(getActivity().getApplicationContext(), "No Record Found From Server", Toast.LENGTH_LONG).show();

                        }
                    });

                }
            } catch (Exception ex) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (progressDialog == null || progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception ex) {
                            Log.d("test--", TAG + " exception in progressDialog.dismiss()--" + ex);
                        }
                        //  Toast.makeText(getActivity().getApplicationContext(), "No Record Found From Server", Toast.LENGTH_LONG).show();

                    }
                });
                Log.d("test--", TAG + "--exception in Service_Call1--" + ex);
            }



    }
}
