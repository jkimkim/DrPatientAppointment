package app.web.jkimtech.drpatientappointment.model.Interface;


import java.util.List;

import app.web.jkimtech.drpatientappointment.model.TimeSlot;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
