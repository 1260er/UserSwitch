package de.pritcloud.userswitch;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int HOST_ID = 0x5553;
    private static final int REQUEST_BIND_WIDGET = 1001;
    private static final String PREFS = "widget_host";
    private static final String KEY_WIDGET_ID = "widget_id";

    private static final int AUTO_CLICK_ATTEMPTS = 32;
    private static final long AUTO_CLICK_DELAY_MS = 16L;
    private static final int START_LISTENING_ATTEMPT = 0;

    private AppWidgetManager widgetManager;
    private AppWidgetHost widgetHost;
    private FrameLayout container;
    private int pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private boolean widgetHostListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFinishOnTouchOutside(true);

        // Unsichtbare Brücke: Der Nutzer soll nur den System-User-Switcher sehen.
        getWindow().getDecorView().setAlpha(0f);

        widgetManager = AppWidgetManager.getInstance(this);
        widgetHost = new AppWidgetHost(this, HOST_ID);

        container = new FrameLayout(this);
        container.setPadding(dp(8), dp(8), dp(8), dp(8));
        setContentView(container);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = Math.min(
                dp(420),
                getResources().getDisplayMetrics().widthPixels - dp(24)
        );
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);

        getWindow().setDimAmount(0f);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Fast path: Die bereits gebundene Widget-ID zuerst verwenden.
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        int savedId = prefs.getInt(
                KEY_WIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        );

        if (savedId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            AppWidgetProviderInfo savedInfo =
                    widgetManager.getAppWidgetInfo(savedId);

            if (savedInfo != null
                    && savedInfo.provider != null
                    && "com.android.multiuser".equals(
                            savedInfo.provider.getPackageName()
                    )
                    && savedInfo.provider.getClassName().endsWith(
                            ".MultiuserWidgetReceiver"
                    )) {
                showWidget(savedId, savedInfo);
                return;
            }

            widgetHost.deleteAppWidgetId(savedId);
            prefs.edit().remove(KEY_WIDGET_ID).apply();
        }

        // Provider-Scan nur bei erster Einrichtung oder ungültiger Bindung.
        AppWidgetProviderInfo provider = findMultiuserProvider();

        if (provider == null) {
            showMessage("GrapheneOS-Multiuser-Widget nicht gefunden.");
            return;
        }

        requestWidgetBinding(provider);
    }

    @Override
    protected void onStop() {
        if (widgetHostListening) {
            try {
                widgetHost.stopListening();
            } catch (Exception ignored) {
            }

            widgetHostListening = false;
        }

        super.onStop();
    }

    private AppWidgetProviderInfo findMultiuserProvider() {
        List<AppWidgetProviderInfo> providers =
                widgetManager.getInstalledProviders();

        for (AppWidgetProviderInfo info : providers) {
            String packageName = info.provider.getPackageName();
            String className = info.provider.getClassName();

            if ("com.android.multiuser".equals(packageName)
                    && className.endsWith(".MultiuserWidgetReceiver")) {
                return info;
            }
        }

        return null;
    }

    private void requestWidgetBinding(AppWidgetProviderInfo provider) {
        pendingWidgetId = widgetHost.allocateAppWidgetId();

        Bundle options = createWidgetOptions();

        boolean bound = widgetManager.bindAppWidgetIdIfAllowed(
                pendingWidgetId,
                provider.getProfile(),
                provider.provider,
                options
        );

        if (bound) {
            saveAndShowWidget(pendingWidgetId);
            return;
        }

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
        intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                pendingWidgetId
        );
        intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,
                provider.provider
        );
        intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE,
                provider.getProfile()
        );
        intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_OPTIONS,
                options
        );

        try {
            startActivityForResult(intent, REQUEST_BIND_WIDGET);
        } catch (Exception e) {
            widgetHost.deleteAppWidgetId(pendingWidgetId);
            pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            showMessage("Widget-Bind-Dialog konnte nicht geöffnet werden.");
        }
    }

    private Bundle createWidgetOptions() {
        int widthDp = Math.min(
                400,
                Math.max(
                        280,
                        (int) (
                                getResources().getDisplayMetrics().widthPixels
                                        / getResources().getDisplayMetrics().density
                        ) - 40
                )
        );

        Bundle options = new Bundle();
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, widthDp);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 80);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 120);

        return options;
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_BIND_WIDGET) {
            return;
        }

        int widgetId = pendingWidgetId;

        if (data != null) {
            widgetId = data.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    widgetId
            );
        }

        if (resultCode == RESULT_OK
                && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            saveAndShowWidget(widgetId);
            return;
        }

        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            widgetHost.deleteAppWidgetId(widgetId);
        }

        pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        finish();
    }

    private void saveAndShowWidget(int widgetId) {
        AppWidgetProviderInfo info =
                widgetManager.getAppWidgetInfo(widgetId);

        if (info == null) {
            showMessage("Multiuser-Widget konnte nicht geladen werden.");
            return;
        }

        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putInt(KEY_WIDGET_ID, widgetId)
                .apply();

        pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        showWidget(widgetId, info);
    }

    private void showWidget(
            int widgetId,
            AppWidgetProviderInfo info
    ) {
        container.removeAllViews();

        AppWidgetHostView widgetView =
                widgetHost.createView(this, widgetId, info);

        widgetView.setAppWidget(widgetId, info);

        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(112)
                );

        widgetView.setLayoutParams(layoutParams);
        container.addView(widgetView);

        scheduleAutomaticSwitchClick(widgetView, 0);
    }

    private void scheduleAutomaticSwitchClick(
            AppWidgetHostView widgetView,
            int attempt
    ) {
        List<View> clickableViews = new ArrayList<>();
        collectClickableViews(widgetView, clickableViews);

        if (clickableViews.size() == 1) {
            View target = clickableViews.get(0);

            if (target.performClick()) {
                finish();
                return;
            }
        }

        // Nach einem Neustart kann createView() noch keine aktuellen
        // RemoteViews besitzen. Dann Updates sofort abonnieren, ohne
        // vorher mehrere Retries abzuwarten.
        if (attempt == START_LISTENING_ATTEMPT) {
            startWidgetListeningIfNeeded();
        }

        if (attempt + 1 < AUTO_CLICK_ATTEMPTS) {
            widgetView.postDelayed(
                    () -> scheduleAutomaticSwitchClick(
                            widgetView,
                            attempt + 1
                    ),
                    AUTO_CLICK_DELAY_MS
            );
        } else {
            revealFallbackWidget();
        }
    }

    private void collectClickableViews(View view, List<View> result) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }

        if (view.hasOnClickListeners()) {
            result.add(view);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                collectClickableViews(group.getChildAt(i), result);
            }
        }
    }

    private void startWidgetListeningIfNeeded() {
        if (widgetHostListening) {
            return;
        }

        try {
            widgetHost.startListening();
            widgetHostListening = true;
        } catch (Exception ignored) {
        }
    }

    private void revealFallbackWidget() {
        getWindow()
                .getDecorView()
                .animate()
                .alpha(1f)
                .setDuration(80L)
                .start();
    }

    private void showMessage(String message) {
        getWindow().getDecorView().setAlpha(1f);
        container.removeAllViews();

        TextView text = new TextView(this);
        text.setText(message);
        text.setTextSize(16);
        text.setGravity(Gravity.CENTER);
        text.setPadding(dp(24), dp(24), dp(24), dp(24));

        container.addView(
                text,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
    }

    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }
}
