package com.monstertechno.timely;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotesActivity extends AppCompatActivity {
    private static final String SHARED_PREF_NAME = "notes_shared_pref";
    private static final String KEY_TITLE_PREFIX = "note_title_";
    private static final String KEY_CONTENT_PREFIX = "note_content_";

    private List<String> noteKeys;
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        recyclerView = findViewById(R.id.recyclerView);
        noteKeys = new ArrayList<>();
        notesAdapter = new NotesAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notesAdapter);

        ImageButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNoteDialog();
            }
        });

        loadNotesFromSharedPreferences();
    }

    private void loadNotesFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Map<String, ?> noteEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : noteEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(KEY_TITLE_PREFIX) && sharedPreferences.contains(KEY_CONTENT_PREFIX + key.substring(KEY_TITLE_PREFIX.length()))) {
                noteKeys.add(key);
            }
        }
        notesAdapter.notifyDataSetChanged();
    }

    private void saveNoteToSharedPreferences(String title, String content) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int noteIndex = noteKeys.size();
        String titleKey = KEY_TITLE_PREFIX + noteIndex;
        String contentKey = KEY_CONTENT_PREFIX + noteIndex;

        editor.putString(titleKey, title);
        editor.putString(contentKey, content);
        editor.apply();

        noteKeys.add(titleKey);
        notesAdapter.notifyItemInserted(noteKeys.size() - 1);
    }

    private void deleteNoteFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);

        String contentKey = KEY_CONTENT_PREFIX + key.substring(KEY_TITLE_PREFIX.length());
        editor.remove(contentKey);

        editor.apply();

        int index = noteKeys.indexOf(key);
        if (index >= 0) {
            noteKeys.remove(index);
            notesAdapter.notifyItemRemoved(index);
        }
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_note, null);
        dialogBuilder.setView(dialogView);

        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        final EditText etContent = dialogView.findViewById(R.id.etContent);

        dialogBuilder.setTitle("Add Note");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    saveNoteToSharedPreferences(title, content);
                    Toast.makeText(NotesActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotesActivity.this, "Please enter title and content", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void showEditNoteDialog(final String titleKey, final String contentKey) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_note, null);
        dialogBuilder.setView(dialogView);

        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        final EditText etContent = dialogView.findViewById(R.id.etContent);

        etTitle.setText(getNoteFromSharedPreferences(titleKey));
        etContent.setText(getNoteFromSharedPreferences(contentKey));

        dialogBuilder.setTitle("Edit Note");
        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    updateNoteInSharedPreferences(titleKey, title);
                    updateNoteInSharedPreferences(contentKey, content);
                    Toast.makeText(NotesActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotesActivity.this, "Please enter title and content", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private String getNoteFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    private void updateNoteInSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView;
            public TextView contentTextView;
            public ImageButton editButton;
            public ImageButton deleteButton;

            public ViewHolder(View view) {
                super(view);
                titleTextView = view.findViewById(R.id.titleTextView);
                contentTextView = view.findViewById(R.id.contentTextView);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String titleKey = noteKeys.get(position);
            final String contentKey = KEY_CONTENT_PREFIX + titleKey.substring(KEY_TITLE_PREFIX.length());

            holder.titleTextView.setText(getNoteFromSharedPreferences(titleKey));
            holder.contentTextView.setText(getNoteFromSharedPreferences(contentKey));

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    showEditNoteDialog(titleKey, contentKey);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteNoteFromSharedPreferences(titleKey);
                    Toast.makeText(NotesActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return noteKeys.size();
        }
    }
}
