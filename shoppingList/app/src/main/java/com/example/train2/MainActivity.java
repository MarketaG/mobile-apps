package com.example.train2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String FILENAME = "shopping_list.txt";

    private ListView listView;
    private ShoppingListAdapter adapter;
    private ArrayList<ShoppingItem> shoppingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview_shopping_items);
        shoppingItems = new ArrayList<>();
        adapter = new ShoppingListAdapter(this, R.layout.list_item, shoppingItems);
        listView.setAdapter(adapter);

        String[] items = getResources().getStringArray(R.array.shopping_items);
        for (String item : items) {
            shoppingItems.add(new ShoppingItem(item));
        }

        Button buttonAddItem = findViewById(R.id.button_add_item);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextNewItem = findViewById(R.id.edittext_new_item);
                String newItemName = editTextNewItem.getText().toString();
                ShoppingItem newItem = new ShoppingItem(newItemName);
                shoppingItems.add(newItem);
                adapter.notifyDataSetChanged();
                editTextNewItem.setText("");
            }
        });

        Button buttonSaveList = findViewById(R.id.button_save_list);
        buttonSaveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShoppingList();
            }
        });

        Button buttonLoadList = findViewById(R.id.button_load_list);
        buttonLoadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadShoppingList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoppingItem item = shoppingItems.get(position);
                item.setStrikeThrough(!item.isStrikeThrough());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void saveShoppingList() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            for (ShoppingItem item : shoppingItems) {
                writer.write(item.getName() + "\n");
            }
            writer.close();
            fos.close();
            showToast("Seznam byl uložen.");
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Chyba při ukládání seznamu.");
        }
    }

    private void loadShoppingList() {
        shoppingItems.clear();
        try {
            InputStream is = openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                ShoppingItem item = new ShoppingItem(line);
                shoppingItems.add(item);
            }
            reader.close();
            is.close();
            adapter.notifyDataSetChanged();
            showToast("Seznam byl načten.");
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Chyba při načítání seznamu.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showEditItemDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Přejmenování položky");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_item, null);
        final EditText editText = dialogView.findViewById(R.id.edit_text_item);
        editText.setText(shoppingItems.get(position).getName());

        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newItemName = editText.getText().toString();
            shoppingItems.get(position).setName(newItemName);
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Vymaž produkt", (dialog, which) -> {
            shoppingItems.remove(position);
            adapter.notifyDataSetChanged();
        });

        builder.show();
    }

    static class ShoppingItem {
        private String name;
        private boolean isStrikeThrough;

        public ShoppingItem(String name) {
            this.name = name;
            this.isStrikeThrough = false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isStrikeThrough() {
            return isStrikeThrough;
        }

        public void setStrikeThrough(boolean strikeThrough) {
            isStrikeThrough = strikeThrough;
        }
    }

    class ShoppingListAdapter extends ArrayAdapter<ShoppingItem> {

        private LayoutInflater inflater;

        public ShoppingListAdapter(Context context, int resource, ArrayList<ShoppingItem> items) {
            super(context, resource, items);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, parent, false);
            }

            TextView textViewItem = convertView.findViewById(R.id.textview_item);
            ImageView imageViewEdit = convertView.findViewById(R.id.imageview_edit);

            ShoppingItem item = getItem(position);
            textViewItem.setText(item.getName());

            if (item.isStrikeThrough()) {
                textViewItem.setPaintFlags(textViewItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                convertView.setBackgroundColor(Color.parseColor("#b0cfaf"));
            } else {
                textViewItem.setPaintFlags(textViewItem.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                convertView.setBackgroundColor(Color.WHITE);
            }

            textViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setStrikeThrough(!item.isStrikeThrough());
                    notifyDataSetChanged();
                }
            });

            imageViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditItemDialog(position);
                }
            });

            return convertView;
        }
    }
}
