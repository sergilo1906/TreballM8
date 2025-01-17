package cat.insbaixcamp.gratitudejournal.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cat.insbaixcamp.gratitudejournal.R;
import cat.insbaixcamp.gratitudejournal.adapters.CalendarAdapter;
import cat.insbaixcamp.gratitudejournal.models.CalendarItem;
import cat.insbaixcamp.gratitudejournal.utils.DateUtils;
import cat.insbaixcamp.gratitudejournal.utils.SharedPrefsUtils;

public class CalendarFragment extends Fragment {
    private final List<String> months = new ArrayList<>();
    private final Map<String, List<CalendarItem>> calendarItemsByMonth = new TreeMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_calendar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        prepareCalendarData();

        CalendarAdapter calendarAdapter = new CalendarAdapter(calendarItemsByMonth, months);
        recyclerView.setAdapter(calendarAdapter);

        return view;
    }

    private void prepareCalendarData() {
        List<CalendarItem> allCalendarItems;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            allCalendarItems = SharedPrefsUtils.getCalendarItems(getContext()).stream()
                    .sorted(Comparator.comparing(CalendarItem::getDate).reversed())
                    .toList();
        } else {
            allCalendarItems = SharedPrefsUtils.getCalendarItems(getContext());
            allCalendarItems.sort((item1, item2) -> item2.getDate().compareTo(item1.getDate()));
        }

        assert allCalendarItems != null;
        allCalendarItems.forEach(item -> {
            String monthYear = DateUtils.formatMonthYear(item.getDate());
            calendarItemsByMonth
                    .computeIfAbsent(monthYear, k -> {
                        months.add(k);
                        return new ArrayList<>();
                    })
                    .add(item);
        });

        if (months.isEmpty()) {
            months.add("There are no notes created");
        }
    }
}
