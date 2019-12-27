package trunk.android;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentTestActivity extends AppCompatActivity {

    private final String TAG = "FragmentTestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(new MyFragment(), "MyFragment");
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "onConfigurationChanged: ");
    }

    public static class MyFragment extends Fragment {
        private final String TAG = "MyFragment";
        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            Log.e(TAG, "onAttach: ");
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.e(TAG, "onCreate: ");
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.e(TAG, "onCreateView: ");
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.e(TAG, "onActivityCreated: ");
        }

        @Override
        public void onStart() {
            super.onStart();
            Log.e(TAG, "onStart: ");
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.e(TAG, "onResume: ");
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.e(TAG, "onPause: ");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e(TAG, "onStop: ");
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            Log.e(TAG, "onDestroyView: ");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.e(TAG, "onDestroy: ");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.e(TAG, "onDetach: ");
        }
    }
}
