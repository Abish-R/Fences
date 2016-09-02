//package helixtech.fences.com;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
///**
// * Created by HelixTech-Admin on 4/9/2016.
// */
//public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
//    DatabaseRecordHandler dbrh = new DatabaseRecordHandler(this);
//    EditText mobile,name;
//    Button register;
//    GCMClientManager pushClientManager;
//    String reg_id;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
//        final String value = sp.getString("hack_name", "");
//        if(value=="") {
//            String PROJECT_NUMBER="83366096356";
//            pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
//            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
//                @Override
//                public void onSuccess(String registrationId, boolean isNewRegistration) {
//                    Log.d("Registration id", registrationId);
//                    //send this registrationId to your server
//                }
//                @Override
//                public void onFailure(String ex) {
//                    super.onFailure(ex);
//                }
//            });
//            setContentView(R.layout.registration);
//            initializeViews();
//        }
//        else
//            invokeContactActivity();
//    }
//    private void initializeViews(){
//        mobile = (EditText)findViewById(R.id.mobile);
//        name = (EditText)findViewById(R.id.name);
//        register = (Button)findViewById(R.id.register);
//
//        register.setOnClickListener(this);
//    }
//    public void onClick(View v){
//        if(v.getId()==R.id.register){
//            reg_id = pushClientManager.getRegistrationId(RegisterActivity.this);
//            //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
//            if(mobile.getText().toString().length()<10)
//                Toast.makeText(this, "Check Mobile", Toast.LENGTH_SHORT).show();
//            else if(name.getText().toString().length()<3)
//                Toast.makeText(this, "Check Name", Toast.LENGTH_SHORT).show();
//            else if(reg_id.length()<20)
//                Toast.makeText(this, "Not registered with GCM. Try Again", Toast.LENGTH_SHORT).show();
//            else
//                new AsyncRegistration(this).execute(name.getText().toString(), mobile.getText().toString(), reg_id);
//        }
//    }
//    public void saveRegistration(int userid, String name, String mobile){
//        SharedPreferences sp=getSharedPreferences("key", Context.MODE_PRIVATE);
//        SharedPreferences.Editor ed=sp.edit();
//        ed.putString("hack_name", name);
//        ed.putInt("hack_id",userid);
//        ed.commit();
//        if(dbrh.addContacts(new GetSetContact(userid, name,mobile))==1)
//            invokeContactActivity();
//        else
//            Toast.makeText(this,"You were registered already\n Something went wrong",Toast.LENGTH_SHORT).show();
//    }
//    private void invokeContactActivity(){
//        Intent intent=new Intent(this,ContactsActivity.class);
//        startActivity(intent);
//        finish();
//    }
//}
