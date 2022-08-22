package com.black.amoledwallpapers.rate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import com.black.amoledwallpapers.R;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

public class AppRaterDialog extends AlertDialog {

    protected AppRaterDialog(Context context) {
        super(context);
    }

    public static class Builder extends AlertDialog.Builder {
        private final Context context;
        private SmileRating ratingBar;
        private String packageName;
        private int minimumNumberOfStars;
        private String email;

        public Builder(Context context) {
            super(context);
            this.context = context;
            setCancelable(false);
        }

        public AlertDialog.Builder setDialogPositiveButton(String rateButtonText) {
            this.setPositiveButton(rateButtonText, new RateClickListener());
            return this;
        }

        public void setDialogNeutralButton(String notNowButtonText, OnClickListener onClickListener) {
            this.setNeutralButton(notNowButtonText, onClickListener);
        }

        public void setDialogNegativeButton(String neverButtonText, final OnClickListener onClickListener) {
            this.setNegativeButton(neverButtonText, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new ConditionManager(context).dontShowDialogAgain();
                    onClickListener.onClick(dialogInterface, i);
                }
            });
        }

        public void setMinimumNumberOfStars(int minimumNumberOfStars) {
            this.minimumNumberOfStars = minimumNumberOfStars;
        }

        public void showStars() {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_with_stars, null);
            ratingBar = view.findViewById(R.id.ratingBar);
            ratingBar.setSelectedSmile(BaseRating.GREAT);
            this.setView(view);
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        private class RateClickListener implements OnClickListener {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ratingBar != null) {
                    if (ratingBar.getRating() >= minimumNumberOfStars) {
                        openPlayStore(packageName);
                        new ConditionManager(context).dontShowDialogAgain();
                    } else {
                        if (email != null) {
                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.star_title_improve_app)
                                    .setMessage(R.string.star_message_improve_app)
                                    .setPositiveButton(R.string.star_mail_improve_app, new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            openEmailApp();
                                        }
                                    })
                                    .setNegativeButton(R.string.star_cancel_improve_app, new OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    }).show();
                        }
                    }
                } else {
                    openPlayStore(packageName);
                    new ConditionManager(context).dontShowDialogAgain();
                }
            }

            private void openEmailApp() {
                Intent intent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                        "mailto", email, null));
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                Intent mailer = Intent.createChooser(intent, null);
                context.startActivity(mailer);
            }

            private void openPlayStore(String packageName) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RateConst.MARKET_STORE_URL + packageName)));
                } catch (ActivityNotFoundException exception) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RateConst.PLAY_STORE_URL + packageName)));
                }
            }
        }
    }
}