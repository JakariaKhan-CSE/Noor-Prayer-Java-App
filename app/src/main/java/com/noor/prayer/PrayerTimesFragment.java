package com.noor.prayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.noor.prayer.api.AladhanService;
import com.noor.prayer.api.RetrofitClient;
import com.noor.prayer.models.PrayerTimesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrayerTimesFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvFajr, tvDhuhr, tvAsr, tvMaghrib, tvIsha;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prayer_times, container, false);
        
        setupPrayerItem(view.findViewById(R.id.card_fajr), "Fajr");
        setupPrayerItem(view.findViewById(R.id.card_dhuhr), "Dhuhr");
        setupPrayerItem(view.findViewById(R.id.card_asr), "Asr");
        setupPrayerItem(view.findViewById(R.id.card_maghrib), "Maghrib");
        setupPrayerItem(view.findViewById(R.id.card_isha), "Isha");

        tvFajr = view.findViewById(R.id.card_fajr).findViewById(R.id.tv_prayer_time);
        tvDhuhr = view.findViewById(R.id.card_dhuhr).findViewById(R.id.tv_prayer_time);
        tvAsr = view.findViewById(R.id.card_asr).findViewById(R.id.tv_prayer_time);
        tvMaghrib = view.findViewById(R.id.card_maghrib).findViewById(R.id.tv_prayer_time);
        tvIsha = view.findViewById(R.id.card_isha).findViewById(R.id.tv_prayer_time);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermission();

        return view;
    }

    private void setupPrayerItem(View cardView, String name) {
        TextView tvName = cardView.findViewById(R.id.tv_prayer_name);
        tvName.setText(name);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndFetchPrayerTimes();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndFetchPrayerTimes();
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocationAndFetchPrayerTimes() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && 
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        fetchPrayerTimes(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(getContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void fetchPrayerTimes(double latitude, double longitude) {
        AladhanService service = RetrofitClient.getClient().create(AladhanService.class);
        Call<PrayerTimesResponse> call = service.getPrayerTimes(latitude, longitude, 2); // Method 2: ISNA

        call.enqueue(new Callback<PrayerTimesResponse>() {
            @Override
            public void onResponse(Call<PrayerTimesResponse> call, Response<PrayerTimesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PrayerTimesResponse.Timings timings = response.body().getData().getTimings();
                    tvFajr.setText(timings.getFajr());
                    tvDhuhr.setText(timings.getDhuhr());
                    tvAsr.setText(timings.getAsr());
                    tvMaghrib.setText(timings.getMaghrib());
                    tvIsha.setText(timings.getIsha());
                } else {
                    Toast.makeText(getContext(), "Failed to get prayer times", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PrayerTimesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
