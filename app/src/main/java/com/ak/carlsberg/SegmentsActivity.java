package com.ak.carlsberg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SegmentsActivity extends AppCompatActivity {
		
	private static final String DB_NAME = "monolit.db";
	SharedPreferences sp;
	SQLiteDatabase db;
    Animation totop,right,left,bottom;
    TextView textView229,textView230,textView235,textView237,textView238,textView240,textView242,textView243,textView244,textView248,textView249,textView251
            ,textView253,textView255,textView256,textView260,textView261,textView262,textView263,textView264,textView265,textView266,textView267,textView268
            ,textView269,textView272,textView274,textView276,textView278,textView280,textView282,textView283,textView285,textView287,textView289,textView290
            ,textView291,textView293,textView295,textView298,textView304,textView300,textView301,textView302,textView310,textView311,textView312,textView306
            ,textView314,textView315,textView316,textView270;
	/** Called when the activity is first created. */
	
	@SuppressLint("DefaultLocale")
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.segments);
   
    int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
    Typeface fonts = Typeface.createFromAsset(getAssets(), "fonts/brlnsdb.TTF");
    if(actionBarTitleView != null){
        actionBarTitleView.setTypeface(fonts);
        actionBarTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
    }
    sp = PreferenceManager.getDefaultSharedPreferences(this);

        totop = AnimationUtils.loadAnimation(this, R.anim.totop);
        right = AnimationUtils.loadAnimation(this, R.anim.mytrans);
        left = AnimationUtils.loadAnimation(this, R.anim.mytransleft);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom);

        Typeface fontbauhs93 = Typeface.createFromAsset(getAssets(), "fonts/Days.ttf");
        textView229 = (TextView) findViewById(R.id.textView229);
        textView230 = (TextView) findViewById(R.id.textView230);
        textView235 = (TextView) findViewById(R.id.textView235);
        textView237 = (TextView) findViewById(R.id.textView237);
        textView238 = (TextView) findViewById(R.id.textView238);
        textView240 = (TextView) findViewById(R.id.textView240);
        textView242 = (TextView) findViewById(R.id.textView242);
        textView243 = (TextView) findViewById(R.id.textView243);
        textView244 = (TextView) findViewById(R.id.textView244);
        textView248 = (TextView) findViewById(R.id.textView248);
        textView249 = (TextView) findViewById(R.id.textView249);
        textView251 = (TextView) findViewById(R.id.textView251);
        textView253 = (TextView) findViewById(R.id.textView253);
        textView255 = (TextView) findViewById(R.id.textView255);
        textView256 = (TextView) findViewById(R.id.textView256);
        textView260 = (TextView) findViewById(R.id.textView260);
        textView261 = (TextView) findViewById(R.id.textView261);
        textView262 = (TextView) findViewById(R.id.textView262);
        textView263 = (TextView) findViewById(R.id.textView263);
        textView264 = (TextView) findViewById(R.id.textView264);
        textView266 = (TextView) findViewById(R.id.textView266);
        textView268 = (TextView) findViewById(R.id.textView268);
        textView269 = (TextView) findViewById(R.id.textView269);
        textView270 = (TextView) findViewById(R.id.textView270);
        textView272 = (TextView) findViewById(R.id.textView272);
        textView274 = (TextView) findViewById(R.id.textView274);
        textView276 = (TextView) findViewById(R.id.textView276);
        textView278 = (TextView) findViewById(R.id.textView278);
        textView280 = (TextView) findViewById(R.id.textView280);
        textView282 = (TextView) findViewById(R.id.textView282);
        textView283 = (TextView) findViewById(R.id.textView283);
        textView285 = (TextView) findViewById(R.id.textView285);
        textView287 = (TextView) findViewById(R.id.textView287);
        textView289 = (TextView) findViewById(R.id.textView289);
        textView290 = (TextView) findViewById(R.id.textView290);
        textView291 = (TextView) findViewById(R.id.textView291);
        textView293 = (TextView) findViewById(R.id.textView293);
        textView295 = (TextView) findViewById(R.id.textView295);
        textView298 = (TextView) findViewById(R.id.textView298);
        textView304 = (TextView) findViewById(R.id.textView304);
        textView300 = (TextView) findViewById(R.id.textView300);
        textView301 = (TextView) findViewById(R.id.textView301);
        textView302 = (TextView) findViewById(R.id.textView302);
        textView310 = (TextView) findViewById(R.id.textView310);
        textView311 = (TextView) findViewById(R.id.textView311);
        textView312 = (TextView) findViewById(R.id.textView312);
        textView306 = (TextView) findViewById(R.id.textView306);
        textView314 = (TextView) findViewById(R.id.textView314);
        textView315 = (TextView) findViewById(R.id.textView315);
        textView316 = (TextView) findViewById(R.id.textView316);

        textView229.setTypeface(fontbauhs93);
        textView230.setTypeface(fontbauhs93);
        textView235.setTypeface(fontbauhs93);
        textView237.setTypeface(fontbauhs93);
        textView238.setTypeface(fontbauhs93);
        textView240.setTypeface(fontbauhs93);
        textView242.setTypeface(fontbauhs93);
        textView243.setTypeface(fontbauhs93);
        textView244.setTypeface(fontbauhs93);
        textView248.setTypeface(fontbauhs93);
        textView249.setTypeface(fontbauhs93);
        textView251.setTypeface(fontbauhs93);
        textView253.setTypeface(fontbauhs93);
        textView255.setTypeface(fontbauhs93);
        textView256.setTypeface(fontbauhs93);
        textView260.setTypeface(fontbauhs93);
        textView261.setTypeface(fontbauhs93);
        textView262.setTypeface(fontbauhs93);
        textView263.setTypeface(fontbauhs93);
        textView264.setTypeface(fontbauhs93);
        textView266.setTypeface(fontbauhs93);
        textView268.setTypeface(fontbauhs93);
        textView269.setTypeface(fontbauhs93);
        textView270.setTypeface(fontbauhs93);
        textView272.setTypeface(fontbauhs93);
        textView274.setTypeface(fontbauhs93);
        textView276.setTypeface(fontbauhs93);
        textView278.setTypeface(fontbauhs93);
        textView280.setTypeface(fontbauhs93);
        textView282.setTypeface(fontbauhs93);
        textView283.setTypeface(fontbauhs93);
        textView285.setTypeface(fontbauhs93);
        textView287.setTypeface(fontbauhs93);
        textView289.setTypeface(fontbauhs93);
        textView290.setTypeface(fontbauhs93);
        textView291.setTypeface(fontbauhs93);
        textView293.setTypeface(fontbauhs93);
        textView295.setTypeface(fontbauhs93);
        textView298.setTypeface(fontbauhs93);
        textView304.setTypeface(fontbauhs93);
        textView300.setTypeface(fontbauhs93);
        textView301.setTypeface(fontbauhs93);
        textView302.setTypeface(fontbauhs93);
        textView310.setTypeface(fontbauhs93);
        textView311.setTypeface(fontbauhs93);
        textView312.setTypeface(fontbauhs93);
        textView306.setTypeface(fontbauhs93);
        textView314.setTypeface(fontbauhs93);
        textView315.setTypeface(fontbauhs93);
        textView316.setTypeface(fontbauhs93);

        Button sbros = (Button) findViewById(R.id.button16);
        OnClickListener refresh = new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(SegmentsActivity.this, SegmentsActivity.class);
                startActivity(intent);
            }
        };
        sbros.setOnClickListener(refresh);

        Button button14 = (Button) findViewById(R.id.button14);
        OnClickListener off = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id0);
                layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id1);
                layot1.setAnimation(totop);
                layot1.setVisibility(View.VISIBLE);
            }
        };
        button14.setOnClickListener(off);

        Button radioButton = (Button) findViewById(R.id.radioButton);
        OnClickListener hide = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id1);
                layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id2);
                layot1.setAnimation(left);
                layot1.setVisibility(View.VISIBLE);
            }
        };
        radioButton.setOnClickListener(hide);

        Button button2 = (Button) findViewById(R.id.button18);
        OnClickListener hide2 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id2);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id3);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button2.setOnClickListener(hide2);

        Button button17 = (Button) findViewById(R.id.button17);
        OnClickListener hide3 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id2);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id3);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id4);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button17.setOnClickListener(hide3);

        Button button19 = (Button) findViewById(R.id.button19);
        OnClickListener hide4 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id4);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id5);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button19.setOnClickListener(hide4);

        Button button20 = (Button) findViewById(R.id.button20);
        OnClickListener hide5 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id4);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id5);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id6);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button20.setOnClickListener(hide5);


        Button button21 = (Button) findViewById(R.id.button21);
        OnClickListener hide6 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id6);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id7);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button21.setOnClickListener(hide6);

        Button button22 = (Button) findViewById(R.id.button22);
        OnClickListener hide7 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id6);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id7);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id8);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button22.setOnClickListener(hide7);

        Button button23 = (Button) findViewById(R.id.button23);
        OnClickListener hide8 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id8);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id9);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button23.setOnClickListener(hide8);

        Button button24 = (Button) findViewById(R.id.button24);
        OnClickListener hide9 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id8);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id9);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id10);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button24.setOnClickListener(hide9);

        Button button25 = (Button) findViewById(R.id.button25);
        OnClickListener hide10 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id6);
                LinearLayout layot = (LinearLayout) findViewById(R.id.id7);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id8);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id9);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id10);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                layot3.setVisibility(View.GONE);
                layot4.setVisibility(View.GONE);
                layot5.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id11);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);

            }
        };
        button25.setOnClickListener(hide10);

        Button button26 = (Button) findViewById(R.id.button26);
        OnClickListener hide11 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id11);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id12);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button26.setOnClickListener(hide11);

        Button button27 = (Button) findViewById(R.id.button27);
        OnClickListener hide12 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id11);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id12);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id13);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button27.setOnClickListener(hide12);

        Button button28 = (Button) findViewById(R.id.button28);
        OnClickListener hide13 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id13);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id14);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button28.setOnClickListener(hide13);

        Button button29 = (Button) findViewById(R.id.button29);
        OnClickListener hide14 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id13);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id14);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id15);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button29.setOnClickListener(hide14);

        Button button30 = (Button) findViewById(R.id.button30);
        OnClickListener hide15 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id13);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id14);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id15);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                layot3.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id16);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button30.setOnClickListener(hide15);

        Button radioButton2 = (Button) findViewById(R.id.radioButton2);
        OnClickListener hide16 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id1);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id2);layot2.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id3);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id4);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id5);layot5.setVisibility(View.GONE);
                LinearLayout layot6 = (LinearLayout) findViewById(R.id.id6);layot6.setVisibility(View.GONE);
                LinearLayout layot7 = (LinearLayout) findViewById(R.id.id7);layot7.setVisibility(View.GONE);
                LinearLayout layot8 = (LinearLayout) findViewById(R.id.id8);layot8.setVisibility(View.GONE);
                LinearLayout layot9 = (LinearLayout) findViewById(R.id.id9);layot9.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id10);layot10.setVisibility(View.GONE);
                LinearLayout layot11 = (LinearLayout) findViewById(R.id.id11);layot11.setVisibility(View.GONE);
                LinearLayout layot12 = (LinearLayout) findViewById(R.id.id12);layot12.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id13);layot13.setVisibility(View.GONE);
                LinearLayout layot14 = (LinearLayout) findViewById(R.id.id14);layot14.setVisibility(View.GONE);
                LinearLayout layot15 = (LinearLayout) findViewById(R.id.id15);layot15.setVisibility(View.GONE);
                LinearLayout layot16 = (LinearLayout) findViewById(R.id.id16);layot16.setVisibility(View.GONE);
                LinearLayout layot17 = (LinearLayout) findViewById(R.id.id17);
                layot17.setAnimation(left);
                layot17.setVisibility(View.VISIBLE);
            }
        };
        radioButton2.setOnClickListener(hide16);

        Button button31 = (Button) findViewById(R.id.button31);
        OnClickListener hide17 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id17);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id18);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button31.setOnClickListener(hide17);

        Button button32 = (Button) findViewById(R.id.button32);
        OnClickListener hide18 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id17);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id18);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id19);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button32.setOnClickListener(hide18);

        Button button34 = (Button) findViewById(R.id.button34);
        OnClickListener hide19 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id19);
                layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id20);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button34.setOnClickListener(hide19);

        Button button35 = (Button) findViewById(R.id.button35);
        OnClickListener hide20 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id19);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id20);
                layot.setVisibility(View.GONE);
                layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id21);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button35.setOnClickListener(hide20);

        Button button33 = (Button) findViewById(R.id.button33);
        OnClickListener hide21 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id17);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id18);layot1.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id19);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id20);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id21);layot5.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id22);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button33.setOnClickListener(hide21);

        Button button36 = (Button) findViewById(R.id.button36);
        OnClickListener hide22 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id22);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id23);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button36.setOnClickListener(hide22);

        Button button37 = (Button) findViewById(R.id.button37);
        OnClickListener hide23 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id22);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id23);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id24);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button37.setOnClickListener(hide23);

        Button button38 = (Button) findViewById(R.id.button38);
        OnClickListener hide24 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id24);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id25);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button38.setOnClickListener(hide24);

        Button button39 = (Button) findViewById(R.id.button39);
        OnClickListener hide25 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id24);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id25);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id26);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button39.setOnClickListener(hide25);

        Button button41 = (Button) findViewById(R.id.button41);
        OnClickListener hide26 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id26);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id27);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button41.setOnClickListener(hide26);

        Button button42 = (Button) findViewById(R.id.button42);
        OnClickListener hide27 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id26);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id27);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id28);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button42.setOnClickListener(hide27);

        Button button40 = (Button) findViewById(R.id.button40);
        OnClickListener hide28 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id24);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id25);layot1.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id26);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id27);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id28);layot5.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id29);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button40.setOnClickListener(hide28);

        Button button43 = (Button) findViewById(R.id.button43);
        OnClickListener hide29 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id29);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id30);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button43.setOnClickListener(hide29);

        Button button44 = (Button) findViewById(R.id.button44);
        OnClickListener hide30 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id29);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id30);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id31);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button44.setOnClickListener(hide30);

        Button radioButton3 = (Button) findViewById(R.id.radioButton3);
        OnClickListener hide31 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id2);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id1);layot1.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id3);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id4);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id5);layot5.setVisibility(View.GONE);
                LinearLayout layot6 = (LinearLayout) findViewById(R.id.id6);layot6.setVisibility(View.GONE);
                LinearLayout layot7 = (LinearLayout) findViewById(R.id.id7);layot7.setVisibility(View.GONE);
                LinearLayout layot8 = (LinearLayout) findViewById(R.id.id8);layot8.setVisibility(View.GONE);
                LinearLayout layot9 = (LinearLayout) findViewById(R.id.id9);layot9.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id10);layot10.setVisibility(View.GONE);
                LinearLayout layot11 = (LinearLayout) findViewById(R.id.id11);layot11.setVisibility(View.GONE);
                LinearLayout layot12 = (LinearLayout) findViewById(R.id.id12);layot12.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id13);layot13.setVisibility(View.GONE);
                LinearLayout layot14 = (LinearLayout) findViewById(R.id.id14);layot14.setVisibility(View.GONE);
                LinearLayout layot15 = (LinearLayout) findViewById(R.id.id15);layot15.setVisibility(View.GONE);
                LinearLayout layot16 = (LinearLayout) findViewById(R.id.id16);layot16.setVisibility(View.GONE);
                LinearLayout layot17 = (LinearLayout) findViewById(R.id.id17);layot17.setVisibility(View.GONE);
                LinearLayout layot18 = (LinearLayout) findViewById(R.id.id18);layot18.setVisibility(View.GONE);
                LinearLayout layot19 = (LinearLayout) findViewById(R.id.id19);layot19.setVisibility(View.GONE);
                LinearLayout layot20 = (LinearLayout) findViewById(R.id.id20);layot20.setVisibility(View.GONE);
                LinearLayout layot21 = (LinearLayout) findViewById(R.id.id21);layot21.setVisibility(View.GONE);
                LinearLayout layot22 = (LinearLayout) findViewById(R.id.id22);layot22.setVisibility(View.GONE);
                LinearLayout layot23 = (LinearLayout) findViewById(R.id.id23);layot23.setVisibility(View.GONE);
                LinearLayout layot24 = (LinearLayout) findViewById(R.id.id24);layot24.setVisibility(View.GONE);
                LinearLayout layot25 = (LinearLayout) findViewById(R.id.id25);layot25.setVisibility(View.GONE);
                LinearLayout layot26 = (LinearLayout) findViewById(R.id.id26);layot26.setVisibility(View.GONE);
                LinearLayout layot27 = (LinearLayout) findViewById(R.id.id27);layot27.setVisibility(View.GONE);
                LinearLayout layot28 = (LinearLayout) findViewById(R.id.id28);layot28.setVisibility(View.GONE);
                LinearLayout layot29 = (LinearLayout) findViewById(R.id.id29);layot29.setVisibility(View.GONE);
                LinearLayout layot30 = (LinearLayout) findViewById(R.id.id30);layot30.setVisibility(View.GONE);
                LinearLayout layot31 = (LinearLayout) findViewById(R.id.id31);layot31.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id32);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        radioButton3.setOnClickListener(hide31);

        Button radioButton4 = (Button) findViewById(R.id.radioButton4);
        OnClickListener hide32 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id2);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id1);layot1.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id3);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id4);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id5);layot5.setVisibility(View.GONE);
                LinearLayout layot6 = (LinearLayout) findViewById(R.id.id6);layot6.setVisibility(View.GONE);
                LinearLayout layot7 = (LinearLayout) findViewById(R.id.id7);layot7.setVisibility(View.GONE);
                LinearLayout layot8 = (LinearLayout) findViewById(R.id.id8);layot8.setVisibility(View.GONE);
                LinearLayout layot9 = (LinearLayout) findViewById(R.id.id9);layot9.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id10);layot10.setVisibility(View.GONE);
                LinearLayout layot11 = (LinearLayout) findViewById(R.id.id11);layot11.setVisibility(View.GONE);
                LinearLayout layot12 = (LinearLayout) findViewById(R.id.id12);layot12.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id13);layot13.setVisibility(View.GONE);
                LinearLayout layot14 = (LinearLayout) findViewById(R.id.id14);layot14.setVisibility(View.GONE);
                LinearLayout layot15 = (LinearLayout) findViewById(R.id.id15);layot15.setVisibility(View.GONE);
                LinearLayout layot16 = (LinearLayout) findViewById(R.id.id16);layot16.setVisibility(View.GONE);
                LinearLayout layot17 = (LinearLayout) findViewById(R.id.id17);layot17.setVisibility(View.GONE);
                LinearLayout layot18 = (LinearLayout) findViewById(R.id.id18);layot18.setVisibility(View.GONE);
                LinearLayout layot19 = (LinearLayout) findViewById(R.id.id19);layot19.setVisibility(View.GONE);
                LinearLayout layot20 = (LinearLayout) findViewById(R.id.id20);layot20.setVisibility(View.GONE);
                LinearLayout layot21 = (LinearLayout) findViewById(R.id.id21);layot21.setVisibility(View.GONE);
                LinearLayout layot22 = (LinearLayout) findViewById(R.id.id22);layot22.setVisibility(View.GONE);
                LinearLayout layot23 = (LinearLayout) findViewById(R.id.id23);layot23.setVisibility(View.GONE);
                LinearLayout layot24 = (LinearLayout) findViewById(R.id.id24);layot24.setVisibility(View.GONE);
                LinearLayout layot25 = (LinearLayout) findViewById(R.id.id25);layot25.setVisibility(View.GONE);
                LinearLayout layot26 = (LinearLayout) findViewById(R.id.id26);layot26.setVisibility(View.GONE);
                LinearLayout layot27 = (LinearLayout) findViewById(R.id.id27);layot27.setVisibility(View.GONE);
                LinearLayout layot28 = (LinearLayout) findViewById(R.id.id28);layot28.setVisibility(View.GONE);
                LinearLayout layot29 = (LinearLayout) findViewById(R.id.id29);layot29.setVisibility(View.GONE);
                LinearLayout layot30 = (LinearLayout) findViewById(R.id.id30);layot30.setVisibility(View.GONE);
                LinearLayout layot31 = (LinearLayout) findViewById(R.id.id31);layot31.setVisibility(View.GONE);
                LinearLayout layot32 = (LinearLayout) findViewById(R.id.id32);layot32.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id33);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        radioButton4.setOnClickListener(hide32);

        Button button45 = (Button) findViewById(R.id.button45);
        OnClickListener hide33 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id33);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id34);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button45.setOnClickListener(hide33);

        Button button46 = (Button) findViewById(R.id.button46);
        OnClickListener hide34 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id33);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id34);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id35);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button46.setOnClickListener(hide34);

        Button button47 = (Button) findViewById(R.id.button47);
        OnClickListener hide35 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id35);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id36);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button47.setOnClickListener(hide35);

        Button button48 = (Button) findViewById(R.id.button48);
        OnClickListener hide36 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id35);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id36);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id37);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button48.setOnClickListener(hide36);

        Button radioButton5 = (Button) findViewById(R.id.radioButton5);
        OnClickListener hide37 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id2);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id1);layot1.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id3);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id4);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id5);layot5.setVisibility(View.GONE);
                LinearLayout layot6 = (LinearLayout) findViewById(R.id.id6);layot6.setVisibility(View.GONE);
                LinearLayout layot7 = (LinearLayout) findViewById(R.id.id7);layot7.setVisibility(View.GONE);
                LinearLayout layot8 = (LinearLayout) findViewById(R.id.id8);layot8.setVisibility(View.GONE);
                LinearLayout layot9 = (LinearLayout) findViewById(R.id.id9);layot9.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id10);layot10.setVisibility(View.GONE);
                LinearLayout layot11 = (LinearLayout) findViewById(R.id.id11);layot11.setVisibility(View.GONE);
                LinearLayout layot12 = (LinearLayout) findViewById(R.id.id12);layot12.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id13);layot13.setVisibility(View.GONE);
                LinearLayout layot14 = (LinearLayout) findViewById(R.id.id14);layot14.setVisibility(View.GONE);
                LinearLayout layot15 = (LinearLayout) findViewById(R.id.id15);layot15.setVisibility(View.GONE);
                LinearLayout layot16 = (LinearLayout) findViewById(R.id.id16);layot16.setVisibility(View.GONE);
                LinearLayout layot17 = (LinearLayout) findViewById(R.id.id17);layot17.setVisibility(View.GONE);
                LinearLayout layot18 = (LinearLayout) findViewById(R.id.id18);layot18.setVisibility(View.GONE);
                LinearLayout layot19 = (LinearLayout) findViewById(R.id.id19);layot19.setVisibility(View.GONE);
                LinearLayout layot20 = (LinearLayout) findViewById(R.id.id20);layot20.setVisibility(View.GONE);
                LinearLayout layot21 = (LinearLayout) findViewById(R.id.id21);layot21.setVisibility(View.GONE);
                LinearLayout layot22 = (LinearLayout) findViewById(R.id.id22);layot22.setVisibility(View.GONE);
                LinearLayout layot23 = (LinearLayout) findViewById(R.id.id23);layot23.setVisibility(View.GONE);
                LinearLayout layot24 = (LinearLayout) findViewById(R.id.id24);layot24.setVisibility(View.GONE);
                LinearLayout layot25 = (LinearLayout) findViewById(R.id.id25);layot25.setVisibility(View.GONE);
                LinearLayout layot26 = (LinearLayout) findViewById(R.id.id26);layot26.setVisibility(View.GONE);
                LinearLayout layot27 = (LinearLayout) findViewById(R.id.id27);layot27.setVisibility(View.GONE);
                LinearLayout layot28 = (LinearLayout) findViewById(R.id.id28);layot28.setVisibility(View.GONE);
                LinearLayout layot29 = (LinearLayout) findViewById(R.id.id29);layot29.setVisibility(View.GONE);
                LinearLayout layot30 = (LinearLayout) findViewById(R.id.id30);layot30.setVisibility(View.GONE);
                LinearLayout layot31 = (LinearLayout) findViewById(R.id.id31);layot31.setVisibility(View.GONE);
                LinearLayout layot32 = (LinearLayout) findViewById(R.id.id32);layot32.setVisibility(View.GONE);
                LinearLayout layot33 = (LinearLayout) findViewById(R.id.id33);layot33.setVisibility(View.GONE);
                LinearLayout layot34 = (LinearLayout) findViewById(R.id.id34);layot34.setVisibility(View.GONE);
                LinearLayout layot35 = (LinearLayout) findViewById(R.id.id35);layot35.setVisibility(View.GONE);
                LinearLayout layot36 = (LinearLayout) findViewById(R.id.id36);layot36.setVisibility(View.GONE);
                LinearLayout layot37 = (LinearLayout) findViewById(R.id.id37);layot37.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id38);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        radioButton5.setOnClickListener(hide37);

        /**********************************OFF TRADE*****************************************************/

        Button button15 = (Button) findViewById(R.id.button15);
        OnClickListener hide38 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id0);layot.setVisibility(View.GONE);
                hideOnTrade();
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id39);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button15.setOnClickListener(hide38);

        Button button52 = (Button) findViewById(R.id.button52);
        OnClickListener hide39 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id0);layot.setVisibility(View.GONE);
                hideOnTrade();
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id39);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id40);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button52.setOnClickListener(hide39);

        Button button50 = (Button) findViewById(R.id.button50);
        OnClickListener hide40 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id39);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id40);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id41);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button50.setOnClickListener(hide40);

        Button button53 = (Button) findViewById(R.id.button53);
        OnClickListener hide41 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id41);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id42);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button53.setOnClickListener(hide41);

        Button button54 = (Button) findViewById(R.id.button54);
        OnClickListener hide42 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id41);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id42);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id43);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button54.setOnClickListener(hide42);

        Button button55 = (Button) findViewById(R.id.button55);
        OnClickListener hide43 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id43);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id44);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button55.setOnClickListener(hide43);

        Button button56 = (Button) findViewById(R.id.button56);
        OnClickListener hide44 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id43);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id44);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id45);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button56.setOnClickListener(hide44);

        Button button57 = (Button) findViewById(R.id.button57);
        OnClickListener hide45 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id45);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id46);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button57.setOnClickListener(hide45);

        Button button58 = (Button) findViewById(R.id.button58);
        OnClickListener hide46 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id45);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id46);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id47);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button58.setOnClickListener(hide46);

        Button button59 = (Button) findViewById(R.id.button59);
        OnClickListener hide47 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id47);layot.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id48);
                layot2.setAnimation(bottom);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button59.setOnClickListener(hide47);

        Button button60 = (Button) findViewById(R.id.button60);
        OnClickListener hide48 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id47);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id48);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id49);
                layot2.setAnimation(left);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button60.setOnClickListener(hide48);

        Button button61 = (Button) findViewById(R.id.button61);
        OnClickListener hide49 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id49);layot.setVisibility(View.GONE);
               LinearLayout layot2 = (LinearLayout) findViewById(R.id.id50);
                layot2.setAnimation(totop);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button61.setOnClickListener(hide49);

        Button button62 = (Button) findViewById(R.id.button62);
        OnClickListener hide50 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id49);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id50);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id51);
                layot2.setAnimation(right);
                layot2.setVisibility(View.VISIBLE);
            }
        };
        button62.setOnClickListener(hide50);

        Button button51 = (Button) findViewById(R.id.button51);
        OnClickListener hide51 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id39);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id40);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id41);layot2.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id42);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id43);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id44);layot5.setVisibility(View.GONE);
                LinearLayout layot6 = (LinearLayout) findViewById(R.id.id45);layot6.setVisibility(View.GONE);
                LinearLayout layot7 = (LinearLayout) findViewById(R.id.id46);layot7.setVisibility(View.GONE);
                LinearLayout layot8 = (LinearLayout) findViewById(R.id.id47);layot8.setVisibility(View.GONE);
                LinearLayout layot9 = (LinearLayout) findViewById(R.id.id48);layot9.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id49);layot10.setVisibility(View.GONE);
                LinearLayout layot11 = (LinearLayout) findViewById(R.id.id50);layot11.setVisibility(View.GONE);
                LinearLayout layot12 = (LinearLayout) findViewById(R.id.id51);layot12.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id52);
                layot13.setAnimation(totop);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button51.setOnClickListener(hide51);

        Button button63 = (Button) findViewById(R.id.button63);
        OnClickListener hide52 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot12 = (LinearLayout) findViewById(R.id.id52);layot12.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id53);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button63.setOnClickListener(hide52);

        Button button64 = (Button) findViewById(R.id.button64);
        OnClickListener hide53 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id52);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id53);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id54);
                layot13.setAnimation(left);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button64.setOnClickListener(hide53);

        Button button65 = (Button) findViewById(R.id.button65);
        OnClickListener hide54 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id54);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id55);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button65.setOnClickListener(hide54);

        Button button66 = (Button) findViewById(R.id.button66);
        OnClickListener hide55 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id54);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id55);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id56);
                layot13.setAnimation(left);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button66.setOnClickListener(hide55);

        Button button67 = (Button) findViewById(R.id.button67);
        OnClickListener hide56 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id56);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id57);
                layot13.setAnimation(totop);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button67.setOnClickListener(hide56);

        Button button68 = (Button) findViewById(R.id.button68);
        OnClickListener hide57 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id56);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id57);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id58);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button68.setOnClickListener(hide57);

        Button button69 = (Button) findViewById(R.id.button69);
        OnClickListener hide58 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id56);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id57);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id58);layot2.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id59);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button69.setOnClickListener(hide58);

        Button button49 = (Button) findViewById(R.id.button49);
        OnClickListener hide59 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layot = (LinearLayout) findViewById(R.id.id0);layot.setVisibility(View.GONE);
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id60);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button49.setOnClickListener(hide59);

        Button button70 = (Button) findViewById(R.id.button70);
        OnClickListener hide60 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id60);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id61);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button70.setOnClickListener(hide60);

        Button button71 = (Button) findViewById(R.id.button71);
        OnClickListener hide61 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id60);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id61);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id62);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button71.setOnClickListener(hide61);

        Button button73 = (Button) findViewById(R.id.button73);
        OnClickListener hide62 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id62);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id63);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button73.setOnClickListener(hide62);

        Button button72 = (Button) findViewById(R.id.button72);
        OnClickListener hide63 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id62);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id63);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id64);
                layot13.setAnimation(left);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button72.setOnClickListener(hide63);

        Button button74 = (Button) findViewById(R.id.button74);
        OnClickListener hide64 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id64);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id65);
                layot13.setAnimation(left);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button74.setOnClickListener(hide64);

        Button button77 = (Button) findViewById(R.id.button77);
        OnClickListener hide65 = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id65);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id66);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button77.setOnClickListener(hide65);

        Button button78 = (Button) findViewById(R.id.button78);
        OnClickListener hide66= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id65);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id66);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id67a);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button78.setOnClickListener(hide66);

        Button button82 = (Button) findViewById(R.id.button82);
        OnClickListener hide70= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id67a);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id67b);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button82.setOnClickListener(hide70);

        Button button83 = (Button) findViewById(R.id.button83);
        OnClickListener hide71= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id67a);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id67b);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id67);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button83.setOnClickListener(hide71);

        Button button79 = (Button) findViewById(R.id.button79);
        OnClickListener hide67= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id67);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id68);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button79.setOnClickListener(hide67);

        Button button80 = (Button) findViewById(R.id.button80);
        OnClickListener hide68= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id67);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id68);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id69);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button80.setOnClickListener(hide68);

        Button button81 = (Button) findViewById(R.id.button81);
        OnClickListener hide69= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id67);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id68);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id69);layot2.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id70);
                layot13.setAnimation(left);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button81.setOnClickListener(hide69);

        Button button75 = (Button) findViewById(R.id.button75);
        OnClickListener hide72= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id60);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id61);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id62);layot2.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id63);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id64);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id65);layot5.setVisibility(View.GONE);
                LinearLayout layot6 = (LinearLayout) findViewById(R.id.id66);layot6.setVisibility(View.GONE);
                LinearLayout layot7 = (LinearLayout) findViewById(R.id.id67);layot7.setVisibility(View.GONE);
                LinearLayout layot7a = (LinearLayout) findViewById(R.id.id67a);layot7a.setVisibility(View.GONE);
                LinearLayout layot7b = (LinearLayout) findViewById(R.id.id67b);layot7b.setVisibility(View.GONE);
                LinearLayout layot8 = (LinearLayout) findViewById(R.id.id68);layot8.setVisibility(View.GONE);
                LinearLayout layot9 = (LinearLayout) findViewById(R.id.id69);layot9.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id70);layot10.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id71);
                layot13.setAnimation(totop);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button75.setOnClickListener(hide72);

        Button button86 = (Button) findViewById(R.id.button86);
        OnClickListener hide73= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id71);layot10.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id72);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button86.setOnClickListener(hide73);

        Button button87 = (Button) findViewById(R.id.button87);
        OnClickListener hide74= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id71);layot.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id72);layot10.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id73);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button87.setOnClickListener(hide74);

        Button button88 = (Button) findViewById(R.id.button88);
        OnClickListener hide75= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id73);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id74);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button88.setOnClickListener(hide75);

        Button button89 = (Button) findViewById(R.id.button89);
        OnClickListener hide76= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id73);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id74);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id75);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button89.setOnClickListener(hide76);

        Button button90 = (Button) findViewById(R.id.button90);
        OnClickListener hide77= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id73);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id74);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id75);layot2.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id76);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button90.setOnClickListener(hide77);

        Button button76 = (Button) findViewById(R.id.button76);
        OnClickListener hide78= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot = (LinearLayout) findViewById(R.id.id60);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id61);layot1.setVisibility(View.GONE);
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id62);layot2.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id63);layot3.setVisibility(View.GONE);
                LinearLayout layot4 = (LinearLayout) findViewById(R.id.id64);layot4.setVisibility(View.GONE);
                LinearLayout layot5 = (LinearLayout) findViewById(R.id.id65);layot5.setVisibility(View.GONE);
                LinearLayout layot6 = (LinearLayout) findViewById(R.id.id66);layot6.setVisibility(View.GONE);
                LinearLayout layot7 = (LinearLayout) findViewById(R.id.id67);layot7.setVisibility(View.GONE);
                LinearLayout layot7a = (LinearLayout) findViewById(R.id.id67a);layot7a.setVisibility(View.GONE);
                LinearLayout layot7b = (LinearLayout) findViewById(R.id.id67b);layot7b.setVisibility(View.GONE);
                LinearLayout layot8 = (LinearLayout) findViewById(R.id.id68);layot8.setVisibility(View.GONE);
                LinearLayout layot9 = (LinearLayout) findViewById(R.id.id69);layot9.setVisibility(View.GONE);
                LinearLayout layot10 = (LinearLayout) findViewById(R.id.id70);layot10.setVisibility(View.GONE);
                LinearLayout layot11 = (LinearLayout) findViewById(R.id.id71);layot11.setVisibility(View.GONE);
                LinearLayout layot12 = (LinearLayout) findViewById(R.id.id72);layot12.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id73);layot13.setVisibility(View.GONE);
                LinearLayout layot14 = (LinearLayout) findViewById(R.id.id74);layot14.setVisibility(View.GONE);
                LinearLayout layot15 = (LinearLayout) findViewById(R.id.id75);layot15.setVisibility(View.GONE);
                LinearLayout layot16 = (LinearLayout) findViewById(R.id.id76);layot16.setVisibility(View.GONE);
                LinearLayout layot17 = (LinearLayout) findViewById(R.id.id77);
                layot17.setAnimation(left);
                layot17.setVisibility(View.VISIBLE);
            }
        };
        button76.setOnClickListener(hide78);

        Button button84 = (Button) findViewById(R.id.button84);
        OnClickListener hide79= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id77);layot2.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id78);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button84.setOnClickListener(hide79);

        Button button85 = (Button) findViewById(R.id.button85);
        OnClickListener hide80= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id77);layot2.setVisibility(View.GONE);
                LinearLayout layot3 = (LinearLayout) findViewById(R.id.id78);layot3.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id79);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button85.setOnClickListener(hide80);

        Button button91 = (Button) findViewById(R.id.button91);
        OnClickListener hide81= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id79);layot2.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id80);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button91.setOnClickListener(hide81);

        Button button92 = (Button) findViewById(R.id.button92);
        OnClickListener hide82= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id79);layot2.setVisibility(View.GONE);
                LinearLayout layot = (LinearLayout) findViewById(R.id.id80);layot.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id81);
                layot13.setAnimation(bottom);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button92.setOnClickListener(hide82);

        Button button93 = (Button) findViewById(R.id.button93);
        OnClickListener hide83= new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOnTrade();
                hideOffTrade();
                LinearLayout layot2 = (LinearLayout) findViewById(R.id.id79);layot2.setVisibility(View.GONE);
                LinearLayout layot = (LinearLayout) findViewById(R.id.id80);layot.setVisibility(View.GONE);
                LinearLayout layot1 = (LinearLayout) findViewById(R.id.id81);layot1.setVisibility(View.GONE);
                LinearLayout layot13 = (LinearLayout) findViewById(R.id.id82);
                layot13.setAnimation(right);
                layot13.setVisibility(View.VISIBLE);
            }
        };
        button93.setOnClickListener(hide83);

    }

    public void hideOnTrade (){
        LinearLayout layot = (LinearLayout) findViewById(R.id.id2);layot.setVisibility(View.GONE);
        LinearLayout layot1 = (LinearLayout) findViewById(R.id.id1);layot1.setVisibility(View.GONE);
        LinearLayout layot3 = (LinearLayout) findViewById(R.id.id3);layot3.setVisibility(View.GONE);
        LinearLayout layot4 = (LinearLayout) findViewById(R.id.id4);layot4.setVisibility(View.GONE);
        LinearLayout layot5 = (LinearLayout) findViewById(R.id.id5);layot5.setVisibility(View.GONE);
        LinearLayout layot6 = (LinearLayout) findViewById(R.id.id6);layot6.setVisibility(View.GONE);
        LinearLayout layot7 = (LinearLayout) findViewById(R.id.id7);layot7.setVisibility(View.GONE);
        LinearLayout layot8 = (LinearLayout) findViewById(R.id.id8);layot8.setVisibility(View.GONE);
        LinearLayout layot9 = (LinearLayout) findViewById(R.id.id9);layot9.setVisibility(View.GONE);
        LinearLayout layot10 = (LinearLayout) findViewById(R.id.id10);layot10.setVisibility(View.GONE);
        LinearLayout layot11 = (LinearLayout) findViewById(R.id.id11);layot11.setVisibility(View.GONE);
        LinearLayout layot12 = (LinearLayout) findViewById(R.id.id12);layot12.setVisibility(View.GONE);
        LinearLayout layot13 = (LinearLayout) findViewById(R.id.id13);layot13.setVisibility(View.GONE);
        LinearLayout layot14 = (LinearLayout) findViewById(R.id.id14);layot14.setVisibility(View.GONE);
        LinearLayout layot15 = (LinearLayout) findViewById(R.id.id15);layot15.setVisibility(View.GONE);
        LinearLayout layot16 = (LinearLayout) findViewById(R.id.id16);layot16.setVisibility(View.GONE);
        LinearLayout layot17 = (LinearLayout) findViewById(R.id.id17);layot17.setVisibility(View.GONE);
        LinearLayout layot18 = (LinearLayout) findViewById(R.id.id18);layot18.setVisibility(View.GONE);
        LinearLayout layot19 = (LinearLayout) findViewById(R.id.id19);layot19.setVisibility(View.GONE);
        LinearLayout layot20 = (LinearLayout) findViewById(R.id.id20);layot20.setVisibility(View.GONE);
        LinearLayout layot21 = (LinearLayout) findViewById(R.id.id21);layot21.setVisibility(View.GONE);
        LinearLayout layot22 = (LinearLayout) findViewById(R.id.id22);layot22.setVisibility(View.GONE);
        LinearLayout layot23 = (LinearLayout) findViewById(R.id.id23);layot23.setVisibility(View.GONE);
        LinearLayout layot24 = (LinearLayout) findViewById(R.id.id24);layot24.setVisibility(View.GONE);
        LinearLayout layot25 = (LinearLayout) findViewById(R.id.id25);layot25.setVisibility(View.GONE);
        LinearLayout layot26 = (LinearLayout) findViewById(R.id.id26);layot26.setVisibility(View.GONE);
        LinearLayout layot27 = (LinearLayout) findViewById(R.id.id27);layot27.setVisibility(View.GONE);
        LinearLayout layot28 = (LinearLayout) findViewById(R.id.id28);layot28.setVisibility(View.GONE);
        LinearLayout layot29 = (LinearLayout) findViewById(R.id.id29);layot29.setVisibility(View.GONE);
        LinearLayout layot30 = (LinearLayout) findViewById(R.id.id30);layot30.setVisibility(View.GONE);
        LinearLayout layot31 = (LinearLayout) findViewById(R.id.id31);layot31.setVisibility(View.GONE);
        LinearLayout layot32 = (LinearLayout) findViewById(R.id.id32);layot32.setVisibility(View.GONE);
        LinearLayout layot33 = (LinearLayout) findViewById(R.id.id33);layot33.setVisibility(View.GONE);
        LinearLayout layot34 = (LinearLayout) findViewById(R.id.id34);layot34.setVisibility(View.GONE);
        LinearLayout layot35 = (LinearLayout) findViewById(R.id.id35);layot35.setVisibility(View.GONE);
        LinearLayout layot36 = (LinearLayout) findViewById(R.id.id36);layot36.setVisibility(View.GONE);
        LinearLayout layot37 = (LinearLayout) findViewById(R.id.id37);layot37.setVisibility(View.GONE);
        LinearLayout layot38 = (LinearLayout) findViewById(R.id.id38);layot38.setVisibility(View.GONE);
    }

    public void hideOffTrade (){
        LinearLayout layot39 = (LinearLayout) findViewById(R.id.id39);layot39.setVisibility(View.GONE);
        LinearLayout layot40 = (LinearLayout) findViewById(R.id.id40);layot40.setVisibility(View.GONE);
        LinearLayout layot41 = (LinearLayout) findViewById(R.id.id41);layot41.setVisibility(View.GONE);
        LinearLayout layot42 = (LinearLayout) findViewById(R.id.id42);layot42.setVisibility(View.GONE);
        LinearLayout layot43 = (LinearLayout) findViewById(R.id.id43);layot43.setVisibility(View.GONE);
        LinearLayout layot44 = (LinearLayout) findViewById(R.id.id44);layot44.setVisibility(View.GONE);
        LinearLayout layot45 = (LinearLayout) findViewById(R.id.id45);layot45.setVisibility(View.GONE);
        LinearLayout layot46 = (LinearLayout) findViewById(R.id.id46);layot46.setVisibility(View.GONE);
        LinearLayout layot47 = (LinearLayout) findViewById(R.id.id47);layot47.setVisibility(View.GONE);
        LinearLayout layot48 = (LinearLayout) findViewById(R.id.id48);layot48.setVisibility(View.GONE);
        LinearLayout layot49 = (LinearLayout) findViewById(R.id.id49);layot49.setVisibility(View.GONE);
        LinearLayout layot50 = (LinearLayout) findViewById(R.id.id50);layot50.setVisibility(View.GONE);
        LinearLayout layot51 = (LinearLayout) findViewById(R.id.id51);layot51.setVisibility(View.GONE);
        LinearLayout layot52 = (LinearLayout) findViewById(R.id.id52);layot52.setVisibility(View.GONE);
        LinearLayout layot53 = (LinearLayout) findViewById(R.id.id53);layot53.setVisibility(View.GONE);
        LinearLayout layot54 = (LinearLayout) findViewById(R.id.id54);layot54.setVisibility(View.GONE);
        LinearLayout layot55 = (LinearLayout) findViewById(R.id.id55);layot55.setVisibility(View.GONE);
        LinearLayout layot56 = (LinearLayout) findViewById(R.id.id56);layot56.setVisibility(View.GONE);
        LinearLayout layot57 = (LinearLayout) findViewById(R.id.id57);layot57.setVisibility(View.GONE);
        LinearLayout layot58 = (LinearLayout) findViewById(R.id.id58);layot58.setVisibility(View.GONE);
        LinearLayout layot59 = (LinearLayout) findViewById(R.id.id59);layot59.setVisibility(View.GONE);

    }
}
