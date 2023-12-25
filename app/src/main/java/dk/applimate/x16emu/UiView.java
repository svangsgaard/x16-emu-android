package dk.applimate.x16emu;

import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
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

    Keyboard keyboard = new Keyboard(context, R.xml.keyboard);
    KeyboardView keyboardView = new KeyboardView(context, null);
    keyboardView.setKeyboard(keyboard);
    keyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
      @Override
      public void onPress(int primaryCode) {

      }

      @Override
      public void onRelease(int primaryCode) {

      }

      @Override
      public void onKey(int primaryCode, int[] keyCodes) {

      }

      @Override
      public void onText(CharSequence text) {

      }

      @Override
      public void swipeLeft() {

      }

      @Override
      public void swipeRight() {

      }

      @Override
      public void swipeDown() {

      }

      @Override
      public void swipeUp() {

      }
    });

    linearLayout.addView(message);
    linearLayout.addView(button);
    linearLayout.addView(keyboardView);

    //this.addView(linearLayout);
  }

}
