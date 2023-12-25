package dk.applimate.x16emu;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UiView extends RelativeLayout {

  public UiView(Context context) {
    super(context);

    LinearLayout linearLayout = new LinearLayout(context);
    linearLayout.setOrientation(LinearLayout.VERTICAL);

    TextView message = new TextView(context);
    message.setGravity(Gravity.CENTER);
    message.setText("this is a test");

    Button button = new Button(context);
    button.setText("I am a button!");
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_H);
      }
    });

    linearLayout.addView(message);
    linearLayout.addView(button);

    this.addView(linearLayout);
  }

}
