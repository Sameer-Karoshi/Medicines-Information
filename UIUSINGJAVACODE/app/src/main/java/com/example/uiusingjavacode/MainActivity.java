package com.example.uiusingjavacode;
import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.graphics.Color;
import android.widget.EditText;
import android.content.res.Resources;
import android.util.TypedValue;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout myLayout = new RelativeLayout(this);
        //This is basically object of the layout
        Button myButton = new Button(this);

        // my layout madhe button disnar
        myLayout.setBackgroundColor(Color.BLUE);
        myButton.setBackgroundColor(Color.GREEN);
        myButton.setText("Click Here");

        RelativeLayout.LayoutParams ButtonDetails =
             new    RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT

            );
        ButtonDetails.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ButtonDetails.addRule(RelativeLayout.CENTER_VERTICAL);

        myLayout.addView(myButton,ButtonDetails);

        EditText USERNAME = new EditText(this);
        myButton.setId(Integer.parseInt("1"));
        USERNAME.setId(Integer.parseInt("2"));

        RelativeLayout.LayoutParams USERNAMEDETAILS =
                new    RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT

                );
        USERNAMEDETAILS.addRule(RelativeLayout.ABOVE,myButton.getId());
        USERNAMEDETAILS.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //USERNAME.setWidth();
        Resources R = getResources();
        int Pixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,200,R.getDisplayMetrics());

        USERNAME.setWidth(Pixels);



        USERNAMEDETAILS.setMargins(0,0,0,50);

        myLayout.addView(USERNAME,USERNAMEDETAILS);





        setContentView(myLayout);


    }
}
