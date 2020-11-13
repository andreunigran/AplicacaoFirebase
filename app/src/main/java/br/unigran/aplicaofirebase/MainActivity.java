package br.unigran.aplicaofirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import br.unigran.aplicaofirebase.domain.Pessoa;

public class MainActivity extends AppCompatActivity {


    private EditText nome, email;
private  List<Pessoa> lista;
private ListView listView;

    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nome=findViewById(R.id.id_nome);
        email=findViewById(R.id.id_email);
        listView=findViewById(R.id.lista_pessoas);
        iniciaFirebase();
        buscaItens();
        lista= new LinkedList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void iniciaFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);//caso esteja falso
        databaseReference= firebaseDatabase.getReference();
    }
    private void salva(){
        Pessoa pessoa = new Pessoa();
        pessoa.setEmail(email.getText().toString());
        pessoa.setNome(nome.getText().toString());
        pessoa.setId(idMaior());
        databaseReference.child("Pessoa").child(pessoa.getId()+"").setValue(pessoa);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_novo){
            salva();
        }
        return true;
    }
    private void buscaItem(){
      //  databaseReference.child("Pessoa").child("1");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object nome = snapshot.child("Pessoa").child("1").child("nome").getValue(String.class);
                Toast.makeText(MainActivity.this,nome.toString(),Toast.LENGTH_LONG).show();
                //Pessoa pessoa = snapshot.getValue(Pessoa.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void buscaItens(){
        databaseReference.child("Pessoa");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot dataSnapshot = snapshot.child("Pessoa");
                lista.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Pessoa pessoa = postSnapshot.getValue(Pessoa.class);
                    lista.add(pessoa);
                }
                preencheLista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void preencheLista(){
        ArrayAdapter arrayAdapter =new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,lista);
        listView.setAdapter(arrayAdapter);
    }

    private Integer idMaior(){
        int maior=0;
        for (Pessoa p:
             lista) {
            if (p.getId()>maior)
                maior=p.getId();
        }
        return maior+1;
    }


}