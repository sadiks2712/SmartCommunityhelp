package com.example.smartcommunityhelp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ListView listIssues;

    List<String> issueTitles = new ArrayList<>();
    List<String> issueIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        listIssues = findViewById(R.id.listIssues);

        loadPendingIssues();
    }

    // STEP 3.1: Load only Pending issues
    private void loadPendingIssues() {

        issueTitles.clear();
        issueIds.clear();

        db.collection("issues")
                .whereEqualTo("status", "Pending")
                .get()
                .addOnSuccessListener(query -> {

                    for (DocumentSnapshot doc : query) {
                        issueTitles.add(doc.getString("type"));
                        issueIds.add(doc.getId());
                    }

                    listIssues.setAdapter(new IssueAdapter());
                });
    }

    // STEP 3.2: Custom Adapter (Resolve button logic)
    class IssueAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return issueTitles.size();
        }

        @Override
        public Object getItem(int position) {
            return issueTitles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater()
                    .inflate(R.layout.item_issue, parent, false);

            TextView txtIssue = view.findViewById(R.id.txtIssueType);
            Button btnResolve = view.findViewById(R.id.btnResolve);

            txtIssue.setText(issueTitles.get(position));

            // STEP 3.3: Resolve button click
            btnResolve.setOnClickListener(v -> resolveIssue(position));

            return view;
        }
    }

    // STEP 3.4: Resolve issue
    private void resolveIssue(int position) {

        new AlertDialog.Builder(this)
                .setTitle("Resolve Issue")
                .setMessage("Are you sure you want to resolve this issue?")
                .setPositiveButton("YES", (d, w) -> {

                    db.collection("issues")
                            .document(issueIds.get(position))
                            .update("status", "Resolved")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this,
                                        "Issue Resolved",
                                        Toast.LENGTH_SHORT).show();
                                loadPendingIssues(); // refresh list
                            });
                })
                .setNegativeButton("NO", null)
                .show();
    }
}
