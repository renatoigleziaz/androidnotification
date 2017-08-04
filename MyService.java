package org.qtproject.velox;

/*
*
*  PORT ANDROID
*
*  Java - Classe para Serviços sob demanda
*
*  por Renato Igleziaz
*  15/12/2016
*
*  Requer o QT 5.7 compilador
*
*/

import android.os.Build;
import android.os.Bundle;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.util.Log;
import android.net.Uri;
import android.database.Cursor;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import android.provider.MediaStore;
import android.provider.DocumentsContract;
import android.content.ContentUris;
import android.os.Handler;

import java.util.Random;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyService extends org.qtproject.qt5.android.bindings.QtService {

    private static NotificationManager m_notificationManager;
    private static Notification.Builder m_builder;
    private static Context m_instance;
    private static Activity m_act;
    private static Intent m_intent;
    private static TaskStackBuilder stackBuilder;
    private static PendingIntent resultPendingIntent;
    private static Random generator;
    private static String message;
    private static Handler handler;

    @Override
    public void onCreate() {
        // cria um handler objeto para acessar a função em thread-mode
        handler = new Handler();
        super.onCreate();
    }

    private static void runOnUiThread(Runnable runnable) {

        handler.post(runnable);
    }

    public MyService() {

        m_instance = this;
    }

    public static void startMyService(Context ctx) {
        // iniciando o serviço

        ctx.startService(new Intent(ctx, MyService.class));
        m_instance = ctx;
        Log.v("Java.Main.AndroidPort", "PORT ANDROID/JAVA - Serviço Iniciado! ");
    }

    public static void notify(String s)
    {
        // Trata mensagens de notificação com o sistema Android dentro de um serviço

        try {
            // se não tiver instanciado

            message = s;

            runOnUiThread(new Runnable() {

                public void run() {

                    Toast.makeText(m_instance, message, Toast.LENGTH_LONG).show();

                    if (m_notificationManager == null) {

                        // identidade
                        m_intent = new Intent(m_instance, Main.class);

                        // aqui resolve um problema o app fica todo branco sem
                        // obter a interface correta
                        m_intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // gera uma ID de forma randomica
                        generator = new Random();

                        // obtem a instancia da outra Activity
                        resultPendingIntent = PendingIntent.getActivity(m_instance, generator.nextInt(), m_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        // Gerenciador de Sessão de mensagens
                        m_notificationManager = (NotificationManager)m_instance.getSystemService(Context.NOTIFICATION_SERVICE);
                        m_builder = new Notification.Builder(m_instance);

                        // icone do app na notificação
                        m_builder.setSmallIcon(R.drawable.icon);
                        // Titulo
                        m_builder.setContentTitle("App");
                        // auto cancel
                        m_builder.setAutoCancel(true);
                        // vibra | acende as luzes | avisa com som
                        m_builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

                        // registra a intenção
                        m_builder.setContentIntent(resultPendingIntent);
                    }

                    // set builder
                    m_builder.setContentText(message);
                    m_notificationManager.notify(1, m_builder.build());
                }
            });


        } catch(Throwable e) {
            // se tiver erro print no debug
            e.printStackTrace();
        }
    }
}
