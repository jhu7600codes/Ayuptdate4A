package com.exteragram.messenger.preferences;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.exteragram.messenger.ExteraConfig;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class OtherPreferencesActivity extends BasePreferencesActivity {

    private static final int ITEM_RESET_SETTINGS = 1;
    private static final int ITEM_DELETE_ACCOUNT = 2;

    @Override
    public String getTitle() {
        return LocaleController.getString(R.string.LocalOther);
    }

    @Override
    protected void fillItems(ArrayList arrayList, UniversalAdapter adapter) {
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.LocalOther)));
        arrayList.add(UItem.asButton(ITEM_RESET_SETTINGS, R.drawable.msg_reset, LocaleController.getString(R.string.ResetSettings)));
        arrayList.add(UItem.asButton(ITEM_DELETE_ACCOUNT, R.drawable.msg_clearcache, LocaleController.getString(R.string.DeleteAccount)).red());
        arrayList.add(UItem.asShadow());
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ITEM_RESET_SETTINGS) {
            handleResetSettingsClick();
        } else if (item.id == ITEM_DELETE_ACCOUNT) {
            handleDeleteAccountClick();
        }
    }

    private void handleResetSettingsClick() {
        AlertDialog dialog = new AlertDialog.Builder(getParentActivity())
                .setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.ResetPreferencesInfo)))
                .setTitle(LocaleController.getString(R.string.ResetSettings))
                .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
                .setPositiveButton(LocaleController.getString(R.string.Reset), (d, w) -> resetSettings())
                .create();
        showDialog(dialog);
        TextView button = (TextView) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    private void resetSettings() {
        ExteraConfig.preferences.edit().clear().apply();
        ExteraConfig.reloadConfig();
        getParentLayout().rebuildAllFragmentViews(false, false);
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated);
        LocaleController.getInstance().recreateFormatters();
        Theme.reloadAllResources(getParentActivity());
        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.ResetPreferences), getResourceProvider()).show();
    }

    private void handleDeleteAccountClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString(R.string.TosDeclineDeleteAccount));
        builder.setTitle(LocaleController.getString(R.string.DeleteAccount));
        builder.setPositiveButton(LocaleController.getString(R.string.Deactivate), (d, w) -> deleteAccount());
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(di -> {
            TextView button = (TextView) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            button.setEnabled(false);
            CharSequence text = button.getText();
            new CountDownTimer(30000L, 100L) {
                @Override
                public void onTick(long millisLeft) {
                    button.setText(String.format(Locale.getDefault(), "%s • %d", text, (millisLeft / 1000) + 1));
                }

                @Override
                public void onFinish() {
                    button.setText(text);
                    button.setEnabled(true);
                }
            }.start();
        });
        showDialog(dialog);
    }

    private void deleteAccount() {
        AlertDialog progress = new AlertDialog(getParentActivity(), AlertDialog.ALERT_TYPE_SPINNER);
        progress.setCanCancel(false);
        Utilities.globalQueue.postRunnable(() -> {
            TL_account.deleteAccount req = new TL_account.deleteAccount();
            req.reason = "AyuGram";
            getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                try {
                    progress.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (response instanceof TLRPC.TL_boolTrue) {
                    getMessagesController().performLogout(0);
                    return;
                }
                if (error == null || error.code != -1000) {
                    String message = LocaleController.getString(R.string.ErrorOccurred);
                    if (error != null) {
                        message = message + "\n" + error.text;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString(R.string.AppName));
                    builder.setMessage(message);
                    builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
                    builder.show();
                }
            }));
        }, 500L);
        progress.show();
    }
}
