package com.tecnoredsa.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.tecnoredsa.adapter.PostesArrayAdapter;
import com.tecnoredsa.model.Obra;
import com.tecnoredsa.model.Postacion;
import com.tecnoredsa.postaciones.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisco on 13/01/14.
 */
public class SqlHelperRelevamiento extends SQLiteOpenHelper{

    public static final String OBRA_TABLE = "obra";
//    public static final String OBRA_COLUMN_ID = "obra_id";
//    public static final String OBRA_COLUMN_NOMBRE = "nombre";

    public static final String POSTACION_TABLE = "postacion";
    //    public static final String POSTACION_COLUMN_ID = "postacion_id";
//    public static final String POSTACION_COLUMN_OBRA_ID = "obra_id";
//    public static final String POSTACION_COLUMN_GPS_LONGITUDE = "gps_longitude";
//    public static final String POSTACION_COLUMN_GPS_LATITUDE = "gps_latitude";
    private static final String SQL_OBRA_SELECT_ALL = "SELECT * FROM OBRA";


    private static final String DATABASE_NAME = "relevamiento.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String TABLE_OBRA_CREATE = "CREATE TABLE obra (obra_id INTEGER PRIMARY KEY, nombre TEXT)";
    private static final String TABLE_POSTACION_CREATE = "CREATE TABLE postacion (obra_id NUMERIC, postacion_id INTEGER PRIMARY KEY, gps_longitude TEXT, gps_latitude TEXT, tipo TEXT, preformada TEXT, ganancia TEXT, empalme TEXT, mensula_prolongada TEXT, agregar TEXT,detalle_adicional TEXT)";

    private static SqlHelperRelevamiento INSTANCE = null;

    private SQLiteDatabase db=null;
    private Context context;

    private SqlHelperRelevamiento(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
        db=getWritableDatabase();

    }
    public static SqlHelperRelevamiento getInstance(Context context){
        if(INSTANCE == null) INSTANCE = new SqlHelperRelevamiento(context);
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.v("DebugAPP","Creamos la base de datos");
        database.execSQL(TABLE_OBRA_CREATE);
        Log.v("DebugAPP",TABLE_OBRA_CREATE);
        database.execSQL(TABLE_POSTACION_CREATE);
        Log.v("DebugAPP",TABLE_POSTACION_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SqlHelperRelevamiento.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + OBRA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + POSTACION_TABLE);
        onCreate(db);
    }

    public boolean addObra(Obra o){
        ContentValues obraValues=new ContentValues();
        obraValues.put("nombre",o.getNombre());
        if(db.insert(OBRA_TABLE,null,obraValues) >0){
            Log.v("DebugAPP","Insert OK");
            return true;
        }
        else{
            Log.v("DebugAPP","Insert FAIL");
            return false;
        }
    }
    public boolean updateObra(Obra o){
        ContentValues obraValues=new ContentValues();
        String[] ids={o.getId().toString()};
        obraValues.put("nombre",o.getNombre());
        if(db.update(OBRA_TABLE,obraValues,"obra_id=?",ids) >0){
            Log.v("DebugAPP","Insert OK");
            return true;
        }
        else{
            Log.v("DebugAPP","Insert FAIL");
            return false;
        }
    }
    public boolean deleteObra(Obra o){
        String[] ids={(new Integer(o.getId()).toString())};
        if(db.delete(OBRA_TABLE, "obra_id=?",ids) >0){
            db.delete(POSTACION_TABLE, "obra_id=?",ids);
            Log.v("DebugAPP","Delete OK");
            return true;
        }
        else{
            Log.v("DebugAPP","Delete FAIL");
            return false;
        }
    }
    public ArrayList<Obra> getArrayObra(){
        String columns[]={"obra_id","nombre"};
        ArrayList<Obra> olist=new ArrayList<Obra>();
        Cursor cur=db.query(OBRA_TABLE,columns,null,null,null,null,"obra_id desc");
        cur.moveToFirst();
        do{
            Log.v("DebugAPP","obra_id: "+cur.getInt(0)+", Nombre: "+cur.getString(1));
            Obra o=new Obra(cur.getString(1),cur.getInt(0));
            olist.add(o);
        }while(cur.moveToNext());
        return olist;
    }

