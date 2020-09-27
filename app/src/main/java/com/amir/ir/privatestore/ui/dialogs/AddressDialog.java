/*
package com.amir.ir.privatestore.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.amir.ir.privatestore.R;
import com.amir.ir.privatestore.models.Address;
import com.amir.ir.privatestore.ui.customs.MyRaisedButton;

public class AddressDialog extends MyBaseDialog {

    private OnDialogAction onDialogAction;

    public AddressDialog(@NonNull Context context, int themeResId, OnDialogAction onDialogAction) {
        super(context, themeResId, R.layout.layout_address_dialog);
        this.onDialogAction = onDialogAction;
    }

    @Override
    protected void initViews() {
        EditText etName = findViewById(R.id.et_name);
        EditText etTitle = findViewById(R.id.et_title);
        EditText etAddress = findViewById(R.id.et_address);
        EditText etPelak = findViewById(R.id.et_pelak);
        EditText etPostCode = findViewById(R.id.et_post_code);

        ImageView btnClose = findViewById(R.id.iv_close);


        MyRaisedButton btnSubmit = findViewById(R.id.btn_submit);



        btnClose.setOnClickListener(v -> {
            if(onDialogAction != null) {
                onDialogAction.onClose(AddressDialog.this);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if(onDialogAction != null) {
                String name = etName.getText().toString().trim();
                String title = etTitle.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String pelak = etPelak.getText().toString().trim();
                String postCode = etPostCode.getText().toString().trim();

                onDialogAction.onSubmit(AddressDialog.this, new Address(title, name, address, pelak, postCode));

                //dismiss();
            }

            //todo ui purpose
            btnSubmit.showProgressBar(true);
            new Handler().postDelayed(() -> {
                btnSubmit.showProgressBar(false);
                dismiss();
            }, 2000);
        });
    }

    public interface OnDialogAction {
        void onSubmit(Dialog dialog, Address address);
        void onClose(Dialog dialog);
    }
}
*/
