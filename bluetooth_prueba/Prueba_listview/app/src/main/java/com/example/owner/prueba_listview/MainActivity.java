package com.example.owner.prueba_listview;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    ArrayAdapter<String> listAdapter;
    ArrayAdapter<String> listAdapter_address;
    ListView listView;
    private Button buscar, on;
    private TextView txtbuscar;
    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<String> pairedDevices_address;
    ArrayList<BluetoothDevice> devices;
    protected String toastText;
    public BluetoothSocket Slave;
    public BluetoothDevice MODULO;
    OutputStream outStream = null;
    InputStream inStream = null;
    IntentFilter filter;
    BroadcastReceiver reciver;
    private static final int    REQUEST_ENABLE_BT   = 1;
   // private static final UUID MY_UUID =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static UUID UUID2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        txtbuscar.setText("");
        if(btAdapter==null)
        {
            Toast.makeText(getApplicationContext(),"No bluetooth detected",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    public void init()
    {
        listView = (ListView) findViewById(R.id.ListaDevices);
        buscar = (Button) findViewById(R.id.btnsearch);
        on = (Button) findViewById(R.id.btnbluetooth);
        txtbuscar=(TextView) findViewById(R.id.txtvariable);
        buscar.setEnabled(false);
        btAdapter=BluetoothAdapter.getDefaultAdapter();
        devices=new ArrayList<BluetoothDevice>();
        pairedDevices=new ArrayList<String>();
        pairedDevices_address=new ArrayList<String>();
        listAdapter=new ArrayAdapter<String>(listView.getContext(),android.R.layout.simple_list_item_1, 0);
        listAdapter_address=new ArrayAdapter<String>(listView.getContext(),android.R.layout.simple_list_item_1, 0);
        listView.setEnabled(false);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (btAdapter.isDiscovering()) {
                    btAdapter.cancelDiscovery();
                }
                if (listAdapter.getItem(i).contains("PAIRED")) {
                    Toast.makeText(getApplicationContext(), "DEVICES ARE PAIRED", Toast.LENGTH_SHORT).show();

                    try {
                        EstablishConection(listAdapter_address.getItem(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "DEVICES ARE NOT PAIRED", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void buscar(View view)
    {
        btAdapter.startDiscovery();
        buscar.setEnabled(false);
        listView.setEnabled(false);
        txtbuscar.setText("BUSCANDO...");
        BroadcastReceiver reciver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    buscar.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"BUSCANDO DISPOSITIVOS",Toast.LENGTH_SHORT).show();
                    BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s="";
                    listAdapter.add(device.getName()+" "+"PAIRED");
                    listAdapter_address.add(device.getAddress());


                }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {

                    Toast.makeText(getApplicationContext(),"TERMINO BUSQUEDAD",Toast.LENGTH_SHORT).show();
                    buscar.setEnabled(true);
                    txtbuscar.setText("");
                    listView.setEnabled(true);
                    unregisterReceiver(this);

                }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if(btAdapter.getState()== btAdapter.STATE_OFF){
                        turnOnBT();
                    }
                }
                listView.setAdapter(listAdapter);
            }

        };

        filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(reciver,filter);
        filter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(reciver,filter);
        filter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(reciver,filter);
    }
    private void turnOnBT() {

        if(!btAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            buscar.setEnabled(true);
        }
        else {
            Toast.makeText(getApplicationContext(), "Already on",Toast.LENGTH_LONG).show();
            buscar.setEnabled(true);
        }
    }
    public void turnOn(View view) {

        if(!btAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            buscar.setEnabled(true);
        }
        else {
            Toast.makeText(getApplicationContext(), "Already on",Toast.LENGTH_LONG).show();
            buscar.setEnabled(true);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_CANCELED)
        {
            Toast.makeText(getApplicationContext(),"EL BLUETOOTH DEBE ESTAR PRENDIDO PARA CONTINUAR",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void EstablishConection(String Address) throws IOException {

        if(BluetoothAdapter.checkBluetoothAddress(Address))
        {
            MODULO = btAdapter.getRemoteDevice(Address);


            try {
                UUID2 = MODULO.getUuids()[0].getUuid();
                Slave = MODULO.createRfcommSocketToServiceRecord(UUID2);

                btAdapter.cancelDiscovery();

                Slave.connect();

                toastText = "Coneccion exitosa";
                outStream = Slave.getOutputStream();
                inStream = Slave.getInputStream();
                Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
            }
            catch (IOException connectException) {

                try {
                    toastText = "Fallo de coneccion";
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    Slave.close();
                } catch (IOException closeException) { }
                return;
            }
        }

        else
        {
            toastText = "Insert a correct device address";
            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
        }

    }


    public void envio(View view) throws InterruptedException {
        write("H");
        Thread.sleep(400);
        write("O");
        Thread.sleep(400);
        write("L");
        Thread.sleep(400);
        write("A");
        Thread.sleep(400);
        write("\n");
        Thread.sleep(400);
    }
    private void write(String on) {
        try {
            byte[] Send = on.getBytes();
            outStream.write(Send);
        } catch (IOException e) {
            toastText = "NO SE HA PODIDO CONECTAR CON ARGUS-PET";
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void desconectar_modulo(View view)
    {
        if(Slave.isConnected())
        {
            try {
                Slave.close();
                outStream.close();
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "ERROR AL DESCONECTAR", Toast.LENGTH_SHORT).show();
            }
        }
        btAdapter.disable();
        txtbuscar.setText("Bluetooth is off");
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(reciver);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
