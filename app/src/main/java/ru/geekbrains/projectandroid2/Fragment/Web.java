package ru.geekbrains.projectandroid2.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import ru.geekbrains.projectandroid2.R;
import ru.geekbrains.projectandroid2.Web.OkHttpRequester;

public class Web extends Fragment {

    private OkHttpRequester requester;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewWeb = inflater.inflate(R.layout.activity_wev_page, container, false);
        final WebView page = viewWeb.findViewById(R.id.webView);

        requester = new OkHttpRequester(new OkHttpRequester.OnResponseCompleted() {
            @Override
            public void onCompleted(String content) {
                page.loadData(content, "text/html; charset=utf-8", "utf-8");
            }
        });

        InitView(viewWeb);
        return viewWeb;
    }

    private void InitView(View viewWeb) {
        final EditText editTextWeb =  viewWeb.findViewById(R.id.editTextWeb);
        Button button = viewWeb.findViewById(R.id.buttonGo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = String.valueOf(editTextWeb.getText());
                if (!url.equals("http://")){
                    if (URLUtil.isValidUrl(url))
                        requester.run(url);
                }
                else
                    requester.run("https://geekbrains.ru");
            }
        });
    }
}
