package com.example.asistente;

import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.Normalizer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int RECONOCEDOR_VOZ = 7;
    private TextView escuchando;
    private TextView respuesta;
    private String casa = "hola";
    private ArrayList<Respuesta> respuest;
    private TextToSpeech leer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == RECONOCEDOR_VOZ){
            ArrayList<String> reconocido = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String escuchado = reconocido.get(0);
            escuchando.setText(escuchado);
            prepararRespuesta(escuchado);
        }
    }

    private void prepararRespuesta(String escuchado) {
        String normalizar = Normalizer.normalize(escuchado, Normalizer.Form.NFD);
        String sintilde = normalizar.replaceAll("[^\\p{ASCII}]", "");

        int resultado;
        String respuesta = respuest.get(0).getRespuestas();
        for (int i = 0; i < respuest.size(); i++) {
            resultado = sintilde.toLowerCase().indexOf(respuest.get(i).getCuestion());
            if(resultado != -1){
                respuesta = respuest.get(i).getRespuestas();
               if (!operacion(respuest.get(i).getCuestion(), sintilde).equals("")){
                   respuesta = respuesta + operacion(respuest.get(i).getCuestion() , sintilde);
               }
            }
        }
        responder(respuesta);
    }
 // para realizar las operaciones
    private String operacion(String cuestion, String escuchado) {
        String rpta = "";// devuelve el numero final
        if(cuestion.equals("mas") || cuestion.equals("menos") || cuestion.equals("por") || cuestion.equals("entre")){
           rpta = operaciones(cuestion, escuchado);
        }
        return rpta;
    }
    // *****dividir ( el string por ejemplo 5  mas 5
    private  String operaciones(String operador, String numeros){
      String rpta = "";
      double respuesta = 0;
      String[] numero = numeros.split(operador);
      double num1 = obtenerNumero(numero[0]);
      double num2 = obtenerNumero(numero[1]);
      switch (operador){
          case "mas":
              respuesta = num1 + num2;
              break;
          case "menos":
              respuesta = num1 - num2;
              break;
          case "por":
              respuesta = num1 * num2;
              break;
          case "entre":
              if(num1 > 0 ) {
                  respuesta = num1 / num2;
              }
              break;
      }
        rpta = String.valueOf(respuesta);
      return rpta;
    }
//***********************************************
    private double obtenerNumero(String cadena){
      double num;
      String n = "";
      char[] numero = cadena.toCharArray();
      for (int i = 0; i < numero.length; i++){
      if(Character.isDigit(numero[i])) {
          n = n + String.valueOf(numero[i]);
      }
      }
      num =  Double.parseDouble(n);
      return num;
    }

    private void responder(String respuestita) {
        respuesta.setText(respuestita);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            leer.speak(respuestita, TextToSpeech.QUEUE_FLUSH, null, null);
        }else {
            leer.speak(respuestita, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void inicializar(){
        escuchando = (TextView)findViewById(R.id.tvEscuchando);
        respuesta = (TextView)findViewById(R.id.tvRespuesta);
        respuest = proveerDatos();
        leer = new TextToSpeech(this, this);
    }

    public void hablar(View v){
        Intent hablar = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //en-US
        hablar.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        startActivityForResult(hablar, RECONOCEDOR_VOZ);
    }

    public ArrayList<Respuesta> proveerDatos(){
        ArrayList<Respuesta> respuestas = new ArrayList<>();
        respuestas.add(new Respuesta("defecto", "¡te recomendo que utilices una oracion para decirlo!"));
        respuestas.add(new Respuesta("hola", "como se dice ayuda en ingles"));
        respuestas.add(new Respuesta("help", "muy bien sigue adelante"));
        respuestas.add(new Respuesta("adios", "que descanses"));
        respuestas.add(new Respuesta("como estas", "esperando serte de ayuda"));
        respuestas.add(new Respuesta("help", "muy bien" + "     "+"como se pregunta cual es tu nombre "));
        respuestas.add(new Respuesta("is your name", "muy bien"+"     "+"my name is alejandra"+"  "+"cual es el verbo hacer "));
        respuestas.add(new Respuesta("make", "muy bien"+"     "+" como se dice esta pregunta en ingles, ¿Te gusta el chocolate?"));
        respuestas.add(new Respuesta("do you like", "muy bien"));
        return respuestas;
    }

    @Override
    public void onInit(int status) {

    }
}