    public ArrayAdapter<Obra> getAdapterObra(){
        String columns[]={"obra_id","nombre"};
        ArrayAdapter<Obra> olist;
        olist = new ArrayAdapter<Obra>(context, R.layout.item_obra,R.id.item_obra_string);
        Cursor cur=db.query(OBRA_TABLE,columns,null,null,null,null,"obra_id desc");
        if(cur.getCount() > 0){
            cur.moveToFirst();
            do{
                Log.v("DebugAPP","obra_id: "+cur.getInt(0)+", Nombre: "+cur.getString(1));
                Obra o=new Obra(cur.getString(1),cur.getInt(0));
                olist.add(o);
            }while(cur.moveToNext());
            return olist;
        }else
        {
            return null;
        }
    }
    public boolean addPoste(Obra o,Postacion p){
        ContentValues Values=new ContentValues();
        Values.put("obra_id",o.getId());
        Values.put("gps_longitude", p.getGps_longitude().toString());
        Values.put("gps_latitude",p.getGps_latitude().toString());
        Values.put("tipo",p.getTipo());
        Values.put("preformada",p.getPreformada());
        Values.put("ganancia",p.getGanancia());
        Values.put("empalme",p.getEmpalme());
        Values.put("mensula_prolongada",p.getMensula_prolongada());
        Values.put("agregar",p.getAgregar());
        Values.put("detalle_adicional",p.getDetalle_adicional());
        if(db.insert(POSTACION_TABLE,null,Values) >0){
            Log.v("DebugAPP","Insert OK");
            return true;
        }
        else{
            Log.v("DebugAPP","Insert FAIL");
            return false;
        }
    }
    public PostesArrayAdapter getAdapterPostacion(Obra o){
        String columns[]={"postacion_id","obra_id","gps_longitude","gps_latitude","tipo","preformada","ganancia","empalme","mensula_prolongada","agregar","detalle_adicional"};
        String[] ids={o.getId().toString()};
        PostesArrayAdapter plist;
        plist = new PostesArrayAdapter(context, R.layout.item_postacion);
        Cursor cur=db.query(POSTACION_TABLE,columns,"obra_id=?",ids,null,null,"postacion_id desc");
        Integer numeracion=cur.getCount();
        if(cur.getCount() > 0){
            cur.moveToFirst();
            do{
                Postacion p=new Postacion(cur.getInt(0));
                p.setObra_id(cur.getInt(1));
                p.setGps_longitude(new Double(cur.getString(2)));
                p.setGps_latitude(new Double(cur.getString(3)));
                p.setTipo(cur.getString(4));
                p.setPreformada(cur.getString(5));
                p.setGanancia(cur.getString(6));
                p.setEmpalme(cur.getString(7));
                p.setMensula_prolongada(cur.getString(8));
                p.setAgregar(cur.getString(9));
                p.setDetalle_adicional(cur.getString(10));
                p.setNumero(numeracion--);
                Log.v("DebugAPP","postacion_id: "+p.getPostacion_id()+" "+p.getTipo()+" "+p.getPreformada()+" "+p.getGanancia()+" "+p.getEmpalme()+" "+p.getAgregar()+" "+p.getMensula_prolongada()+" "+p.getDetalle_adicional());
                plist.add(p);
            }while(cur.moveToNext());
            return plist;
        }else
        {
            return null;
        }
    }
    public boolean deletePostacion(Postacion p){
        String[] ids={(new Integer(p.getPostacion_id()).toString())};
        if(db.delete(POSTACION_TABLE, "postacion_id=?",ids) >0){
            Log.v("DebugAPP","Delete OK");
            return true;
        }
        else{
            Log.v("DebugAPP","Delete FAIL");
            return false;
        }
    }
    public boolean updatePostacion(Obra o,Postacion p){
        ContentValues obraValues=new ContentValues();
        String[] ids={o.getId().toString(), p.getPostacion_id().toString()};
        obraValues.put("tipo",p.getTipo());
        obraValues.put("preformada",p.getPreformada());
        obraValues.put("ganancia",p.getGanancia());
        obraValues.put("empalme",p.getEmpalme());
        obraValues.put("mensula_prolongada",p.getMensula_prolongada());
        obraValues.put("detalle_adicional",p.getDetalle_adicional());
        obraValues.put("agregar",p.getAgregar());
        obraValues.put("gps_latitude",p.getGps_latitude());
        obraValues.put("gps_longitude",p.getGps_longitude());

        if(db.update(POSTACION_TABLE,obraValues,"obra_id=? and postacion_id=?",ids) >0){
            Log.v("DebugAPP","Update OK");
            return true;
        }
        else{
            Log.v("DebugAPP","Update FAIL");
            return false;
        }
    }


    /*public Postacion getUltimoPoesteInsertado(Obra o){
        String columns[]={"postacion_id","obra_id","gps_longitude","gps_latitude","tipo","preformada","ganancia","empalme","mensula_prolongada","ganancia","detalle_adicional"};
        String[] ids={o.getId().toString()};
        PostesArrayAdapter plist;
        plist = new PostesArrayAdapter(context, R.layout.item_postacion);
        Cursor cur=db.query(POSTACION_TABLE,columns,"obra_id=?",ids,null,null,"postacion_id desc");
        if(cur.getCount() > 0){
            cur.moveToFirst();
            do{
                Postacion p=new Postacion(cur.getInt(0));
                p.setObra_id(cur.getInt(1));
                p.setGps_longitude(new Double(cur.getString(2)));
                p.setGps_latitude(new Double(cur.getString(3)));
                p.setTipo(cur.getString(4));
                p.setPreformada(cur.getString(5));
                p.setGanancia(cur.getString(6));
                p.setEmpalme(cur.getString(7));
                p.setMensula_prolongada(cur.getString(8));
                p.setAgregar(cur.getString(9));
                p.setDetalle_adicional(cur.getString(10));
                Log.v("DebugAPP","postacion_id: "+p.getPostacion_id()+" "+p.getTipo()+" "+p.getPreformada()+" "+p.getGanancia()+" "+p.getEmpalme()+" "+p.getAgregar()+" "+p.getMensula_prolongada()+" "+p.getDetalle_adicional());
                plist.add(p);
            }while(cur.moveToNext());
            return plist;
        }else
        {
            return null;
        }
    }*/
}
