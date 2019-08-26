package ru.geekbrains.projectandroid2.Web;

import android.os.Handler;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class OkHttpRequester {
    private OnResponseCompleted listener;

    public OkHttpRequester(OnResponseCompleted listener) {
        this.listener = listener;
    }

    public void run(String url) {
        OkHttpClient client = new OkHttpClient();        // Клиент
        Request.Builder builder = new Request.Builder(); // создадим строителя
        builder.url(url);                                // укажем адрес сервера
        Request request = builder.build();               // построим запрос

        Call call = client.newCall(request);            // Ставим запрос в очередь
        call.enqueue(new Callback() {

            final Handler handler = new Handler();

            // Это срабатывает по приходу ответа от сервера
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if(response.code() / 100 == 2) { //Все хорошо
                    final String answer = Objects.requireNonNull(response.body()).string();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCompleted(answer); // вызовем наш метод обратного вызова
                        }
                    });
                }
            }

            // При сбое
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });
    }

    public interface OnResponseCompleted {
        void onCompleted(String content);
    }
}