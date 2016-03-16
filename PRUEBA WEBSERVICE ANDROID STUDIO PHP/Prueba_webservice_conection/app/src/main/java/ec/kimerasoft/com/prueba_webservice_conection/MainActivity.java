package ec.kimerasoft.com.prueba_webservice_conection;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText et_nombre;
    private Button bt_aceptar;
    private final static String posturl="http://prueba-webservice.kimerasoft-ec.com/prueba.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_nombre=(EditText)findViewById(R.id.et_nombre);
        bt_aceptar=(Button)findViewById(R.id.bt_aceptar);

        StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(p);

        bt_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), metodo(et_nombre.getText().toString()), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public String metodo(String nombre) {
        try{
            HttpClient httpclient=new DefaultHttpClient();
            HttpPost httppost=new HttpPost(posturl);

            //añadir parametros para el envio a un metodo de un webservice
            List<NameValuePair> params=new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nombre_get",nombre));
            httppost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse resp=httpclient.execute(httppost); //se envia la peticion al servidor y la respuesta se la guarda en  el objeto resp

            HttpEntity ent= (HttpEntity) resp.getEntity();//.getContent();//se pasa resp a formato httpentity

            String texto= EntityUtils.toString(ent);


            return texto;

        }
        catch(Exception e)
        {
            String error;
            error=e.getMessage();
            return error;
        }
    }
}
