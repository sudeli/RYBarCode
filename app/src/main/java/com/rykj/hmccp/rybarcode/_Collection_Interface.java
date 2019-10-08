package com.rykj.hmccp.rybarcode;

import android.os.Bundle;
import android.view.View;

interface OnListItemClickListener {
    void onListItemClick(View v, Bundle bundle);
}
interface OnConfirmClickListener{
    void OnAgreeClick(Bundle bundle);
    void OnCancelClick();
}