package com.component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sdhy.video.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class DatePickActivity extends Activity implements OnClickListener {

    private DatePicker datePicker;

    private TimePicker timePicker;

    private String datestrold;
    private String datestr;
    private View viewother;
    private Button queren, quxiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
        datePicker = (DatePicker) findViewById(R.id.date_picker);
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        viewother = (View) findViewById(R.id.repair_date_other);
        queren = (Button) findViewById(R.id.repair_date_sel_cancel);
        quxiao = (Button) findViewById(R.id.repair_date_sel_ok);

        init();
        viewother.setOnClickListener(this);
        queren.setOnClickListener(this);
        quxiao.setOnClickListener(this);
    }

    /**
     * 数据初始化
     */
    private void init() {
        datePicker.setCalendarViewShown(false);
        timePicker.setIs24HourView(true);
        resizePikcer(datePicker);// 调整datepicker大小
        resizePikcer(timePicker);// 调整timepicker大小
        String str = getIntent().getStringExtra("date");
        if (TextUtils.isEmpty(str)) {
            System.out.println("isempty");
            datestrold = "";
            datestr = "";
        } else {
            datestr = str;
            datestrold = str;
        }

    }


    /**
     * 调整FrameLayout大小
     *
     * @param tp
     */
    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    /*
     * 调整numberpicker大小
     */
    private void resizeNumberPicker(NumberPicker np) {
        LayoutParams params = new LayoutParams(Util.dip2px(this, 45),
                LayoutParams.WRAP_CONTENT);
        params.setMargins(Util.dip2px(this, 5), 0, Util.dip2px(this, 5), 0);
        np.setLayoutParams(params);

    }

    /**
     * 得到viewGroup里面的numberpicker组件
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }


    /**
     * 处理返回按键
     */
    @Override
    public void onBackPressed() {
        back(true);
        super.onBackPressed();
    }

    /**
     * 关闭调用 old为true则不变，false则改变
     *
     * @param old是否不变
     */
    private void back(boolean old) {
        // 获取时间选择
        Intent intent = new Intent();
        if (old) {
            intent.putExtra("date", datestrold);
        } else {
            datestr = getData();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date date = sdf.parse(datestr);
                if (!compare(date))
                    return;
                intent.putExtra("date", datestr);
                setResult(Activity.RESULT_OK, intent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    // 比较时间
    private boolean compare(Date dt1) {
        Date curDate = new Date(System.currentTimeMillis());
        if (dt1.getTime() < curDate.getTime()) {
            System.out.println("选的时间大于现在的时间");
            return true;
        } else if (dt1.getTime() > curDate.getTime()) {
            Util.showToast(this, "预约时间必须小于当前时间");
            return false;
        } else {// 相等
            System.out.println("相等");
            return false;
        }
    }

    private String getData() {
        StringBuilder str = new StringBuilder().append(datePicker.getYear()).append("-")
                .append((datePicker.getMonth() + 1) < 10 ? "0" + (datePicker.getMonth() + 1)
                        : (datePicker.getMonth() + 1))
                .append("-")
                .append((datePicker.getDayOfMonth() < 10) ? "0" + datePicker.getDayOfMonth()
                        : datePicker.getDayOfMonth())
                .append(" ")
                .append((timePicker.getCurrentHour() < 10) ? "0" + timePicker.getCurrentHour()
                        : timePicker.getCurrentHour())
                .append(":").append((timePicker.getCurrentMinute() < 10) ? "0" + timePicker.getCurrentMinute()
                        : timePicker.getCurrentMinute());

        return str.toString();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.repair_date_other:
                back(false);
                break;
            case R.id.repair_date_sel_cancel:
                back(true);
                break;
            case R.id.repair_date_sel_ok:
                back(false);
                break;

            default:
                break;
        }

    }
}
